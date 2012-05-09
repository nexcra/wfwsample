/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : ResponseData
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : XML ��ſ� AJAX �׼� 
*   �������  : Golf
*   �ۼ�����  : 2009-04-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public abstract class AjaxActn extends AbstractAction {
	
	protected String title;

	/***********************************************************************
	 * �׼�ó��.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @return ��������
	 **********************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		if ( title == null ) getActionResponse(context).getDesc();
		ResponseData responseData = new ResponseData(title);
		request.setAttribute("com.bccard.golf.common.ResponseData", responseData);
		return ajaxExecute(context, request, response, responseData);
	}
	
	/***********************************************************************
	 * RequestParser.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @return ��������
	 **********************************************************************/
	public RequestParser getRequestParser(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		String bank = request.getParameter("AjaxBCMbdata");		
		if ( bank != null) {
			debug("RequestParser : bank");
			return context.getRequestParser("bank", request, response);
		} else {
			debug("RequestParser : default");
			return context.getRequestParser("default", request, response);
		}
	}
	
    /** ************************************************************************
     * AJAX Exception ����.
	 * @param request     HttpServletRequest
	 * @param t     Throwable
	 * @param responseKey     String
     ************************************************************************ */		
	protected void ajaxException(WaContext context, HttpServletRequest request, Throwable t) throws BaseException {
		request.setAttribute("RESPONSE_HANDLER", "xml");
		
		MsgEtt msgEtt;
 		if ( t instanceof MsgHandler ) {
 			msgEtt = ((MsgHandler)t).getMsgEtt();
 			msgEtt.setTitle(title);
 		} else {
 			msgEtt = new MsgEtt( MsgEtt.TYPE_ERROR, title, context.getMessage("SYSTEM_ERROR", null) );
 		}
 		throw new GolfException(msgEtt, t);
	}
	
	/***********************************************************************
	 * AJAX �׼�ó��.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @param responseData ResponseData
	 * @return ��������
	 **********************************************************************/
	public abstract ActionResponse ajaxExecute(WaContext context, HttpServletRequest request, HttpServletResponse response, ResponseData responseData) throws ServletException, IOException, BaseException;
}