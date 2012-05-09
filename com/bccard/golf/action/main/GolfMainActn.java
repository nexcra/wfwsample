/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMainActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 골프 메인
*   적용범위  : golf
*   작성일자  : 2009-07-02
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
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.main.GolfMainDaoProc;
import com.bccard.golf.dbtao.proc.lounge.GolfFieldWthInqDaoProc;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfMainActn extends GolfActn{
	
	public static final String TITLE = "골프 메인";

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
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			//String lsn_seq_no = parser.getParameter("p_idx", "");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			//dataSet.setString("LSN_SEQ_NO", lsn_seq_no);
			
			// 04.실제 테이블(Proc) 조회
			//GolfMainDaoProc proc = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
			
			// 레슨 호출 => CMS
//			GolfMainDaoProc proc1 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
//			DbTaoResult mainLessonList = proc1.getLessonList(context, dataSet, 3, 0);			
//			request.setAttribute("mainLessonList", mainLessonList);	
//			paramMap.put("lessonImgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lesson");
			
			// 파3 호출 => CMS
//			GolfMainDaoProc proc2 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
//			DbTaoResult mainParList = proc2.getParList(context, dataSet, 3, 0);			
//			request.setAttribute("mainParList", mainParList);	
//			paramMap.put("ParImgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/bk_gr");
			
			// 파3 스크롤 호출
			GolfMainDaoProc proc3 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
			DbTaoResult mainParScrollList = proc3.getParScrollList(context, dataSet);			
			request.setAttribute("mainParScrollList", mainParScrollList);	
			
			// 게시판 호출 (골프뉴스)
			GolfMainDaoProc proc4 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
			DbTaoResult mainGolfNewsList = proc4.getNewsList(context, dataSet, 3, 30);			
			request.setAttribute("mainGolfNewsList", mainGolfNewsList);
			
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

			// 대표 맛집 호출 => CMS
//			GolfMainDaoProc proc5 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
//			DbTaoResult mainMainGoodFoodList = proc5.getMainGoodFoodList(context, dataSet, 1, 0);			
//			request.setAttribute("mainMainGoodFoodList", mainMainGoodFoodList);

			// 맛집 호출
			GolfMainDaoProc proc6 = (GolfMainDaoProc)context.getProc("GolfMainDaoProc");
			DbTaoResult mainGoodFoodList = proc6.getGoodFoodList(context, dataSet, 3, 50);			
			request.setAttribute("mainGoodFoodList", mainGoodFoodList);
			paramMap.put("goodFoodImgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lounge");
			
			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
