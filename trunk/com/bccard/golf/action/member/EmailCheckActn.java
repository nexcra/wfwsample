/***************************************************************************************
*  Ŭ������        :   EmailCheckActn
*  �� �� ��        :   khko
*  ��    ��        :   �̸��� �ߺ�üũ
*  �������        :   bccard 
*  �ۼ�����        :   2006.05.12
************************** �����̷� ***************************************************
 * ���������� ���뿹���� �����Ϸ��� ����Ϸ��� �ۼ��� �������
 * 2008.10.28 2008.10.29 2008.10.28            ���뱹 ȸ������ �̸��� üũ �˾� �߰�
****************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import com.bccard.waf.core.*;
import com.bccard.waf.common.*;

import com.bccard.waf.action.AbstractAction;


/***************************************************************************************
 * �̸��� �ߺ�üũ
 * @version 2006 05 12
 * @author  khko
****************************************************************************************/
public class EmailCheckActn extends AbstractAction {
	
	/** *****************************************************************
	 * Action excecution method
	 * @param context		WaContext Object
	 * @param request		HttpServletRequest Object
	 * @param response		HttpServletResponse Object
	 * @return				ActionResponse Object
	 * @exception IOException, ServletException, BaseException if errors occur
	 ***************************************************************** */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) 
						throws IOException, ServletException, BaseException {

		RequestParser parser = context.getRequestParser("default", request, response);
		String responseKey  = parser.getParameter("gubun", "default");
		
		IndModifyOnlineProc proc  = (IndModifyOnlineProc)context.getProc("IndModifyOnlineProc");

		// �����ּ� �ߺ� üũ ����
		String mailId = parser.getParameter("email_id", "");
	    String mailDomain = parser.getParameter("select", "");
		
	    debug("responseKey = [" + responseKey + "]");
		debug("mailId + @ + mailDomain = [" + mailId + "@" + mailDomain + "]");
	    debug("parser.getParameter(EMAIL) = [" + parser.getParameter("EMAIL") + "]");

		boolean isMailChange = (mailId + "@" + mailDomain).equals(parser.getParameter("EMAIL")) ? false : true;
		int mailCnt = 0;
		
		if(isMailChange) {	// ������ �����Ǿ��� ��� ���ο� ���� �ߺ� üũ
			mailCnt = proc.getEmailCnt(context, parser);
		}
		//	�����ּ� �ߺ� üũ ��
		
		// 2008.10.29 �̸��ϵ� �߰��� �ѱ�
		request.setAttribute("email", mailId + "@" + mailDomain);
		request.setAttribute("mailCnt", "" + mailCnt);
		request.setAttribute("isMailChange", isMailChange==true?"true":"false");

		return getActionResponse(context, responseKey);	
	}
}
