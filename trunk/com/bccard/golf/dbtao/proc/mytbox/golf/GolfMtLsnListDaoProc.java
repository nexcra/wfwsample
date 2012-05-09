/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtLsnListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 마이티박스(레슨정보)
*   적용범위  : golf
*   작성일자  : 2009-06-22
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.mytbox.golf;

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
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfMtLsnListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfMtLsnListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMtLsnListDaoProc() {}	

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
			
			String sql = this.getSelectQuery(search_sel, search_word);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));

			pstmt.setString(++idx, data.getString("CDHD_ID"));
			if (!GolfUtil.isNull(search_word)) pstmt.setString(++idx, "%"+search_word+"%");
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("LSN_SEQ_NO" 		,rs.getString("LESN_SEQ_NO") );
					result.addString("LSN_NM" 			,rs.getString("LESN_NM") );
					result.addString("LSN_PRD_CLSS" 	,rs.getString("LESN_TRM_INP_TP_CLSS") );
					
					result.addString("LSN_START_DT" 	,rs.getString("LESN_STRT_DATE") );
					result.addString("LSN_END_DT" 		,rs.getString("LESN_END_DATE") );
					result.addString("LSN_PRD_INFO"		,rs.getString("LESN_TRM_INFO") );
					result.addLong("LSN_STTL_CST" 		,rs.getLong("LESN_NORM_COST") );
					result.addLong("LSN_DC_CST" 		,rs.getLong("LESN_DC_COST") );
					result.addString("LSN_PL"			,rs.getString("LESN_PL_INFO") );
					result.addLong("LESN_DC_RT" 		,rs.getLong("LESN_DC_RT") );
					result.addString("LESN_CLSS_NM" 	,rs.getString("LESN_CLSS_NM") );
					
					result.addLong("APLC_SEQ_NO" 		,rs.getLong("APLC_SEQ_NO") );
					
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
		sql.append("\n 			LESN_SEQ_NO, LESN_NM, LESN_TRM_INP_TP_CLSS,	");
		sql.append("\n 			TO_CHAR(TO_DATE(LESN_STRT_DATE, 'YYYYMMDD'), 'YYYY.MM.DD') LESN_STRT_DATE, 	");
		sql.append("\n 			TO_CHAR(TO_DATE(LESN_END_DATE, 'YYYYMMDD'), 'YYYY.MM.DD') LESN_END_DATE, 	");
		sql.append("\n 			LESN_TRM_INFO, LESN_NORM_COST, LESN_DC_COST, LESN_PL_INFO, LESN_DC_RT,	");
		sql.append("\n 			APLC_SEQ_NO, LESN_CLSS_NM,	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");	
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TGL.LESN_SEQ_NO, TGL.LESN_NM, TGL.LESN_TRM_INP_TP_CLSS,	");
		sql.append("\n 				TGL.LESN_STRT_DATE, TGL.LESN_END_DATE, TGL.LESN_TRM_INFO, TGL.LESN_NORM_COST, TGL.LESN_DC_COST, TGL.LESN_PL_INFO, TGL.LESN_DC_RT,	");
		sql.append("\n 				TGA.APLC_SEQ_NO,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0003' AND GOLF_CMMN_CODE=TGL.LESN_CLSS) LESN_CLSS_NM	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGLESNMGMT TGL, BCDBA.TBGAPLCMGMT TGA	");
		sql.append("\n 				WHERE TGL.LESN_SEQ_NO=TGA.LESN_SEQ_NO	");
		sql.append("\n 				AND TGA.CDHD_ID = ?		");
		if (!GolfUtil.isNull(search_word))	sql.append("\n 	AND TGL.LESN_NM LIKE ?	");
		sql.append("\n 				ORDER BY TGA.APLC_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		
		
    	return sql.toString();
    }
}
