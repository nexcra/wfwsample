/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfMemDelOutActn
*   작성자	: 미디어포스 
*   내용		: 서비스 해지 > 비씨 패밀리 사이트 회원탈퇴시 골프라운지 자동탈퇴 연동
*   적용범위	: golf 
*   작성일자	: 2009-11-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

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
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.StringEncrypter;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemDelDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemDelFormDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemDelOutActn extends GolfActn{
	
	public static final String TITLE = "서비스 해지 > 비씨 패밀리 사이트 회원 탈퇴시 골프라운지 자동탈퇴 연동";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default"; 
		String userId = "";
		String strMemGrd = ""; 	
		String isDelAble = "Y";	// 골프라운지 해지가능여부 (Y:해지가능, N:해지불가)
		
		String one_month_later = "Y";	// 유료회원 이후 한달 경과 여부
		int money_cnt = 0;	// 사용건수 
		int payCancel = 0;	// 전문취소 결과
		int memCancelResult = 0;	// 회원 해지 쿼리 취소결과 
		
		try {
			
			// 세션정보체크하자
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			
			// 입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			userId = parser.getParameter("memAccount", "");	// 회원아이디
			
			debug("## GolfMemDelOutActn | 시작 ");
			debug("## GolfMemDelOutActn | userId 암호화된값 : " + userId);
			
			if(!(userId == null || userId.equals(""))){
							
				StringEncrypter receiver = new StringEncrypter("BCCARD", "GOLF");
				userId = receiver.decrypt(userId); 

				debug("## GolfMemDelOutActn | userId : " + userId);
				
				// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				dataSet.setString("userId", userId);	

				GolfMemDelFormDaoProc chkProc = (GolfMemDelFormDaoProc)context.getProc("GolfMemDelFormDaoProc");

				// 멤버십 등급 가져오기
				DbTaoResult memGrade = chkProc.getMemGrd(context, dataSet, request);
				if (memGrade != null && memGrade.isNext()) {
					memGrade.first();
					memGrade.next();
					strMemGrd = (String) memGrade.getString("CDHD_SQ2_CTGO");					
				}	
				else{
					isDelAble = "N";	
				}
				debug("## GolfMemDelOutActn | strMemGrd : " + strMemGrd);				

				/*
				// TM 회원 해지 불가
				DbTaoResult addResult = chkProc.execute(context, dataSet, request);			
				if (addResult != null && addResult.isNext()) {
					addResult.first();
					addResult.next();
					String join_chnl = (String) addResult.getString("JOIN_CHNL");
					
					if(  !(join_chnl.equals("0001") || join_chnl.equals("1000") || join_chnl.equals("2000")) ){
						isDelAble = "N";
						debug("## GolfMemDelOutActn | TM 회원 - 해지 불가");
					}
				}	
				
				// 골프카드회원은 해지 불가
				DbTaoResult cardUser = chkProc.getCardMem(context, dataSet, request);
				if (cardUser != null && cardUser.isNext()) {
					cardUser.first();
					cardUser.next();
					String strMemCard = (String) cardUser.getString("CDHD_SQ2_CTGO");
					
					debug("## GolfMemDelOutActn | strMemCard : "+strMemCard);
									
					if("0005".equals(strMemCard) || "0006".equals(strMemCard)){	// 골프카드회원인 경우 - 해지 불가
						isDelAble = "N";
						debug("## GolfMemDelOutActn | 골프 카드  회원 - 해지 불가");
					}
					
				}
				
				// 챔피온 회원인 경우 - 사은품 은 회원은 해지 불가
				if("0001".equals(strMemGrd)){	
					DbTaoResult addChampion = chkProc.getChamp(context, dataSet, request);
						
					if (addChampion != null && addChampion.isNext()) {
						addChampion.first();
						addChampion.next();
						String champ_seq_no = (String) addChampion.getString("SEQ_NO");
							
						if(!GolfUtil.empty(champ_seq_no) && !champ_seq_no.equals("")){
							isDelAble = "N";
							debug("## GolfMemDelOutActn | 사은품 받은 챔피온 회원 - 해지 불가");
						}
					}
				}							
				*/		
						
				// 해지 가능한 회원 - 탈퇴 처리
				if(isDelAble.equals("Y")){

					GolfMemDelDaoProc proc = (GolfMemDelDaoProc)context.getProc("GolfMemDelDaoProc");			// 회원탈퇴 프로세스

					if(strMemGrd.equals("0004")){
						debug("## GolfMemDelOutActn | 무료회원 탈퇴 => 탈퇴처리");
						memCancelResult = proc.execute(context, dataSet, request);
						payCancel = 1;
						debug("## GolfMemDelOutActn | 무료회원 탈퇴 결과 : " + memCancelResult);
					}else{
						debug("## GolfMemDelOutActn | 유료회원 시작");
						DbTaoResult periodResult = proc.execute_period(context, dataSet, request);

						if (periodResult != null && periodResult.isNext()) {
							periodResult.first();
							periodResult.next();
							
							if(periodResult.getString("RESULT").equals("00")){
								one_month_later = (String) periodResult.getString("ONE_MONTH_LATER");
								
								if(one_month_later.equals("N")){
									debug("## GolfMemDelOutActn | 한달이 지나지 않은 경우 시작");
									DbTaoResult moneyCntResult = proc.execute_money_cnt(context, dataSet, request);
									payCancel = 1;
									
									if (moneyCntResult != null && moneyCntResult.isNext()) {
										moneyCntResult.first();
										moneyCntResult.next();

										if(moneyCntResult.getString("RESULT").equals("00")){
											money_cnt = (int) moneyCntResult.getInt("MONEY_CNT");
											
											if(money_cnt==0){
												debug("## GolfMemDelOutActn | 사용 내역이 없는 경우 시작");
												
												payCancel = proc.execute_payCancel(context, dataSet, request);
												debug("## GolfMemDelOutActn | 취소 전문 결과 : " + payCancel);
												
												if(payCancel>0){
													memCancelResult = proc.execute(context, dataSet, request);
													debug("## GolfMemDelOutActn | 사용 내역이 없는 경우 탈퇴 결과 : " + memCancelResult);
												}
												
												debug("## GolfMemDelOutActn | 사용 내역이 없는 경우 종료");
											}else{
												debug("## GolfMemDelOutActn | 사용 내역이 있는경우 => 탈퇴처리");
												memCancelResult = proc.execute(context, dataSet, request);
												payCancel = 1;
												debug("## GolfMemDelOutActn | 사용 내역이 있는경우 탈퇴 결과 : " + memCancelResult);
											}
										}
										
									}	//if (moneyCntResult != null && moneyCntResult.isNext()) {
									debug("## GolfMemDelOutActn | 한달이 지나지 않은 경우 종료");
									
								}else{	//if(one_month_later.equals("N")){
									debug("## GolfMemDelOutActn | 한달이 지난 경우 => 탈퇴처리");
									memCancelResult = proc.execute(context, dataSet, request);
									payCancel = 1;
									debug("## GolfMemDelOutActn | 무료회원 탈퇴 결과 : " + memCancelResult);
									
								}	//if(one_month_later.equals("N")){
							}
						}
						
						debug("## GolfMemDelOutActn | 유료회원 종료");
					}			
					
				}
				
			}			
						
			String returnUrlTrue = "";
			String returnUrlFalse = "/app/golfloung/index.jsp";
			
	        if(strMemGrd.equals("0004")){
	        	returnUrlTrue = "GolfMemCcGeneralEnd.do";
	        }else{
	        	returnUrlTrue = "GolfMemCcChargeEnd.do";
	        }      

			debug("## GolfMemDelOutActn | payCancel : " + payCancel);
			debug("## GolfMemDelOutActn | memCancelResult : " + memCancelResult);
	        
			if (payCancel>0 && memCancelResult>0) {
				
				// 세션 날리기 
				if(usrEntity != null){
					usrEntity.setMemGrade("");
					usrEntity.setIntMemGrade(0);
					usrEntity.setCyberMoney(0);					
				}
				
				request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "해지가 처리 되었습니다.");      
	       
	        } else {
				request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "해지가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
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
