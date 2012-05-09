/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntCouponOrdProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 쿠폰할당
*   적용범위  : Golf
*   작성일자  : 2011-04-13
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.coupon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfEvntCouponOrdProc extends AbstractProc{
	
	
	/**
	 * 할당 할 쿠폰 가져오기 
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param TaoDataSet data
	 * @return HashMap valMap
	 */	
	public HashMap cupnAlloc(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		
		Connection conn = null;		
		ResultSet rs = null;		
		PreparedStatement pstmt = null;
		
		String qty = data.getString("qty");//수량
		
		DbTaoResult  result =  new DbTaoResult(title);
		HashMap valMap = new HashMap();		
		
		int doUpdate = 0;
		int cnt = 0;

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getCupnNumber(); 
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(1,"122");
			pstmt.setString(2, qty);

			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next()){					
					result.addString("cupn", rs.getString("CUPN"));
					result.addString("pwin_grd", Integer.toString(++cnt));
				}
			}
		
			valMap = insertCupnNumber(context, request, data, result);
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		
		return valMap;
		
	}
	
	
	/**
	 * 쿠폰할당
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param TaoDataSet data
	 * @param DbTaoResult result
	 * @return HashMap valMap
	 */
	public HashMap insertCupnNumber(WaContext context,  HttpServletRequest request ,TaoDataSet data ,DbTaoResult result) throws BaseException {		
		
		Connection conn = null;
		PreparedStatement pstmt = null;	
		PreparedStatement pstmt2 = null;
		
		String userNm = data.getString("userNm");
		String socid  = data.getString("socid");		
		String mobile1	= data.getString("mobile1");	
		String mobile2	= data.getString("mobile2");	
		String mobile3	= data.getString("mobile3");		
		String order_no = data.getString("ORDER_NO");//주문번호
		String tm_evt_no = "122";
		String cupn = "";	// 쿠폰번호
		String pwin_grd = "";	// 등수
		
		HashMap valMap = new HashMap();
		Vector cupnV = new Vector();
		
		int doUpdate = 0;
		int idx = 0, idx2 = 0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			String sql = this.getInsert();
			String sql2 = this.getCpnUpdate(Integer.parseInt(data.getString("qty")));

			pstmt = conn.prepareStatement(sql);
			pstmt2 = conn.prepareStatement(sql2);
			
			pstmt2.setString(++idx2, tm_evt_no);			
			
			while(result!=null && result.isNext()){
				
				result.next();
				
				cupn = result.getString("cupn");
				cupnV.add(cupn);
				pwin_grd = result.getString("pwin_grd");
				
				idx = 0;
				pstmt.setString(++idx, tm_evt_no);
				pstmt.setString(++idx, pwin_grd);
				pstmt.setString(++idx, socid);
				pstmt.setString(++idx, userNm);
				pstmt.setString(++idx, cupn);
				pstmt.setString(++idx, "3");
				pstmt.setString(++idx, order_no);
				pstmt.setString(++idx, mobile1);
				pstmt.setString(++idx, mobile2);
				pstmt.setString(++idx, mobile3);		
				
				pstmt.executeUpdate();
				
				pstmt2.setString(++idx2, cupn);
				
			}
			
			if ( pstmt2.executeUpdate() > 0){
				doUpdate = 1;
				conn.commit();
			}			
			conn.setAutoCommit(true);
			
			debug("쿠폰할당  완료 => socid : " + socid + ", userNm : " + userNm +"/n couponNo : " + cupnV);
			
		} catch (Throwable t) {
			try	{
				error("쿠폰할당시 에러 발생/n");
				conn.rollback();
			}catch (Exception c){}
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		
		valMap.put("resultCnt", Integer.toString(doUpdate));
		valMap.put("couponNo", cupnV);
				
		return valMap;
	}		
	

	/**
	 * 쿠폰결제 실패로 인한 쿠폰 반납
	 * @param WaContext context
	 * @param HttpServletRequest request
	 * @param TaoDataSet data
	 * @param HashMap valMap
	 * @return int result
	 */
	public int cupnAlloCancel(WaContext context,  HttpServletRequest request, TaoDataSet data, HashMap valMap) throws BaseException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		Vector cupnV = (Vector)valMap.get("couponNo");		
		String couponNo = "";
		
		for (int i=0; i<cupnV.size(); i++){
			couponNo += "'" + cupnV.elementAt(i) + "',";
		}
		
		couponNo = couponNo.substring(0,couponNo.length()-1);
		
		int  result =  0, result2 =  0;

		try {
			
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			//결제실패시 이벤트 당첨테이블에서 쿠폰삭제
			String sql = this.cpnDelete(couponNo);
			pstmt = conn.prepareStatement(sql.toString());			
			pstmt.setString(1,"122");
			
			result = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			
			
			//결제실패시 할당 받은 할인 쿠폰 반납			
			String sql2 = this.getCpnCancleUpdate(couponNo);
			pstmt = conn.prepareStatement(sql2.toString());			
			pstmt.setString(1,"122");
			
			result2 = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			
			if(result > 0 && result2 > 0) {
				result = 1;
				conn.commit();
			} else {				
				result = 0;
				conn.rollback();
			}			
			
			conn.setAutoCommit(true);
			
			debug("쿠폰결제 실패로 인한 쿠폰 반납 완료/n : " + couponNo);
			
						
		} catch (Throwable t) {
			try{
				error("쿠폰결제 실패시 선 할당한  쿠폰 반납시 에러 발생/n");
				result = 0;				
				conn.rollback();				
			}catch(Exception e){
				debug(e.getMessage());
			}				
			throw new BaseException(t);
			
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}			
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		
		return result;
		
	}
	
	
	/*************************************************************************
    * 구매한 수량 만큼 미사용 쿠폰번호 발급
    ************************************************************************ */
	public String getCupnNumber(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	SELECT RNUM, CUPN, CUPN_VALD_STRT_DATE, CUPN_VALD_END_DATE FROM (	");
		 sql.append("\n		SELECT ROWNUM RNUM, CUPN_NO CUPN, CUPN_VALD_STRT_DATE, CUPN_VALD_END_DATE FROM (	");
		 sql.append("\n			SELECT CUPN_NO, CUPN_VALD_STRT_DATE, CUPN_VALD_END_DATE	");
		 sql.append("\n			FROM BCDBA.TBEVNTUNIFCUPNINFO	");
		 sql.append("\n			WHERE SITE_CLSS='10' AND EVNT_NO=? AND CUPN_PYM_YN='N'	");
		 sql.append("\n			ORDER BY CUPN_VALD_STRT_DATE, CUPN_NO	");
		 sql.append("\n		)	");
		 sql.append("\n	) WHERE RNUM <= ?	");
		 return sql.toString();
	}	
	
	/** ***********************************************************************
    *  이벤트 당첨테이블에 정보 입력
    ************************************************************************ */
	public String getInsert(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	INSERT INTO  BCDBA.TBEVNTLOTPWIN (SEQ_NO ,SITE_CLSS,EVNT_NO,PWIN_GRD,PWIN_DATE ,JUMIN_NO,HG_NM,PROC_YN, CUPN_NO ,USE_NO, EA_INFO, HP_DDD_NO, HP_TEL_NO1, HP_TEL_NO2) ");
		 sql.append("\n	VALUES (EVNTLOTCTNT_SEQ.NEXTVAL,'10',?, ? ,TO_CHAR(SYSDATE,'yyyyMMdd'),?, ?, 1, ?, ? ,?, ?, ?, ? ) ");
		 return sql.toString();
	}
		
	/** ***********************************************************************
    *  할인쿠폰 사용처리
    ************************************************************************ */
	public String getCpnUpdate(int qty){
		
		 StringBuffer sql = new StringBuffer();
		 String str = "";
		 
		 for(int i=0; i<qty; i++){
			 str += "?,";
		 }
		 
		 str = str.substring(0,str.length()-1);
		 sql.append("\n	UPDATE BCDBA.TBEVNTUNIFCUPNINFO SET CUPN_PYM_YN ='Y' WHERE SITE_CLSS='10' AND EVNT_NO=? AND CUPN_NO IN ("+str+") ");

		 return sql.toString();
	}		
	
	
	
	/*************************************************************************
    *  결제실패시 이벤트 당첨테이블에서 쿠폰삭제
    ************************************************************************ */
	public String cpnDelete(String couponNo){
		
		 StringBuffer sql = new StringBuffer();
		 
		 sql.append("\n	DELETE FROM BCDBA.TBEVNTLOTPWIN  ");
		 sql.append("\n	WHERE SITE_CLSS='10' AND EVNT_NO=?  ");		 
		 sql.append("\n	AND CUPN_NO IN (" + couponNo + ") ");
		 
		 return sql.toString();
	}
		
	/*************************************************************************
    *  결제실패시 할당 받은 할인 쿠폰 반납
    ************************************************************************ */
	public String getCpnCancleUpdate(String couponNo){
		
		 StringBuffer sql = new StringBuffer();
		 
		 sql.append("\n	UPDATE BCDBA.TBEVNTUNIFCUPNINFO SET CUPN_PYM_YN ='N' WHERE SITE_CLSS='10' AND EVNT_NO=? AND CUPN_NO IN ("+couponNo+") ");

		 return sql.toString();
	}		

}
