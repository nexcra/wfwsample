/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : BaseAction
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 기초 AbstractAction
*   적용범위  : golf
*   작성일자  : 2009-03-23
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public abstract class BaseAction  extends AbstractAction {
	
    /**
     * 빈 값인지 체크
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return (str == null || "".equals(str.trim()));
    }
    /**
     * 오브잭트가 Null 인지 비교
     * @param obj
     * @return
     */
    public static boolean isNull(Object obj) {
        return (obj == null );
    } 
    
    /**
     * 업로드 물리적 경로
     * @return
     */
    public String getUploadDirectory(){
    	return "";
    }
    
    
    /**
     * parameter 에 대한 GET query string을 만든다.
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
     * parameter 에 대한 GET query string을 만든다.
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
     * queryString value를 인코딩 한다
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
     * 모든 파라미터값을 맵에 담아 반환한다. <br>
     * String Array에 대한 처리 추가
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
	 * 페이지 이동시 메시지 정보를 세션에 세팅한다.
	 * @param request HttpServletRequest 객체
	 * @param message 세션에 저장할 메시지문자열
	 */
	public void setRedirectMessage(HttpServletRequest request, String message) {
		HttpSession session = request.getSession();
		session.setAttribute("message", message);
	}
	
	/**
	 * 이전 페이지에서 설정한 세션메시지를 가져와 request에 세팅한다. <br>
	 * <pre>
	 * view단에서 수정/삭제/등록후 처리된 알림메시지를 출력할때 사용함.
	 * ex)
	 * <c:if test='${not empty message}'>
	 *		<script type='text/javascript'>
	 *		alert("<c:out value='${message}'/>");
	 *		</script>
	 *	</c:if>
	 * </pre>
	 * @param request HttpServletRequest객체
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
