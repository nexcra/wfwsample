/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemVipCardInsActn
*   작성자    : 미디어포스 권영만
*   내용      : 가입 > VIP카드 등록처리
*   적용범위  : golf 
*   작성일자  : 2010-09-14
************************* 수정이력 ***************************************************************** 
*    일자       작성자      변경사항
* 2011.02.15    이경희	   ISP인증기록 추가
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
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemVipCardInsActn extends GolfActn{
	
	public static final String TITLE = "가입 > VIP카드 등록처리";
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
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // 검증결과 코드 (1: 정상주문완료   3:주문오류시)	
		String ispCardNo   	= "";// isp카드번호
		String ip = request.getRemoteAddr();
		String memSocid = "";
		
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
				memSocid = (String)usrEntity.getSocid();			//-  주민등록번호			
				
			}			
			String strResultCode	= "99";
						
			// 02.입력값 조회		
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			RequestParser parser 	= context.getRequestParser("default",request,response); 
			
			String insMode 			= StrUtil.isNull(parser.getParameter("insMode"), "");
			//String accede 			= StrUtil.isNull(parser.getParameter("accede"), "N");
			String vipCardPayAmt	= "";
			String cardSel			= StrUtil.isNull(parser.getParameter("cardSel"), "");
			
			double sum_money = 0;
			//double select_sum_money = 0;
			//double temp_sum_money = 0;
			//String select_card_no = "";
			boolean payResult = false;			 
			String toDate  = DateUtil.currdate("yyyyMMdd");	
			String fromDate =  DateUtil.dateAdd('M', -3, toDate, "yyyyMMdd");
			fromDate = fromDate.substring(0,6) + "01";
						
			GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			
			GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD");		// 가맹점 번호(766559864) //topn : 745300778
			
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("CSTMR_ID", userId);
			
			if("vipCard".equals(insMode))
			{
					
					//VIP 해당 카드로 승인 결제 시작								
					//List cardVipList 		= mbr.getCardVipInfoList();
					String vipCardNo		= cardSel;
					String vipCardExpDate	= "";
					//String vipCardExpDate	= StrUtil.isNull(mbr.getVipCardExpDate(), "");					
					String insTerm 			= "00";	//일시불
					System.out.print("## GolfMemVipCardInsActn VIP카드 : userId : "+userId+" | vipCardNo : "+vipCardNo+"\n");
					
					
					if("".equals(vipCardNo) )
					{
						request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
						request.setAttribute("resultMsg", "VIP카드가 없습니다.\\n\\n반복적으로 결제되지 않을 경우 관리자에 문의하십시오.");
						debug("VIP카드가 없음");
						strResultCode = "77";
					}
					else
					{
						// SBS골프멤버쉽 회원으로 5천원 공제받은 회원은 2만원 승인 
						// 자료가 있다면 5천원 할인 제외 => 2만원 승인(실적체크 OK 되더라도 2만원 승인)
						// 자료가 없다면 기존 로직 처리 (1만5천원 승인, 실적체크에 따른 승인)
						int resultCk = 0;

						dataSet.setString("socid", usrEntity.getSocid());	
						GolfMemCardInsDaoProc proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
						
						try{
							resultCk = proc.sbsMemberCk(context, dataSet, request);
						}catch(Throwable t){}
						
						String checkCardYn = "N";
						//결제할 카드가 VIP카드인지 다시 한번 확인
						List cardVipList = mbr.getCardVipInfoList();
						if( cardVipList!=null && cardVipList.size() > 0 )
						{
							for (int i = 0; i < cardVipList.size(); i++) 
							{								
								sum_money = 0;
								int usedMoney = 0;
								
								try { 
									
									CardVipInfoEtt record = (CardVipInfoEtt)cardVipList.get(i);
									String cardNo 		= StrUtil.isNull((String)record.getCardNo(), ""); 
									String grade 		= (String)record.getVipGrade();
									String expDate 		= (String)record.getExpDate();
									String cardType 	= (String)record.getCardType();
									String last_cardApp = (String)record.getCardAppType();
									String last_cardNo 	= (String)record.getLastCardNo();									
									String reg_date 	= StrUtil.isNull((String)record.getAppDate(), "");
									String cardJoinDate	= StrUtil.isNull((String)record.getCardJoinDate(), "");
									
									System.out.print("## GolfMemVipCardInsActn VIP카드 비교 확인 : userId : "+userId+" | vipCardNo : "+vipCardNo+" | cardNo : "+cardNo+" | grade : "+grade+"\n");
									if(vipCardNo.equals(cardNo))
									{			
										debug("## VIP카드 맞음");
										
										
										//해당 카드의 결제금액 구하기
										//실적체크
										/*
										CardAppType
										11:신규, 12:추가신규, 21:훼손재발급, 22:등급변경재발급, 
										24:분실재발급, 25:등급변경분실재발급, 31:일반갱신, 32:등급변경일반갱신, 
										33:자동갱신, 34:등급변경자동갱신, 35:조기갱신, 36:등급변경조기갱신, 
										37:지연갱신, 38:등급변경지연갱신, 41:제신고
										CardType
										1:본인,2:가족,3:지정,4:공용
										*/
										
										
										// PT카드가  03,12,30,91 고객일경우 1만5천원 승인
										if( "03".equals(grade) || "12".equals(grade) || "30".equals(grade) || "91".equals(grade)  )
										{
											vipCardPayAmt = "15000";
										
										}
										else
										{
											vipCardPayAmt = "20000";
										}
										
										if( "03".equals(grade) || "12".equals(grade) )
										{
											
											if("00".equals(expDate)){
												
												// 1:본인,2:가족,3:지정,4:공용
												if("1".equals(cardType) || "3".equals(cardType))
												{
													sum_money = getSumMoney(cardNo,cardType,sum_money,context,request,response);
													usedMoney = (int) sum_money;
													debug("## GolfMemVipCardInsActn | 현재 카드번호 : "+cardNo+" | 실적금액 : "+usedMoney);
													
													if(last_cardApp.equals("21") || last_cardApp.equals("24") || last_cardApp.equals("31") || last_cardApp.equals("33") || last_cardApp.equals("35") || last_cardApp.equals("37")){
														sum_money = getSumMoney(last_cardNo,cardType,sum_money,context,request,response);
														usedMoney = (int) sum_money;
														debug("## GolfMemVipCardInsActn | 이전 카드번호 : "+last_cardNo+" | 실적금액 : "+usedMoney);
													}
													
												}
												
												
											}
											
											//카드가 발급날짜가 3개월 이내일 경우 체크
											String ckDate =  DateUtil.dateAdd('M', 3, cardJoinDate, "yyyyMMdd");											
											debug("## GolfMemVipCardInsActn | 카드발급날짜 비교 | 오늘날짜 : "+toDate+" | 카드발급날짜 : "+cardJoinDate+ " | 카드3개월비교날짜 : "+ckDate);
											
											if( Integer.parseInt(toDate) < Integer.parseInt(ckDate) )
											{
												debug("## GolfMemVipCardInsActn | 3개월이내 발급카드입니다. last_cardApp : "+last_cardApp+" | 카드발급 3개월 이내 등");
												sum_money = 10000000; //신규면 실적 천만원 넣어 우선부여.....
												usedMoney = (int) sum_money;
											}
											/*
											
											if(!(Integer.parseInt(fromDate) <= Integer.parseInt(reg_date))){
			
											} else {
												//단, 30만원 미만인경우 최근 최초 카드발급일자로 3개월 미만 고객은 1만 5천원 승인. 3개월 경과 회원은 2만원 승인
												//단, 신규 3개월 이내에 훼손재발급, 분실재발급 등 카드를 재발급 재변경시도 실적체크에서 예외처리 한다.(1만5천원 승인 해줌)
												if (last_cardApp.equals("11") || last_cardApp.equals("12") || last_cardApp.equals("21") || last_cardApp.equals("22") || last_cardApp.equals("24") || last_cardApp.equals("25")   ) {
													debug(" last_cardApp : "+last_cardApp+" | 신규 3개월 이내에 훼손재발급, 분실재발급 등");
													sum_money = 10000000; //신규면 실적 천만원 넣어 우선부여.....
													usedMoney = (int) sum_money;
												}
											}
																							
											*/
											if (usedMoney >= 300000) {
												
												vipCardPayAmt = "15000";
												
											}else if (usedMoney < 300000) {
												
												vipCardPayAmt = "20000";
												
											}
										
										}
																		
										
										//SBS회원일경우 무조건 2만원 
										if(resultCk>0)
										{
											vipCardPayAmt = "20000";
										}
										
										
																																							
									
										
									}
									
									
								System.out.print("## GolfMemVipCardInsActn 현재VIP카드: userId : "+userId+" | cardNo : "+cardNo+" | vipCardPayAmt : "+vipCardPayAmt+"\n");
								
								} catch(Throwable t) {checkCardYn = "N";}
								
							
							}
						}
						if(!"".equals(vipCardPayAmt))
						{
							checkCardYn = "Y";
						}
						
						
						// ISP인증 체크
						String iniplug 		= parser.getParameter("KVPpluginData", "");					// ISP 인증값
						HashMap kvpMap 		= null;
						String pcg         	= "";														// 개인/법인 구분						
						String valdlim	   	= "";														// 만료 일자
						String pid 			= null;														// 개인아이디						
						String assName 		= "";														// 기관이름						
						
						String host_ip 		= java.net.InetAddress.getLocalHost().getHostAddress();
						
						if(iniplug !=null && !"".equals(iniplug)) {
							kvpMap = payProc.getKvpParameter( iniplug );
						}	
						
						if(kvpMap != null) {
							ispAccessYn = "Y";
							pcg         = (String)kvpMap.get("PersonCorpGubun");		// 개인/법인 구분
							ispCardNo   = (String)kvpMap.get("CardNo");					// isp카드번호
							assName		= (String)kvpMap.get("AssociationName");		// 기관이름
							valdlim		= (String)kvpMap.get("CardExpire");				// 만료 일자
							if ( "2".equals(pcg) ) {
								pid = (String)kvpMap.get("BizId");								// 사업자번호
							} else {
								pid = (String)kvpMap.get("Pid");									// 개인 주민번호
							}
						} else {
							ispCardNo = parser.getParameter("isp_card_no","");	// 하나비자카드 경우
						}
						
						if ( valdlim.length() == 6 ) {
							valdlim = valdlim.substring(2);											
						}
						System.out.print("## GolfMemVipCardInsActn ISP인증정보 : userId : "+userId+" | ispCardNo : "+ispCardNo+"\n");
						
						debug("## VIP카드 비교 | vipCardNo : "+vipCardNo+" = ispCardNo : "+ispCardNo);
						
						if(vipCardNo.equals(ispCardNo))
						{
							checkCardYn = "Y";
						}
						else
						{
							checkCardYn = "N";
						}
						
						 
						
						System.out.print("## GolfMemVipCardInsActn VIP카드 : userId : "+userId+" | 결제금액 : "+vipCardPayAmt+" | checkCardYn : "+checkCardYn+" | vipCardNo : "+vipCardNo+"\n");
									
						
						if("Y".equals(checkCardYn))
						{
							//카드유효기간 가져오기
							try { 
								JoltInput input = null;
								JtProcess jt = new JtProcess();
								
								input = new JoltInput(JoltServiceName);
								input.setString("fml_trcode", TSN025); 
								input.setString("fml_arg1", "3");				//1:주민번호 2:사업자번호 3: 카드번호
								input.setString("fml_arg2",vipCardNo);
								input.setString("fml_arg3", "");
								
								java.util.Properties prop = new java.util.Properties();
								prop.setProperty("RETURN_CODE", "fml_ret1");
								String ret1 = "";
								do {
									TaoResult result = jt.call(context, request, input, prop);
									vipCardExpDate = result.getString("fml_ret6").trim(); //유효기간
									
									
								} while ( "01".equals(ret1) );
							} catch(Throwable t) {}
							if(!"".equals(vipCardExpDate)) vipCardExpDate = vipCardExpDate.substring(2,6);
							
							System.out.print("## GolfMemVipCardInsActn VIP카드 : userId : "+userId+" | vipCardNo : "+vipCardNo+" | vipCardExpDate : "+vipCardExpDate+"\n");							
							
							//결제 정보 넣기
							payEtt.setMerMgmtNo(merMgmtNo);
							payEtt.setCardNo(vipCardNo);
							payEtt.setValid(vipCardExpDate);			
							payEtt.setAmount(vipCardPayAmt);
							payEtt.setInsTerm(insTerm);
							payEtt.setRemoteAddr(ip);
							
							debug("## 결제처리 시작");
							
							//결제처리 연동	
							try { 
								payResult = payProc.executePayAuth(context, request, payEtt);
							} catch(Throwable t) {}
							debug("## GolfMemVipCardInsActn 결제처리 결과 payResult : "+payResult);
							
							
							
							if (payResult) 
							{
								addResult = 1;
								
								// 결제 정보 테이블에 INSERT 
								String sttl_mthd_clss = "";
								if (insTerm.equals("00")) sttl_mthd_clss="0001";
								else sttl_mthd_clss="0002";
								
								dataSet.setString("CDHD_ID", userId);
								dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
								dataSet.setString("STTL_GDS_CLSS", "0003");
								dataSet.setString("STTL_STAT_CLSS", "N");
								dataSet.setString("STTL_AMT", vipCardPayAmt);
								dataSet.setString("MER_NO", merMgmtNo);
								dataSet.setString("CARD_NO", vipCardNo);
								dataSet.setString("VALD_DATE", vipCardExpDate);
								dataSet.setString("INS_MCNT", insTerm.toString());
								dataSet.setString("AUTH_NO", payEtt.getUseNo());
								dataSet.setString("STTL_GDS_SEQ_NO", "");
								dataSet.setString("STTL_MINS_NM", "BCPT카드");
								
								if (addResult == 1) {
									// 결제 저장 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
									try {
										addResult = addResult + addPayProc.execute(context, dataSet);
									} catch(Throwable t) {}
								}
								debug("## GolfMemVipCardInsActn 결제 정보 저장 결과 addResult : "+addResult);
								
								boolean payCancelResult = false;
								if (addResult == 2) //결제저장 성공시
								{								
									
									int addMemResult = 0;
									GolfMemCardInsDaoProc mem_proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
									dataSet.setString("strCode", "0003");	// Gold등급으로 입력	
									try {
										debug("## GolfMemVipCardInsActn | 회원정보 DB 입력 처리 시작");
										addMemResult = mem_proc.vipCardMemIns(context, dataSet, request);	
										debug("## GolfMemVipCardInsActn | 회원정보 DB 입력 처리 결과 addMemResult : "+addMemResult);
									} catch(Throwable t) {}
									
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
										
										
										debug("## email_id : "+email_id);
										
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
												sender.send(emailEtt);  
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
											String message = "골프라운지 Gold회원으로 정상적으로 가입되셨습니다. 감사합니다."; 
							 
											SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
											String smsRtn = smsProc.send(smsClss, smsMap, message);
										} catch(Throwable t) {}
										
										request.setAttribute("returnUrl", "GolfMemVipCardEnd.do");
										//request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp'");		
										request.setAttribute("resultMsg", vipCardPayAmt+"원 정상결제 되었습니다.");	
										debug("결제정보 저장 성공");
										strResultCode = "11";
										
									}
									else
									{
										try { 
											payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
										} catch(Throwable t) {}
										request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
										request.setAttribute("resultMsg", "회원가입이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 가입되지 않을 경우 관리자에 문의하십시오.");
										debug("회원가입 DB처리 실패");
										strResultCode = "44";
										veriResCode = "3";
									}
								
								} else if (addResult == 9) { //한번 더 체크함
						        	// DB저장 실패시 승인취소 전문	   
									try { 
										payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
									} catch(Throwable t) {}
									request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
									request.setAttribute("resultMsg", "이미 신청하셨습니다.");
									debug("결제정보 저장 실패");
									strResultCode = "33";
									veriResCode = "3";
						        } else {
						        	// DB저장 실패시 승인취소 전문
						        	try { 
						        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
						        	} catch(Throwable t) {}
									request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
									request.setAttribute("resultMsg", "VIP카드결제가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");
									debug("결제정보 저장 실패");
									strResultCode = "33";
									veriResCode = "3";
						        }	
								
								debug("## GolfMemVipCardInsActn 결제 정보 저장 결과 payCancelResult : "+payCancelResult);
							
							}
							else
							{
								request.setAttribute("script", "");
								request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
								request.setAttribute("resultMsg", "BC VIP 카드결제가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 결제되지 않을 경우 관리자에 문의하십시오.");
								debug("결제 실패");
								strResultCode = "22";
								veriResCode = "3";
								
								// 결제실패시 내역 저장 	
								try {
								int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);				
								debug("결제실패내역저장결과  result_fail : " + result_fail);
								} catch(Throwable t) {}								
								
							}
						
						}
						else
						{
							request.setAttribute("script", "");
							request.setAttribute("returnUrl", "GolfMemVipCardJoin.do");
							request.setAttribute("resultMsg", "현재 고객님이 결제하시는 카드 정보는  "+ assName + " " 
									+ ispCardNo.substring(0, 4)+"-"+ispCardNo.substring(4, 8)+"-****-"+ispCardNo.substring(12, 16)+"입니다.\\n\\n다시 확인하여 주십시요.");
							
							debug("결제 실패 33");
							strResultCode = "22";
							veriResCode = "3";
						}
						
						
					}
									
				
			}
			
			System.out.print("## GolfMemVipCardInsActn VIP카드 등록처리 : userId : "+userId+" | insMode : "+insMode+" | strResultCode : "+strResultCode+" \n");
			
			// 05. Return 값 세팅				
			paramMap.put("editResult", String.valueOf(addResult));	
	        request.setAttribute("paramMap", ""); 
	        request.setAttribute("strResultCode", strResultCode);
	        
		} catch(Throwable t) {
			veriResCode = "3";
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
						
			if(ispAccessYn.equals("Y")){
				
				//ISP인증 로그 기록
				HashMap hmap = new HashMap();
				hmap.put("ispAccessYn", ispAccessYn);
				hmap.put("veriResCode", veriResCode);
				hmap.put("title", TITLE);
				hmap.put("memName", userNm);
				hmap.put("memSocid", memSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", ip);
				hmap.put("className", "GolfMemVipCardInsActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}

		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
	
	
	/***********************************************************************
	 * 실적체크 로직
	 **********************************************************************/
	public double getSumMoney(String cardNo, String cardType, double sum,WaContext context, HttpServletRequest request, HttpServletResponse response)
		throws ServletException, IOException, BaseException{

		
		JtProcess process = new JtProcess();
		String joltServiceName = "BSNINPT";
		String toDay  = DateUtil.currdate("yyyyMMdd");
		
		String toDate  = DateUtil.dateAdd('M', -1, toDay, "yyyyMMdd");
		int datcount = DateUtil.getMonthlyDayCount(
						Integer.parseInt(toDate.substring(0,4)),
						Integer.parseInt(toDate.substring(4,6))); // 해당월의 말일
		toDate = toDate.substring(0,6) + Integer.toString(datcount);
		debug("toDate : " + toDate);
		String fromDate =  DateUtil.dateAdd('M', -3, toDay, "yyyyMMdd");
		fromDate = fromDate.substring(0,6) + "01";		

		if(cardType.equals("1") || cardType.equals("3")){  //1:본인 3:법인지정
			// 2008-10-13 수정
			JoltInput entity = new JoltInput(joltServiceName);					
			entity.setServiceName(joltServiceName);
			entity.setString("fml_trcode", "MGA0100R1600");

			TaoResult jout = null;

			entity.setString("fml_arg1", cardNo);		// 번호: 개인:주민번호/기업:회원사회원번호/기업:카드번호				
			entity.setString("fml_arg2", "3");			// 개인,기업구분: '1':주민번호(개인),'2':회원사회원번호(기업)'3':카드번호(기업)
			entity.setString("fml_arg3", fromDate);		// 이용조회일_FROM, YYYYMMDD(기업한달)
			entity.setString("fml_arg4", toDate);		// 이용조회일_TO, YYYYMMDD(기업한달)
			entity.setString("fml_arg5", " ");			// ISP구분(1.ISP, 나머지 : SPACE)
			entity.setString("fml_arg7", "1");			// 이전카드실적포함

			jout = process.call(context, request, entity);

			String rescode = jout.getString("fml_ret1");
			
				if ("0".equals(rescode)) {
					sum = sum + jout.getDouble("fml_retd3") + jout.getDouble("fml_retd5");
					debug("sum_money### >> " + sum);
			} 
		}
		return sum;
	}
}
