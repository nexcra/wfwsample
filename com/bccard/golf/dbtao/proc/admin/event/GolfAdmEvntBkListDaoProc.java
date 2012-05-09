/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 부킹 이벤트 리스트
*   적용범위  : golf
*   작성일자  : 2009-05-26
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
public class GolfAdmEvntBkListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBkListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBkListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data, String key) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
	
		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String sgr_nm		= data.getString("SGR_NM");
			String sevent_yn		= data.getString("SEVENT_YN");
			String sbkps_sdate		= data.getString("SBKPS_SDATE");
			String sbkps_edate		= data.getString("SBKPS_EDATE");
			String sreg_sdate		= data.getString("SREG_SDATE");
			String sreg_edate		= data.getString("SREG_EDATE");

			String sql = this.getSelectQuery(sgr_nm, sevent_yn, sbkps_sdate, sbkps_edate, sreg_sdate, sreg_edate, key);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));

			if (!GolfUtil.isNull(sgr_nm))		pstmt.setString(++idx, sgr_nm);
			//if (!GolfUtil.isNull(sevent_yn))	pstmt.setString(++idx, sevent_yn);
			if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
				pstmt.setString(++idx, sbkps_sdate);
				pstmt.setString(++idx, sbkps_edate);
			}
			if (!GolfUtil.isNull(sreg_sdate) && !GolfUtil.isNull(sreg_edate)) {
				pstmt.setString(++idx, sreg_sdate);
				pstmt.setString(++idx, sreg_edate);
			}
			if(!key.equals("admEvntBkExcel")){
				pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			}
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 			,rs.getLong("EVNT_SEQ_NO") );
					result.addString("DISP_YN" 			,rs.getString("BLTN_YN") );
					result.addLong("TIME_SEQ_NO" 		,rs.getLong("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GREEN_NM") );
					result.addString("COURSE" 			,rs.getString("GOLF_RSVT_CURS_NM") );
					result.addString("BKPS_DATE" 		,rs.getString("BOKG_ABLE_DATE") );
					result.addString("BKPS_TIME" 		,rs.getString("BOKG_ABLE_TIME") );
					result.addString("DIPY_BKPS_TIME" 	,rs.getString("DIPY_BOKG_ABLE_TIME") );
					result.addString("EVNT_FROM" 		,rs.getString("EVNT_STRT_DATE") );
					result.addString("EVNT_TO" 			,rs.getString("EVNT_END_DATE") );	
					result.addString("PRIZE_NM" 		,rs.getString("EVNT_BNFT_EXPL") );
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
    private String getSelectQuery(String sgr_nm, String sevent_yn, String  sbkps_sdate, String  sbkps_edate, String  sreg_sdate,  String sreg_edate, String key){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			EVNT_SEQ_NO, BLTN_YN, RSVT_ABLE_BOKG_TIME_SEQ_NO, GREEN_NM, BOKG_ABLE_DATE, GOLF_RSVT_CURS_NM,	");
		sql.append("\n 			BOKG_ABLE_TIME, DIPY_BOKG_ABLE_TIME, 	");
		sql.append("\n 			EVNT_STRT_DATE, EVNT_END_DATE, EVNT_BNFT_EXPL, REG_ATON,	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				EVNT_SEQ_NO, BLTN_YN, RSVT_ABLE_BOKG_TIME_SEQ_NO, GREEN_NM, TO_CHAR(TO_DATE(BOKG_ABLE_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') BOKG_ABLE_DATE, GOLF_RSVT_CURS_NM,  	");
		sql.append("\n 				BOKG_ABLE_TIME, TO_CHAR (TO_DATE (BOKG_ABLE_TIME, 'HH24MI'), 'HH24:MI') DIPY_BOKG_ABLE_TIME,	");
		sql.append("\n 				TO_CHAR(TO_DATE(EVNT_STRT_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_STRT_DATE, TO_CHAR(TO_DATE(EVNT_END_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_END_DATE,	");
		sql.append("\n 				EVNT_BNFT_EXPL, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGEVNTMGMT	");
		sql.append("\n 				WHERE EVNT_CLSS = '0002' 	");
		if (!GolfUtil.isNull(sgr_nm)) {
			sql.append("\n 				AND GREEN_NM = ?	");
		}
		if (!GolfUtil.isNull(sevent_yn)) {
			sql.append("\n 				AND EVNT_BNFT_EXPL IS NOT NULL	");
		}
		if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
			sql.append("\n 				AND BOKG_ABLE_DATE BETWEEN ? AND ?	");
		}
		if (!GolfUtil.isNull(sreg_sdate) && !GolfUtil.isNull(sreg_edate)) {
			sql.append("\n 				AND REG_ATON BETWEEN ? AND ?	");
		}

		sql.append("\n 				ORDER BY EVNT_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		
		if(!key.equals("admEvntBkExcel")){
			sql.append("\n WHERE PAGE = ?	");		
		}	
		return sql.toString();
    }
}
