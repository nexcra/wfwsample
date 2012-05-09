/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkChgActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 프리미엄 부킹 이벤트 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-26
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.event.GolfAdmEvntBkUpdDaoProc;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBkChgActn extends GolfActn{
	
	public static final String TITLE = "관리자 프리미엄 부킹 이벤트 수정 처리";

	/***************************************************************************************
	* 골프 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){
				admin_id	= (String)userEtt.getMemId();
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String seq_no	= parser.getParameter("p_idx", "");// 이벤트일련번호
			String evnt_from = parser.getParameter("evnt_from", "");	// 이벤트시작일자
			String evnt_to = parser.getParameter("evnt_to", "");	// 이벤트종료일자
			String prize_nm = parser.getParameter("prize_nm", "");	// 이벤트 혜택
			String disp_yn = parser.getParameter("disp_yn", "");	// 게시여부
			
			evnt_from = evnt_from.length() == 10 ? DateUtil.format(evnt_from, "yyyy-MM-dd", "yyyyMMdd"): "";
			evnt_to = evnt_to.length() == 10 ? DateUtil.format(evnt_to, "yyyy-MM-dd", "yyyyMMdd"): "";
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("EVNT_FROM", evnt_from);
			dataSet.setString("EVNT_TO", evnt_to);
			dataSet.setString("PRIZE_NM", prize_nm);
			dataSet.setString("DISP_YN", disp_yn);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmEvntBkUpdDaoProc proc = (GolfAdmEvntBkUpdDaoProc)context.getProc("GolfAdmEvntBkUpdDaoProc");
			
			// 레슨 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet);			
			
	        if (editResult == 1) {
				request.setAttribute("returnUrl", "admEvntBkList.do");
				request.setAttribute("resultMsg", "프리미엄 부킹 이벤트 수정이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", "admEvntBkChgForm.do");
				request.setAttribute("resultMsg", "프리미엄 부킹 이벤트 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("editResult", String.valueOf(editResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
