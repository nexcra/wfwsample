/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGiftXlsActn
*   작성자    : (주)미디어포스 조은미
*   내용      : 관리자 > 회원관리 >  사은품관리 > 엑셀
*   적용범위  : Golf
*   작성일자  : 2009-08-25
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
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmGiftXlsDaoProc;

/******************************************************************************
* Topn
* @author	(주)미디어포스 
* @version	1.0  
******************************************************************************/
public class GolfAdmGiftXlsActn extends GolfActn{ 
	
	public static final String TITLE = "관리자 > 회원관리 >  사은품관리 > 엑셀";

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
		
		//debug("==== GolfAdmGiftXlsActn start ===");
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String search_yn	= parser.getParameter("search_yn", "N");				// 검색여부
			String sch_date_st	= parser.getParameter("SCH_DATE_ST", "");				// 신청일
			String sch_date_ed	= parser.getParameter("SCH_DATE_ED", "");				// 신청일
			String sch_type		= parser.getParameter("SCH_TYPE", "");					// 검색조건
			String sch_text		= parser.getParameter("SCH_TEXT", "");					// 내용
			String sch_snd_yn 	= parser.getParameter("SCH_SND_YN","A");				// 발송여부
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("search_yn", search_yn);
			dataSet.setString("sch_date_st", sch_date_st);
			dataSet.setString("sch_date_ed", sch_date_ed);
			dataSet.setString("sch_type", sch_type);
			dataSet.setString("sch_text", sch_text);
			dataSet.setString("sch_snd_yn", sch_snd_yn);
			
			//debug("==== search_yn ++++++++++++++++++++++++++ ==="+search_yn);
			//debug("==== sch_type ++++++++++++++++++++++++++ ==="+sch_type);
			//debug("==== sch_text ++++++++++++++++++++++++++ ==="+sch_text);
			// 04.실제 테이블(Proc) 조회
			GolfAdmGiftXlsDaoProc proc = (GolfAdmGiftXlsDaoProc)context.getProc("GolfAdmGiftXlsDaoProc");
			DbTaoResult listResult = (DbTaoResult) proc.execute(context, request, dataSet);

			request.setAttribute("ListResult", listResult);
	        request.setAttribute("paramMap", paramMap);
	        
	        //debug("==== GolfAdmGiftXlsActn end ===");
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
