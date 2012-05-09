/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopListActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > ����Ʈ 
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.benest;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfElecAauthProcess;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstRegDaoProc;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstPayDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntBnstPayActn extends GolfActn{
	
	public static final String TITLE = "���򺣳׽�Ʈ ���";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		HttpSession session = null;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		
		String resultMsg = "����� �������� ����"; 
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)		
		boolean resMsg = true;   // ���� ���� �޼��� ����
		String validCert = "false"; 
		String clientAuth = "false";
		String[] semiCertVal = new String[2]; 		
		
		String ispAccessYn  = "N";;
		String userNm = "";
		String pid = "";
		String ispCardNo   = "";// ispī���ȣ
		String cstIP = request.getRemoteAddr(); //����IP
		
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
		GolfElecAauthProcess elecAath = new GolfElecAauthProcess();//������������
		
		RequestParser parser = context.getRequestParser(subpage_key, request, response);
		Map paramMap = BaseAction.getParamToMap(request);
		paramMap.put("title", TITLE);
		
		try { 
			
			semiCertVal = elecAath.semiCert(request, response);
			
			validCert = semiCertVal[0];
			clientAuth = semiCertVal[1];
			
			if (validCert.equals("false")){
				resultMsg = "�ùٸ� �������� �ƴմϴ�.";
			}
			
			if (clientAuth.equals("false")){
				debug("������ �ʿ���� ������");				
			}
			
			debug (" ### validCert : " + validCert +", clientAuth : " + clientAuth);	
			String aplc_seq_no				= parser.getParameter("aplc_seq_no","");
			
			if (validCert.equals("true")||clientAuth.equals("false")){
				// ��ó��
				
				boolean payResult = false;
				boolean payCancelResult = false;
				int addResult = 0;
	
				
				String juminno1					= parser.getParameter("juminno1","");
				String juminno2					= parser.getParameter("juminno2","");
				userNm							= parser.getParameter("userNm","");
				String userId					= parser.getParameter("userId","");
	
				String order_no					= parser.getParameter("order_no", "");			// �ֹ��ڵ�
				String realPayAmt				= parser.getParameter("realPayAmt", "0");		// ���� �ݾ�
				
				debug("aplc_seq_no : " + aplc_seq_no
						 + " / juminno1 : " + juminno1 + " / juminno2 : " + juminno2 + " / userNm : " + userNm + " / userId : " + userId 
						 + " / order_no : " + order_no+ " / realPayAmt : " + realPayAmt);
				
				
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				for(int i=1; i<21; i++){
					dataSet.setString("seq_no"+i, parser.getParameter("seq_no"+i, ""));
				}
				dataSet.setString("aplc_seq_no", aplc_seq_no);
				dataSet.setString("jumin_no", juminno1+juminno2);
				dataSet.setString("userNm", userNm);
				dataSet.setString("userId", userId);
	
	
				// 04.���� ���̺�(Proc) ��ȸ
				// �ֹ����� ����
				GolfEvntBnstPayDaoProc proc = (GolfEvntBnstPayDaoProc)context.getProc("GolfEvntBnstPayDaoProc");
	//			addResult = proc.execute(context, request, dataSet);	// 1 time	
				debug("GolfEvntBnstPayActn = addResult : " + addResult);	
				
				
				/***�ֹ��������� end **���� start***********************************************/
				
				//debug("// STEP 1. �Է°��� ���� ����üũ+");
				String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
				if ( st_s == null ) st_s = "";
				request.getSession().removeAttribute("ParameterManipulationProtectKey");
	
				String st_p = request.getParameter("ParameterManipulationProtectKey");
				if ( st_p == null ) st_p = "";
	
				if ( !st_p.equals(st_s) ) {
					MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
				//	throw new EtaxException(msgEtt);
				}				
	
				// �������� ���� => ����
				String payType 			= parser.getParameter("payType", "").trim();		// 1:ī�� 2:ī��+����Ʈ 3:Ÿ��ī��
				String ip 				= request.getRemoteAddr();  
				String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD4");				// ������ ��ȣ ��������� ������Ű��	770119761
				String iniplug 			= parser.getParameter("KVPpluginData", "");			// ISP ������
	
				String cardNo			= parser.getParameter("card_no", "0");				// ī���ȣ
				String insTerm			= parser.getParameter("ins_term", "00");			// �Һΰ�����
				String siteType			= parser.getParameter("site_type", "1");			// ����Ʈ ���� 1: ��, 2:����ü	
				
							
				// ��ī�� debug("// STEP 1_2. �Ķ���� �Է�"); 
				HashMap kvpMap = null;
				String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// ����� ���̵�
				String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// ���ΰ�
				String pcg         = "";														// ����/���� ����
				
				String valdlim	   = "";														// ���� ����
				
				String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
				
				// �þ����̿� ����
				String sPayType = "";		// ���Ҽ���
				String sApprovalNo = "";	// �ſ�ī�� ���ι�ȣ
				String sCardNm = "";		// �ſ�ī�� �̸�(������ü �����̸�)
	
				// �������� ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				String sttl_mthd_clss = GolfUtil.lpad(payType+"", 4, "0");
				String sttl_gds_clss = "0010";
				
				// ��ī�� �Ǵ� ��ī��+����Ʈ ������ ��� 
				if(payType.equals("1")){
					
					if(iniplug !=null && !"".equals(iniplug)) {
						kvpMap = payProc.getKvpParameter( iniplug );
					}	
					
					if(kvpMap != null) {
						ispAccessYn = "Y";
						pcg         = (String)kvpMap.get("PersonCorpGubun");		// ����/���� ����
						ispCardNo   = (String)kvpMap.get("CardNo");					// ispī���ȣ
						valdlim		= (String)kvpMap.get("CardExpire");				// ���� ����
						if ( "2".equals(pcg) ) {
							pid = (String)kvpMap.get("BizId");								// ����ڹ�ȣ
						} else {
							pid = (String)kvpMap.get("Pid");									// ���� �ֹι�ȣ
						}
					} else {
						ispCardNo = parser.getParameter("isp_card_no","");	// �ϳ�����ī�� ���
					}					
					
					//������ ����
					if (clientAuth.equals("true")){
						
						session = request.getSession();
						
//						if(!elecAath.isValidCert(request, response, pid, pcg)){
						
							elecAath.setValidCertMsg(session);
							resultMsg = (String)session.getAttribute("validCertMsg");
							
							paramMap.put("aplc_seq_no", aplc_seq_no);
							request.setAttribute("resultMsg", resultMsg);
							request.setAttribute("returnUrl", "GolfEvntBnstPayForm.do");
					        request.setAttribute("paramMap", paramMap);
					        
					        return super.getActionResponse(context, subpage_key);
							
//						}else {
//							debug("####### ISP & �������� ���� OK..  ");
//						}
					
					}					
					
					if ( valdlim.length() == 6 ) {
						valdlim = valdlim.substring(2);											
					}					
	
					//debug("// STEP 5. ����ó��");
					payEtt.setMerMgmtNo(merMgmtNo);
					payEtt.setCardNo(ispCardNo);
					payEtt.setValid(valdlim);			
					payEtt.setAmount(realPayAmt);
					payEtt.setInsTerm(insTerm);
					payEtt.setRemoteAddr(ip);				 
		
					if( "211.181.255.40".equals(host_ip)) {
						payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
					} else {
						payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
					}
					
					if("211.181.255.40".equals(host_ip)) {
						dataSet.setString("AUTH_NO", payEtt.getUseNo());
					} else {
						dataSet.setString("AUTH_NO", payEtt.getUseNo());
					}
	
					  dataSet.setString("STTL_MINS_NM", "��ī��");	// �ſ�ī�� �̸�(������ü �����̸�)						  
				
				}	
				// Ÿ��ī�� ������ ���(�þ�����)
				else if(payType.equals("3")){
	
					  String sEncData  = request.getParameter("allat_enc_data");
					  String sShopid  = request.getParameter("allat_shop_id");
					  String sCrossKey  = request.getParameter("allat_cross_key");
						
					  payEtt.setAmount(realPayAmt);
					  payEtt.setEncData(sEncData);
					  payEtt.setShopId(sShopid);
					  payEtt.setCrossKey(sCrossKey);
			
					  // �������� ��û
					  payResult = payProc.executePayAuth_Allat(context, request, payEtt);	
					   					  
					  // �������� ����
					  if(payResult){						  
						  sPayType = payEtt.getPayType();	// ���Ҽ���
						  sApprovalNo = payEtt.getUseNo();	// �ſ�ī�� ���ι�ȣ
						  insTerm = payEtt.getInsTerm();	// �ſ�ī�� �Һΰ���
						  sCardNm = payEtt.getCardNm();		// �ſ�ī�� �̸�(������ü �����̸�)
						  merMgmtNo = "";
						  
						  if(insTerm == null || insTerm.equals(""))	insTerm = "00";
						  
						  // �������ܰ� ����
						  if(sPayType.equals("CARD")){
							  sttl_mthd_clss = "0003";
						  }
						  else if(sPayType.equals("ABANK")){
							  sttl_mthd_clss = "0004";
						  }	
						  
						  dataSet.setString("AUTH_NO", sApprovalNo);	// �ſ�ī�� ���ι�ȣ
						  dataSet.setString("STTL_MINS_NM", sCardNm);	// �ſ�ī�� �̸�(������ü �����̸�)						  
					  }
	
				}
				
				// ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
				dataSet.setString("ORDER_NO", order_no);
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("STTL_AMT", realPayAmt);				// ���� �ݾ�
				dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
				dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
				dataSet.setString("STTL_GDS_SEQ_NO", aplc_seq_no);
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("MER_NO", merMgmtNo);
				dataSet.setString("CARD_NO", ispCardNo);
				dataSet.setString("VALD_DATE", valdlim);
				dataSet.setString("INS_MCNT", insTerm.toString());
	
				debug("�������� ���� ���� => merMgmtNo(��������ȣ) : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
						 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip + " / aplc_seq_no : " + aplc_seq_no);
	
				if (payResult) {
					addResult = proc.execute(context, request, dataSet);		
					debug("GolfEvntBnstPayActn : �ֹ� ���̺� ������Ʈ = addResult2 : " + addResult);	
	
					if (addResult > 0) {
						addResult = addResult + addPayProc.execute(context, dataSet);					
						debug("GolfEvntBnstPayActn : ���� ���� = addResult3 : " + addResult);	
					}
				}
				else{	// �������н� ���� ���� 2009.11.27 
					
					int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
					debug("GolfEvntBnstPayActn = result_fail : " + result_fail);					
					
					veriResCode = "3";
					resMsg = false;
					
				}
	
				if(addResult > 1 ){
					resultMsg = "������ �Ϸ� �Ǿ����ϴ�.";
				}else{
					
					veriResCode = "3";
	
					// ��ī�� �Ǵ� ���հ����� ���
					if(payType.equals("1") || payType.equals("2")){									
			        	if(!GolfUtil.empty(payEtt.getUseNo())){
			        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// ������� ȣ��
			        	}
					}
					// Ÿ��ī��(�þ�����)�� ���
					else if(payType.equals("3") && payResult){
					  payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		
					  
					  dataSet.setString("STTL_STAT_CLSS", "Y");	// Y:�������, N:�������ο�û
					  // ���� ��� ����
					  if (payCancelResult) {
						  int result = addPayProc.execute(context, dataSet);
						  debug("GolfEvntBnstPayActn ���� ��� ���� | ���������� : " + result);											
					  }
					  // ���� ��� ����
					  else{
						  int result = addPayProc.failExecute(context, dataSet, request, payEtt);				
						  debug("GolfEvntBnstPayActn ���� ��� ���� | ���������� : " + result);											
					  }								  
					}					
					
					if ( !resMsg ){
						resultMsg = payEtt.getResMsg() + "\\n������ �����߽��ϴ�. �ٽ� �õ��� �ֽñ� �ٶ��ϴ�.";
					}else {
						resultMsg = "������ �����߽��ϴ�. �ٽ� �õ��� �ֽñ� �ٶ��ϴ�.";
					}
					
				}
	
				paramMap.put("aplc_seq_no", aplc_seq_no);
				request.setAttribute("resultMsg", resultMsg);
				request.setAttribute("returnUrl", "GolfEvntBnstPayForm.do");
		        request.setAttribute("paramMap", paramMap);				
		        
			}else {
				
				String script ="";
				script = "alert('"+resultMsg+"'); top.window.close(); ";
				request.setAttribute("paramMap", paramMap);
				request.setAttribute("script", script);				
				
			}	
	        
		} catch(Throwable t) {
			veriResCode = "3";
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
			
			if(ispAccessYn.equals("Y")){
				
				//ISP���� �α� ���
				HashMap hmap = new HashMap();
				hmap.put("ispAccessYn", ispAccessYn);
				hmap.put("veriResCode", veriResCode);
				hmap.put("title", TITLE);
				hmap.put("memName", userNm);
				hmap.put("memSocid", pid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", cstIP);
				hmap.put("className", "GolfEvntKvpActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}
		
		}
		

		return super.getActionResponse(context, subpage_key);
		
	}
}
