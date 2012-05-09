/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntReportActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > ������ �Ű� ó�� 
*   �������  : Golf
*   �ۼ�����  : 2010-03-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event.shop;

import java.io.IOException;
import java.net.InetAddress;
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
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopOrdDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfEvntReportActn extends GolfActn{
	
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
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try { 
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// ��ó��
			String script = "";
			
			// �Ű���
			String userNm			= parser.getParameter("userNm","");	
			String juminno1			= parser.getParameter("juminno1","");	
			String juminno2			= parser.getParameter("juminno2","");	
			String mobile1			= parser.getParameter("mobile1","");	
			String mobile2			= parser.getParameter("mobile2","");	
			String mobile3			= parser.getParameter("mobile3","");	
			String regDate			= parser.getParameter("regDate","");	
			String product			= parser.getParameter("product","");	
			String pro_price		= parser.getParameter("pro_price","");	
			String price			= parser.getParameter("price","");	
			String siteurl			= parser.getParameter("siteurl","");		

			StringBuffer resultDesc_desc = new StringBuffer();
			StringBuffer resultDesc_html_desc = new StringBuffer();
			StringBuffer resultDesc = new StringBuffer();
			StringBuffer resultDesc_html = new StringBuffer(); 

			String resultString = "";
			resultString += "���� : " + userNm + "\n";
			resultString += "�ֹι�ȣ : " + juminno1 + "-" + juminno2 + "\n";
			resultString += "����ó : "  + mobile1 + "-" + mobile2 + "-" + mobile3 +  "\n";
			resultString += "������ : " + regDate + "\n";
			resultString += "�Ű�ǰ : " + product + "\n";
			resultString += "��ǰ���� : " + pro_price + "\n";
			resultString += "�ݾ����� : " + price + "\n";
			resultString += "�� ��� ����Ʈ (URL �ּ�) : " + siteurl + "\n";
			
			resultDesc.append(resultString+ "\n");
			resultDesc_html.append(resultString+ "<br>");

			debug(resultDesc.toString());

			String serverip = "";  // ����������
			String devip = "";	   // ���߱� ip ����

			try {
				serverip = InetAddress.getLocalHost().getHostAddress();
			} catch(Throwable t) {}

			try {
				devip = AppConfig.getAppProperty("DV_WAS_1ST");
			} catch(Throwable t) {}
			
			String emailTitle = "" ;
			String emailAdmin = "" ;
			String toMail =""; 
			
			debug("[��������� TM ����IP="  + serverip );	
			debug("[��������� TM ����IP="  + devip );

			/*���Ϲ߼�*/
			if (devip.equals(serverip)) {  //���߱�
				emailTitle = "[���߱� �׽�Ʈ ���� - ������ �Ű���] " + userNm + " ���� ";
				emailAdmin = "\"DEV���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
				resultDesc.append("\n�߽�>���� �� ������ ���߱⿡�� �׽�Ʈ������ ������ �� �̹Ƿ� �����Ͽ� �ֽʽÿ�.");
				resultDesc_html.append("<br>���� �� ������ ���߱⿡�� �׽�Ʈ������ ������ ���̹Ƿ� �����Ͽ� �ֽʽÿ�.");

				toMail="20109028@bcnuri.com;simijoa@naver.com;simijoa@hanmail.net;beagopa9@nate.com";
			} else {	// ���
				emailTitle = "[��������� - ������ �Ű���] " + userNm + " ���� ";
				emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
				toMail="dunk2000@bccard.com;yskkang@bccard.com;kshwan@bccard.com;bbongs7@bccard.com;bcgolf@bccard.com";
			}

			info("[��������� TM emailTitle="  + emailTitle );	
			info("[��������� TM emailAdmin="  + emailAdmin );
			info("[��������� TM toMail="  + toMail );

			
			EmailSend sender = new EmailSend();
			EmailEntity emailEtt = new EmailEntity("EUC_KR");
			
//			sender.setHost("211.181.255.38"); //���� host���� (���� 211.181.255.109)
			
			emailEtt.setFrom(emailAdmin);
			emailEtt.setSubject(emailTitle);
			emailEtt.setContents(resultDesc.toString(),resultDesc_html.toString());
			emailEtt.setTo(toMail);
			sender.send(emailEtt);
			
			script = "alert('������ �Ű� ��� �Ǿ����ϴ�.'); self.close();";

			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}

}
