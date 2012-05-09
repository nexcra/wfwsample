/**************************************************************************
*	클래스명	: UpdateIndActn
*	작성자		: 현광준
*	내용		: 인터넷회원정보수정 처리부분
*	적용범위	: bccard전체
*	작성일자	: 2004.02.3
************************** 수정이력 ******************************************
* 수정시작일	적용예정일	수정완료일	적용완료일	작성자	변경사항
* 2004.12.03										임건국	SMS 수신 여부 확인 전문 수정
* 2006.08.29												농촌사랑카드 소지자의 제신고 전문 회원사 변경
*															회원사번호 13 -> 11, 14 -> 12
* 2006.09.26										hklee	온라인 전문변환
*															(UHB003_Pb_Svc  => BSXINPT(MHB3000I0103)
* 2008.11.24	2008.11.25	2008.11.24				hklee	모바일 서비스 한글문제
* 2008.12.11	2008.12.11	2008.12.11	2008.12.11	조용국	프라운지 회원인 경우 팝업 안띄우도록 조회
* 2009.06.11                    2009.06.11                      안광현  UPX025_R01 => MPX0250R010
* 2009.10.14										진현구	골프회원정보수정 비씨측 참조
**************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.CommandToken;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.jolt.JtTransactionProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractAction;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;

/**
 * UpdateInd Action
 * @version   2004.02.03
 * @author    <A href="mailto:kjhyun@e4net.net">hyun kwang joon</A>
 **/
public class UpdateIndActn extends AbstractAction {

	public static final String Title = "이벤트->회원정보 수정 팝업 처리";
	static final String JoltXAServiceName = "BSXINPT";
    static final String JoltServiceName = "BSNINPT";
    //static final String TSN025 = "MPX0250R0100";
    static final String TSN025 = "MHL0260R0100";
    static final String TSN300 = "MHB3000I0103"; 
    private static final String BSNINPT = "BSNINPT";					// 프레임웍 조회서비스

	/***********************************************************************
	 * 액션처리.
	 * @param context       WaContext
	 * @param request       HttpServletRequest
	 * @param response      HttpServletResponse
	 * @return 응답정보
	 **********************************************************************/
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, BaseException {
		TaoConnection	con			= null;
		RequestParser	parser		= context.getRequestParser("default", request, response);
		String subpage_key = "default";
		
		ResultException re;
		String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // 버튼
		String goPage = "/app/golfloung/memberActn.do"; // 이동 액션
		
		HttpSession session = request.getSession( false );
		
		try {
		
			Map paramMap 			= parser.getParameterMap();																				
			
			UcusrinfoEntity bcuser = SessionUtil.getFrontUserInfo(request);

			String cardUser = bcuser.getMemberClss();
			String cusNo = "";
			String bnk = "";
			String resultCode_pt = "";
			String todayDate  = DateUtil.currdate("yyyyMM");
			SimpleDateFormat format = new SimpleDateFormat( "yyyyMM" );
			
			// 로그인 유무 확인
			if(bcuser != null)
			{
				if(!"5".equals(cardUser)) {
					/** *****************************************************************
					 * 1. 로그인 유저의 인증
					 ***************************************************************** */
					System.out.println("## MemUpdActlog | Jolt MHL0160R0100 전문 호출 ");
					JoltInput cardInput_pt = new JoltInput(BSNINPT);
					cardInput_pt.setServiceName(BSNINPT);
					cardInput_pt.setString("fml_trcode", "MHL0160R0100");
					cardInput_pt.setString("fml_arg1", bcuser.getSocid());
					//cardInput_pt.setString("fml_trcode", "MHL0230R0100");
					//cardInput_pt.setString("fml_arg1", "1");	// 1.주민번호 2.사업자번호 3.전체(지정자주민번호+사업자)
					//cardInput_pt.setString("fml_arg2", bcuser.getSocid());	// 주민번호				
					//cardInput_pt.setString("fml_arg3", " ");	// 사업자번호
					//cardInput_pt.setString("fml_arg4", "1");	// 1.개인 2.기업
		
					JtProcess jt_pt = new JtProcess();
					java.util.Properties prop_pt = new java.util.Properties();
					prop_pt.setProperty("RETURN_CODE","fml_ret1");
					
					TaoResult cardinfo_pt = null;

					try
					{
						cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
						
						resultCode_pt = cardinfo_pt.getString("fml_ret1");	// 01 : 정상, 02:해당주민번호없음, 03:시스템오류, 04:개인회원 회원사정보 없음.
						
						if ( "01".equals(resultCode_pt))
						{
							while( cardinfo_pt.isNext() ) 
							{
								cusNo 		= cardinfo_pt.getString("fml_ret5");	//회원사번호
								debug("==============cusNo : " + cusNo);
								break;
							}					
						}
						
					}catch(Throwable t) {
						//t.printStackTrace();
						request.setAttribute("result", "01");
						System.out.print(" ## MemUpdActlog | 전문통신 실패 : 카드가 없음 | ID : "+bcuser.getAccount());
						
					}
				}

				//로그인시 회원정보 가져오기 전문
				IndModifyOnlineProc proc  = (IndModifyOnlineProc)context.getProc("IndModifyOnlineProc");
				
				TaoResult output = null;
				JoltInput input = null;
				JtProcess jt = new JtProcess();
								
				String retCode = "";
				System.out.print(" ## MemUpdActlog | 시작 ID : "+bcuser.getAccount()+" | getBankListCodes : "+bnk+"\n" );
				
				try {
					if(!"5".equals(cardUser) && "01".equals(resultCode_pt)) {
						/** *****************************************************************
						 * 2. 로그인 유저의 인터넷카드정보조회 전문
						 ***************************************************************** */
						input = new JoltInput(JoltServiceName);
						input.setString("fml_trcode", TSN025); 
						input.setString("fml_arg1", "1");
						input.setString("fml_arg2", bcuser.getSocid());
						input.setString("fml_arg3", cusNo);
	
						java.util.Properties prop = new java.util.Properties();
						prop.setProperty("RETURN_CODE", "fml_ret1");

						String cardNo = "";
						String ret1 = "";
						String cardValdDate = "";
						
						System.out.print(" ## MemUpdActlog | 2단계 인터넷카드정보조회  전문조회결과 ID : "+bcuser.getAccount()+" |getBankListCodes : "+bnk+ " | retCode : "+retCode+" \n");
						do {
							TaoResult result = jt.call(context, request, input, prop);
							ret1 = result.getString("fml_ret1");
							if ( "00".equals(ret1) ) {         // 정상 (마지막자료)
								// 할일없음...
							} else if ( "01".equals(ret1) ) {  // 다음자료있음
								input.setString("fml_arg4", result.getString("fml_ret2") );
							} else {   // 02 주민번호 Not found, 99 시스템 장애
								subpage_key = "error";
								re = new ResultException();
								re.setTitleImage("error");
								re.setTitleText("회원정보변경");
								re.setKey("UPX025_R01_" + retCode);
								re.addButton(goPage, addButton);
								throw re;
							}

							// 마지막 자료(00)일 경우에 한건도 없을 수 있기 때문에 fml_ret3 이 존재하는지 확인한다.
							if ( ("00".equals(ret1)||"01".equals(ret1)) && result.containsKey("fml_ret3") ) {
								boolean isFinal     = false;  // 최종카드여부
								boolean isCard     = false;  // 본인카드여부

								while( result.isNext() ) {
									result.next();

									isFinal     = "1".equals( result.getString("fml_ret11") );     // 1:최종카드,2:최종카드아님,3:등록미완료(바로카드)
									isCard		= "1".equals( result.getString("fml_ret10") );     // 1:본인 2:가족 3:지정 4:공용
									
									debug("isFinal:" + isFinal);
									debug("isCard:" + isFinal);
									
									//카드유효기간 추가
									cardValdDate = result.getString("fml_ret6");
									
									debug("frm_ret12:" + result.getString("fml_ret12") );
									debug("frm_ret16:" + result.getString("fml_ret16") );
									debug("fml_ret6:" + cardValdDate );
									
									
									
									if ( isFinal && isCard) {
										cardNo 		= result.getString("fml_ret3");	//은행코드
										debug("=============== cardNo : " + cardNo);
										
										
										//유효기간 비교
										debug("## 유효기간비교 | todayDate : "+todayDate+" | 카드유효기간 : "+cardValdDate);
										
								        Date end_date = format.parse( cardValdDate );
								        Date current_date = format.parse( todayDate );

								        if ( current_date.getTime() > end_date.getTime() )
								        {
								            debug("## 유효기간 만료로 인해 다음카드 비교");
								        }
								        else
								        {
								        	debug("## 유효기간 사용가능");
								        	break;
								        }

										
										
										
									}
								}
							}
						} while ( "01".equals(ret1) );  // 다음자료 있어서 루프..
						
						if("".equals(cardNo)) {
							subpage_key = "error";
							re = new ResultException();
							re.setTitleImage("error");
							re.setTitleText("회원정보변경");
							re.setKey("UPX025_R01_" + retCode);
							re.addButton(goPage, addButton);
							throw re;
						}

						/** *****************************************************************
						 * 3. 로그인 유저의 BIN정보 가져오기 전문
						 ***************************************************************** */
						TaoResult binResult = null;
						JoltInput entity_bin = new JoltInput();
						entity_bin.setServiceName("BSNINPT");
						entity_bin.setString("fml_trcode", "MHA0010R0700"); //빈 번호조회
						entity_bin.setString("fml_arg1", cardNo.substring(0,6) );

						java.util.Properties prop_bin = new java.util.Properties();
						prop_bin.setProperty("RETURN_CODE","fml_ret1");

						JtProcess jtproc_bin = new JtProcess();
						binResult = jtproc_bin.call(context, request, entity_bin, prop_bin);
						debug(binResult.toString());
						String bin_ret_code = binResult.getString("fml_ret1").trim(); 
						String bankcode = "00"; 
						if ("00".equals(bin_ret_code))
						{
							bankcode = binResult.getString("fml_ret2").trim();		// 회원사번호
						} else {	// 01.해당 BIN번호 미존재, 99.시스템장애
							subpage_key = "error";
							re = new ResultException();
							re.setTitleImage("error");
							re.setTitleText("회원정보변경");
							re.setKey("UPX025_R01_" + retCode);
							re.addButton(goPage, addButton);
							throw re;
						}
						
						// 은행 통합으로 인한 은행코드변경
						if ("20".equals(bankcode)) {
							bankcode = "24";
						} else if("13".equals(bankcode)) {
							bankcode = "11";
						} else if("14".equals(bankcode)) {
							bankcode = "12";
						}

						/** *****************************************************************
						 * 4. 회원정보 수정 UPDATE 전문
						 ***************************************************************** */
						input = new JoltInput(JoltXAServiceName);
	
						String name = bcuser.getName();

						TaoResult output1 = null;							
						input = new JoltInput(JoltXAServiceName);
						debug("bankcode:"+bankcode);
						String bankAlias = "";
						
						String detailAddr = parser.getParameter("detailaddr", "");
						String co_detailaddr = parser.getParameter("co_detailaddr");
	
						String co_name = parser.getParameter("co_name");
						String position_name = parser.getParameter("position_name");
						
						// WafService (모바일을 통한 입력 : 한글문제 )
						if ( request instanceof com.bccard.waf.action.ServiceRequest ) {
	
							detailAddr = new String(detailAddr.getBytes("ISO-8859-1"), "UTF-8");
							co_detailaddr = new String(co_detailaddr.getBytes("ISO-8859-1"), "UTF-8");
							
							co_name = new String(co_name.getBytes("ISO-8859-1"), "UTF-8");
							position_name = new String(position_name.getBytes("ISO-8859-1"), "UTF-8");
						}
						
						// 고정값 부분 셋팅
						input.setString("fml_trcode", TSN300 );
						input.setString("fml_arg96", "4");						// 개인 , 직업구분 : 1.전문,2.MT,3.FTP,4.BCNS/인터넷 등
						input.setString("fml_arg102", "BCC");					// startchar 고정 "BCC"
						input.setString("fml_arg103", "6020");					// 개인 전문번호
						input.setString("fml_arg104", "000000000000");			// 거래 고유번호 - 고정
						input.setString("fml_arg107", bankcode);				// 회원사번호
						input.setString("fml_arg108", "02");					// 발급구분 - 본사 02
						input.setString("fml_arg110", "00000000000");			// 취급단말번호 - 고정 00000000000
						input.setString("fml_arg111", "1");						// 처리구분 - 고정 1
						input.setString("fml_arg113", "1");						// 신청카드구분 - 본인 :1 , 2:가족, 3:지정, 4:공용 
						input.setString("fml_arg114", "41");					// 신청발급구분 - 고정 41
						input.setString("fml_arg121", "1001000");				// 부서번호 ???
						input.setString("fml_arg122", "19941245");				// 직원번호 ???
						input.setString("fml_arg117", "6");						// 접수방법 - 6 (PC통신)
						input.setString("fml_arg118", "1");						// 접수주체 - 1 본사
						input.setString("fml_arg119", name);					// 신고인 - 신고인성명
						input.setString("fml_arg120", "1");						// 신고인관계 - 1 본인
						input.setString("fml_arg40", "000000");   				// 제휴업체코드
						input.setString("fml_arg1", cardNo) ;					// 은행코드로 영업점번호(셀프메이킹:501240)
						input.setString("fml_arg13", parser.getParameter("phone_0", "")) ;		// 자택전화번호
						input.setString("fml_arg14", parser.getParameter("phone_1", "")) ;		// 자택전화번호1
						input.setString("fml_arg15", parser.getParameter("phone_2", "")) ;		// 자택전화번호2
						input.setString("fml_arg16", parser.getParameter("zipcode1", "") + parser.getParameter("zipcode2", ""));	// 자택우편번호
		
						String addrClss = parser.getParameter("addrClss", ""); // 주소구분코드 => 1:구주소, 2:새주소, 3:미시행사구주소  (3번은 인터넷과 무관)
						
						if ( addrClss.equals("2") ) {
							input.setString("fml_arg17", this.cpReplace(parser.getParameter("zipaddr", ""))) ;// 새주소의  기본주소
							input.setString("fml_arg202", this.cpReplace(parser.getParameter("detailaddr", ""))) ; // 새주소의  상세주소
							input.setString("fml_arg201", addrClss) ; // 자택주소구분코드  '2'
							input.setString("fml_arg203", parser.getParameter("roadCode", "")) ;	//자택 도로명 코드							
						}else {
							input.setString("fml_arg17", this.cpReplace(parser.getParameter("detailaddr", ""))) ;// 구주소 동이하
							input.setString("fml_arg201", addrClss) ; // 자택주소구분코드 '1'
						}

						String fml_arg58 = parser.getParameter("fml_arg58", "");
	
						if(!"".equals(fml_arg58)) {			//퇴직 및 무직인경우
							input.setString("fml_arg58","1");
							input.setString("fml_arg11","1");
						}else{											//직장이 있는경우
	
							input.setString("fml_arg8", co_name);
							input.setString("fml_arg9", position_name);
	
							input.setString("fml_arg53",parser.getParameter("co_phone_0"));
							input.setString("fml_arg54",parser.getParameter("co_phone_1"));
							input.setString("fml_arg55",parser.getParameter("co_phone_2"));
							
							if(co_detailaddr != null){
								
								String addrClss2 = parser.getParameter("addrClss2", ""); // 주소구분코드 => 1:구주소, 2:새주소, 3:미시행사구주소  (3번은 인터넷과 무관)
								
								input.setString("fml_arg56",parser.getParameter("co_zipcode1") + parser.getParameter("co_zipcode2"));
								
								if ( addrClss2.equals("2") ) {
									input.setString("fml_arg57", this.cpReplace(parser.getParameter("co_zipaddr", ""))) ;// 새주소의  기본주소
									input.setString("fml_arg205", this.cpReplace(parser.getParameter("co_detailaddr", ""))) ; // 새주소의  상세주소
									input.setString("fml_arg204", addrClss2) ; // 근무주소구분코드  '2'
									input.setString("fml_arg206", parser.getParameter("roadCode2", "")) ;	//근무 도로명 코드							
								}else {
									input.setString("fml_arg57", this.cpReplace(parser.getParameter("co_detailaddr", ""))) ;// 구주소 동이하
									input.setString("fml_arg204", addrClss2) ; // 근무주소구분코드 '1'
								}								
								
							}

						}
						
						String mob0 = parser.getParameter("mobile_0", "");
						String mob1 = parser.getParameter("mobile_1", "");
						String mob2 = parser.getParameter("mobile_2", "");
	
						if ( "".equals(mob1)) {
							mob0 = "";
							mob1 = "";
							mob2 = "";
						}
						
						input.setString("fml_arg65",mob0) ;			// 핸펀국번
						input.setString("fml_arg66",mob1) ;			// 핸펀번호1
						input.setString("fml_arg67",mob2) ;			// 핸펀번호2

						System.out.print(" ## MemUpdActlog | ID : "+bcuser.getAccount()+" | input : "+input.toString()+"\n");
						
						java.util.Properties prop2 = new java.util.Properties();
						prop2.setProperty("RETURN_CODE", "fml_ret1");
	
						output1 = jt.call(context, request, input, prop2);

			            retCode = output1.getString("fml_ret1");
			            
			            System.out.print(" ## MemUpdActlog | 2단계 전문저장처리결과 ID : "+bcuser.getAccount()+" | bankcode : "+bankcode+ " | retCode : "+retCode+" \n");
						
						if( !"1".equals(retCode) ){
							re = new ResultException();
							re.setTitleImage("error");
							re.setTitleText("회원정보변경");
							re.setKey("UHB003_Pb_Svc_" + retCode);
							re.addButton(goPage, addButton);
							throw re;
						}

					}
				
					String msg = "";
					/** *****************************************************************
					 * 4. 회원정보 수정 WEB DB UPDATE 전문
					 ***************************************************************** */
					if (proc.getMemberInfoUpdate2(context, parser, session, request, cardUser)) {
						msg = "## MemUpdActlog | WEBDB Update 성공 " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
						request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"\n";						
						System.out.println(msg);

						/** *****************************************************************
						 * 5. 법인회원정보 수정 WEB DB UPDATE
						 ***************************************************************** */
						if("5".equals(cardUser)) {
							if (proc.getMemberInfoUpdate5(context, parser, session, request)) {
								msg = "## MemUpdActlog | 법인 WEBDB Update 성공 " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
								request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"\n";						
								System.out.println(msg);
								
								request.setAttribute("resultChk", "00");
							} else {
								msg = "## MemUpdActlog | 법인 WEBDB Update 실패 | " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
								request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"\n";						
								System.out.println(msg);
								
								request.setAttribute("resultChk", "01");
							}
						} else {
							request.setAttribute("resultChk", "00");
						}
					} else {
						msg = "## MemUpdActlog | WEBDB Update 실패 | " + bcuser.getSocid() + "|1|" + bcuser.getAccount() + "|" +
						request.getRemoteAddr() + "|" + DateUtil.currdate("yyyy/MM/dd HH:mm:ss")+"\n";						
						System.out.println(msg);

						request.setAttribute("resultChk", "01");
					}

					request.setAttribute("result", "00");
					System.out.print(" ## MemUpdActlog | 정상적으로 전문저장완료 | ID : "+bcuser.getAccount()+" \n");
				
				} catch(Throwable t) {
					//t.printStackTrace();
					subpage_key = "error";
					request.setAttribute("result", "01");
					System.out.print(" ## MemUpdActlog | 저장 실패 | ID : "+bcuser.getAccount()+" \n");
				}
			}
			else
			{
				//비로그인시
				request.setAttribute("result", "01");
				System.out.print(" ## MemUpdActlog | 비로그인 실패 "+" \n");
			}

			CommandToken.set(request);  
			paramMap.put("token", request.getAttribute("token"));   
			
			request.setAttribute("paramMap", paramMap);
			

		} catch (Throwable be) {			
			throw new GolfException(Title, be);
		} finally {
			try { if(con != null) { con.close(); } else {;} } catch(Throwable ignore) {}
		}
		return super.getActionResponse(context, subpage_key);
	}
	
	 /**
     * XSS 값 변환
     * @param sContent
     * @return String
     */
	public String cpReplace(String sContent) {
		sContent = StrUtil.replace(sContent, "<", "&lt;");
		sContent = StrUtil.replace(sContent, ">", "&gt;");
		sContent = StrUtil.replace(sContent, "\"", "&#034;");
		sContent = StrUtil.replace(sContent, "\'", "&#039;");
		sContent = StrUtil.replace(sContent, "\n", "<br>");
		return sContent;
	}
}
