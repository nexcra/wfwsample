/*******************************************************************************
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�. �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�. 
 * �ۼ� : 2004.12.14[������(altair00@hotmail.com)] 
 * ���� :  
 * ���� : 
 * ���� :
 ******************************************************************************/
package com.bccard.golf.common;

import java.security.MessageDigest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * �� Program�� refresh ���� ������ ���� Ŭ����
 */


/******************************************************************************
* Golf : CommandToken
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

public class CommandToken {
 
	/**
	 * session�� request�� ������ token�� �����Ѵ�.
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
	 * session�� request�� token���� ���Ͽ� ��ȿ���� �˻��Ѵ�.
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
	 * ��ū����
	 * @ none
	 * @ �̵���������
	 */
	private static String toHex(byte[] digest) {
		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < digest.length; i++)
			buf.append(Integer.toHexString((int) digest[i] & 0x00ff));
		return buf.toString();
	} //toHex()

}