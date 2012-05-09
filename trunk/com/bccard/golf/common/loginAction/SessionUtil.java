/*
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 작성 일자 : 2007. 12. 14 [sjjo@intermajor.com]
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
	 * 세션에서 관리자 정보를 찾는다.
	 * 
	 * @param request 요청
	 * @return 세션 관리자 정보, 없는 경우 null
	 * @see #getSessionAttribute(HttpServletRequest, String)
	 */
	public static GolfAdminEtt getUserInfo(HttpServletRequest request) {
		return (GolfAdminEtt) SessionUtil.getSessionAttribute(request, "ADMIN_ENTITY");
	}
	/** 
	 * 세션에서 Front 사용자 정보를 찾는다.
	 * 
	 * @param request 요청
	 * @return 세션 관리자 정보, 없는 경우 null
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
	 * 세션에서 Jolt Front 사용자 정보를 찾는다.
	 * 
	 * @param request 요청
	 * @return 세션 관리자 정보, 없는 경우 null
	 * @see #getSessionAttribute(HttpServletRequest, String)
	 */
	public static GolfUserEtt getTopnUserInfo(HttpServletRequest request) {
		return (GolfUserEtt) SessionUtil.getSessionAttribute(request, "GOLF_ENTITY");
	}
	
	/**
	 * 주어진 이름으로 세션 값을 찾는다.
	 * 
	 * @param request 요청
	 * @param name 세션 속성 이름
	 * @return 세션 속성 값, 없는 경우 null
	 */
	public static Object getSessionAttribute(HttpServletRequest request, String name) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}

		return session.getAttribute(name);
	}
		
	/**
	 * SSO 에서 로그인되어있는 아이디 획득, 로그인 안되있다면 null
	 * @param request
	 * @return
	 */
	public static String getSsoId(HttpServletRequest request) {
		String sso_id = null;
		sso_id = CookieManager.getCookieValue(SECode.USER_ID, request);
		return sso_id;
	}
}
