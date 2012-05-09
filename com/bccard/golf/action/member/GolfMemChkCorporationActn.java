/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemChkCorporationActn
*   작성자     : 미디어포스 임은혜
*   내용        : 회원 > 정회원 전환 > 폼
*   적용범위  : golf 
*   작성일자  : 2009-07-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.StringEncrypter;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMemChkCorporationActn extends GolfActn { 
	
	private static final String BSNINPT = "BSNINPT";					// 프레임웍 조회서비스
	public static final String TITLE = "법인플랫폼 통해서 골프라운지 입장 - 회원가입시 20% 할인"; 

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		Connection con = null;
		try {
			Map paramMap = BaseAction.getParamToMap(request);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			
			String str_msg_enc = parser.getParameter("message", "");
			String str_msg = "";
			String strType = "";	// 전송구분값 (Y:로그인, N:탈퇴)
			String strMemId = "";
			String strMemAccount = "";
			String peCk = "";
			
			debug("## GolfMemChkCorporationActn | str_msg_enc:"+str_msg_enc+"\n");
			
			if(!("".equals(str_msg_enc) || str_msg_enc == null)){

//				StringEncrypter sender = new StringEncrypter("GOLF", "TEST");
//				str_msg_enc = sender.encrypt(str_msg_enc);
				
//				debug("## GolfMemChkCorporationActn | str_msg_enc_enc:"+str_msg_enc+"\n");

				/*받는 쪽*/
				StringEncrypter receiver = new StringEncrypter("BCCARD", "GOLF");
				str_msg = receiver.decrypt(str_msg_enc);
				debug("## GolfMemChkCorporationActn | str_msg:"+str_msg+"\n");

				// 전송구분값 추출
				strType = getSubString(str_msg,0,1).trim();
				
				// mem_id값(숫자) 추출
				strMemId = getSubString(str_msg,58,8).trim();
				
				// account값(아이디) 추출  
				strMemAccount = getSubString(str_msg,66,40).trim();	

				debug("## GolfMemChkCorporationActn | strType:" + strType + " | strMemId:" + strMemId + " | strMemAccount:" + strMemAccount + "\n");
				
			}
			
			System.out.print("## GolfMemChkCorporationActn | strType : " + strType + " | strMemId : "+strMemId+" | strMemAccount : "+strMemAccount+"\n");						
			 
			UcusrinfoEntity ucusrinfo = null;			
			con = context.getDbConnection("default", null);
			
			// mem_id값 있는 경우
			if(!("".equals(strMemId) || strMemId == null)){
				
				UcusrinfoDaoProc proc = (UcusrinfoDaoProc)context.getProc("UcusrinfoDao");
				
				
				ucusrinfo= proc.selectByAccountCot( con, strMemId);	// 법인회원인지 검색	
				
				if(!("".equals(strMemAccount) || strMemAccount == null)){
					peCk = proc.selectPeByCkNum( con, strMemAccount);	// 골프회원인지 검색
				}
				
			}
			
			// 로그인 
			if (strType.equals("Y")){

				// 법인 회원인 경우
				if (ucusrinfo != null) {	
					
					// 법인 회원인 경우 세션 생성
					HttpSession session = request.getSession(true); 
					UcusrinfoEntity usrEntity = (UcusrinfoEntity)session.getAttribute("COEVNT_ENTITY"); // 기본정보  | COEVNT_ENTITY 세션명을 추가했음
					 			
					// 로그인을 안 한 유저인경우 usrEntity가 null값이므로 객체 생성해줘야됨. 5566268
					if(usrEntity == null){	
						usrEntity = new UcusrinfoEntity(); 
						System.out.print("## GolfMemChkCorporationActn | usrEntity null --> 생성 작업 \n");
					}
					  
					usrEntity.setStrEnterCorporation("Y");
					usrEntity.setStrEnterCorporationMemId(strMemId);
					usrEntity.setStrEnterCorporationAccountId(strMemAccount);
					
					//세션굽기
					session.setAttribute("COEVNT_ENTITY", usrEntity);	
					session.setAttribute("FRONT_ENTITY", null);	
					
					debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | strEnterCorporation : " + usrEntity.getStrEnterCorporation());
					debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | strEnterCorporationMemId : " + usrEntity.getStrEnterCorporationMemId());
					
					// 지정카드(6)인 경우
					if("6".equals(ucusrinfo.getStrCoMemType())){					
						
						if("Y".equals(peCk)){
							//골프메인으로 이동
							debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드이고 골프회원인 경우 - 메인으로 이동");
							request.setAttribute("returnUrl", "");	
							request.setAttribute("resultMsg", "");	
							request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
							request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
						}
						else{
							debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드인데 골프회원이 아닌경우 - 가입페이지로 이동");
							request.setAttribute("returnUrl", "");	
							request.setAttribute("resultMsg", "");	
							request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/mytbox/basis_info/my_basis_info.jsp';");	
							request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.						
						}
										
					}
					else{
						debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 공용카드회원인 경우 탑골프카드 소지인지 체크");
						
						//탑골프카드 소지하고 있는지 체크
						String memClss= ucusrinfo.getMemberClss();
						String strBizNo = "";
						//법인인 경우에는 법인전문
						if("5".equals(memClss))
						{
							System.out.println("## GolfEvntCorporationActn | 법인회원이므로 사업자번호 가져오기 | ID : "+ucusrinfo.getAccount()+" | memClss : "+memClss+"\n");
							strBizNo = selectBizNo(ucusrinfo.getAccount(), con);
							
							/** *****************************************************************
							 *Card정보를 읽어오기
							 ***************************************************************** */
							System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 전문 호출 <<<<<<<<<<<<"+"\n");
							JoltInput cardInput_pt = new JoltInput(BSNINPT);
							cardInput_pt.setServiceName(BSNINPT);
							
							//지정카드 법인인지
							if("6".equals(ucusrinfo.getStrCoMemType() ))
							{
								System.out.println("## GolfCtrlServ | checkJolt 지정카드 법인회원 전문 | ID : "+ucusrinfo.getAccount()+" \n");
								cardInput_pt.setString("fml_trcode", "MHL0230R0100");
								cardInput_pt.setString("fml_arg1", "3");				// 1.주민번호 2.사업자번호 3.전체(지정자주민번호+사업자)
								cardInput_pt.setString("fml_arg2", ucusrinfo.getSocid());	// 주민번호						
								cardInput_pt.setString("fml_arg3", strBizNo);				// 사업자번호
								cardInput_pt.setString("fml_arg4", "2");				// 1.개인 2.기업
							}
							else
							{
								System.out.println("## GolfCtrlServ | checkJolt 공용카드 법인회원 전문 | ID : "+ucusrinfo.getAccount()+" \n");
								cardInput_pt.setString("fml_trcode", "MHL0230R0100");
								cardInput_pt.setString("fml_arg1", "2");				// 1.주민번호 2.사업자번호 3.전체(지정자주민번호+사업자)
								cardInput_pt.setString("fml_arg2", "");					// 주민번호						
								cardInput_pt.setString("fml_arg3", strBizNo);				// 사업자번호
								cardInput_pt.setString("fml_arg4", "2");				// 1.개인 2.기업
							}
							
							JtProcess jt_pt = new JtProcess();
							java.util.Properties prop_pt = new java.util.Properties();
							prop_pt.setProperty("RETURN_CODE","fml_ret1");
							
							TaoResult cardinfo_pt = null;
							String resultCode_pt = "";	
							boolean existsData = false;
							String cardType = "";
							
							String joinNo = "";
							
							cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
							
							resultCode_pt = cardinfo_pt.getString("fml_ret1");
							System.out.println("## resultCode_pt ::  " + resultCode_pt+"\n");
							
							
							
							
							
							if("6".equals(ucusrinfo.getStrCoMemType() ))
							{
							
							
							
							
							
							
								if ( !"00".equals(resultCode_pt) && !"02".equals(resultCode_pt) ) {		// 00 정상, 02 다음조회 있음
									System.out.println("## 법인 전문 내용 없음 \n");
									
									//가입페이지로 이동
									request.setAttribute("returnUrl", "");	
									//20100209 와이즈비스 삭제요청
									//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
									request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
									request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
									
									
								}else{
	
									while( cardinfo_pt.isNext() ) {
										
										if(!existsData){																			
											existsData = true;
										}
										
										cardinfo_pt.next();
										
										System.out.println("## GolfMemChkCorporationActn | 골프카드 체크 전문  | ID : "+ucusrinfo.getAccount()+"\n");
										
										try{
										
										
											cardType 	= (String) cardinfo_pt.getString("fml_ret4");	//카드종류 1:골프카드 / 2:PT카드 / 3:일반카드								
											joinNo 		= (String) cardinfo_pt.getString("fml_ret8");	//제퓨코드						
																	
											
		//									- 상품명 :  나의 알파 플래티늄골프카드 / 제휴코드
		//									 ㅇ 나의알파플래티늄골프_캐쉬백     / 030478
		//									 ㅇ 나의알파플래티늄골프_아시아나  / 030481
		//									 ㅇ 나의알파플래티늄골프_대한항공  / 030494
		//									 ㅇ 경남은행 Family카드  / 394033
		//								     * IBK APT 프리미엄카드-일반(제휴코드 : 740276) 
		//								     * IBK APT 프리미엄카드-스카이패스(제휴코드 : 740289) 
		//								     * IBK APT 프리미엄카드-아시아나(제휴코드 : 740292) 
											
											System.out.println("## GolfMemChkCorporationActn | 골프카드 체크 전문  | ID : "+ucusrinfo.getAccount()+" | cardType : "+cardType+"\n");
											
											if("1".equals(cardType)){
												
												// 탑골프카드 소지 여부 체크 
												if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
												//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //테스트임 위에꺼로 운영기적용
												{
																								
													
													System.out.println("## GolfMemChkCorporationActn | 탑골프카드 소유 | ID : "+ucusrinfo.getAccount()+"\n");
													
													if("Y".equals(peCk)){
														//골프메인으로 이동
														debug("## GolfEvntCorporationActn | 탑골프카드 소유 | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드이고 골프회원인 경우 - 메인으로 이동");
														request.setAttribute("returnUrl", "");	
														request.setAttribute("resultMsg", "");	
														request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
														request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
														break;
													}
													else{
														debug("## GolfEvntCorporationActn | 탑골프카드 소유 | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드인데 골프회원이 아닌경우 - 가입페이지로 이동");
														request.setAttribute("returnUrl", "");	
														request.setAttribute("resultMsg", "");	
														request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/mytbox/basis_info/my_basis_info.jsp';");	
														request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.						
													}
													
													
												}
												
																						
												
											}
											else
											{
												debug("## GolfMemChkCorporationActn | 88");	
												//가입페이지로 이동
												request.setAttribute("returnUrl", "");	
												//20100209 와이즈비스 삭제요청
												//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
												request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
												request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
											}
										
										} 
										catch(Throwable t) {
											debug("## GolfMemChkCorporationActn | 44");	
											//가입페이지로 이동
											request.setAttribute("returnUrl", "");	
											//20100209 와이즈비스 삭제요청
											//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
											request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
											request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
										}
										
										
									}
									
									if(!existsData){
									debug("## GolfMemChkCorporationActn | 55");	
									//가입페이지로 이동
									request.setAttribute("returnUrl", "");	
									//20100209 와이즈비스 삭제요청
									//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
									request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
									request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
									}
									
									
								}
							
							}
							else
							{
								
								if("00".equals(resultCode_pt))
								{
									
									while( cardinfo_pt.isNext() ) {
										
										if(!existsData){																			
											existsData = true;
										}
										
										cardinfo_pt.next();
										
										System.out.println("## GolfMemChkCorporationActn | 골프카드 체크 전문  | ID : "+ucusrinfo.getAccount()+"\n");
										
										try{
										
										
											cardType 	= (String) cardinfo_pt.getString("fml_ret4");	//카드종류 1:골프카드 / 2:PT카드 / 3:일반카드								
											joinNo 		= (String) cardinfo_pt.getString("fml_ret8");	//제퓨코드						
																	
											
		//									- 상품명 :  나의 알파 플래티늄골프카드 / 제휴코드
		//									 ㅇ 나의알파플래티늄골프_캐쉬백     / 030478
		//									 ㅇ 나의알파플래티늄골프_아시아나  / 030481
		//									 ㅇ 나의알파플래티늄골프_대한항공  / 030494
		//									 ㅇ 경남은행 Family카드  / 394033
		//								     * IBK APT 프리미엄카드-일반(제휴코드 : 740276) 
		//								     * IBK APT 프리미엄카드-스카이패스(제휴코드 : 740289) 
		//								     * IBK APT 프리미엄카드-아시아나(제휴코드 : 740292) 
											
											System.out.println("## GolfMemChkCorporationActn | 골프카드 체크 전문  | ID : "+ucusrinfo.getAccount()+" | cardType : "+cardType+"\n");
											
											if("1".equals(cardType)){
												
												// 탑골프카드 소지 여부 체크 
												if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
												//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //테스트임 위에꺼로 운영기적용
												{
																								
													
													System.out.println("## GolfMemChkCorporationActn | 탑골프카드 소유 | ID : "+ucusrinfo.getAccount()+"\n");
													
													if("Y".equals(peCk)){
														//골프메인으로 이동
														debug("## GolfEvntCorporationActn | 탑골프카드 소유 | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드이고 골프회원인 경우 - 메인으로 이동");
														request.setAttribute("returnUrl", "");	
														request.setAttribute("resultMsg", "");	
														request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
														request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
														break;
													}
													else{
														debug("## GolfEvntCorporationActn | 탑골프카드 소유 | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드인데 골프회원이 아닌경우 - 가입페이지로 이동");
														request.setAttribute("returnUrl", "");	
														request.setAttribute("resultMsg", "");	
														request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/mytbox/basis_info/my_basis_info.jsp';");	
														request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.						
													}
													
													
												}
												
																						
												
											}
											else
											{
												debug("## GolfMemChkCorporationActn | 88");	
												//가입페이지로 이동
												request.setAttribute("returnUrl", "");	
												//20100209 와이즈비스 삭제요청
												//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
												request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
												request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
											}
										
										} 
										catch(Throwable t) {
											debug("## GolfMemChkCorporationActn | 44");	
											//가입페이지로 이동
											request.setAttribute("returnUrl", "");	
											//20100209 와이즈비스 삭제요청
											//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
											request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
											request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
										}
										
										
									}
									if(!existsData){
										debug("## GolfMemChkCorporationActn | 55");	
										//가입페이지로 이동
										request.setAttribute("returnUrl", "");	
										//20100209 와이즈비스 삭제요청
										//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
										request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
										request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
									}
								}
								else if("01".equals(resultCode_pt))
								{
									
									// 리턴값이 01일경우 한번 더 전문을 날린다.
									
									/** *****************************************************************
									 *Card정보를 읽어오기
									 ***************************************************************** */
									System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 전문 호출 <<<<<<<<<<<<"+"\n");
									JoltInput cardInput_pt2 = new JoltInput(BSNINPT);
									cardInput_pt2.setServiceName(BSNINPT);
									
									System.out.println("## GolfCtrlServ | checkJolt 리턴값이 01일경우 한번 더 전문을 날린다. 법인회원 전문 | ID : "+ucusrinfo.getAccount()+" \n");
									cardInput_pt2.setString("fml_trcode", "MHL0230R0100");
									cardInput_pt2.setString("fml_arg1", "3");				// 1.주민번호 2.사업자번호 3.전체(지정자주민번호+사업자)
									cardInput_pt2.setString("fml_arg2", ucusrinfo.getSocid());	// 주민번호						
									cardInput_pt2.setString("fml_arg3", strBizNo);				// 사업자번호
									cardInput_pt2.setString("fml_arg4", "2");				// 1.개인 2.기업
									
									cardinfo_pt = jt_pt.call(context, request, cardInput_pt2, prop_pt);			
									
									resultCode_pt = cardinfo_pt.getString("fml_ret1");
									debug("## resultCode_pt ::  " + resultCode_pt);
									
									if("00".equals(resultCode_pt))
									{
										
										while( cardinfo_pt.isNext() ) {
											
											if(!existsData){																			
												existsData = true;
											}
											
											cardinfo_pt.next();
											
											System.out.println("## GolfMemChkCorporationActn | 골프카드 체크 전문  | ID : "+ucusrinfo.getAccount()+"\n");
											
											try{
											
											
												cardType 	= (String) cardinfo_pt.getString("fml_ret4");	//카드종류 1:골프카드 / 2:PT카드 / 3:일반카드								
												joinNo 		= (String) cardinfo_pt.getString("fml_ret8");	//제퓨코드						
																		
												
			//									- 상품명 :  나의 알파 플래티늄골프카드 / 제휴코드
			//									 ㅇ 나의알파플래티늄골프_캐쉬백     / 030478
			//									 ㅇ 나의알파플래티늄골프_아시아나  / 030481
			//									 ㅇ 나의알파플래티늄골프_대한항공  / 030494
			//									 ㅇ 경남은행 Family카드  / 394033
			//								     * IBK APT 프리미엄카드-일반(제휴코드 : 740276) 
			//								     * IBK APT 프리미엄카드-스카이패스(제휴코드 : 740289) 
			//								     * IBK APT 프리미엄카드-아시아나(제휴코드 : 740292) 
												
												System.out.println("## GolfMemChkCorporationActn | 골프카드 체크 전문  | ID : "+ucusrinfo.getAccount()+" | cardType : "+cardType+"\n");
												
												if("1".equals(cardType)){
													
													// 탑골프카드 소지 여부 체크 
													if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
													//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //테스트임 위에꺼로 운영기적용
													{
																									
														
														System.out.println("## GolfMemChkCorporationActn | 탑골프카드 소유 | ID : "+ucusrinfo.getAccount()+"\n");
														
														if("Y".equals(peCk)){
															//골프메인으로 이동
															debug("## GolfEvntCorporationActn | 탑골프카드 소유 | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드이고 골프회원인 경우 - 메인으로 이동");
															request.setAttribute("returnUrl", "");	
															request.setAttribute("resultMsg", "");	
															request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
															request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
															break;
														}
														else{
															debug("## GolfEvntCorporationActn | 탑골프카드 소유 | 법인플랫폼 통해서 골프라운지 입장 | 법인 지정카드인데 골프회원이 아닌경우 - 가입페이지로 이동");
															request.setAttribute("returnUrl", "");	
															request.setAttribute("resultMsg", "");	
															request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/mytbox/basis_info/my_basis_info.jsp';");	
															request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.						
														}
														
														
													}
													
																							
													
												}
												else
												{
													debug("## GolfMemChkCorporationActn | 88");	
													//가입페이지로 이동
													request.setAttribute("returnUrl", "");	
													//20100209 와이즈비스 삭제요청
													//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
													request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
													request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
												}
											
											} 
											catch(Throwable t) {
												debug("## GolfMemChkCorporationActn | 44");	
												//가입페이지로 이동
												request.setAttribute("returnUrl", "");	
												//20100209 와이즈비스 삭제요청
												//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
												request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
												request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
											}
											
											
										}
										if(!existsData){
											debug("## GolfMemChkCorporationActn | 55");	
											//가입페이지로 이동
											request.setAttribute("returnUrl", "");	
											//20100209 와이즈비스 삭제요청
											//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
											request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
											request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
										}
										
										
										
										
										
									}
									else
									{
										debug("## GolfMemChkCorporationActn | 55");	
										//가입페이지로 이동
										request.setAttribute("returnUrl", "");	
										//20100209 와이즈비스 삭제요청
										//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
										request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
										request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
									}
									
									
									
									
								}
								else
								{
									debug("## GolfMemChkCorporationActn | 55");	
									//가입페이지로 이동
									request.setAttribute("returnUrl", "");	
									//20100209 와이즈비스 삭제요청
									//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
									request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
									request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
									debug("## 법인 전문 내용 없음");						
								}
									
								
								
								
								
								
								
								
								
								
								
								
								
								
								
								
								
								
							}
							
						}
						else
						{
							debug("## GolfMemChkCorporationActn | 66");	
							//가입페이지로 이동
							request.setAttribute("returnUrl", "");	
							//20100209 와이즈비스 삭제요청
							//request.setAttribute("resultMsg", "공용카드 소지자는 개인회원으로 골프라운지 멤버쉽 회원으로 가입하여 주시기 바랍니다.");	
							request.setAttribute("script", "window.top.location.href='/app/golfloung/join_frame2.do?url=/app/golfloung/html/member/membership_club.jsp';");	
							request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	 
						}
						
						
						
						
						
						
					}
					
					
					
				}
				else{
					debug("## GolfMemChkCorporationActn | 77");	
					//골프메인으로 이동
					debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 기타");
					request.setAttribute("returnUrl", "");	
					request.setAttribute("resultMsg", "");	
					request.setAttribute("script", "window.top.location.href='/app/golfloung/index.jsp';");	
					request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.	
				}
  
			}
			// 탈퇴 - 골프회원인 경우만 진행  
			else if (strType.equals("N") && peCk.equals("Y")){
				
				debug("## GolfEvntCorporationActn | 법인플랫폼 통해서 골프라운지 입장 | 골프회원 탈퇴 처리로 이동 | strMemAccount : " + strMemAccount);
				
				// 회원아이디 암호화
				StringEncrypter sender = new StringEncrypter("BCCARD", "GOLF");
				String strMemAccountEnc = sender.encrypt(strMemAccount);
				
				paramMap.put("memAccount", strMemAccountEnc);	
				request.setAttribute("returnUrl", "/app/golfloung/GolfMemDelOut.do");	
				request.setAttribute("resultMsg", "");	
				request.setAttribute("script", "");	
				request.setAttribute("paramMap", paramMap); 
				 
			}
						  
			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
	

	public static String getSubString(String str, int startIndex, int length) { 
		
		byte[] b1 = null; 
		byte[] b2 = null; 
	
		try { 
			if (str == null) { 
				return ""; 
			} 
		
			b1 = str.getBytes(); 
			b2 = new byte[length]; 
		
			if (length > (b1.length - startIndex)) { 
				length = b1.length - startIndex; 
			} 
		
			System.arraycopy(b1, startIndex, b2, 0, length); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 
	
		return new String(b2); 
		
	}
	/**
	 * 플랫폼 계정으로 법인만 체크 | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public String selectBizNo(String account, Connection con) throws BaseException {
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			//Connection con = null;
			
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	D.MEM_CLSS, B.BUZ_NO");
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
			sb.append(" WHERE");
			sb.append(" 	A.ACCOUNT = ? AND D.MEM_STAT = '2' AND D.SEC_DATE IS NULL ");

			String query = sb.toString();

			String coChk = "N"; 
			try {
								
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				if (rs.next()) {
					coChk = rs.getString("BUZ_NO");
				}
				
			}catch (Throwable t) {
			} finally {
				/*try {
					if (con != null)
						con.close();
				} catch (Throwable ignored) {
				}*/
			}

			return coChk;
		}
	
}
