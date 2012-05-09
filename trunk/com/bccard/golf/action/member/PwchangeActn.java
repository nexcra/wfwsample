/**********************************************************************************************************************
*   클래스명  : PwchangeActn
*   작성자    : 조용국
*   내용      : 회원 전환 액션
*   적용범위  : bccard전체
*   작성일자  : 2004.02.3
************************** 수정이력 ***********************************************************************************
*    일자      버전   작성자   변경사항
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