/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeRsViewActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 신청 결과
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.sky;

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
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.booking.sky.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfBkSkyTimeRegFormActn extends GolfActn{
	
	public static final String TITLE = "부킹 신청 내용 확인";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String permission = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			int cyberMoney = 0;
			String userNm = "";
			String userId = "";
			int intMemGrade = 0;
			
			
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				cyberMoney = userEtt.getCyberMoney();
				userNm = userEtt.getName();
				userId = (String)userEtt.getAccount();
				intMemGrade = userEtt.getIntMemGrade();
			}

			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String time_SEQ_NO			= parser.getParameter("TIME_SEQ_NO", "");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("TIME_SEQ_NO", time_SEQ_NO);					

			// 04.실제 테이블(Proc)
			GolfBkSkyTimeRegFormDaoProc proc = (GolfBkSkyTimeRegFormDaoProc)context.getProc("GolfBkSkyTimeRegFormDaoProc");
			DbTaoResult rsView = proc.execute(context, dataSet);		

			// 05. 부킹 횟수 조회 - 사이버 머니 사용내역은 회수에서 차감된다.
			int drds_BOKG_YR = 0;	// 일반주중부킹횟수
			int drds_BOKG_MO = 0;	// 일반주말부킹횟수
			
//			GolfBkSkyTimesDaoProc proc_times = (GolfBkSkyTimesDaoProc)context.getProc("GolfBkSkyTimesDaoProc");
//			DbTaoResult timesView = proc_times.execute(context, dataSet, request);
//			timesView.next();
//			if(timesView.getString("RESULT").equals("00")){
//				drds_BOKG_YR = timesView.getString("DRDS_BOKG_YR");
//				drds_BOKG_MO = timesView.getString("DRDS_BOKG_MO");
//			}

			GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult skyView = proc_times.getSkyBenefit(context, dataSet, request);
			skyView.next();
			drds_BOKG_YR = skyView.getInt("DRDS_BOKG_YR");
			drds_BOKG_MO = skyView.getInt("DRDS_BOKG_MO");
			cyberMoney = skyView.getInt("CY_MONEY");

			//접근권한 체크	
			String permissionColum = "DRDS_BOKG_LIMT_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next();
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				
			}else{
				permission = "N";
			}	
			debug(" >>>>>>> permission : "+permission);
			paramMap.put("permission", permission);	
			paramMap.put("cyberMoney", cyberMoney+"");	
			paramMap.put("userNm", userNm);	
			paramMap.put("intMemGrade", intMemGrade+"");	
			paramMap.put("DRDS_BOKG_YR", drds_BOKG_YR+"");	
			paramMap.put("DRDS_BOKG_MO", drds_BOKG_MO+"");
	        
			
			// 05. Return 값 세팅
	        request.setAttribute("TIME_SEQ_NO", time_SEQ_NO);
	        request.setAttribute("RsView", rsView); //모든 파라미터값을 맵에 담아 반환한다.	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
