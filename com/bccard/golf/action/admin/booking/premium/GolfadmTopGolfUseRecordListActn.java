/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfUseRecordListActn
*   작성자    : 장성재
*   내용      : 관리자 > 부킹 > TOP골프카드부킹전용 > TOP년월별카드사용실적
*   적용범위  : Golf
*   작성일자  : 2010-11-11
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

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;

/******************************************************************************
* Topn
* @author	 
* @version	1.0 
******************************************************************************/
public class GolfadmTopGolfUseRecordListActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 부킹 > TOP골프카드부킹전용 > TOP년월별카드사용실적";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String actnKey = getActionKey(context);

		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// Request 값 저장
			String diff = parser.getParameter("diff", "0");
			String yyyy = parser.getParameter("yyyy");
			String from = parser.getParameter("from");
			String to   = parser.getParameter("to");

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("diff", diff); 
			dataSet.setString("yyyy", yyyy);
			dataSet.setString("from",from);
			dataSet.setString("to",to); 
			
			// 04.실제 테이블(Proc) 조회
			GolfadmTopGolfUseRecordDaoProc proc = (GolfadmTopGolfUseRecordDaoProc)context.getProc("GolfadmTopGolfUseRecordDaoProc");
			paramMap.put("diff",diff);
			paramMap.put("yyyy",yyyy);
			paramMap.put("from",from);
			paramMap.put("to",to); 

			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("listResult", listResult);	
	        request.setAttribute("paramMap", paramMap);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
