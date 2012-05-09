/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBnstPayFormDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 이벤트 > 월례회 > 결제 처리
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		20100524	임은혜	6월 이벤트
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.benest;

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
public class GolfEvntBnstPayFormDaoProc extends AbstractProc {
	
	public GolfEvntBnstPayFormDaoProc() {}	

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
			conn.setAutoCommit(false);			
			
			String sql = this.getEvtQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, data.getString("aplc_seq_no"));

			rs = pstmt.executeQuery();
			
			if ( rs != null ) {

				while(rs.next())  {
					
					result.addString("BKG_PE_NM" 			,rs.getString("BKG_PE_NM") );
					result.addString("JUMIN_NO1" 			,rs.getString("JUMIN_NO1") );
					result.addString("JUMIN_NO2" 			,rs.getString("JUMIN_NO2") );
					result.addString("HP_DDD_NO" 			,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO" 			,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO" 			,rs.getString("HP_TEL_SNO") );
					result.addString("EMAIL" 				,rs.getString("EMAIL") );
					result.addString("HADC_NUM" 			,rs.getString("HADC_NUM") );
					result.addString("CNT" 					,rs.getString("CNT") );
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("RSVT_DATE" 			,rs.getString("RSVT_DATE") );
					result.addString("NOTE" 				,rs.getString("NOTE") );
					result.addString("MONTHS" 				,rs.getString("MONTHS") );
					result.addString("RESULT", "00"); //정상결과
				}
				
			} else {
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

	public DbTaoResult execute_compn(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);			
			
			String sql = this.getCompnQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, data.getString("aplc_seq_no"));

			rs = pstmt.executeQuery();
			
			String sttl_amt = "";

			if ( rs != null ) {

				while(rs.next())  {

					sttl_amt = rs.getString("STTL_AMT");
					if(!GolfUtil.empty(sttl_amt)){
						sttl_amt = GolfUtil.comma(sttl_amt);
					}else{
						sttl_amt = "0";
					}
					
					result.addString("SEQ_NO" 				,rs.getString("SEQ_NO") );
					result.addString("BKG_PE_NM" 			,rs.getString("BKG_PE_NM") );
					result.addString("HP_DDD_NO" 			,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO" 			,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO" 			,rs.getString("HP_TEL_SNO") );
					result.addString("CDHD_NON_CDHD_CLSS" 	,rs.getString("CDHD_NON_CDHD_CLSS") );
					result.addString("STTL_AMT" 			,sttl_amt );
					result.addString("STTL_STAT_CLSS" 		,rs.getString("STTL_STAT_CLSS") );
					result.addString("RESULT", "00"); //정상결과
				}
				
			} else {
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
    * 이벤트 정보 가져오기
    ************************************************************************ */
    private String getEvtQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n	SELECT BKG_PE_NM, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, CDHD_ID	");
		sql.append("\n	, SUBSTR(JUMIN_NO,1,6) JUMIN_NO1, SUBSTR(JUMIN_NO,7,7) JUMIN_NO2	");
		sql.append("\n	, (SELECT COUNT(*) FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=EVT.APLC_SEQ_NO) CNT	");
		sql.append("\n	, GREEN_NM, TO_CHAR(TO_DATE(RSVT_DATE),'YYYY. MM. DD (DY)') RSVT_DATE, EVT.NOTE	");
		sql.append("\n	, SUBSTR(RSVT_DATE,5,2) MONTHS	");
		sql.append("\n	FROM BCDBA.TBGGOLFEVNTAPLC EVT WHERE APLC_SEQ_NO=?	");
		
		return sql.toString();
    }
    
	/** ***********************************************************************
     * 동반자 정보 가져오기
     ************************************************************************ */
     private String getCompnQuery(){
        StringBuffer sql = new StringBuffer();
         
 		sql.append("\n	SELECT SEQ_NO, BKG_PE_NM, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, STTL_AMT, STTL_STAT_CLSS	");
 		sql.append("\n	, CASE CDHD_NON_CDHD_CLSS WHEN '1' THEN '회원' WHEN '2' THEN '비회원' END CDHD_NON_CDHD_CLSS 	");
 		sql.append("\n	FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=?	");
 		
 		return sql.toString();
     }
}
