/*******************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다. 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다. 
 * 작성 : 2004.12.14[서영준(altair00@hotmail.com)] 
 * 내용 :  
 * 수정 : 
 * 내용 :
 ******************************************************************************/
package com.bccard.golf.common;

import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 본 Program은 refresh 방지 목적을 위한 클래스
 */


/******************************************************************************
* Golf : CommandToken
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/

public class CommandToken {
 
	/**
	 * session과 request에 동일한 token을 세팅한다.
	 * @param request
	 */
	public static void set(HttpServletRequest request) {

		HttpSession session = request.getSession(true);
		long systime = System.currentTimeMillis();
		byte[] time = new Long(systime).toString().getBytes();
		byte[] id = session.getId().getBytes();

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(id);
			md5.update(time);

			String token = CommandToken.toHex(md5.digest());
			request.setAttribute("token", token);
			session.setAttribute("token", token);
		} catch (Exception e) {
		}
	} //set()

	/**
	 * session과 request의 token값을 비교하여 유효한지 검사한다.
	 * @param request
	 * @return
	 */
	public static boolean isValid(HttpServletRequest request) {
		HttpSession session = request.getSession(true);
		String requestToken = request.getParameter("token");
		String sessionToken = (String) session.getAttribute("token");

		if (requestToken == null || sessionToken == null) {
			return false;
		} else {
			return requestToken.equals(sessionToken);
		}
	} //isValid()

	/**
	 * 토큰생성
	 * @ none
	 * @ 이동할페이지
	 */
	private static String toHex(byte[] digest) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < digest.length; i++)
			buf.append(Integer.toHexString((int) digest[i] & 0x00ff));
		return buf.toString();
	} //toHex()

}