/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeListActn
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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.dbtao.proc.booking.GolfBkPenaltyDaoProc;
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.dbtao.proc.booking.premium.GolfBkPreGrViewDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfBkParTimeListActn extends GolfActn{
	
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
		String penalty = "";
		String penalty_start = "";
		String penalty_end = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE);	

			// 04-01. 부킹 제한 조회
			GolfBkPenaltyDaoProc proc_penalty = (GolfBkPenaltyDaoProc)context.getProc("GolfBkPenaltyDaoProc");
			DbTaoResult penaltyView = proc_penalty.execute(context, dataSet, request);
			
			penaltyView.next();
			if(penaltyView.getString("RESULT").equals("00")){
				penalty = "Y";
				penalty_start = penaltyView.getString("BK_LIMIT_ST");
				penalty_end = penaltyView.getString("BK_LIMIT_ED");
			}else{
				penalty = "N";
			}
			paramMap.put("penalty", penalty);
			paramMap.put("penalty_start", penalty_start);
			paramMap.put("penalty_end", penalty_end);
//			debug("=`=`=`=`=`=`=`=`=GolfBkPreGrListActn ===  penalty => " + penalty);
			
			
			
			// 02. 골프장 idx 가져오기
			String affi_GREEN_SEQ_NO = parser.getParameter("AFFI_GREEN_SEQ_NO", "");			
			GolfBkParTimeGrNewViewDaoProc proc3 = (GolfBkParTimeGrNewViewDaoProc)context.getProc("GolfBkParTimeGrNewViewDaoProc");
			DbTaoResult titimeGrNewView = (DbTaoResult) proc3.execute(context, request, dataSet);
			if(affi_GREEN_SEQ_NO.equals("")){
				if (titimeGrNewView != null && titimeGrNewView.isNext()) {
					titimeGrNewView.first();
					titimeGrNewView.next();
					affi_GREEN_SEQ_NO = (String) titimeGrNewView.getObject("MAX_AFFI_GREEN_SEQ_NO");
				}
			}			
			paramMap.put("AFFI_GREEN_SEQ_NO", affi_GREEN_SEQ_NO);			
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			dataSet.setString("AFFI_GREEN_SEQ_NO", affi_GREEN_SEQ_NO);
			
			
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
			

			// 04.실제 테이블(Proc) 조회 - 연습장
			GolfBkParTimeGrListDaoProc proc2 = (GolfBkParTimeGrListDaoProc)context.getProc("GolfBkParTimeGrListDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);
			
			// 04.실제 테이블(Proc) 조회 - 티타임 리스트
			GolfBkParTimeListDaoProc proc = (GolfBkParTimeListDaoProc)context.getProc("GolfBkParTimeListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("ListResult", listResult);
			
			// 04.실제 테이블(Proc) 조회 - 휴장일
			GolfBkParTimeHolyListDaoProc proc4 = (GolfBkParTimeHolyListDaoProc)context.getProc("GolfBkParTimeHolyListDaoProc");
			DbTaoResult titimeHolyList = (DbTaoResult) proc4.execute(context, request, dataSet);
			request.setAttribute("TitimeHolyList", titimeHolyList);
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
