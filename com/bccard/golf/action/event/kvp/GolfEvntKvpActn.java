/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntKvpActn 
*   작성자	: (주)미디어포스 임은혜
*   내용		: KVP 처리
*   적용범위	: golf
*   작성일자	: 2010-05-25
************************* 수정이력 ***************************************************************** 
*    일자       작성자      변경사항
* 2011.02.11    이경희	   ISP인증기록 추가
***************************************************************************************************/
package com.bccard.golf.action.event.kvp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.kvp.GolfEvntKvpDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntKvpActn extends GolfActn{
	
	public static final String TITLE = "KVP 처리";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";

		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();											// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");		
		GolfEvntKvpDaoProc proc = (GolfEvntKvpDaoProc)context.getProc("GolfEvntKvpDaoProc");	// KVP 저장
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // 검증결과 코드 (1: 정상주문완료   3:주문오류시)		
		String ispCardNo = "";	// isp카드번호
		String cstIP = request.getRemoteAddr(); //접속IP
		String socid = ""; 			// 주민번호
		String name = "";
		boolean payCancelResult = false;
				
		RequestParser parser = context.getRequestParser(subpage_key, request, response);
		Map paramMap = BaseAction.getParamToMap(request);
		paramMap.put("title", TITLE);
		
		String payType			= parser.getParameter("payType", "").trim();		// 1:카드 2:카드+포인트 3:타사카드
		
		DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
		boolean payResult = false;
		
		// 올앳페이용 변수
		String sPayType = "";		// 지불수단
		String sApprovalNo = "";	// 신용카드 승인번호
		String sCardNm = "";		// 신용카드 이름(계좌이체 은행이름)

		try {
			
			String social_id_1 = "";
			String social_id_2 = ""; 
 
			String ddd_no = ""; 
			String tel_hno = ""; 
			String tel_sno = ""; 
			String hp_ddd_no = ""; 
			String hp_tel_hno = ""; 
			String hp_tel_sno = ""; 
			String email = "";  
			String idx = ""; 
			String realPayAmt = "";			
			String chkResult = "";

			// 후처리
			String resultMsg = "";
			String script = "";
			int addResult = 0;

			// 기본 조회 
			social_id_1 = (String)parser.getParameter("social_id_1").trim();
			social_id_2 = (String)parser.getParameter("social_id_2").trim();
			socid = social_id_1 + social_id_2; 
			name = (String)parser.getParameter("name","").trim(); 
			
			ddd_no = (String)parser.getParameter("ddd_no",""); 
			tel_hno = (String)parser.getParameter("tel_hno",""); 
			tel_sno = (String)parser.getParameter("tel_sno",""); 
			hp_ddd_no = (String)parser.getParameter("hp_ddd_no",""); 
			hp_tel_hno = (String)parser.getParameter("hp_tel_hno",""); 
			hp_tel_sno = (String)parser.getParameter("hp_tel_sno",""); 
			email = (String)parser.getParameter("email",""); 
			idx = (String)parser.getParameter("idx",""); 
			realPayAmt = (String)parser.getParameter("realPayAmt","");
			chkResult = (String)parser.getParameter("chkResult",""); 
			
			String gds_code 				= parser.getParameter("gds_code", "").trim();
			String grdName					= parser.getParameter("grdName", "").trim();
			String rcvr_nm 					= parser.getParameter("rcvr_nm", "").trim();
			String zp1 						= parser.getParameter("zp1", "").trim();
			String zp2 						= parser.getParameter("zp2", "").trim();
			String addr 					= parser.getParameter("addr", "").trim();
			String dtl_addr					= parser.getParameter("dtl_addr", "").trim();
			String addr_clss				= parser.getParameter("addr_clss", "").trim();

			String order_no = parser.getParameter("allat_order_no", "").trim();
			debug("order_no : " + order_no);		
			dataSet.setString("socid", socid);
			dataSet.setString("social_id_1", social_id_1);
			dataSet.setString("name", name);
			dataSet.setString("ddd_no", ddd_no);
			dataSet.setString("tel_hno", tel_hno);
			dataSet.setString("tel_sno", tel_sno);
			dataSet.setString("hp_ddd_no", hp_ddd_no);
			dataSet.setString("hp_tel_hno", hp_tel_hno);
			dataSet.setString("hp_tel_sno", hp_tel_sno);
			dataSet.setString("email", email);
			dataSet.setString("idx", idx);
			dataSet.setString("realPayAmt", realPayAmt);
			dataSet.setString("chkResult", chkResult);

			//***주문정보저장 end **결제 start***********************************************//*
			
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
			
			String ip 				= request.getRemoteAddr();  
			String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD");				// 가맹점 번호 골프라운지 연회비 : 765943401
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
			String pid = null;																// 개인아이디
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			
			// 결제정보 저장 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType, 4, "0");	
			String sttl_gds_clss = GolfUtil.lpad(idx, 4, "0");
			
			if (sttl_gds_clss.equals("0004")) sttl_gds_clss = "0001";
			if (sttl_gds_clss.equals("0005")) sttl_gds_clss = "0002";
			if (sttl_gds_clss.equals("0006")) sttl_gds_clss = "0003";
			
			if (sttl_gds_clss.equals("0007")) sttl_gds_clss = "0001";
			if (sttl_gds_clss.equals("0008")) sttl_gds_clss = "0002";
			if (sttl_gds_clss.equals("0009")) sttl_gds_clss = "0003";
			
			// 비씨카드 또는 비씨카드+포인트 결제인 경우
			if(payType.equals("1") || payType.equals("2")){			
			
				if(iniplug !=null && !"".equals(iniplug)) {
					kvpMap = payProc.getKvpParameter( iniplug );
				}	
				
				if(kvpMap != null) {
					ispAccessYn = "Y";
					pcg         = (String)kvpMap.get("PersonCorpGubun");		// 개인/법인 구분
					ispCardNo   = (String)kvpMap.get("CardNo");					// isp카드번호
					valdlim		= (String)kvpMap.get("CardExpire");				// 만료 일자
					if ( "2".equals(pcg) ) {
						pid = (String)kvpMap.get("BizId");						// 사업자번호
					} else {
						pid = (String)kvpMap.get("Pid");						// 개인 주민번호
					}
				} else {
					ispCardNo = parser.getParameter("isp_card_no","");	// 하나비자카드 경우
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
	
				payResult = payProc.executePayAuth(context, request, payEtt);	// 승인전문 호출
				dataSet.setString("AUTH_NO", payEtt.getUseNo());
				dataSet.setString("STTL_MINS_NM", "비씨카드");	// 신용카드 이름(계좌이체 은행이름)						  

			}	
			// 타사카드 결제인 경우(올앳페이)
			else if(payType.equals("3")){
				
				  String sEncData  = request.getParameter("allat_enc_data");
				 
				  payEtt.setAmount(realPayAmt);
				  payEtt.setEncData(sEncData);
		
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
			dataSet.setString("CDHD_ID", socid);
			dataSet.setString("STTL_AMT", realPayAmt);				// 결제 금액
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("payType", payType);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());

			debug("결제관련 변수 셋팅 => merMgmtNo(가맹점번호) : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
					 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip + " / idx : " + idx);

			if (payResult) {
				
				addResult = proc.execute(context, request, dataSet);		
				debug("GolfEvntKvpActn 신청테이블 인서트  :  " + addResult);	

				if (addResult > 0) {
					
					String dcAmt = ""; //할인가
					String normAmt = ""; //정가
					
					/*할인가가 저장되는 테이블이나 별도 저장될 곳이 없어 하드코딩
					 * 하드코딩 완전 비추이나..
					*/
					//회원생일 비대상자
					if(idx.equals("1")){//Champion
						dcAmt = "50000";
						normAmt = "200000";
					}else if(idx.equals("2")){//Blue
						dcAmt = "25000";
						normAmt = "50000";						
					}else if(idx.equals("3")){//Gold
						dcAmt = "12500";
						normAmt = "25000";
					
					//회원생일 대상자
					}else if(idx.equals("4")){//Champion
						dcAmt = "55000";
						normAmt = "200000";
					}else if(idx.equals("5")){//Blue
						dcAmt = "30000";
						normAmt = "50000";
					}else if(idx.equals("6")){//Gold
						dcAmt = "17500";
						normAmt = "25000";
						
					//비회원생일 대상자
					}else if(idx.equals("7")){//Champion
						dcAmt = "5000";
						normAmt = "200000";
					}else if(idx.equals("8")){//Blue
						dcAmt = "5000";
						normAmt = "50000";
					}else if(idx.equals("9")){//Gold
						dcAmt = "5000";
						normAmt = "25000";
					}
					
					dataSet.setString("DC_AMT", dcAmt);
					dataSet.setString("NORM_AMT", normAmt);
					dataSet.setString("CUPN_CTNT", "KT Olleh Club제휴");
					dataSet.setString("STTL_GDS_SEQ_NO", Integer.toString(addResult));	// 상품코드에 신청테이블 idx를 넣어준다.
					
					addResult = addResult + addPayProc.execute(context, dataSet);					
					debug("GolfEvntKvpActn 결제 저장  : " + addResult);
				}
				
				if(!gds_code.equals("") && grdName.equals("Champion")) {
					dataSet.setString("gds_code", gds_code);	
					dataSet.setString("rcvr_nm", rcvr_nm);	
					dataSet.setString("zp", zp1+""+zp2);	
					dataSet.setString("addr", addr);		
					dataSet.setString("dtl_addr", dtl_addr);
					dataSet.setString("addr_clss", addr_clss);
					dataSet.setString("hp_ddd_no", hp_ddd_no);	
					dataSet.setString("hp_tel_hno", hp_tel_hno);	
					dataSet.setString("hp_tel_sno", hp_tel_sno);
					dataSet.setString("flag", "KT");
		
					// 01.사은품신청하기(Proc) 조회 20091216
					GolfMemPresentDaoProc procPresent = (GolfMemPresentDaoProc)context.getProc("GolfMemPresentDaoProc");			// 사은품신청하기프로세스
					procPresent.execute(context, dataSet, request);
					
				}
				
			}else{	// 결제실패시 내역 저장 2009.11.27 
				
				veriResCode = "3";
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
				debug("GolfEvntKvpActn  결제 실패 내역 저장 : " + result_fail);
				
			}

			String map_url_path = AppConfig.getAppProperty("MAP_URL_PATH")+"/app/golfloung/join_frame2.do?url=/app/golfloung/html/common/join_isp.jsp";
			
			if(addResult > 1 ){
				
				script += "alert('결제가 완료되었습니다. 감사합니다.');";
				
				 if(payType.equals("3")){
					
					script += "window.open('"+map_url_path+"','_blank','');"; 
					script += "parent.window.close();"; 

				 }else {

					script += "window.open('"+map_url_path+"','_blank','');";
					script += "parent.top.window.close();";
				 
				 }
				
			}else{	
				
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
					  debug("GolfEvntShopOrdActn 결제 취소 성공 | 내역저장결과 : " + result);											
				  }
				  // 결제 취소 실패
				  else{
					  int result = addPayProc.failExecute(context, dataSet, request, payEtt);				
					  debug("GolfEvntShopOrdActn 결제 취소 실패 | 내역저장결과 : " + result);											
				  }								  
				}	
		        	
	        	veriResCode = "3";
	        	resultMsg = "결제에 실패했습니다. 다시 시도해 주시기 바랍니다.";
	        	script = "parent.top.window.close();";
			}		

			request.setAttribute("resultMsg", resultMsg);
			request.setAttribute("script", script);			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			
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
				  debug("GolfEvntShopOrdActn 결제 취소 성공 | 내역저장결과 : " + result);											
			  }
			  // 결제 취소 실패
			  else{
				  int result = addPayProc.failExecute(context, dataSet, request, payEtt);				
				  debug("GolfEvntShopOrdActn 결제 취소 실패 | 내역저장결과 : " + result);											
			  }								  
			}		
			
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
				hmap.put("memName", name);
				hmap.put("memSocid", socid);
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
