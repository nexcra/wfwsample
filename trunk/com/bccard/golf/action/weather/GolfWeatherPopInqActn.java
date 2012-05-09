/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfWeatherPopInqActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 날씨코드 검색
*   적용범위  : golf
*   작성일자  : 2009-06-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.weather;

import java.io.IOException;
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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.weather.GolfWeatherPopInqDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfWeatherPopInqActn extends GolfActn{
	
	public static final String TITLE = "관리자 날씨코드 검색";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("RurlPath", AppConfig.getAppProperty("URL_REAL"));

			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			
			String gf_nm = parser.getParameter("gf_nm", ""); //골프장명
			
			
			String rgn_nm	= parser.getParameter("s_rgn_nm", ""); //지역명
			
			
			if(rgn_nm.equals("1")) rgn_nm = "서울경기";
			if(rgn_nm.equals("2")) rgn_nm = "강원도";
			if(rgn_nm.equals("3")) rgn_nm = "충청북도";
			if(rgn_nm.equals("4")) rgn_nm = "충청남도";
			if(rgn_nm.equals("5")) rgn_nm = "전라북도";
			if(rgn_nm.equals("6")) rgn_nm = "전라남도";
			if(rgn_nm.equals("7")) rgn_nm = "경상북도";
			if(rgn_nm.equals("8")) rgn_nm = "경상남도";
			if(rgn_nm.equals("9")) rgn_nm = "제주도";
			
			String fmnm = parser.getParameter("fmnm", "");
			String cd = parser.getParameter("cd", "");
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("GF_NM", gf_nm);
			dataSet.setString("RGN_NM", rgn_nm);
			
			// 04.실제 테이블(Proc) 조회
			GolfWeatherPopInqDaoProc proc = (GolfWeatherPopInqDaoProc)context.getProc("GolfWeatherPopInqDaoProc");
			
			// 날씨코드 조회 :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult weatherInq = proc.execute(context, request, dataSet);
			
			// 05. Return 값 세팅			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			paramMap.put("fmnm", fmnm);
			paramMap.put("cd", cd);
			
			paramMap.put("resultSize", String.valueOf(weatherInq.size()));
			
			request.setAttribute("weatherInqResult", weatherInq);
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
