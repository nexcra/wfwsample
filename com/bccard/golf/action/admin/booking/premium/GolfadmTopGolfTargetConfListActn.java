/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfTargetConfListActn
*   작성자    : (주)미디어포스 권영만
*   내용      : 관리자 부킹대상관리 평가 목록
*   적용범위  : Golf
*   작성일자  : 2010-11-02
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
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfadmPreTimeListDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfadmTopGolfTargetConfListActn extends GolfActn{
	
	public static final String TITLE = "관리자 부킹대상관리 평가 목록";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면 
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
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
			long page_no			= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size		= parser.getLongParameter("record_size", 20);		// 페이지당출력수
			
			String sort				= parser.getParameter("sort", "1000"); //0001:프리미엄  0002:파3부킹 , 1000:TOP골프
			
			String sch_YN			= parser.getParameter("SCH_YN", "");
			String sch_GR_SEQ_NO	= parser.getParameter("SCH_GR_SEQ_NO", "");
			String sch_RESER_CODE	= parser.getParameter("SCH_RESER_CODE", "");
			String sch_VIEW_YN		= parser.getParameter("SCH_VIEW_YN", "");
			String sch_EVNT_YN		= parser.getParameter("SCH_EVNT_YN", "");			
			String sch_DATE			= parser.getParameter("SCH_DATE", "");
			String st_year 			= parser.getParameter("ST_YEAR","");
			String st_month 		= parser.getParameter("ST_MONTH","");
			String st_day 			= parser.getParameter("ST_DAY","");
			String ed_year 			= parser.getParameter("ED_YEAR","");
			String ed_month 		= parser.getParameter("ED_MONTH","");
			String ed_day 			= parser.getParameter("ED_DAY","");
			
			String sch_date_st		= st_year+st_month+st_day;
			String sch_date_ed		= ed_year+ed_month+ed_day;
						
			String[] arr_seq_no = parser.getParameterValues("cidx", ""); 		// 일련번호
			/*
			String[] roundDate = null;
			long[] bkngObjNo = null;
			for(int i=0; arr_seq_no!=null && i<arr_seq_no.length; i++){
				if(i==0){
					roundDate = new String[arr_seq_no.length];
					bkngObjNo = new long[arr_seq_no.length];
				}
				roundDate[i] = arr_seq_no[i].substring(0, arr_seq_no[i].indexOf("|"));
				String objNoRem = arr_seq_no[i].substring(arr_seq_no[i].indexOf("|")+1);
				bkngObjNo[i] = Long.parseLong(objNoRem.substring(0, objNoRem.indexOf("|")));
			}*/
			String[] bkngObjNo = null;
			
			for(int i=0; arr_seq_no!=null && i<arr_seq_no.length; i++){
				if(i==0){
					bkngObjNo = new String[arr_seq_no.length];					
				}
				String[] pudarry = arr_seq_no[i].split("\\|");
				bkngObjNo[i] = pudarry[0];
			}
            
			
			paramMap.put("sort", sort);
			
			paramMap.put("SCH_YN", sch_YN);
			paramMap.put("SCH_GR_SEQ_NO", sch_GR_SEQ_NO);
			paramMap.put("SCH_RESER_CODE", sch_RESER_CODE);
			paramMap.put("SCH_VIEW_YN", sch_VIEW_YN);
			paramMap.put("SCH_DATE", sch_DATE);
			paramMap.put("SCH_DATE_ST", sch_date_st);
			paramMap.put("SCH_DATE_ED", sch_date_ed);
			paramMap.put("ST_YEAR",st_year);
			paramMap.put("ST_MONTH",st_month);
			paramMap.put("ST_DAY",st_day);
			paramMap.put("ED_YEAR",ed_year);
			paramMap.put("ED_MONTH",ed_month);
			paramMap.put("ED_DAY",ed_day);
			paramMap.put("SCH_EVNT_YN", sch_EVNT_YN);
			
						
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
			dataSet.setString("SCH_DATE_ST", sch_date_st);
			dataSet.setString("SCH_DATE_ED", sch_date_ed);
			dataSet.setString("LISTTYPE", "");
			//dataSet.setObject("roundDate",roundDate);
			dataSet.setObject("bkngObjNo",bkngObjNo);

									
			// 04.실제 테이블(Proc) 조회 - 리스트
			GolfadmPreTimeListDaoProc proc = (GolfadmPreTimeListDaoProc)context.getProc("admPreTimeListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.topGolfConfList(context, request, dataSet);
			request.setAttribute("ListResult", listResult);
			
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			String admNm = userEtt.getMemNm();
			
			listResult.next();
			String result = listResult.getString("RESULT");
			if ("00".equals(result))
				request.setAttribute("total_cnt", listResult.getString("TOT_CNT"));
			else
				request.setAttribute("total_cnt", "0");

			request.setAttribute("record_size", String.valueOf(record_size));
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("sort", sort);
	        
	        request.setAttribute("admId", admId);
	        request.setAttribute("admNm", admNm);
	        request.setAttribute("evalDateFmt", DateUtil.currdate("yyyy.MM.dd"));
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace(); 
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
