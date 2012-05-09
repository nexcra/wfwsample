/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEventCpnReqActn
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
public class GolfAdmEventCpnReqActn extends GolfActn{
	
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
			paramMap.put("title", TITLE);

			// Request 값 저장			
			String evnt_no      = parser.getParameter("evnt_no" ,"");               // 이벤트 번호
			String cupn         = parser.getParameter("cupn" ,"");                // 쿠폰 번호		
			String userNm       = parser.getParameter("userNm" ,"");                 // 이름	
			String socid        = parser.getParameter("socid" ,"");              // 주민등록번호
			String email        = parser.getParameter("email" ,"");                 // 이름	
			String useYN        = "N";
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			dataSet.setString("evnt_no"  , evnt_no);
			dataSet.setString("cupn"     , cupn);		
			dataSet.setString("userNm"   , userNm);	
			dataSet.setString("socid"    , socid);	
			dataSet.setString("email"    , email);	
			dataSet.setString("evnt_no"  , "109");

			GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
			
			if(evnt_no.equals("109")){	// 인터파크 이벤트

				boolean doUpdate = false;
				int cnt = 1;			
	
				cnt = (int)inter.getDplCheck(context, request, dataSet);
	
				if(cnt == 0){
					debug("인터파크 이벤트정보 인서트 or 업데이트");
					doUpdate = (boolean) inter.insertCupnNumber(context, request, dataSet);
				}else{
					request.getSession().removeAttribute("isInterpark");
					request.setAttribute("msg","이미 쿠폰을 발급받으셨습니다."); 
				}					
	
				if(cnt == 0){
	
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String emailTitle = "";
					String emailFileNm = "";
	
					if(doUpdate == true){
						request.setAttribute("msg","쿠폰이 정상적으로 발급처리 되었습니다."); 
						
						useYN = (String) inter.getUseYN(context, request, dataSet);	
	
						if(useYN.equals("Y")){
							emailFileNm = "/email_interpark1.html";
						}else{
							emailFileNm = "/email_interpark.html";
						}
	
						emailTitle = "골프라운지 회원가입 인터파크 할인쿠폰";
						emailFileNm = "/email_interpark.html";
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setTo(email);
						sender.send(emailEtt);
					}else{
						request.setAttribute("msg","쿠폰이 발급처리에 실패하였습니다."); 
					}	
				}			
				
			}else if(evnt_no.equals("119")){		// TM 회원 영화예매권 이벤트

				EmailSend sender = new EmailSend();
				EmailEntity emailEtt = new EmailEntity("EUC_KR");
				String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
				String imgPath = "<img src=\"";
				String hrefPath = "<a href=\"";
				String emailTitle = "";
				String emailFileNm = "";

				request.setAttribute("msg","쿠폰이 정상적으로 발급처리 되었습니다."); 

				emailTitle = "[Golf Loun.G] 골프라운지 TM 영화예매권";
				emailFileNm = "/eamil_tm_movie.html";
				emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, "");
				
				emailEtt.setFrom(emailAdmin);
				emailEtt.setSubject(emailTitle);
				emailEtt.setTo(email);
				sender.send(emailEtt);
			}

			paramMap.put("evnt_no",evnt_no);
			paramMap.put("cpn_no", cupn);						
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}