/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPracPopActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      :  드라이빙레인지/스크린 할인쿠폰(팝업)
*   적용범위  : golf
*   작성일자  : 2009-06-13
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.drivrange;

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
import com.bccard.golf.dbtao.proc.drivrange.GolfPracPopInqDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfPracPopActn extends GolfActn{
	
	public static final String TITLE = "드라이빙레인지/스크린 할인쿠폰(팝업)";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
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
			long gf_seq_no	= parser.getLongParameter("p_idx", 0L);
			String cpn_serial	= parser.getParameter("cpn_serial", "");
			String exec_type_cd	= parser.getParameter("s_exec_type_cd", "");		// 구분
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("GF_SEQ_NO", gf_seq_no);
			dataSet.setString("CPN_SERIAL", cpn_serial);
			
			// 04.실제 테이블(Proc) 조회
			GolfPracPopInqDaoProc proc = (GolfPracPopInqDaoProc)context.getProc("GolfPracPopInqDaoProc");
			
			// 프로그램 상세조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			DbTaoResult pracPopInq = proc.execute(context, dataSet);
			
			// 05. Return 값 세팅			
			
			request.setAttribute("pracPopInqResult",pracPopInq);	
			request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
