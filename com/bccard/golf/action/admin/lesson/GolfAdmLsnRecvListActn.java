/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLessonListActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨프로그램 리스트
*   적용범위  : Golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lesson;

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
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLsnRecvListDaoProc;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmLsnRecvListActn extends GolfActn{
	
	public static final String TITLE = "관리자 레슨프로그램 리스트";

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
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String search_sel		= parser.getParameter("search_sel", "");
			String search_word		= parser.getParameter("search_word", "");

			String ssex	= parser.getParameter("ssex", ""); //성별
			String scoop_cp_cd		= parser.getParameter("scoop_cp_cd", ""); //0001:제이슨골프 0002:골프다이제스트	
			String slsn_type_cd		= parser.getParameter("slsn_type_cd", "0001"); //0001:일반레슨 0002:특별레슨	
			String suser_clss		= parser.getParameter("suser_clss", ""); //회원등급
			String slsn_expc_clss		= parser.getParameter("slsn_expc_clss", ""); //레슨경험
			
			paramMap.put("scoop_cp_cd", scoop_cp_cd);
			paramMap.put("slsn_type_cd", slsn_type_cd);
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SEARCH_SEL", search_sel);
			dataSet.setString("SEARCH_WORD", search_word);

			dataSet.setString("SSEX", ssex);
			dataSet.setString("SCOOP_CP_CD", scoop_cp_cd);
			dataSet.setString("SLSN_TYPE_CD", slsn_type_cd);
			dataSet.setString("SUSER_CLSS", suser_clss);
			dataSet.setString("SLSN_EXPC_CLSS", slsn_expc_clss);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmLsnRecvListDaoProc proc = (GolfAdmLsnRecvListDaoProc)context.getProc("GolfAdmLsnRecvListDaoProc");
			GolfAdmCodeSelDaoProc coodSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			DbTaoResult lsnRecvListResult = (DbTaoResult) proc.execute(context, request, dataSet);
			DbTaoResult coopCpSel = (DbTaoResult) coodSelProc.execute(context, dataSet, "0004", "Y"); //제휴업체
			DbTaoResult lsnTypeSel = (DbTaoResult) coodSelProc.execute(context, dataSet, "0003", "Y"); //레슨구분
			DbTaoResult lsnExpcSel = (DbTaoResult) coodSelProc.execute(context, dataSet, "0006", "Y"); //레슨경험

			paramMap.put("resultSize", String.valueOf(lsnRecvListResult.size()));
			
			request.setAttribute("lsnRecvListResult", lsnRecvListResult);
			request.setAttribute("coopCpSel", coopCpSel);	
			request.setAttribute("lsnTypeSel", lsnTypeSel);	
			request.setAttribute("lsnExpcSel", lsnExpcSel);	
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
