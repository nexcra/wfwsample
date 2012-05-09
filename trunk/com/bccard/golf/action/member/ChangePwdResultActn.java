/**************************************************************************************************
*   클래스명  : ChangePwdResultActn
*   작성자    : 현광준
*   내용      : 비밀번호변경 UPDATE action
*   적용범위  : bccard전체
*   작성일자  : 2004.02.3
************************** 수정이력 *****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/** ****************************************************************************
 * Login Action  
 * @version   2004.02.03
 * @author    <A href="mailto:kjhyun@e4net.net">hyun kwang joon</A>
 **************************************************************************** */

public class ChangePwdResultActn extends AbstractAction {

	/**
    * overrides execute() in bccard.redapple.action.AbstractAction 
    * @param context WaContext
    * @param request HttpServletRequest
    * @param response HttpServletResponse
    * @version 2007-06-20
 	* @author 
    * @return ActionResponse
    * @exception IOException, ServletException, BaseException
    */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) 
                        throws IOException, ServletException, BaseException {
				
		String responseKey  = "default";

		//String currentActionKey = getActionKey(context); //getActionKey(servlet, request);
		//HttpSession session = request.getSession(true);
		//IndUserEtt user = (IndUserEtt)session.getAttribute("LOGIN_USER");
		UcusrinfoEntity user = SessionUtil.getFrontUserInfo(request);

		RequestParser parser = context.getRequestParser("default",request,response);

		ChangePwdProc proc  = (ChangePwdProc)context.getProc("ChangePwdProc");

		String argv3 = parser.getParameter("argv3" , "");

		UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
		String memClss = userEtt.getMemberClss();
		
		// 서버 IP
		String appip = "";
		try {
			appip = InetAddress.getLocalHost().getHostAddress();
		} catch(Throwable t) {
		}
		if (appip == null) appip = "unknown";

		String devip = "";// 개발기 ip정보
		String devWebIp = ""; //개발기WEB IP정보

		try {
			devip = AppConfig.getAppProperty("DEV_APP_IP");
			devWebIp = "http://" + AppConfig.getAppProperty("DEV_WEB_IP");
		} catch (IOException ignore) {}
		if ( devip == null ) devip = "";

		String url = "";

		if(argv3.equals("2")){
			if ( devip.equals(appip) ) {  //개발기
				url = "http://pilot.bcline.com:7100";
			} else {
				url = "http://www.bcline.com";
			}
		}else if(argv3.equals("3")){
			if ( devip.equals(appip) ) {  //개발기
				url = "http://test.luckybc.com";
			} else {
				url = "http://www.luckybc.com";
			}
		}else if(argv3.equals("5")){
			url = "http://www.edubc.com";   
		}else if(argv3.equals("4")){
			//url = "4";//11.19 링크경로 수정
			if ( devip.equals(appip) ) {  //개발기
				url = devWebIp + ":7200/";
			} else {
				url = "http://www.bcshopping.co.kr";
			}
		}else if(argv3.equals("7")){
			if ( devip.equals(appip) ) {  //개발기
				url = devWebIp + ":7700/";
			} else {
				url = "http://point.bccard.com";
			}
		}else if(argv3.equals("8")){
			if ( devip.equals(appip) ) {  //개발기
				url = "http://vip.bccard.com/app/view/platinum_jsp/ptnm_lgin_rslt.jsp";
			} else {
				url = devWebIp + ":8200/app/view/platinum_jsp/ptnm_lgin_rslt.jsp";
			}
		}else if ("9".equals(argv3)) {
			if ( devip.equals(appip) ) {  //개발기
				url = "http://develop.bccard.com:9520/golf"; 
			} else {  // 운영기
				//url = "http://vip.bccard.com/app/view/platinum_jsp/ptnm_lgin_rslt.jsp"; 
				url = "http://golf.bccard.com"; 
			}
		} else {
			url = "/app/card/index.do";
		}

		request.setAttribute("argv3",argv3);
		request.setAttribute("url",url);

		if(proc.addMember(context, parser, memClss)){
			// BCLOG관련 추가 코드 ***************************************************************************
			String msg = "CHPASSWD" + "|" + user.getSocid() + "|" + "1" + "|" + user.getAccount() + "|" +
			request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss");
			//BcLog.memberLog(msg);
			System.out.println(msg);
			//**********************************************************************************************
			request.setAttribute("RESULT_MESSAGE","비밀번호변경이 완료되었습니다.");
		}
		else {
			request.setAttribute("RESULT_MESSAGE","비밀번호변경이 실패되었습니다. 관리자에게 문의하여 주시기 바랍니다. ");
		}
		return getActionResponse(context,responseKey);
	}
}