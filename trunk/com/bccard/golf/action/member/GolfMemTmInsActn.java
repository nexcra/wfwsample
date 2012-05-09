/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemTmInsActn
*   작성자    : 미디어포스 임은혜
*   내용      : 가입 > 등록처리 > TM
*   적용범위  : golf 
*   작성일자  : 2009-07-29
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		20100601	임은혜	 DM 영화이벤트 팝업창 사이즈 변경 
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;
import com.bccard.golf.dbtao.proc.event.tmMovie.GolfEvntTmMovieProc;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemTmInsActn extends GolfActn{
	
	public static final String TITLE = "가입 > TM 등록";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userNm = ""; 
		String userId = "";
		String memGrade = ""; 
		int intMemGrade = 0; 
		String email_id = "";
		String join_chnl = "";
		int addResult = 0;
		String userSocid = "";
		int ibkResult = 0;		// ibk 기간연장 결과
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				userSocid 	= (String)usrEntity.getSocid();
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
						
			// 04.실제 테이블(Proc) 조회
			GolfMemTmInsDaoProc proc = (GolfMemTmInsDaoProc)context.getProc("GolfMemTmInsDaoProc");
			//int addResult = proc.execute(context, dataSet, request);	
			DbTaoResult tmView = proc.execute(context, dataSet, request);
			
			if (tmView != null && tmView.isNext()) {
				tmView.first();
				tmView.next();
				addResult =  (int) tmView.getInt("addResult");
				intMemGrade = (int) tmView.getInt("intMemGrade");	
				memGrade = (String) tmView.getString("memGrade");	
				join_chnl = (String) tmView.getString("joinChnl");			
				
				// 기업회원일 경우 혜택기간을 2달 늘려준다.
				ibkResult = proc.execute_ibk(context, dataSet, request);
				if(ibkResult>0){

					HashMap smsMap = new HashMap();
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", usrEntity.getName());
					smsMap.put("sPhone1", usrEntity.getMobile1());
					smsMap.put("sPhone2", usrEntity.getMobile2());
					smsMap.put("sPhone3", usrEntity.getMobile3());

					SimpleDateFormat fmt = new SimpleDateFormat("yyyy/MM/dd");   
					GregorianCalendar cal = new GregorianCalendar();
			        cal.add(cal.MONTH, 14);
			        Date edDate = cal.getTime();
			        String strEdDate = fmt.format(edDate);	// 유료회원기간 종료일
			        
					String smsClss = "674";
		        	String message = "IBK굿샷퍼레이드 이벤트로 유료가입일자가 "+strEdDate+"까지 2개월연장되었습니다.";

					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					
				}
			}	
			debug("GolfMemTmIns :: memGrade : " + memGrade + " / intMemGrade : " + intMemGrade);

        	String returnUrlTrue = "GolfMemJoinEnd.do";
        	String returnUrlFalse =  "GolfMemJoinNoCard.do";
        	String script = "parent.location.href='/app/golfloung/html/common/member_join_finish.jsp'";

			if (addResult == 1) {

				usrEntity.setMemGrade(memGrade);
				usrEntity.setIntMemGrade((int)intMemGrade);
				usrEntity.setCyberMoney(0);

				if (!email_id.equals("")) {

					String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String emailTitle = "";
					String emailFileNm = "";
					
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailTitle = "[Golf Loun.G] 골프라운지 서비스 가입을 축하드립니다.";
					emailFileNm = "/email_tpl19.html";
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+memGrade);
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setTo(email_id);
					//sender.send(emailEtt);  
				}
				
				//이벤트 쿠폰 발급
/*
				debug("##################################TM 이벤트 처리 시작##############################");

				String from_date = "";
				String to_date   = "";
				String cupn      = ""; 
				String currDate  = DateUtil.currdate("yyyyMMdd");

				dataSet.setString("evnt_no","110");
				GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
				DbTaoResult evntInterpark = (DbTaoResult) inter.eventDateCheck(context, request, dataSet);
				
				if (evntInterpark != null && evntInterpark.isNext()) {
					evntInterpark.first(); 
					evntInterpark.next();
					if(evntInterpark.getString("RESULT").equals("00")){
						from_date = evntInterpark.getString("FROM_DATE");
						to_date = evntInterpark.getString("TO_DATE");
						debug("from_date ~ to_date >>>>>>>>>>>>>>>" + from_date + "~" + to_date + ",현재날짜 : "+ currDate);
					}
				}

				if((Integer.parseInt(from_date) <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt(to_date))){
					debug("기간내에 들어와 이벤트 진행함");
					boolean doUpdate = false;
					int cnt = 1;
					synchronized(this) {	// 동시 유저 발생시 같은 max 값 얻어오는걸 방지
						DbTaoResult cupnInterpark = (DbTaoResult) inter.cupnNumber(context, request, dataSet);
						if (cupnInterpark != null && cupnInterpark.isNext()) {
							cupnInterpark.first();
							cupnInterpark.next();
							if(cupnInterpark.getString("RESULT").equals("00")){
								cupn = cupnInterpark.getString("CUPN");
								debug("쿠폰번호 >>>>>>>>>>>>>>>>>>>>>>>>>" + cupn);
							}else if(cupnInterpark.getString("RESULT").equals("01")){
								request.getSession().removeAttribute("isInterpark");
								request.setAttribute("script", script);
								request.setAttribute("returnUrl", returnUrlTrue);
								return super.getActionResponse(context, subpage_key);
							}
						}
						dataSet.setString("email"	, email_id);
						dataSet.setString("socid"	, usrEntity.getSocid());
						dataSet.setString("cupn"	, cupn);
						dataSet.setString("userNm"	, userNm);
						dataSet.setString("ea_info"	, "");

						cnt = (int)inter.getDplCheck(context, request, dataSet);

						//Thread.sleep(5000);

						if(cnt == 0){
							debug("TM 이벤트정보 인서트 or 업데이트");
							doUpdate = (boolean) inter.insertCupnNumber(context, request, dataSet);
						}
					}	// synchronized

					if(cnt == 0){

						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String emailTitle = "";
						String emailFileNm = "";
						String useYN = "N";

						if(doUpdate == true){	
							request.getSession().setAttribute("isTm","Y");
							emailTitle = "TM가입기념 이벤트 할인쿠폰";
							emailFileNm = "/email_interpark.html";	
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							emailEtt.setTo(email_id);
							sender.send(emailEtt);
						}
					}				 
				} //이벤트 쿠폰발급 끝...
				*/
				
				
				debug("isTM>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + request.getSession().getAttribute("isTm"));
				
				// TM회원 영화예매권 교부 이벤트 처리 시작 
				dataSet.setString("tm_evt_no", "119");
				dataSet.setString("userSocid", userSocid);
				
				// 1) 메일을 클릭하고 들어왔을 경우 세션 체크
				String currDate  = DateUtil.currdate("yyyyMMdd");
				String isTmMovie = (String)request.getSession().getAttribute("isTmMovie");
				debug("GolfmemInsActn:::isTmMovie : " + isTmMovie + " / userSocid : " + userSocid);
				if(isTmMovie == null){
					isTmMovie = "N";
				}
				
				if(isTmMovie.equals("Y") ){
					
					if(join_chnl.equals("0103")){
						// 카젠 TM SK 주유권
						script = " window.open('GolfEvntTmSKInfoPop.do','tmInfo', 'width=539, height=294'); " + script;
						request.getSession().removeAttribute("isTmMovie");
				
					}else{
						// 예매권 처리 시작
						GolfEvntTmMovieProc proc_tmMovie = (GolfEvntTmMovieProc)context.getProc("GolfEvntTmMovieProc");
						
						// 2) TM회원인지 확인
						String tmMovieTmCheck = (String) proc_tmMovie.isTmCheck(context, request, dataSet);		
						debug("GolfmemInsActn:::tmMovieTmCheck : " + tmMovieTmCheck );		
						
						// 2010 07 까지 회원은 영화예매권 08부터는 주유권으로 발급한다.
						if(!GolfUtil.empty(tmMovieTmCheck)){
						
							if(tmMovieTmCheck.equals("SK")){
								
								// 카젠 TM SK 주유권
								script = " window.open('GolfEvntTmSKInfoPop.do','tmInfo', 'width=539, height=294'); " + script;
								request.getSession().removeAttribute("isTmMovie");
								
							}else{
							
								// 3) 이벤트 기간 체크
								String from_date = "";
								String to_date   = "";
								
								DbTaoResult tmMovieDateCheck = (DbTaoResult) proc_tmMovie.eventDateCheck(context, request, dataSet);
								
								if (tmMovieDateCheck != null && tmMovieDateCheck.isNext()) {
									tmMovieDateCheck.first(); 
									tmMovieDateCheck.next();
									if(tmMovieDateCheck.getString("RESULT").equals("00")){
										from_date = tmMovieDateCheck.getString("FROM_DATE");
										to_date = tmMovieDateCheck.getString("TO_DATE");
										debug("GolfmemInsActn:::from_date ~ to_date >>>>>>>>>>>>" + from_date + "~" + to_date + ",현재날짜 : "+ currDate);
									}
								}
						
								if((Integer.parseInt(from_date) <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt(to_date))){
									
									// 4) 쿠폰600(가변)개 소진시 이벤트 진행 하지 않음 
									String tmEvtCntYn = (String) proc_tmMovie.eventCountYn(context, request, dataSet);		
									debug("GolfmemInsActn:::tmEvtCntYn : " + tmEvtCntYn );			
									if(tmEvtCntYn.equals("Y")){
										
										// 5) 영화예매권 지급여부 확인 - 1건 이상일경우
										int useEvtCpnCnt = (int) proc_tmMovie.useEvtCpnCnt(context, request, dataSet);	
										debug("GolfmemInsActn:::useEvtCpnCnt : " + useEvtCpnCnt );			
										
										if(useEvtCpnCnt>0){
											// 5-1) 1건이상이면  인증번호 4개 다시 보여줌
											script = " window.open('GolfEvntTmMovieCpnPop.do','tmCoupon', 'width=474, height=202'); " + script;
											
										}else{
											// 6) 모든조건을 충족했을 경우 이벤트 안내 팝업 띄움
											script = " window.open('GolfEvntTmMovieInfoPop.do','tmInfo', 'width=539, height=695'); " + script;
										}											
										
										request.getSession().removeAttribute("isTmMovie");
						
									} else {	// 4) 쿠폰600(가변)개 소진시 이벤트 진행 하지 않음  
										request.getSession().removeAttribute("isTmMovie");
									}
						
								} else {	// 3) 이벤트 기간 체크
									request.getSession().removeAttribute("isTmMovie");
								}
							}// 가입월에 따른 이벤트 구분 종료
							
							
							
						} else {	// 2) TM회원인지 확인
							request.getSession().removeAttribute("isTmMovie");
						}
					}	// 1) 메일을 클릭하고 들어왔을 경우 세션 체크
					// TM회원 영화예매권 교부 이벤트 처리 종료
				}

				debug("GolfmemInsActn:::script : " + script + " / returnUrlTrue : " + returnUrlTrue );	
				request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);
				//request.setAttribute("resultMsg", "등록이 정상적으로 처리 되었습니다.");      	
				
	        } else {

				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }	
			
			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));
			paramMap.put("join_chnl", join_chnl);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			request.setAttribute("join_chnl", join_chnl);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
