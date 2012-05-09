/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GenericHttpForwardActn
*   �ۼ���    : e4net 
*   ����      : ȭ�� �����ϱ�
*   �������  : welco
*   �ۼ�����  : 2006.12.27
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.util.*;
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
public class GenericHttpForwardActn extends GolfActn 
{	
	/**
    * overrides execute() in bccard.redapple.action.AbstractAction 
    * @param context WaContext
    * @param request HttpServletRequest
    * @param response HttpServletResponse
    * @version 2008.11.22
 	* @author 
    * @return ActionResponse 
    * @exception BaseException
    */
    public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		String sKey = null;
		String sValue = null;

		try {	    	
        	debug("=============>GenericHttpForwardActn start");
			RequestParser parser = context.getRequestParser("default", request, response);
			String iniPlug = parser.getParameter("INIpluginData", "");
			if ("".equals(iniPlug)) { request.setCharacterEncoding("8859_1"); }		

			Map paramMap = parser.getParameterMap();	
 		
			if(paramMap != null && paramMap.size()>0){
	
				if(paramMap.keySet() != null) {
					Iterator iIt = paramMap.keySet().iterator();
					while (iIt.hasNext()) {
						sKey = (String)iIt.next();
						Object obj = paramMap.get(sKey);
						if(obj instanceof  String ) {
							sValue = (String)paramMap.get(sKey);
							debug("===============>:"+sKey+" : ["+sValue+"]");
					} else {
							sValue = ((String[])paramMap.get(sKey))[0];
							debug("===============>2:"+sKey+" : ["+sValue+"]");
					}
					
						paramMap.put(sKey, sValue );
					}
				}
			}

			request.setAttribute("paramMap",paramMap);
        	debug("=============>GenericHttpForwardActn end");

		} catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}
		return super.getActionResponse(context);
	 }
}