/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : CookUtil
*   �ۼ���    : (��)�̵������ �ǿ��� 
*   ����      : ȭ�� �����ϱ�
*   �������  : golf
*   �ۼ�����  : 2008.03.23
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
	 *	��Ű���� ������ ��Ű�� ��ȯ
	 *	@param	request 	��Ű��
	 *	@return	��Ű��
	 */
	public static String getCookie(HttpServletRequest request, String name) {
		Cookie cook, cookies[] = request.getCookies();
		String return_value = "";
		
		try {
			for (int i = 0; i < cookies.length; i++) {
				cook = cookies[i];
				if (cook.getName().equals(name)) return cook.getValue();
				// ������ �˷��ּ��� ������2006-08-27
				//if (cook.getName().equals(name)) return java.net.URLDecoder.decode(cook.getValue()); 
			}
		}catch(Exception e) { return ""; }
		return return_value;
	}


	/**
	 *	��Ű���� ������ ��Ű�� ����
	 *	@param	response 	��Ű��
	 *	@param 	name
	 *	@param 	value
	 *	@return void 		��Ű��
	 */
	public static void setCookie(HttpServletResponse response, String name, String value) {
		
		//���Ȱ˼� 2009.9.15
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
	 *	��Ű���� ������ �ش� ��Ű�� ����
	 *	@param	request	��Ű��
	 *	@param 	response
	 * 	@param 	name
	 *	@return	��Ű��
	 */
	public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {

		Cookie cook, cookies[] = request.getCookies();

		for (int i = 0; i < cookies.length; i++) {
			cook = cookies[i];	// ���ϰ˻� ���� 2009.09.08		
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
