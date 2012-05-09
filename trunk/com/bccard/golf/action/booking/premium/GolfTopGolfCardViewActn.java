/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTopGolfCardViewActn
*   작성자    : 이정규
*   내용      : top골프 부킹 상세내역
*   적용범위  : Golf
*   작성일자  : 2010-10-20
***************************************************************************************************/
package com.bccard.golf.action.booking.premium;

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
import com.bccard.golf.dbtao.proc.booking.premium.GolfTopGolfCardListDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfTopGolfCardViewActn extends GolfActn{
	
	public static final String TITLE = "Top골프카드 전용부킹 > 회원부킹 > 티타임 리스트-상세보기";

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
			
			String msg = "";		//광릉 썬힐
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				userNm = userEtt.getName();
				userId = userEtt.getAccount();
			}
			
			// 02.입력값 조회		
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			
			paramMap.put("title", TITLE); 	

			// 03. 골프장 idx 가져오기
			String affi_green_seq_no = parser.getParameter("AFFI_GREEN_SEQ_NO", "");
			String nYear = parser.getParameter("nYear", "");
			String nMonth = parser.getParameter("nMonth", "");
			String nDay = parser.getParameter("nDay", "");
			String green_nm = parser.getParameter("green_nm", "");
			
			if(green_nm.equals("광릉")){
				msg = "kwang"; 
			}else if(green_nm.equals("썬힐")){
				msg = "sun";
			}
			
			/*
			 * 월이나 날짜가 10보다 작으면 앞에 0을 붙여줌
			 * */
			if(nMonth.length() == 1){
				nMonth = "0"+nMonth;
			}
			if(nDay.length() == 1){ 
				nDay = "0"+nDay;
			}
			
			String teof_date = nYear+nMonth+nDay;
			dataSet.setString("affi_green_seq_no", affi_green_seq_no); 
			dataSet.setString("teof_date", teof_date);
			
			
			// 04.실제 테이블(Proc) 조회 - 위약금 조회
			GolfTopGolfCardListDaoProc proc = (GolfTopGolfCardListDaoProc)context.getProc("GolfTopGolfCardListDaoProc");
			DbTaoResult getPaly = (DbTaoResult) proc.getPanalty(context, request, dataSet);
			request.setAttribute("getPaly", getPaly);
			
			
			//티타임리스트
			DbTaoResult listResult = (DbTaoResult) proc.getTtimelist(context, request, dataSet);
			
			paramMap.put("AFFI_GREEN_SEQ_NO", affi_green_seq_no);	
			paramMap.put("userNm", userNm);	
			paramMap.put("userId", userId);
			paramMap.put("GREEN_NM", green_nm);
			paramMap.put("v_TEOF_DATE", nYear+"년"+nMonth+"월"+nDay+"일");
			paramMap.put("TEOF_DATE", teof_date);
			
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("msg", msg);
	        request.setAttribute("listResult", listResult);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
