/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmOrdUpdActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 쇼핑 > 구매 수정 처리
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

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.shop.GolfAdmOrdUpdDaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0
******************************************************************************/
public class GolfAdmOrdUpdActn extends GolfActn{

	public static final String TITLE = "관리자 > 이벤트 > 쇼핑 > 구매 수정 처리";

	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 02.입력값 조회한다.
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 수정사항 변수
			String buy_yn			= parser.getParameter("BUY_YN", "");
			String dlv_yn			= parser.getParameter("DLV_YN", "");
			String refund_yn		= parser.getParameter("REFUND_YN", "");
			String ord_no			= parser.getParameter("odr_no", "");
			String cdhd_id			= parser.getParameter("cdhd_id", "");
			String sttl_stat_clss	= parser.getParameter("sttl_stat_clss", "");
			String jumin_no			= parser.getParameter("jumin_no", "");
			
			if(GolfUtil.empty(cdhd_id)){
				cdhd_id = jumin_no;
			}
			
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("buy_yn", buy_yn);
			dataSet.setString("dlv_yn", dlv_yn);
			dataSet.setString("refund_yn", refund_yn);
			dataSet.setString("ord_no", ord_no);
			dataSet.setString("cdhd_id", cdhd_id);
			dataSet.setString("sttl_stat_clss", sttl_stat_clss);

			// Proc 파일 정의
			GolfAdmOrdUpdDaoProc proc = (GolfAdmOrdUpdDaoProc)context.getProc("GolfAdmOrdUpdDaoProc");
			int editResult = proc.execute(context, request, dataSet);	
			
			if (editResult == 1) {
				request.setAttribute("resultMsg", "수정이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("resultMsg", "수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			request.setAttribute("returnUrl", "admOrdList.do?gubun=B");
				
			
			// 05. Return 값 세팅
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.		

			
		} catch(Throwable t) {
			debug(TITLE, t);
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
