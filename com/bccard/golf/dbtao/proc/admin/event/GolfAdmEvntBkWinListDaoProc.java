/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkWinListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 > 이벤트 > 프리미엄 부킹 이벤트 > 당첨자 입력
*   적용범위  : golf
*   작성일자  : 2009-05-27
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event;

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
public class GolfAdmEvntBkWinListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBkWinListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBkWinListDaoProc() {}	

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
			String sbkps_sdate		= data.getString("SBKPS_SDATE");
			String sbkps_edate		= data.getString("SBKPS_EDATE");
			String sevnt_from		= data.getString("SEVNT_FROM");
			String sevnt_to			= data.getString("SEVNT_TO");
			String event_no			= data.getString("EVNT_CLSS");

			String sql = this.getSelectQuery(search_sel, search_word, sbkps_sdate, sbkps_edate, sevnt_from, sevnt_to);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setString(++idx, event_no);

			if (!GolfUtil.isNull(search_word)) {

				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}

			if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
				pstmt.setString(++idx, sbkps_sdate+"000000");
				pstmt.setString(++idx, sbkps_edate+"235959");
			}
			if (!GolfUtil.isNull(sevnt_from) && !GolfUtil.isNull(sevnt_to)) {
				pstmt.setString(++idx, sevnt_to);
				pstmt.setString(++idx, sevnt_from);
			}
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 			,rs.getLong("SEQ_NO") );
					result.addString("TITL" 			,rs.getString("TITL") );
					result.addString("DISP_YN" 			,rs.getString("BLTN_YN") );
					result.addLong("INQR_NUM" 			,rs.getLong("INQR_NUM") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );				
					
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
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	
    private String getSelectQuery(String search_sel, String search_word, String sbkps_sdate, String  sbkps_edate, String sevnt_from, String sevnt_to){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			SEQ_NO, TITL, BLTN_YN, INQR_NUM, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TGEJ.SEQ_NO, TGEJ.TITL, TGEJ.BLTN_YN, TGEJ.INQR_NUM,   	");
		sql.append("\n 				TGEJ.REG_ATON   	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGEVNTMGMT TGE, BCDBA.TBGEVNTPRZPEMGMT TGEJ	");
		sql.append("\n 				WHERE TGE.EVNT_SEQ_NO = TGEJ.EVNT_SEQ_NO 	");
		sql.append("\n 				AND TGE.EVNT_CLSS = ? 	");
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 			AND (TGEJ.TITL LIKE ?	");
				sql.append("\n 			OR TGEJ.CTNT LIKE ?)	");				
			} else {
				sql.append("\n 			AND "+search_sel+" LIKE ?	");
			}
		}
//		if (!GolfUtil.isNull(sreg_sdate) && !GolfUtil.isNull(sreg_edate)) {
//			sql.append("\n 				AND TGE.REG_ATON BETWEEN ? AND ?	");
//		}
		if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
			sql.append("\n 				AND TGE.REG_ATON BETWEEN ? AND ?	");
		}
		if (!GolfUtil.isNull(sevnt_from) && !GolfUtil.isNull(sevnt_to)) {
			sql.append("\n 				AND TGE.EVNT_STRT_DATE <= ?	");
			sql.append("\n 				AND TGE.EVNT_END_DATE >=  ?	");
		}

		sql.append("\n 				ORDER BY TGEJ.REG_ATON DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
