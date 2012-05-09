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

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmSpecialBookingDaoProc;

/******************************************************************************
* Golf
* @author	이포넷 은장선
* @version	1.0
******************************************************************************/
public class GolfAdmSpecialBookingListActn extends GolfActn{
	
	public static final String TITLE = "관리자 프리미엄 부킹 이벤트 당첨자 리스트";

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
			String green_nm				= parser.getParameter("green_nm","");           //신청골프장명
			String golf_cmmn_code		= parser.getParameter("golf_cmmn_code","");     //예약코드
			String grade				= parser.getParameter("grade","");     
			String sch_yn				= parser.getParameter("sch_yn","");          	//검색여부
			String sch_date				= parser.getParameter("sch_date","");          	//검색일자구분
			String sch_type				= parser.getParameter("sch_type","");           //이름,ID조회 여부
			String search_word			= parser.getParameter("search_word","");        //조회 명

			String sch_chng_aton_st		= parser.getParameter("sch_chng_aton_st","");   //조회 취소일자 시작일
			String sch_chng_aton_ed		= parser.getParameter("sch_chng_aton_ed","");   //조회 취소일자 종료일
			String doyn			        = parser.getParameter("doyn","");               //처리여부
			

			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;
						
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			
//			debug("green_nm>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + green_nm); 
//			debug("sch_yn>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_yn); 
//			debug("golf_cmmn_code>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + golf_cmmn_code); 
//			debug("grade>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + grade);
//			debug("sch_reg_aton_st>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_reg_aton_st);
//			debug("sch_reg_aton_ed>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_reg_aton_ed);
//			debug("sch_pu_date_st>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_pu_date_st);
//			debug("sch_pu_date_ed>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_pu_date_ed);
//			debug("sch_type>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + sch_type);
//			debug("search_word>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + search_word);

			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("sch_yn",sch_yn);
			dataSet.setString("sch_date",sch_date);
			dataSet.setString("golf_cmmn_code",golf_cmmn_code);
			dataSet.setString("grade",grade);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("sch_reg_aton_st",sch_date_st);
			dataSet.setString("sch_reg_aton_ed",sch_date_ed);
			dataSet.setString("sch_type",sch_type);
			dataSet.setString("search_word",search_word);
			dataSet.setString("actnKey",actnKey);
			dataSet.setString("sch_chng_aton_st",sch_chng_aton_st);
			dataSet.setString("sch_chng_aton_ed",sch_chng_aton_ed);
			dataSet.setString("doyn",doyn);


	
			// 04.실제 테이블(Proc) 조회
			GolfAdmSpecialBookingDaoProc proc = (GolfAdmSpecialBookingDaoProc)context.getProc("GolfAdmSpecialBookingDaoProc");
		
			DbTaoResult evntMMListResult = (DbTaoResult) proc.execute(context, request, dataSet);

			paramMap.put("resultSize", String.valueOf(evntMMListResult.size()));
			paramMap.put("page_no",String.valueOf(page_no));

			
			paramMap.put("green_nm",green_nm);			
			paramMap.put("golf_cmmn_code",golf_cmmn_code);
			paramMap.put("grade",grade	);		
			paramMap.put("sch_type"	,	sch_type	);	
			paramMap.put("search_word",		search_word);	
			paramMap.put("total_cnt",String.valueOf(evntMMListResult.size()));
			paramMap.put("sch_chng_aton_st",sch_chng_aton_st);	
			paramMap.put("sch_chng_aton_ed",sch_chng_aton_ed);	
			paramMap.put("doyn",doyn);	
		
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
