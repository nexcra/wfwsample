/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : LoginYnActn
*   �ۼ���     : (��)�̵������ ������
*   ����        : �α��� ����
*   �������  : Golf
*   �ۼ�����  : 2009-06-11
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class LoginActn extends AbstractAction {

    /** Message Title       */ 
	public static final String TITLE ="�α���";    
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

