/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkParTimeViewActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 티타임 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-26
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.par;

import java.io.IOException;
import java.util.*;
import java.text.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPermissionDaoProc;
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.dbtao.proc.booking.premium.GolfBkPreGrViewDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfBkParTimeViewActn extends GolfActn{
	
	public static final String TITLE = "부킹티타임 리스트";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String permission = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			int cyberMoney = 0;
			String userNm = "";
			String userId = "";
			int intMemGrade = 0;
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				cyberMoney = userEtt.getCyberMoney();
				userNm = userEtt.getName();
				userId = userEtt.getAccount();
				intMemGrade = userEtt.getIntMemGrade();
			}
			
			
			// 02.입력값 조회		
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE);	

			// 02. 골프장 idx 가져오기
			String bk_DATE = parser.getParameter("BK_DATE", "");
			String affi_GREEN_SEQ_NO = parser.getParameter("AFFI_GREEN_SEQ_NO", "");
			
			dataSet.setString("BK_DATE", bk_DATE);
			dataSet.setString("AFFI_GREEN_SEQ_NO", affi_GREEN_SEQ_NO);

			// 04.실제 테이블(Proc) 조회 - 연습장 셀렉트 박스
			GolfBkParTimeGrListDaoProc proc2 = (GolfBkParTimeGrListDaoProc)context.getProc("GolfBkParTimeGrListDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);

			
			// 주말부킹 사용여부 가져온다.
			String co_nm = "";	// 주말부킹여부
			GolfBkPreGrViewDaoProc proc_weekend = (GolfBkPreGrViewDaoProc)context.getProc("GolfBkPreGrViewDaoProc");
			DbTaoResult grViewResult = proc_weekend.execute_weekend(context, dataSet);
			if (grViewResult != null && grViewResult.isNext()) {
				grViewResult.first();
				grViewResult.next();
				co_nm = (String) grViewResult.getObject("CO_NM");
			}	
			dataSet.setString("co_nm", co_nm);
			
			
			// 04.실제 테이블(Proc) 조회 - 달력
			GolfBkParTimeListDaoProc proc = (GolfBkParTimeListDaoProc)context.getProc("GolfBkParTimeListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("ListResult", listResult);
			
			// 04.실제 테이블(Proc) 조회 - 휴장일
			GolfBkParTimeHolyListDaoProc proc4 = (GolfBkParTimeHolyListDaoProc)context.getProc("GolfBkParTimeHolyListDaoProc");
			DbTaoResult titimeHolyList = (DbTaoResult) proc4.execute(context, request, dataSet);
			request.setAttribute("TitimeHolyList", titimeHolyList);
			
			// 04.실제 테이블(Proc) 조회 - 예약정보 확인
			GolfBkParTimeResultDaoProc proc5 = (GolfBkParTimeResultDaoProc)context.getProc("GolfBkParTimeResultDaoProc");
			DbTaoResult titimeResult = (DbTaoResult) proc5.execute(context, request, dataSet);
			request.setAttribute("TitimeResult", titimeResult);
			

			// 05. 부킹 횟수 조회 - 사이버 머니 사용내역은 회수에서 차감된다.
			int par_3_BOKG_YR = 0;			// 일년 사용 건수
			int par_3_BOKG_MO = 0;			// 한달 사용 건수
			int par_3_BOKG_YR_GREEN = 0;	// 일년 사용 건수(골프장별)
			int par_3_BOKG_MO_GREEN = 0;	// 한달 사용 건수(골프장별)
			

			GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
			DbTaoResult parTimeView = proc_times.getParBenefit(context, dataSet, request);
			if(parTimeView.isNext()){
				parTimeView.next();
				par_3_BOKG_YR = parTimeView.getInt("PAR_3_BOKG_YR");
				par_3_BOKG_MO = parTimeView.getInt("PAR_3_BOKG_MO");
				par_3_BOKG_YR_GREEN = parTimeView.getInt("PAR_3_BOKG_YR_GREEN");
				par_3_BOKG_MO_GREEN = parTimeView.getInt("PAR_3_BOKG_MO_GREEN"); 
			}
			
//			GolfBkParTimesDaoProc proc_times = (GolfBkParTimesDaoProc)context.getProc("GolfBkParTimesDaoProc");
//			DbTaoResult timesView = proc_times.execute(context, dataSet, request);
//			timesView.next();
//			if(timesView.getString("RESULT").equals("00")){
//				par_3_BOKG_YR = timesView.getString("PAR_3_BOKG_YR");
//				par_3_BOKG_MO = timesView.getString("PAR_3_BOKG_MO");
//			}
			
			debug("par_3_BOKG_YR : " + par_3_BOKG_YR + " / par_3_BOKG_MO : " + par_3_BOKG_MO); 
			debug("par_3_BOKG_YR_GREEN : " + par_3_BOKG_YR_GREEN + " / PAR_3_BOKG_MO_GREEN : " + par_3_BOKG_MO_GREEN);
			
			// 04. 접근권한 조회	
			String permissionColum = "PAR_3_BOKG_LIMT_YN";
			GolfBkPermissionDaoProc proc_permission = (GolfBkPermissionDaoProc)context.getProc("GolfBkPermissionDaoProc");
			DbTaoResult permissionView = proc_permission.execute(context, dataSet, userId, permissionColum);

			permissionView.next(); 
			if(permissionView.getString("RESULT").equals("00")){
				permission = permissionView.getString("LIMT_YN");
				//debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn === PMI_BOKG_APO_YN => " + permissionView.getString("PMI_BOKG_APO_YN"));
			}else{
				permission = "N"; 
			}
			
			debug("par3 permission : "+ permission);
			
			
			paramMap.put("permission",permission);
			paramMap.put("AFFI_GREEN_SEQ_NO_ECN", affi_GREEN_SEQ_NO);	
			paramMap.put("BK_DATE_ECN", bk_DATE);		
			paramMap.put("cyberMoney", cyberMoney+"");	
			paramMap.put("userNm", userNm);	
			paramMap.put("intMemGrade", intMemGrade+"");	
			paramMap.put("PAR_3_BOKG_YR", par_3_BOKG_YR+"");	
			paramMap.put("PAR_3_BOKG_MO", par_3_BOKG_MO+"");	
			paramMap.put("PAR_3_BOKG_YR_GREEN", par_3_BOKG_YR_GREEN+"");	
			paramMap.put("PAR_3_BOKG_MO_GREEN", par_3_BOKG_MO_GREEN+"");
	        request.setAttribute("BK_DATE", bk_DATE);
	        request.setAttribute("paramMap", paramMap);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
