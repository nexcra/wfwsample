/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLsnVodListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 리스트
*   적용범위  : golf
*   작성일자  : 2009-05-20
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
public class GolfBoardListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBoardListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBoardListDaoProc() {}	

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
			
			//조회 ---------------------------------------------------------
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			String sql = this.getSelectQuery(search_sel, search_word);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setString(++idx, data.getString("SCH_CLUB_SEQ_NO"));
			pstmt.setString(++idx, data.getString("SCH_BBRD_SEQ_NO"));
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");		
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}

			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("BBRD_SEQ_NO" 			,rs.getLong("BBRD_SEQ_NO") );
					result.addString("CLUB_BBRD_CLSS" 		,rs.getString("CLUB_BBRD_CLSS") );
					result.addString("BBRD_INFO" 			,rs.getString("BBRD_INFO") );
					result.addLong("SEQ_NO"					,rs.getLong("SEQ_NO") );
					result.addString("TITL" 				,rs.getString("TITL") );
					result.addLong("INOR_NUM"				,rs.getLong("INQR_NUM") );
					result.addString("URNK_EPS_YN"			,rs.getString("URNK_EPS_YN") );
					result.addString("ANNX_FILE_PATH"		,rs.getString("ANNX_FILE_PATH") );					
					result.addString("DEL_YN"				,rs.getString("DEL_YN") );					
					result.addString("REG_ATON"				,rs.getString("REG_ATON") );
					
					result.addString("CDHD_ID"				,rs.getString("CDHD_ID") );
					result.addString("CDHD_NM"				,rs.getString("CDHD_NM") );
					result.addString("RE_CNT"				,rs.getString("RE_CNT") );
					result.addString("ICON_NEW"				,rs.getString("ICON_NEW") );
					
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
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			BBRD_SEQ_NO, CLUB_BBRD_CLSS, BBRD_INFO,	");
		sql.append("\n 			SEQ_NO, CLUB_CDHD_SEQ_NO, TITL, INQR_NUM, URNK_EPS_YN, ANNX_FILE_PATH, DEL_YN, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, 	");
		sql.append("\n 			CDHD_ID, CDHD_NM, 	");
		sql.append("\n 			RE_CNT, 	");
		sql.append("\n 			CASE WHEN TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS')+1, 'YYYYMMDDHH24MISS')-TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') > 0 THEN 'Y' ELSE 'N' END ICON_NEW, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TCBM.BBRD_SEQ_NO, TCBM.CLUB_BBRD_CLSS, TCBM.BBRD_INFO,	");
		sql.append("\n 				TCB.SEQ_NO, TCB.CLUB_CDHD_SEQ_NO, TCB.TITL, TCB.INQR_NUM, TCB.URNK_EPS_YN, TCB.ANNX_FILE_PATH, TCB.DEL_YN, TCB.REG_ATON, 	");
		sql.append("\n 				TCM.CDHD_ID, NVL(TCM.CDHD_NM,'운영자') CDHD_NM, 	");
		sql.append("\n 				(SELECT COUNT(REPY_SEQ_NO) FROM BCDBA.TBGCLUBBBRDREPY WHERE BBRD_SEQ_NO=TCB.SEQ_NO) RE_CNT 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGCLUBBBRDMGMT TCBM, BCDBA.TBGCLUBBBRD TCB, BCDBA.TBGCLUBCDHDMGMT TCM	");
		sql.append("\n 				WHERE TCBM.BBRD_SEQ_NO = TCB.BBRD_UNIQ_SEQ_NO	");
		sql.append("\n 				AND TCB.CLUB_CDHD_SEQ_NO = TCM.CLUB_CDHD_SEQ_NO(+)	");
		sql.append("\n 				AND TCB.DEL_YN = 'N'	");
		sql.append("\n 				AND TCBM.CLUB_SEQ_NO = ?	");
		sql.append("\n 				AND TCBM.BBRD_SEQ_NO = ?	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TCB.TITL LIKE ?	");
				sql.append("\n 				OR TCB.CTNT LIKE ? 	");	
				sql.append("\n 				OR TCM.CDHD_ID LIKE ? )	");	
			} else {
				sql.append("\n 				AND "+search_sel+" LIKE ?	");
			}
		}

		sql.append("\n 				ORDER BY TCB.URNK_EPS_YN DESC, TCB.SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}