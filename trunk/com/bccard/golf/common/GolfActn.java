/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : BcasActn
*   작성자    : e4net 
*   내용      : 화면 전달하기
*   적용범위  : welco
*   작성일자  : 2006.12.27
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.io.IOException;
import java.util.Random;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/**
* GenericHttpForwardActn
* @version 2006.12.27
* @author e4net
*/ 
public class GolfActn extends AbstractAction 
{	
	/**
    * overrides execute() in bccard.redapple.action.AbstractAction 
    * @param context WaContext
    * @param request HttpServletRequest
    * @param response HttpServletResponse
    * @version 2006.11.22
 	* @author 
    * @return ActionResponse
    * @exception BaseException
    */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		try {	    	
			debug("GenericHttpForwardActn");
			RequestParser parser = context.getRequestParser("default", request, response);
			String iniPlug = parser.getParameter("INIpluginData", "");
			if ("".equals(iniPlug)) { request.setCharacterEncoding("8859_1"); }		

			Map paramMap = parser.getParameterMap();	
			request.setAttribute("paramMap",paramMap);
			return super.getActionResponse(context);
		} catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
	}

	/**
	 * 예외 처리.
	 * @param context WaContext
	 * @param req HttpServletRequest
	 * @param res HttpServletResponse
	 * @param t Throwable
	 * @return ActionResponse
	 */
	protected ActionResponse errorHandler(WaContext context, HttpServletRequest req,HttpServletResponse res, Throwable t) {
		if ( t instanceof GolfException ) {
			req.setAttribute("com.bccard.golf.common.GolfException",t);
		} else {
			req.setAttribute("com.bccard.golf.common.GolfException",new GolfException("GOLF.ERROR",t));
		}
		return getActionResponse(context,"error");
	}
	
}