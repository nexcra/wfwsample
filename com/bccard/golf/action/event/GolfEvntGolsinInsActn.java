/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntGolsinInsActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 프리미엄 부킹 이벤트 신청 처리
*   적용범위  : golf
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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.GolfEvntGolsinInsDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfEvntGolsinInsActn extends GolfActn{
	
	public static final String TITLE = "골신 이벤트 신청 처리"; 

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int myPointResult =  0;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String reUrl = "golfGolsinInsForm.do";
		String errReUrl = "golfGolsinInsForm.do";
		request.setAttribute("layout", layout);

		try {
			// 01.세션정보체크
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

			long recv_no	= 6;
			String lsn_type_cd = "0009";
			

			String taNum = parser.getParameter("taNum", "");
			String mobile1 = parser.getParameter("mobile1", "");
			String mobile2 = parser.getParameter("mobile2", "");
			String mobile3 = parser.getParameter("mobile3", "");

			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("userId", userId);		
			dataSet.setString("taNum", taNum);		
			dataSet.setString("mobile1", mobile1);	
			dataSet.setString("mobile2", mobile2);	
			dataSet.setString("mobile3", mobile3);		
			dataSet.setLong("RECV_NO", recv_no);
			dataSet.setString("LSN_TYPE_CD", lsn_type_cd);
			
			// 04.실제 테이블(Proc) 조회
			
			GolfEvntGolsinInsDaoProc proc = (GolfEvntGolsinInsDaoProc)context.getProc("GolfEvntGolsinInsDaoProc");
			int addResult = proc.execute(context, dataSet);
			String aplc_seq_no = proc.getMaxSeqNo(context, dataSet);
			
	        if (addResult == 1) {
	        	request.setAttribute("script", "parent.location.href='http://www.golfloung.com/app/golfloung/html/event/bcgolf_event/progress_event.jsp?p_idx=6';");
				//request.setAttribute("returnUrl", reUrl);
				request.setAttribute("resultMsg", "골신만들기 이벤트 신청이 \\n\\n정상적으로 완료되었습니다.\\n\\n감사합니다.");      	
	        } else if (addResult == 2) {
				//request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("script", "parent.location.href='http://www.golfloung.com/app/golfloung/html/event/bcgolf_event/progress_event.jsp?p_idx=6';");
				request.setAttribute("resultMsg", "이미 신청하셨습니다.");      		        	
	        } else {
				request.setAttribute("returnUrl", errReUrl);
				request.setAttribute("resultMsg", "골신 이벤트 신청이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
			
			// 05. Return 값 세팅			
			paramMap.put("aplc_seq_no", aplc_seq_no);		
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
