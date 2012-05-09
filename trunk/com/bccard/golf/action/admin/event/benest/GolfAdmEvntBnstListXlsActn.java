/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBnstListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 가평베네스트 > 구매 리스트
*   적용범위  : Golf
*   작성일자  : 2010-03-23
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.benest;

import java.io.IOException;
import java.util.Calendar;
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
import com.bccard.golf.dbtao.proc.admin.event.benest.GolfAdmEvntBnstListXlsDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBnstListXlsActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 이벤트 > 쇼핑 > 구매 리스트";

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
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// 검색값		
			String sch_yn				= parser.getParameter("sch_yn", "");	
			String st_year 				= parser.getParameter("st_year","");
			String st_month 			= parser.getParameter("st_month","");
			String st_day 				= parser.getParameter("st_day","");
			String ed_year 				= parser.getParameter("ed_year","");
			String ed_month 			= parser.getParameter("ed_month","");
			String ed_day 				= parser.getParameter("ed_day","");
			String sch_date_st			= st_year+st_month+st_day;
			String sch_date_ed			= ed_year+ed_month+ed_day;	
			String sch_date				= parser.getParameter("sch_date", "");	
			String sch_type				= parser.getParameter("sch_type", "");	
			String sch_text				= parser.getParameter("sch_text", "");
			String sch_sttl_stat_clss	= parser.getParameter("sch_sttl_stat_clss", "");	// 결제상태 구분코드
			String sch_evnt_pgrs_clss	= parser.getParameter("sch_evnt_pgrs_clss", "");	// 참가등록 구분코드		
			String sch_green_nm			= parser.getParameter("sch_green_nm", "");
			String sch_rsvt_date		= parser.getParameter("sch_rsvt_date", "");	
			
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("sch_yn", sch_yn);
			dataSet.setString("sch_date_st", sch_date_st);
			dataSet.setString("sch_date_ed", sch_date_ed);
			dataSet.setString("sch_date", sch_date);
			dataSet.setString("sch_type", sch_type);
			dataSet.setString("sch_text", sch_text);
			dataSet.setString("sch_sttl_stat_clss", sch_sttl_stat_clss);
			dataSet.setString("sch_evnt_pgrs_clss", sch_evnt_pgrs_clss);
			dataSet.setString("sch_green_nm", sch_green_nm);
			dataSet.setString("sch_rsvt_date", sch_rsvt_date);
			
			
			// 04.실제 테이블(Proc) 조회 
			GolfAdmEvntBnstListXlsDaoProc proc = (GolfAdmEvntBnstListXlsDaoProc)context.getProc("GolfAdmEvntBnstListXlsDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			

			request.setAttribute("ListResult", listResult);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
