/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmUNEventWinListActn
*   작성자    : E4NET 은장선
*   내용      : 관리자 > 부킹 > 프리미엄 > 골프장 리스트
*   적용범위  : Golf
*   작성일자  : 2009-08-05
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

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
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmUNEventInfoProc;

/******************************************************************************
* Topn
* @author	E4NET
* @version	1.0
******************************************************************************/
public class GolfAdmUNEventWinListActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 어드민관리 > 회원관리 > 회원리스트";

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
			String evnt_no      = parser.getParameter("evnt_no" ,"");               // 이벤트 번호
			String use_no      = parser.getParameter("use_no" ,"");               // 상품구분
			
			String sch_state    = parser.getParameter("SCH_STATE", "A");             // 검색조건
			String sch_text		= parser.getParameter("SCH_TEXT", "");              // 검색어
			String sch_date_st	= parser.getParameter("SCH_DATE_ST", "").replaceAll("-", "");           // 시작일
			String sch_date_ed	= parser.getParameter("SCH_DATE_ED", "").replaceAll("-", "");              // 종료일
			
			String type = parser.getParameter("type","");
			
			String excelYn		= parser.getParameter("excelYn", "N");  
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);	
			dataSet.setString("evnt_no", evnt_no);
			dataSet.setString("use_no", use_no);
			dataSet.setString("sch_state",sch_state);
			dataSet.setString("sch_text",sch_text);
			dataSet.setString("sch_date_st",sch_date_st);
			dataSet.setString("sch_date_ed",sch_date_ed);
			dataSet.setString("excelYn",	excelYn);
			
			// 03.실제 테이블(Proc) 조회
			GolfAdmUNEventInfoProc proc = (GolfAdmUNEventInfoProc)context.getProc("GolfAdmUNEventInfoProc");
			DbTaoResult listResult =null;
			if(type.equals("excel")){
				listResult = (DbTaoResult) proc.detExcelResult(context, request, dataSet);
			}else{
				listResult = (DbTaoResult) proc.detResult(context, request, dataSet);
			}
			
			if("N".equals(excelYn))
			{
			
				listResult.next();
				String result = listResult.getString("RESULT");
				if ("00".equals(result))
					paramMap.put("total_cnt", listResult.getString("TOT_CNT"));
				else
					paramMap.put("total_cnt", "0");
			}
			paramMap.put("resultSize", String.valueOf(listResult.size()));
			paramMap.put("evnt_no",evnt_no);
			paramMap.put("use_no",use_no);
			paramMap.put("SCH_STATE",sch_state);
			paramMap.put("SCH_TEXT",sch_text);
			paramMap.put("SCH_DATE_ST",sch_date_st);
			paramMap.put("SCH_DATE_ED",sch_date_ed);

			request.setAttribute("ListResult", listResult);			
			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}