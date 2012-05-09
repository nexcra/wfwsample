/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsSkiActn
*   작성자    : 미디어포스 진현
*   내용      : 스키이벤트권 등록
*   적용범위  : golf 
*   작성일자  : 2009-12-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

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
import com.bccard.golf.common.ResultException;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemSkiInsDaoProc;
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
* @author	미디어포스  
* @version	1.0 
******************************************************************************/
public class GolfMemInsSkiActn extends GolfActn{
	
	public static final String TITLE = "스키이벤트권 등록";  

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

		// 후처리 관련 변수
		boolean payResult = false;
		boolean payCancelResult = false;
		int addResult = 0;
		int		sale_amt = 0;

		String returnUrlTrue = ""; 
    	String returnUrlFalse =  "";
    	String script = ""; 
    	String scriptFalse = "";
    	String strMem = "";
		String sum = "0";
		String couponYN = "N";
		ResultException re;
		String addButton = "<img src='/img/bbs/bt1_confirm.gif' border='0'>"; // 버튼
		String goPage = "/app/card/memberActn.do"; // 이동 액션
		int intUsrGrad = 0; //로그인유저의 등급
		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		
		try {
			// 01.세션정보체크 
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
				userMobile1 	= (String)usrEntity.getMobile1();
				userMobile2 	= (String)usrEntity.getMobile2();
				userMobile3 	= (String)usrEntity.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
				strMem 		= (String)usrEntity.getMemGrade();
				
				//로그인 유저의 등급 setIntMemGrade  1:챔피온 / 2:블루 / 3.골드 / 4.화이트 5.카드
				intUsrGrad 	= usrEntity.getIntMemGrade();
				
				System.out.print("## strMem:"+strMem);
			} else {
				// 인증실패 - 오류
				re = new ResultException();
				re.setTitleImage("error");
				re.setTitleText(TITLE);
				re.setKey("USERCERT_ERROR");
				re.addButton(goPage, addButton);
				throw re;
			}
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String payType 				= parser.getParameter("payType", "").trim();	// 1:카드 2:카드+포인트
			String moneyType 			= parser.getParameter("moneyType", "").trim();	
			// 1:champion(200,000) 2:Black(150,000) 3:blue(50,000) 4:gold(25,000) 5:White(무료)
			String memType 				= parser.getParameter("memType", "").trim();	// 회원구분 - 정회원 : 1 비회원:2
			String insType				= parser.getParameter("insType", "").trim();	// 유입경로 - TM : 1 일반 : ""
			String openerType			= parser.getParameter("openerType", "").trim();	// N:업그레이드 회원
			String realPayAmt			= parser.getParameter("realPayAmt", "").trim();	// 실결제금액
			String tmYn					= parser.getParameter("tmYn", "").trim();		// Y:TM 고객
			String type 				= parser.getParameter("type", "");
			
			String code					= parser.getParameter("code", "").trim();		//제휴구분코드
			String joinChnl				= "2302";

			//-- 2009.11.12 추가 
			String cupn_type 			= parser.getParameter("cupn_type", "");
			String pmgds_pym_yn 		= parser.getParameter("pmgds_pym_yn", "");

			debug("=`==`=`=`=`=`=`=`=` 스키이벤트권 관련 변수 셋팅 ");
			debug("===================payType : " + payType);
			debug("===================moneyType : " + moneyType);
			debug("===================memType : " + memType);
			debug("===================insType : " + insType);
			debug("===================openerType : " + openerType);
			debug("===================realPayAmt : " + realPayAmt);
			debug("===================cupn_type : " + cupn_type);
			debug("===================pmgds_pym_yn : " + pmgds_pym_yn);
			debug("===================tmYn : " + tmYn);

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setString("payType", payType);	
			dataSet.setString("moneyType", moneyType);	
			dataSet.setString("memType", memType);	
			dataSet.setString("insType", insType);	
			
			dataSet.setInt("intUsrGrad", intUsrGrad);			//로그인유저의 등급
						
			dataSet.setString("CODE", code); 					//쿠폰코드  
			dataSet.setString("SITE_CLSS", "10");				//사이트구분코드 10:골프라운지
			dataSet.setString("EVNT_NO", "111");				//이벤트번호1
			dataSet.setString("EVNT_NO2", "112");				//이벤트번호2 
			dataSet.setString("CUPN_TYPE", cupn_type);			//쿠폰구분 
			dataSet.setString("PMGDS_PYM_YN", pmgds_pym_yn);	//경품지급여부 

			// 04.실제 테이블(Proc) 조회  
			GolfMemSkiInsDaoProc procSky = (GolfMemSkiInsDaoProc)context.getProc("GolfMemSkiInsDaoProc");
			
			// 결제 테이블에 결제 결과 저장 
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");

			System.out.print("## userId : "+userId+" | intUsrGrad "+intUsrGrad+" | moneyType: "+moneyType+"\n");
			 
			// 블루 회원 등급 가져오기 					
			DbTaoResult gradeView = procSky.gradeExecute(context, dataSet, request);
			debug("===================gradeView : " + gradeView);
			if (gradeView != null && gradeView.isNext()) {
				gradeView.first();
				gradeView.next();
				debug("===================memGrade : " + gradeView.getString("RESULT"));
				if(gradeView.getString("RESULT").equals("00")){
					memGrade = (String) gradeView.getString("memGrade");	
					intMemGrade = (int) gradeView.getInt("intMemGrade");	
				}
				
			}
			debug("=`==`=`=`=`=`=`=`=` 회원등급 관련 변수 셋팅 ");
			debug("===================memGrade : " + memGrade);
			debug("===================intMemGrade : " + intMemGrade);
			
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
			debug("=`==`=`=`=`=`=`=`=` 결제금액");
			debug("===================sum : " + sum);
			debug("===================norm_amt : " + norm_amt);

			dataSet.setString("CODE_NO", "SKI"); 			//제휴업체코드
			dataSet.setString("JOIN_CHNL", joinChnl);
			dataSet.setString("CUPN_CTNT", ctnt);
			dataSet.setString("CUPN_NO", code_no); 			//신용카드번호에 입력할것임.
			dataSet.setString("NORM_AMT", norm_amt); 		//정상요금
			dataSet.setString("DC_AMT", dc_amt);			//할인금액
			dataSet.setString("STTL_AMT", sum); 			//결제금액
			dataSet.setString("CUPN_CLSS", cupn_clss); 		//쿠폰구분
			dataSet.setString("CODE_EVNT_NO", evnt_no); 	//쿠폰이벤트번호
			

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
			String ip = request.getRemoteAddr();  
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD");		// 가맹점 번호(766559864) //topn : 745300778
			String iniplug = parser.getParameter("KVPpluginData", "");	// ISP 인증값

			String cardNo		= parser.getParameter("card_no", "0");				// 카드번호
			String insTerm		= parser.getParameter("ins_term", "00");			// 할부개월수
			String siteType		= parser.getParameter("site_type", "1");			// 사이트 구분 1: 비씨, 2:지자체	
			
			//debug("// STEP 1_2. 파라미터 입력");
			HashMap kvpMap = null;
			if(iniplug !=null && !"".equals(iniplug)) {
				kvpMap = payProc.getKvpParameter( iniplug );
			}			

			//debug("// STEP 1_3. 공인인증값이 있을 경우 유효성 검사..");
			String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// 사용자 아이디
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// 사인값
			String pcg         = "";														// 개인/법인 구분
			String ispCardNo   = "";														// isp카드번호
			String valdlim	   = "";														// 만료 일자
			String pid = null;																// 개인아이디

			if(kvpMap != null) {
				pcg         = (String)kvpMap.get("PersonCorpGubun");		// 개인/법인 구분
				ispCardNo   = (String)kvpMap.get("CardNo");					// isp카드번호
				valdlim		= (String)kvpMap.get("CardExpire");				// 만료 일자
				if ( "2".equals(pcg) ) {
					pid = (String)kvpMap.get("BizId");								// 사업자번호
				} else {
					pid = (String)kvpMap.get("Pid");									// 개인 주민번호
				}
			} else {
				ispCardNo = 	parser.getParameter("isp_card_no","");	// 하나비자카드 경우
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

			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			if( "211.181.255.40".equals(host_ip)) {
				payResult = payProc.executePayAuth(context, request, payEtt);			// 승인전문 호출
				
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////
				//payResult=true;
//////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////

			} else {
				payResult = payProc.executePayAuth(context, request, payEtt);			// 승인전문 호출
			}
			// 결제 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = "000"+payType;
			String sttl_gds_clss = "000"+moneyType;
			
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());

			if("211.181.255.40".equals(host_ip)) {
			//	dataSet.setString("AUTH_NO", "");
				dataSet.setString("AUTH_NO", payEtt.getUseNo());

			} else {
				dataSet.setString("AUTH_NO", payEtt.getUseNo());
			}

			debug("=`==`=`=`=`=`=`=`=` 결제관련 변수 셋팅 ");
			debug("===================merMgmtNo : " + merMgmtNo);
			debug("===================ispCardNo : " + ispCardNo);
			debug("===================valdlim : " + valdlim);
			debug("===================sum : " + sum);
			debug("===================insTerm : " + insTerm);
			debug("===================ip : " + ip);


			// 04.결제처리	

			// 결제 승인 완료 
			//payResult = true;
			if (payResult) { // 꼭~~ 수정해야 할 사항
				
				addResult = addResult + procSky.executeSky(context, dataSet, request);		

				debug("결제자료 입력");
				debug("===================addResult : " + addResult);
				debug("===================intMemGrade : " + intMemGrade);
				debug("===================tmYn : " + tmYn);

				if (addResult == 1 && (intMemGrade<4 || intMemGrade==7) && !tmYn.equals("Y")) {
					debug("===================완료 ");
					// 결제 저장 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					addResult = addResult + addPayProc.execute(context, dataSet);
				}
			}			

			returnUrlTrue = "/app/golfloung/golfMemSkiPop.do";
        	returnUrlFalse =  "/app/golfloung/golfMemSkiPop.do";
        	//script = "alert('처리가 완료되었습니다.'); self.close();";
        	//script = "if(opener.opener) { opener.opener.golfMemSkiPop(); } else { self.close(); }";
        	scriptFalse = "";
        	if ("3".equals(type)) {
        		script = "opener.golfMemSkiPop(); opener.parent.document.location.href='/'; self.close();";
        	}

			if (addResult == 2) {
				//카드회원인지 멤버쉽회원인지
				if("나의알파플래티늄".equals(strMem)) {
					usrEntity.setIntMemberGrade((int)intMemGrade);
					if((int)intMemGrade < 2) {				
						usrEntity.setIntMemGrade((int)intMemGrade);
					}
					usrEntity.setCyberMoney(0);
				} else {
					
					// 챔피온등급이 아니라면 다시 등급세션굽기
					if( intUsrGrad != 1)
					{
					usrEntity.setMemGrade(memGrade);
					usrEntity.setIntMemberGrade((int)intMemGrade);
					usrEntity.setIntMemGrade((int)intMemGrade);
					usrEntity.setCyberMoney(0);
					}
				}

				request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);

	        } else if (addResult == 9) { //한번 더 체크함
	        	// DB저장 실패시 승인취소 전문	        	
				debug("====================GolfMemInsSkiActn =============DB저장 실패시 승인취소 전문 1 ");
	        	if(!GolfUtil.empty(payEtt.getUseNo())){
	        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
	        	}

				request.setAttribute("script", scriptFalse);
	        	request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");	
	        } else {
	        	// DB저장 실패시 승인취소 전문 
				debug("====================GolfMemInsSkiActn =============DB저장 실패시 승인취소 전문 2 ");
	        	if(!GolfUtil.empty(payEtt.getUseNo())){
	        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
	        	}

				request.setAttribute("script", scriptFalse);
	        	request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }	

			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
	}
}
