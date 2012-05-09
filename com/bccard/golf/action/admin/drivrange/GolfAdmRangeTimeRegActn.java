/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeTimeRegActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 일정시간 추가 처리
*   적용범위  : golf
*   작성일자  : 2009-07-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.drivrange;

import java.io.IOException;
import java.util.Calendar;
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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.drivrange.GolfAdmRangeTimeInsDaoProc;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmRangeTimeRegActn extends GolfActn{
	
	public static final String TITLE = "관리자 드림 골프레인지 일정시간 추가 처리";

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
		int addResult = 0;
		
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
			paramMap.remove("start_hh");
			paramMap.remove("start_mi");
			paramMap.remove("end_hh");
			paramMap.remove("end_mi");
			paramMap.remove("day_rsvt_num");

			long rsvtdialy_sql_no	= parser.getLongParameter("p_idx", 0L);// 일정번호
			String sls_end_yn = parser.getParameter("sls_end_yn", "");	// 휴장여부
			long rsvt_total_num = parser.getLongParameter("rsvt_total_num", 0L);	// 예약제한인원
			
			String[] start_hh = parser.getParameterValues("start_hh", "");	// 시간1
			String[] start_mi = parser.getParameterValues("start_mi", "");	// 분1
			String[] end_hh = parser.getParameterValues("end_hh", "");	// 시간2
			String[] end_mi = parser.getParameterValues("end_mi", "");	// 분2
			String[] day_rsvt_num = parser.getParameterValues("day_rsvt_num", "");	// 인원
		
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", admin_id);			
			dataSet.setLong("RSVTDIALY_SQL_NO", rsvtdialy_sql_no);
			dataSet.setString("SLS_END_YN", sls_end_yn);
			dataSet.setLong("RSVT_TOTAL_NUM", rsvt_total_num);
			
			// 04.실제 테이블(Proc) 조회
			GolfAdmRangeTimeInsDaoProc proc = (GolfAdmRangeTimeInsDaoProc)context.getProc("GolfAdmRangeTimeInsDaoProc");
			
			//  프로그램 등록 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			if (start_hh != null && start_hh.length > 0) {
				addResult = proc.execute(context, dataSet, start_hh, start_mi, end_hh, end_mi, day_rsvt_num);
			}
			
			debug("addResult =====> "+ addResult);
			
	        if (addResult == 1) {
				request.setAttribute("returnUrl", "admRangeDialyList.do");
				request.setAttribute("resultMsg", "드림 골프레인지 일정 추가가 정상적으로 처리 되었습니다.");      	
	        } else if (addResult == 9) {
				request.setAttribute("returnUrl", "admRangeTimeRegForm.do");
				request.setAttribute("resultMsg", "이미 등록되어있는 드림 골프레인지 일정입니다.");      		        	
	        } else {
				request.setAttribute("returnUrl", "admRangeTimeRegForm.do");
				request.setAttribute("resultMsg", "드림 골프레인지 일정 추가가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
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
