/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRangeRsvtListActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : SKY72드림골프레인지 예약확인
*   적용범위  : Golf
*   작성일자  : 2009-06-16
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
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfBenefitInqDaoProc;
import com.bccard.golf.dbtao.proc.drivrange.GolfRangeRsvtListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfRangeRsvtListActn extends GolfActn{
	
	public static final String TITLE = " SKY72드림골프레인지 예약확인";

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
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		
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
			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("intCyberMoney", String.valueOf(intCyberMoney));
			
			
			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			
			//String gf_id = parser.getParameter("gf_id", "manse");	// 아이디
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			
			dataSet.setString("GF_ID", userId);
			
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
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N";
			}
			
			// 이용제한 체크
			//if (isLogin.equals("1") && (intMemGrade < 3 || intCyberMoney > 0)) { // 골드회원이상 및 사이버머니 1원 이상 접근
			if (isLogin.equals("1")) { // 전체회원 접근
				
				// 04.실제 테이블(Proc) 조회
				GolfRangeRsvtListDaoProc proc = (GolfRangeRsvtListDaoProc)context.getProc("GolfRangeRsvtListDaoProc");
				DbTaoResult rsvtListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				
				paramMap.put("resultSize", String.valueOf(rsvtListResult.size()));
				request.setAttribute("rsvtListResult", rsvtListResult);
				request.setAttribute("record_size", String.valueOf(record_size));
				request.setAttribute("paramMap", paramMap);
			
			} else{
				subpage_key = "limitReUrl";
			}
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
