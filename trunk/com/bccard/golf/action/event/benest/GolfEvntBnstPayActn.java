/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntShopListActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 리스트 
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.benest;

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfElecAauthProcess;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstRegDaoProc;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstPayDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntBnstPayActn extends GolfActn{
	
	public static final String TITLE = "가평베네스트 등록";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";		
		HttpSession session = null;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		
		String resultMsg = "사용자 공인인증 실패"; 
		String veriResCode = "1"; // 검증결과 코드 (1: 정상주문완료   3:주문오류시)		
		boolean resMsg = true;   // 결제 실패 메세지 유무
		String validCert = "false"; 
		String clientAuth = "false";
		String[] semiCertVal = new String[2]; 		
		
		String ispAccessYn  = "N";;
		String userNm = "";
		String pid = "";
		String ispCardNo   = "";// isp카드번호
		String cstIP = request.getRemoteAddr(); //접속IP
		
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
		GolfElecAauthProcess elecAath = new GolfElecAauthProcess();//공인인증검증
		
		RequestParser parser = context.getRequestParser(subpage_key, request, response);
		Map paramMap = BaseAction.getParamToMap(request);
		paramMap.put("title", TITLE);
		
		try { 
			
			semiCertVal = elecAath.semiCert(request, response);
			
			validCert = semiCertVal[0];
			clientAuth = semiCertVal[1];
			
			if (validCert.equals("false")){
				resultMsg = "올바른 인증서가 아닙니다.";
			}
			
			if (clientAuth.equals("false")){
				debug("인증이 필요없는 페이지");				
			}
			
			debug (" ### validCert : " + validCert +", clientAuth : " + clientAuth);	
			String aplc_seq_no				= parser.getParameter("aplc_seq_no","");
			
			if (validCert.equals("true")||clientAuth.equals("false")){
				// 후처리
				
				boolean payResult = false;
				boolean payCancelResult = false;
				int addResult = 0;
	
				
				String juminno1					= parser.getParameter("juminno1","");
				String juminno2					= parser.getParameter("juminno2","");
				userNm							= parser.getParameter("userNm","");
				String userId					= parser.getParameter("userId","");
	
				String order_no					= parser.getParameter("order_no", "");			// 주문코드
				String realPayAmt				= parser.getParameter("realPayAmt", "0");		// 결제 금액
				
				debug("aplc_seq_no : " + aplc_seq_no
						 + " / juminno1 : " + juminno1 + " / juminno2 : " + juminno2 + " / userNm : " + userNm + " / userId : " + userId 
						 + " / order_no : " + order_no+ " / realPayAmt : " + realPayAmt);
				
				
				// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
				DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				for(int i=1; i<21; i++){
					dataSet.setString("seq_no"+i, parser.getParameter("seq_no"+i, ""));
				}
				dataSet.setString("aplc_seq_no", aplc_seq_no);
				dataSet.setString("jumin_no", juminno1+juminno2);
				dataSet.setString("userNm", userNm);
				dataSet.setString("userId", userId);
	
	
				// 04.실제 테이블(Proc) 조회
				// 주문정보 저장
				GolfEvntBnstPayDaoProc proc = (GolfEvntBnstPayDaoProc)context.getProc("GolfEvntBnstPayDaoProc");
	//			addResult = proc.execute(context, request, dataSet);	// 1 time	
				debug("GolfEvntBnstPayActn = addResult : " + addResult);	
				
				
				/***주문정보저장 end **결제 start***********************************************/
				
				//debug("// STEP 1. 입력값에 대한 세션체크+");
				String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
				if ( st_s == null ) st_s = "";
				request.getSession().removeAttribute("ParameterManipulationProtectKey");
	
				String st_p = request.getParameter("ParameterManipulationProtectKey");
				if ( st_p == null ) st_p = "";
	
				if ( !st_p.equals(st_s) ) {
					MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
				//	throw new EtaxException(msgEtt);
				}				
	
				// 결제정보 변수 => 공통
				String payType 			= parser.getParameter("payType", "").trim();		// 1:카드 2:카드+포인트 3:타사카드
				String ip 				= request.getRemoteAddr();  
				String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD4");				// 가맹점 번호 골프라운지 골프패키지	770119761
				String iniplug 			= parser.getParameter("KVPpluginData", "");			// ISP 인증값
	
				String cardNo			= parser.getParameter("card_no", "0");				// 카드번호
				String insTerm			= parser.getParameter("ins_term", "00");			// 할부개월수
				String siteType			= parser.getParameter("site_type", "1");			// 사이트 구분 1: 비씨, 2:지자체	
				
							
				// 비씨카드 debug("// STEP 1_2. 파라미터 입력"); 
				HashMap kvpMap = null;
				String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// 사용자 아이디
				String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// 사인값
				String pcg         = "";														// 개인/법인 구분
				
				String valdlim	   = "";														// 만료 일자
				
				String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
				
				// 올앳페이용 변수
				String sPayType = "";		// 지불수단
				String sApprovalNo = "";	// 신용카드 승인번호
				String sCardNm = "";		// 신용카드 이름(계좌이체 은행이름)
	
				// 결제정보 저장 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				String sttl_mthd_clss = GolfUtil.lpad(payType+"", 4, "0");
				String sttl_gds_clss = "0010";
				
				// 비씨카드 또는 비씨카드+포인트 결제인 경우 
				if(payType.equals("1")){
					
					if(iniplug !=null && !"".equals(iniplug)) {
						kvpMap = payProc.getKvpParameter( iniplug );
					}	
					
					if(kvpMap != null) {
						ispAccessYn = "Y";
						pcg         = (String)kvpMap.get("PersonCorpGubun");		// 개인/법인 구분
						ispCardNo   = (String)kvpMap.get("CardNo");					// isp카드번호
						valdlim		= (String)kvpMap.get("CardExpire");				// 만료 일자
						if ( "2".equals(pcg) ) {
							pid = (String)kvpMap.get("BizId");								// 사업자번호
						} else {
							pid = (String)kvpMap.get("Pid");									// 개인 주민번호
						}
					} else {
						ispCardNo = parser.getParameter("isp_card_no","");	// 하나비자카드 경우
					}					
					
					//인증서 검증
					if (clientAuth.equals("true")){
						
						session = request.getSession();
						
//						if(!elecAath.isValidCert(request, response, pid, pcg)){
						
							elecAath.setValidCertMsg(session);
							resultMsg = (String)session.getAttribute("validCertMsg");
							
							paramMap.put("aplc_seq_no", aplc_seq_no);
							request.setAttribute("resultMsg", resultMsg);
							request.setAttribute("returnUrl", "GolfEvntBnstPayForm.do");
					        request.setAttribute("paramMap", paramMap);
					        
					        return super.getActionResponse(context, subpage_key);
							
//						}else {
//							debug("####### ISP & 공인인증 검증 OK..  ");
//						}
					
					}					
					
					if ( valdlim.length() == 6 ) {
						valdlim = valdlim.substring(2);											
					}					
	
					//debug("// STEP 5. 승인처리");
					payEtt.setMerMgmtNo(merMgmtNo);
					payEtt.setCardNo(ispCardNo);
					payEtt.setValid(valdlim);			
					payEtt.setAmount(realPayAmt);
					payEtt.setInsTerm(insTerm);
					payEtt.setRemoteAddr(ip);				 
		
					if( "211.181.255.40".equals(host_ip)) {
						payResult = payProc.executePayAuth(context, request, payEtt);			// 승인전문 호출
					} else {
						payResult = payProc.executePayAuth(context, request, payEtt);			// 승인전문 호출
					}
					
					if("211.181.255.40".equals(host_ip)) {
						dataSet.setString("AUTH_NO", payEtt.getUseNo());
					} else {
						dataSet.setString("AUTH_NO", payEtt.getUseNo());
					}
	
					  dataSet.setString("STTL_MINS_NM", "비씨카드");	// 신용카드 이름(계좌이체 은행이름)						  
				
				}	
				// 타사카드 결제인 경우(올앳페이)
				else if(payType.equals("3")){
	
					  String sEncData  = request.getParameter("allat_enc_data");
					  String sShopid  = request.getParameter("allat_shop_id");
					  String sCrossKey  = request.getParameter("allat_cross_key");
						
					  payEtt.setAmount(realPayAmt);
					  payEtt.setEncData(sEncData);
					  payEtt.setShopId(sShopid);
					  payEtt.setCrossKey(sCrossKey);
			
					  // 결제승인 요청
					  payResult = payProc.executePayAuth_Allat(context, request, payEtt);	
					   					  
					  // 결제승인 성공
					  if(payResult){						  
						  sPayType = payEtt.getPayType();	// 지불수단
						  sApprovalNo = payEtt.getUseNo();	// 신용카드 승인번호
						  insTerm = payEtt.getInsTerm();	// 신용카드 할부개월
						  sCardNm = payEtt.getCardNm();		// 신용카드 이름(계좌이체 은행이름)
						  merMgmtNo = "";
						  
						  if(insTerm == null || insTerm.equals(""))	insTerm = "00";
						  
						  // 결제수단값 셋팅
						  if(sPayType.equals("CARD")){
							  sttl_mthd_clss = "0003";
						  }
						  else if(sPayType.equals("ABANK")){
							  sttl_mthd_clss = "0004";
						  }	
						  
						  dataSet.setString("AUTH_NO", sApprovalNo);	// 신용카드 승인번호
						  dataSet.setString("STTL_MINS_NM", sCardNm);	// 신용카드 이름(계좌이체 은행이름)						  
					  }
	
				}
				
				// 결제 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
				dataSet.setString("ORDER_NO", order_no);
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("STTL_AMT", realPayAmt);				// 결제 금액
				dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
				dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
				dataSet.setString("STTL_GDS_SEQ_NO", aplc_seq_no);
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("MER_NO", merMgmtNo);
				dataSet.setString("CARD_NO", ispCardNo);
				dataSet.setString("VALD_DATE", valdlim);
				dataSet.setString("INS_MCNT", insTerm.toString());
	
				debug("결제관련 변수 셋팅 => merMgmtNo(가맹점번호) : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
						 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip + " / aplc_seq_no : " + aplc_seq_no);
	
				if (payResult) {
					addResult = proc.execute(context, request, dataSet);		
					debug("GolfEvntBnstPayActn : 주문 테이블 업데이트 = addResult2 : " + addResult);	
	
					if (addResult > 0) {
						addResult = addResult + addPayProc.execute(context, dataSet);					
						debug("GolfEvntBnstPayActn : 결제 저장 = addResult3 : " + addResult);	
					}
				}
				else{	// 결제실패시 내역 저장 2009.11.27 
					
					int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
					debug("GolfEvntBnstPayActn = result_fail : " + result_fail);					
					
					veriResCode = "3";
					resMsg = false;
					
				}
	
				if(addResult > 1 ){
					resultMsg = "결제가 완료 되었습니다.";
				}else{
					
					veriResCode = "3";
	
					// 비씨카드 또는 복합결제인 경우
					if(payType.equals("1") || payType.equals("2")){									
			        	if(!GolfUtil.empty(payEtt.getUseNo())){
			        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
			        	}
					}
					// 타사카드(올앳페이)인 경우
					else if(payType.equals("3") && payResult){
					  payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		
					  
					  dataSet.setString("STTL_STAT_CLSS", "Y");	// Y:결제취소, N:결제승인요청
					  // 결제 취소 성공
					  if (payCancelResult) {
						  int result = addPayProc.execute(context, dataSet);
						  debug("GolfEvntBnstPayActn 결제 취소 성공 | 내역저장결과 : " + result);											
					  }
					  // 결제 취소 실패
					  else{
						  int result = addPayProc.failExecute(context, dataSet, request, payEtt);				
						  debug("GolfEvntBnstPayActn 결제 취소 실패 | 내역저장결과 : " + result);											
					  }								  
					}					
					
					if ( !resMsg ){
						resultMsg = payEtt.getResMsg() + "\\n결제에 실패했습니다. 다시 시도해 주시기 바랍니다.";
					}else {
						resultMsg = "결제에 실패했습니다. 다시 시도해 주시기 바랍니다.";
					}
					
				}
	
				paramMap.put("aplc_seq_no", aplc_seq_no);
				request.setAttribute("resultMsg", resultMsg);
				request.setAttribute("returnUrl", "GolfEvntBnstPayForm.do");
		        request.setAttribute("paramMap", paramMap);				
		        
			}else {
				
				String script ="";
				script = "alert('"+resultMsg+"'); top.window.close(); ";
				request.setAttribute("paramMap", paramMap);
				request.setAttribute("script", script);				
				
			}	
	        
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
				hmap.put("memSocid", pid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", cstIP);
				hmap.put("className", "GolfEvntKvpActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}
		
		}
		

		return super.getActionResponse(context, subpage_key);
		
	}
}
