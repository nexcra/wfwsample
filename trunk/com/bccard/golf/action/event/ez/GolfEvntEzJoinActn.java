/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntEzInsFormActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ������ > �����
*   �������  : Golf
*   �ۼ�����  : 2010-08-10
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* 
***************************************************************************************************/
package com.bccard.golf.action.event.ez;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstPayFormDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.security.cryptography.Base64Encoder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntEzJoinActn extends GolfActn{
	
	public static final String TITLE = "�̺�Ʈ > ������ > �����";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		String actnKey = super.getActionKey(context);
		
		try { 
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String cspCd = "";		// ���޻�(CP) ��ü�ڵ�
			String clientCd = "";	// ���� �ڵ�
			String userKey = "";	// ����Ű
			String userNm = "";		// ����� �̸�
			String homeTel = "";	// ����� �� ��ȭ��ȣ
			String mobile = "";		// ����� �޴���
			String email = "";		// ����� �̸���
			String goUrl = "";		// ���޻� �α����� �̵� URL , �̺�Ʈ �� ��Ÿ�뵵�� �̿�
						
			String enc_cspCd 	= (String)parser.getParameter("cspCd");		// ���޻�(CP) ��ü�ڵ�
			String enc_clientCd	= (String)parser.getParameter("clientCd");	// ���� �ڵ�
			String enc_userKey 	= (String)parser.getParameter("userKey");	// ����Ű
			String enc_userNm 	= (String)parser.getParameter("userNm");	// ����� �̸�
			String enc_homeTel 	= (String)parser.getParameter("homeTel");	// ����� �� ��ȭ��ȣ
			String enc_mobile 	= (String)parser.getParameter("mobile");	// ����� �޴���
			String enc_email 	= (String)parser.getParameter("email");		// ����� �̸���
			String enc_goUrl 	= (String)parser.getParameter("goUrl");		// ���޻� �α����� �̵� URL , �̺�Ʈ �� ��Ÿ�뵵�� �̿�

			if(!GolfUtil.empty(enc_cspCd)) 		cspCd 		= new String(Base64Encoder.decode(enc_cspCd));
			if(!GolfUtil.empty(enc_clientCd))	clientCd 	= new String(Base64Encoder.decode(enc_clientCd));
			if(!GolfUtil.empty(enc_userKey)) 	userKey 	= new String(Base64Encoder.decode(enc_userKey));
			if(!GolfUtil.empty(enc_userNm)) 	userNm 		= new String(Base64Encoder.decode(enc_userNm));
			if(!GolfUtil.empty(enc_homeTel)) 	homeTel 	= new String(Base64Encoder.decode(enc_homeTel));
			if(!GolfUtil.empty(enc_mobile)) 	mobile 		= new String(Base64Encoder.decode(enc_mobile));
			if(!GolfUtil.empty(enc_email)) 		email 		= new String(Base64Encoder.decode(enc_email));
			if(!GolfUtil.empty(enc_goUrl)) 		goUrl 		= new String(Base64Encoder.decode(enc_goUrl));
			
			
			debug(actnKey + " / cspCd : " + cspCd +" / clientCd : " + clientCd +" / userKey : " + userKey +" / userNm : " + userNm +" / homeTel : " + homeTel
					+" / mobile : " + mobile + " / email : " + email + " / goUrl : " + goUrl);
			

			request.getSession().removeAttribute("ezCspCd");
			request.getSession().removeAttribute("ezClientCd");
			request.getSession().removeAttribute("ezUserKey");
			request.getSession().removeAttribute("ezUserNm");
			request.getSession().removeAttribute("ezHomeTel");
			request.getSession().removeAttribute("ezMobile");
			request.getSession().removeAttribute("ezEmail");
			session.setAttribute("ezCspCd",cspCd);
			session.setAttribute("ezClientCd",clientCd);
			session.setAttribute("ezUserKey",userKey);
			session.setAttribute("ezUserNm",userNm);
			session.setAttribute("ezHomeTel",homeTel);
			session.setAttribute("ezMobile",mobile);
			session.setAttribute("ezEmail",email);

			
			paramMap.put("cspCd", cspCd); 
			paramMap.put("clientCd", clientCd); 
			paramMap.put("userKey", userKey); 
			paramMap.put("userNm", userNm); 
			paramMap.put("homeTel", homeTel); 
			paramMap.put("mobile", mobile); 
			paramMap.put("email", email); 
			paramMap.put("goUrl", goUrl); 
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
