/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admParTimeDelActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄부킹 티타임 삭제 처리
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
public class GolfadmParTimeDelActn extends GolfActn{
	
	public static final String TITLE = "관리자 프리미엄부킹 티타임 삭제 처리";

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
			// ResultScriptPag.jsp 에서 배열로 저장된 Object 있을 경우 에러 발생.
			paramMap.remove("cidx");

			// Request 값 저장
			String[] lsn_seq_no = parser.getParameterValues("cidx", ""); 		// 레슨 일련번호
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			//debug("==================GolfadmParTimeDelActn================== 1");
			// 04.실제 테이블(Proc) 조회
			GolfadmParTimeDelDaoProc proc = (GolfadmParTimeDelDaoProc)context.getProc("admParTimeDelDaoProc");
			//debug("==================GolfadmParTimeDelActn================== 2");
			if (lsn_seq_no != null && lsn_seq_no.length > 0) {
				//debug("==================GolfadmParTimeDelActn================== 3");
				lessonDelResult = proc.execute(context, dataSet, lsn_seq_no);
				//debug("==================GolfadmParTimeDelActn================== 4");
			}			
			//debug("==================GolfadmParTimeDelActn================== 5");

			request.setAttribute("returnUrl", "admParTimeList.do?SORT=0001");	
			//debug("==================GolfadmParTimeDelActn================== 6");
			// 실패일 경우
			if (lessonDelResult == lsn_seq_no.length) {
				request.setAttribute("resultMsg", "티타임 삭제가 정상적으로 처리 되었습니다.");	
			} else {
				request.setAttribute("resultMsg", "티타임 삭제가 정상적으로 처리 되지 않았습니다.");
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
