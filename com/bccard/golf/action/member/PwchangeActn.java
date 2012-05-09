/**********************************************************************************************************************
*   Ŭ������  : PwchangeActn
*   �ۼ���    : ���뱹
*   ����      : ȸ�� ��ȯ �׼�
*   �������  : bccard��ü
*   �ۼ�����  : 2004.02.3
************************** �����̷� ***********************************************************************************
*    ����      ����   �ۼ���   �������
*
**********************************************************************************************************************/

package com.bccard.golf.action.member;

import java.io.IOException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractAction;

public class PwchangeActn extends AbstractAction {
	
	/**
    * overrides execute() in bccard.redapple.action.AbstractAction 
    * @param context WaContext
    * @param request HttpServletRequest
    * @param response HttpServletResponse
    * @version 2007-06-20
 	* @author 
    * @return ActionResponse
    * @exception IOException, ServletException, BaseException
    */
    public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) 
                        throws IOException, ServletException, BaseException {

        RequestParser parser = context.getRequestParser("default",request,response); 
        String account = parser.getParameter("account");
        String socid = parser.getParameter("socid");

        request.setAttribute("account",account);
        request.setAttribute("socid",socid);
        
        return getActionResponse(context);

	}
}