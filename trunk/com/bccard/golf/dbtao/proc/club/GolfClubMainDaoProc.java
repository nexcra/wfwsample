/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLessonInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨프로그램 상세조회
*   적용범위  : golf
*   작성일자  : 2009-06-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션 
 * @version	1.0
 ******************************************************************************/
public class GolfClubMainDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfMainDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMainDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getBoardList(WaContext context, TaoDataSet data, String cateCode, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String club_code = data.getString("CLUB_CODE");
			
	        StringBuffer sql = new StringBuffer();

			sql.append("\n 	SELECT");
			sql.append("\n 		RNUM, BBRD_SEQ_NO, CLUB_SEQ_NO,	");
			sql.append("\n 		SEQ_NO, TITL, ANNX_FILE_PATH, REG_ATON	");
			sql.append("\n 	FROM	");
			sql.append("\n 	(	");
			sql.append("\n 		SELECT");
			sql.append("\n 			ROWNUM RNUM, BBRD_SEQ_NO, CLUB_SEQ_NO,  	");
			sql.append("\n 			SEQ_NO, TITL, ANNX_FILE_PATH, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD') REG_ATON 	");
			sql.append("\n 		FROM(	SELECT	");
			sql.append("\n 					TCBM.BBRD_SEQ_NO, TCBM.CLUB_SEQ_NO, 	");
			sql.append("\n 					TCB.SEQ_NO, TCB.TITL, TCB.ANNX_FILE_PATH, TCB.REG_ATON 	");
			sql.append("\n 				FROM");
			sql.append("\n 				BCDBA.TBGCLUBBBRDMGMT TCBM, BCDBA.TBGCLUBBBRD TCB	");
			sql.append("\n 				WHERE TCBM.BBRD_SEQ_NO = TCB.BBRD_UNIQ_SEQ_NO	");	
			sql.append("\n 				AND TCB.DEL_YN = 'N'	");
			if (!GolfUtil.isNull(club_code)) sql.append("\n 			AND TCBM.CLUB_SEQ_NO = ?	");			
			if (!GolfUtil.isNull(cateCode)) sql.append("\n 				AND TCBM.CLUB_BBRD_CLSS = ?	");
			sql.append("\n 				ORDER BY TCB.SEQ_NO DESC	");	
			sql.append("\n 		)	");	
			sql.append("\n 	)	");
			sql.append("\n 	WHERE RNUM <= ?	");	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			if (!GolfUtil.isNull(club_code)) pstmt.setString(++idx, club_code);
			if (!GolfUtil.isNull(cateCode)) pstmt.setString(++idx, cateCode);			
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addInt("BBRD_SEQ_NO" 			,rs.getInt("BBRD_SEQ_NO") );
					result.addString("CLUB_SEQ_NO" 		,rs.getString("CLUB_SEQ_NO") );
					result.addLong("SEQ_NO" 				,rs.getLong("SEQ_NO") );
					result.addString("ANNX_FILE_PATH" 		,rs.getString("ANNX_FILE_PATH") );
					result.addString("REG_ATON" 			,rs.getString("REG_ATON") );
					
					String tTileTxt = rs.getString("TITL");
					if (TitleLen > 0) {
						if (tTileTxt.getBytes().length > TitleLen)	tTileTxt = GolfUtil.getCutKSCString(tTileTxt, TitleLen, "..."); 
					}
					result.addString("TITL" 	, tTileTxt);
					
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}


	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getClubList(WaContext context, TaoDataSet data, String flag, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String club_code = data.getString("CLUB_CODE");
			
	        StringBuffer sql = new StringBuffer();			
			
			sql.append("\n 	SELECT");
			sql.append("\n 		RNUM, CLUB_SEQ_NO,	");
			sql.append("\n 		GOLF_CLUB_CTGO, CLUB_NM, GOLF_CLUB_CTGO_NM	");
			sql.append("\n 	FROM	");
			sql.append("\n 	(	");
			sql.append("\n 		SELECT");
			sql.append("\n 			ROWNUM RNUM,  	");
			sql.append("\n 			CLUB_SEQ_NO, GOLF_CLUB_CTGO, CLUB_NM, GOLF_CLUB_CTGO_NM 	");
			sql.append("\n 		FROM(	SELECT	");
			sql.append("\n 					TC.CLUB_SEQ_NO, 	");
			sql.append("\n 					TC.GOLF_CLUB_CTGO, TC.CLUB_NM, 	");
			sql.append("\n 					(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0042' AND GOLF_CMMN_CODE=TC.GOLF_CLUB_CTGO) GOLF_CLUB_CTGO_NM 	");
			sql.append("\n 				FROM");
			sql.append("\n 				BCDBA.TBGCLUBMGMT TC	");
			sql.append("\n 				WHERE TC.CLUB_OPN_AUTH_YN = 'Y'	");
			sql.append("\n 				AND TC.CLUB_ACT_YN = 'Y'	");
			if (flag.equals("BEST")) sql.append("\n 	ORDER BY NVL((SELECT COUNT(CLUB_CDHD_SEQ_NO) FROM BCDBA.TBGCLUBCDHDMGMT WHERE CLUB_SEQ_NO=TC.CLUB_SEQ_NO),0) DESC	");
			if (flag.equals("NEW")) sql.append("\n 		ORDER BY TC.CLUB_SEQ_NO DESC	");
			sql.append("\n 		)	");	
			sql.append("\n 	)	");
			sql.append("\n 	WHERE RNUM <= ?	");	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());	
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addInt("CLUB_SEQ_NO" 			,rs.getInt("CLUB_SEQ_NO") );
					result.addString("GOLF_CLUB_CTGO" 		,rs.getString("GOLF_CLUB_CTGO") );
					//result.addString("CLUB_NM" 				,rs.getString("CLUB_NM") );
					result.addString("GOLF_CLUB_CTGO_NM" 	,rs.getString("GOLF_CLUB_CTGO_NM") );
					
					String tTileTxt = rs.getString("CLUB_NM");
					if (TitleLen > 0) {
						if (tTileTxt.getBytes().length > TitleLen)	tTileTxt = GolfUtil.getCutKSCString(tTileTxt, TitleLen, "..."); 
					}
					result.addString("CLUB_NM" 	, tTileTxt);
					
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}

}
