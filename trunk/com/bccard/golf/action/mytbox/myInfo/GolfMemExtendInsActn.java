/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemExtendInsActn
*   작성자    : 미디어포스 임은혜
*   내용      : 마이페이지 > 유료회원 기간 연장 처리
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************* 수정이력 ***************************************************************** 
*    일자       작성자      변경사항
* 2011.02.14    이경희	   ISP인증기록 추가
***************************************************************************************************/
package com.bccard.golf.action.mytbox.myInfo;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.*;
import com.bccard.golf.dbtao.proc.mytbox.myInfo.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.AppConfig;


/******************************************************************************
* Golf
* @author	미디어포스  
* @version	1.0 
******************************************************************************/
public class GolfMemExtendInsActn extends GolfActn{
	
	public static final String TITLE = "마이페이지 > 유료회원 기간 연장처리"; 

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
 
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userId = "";

		// 후처리 관련 변수
		boolean payResult = false;
		boolean payCancelResult = false;
		int addResult = 0;

    	String script = ""; 
    	String strMem = "";
    	int intMemGrade = 0;
		int resultPresent = 0;
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // 검증결과 코드 (1: 정상주문완료   3:주문오류시)	
		String memName = "";
		String memSocid = "";	
		String ispCardNo = "";	// isp카드번호
		String ip = request.getRemoteAddr();		

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

		try {
			// 01.세션정보체크 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
				strMem 		= (String)usrEntity.getMemGrade();
				intMemGrade = (int)usrEntity.getIntMemGrade();
				memName 	= (String)usrEntity.getName();			//성명
				memSocid 	= (String)usrEntity.getSocid();			//주민등록번호				
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String payType 				= parser.getParameter("payType", "").trim();	// 1:카드 2:카드+포인트 3:타사카드
			String moneyType 			= parser.getParameter("moneyType", "").trim();	
			// 1:champion(200,000) 2:Black(150,000) 3:blue(50,000) 4:gold(25,000) 5:White(무료)
			String memType 				= parser.getParameter("memType", "").trim();	// 회원구분 - 정회원 : 1 비회원:2
			String insType				= parser.getParameter("insType", "").trim();	// 유입경로 - TM : 1 일반 : ""
			String realPayAmt			= parser.getParameter("realPayAmt", "").trim();	// 실결제금액
			String tmYn					= parser.getParameter("tmYn", "").trim();		// Y:TM 고객
			String payWay				= parser.getParameter("payWay", "").trim();		// yr:연회비, mn:월회비
			String code					= parser.getParameter("code", "").trim();		//제휴구분코드
			String openerType			= parser.getParameter("openerType", "");		// N:업그레이드 회원
			String fromUrl				= parser.getParameter("fromUrl", "");		// N:업그레이드 회원

			//-- 2009.11.12 추가  쿠폰, 사은품
			String cupn_type 			= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 		= parser.getParameter("pmgds_pym_yn", "");

			String gds_code 			= parser.getParameter("gds_code", "").trim();
			String rcvr_nm 				= parser.getParameter("rcvr_nm", "").trim();
			String zp1 					= parser.getParameter("zp1", "").trim();
			String zp2 					= parser.getParameter("zp2", "").trim();
			String addr 				= parser.getParameter("addr", "").trim();
			String dtl_addr				= parser.getParameter("dtl_addr", "").trim();
			String addr_clss			= parser.getParameter("addr_clss", "").trim();
			String hp_ddd_no 			= parser.getParameter("hp_ddd_no", "").trim();
			String hp_tel_hno 			= parser.getParameter("hp_tel_hno", "").trim();
			String hp_tel_sno 			= parser.getParameter("hp_tel_sno", "").trim();
			
			String sum		 			= parser.getParameter("realPayAmt", "0");	// 결제금액
			int	sale_amt 				= 0;			// 세일금액
			String joinChnl				= "0001";		// 가입경로
			String couponYN 			= "N";			// 쿠폰사용여부
			
			//쿠폰이용코드 체크 시작
			String ctnt	 = "";
			String evnt_no = "";
			String cupn_clss = "";
			String code_no = "";
			String norm_amt ="0";
			String dc_amt = "0"; //할인금액
			
			if(sum != null && !"".equals(sum)){
				sum = StrUtil.replace(sum,",","");
				norm_amt = sum;  //정상금액
			}   

			debug("::golfmemExtendIns:: sum : " + sum + " / payType : " + payType + " / moneyType : " + moneyType + " / memType : " + memType + " / insType : " + insType
					+ " / openerType : " + openerType + " / realPayAmt : " + realPayAmt + " / cupn_type : " + cupn_type
					+ " / pmgds_pym_yn : " + pmgds_pym_yn + " / tmYn : " + tmYn + " / payWay : " + payWay + " / intMemGrade : " + intMemGrade);
			

			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");		// 결제내역 저장
			GolfMemInsDaoProc proc_mem = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");					// 회원저장
			GolfMemExtendInsDaoProc proc = (GolfMemExtendInsDaoProc)context.getProc("GolfMemExtendInsDaoProc");		// 회원연장 저장
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// 결제 테이블에 결제 결과 저장 
			String order_no = parser.getParameter("allat_order_no", "").trim();
			dataSet.setString("ORDER_NO", order_no);	
			//debug("주문번호========> " + order_no);			
			

			dataSet.setString("CODE", code); //쿠폰코드  
			dataSet.setString("SITE_CLSS", "10");//사이트구분코드 10:골프라운지
			dataSet.setString("EVNT_NO", "111");//이벤트번호1
			dataSet.setString("EVNT_NO2", "112");//이벤트번호2 
			dataSet.setString("CUPN_TYPE", cupn_type);//쿠폰구분 
			dataSet.setString("PMGDS_PYM_YN", pmgds_pym_yn);//경품지급여부 

			//쿠폰이용코드시 할인 10% 입력
			if (!"".equals(code))
			{
				DbTaoResult codeCheck = proc_mem.codeExecute(context, dataSet, request);
				debug("===================codeCheck : " + codeCheck);
				if (codeCheck != null && codeCheck.isNext()) {
					codeCheck.first();
					codeCheck.next();
					debug("===================memGrade : " + codeCheck.getString("RESULT"));
					if(codeCheck.getString("RESULT").equals("00")){
						ctnt = (String) codeCheck.getString("CUPN_CTNT");	
						sale_amt = (int) codeCheck.getInt("CUPN_AMT");
						code_no = (String) codeCheck.getString("CUPN_NO");
						evnt_no = (String) codeCheck.getString("EVNT_NO");
						cupn_clss = (String) codeCheck.getString("CUPN_CLSS");
						if ( cupn_clss.equals("02") ) {
							joinChnl = "2000";  //무료쿠폰이용으로  들어온경우 가입경로를 2000 으로
						} else  {
							joinChnl = "1000";  //할인쿠폰코드로 들어온경우 가입경로를 1000 으로
						}
						//할인률
						if ("01".equals(cupn_clss))	{
							double div = ((double)(100-(double)sale_amt)/100); //할인율 (0.9)
							int sttl_amt = (int)(Double.parseDouble(sum) * div); //실결제금액
							sum = String.valueOf(sttl_amt) ; //실결제금액
							int dc = Integer.parseInt(norm_amt) - Integer.parseInt(sum); //할인금액
							dc_amt = String.valueOf(dc); //할인금액
							debug("실결제금액="+sum+",할인금액="+dc_amt+",할인율="+div);

						//무료쿠폰
						} else {
							dc_amt = String.valueOf(sale_amt) ;
							int sttl_amt = Integer.parseInt(norm_amt) - Integer.parseInt(dc_amt);
							sum = String.valueOf(sttl_amt) ; //실결제금액
							debug("쿠폰무료금액="+String.valueOf(sale_amt)+",실결제금액="+sum+",할인금액="+dc_amt);
						}
						couponYN= "Y";

					}
					
				}

			}

			// 쿠폰관련
			dataSet.setString("CODE_NO", code_no); 		//쿠폰번호
			dataSet.setString("JOIN_CHNL", joinChnl); 

			debug("332 : join_chnl : " + joinChnl);
			dataSet.setString("CUPN_CTNT", ctnt);
			dataSet.setString("CUPN_NO", code_no); 		//신용카드번호에 입력할것임.
			dataSet.setString("NORM_AMT", norm_amt); 	//정상요금 
			dataSet.setString("DC_AMT", dc_amt);		//할인금액
			dataSet.setString("STTL_AMT", sum); 		//결제금액 
			dataSet.setString("CUPN_CLSS", cupn_clss); 	//쿠폰구분
			dataSet.setString("CODE_EVNT_NO", evnt_no); //쿠폰이벤트번호
			
			dataSet.setString("realPayAmt", realPayAmt);
			dataSet.setString("payWay", payWay);
				
			if(!"".equals(gds_code) && "1".equals(moneyType)) {
				dataSet.setString("gds_code", gds_code);	
				dataSet.setString("rcvr_nm", rcvr_nm);	
				dataSet.setString("zp", zp1+""+zp2);	
				dataSet.setString("addr", addr);		
				dataSet.setString("dtl_addr", dtl_addr);
				dataSet.setString("addr_clss", addr_clss);
				dataSet.setString("hp_ddd_no", hp_ddd_no);	
				dataSet.setString("hp_tel_hno", hp_tel_hno);	
				dataSet.setString("hp_tel_sno", hp_tel_sno);
	
				// 01.사은품신청하기(Proc) 조회 20091216
				GolfMemPresentDaoProc procPresent = (GolfMemPresentDaoProc)context.getProc("GolfMemPresentDaoProc");			// 사은품신청하기프로세스
				resultPresent = procPresent.execute(context, dataSet, request);
				//debug("=========result : " + result);
			}
			
			dataSet.setString("payType", payType);	
			dataSet.setString("moneyType", moneyType);
			dataSet.setString("memType", memType);	
			dataSet.setString("insType", insType);	
			dataSet.setString("CODE", code); //쿠폰코드  
			dataSet.setString("SITE_CLSS", "10");//사이트구분코드 10:골프라운지
			dataSet.setString("EVNT_NO", "111");//이벤트번호1
			dataSet.setString("EVNT_NO2", "112");//이벤트번호2 
			dataSet.setString("CUPN_TYPE", cupn_type);//쿠폰구분 
			dataSet.setString("PMGDS_PYM_YN", pmgds_pym_yn);//경품지급여부 
			dataSet.setString("payWay", payWay);// yr:연회비, mn:월회비

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
			
			
			// 공통
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD");		// 가맹점 번호(766559864) //topn : 745300778
			String iniplug = parser.getParameter("KVPpluginData", "");	// ISP 인증값

			String cardNo		= parser.getParameter("card_no", "0");				// 카드번호
			String insTerm		= parser.getParameter("ins_term", "00");			// 할부개월수
			String siteType		= parser.getParameter("site_type", "1");			// 사이트 구분 1: 비씨, 2:지자체	
			
			//debug("// STEP 1_2. 파라미터 입력");
			HashMap kvpMap = null;
			String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// 사용자 아이디
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// 사인값
			String pcg         = "";														// 개인/법인 구분			
			String valdlim	   = "";														// 만료 일자
			String pid = null;																// 개인아이디
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			
			// 올앳페이용 변수
			String sPayType = "";		// 지불수단
			String sApprovalNo = "";	// 신용카드 승인번호
			String sCardNm = "";		// 신용카드 이름(계좌이체 은행이름)

			// 결제 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType+"", 4, "0");
			String sttl_gds_clss = GolfUtil.lpad(moneyType+"", 4, "0");
			if("7".equals(moneyType)){
				sttl_gds_clss = "0008";		// 블랙등급에 대한 결제 상품구분코드, 나머지는 머니타입과 일치
			}

			//debug("// STEP 1_3. 공인인증값이 있을 경우 유효성 검사..");
			
			// 04.실제 테이블(Proc) 조회  			
			if(intMemGrade==4 || tmYn.equals("Y")){	
				// 일반회원은 결제 처리 하지 않고 넘어가게 한다.
				//dataSet.setString("CODE_NO", code_no);
				//dataSet.setString("JOIN_CHNL", joinChnl);
				payResult = true;	
				addResult = 1;
			
			//무료쿠폰이용고객
			}else if( cupn_clss.equals("02")){	
				// 무료쿠폰이용고객은 결제 처리 하지 않고 넘어가게 한다.
				
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("STTL_MTHD_CLSS", "1001");
				dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("MER_NO", "");
				dataSet.setString("CARD_NO", code_no); //쿠폰번호
				dataSet.setString("VALD_DATE", "");
				dataSet.setString("INS_MCNT", "");
				dataSet.setString("AUTH_NO","");

				payResult = true;	
				addResult = 0;
			}else{
				
				
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
	
					//debug("// STEP 5. 승인처리");
					payEtt.setMerMgmtNo(merMgmtNo);
					payEtt.setCardNo(ispCardNo);
					payEtt.setValid(valdlim);			
					payEtt.setAmount(sum);
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
						
					  payEtt.setAmount(sum);
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
				dataSet.setString("CDHD_ID", userId);
				dataSet.setString("STTL_AMT", sum); //결제금액
				dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
				dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("MER_NO", merMgmtNo);
				dataSet.setString("CARD_NO", ispCardNo);
				dataSet.setString("VALD_DATE", valdlim);
				dataSet.setString("INS_MCNT", insTerm.toString());
				debug("merMgmtNo : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim + " / sum : " + sum);
				debug("insTerm : " + insTerm + " / ip : " + ip);	

			}

			// 04.결제처리	
			// 결제 승인 완료 
			//payResult = true;
			if (payResult) { 
				addResult = addResult + proc.execute(context, dataSet, request);	// 유료회원 기간 연장하기		

				debug("결제자료 입력 / addResult : " + addResult + " / intMemGrade : " + intMemGrade + " / tmYn : " + tmYn);

				if (addResult == 1) {
					debug("결제 저장 ");
					addResult = addResult + addPayProc.execute(context, dataSet);

					//유료쿠폰 자료 저장
					if ( "Y".equals(couponYN))	{
						int couponResult = proc_mem.couponUpExecute(context, dataSet); 
						if (couponResult != 1) {
							debug("쿠폰저장 오류");
						}

					}
				}
			}
			else{	// 결제실패시 내역 저장 2009.11.27 
				veriResCode = "3";
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);				
				debug("결제실패내역저장결과========> " + result_fail);						
				
			}				 

			// 리턴 URL 지정
			//script = script + "top.opener.location.reload(); top.window.close();";
			script = script + "parent.location.reload();";
			
			debug("script : " + script + " / openerType : " + openerType + " / fromUrl : " + fromUrl);

			if (addResult == 2) {
				// IBK기업골드 회원은 등급이 변경되므로 세션을 변경한다.
				debug("intMemGrade : " + intMemGrade);
				if(intMemGrade==13){
					usrEntity.setMemGrade("Gold");
					usrEntity.setIntMemGrade(3);
					usrEntity.setIntMemberGrade(3);
					
					script = script + "window.top.document.mForm.isLogin.value='1';\n";
					script = script + "window.top.document.mForm.userNm.value='Gold'\n;";
					script = script + "window.top.document.mForm.memGrade.value='3'\n;";
					script = script + "window.top.document.mForm.intMemGrade.value='3'\n;";
				}

				request.setAttribute("script", script);
				request.setAttribute("resultMsg", "유료회원 기간 연장이 정상적으로 처리 되었습니다.");
				
	        } else if (addResult == 9) { //한번 더 체크함
	        	// DB저장 실패시 승인취소 전문	        	
				debug("=golfmemExtendIns =============DB저장 실패시 승인취소 전문 1 ");
				
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
					  debug("=golfmemExtendIns ============= 결제 취소 성공 | 내역저장결과 : " + result);											
				  }
				  // 결제 취소 실패
				  else{
					  int result = addPayProc.failExecute(context, dataSet, request, payEtt);				
					  debug("=golfmemExtendIns ============= 결제 취소 실패 | 내역저장결과 : " + result);											
				  }								  
				}

				request.setAttribute("script", script);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");	
	        } else {
	        	// DB저장 실패시 승인취소 전문 
				debug("=golfmemExtendIns =============DB저장 실패시 승인취소 전문 2 ");
				
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
					  debug("=golfmemExtendIns ============= 결제 취소 성공 | 내역저장결과 : " + result);											
				  }
				  // 결제 취소 실패
				  else{
					  int result = addPayProc.failExecute(context, dataSet, request, payEtt);				
					  debug("=golfmemExtendIns ============= 결제 취소 실패 | 내역저장결과 : " + result);											
				  }								  
				}
				  				
				request.setAttribute("script", script);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }	
			
			// 05. Return 값 세팅			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
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
				hmap.put("memName", memName);
				hmap.put("memSocid", memSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", ip);
				hmap.put("className", "GolfMemExtendInsActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
				
			}

		}
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
