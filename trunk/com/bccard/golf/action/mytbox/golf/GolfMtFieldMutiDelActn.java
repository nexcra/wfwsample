/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtFieldMutiDelActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 마이티박스(골프장정보) 다중 삭제 처리
*   적용범위  : golf
*   작성일자  : 2009-06-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.mytbox.golf;

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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.mytbox.golf.GolfMtFieldMutiDelDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfMtFieldMutiDelActn extends GolfActn{
	
	public static final String TITLE = "마이티박스(골프장정보) 다중 삭제 처리";

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
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
		int golfmtfieldDelResult = 0;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); //이름
				memClss		= (String)usrEntity.getMemberClss(); //등급번호
				userId		= (String)usrEntity.getAccount(); //아이디
				juminno 	= (String)usrEntity.getSocid(); //주민번호
				memGrade 	= (String)usrEntity.getMemGrade(); //등급
				intMemGrade	= (int)usrEntity.getIntMemGrade(); //등급번호
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
			// ResultScriptPag.jsp 에서 배열로 저장된 Object 있을 경우 에러 발생.
			paramMap.remove("cidx");

			// Request 값 저장
			String[] seq_no = parser.getParameterValues("cidx", ""); 		//일련번호
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ADMIN_NO", userId);
			
			// 04.실제 테이블(Proc) 조회
			GolfMtFieldMutiDelDaoProc proc = (GolfMtFieldMutiDelDaoProc)context.getProc("GolfMtFieldMutiDelDaoProc");
			// 찜 삭제 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
			if (seq_no != null && seq_no.length > 0) {
				golfmtfieldDelResult = proc.execute(context, dataSet, seq_no);
			}			

			request.setAttribute("returnUrl", "golfMtFieldList.do");	
			// 실패일 경우
			if (golfmtfieldDelResult == seq_no.length) {
				request.setAttribute("resultMsg", "");	
			} else {
				request.setAttribute("resultMsg", "골프장정보 삭제가 정상적으로 처리 되지 않았습니다.");
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
