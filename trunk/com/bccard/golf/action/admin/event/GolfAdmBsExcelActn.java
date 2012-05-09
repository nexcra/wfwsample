/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBsExcelActn
*   작성자    : (주)미디어포스 천선정
*   내용      : 관리자 BC Golf 특별레슨 이벤트 엑셀다운로드
*   적용범위  : Golf
*   작성일자  : 2009-07-20
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

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmEvntBsExcelDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmBsExcelActn extends GolfActn{
	
	public static final String TITLE = "관리자 BC Golf 특별레슨 이벤트 리스트";

	/**************************************************************************************
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
		String subject = "";
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			// Request 값 저장
			String mode 			= parser.getParameter("mode","");
			String p_idx 			= parser.getParameter("p_idx","");

			String search_evnt		= parser.getParameter("search_evnt", "");
			String search_status	= parser.getParameter("search_status", "");
			String search_przwin	= parser.getParameter("search_przwin", "");
			String search_clss		= parser.getParameter("search_clss", "");
			String search_word		= parser.getParameter("search_word", "");
			String search_sdate 	= parser.getParameter("search_sdate", "");
			String search_edate 	= parser.getParameter("search_edate", "");
			String search_yn 		= parser.getParameter("search_yn", "");
			String search_grade 	= parser.getParameter("search_grade", "");
			String search_sex 		= parser.getParameter("search_sex", "");
			
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("mode", 			mode);
			dataSet.setString("p_idx", 			p_idx);
			dataSet.setString("search_evnt", 	search_evnt);
			dataSet.setString("search_status", 	search_status);
			dataSet.setString("search_przwin", 	search_przwin);
			dataSet.setString("search_clss", 	search_clss);
			dataSet.setString("search_word", 	search_word);
			dataSet.setString("search_sdate", 	search_sdate);
			dataSet.setString("search_edate", 	search_edate);
			dataSet.setString("search_yn", 		search_yn); 
			dataSet.setString("search_grade", 	search_grade); 
			dataSet.setString("search_sex", 	search_sex); 
			
			
			//TITLE 매핑
			
			if("EvntListExcel".equals(mode)){
				subject = "특별레슨 이벤트 목록";
			}else if("SpApplicantListExcel".equals(mode)){
				subject = "특별레슨 이벤트  신청관리 목록";
			}else if("SpSettleListExcel".equals(mode)){
				subject = "특별레슨 이벤트  결제관리 목록";
			}
			
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmEvntBsExcelDaoProc proc = (GolfAdmEvntBsExcelDaoProc)context.getProc("GolfAdmEvntBsExcelDaoProc");
			DbTaoResult boardInqList = (DbTaoResult) proc.execute(context, request, dataSet);
			request.setAttribute("boardInqList", boardInqList);
		 	
			
			paramMap.put("subject", subject);
			paramMap.put("mode",	mode);
	        request.setAttribute("paramMap", paramMap);
	       
	         
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
