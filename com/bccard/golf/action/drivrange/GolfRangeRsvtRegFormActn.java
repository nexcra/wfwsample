/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRangeRsvtRegFormActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      :  SKY72드림골프레인지 예약신청
*   적용범위  : golf
*   작성일자  : 2009-06-11
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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmCyberBenefitDetailInqDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfRangeRsvtInqDaoProc;
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
public class GolfRangeRsvtRegFormActn extends GolfActn{
	
	public static final String TITLE = "SKY72드림골프레인지 예약신청";

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
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		String email1 = ""; 
		String permission = "";
		String rntB = "000";

		String mobile = "";
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); //이름
				memClss		= (String)usrEntity.getMemberClss(); //등급번호
				userId		= (String)usrEntity.getAccount(); //아이디
				juminno 	= (String)usrEntity.getSocid(); //주민번호
				memGrade 	= (String)usrEntity.getMemGrade(); //등급
				intMemGrade	= (int)usrEntity.getIntMemGrade(); //등급번호
				intCyberMoney	= (int)usrEntity.getCyberMoney(); //사이버머니
				email1 	= (String)usrEntity.getEmail1(); 
				
				mobile 	= (String)usrEntity.getMobile(); 
				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3(); 
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
			String nowDay = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			
			// Request 값 저장
			String search_yn	= parser.getParameter("search_yn", "N");
			String year			= parser.getParameter("s_year", nowYear);
			String month		= parser.getParameter("s_month", nowMonth);
			String day			= parser.getParameter("s_day", nowDay);
			String sch_gr 		= parser.getParameter("SCH_GR_SEQ_NO","");
			
			String date = year +"년 "+ month +"월 "+ day +"일";
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("USERID", userId);
			
			// 사이버머니 정책 가져오기
			GolfAdmCyberBenefitDetailInqDaoProc proc1 = (GolfAdmCyberBenefitDetailInqDaoProc)context.getProc("GolfAdmCyberBenefitDetailInqDaoProc");			
			DbTaoResult detailInq1 = (DbTaoResult) proc1.execute(context, request, dataSet);
			
			String drvr_amt = "";
			if (detailInq1 != null && detailInq1.isNext()) {
				detailInq1.first();
				detailInq1.next();
				if (detailInq1.getObject("RESULT").equals("00")) {
					drvr_amt = (String)detailInq1.getString("DV_RG");
				}
			}
			paramMap.put("intMemGrade", String.valueOf(intMemGrade));
			paramMap.put("drvr_amt", drvr_amt);
			//paramMap.put("intCyberMoney", String.valueOf(intCyberMoney));
			
			
			//--------------- 여기서부터 2009.11.05 새로 적용 Start -----------------------//
			int drgf_yr_done = 0;
			int drgf_mo_done = 0;
			int intBkGrade = 0;
			
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
			
			//접근권한이 있으면 해당 모든 데이터 GET 
			 if("Y".equals(permission)){
			 
				// 02.골프혜택가져오기
				GolfBkBenefitTimesDaoProc proc_benefit = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
				DbTaoResult benefit = proc_benefit.getDrivingSkyBenefit(context, dataSet, request);
				
				if(benefit.isNext()){
					benefit.next();
					drgf_yr_done = benefit.getInt("DRGF_BOKG_YR"); // 드림골프 남은 연횟수 
					drgf_mo_done = benefit.getInt("DRGF_BOKG_MO"); // 드림골프 남은 월횟수
					intBkGrade = benefit.getInt("intBkGrade");  //등록할 등급
					intCyberMoney = benefit.getInt("CY_MONEY");  //사이버머니					
				}
				debug("드림골프레인지 :: drgf_yr_done : " + drgf_yr_done + " / drgf_mo_done : " + drgf_mo_done + " / intCyberMoney : " + intCyberMoney 
						 + " / drvr_amt : " + drvr_amt + " / permission : " + permission + " / intBkGrade : " + intBkGrade);
				
				//- 드림골프레인지 :: drgf_yr_done : 0 / drgf_mo_done : 0 / intCyberMoney : 0 / drvr_amt : 5000 / permission : Y / intBkGrade : 0
			
				//사용횟수가 남아있으면
				if(drgf_yr_done > 0 && drgf_mo_done > 0 ){ debug ("##-------------1");
					rntB = "001";
				}else{
					//사용횟수는 없고, 사이버 머니가있을때
					if (intCyberMoney < Integer.parseInt(drvr_amt)){debug ("##-------------2");
						rntB = "000";	
					}else{debug ("##-------------3");
						rntB = "002";
					}
				}
				
				paramMap.put("rntB", rntB);
				
				
				dataSet.setString("SORT", AppConfig.getDataCodeProp("DrivingRange"));
				dataSet.setString("DrivR", AppConfig.getDataCodeProp("DrivingRangeClss"));
				
				// 04.실제 테이블(Proc) 조회 - 골프장
				GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
				DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
				request.setAttribute("TitimeGreenList", titimeGreenList);	
				
				if(sch_gr.equals("")){
					if (titimeGreenList != null && titimeGreenList.isNext()) {
						titimeGreenList.first();
						titimeGreenList.next();
						sch_gr =  Integer.toString(titimeGreenList.getInt("SEQ_NO"));
					}
				}			
			
				paramMap.put("SCH_GR_SEQ_NO", sch_gr);
				
				dataSet.setString("SCH_GR_SEQ_NO", sch_gr);
					
				
				//테이블조회 : 스카이27 드림골프 골프장 예약가능 목록 
				GolfRangeRsvtInqDaoProc proc = (GolfRangeRsvtInqDaoProc)context.getProc("GolfRangeRsvtInqDaoProc");
				DbTaoResult rangersvtInq = proc.execute(context, dataSet);
				
				// 05. Return 값 세팅			

				paramMap.put("mobile1", mobile1);
				paramMap.put("mobile2", mobile2);
				paramMap.put("mobile3", mobile3);
				paramMap.put("search_yn", search_yn);
				paramMap.put("s_year", year);
				paramMap.put("s_month", month);
				paramMap.put("s_day", day);
				paramMap.put("s_date", date);				
				paramMap.put("permission", permission);
				paramMap.put("intCyberMoney",Integer.toString(intCyberMoney));
				paramMap.put("drgf_yr_done", Integer.toString(drgf_yr_done));
				paramMap.put("drgf_mo_done", Integer.toString(drgf_mo_done));
				paramMap.put("intBkGrade", Integer.toString(intBkGrade));
				paramMap.put("regDate", year+month+day);
				
				request.setAttribute("rangersvtInqResult",rangersvtInq);	
			
				
			//접근권한이 없으면 별도 페이지 이동
			}else{
				subpage_key = "limitReUrl";
			}
			
			 request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			 
			 
			 
			//--------------- 여기서부터 2009.11.05 새로 적용  End -----------------------//
			
			/*   //2009.11.05 통합관리쪽으로 수정
			// 골프회원혜택 가져오기
			GolfBenefitInqDaoProc proc2 = (GolfBenefitInqDaoProc)context.getProc("GolfBenefitInqDaoProc");
			
			DbTaoResult benefitInq = (DbTaoResult) proc2.execute(context, dataSet);
			
			
			
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

//			if (intMemGrade == 1) { // Champion 등급 무제한 접근가능
//				rntB = "001";
				
//			} else {	
				if (intCyberMoney < Integer.parseInt(drvr_amt)){ //사이버머니로 불가능시
					if (drgf_limt_yn.equals("Y") && GolfUtil.isNull(drgf_yr_able_num) && GolfUtil.isNull(drgf_mo_able_num)){ //무제한 접근가능
						rntB = "001";
						
					} else if (drgf_limt_yn.equals("Y") && (!GolfUtil.isNull(drgf_yr_able_num) || !GolfUtil.isNull(drgf_mo_able_num))){ //접근가능(연월제한)
						// 연월 제한 가능여부
						GolfRangeRsvtSelDaoProc proc3 = (GolfRangeRsvtSelDaoProc)context.getProc("GolfRangeRsvtSelDaoProc");
						DbTaoResult rangersvtSel = (DbTaoResult) proc3.execute(context, dataSet);
						
						String rsvt_year_cnt = "";
						String rsvt_month_cnt = "";
						if (rangersvtSel != null && rangersvtSel.isNext()) {
							rangersvtSel.first();
							rangersvtSel.next();
							if (rangersvtSel.getObject("RESULT").equals("00")) {
								rsvt_year_cnt = (String)rangersvtSel.getString("RSVT_YEAR_CNT");
								rsvt_month_cnt = (String)rangersvtSel.getString("RSVT_MONTH_CNT");
							}
						}
						debug("---------------  rsvt_year_cnt : "+rsvt_year_cnt +" / rsvt_month_cnt : "+rsvt_month_cnt);
						
						if (Integer.parseInt(rsvt_year_cnt) < Integer.parseInt(drgf_yr_able_num)){
							if (Integer.parseInt(rsvt_month_cnt) < Integer.parseInt(drgf_mo_able_num)){
								rntB = "001";
							}
						}
					} else {
						rntB = "000";
					}
				} else { //사이버머니로 가능시
					rntB = "002";
				}
//			}
			*/
			
			
			
			//paramMap.put("userId", userId);
			//paramMap.put("intCyberMoney", String.valueOf(intCyberMoney));
			//paramMap.put("email1", email1);
			
			
			
			// 이용제한 체크
			//if (isLogin.equals("1") && (intMemGrade < 3 || intCyberMoney > 0)) { // 골드회원이상 및 사이버머니 1원 이상 접근
			//if (isLogin.equals("1") && permission.equals("Y") &&  (drgf_limt_yn.equals("Y") || intCyberMoney > 0)) { // 골드회원이상 및 사이버머니 1원 이상 접근
				//로그인체크 // 드림골프 접근제한 //드림골프제한여부//사이버머니금액
				
			   
			
			//} else {
				//subpage_key = "limitReUrl";
			//}
			    
			   
			    
			    
			    
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
