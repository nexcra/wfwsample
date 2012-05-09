/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : CookUtil
*   작성자    : (주)미디어포스 권영만 
*   내용      : 화면 전달하기
*   적용범위  : golf
*   작성일자  : 2008.03.23
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** 
* CookUtil
* @version 2008.03.23
* @author media4th ymkwun
*/ 
public class CookUtil {
	/**
	 *	쿠키명을 넣으면 쿠키값 반환
	 *	@param	request 	쿠키명
	 *	@return	쿠키값
	 */
	public static String getCookie(HttpServletRequest request, String name) {
		Cookie cook, cookies[] = request.getCookies();
		String return_value = "";
		
		try {
			for (int i = 0; i < cookies.length; i++) {
				cook = cookies[i];
				if (cook.getName().equals(name)) return cook.getValue();
				// 수정시 알려주세요 문승주2006-08-27
				//if (cook.getName().equals(name)) return java.net.URLDecoder.decode(cook.getValue()); 
			}
		}catch(Exception e) { return ""; }
		return return_value;
	}


	/**
	 *	쿠키명을 넣으면 쿠키값 저장
	 *	@param	response 	쿠키명
	 *	@param 	name
	 *	@param 	value
	 *	@return void 		쿠키값
	 */
	public static void setCookie(HttpServletResponse response, String name, String value) {
		
		//보안검수 2009.9.15
		name = GolfUtil.sqlInjectionFilter(name);
		value = GolfUtil.sqlInjectionFilter(value); 
		
		Cookie cook = new Cookie(name, value);
//		Cookie cook = new Cookie(name, java.net.URLEncoder.encode(value));
		cook.setDomain("bccard.com");
		cook.setPath("/");
		cook.setSecure(true);
		response.addCookie(cook);
	}


	/**
	 *	쿠키명을 넣으면 해당 쿠키값 삭제
	 *	@param	request	쿠키명
	 *	@param 	response
	 * 	@param 	name
	 *	@return	쿠키값
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {

		Cookie cook, cookies[] = request.getCookies();

		for (int i = 0; i < cookies.length; i++) {
			cook = cookies[i];	// 보완검사 수정 2009.09.08		
			if (cook.getName().equals(name)) {
				cook = new Cookie(name, "");
				cook.setMaxAge(0);
				cook.setDomain("bccard.com");
				cook.setPath("/");
				cook.setSecure(true);
				response.addCookie(cook);
			} 
		}
	}
} 
