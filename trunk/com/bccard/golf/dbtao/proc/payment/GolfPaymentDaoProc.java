/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAuthEtt
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ���� ���� ���μ���
*   �������  : golf
*   �ۼ�����  : 2009-06-12
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.payment;

import java.util.HashMap;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AllatUtil;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;
import com.inicis.crypto.SymEncDec;


public class GolfPaymentDaoProc extends AbstractObject {

	public static final String TITLE = "���� ���μ���";
	private final String BSXINPT   = "BSXINPT";					// �����ӿ� ��ȸ���� 
	/**
	 * Constructor.
	 */
	public GolfPaymentDaoProc() {
		
	}
	
	/**
	 * �������� ����.
	 * @param context WaContext
	 * @param seqNo �Ϸù�ȣ
	 * @param ett EtaxAuthEtt
	 * @param cardno ī���ȣ
	 * @param valid ��ȿ�Ⱓ
	 * @param remoteAddr ������
	 * @param auth ���ο��� (������ �ƴϸ� ����)
	 * @return ���ι�ȣ
	 * @throws EtaxException
	 */
	public boolean executePayAuth(WaContext context, HttpServletRequest request, GolfPayAuthEtt ett) throws GolfException { 
		JoltInput entity = null;
		TaoResult taoResult= null;
		
		boolean result = false;
		debug(" executeAuth start!!! ");
		
		String fml_arg1	 = "1080";											// ������ȣ(�⺻��)
		String fml_arg2  = ett.getMerMgmtNo();								// ��������ȣ
		String fml_arg3  = ett.getCardNo();									// ī���ȣ
		String fml_arg4  = ett.getValid();									// ��ȿ�Ⱓ. �⵵�� 2�ڸ� ���� (200507 �� 0507) 
	
		String fml_arg5  = ett.getAmount();									// ���αݾ�
		String fml_arg6  = ett.getInsTerm();								// �ҺαⰣ(00:�Ͻú�, 60:Point)
		String fml_arg7  = StrUtil.replace(ett.getRemoteAddr(), ".", "");	// �ܸ���ȣ(IP, '.'����)
		String fml_arg8  = " ";												// ����������������ȣ(����ó��)
		String fml_arg9  = "2";												// �����ڵ�(1:����, 2:����)
		String fml_arg10 = "35";											// ��������(35:Web����, 26:����, 25:����, 24:����)
		
		try {			

			entity = new JoltInput();

			entity.setServiceName(BSXINPT);
			entity.setString("fml_trcode", "MGA0030I0700");
			entity.setString("fml_channel", "WEB");
			entity.setString("fml_sec50", "UNKNOWN");
			entity.setString("fml_sec51", ett.getRemoteAddr());
			
			entity.setString("fml_arg1",  fml_arg1 );
			entity.setString("fml_arg2",  fml_arg2 );
			entity.setString("fml_arg3",  fml_arg3 );
			entity.setString("fml_arg4",  fml_arg4 );
			entity.setString("fml_arg5",  fml_arg5 );
			entity.setString("fml_arg6",  fml_arg6 );
			entity.setString("fml_arg7",  fml_arg7 );
			entity.setString("fml_arg8",  fml_arg8 );
			entity.setString("fml_arg9",  fml_arg9 );
			entity.setString("fml_arg10", fml_arg10);

			debug("+2++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");		

			debug(" UGA003_TE_AUTHProc entity= ["+entity+"]");

			JtTransactionProc pgProc = null;

     		pgProc = (JtTransactionProc)context.getProc("UGA003_TE_AUTH");
			info("GOLF_JAE START:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5  );

			JoltOutput output = pgProc.execute(context,entity);


			if(output == null){
				info("GOLF_JAE END:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5 +"|null" );
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"GOLF.AUTH.FAIL", null);
				throw new GolfException(msgEtt);
			}
			
			if (output.containsKey("fml_ret5") && output.containsKey("fml_ret6")) { // ��������̸�
				String fml_ret5 = output.getString("fml_ret5").trim();
				String fml_ret6 = output.getString("fml_ret6").trim();
				ett.setResCode(fml_ret5);
				ett.setResMsg(fml_ret6);
				if ("1".equals(fml_ret5))	{
					result = true;
					ett.setUseNo(output.getString("fml_ret4"));		// ���ι�ȣ
					ett.setBankNo(output.getString("fml_ret14"));	// ȸ�����ȣ
				} 
			} else {
				result = false;
			}
			
			info ("GOLF_JAE END:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5 +"|" + result);

			debug("+3++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");		
			
		} catch(GolfException e) {
			throw e;
		} catch (Throwable t) {
			throw new GolfException(TITLE,t);
		} finally {
		}
		
		return result;
	}


	/**
	 * ������� ����.
	 * @param context WaContext
	 * @param seqNo �Ϸù�ȣ
	 * @param ett EtaxAuthEtt
	 * @param cardno ī���ȣ
	 * @param valid ��ȿ�Ⱓ
	 * @param remoteAddr ������
	 * @param auth ���ο��� (������ �ƴϸ� ����)
	 * @return ��ҿ���
	 * @throws EtaxException
	 */
	public boolean executePayAuthCancel(WaContext context, GolfPayAuthEtt ett) throws GolfException {
		boolean result = false;
		debug(" executePayAuthCancel start!!! ");

		String fml_arg1	 = "1090";												// ������ȣ(�⺻��)
		String fml_arg2  = ett.getMerMgmtNo();									// ��������ȣ
		String fml_arg3  = ett.getCardNo();										// ī���ȣ
		String fml_arg4  = ett.getValid();										// ��ȿ�Ⱓ. �⵵�� 2�ڸ� ���� (200507 �� 0507) 
		String fml_arg5  = ett.getAmount();										// ���αݾ�
		String fml_arg6  = ett.getInsTerm();									// �ҺαⰣ(00:�Ͻú�, 60:Point)-> 60 �϶��� ����Ҷ��� ������ 00 ó��
		if(GolfUtil.empty(fml_arg6)){
			fml_arg6 = "00";
		}else{
			if(fml_arg6.equals("60")){
				fml_arg6 = "00";
			}
		}
		//String fml_arg6  = "00";												// �ҺαⰣ(00:�Ͻú�, 60:Point) -> ����Ҷ��� ������ 00 ó�� - 20090811 
		String fml_arg7  = StrUtil.replace(ett.getRemoteAddr(), ".", "");		// �ܸ���ȣ(IP, '.'����)
		String fml_arg8  = "���ȯ";												// ����������������ȣ(����ó��)
		String fml_arg9  = "19931311";											// ���������� ������~ȣ(����ó��)
		String fml_arg10 = ett.getUseNo();										// ���ι�ȣ
		String fml_arg11 = "35";												// ��������(35:Web����, 26:����, 25:����, 24:����)
		try {
			if (fml_arg6.length() < 2)		{ fml_arg6 = "0"+fml_arg6; }
			if (fml_arg7.length() > 11)		{ fml_arg7 = fml_arg7.substring(0,11); }

			// INPUT ENTITY GENERATE
			debug(" executePayAuthCancel start >> ADMIN F/W JoltInput!!! ");

			JoltInput entity = new JoltInput();
			entity.setServiceName("BSXINPT");
			entity.setString("fml_trcode", "MGA0030I0800");
			entity.setString("fml_channel", "WEB");
			entity.setString("fml_sec50", " ");
			entity.setString("fml_sec51", ett.getRemoteAddr());
//			JoltInput entity = null;											// Jolt Input Entity
//			entity = new JoltInput("UGA003_TE_CAN");
//			  entity.setServiceName("UGA003_TE_CAN");
			  entity.setString("fml_arg1",  fml_arg1 );						// ������ȣ(�⺻��)
			  entity.setString("fml_arg2",  fml_arg2 );						// ��������ȣ
			  entity.setString("fml_arg3",  fml_arg3 );						// ī���ȣ
			  entity.setString("fml_arg4",  fml_arg4 );						// ��ȿ�Ⱓ
			  entity.setString("fml_arg5",  fml_arg5 );						// ���αݾ�
			  entity.setString("fml_arg6",  fml_arg6 );						// �ҺαⰣ(00:�Ͻú�, 60:Point)
			  entity.setString("fml_arg7",  fml_arg7 );						// �ܸ���ȣ(IP Address, '.'����)
			  entity.setString("fml_arg8",  fml_arg8 );						// ���������� ��û��
			  entity.setString("fml_arg9",  fml_arg9 );						// ���������� ������~ȣ(����ó��)
			  entity.setString("fml_arg10", fml_arg10);						// ���ι�ȣ
			  entity.setString("fml_arg11", fml_arg11);						// ��������(35:Web��~��)

			debug(" UGA003_TE_CANProc entity= ["+entity+"]");

			JtTransactionProc pgProc = null;

     		pgProc = (JtTransactionProc)context.getProc("UGA003_TE_CAN");
			info ("GOLF_JAE:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5  );

			debug(" UGA003_TE_CANProc pgProc= ["+pgProc+"]");

			JoltOutput output = pgProc.execute(context,entity);


			if(output == null){
				info ("GOLF_JAE:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5 +"|" + result);
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.AUTH.FAIL", null);
				throw new GolfException(msgEtt);
			}
			if (output.containsKey("fml_ret2") && "2".equals(output.getString("fml_ret2"))) { // ��������̸�
				result = true;
				ett.setResCode(output.getString("fml_ret2"));
				ett.setResMsg(output.getString("fml_ret3"));
			} else {
				ett.setResCode(output.getString("fml_ret2"));
				ett.setResMsg(output.getString("fml_ret3"));
				result = false;
			}

			info ("GOLF_JAE:" + fml_arg2 + "|" + fml_arg3 + "|" + fml_arg4 + "|" + fml_arg5 +"|" + result);

		} catch(GolfException e) {
			throw e;
		} catch (Throwable t) {
			throw new GolfException(TITLE,t);
		} finally {
		}
		
		return result;
	}

	
	/**
	 * ���� ��û - �þ�����
	 */
	public boolean executePayAuth_Allat(WaContext context, HttpServletRequest request, GolfPayAuthEtt ett) throws GolfException { 
		
		boolean result = false;
		debug("## GolfPaymentDaoProc | executeAuth AllatPay START!!! ");
	
		  String sAmount   		= ett.getAmount();              			// ���αݾ�
		  String sEncData  		= ett.getEncData();							// submit��ȣȭ��
		  String sShopid  		= ett.getShopId();							// �����̵�
		  String sCrossKey 		= ett.getCrossKey();						// ũ�ν�Ű
		  String strReq 		= "";
		  String sPayType		= "";										// ���Ҽ���
		  String sApprovalNo	= "";										// �ſ�ī�� ���ι�ȣ
		  String sCardNm		= "";										// �ſ�ī�� �̸�
		  String sBankNm		= "";										// ������ü �����̸�
		  String sSellMm		= "";										// �Һΰ���
		  String sOrderNo		= "";										// �ֹ���ȣ
		  
		  if(GolfUtil.empty(sCrossKey)){
			  sCrossKey = "e3f8453680e39fc1d6dfe72079874219";
		  }
		  
		  if(GolfUtil.empty(sShopid)){
			  sShopid = "bcgolf";
		  }
		
		  // ��û ������ ����
		  strReq  ="allat_shop_id="   +sShopid;
		  strReq +="&allat_amt="      +sAmount;
		  strReq +="&allat_enc_data=" +sEncData;
		  strReq +="&allat_cross_key="+sCrossKey;
		  		
		  try {			

			  // �þ� ���� ������ ���  : AllatUtil.approvalReq->����Լ�, HashMap->�����
			  AllatUtil util = new AllatUtil();
			  HashMap hm     = null;
			  hm = util.approvalReq(strReq, "SSL");

			  // ���� ��� �� Ȯ��
			  String sReplyCd		= (String)hm.get("reply_cd");		// ����ڵ�
			  String sReplyMsg		= (String)hm.get("reply_msg");		// ����޼���
			  
			  ett.setResCode(sReplyCd);
			  ett.setResMsg(sReplyMsg);			  

			  // ���� ���� 
			  //if( sReplyCd.equals("0001") ){	// �׽�Ʈ��
			  if( sReplyCd.equals("0000") ){	// �ǰ�����
				  
				  sPayType		= (String)hm.get("pay_type");		// ���Ҽ���
				  sApprovalNo	= (String)hm.get("approval_no");	// �ſ�ī�� ���ι�ȣ
				  sCardNm		= (String)hm.get("card_nm");		// �ſ�ī�� �̸�			  
				  sBankNm		= (String)hm.get("bank_nm");		// ������ü �����̸�
				  sSellMm		= (String)hm.get("sell_mm");		// �Һΰ���
				  sOrderNo 		= (String)hm.get("order_no");		// �ֹ���ȣ

				  if(sPayType.equals("3D") || sPayType.equals("ISP") || sPayType.equals("NOR")){
					  sPayType = "CARD";
					  sCardNm = sCardNm + "ī��";
				  }
				  else if(sPayType.equals("ABANK")){
					  sCardNm = sBankNm + "����";
				  }
				    			   				
				  ett.setPayType(sPayType);
				  ett.setUseNo(sApprovalNo);
				  ett.setCardNm(sCardNm);
				  ett.setInsTerm(sSellMm);
				  ett.setOrderNo(sOrderNo);

				  result = true;
				  

				  // �α���� ����
				  String sAmt            = (String)hm.get("amt");
				  String sApprovalYmdHms = (String)hm.get("approval_ymdhms");
				  String sSeqNo          = (String)hm.get("seq_no");
				  String sCardId         = (String)hm.get("card_id");
				  String sZerofeeYn      = (String)hm.get("zerofee_yn");
				  String sCertYn         = (String)hm.get("cert_yn");
				  String sContractYn     = (String)hm.get("contract_yn");
				  String sSaveAmt        = (String)hm.get("save_amt");
				  String sBankId         = (String)hm.get("bank_id");
				  String sCashBillNo     = (String)hm.get("cash_bill_no");
				  String sCashApprovalNo = (String)hm.get("cash_approval_no");
				  String sEscrowYn       = (String)hm.get("escrow_yn");			  

				  debug("����ڵ�               : " + sReplyCd          + "\n");
				  debug("����޼���             : " + sReplyMsg         + "\n");
				  debug("�ֹ���ȣ               : " + sOrderNo          + "\n");
				  debug("���αݾ�               : " + sAmt              + "\n");
				  debug("���Ҽ���               : " + sPayType          + "\n");
				  debug("�����Ͻ�               : " + sApprovalYmdHms   + "\n");
				  debug("�ŷ��Ϸù�ȣ           : " + sSeqNo            + "\n");
				  debug("����ũ�� ���� ����     : " + sEscrowYn         + "\n");
				  debug("==================== �ſ� ī�� ===================\n");
				  debug("���ι�ȣ               : " + sApprovalNo       + "\n");
				  debug("ī��ID                 : " + sCardId           + "\n");
				  debug("ī���                 : " + sCardNm           + "\n");
				  debug("�Һΰ���               : " + sSellMm           + "\n");
				  debug("�����ڿ���             : " + sZerofeeYn        + "\n");   //������(Y),�Ͻú�(N)
				  debug("��������               : " + sCertYn           + "\n");   //����(Y),������(N)
				  debug("�����Ϳ���             : " + sContractYn       + "\n");   //3�ڰ�����(Y),��ǥ������(N)
				  debug("���̺� ���� �ݾ�       : " + sSaveAmt          + "\n");
				  debug("=============== ���� ��ü / ������� =============\n");
				  debug("����ID                 : " + sBankId           + "\n");
				  debug("�����                 : " + sBankNm           + "\n");
				  debug("���ݿ����� �Ϸ� ��ȣ   : " + sCashBillNo       + "\n");
				  debug("���ݿ����� ���� ��ȣ   : " + sCashApprovalNo   + "\n");
				  // �α���� ��
				    
			  } 
			  // ����  ����
			  else {
				  result = false;
			  }
			
			  info("## GolfPaymentDaoProc | executeAuth AllatPay END | result : " + result + "| sReplyCd : " + sReplyCd + "| sReplyMsg : " + sReplyMsg + "| sApprovalNo : " + sApprovalNo + "| sCardNm : " + sCardNm + "\n");
			
		} catch (Throwable t) {
			throw new GolfException(TITLE,t);
		} finally {
		}
		
		return result;
	}


	/**
	 * ���� ��� - �þ�����
	 */
	public boolean executePayAuthCancel_Allat(WaContext context, GolfPayAuthEtt ett) throws GolfException { 
		
		boolean result = false;
		debug("## GolfPaymentDaoProc | executeAuthCancel AllatPay START!!! ");

		String szReqMsg 		= "";
		String szAllatEncData 	= "";			    
		String szAmt           	= ett.getAmount(); 		// ��� �ݾ�               (�ִ�  10 �ڸ�)
		String szOrderNo       	= ett.getOrderNo(); 	// �ֹ���ȣ                (�ִ�  80 �ڸ�)	      
		String szPayType       	= ett.getPayType(); 	// ���ŷ����� �������[ī��:CARD,������ü:ABANK]
		String szSeqNo         	= "";    				// �ŷ��Ϸù�ȣ:�ɼ��ʵ�   (�ִ�  10�ڸ�)
		String sCancelYMDHMS	= "";					// ��� �Ͻ�
		String sShopid  		= ett.getShopId();		// �����̵�
		String sCrossKey 		= ett.getCrossKey();	// ũ�ν�Ű

		  
		if(GolfUtil.empty(sCrossKey)){
			sCrossKey = "e3f8453680e39fc1d6dfe72079874219";
		}
	  
		if(GolfUtil.empty(sShopid)){
			sShopid = "bcgolf";
		}


		try {			

			AllatUtil util = new AllatUtil();
			HashMap reqHm = new HashMap();
			HashMap resHm = null;

			reqHm.put("allat_shop_id" ,  sShopid );
			reqHm.put("allat_cross_key", sCrossKey);
			reqHm.put("allat_order_no",  szOrderNo);
			reqHm.put("allat_amt"     ,  szAmt    );
			reqHm.put("allat_pay_type",  szPayType);
			reqHm.put("allat_test_yn" ,  "N"      );    //�׽�Ʈ :Y, ���� :N 
			reqHm.put("allat_opt_pin" ,  "NOUSE"  );    //��������(�þ� ���� �ʵ�)
			reqHm.put("allat_opt_mod" ,  "APP"    );    //��������(�þ� ���� �ʵ�)
			reqHm.put("allat_seq_no"  ,  szSeqNo  );    //�ɼ� �ʵ�( ���� ������ )

			szAllatEncData = util.setValue(reqHm);
			szReqMsg  = "allat_shop_id="   + sShopid
						+ "&allat_amt="      + szAmt
						+ "&allat_enc_data=" + szAllatEncData
						+ "&allat_cross_key="+ sCrossKey;

			resHm = util.cancelReq(szReqMsg, "SSL");

			// ��� ��� �� Ȯ��
			String sReplyCd   = (String)resHm.get("reply_cd");
			String sReplyMsg  = (String)resHm.get("reply_msg");
			  
			ett.setResCode(sReplyCd);
			ett.setResMsg(sReplyMsg);			  

			// ��� ����
			//if( sReplyCd.equals("0001") ){		// �׽�Ʈ��
			if( sReplyCd.equals("0000") ){	// �ǰ�����			
				sCancelYMDHMS = (String)resHm.get("cancel_ymdhms");			      
				result = true;
			} 
			// ��� ����
			else {
				result = false;
			}
			
			info("## GolfPaymentDaoProc | executeAuthCancel AllatPay END | result : " + result +  "| sReplyCd : " + sReplyCd + "| sReplyMsg : " + sReplyMsg + "| sCancelYMDHMS : " + sCancelYMDHMS + "\n");
			
		} catch (Throwable t) {
			throw new GolfException(TITLE,t);
		} finally {
		}
		
		return result;
	}

	
	/**
	 * ISP �Ķ���� �Ľ�.
	 * @param parameterValue �Ķ���Ͱ�
	 * @return HashMap 
	 * @throws EtaxException
	 */
	public HashMap getKvpParameter(String parameterValue) throws GolfException {
		
		HashMap map = new HashMap();
				
		try {
			
			String decryptData = SymEncDec.decrypt(1, parameterValue);

			for( StringTokenizer st = new StringTokenizer(decryptData,"%"); st.hasMoreTokens(); ) {
				String tempString = st.nextToken();
				int idx = tempString.indexOf('=');
				String key = tempString.substring(0,idx);
				String val = tempString.substring(idx+1);
				debug("GOLF_PIV:" + key + "=" + val + ".");
				map.put(key,val);
			}

		} catch(GolfException e) {
			throw e;
		} catch(Throwable t) {
			// �Ķ���� �Ľ� ����
			warn("GOLF_PIF:" + parameterValue , t );
			throw new GolfException(TITLE,t);
		}
		return map;
	}	
	
}
