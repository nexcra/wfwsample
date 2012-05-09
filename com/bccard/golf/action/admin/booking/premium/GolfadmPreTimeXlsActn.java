/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admPreTimeListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄 티타임 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-21
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
import com.bccard.golf.dbtao.proc.admin.booking.*;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfadmPreTimeXlsActn extends GolfActn{
	
	public static final String TITLE = "관리자 프리미엄 티타임 리스트";

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
			String sort			= parser.getParameter("SORT", "0001"); //0001:프리미엄 0002:파3부킹
			String sch_YN				= parser.getParameter("SCH_YN", "");
			String sch_GR_SEQ_NO		= parser.getParameter("SCH_GR_SEQ_NO", "");
			String sch_RESER_CODE		= parser.getParameter("SCH_RESER_CODE", "");
			String sch_VIEW_YN			= parser.getParameter("SCH_VIEW_YN", "");
			String sch_EVNT_YN			= parser.getParameter("SCH_EVNT_YN", "");
			String sch_DATE				= parser.getParameter("SCH_DATE", "");
			String sch_DATE_ST			= parser.getParameter("SCH_DATE_ST", "");
			String sch_DATE_ED			= parser.getParameter("SCH_DATE_ED", "");
			
			
			paramMap.put("SORT", sort);
			paramMap.put("SCH_YN", sch_YN);
			paramMap.put("SCH_GR_SEQ_NO", sch_GR_SEQ_NO);
			paramMap.put("SCH_RESER_CODE", sch_RESER_CODE);
			paramMap.put("SCH_VIEW_YN", sch_VIEW_YN);
			paramMap.put("SCH_EVNT_YN", sch_EVNT_YN);
			paramMap.put("SCH_DATE", sch_DATE);
			paramMap.put("SCH_DATE_ST", sch_DATE_ST);
			paramMap.put("SCH_DATE_ED", sch_DATE_ED);

						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SORT", sort);
			dataSet.setString("SCH_YN", sch_YN);
			dataSet.setString("SCH_GR_SEQ_NO", sch_GR_SEQ_NO);
			dataSet.setString("SCH_RESER_CODE", sch_RESER_CODE);
			dataSet.setString("SCH_VIEW_YN", sch_VIEW_YN);
			dataSet.setString("SCH_EVNT_YN", sch_EVNT_YN);
			dataSet.setString("SCH_DATE", sch_DATE);
			dataSet.setString("SCH_DATE_ST", sch_DATE_ST);
			dataSet.setString("SCH_DATE_ED", sch_DATE_ED);
			dataSet.setString("LISTTYPE", "XLS");


			// 04.실제 테이블(Proc) 조회 - 골프장
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);

									
			// 04.실제 테이블(Proc) 조회 - 리스트
			GolfadmPreTimeListDaoProc proc = (GolfadmPreTimeListDaoProc)context.getProc("admPreTimeListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("ListResult", listResult);
			
			listResult.next();
			String result = listResult.getString("RESULT");
			if ("00".equals(result))
				request.setAttribute("total_cnt", listResult.getString("TOT_CNT"));
			else
				request.setAttribute("total_cnt", "0");

			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("sort", sort);
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
