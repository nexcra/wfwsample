/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkJoinListActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 > 이벤트 > 프리미엄 부킹 이벤트 > 신청 관리 
*   적용범위  : Golf
*   작성일자  : 2009-05-27
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
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmEvntBkJoinListDaoProc;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBkJoinListActn extends GolfActn{
	
	public static final String TITLE = "관리자 프리미엄 부킹 이벤트 신청 관리 리스트";

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
		request.setAttribute("layout", layout);

		String actionKey = super.getActionKey(context);
		debug("actionKey=========>"+actionKey);
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.remove("chkEvent_yn");

			// Request 값 저장
			long page_no			= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size		= parser.getLongParameter("record_size", 20);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");
			String sgr_nm			= parser.getParameter("sgr_nm", "");
			String sprize_yn		= parser.getParameter("sprize_yn", "");
			String sort				= parser.getParameter("SORT", "0001"); //0001:프리미엄 0002:파3부킹

			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 			= parser.getParameter("ST_MONTH","");
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

			String st_year2 			= parser.getParameter("ST_YEAR2","");
			String st_month2 			= parser.getParameter("ST_MONTH2","");
			String st_day2 			= parser.getParameter("ST_DAY2","");
			String ed_year2 			= parser.getParameter("ED_YEAR2","");
			String ed_month2 		= parser.getParameter("ED_MONTH2","");
			String ed_day2 			= parser.getParameter("ED_DAY2","");
			
			String sch_date_st2		= st_year2+st_month2+st_day2;
			String sch_date_ed2		= ed_year2+ed_month2+ed_day2;
						
			paramMap.put("ST_YEAR2",st_year2);
			paramMap.put("ST_MONTH2",st_month2);
			paramMap.put("ST_DAY2",st_day2);
			paramMap.put("ED_YEAR2",ed_year2);
			paramMap.put("ED_MONTH2",ed_month2);
			paramMap.put("ED_DAY2",ed_day2);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);
			dataSet.setString("SGR_NM", sgr_nm);
			dataSet.setString("SPRIZE_YN", sprize_yn);
			dataSet.setString("SBKPS_SDATE", sch_date_st);
			dataSet.setString("SBKPS_EDATE", sch_date_ed);
			dataSet.setString("SEVNT_FROM", sch_date_st2);
			dataSet.setString("SEVNT_TO", sch_date_ed2);
			dataSet.setString("SORT", sort);
			
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmEvntBkJoinListDaoProc proc = (GolfAdmEvntBkJoinListDaoProc)context.getProc("GolfAdmEvntBkJoinListDaoProc");
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");

			DbTaoResult evntBkJoinListResult	= null;
			DbTaoResult titimeGreenListResult	= null;

			//9월의 이벤트 조회
			if (actionKey.equals("admEvntBkJoin9List"))
			{

				dataSet.setString("LESN_SEQ_NO","21");
				evntBkJoinListResult = (DbTaoResult) proc.execute2(context, request, dataSet);

			//일반 이벤트 신청 조회
			} else {
				evntBkJoinListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				titimeGreenListResult = (DbTaoResult) proc2.execute(context, request, dataSet);

			}
			paramMap.put("resultSize", String.valueOf(evntBkJoinListResult.size()));

			request.setAttribute("evntBkJoinListResult", evntBkJoinListResult);
			request.setAttribute("titimeGreenListResult", titimeGreenListResult);
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
