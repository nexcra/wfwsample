/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntKvpViewActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > Kvp > 상세보기
*   적용범위  : Golf
*   작성일자  : 2010-06-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event.kvp;

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
import com.bccard.golf.dbtao.proc.admin.event.kvp.GolfAdmEvntKvpViewDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntKvpViewActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 이벤트 > Kvp > 상세보기";
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			
			// 검색값		APLC_SEQ_NO
			String aplc_seq_no		= parser.getParameter("aplc_seq_no", "");
			String jumin_no			= parser.getParameter("jumin_no", "");
			int cmmCode				= Integer.parseInt(parser.getParameter("cmmcode", ""));
			
			
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aplc_seq_no", aplc_seq_no);
			dataSet.setString("jumin_no", jumin_no);
			
			// 04.실제 테이블(Proc) 조회 
			GolfAdmEvntKvpViewDaoProc proc = (GolfAdmEvntKvpViewDaoProc)context.getProc("GolfAdmEvntKvpViewDaoProc");
			
			// 04-1. 상세보기
			DbTaoResult viewResult = (DbTaoResult) proc.execute(context, request, dataSet);
			
			// 04-2. 결제내역 보기
			DbTaoResult payResult = (DbTaoResult) proc.execute_pay(context, request, dataSet);
			
			paramMap.put("cmmCode", Integer.toString(cmmCode));
			
			request.setAttribute("viewResult", viewResult);
			request.setAttribute("payResult", payResult);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
