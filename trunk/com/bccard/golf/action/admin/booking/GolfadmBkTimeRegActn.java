/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admBkTimeRegActn
*   작성자    : 미디어포스 임은혜
*   내용      : 관리자 부킹 티타임 등록
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0   
******************************************************************************/
public class GolfadmBkTimeRegActn extends GolfActn{
	
	public static final String TITLE = "관리자 부킹 티타임 등록 처리"; 

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
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE); 

			String sort 				= parser.getParameter("SORT", "").trim();			// 등록구분코드
			String gr_seq_no 			= parser.getParameter("GR_SEQ_NO", "").trim();		// 부킹골프장일련번호
			String course 				= parser.getParameter("COURSE", "").trim();			// 코스
			String bkps_date 			= parser.getParameter("BKPS_DATE", "").trim();		// 날짜
			String bkps_time 			= parser.getParameter("BKPS_TIME", "").trim();		// 티타임
			String free_memo 			= parser.getParameter("FREE_MEMO", "").trim();		// 휴장일 사유
			String sky_code 			= parser.getParameter("SKY_CODE", "").trim();		// 홀선택
			String free_yn 				= parser.getParameter("FREE_YN", "").trim();		// 파3부킹휴장일
			String par_free 			= parser.getParameter("PAR_FREE", "").trim();		// 휴장일 사유
			String evnt_yn	 			= parser.getParameter("EVNT_YN", "N").trim();		// 
			String close_yn	 			= parser.getParameter("CLOSE_YN", "N").trim();		// 휴장일여부
			String holy_yn	 			= parser.getParameter("HOLY_YN", "N").trim();		// 공휴일여부
			
			if("".equals(evnt_yn) || evnt_yn == null) evnt_yn="N";
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			debug("## sky_code : "+sky_code);
			
			dataSet.setString("SORT", sort);
			dataSet.setString("GR_SEQ_NO", gr_seq_no);
			dataSet.setString("COURSE", course);
			dataSet.setString("BKPS_DATE", bkps_date);
			dataSet.setString("BKPS_TIME", bkps_time);
			dataSet.setString("FREE_MEMO", free_memo);
			dataSet.setString("SKY_CODE", sky_code);
			dataSet.setString("FREE_YN", free_yn);
			dataSet.setString("PAR_FREE", par_free);
			dataSet.setString("evnt_yn", evnt_yn);
			dataSet.setString("CLOSE_YN", close_yn);
			dataSet.setString("HOLY_YN", holy_yn);
						
			// 04.실제 테이블(Proc) 조회
			GolfadmBkTimeRegDaoProc proc = (GolfadmBkTimeRegDaoProc)context.getProc("admBkTimeRegDaoProc");
			int addResult = proc.execute(context, request, dataSet);
			
			// 05.결과 페이지 셋팅
			String returnTrueUrl = "";
			String returnFalseUrl = "";
			
			if (sort.equals("0001")){
				returnTrueUrl = "admPreTimeList.do";
				returnFalseUrl = "admPreTimeRegForm.do";
			} else if (sort.equals("0002")){
				returnTrueUrl = "admParTimeList.do";
				returnFalseUrl = "admParTimeRegForm.do";
			} else if (sort.equals("0003")){
				returnTrueUrl = "admSkyTimeList.do";
				returnFalseUrl = "admSkyTimeRegForm.do";
			} else if (sort.equals("1000")){
				returnTrueUrl = "admPreTimeList.do";
				returnFalseUrl = "admPreTimeRegForm.do";
			} else {
				returnTrueUrl = "admSkyTimeList.do";
				returnFalseUrl = "admSkyTimeRegForm.do";
			}
			
			if (addResult == 1) {
				request.setAttribute("returnUrl", returnTrueUrl);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", returnFalseUrl);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 06. Return 값 세팅			
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
