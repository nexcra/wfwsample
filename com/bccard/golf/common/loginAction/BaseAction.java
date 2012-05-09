/*
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� ���� : 2007. 12. 13 [hsbang@intermajor.com]
*/
package com.bccard.golf.common.loginAction;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.WaContext;

public abstract class BaseAction extends AbstractAction{
	
	/** ������ ��û ���� �Ӽ� �̸� */
	public static final String DEFAULT_ADMIN_ATTRIBUTE_NAME = "admin";
	
	/**
	 * @param context
	 * @param request
	 * @param response
	 * @return 
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse goRedirect(WaContext context, HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException, BaseException{
		return goRedirect(context, request, response, false, "default");
	}

	/**
	 * @param context
	 * @param request
	 * @param response
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse goRedirect(WaContext context, HttpServletRequest request, HttpServletResponse response, String key)throws IOException, ServletException, BaseException{
		return goRedirect(context, request, response, false, key);
	}
	
	/**
	 * @param context
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse goRedirectIni(WaContext context, HttpServletRequest request, HttpServletResponse response)throws IOException, ServletException, BaseException{
		return goRedirect(context, request, response, true, "default");
	}	
	
	/**
	 * @param context
	 * @param request
	 * @param response
	 * @param isINIpluginData
	 * @param key
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	private ActionResponse goRedirect(WaContext context, HttpServletRequest request, HttpServletResponse response, boolean isINIpluginData, String key)throws IOException, ServletException, BaseException{
		String responseKey = getActionResponse(context, key).getContents();
		Map returnMap = (Map) request.getAttribute("paramMap");
		
		PrintWriter out = response.getWriter();
		htmlContent(out, responseKey, returnMap, isINIpluginData);
		ActionResponse respon = new ActionResponse();
		respon.setType(ActionResponse.TYPE_OUT);
		return respon;
	}
	
	/**
	 * @param out
	 * @param responseKey
	 * @param returnMap
	 * @param isINIpluginData
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	private void htmlContent(PrintWriter out, String responseKey, Map returnMap, boolean isINIpluginData)throws IOException, ServletException, BaseException{
		out.print("<html>");
		out.print("\n <script language='javascript'>");
		out.print("\n 	function goAction(){");
		if(returnMap != null && returnMap.get("script") != null) out.print( returnMap.get("script").toString() );
		if(isINIpluginData) out.print("\n 		EncForm(document.frm);");
		else out.print("\n 		document.frm.submit();");
		out.print("\n  }");
		out.print("\n </script>");
		out.print("\n  <body onload=\"goAction();\">");
		out.print("\n <form name=\"frm\" method=\"post\" action=\"" + responseKey + "\">");
		
		if(isINIpluginData) out.print("\n<input type='hidden' name='INIpluginData'>");
		
		if(returnMap != null){
			for(Iterator it = returnMap.keySet().iterator(); it.hasNext(); ) {
	            String param = (String)it.next();
	            if(!"script".equals(param)){
		            Object o = returnMap.get(param);
		            if ( o instanceof String ) {
		                String value = (String)o;
		                out.print("\n <input type='hidden' name='"+param+"' value=\""+StrUtil.replace(value,"\"","&quot;")+"\">");
		            } else if ( o instanceof String[] ) {
		                String[] values = (String[])o;
		                for (int i=0;i<values.length;i++ ) {
		                    out.print("\n <input type='hidden' name='"+param+"' value=\""+StrUtil.replace(values[i],"\"","&quot;")+"\">");
		                }
		            }
	            }
	        }
		}
		out.print("\n </form>");
		out.print("\n </body>");
		out.print("\n</html>");
	}
	
	/**
	 * ������ ������ ��û ������ �����Ѵ�.
	 * 
	 * @param request ��û
	 * @see #exposeAdminToRequest(HttpServletRequest, String)
	 */
	protected void exposeAdminToRequest(HttpServletRequest request) {
		exposeAdminToRequest(request, DEFAULT_ADMIN_ATTRIBUTE_NAME);
	}
	
	/**
	 * ������ ������ ��û ������ �����Ѵ�.
	 * 
	 * @param request ��û
	 * @param adminAttributeName ������ �Ӽ� �̸�
	 */
	protected void exposeAdminToRequest(HttpServletRequest request, String adminAttributeName) {
		//TopnSessionEtt admin = TopnSessionEtt.getMemId(request);
		//if (admin != null) {
		//	request.setAttribute(adminAttributeName, admin);
		//}
	}
	
	/**
	 * 
	 * @see com.bccard.waf.action.AbstractAction#execute(com.bccard.waf.core.WaContext, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 * @param context
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public abstract ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException;

}
















