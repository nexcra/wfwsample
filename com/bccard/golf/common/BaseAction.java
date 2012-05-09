/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : BaseAction
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ���� AbstractAction
*   �������  : golf
*   �ۼ�����  : 2009-03-23
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.core.RequestParser;

import com.bccard.golf.common.GolfUtil;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public abstract class BaseAction  extends AbstractAction {
	
    /**
     * �� ������ üũ
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return (str == null || "".equals(str.trim()));
    }
    /**
     * ������Ʈ�� Null ���� ��
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj) {
        return (obj == null );
    } 
    
    /**
     * ���ε� ������ ���
     * @return
     */
    public String getUploadDirectory(){
    	return "";
    }
    
    
    /**
     * parameter �� ���� GET query string�� �����.
     * @param paramNames
     * @return
     */
    public String makeParameterQuery(RequestParser parser, String[] paramNames) {
    	String query = "";
    	String s = null;
    	for(int i=0, n=0; i<paramNames.length; i++){
    		s = parser.getParameter(paramNames[i]);
    		if(s != null && !"".equals(s.trim())){
    			query += "&"+paramNames[i]+"="+URLEncoder.encode(s);
    			n++;
    		}
    	}
    	return query;
    }

    /**
     * parameter �� ���� GET query string�� �����.
     * @param paramNames
     * @return
     */
    public String makeParameterQuery(HttpServletRequest parser, String[] paramNames) {
    	String query = "";
    	String s = null;
    	for(int i=0, n=0; i<paramNames.length; i++){
    		s = parser.getParameter(paramNames[i]);
    		if(s != null && !"".equals(s.trim())){
    			query += "&"+paramNames[i]+"="+URLEncoder.encode(s);
    			n++;
    		}
    	}
    	return query;
    }
    
    /**
     * queryString value�� ���ڵ� �Ѵ�
     * @return
     */
    public String getQueryString(HttpServletRequest request) {
    	String query="";
    	Enumeration em = request.getParameterNames();
    	String k = null;
    	while(em.hasMoreElements()) {
    		k = (String)em.nextElement();
    		query += "&"+ k +"="+URLEncoder.encode(request.getParameter(k));
    	}
    	return query;
    }  
    
    /**
     * ��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�. <br>
     * String Array�� ���� ó�� �߰�
     * @param request
     * @return Map
     */
	public static Map getParamToMap(HttpServletRequest request){
		java.util.Enumeration em = request.getParameterNames();
		Map parametersMap = new HashMap();
		
		while (em.hasMoreElements()) {
			String str = (String) em.nextElement();
			String[] values = request.getParameterValues(str);
			if(values != null){
				if(values.length==1){
					if (!GolfUtil.isNull(values[0])) {
						parametersMap.put(str, values[0]);
						
					}
				}else{
					parametersMap.put(str, values);
				}
			}
		}
		return parametersMap;
	}
	
	/**
	 * ������ �̵��� �޽��� ������ ���ǿ� �����Ѵ�.
	 * @param request HttpServletRequest ��ü
	 * @param message ���ǿ� ������ �޽������ڿ�
	 */
	public void setRedirectMessage(HttpServletRequest request, String message) {
		HttpSession session = request.getSession();
		session.setAttribute("message", message);
	}
	
	/**
	 * ���� ���������� ������ ���Ǹ޽����� ������ request�� �����Ѵ�. <br>
	 * <pre>
	 * view�ܿ��� ����/����/����� ó���� �˸��޽����� ����Ҷ� �����.
	 * ex)
	 * <c:if test='${not empty message}'>
	 *		<script type='text/javascript'>
	 *		alert("<c:out value='${message}'/>");
	 *		</script>
	 *	</c:if>
	 * </pre>
	 * @param request HttpServletRequest��ü
	 */
	public void getRedirectMessage(HttpServletRequest request) {
		HttpSession session = request.getSession();
		String message = (String)session.getAttribute("message");
		if(!BaseAction.isNull(message)){
			request.setAttribute("message", message);
			session.removeAttribute("message");
		}
	}
	
}
