/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
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
import com.bccard.golf.dbtao.proc.mania.GolfManiaInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfManiaRegActn extends GolfActn{
	
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
		String lsn_nm ="";
		String admin_no = "";
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		String addrtype = "";
		int intMemGrade = 0; 
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
//			 01.세션정보체크
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

			String aplc_pe_clss = parser.getParameter("aplc_pe_clss", "");	// 리무진,잡지 등록구분타입
			String id = userId ;											// 신청인아이디
			String cp_nm = parser.getParameter("cp_nm", "");				// 신청이 이름(이름수정시 예비필드)
			String zip1 = parser.getParameter("zipcode1", "");				// 우편번호1
			String zip2 = parser.getParameter("zipcode2", "");				// 우편번호2
			String addr = parser.getParameter("zipaddr", "");				// 주소1
			String addr2 = parser.getParameter("detailaddr", "");			// 주소2
			String addr_clss = parser.getParameter("addr_clss"); 			//주소구분(구:1, 신:2)
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");		// 연락처 국번
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// 연락처 가운데
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// 연락처 끝
			String hp_ddd_no = parser.getParameter("hp_ddd_no", "");		// 휴대폰 국번
			String hp_tel_hno = parser.getParameter("hp_tel_hno", "");		// 휴대폰 가운데
			String hp_tel_sno = parser.getParameter("hp_tel_sno", "");		// 휴대폰 끝
			String email_1 = parser.getParameter("email_id", "");			// 이메일 앞자리
			String email_2 = parser.getParameter("email_id2", "");			// 이메일 뒷자리
			String email_id = email_1+"@"+email_2;							// 이메일 결합
			String str_plc = parser.getParameter("str_plc", "");			// 출발장소
			String price = "000,000원";			// 출발장소

			//	이메일에서 사용
			String adress = zip1+"-"+zip2+" "+addr+" "+addr2;
			String phone = chg_ddd_no + chg_tel_hno + chg_tel_sno;
			String tel_no = chg_ddd_no+"-"+chg_tel_hno+"-"+chg_tel_sno;
			String hp_no = hp_ddd_no+"-"+hp_tel_hno+"-"+hp_tel_sno;
			// 	이메일에서 사용
			
			String pic_date = parser.getParameter("pic_date", "");						// 픽업날짜
			String toff_date = parser.getParameter("toff_date", "");					// 티오프날짜
			String start_hh = parser.getParameter("start_hh", "");						// 픽업시간
			String start_mi = parser.getParameter("start_mi", "");						// 픽업분
			String end_hh = parser.getParameter("end_hh", "");							// 티오프시간
			String end_mi = parser.getParameter("end_mi", "");							// 티오프분
			String gcc_nm = parser.getParameter("gcc_nm", "");							// 골프장명
			String golf_mgz_dlv_clss = parser.getParameter("golf_mgz_dlv_clss", "");	// 배송지 구분
			if(golf_mgz_dlv_clss.equals("H")) { addrtype ="자택"; }
			if(golf_mgz_dlv_clss.equals("O")) { addrtype ="직장"; }
			String ckd_code = parser.getParameter("ckd_code", "");						// 차량
			int tk_prs = parser.getIntParameter("tk_prs", 0);							// 승차인원
			String memo = parser.getParameter("memo", "");								// 요청/특이사항
			String scoop_cp_cd		= parser.getParameter("scoop_cp_cd", ""); 			//0002:리무진할인 0003:골프잡지
			
			pic_date = pic_date.length() == 10 ? DateUtil.format(pic_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			toff_date = toff_date.length() == 10 ? DateUtil.format(toff_date, "yyyy-MM-dd", "yyyyMMdd"): "";			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("ADMIN_NO", admin_no);
			dataSet.setString("ID", id);
			
			dataSet.setString("CP_NM", cp_nm);
			dataSet.setString("APLC_PE_CLSS", aplc_pe_clss);
			dataSet.setString("ZIPCODE", zip1+""+zip2);
			
			dataSet.setString("ADDR", addr);
			dataSet.setString("ADDR2", addr2);
			dataSet.setString("ADDR_CLSS", addr_clss);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("HP_DDD_NO", hp_ddd_no);
			dataSet.setString("HP_TEL_HNO", hp_tel_hno);
			dataSet.setString("HP_TEL_SNO", hp_tel_sno);
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("STR_PLC", str_plc);
			dataSet.setString("PIC_DATE", pic_date);
			dataSet.setString("TOFF_DATE", toff_date);
			dataSet.setString("PIC_TIME", start_hh+start_mi);
			dataSet.setString("TOFF_TIME", end_hh+end_mi);
			dataSet.setString("GCC_NM", gcc_nm);
			dataSet.setString("GOLF_MGZ_DLV_PL_CLSS", golf_mgz_dlv_clss);
			dataSet.setString("CKD_CODE", ckd_code);			
			dataSet.setInt("TK_PRS", tk_prs);
			dataSet.setString("MEMO", memo);
			
			// 04.실제 테이블(Proc) 조회
			GolfManiaInsDaoProc proc = (GolfManiaInsDaoProc)context.getProc("GolfManiaInsDaoProc");
			
			// 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int addResult = proc.execute(context, dataSet);			

			
			HashMap smsMap = new HashMap();
			
			smsMap.put("ip", request.getRemoteAddr());
			smsMap.put("sName", userNm);
			smsMap.put("sPhone1", hp_ddd_no);
			smsMap.put("sPhone2", hp_tel_hno);
			smsMap.put("sPhone3", hp_tel_sno);
			
	        if (addResult == 1) {
	        	
	        	if (scoop_cp_cd.equals("0003")) {
	        		lsn_nm = "골프매거진 할인";
	        		
	        		//	메일발송 
					if (!email_id.equals("")) {
						String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String emailTitle = userNm +"님 골프매거진 할인 신청이 완료되었습니다.";
						String emailFileNm = "/email_tpl22.html";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
											
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+nowDay+"|"+userNm+"|"+tel_no+"|"+hp_no+"|"+email_id+"|"+addrtype+"|"+adress);
						emailEtt.setTo(email_id);
						//sender.send(emailEtt);
					}
					
					
//					sms발송
					if (!phone.equals("")) {
						
						debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "653";
						String message = "[골프잡지구독할인]"+userNm+"님 상담신청이 완료되었습니다.- Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = smsProc.send(smsClss, smsMap, message);
						debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
					}

	        	}else{
	        		lsn_nm = "리무진서비스";
	        		
//	        		메일발송
					if (!email_id.equals("")) {
						String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String emailTitle = userNm +"님 리무진서비스 신청이 완료되었습니다.";
						String emailFileNm = "/email_tpl23.html";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
											
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+nowDay+"|"+userNm+"|"+tel_no+"|"+hp_no+"|"+email_id+"|"+str_plc+"|"+pic_date+"|"+toff_date+"|"+gcc_nm+"|"+ckd_code+"|"+tk_prs+"|"+price+"|"+memo);
						//2이름,3주소,4연락처,휴대폰,E-mail,출발장소,픽업시간,티오프시간,골프장명,차종,승차인원,가격,요청/특이사항
						emailEtt.setTo(email_id);
						//sender.send(emailEtt);
					}
					
					
//					sms발송
					if (!phone.equals("")) {
						
						debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "649";
						String message = "[리무진할인]"+userNm+"님 상담신청이 완료되었습니다.- Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = smsProc.send(smsClss, smsMap, message);
						debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
					}
	        	}
	        	

	        	
	        	
	        	
	        	if (scoop_cp_cd.equals("0003")) {
	        		request.setAttribute("returnUrl", "golfMagazineRegEnd.do");
	        	}else{
	        		request.setAttribute("returnUrl", "golfManiaRegEnd.do");
	        	}

				//request.setAttribute("resultMsg", "할인 신청 프로그램 등록이 정상적으로 처리 되었습니다.");   
				
	        } else {
				request.setAttribute("returnUrl", "ManiaRegForm.do");
				request.setAttribute("resultMsg", "골프장리무진할인 신청 프로그램 등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
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
