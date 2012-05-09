/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ȸ������ó��
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0  
******************************************************************************/
public class GolfMemDelDaoProc extends AbstractProc {

	public static final String TITLE = "ȸ������ó��";

	public GolfMemDelDaoProc() {}
	
	public int execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		int result = 0; 

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			String payWay = data.getString("payWay");	//mn:��ȸ��
			int idx = 0;
			String tb_rslt_clss = "03";					// TM ���� - Ż��
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}
			else{
				userId = data.getString("userId").trim();	// �ܺ� ���� �ڵ� Ż�� ó���ϴ� ��� �߰� 2009.11.23
			}
			
			// ī��ȸ�� ��� ���� ��������
			String cardMemCnt = execute_card_grd_cnt(context, data, request);		// ī�� ��� ����
			debug("GolfMemDelDaoProc ::: cardMemCnt(ī�� ��� ����) : " + cardMemCnt);
			
			if(cardMemCnt.equals("0")){
				// ī�� ����� �������, Ż��ó��
				
				// Ż�������Ʈ => SECE_YN : Y, Ż���Ͻ� => SECE_ATON : YYYYMMDDHH24MISS
	            sql = this.getUpdateQuery();
				pstmt = conn.prepareStatement(sql);
	        	pstmt.setString(1, userId );
	        	
	        	result = pstmt.executeUpdate();
	        	
	        	if("mn".equals(payWay)){
	        		
	                // ��û ���̺� ������ ���� ���� ó��
	        		sql = this.getUpdateMnPayQuery();
	    			pstmt = conn.prepareStatement(sql);
	            	pstmt.setString(1, userId );
	            	pstmt.executeUpdate();

					// TM ���̺� ���� ���� 
					pstmt = conn.prepareStatement(getTmUpdQuery());
					idx = 0;
					pstmt.setString(++idx, tb_rslt_clss );
					pstmt.setString(++idx, userId );
					pstmt.executeUpdate();
	        	}
	        	
	            if(pstmt != null) pstmt.close();
				
			}else{
				// ī�� ����� �������, ����� ��� ����, ī�� ������� ��ǥ��� ����

				// ����� ��� ����
                sql = this.getDelMemGradQuery();
    			pstmt = conn.prepareStatement(sql);
            	pstmt.setString(1, userId );
            	
            	result = pstmt.executeUpdate();
	            	
				// ��ǥ��� ī��� ����, ����ȸ���Ⱓ ����
                sql = this.getUpdMemGrdQuery();
    			pstmt = conn.prepareStatement(sql);
            	pstmt.setString(1, userId );
            	pstmt.setString(2, userId );
            	
            	result = pstmt.executeUpdate();
            	
	            if(pstmt != null) pstmt.close();
				
			}
			
			
			if(result > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	public DbTaoResult execute_period(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		DbTaoResult result =  new DbTaoResult(title);
		ResultSet rs = null;

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			String userId = "";
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}
			else{
				userId = data.getString("userId").trim();	// �ܺ� ���� �ڵ� Ż�� ó���ϴ� ��� �߰� 2009.11.23
			}

			// ����ȸ������ �������� �Ѵ��� �Ѿ��°�?
			sql = this.getPeriodQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
            rs = pstmt.executeQuery();	
			
            if(rs.next()){
	            result.addString("ONE_MONTH_LATER", rs.getString("ONE_MONTH_LATER"));
	            if(GolfUtil.empty(rs.getString("APLC_SEQ_NO"))){
		            result.addString("payWay", "yr");
	            }else{
	            	result.addString("payWay", "mn");
	            }
	            result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	public DbTaoResult execute_money_cnt(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}
			else{
				userId = data.getString("userId").trim();	// �ܺ� ���� �ڵ� Ż�� ó���ϴ� ��� �߰� 2009.11.23
			}

			// ��� ������ ������ �������� �ʴ´�.
			sql = this.getMoneyCountQuery(userId); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();	
			if(rs.next()){
				result.addInt("MONEY_CNT", rs.getInt("MONEY_CNT"));
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	public DbTaoResult execute_cancel(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		DbTaoResult result =  new DbTaoResult(title);

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}

            sql = this.getCancelUpdateQuery();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
        	
			result_upd = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            			
			
			if(result_upd > 0) {				
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	public int execute_payUp(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int result = 0;

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// �������̺��� ������Ʈ ���ش�.

			String odr_no	= data.getString("ODR_NO").trim();		// 1:ī�� 2:ī��+����Ʈ
			sql = this.getPayUpdateQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, odr_no );
			result_upd = pstmt.executeUpdate();
        	
            if(pstmt != null) pstmt.close();
            			
			
			if(result_upd > 0) {	
				result = 1;
				conn.commit();
			} else {
				result = 0;
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}


	public int execute_payCancel(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int result = 0;
		int result_upd = 0;
		boolean payCancelResult = false;
		
		// ���� ����
		String odr_no = "";
		String sttl_amt = "";
		String mer_no = "";
		String card_no = "";
		String vald_date = "";
		String ins_mcnt = "";
		String auth_no = "";
		String ip = request.getRemoteAddr();
		String sttl_mthd_clss = "";
		String sttl_gds_clss = "";

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// ���� ������ �����´�.  

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);		 	
			String userId = "";	
			int intMemGrade = 0;
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}
			else{
				userId = data.getString("userId").trim();	// �ܺ� ���� �ڵ� Ż�� ó���ϴ� ��� �߰� 2009.11.23
			}

			sql = this.getPayViewQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );

            rs = pstmt.executeQuery();	
			if(rs != null) {
				while(rs.next())  {	
					
					odr_no = rs.getString("ORDER_NO");
					sttl_amt = rs.getString("STTL_AMT");
					mer_no = rs.getString("MER_NO");
					card_no = rs.getString("CARD_NO");
					vald_date = rs.getString("VALD_DATE");
					ins_mcnt = rs.getString("INS_MCNT");
					auth_no = rs.getString("AUTH_NO");
					sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
					sttl_gds_clss = rs.getString("STTL_GDS_CLSS");
					
					ip = request.getRemoteAddr();
					
					debug("====GolfMemDelActn======odr_no========> " + odr_no);
					debug("====GolfMemDelActn======STTL_AMT========> " + sttl_amt);
					debug("====GolfMemDelActn======MER_NO========> " + mer_no);
					debug("====GolfMemDelActn======CARD_NO========> " + card_no);
					debug("====GolfMemDelActn======VALD_DATE========> " + vald_date);
					debug("====GolfMemDelActn======INS_MCNT========> " + ins_mcnt);
					debug("====GolfMemDelActn======AUTH_NO========> " + auth_no);

					//���������� ��� ������ҳ��� skip
					if ("1001".equals(sttl_mthd_clss)) {
						payCancelResult = true;
					} 
					
					// ��ī�� �Ǵ� ���հ����� ���
					else if("0001".equals(sttl_mthd_clss) || "0002".equals(sttl_mthd_clss)) {
					
						payEtt.setMerMgmtNo(mer_no);		// ������ ��ȣ
						payEtt.setCardNo(card_no);			// ispī���ȣ
						payEtt.setValid(vald_date);			// ���� ����
						payEtt.setAmount(sttl_amt);				// �����ݾ�	
						payEtt.setInsTerm(ins_mcnt);		// �Һΰ�����
						payEtt.setRemoteAddr(ip);			// ip �ּ�
						payEtt.setUseNo(auth_no);			// ���ι�ȣ

						String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
						if( "211.181.255.40".equals(host_ip)) {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
							//payCancelResult=true;

						} else {

							payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
						}
						debug("====GolfMemDelActn======payCancelResult========> " + payCancelResult);
					}
					
					// Ÿ��ī�� �Ǵ� ������ü�� ���(�þ�����)
					else if("0003".equals(sttl_mthd_clss) || "0004".equals(sttl_mthd_clss)) {
					
						String payType = "";
						if("0003".equals(sttl_mthd_clss))			payType = "CARD";	// �ſ�ī��
						else if("0004".equals(sttl_mthd_clss))		payType = "ABANK";	// ������ü
						
						payEtt.setOrderNo(odr_no);			// �ֹ���ȣ
						payEtt.setAmount(sttl_amt);			// �����ݾ�	
						payEtt.setPayType(payType);			// �������

						payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		// ������� ȣ��	
						
						debug("====GolfMemDelActn======payCancelResult========> " + payCancelResult + " | odr_no : " + odr_no + " | sttl_amt : " + sttl_amt + " | payType : " + payType);
					}					
					
					if(payCancelResult){
						
						sql = this.getPayUpdateQuery(); 
			            pstmt = conn.prepareStatement(sql);
			        	pstmt.setString(1, odr_no );
						result_upd = pstmt.executeUpdate();
						
						debug("====GolfMemDelActn======ODR_NO========> " + odr_no);
						debug("====GolfMemDelActn======result_upd========> " + result_upd);

					}
					else{	// �������н� ���� ���� 2009.11.26
						
						GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
						
						DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
						dataSet.setString("CDHD_ID", userId);						//ȸ�����̵�
						dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//������������ڵ� :0001 : BCī�� / 0002:BCī�� + TOP����Ʈ / 0003:Ÿ��ī�� / 0004:������ü 
						dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//������ǰ�����ڵ� 0001:è�ǿ¿�ȸ�� 0002:��翬ȸ�� 0003:��忬ȸ�� 0008:����ȸ�� 
						dataSet.setString("STTL_STAT_CLSS", "Y");					//�������� N:�����Ϸ� / Y:�������
							
						int result_fail = payFailProc.failExecute(context, dataSet, request, payEtt);
						
						debug("====GolfMemDelActn======�������г���������========> " + result_fail);						
						
					}				
					
					debug("====GolfMemDelActn======payCancelResult========> " + payCancelResult);
					
				}
			}

			debug("====GolfMemDelActn======payCancelResult========> " + payCancelResult);
			debug("====GolfMemDelActn======result_upd========> " + result_upd);
        	
            if(pstmt != null) pstmt.close();
            if(rs != null) rs.close();

			if(result_upd > 0 && payCancelResult) {	
				result = 1;
				conn.commit();
			} else {
				result = 0;
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	public int execute_isCardMem(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int result = 0;

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// �������̺��� ������Ʈ ���ش�.

			String odr_no	= data.getString("ODR_NO").trim();		// 1:ī�� 2:ī��+����Ʈ
			sql = this.getCardGrdQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, odr_no );
			result_upd = pstmt.executeUpdate();
        	
            if(pstmt != null) pstmt.close();
            			
			
			if(result_upd > 0) {	
				result = 1;
				conn.commit();
			} else {
				result = 0;
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}



	// ī�� ��� ���� ��������
	public String execute_card_grd_cnt(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		String result = "0";

				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			String userId = "";
			if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount();
			}

			sql = this.getCardGrdQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userId );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				result = rs.getString("CARD_GRD_CNT");
			}
						
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
 	/** ***********************************************************************
	* �������� �Ѵ��� �Ѿ����� �����´�.
	************************************************************************ */
	private String getPeriodQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT CDHD.CDHD_ID, CDHD.ACRG_CDHD_JONN_DATE, TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMMDD') MONTH_AGO	\n");
		sql.append("\t	, CASE WHEN CDHD.ACRG_CDHD_JONN_DATE>TO_CHAR(ADD_MONTHS(SYSDATE,-1),'YYYYMMDD') THEN 'N' ELSE 'Y' END ONE_MONTH_LATER	\n");
		sql.append("\t	, LST.APLC_SEQ_NO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD CDHD	\n");
		sql.append("\t	LEFT JOIN BCDBA.TBGAPLCMGMT LST ON LST.CDHD_ID=CDHD.CDHD_ID AND LST.GOLF_SVC_APLC_CLSS='1001' AND LST.PGRS_YN='Y'	\n");
		sql.append("\t	WHERE CDHD.CDHD_ID=?	\n");
		return sql.toString();
	}
	
 	/** ***********************************************************************
	* ��û������ �ִ��� Ȯ�� 
	************************************************************************ */
	private String getMoneyCountQuery(String userId){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT (CNT_ABLE-CNT_DEL+CNT_RSV+CNT_PAY) MONEY_CNT	\n");
		sql.append("\t	FROM (SELECT	\n");
		sql.append("\t	(SELECT COUNT(*) CNT_ABLE FROM BCDBA.TBGAPLCMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE REG_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND GOLF_SVC_APLC_CLSS IN ('0006','0007','0008') AND PGRS_YN='Y') CNT_ABLE	\n");
		sql.append("\t	, (SELECT COUNT(*) CNT_DEL FROM BCDBA.TBGAPLCMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE REG_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND GOLF_SVC_APLC_CLSS IN ('0006','0007','0008') AND PGRS_YN='N')  CNT_DEL	\n");
		sql.append("\t	, (SELECT COUNT(*) CNT_RSV FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE REG_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1) IN ('M','P','S','D') AND RSVT_YN='Y')  CNT_RSV	\n");
		sql.append("\t	, (SELECT COUNT(*) CNT_PAY FROM BCDBA.TBGSTTLMGMT T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE STTL_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		//sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND STTL_GDS_CLSS NOT IN (SELECT REPLACE(CDHD_SQ2_CTGO,'0007','0008') FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE ANL_FEE>0 UNION SELECT '0004' FROM DUAL) AND STTL_STAT_CLSS='Y') CNT_PAY	\n");
		sql.append("\t	AND T1.CDHD_ID='"+userId+"' AND ( STTL_GDS_CLSS NOT IN (SELECT REPLACE(CDHD_SQ2_CTGO,'0007','0008') FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE ANL_FEE>0 UNION SELECT '0004' FROM DUAL) AND STTL_STAT_CLSS='Y'	\n");		
		sql.append("\t										AND STTL_GDS_CLSS != '0009') ) CNT_PAY	\n");
		sql.append("\t	FROM DUAL)	\n");
		
		return sql.toString();
	}
	
 	/** ***********************************************************************
	* ���� ��û������ �ִ��� Ȯ�� 
	************************************************************************ */
	private String getMoneyMinusQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT COUNT(*) MONEY_CNT2 FROM (	\n");
		sql.append("\t	    SELECT CDHD_ID, SUBSTR(REG_ATON,1,8) REG_ATON FROM (	\n");
		sql.append("\t	        SELECT CDHD_ID, REG_ATON FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS IN ('0006','0007','0008') AND PGRS_YN='N'	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	) T1	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID	\n");
		sql.append("\t	WHERE REG_ATON BETWEEN ACRG_CDHD_JONN_DATE AND ACRG_CDHD_END_DATE	\n");
		sql.append("\t	AND T1.CDHD_ID=?	\n");
		return sql.toString();
	}

    /** ***********************************************************************
    * ȸ�� ���� ������Ʈ    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET								\n");
 		sql.append("\t	SECE_YN='Y', SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	WHERE CDHD_ID=?												\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * ��ȸ��ȸ�� ������ ��ȸ�� ��û���� ����ó��  
    ************************************************************************ */
    private String getUpdateMnPayQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	UPDATE BCDBA.TBGAPLCMGMT SET PGRS_YN='N'	\n");
 		sql.append("\t	, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	WHERE CDHD_ID=? AND GOLF_SVC_APLC_CLSS='1001' AND PGRS_YN='Y'	\n");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * TM ���� ����
     ************************************************************************ */
 	private String getTmUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS=? WHERE RND_CD_CLSS='2' AND RCRU_PL_CLSS='5000' AND JUMIN_NO=(SELECT JUMIN_NO FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?)	\n");
 		return sql.toString();
 	}

    /** ***********************************************************************
    * ȸ�� ���� ������Ʈ    
    ************************************************************************ */
    private String getCancelUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET								\n");
 		sql.append("\t	SECE_YN='', SECE_ATON=''	\n");
 		sql.append("\t	WHERE CDHD_ID=?												\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * ���� ��� ������Ʈ    
    ************************************************************************ */
    private String getPayUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGSTTLMGMT	\n");
 		sql.append("\t	SET STTL_STAT_CLSS='Y', CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	WHERE ODR_NO=?	\n");
        return sql.toString();
    }

 	/** ***********************************************************************
	* �������� ��������
	************************************************************************ */
	private String getPayViewQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT ODR_NO AS ORDER_NO, STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT, AUTH_NO ,STTL_MTHD_CLSS, STTL_GDS_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGSTTLMGMT T_PAY	\n");
		sql.append("\t	JOIN BCDBA.TBGGOLFCDHD T_MEM ON T_PAY.CDHD_ID=T_MEM.CDHD_ID	\n");
		sql.append("\t	WHERE T_PAY.CDHD_ID=? AND STTL_STAT_CLSS='N'	\n");
		sql.append("\t	AND STTL_GDS_CLSS IN (SELECT REPLACE(CDHD_SQ2_CTGO,'0007','0008') FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE ANL_FEE>0 UNION SELECT '0004' FROM DUAL)	\n");
		sql.append("\t	AND STTL_ATON BETWEEN ACRG_CDHD_JONN_DATE AND TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
		sql.append("\t	ORDER BY STTL_ATON DESC	\n");
		return sql.toString();
	}

 	/** ***********************************************************************
	* ī�� ����� �ִ��� �˾ƺ���
	************************************************************************ */
	private String getCardGrdQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  SELECT COUNT(*) CARD_GRD_CNT	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE CTGO.CDHD_SQ1_CTGO='0001' AND GRD.CDHD_ID=?	\n");
		return sql.toString();
	}
		
 	/** ***********************************************************************
	* ����� ��� ����
	************************************************************************ */
	private String getDelMemGradQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
		sql.append("\t  AND CDHD_CTGO_SEQ_NO IN (SELECT CDHD_CTGO_SEQ_NO FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_SQ1_CTGO='0002')	\n");
		return sql.toString();
	}

 	/** ***********************************************************************
	* ��ǥ��� ������Ʈ 
	************************************************************************ */
	private String getUpdMemGrdQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD SET ACRG_CDHD_JONN_DATE='', ACRG_CDHD_END_DATE=''	\n");
		sql.append("\t  , CDHD_CTGO_SEQ_NO = (	\n");
		sql.append("\t      SELECT CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t      FROM (	\n");
		sql.append("\t          SELECT GRD.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t          FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
		sql.append("\t          JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t          WHERE CTGO.CDHD_SQ1_CTGO='0001' AND GRD.CDHD_ID=?	\n");
		sql.append("\t          ORDER BY SORT_SEQ DESC	\n");
		sql.append("\t      ) WHERE ROWNUM=1	\n");
		sql.append("\t  )	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
		return sql.toString();
	}
		
		
}
