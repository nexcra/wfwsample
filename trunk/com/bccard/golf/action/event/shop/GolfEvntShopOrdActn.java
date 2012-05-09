/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopOrdActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > ����ó�� 
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
*    ����  �ۼ���   �������
*20110323  �̰��� 	���̽�ĳ��
*20110419  �̰��� 	���հ����� �Һα�� ���� �� ���հ��� �Ͻúҽ� 60ó��
***************************************************************************************************/
package com.bccard.golf.action.event.shop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfElecAauthProcess;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopOrdDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntShopOrdActn extends GolfActn{
	
	public static final String TITLE = "���� ����Ʈ";

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
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");		
		GolfElecAauthProcess elecAath = new GolfElecAauthProcess();//������������
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)		
		boolean resMsg = true;   // ���� ���� �޼��� ����
		String memName = "";			
		String ispCardNo = "";	// ispī���ȣ
		String cstIP = request.getRemoteAddr(); //����IP		
		String pid = null;						// ���ξ��̵�		
		
		String rtnMsg = "����� �������� ����"; 

		String validCert = "false"; 
		String clientAuth = "false";
		String[] semiCertVal = new String[2]; 
		
		Map paramMap = BaseAction.getParamToMap(request);
		paramMap.put("title", TITLE);
		String script = "";
	
		try {
			
			semiCertVal = elecAath.semiCert(request, response);
				
			validCert = semiCertVal[0];
			clientAuth = semiCertVal[1];
			
			if (validCert.equals("false")){
				rtnMsg = "�ùٸ� �������� �ƴմϴ�.";
			}
			
			if (clientAuth.equals("false")){
				debug("������ �ʿ���� ������");				
			}
			
			debug (" ### validCert : " + validCert +", clientAuth : " + clientAuth);			
			
			if (validCert.equals("true")||clientAuth.equals("false")){

				// 02.�Է°� ��ȸ		
				RequestParser parser = context.getRequestParser(subpage_key, request, response);
				
				UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			 	
				if(usrEntity != null) {
					memName		= (String)usrEntity.getName();				
				}else {
					memName = (String)parser.getParameter("userNm","").trim();				
				}
				
				// ��ó��				
				boolean payResult = false;
				boolean payCancelResult = false;
				int addResult = 0;
	
				// ��ǰ ����
				String order_no			= parser.getParameter("order_no", "");			// �ֹ��ڵ�
				String gds_code			= parser.getParameter("gds_code", "");			// ��ǰ�ڵ�
				String sgl_lst_itm_code	= parser.getParameter("sgl_lst_itm_code", "");	// �ɼ�
				String qty				= parser.getParameter("qty","");				// ����
				String int_atm			= parser.getParameter("int_atm","");			// ��ǰ�ݾ�
				String realPayAmt		= parser.getParameter("realPayAmt", "0");		// ���� �ݾ�
				
				// ������ - ������
				String juminno1			= parser.getParameter("juminno1","");	
				String juminno2			= parser.getParameter("juminno2","");	
				String userNm			= parser.getParameter("userNm","");	
				String userId			= parser.getParameter("userId","");	
				String zip_code1		= parser.getParameter("zip_code1","");	
				String zip_code2		= parser.getParameter("zip_code2","");	
				String zipaddr			= parser.getParameter("zipaddr","");	
				String detailaddr		= parser.getParameter("detailaddr","");	
				String addr_clss		= parser.getParameter("addr_clss","");
				String mobile1			= parser.getParameter("mobile1","");	
				String mobile2			= parser.getParameter("mobile2","");	
				String mobile3			= parser.getParameter("mobile3","");	
				String hdlv_msg_ctnt	= parser.getParameter("hdlv_msg_ctnt","");	
				
				// ������ - ������
				String dlv_userNm		= parser.getParameter("dlv_userNm","");	
				String dlv_zip_code1	= parser.getParameter("dlv_zip_code1","");	
				String dlv_zip_code2	= parser.getParameter("dlv_zip_code2","");	
				String dlv_zipaddr		= parser.getParameter("dlv_zipaddr","");	
				String dlv_detailaddr	= parser.getParameter("dlv_detailaddr","");
				String dlv_addr_clss	= parser.getParameter("dlv_addr_clss","");
				String dlv_mobile1		= parser.getParameter("dlv_mobile1","");	
				String dlv_mobile2		= parser.getParameter("dlv_mobile2","");	
				String dlv_mobile3		= parser.getParameter("dlv_mobile3","");			
				
				// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
	
				dataSet.setString("ORDER_NO", order_no);
				dataSet.setString("gds_code", gds_code);
				dataSet.setString("sgl_lst_itm_code", sgl_lst_itm_code);
				dataSet.setString("qty", qty);
				dataSet.setString("int_atm", int_atm);
				dataSet.setString("realPayAmt", realPayAmt);
				
				dataSet.setString("juminno1", juminno1);
				dataSet.setString("juminno2", juminno2);
				dataSet.setString("userId", userId);
				dataSet.setString("userNm", userNm);
				dataSet.setString("zip_code1", zip_code1);
				dataSet.setString("zip_code2", zip_code2);
				dataSet.setString("zipaddr", zipaddr);
				dataSet.setString("detailaddr", detailaddr);
				dataSet.setString("addr_clss", addr_clss);			
				dataSet.setString("mobile1", mobile1);
				dataSet.setString("mobile2", mobile2);
				dataSet.setString("mobile3", mobile3);
				dataSet.setString("hdlv_msg_ctnt", hdlv_msg_ctnt);
				
				dataSet.setString("dlv_userNm", dlv_userNm);
				dataSet.setString("dlv_zip_code1", dlv_zip_code1);
				dataSet.setString("dlv_zip_code2", dlv_zip_code2);
				dataSet.setString("dlv_zipaddr", dlv_zipaddr);
				dataSet.setString("dlv_detailaddr", dlv_detailaddr);
				dataSet.setString("dlv_addr_clss", dlv_addr_clss);
				dataSet.setString("dlv_mobile1", dlv_mobile1);
				dataSet.setString("dlv_mobile2", dlv_mobile2);
				dataSet.setString("dlv_mobile3", dlv_mobile3);			

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
				String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD3");				// ������ ��ȣ 769835680-�����������ǰ
				String iniplug 			= parser.getParameter("KVPpluginData", "");			// ISP ������
	
				String cardNo			= parser.getParameter("card_no", "0");				// ī���ȣ
				String insTerm			= parser.getParameter("ins_term", "00");			// �Һΰ�����
				String siteType			= parser.getParameter("site_type", "1");			// ����Ʈ ���� 1: ��, 2:����ü
											
				// ��ī�� debug("// STEP 1_2. �Ķ���� �Է�"); 
				HashMap kvpMap = null;
				//String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// ����� ���̵�
				//String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// ���ΰ�
				String pcg         = "";														// ����/���� ����			
				String valdlim	   = "";														// ���� ����
				
				String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
				
				// �þ����̿� ����
				String sPayType = "";		// ���Ҽ���
				String sApprovalNo = "";	// �ſ�ī�� ���ι�ȣ
				String sCardNm = "";		// �ſ�ī�� �̸�(������ü �����̸�)
	
				// �������� ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				String sttl_mthd_clss = GolfUtil.lpad(payType+"", 4, "0");
				String sttl_gds_clss = "0009";
				
				if (payType.equals("2")){
					insTerm = "60";
				}
								
				// ��ī�� �Ǵ� ��ī��+����Ʈ ������ ���
				if(payType.equals("1") || payType.equals("2")){
					
					if(iniplug !=null && !"".equals(iniplug)) {
						kvpMap = payProc.getKvpParameter( iniplug );					
					}	
					
					if(kvpMap != null) {
						
						ispAccessYn = "Y";
						pcg         = (String)kvpMap.get("PersonCorpGubun");		// ����/���� ����
						ispCardNo   = (String)kvpMap.get("CardNo");					// ispī���ȣ
						valdlim		= (String)kvpMap.get("CardExpire");				// ���� ����
						if ( "2".equals(pcg) ) {
							pid = (String)kvpMap.get("BizId");						// ����ڹ�ȣ
							
						} else {
							pid = (String)kvpMap.get("Pid");						// ���� �ֹι�ȣ
						}	
						
						//������ ����
						if (clientAuth.equals("true")){
							
							session = request.getSession();
							
//							if(!elecAath.isValidCert(request, response, pid, pcg)){
								
								elecAath.setValidCertMsg(session);
								
								String sessionMsg = (String)session.getAttribute("validCertMsg");								
								script = "alert('"+sessionMsg+"'); location.href='html/event/bcgolf_event/progress_event.jsp';";
								
								request.setAttribute("script", script);
								request.setAttribute("paramMap", paramMap);								
								return super.getActionResponse(context, subpage_key);
								
//							}else {
//								debug("####### ISP & �������� ���� OK..  ");
//							}
						
						}
						
					} else {
						ispCardNo = parser.getParameter("isp_card_no","");	// �ϳ�����ī�� ���
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
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("STTL_AMT", realPayAmt);				// ���� �ݾ�
				dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
				dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("MER_NO", merMgmtNo);
				dataSet.setString("CARD_NO", ispCardNo);
				dataSet.setString("VALD_DATE", valdlim);
				dataSet.setString("INS_MCNT", insTerm.toString());
	
				debug("�������� ���� ���� => merMgmtNo : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
						 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip +", userId : " + userId);
	
				if (payResult) {

					// ���� ���̺�(Proc) ��ȸ
					// �ֹ����� ����
					GolfEvntShopOrdDaoProc proc = (GolfEvntShopOrdDaoProc)context.getProc("GolfEvntShopOrdDaoProc");
					addResult = proc.execute(context, request, dataSet);	// 1 time	
					debug("GolfEvntShopOrdActn = addResult : " + addResult);	
					
					// �ֹ� ���̺� ������Ʈ(���� ��������)
					addResult = addResult + proc.execute_upd(context, request, dataSet);		
					debug("GolfEvntShopOrdActn = addResult2 : " + addResult);	
	
					if (addResult == 2) {
						// ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
						addResult = addResult + addPayProc.execute(context, dataSet);					
						debug("GolfEvntShopOrdActn = addResult3 : " + addResult);	
					}
				}
				else{	// �������н� ���� ���� 2009.11.27 
					
					veriResCode = "3";
					resMsg = false;
					
					int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);
					
				}
					
				if(addResult == 3 ){
					script = "alert('���Ű� �Ϸ� �Ǿ����ϴ�.'); location.href='html/event/bcgolf_event/progress_event.jsp';";
					
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
						  debug("GolfEvntShopOrdActn ���� ��� ���� | ���������� : " + result);											
					  }
					  // ���� ��� ����
					  else{
						  int result = addPayProc.failExecute(context, dataSet, request, payEtt);				
						  debug("GolfEvntShopOrdActn ���� ��� ���� | ���������� : " + result);											
					  }								  
					}
					
					if ( !resMsg ){
						script = "alert('"+payEtt.getResMsg()+" \\n���ſ� �����߽��ϴ�.'); location.href='html/event/bcgolf_event/progress_event.jsp';";
					}else {
						script = "alert('���ſ� �����߽��ϴ�.'); location.href='html/event/bcgolf_event/progress_event.jsp';";
					}
					
				}	
			
			}else{
				script = "alert('"+rtnMsg+"'); location.href='html/event/bcgolf_event/progress_event.jsp';";
			}			

			request.setAttribute("script", script);
			request.setAttribute("paramMap", paramMap);
	        
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
				hmap.put("memName", memName);
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
