/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntCouponOrdProc
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : �����Ҵ�
*   �������  : Golf
*   �ۼ�����  : 2011-04-13
************************** �����̷� ****************************************************************
*    ����    �ۼ���   �������
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
	 * �Ҵ� �� ���� �������� 
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
		
		String qty = data.getString("qty");//����
		
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
	 * �����Ҵ�
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
		String order_no = data.getString("ORDER_NO");//�ֹ���ȣ
		String tm_evt_no = "122";
		String cupn = "";	// ������ȣ
		String pwin_grd = "";	// ���
		
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
			
			debug("�����Ҵ�  �Ϸ� => socid : " + socid + ", userNm : " + userNm +"/n couponNo : " + cupnV);
			
		} catch (Throwable t) {
			try	{
				error("�����Ҵ�� ���� �߻�/n");
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
	 * �������� ���з� ���� ���� �ݳ�
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
			
			//�������н� �̺�Ʈ ��÷���̺��� ��������
			String sql = this.cpnDelete(couponNo);
			pstmt = conn.prepareStatement(sql.toString());			
			pstmt.setString(1,"122");
			
			result = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			
			
			//�������н� �Ҵ� ���� ���� ���� �ݳ�			
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
			
			debug("�������� ���з� ���� ���� �ݳ� �Ϸ�/n : " + couponNo);
			
						
		} catch (Throwable t) {
			try{
				error("�������� ���н� �� �Ҵ���  ���� �ݳ��� ���� �߻�/n");
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
    * ������ ���� ��ŭ �̻�� ������ȣ �߱�
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
    *  �̺�Ʈ ��÷���̺� ���� �Է�
    ************************************************************************ */
	public String getInsert(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	INSERT INTO  BCDBA.TBEVNTLOTPWIN (SEQ_NO ,SITE_CLSS,EVNT_NO,PWIN_GRD,PWIN_DATE ,JUMIN_NO,HG_NM,PROC_YN, CUPN_NO ,USE_NO, EA_INFO, HP_DDD_NO, HP_TEL_NO1, HP_TEL_NO2) ");
		 sql.append("\n	VALUES (EVNTLOTCTNT_SEQ.NEXTVAL,'10',?, ? ,TO_CHAR(SYSDATE,'yyyyMMdd'),?, ?, 1, ?, ? ,?, ?, ?, ? ) ");
		 return sql.toString();
	}
		
	/** ***********************************************************************
    *  �������� ���ó��
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
    *  �������н� �̺�Ʈ ��÷���̺��� ��������
    ************************************************************************ */
	public String cpnDelete(String couponNo){
		
		 StringBuffer sql = new StringBuffer();
		 
		 sql.append("\n	DELETE FROM BCDBA.TBEVNTLOTPWIN  ");
		 sql.append("\n	WHERE SITE_CLSS='10' AND EVNT_NO=?  ");		 
		 sql.append("\n	AND CUPN_NO IN (" + couponNo + ") ");
		 
		 return sql.toString();
	}
		
	/*************************************************************************
    *  �������н� �Ҵ� ���� ���� ���� �ݳ�
    ************************************************************************ */
	public String getCpnCancleUpdate(String couponNo){
		
		 StringBuffer sql = new StringBuffer();
		 
		 sql.append("\n	UPDATE BCDBA.TBEVNTUNIFCUPNINFO SET CUPN_PYM_YN ='N' WHERE SITE_CLSS='10' AND EVNT_NO=? AND CUPN_NO IN ("+couponNo+") ");

		 return sql.toString();
	}		

}
