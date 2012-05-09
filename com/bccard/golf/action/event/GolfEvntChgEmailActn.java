/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntChgEmailActn
*   �ۼ���    : E4NET ���弱
*   ����      : �̺�Ʈ > ������ũ�̺�Ʈ > ������ũ���� üũ
*   �������  : Golf
*   �ۼ�����  : 2009-08-03
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
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

import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkWinListDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	JSEUN
* @version	1.0
******************************************************************************/
public class GolfEvntChgEmailActn extends GolfActn{
	
	public static final String TITLE = "�����̾� ��ŷ �̺�Ʈ ��÷�� ����Ʈ";

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		
		
		try {
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// 01.��������üũ			
			
			String front_mail	= parser.getParameter("front_mail","");  //e-mail
			String last_mail	= parser.getParameter("last_mail","");  //e-mail
			String userId		= "";  //ID
			String jumin_no		= "";  //�ֹε�Ϲ�ȣ		
			String cupn         = "";  //������ȣ
			String userNm       = "";  //������̸�
			String email        = front_mail + "@" + last_mail;			

			//���������� ���� �Ķ����
			EmailSend sender = new EmailSend();
			EmailEntity emailEtt = new EmailEntity("EUC_KR");
			String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
			String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
			String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
			String emailTitle = "";
			String emailFileNm = "";
			String useYN       = "";

			boolean doUpdate = false;

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			if(usrEntity != null) {				
				userId          = (String)usrEntity.getAccount(); 
				jumin_no        = (String)usrEntity.getSocid();
				userNm			= (String)usrEntity.getName(); 
			}

			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("socid"   , jumin_no);
			dataSet.setString("email"   , email   );
			dataSet.setString("evnt_no" , "109"   );

			GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
			DbTaoResult cpnInfo = (DbTaoResult) inter.getCpnNumber(context, request, dataSet);

			if (cpnInfo != null && cpnInfo.isNext()) {
				cpnInfo.first();
				cpnInfo.next();
				if(cpnInfo.getString("RESULT").equals("00")){
					cupn		= cpnInfo.getString("CUPN");								
				}
			}

			doUpdate =  (boolean) inter.setChgEmailAddress(context, request, dataSet);

			if(doUpdate == true){							
				useYN = (String) inter.getUseYN(context, request, dataSet);

				emailTitle = "��������� ȸ������ ������ũ ��������";

				if(useYN.equals("Y")){
					emailFileNm = "/email_interpark1.html";
				}else{
					emailFileNm = "/email_interpark.html";
				}
				emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);							
				emailEtt.setFrom(emailAdmin);
				emailEtt.setSubject(emailTitle);
				emailEtt.setTo(email);
				sender.send(emailEtt);
			}
			request.setAttribute("useYN"    ,       useYN);
			request.setAttribute("email"    ,		email);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
