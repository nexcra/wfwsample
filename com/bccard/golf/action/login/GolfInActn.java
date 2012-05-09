/**********************************************************************
*   Activity명  : GolfInActn
*   작성자    : ㈜스포렘코리아 이종성
*   내용      : 회원정보조회 Inq action
*   적용범위  : Golf
*   작성일자  : 2005.08.31
******************************* 수정사항  ****************************
*    일자      버전   작성자      변경사항    
*********************************************************************
*  .
*********************************************************************/
package com.bccard.golf.action.login;

import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.common.DateUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.msg.MsgHandler;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

public class GolfInActn extends AbstractAction {
	public static final String TITLE ="golf 로그인후 정보조회";

	public ActionResponse execute(WaContext waContext,HttpServletRequest request,HttpServletResponse response) 
		throws ServletException, IOException, BaseException {
		debug("\n GolfInActn 처리 시작");
		TaoConnection con = null;
		String sysid = null;
		String uurl = null;
		//이메일 아이디와 주소를 분리하기 위해...
		//String emailID = null; 
		//String emailAddress = null;
		try {// 처리 사항.   
			RequestParser parser = waContext.getRequestParser("default",request,response);
			HttpSession session = request.getSession();
			
			sysid	= (String)session.getAttribute("SYSID");
			uurl	= (String)session.getAttribute("REQ_UURL");
			if(uurl == null || uurl.equals("")){
				uurl = "Main";
			}
						
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			
			if(usrEntity != null) {
				
				
				
			}
			

			

			request.setAttribute("paramMap",parser.getParameterMap());
		} catch (Throwable t) {
			MsgEtt ett = null;
			if ( t instanceof MsgHandler ) {
				ett = ((MsgHandler)t).getMsgEtt();
				ett.setTitle(TITLE);
			} else {
				ett = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, t.getMessage());
			}
			debug("\n t.getMessage() "+t.getMessage());
		} finally {
			try { con.close(); } catch(Throwable ignore) {}		
		}
		debug(" GO UURL "+uurl);
//		debug(" GO UURL "+uurl);
//		debug(" GO UURL "+uurl);
//		debug(" GO UURL "+uurl);
//		debug(" GO UURL "+uurl);
//		debug(" GO UURL "+uurl);
//		debug(" GO UURL "+uurl);
//		debug(" GO UURL "+uurl);
		
		//return getActionResponse(waContext, UURL);
		return getActionResponse(waContext, uurl); // response key
		//return executeAction(waContext, UURL, request, response);
	}
	
}