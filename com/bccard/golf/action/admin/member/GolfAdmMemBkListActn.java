/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 부킹 > 프리미엄 > 골프장 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

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
import com.bccard.golf.dbtao.proc.admin.member.*;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfAdmMemBkListActn extends GolfActn{
	
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
			long bk_page_no		= parser.getLongParameter("bk_page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 20);		// 페이지당출력수	
			String sch_GRADE	= parser.getParameter("SCH_GRADE", "");	
			String sch_STATE	= parser.getParameter("SCH_STATE", "");	
			String sch_MONEY_ST	= parser.getParameter("SCH_MONEY_ST", "");	
			String sch_MONEY_ED	= parser.getParameter("SCH_MONEY_ED", "");	
			String sch_DATE_ST	= parser.getParameter("SCH_DATE_ST", "");	
			String sch_DATE_ED	= parser.getParameter("SCH_DATE_ED", "");	
			String sch_TYPE		= parser.getParameter("SCH_TYPE", "");	
			String sch_TEXT		= parser.getParameter("SCH_TEXT", "");	
			String cdhd_ID		= parser.getParameter("CDHD_ID", "");	
			String order_yn		= parser.getParameter("ORDER_YN", "");	
			String order_nm		= parser.getParameter("ORDER_NM", "");	
			String order_value	= parser.getParameter("ORDER_VALUE", "");	
			String juminNo		= parser.getParameter("JUMIN_NO");
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("bk_page_no", bk_page_no);
			dataSet.setLong("record_size", record_size);
			dataSet.setString("SCH_GRADE", sch_GRADE);
			dataSet.setString("SCH_STATE", sch_STATE);
			dataSet.setString("SCH_MONEY_ST", sch_MONEY_ST);
			dataSet.setString("SCH_MONEY_ED", sch_MONEY_ED);
			dataSet.setString("SCH_DATE_ST", sch_DATE_ST);
			dataSet.setString("SCH_DATE_ED", sch_DATE_ED);
			dataSet.setString("SCH_TYPE", sch_TYPE);
			dataSet.setString("SCH_TEXT", sch_TEXT);
			dataSet.setString("CDHD_ID", cdhd_ID);
			dataSet.setString("ORDER_YN", order_yn);
			dataSet.setString("ORDER_NM", order_nm);
			dataSet.setString("ORDER_VALUE", order_value);

			// 04.실제 테이블(Proc) 조회 - 전체 넘버
			GolfAdmMemBkViewDaoProc procView = (GolfAdmMemBkViewDaoProc)context.getProc("GolfAdmMemBkViewDaoProc");
			DbTaoResult viewResult = (DbTaoResult) procView.execute(context, request, dataSet);

			// 04.실제 테이블(Proc) 조회
			GolfAdmMemBkListDaoProc proc = (GolfAdmMemBkListDaoProc)context.getProc("GolfAdmMemBkListDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);
			DbTaoResult grdResult = (DbTaoResult) proc.memGrd_execute(context, request, dataSet);

			paramMap.put("resultSize", String.valueOf(listResult.size()));

			request.setAttribute("ViewResult", viewResult);
			request.setAttribute("ListResult", listResult);
			request.setAttribute("grdResult", grdResult);
			request.setAttribute("record_size", String.valueOf(record_size));			
	        request.setAttribute("paramMap", paramMap);
	        request.setAttribute("JUMIN_NO", juminNo);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
