/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPlatinumUpdActn
*   작성자    : 이정규
*   내용      : 관리자 > 부킹 > 플래티넘 > 수정처리
*   적용범위  : golf
*   작성일자  : 2010-09-13
************************** 수정이력 ****************************************************************
*    일자      작성자   변경사항
*  20110523   이경희  플래티늄부킹대행 삭제기능추가
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.platinum;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.platinum.GolfadmPlatinumListDaoProc;
import com.bccard.golf.dbtao.proc.admin.booking.sky.GolfadmSkyRsUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfadmPlatinumUpdActn extends GolfActn{
	
	public static final String TITLE = "관리자 > 부킹 > 플래티넘 > 수정처리"; 

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

	        
			// Request 값 저장
			String golf_svc_rsvt_no = parser.getParameter("GOLF_SVC_RSVT_NO", "");
			String note_mttr_expl = parser.getParameter("NOTE_MTTR_EXPL", "");
			String cncl_aton = parser.getParameter("CNCL_ATON", "");
			String rount_hope_date = parser.getParameter("ROUND_HOPE_DATE", "");
			String hope_rgn_code = parser.getParameter("HOPE_RGN_CODE", "");
			String email = parser.getParameter("EMAIL", "");
			String tot_pers_num = parser.getParameter("TOT_PERS_NUM", "");
			String ctnt = parser.getParameter("CTNT", "");
			String rsvt_yn = parser.getParameter("RSVT_YN", "");
			String chng_mgr_id = parser.getParameter("CHNG_MGR_ID", "");
			String fit_hope_club_clss = parser.getParameter("FIT_HOPE_CLUB_CLSS", "");
			String delcheck = parser.getParameter("delcheck", "N");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("GOLF_SVC_RSVT_NO", golf_svc_rsvt_no);
			dataSet.setString("NOTE_MTTR_EXPL", note_mttr_expl);
			dataSet.setString("CNCL_ATON", cncl_aton);
			dataSet.setString("ROUND_HOPE_DATE", rount_hope_date);
			dataSet.setString("HOPE_RGN_CODE", hope_rgn_code);
			dataSet.setString("EMAIL", email);
			dataSet.setString("TOT_PERS_NUM", tot_pers_num);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("RSVT_YN", rsvt_yn);
			dataSet.setString("CHNG_MGR_ID", chng_mgr_id);
			dataSet.setString("FIT_HOPE_CLUB_CLSS", fit_hope_club_clss);
			

			if ( delcheck.equals("Y") ){ //삭제버튼 클릭시 
				
				GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");		
				int editResult = proc.executeDelete(context, dataSet);				

				request.setAttribute("returnUrl", "admPlatinumList.do");
				
				if (editResult == 1) {
					request.setAttribute("resultMsg", "삭제되었습니다.");  
				}else if (editResult == 2) {					
					request.setAttribute("resultMsg", "이미 정산된 내역으로 삭제 불가합니다.");					
				}else {					
					request.setAttribute("resultMsg", "삭제가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");					
				}
			
			}else { //수정버튼 클릭시
				
				// 04.실제 테이블(Proc) 조회
				GolfadmPlatinumListDaoProc proc = (GolfadmPlatinumListDaoProc)context.getProc("GolfadmPlatinumListDaoProc");		
				int editResult = proc.executeUpdate(context, dataSet);
				
				if (editResult == 1) {
					request.setAttribute("returnUrl", "admPlatinumList.do");
					request.setAttribute("resultMsg", "예약 상세정보가 수정 되었습니다.");  
				}else{
					request.setAttribute("returnUrl", "admPlatinumList.do");
					request.setAttribute("resultMsg", "예약 상세정보 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");
				}

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
