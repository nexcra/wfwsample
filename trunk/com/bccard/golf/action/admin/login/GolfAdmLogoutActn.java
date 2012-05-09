/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointAdmLogoutActn
*   작성자    : (주)미디어포스 조은미
*   내용      : 골프 관리자 로그인 프로세스
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.action.admin.login;
 
import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.proc.login.ChkDupAccountDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmLogoutActn extends AbstractAction {
	
	public static final String TITLE="로그아웃"; 
	
	/***************************************************************************************
	* 비씨골프관리자로그아웃 프로세스
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(  WaContext context,
				HttpServletRequest request,
				HttpServletResponse response)
				throws IOException, ServletException, BaseException {
		
		//debug("==== GolfPointAdmLogoutActn start ===");
		//------------------------------------------------------------------------------------
		HttpSession session = request.getSession(false);
		if(session != null) {
			session.invalidate();
		}
		//debug("LOGOUT === > ");
		
		//debug("==== GolfPointAdmLogoutActn end ===");
		return getActionResponse(context, "default");
	
	
	}
}
