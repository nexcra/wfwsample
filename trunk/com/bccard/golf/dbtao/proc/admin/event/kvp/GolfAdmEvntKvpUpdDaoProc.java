/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntKvpUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > ��
*   �������  : golf
*   �ۼ�����  : 2010-06-04
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.kvp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;


/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmEvntKvpUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ > �̺�Ʈ > Kvp > ����ó��";

	/** *****************************************************************
	 * GolfAdmOrdUpdDaoProc ���μ��� ������
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmEvntKvpUpdDaoProc() {}

	/** *****************************************************************
	 * execute ���࿩�� ����
	 ***************************************************************** */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		PreparedStatement pstmt_mem = null;
		int idx = 0;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no = data.getString("aplc_seq_no");	
			String pgrs_yn = data.getString("pgrs_yn");
			String jumin_no = data.getString("jumin_no");
			String cslt_yn = data.getString("cslt_yn");
			String cdhd_id = data.getString("cdhd_id");
			String mem_upd = "N";
			String tb_rslt_clss = "";	// TM��������ڵ�
			int cmmCode = data.getInt("cmmCode");
						
			//kvp ��ȸ��
			if (cmmCode != Integer.parseInt(AppConfig.getDataCodeProp("0005CODE11"))){
			
				if(pgrs_yn.equals("Y")){
					if(cslt_yn.equals("Y")){
						tb_rslt_clss = "00";
						// ȸ�� ������������ : ���۳�¥+365
						pstmt_mem = conn.prepareStatement(getMemPlusUpdQuery());
						mem_upd = "Y";
					}else{
						tb_rslt_clss = "01";
					}
				}else{
					tb_rslt_clss = "03";
					if(cslt_yn.equals("Y")){
						// ȸ�� ������������ : ����
						pstmt_mem = conn.prepareStatement(getMemMinusUpdQuery());
						mem_upd = "Y";
					}
				}
	
				// ��û���̺� ���� ����
				pstmt = conn.prepareStatement(getUpdQuery());
				idx = 0;
				pstmt.setString(++idx, pgrs_yn );
				pstmt.setString(++idx, aplc_seq_no );
				result += pstmt.executeUpdate();
				
				// TM ���̺� ���� ���� getTmUpdQuery
				pstmt = conn.prepareStatement(getTmUpdQuery());
				idx = 0;
				pstmt.setString(++idx, tb_rslt_clss );
				pstmt.setString(++idx, jumin_no );
				result += pstmt.executeUpdate();
				
				// ȸ�� ��������Ⱓ ������ ������Ʈ 
				if(mem_upd.equals("Y") && !cdhd_id.equals("�̰���")){
					idx = 0;
					pstmt_mem.setString(++idx, cdhd_id );
					result += pstmt_mem.executeUpdate();
				}
							
			//����Ʈ ��ȸ��
			}else {
				
				if(pgrs_yn.equals("N")){			              
					result = exeGrdAction(conn, cdhd_id, pgrs_yn, aplc_seq_no);			      
			    }			    
			}			
			
			debug("result : " + result);
				
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			//
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
            
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
		
	}
	

	private int exeGrdAction(Connection con, String cdhdId, String pgrs_yn, String aplc_seq_no) throws DbTaoException {

		int cnt = 0;
	    int idx = 0;
	    int result = 0;
	    ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try{
	       
			//ȸ�� ���� ��� ����
			idx = 0;
			pstmt = con.prepareStatement(getGrdCnt());
			pstmt.setString(++idx, cdhdId );
			rs = pstmt.executeQuery(); 
	 
			if (rs.next()){     
				cnt = rs.getInt("CNT");       
			}
	   
			//���ó��
			if(cnt > 1 ){
	     
				//������ ��� �����丮 ���
				idx = 0;
				pstmt = con.prepareStatement(inGrdHistoryQuery());   
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate();   
				if(pstmt != null) pstmt.close();             
	 
				//��� ����
				idx = 0;
				pstmt = con.prepareStatement(delGrd());
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate(); 
				if(pstmt != null) pstmt.close();
				
				//��ǥ��� ����				
				idx = 0;
				pstmt = con.prepareStatement(exeUpdTopGrade1());
				pstmt.setString(++idx, cdhdId);
				pstmt.setString(++idx, cdhdId);	
				pstmt.executeUpdate();	
				if(pstmt != null) pstmt.close(); 
		        info(" || cdhdId : "+ cdhdId + " | ��ǥ���  ����");						
	     
			}else if( cnt == 1){
	    
				//������ ��� �����丮 ���
				idx = 0;
				pstmt = con.prepareStatement(inGrdHistoryQuery());   
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate();   
				if(pstmt != null) pstmt.close();    
	 
				//ȭ��Ʈ ������� ����
				idx = 0;
				pstmt = con.prepareStatement(updateGrd());
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate(); 
				if(pstmt != null) pstmt.close(); 
				
				//��ǥ��� ����
				idx = 0;
				pstmt = con.prepareStatement(exeUpdTopGrade2());
				pstmt.setInt(++idx, 8);
				pstmt.setString(++idx, cdhdId);	
				pstmt.executeUpdate();		
				if(pstmt != null) pstmt.close(); 
		        info(" || cdhdId : "+ cdhdId + " | ��ǥ��� [8]���� ����");				
	   
			}
	  
			if (cnt > 0 ){    
				// ��û���̺� ���� ����
				pstmt = con.prepareStatement(getUpdQuery());
				idx = 0;
				pstmt.setString(++idx, pgrs_yn );
				pstmt.setString(++idx, aplc_seq_no );
				result = pstmt.executeUpdate();  
	     
			} else {
				// ������� ���� �����ϴ�. 
				result = 0;
				//���ϸ޽��� Ȯ������
				
				debug(" not date ");
			}
			
			if(rs != null) rs.close();   
			if(pstmt != null) pstmt.close();
	   
		} catch(Exception e) {
		
			try {
				con.rollback();
		   }catch (Exception c){
	   
		   		MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, "", "�ý��ۿ����Դϴ�." );
		        throw new DbTaoException(msgEtt,e);
		        
		   }
	        
	  } finally {
	  
		   try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
		   try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	  }
	  
	  return result;
	  
	 } 	


	/** *****************************************************************
	 * execute_del ���� ��� 
	 ***************************************************************** */
	public String execute_cancel(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		int result_true = 0;
		int result_false = 0;
		String str_result = "";
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(true);
			
			String pay_no 		= data.getString("pay_no");
			String aplc_seq_no = data.getString("aplc_seq_no");	
			
			debug("pay_no : " + pay_no);
			String [] arr_pay_no = GolfUtil.split(pay_no, "||");
			int cmmCode = data.getInt("cmmCode");

    		// ��ȸ�� ȯ��ó�� ���ش�.
    		boolean payCancelResult = false;
    		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
    		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

			// ��������
			String odr_no = "";						// �ֹ���ȣ
			String sttl_amt = "";					// �����ݾ�
			String mer_no = "";						// ��������ȣ
			String card_no = "";					// ī���ȣ
			String vald_date = "";					// ��ȿ����
			String ins_mcnt = "";					// �Һΰ�����
			String auth_no = "";					// ���ι�ȣ
			String ip = request.getRemoteAddr();	// �ܸ���ȣ(IP, '.'����)
			String sttl_mthd_clss = "";				// ������������ڵ�
			String sttl_gds_clss = "";				// ������ǰ�����ڵ�
			String cdhd_id = "";
			
			//Smart1000��ȸ��ó��		
			if (cmmCode == Integer.parseInt(AppConfig.getDataCodeProp("0005CODE11"))){   
				
				// ���� ���� ��ȸ
				pstmt = conn.prepareStatement(setPayBackListQuery());
				pstmt.setString(1, arr_pay_no[0] );
				rs = pstmt.executeQuery(); 
	
				if (rs.next()){
		
					odr_no = rs.getString("ODR_NO");
					sttl_amt = rs.getString("STTL_AMT");
					mer_no = rs.getString("MER_NO");
					card_no = rs.getString("CARD_NO");
					vald_date = rs.getString("VALD_DATE");
					ins_mcnt = rs.getString("INS_MCNT");
					auth_no = rs.getString("AUTH_NO");
					sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
					sttl_gds_clss = rs.getString("STTL_GDS_CLSS");
					cdhd_id = rs.getString("CDHD_ID");					
				
					// ��ī�� ������ ���
					if("0001".equals(sttl_mthd_clss)) {
		
						payEtt.setMerMgmtNo(mer_no);  // ������ ��ȣ
						payEtt.setCardNo(card_no);   // ispī���ȣ
						payEtt.setValid(vald_date);   // ���� ����
						payEtt.setAmount(sttl_amt);   // �����ݾ� 
						payEtt.setInsTerm(ins_mcnt);  // �Һΰ�����
						payEtt.setRemoteAddr(ip);   // ip �ּ�
						payEtt.setUseNo(auth_no);   // ���ι�ȣ
			
						String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
						
						if( "211.181.255.40".equals(host_ip)) {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);   // ������� ȣ��
						} else {
							payCancelResult = payProc.executePayAuthCancel(context, payEtt);   // ������� ȣ��
						}
						
						debug("payCancelResult========> " + payCancelResult);
						
					} 
					
					if(payCancelResult){
						
						exeGrdAction(conn, cdhd_id, "N", aplc_seq_no);		
						
						sql = this.getPayUpdateQuery(); 
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, odr_no );
						result_true += pstmt.executeUpdate();
				
					// �������н� ���� ����
					}else{		
						
						GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");      
			
						DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);      
						dataSet.setString("CDHD_ID", cdhd_id);      //ȸ�����̵�
						dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);  //������������ڵ� :0001 : BCī�� / 0002:BCī�� + TOP����Ʈ / 0003:Ÿ��ī�� / 0004:������ü 
						dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);   //������ǰ�����ڵ� 0001:è�ǿ¿�ȸ�� 0002:��翬ȸ�� 0003:��忬ȸ�� 0008:����ȸ�� 
						dataSet.setString("STTL_STAT_CLSS", "Y");     //�������� N:�����Ϸ� / Y:�������		
						
						result_false += payFailProc.failExecute(context, dataSet, request, payEtt);
			
						debug("�������г���������==> " + result_false);
						
					}
		
					debug("ODR_NO : " + odr_no + " / result_true : " + result_true +" / result_false : " + result_false);    
					//*****************************************************************************//*
				}   

			// KVP��ȸ�� ó��
			}else {				

				for(int i=0; i<arr_pay_no.length; i++){
					
					// ���� ���� ��ȸ
		            pstmt = conn.prepareStatement(setPayBackListQuery());
					pstmt.setString(1, arr_pay_no[i] );
		            rs = pstmt.executeQuery();	
		            
		            while(rs.next()){
		            	
		            	payCancelResult = false;
		
		            	odr_no = rs.getString("ODR_NO");
						sttl_amt = rs.getString("STTL_AMT");
						mer_no = rs.getString("MER_NO");
						card_no = rs.getString("CARD_NO");
						vald_date = rs.getString("VALD_DATE");
						ins_mcnt = rs.getString("INS_MCNT");
						auth_no = rs.getString("AUTH_NO");
						sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
						sttl_gds_clss = rs.getString("STTL_GDS_CLSS");
						cdhd_id = rs.getString("CDHD_ID");
		
						// ��ī�� �Ǵ� ���հ����� ���
						if("0001".equals(sttl_mthd_clss) || "0002".equals(sttl_mthd_clss)) {
						
							payEtt.setMerMgmtNo(mer_no);		// ������ ��ȣ
							payEtt.setCardNo(card_no);			// ispī���ȣ
							payEtt.setValid(vald_date);			// ���� ����
							payEtt.setAmount(sttl_amt);			// �����ݾ�	
							payEtt.setInsTerm(ins_mcnt);		// �Һΰ�����
							payEtt.setRemoteAddr(ip);			// ip �ּ�
							payEtt.setUseNo(auth_no);			// ���ι�ȣ
		
							String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
							if( "211.181.255.40".equals(host_ip)) {
								payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
							} else {
								payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
							}
							debug("payCancelResult========> " + payCancelResult);
						}
						
						// Ÿ��ī�� �Ǵ� ������ü�� ���(�þ�����)
						else if("0003".equals(sttl_mthd_clss) || "0004".equals(sttl_mthd_clss)) {
						
							String payType = "";
							if("0003".equals(sttl_mthd_clss))			payType = "CARD";	// �ſ�ī��
							else if("0004".equals(sttl_mthd_clss))		payType = "ABANK";	// ������ü
		
							String sShopid  = "bcgolf";							// �����̵�
							String sCrossKey  = "e3f8453680e39fc1d6dfe72079874219";	// ũ�ν�Ű
							
							payEtt.setOrderNo(odr_no);			// �ֹ���ȣ
							payEtt.setAmount(sttl_amt);			// �����ݾ�	
							payEtt.setPayType(payType);			// �������
							payEtt.setShopId(sShopid);
							payEtt.setCrossKey(sCrossKey);
		
							payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		// ������� ȣ��	
							
							debug("payCancelResult========> " + payCancelResult + " | odr_no : " + odr_no + " | sttl_amt : " + sttl_amt + " | payType : " + payType);
						}				
						
						if(payCancelResult){
							
							sql = this.getPayUpdateQuery(); 
				            pstmt = conn.prepareStatement(sql);
				        	pstmt.setString(1, odr_no );
				        	result_true += pstmt.executeUpdate();
							
							debug("ODR_NO : " + odr_no + " / result_true : " + result_true);
			
						}else{	// �������н� ���� ���� 2009.11.26
							
							GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
							
							DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
							dataSet.setString("CDHD_ID", cdhd_id);						//ȸ�����̵�
							dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//������������ڵ� :0001 : BCī�� / 0002:BCī�� + TOP����Ʈ / 0003:Ÿ��ī�� / 0004:������ü 
							dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//������ǰ�����ڵ� 0001:è�ǿ¿�ȸ�� 0002:��翬ȸ�� 0003:��忬ȸ�� 0008:����ȸ�� 
							dataSet.setString("STTL_STAT_CLSS", "Y");					//�������� N:�����Ϸ� / Y:�������
								
							result_false += payFailProc.failExecute(context, dataSet, request, payEtt);
							
							debug("�������г���������==> " + result_false);		
							
						}
		            }		
			        //*****************************************************************************//*
					debug("ODR_NO : " + odr_no + " / result_true : " + result_true +" / result_false : " + result_false);
				}
			
			}
			
			if(payCancelResult) {
				conn.commit();
				str_result = arr_pay_no.length + "�� �� " + "���� : "+result_true+", ���� : "+result_false+" �Դϴ�.";
			} else {
				
				conn.rollback();	

				if ( result_false> 0 ) {
					str_result = "���� ��� ���� => �����ڵ� : "+ payEtt.getResCode() + " ����޼��� : " + payEtt.getResMsg() + " �Դϴ�.";
				}
				
			}	   			
						
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return str_result;
	}
								

	/** ***********************************************************************
	 * ���� ���� ��������
	 ************************************************************************ */
	private String setPayBackListQuery(){
		StringBuffer sql = new StringBuffer();        
		sql.append("\n	SELECT ODR_NO, STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT, AUTH_NO, STTL_MTHD_CLSS, STTL_GDS_CLSS, CDHD_ID	\n");
		sql.append("\t	FROM BCDBA.TBGSTTLMGMT	\n");
		sql.append("\t	WHERE ODR_NO=?	\n");
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
     * ���࿩�� ����
     ************************************************************************ */
 	private String getUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBGAPLCMGMT SET PGRS_YN=? WHERE APLC_SEQ_NO=?	\n");		
 		return sql.toString();
 	}
    
    /** ***********************************************************************
     * TM ���� ����
     ************************************************************************ */
 	private String getTmUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS=? WHERE RND_CD_CLSS='2' AND RCRU_PL_CLSS='5000' AND JUMIN_NO=?	\n");
 		return sql.toString();
 	}
    
    /** ***********************************************************************
     * ����ȸ���������� : ����
     ************************************************************************ */
 	private String getMemMinusUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHD SET ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE-1,'YYYYMMDD') WHERE CDHD_ID=?	\n");
 		return sql.toString();
 	}
 	
    
    /** ***********************************************************************
     * ����ȸ���������� : ��������+365
     ************************************************************************ */
 	private String getMemPlusUpdQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHD SET ACRG_CDHD_END_DATE=TO_CHAR(TO_DATE(ACRG_CDHD_JONN_DATE)+365,'YYYYMMDD') WHERE CDHD_ID=?	\n");
 		return sql.toString();
 	}
 	
 	
 	/*************************************************************************
 	* ȸ�� ���� ��� ����
 	************************************************************************ */ 
 	private String getGrdCnt(){

	 	StringBuffer sql = new StringBuffer();
	
	 	sql.append(" \n");
	 	sql.append("\t  SELECT COUNT(*) CNT \n");
	 	sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
	 	sql.append("\t  WHERE CDHD_ID=? \n");
	
	 	return sql.toString();

 	}  
 	
 	
    /*************************************************************************
     * ��� �����丮 ���̺� �μ�Ʈ -> ���泻�� Ȯ��
     ************************************************************************ */    
 	private String inGrdHistoryQuery(){
	   
 		StringBuffer sql = new StringBuffer();
	   
		sql.append(" \n");
		sql.append("\t  INSERT INTO BCDBA.TBGCDHDGRDCHNGHST \n");
		sql.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST) \n");
		sql.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') \n");
		sql.append("\n  , B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL ");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD \n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO \n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHD B ON GRD.CDHD_ID=B.CDHD_ID \n");
		sql.append("\t  WHERE GRD.CDHD_ID=? AND GRD.CDHD_CTGO_SEQ_NO = 27 \n");
		   
		return sql.toString();   
		   
	}   
  
 	/*************************************************************************
 	* ����Ʈ��� ����
 	************************************************************************ */    
 	private String delGrd(){

	 	StringBuffer sql = new StringBuffer();
	
	 	sql.append(" \n");
	 	sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
	 	sql.append("\t  WHERE CDHD_ID=?  \n");
	 	sql.append("\t  AND CDHD_CTGO_SEQ_NO = '27' \n");
	
	 	return sql.toString();   

 	}     


 	/*************************************************************************
 	* ����Ʈ�� ȭ��Ʈ�� ����
 	************************************************************************ */    
 	private String updateGrd(){

	 	StringBuffer sql = new StringBuffer();
	
	 	sql.append(" \n");
	 	sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT \n");
	 	sql.append("\t  SET CDHD_CTGO_SEQ_NO = '8' , CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') \n");
	 	sql.append("\t  WHERE CDHD_ID=?  \n");
	
	 	return sql.toString();   

 	}     

 	/*************************************************************************
 	* ��ǥ��� ����
 	************************************************************************ */    	
	private String exeUpdTopGrade1(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD																			\n");				
		sql.append("\t	SET JOIN_CHNL ='0001', CDHD_CTGO_SEQ_NO=(SELECT CDHD_CTGO_SEQ_NO														\n");
		sql.append("\t						  FROM (	 																	\n");
		sql.append("\t								SELECT  GRD.CDHD_CTGO_SEQ_NO											\n");
		sql.append("\t								FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD, BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO		\n");
		sql.append("\t								WHERE CDHD_ID = ?														\n");
		sql.append("\t								AND  GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO						\n");
		sql.append("\t								ORDER BY T_CTGO.SORT_SEQ ASC											\n");
		sql.append("\t								 )																		\n");
		sql.append("\t						 WHERE ROWNUM = 1																\n");
		sql.append("\t						 )																				\n");
		sql.append("\t	WHERE CDHD_ID = ?																					\n");
		
		return sql.toString();
		
	} 	 	
 
 	/*************************************************************************
 	* ��ǥ��� ����
 	************************************************************************ */   	
	private String exeUpdTopGrade2(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO=? , JOIN_CHNL ='0001' \n");		
		sql.append("\t	WHERE CDHD_ID = ?	\n");
		
		return sql.toString();
		
	}
	
}


