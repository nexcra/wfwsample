/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopBkSuccSatisAction
*   작성자    : (주)미디어포스 이경희
*   내용      : 관리자 > 부킹 > TOP골프카드전용부킹 > TOP년월별부킹신청현황
*   적용범위  : golf
*   작성일자  : 2010-12-29
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmTopBkSuccSatisDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


public class GolfadmTopBkSuccSatisAction extends GolfActn{
 

	public static final String TITLE = "TOP부킹성공횟수별결과";
	
	/***************************************************************************************
	 * 탑부킹성공횟수별결과 관리자화면 
	 * @param context  WaContext 객체. 
	 * @param request  HttpServletRequest 객체. 
	 * @param response  HttpServletResponse 객체. 
	 * @return ActionResponse Action 처리후 화면에 디스플레이할 정보. 
	 ***************************************************************************************/	 
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
	
		String subpage_key = "default"; 
		  
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		  
		try {
					   
			// 02.입력값 조회  
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request 값 저장
			String mode = parser.getParameter("mode", "INIT");			
			String from = parser.getParameter("from");
			String to   = parser.getParameter("to");
			String repMbNo = parser.getParameter("repMbNo", "00");
			   
			paramMap.put("mode", mode);
			paramMap.put("from", from);
			paramMap.put("to", to);
			paramMap.put("repMbNo", repMbNo);
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			dataSet.setString("mode", mode);
			dataSet.setString("from", from);
			dataSet.setString("to", to);
			dataSet.setString("repMbNo", repMbNo);
						
			GolfadmTopBkSuccSatisDaoProc instance = GolfadmTopBkSuccSatisDaoProc.getInstance();
			
			DbTaoResult listResult = null;
			
			if (!"INIT".equals(mode)) {	
				listResult = instance.execute(context, request, dataSet);								
				request.setAttribute("BkngSuccessStatis", listResult);			    
			}			

			request.setAttribute("paramMap", paramMap);			   
			
			if (mode.equals("EXCEL")) { subpage_key = "excel"; }
			if (mode.equals("PRINT")) { subpage_key = "print"; } 
		         
		} catch(Throwable t) {
			debug(TITLE, t);
			t.printStackTrace(); 
		    throw new GolfException(TITLE, t);
		} 
		  
		return super.getActionResponse(context, subpage_key);
	
	} 	  

}