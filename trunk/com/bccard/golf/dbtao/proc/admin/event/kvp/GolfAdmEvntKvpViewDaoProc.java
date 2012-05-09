/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBnstCompnListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 베네스트 > 상세보기
*   적용범위  : golf
*   작성일자  : 2010-03-23
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		20100524	임은혜	6월 이벤트
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.kvp;

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
import com.bccard.waf.tao.TaoResult;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0 
 ******************************************************************************/
public class GolfAdmEvntKvpViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmOrdListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntKvpViewDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		String title					= data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result = new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			pstmt = conn.prepareStatement(getSelectQuery());
			pstmt.setString(1, data.getString("aplc_seq_no"));
						
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				GolfUtil.toTaoResult(result, rs);
				result.addString("Result", "00");
			}else{
				result.addString("Result", "01");
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


	public DbTaoResult execute_pay(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값
			String jumin_no		= data.getString("jumin_no");
			String aplc_seq_no	= data.getString("aplc_seq_no");	

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(getPayListQuery());
			pstmt.setString(++idx, aplc_seq_no);
			rs = pstmt.executeQuery();
			
			String sttl_amt = "";
			String card_no = "";
			String sttlAtionView = "";
			String sttlStatClss = "";
			String nextmon = "";
			String sttlAtion = "";
			boolean chk = false;
			
			String curDate = DateUtil.currdate("yyyyMM");
			
			if(rs != null) {	

				while(rs.next())  {	
					
					if(!GolfUtil.empty(rs.getString("STTL_AMT"))){
						sttl_amt = GolfUtil.comma(rs.getString("STTL_AMT"));
					}else{
						sttl_amt = "";
					}
					
					if(!GolfUtil.empty(rs.getString("CARD_NO"))){
						card_no = GolfUtil.getFmtCardNo(rs.getString("CARD_NO"));
					}else{
						card_no = "";
					} 
					 
					nextmon = rs.getString("NEXTMON");
					sttlAtionView = rs.getString("STTL_ATON_VIEW");
					sttlAtion = sttlAtionView.replaceAll("-", "").substring(0,6);
					sttlStatClss = rs.getString("STTL_STAT_CLSS");
					
					nextmon = nextmon.substring(0,6);

					if ( Integer.parseInt(curDate)< Integer.parseInt(nextmon)){
						
						debug("# sttlAtion : " + sttlAtion  + ", curDate : "+ curDate +", nextmon : "+nextmon);
						chk = true;
					}else {
						chk = false;
					}

					if (sttlStatClss.equals("Y") || !chk) {
						result.addString("CHKYN"     ,"N" );
					}else {
						result.addString("CHKYN"     ,"Y" );
					}					
					
					result.addString("ODR_NO"     ,rs.getString("ODR_NO") );
					result.addString("CARD_NO"    ,card_no );
					result.addString("VALD_DATE"    ,rs.getString("VALD_DATE") );
					result.addString("INS_MCNT"    ,rs.getString("INS_MCNT") );

					result.addString("STTL_ATON"    ,sttlAtionView );
					result.addString("CNCL_ATON"    ,rs.getString("CNCL_ATON_VIEW") );
					result.addString("STTL_MINS_NM"   ,rs.getString("STTL_MINS_NM") );
					result.addString("AUTH_NO"     ,rs.getString("AUTH_NO") );
					result.addString("STTL_AMT"    ,sttl_amt );

					result.addString("STTL_STAT_CLSS"   ,sttlStatClss );
					result.addString("STR_STTL_STAT_CLSS" ,rs.getString("STR_STTL_STAT_CLSS") );
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
    * 신청내역 상세보기
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();	
		
		sql.append("\n	SELECT T_APL.APLC_SEQ_NO, T_GC.HG_NM, T_GC.EMAIL, T_APL.PGRS_YN, T_APL.GOLF_LESN_RSVT_NO		\n");		
		sql.append("\t	, T_GC.CDHD_ID, T_GC.PHONE, T_GC.MOBILE	\n");
		sql.append("\t	, SUBSTR(T_GC.JUMIN_NO,1,8) STR_JUMIN_NO, T_GC.JUMIN_NO	\n");
		sql.append("\t	, TO_CHAR(TO_DATE(SUBSTR(T_APL.REG_ATON,1,8)),'YYYY-MM-DD') STR_REG_ATON	\n");
		sql.append("\t	, TO_CHAR(TO_DATE(SUBSTR(T_APL.CHNG_ATON,1,8)),'YYYY-MM-DD') STR_CHNG_ATON	\n");
		sql.append("\t	, TO_CHAR(TO_DATE(T_APL.PU_DATE),'YYYY-MM-DD') STR_PU_DATE	\n");
		sql.append("\t	, T_CODE.GOLF_CMMN_CODE, T_CODE.EXPL GRD_NM, T_APL.CSLT_YN	\n");
		sql.append("\t	FROM BCDBA.TBGAPLCMGMT T_APL	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T_GC ON T_APL.CDHD_ID = T_GC.CDHD_ID	\n");
		sql.append("\t	JOIN BCDBA.TBGCMMNCODE T_CODE ON T_APL.RSVT_CDHD_GRD_SEQ_NO=TO_NUMBER(T_CODE.GOLF_CMMN_CODE) AND GOLF_CMMN_CLSS='0056'	\n");
		sql.append("\t	WHERE T_APL.APLC_SEQ_NO=?	\n");
		
		return sql.toString();
    }
    

	/** ***********************************************************************
    * 결제내역 리스트보기
    ************************************************************************ */
    private String getPayListQuery(){
        StringBuffer sql = new StringBuffer();
   		sql.append("\n	 SELECT ODR_NO, CARD_NO, VALD_DATE, INS_MCNT,STTL_MINS_NM, STTL_AMT, AUTH_NO	\n");
  		sql.append("\t	 , TO_CHAR(TO_DATE(SUBSTR(STTL_ATON,1,8)),'YYYY-MM-DD') STTL_ATON_VIEW	\n");
  		sql.append("\t	 , TO_CHAR(ADD_MONTHS(TO_DATE(SUBSTR(STTL_ATON,1,8)),1), 'YYYYMMDD') NEXTMON	\n");
  		sql.append("\t	 , TO_CHAR(TO_DATE(SUBSTR(CNCL_ATON,1,8)),'YYYY-MM-DD') CNCL_ATON_VIEW	\n");
  		sql.append("\t	 , CASE STTL_STAT_CLSS WHEN 'N' THEN '결제완료' ELSE '결제취소' END STR_STTL_STAT_CLSS, STTL_STAT_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGSTTLMGMT	\n");
		sql.append("\t	WHERE STTL_GDS_SEQ_NO=?	\n");
		
		return sql.toString();
    }
    
}
