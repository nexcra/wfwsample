/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmLsnPreVodChgFormActn
*   작성자	: (주)미디어포스
*   내용		: 관리자 > 레슨 > 프리미엄 동영상 > 수정 폼
*   적용범위	: golf
*   작성일자	: 2009-12-04
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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.lesson.GolfAdmLsnVodUpdFormDaoProc;
import com.bccard.golf.dbtao.proc.admin.code.GolfAdmCodeSelDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmLsnPreVodChgFormActn extends GolfActn{
	
	public static final String TITLE = "관리자 레슨동영상 수정 폼";

	/***************************************************************************************
	* Golf 관리자화면
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
			paramMap.put("imgUrlPath", AppConfig.getAppProperty("IMG_URL_REAL")+"/lesson");
			
			// Request 값 저장
			String seq_no	= parser.getParameter("p_idx", "");
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmLsnVodUpdFormDaoProc proc = (GolfAdmLsnVodUpdFormDaoProc)context.getProc("GolfAdmLsnVodUpdFormDaoProc");
			GolfAdmCodeSelDaoProc coodSelProc = (GolfAdmCodeSelDaoProc)context.getProc("GolfAdmCodeSelDaoProc");
			
			// 레슨 동영상 상세조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult lsnVodInq = proc.execute(context, dataSet);
			DbTaoResult vodClssSel = coodSelProc.execute(context, dataSet, "0045", "Y"); //동영상구분
			DbTaoResult vodLsnClssSel = coodSelProc.execute(context, dataSet, "0046", "Y"); //레슨분류
			
			// 05. Return 값 세팅			
			request.setAttribute("lsnVodInqResult", lsnVodInq);	
			request.setAttribute("vodClssSel", vodClssSel);	
			request.setAttribute("vodLsnClssSel", vodLsnClssSel);	
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
