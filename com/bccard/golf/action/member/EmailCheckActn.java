/***************************************************************************************
*  클래스명        :   EmailCheckActn
*  작 성 자        :   khko
*  내    용        :   이메일 중복체크
*  적용범위        :   bccard 
*  작성일자        :   2006.05.12
************************** 수정이력 ***************************************************
 * 수정시작일 적용예정일 수정완료일 적용완료일 작성자 변경사항
 * 2008.10.28 2008.10.29 2008.10.28            조용국 회원은행 이메일 체크 팝업 추가
****************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

import com.bccard.waf.core.*;
import com.bccard.waf.common.*;

import com.bccard.waf.action.AbstractAction;


/***************************************************************************************
 * 이메일 중복체크
 * @version 2006 05 12
 * @author  khko
****************************************************************************************/
public class EmailCheckActn extends AbstractAction {
	
	/** *****************************************************************
	 * Action excecution method
	 * @param context		WaContext Object
	 * @param request		HttpServletRequest Object
	 * @param response		HttpServletResponse Object
	 * @return				ActionResponse Object
	 * @exception IOException, ServletException, BaseException if errors occur
	 ***************************************************************** */
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) 
						throws IOException, ServletException, BaseException {

		RequestParser parser = context.getRequestParser("default", request, response);
		String responseKey  = parser.getParameter("gubun", "default");
		
		IndModifyOnlineProc proc  = (IndModifyOnlineProc)context.getProc("IndModifyOnlineProc");

		// 메일주소 중복 체크 시작
		String mailId = parser.getParameter("email_id", "");
	    String mailDomain = parser.getParameter("select", "");
		
	    debug("responseKey = [" + responseKey + "]");
		debug("mailId + @ + mailDomain = [" + mailId + "@" + mailDomain + "]");
	    debug("parser.getParameter(EMAIL) = [" + parser.getParameter("EMAIL") + "]");

		boolean isMailChange = (mailId + "@" + mailDomain).equals(parser.getParameter("EMAIL")) ? false : true;
		int mailCnt = 0;
		
		if(isMailChange) {	// 메일이 수정되었을 경우 새로운 메일 중복 체크
			mailCnt = proc.getEmailCnt(context, parser);
		}
		//	메일주소 중복 체크 끝
		
		// 2008.10.29 이메일도 추가로 넘김
		request.setAttribute("email", mailId + "@" + mailDomain);
		request.setAttribute("mailCnt", "" + mailCnt);
		request.setAttribute("isMailChange", isMailChange==true?"true":"false");

		return getActionResponse(context, responseKey);	
	}
}
