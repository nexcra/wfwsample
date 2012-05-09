/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRangeRsvtRegActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : SKY72드림골프레인지 예약신청 처리
*   적용범위  : golf
*   작성일자  : 2009-06-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.drivrange;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfRangeRsvtInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfRangeRsvtRegActn extends GolfActn{
	
	public static final String TITLE = "SKY72드림골프레인지 예약신청 처리";

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
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		String drgf_apo_yn = "";
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		String email1 = ""; 
		String permission = "";
		String drgf_limt_yn = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
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
				intCyberMoney	= (int)usrEntity.getCyberMoney(); //사이버머니
				email1 	= (String)usrEntity.getEmail1(); 
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
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);

			long rsvttime_sql_no = parser.getLongParameter("rsvt_time", 0L);  // 예약가능티타임일련번호
			String rsvt_clss = nowYear+"D"; // 예약구분자
			//String gf_id = parser.getParameter("gf_id", "manse");	// 아이디
			String hp_ddd_no = parser.getParameter("hp_ddd_no", "");	// 휴대전화DDD번호
			String hp_tel_hno = parser.getParameter("hp_tel_hno", "");	// 휴대전화국번호
			String hp_tel_sno = parser.getParameter("hp_tel_sno", "");	// 휴대전화일련번호
			
			String rntB = parser.getParameter("rntB", "");	// 처리사항 : 기본(001), 사이버머니차감(002), 접근제한(000)
			String drvr_amt = parser.getParameter("drvr_amt", ""); //사이버머니 결제금액
			String s_year = parser.getParameter("s_year", "");
			String s_month = parser.getParameter("s_month", "");
			String s_day = parser.getParameter("s_day", "");
			String regDate 	= parser.getParameter("regDate", "");
			String sch_gr 	= parser.getParameter("SCH_GR_SEQ_NO","");
			String intBkGrade 	= parser.getParameter("intBkGrade","");
						
			debug("## GolfRangeRsvtRegActn ID : "+userId+" | 예약 날짜 : "+regDate);
			
			s_month = "00" + s_month;
			s_month = s_month.substring(s_month.length()-2);
		
			debug("## GolfRangeRsvtRegActn 날짜 : "+s_year + s_month + s_day );
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("RSVTTIME_SQL_NO", rsvttime_sql_no);
			dataSet.setString("RSVT_CLSS", rsvt_clss);
			dataSet.setString("USERID", userId);
			dataSet.setString("HP_DDD_NO", hp_ddd_no);
			dataSet.setString("HP_TEL_HNO", hp_tel_hno);
			dataSet.setString("HP_TEL_SNO", hp_tel_sno);
			dataSet.setString("RNTB", rntB);
			dataSet.setString("DRVR_AMT", drvr_amt);
			dataSet.setString("BK_DATE", s_year + s_month + s_day);
			dataSet.setString("regDate", regDate);			
			dataSet.setString("SCH_GR_SEQ_NO", sch_gr);
			dataSet.setString("BKGRADE", intBkGrade);
			
			/*
			// 골프회원혜택 가져오기
			GolfBenefitInqDaoProc proc2 = (GolfBenefitInqDaoProc)context.getProc("GolfBenefitInqDaoProc");
			DbTaoResult benefitInq = (DbTaoResult) proc2.execute(context, dataSet);
			
			String drgf_limt_yn = "";
			String cupn_prn_num = "";
			String drvr_apo_yn = "";
			String drgf_yr_able_num = "";
			String drgf_mo_able_num = "";
			String eths_apo_yn = "";
			
			if (benefitInq != null && benefitInq.isNext()) {
				benefitInq.first();
				benefitInq.next();
				if (benefitInq.getObject("RESULT").equals("00")) {
					drgf_limt_yn = (String)benefitInq.getString("DRGF_LIMT_YN"); // 드림골프제한여부(Y:접근가능 N:접근불가)
					drgf_apo_yn = (String)benefitInq.getString("DRGF_APO_YN");  //드림골프 접근제한(Y:접근가능 N:접근불가)
					cupn_prn_num = (String)benefitInq.getString("CUPN_PRN_NUM"); // 쿠폰인쇄횟수
					drvr_apo_yn = (String)benefitInq.getString("DRVR_APO_YN"); // 기타드라이빙레인지접근여부(Y:접근가능 N:접근불가)
					drgf_yr_able_num = (String)benefitInq.getString("DRGF_YR_ABLE_NUM"); // 드림골프년가능횟수
					drgf_mo_able_num = (String)benefitInq.getString("DRGF_MO_ABLE_NUM"); // 드림골프월가능횟수
					eths_apo_yn = (String)benefitInq.getString("ETHS_APO_YN"); // 골프장주변맛집접근여부(Y:접근가능 N:접근불가)
				}
			}
			
			*/
			// 01.접근권한 체크
			String permissionColum = "DRGF_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
			}else{
				permission = "N";
			}
			// 02.골프혜택가져오기 
			GolfBkBenefitTimesDaoProc proc_benefit = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult benefit = proc_benefit.getDrivingSkyBenefit(context, dataSet, request);
			
			if(benefit.isNext()){
				benefit.next();
				drgf_limt_yn = (String)benefit.getString("DRGF_LMT_YN"); // 드림골프제한여부(Y:접근가능 N:접근불가)
				drgf_apo_yn = (String)benefit.getString("DRGF_APO_YN");  //드림골프 접근제한(Y:접근가능 N:접근불가)
				
			}
			
//			if("Y".equals(permission)){
			// 이용제한 체크
			// if (isLogin.equals("1") && (intMemGrade < 3 || intCyberMoney > 0)) { // 골드회원이상 및 사이버머니 1원 이상 접근
			//if (isLogin.equals("1") && permission.equals("Y") && (drgf_limt_yn.equals("Y") || intCyberMoney > 0)) { // 골드회원이상 및 사이버머니 1원 이상 접근
				
				// 04.실제 테이블(Proc) 조회
				GolfRangeRsvtInsDaoProc proc = (GolfRangeRsvtInsDaoProc)context.getProc("GolfRangeRsvtInsDaoProc");
				
				// 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				int addResult = proc.execute(context, request, dataSet);
				
				debug("GolfRangeRsvtRegActn :: addResult : " + addResult + " / permission : " + permission + " / drgf_limt_yn : " + drgf_limt_yn 
						+ " / drgf_apo_yn : " + drgf_apo_yn + " / addResult : " + addResult);
				
				if (addResult == 0) {
					request.setAttribute("returnUrl", "golfRangeRsvtRegForm.do");
					request.setAttribute("resultMsg", "이미 예약신청 되어있습니다.");      	
		        } else if (addResult == 1) {
		        	request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "예약이 완료되었습니다.");      	
		        } else if (addResult == 2) {
		        	request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "신청일자로 이미 예약을 하셨습니다. 1일 1회 예약 하실 수 있습니다.");  
		        } else if (addResult == 3) {
		        	request.setAttribute("returnUrl", "golfRangeRsvtList.do");
					request.setAttribute("resultMsg", "예약이 마감되었습니다.");  
				} else {
					request.setAttribute("returnUrl", "golfRangeRsvtRegForm.do");
					request.setAttribute("resultMsg", "예약 등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
		        }
				
				// 05. Return 값 세팅			
				paramMap.put("addResult", String.valueOf(addResult));			
		        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
		        
//			} else {
//				subpage_key = "limitReUrl";
//			}
			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
