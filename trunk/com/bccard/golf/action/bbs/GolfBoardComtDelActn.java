/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardReDelActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 덧글 삭제 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.bbs;

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
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardComtDelDaoProc;
import com.bccard.golf.dbtao.proc.bbs.GolfBoardComtInqDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfBoardComtDelActn extends GolfActn{
	
	public static final String TITLE = "공통게시판 덧글 삭제 처리";

	/***************************************************************************************
	* 골프 관리자화면
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
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		String reUrl = super.getActionParam(context, "reUrl");
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

			// Request 값 저장
			String reply_no = parser.getParameter("re_idx", "");
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("REPLY_NO", reply_no);
			
			// 04.실제 테이블(Proc) 조회
			GolfBoardComtInqDaoProc comtInqProc = (GolfBoardComtInqDaoProc)context.getProc("GolfBoardComtInqDaoProc");
			DbTaoResult comtInResult = comtInqProc.execute(context, dataSet);
			
			String rgs_pe_id = "";
			// 작성자 체크
			if (comtInResult != null && comtInResult.isNext()) {
				comtInResult.next();
				if (comtInResult.getObject("RESULT").equals("00")) {
					rgs_pe_id = (String)comtInResult.getString("RGS_PE_ID");
				}
			}
			
			int bbsReDelResult = 0;
			if (userId.equals(rgs_pe_id)) {
				GolfBoardComtDelDaoProc proc = (GolfBoardComtDelDaoProc)context.getProc("GolfBoardComtDelDaoProc");
				bbsReDelResult = proc.execute(context, request, dataSet);
	
				if (bbsReDelResult == 1) {
					request.setAttribute("resultMsg", "");
				} else {
					request.setAttribute("resultMsg", "게시물 덧글 삭제가 정상적으로 처리 되지 않았습니다.");
				}				
			} else {
				request.setAttribute("resultMsg", "본인이 작성한 글만 삭제할 수 있습니다.");
			}
			
			request.setAttribute("returnUrl", reUrl);	
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
