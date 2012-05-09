/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmUNEventWinReSendActn
*   �ۼ���    : E4NET ���弱
*   ����      : ������ > ���ΰ��� > ȸ������ > ȸ������Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-08-05
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.*;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;

/******************************************************************************
* Topn
* @author	E4NET
* @version	1.0
******************************************************************************/
public class GolfAdmUNEventWinReSendActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ���� > �����̺�Ʈ > ����";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
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
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			

			paramMap.put("title", TITLE);			

			// Request �� ����				
			String cupn         = parser.getParameter("cupn" ,"");                // ���� ��ȣ				
			String email        = parser.getParameter("email" ,"");               // email	
			String userNm       = parser.getParameter("userNm", "");			  // �̸�	
			String socid        = parser.getParameter("socid", "");				  // �ֹε�Ϲ�ȣ	
			String evnt_no      = parser.getParameter("evnt_no", "");			  // �̺�Ʈ ��ȣ	

			dataSet.setString("socid",socid);
			dataSet.setString("evnt_no", evnt_no);

			GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
			String useYN = (String) inter.getUseYN(context, request, dataSet);

			EmailSend sender = new EmailSend();
			EmailEntity emailEtt = new EmailEntity("EUC_KR");
			String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
			String imgPath = "<img src=\"";
			String hrefPath = "<a href=\"";
			String emailTitle = "";
			String emailFileNm = "";


			if(evnt_no.equals("109")){	// ������ũ
				emailTitle = "��������� ȸ������ ������ũ ��������";
				if(useYN.equals("Y")){
					emailFileNm = "/email_interpark1.html";
				}else{
					emailFileNm = "/email_interpark.html";
				}
				emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);
				
			}else if(evnt_no.equals("119")){		// TM ȸ�� ��ȭ���ű� �̺�Ʈ
				emailTitle = "[Golf Loun.G] ��������� TM ��ȭ���ű�";
				emailFileNm = "/eamil_tm_movie.html";
				emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, "");
			}
			
			
			
			emailEtt.setFrom(emailAdmin);
			emailEtt.setSubject(emailTitle);
			emailEtt.setTo(email);
			sender.send(emailEtt);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}