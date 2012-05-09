/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLsnVodReplyListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨동영상 덧글 리스트
*   적용범위  : golf
*   작성일자  : 2009-05-22
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lesson;

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
public class GolfAdmLsnVodReplyListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLsnVodReplyListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLsnVodReplyListDaoProc() {}	

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
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			if (!GolfUtil.isNull(svod_clss))	pstmt.setString(++idx, svod_clss);
			if (!GolfUtil.isNull(svod_lsn_clss))	pstmt.setString(++idx, svod_lsn_clss);
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 			,rs.getLong("GOLF_MVPT_SEQ_NO") );
					result.addString("VOD_CLSS" 		,rs.getString("GOLF_MVPT_CLSS") );
					result.addString("VOD_LSN_CLSS" 	,rs.getString("GOLF_MVPT_LESN_CLSS") );
					result.addString("TITL" 			,rs.getString("TITL") );
					result.addString("BEST_YN" 			,rs.getString("BEST_YN") );
					result.addString("NEW_YN" 			,rs.getString("ANW_BLTN_ARTC_YN") );
					result.addLong("REPLY_NO" 			,rs.getLong("REPY_SEQ_NO") );
					result.addString("REPLY_CTNT"		,GolfUtil.getUrl(rs.getString("REPY_CTNT")) );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("HAN_NM"			,rs.getString("HG_NM") );
					result.addString("VOD_CLSS_NM"		,rs.getString("GOLF_MVPT_CLSS_NM") );
					result.addString("VOD_LSN_CLSS_NM"	,rs.getString("GOLF_MVPT_LESN_CLSS_CLSS_NM") );
					
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
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
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
		sql.append("\n 			GOLF_MVPT_SEQ_NO, GOLF_MVPT_CLSS, GOLF_MVPT_LESN_CLSS, TITL, BEST_YN, ANW_BLTN_ARTC_YN, 	");
		sql.append("\n 			REPY_SEQ_NO, REPY_CTNT, REG_ATON,	");
		sql.append("\n 			HG_NM,	");
		sql.append("\n 			GOLF_MVPT_CLSS_NM, GOLF_MVPT_LESN_CLSS_CLSS_NM,	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TGV.GOLF_MVPT_SEQ_NO, TGV.GOLF_MVPT_CLSS, TGV.GOLF_MVPT_LESN_CLSS, TGV.TITL, TGV.BEST_YN, TGV.ANW_BLTN_ARTC_YN, 	");
		sql.append("\n 				TBR.REPY_SEQ_NO, TBR.REPY_CTNT, TO_CHAR(TO_DATE(TBR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI:SS') REG_ATON, 	");
		sql.append("\n 				TBU.HG_NM, 	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0007' AND GOLF_CMMN_CODE=TGV.GOLF_MVPT_CLSS) GOLF_MVPT_CLSS_NM, 	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0013' AND GOLF_CMMN_CODE=TGV.GOLF_MVPT_LESN_CLSS) GOLF_MVPT_LESN_CLSS_CLSS_NM 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGMVPTMGMT TGV, BCDBA.TBGBBRDREPY TBR, BCDBA.TBGGOLFCDHD TBU	");
		sql.append("\n 				WHERE TBR.BBRD_SEQ_NO = TGV.GOLF_MVPT_SEQ_NO	");
		sql.append("\n 				AND TBR.RGS_PE_ID = TBU.CDHD_ID	");
		sql.append("\n 				AND TBR.REPY_CLSS = '0002'	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TGV.TITL LIKE ?	");
				sql.append("\n 				OR TGV.CTNT LIKE ? )	");				
			} else {
				sql.append("\n 				AND "+search_sel+" LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(svod_clss)) sql.append("\n 		AND TGV.GOLF_MVPT_CLSS = ?	");
		if (!GolfUtil.isNull(svod_lsn_clss)) sql.append("\n 	AND TGV.GOLF_MVPT_LESN_CLSS = ?	");
		sql.append("\n 				ORDER BY TGV.GOLF_MVPT_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
