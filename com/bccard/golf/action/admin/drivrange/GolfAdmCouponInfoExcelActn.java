/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCouponInfoExcelActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 쿠폰인쇄이력 엑셀다운로드
*   적용범위  : Golf
*   작성일자  : 2009-07-07
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

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
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmCouponInfoExcelDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmCouponInfoExcelActn extends GolfActn{
	
	public static final String TITLE = "관리자 쿠폰인쇄이력 엑셀다운로드";

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
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			
			String exec_type_cd	= parser.getParameter("s_exec_type_cd", "");		// 구분
			String user_clss	= parser.getParameter("s_user_clss", "");		// 회원등급
			String start_dt	= parser.getParameter("s_start_dt", "");		// 출력일자1
			String end_dt	= parser.getParameter("s_end_dt", "");		// 출력일자2
			String search_sel	= parser.getParameter("search_sel", "");		// 직접검색조건
			String search_word	= parser.getParameter("search_word", "");		// 직접검색내용
			
			
			//debug("page_no :::: >>>> " + page_no);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("EXEC_TYPE_CD", exec_type_cd);
			dataSet.setString("USER_CLSS", user_clss);
			dataSet.setString("START_DT", start_dt);
			dataSet.setString("END_DT", end_dt);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmCouponInfoExcelDaoProc proc = (GolfAdmCouponInfoExcelDaoProc)context.getProc("GolfAdmCouponInfoExcelDaoProc");
			GolfAdmCodeSelDaoProc coopCpSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			
			DbTaoResult couponinfoListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			
			//코드 조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0008", "Y"); //연습장종류코드

			paramMap.put("resultSize", String.valueOf(couponinfoListResult.size()));
			
			if (exec_type_cd.equals("")){
				paramMap.put("s_exec_type_cd", "0001");
			}
			
			request.setAttribute("couponinfoListResult", couponinfoListResult);
			request.setAttribute("record_size", String.valueOf(record_size));
			request.setAttribute("coopCpSel", coopCpSel);	
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
