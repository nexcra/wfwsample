/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfCyberMoneySelInqActn
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      :  SKY72드림골프레인지 사이버머니 확인
*   적용범위  : golf
*   작성일자  : 2009-07-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.drivrange;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.text.SimpleDateFormat; 

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
//import com.bccard.golf.dbtao.proc.drivrange.GolfCyberMoneySelDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfCyberMoneySelInqActn extends GolfActn{
	
	public static final String TITLE = "SKY72드림골프레인지 사이버머니 확인";

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
		int intCyberMoney = 0; 
		
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
				intCyberMoney	= (int)usrEntity.getCyberMoney(); //사이버머니
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
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setLong("INTCYBERMONEY", intCyberMoney);
			
				
			// 04.실제 테이블(Proc) 조회
			//GolfCyberMoneySelDaoProc proc = (GolfCyberMoneySelDaoProc)context.getProc("GolfCyberMoneySelDaoProc");
			
			// 프로그램 상세조회 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			//DbTaoResult result = proc.execute(context, dataSet);
			
			// 05. Return 값 세팅			
			//debug("lessonInq.size() ::> " + lessonInq.size());
			
			//request.setAttribute("result",result);	
		    request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
		
			    
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
