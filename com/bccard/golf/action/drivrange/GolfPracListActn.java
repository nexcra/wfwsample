/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPracListActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 드라이빙레인지/스크린 할인쿠폰
*   적용범위  : Golf
*   작성일자  : 2009-06-13
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.drivrange;

import java.io.IOException;
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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.category.GolfCateSelInqDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfBenefitInqDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfPracPopListDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfCupnPrintSelDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfPracListActn extends GolfActn{
	
	public static final String TITLE = "드라이빙레인지/스크린 할인쿠폰";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		String permission = "";
		String cupn_prn_num = "0";
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		boolean flag = false;
		String rntB = "000";
		
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
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/drivrange");
			
			
			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			
			String sido	= parser.getParameter("s_sido", "");		// 지역
			String gugun	= parser.getParameter("s_gugun", "");		// 구군지역
			String dong	= parser.getParameter("s_dong", "");		// 상세지역
			//String exec_type_cd	= parser.getParameter("s_exec_type_cd", "");		// 구분
			
			//debug("exec_type_cd ====> "+ exec_type_cd);
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			dataSet.setString("SIDO", sido);
			dataSet.setString("GUGUN", gugun);
			dataSet.setString("DONG", dong);
			//dataSet.setString("EXEC_TYPE_CD", exec_type_cd);
			
			dataSet.setString("USERID", userId);
			
			// 01.접근권한 체크 : 2009.11.05 퍼미션 통합으로 수정
			String permissionColum = "DRVR_APO_YN";
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
				cupn_prn_num = (String)benefit.getString("CUPN_PRN_NUM"); // 쿠폰인쇄횟수
			}
			debug(">>>>>>>>>>>>>>>>  cupn_prn_num : "+cupn_prn_num); 
			
			
			debug(">>>>>>>>>>>>>>>  permission : "+permission);
			if(isLogin.equals("1") && permission.equals("Y") ){
				flag = true;
			} else {
				rntB = "002";
			}
			
			/*   //2009.11.05 퍼미션 통합으로 수정
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
					cupn_prn_num = (String)benefitInq.getString("CUPN_PRN_NUM"); // 쿠폰인쇄횟수
					drvr_apo_yn = (String)benefitInq.getString("DRVR_APO_YN"); // 기타드라이빙레인지접근여부(Y:접근가능 N:접근불가)
					drgf_yr_able_num = (String)benefitInq.getString("DRGF_YR_ABLE_NUM"); // 드림골프년가능횟수
					drgf_mo_able_num = (String)benefitInq.getString("DRGF_MO_ABLE_NUM"); // 드림골프월가능횟수
					eths_apo_yn = (String)benefitInq.getString("ETHS_APO_YN"); // 골프장주변맛집접근여부(Y:접근가능 N:접근불가)
				}
			}
			
			//if (exec_type_cd.equals("0001")){ //기타 드라이빙레인지
				//if (isLogin.equals("1") && intMemGrade < 4) { // 우량회원 이상접근
				if (isLogin.equals("1") && drvr_apo_yn.equals("Y")) { // 우량회원 이상접근
					flag = true;
				} else {
					rntB = "002";
				}
			/*
			} else if (exec_type_cd.equals("0002")){ //스크린 할인 
				if (isLogin.equals("1")) { // 전체회원 접근
					flag = true;
					
					if (!GolfUtil.isNull(cupn_prn_num)){ // 쿠폰 횟수 제한
						// 쿠폰 횟수 제한 가능여부
						GolfCupnPrintSelDaoProc proc3 = (GolfCupnPrintSelDaoProc)context.getProc("GolfCupnPrintSelDaoProc");
						DbTaoResult cupnprintSel = (DbTaoResult) proc3.execute(context, dataSet);
						
						String cupn_year_cnt = "";
						if (cupnprintSel != null && cupnprintSel.isNext()) {
							cupnprintSel.first();
							cupnprintSel.next();
							if (cupnprintSel.getObject("RESULT").equals("00")) {
								cupn_year_cnt = (String)cupnprintSel.getString("CUPN_YEAR_CNT");
							}
						}
						
						if (Integer.parseInt(cupn_prn_num) < Integer.parseInt(cupn_year_cnt)){
							rntB = "001";
						}
					}
				}
			}
			*/
			paramMap.put("rntB", rntB);
			paramMap.put("cupn_prn_num", cupn_prn_num);
			paramMap.put("isLogin", isLogin);
			
			
			// 이용제한 체크
			//if (flag) {
				
				// 04.할인쿠폰 리스트 가져오기
				GolfPracPopListDaoProc proc = (GolfPracPopListDaoProc)context.getProc("GolfPracPopListDaoProc");
				DbTaoResult pracListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				// 전체 0건  [ 0/0 page] 형식 가져오기
				long totalRecord = 0L;
				long currPage = 0L;
				long totalPage = 0L;
				
				if (pracListResult != null && pracListResult.isNext()) {
					pracListResult.first();
					pracListResult.next();
					if (pracListResult.getObject("RESULT").equals("00")) {
						totalRecord = Long.parseLong((String)pracListResult.getString("TOTAL_CNT"));
						currPage = Long.parseLong((String)pracListResult.getString("CURR_PAGE"));
						totalPage = (totalRecord % record_size == 0) ? (totalRecord / record_size) : (totalRecord / record_size)+1;
					}
				}
				
				
				paramMap.put("totalRecord", String.valueOf(totalRecord));
				paramMap.put("currPage", String.valueOf(currPage));
				paramMap.put("totalPage", String.valueOf(totalPage));
				paramMap.put("resultSize", String.valueOf(pracListResult.size()));
				
				request.setAttribute("pracListResult", pracListResult);
				request.setAttribute("record_size", String.valueOf(record_size));
				request.setAttribute("paramMap", paramMap);
			
			//} else {
			//	subpage_key = "limitReUrl";
			//}
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
