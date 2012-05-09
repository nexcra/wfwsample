/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemCouponPopActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 가입 팝업
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.*;


import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfMemCouponPopActn extends GolfActn{
	
	public static final String TITLE = "회원 > 쿠폰조회 팝업";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			String code 					= parser.getParameter("code", "");
			String openerType 				= parser.getParameter("openerType", "").trim();
			String formtarget 				= parser.getParameter("formtarget", "");
			String formtargetEx 			= parser.getParameter("formtargetEx", "");
			String gds_code_name 			= parser.getParameter("gds_code_name", "");
			String ctgo_seq 				= parser.getParameter("ctgo_seq", "");		// 연장
			String idx 						= parser.getParameter("idx", "");	
			
			
			
			
			paramMap.put("code", code);
			paramMap.put("openerType", openerType);
			paramMap.put("formtarget", formtarget);
			paramMap.put("formtargetEx", formtargetEx);
			paramMap.put("gds_code_name", gds_code_name);
			paramMap.put("ctgo_seq", ctgo_seq);
			paramMap.put("idx", idx);
					
			debug(" formtarget => " + formtarget + " / openerType : " + openerType + " / code : " + code + " / gds_code_name : " + gds_code_name);
			// 01. 세션정보체크
			//debug("========= GolfMemJoinPopActn =========> ");
			HttpSession session	= request.getSession(false);	
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("CODE", code); //제휴업체코드
			dataSet.setString("SITE_CLSS", "10");//제휴업체코드
			dataSet.setString("EVNT_NO", "111");//제휴업체코드
			dataSet.setString("EVNT_NO2", "112");//무료쿠폰코드
			dataSet.setString("formtarget", formtarget);	// 유입구분 

			
			GolfMemInsDaoProc proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
			DbTaoResult codeCheck = proc.codeExecute(context, dataSet, request);
			debug("===================codeCheck : " + codeCheck);
			/*
			if (codeCheck != null && codeCheck.isNext()) {
				codeCheck.first();
				codeCheck.next();
				debug("===================memGrade : " + codeCheck.getString("RESULT"));
				if(codeCheck.getString("RESULT").equals("00")){
					//ctnt = (String) codeCheck.getString("CUPN_CTNT");	
					//sale_amt = (int) codeCheck.getInt("CUPN_AMT");
					//code_no = (String) codeCheck.getString("CUPN_NO");	
				}
				
			}
		*/

	        request.setAttribute("ListResult", codeCheck);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
