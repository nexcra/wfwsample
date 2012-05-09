/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsActn
*   작성자    : 미디어포스 임은혜
*   내용      : 가입 > 등록
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************* 수정이력 ***************************************************************** 
*    일자       작성자      변경사항
* 2011.02.11    이경희	   ISP인증기록 추가
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;
import com.bccard.golf.dbtao.proc.member.GolfMemInsDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemMonthJoinDaoProc;
import com.bccard.golf.dbtao.proc.member.GolfMemPresentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.initech.eam.nls.CookieManager;
import com.initech.eam.smartenforcer.SECode;

/******************************************************************************
* Golf
* @author	미디어포스  
* @version	1.0 
******************************************************************************/
public class GolfMemInsActn extends GolfActn{
	
	public static final String TITLE = "가입 > 등록";

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
		String memGrade = "White"; 
		int intMemGrade = 4;  
		String email_id = ""; 
		String userMobile = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		String userSocid = "";	// 20100319 TM영화예매권교부 이벤트 관련 추가
		int anl_fee = 0;	// 등급 관리 테이블의 해당 등급의 결제 금액

		// 후처리 관련 변수
		boolean payResult = false;
		boolean payCancelResult = false;
		int addResult = 0;
		int	sale_amt = 0;

		int result = 0;
		String returnUrlTrue = ""; 
    	String returnUrlFalse =  "";
    	String script = ""; 
    	String scriptFalse = "";
    	String strMem = "";
		String sum = "0";
		String couponYN = "N";
		int resultPresent = 0;
		

		String sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
    	String userIP = (String) request.getRemoteAddr();
    	String uurl = (String) request.getParameter("UURL");
    	String userAcount = "";
		Connection con = null;

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // 검증결과 코드 (1: 정상주문완료   3:주문오류시)		
		String ispCardNo   = "";  // isp카드번호
		boolean monPaySuccYn = false;		
		boolean yearPaySuccYn = false;
		String payType = "";
		ResultException rx = null;

		try {
			con = context.getDbConnection("default", null);
			
			// 01.세션정보체크 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				userMobile1 	= (String)usrEntity.getMobile1();
				userMobile2 	= (String)usrEntity.getMobile2();
				userMobile3 	= (String)usrEntity.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
				strMem 		= (String)usrEntity.getMemGrade();
				userSocid 	= (String)usrEntity.getSocid();
				System.out.print("## strMem:"+strMem);
				
			}else{
				userId = parser.getParameter("userId", "");
				
				HttpSession session = request.getSession();
		    	session.setAttribute("userID", userId);	
		    	session.setAttribute("userIP", userIP);	
		    	session.setAttribute("UURL", uurl);	
		    	session.setAttribute("SYSID", userId);

				UcusrinfoDaoProc proc_user = (UcusrinfoDaoProc) context.getProc("UcusrinfoDao");	
				UcusrinfoEntity ucusrinfo2 = proc_user.selectByAccount(con, userId);
				session.setAttribute("FRONT_ENTITY", ucusrinfo2);
				session.setAttribute("SESSION_USER", ucusrinfo2);
						
	
		        UcusrinfoEntity usrEntity2 = SessionUtil.getFrontUserInfo(request);
				
		        if(usrEntity2 == null) {
		        	
		        	usrEntity = new UcusrinfoEntity(); 
		        	debug("## GolfCtrlServ | usrEntity null --> 생성 작업 \n");
		        }else{
		        	
		        	userAcount = usrEntity.getAccount();
		        	debug("## GolfCtrlServ | usrEntity not null  \n");
		        } 

		        CookieManager.addCookie(SECode.USER_ID, userId, sso_domain, response);
		        String sso_id = CookieManager.getCookieValue(SECode.USER_ID, request);

				debug("## GolfMemBcJoinEndActn | userId 암호화된값 : " + userId + " / sso_domain : " + sso_domain 
						+ " / userAcount : " + userAcount + " / ucusrinfo : " + ucusrinfo2 + "sso_id : " + sso_id);	
			}
			
			
			payType 					= parser.getParameter("payType", "").trim();	// 1:카드 2:카드+포인트 3:타사카드
			String moneyType 			= parser.getParameter("moneyType", "").trim();	// 1:champion(200,000) 2:Black(150,000) 3:blue(50,000) 4:gold(25,000) 5:White(무료)
			String memType 				= parser.getParameter("memType", "").trim();	// 회원구분 - 정회원 : 1 비회원:2
			String insType				= parser.getParameter("insType", "").trim();	// 유입경로 - TM : 1 일반 : ""
			String openerType			= parser.getParameter("openerType", "").trim();	// N:업그레이드 회원
			String realPayAmt			= parser.getParameter("realPayAmt", "").trim();	// 실결제금액
			String tmYn					= parser.getParameter("tmYn", "").trim();		// Y:TM 고객
			String payWay				= parser.getParameter("payWay", "").trim();		// yr:연회비, mn:월회비
			String payWayTxt			= "연회비";
			if("mn".equals(payWay)){
				payWayTxt = "월회비";
			}
			String code					= parser.getParameter("code", "").trim();		//제휴구분코드
			String joinChnl				= "0001";
			String formtarget			= parser.getParameter("formtarget", "");		//제휴구분코드
			
			//-- 2009.11.12 추가 
			String cupn_type 				= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 				= parser.getParameter("pmgds_pym_yn", "");

			String gds_code 				= parser.getParameter("gds_code", "").trim();
			String rcvr_nm 					= parser.getParameter("rcvr_nm", "").trim();
			String zp1 						= parser.getParameter("zp1", "").trim();
			String zp2 						= parser.getParameter("zp2", "").trim();
			String addr 					= parser.getParameter("addr", "").trim();
			String dtl_addr					= parser.getParameter("dtl_addr", "").trim();
			String addr_clss				= parser.getParameter("addr_clss", "").trim();
			String hp_ddd_no 				= parser.getParameter("hp_ddd_no", "").trim();
			String hp_tel_hno 				= parser.getParameter("hp_tel_hno", "").trim();
			String hp_tel_sno 				= parser.getParameter("hp_tel_sno", "").trim();
			
			//VIP카드 여부
			String vipCardYn 				= parser.getParameter("vipCardYn", "N");

			String call_actionKey 			= parser.getParameter("call_actionKey", "");
			String cupn_ctnt 				= parser.getParameter("cupn_ctnt", "");
			String cupn_amt 				= parser.getParameter("cupn_amt", "");

			debug("GolfMemInsActn :: vipCardYn : "+vipCardYn+" / payType : " + payType + " / moneyType : " + moneyType + " / memType : " + memType + " / insType : " + insType 
					+ " / openerType : " + openerType + " / realPayAmt : " + realPayAmt + " / cupn_type : " + cupn_type
					+ " / pmgds_pym_yn : " + pmgds_pym_yn + " / tmYn : " + tmYn + " / payWay : " + payWay + " / code : " + code + " / formtarget : " + formtarget);			

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			//zp  addr dtl_addr
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
			dataSet.setString("vipCardYn", vipCardYn);			
			
			// 04.실제 테이블(Proc) 조회  
			GolfMemInsDaoProc proc = (GolfMemInsDaoProc)context.getProc("GolfMemInsDaoProc");
			
			// 결제 테이블에 결제 결과 저장 
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			String order_no = parser.getParameter("allat_order_no", "").trim();
			dataSet.setString("ORDER_NO", order_no);	
			debug("===================주문번호========> " + order_no);						

			// 회원 등급 가져오기  
			DbTaoResult gradeView = proc.gradeExecute(context, dataSet, request);
			debug("===================gradeView : " + gradeView);
			if (gradeView != null && gradeView.isNext()) {
				gradeView.first();
				gradeView.next();
				debug("===================memGrade : " + gradeView.getString("RESULT"));
				if(gradeView.getString("RESULT").equals("00")){
					memGrade = (String) gradeView.getString("memGrade").trim();	
					intMemGrade = (int) gradeView.getInt("intMemGrade");	
					anl_fee = (int) gradeView.getInt("anl_fee");
				}
			} 
			
			//쿠폰이용코드 체크 시작
			String ctnt	 = "";
			String evnt_no = "";
			String cupn_clss = "";
			String code_no = "";
			String norm_amt ="0";
			String dc_amt = "0"; //할인금액

			sum		 = parser.getParameter("realPayAmt", "0");	// 결제금액(정상금액)
			if(sum != null && !"".equals(sum)){
				sum = StrUtil.replace(sum,",","");
				norm_amt = sum;  //정상금액
			}   

			String vipCardCheckYn = "N";
			
			debug("GolfMemInsActn 회원등급 관련 변수 셋팅 / memGrade : " + memGrade + " / intMemGrade : " + intMemGrade + " / anl_fee : " + anl_fee 
					+ " / sum : " + sum + " / norm_amt : " + norm_amt);

			//쿠폰이용코드시 할인 10% 입력
			if (!"".equals(code))
			{
				DbTaoResult codeCheck = proc.codeExecute(context, dataSet, request);
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
							
							if("Y".equals(vipCardYn) && sale_amt == 25000 && "15000".equals(norm_amt))
							{
								sttl_amt = 0;
								vipCardCheckYn = "Y";
							}	
							
							sum = String.valueOf(sttl_amt) ; //실결제금액
							debug("쿠폰무료금액="+String.valueOf(sale_amt)+",실결제금액="+sum+",할인금액="+dc_amt);
						}
						couponYN= "Y";

					}
					
				}

			}

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

			

			//쿠폰이용코드 체크 종료
			
			if(intMemGrade==4 || tmYn.equals("Y")){	
				// 일반회원은 결제 처리 하지 않고 넘어가게 한다.
				//dataSet.setString("CODE_NO", code_no);
				//dataSet.setString("JOIN_CHNL", joinChnl);
				payResult = true;	
				addResult = 1;
			
			//무료쿠폰이용고객
			}else if( cupn_clss.equals("02")){	
				// 무료쿠폰이용고객은 결제 처리 하지 않고 넘어가게 한다.
				String sttl_gds_clss = GolfUtil.lpad(moneyType+"", 4, "0"); 
				
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
				
				String merMgmtNo = "";
				
				if ( moneyType.equals(AppConfig.getDataCodeProp("0005CODE11")) ){
					merMgmtNo = AppConfig.getDataCodeProp("MBCDHD6");//스마트 월회원 가맹점번호				
				}else {
					merMgmtNo = AppConfig.getAppProperty("MBCDHD");; //멤버쉽 연회원 가맹점번호
				}
				
				// 공통 
				String ip = request.getRemoteAddr();
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
				if("7".equals(moneyType) && intMemGrade==7){
					sttl_gds_clss = "0008";		// 블랙등급에 대한 결제 상품구분코드, 나머지는 머니타입과 일치
				}
				
				
				//VIP카드 검증절차
				debug("## GolfMemInsActn | VIP혜택 검증절차 시작 | userId : "+userId+" | vipCardYn : "+vipCardYn+" | 선택회원종류 : "+moneyType);
				
				
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
					
					//vipCardYn = "Y";
					//moneyType = "3";
					
					//VIP카드 검증
										
					if("Y".equals(vipCardYn) && "3".equals(moneyType))
					{
						debug("## GolfMemInsActn | VIP카드 소지자이면서 Gold 결제시도 | VIP카드혜택받을려고 시도 | userId : "+userId+" | ispCardNo : "+ispCardNo);
						
						
						GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);	
						if(mbr != null)
						{
							
							List cardVipList = mbr.getCardVipInfoList();
							
							if( cardVipList!=null && cardVipList.size() > 0 )
							{
								for (int i = 0; i < cardVipList.size(); i++) 
								{
									CardVipInfoEtt record = (CardVipInfoEtt)cardVipList.get(i);
									
									String userVipCardNo 		= (String)record.getCardNo(); 
									
									debug("## VIP카드 비교 | userVipCardNo : "+userVipCardNo+" = ispCardNo : "+ispCardNo);
									
									if(userVipCardNo.equals(ispCardNo))
									{
										vipCardCheckYn = "Y";
									}
									
									
								}
								
								//강제 테스트
								//vipCardCheckYn = "Y";
								
								debug("## vipCardCheckYn : "+vipCardCheckYn);
								
								if("Y".equals(vipCardCheckYn))
								{
									debug("## GolfMemInsActn | VIP카드 소지자이면서 Gold 결제시도 | 결제시도");
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

									debug("## STTL_MINS_NM : BCPT카드");
									dataSet.setString("STTL_MINS_NM", "BCPT카드");	// 신용카드 이름(계좌이체 은행이름)		
									
									
								}
								else
								{
									debug("## GolfMemInsActn | VIP카드 소지자이면서 Gold 결제시도했지만 VIP카드가 아님 | 결제실패");
									payResult = false;
									payEtt.setMerMgmtNo(merMgmtNo);
									payEtt.setCardNo(ispCardNo);
									payEtt.setValid(valdlim);			
									payEtt.setAmount(sum);
									payEtt.setInsTerm(insTerm);
									payEtt.setRemoteAddr(ip);	
									dataSet.setString("AUTH_NO", "");
									debug("## STTL_MINS_NM : BCPT카드");
									dataSet.setString("STTL_MINS_NM", "BCPT카드");
									
									veriResCode = "3";
									
								}
								
								
							}
							else
							{
								debug("## GolfMemInsActn | VIP카드 소지자이면서 Gold 결제시도했지만 VIP카드내역이 없음 | 결제실패");
								payResult = false;
								payEtt.setMerMgmtNo(merMgmtNo);
								payEtt.setCardNo(ispCardNo);
								payEtt.setValid(valdlim);			
								payEtt.setAmount(sum);
								payEtt.setInsTerm(insTerm);
								payEtt.setRemoteAddr(ip);	
								dataSet.setString("AUTH_NO", "");
								debug("## STTL_MINS_NM : BCPT카드");
								dataSet.setString("STTL_MINS_NM", "BCPT카드");
								
								veriResCode = "3";
							}
							
							
							
						}
						else
						{
							debug("## GolfMemInsActn | VIP카드 소지자이면서 Gold 결제시도했지만 VIP카드내역이 없음 | 결제실패");
							payResult = false;
							payEtt.setMerMgmtNo(merMgmtNo);
							payEtt.setCardNo(ispCardNo);
							payEtt.setValid(valdlim);			
							payEtt.setAmount(sum);
							payEtt.setInsTerm(insTerm);
							payEtt.setRemoteAddr(ip);	
							dataSet.setString("AUTH_NO", "");
							debug("## STTL_MINS_NM : BCPT카드");
							dataSet.setString("STTL_MINS_NM", "BCPT카드");
							
							veriResCode = "3";
						}			
						
											
						
					}
					else
					{
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

						debug("## STTL_MINS_NM : 비씨카드");
						dataSet.setString("STTL_MINS_NM", "비씨카드");	// 신용카드 이름(계좌이체 은행이름)					
						
					}
					

					
					
					  	  
				
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
				dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
				dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
				dataSet.setString("STTL_STAT_CLSS", "N");
				dataSet.setString("MER_NO", merMgmtNo);
				dataSet.setString("CARD_NO", ispCardNo);
				dataSet.setString("VALD_DATE", valdlim);
				dataSet.setString("INS_MCNT", insTerm.toString());

				debug("결제관련 변수 셋팅 => merMgmtNo : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
						 + " / sum : " + sum + " / insTerm : " + insTerm + " / ip : " + ip);
	
			}
			
			int sttlGdsSeq = 0;

			// 04.결제처리 - 결제 승인 완료
			if (payResult) {				
				
				if ( moneyType.equals(AppConfig.getDataCodeProp("0005CODE11")) ){ //월회원 (스마트등급) 해당
					
					GolfMemMonthJoinDaoProc monthProc = (GolfMemMonthJoinDaoProc)context.getProc("GolfMemMonthJoinDaoProc");
					addResult = addResult + monthProc.execute(context, dataSet, request);
										
					sttlGdsSeq = monthProc.getSeq(context, usrEntity.getAccount());				    
					
					debug(" ## sttlGdsSeq : "+sttlGdsSeq);
					
				    dataSet.setString("STTL_GDS_SEQ_NO", Integer.toString(sttlGdsSeq)); 					
					
					if ( addResult > 0){
						monPaySuccYn = true;
						payWayTxt = "월회비";
					}
					
				}else {//멤버쉽 해당				
					
					addResult = addResult + proc.execute(context, dataSet, request);
					yearPaySuccYn = true;
					
				}

				debug("결제자료 입력 / addResult : " + addResult + " / intMemGrade : " + intMemGrade + " / tmYn : " + tmYn +"/ anl_fee : " + anl_fee);
				
				if (addResult == 1 && (anl_fee>0 || monPaySuccYn ) && !tmYn.equals("Y")) {
					addResult = addResult + addPayProc.execute(context, dataSet);
					
					debug("addResult : " + addResult);

					//유료쿠폰 자료 저장
					if ( "Y".equals(couponYN))	{
						int couponResult = proc.couponUpExecute(context, dataSet); 
						if (couponResult != 1) {
							debug("쿠폰저장 오류");
						}

					}

				}
				
				debug("결제처리 - 결제 승인 완료 /monPaySuccYn : " + monPaySuccYn + " / yearPaySuccYn : " + yearPaySuccYn +" / moneyType : " + moneyType + " / addResult : " + addResult );
				
			}else{	// 결제실패시 내역 저장 2009.11.27 
				
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);
				veriResCode = "3";
				debug("결제실패내역저장결과  result_fail : " + result_fail);						
				
			}
   	
	        if(openerType.equals("N")){
	        	// 유료회원으로 업드레이드 하는 팝업에서 유입 
	        	returnUrlTrue = "GolfMemJoinEndPop.do";
	        	//returnUrlFalse =  "GolfMemJoinEndPop.do";
	        	returnUrlFalse =  "";
	        	if(payType.equals("3")){ // 타사카드결제 
		        	script = "parent.location.href='/app/golfloung/html/common/bcgolf_service_join.jsp'; window.close();";	        		
	        	}
	        	else{	// 비씨카드 또는 복합결제 // 마이페이지 > 유료회원전환  
	        		if(formtarget.equals("White")){
	        			script = "opener.parent.location.href='/app/golfloung/html/common/bcgolf_service_join.jsp'; window.close();";	 
	        		}else{
	        			script = "opener.parent.location.href='/app/golfloung/html/common/bcgolf_service_join.jsp'; window.close();";
	        		}
	        	}
	        	scriptFalse = "window.close();";    	

	        }else{
	        	returnUrlTrue = "GolfMemJoinEnd.do";
	        	returnUrlFalse =  "GolfMemJoinNoCard.do";
	        	script = "parent.location.href='/app/golfloung/html/common/member_join_finish.jsp'";
	        	scriptFalse = "";   
	        }

	        //VIP카드 번호 체크
	        debug("script: " + script + " / openerType : " + openerType + " / returnUrlTrue : " + returnUrlTrue);    
	        
	        if("N".equals(vipCardCheckYn) && "Y".equals(vipCardYn) && "3".equals(moneyType))
	        {
	        	debug("## VIP오류 출력");
	        	veriResCode = "3";
	        	request.setAttribute("script", "");
	        	if(openerType.equals("N")){
	        		request.setAttribute("returnUrl", "GolfMemJoinPop.do");
	        	}
	        	else
	        	{
	        		script = "parent.location.href='/app/golfloung/html/member/membership_guide/member_gold.jsp'";
	        		request.setAttribute("returnUrl", "GolfMemJoinNoCard.do");
	        		request.setAttribute("script", script);
	        	}
	        	
	        		        	
	        	request.setAttribute("resultMsg", "VIP혜택은 BC VIP카드로 인증하셔야 합니다. 플래티늄카드로 다시 시도하여주십시요. ");	
	        	
	        }
	        else
	        { 
	        	if (addResult == 2) {
					
	        		/*멤버쉽 결제시에만 세션 새로 설정   
	        		 * 결제를 통한 회원 분류는 멤버쉽이 항상 우선 순위이므로 월회원 결제시에는 세션 설정 하지 않는다.
	        		 */
	        		if (yearPaySuccYn){ 
	        			
						//카드회원인지 멤버쉽회원인지
						if("나의알파플래티늄".equals(strMem))
						{
							usrEntity.setIntMemberGrade((int)intMemGrade);
							if((int)intMemGrade < 2)
							{				
								usrEntity.setIntMemGrade((int)intMemGrade);
							}
							usrEntity.setCyberMoney(0);
						}
						else
						{
							usrEntity.setMemGrade(memGrade);
							usrEntity.setIntMemberGrade((int)intMemGrade);
							usrEntity.setIntMemGrade((int)intMemGrade);
							usrEntity.setCyberMoney(0);
						}
						
	        		}	        		

					if (email_id != null && !email_id.equals("")) {

						String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String emailTitle = "";
						String emailFileNm = "";
						try {
							EmailSend sender = new EmailSend();
							EmailEntity emailEtt = new EmailEntity("EUC_KR");
							
							emailTitle = "[Golf Loun.G] 골프라운지 서비스 가입을 축하드립니다.";
							emailFileNm = "/email_tpl19.html";
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+memGrade);
							
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							emailEtt.setTo(email_id);
							//sender.send(emailEtt);
						} catch(Throwable t) {}
					}
					
					//sms발송
					if (!userMobile.equals("")
							&& (intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE1")) 
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE2")) 
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE3"))  
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE5")) 
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE6"))  
									|| intMemGrade==Integer.parseInt(AppConfig.getDataCodeProp("0005CODE11")) ) 							
							&& !tmYn.equals("Y")) {

						// SMS 관련 셋팅
						try {
							HashMap smsMap = new HashMap();
							
							smsMap.put("ip", request.getRemoteAddr());
							smsMap.put("sName", userNm);
							smsMap.put("sPhone1", userMobile1);
							smsMap.put("sPhone2", userMobile2);
							smsMap.put("sPhone3", userMobile3);
							
	
							//debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
							String smsClss = "674";
							String message = "[Golf Loun.G] "+userNm+"님 "+memGrade+"회원 " + payWayTxt + " " +GolfUtil.comma(sum)+"원 결제되었습니다. 감사합니다.";
	
							if (joinChnl.equals("2000")) {
								message = "[Golf Loun.G] "+userNm+"님 "+memGrade+"회원 쿠폰이용으로 가입되셨습니다.감사합니다.";
							} else if (joinChnl.equals("1000")) {
								if (sale_amt > 0 )
								{
									message = "[Golf Loun.G] "+userNm+"님 "+memGrade+"회원 " + payWayTxt + " " +GolfUtil.comma(sum)+"원 "+String.valueOf(sale_amt)+"% 할인 결제되었습니다.감사합니다.";
								}
								
							} else {
								message = "[Golf Loun.G] "+userNm+"님 "+memGrade+"회원 " + payWayTxt + " " +GolfUtil.comma(sum)+"원 결제되었습니다.감사합니다.";								
							}
							
							SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
							String smsRtn = "";
							smsRtn = smsProc.send(smsClss, smsMap, message);
							
						} catch(Throwable t) {}
					}	
					
					//interpark이벤트 처리
					String isInterpark = (String)request.getSession().getAttribute("isInterpark");
					String currDate  = DateUtil.currdate("yyyyMMdd");
					debug("isInterpark>>>>>>>>>>>>>>>>>>>>>>>" + isInterpark);
					if(isInterpark == null){
						isInterpark = "N";
					}
		
					if(isInterpark.equals("Y") ){	
						if( intMemGrade<4 ){		//유료회원일경우만		
							debug("##################################인터파크 이벤트 처리 시작##############################");

							String from_date = "";
							String to_date   = "";
							String cupn      = ""; 

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
										debug("인터파크 이벤트정보 인서트 or 업데이트");
										doUpdate = (boolean) inter.insertCupnNumber(context, request, dataSet);
									}else{
										request.getSession().removeAttribute("isInterpark");
									}
								}	// synchronized

								if(cnt == 0){
									try {
										EmailSend sender = new EmailSend();
										EmailEntity emailEtt = new EmailEntity("EUC_KR");
										String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
										String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
										String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
										String emailTitle = "";
										String emailFileNm = "";
										String useYN = "N";
	
										if(doUpdate == true){	
											useYN = (String) inter.getUseYN(context, request, dataSet);
	
											request.setAttribute("useYN", useYN);
											debug("useYN>>>>>>>>>>>>>>>>>>>>>" + useYN);
											emailTitle = "골프라운지 회원가입 인터파크 할인쿠폰";
	
											if(useYN.equals("Y")){
												emailFileNm = "/email_interpark1.html";
											}else{
												emailFileNm = "/email_interpark.html";
											}
					
											emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);
											emailEtt.setFrom(emailAdmin);
											emailEtt.setSubject(emailTitle);
											emailEtt.setTo(email_id);
											sender.send(emailEtt);
										}
									} catch(Throwable t) {}
								}

							} else {	// if
								request.getSession().removeAttribute("isInterpark");
							}
						} else {
							request.getSession().removeAttribute("isInterpark");
						}
					}
					// interpark 이벤트 처리 종료

					debug("746. joinChnl : " + joinChnl + " / code : " + code + " / email_id : " + email_id + " / currDate : " + currDate); 
					if((Integer.parseInt("20100501") <= Integer.parseInt(currDate)) && (Integer.parseInt(currDate) <= Integer.parseInt("20100930"))){
						if(joinChnl.equals("1000") && ("EVENTGL12345".equals(code.toString().toUpperCase()) || "EVENTECHAMP201007".equals(code.toString().toUpperCase()) || "EVENTLETTER08".equals(code.toString().toUpperCase()))){
							debug("### DM 이벤트 회원에게 영화쿠폰다운로드 메일 발송 ##" + currDate);
		
							try{
								EmailSend sender = new EmailSend();
								EmailEntity emailEtt = new EmailEntity("EUC_KR");
								String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
								String imgPath = "<img src=\"";
								String hrefPath = "<a href=\"";
								String emailTitle = "";
								String emailFileNm = "";
		
								emailTitle = "[Golf Loun.G] 골프라운지 영화예매권";
								emailFileNm = "/eamil_tm_movie.html";
								emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, "");
								
								emailEtt.setFrom(emailAdmin);
								emailEtt.setSubject(emailTitle);
								emailEtt.setTo(email_id);
								sender.send(emailEtt);
								
								debug("메일전송 성공 : emailTitle : " + emailTitle + " / email_id : " + email_id);
							}catch(Exception e){
								debug("메일 전송 실패 : email_id : " + email_id);
							}
						}
					}
				
					request.setAttribute("script", script);
					request.setAttribute("returnUrl", returnUrlTrue);
					//request.setAttribute("resultMsg", "등록이 정상적으로 처리 되었습니다."); 
					
					
		        } else if (addResult == 9) { //한번 더 체크함
		        	// DB저장 실패시 승인취소 전문	        	
					debug("====================GolfMemInsActn =============DB저장 실패시 승인취소 전문 1 ");
					
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
						  result = addPayProc.execute(context, dataSet);											
					  }else{	// 결제 취소 실패
						  result = addPayProc.failExecute(context, dataSet, request, payEtt);													
					  }								  
					}

					request.setAttribute("script", scriptFalse);
		        	request.setAttribute("returnUrl", returnUrlFalse);
					request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");	
		        } else {
		        	// DB저장 실패시 승인취소 전문 
					debug("====================GolfMemInsActn =============DB저장 실패시 승인취소 전문 2 ");
					
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
					  
					  if (payCancelResult) {	// 결제 취소 성공
						  result = addPayProc.execute(context, dataSet);										
					  }else{	// 결제 취소 실패
						  result = addPayProc.failExecute(context, dataSet, request, payEtt);															
					  }								  
					}
					
					debug("결제취소성공여부 result : " + result);					
					  				
					request.setAttribute("script", scriptFalse);
		        	request.setAttribute("returnUrl", returnUrlFalse);

		        	if (!yearPaySuccYn){
		        		if (!monPaySuccYn){						
							request.setAttribute("resultMsg", "이미 월회원에 가입 되셨습니다.");
		        		}
					}else{
						request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");
					}
							        		
		        }	
	        }
	        			
			// 05. Return 값 세팅			
			paramMap.remove("moneyNo");
			paramMap.remove("YR");
			paramMap.remove("MN");
			paramMap.remove("GRD_NM");
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			
			debug(TITLE, t);			
			throw new GolfException(TITLE, t);
			
		} finally {
			
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
			
			/*멤버쉽 결제에는 해당 안됨; 즉, 월회원 결제로직에만 해당
			 * 결제 후 골프라운지 비즈니스단 DAO처리중 Exception이 발생하면 위 승인취소 로직을 거치지 못함
			 * 멤버쉽도 마찬가지일거 같으나, 우선 새로 추가되는 월회원에 대한 Exception만 처리
			 * 근본적으로 해결하기 위해선 로직을 새로 정비해야함
			 */
			if (!yearPaySuccYn){
				
				if (!monPaySuccYn){
					
					veriResCode = "3";
					
					// 비씨카드 ; 월회비 결제는 비씨카드 결제수단만 있음
					if(payType.equals("1") ){
			        	if(!GolfUtil.empty(payEtt.getUseNo())){
			        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
			        	}				
					}
					
					request.setAttribute("script", scriptFalse);
		        	request.setAttribute("returnUrl", returnUrlFalse);
					request.setAttribute("resultMsg", "월회원 등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");
					
				}
				
			}
			
			if(ispAccessYn.equals("Y")){
			
				//ISP인증 로그 기록
				HashMap hmap = new HashMap();
				hmap.put("ispAccessYn", ispAccessYn);
				hmap.put("veriResCode", veriResCode);
				hmap.put("title", TITLE);
				hmap.put("memName", userNm);
				hmap.put("memSocid", userSocid);
				hmap.put("ispCardNo", ispCardNo);
				hmap.put("cstIP", userIP);
				hmap.put("className", "GolfMemInsActn");
				
				ISPCommon ispCoomon = new ISPCommon();
				ispCoomon.ispRecord(context, hmap);
			
			}
			
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
