/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 티타임리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-05-27
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.premium;

import java.io.Reader;
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
public class GolfBkPreTimeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPreTimeListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBkPreTimeListDaoProc() {}	

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
			int defaultDate				= data.getInt("defaultDate");
			int nHour					= data.getInt("nHour");
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery(defaultDate, nHour);   

			// 입력값 (INPUT)
			pstmt = conn.prepareStatement(sql.toString());
			
			rs = pstmt.executeQuery();
			
			int total_0 = 0;
			int total_1 = 0;
			int total_2 = 0;
			int total_3 = 0;
			int total_4 = 0;
			int total_5 = 0;
			int total_6 = 0;
			int total_7 = 0;
			int total_8 = 0;
			int total_9 = 0;
			int total_10 = 0;
			int total_11 = 0;
			int total_12 = 0;
			int total_13 = 0;
			
			String isTOTAL_0 = "";
			String isTOTAL_1 = "";
			String isTOTAL_2 = "";
			String isTOTAL_3 = "";
			String isTOTAL_4 = "";
			String isTOTAL_5 = "";
			String isTOTAL_6 = "";
			String isTOTAL_7 = "";
			String isTOTAL_8 = "";
			String isTOTAL_9 = "";
			String isTOTAL_10 = "";
			String isTOTAL_11 = "";
			String isTOTAL_12 = "";
			String isTOTAL_13 = "";

			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("SEQ_NO" 			,rs.getString("AFFI_GREEN_SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					
					// 남아있는 부킹 수 가져오기
					total_0 = rs.getInt("TOTAL_0");
					total_1 = rs.getInt("TOTAL_1");
					total_2 = rs.getInt("TOTAL_2");
					total_3 = rs.getInt("TOTAL_3");
					total_4 = rs.getInt("TOTAL_4");
					total_5 = rs.getInt("TOTAL_5");
					total_6 = rs.getInt("TOTAL_6");
					total_7 = rs.getInt("TOTAL_7");
					total_8 = rs.getInt("TOTAL_8");
					total_9 = rs.getInt("TOTAL_9");
					total_10 = rs.getInt("TOTAL_10");
					total_11 = rs.getInt("TOTAL_11");
					total_12 = rs.getInt("TOTAL_12");
					total_13 = rs.getInt("TOTAL_13");
					
					// 부킹이 남아있는지 여부 가져오기
					if(total_0>0){isTOTAL_0="Y";} else {isTOTAL_0="N";}
					if(total_1>0){isTOTAL_1="Y";} else {isTOTAL_1="N";}
					if(total_2>0){isTOTAL_2="Y";} else {isTOTAL_2="N";}
					if(total_3>0){isTOTAL_3="Y";} else {isTOTAL_3="N";}
					if(total_4>0){isTOTAL_4="Y";} else {isTOTAL_4="N";}
					if(total_5>0){isTOTAL_5="Y";} else {isTOTAL_5="N";}
					if(total_6>0){isTOTAL_6="Y";} else {isTOTAL_6="N";}
					if(total_7>0){isTOTAL_7="Y";} else {isTOTAL_7="N";}
					if(total_8>0){isTOTAL_8="Y";} else {isTOTAL_8="N";}
					if(total_9>0){isTOTAL_9="Y";} else {isTOTAL_9="N";}
					if(total_10>0){isTOTAL_10="Y";} else {isTOTAL_10="N";}
					if(total_11>0){isTOTAL_11="Y";} else {isTOTAL_11="N";}
					if(total_12>0){isTOTAL_12="Y";} else {isTOTAL_12="N";}
					if(total_13>0){isTOTAL_13="Y";} else {isTOTAL_13="N";}
					
					result.addInt("TOTAL_0" 			,total_0 );
					result.addInt("TOTAL_1" 			,total_1 );
					result.addInt("TOTAL_2" 			,total_2 );
					result.addInt("TOTAL_3" 			,total_3 );
					result.addInt("TOTAL_4" 			,total_4 );
					result.addInt("TOTAL_5" 			,total_5 );
					result.addInt("TOTAL_6" 			,total_6 );
					result.addInt("TOTAL_7" 			,total_7 );
					result.addInt("TOTAL_8" 			,total_8 );
					result.addInt("TOTAL_9" 			,total_9 );
					result.addInt("TOTAL_10" 			,total_10 );
					result.addInt("TOTAL_11" 			,total_11 );
					result.addInt("TOTAL_12" 			,total_12 );
					result.addInt("TOTAL_13" 			,total_13 );
					
					result.addString("isTOTAL_0" 			,isTOTAL_0 );
					result.addString("isTOTAL_1" 			,isTOTAL_1 );
					result.addString("isTOTAL_2" 			,isTOTAL_2 );
					result.addString("isTOTAL_3" 			,isTOTAL_3 );
					result.addString("isTOTAL_4" 			,isTOTAL_4 );
					result.addString("isTOTAL_5" 			,isTOTAL_5 );
					result.addString("isTOTAL_6" 			,isTOTAL_6 );
					result.addString("isTOTAL_7" 			,isTOTAL_7 );
					result.addString("isTOTAL_8" 			,isTOTAL_8 );
					result.addString("isTOTAL_9" 			,isTOTAL_9 );
					result.addString("isTOTAL_10" 			,isTOTAL_10 );
					result.addString("isTOTAL_11" 			,isTOTAL_11 );
					result.addString("isTOTAL_12" 			,isTOTAL_12 );
					result.addString("isTOTAL_13" 			,isTOTAL_13 );
				}
				result.addString("RESULT", "00"); //정상결과
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
    private String getSelectQuery(int cnt_day, int nHour){
        StringBuffer sql = new StringBuffer();

		String tb_name = "";
		
		sql.append("\n 	SELECT * FROM   "); 
		sql.append("\n 	    (SELECT T1.AFFI_GREEN_SEQ_NO, T1.GREEN_NM AS GR_NM FROM BCDBA.TBGAFFIGREEN T1 WHERE AFFI_FIRM_CLSS='0001' AND MAIN_EPS_YN='Y') T1");
	    
		for (int i=0; i<14; i++){
			tb_name = "TT"+i;
			sql.append("\n 	    LEFT JOIN");
			sql.append("\n 	    (");
			sql.append("\n 	    	SELECT T3.AFFI_GREEN_SEQ_NO AS AFFI_GREEN_SEQ_NO, COUNT(*) TOTAL_"+i+"");
			sql.append("\n 	        FROM BCDBA.TBGRSVTABLESCDMGMT T3");
			sql.append("\n 	        JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T4 ON T3.RSVT_ABLE_SCD_SEQ_NO=T4.RSVT_ABLE_SCD_SEQ_NO ");
			sql.append("\n 	        JOIN BCDBA.TBGAFFIGREEN T5 ON T3.AFFI_GREEN_SEQ_NO=T5.AFFI_GREEN_SEQ_NO");
			sql.append("\n 	        WHERE T3.BOKG_ABLE_DATE=TO_CHAR(SYSDATE+"+ (cnt_day+i) +",'YYYYMMDD')");
			sql.append("\n 	        AND T4.EPS_YN='Y'");
			sql.append("\n 	        AND T4.BOKG_RSVT_STAT_CLSS='0001' AND (T4.EVNT_YN is null or T4.EVNT_YN='N')");
			
			if(nHour>=17){
				sql.append("\n 	        AND T3.BOKG_ABLE_DATE>=TO_CHAR(SYSDATE+T5.BOKG_TIME_COLL_TRM+1,'YYYYMMDD')");
			}else{
				sql.append("\n 	        AND T3.BOKG_ABLE_DATE>=TO_CHAR(SYSDATE+T5.BOKG_TIME_COLL_TRM,'YYYYMMDD')");
			}
			
			sql.append("\n 	        AND T3.GOLF_RSVT_DAY_CLSS='P' AND T5.GREEN_ID='springhills'");
			sql.append("\n 	        GROUP BY T3.AFFI_GREEN_SEQ_NO");
			sql.append("\n 	    	UNION ALL");
			
			sql.append("\n 	    	SELECT T3.AFFI_GREEN_SEQ_NO AS AFFI_GREEN_SEQ_NO, COUNT(*) TOTAL_"+i+"");
			sql.append("\n 	        FROM BCDBA.TBGRSVTABLESCDMGMT T3");
			sql.append("\n 	        JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T4 ON T3.RSVT_ABLE_SCD_SEQ_NO=T4.RSVT_ABLE_SCD_SEQ_NO ");
			sql.append("\n 	        JOIN BCDBA.TBGAFFIGREEN T5 ON T3.AFFI_GREEN_SEQ_NO=T5.AFFI_GREEN_SEQ_NO");
			sql.append("\n 	        WHERE T3.BOKG_ABLE_DATE=TO_CHAR(SYSDATE+"+ (cnt_day+i) +",'YYYYMMDD')");
			sql.append("\n 	        AND T4.EPS_YN='Y'");
			sql.append("\n 	        AND T4.BOKG_RSVT_STAT_CLSS='0001' AND (T4.EVNT_YN is null or T4.EVNT_YN='N')");
			sql.append("\n 	        AND T3.BOKG_ABLE_DATE>=TO_CHAR(SYSDATE+T5.BOKG_TIME_COLL_TRM,'YYYYMMDD')");
			sql.append("\n 	        AND T3.GOLF_RSVT_DAY_CLSS='P' AND T5.GREEN_ID<>'springhills'");
			sql.append("\n 	        GROUP BY T3.AFFI_GREEN_SEQ_NO");
			sql.append("\n 	    )"+tb_name+" ");
			sql.append("\n 	    ON T1.AFFI_GREEN_SEQ_NO="+tb_name+".AFFI_GREEN_SEQ_NO");
		}
	    
		sql.append("\n 	ORDER BY T1.AFFI_GREEN_SEQ_NO ");
	

		return sql.toString();
    }
}

