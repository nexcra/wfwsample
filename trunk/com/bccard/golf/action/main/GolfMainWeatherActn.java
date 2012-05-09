/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMainWeatherActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 골프 메인 > 날씨 정보
*   적용범위  : golf
*   작성일자  : 2010-02-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.main;

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
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.proc.lounge.GolfFieldWthInqDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfMainWeatherActn extends GolfActn{
	
	public static final String TITLE = "골프 메인 > 날씨정보";

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
			
			// 02.입력값 조회		
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// 서울.경기 날씨 호출
			GolfFieldWthInqDaoProc xmlproc = (GolfFieldWthInqDaoProc)context.getProc("GolfFieldWthInqDaoProc");

			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowDate = Integer.toString(cal.get(Calendar.DATE));
			cal.add(Calendar.DATE, 1);			
			String nowDate1 = Integer.toString(cal.get(Calendar.DATE));
			cal.add(Calendar.DATE, 1);			
			String nowDate2 = Integer.toString(cal.get(Calendar.DATE));
			int nowHour = cal.get(Calendar.HOUR_OF_DAY);

			String nowAmPm = "";
			
			if (nowHour >= 5 && nowHour < 11) {
				nowAmPm = "AM";
			}
			if (nowHour >= 11 && nowHour < 5) {
				nowAmPm = "PM";
			}
			xmlEtt = (List) xmlproc.readXml("CC001");
			request.setAttribute("xmlListResult", xmlEtt);
			paramMap.put("nowAmPm", nowAmPm);
			paramMap.put("nowDate", nowDate);
			paramMap.put("nowDate1", nowDate1);
			paramMap.put("nowDate2", nowDate2);
			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
