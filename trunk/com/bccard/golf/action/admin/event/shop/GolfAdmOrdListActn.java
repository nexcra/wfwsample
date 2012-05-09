/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmOrdListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 쇼핑 > 구매 리스트
*   적용범위  : Golf
*   작성일자  : 2010-03-04
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
*20110323  이경희 	보이스캐디 쇼핑
*20110425  이경희 	골프퍼팅3홀컵 + 골프퍼팅매트세트
***************************************************************************************************/
package com.bccard.golf.action.admin.event.shop;

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
import com.bccard.golf.dbtao.proc.admin.event.shop.GolfAdmOrdListDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmOrdListActn extends GolfActn{
	
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

			long page_no				= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size			= parser.getLongParameter("record_size", 20);		// 페이지당출력수
			
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
			String sch_type				= parser.getParameter("sch_type", "");	
			String sch_text				= parser.getParameter("sch_text", "");
			String sch_brand			= parser.getParameter("sch_brand", "");			// 제휴코드
			String sch_ord_dtl_clss		= parser.getParameter("sch_ord_dtl_clss", "");	// 구매여부 => 구매/비구매 ODR_DTL_CLSS:10/20
			String sch_dlv_yn			= parser.getParameter("sch_dlv_yn", "");		// 발송여부 => 발송/미발송 DLV_YN:Y/N
			String sch_ord_stat_clss	= parser.getParameter("sch_ord_stat_clss", "");	// 환불여부 => 환불/해당사항없음 ODR_STAT_CLSS : 61/else		
			String gubun				= parser.getParameter("gubun", "");
			String productName				= parser.getParameter("productName", ""); //상품유형
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("page_no", page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("sch_yn", sch_yn);
			dataSet.setString("sch_date_st", sch_date_st);
			dataSet.setString("sch_date_ed", sch_date_ed);
			dataSet.setString("sch_type", sch_type);
			dataSet.setString("sch_text", sch_text);
			dataSet.setString("sch_brand", sch_brand);
			dataSet.setString("sch_ord_dtl_clss", sch_ord_dtl_clss);
			dataSet.setString("sch_dlv_yn", sch_dlv_yn);
			dataSet.setString("sch_ord_stat_clss", sch_ord_stat_clss);
			dataSet.setString("gubun", gubun);
			dataSet.setString("productName", productName);
			
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmOrdListDaoProc proc = (GolfAdmOrdListDaoProc)context.getProc("GolfAdmOrdListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			listResult.next();
			String result = listResult.getString("RESULT");
			if ("00".equals(result))
				paramMap.put("total_cnt", listResult.getString("TOT_CNT"));
			else
				paramMap.put("total_cnt", "0");

			paramMap.put("resultSize", String.valueOf(listResult.size()));
			paramMap.put("st_year",st_year);
			paramMap.put("st_month",st_month);
			paramMap.put("st_day",st_day); 
			paramMap.put("ed_year",ed_year);
			paramMap.put("ed_month",ed_month);
			paramMap.put("ed_day",ed_day);
			paramMap.put("gubun",gubun);	
			

			//회원 수 조회	
			//DbTaoResult memberResult = (DbTaoResult) proc.execute_member(context, request, dataSet); 

			request.setAttribute("ListResult", listResult);
			//request.setAttribute("MemberResult", memberResult);
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
