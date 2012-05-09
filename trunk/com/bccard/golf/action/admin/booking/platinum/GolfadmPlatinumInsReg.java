/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPlatinumInsReg
*   작성자    : 이정규
*   내용      : 플래티넘 카드 부킹 등록 처리
*   적용범위  : golf
*   작성일자  : 2010-09-13
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.platinum;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
import com.bccard.golf.dbtao.proc.admin.booking.platinum.GolfadmPlatinumListDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.premium.*;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0
******************************************************************************/
public class GolfadmPlatinumInsReg extends GolfActn{
	
	public static final String TITLE = "관리자 플래티넘 카드 부킹 등록 처리";

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
			
			String chng_mgr_id			= parser.getParameter("CHNG_MGR_ID", "").trim();		// 콜센터 등록자아이디
			String hope_rgn_code			= parser.getParameter("HOPE_RGN_CODE", "").trim();		// 회원사구분
			String titl 				= parser.getParameter("TITL", "").trim();					// 성명
			String cdhd_id 				= parser.getParameter("CDHD_ID", "").trim();				// 주민번호
			String fit_hope_club_clss 				= parser.getParameter("FIT_HOPE_CLUB_CLSS", "").trim();				// 등급
			String note_mttr_expl 				= parser.getParameter("NOTE_MTTR_EXPL", "").trim();	// 카드번호
			String cncl_aton 				= parser.getParameter("CNCL_ATON", "").trim();		// 취소일자
			String round_hope_date 			= parser.getParameter("ROUND_HOPE_DATE", "").trim();			//부킹일자
			String tot_res_num 			= parser.getParameter("TOT_PERS_NUM", "").trim();			// 금액D
			String email 			= parser.getParameter("EMAIL", "").trim();			// 골프장명
			String ctnt 			= parser.getParameter("CTNT", "").trim();			//비고
			String rsvt_yn 			= parser.getParameter("RSVT_YN", "").trim();			// 예약여부
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("CHNG_MGR_ID", chng_mgr_id);				//
			dataSet.setString("HOPE_RGN_CODE", hope_rgn_code);				// 
			dataSet.setString("TITL", titl );				//
			dataSet.setString("CDHD_ID", cdhd_id );				//
			dataSet.setString("FIT_HOPE_CLUB_CLSS", fit_hope_club_clss );				//
			
			dataSet.setString("NOTE_MTTR_EXPL", note_mttr_expl );				//
			dataSet.setString("CNCL_ATON", cncl_aton );				//
			dataSet.setString("ROUND_HOPE_DATE",round_hope_date );				//
			dataSet.setString("TOT_PERS_NUM", tot_res_num);				//
			dataSet.setString("EMAIL", email);				//
			dataSet.setString("CTNT",ctnt );				//
			dataSet.setString("RSVT_YN", rsvt_yn);				//
			
			// 04.실제 테이블(Proc) 조회
			GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");
			int addResult = proc.insertBooking(context, dataSet);			
			
	        String returnUrlTrue = "";
	        String returnUrlFalse = "";
	       	returnUrlTrue = "admPlatinumList.do";
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
}
