/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntMkMemberDetailActn
*   작성자    : 이정규
*   내용      : 마케팅 신청 쿠폰 상세보기
*   적용범위  : golf
*   작성일자  : 2010-09-01
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
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntMkMemberProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfEvntMkMemberDetailActn extends GolfActn{
	
	public static final String TITLE = " 마케팅 신청 쿠폰 상세보기";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String jumin_no = "";
		int print_cnt = 0;
		String strResultCode = "";

		try {
			// 01.세션정보체크
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				 jumin_no		= (String)usrEntity.getSocid(); 
			}
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String seq_no			= parser.getParameter("seq_no"); 
			String cupn_no			= parser.getParameter("cupn_no");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("SEQ_NO", seq_no);
			dataSet.setString("CUPN_NO", cupn_no);
			dataSet.setString("JUMIN_NO", jumin_no);
			
			// 04.실제 테이블(Proc) 조회
			GolfEvntMkMemberProc proc = (GolfEvntMkMemberProc)context.getProc("GolfEvntMkMemberProc");
			print_cnt = proc.getCupnPrintCnt(context, cupn_no); 		// 쿠폰 출력횟수확인
			if(print_cnt < 1){
				DbTaoResult evntMkMemberAppDetail = proc.getMkMemberAppDetail(context, dataSet); 		// 쿠폰 정보 조회
				DbTaoResult evntMkPrcGroundDetail = proc.evntMkPrcGroundDetail(context, dataSet);		//골프 연습장 정보
				DbTaoResult getMkMember = proc.getMkMember(context, request, dataSet);		//골프 연습장 정보
				request.setAttribute("evntMkMemberAppDetail", evntMkMemberAppDetail);	
				request.setAttribute("evntMkPrcGroundDetail", evntMkPrcGroundDetail);
				request.setAttribute("getMkMember", getMkMember);
		        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
			}else{
				strResultCode = "99";
				 //모든 파라미터값을 맵에 담아 반환한다.
			}
				
			// 05. Return 값 세팅	 		
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			request.setAttribute("strResultCode", strResultCode);		
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
