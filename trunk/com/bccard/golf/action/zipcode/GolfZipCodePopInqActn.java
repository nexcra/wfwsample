/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfZipCodePopInqActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 우편번호 검색
*   적용범위  : golf
*   작성일자  : 2009-06-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.zipcode;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.zipcode.GolfZipCodePopInqDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfZipCodePopInqActn extends GolfActn{
	
	public static final String TITLE = "관리자 우편번호 검색";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		DbTaoResult zipcodeInq = null;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String dong = parser.getParameter("dong", "");
			String fmnm = parser.getParameter("fmnm", "");
			String zpcd1 = parser.getParameter("zpcd1", "");
			String zpcd2 = parser.getParameter("zpcd2", "");
			String addr = parser.getParameter("addr", "");
			String dtaddr = parser.getParameter("dtaddr", "");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("DONG", dong); 
			
			// 04.실제 테이블(Proc) 조회
			GolfZipCodePopInqDaoProc proc = (GolfZipCodePopInqDaoProc)context.getProc("GolfZipCodePopInqDaoProc");
			
			// 우편번호 조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			if (!GolfUtil.isNull(dong)) {
				zipcodeInq = proc.execute(context, request, dataSet);
			}
			
			// 05. Return 값 세팅			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			paramMap.put("fmnm", fmnm);
			paramMap.put("zpcd1", zpcd1);
			paramMap.put("zpcd2", zpcd2);
			paramMap.put("addr", addr);
			paramMap.put("dtaddr", dtaddr);
			
			request.setAttribute("zipcodeInqResult", zipcodeInq);	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
