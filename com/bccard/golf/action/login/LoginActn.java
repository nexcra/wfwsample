/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : LoginYnActn
*   작성자     : (주)미디어포스 조은미
*   내용        : 로그인 유무
*   적용범위  : Golf
*   작성일자  : 2009-06-11
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.login;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.common.DateUtil;

import com.bccard.golf.dbtao.DbTaoDataSet;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;

import com.bccard.waf.core.Code;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class LoginActn extends AbstractAction {

    /** Message Title       */ 
	public static final String TITLE ="로그인";    
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
        RequestParser parser = context.getRequestParser("default",request,response);
		HttpSession session = request.getSession();
		String sUURL = null;
		String uurl= null;
		String actionMode	= null;
		actionMode	= parser.getParameter("reActionMode");
		Map paramMap = parser.getParameterMap();
		//paramMap.put("reActionMode",actionMode);

		uurl= parser.getParameter("REQ_UURL", "");
		sUURL = (String) session.getAttribute("REQ_UURL");
		if( uurl== null || uurl.equals("") ){ 
			debug(" uurlis null ");
			uurl= sUURL;
		}
		//debug(" At LoginActn's UURL ==> "+ UURL);
		//debug(" At LoginActn's actionMode ==> "+ actionMode);
        request.setAttribute("paramMap", paramMap);
		//return getActionResponse(context); // response key
		return getActionResponse(context, "default");
    }
}

