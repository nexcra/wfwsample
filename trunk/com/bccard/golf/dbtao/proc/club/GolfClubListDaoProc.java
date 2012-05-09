/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfClubListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 리스트
*   적용범위  : golf
*   작성일자  : 2009-07-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfClubListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfClubListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			String sch_golf_club_ctgo = data.getString("SCH_GOLF_CLUB_CTGO");
			
			String sql = this.getSelectQuery(search_sel, search_word, sch_golf_club_ctgo);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			if (!GolfUtil.isNull(search_word)) pstmt.setString(++idx, "%"+search_word+"%");
			if (!GolfUtil.isNull(sch_golf_club_ctgo)) pstmt.setString(++idx, sch_golf_club_ctgo);

			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("CLUB_SEQ_NO" 			,rs.getLong("CLUB_SEQ_NO") );
					result.addString("GOLF_CLUB_CTGO" 		,rs.getString("GOLF_CLUB_CTGO") );
					result.addString("CLUB_NM" 				,rs.getString("CLUB_NM") );
					result.addString("OPN_PE_ID" 			,rs.getString("OPN_PE_ID") );
					result.addString("OPN_PE_NM"			,rs.getString("OPN_PE_NM") );
					result.addString("OPN_ATON"				,rs.getString("OPN_ATON") );					
					result.addString("GOLF_CLUB_CTGO_NM"	,rs.getString("GOLF_CLUB_CTGO_NM") );					
					result.addString("MEM_CNT"				,rs.getString("MEM_CNT") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
					result.addString("RNUM"				,rs.getString("RNUM") );
										
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
	public DbTaoResult getClubFlagList(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String flag		= data.getString("FLAG");
			
			String sql = this.getSelectQuery2(flag);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("CLUB_SEQ_NO" 			,rs.getLong("CLUB_SEQ_NO") );
					result.addString("GOLF_CLUB_CTGO" 		,rs.getString("GOLF_CLUB_CTGO") );
					result.addString("CLUB_NM" 				,rs.getString("CLUB_NM") );
					result.addString("OPN_PE_ID" 			,rs.getString("OPN_PE_ID") );
					result.addString("OPN_PE_NM"			,rs.getString("OPN_PE_NM") );
					result.addString("CLUB_IMG"				,rs.getString("CLUB_IMG") );
					result.addString("OPN_ATON"				,rs.getString("OPN_ATON") );
					result.addString("GOLF_CLUB_CTGO_NM"	,rs.getString("GOLF_CLUB_CTGO_NM") );					
					result.addString("MEM_CNT"				,rs.getString("MEM_CNT") );
					
					String tTileTxt = rs.getString("CLUB_SBJT_CTNT");
					if (tTileTxt.getBytes().length > 100)	tTileTxt = GolfUtil.getCutKSCString(tTileTxt, 100, "..."); 
					result.addString("CLUB_SBJT_CTNT" 	, tTileTxt);
					
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
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(String search_sel, String search_word, String sch_golf_club_ctgo){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			CLUB_SEQ_NO, GOLF_CLUB_CTGO, CLUB_NM, OPN_PE_ID, OPN_PE_NM, TO_CHAR(TO_DATE(OPN_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') OPN_ATON,	");
		sql.append("\n 			GOLF_CLUB_CTGO_NM, 	");
		sql.append("\n 			MEM_CNT, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TC.CLUB_SEQ_NO, TC.GOLF_CLUB_CTGO, TC.CLUB_NM, TC.OPN_PE_ID, TC.OPN_PE_NM, TC.OPN_ATON,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0042' AND GOLF_CMMN_CODE=TC.GOLF_CLUB_CTGO) GOLF_CLUB_CTGO_NM, 	");
		sql.append("\n 				NVL((SELECT COUNT(CLUB_CDHD_SEQ_NO) FROM BCDBA.TBGCLUBCDHDMGMT WHERE CLUB_SEQ_NO=TC.CLUB_SEQ_NO AND JONN_YN='Y' AND SECE_YN='N'),0) MEM_CNT 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGCLUBMGMT TC	");
		sql.append("\n 				WHERE TC.CLUB_OPN_AUTH_YN = 'Y'	");
		sql.append("\n 				AND TC.CLUB_ACT_YN = 'Y'	");		
		if (!GolfUtil.isNull(search_word)) sql.append("\n 				AND "+search_sel+" LIKE ?	");
		if (!GolfUtil.isNull(sch_golf_club_ctgo)) sql.append("\n 				AND TC.GOLF_CLUB_CTGO = ?	");
		sql.append("\n 				ORDER BY TC.CLUB_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
    

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery2(String flag){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			CLUB_SEQ_NO, GOLF_CLUB_CTGO, CLUB_NM, OPN_PE_ID, OPN_PE_NM, CLUB_IMG, CLUB_SBJT_CTNT, TO_CHAR(TO_DATE(OPN_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') OPN_ATON,	");
		sql.append("\n 			GOLF_CLUB_CTGO_NM, 	");
		sql.append("\n 			MEM_CNT 	");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TC.CLUB_SEQ_NO, TC.GOLF_CLUB_CTGO, TC.CLUB_NM, TC.OPN_PE_ID, TC.OPN_PE_NM, CLUB_IMG, TC.CLUB_SBJT_CTNT, TC.OPN_ATON,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0042' AND GOLF_CMMN_CODE=TC.GOLF_CLUB_CTGO) GOLF_CLUB_CTGO_NM, 	");
		sql.append("\n 				NVL((SELECT COUNT(CLUB_CDHD_SEQ_NO) FROM BCDBA.TBGCLUBCDHDMGMT WHERE CLUB_SEQ_NO=TC.CLUB_SEQ_NO),0) MEM_CNT 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGCLUBMGMT TC	");
		sql.append("\n 				WHERE TC.CLUB_OPN_AUTH_YN = 'Y'	");
		sql.append("\n 				AND TC.CLUB_ACT_YN = 'Y'	");		
		if (flag.equals("BEST")) sql.append("\n 	ORDER BY NVL((SELECT COUNT(CLUB_CDHD_SEQ_NO) FROM BCDBA.TBGCLUBCDHDMGMT WHERE CLUB_SEQ_NO=TC.CLUB_SEQ_NO),0) DESC	");
		if (flag.equals("NEW")) sql.append("\n 		ORDER BY TC.CLUB_SEQ_NO DESC	");
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE RNUM <= 10	");		

		return sql.toString();
    }
}