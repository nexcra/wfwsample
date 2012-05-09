/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardUccSelThumbActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : UCC 파일 등록 썸네일
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.bbs;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.lang.*;
import java.text.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfBoardUccSelThumbActn extends GolfActn{
	
	public static final String TITLE = "UCC 파일 등록 썸네일";

	/***************************************************************************************
	* Golf 관리자화면
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

		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

//			----------------------------------------------------------------
//			가비아 연동 JSP소스
//			파일이름 : sample_mysite_select_thumb.jsp
//			대표이미지 수동선택 - 파일업로드 완료후 대표이미지를 선택하는 화면
//			----------------------------------------------------------------

//			고객님이 가비아에서 받은 정보
//			가비아에서 생성한 파일키 
			String file_key = parser.getParameter("file_key");
//			고객님이 생성해서 보내주신 고객님 서버에서 관리되는 파일키
			String client_key = parser.getParameter("client_key");
//			대표이미지를 선택하고 호출해야 되는 URL
			String call_url = parser.getParameter("call_url");

//			가비아에서 보내준 대표이미지 선택을 위한 임시위치
			String thumbnail_img1 = parser.getParameter("thumbnail_img1");
			String thumbnail_img2 = parser.getParameter("thumbnail_img2");
			String thumbnail_img3 = parser.getParameter("thumbnail_img3");
			String thumbnail_img4 = parser.getParameter("thumbnail_img4");
			String thumbnail_img5 = parser.getParameter("thumbnail_img5");
			
//			---------------------------------------------------------------------------
//			해당값에 대해서 DB를 이용하기 위해서 이부분에 추가하십시요

//			추가

//			---------------------------------------------------------------------------
			
//			대표이미지를 선택하고 넘기셔야 되는 정보
//			대표이미지가 정상적으로 처리 되었을경우 호출되는 URL - 처리완료화면을 구성하시면 됩니다.
			String url_success2 = AppConfig.getAppProperty("URL_REAL")+"/golfUccFileRegEnd.do";
			url_success2 = url_success2.replaceAll("\\.\\.","");
//			대표이미지 선택처리가 실패되었을 경우 호출되는 URL - 처리실패화면을 구성하시면 됩니다.
			String url_error2 = AppConfig.getAppProperty("URL_REAL")+"/golfUccFileRegErr.do";
			url_error2 = url_error2.replaceAll("\\.\\.","");
//			String user_string4 = "DD";
			
			paramMap.put("call_url", call_url);	
			paramMap.put("client_key", client_key);	
			paramMap.put("url_success2", url_success2);	
			paramMap.put("url_error2", url_error2);	
			
			paramMap.put("thumbnail_img1", thumbnail_img1);	
			paramMap.put("thumbnail_img2", thumbnail_img2);	
			paramMap.put("thumbnail_img3", thumbnail_img3);	
			paramMap.put("thumbnail_img4", thumbnail_img4);	
			paramMap.put("thumbnail_img5", thumbnail_img5);			
			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
