/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfFieldWthInqActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      :  전국골프장 안내 상세보기(날씨정보)
*   적용범위  : golf
*   작성일자  : 2009-06-17
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.lounge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
import com.bccard.golf.dbtao.proc.lounge.GolfFieldWthInqDaoProc;


/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfFieldWthInqActn extends GolfActn{
	
	public static final String TITLE = "전국골프장 안내 상세보기(날씨정보)";

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
		List xmlEtt = new ArrayList();
		
		try {
			
			// 01.세션정보체크
			
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int nowHour = cal.get(Calendar.HOUR_OF_DAY);

			String nowAmPm = "";
			
			if (nowHour >= 5 && nowHour < 11) {
				nowAmPm = "AM";
			}
			if (nowHour >= 11 && nowHour < 5) {
				nowAmPm = "PM";
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("nowAmPm", nowAmPm);
			
			// Request 값 저장
			String code	= parser.getParameter("code", "");
			
			//debug("code ====> "+ code);

			GolfFieldWthInqDaoProc proc = (GolfFieldWthInqDaoProc)context.getProc("GolfFieldWthInqDaoProc");
			xmlEtt = (List) proc.readXml(code); 
			
			request.setAttribute("xmlListResult", xmlEtt);
	        request.setAttribute("paramMap", paramMap);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
