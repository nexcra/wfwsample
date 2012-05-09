/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntGolfShowPopActn
*   작성자	: (주)미디어포스 임은혜
*   내용		: 골프박람회 쿠폰 출력 
*   적용범위	: golf
*   작성일자	: 2010-05-18
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
import com.bccard.golf.dbtao.proc.event.GolfEvntGolfShowPopDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntGolfShowPopActn extends GolfActn{
	
	public static final String TITLE = "골프박람회 쿠폰 출력";

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
		
		String userId = "";
		String userNm = ""; 
		String memGrade = ""; 
		String memMobile = "";
		String memMobile1 = "";
		String memMobile2 = "";
		String memMobile3 = "";
		String result = "";
		String printYn = "N";
		

		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
				userNm		= (String)usrEntity.getName(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				memMobile 	= (String)usrEntity.getMobile();
				memMobile1 	= (String)usrEntity.getMobile1();
				memMobile2 	= (String)usrEntity.getMobile2(); 
				memMobile3 	= (String)usrEntity.getMobile3(); 
			}

			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			paramMap.put("userId", userId);
			paramMap.put("userNm", userNm);
			paramMap.put("memGrade", memGrade);
			paramMap.put("memMobile", memMobile);
			paramMap.put("memMobile1", memMobile1);
			paramMap.put("memMobile2", memMobile2);
			paramMap.put("memMobile3", memMobile3);

			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("userId", userId);

			GolfEvntGolfShowPopDaoProc proc = (GolfEvntGolfShowPopDaoProc)context.getProc("GolfEvntGolfShowPopDaoProc");
			if(usrEntity != null) {
				// 04.실제 테이블(Proc) 조회
				DbTaoResult golfShowPopResult = (DbTaoResult) proc.execute(context, dataSet);

				if(golfShowPopResult!=null && golfShowPopResult.isNext()){
					golfShowPopResult.next();
					result = golfShowPopResult.getString("RESULT");
					if(result.equals("01")){
						printYn = "Y";
					}
				}
			}
			
			paramMap.put("printYn", printYn);
			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
