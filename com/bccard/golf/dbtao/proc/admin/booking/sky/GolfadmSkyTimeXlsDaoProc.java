/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admPreTimeListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄 티타임 리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.sky;

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
 * @author	미디어포스  
 * @version	1.0
 ******************************************************************************/
public class GolfadmSkyTimeXlsDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLessonListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmSkyTimeXlsDaoProc() {}	

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
			String sch_GR_SEQ_NO = data.getString("SCH_GR_SEQ_NO");
			String sch_RESER_CODE = data.getString("SCH_RESER_CODE");
			String sch_VIEW_YN = data.getString("SCH_VIEW_YN");
			String sch_DATE = data.getString("SCH_DATE");
			String sch_DATE_ST = data.getString("SCH_DATE_ST");
			String sch_DATE_ED = data.getString("SCH_DATE_ED");		
			String sql = this.getSelectQuery(sch_GR_SEQ_NO, sch_RESER_CODE, sch_VIEW_YN, sch_DATE, sch_DATE_ST, sch_DATE_ED);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			if (!sch_GR_SEQ_NO.equals("")){
				pstmt.setString(++idx, sch_GR_SEQ_NO);
			}
			if (!sch_RESER_CODE.equals("")){
				pstmt.setString(++idx, sch_RESER_CODE);
			}
			if (!sch_VIEW_YN.equals("")){
				pstmt.setString(++idx, sch_VIEW_YN);
			}			

			if(sch_DATE.equals("rounding")){
				if(!sch_DATE_ST.equals("")){
					pstmt.setString(++idx, sch_DATE_ST);
				}
				if(!sch_DATE_ED.equals("")){
					pstmt.setString(++idx, sch_DATE_ED);
				}
			}else{
				if(!sch_DATE_ST.equals("")){
					pstmt.setString(++idx, sch_DATE_ST);
				}
				if(!sch_DATE_ED.equals("")){
					pstmt.setString(++idx, sch_DATE_ED);
				}
			}

			
			rs = pstmt.executeQuery();

			boolean eof = false;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addInt("ART_NUM" 		,rs.getInt("ART_NUM") );
					result.addInt("TIME_SEQ_NO" 		,rs.getInt("TIME_SEQ_NO") );
					result.addString("VIEW_YN" 			,rs.getString("VIEW_YN") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("BKPS_DATE"			,DateUtil.format(rs.getString("BKPS_DATE"), "yyyyMMdd", "yy-MM-dd") );
					result.addString("COURSE" 			,rs.getString("COURSE") );
					result.addString("BKPS_TIME" 			,rs.getString("BKPS_TIME").substring(0,2)+":"+rs.getString("BKPS_TIME").substring(2,4) );
					result.addString("RESER_CODE" 			,rs.getString("RESER_CODE") );
					result.addString("REG_DATE"			,DateUtil.format(rs.getString("REG_DATE"), "yyyyMMdd", "yy-MM-dd") );
									
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
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
    private String getSelectQuery(String sch_GR_SEQ_NO, String sch_RESER_CODE, String sch_VIEW_YN, String sch_DATE, String sch_DATE_ST, String sch_DATE_ED){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 		, TIME_SEQ_NO, VIEW_YN, GR_NM, BKPS_DATE, COURSE, BKPS_TIME, RESER_CODE, REG_DATE  	");		
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE	");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(ROWNUM*?)+1) AS ART_NUM  	");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 			, T1.RSVT_ABLE_BOKG_TIME_SEQ_NO AS TIME_SEQ_NO	");
		sql.append("\n 			, (CASE WHEN T1.EPS_YN='Y' THEN '노출' ELSE '비노출' END) VIEW_YN	");
		sql.append("\n 			, T3.GREEN_NM AS GR_NM	");
		sql.append("\n 			, T2.BOKG_ABLE_DATE AS BKPS_DATE, T2.GOLF_RSVT_CURS_NM AS COURSE, T1.BOKG_ABLE_TIME AS BKPS_TIME	");
		sql.append("\n 			, (CASE WHEN T1.BOKG_RSVT_STAT_CLSS='0001' THEN '부킹대기' ELSE '부킹확정' END) RESER_CODE	");
		sql.append("\n 			, T2.REG_ATON AS REG_DATE	");
		sql.append("\n 			FROM 	");
		sql.append("\n 			BCDBA.TBGRSVTABLEBOKGTIMEMGMT T1  	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO 	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGAFFIGREEN T3 ON T2.AFFI_GREEN_SEQ_NO=T3.AFFI_GREEN_SEQ_NO	");
		sql.append("\n 			WHERE T2.GOLF_RSVT_CURS_NM IS NOT NULL AND T2.BOKG_ABLE_DATE IS NOT NULL AND T2.PAR_3_BOKG_RESM_DATE IS NULL AND T2.SKY72_HOLE_CODE IS NULL	");
		
		if(!sch_GR_SEQ_NO.equals("")){
			sql.append("\n 				AND T3.AFFI_GREEN_SEQ_NO = ?	");
		}
		if(!sch_RESER_CODE.equals("")){
			sql.append("\n 				AND T1.BOKG_RSVT_STAT_CLSS = ?	");
		}
		if(!sch_VIEW_YN.equals("")){
			sql.append("\n 				AND T1.EPS_YN = ?	");
		}
		if(sch_DATE.equals("rounding")){
			if(!sch_DATE_ST.equals("")){
				sql.append("\n 			AND T2.BOKG_ABLE_DATE >= ?	");
			}
			if(!sch_DATE_ED.equals("")){
				sql.append("\n 			AND T2.BOKG_ABLE_DATE <= ?	");
			}
		}else{
			if(!sch_DATE_ST.equals("")){
				sql.append("\n 			AND T2.REG_ATON >= ?	");
			}
			if(!sch_DATE_ED.equals("")){
				sql.append("\n 			AND T2.REG_ATON <= ?	");
			}
		}		
		
		sql.append("\n 				ORDER BY T2.BOKG_ABLE_DATE DESC, T1.BOKG_ABLE_TIME DESC 	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");

		return sql.toString();
    }
}
