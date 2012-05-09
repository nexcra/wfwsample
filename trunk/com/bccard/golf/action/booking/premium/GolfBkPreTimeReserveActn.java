/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeReserveActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 신청 처리
*   적용범위  : golf
*   작성일자  : 2009-05-29
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmGrRegDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmGrUpdDaoProc;
import com.bccard.golf.dbtao.proc.booking.premium.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfBkPreTimeReserveActn extends GolfActn{
	
	public static final String TITLE = "부킹 신청 처리";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String userId = "";
		String memGrade = "White"; 
		int intMemGrade = 4; 
		String email_id = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
			}
			//debug("==========email_id========> " + email_id);
			//email_id = "simijoa@hanmail.net";
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String time_SEQ_NO			= parser.getParameter("TIME_SEQ_NO", "");
			String gr_SEQ_NO			= parser.getParameter("GR_SEQ_NO", "");
			String hp_DDD_NO			= parser.getParameter("HP_DDD_NO", "");
			String hp_TEL_HNO			= parser.getParameter("HP_TEL_HNO", "");
			String hp_TEL_SNO			= parser.getParameter("HP_TEL_SNO", "");
			// 이메일 변수
			String gr_NM				= parser.getParameter("GR_NM", "");
			String gl_green_nm			= parser.getParameter("RL_GREEN_NM", "");
			String bk_DATE				= parser.getParameter("BK_DATE", "");
			String bkps_TIME			= parser.getParameter("BKPS_TIME", "");
			String bkps_MINUTE			= parser.getParameter("BKPS_MINUTE", "");
			String course				= parser.getParameter("COURSE", "");
			String cancle_DATE			= parser.getParameter("CANCLE_DATE", "");
			
			// SMS 변수 
			String hp_TEL				= hp_DDD_NO+hp_TEL_HNO+hp_TEL_SNO;
			//debug("==========userMobile1========> " + hp_DDD_NO);
			//debug("==========userMobile2========> " + hp_TEL_HNO);
			//debug("==========userMobile3========> " + hp_TEL_SNO);
			
			if(gl_green_nm.equals("")) gl_green_nm = gr_NM;

	        // 이메일 변수
			GregorianCalendar today = new GregorianCalendar ( );
	        int nYear = today.get ( today.YEAR );
	        int nMonth = today.get ( today.MONTH ) + 1;
	        int nDay = today.get ( today.DAY_OF_MONTH ); 
	        String strToday = nYear+"년 "+nMonth+"월 "+nDay+"일";

			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("TIME_SEQ_NO", time_SEQ_NO);
			dataSet.setString("GR_SEQ_NO", gr_SEQ_NO);
			dataSet.setString("HP_DDD_NO", hp_DDD_NO);
			dataSet.setString("HP_TEL_HNO", hp_TEL_HNO);
			dataSet.setString("HP_TEL_SNO", hp_TEL_SNO);
			dataSet.setString("BK_DATE", bk_DATE);
					
			// 04.실제 테이블(Proc) 조회 1. 등록여부 확인
			GolfBkPreTimeReserveDaoProc proc = (GolfBkPreTimeReserveDaoProc)context.getProc("GolfBkPreTimeReserveDaoProc");
			int fineResult = proc.execute(context, dataSet);
			
			
			if (fineResult==0){
				// 이미등록
				request.setAttribute("returnUrl", "GolfBkPreTimeList.do");
				request.setAttribute("resultMsg", "해당 티타임이 이미 등록되었습니다.\\n티타임을 다시 선택해주세요.");      
			}else{
				// 예약처리
				GolfBkPreTimeRsInsDaoProc proc2 = (GolfBkPreTimeRsInsDaoProc)context.getProc("GolfBkPreTimeRsInsDaoProc");
				int addResult = proc2.execute(context, dataSet, request);
		        if (addResult == 1) {
		        	

					//debug("====================GolfMemInsActn === 메일발송 ===");
					if (!email_id.equals("")) {

						String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String emailTitle = "";
						String emailFileNm = "";
						
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						
						emailTitle = "[Golf Loun.G] VIP 부킹이 완료되었습니다.";
						emailFileNm = "/email_tpl01.html";						
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+strToday+"|"+gr_NM+"|"+bk_DATE+"|"+bkps_TIME+"|"+bkps_MINUTE+"|"+course+"|"+cancle_DATE);
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setTo(email_id);
						//sender.send(emailEtt);
					}
					
					//sms발송
					if (!hp_TEL.equals("")) {

						// SMS 관련 셋팅
						HashMap smsMap = new HashMap();
						
						smsMap.put("ip", request.getRemoteAddr());
						smsMap.put("sName", userNm);
						smsMap.put("sPhone1", hp_DDD_NO);
						smsMap.put("sPhone2", hp_TEL_HNO);
						smsMap.put("sPhone3", hp_TEL_SNO);
						
						//debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "634";
						String message = "[VIP부킹] "+userNm+"님 "+gl_green_nm+" "+course+" "+bk_DATE+" "+bkps_TIME+":"+bkps_MINUTE+" 예약완료- Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = smsProc.send(smsClss, smsMap, message);
						//debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
					}			

					request.setAttribute("returnUrl", "GolfBkPreTimeRsView.do");
					request.setAttribute("resultMsg", "예약이 완료되었습니다.");
		        } else if (addResult==2) {
					request.setAttribute("returnUrl", "GolfBkPreTimeList.do");
					request.setAttribute("resultMsg", "신청일자로 이미 예약을 하셨습니다. 1일 1회 예약 하실 수 있습니다.");
		        } else {
					request.setAttribute("returnUrl", "GolfBkPreTimeList.do");
					request.setAttribute("resultMsg", "예약이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
		        }
			}
			
			// 05. Return 값 세팅
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
