/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntMngViewActn.java
*   작성자    : 이정규
*   내용      : 관리자 > 이벤트 > 월례회 > 월례회 상세보기
*   적용범위  : Golf
*   작성일자  : 2010-09-30
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.benest;


import java.io.IOException;
import java.util.Calendar;
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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntMngListDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntMngViewActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 이벤트 > 월례회 > 월례회 상세보기";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체.  
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보.  
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
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/benest");

			// 검색값		APLC_SEQ_NO
			String seq_no			= parser.getParameter("seq_no", "");	
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("seq_no", seq_no);
			
			
			// 04.실제 테이블(Proc) 조회 
			GolfAdmEvntMngListDaoProc proc = (GolfAdmEvntMngListDaoProc)context.getProc("GolfAdmEvntMngListDaoProc");
			
			// 04-1. 월례회 상세보기
			DbTaoResult viewResult = (DbTaoResult) proc.execute_view(context, request, dataSet);
			
			// 04-2. 월례회 날짜 보여주기
			DbTaoResult listResult = (DbTaoResult) proc.execute_datelist(context, request, dataSet);
			/*String max_seq_no = "";
			while(listResult != null && listResult.isNext()){
				listResult.next();
				max_seq_no = listResult.getString("max_seq_no");
			}*/
						
			request.setAttribute("viewResult", viewResult);
			request.setAttribute("listResult", listResult);
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("seq_no", seq_no);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
