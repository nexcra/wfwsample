/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMngClubChgFormActn
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 > 전체 동호회 관리 수정폼
*   적용범위  : golf
*   작성일자  : 2009-07-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.club;

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
import com.bccard.golf.dbtao.proc.admin.club.GolfAdmMngClubUpdFormDaoProc;
//import com.bccard.golf.dbtao.proc.mania.GolfManiaCodeSelDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfadmMngClubChgFormActn2 extends GolfActn{
	
	public static final String TITLE = "관리자 > 동호회 개설자 수정폼";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면bb
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
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
			long seq_no			= parser.getLongParameter("p_idx", 0);
			
			//debug("lessonㅊㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇㅇInq.size() ::> " + seq_no);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("RECV_NO", seq_no);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmMngClubUpdFormDaoProc proc = (GolfAdmMngClubUpdFormDaoProc)context.getProc("GolfAdmMngClubUpdFormDaoProc");
			//리무진별 금액관리 추출
			//GolfManiaCodeSelDaoProc coopCpSelProc = (GolfManiaCodeSelDaoProc)context.getProc("GolfManiaCodeSelDaoProc");
			
			// 리무진할인신청프로그램 프로그램 상세조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult maniaInq = proc.execute(context, dataSet);
			//DbTaoResult coopCpSel = coopCpSelProc.execute(context, dataSet, "0012", "Y"); //차종코드
			
			// 05. Return 값 세팅			
			//debug("maniaInq.size() ::> " + maniaInq.size());
			
			request.setAttribute("maniaInqResult", maniaInq);	
			//request.setAttribute("coopCpSel", coopCpSel);	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
