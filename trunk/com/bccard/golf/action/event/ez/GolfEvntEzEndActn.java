/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntKvpActn 
*   작성자	: (주)미디어포스 임은혜
*   내용		: KVP 처리
*   적용범위	: golf
*   작성일자	: 2010-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.ez;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.ez.GolfEvntEzEndDaoProc;
import com.bccard.golf.msg.MsgEtt;

import com.bccard.golf.common.security.cryptography.*;
import com.initech.util.Base64Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntEzEndActn extends GolfActn{
	
	public static final String TITLE = "이지웰 리턴 처리";

	/***************************************************************************************
	* 골프 사용자화면
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

			// 후처리
			String resultMsg = "";
			String script = "";
						
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 기본 조회 
			String aspOrderNum 	= (String)parser.getParameter("dec_aspOrderNum");	// 주문번호 (제휴사측) -> 골프라운지 주문번호
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("aspOrderNum", aspOrderNum);

			
			// BC 회원인지 알아본다.
			GolfEvntEzEndDaoProc proc = (GolfEvntEzEndDaoProc)context.getProc("GolfEvntEzEndDaoProc");
			int bcMemCnt = proc.updEvntFunction(context, request, dataSet);
			
			String real_host_domain = AppConfig.getAppProperty("REAL_HOST_DOMAIN");
			String goUrl = real_host_domain+"/app/golfloung/join_frame2.do?url=/app/golfloung/index.jsp";
			
			
			if(bcMemCnt==0){
				// 회원이 아니면 가입 페이지로 넘긴다.
				goUrl = real_host_domain+"/app/golfloung/join_frame2.do?url=/app/golfloung/html/common/member_join.jsp";
			}
			paramMap.put("goUrl", goUrl);
			

			request.setAttribute("resultMsg", resultMsg);
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
