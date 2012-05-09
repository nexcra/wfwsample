/***************************************************************************************************
*   이 소스는 ㈜골프라운지 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaRegActn
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 등록처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.mania;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.mania.GolfFittingInsDaoProc;
import com.bccard.golf.common.GolfUtil;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfFittingRegActn extends GolfActn{
	
	public static final String TITLE = "골프장리무진할인신청관리 등록처리";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no	= (String)userEtt.getMemNo(); 							
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
			String nowDate = String.valueOf(cal.get(Calendar.DATE));
			String nowDay = nowYear +"년 "+ nowMonth +"월 "+ nowDate +"일";
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String aplc_pe_clss = parser.getParameter("aplc_pe_clss", "");	// 리무진,잡지 등록구분타입
			String id = parser.getParameter("id", "");						// 신청인아이디
			String note = parser.getParameter("note", "");						// 신청자 이름 수정시 NOTE필드로
			
			String hp_ddd_no = parser.getParameter("hp_ddd_no", "");		// 휴대폰
			String hp_tel_hno = parser.getParameter("hp_tel_hno", "");		// 
			String hp_tel_sno = parser.getParameter("hp_tel_sno", "");		// 
			
			String phone = hp_ddd_no +"-"+ hp_tel_hno +"-"+ hp_tel_sno;
			
			String email1 = parser.getParameter("email_id", "");			// 이메일1
			String email2 = parser.getParameter("email_id2", "");			// 이메일2
			String email_id = email1+"@"+email2;
			
			String pic_date = parser.getParameter("pic_date", "");			// 테스트 희망일자
			String start_hh = parser.getParameter("start_hh", "");			// 테스트 희망시간
						
			String ckd_code = parser.getParameter("ckd_code", "");			// 피팅희망클럽종류
			String wclub ="";
			if(ckd_code.equals("0001"))wclub = "드라이버";
			if(ckd_code.equals("0002"))wclub = "페어웨이 우드";
			if(ckd_code.equals("0003"))wclub = "아이언";
			if(ckd_code.equals("0004"))wclub = "퍼터";
			
			String gcc_nm = parser.getParameter("gcc_nm", "");				// 제목 			
			String memo = parser.getParameter("memo", "");					// 내용
			
			//pic_date = pic_date.length() == 10 ? DateUtil.format(pic_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			pic_date = GolfUtil.toDateFormat(pic_date);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			//SEQ_NO, APLC_PE_CLSS, 
			dataSet.setString("ADMIN_NO", admin_no);
			dataSet.setString("ID", id);
			dataSet.setString("NOTE", note);
			dataSet.setString("APLC_PE_CLSS", aplc_pe_clss);

			dataSet.setString("HP_DDD_NO", hp_ddd_no);
			dataSet.setString("HP_TEL_HNO", hp_tel_hno);
			dataSet.setString("HP_TEL_SNO", hp_tel_sno);
			
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("PIC_DATE", pic_date);
			dataSet.setString("PIC_TIME", start_hh);
			dataSet.setString("GCC_NM", gcc_nm);
			dataSet.setString("CKD_CODE", ckd_code);			
			dataSet.setString("MEMO", memo);
			
			//ZP, REG_DATE
			
			// 04.실제 테이블(Proc) 조회
			GolfFittingInsDaoProc proc = (GolfFittingInsDaoProc)context.getProc("GolfFittingInsDaoProc");
			
			// 레슨 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int addResult = proc.execute(context, dataSet);		
			
			HashMap smsMap = new HashMap();
			
			smsMap.put("ip", request.getRemoteAddr());
			smsMap.put("sName", note);
			smsMap.put("sPhone1", hp_ddd_no);
			smsMap.put("sPhone2", hp_tel_hno);
			smsMap.put("sPhone3", hp_tel_sno);
			
	        if (addResult == 1) {

	        	request.setAttribute("returnUrl", "FittingRegEnd.do");

				//request.setAttribute("resultMsg", "클럽피팅 온라인예약이  정상적으로 처리 되었습니다."); 
	        	
	        	//	메일발송 
				if (!email_id.equals("")) {
					String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String emailTitle = note +"님 온라인 피팅 신청이 완료되었습니다.";
					String emailFileNm = "/email_tpl25.html";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
										
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, note+"|"+nowDay+"|"+note+"|"+phone+"|"+email_id+"|"+pic_date+"|"+wclub+"|"+gcc_nm+"|"+memo);
					//0이름1날짜02이름,3휴대폰4이메일5테스트희망일6희망클럽7제목8내용
					emailEtt.setTo(email_id);
					//sender.send(emailEtt);
				}
				
				
				//	sms발송
				if (!phone.equals("")) {
					
					debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "654";
					String message = "[온라인피팅]"+note+"님 상담신청이 완료되었습니다.- Golf Loun.G";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}
	        	
	        	
	        	
				
	        } else {
				request.setAttribute("returnUrl", "golfFittingRegFormPag.do");
				request.setAttribute("resultMsg", "클럽피팅 온라인예약이 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}