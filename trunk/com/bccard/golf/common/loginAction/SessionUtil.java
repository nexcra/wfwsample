/*
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 * �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 * �ۼ� ���� : 2007. 12. 14 [sjjo@intermajor.com]
 */
package com.bccard.golf.common.loginAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.initech.eam.nls.CookieManager;
import com.initech.eam.smartenforcer.SECode;

public class SessionUtil {
	
	/**
	 * ���ǿ��� ������ ������ ã�´�.
	 * 
	 * @param request ��û
	 * @return ���� ������ ����, ���� ��� null
	 * @see #getSessionAttribute(HttpServletRequest, String)
	 */
	public static GolfAdminEtt getUserInfo(HttpServletRequest request) {
		return (GolfAdminEtt) SessionUtil.getSessionAttribute(request, "ADMIN_ENTITY");
	}
	/** 
	 * ���ǿ��� Front ����� ������ ã�´�.
	 * 
	 * @param request ��û
	 * @return ���� ������ ����, ���� ��� null
	 * @see #getSessionAttribute(HttpServletRequest, String)
	 */
	public static UcusrinfoEntity getFrontUserInfo(HttpServletRequest request) {
		
		//UcusrinfoEntity usrInfo = new UcusrinfoEntity();
		//usrInfo.setMemid(1111);
		//usrInfo.setAccount("ehapple");
		//usrInfo.setEmail1("sjjo@intermajor.com");
		//usrInfo.setName("test");
		//return usrInfo;
		return (UcusrinfoEntity) SessionUtil.getSessionAttribute(request, "FRONT_ENTITY");
	}
	
	
	/**
	 * ���ǿ��� Jolt Front ����� ������ ã�´�.
	 * 
	 * @param request ��û
	 * @return ���� ������ ����, ���� ��� null
	 * @see #getSessionAttribute(HttpServletRequest, String)
	 */
	public static GolfUserEtt getTopnUserInfo(HttpServletRequest request) {
		return (GolfUserEtt) SessionUtil.getSessionAttribute(request, "GOLF_ENTITY");
	}
	
	/**
	 * �־��� �̸����� ���� ���� ã�´�.
	 * 
	 * @param request ��û
	 * @param name ���� �Ӽ� �̸�
	 * @return ���� �Ӽ� ��, ���� ��� null
	 */
	public static Object getSessionAttribute(HttpServletRequest request, String name) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}

		return session.getAttribute(name);
	}
		
	/**
	 * SSO ���� �α��εǾ��ִ� ���̵� ȹ��, �α��� �ȵ��ִٸ� null
	 * @param request
	 * @return
	 */
	public static String getSsoId(HttpServletRequest request) {
		String sso_id = null;
		sso_id = CookieManager.getCookieValue(SECode.USER_ID, request);
		return sso_id;
	}
}
