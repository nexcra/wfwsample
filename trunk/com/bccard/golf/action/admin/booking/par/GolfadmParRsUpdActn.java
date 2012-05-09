/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admPreTimeChgActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄부킹 티타임 노출여부 처리
*   적용범위  : golf
*   작성일자  : 2009-05-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.par;

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
import com.bccard.golf.dbtao.proc.admin.booking.par.*;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfadmParRsUpdActn extends GolfActn{
	
	public static final String TITLE = "관리자 프리미엄부킹 티타임 노출여부 처리";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int lessonDelResult = 0;
		
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
			String rsvt_sql_no = parser.getParameter("RSVT_SQL_NO", "");
			String rsvt_yn = parser.getParameter("RSVT_YN", "");
			String ctnt = parser.getParameter("CTNT", "");
			String cdhd_id = parser.getParameter("CDHD_ID", "");
			String appr_opion = parser.getParameter("APPR_OPION","");
			String add_appr_opion = parser.getParameter("ADD_APPR_OPION","");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_sql_no);
			dataSet.setString("RSVT_YN", rsvt_yn);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("CDHD_ID",cdhd_id);
			dataSet.setString("APPR_OPION",appr_opion);
			dataSet.setString("ADD_APPR_OPION",add_appr_opion);
			
			// 04.실제 테이블(Proc) 조회
			GolfadmParRsUpdDaoProc proc = (GolfadmParRsUpdDaoProc)context.getProc("GolfadmParRsUpdDaoProc");		
			int editResult = proc.execute(context, dataSet);
			
			
	        if (editResult == 1) {
				request.setAttribute("returnUrl", "admParRsList.do");
				request.setAttribute("resultMsg", "예약 상세정보가 수정 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", "admParRsList.do");
				request.setAttribute("resultMsg", "예약 상세정보 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
	
	        request.setAttribute("paramMap", paramMap);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
