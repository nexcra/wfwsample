/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkCheckUpdActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 > 확인/취소 > 취소
*   적용범위  : golf
*   작성일자  : 2009-05-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.booking.check;

import java.io.IOException;
import java.util.HashMap;
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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.check.*;
import com.bccard.golf.dbtao.proc.booking.sky.*;
import com.bccard.golf.dbtao.proc.booking.premium.*;
import com.bccard.golf.dbtao.proc.booking.par.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfBkCheckUpdActn extends GolfActn{
	
	public static final String TITLE = "부킹 > 확인/취소 > 취소";

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
		String userId = "";
		String email_id = "";
		String userMobile = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				userMobile1 	= (String)usrEntity.getMobile1();
				userMobile2 	= (String)usrEntity.getMobile2();
				userMobile3 	= (String)usrEntity.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
			}
			 /*
			debug("==========email_id========> " + email_id);
			debug("==========userMobile1========> " + userMobile1);
			debug("==========userMobile2========> " + userMobile2);
			debug("==========userMobile3========> " + userMobile3);
			email_id = "simijoa@hanmail.net";
			userMobile1 = "010";
			userMobile2 = "9192";
			userMobile3 = "4738";
			*/
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request 값 저장
			String type = parser.getParameter("type", "");
			String idx = parser.getParameter("idx", "");
			String seq = parser.getParameter("seq", "");
			String returnUrl = parser.getParameter("returnUrl", "");
			//debug("==========returnUrl========> " + returnUrl);
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("type", type);
			dataSet.setString("idx", idx);
			dataSet.setString("userId", userId);
			
			
			// 04.실제 테이블(Proc) 조회
			GolfBkCheckUpdDaoProc proc = (GolfBkCheckUpdDaoProc)context.getProc("GolfBkCheckUpdDaoProc");		
			int editResult = proc.execute(context, dataSet);
						
	        if (editResult == 1) {

				//sms발송
				if (!userMobile.equals("")) {

					// SMS 관련 셋팅
					HashMap smsMap = new HashMap();
					
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", userNm);

					String smsClss = "";
					String message = "";
					
					//debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					
					if(type.equals("M")){
						// 프리미엄

						String gr_nm = "";
						
						dataSet.setString("TIME_SEQ_NO",	seq);
						GolfBkPreTimeRsViewDaoProc proc_mail = (GolfBkPreTimeRsViewDaoProc)context.getProc("GolfBkPreTimeRsViewDaoProc");
						DbTaoResult rsView = proc_mail.execute_cancel(context, dataSet);
						rsView.first();
						rsView.next();
						
						userMobile1 = (String) rsView.getObject("HP_DDD_NO");
						userMobile2 = (String) rsView.getObject("HP_TEL_HNO");
						userMobile3 = (String) rsView.getObject("HP_TEL_SNO");

						if(!GolfUtil.empty((String) rsView.getObject("RL_GREEN_NM"))){
							gr_nm = (String) rsView.getObject("RL_GREEN_NM");
						}else if(!GolfUtil.empty((String) rsView.getObject("GR_NM"))){
							gr_nm = (String) rsView.getObject("GR_NM");
						}else{
							gr_nm = "";
						}
						
						smsMap.put("sPhone1", userMobile1);
						smsMap.put("sPhone2", userMobile2);
						smsMap.put("sPhone3", userMobile3);
						
						smsClss = "636";
						message = "[VIP부킹] "+userNm+"님 "+ gr_nm +" "+(String) rsView.getObject("BKPS_DATE")+" "+(String) rsView.getObject("BKPS_TIME")+":"+(String) rsView.getObject("BKPS_MINUTE")+" 예약취소 - Golf Loun.G";
						
					}else if(type.equals("P")){
						// 파3부킹

						dataSet.setString("RSVT_SQL_NO",	idx);
						GolfBkParTimeRsViewDaoProc proc_mail = (GolfBkParTimeRsViewDaoProc)context.getProc("GolfBkParTimeRsViewDaoProc");
						DbTaoResult rsView = proc_mail.execute(context, dataSet);
						rsView.first();
						rsView.next();
						
						userMobile1 = (String) rsView.getObject("HP_DDD_NO");
						userMobile2 = (String) rsView.getObject("HP_TEL_HNO");
						userMobile3 = (String) rsView.getObject("HP_TEL_SNO");

						smsMap.put("sPhone1", userMobile1);
						smsMap.put("sPhone2", userMobile2);
						smsMap.put("sPhone3", userMobile3);
						
						smsClss = "638";
						message = "[파3] "+userNm+"님 "+(String) rsView.getObject("GREEN_NM")+" "+(String) rsView.getObject("BK_DATE")+ " 예약취소 - Golf Loun.G";
						
					}else if(type.equals("S")){
						// Sky72 드림듄스

						dataSet.setString("RSVT_SQL_NO",	idx);
						GolfBkSkyTimeRsViewDaoProc proc_mail = (GolfBkSkyTimeRsViewDaoProc)context.getProc("GolfBkSkyTimeRsViewDaoProc");
						DbTaoResult rsView = proc_mail.execute(context, dataSet);
						rsView.first();
						rsView.next();
						
						userMobile1 = (String) rsView.getObject("HP_DDD_NO");
						userMobile2 = (String) rsView.getObject("HP_TEL_HNO");
						userMobile3 = (String) rsView.getObject("HP_TEL_SNO");

						smsMap.put("sPhone1", userMobile1);
						smsMap.put("sPhone2", userMobile2);
						smsMap.put("sPhone3", userMobile3);

						smsClss = "642";
						message = "[드림듄스]"+userNm+"님 "+(String) rsView.getObject("HOLE")+"홀 "+(String) rsView.getObject("BK_DATE")+" "+(String) rsView.getObject("BK_TIME")+" 예약취소 - Golf Loun.G";
					}					

					debug("==========userMobile1========> " + userMobile1);
					debug("==========userMobile2========> " + userMobile2);
					debug("==========userMobile3========> " + userMobile3);
					
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					//debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}			
				
				request.setAttribute("returnUrl", returnUrl);
				request.setAttribute("resultMsg", "예약이 취소 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrl);
				request.setAttribute("resultMsg", "예약취소가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
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
