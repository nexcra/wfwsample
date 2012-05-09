/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemTopGolfCardInsActn
*   작성자    : 미디어포스 권영만
*   내용      : 가입 > 탑골프카드 등록처리
*   적용범위  : golf 
*   작성일자  : 2010-09-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemTopGolfCardInsActn extends GolfActn{
	
	public static final String TITLE = "가입 > 탑골프카드 등록처리";
	static final String JoltServiceName = "BSNINPT";
	static final String TSN025 = "MHL0260R0100";
	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";
		String userNm = ""; 
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		String memGrade = ""; 
		int intMemGrade = 0; 
		int intMemberGrade = 0; 
		int intCardGrade = 0; 
		String email_id = "";
		int addResult = 0;
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
		 	
			 if(usrEntity != null) {
				 userId		= (String)usrEntity.getAccount();
				 userNm		= (String)usrEntity.getName(); 
				 email_id 	= (String)usrEntity.getEmail1(); 
				 userMobile1 = (String)usrEntity.getMobile1();
				userMobile2 = (String)usrEntity.getMobile2();
				userMobile3 = (String)usrEntity.getMobile3();
			}			
			String strResultCode	= "99";
						
			// 02.입력값 조회		
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			RequestParser parser 	= context.getRequestParser("default",request,response); 
			
			String accede 			= StrUtil.isNull(parser.getParameter("accede"), "N");
			String topGolfCardYn	= "N";
			int addMemResult 		= 0;
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CSTMR_ID", userId);
			
				//동의여부 다시 한번 체크
				if("Y".equals(accede))
				{
					
					System.out.print("## GolfMemVipCardInsActn VIP카드 : userId : "+userId+"\n");
					
					//탑골프카드 소지여부 체크
					try {
						List topGolfCardList = mbr.getTopGolfCardInfoList();
						if( topGolfCardList!=null && topGolfCardList.size() > 0 )
						{
							for (int i = 0; i < topGolfCardList.size(); i++) 
							{
								
								topGolfCardYn = "Y";
								debug("## 탑골프카드 소지 회원");
							}
						}
						else
						{
							topGolfCardYn = "N";
							debug("## 탑골프카드 미소지");					
						}
					} catch(Throwable t) 
					{
						topGolfCardYn = "N";
						debug("## 탑골프카드 체크 에러");	
					}
					
					
					if("Y".equals(topGolfCardYn))
					{
						dataSet.setString("strCode", "0016");	// Gold등급으로 입력
						dataSet.setString("joinMode", "acrgJoin");
						
						GolfMemCardInsDaoProc mem_proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
						debug("## GolfMemTopGolfCardInsActn | 회원정보 DB 입력 처리 시작");
						addMemResult = mem_proc.execute(context, dataSet, request);	
						debug("## GolfMemTopGolfCardInsActn | 회원정보 DB 입력 처리 결과 addMemResult : "+addMemResult);
																		
						if (addMemResult == 1) {
							try {
								DbTaoResult tmView = mem_proc.cardExecute(context, dataSet, request);

								if (tmView != null && tmView.isNext()) {
									tmView.first();
									tmView.next();
									memGrade = (String) tmView.getString("memGrade");	
									intMemGrade = (int) tmView.getInt("intMemGrade");	
									intMemberGrade = (int) tmView.getInt("intMemberGrade");	
									intCardGrade = (int) tmView.getInt("intCardGrade");	
									
								}
							} catch(Throwable t) {}
							usrEntity.setMemGrade(memGrade);				//등급명
							usrEntity.setIntMemGrade(intMemGrade);		//공통등급
							usrEntity.setIntMemberGrade(intMemberGrade);	//멤버쉽등급처리
							usrEntity.setIntCardGrade(intCardGrade);		//카드등급처리
							usrEntity.setCyberMoney(0);
							
							try { 
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
							} catch(Throwable t) {}
							
							// 문자 보내기
							try { 
								HashMap smsMap = new HashMap();
								smsMap.put("ip", request.getRemoteAddr());
								smsMap.put("sName", userNm);
								smsMap.put("sPhone1", userMobile1);
								smsMap.put("sPhone2", userMobile2);
								smsMap.put("sPhone3", userMobile3);
							        
								String smsClss = "674";
								String message = "골프라운지 탑골프회원으로 정상적으로 가입되셨습니다. 감사합니다."; 
				 
								SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
								String smsRtn = smsProc.send(smsClss, smsMap, message);
							} catch(Throwable t) {}
							
							request.setAttribute("returnUrl", "");
							request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp'");								
							request.setAttribute("resultMsg", "회원가입이 정상 처리되었습니다.");	
							debug("결제정보 저장 성공");
							strResultCode = "11";
							
						}
						else
						{
							
							request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
							request.setAttribute("resultMsg", "회원가입이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 가입되지 않을 경우 관리자에 문의하십시오.");
							debug("회원가입 DB처리 실패");
							strResultCode = "44";
						}
						
					}
					else
					{
						request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
						request.setAttribute("resultMsg", "탑골프 카드가 없습니다.\\n\\n반복적으로 가입되지 않을 경우 관리자에 문의하십시오.");
						debug("VIP카드가 없음");
						strResultCode = "77";
					}
					
					
				
						
						
					
					
					
					
					
					
					
					
					
					
					
					
					
					
				}
				else
				{
					request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
					request.setAttribute("resultMsg", "약관을 동의해주세요.\\n\\n반복적으로 가입되지 않을 경우 관리자에 문의하십시오.");
					debug("동의 여부가 확인되지 않음");
					strResultCode = "88";
				}
				
				
				
			
			
			
			
			System.out.print("## GolfMemTopGolfCardInsActn 탑골프카드 등록처리 : userId : "+userId+" | strResultCode : "+strResultCode+" \n");
			
			
			
			// 05. Return 값 세팅				
			paramMap.put("editResult", String.valueOf(addResult));	
	        request.setAttribute("paramMap", paramMap); 
	        request.setAttribute("strResultCode", strResultCode);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
	
}
