/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntKvpActn 
*   �ۼ���	: (��)�̵������ ������
*   ����		: KVP ó��
*   �������	: golf
*   �ۼ�����	: 2010-05-25
************************* �����̷� ***************************************************************** 
*    ����       �ۼ���      �������
* 2011.02.11    �̰���	   ISP������� �߰�
***************************************************************************************************/
package com.bccard.golf.action.event.kvp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.kvp.GolfEvntKvpDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentDaoProc;
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
public class GolfEvntKvpActn extends GolfActn{
	
	public static final String TITLE = "KVP ó��";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";

		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();											// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");		
		GolfEvntKvpDaoProc proc = (GolfEvntKvpDaoProc)context.getProc("GolfEvntKvpDaoProc");	// KVP ����
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // ������� �ڵ� (1: �����ֹ��Ϸ�   3:�ֹ�������)		
		String ispCardNo = "";	// ispī���ȣ
		String cstIP = request.getRemoteAddr(); //����IP
		String socid = ""; 			// �ֹι�ȣ
		String name = "";
		boolean payCancelResult = false;
				
		RequestParser parser = context.getRequestParser(subpage_key, request, response);
		Map paramMap = BaseAction.getParamToMap(request);
		paramMap.put("title", TITLE);
		
		String payType			= parser.getParameter("payType", "").trim();		// 1:ī�� 2:ī��+����Ʈ 3:Ÿ��ī��
		
		DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
		boolean payResult = false;
		
		// �þ����̿� ����
		String sPayType = "";		// ���Ҽ���
		String sApprovalNo = "";	// �ſ�ī�� ���ι�ȣ
		String sCardNm = "";		// �ſ�ī�� �̸�(������ü �����̸�)

		try {
			
			String social_id_1 = "";
			String social_id_2 = ""; 
 
			String ddd_no = ""; 
			String tel_hno = ""; 
			String tel_sno = ""; 
			String hp_ddd_no = ""; 
			String hp_tel_hno = ""; 
			String hp_tel_sno = ""; 
			String email = "";  
			String idx = ""; 
			String realPayAmt = "";			
			String chkResult = "";

			// ��ó��
			String resultMsg = "";
			String script = "";
			int addResult = 0;

			// �⺻ ��ȸ 
			social_id_1 = (String)parser.getParameter("social_id_1").trim();
			social_id_2 = (String)parser.getParameter("social_id_2").trim();
			socid = social_id_1 + social_id_2; 
			name = (String)parser.getParameter("name","").trim(); 
			
			ddd_no = (String)parser.getParameter("ddd_no",""); 
			tel_hno = (String)parser.getParameter("tel_hno",""); 
			tel_sno = (String)parser.getParameter("tel_sno",""); 
			hp_ddd_no = (String)parser.getParameter("hp_ddd_no",""); 
			hp_tel_hno = (String)parser.getParameter("hp_tel_hno",""); 
			hp_tel_sno = (String)parser.getParameter("hp_tel_sno",""); 
			email = (String)parser.getParameter("email",""); 
			idx = (String)parser.getParameter("idx",""); 
			realPayAmt = (String)parser.getParameter("realPayAmt","");
			chkResult = (String)parser.getParameter("chkResult",""); 
			
			String gds_code 				= parser.getParameter("gds_code", "").trim();
			String grdName					= parser.getParameter("grdName", "").trim();
			String rcvr_nm 					= parser.getParameter("rcvr_nm", "").trim();
			String zp1 						= parser.getParameter("zp1", "").trim();
			String zp2 						= parser.getParameter("zp2", "").trim();
			String addr 					= parser.getParameter("addr", "").trim();
			String dtl_addr					= parser.getParameter("dtl_addr", "").trim();
			String addr_clss				= parser.getParameter("addr_clss", "").trim();

			String order_no = parser.getParameter("allat_order_no", "").trim();
			debug("order_no : " + order_no);		
			dataSet.setString("socid", socid);
			dataSet.setString("social_id_1", social_id_1);
			dataSet.setString("name", name);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("email", email);
			dataSet.setString("idx", idx);
			dataSet.setString("realPayAmt", realPayAmt);
			dataSet.setString("chkResult", chkResult);

			//***�ֹ��������� end **���� start***********************************************//*
			
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
			
			String ip 				= request.getRemoteAddr();  
			String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD");				// ������ ��ȣ ��������� ��ȸ�� : 765943401
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
			String pid = null;																// ���ξ��̵�
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			
			// �������� ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType, 4, "0");	
			String sttl_gds_clss = GolfUtil.lpad(idx, 4, "0");
			
			if (sttl_gds_clss.equals("0004")) sttl_gds_clss = "0001";
			if (sttl_gds_clss.equals("0005")) sttl_gds_clss = "0002";
			if (sttl_gds_clss.equals("0006")) sttl_gds_clss = "0003";
			
			if (sttl_gds_clss.equals("0007")) sttl_gds_clss = "0001";
			if (sttl_gds_clss.equals("0008")) sttl_gds_clss = "0002";
			if (sttl_gds_clss.equals("0009")) sttl_gds_clss = "0003";
			
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
	
				payResult = payProc.executePayAuth(context, request, payEtt);	// �������� ȣ��
				dataSet.setString("AUTH_NO", payEtt.getUseNo());
				dataSet.setString("STTL_MINS_NM", "��ī��");	// �ſ�ī�� �̸�(������ü �����̸�)						  

			}	
			// Ÿ��ī�� ������ ���(�þ�����)
			else if(payType.equals("3")){
				
				  String sEncData  = request.getParameter("allat_enc_data");
				 
				  payEtt.setAmount(realPayAmt);
				  payEtt.setEncData(sEncData);
		
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
			dataSet.setString("CDHD_ID", socid);
			dataSet.setString("STTL_AMT", realPayAmt);				// ���� �ݾ�
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("payType", payType);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());

			debug("�������� ���� ���� => merMgmtNo(��������ȣ) : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
					 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip + " / idx : " + idx);

			if (payResult) {
				
				addResult = proc.execute(context, request, dataSet);		
				debug("GolfEvntKvpActn ��û���̺� �μ�Ʈ  :  " + addResult);	

				if (addResult > 0) {
					
					String dcAmt = ""; //���ΰ�
					String normAmt = ""; //����
					
					/*���ΰ��� ����Ǵ� ���̺��̳� ���� ����� ���� ���� �ϵ��ڵ�
					 * �ϵ��ڵ� ���� �����̳�..
					*/
					//ȸ������ ������
					if(idx.equals("1")){//Champion
						dcAmt = "50000";
						normAmt = "200000";
					}else if(idx.equals("2")){//Blue
						dcAmt = "25000";
						normAmt = "50000";						
					}else if(idx.equals("3")){//Gold
						dcAmt = "12500";
						normAmt = "25000";
					
					//ȸ������ �����
					}else if(idx.equals("4")){//Champion
						dcAmt = "55000";
						normAmt = "200000";
					}else if(idx.equals("5")){//Blue
						dcAmt = "30000";
						normAmt = "50000";
					}else if(idx.equals("6")){//Gold
						dcAmt = "17500";
						normAmt = "25000";
						
					//��ȸ������ �����
					}else if(idx.equals("7")){//Champion
						dcAmt = "5000";
						normAmt = "200000";
					}else if(idx.equals("8")){//Blue
						dcAmt = "5000";
						normAmt = "50000";
					}else if(idx.equals("9")){//Gold
						dcAmt = "5000";
						normAmt = "25000";
					}
					
					dataSet.setString("DC_AMT", dcAmt);
					dataSet.setString("NORM_AMT", normAmt);
					dataSet.setString("CUPN_CTNT", "KT Olleh Club����");
					dataSet.setString("STTL_GDS_SEQ_NO", Integer.toString(addResult));	// ��ǰ�ڵ忡 ��û���̺� idx�� �־��ش�.
					
					addResult = addResult + addPayProc.execute(context, dataSet);					
					debug("GolfEvntKvpActn ���� ����  : " + addResult);
				}
				
				if(!gds_code.equals("") && grdName.equals("Champion")) {
					dataSet.setString("gds_code", gds_code);	
					dataSet.setString("rcvr_nm", rcvr_nm);	
					dataSet.setString("zp", zp1+""+zp2);	
					dataSet.setString("addr", addr);		
					dataSet.setString("dtl_addr", dtl_addr);
					dataSet.setString("addr_clss", addr_clss);
					dataSet.setString("hp_ddd_no", hp_ddd_no);	
					dataSet.setString("hp_tel_hno", hp_tel_hno);	
					dataSet.setString("hp_tel_sno", hp_tel_sno);
					dataSet.setString("flag", "KT");
		
					// 01.����ǰ��û�ϱ�(Proc) ��ȸ 20091216
					GolfMemPresentDaoProc procPresent = (GolfMemPresentDaoProc)context.getProc("GolfMemPresentDaoProc");			// ����ǰ��û�ϱ����μ���
					procPresent.execute(context, dataSet, request);
					
				}
				
			}else{	// �������н� ���� ���� 2009.11.27 
				
				veriResCode = "3";
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
				debug("GolfEvntKvpActn  ���� ���� ���� ���� : " + result_fail);
				
			}

			String map_url_path = AppConfig.getAppProperty("MAP_URL_PATH")+"/app/golfloung/join_frame2.do?url=/app/golfloung/html/common/join_isp.jsp";
			
			if(addResult > 1 ){
				
				script += "alert('������ �Ϸ�Ǿ����ϴ�. �����մϴ�.');";
				
				 if(payType.equals("3")){
					
					script += "window.open('"+map_url_path+"','_blank','');"; 
					script += "parent.window.close();"; 

				 }else {

					script += "window.open('"+map_url_path+"','_blank','');";
					script += "parent.top.window.close();";
				 
				 }
				
			}else{	
				
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
		        	
	        	veriResCode = "3";
	        	resultMsg = "������ �����߽��ϴ�. �ٽ� �õ��� �ֽñ� �ٶ��ϴ�.";
	        	script = "parent.top.window.close();";
			}		

			request.setAttribute("resultMsg", resultMsg);
			request.setAttribute("script", script);			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			
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
				hmap.put("memName", name);
				hmap.put("memSocid", socid);
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
