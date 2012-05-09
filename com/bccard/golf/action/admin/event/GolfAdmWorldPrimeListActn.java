/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmSpecialBookingListActn
*   작성자    : 이포넷 은장선
*   내용      : 관리자 > 이벤트->VIP부킹이벤트->명문골프장부킹 
*   적용범위  : Golf
*   작성일자  : 2009-09-17
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
* golfloung		20100305	임은혜	검색수정
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmWorldPrimeDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	이포넷 은장선
* @version	1.0
******************************************************************************/
public class GolfAdmWorldPrimeListActn extends GolfActn{
	
	public static final String TITLE = "관리자 월드 프라임 이벤트 신청 리스트";

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
			long page_no			= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size		= parser.getLongParameter("record_size", 10);		// 페이지당출력수	
						
			String sch_yn			= parser.getParameter("sch_yn","");
			
			String sch_date			= parser.getParameter("sch_date","");          	//검색일자구분
			String sch_type			= parser.getParameter("sch_type","");           //이름,ID조회 여부
			String search_word		= parser.getParameter("search_word","");        //조회 명
			String sch_sttl_stat_clss	= parser.getParameter("sch_sttl_stat_clss","");
			String golf_cmmn_code	= parser.getParameter("golf_cmmn_code","");     //예약코드
			String green_nm			= parser.getParameter("green_nm","");           //신청골프장명
			String sch_rsvt_date	= parser.getParameter("sch_rsvt_date", "");	
			String sch_rsv_time		= parser.getParameter("sch_rsv_time", "");
			
			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;
					
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("actnKey",actnKey);
			dataSet.setString("sch_reg_aton_st",sch_date_st);
			dataSet.setString("sch_reg_aton_ed",sch_date_ed);
			dataSet.setString("sch_date",sch_date);
			dataSet.setString("sch_type",sch_type);
			dataSet.setString("search_word",search_word);
			dataSet.setString("golf_cmmn_code",golf_cmmn_code);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("sch_sttl_stat_clss",sch_sttl_stat_clss);
			dataSet.setString("sch_rsvt_date",sch_rsvt_date);
			dataSet.setString("sch_rsv_time",sch_rsv_time);			
			dataSet.setString("sch_yn",sch_yn);			
			
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmWorldPrimeDaoProc proc = (GolfAdmWorldPrimeDaoProc)context.getProc("GolfAdmWorldPrimeDaoProc");
		
			DbTaoResult evntMMListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			//예약일
			//DbTaoResult schDateListResult = (DbTaoResult) proc.execute_schDate(context, request, dataSet);
			
			try { 
				paramMap.put("resultSize", String.valueOf(evntMMListResult.size()));
				paramMap.put("page_no",String.valueOf(page_no)); 
				paramMap.put("total_cnt",String.valueOf(evntMMListResult.size()));
			} catch(Throwable t) {}
			
			
			paramMap.put("sch_date"	,	sch_date	);		
			paramMap.put("sch_type"	,	sch_type	);	
			paramMap.put("search_word",		search_word);
			paramMap.put("sch_sttl_stat_clss",		sch_sttl_stat_clss);			
			paramMap.put("sch_rsvt_date",sch_rsvt_date);
			paramMap.put("sch_rsv_time",sch_rsv_time);
			paramMap.put("golf_cmmn_code",golf_cmmn_code);
			paramMap.put("green_nm",green_nm);	
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			paramMap.put("page_no",page_no+"");
			paramMap.put("sch_yn",sch_yn);
					
			request.setAttribute("evntMMListResult", evntMMListResult);	
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
