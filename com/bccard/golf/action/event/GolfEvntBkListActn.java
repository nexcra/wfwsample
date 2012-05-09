/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBkListActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 부킹 이벤트 리스트
*   적용범위  : Golf
*   작성일자  : 2009-06-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.booking.GolfadmBkTimeRegFormDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkListDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfEvntBkListActn extends GolfActn{
	
	public static final String TITLE = "부킹 이벤트 리스트";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}
			 
			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			paramMap.put("RurlPath", AppConfig.getAppProperty("URL_REAL"));

			// Request 값 저장
			long page_no		= parser.getLongParameter("page_no", 1L);			// 페이지번호
			long record_size	= parser.getLongParameter("record_size", 10);		// 페이지당출력수
			String sgr_nm			= parser.getParameter("sgr_nm", "");
			String sevent_yn		= parser.getParameter("sevent_yn", "");

			String sort			= parser.getParameter("SORT", "0001"); //0001:프리미엄 0002:파3부킹
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("PAGE_NO", page_no);
			dataSet.setLong("RECORD_SIZE", record_size);
			dataSet.setString("SGR_NM", sgr_nm);
			dataSet.setString("SEVENT_YN", sevent_yn);
			dataSet.setString("SORT", sort);
			
			// 이용제한 체크
			if (isLogin.equals("1") && intMemGrade < 4) {
			
				// 04.실제 테이블(Proc) 조회
				GolfEvntBkListDaoProc proc = (GolfEvntBkListDaoProc)context.getProc("GolfEvntBkListDaoProc");
				GolfadmBkTimeRegFormDaoProc proc2 = (GolfadmBkTimeRegFormDaoProc)context.getProc("admBkTimeRegFormDaoProc");
				DbTaoResult evntPreBkTimeListResult = (DbTaoResult) proc.execute(context, request, dataSet);
				DbTaoResult preBkEvntDateResult = (DbTaoResult) proc.getPreBkEvntDate(context, request, dataSet);
				DbTaoResult titimeGreenListResult = (DbTaoResult) proc2.execute(context, request, dataSet);
	
				if (preBkEvntDateResult != null && preBkEvntDateResult.isNext()) {
					preBkEvntDateResult.first();
					preBkEvntDateResult.next();
					
					if (preBkEvntDateResult.getObject("EVNT_STRT_DATE").equals("")) {
						request.setAttribute("returnUrl", "golfEvntBkDateEnd.do");
						request.setAttribute("resultMsg", "");      
						subpage_key = "errorUrl";
					}
				}
				
				paramMap.put("resultSize", String.valueOf(evntPreBkTimeListResult.size()));
				
				request.setAttribute("evntPreBkTimeListResult", evntPreBkTimeListResult);
				request.setAttribute("preBkEvntDateResult", preBkEvntDateResult);
				request.setAttribute("titimeGreenListResult", titimeGreenListResult);
				request.setAttribute("record_size", String.valueOf(record_size));
			} else {
				subpage_key = "limitReUrl";
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
