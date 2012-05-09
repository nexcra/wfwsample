/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : ResponseData
*   작성자     : (주)미디어포스 권영만
*   내용        : XML 통신용 AJAX 액션 
*   적용범위  : Golf
*   작성일자  : 2009-04-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public abstract class AjaxActn extends AbstractAction {
	
	protected String title;

	/***********************************************************************
	 * 액션처리.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @return 응답정보
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
	 * @return 응답정보
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
     * AJAX Exception 생성.
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
	 * AJAX 액션처리.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @param responseData ResponseData
	 * @return 응답정보
	 **********************************************************************/
	public abstract ActionResponse ajaxExecute(WaContext context, HttpServletRequest request, HttpServletResponse response, ResponseData responseData) throws ServletException, IOException, BaseException;
}