/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsActn
*   �ۼ���    : �̵������ ������
*   ����      : ���� > ���
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member.cyber;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.member.cyber.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.common.AppConfig;


import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	�̵������
* @version	1.0
******************************************************************************/
public class GolfMemCyberInsActn extends GolfActn{
	
	public static final String TITLE = "���̹��Ӵ�  > ���ó��";

	/***************************************************************************************
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userNm = ""; 
		String userId = "";
		String email_id = "";
		String userMobile = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		int cyberMoney = 0;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

		try {
			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
		 	
			 if(userEtt != null) {
				userNm			= (String)userEtt.getName(); 
				userId			= (String)userEtt.getAccount(); 
				email_id 		= (String)userEtt.getEmail1(); 
				userMobile1 	= (String)userEtt.getMobile1();
				userMobile2 	= (String)userEtt.getMobile2();
				userMobile3 	= (String)userEtt.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
				cyberMoney 		= (int)userEtt.getCyberMoney();
			}
			 /*
			debug("==========email_id========> " + email_id);
			debug("==========userMobile1========> " + userMobile1);
			debug("==========userMobile2========> " + userMobile2);
			debug("==========userMobile3========> " + userMobile3);
			email_id = "simijoa@hanmail.net";
			userMobile1 = "010";
			userMobile2 = "9192";
			userMobile3 = "4738";
			*/
			
			// 02.�Է°� ��ȸ	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			int amount 				= parser.getIntParameter("amount", 0);
			String payType 			= parser.getParameter("payType", "");
			debug("GolfMemCyberInsDaoProc =============== amount => " + amount);
			debug("GolfMemCyberInsDaoProc =============== payType => " + payType);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setInt("amount", amount);	
			dataSet.setString("payType", payType);	
						
			// 04.���� ���̺�(Proc) ��ȸ
			GolfMemCyberInsDaoProc proc = (GolfMemCyberInsDaoProc)context.getProc("GolfMemCyberInsDaoProc");
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			

			boolean payResult = false;
			int addResult = 0;
			debug("+1+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");	
			
			debug("// STEP 1. �Է°��� ���� ����üũ+");
			String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
			if ( st_s == null ) st_s = "";
			request.getSession().removeAttribute("ParameterManipulationProtectKey");

			String st_p = request.getParameter("ParameterManipulationProtectKey");
			if ( st_p == null ) st_p = "";

			if ( !st_p.equals(st_s) ) {
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
			//	throw new EtaxException(msgEtt);
			}

			debug("st_s :: >> " + st_s);
			debug("st_p :: >> " + st_p);
			
				
			// ���� 
			String ip = request.getRemoteAddr();  
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD");		// ������ ��ȣ(766559864) //topn : 745300778
			String iniplug = parser.getParameter("KVPpluginData", "");	// ISP ������
			String sum		 = parser.getParameter("realPayAmt", "0");	// �����ݾ�
			if(sum != null && !"".equals(sum)){
				sum = StrUtil.replace(sum,",","");
			}

			String cardNo		= parser.getParameter("card_no", "0");				// ī���ȣ
			String insTerm		= parser.getParameter("ins_term", "00");			// �Һΰ�����
			debug("==========insTerm========> " + insTerm);
			String siteType		= parser.getParameter("site_type", "1");			// ����Ʈ ���� 1: ��, 2:����ü	
			
			debug("// STEP 1_2. �Ķ���� �Է�");
			HashMap kvpMap = null;
			if(iniplug !=null && !"".equals(iniplug)) {
				kvpMap = payProc.getKvpParameter( iniplug );
			}			

			debug("// STEP 1_3. ������������ ���� ��� ��ȿ�� �˻�..");
			String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// ����� ���̵�
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// ���ΰ�
			String pcg         = "";														// ����/���� ����
			String ispCardNo   = "";														// ispī���ȣ
			String valdlim	   = "";														// ���� ����
			String pid = null;																// ���ξ��̵�

			if(kvpMap != null) {
				pcg         = (String)kvpMap.get("PersonCorpGubun");		// ����/���� ����
				ispCardNo   = (String)kvpMap.get("CardNo");					// ispī���ȣ
				valdlim		= (String)kvpMap.get("CardExpire");				// ���� ����
				if ( "2".equals(pcg) ) {
					pid = (String)kvpMap.get("BizId");								// ����ڹ�ȣ
				} else {
					pid = (String)kvpMap.get("Pid");									// ���� �ֹι�ȣ
				}
			} else {
				ispCardNo = 	parser.getParameter("isp_card_no","");	// �ϳ�����ī�� ���
			}
			
			if ( valdlim.length() == 6 ) {
				valdlim = valdlim.substring(2);											
			}
			debug("// STEP 5. ����ó��");
			payEtt.setMerMgmtNo(merMgmtNo);
			payEtt.setCardNo(ispCardNo);
			payEtt.setValid(valdlim);			
			payEtt.setAmount(sum);
			payEtt.setInsTerm(insTerm);
			payEtt.setRemoteAddr(ip);

			payResult = payProc.executePayAuth(context, request, payEtt);			// �������� ȣ��
			

			debug("====================GolfMemInsActn =============payResult => " + payResult);
			debug("+4++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			

			// ���� ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_MTHD_CLSS = "000"+payType;	// ���� ��� �����ڵ�
			String sttl_GDS_CLSS = "0004";	// ���� ��ǰ ���� �ڵ�
			
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("STTL_MTHD_CLSS", sttl_MTHD_CLSS);
			dataSet.setString("STTL_GDS_CLSS", sttl_GDS_CLSS);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("STTL_AMT", sum);
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());
			dataSet.setString("AUTH_NO", payEtt.getUseNo());
			

			// 04.����ó��	
			if (payResult) { // ��~~ �����ؾ� �� ����
				addResult = proc.execute(context, dataSet, request);	

				if (addResult == 1) {
					// ���� ���� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					addResult = addResult + addPayProc.execute(context, dataSet);
				}
			}			
			

			// SMS ���� ����
			HashMap smsMap = new HashMap();
			
			smsMap.put("ip", request.getRemoteAddr());
			smsMap.put("sName", userNm);
			smsMap.put("sPhone1", userMobile1);
			smsMap.put("sPhone2", userMobile2);
			smsMap.put("sPhone3", userMobile3);
			
	        String returnUrlTrue = "GolfMemCyberInsForm.do";
	        String returnUrlFalse =  "GolfMemCyberInsForm.do";
			
			if (addResult == 2) {

				cyberMoney = cyberMoney+Integer.parseInt(sum);
				userEtt.setCyberMoney(cyberMoney);

				//sms�߼�
				if (!userMobile.equals("")) {
					
					debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "660";
					String message = "[Golf Loun.G] "+userNm+"�� ���̹��Ӵ� "+GolfUtil.comma(sum)+"���� �����Ǿ����ϴ�. �����մϴ�.";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}				
				
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "���Ű� ���������� ó�� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "���Ű� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
