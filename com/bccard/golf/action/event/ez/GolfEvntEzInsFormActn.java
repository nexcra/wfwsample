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

import com.bccard.golf.common.AppConfig;
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
public class GolfEvntEzInsFormActn extends GolfActn{
	
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

			String cspCd 	= (String)request.getSession().getAttribute("ezCspCd");		// ���޻�(CP) ��ü�ڵ�
			String clientCd	= (String)request.getSession().getAttribute("ezClientCd");	// ���� �ڵ�
			String userKey 	= (String)request.getSession().getAttribute("ezUserKey");	// ����Ű
			String userNm 	= (String)request.getSession().getAttribute("ezUserNm");	// ����� �̸�
			String homeTel 	= (String)request.getSession().getAttribute("ezHomeTel");	// ����� �� ��ȭ��ȣ
			String mobile 	= (String)request.getSession().getAttribute("ezMobile");	// ����� �޴���
			String email 	= (String)request.getSession().getAttribute("ezEmail");		// ����� �̸���
			

			String idx = "";	// ���õ��
			if(actnKey.equals("GolfEvnt1InsForm")){
				idx = "1";
			}else if(actnKey.equals("GolfEvnt2InsForm")){
				idx = "2";
			}else if(actnKey.equals("GolfEvnt3InsForm")){
				idx = "3";
			}else if(actnKey.equals("GolfEvnt7InsForm")){
				idx = "7";
			}else if(actnKey.equals("GolfEvntInsForm")){
				idx = "";
			}

			
			debug(actnKey + " / cspCd : " + cspCd +" / clientCd : " + clientCd +" / userKey : " + userKey +" / userNm : " + userNm +" / homeTel : " + homeTel
					+" / mobile : " + mobile + " / email : " + email + " / idx : " + idx);

			// �Ǽ�, �¼� ����
			String serverNm = "";
			String serverip = InetAddress.getLocalHost().getHostAddress();	// ����������
			String devip = AppConfig.getAppProperty("DV_WAS_1ST");		// ���߱� ip ����
			if(serverip.equals(devip)){
				serverNm = "dev";
			}else{
				serverNm = "real";
			}
			

			paramMap.put("idx", idx); 
			paramMap.put("cspCd", cspCd); 
			paramMap.put("clientCd", clientCd); 
			paramMap.put("userKey", userKey); 
			paramMap.put("userNm", userNm); 
			paramMap.put("homeTel", homeTel); 
			paramMap.put("mobile", mobile); 
			paramMap.put("email", email); 
			paramMap.put("serverNm", serverNm); 
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
