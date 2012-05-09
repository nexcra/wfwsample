/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLessonRegActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨신청 처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.lesson;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import java.util.Calendar;

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
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.lesson.GolfLessonInsDaoProc;

import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.SmsSendProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfLessonRegActn extends GolfActn{
	
	public static final String TITLE = "레슨신청 처리";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
		String errReUrl = super.getActionParam(context, "errReUrl"); 
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
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

			long recv_no	= parser.getLongParameter("p_idx", 0L);// 레슨일렬번호
			//debug("recv_no>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+recv_no);
			
			String lsn_type_cd = parser.getParameter("slsn_type_cd", "");
			String sex = parser.getParameter("sex", "");	// 성별
			String email_id = parser.getParameter("email_id", "");	// E-mail
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");	// 전화ddd번호
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// 전화국번호
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// 전화일련번호
			String tel_no = chg_ddd_no+"-"+chg_tel_hno+"-"+chg_tel_sno;
			String lsn_expc_clss = parser.getParameter("lsn_expc_clss", "");	// 골프경험코드
			String mttr = parser.getParameter("mttr", "");	// 특이사항
			String lsn_nm = parser.getParameter("lsn_nm", "");	// 레슨명
			// 이메일에서 사용
			String phone = chg_ddd_no + chg_tel_hno + chg_tel_sno;
			String lsn_expc_clss_nm = "";
			if (lsn_expc_clss.equals("0001")) lsn_expc_clss_nm="하 (처음 골프를 접함)";
			if (lsn_expc_clss.equals("0002")) lsn_expc_clss_nm="중 (레슨 경험 있음)";
			if (lsn_expc_clss.equals("0003")) lsn_expc_clss_nm="상 (골프 경험 다수)";
			String sex_nm = "";
			if (sex.equals("F")) sex_nm="여";
			if (sex.equals("M")) sex_nm="남";
			// 이메일에서 사용
			
			String lsn_seq_type = nowYear+"G";
			if (lsn_type_cd.equals("0002")) lsn_seq_type =  nowYear+"S";
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CSTMR_ID", userId);
			dataSet.setString("LSN_SEQ_TYPE", lsn_seq_type);			
			dataSet.setLong("RECV_NO", recv_no);
			dataSet.setString("LSN_TYPE_CD", lsn_type_cd);
			dataSet.setString("SEX", sex);
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("LSN_EXPC_CLSS", lsn_expc_clss);
			dataSet.setString("MTTR", mttr);
			
			// 04.실제 테이블(Proc) 조회
			GolfLessonInsDaoProc proc = (GolfLessonInsDaoProc)context.getProc("GolfLessonInsDaoProc");
			
			// 레슨 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int addResult = proc.execute(context, dataSet);
			String aplc_seq_no = proc.getMaxSeqNo(context, dataSet);

			HashMap smsMap = new HashMap();
			
			smsMap.put("ip", request.getRemoteAddr());
			smsMap.put("sName", userNm);
			smsMap.put("sPhone1", chg_ddd_no);
			smsMap.put("sPhone2", chg_tel_hno);
			smsMap.put("sPhone3", chg_tel_sno);
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "");

				// 메일발송
//				if (!email_id.equals("")) {
//					String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
//					String emailTitle = userNm +"님 즐거운레슨 신청이 완료되었습니다.";
//					String emailFileNm = "/email_tpl10.html";
//					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
//					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
//										
//					EmailSend sender = new EmailSend();
//					EmailEntity emailEtt = new EmailEntity("EUC_KR");
//					
//					emailEtt.setFrom(emailAdmin);
//					emailEtt.setSubject(emailTitle);
//					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+nowDay+"|"+lsn_nm+"|"+userNm+"|"+sex_nm+"|"+tel_no+"|"+email_id+"|"+lsn_expc_clss_nm+"|"+mttr);
//					emailEtt.setTo(email_id);
//					sender.send(emailEtt);
//				}
				
				//sms발송
				if (!phone.equals("")) {
					
					debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "645";
					String message = "[즐거운레슨]"+userNm+"님 "+lsn_nm+" 신청완료 - Golf Loun.G";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}				
				
	        } else if (addResult == 9) {
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "이미 신청하셨습니다.");      		        	
	        } else {
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "레슨신청이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("aplc_seq_no", aplc_seq_no);		
			paramMap.put("editResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
}
