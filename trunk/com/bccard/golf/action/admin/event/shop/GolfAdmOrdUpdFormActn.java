/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmOrdUpdFormActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 주문상세
*   적용범위  : golf
*   작성일자  : 2010-03-04
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
*20110323  이경희 	보이스캐디 쇼핑
***************************************************************************************************/
package com.bccard.golf.action.admin.event.shop;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.shop.GolfAdmOrdUpdFormDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfAdmOrdUpdFormActn extends GolfActn{

	public static final String TITLE = "관리자 > 이벤트 > 주문상세";
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String odr_no			= parser.getParameter("odr_no", "");
			String gubun			= parser.getParameter("gubun", "");	
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("odr_no", odr_no);
			dataSet.setString("gubun", gubun);

			// 04.실제 테이블(Proc) 조회
			GolfAdmOrdUpdFormDaoProc proc = (GolfAdmOrdUpdFormDaoProc)context.getProc("GolfAdmOrdUpdFormDaoProc");
			DbTaoResult updFormResult = proc.execute(context, dataSet);

			// JSP 페이지로 내려보낼 맵설정
			request.setAttribute("UpdFormResult", updFormResult);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
