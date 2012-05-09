/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeRsvtListActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 예약 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
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
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeRsvtListDaoProc;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeTimeSelDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmRangeRsvtListActn extends GolfActn{
	
	public static final String TITLE = "관리자 드림 골프레인지 예약 리스트";

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
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			
			String rsvt_clss = nowYear+"D"; // 예약구분자
			String time	= parser.getParameter("s_time", "");		// 시간
			String start_time = "";
			String end_time = "";
			
			if (!GolfUtil.isNull(time)){
				String[] arr_time = time.split("~");
				start_time = arr_time[0];
				end_time = arr_time[1];
			}

			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			String sch_gr 			= parser.getParameter("SCH_GR_SEQ_NO","");
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;
						
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			 
			
			
			//debug("page_no :::: >>>> " + page_no);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			
			dataSet.setString("RSVT_CLSS", rsvt_clss);
			dataSet.setString("START_DT", sch_date_st);
			dataSet.setString("END_DT", sch_date_ed);
			dataSet.setString("START_TIME", start_time);
			dataSet.setString("END_TIME", end_time);
			dataSet.setString("SCH_GR_SEQ_NO", sch_gr);
			dataSet.setString("SORT", AppConfig.getDataCodeProp("DrivingRange"));
			dataSet.setString("DrivR", AppConfig.getDataCodeProp("DrivingRangeClss"));
			
			// 04.실제 테이블(Proc) 조회 - 골프장
			GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
			DbTaoResult titimeGreenList = (DbTaoResult) proc2.execute(context, request, dataSet);
			request.setAttribute("TitimeGreenList", titimeGreenList);			
			
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmRangeRsvtListDaoProc proc = (GolfAdmRangeRsvtListDaoProc)context.getProc("GolfAdmRangeRsvtListDaoProc");
			GolfAdmRangeTimeSelDaoProc coopTimeSelProc = (GolfAdmRangeTimeSelDaoProc)context.getProc("GolfAdmRangeTimeSelDaoProc");
			
			DbTaoResult rangersvtListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			// 예약시간대 조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult coopTimeSel = coopTimeSelProc.execute(context, dataSet); 

			paramMap.put("resultSize", String.valueOf(rangersvtListResult.size()));
			
			request.setAttribute("rangersvtListResult", rangersvtListResult);
			request.setAttribute("coopTimeSel", coopTimeSel);
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
