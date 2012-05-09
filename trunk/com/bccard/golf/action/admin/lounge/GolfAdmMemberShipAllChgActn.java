/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemberShipAllChgActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 회원권 시세 전체수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.lounge;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.lounge.GolfAdmMemberShipAllUpdDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmMemberShipAllChgActn extends GolfActn{
	
	public static final String TITLE = "관리자 회원권 시세 전체수정 처리";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no	= (String)userEtt.getMemNo(); 							
			}
			
			/*
			SimpleDateFormat DateFormat = new SimpleDateFormat("yyyyMMdd");
			Date toDay = new Date(); 
			String nowDate = DateFormat.format(toDay);
			*/
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.remove("gf_seq_no");
			paramMap.remove("today_fee");

			String[] gf_seq_no = parser.getParameterValues("gf_seq_no", "");	// 골프장 번호
			
			String fee_year = parser.getParameter("fee_year", "");	// 날짜 년도
			String fee_month = parser.getParameter("fee_month", "");	// 날짜 월
			String fee_day = parser.getParameter("fee_day", "");	// 날짜 일
			
			String[] today_fee = parser.getParameterValues("today_fee", "");	// 시세입력
			
			String fee_date = fee_year + fee_month + fee_day; //날짜
			
			//debug("fee_date :::: >>>> " + fee_date);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_no);			
			dataSet.setString("FEE_DATE", fee_date);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmMemberShipAllUpdDaoProc proc = (GolfAdmMemberShipAllUpdDaoProc)context.getProc("GolfAdmMemberShipAllUpdDaoProc");
			
			// 프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int editResult = proc.execute(context, dataSet, gf_seq_no, today_fee);			
			
	        if (editResult == gf_seq_no.length) {
				request.setAttribute("returnUrl", "admMemberShipList.do");
				request.setAttribute("resultMsg", "회원권 시세 전체수정이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", "admMemberShipAllChgForm.do");
				request.setAttribute("resultMsg", "회원권 시세 전체수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
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
