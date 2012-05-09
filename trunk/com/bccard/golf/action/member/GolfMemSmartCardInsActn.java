/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemSmartCardInsActn
*   작성자    : (주)미디어포스 이경희
*   내용      : 가입 > 등록처리 
*   적용범위  : golf 
*   작성일자  : 20110608
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

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
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemSmartCardInsActn extends GolfActn{
	
	public static final String TITLE = "가입 > 스마트카드 가입";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		int insResult = 0; 
		int grd = 0;
		
		String script = "";
		String returnUrl = "";
		String resultMsg = "";
		
		String userNm = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		String memGrade = "";
		int intMemGrade = 0;		
		
		try {			
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userMobile1 = (String)usrEntity.getMobile1();
				userMobile2 = (String)usrEntity.getMobile2();
				userMobile3 = (String)usrEntity.getMobile3();
			}
			 
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			//dataSet.setString("moneyType", "13");  // 스마트 카드 등급 할당 해줘야.. **
			
			int vals[]					= new int[2];
			// 04.실제 테이블(Proc) 조회
			GolfMemSmartDaoProc proc = (GolfMemSmartDaoProc)context.getProc("GolfMemSmartDaoProc");			
			//insResult = proc.execute(context, dataSet, request);			
			vals = proc.execute(context, dataSet, request);
			
			//grd moneyType
		
			
			insResult = vals[0];
			grd = vals[1];
			
			dataSet.setString("moneyType", Integer.toString(grd));	
			
			
			if(insResult>0){
				
				// 회원 등급 가져오기  
				GolfMemInsDaoProc memIns_proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
				DbTaoResult gradeView = memIns_proc.gradeExecute(context, dataSet, request);
				
				if (gradeView != null && gradeView.isNext()) {
					gradeView.first();
					gradeView.next();
					if(gradeView.getString("RESULT").equals("00")){
						memGrade = (String) gradeView.getString("memGrade").trim();	
						intMemGrade = (int) gradeView.getInt("intMemGrade");
					}
				} 				

				// 회원등급 세션 설정
				usrEntity.setMemGrade(memGrade);
				usrEntity.setIntMemberGrade((int)intMemGrade);
				usrEntity.setIntMemGrade((int)intMemGrade);
				usrEntity.setCyberMoney(0);
				
				// 문자 보내기 - 골프라운지 회원가입이 완료되었으며 00월 00일까지 서비스를 이용할 수 있습니다
				// 문자 보내기 - 골프라운지 Gold회원가입이 완료되었으며, 00월 00일까지 서비스 이용 가능합니다. (변경)
				HashMap smsMap = new HashMap();
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", userMobile1);
				smsMap.put("sPhone2", userMobile2);
				smsMap.put("sPhone3", userMobile3);

				SimpleDateFormat fmt = new SimpleDateFormat("MM월 dd일");   
				GregorianCalendar cal = new GregorianCalendar();
		        cal.add(cal.MONTH, 3);
		        Date edDate = cal.getTime();
		        String strEdDate = fmt.format(edDate);	// 유료회원기간 종료일
		        
				String smsClss = "674";
				//String message = "골프라운지 Smart 회원가입이 완료되었으며, "+strEdDate+"까지 서비스 이용 가능합니다."; 
				String message = "골프라운지 Smart 회원가입이 완료되었습니다.";
 
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = smsProc.send(smsClss, smsMap, message);
				
				returnUrl = "GolfMemJoinEnd.do";
				
			}else{
				
				resultMsg = "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.";
				returnUrl = "GolfMemMonth.do";
				
			}

			
			// 05. Return 값 세팅		
			request.setAttribute("script", script);
			request.setAttribute("returnUrl", returnUrl);
			request.setAttribute("resultMsg", resultMsg);  
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
