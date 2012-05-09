/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMojibDetailActn
*   작성자    : 서비스개발팀 강선영
*   내용      : 관리자 TM회원 상세보기
*   적용범위  : golf
*   작성일자  : 2009-07-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.tm_member;

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
import com.bccard.golf.dbtao.proc.admin.tm_member.GolfAdmMojibProc;


/******************************************************************************
* Golf
* @author	
* @version	1.0 
******************************************************************************/
public class GolfAdmMojibDetailActn extends GolfActn{
	
	public static final String TITLE = "관리자 > TM관리 > TM회원보기 ";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

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
			String jumin_no			= parser.getParameter("jumin_no", "");
			String work_date		= parser.getParameter("work_date", "");
			String page_no          = parser.getParameter("page_no","");
			String start_date       = parser.getParameter("start_date","");
			String end_date         = parser.getParameter("end_date","");
			String sch_type         = parser.getParameter("SCH_TYPE","");
			String sch_text         = parser.getParameter("SCH_TEXT","");
			String sch_state		= parser.getParameter("SCH_STATE","");

			if(jumin_no == null || jumin_no == ""){				
				jumin_no = (String)request.getAttribute("jumin_no");
				debug("jumin_no111>>>>>>>>>>>>>>>>>>>>" + jumin_no);
			}			
			
			paramMap.put("jumin_no",jumin_no);
			paramMap.put("work_date",work_date);
			paramMap.put("page_no",page_no);
			paramMap.put("start_date",start_date);
			paramMap.put("end_date",end_date);
			paramMap.put("sch_type",sch_type);
			paramMap.put("sch_text",sch_text);
			paramMap.put("sch_state",sch_state);

			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("work_date", work_date);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmMojibProc proc = new GolfAdmMojibProc();
			//GolfLoungTMDetailProc proc = (GolfLoungTMDetailProc)context.getProc("GolfLoungTMDetailProc");

			DbTaoResult dUpdFormResult = (DbTaoResult)proc.getDetail(context, dataSet);
			DbTaoResult taoResult = (DbTaoResult)proc.getCommonCode(context, dataSet,"0050");		

			DbTaoResult taoCode =  (DbTaoResult)proc.getCommonCode(context, dataSet , "0051");	
			
			request.setAttribute("taoCode", taoCode);

			request.setAttribute("UpdFormResult", dUpdFormResult);	
			request.setAttribute("taoResult", taoResult);	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		}  
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
