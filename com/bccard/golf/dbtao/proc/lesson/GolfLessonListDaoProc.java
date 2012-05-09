/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLessonListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨프로그램 리스트
*   적용범위  : golf
*   작성일자  : 2009-06-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lesson;

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
public class GolfLessonListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfLessonListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfLessonListDaoProc() {}	

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
			String scoop_cp_cd		= data.getString("SCOOP_CP_CD");
			String slsn_type_cd		= data.getString("SLSN_TYPE_CD");

			String sql = this.getSelectQuery(search_sel, search_word, scoop_cp_cd, slsn_type_cd);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));

			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			if (!GolfUtil.isNull(scoop_cp_cd))	pstmt.setString(++idx, scoop_cp_cd);
			pstmt.setString(++idx, slsn_type_cd);
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("LSN_SEQ_NO" 		,rs.getString("LESN_SEQ_NO") );
					result.addString("LSN_TYPE_CD" 		,rs.getString("LESN_CLSS") );
					result.addString("LSN_NM" 			,rs.getString("LESN_NM") );
					result.addString("EVNT_YN" 			,rs.getString("EVNT_YN") );
					result.addString("IMG_NM" 			,rs.getString("LESN_IMG") );
					result.addString("LSN_PRD_CLSS" 	,rs.getString("LESN_TRM_INP_TP_CLSS") );
					
					result.addString("LSN_START_DT" 	,rs.getString("LESN_STRT_DATE") );
					result.addString("LSN_END_DT" 		,rs.getString("LESN_END_DATE") );
					result.addString("LSN_PRD_INFO"		,rs.getString("LESN_TRM_INFO") );
					result.addLong("LSN_STTL_CST" 		,rs.getLong("LESN_NORM_COST") );
					result.addLong("LSN_DC_CST" 		,rs.getLong("LESN_DC_COST") );
					result.addString("LSN_PL"			,rs.getString("LESN_PL_INFO") );
					result.addString("LSN_INTD"			,rs.getString("LESN_EXPL") );					
					result.addLong("INQR_NUM" 			,rs.getLong("INQR_NUM") );
					result.addInt("CMD_NUM" 			,rs.getInt("CMD_NUM") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addLong("LESN_DC_RT" 		,rs.getLong("LESN_DC_RT") );
					result.addString("COOP_CP_CD_NM"	,rs.getString("GOLF_LESN_AFFI_FIRM_CLSS_NM") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
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
    private String getSelectQuery(String search_sel, String search_word, String scoop_cp_cd, String slsn_type_cd){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			LESN_SEQ_NO, LESN_CLSS, LESN_NM, EVNT_YN, LESN_IMG, LESN_TRM_INP_TP_CLSS,	");
		sql.append("\n 			TO_CHAR(TO_DATE(LESN_STRT_DATE, 'YYYYMMDD'), 'YYYY.MM.DD') LESN_STRT_DATE, 	");
		sql.append("\n 			TO_CHAR(TO_DATE(LESN_END_DATE, 'YYYYMMDD'), 'YYYY.MM.DD') LESN_END_DATE, 	");
		sql.append("\n 			LESN_TRM_INFO, LESN_NORM_COST, LESN_DC_COST, LESN_PL_INFO, LESN_EXPL,	");
		sql.append("\n 			INQR_NUM, CMD_NUM, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, LESN_DC_RT, GOLF_LESN_AFFI_FIRM_CLSS_NM, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				LESN_SEQ_NO, LESN_CLSS, LESN_NM, EVNT_YN, LESN_IMG, LESN_TRM_INP_TP_CLSS,	");
		sql.append("\n 				LESN_STRT_DATE, LESN_END_DATE, LESN_TRM_INFO, LESN_NORM_COST, LESN_DC_COST, LESN_PL_INFO, LESN_EXPL,	");
		sql.append("\n 				INQR_NUM, CMD_NUM, REG_ATON, LESN_DC_RT,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0004' AND GOLF_CMMN_CODE=TGL.GOLF_LESN_AFFI_FIRM_CLSS) GOLF_LESN_AFFI_FIRM_CLSS_NM	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGLESNMGMT TGL	");
		sql.append("\n 				WHERE LESN_SEQ_NO = LESN_SEQ_NO	");
		if (!GolfUtil.isNull(search_word)) { 
			if (search_sel.equals("ALL")) {
				sql.append("\n 	AND LESN_NM LIKE ?	");
				sql.append("\n 	OR LESN_CTNT LIKE ?	");				
			} else {
				sql.append("\n 	AND "+search_sel+" LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(scoop_cp_cd)) sql.append("\n 	AND GOLF_LESN_AFFI_FIRM_CLSS = ?	");
		sql.append("\n 				AND LESN_CLSS = ?	");
		sql.append("\n 				ORDER BY REG_ATON DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
