/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBkMMDetActn
*   작성자    : 이포넷 은장선
*   내용      : 명문골프장 부킹 이벤트 신청 처리
*   적용범위  : golf
*   작성일자  : 2009-09-10
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkMMDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;

/******************************************************************************
* Golf
* @author	
* @version	1.0
******************************************************************************/
public class GolfEvntBkMMDetActn extends GolfActn{
	
	public static final String TITLE = "9월 VIP 부킹 이벤트 신청 처리";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userId = "";
		String isLogin = "";   	
		String intMemGrade = "";
		String permission = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		//String reUrl = super.getActionParam(context, "reUrl");
		//String errReUrl = super.getActionParam(context, "errReUrl");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {					
				userId		= (String)usrEntity.getAccount();  
				intMemGrade = String.valueOf((int)usrEntity.getIntMemGrade());
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";				
			}
			debug(">>>>>>>>>>>  intMemGrade : "+intMemGrade);
/*
			if(!(intMemGrade.equals("1") || intMemGrade.equals("2") || intMemGrade.equals("2"))){
				subpage_key = "deny";
				return super.getActionResponse(context, subpage_key);				
			}
	*/		
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);			
			
			paramMap.put("title", TITLE);						
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("cdhd_id",userId);           //사용자 ID셋팅
			dataSet.setString("userId",userId);            //사용자 ID셋팅 
			dataSet.setString("intMemGrade", intMemGrade); //사용자 등급

			// 04.실제 테이블(Proc) 조회
			
			GolfEvntBkMMDaoProc proc = (GolfEvntBkMMDaoProc)context.getProc("GolfEvntBkMMDaoProc");
			DbTaoResult evntResult = (DbTaoResult)proc.execute(context, dataSet);
			String str_memGradeTxt = proc.getAllGrade(context, dataSet, userId);
			String cnt = "0";
			String tot_cnt = "0";
			String can_cnt = "0";

			
			// 06.총실행카운트 구하기
			GolfBkBenefitTimesDaoProc proc_count = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult evntMMInq = proc_count.getPreBkEvntBenefit2(context, dataSet, request);
			if(evntMMInq.isNext()){
				evntMMInq.next();
				
				tot_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_PMI_NUM"));
				cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_DONE"));
				can_cnt = Integer.toString(evntMMInq.getInt("PRE_EVNT_BOKG_MO"));
				debug("Actn : 사용건수: cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + cnt);
				debug("Actn : 남은건수 : can_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + can_cnt);
				debug("Actn : 총사용할 수있는건수 : tot_cnt>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + tot_cnt);
				
			}

			paramMap.put("cnt",cnt);
			paramMap.put("tot_cnt",tot_cnt);
			paramMap.put("can_cnt",can_cnt);

			// 05. 접근권한 조회 : 수정 - 20091029 
			String permissionColum = "PMI_EVNT_APO_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N";
			}
			
			debug(">>>>>>>>>> permission : "+permission);
			paramMap.put("permission", permission); 
			
			paramMap.put("memGradeNM", str_memGradeTxt);
			request.setAttribute("evntResult", evntResult);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
