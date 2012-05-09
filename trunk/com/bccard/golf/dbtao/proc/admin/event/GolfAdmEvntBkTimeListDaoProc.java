/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkTimeListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 > 이벤트 > 프리미엄 부킹 이벤트 > 부킹 티타임 
*   적용범위  : golf
*   작성일자  : 2009-05-25
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
public class GolfAdmEvntBkTimeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBkTimeListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBkTimeListDaoProc() {}	

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
			String sgr_nm		= data.getString("SGR_NM");
			String sevent_yn		= data.getString("SEVENT_YN");
			String sbkps_sdate		= data.getString("SBKPS_SDATE");
			String sbkps_edate		= data.getString("SBKPS_EDATE");
			String sreg_sdate		= data.getString("SREG_SDATE");
			String sreg_edate		= data.getString("SREG_EDATE");

			String sql = this.getSelectQuery(sgr_nm, sevent_yn, sbkps_sdate, sbkps_edate, sreg_sdate, sreg_edate);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));

			if (!GolfUtil.isNull(sgr_nm))		pstmt.setString(++idx, sgr_nm);
			if (!GolfUtil.isNull(sevent_yn))	pstmt.setString(++idx, sevent_yn);
			if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
				pstmt.setString(++idx, sbkps_sdate);
				pstmt.setString(++idx, sbkps_edate);
			}
			if (!GolfUtil.isNull(sreg_sdate) && !GolfUtil.isNull(sreg_edate)) {
				pstmt.setString(++idx, sreg_sdate);
				pstmt.setString(++idx, sreg_edate);
			}
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("TIME_SEQ_NO" 		,rs.getLong("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
					result.addString("BKPS_TIME" 		,rs.getString("BOKG_ABLE_TIME") );
					result.addString("DIPY_BKPS_TIME" 	,rs.getString("DIPY_BOKG_ABLE_TIME") );
					result.addString("EVENT_YN" 		,rs.getString("EVNT_YN") );
					result.addString("REG_DATE" 		,rs.getString("REG_ATON") );
					result.addString("BKPS_DATE" 		,rs.getString("BOKG_ABLE_DATE") );
					result.addString("COURSE" 			,rs.getString("GOLF_RSVT_CURS_NM") );	
					result.addString("GR_NM" 			,rs.getString("GREEN_NM") );
					
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
    private String getSelectQuery(String sgr_nm, String sevent_yn, String  sbkps_sdate, String  sbkps_edate, String  sreg_sdate,  String sreg_edate){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			RSVT_ABLE_BOKG_TIME_SEQ_NO, BOKG_ABLE_TIME, DIPY_BOKG_ABLE_TIME, 	");
		sql.append("\n 			EVNT_YN, REG_ATON, 	");
		sql.append("\n 			BOKG_ABLE_DATE, GOLF_RSVT_CURS_NM, GREEN_NM, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TBT.RSVT_ABLE_BOKG_TIME_SEQ_NO, TBT.BOKG_ABLE_TIME, TO_CHAR (TO_DATE (TBT.BOKG_ABLE_TIME, 'HH24MI'), 'HH24:MI') DIPY_BOKG_ABLE_TIME,  	");
		sql.append("\n 				DECODE (NVL(TBT.EVNT_YN,'N'), 'N', '-', 'Y', '이벤트') EVNT_YN, TO_CHAR(TO_DATE(TBT.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, 	");
		sql.append("\n 				TO_CHAR(TO_DATE(TBD.BOKG_ABLE_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') BOKG_ABLE_DATE, TBD.GOLF_RSVT_CURS_NM, TBG.GREEN_NM	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGRSVTABLEBOKGTIMEMGMT TBT, BCDBA.TBGRSVTABLESCDMGMT TBD, BCDBA.TBGAFFIGREEN TBG	");
		sql.append("\n 				WHERE TBT.RSVT_ABLE_SCD_SEQ_NO = TBD.RSVT_ABLE_SCD_SEQ_NO 	");
		sql.append("\n 				AND TBD.AFFI_GREEN_SEQ_NO = TBG.AFFI_GREEN_SEQ_NO(+) 	");
		sql.append("\n 				AND TBD.GOLF_RSVT_CURS_NM IS NOT NULL 	");
		sql.append("\n 				AND TBD.BOKG_ABLE_DATE IS NOT NULL 	");
		sql.append("\n 				AND TBD.PAR_3_BOKG_RESM_DATE IS NULL 	");
		sql.append("\n 				AND TBD.SKY72_HOLE_CODE IS NULL 	");
		if (!GolfUtil.isNull(sgr_nm)) {
			sql.append("\n 				AND TBG.AFFI_GREEN_SEQ_NO = ?	");
		}
		if (!GolfUtil.isNull(sevent_yn)) {
			sql.append("\n 				AND TBT.EVNT_YN = ?	");
		}
		if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
			sql.append("\n 				AND TBD.BOKG_ABLE_DATE BETWEEN ? AND ?	");
		}
		if (!GolfUtil.isNull(sreg_sdate) && !GolfUtil.isNull(sreg_edate)) {
			sql.append("\n 				AND TBT.REG_ATON BETWEEN ? AND ?	");
		}

		sql.append("\n 				ORDER BY TBD.BOKG_ABLE_DATE DESC, TBT.BOKG_ABLE_TIME DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
