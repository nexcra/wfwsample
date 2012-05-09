/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsActn
*   작성자    : 미디어포스 임은혜
*   내용      : 가입 > 등록
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.File;
import java.io.IOException;
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
import com.bccard.golf.dbtao.proc.member.GolfMemDelDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;

import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemDelActn extends GolfActn{
	
	public static final String TITLE = "가입 > 해지";

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
		String userMobile = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		
		String one_month_later = "Y";	// 유료회원 이후 한달 경과 여부
		String payWay = "yr"; // mn: 월회비
		int money_cnt = 0;	// 사용건수 
		int payCancel = 0;	// 전문취소 결과
		int memCancelResult = 0;	// 회원 해지 쿼리 취소결과
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade = (int)usrEntity.getIntMemGrade(); 
				userMobile1 	= (String)usrEntity.getMobile1();
				userMobile2 	= (String)usrEntity.getMobile2();
				userMobile3 	= (String)usrEntity.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// 04.실제 테이블(Proc) 조회
			GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");		// 승인취소 프로세스
			GolfMemDelDaoProc proc = (GolfMemDelDaoProc)context.getProc("GolfMemDelDaoProc");			// 회원탈퇴 프로세스
			

			if(intMemGrade==4){
				memCancelResult = proc.execute(context, dataSet, request);
				payCancel = 1;
				debug("=====GolfMemDelActn===== 무료회원 탈퇴  => 탈퇴처리 : " + memCancelResult);
			}else{
				DbTaoResult periodResult = proc.execute_period(context, dataSet, request);

				if (periodResult != null && periodResult.isNext()) {
					periodResult.first();
					periodResult.next();
					
					if(periodResult.getString("RESULT").equals("00")){
						one_month_later = (String) periodResult.getString("ONE_MONTH_LATER");
						payWay = (String) periodResult.getString("payWay");
						debug("=====GolfMemDelActn===== one_month_later : " + one_month_later + " / payWay : " + payWay);
												
						if(one_month_later.equals("N") && "yr".equals(payWay)){
							
							// 사용내역 가져오기
							DbTaoResult moneyCntResult = proc.execute_money_cnt(context, dataSet, request);
							payCancel = 1;
							
							if (moneyCntResult != null && moneyCntResult.isNext()) {
								moneyCntResult.first();
								moneyCntResult.next();

								if(moneyCntResult.getString("RESULT").equals("00")){
									money_cnt = (int) moneyCntResult.getInt("MONEY_CNT");
									
									if(money_cnt==0){										
										payCancel = proc.execute_payCancel(context, dataSet, request);
										debug("=====GolfMemDelActn===== 사용 내역이 없는 경우 시작 취소 전문 결과 : " + payCancel);
										
										if(payCancel>0){
											memCancelResult = proc.execute(context, dataSet, request);
											debug("=====GolfMemDelActn===== 사용 내역이 없는 경우 탈퇴 결과 : " + memCancelResult);
										}
									}else{
										memCancelResult = proc.execute(context, dataSet, request);
										payCancel = 1;
										debug("=====GolfMemDelActn===== 사용 내역이 있는경우 탈퇴 결과 : " + memCancelResult);
									}
								}
								
							}
							
						}else{
							dataSet.setString("payWay", payWay);
							memCancelResult = proc.execute(context, dataSet, request);
							payCancel = 1;	
							debug("=====GolfMemDelActn== 한달 지난 유료회원 탈퇴결과 : " + memCancelResult);						
						}
					}
				}
				
			}
			
			
			String returnUrlTrue = "";
			String returnUrlFalse = "GolfMemCcForm.do";
			String script = "";
			String emailTitle = "";
			String emailFileNm = "";
			
	        if(intMemGrade==4){
	        	returnUrlTrue = "GolfMemCcGeneralEnd.do";
				emailFileNm = "/email_tpl20.html";
	        }else{
	        	returnUrlTrue = "GolfMemCcChargeEnd.do";
				emailFileNm = "/email_tpl21.html";
	        }

			debug("GolfMemDelActn // payCancel(결제삭제결과) : " + payCancel + " / memCancelResult(회원탈퇴 처리결과) : " + memCancelResult);
	        
			if (payCancel>0 && memCancelResult>0) {

				
				// 세션 날리기
				usrEntity.setMemGrade("");
				usrEntity.setIntMemGrade(0);
				usrEntity.setCyberMoney(0);

				if (!email_id.equals("")) {

					String emailAdmin = "\"비씨카드\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailTitle = "[Golf Loun.G] 골프라운지 서비스가 해지되었습니다.";
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm);
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setTo(email_id);
					//sender.send(emailEtt);
				}

				request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);
				//request.setAttribute("resultMsg", "해지가 처리 되었습니다.");      
	       
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "해지가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 처리되지 않을 경우 관리자에 문의하십시오.");		        		
	        }	
			
			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(memCancelResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
