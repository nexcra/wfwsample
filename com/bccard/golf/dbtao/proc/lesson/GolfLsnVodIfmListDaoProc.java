/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLsnVodListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨동영상 리스트
*   적용범위  : golf
*   작성일자  : 2009-06-02
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
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfLsnVodIfmListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfLsnVodListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfLsnVodIfmListDaoProc() {}	

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
			String svod_clss		= data.getString("SVOD_CLSS");
			String svod_lsn_clss		= data.getString("SVOD_LSN_CLSS");

			String sql = this.getSelectQuery(search_sel, search_word, svod_clss, svod_lsn_clss);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			pstmt.setString(++idx, svod_clss);
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			if (!GolfUtil.isNull(svod_lsn_clss))	pstmt.setString(++idx, svod_lsn_clss);
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 			,rs.getLong("GOLF_MVPT_SEQ_NO") );
					result.addString("VOD_CLSS" 		,rs.getString("GOLF_MVPT_CLSS") );
					result.addString("VOD_LSN_CLSS" 	,rs.getString("GOLF_MVPT_LESN_CLSS") );
					result.addString("VOD_NM" 			,rs.getString("MVPT_ANNX_FILE_PATH") );
					result.addString("IMG_NM" 			,rs.getString("ANNX_IMG") );
					result.addString("TITL" 			,rs.getString("TITL") );
					result.addString("CTNT" 			,rs.getString("CTNT") );
					result.addString("BEST_YN" 			,rs.getString("BEST_YN") );
					result.addString("NEW_YN" 			,rs.getString("ANW_BLTN_ARTC_YN") );
					result.addLong("INQR_NUM" 			,rs.getLong("INQR_NUM") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("VOD_CLSS_NM"		,rs.getString("GOLF_MVPT_CLSS_NM") );
					result.addString("VOD_LSN_CLSS_NM"	,rs.getString("GOLF_MVPT_LESN_CLSS_NM") );
					
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
    private String getSelectQuery(String search_sel, String search_word, String svod_clss, String svod_lsn_clss){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			GOLF_MVPT_SEQ_NO, GOLF_MVPT_CLSS, GOLF_MVPT_LESN_CLSS, MVPT_ANNX_FILE_PATH, ANNX_IMG, TITL, CTNT, BEST_YN, ANW_BLTN_ARTC_YN, INQR_NUM, REG_ATON, 	");
		sql.append("\n 			GOLF_MVPT_CLSS_NM, GOLF_MVPT_LESN_CLSS_NM,	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TGV.GOLF_MVPT_SEQ_NO, TGV.GOLF_MVPT_CLSS, TGV.GOLF_MVPT_LESN_CLSS, TGV.MVPT_ANNX_FILE_PATH, TGV.ANNX_IMG, TGV.TITL, TGV.CTNT, TGV.BEST_YN, TGV.ANW_BLTN_ARTC_YN, TGV.INQR_NUM, TO_CHAR(TO_DATE(TGV.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, 	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0007' AND GOLF_CMMN_CODE=TGV.GOLF_MVPT_CLSS) GOLF_MVPT_CLSS_NM, 	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0013' AND GOLF_CMMN_CODE=TGV.GOLF_MVPT_LESN_CLSS) GOLF_MVPT_LESN_CLSS_NM 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGMVPTMGMT TGV	");
		sql.append("\n 				WHERE TGV.GOLF_MVPT_CLSS = ?	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TGV.TITL LIKE ?	");
				sql.append("\n 				OR TGV.CTNT LIKE ? )	");				
			} else {
				sql.append("\n 				AND "+search_sel+" LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(svod_lsn_clss)) sql.append("\n 	AND TGV.GOLF_MVPT_LESN_CLSS = ?	");
		sql.append("\n 				ORDER BY TGV.GOLF_MVPT_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
