/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmUNEventWinReSendActn
*   작성자    : E4NET 은장선
*   내용      : 관리자 > 어드민관리 > 회원관리 > 회원리스트
*   적용범위  : Golf
*   작성일자  : 2009-08-05
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.*;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;

/******************************************************************************
* Topn
* @author	E4NET
* @version	1.0
******************************************************************************/
public class GolfAdmUNEventWinReSendActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 이벤트관리 > 통합이벤트 > 쿠폰";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체.  
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보.  
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);

			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			

			paramMap.put("title", TITLE);			

			// Request 값 저장				
			String cupn         = parser.getParameter("cupn" ,"");                // 쿠폰 번호				
			String email        = parser.getParameter("email" ,"");               // email	
			String userNm       = parser.getParameter("userNm", "");			  // 이름	
			String socid        = parser.getParameter("socid", "");				  // 주민등록번호	
			String evnt_no      = parser.getParameter("evnt_no", "");			  // 이벤트 번호	

			dataSet.setString("socid",socid);
			dataSet.setString("evnt_no", evnt_no);

			GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
			String useYN = (String) inter.getUseYN(context, request, dataSet);

			EmailSend sender = new EmailSend();
			EmailEntity emailEtt = new EmailEntity("EUC_KR");
			String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
			String imgPath = "<img src=\"";
			String hrefPath = "<a href=\"";
			String emailTitle = "";
			String emailFileNm = "";


			if(evnt_no.equals("109")){	// 인터파크
				emailTitle = "골프라운지 회원가입 인터파크 할인쿠폰";
				if(useYN.equals("Y")){
					emailFileNm = "/email_interpark1.html";
				}else{
					emailFileNm = "/email_interpark.html";
				}
				emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);
				
			}else if(evnt_no.equals("119")){		// TM 회원 영화예매권 이벤트
				emailTitle = "[Golf Loun.G] 골프라운지 TM 영화예매권";
				emailFileNm = "/eamil_tm_movie.html";
				emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, "");
			}
			
			
			
			emailEtt.setFrom(emailAdmin);
			emailEtt.setSubject(emailTitle);
			emailEtt.setTo(email);
			sender.send(emailEtt);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}