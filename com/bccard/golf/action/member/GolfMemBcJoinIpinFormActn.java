/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemJoinPopActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 가입 팝업
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.StringEncrypter;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemJoinNocardDaoProc;
import com.initech.eam.nls.CookieManager;
import com.initech.eam.smartenforcer.SECode;
import com.initech.eam.nls.NLSHelper;



/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemBcJoinIpinFormActn extends GolfActn{
	
	public static final String TITLE = "회원 > BC 회원가입 완료";

	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";

		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		UcusrinfoEntity ucusrinfo = null; 
		Connection con = null;

		UcusrinfoDaoProc proc = (UcusrinfoDaoProc) context.getProc("UcusrinfoDao");	


		try {			
			// 01. 세션정보체크
	        UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
        	usrEntity = new UcusrinfoEntity(); 
			
			// 입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);

			String type = parser.getParameter("type", "");
			String userId = parser.getParameter("userId", "");
			
			paramMap.put("title", TITLE);
	        paramMap.put("userId", userId);
	        paramMap.put("type", type);
	        paramMap.remove("INIpluginData");

			debug("GolfMemBcJoinIpinFormActn : userId = " + userId + " / type : " + type);
			
			request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
