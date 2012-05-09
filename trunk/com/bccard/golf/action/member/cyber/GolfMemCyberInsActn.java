/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsActn
*   작성자    : 미디어포스 임은혜
*   내용      : 가입 > 등록
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member.cyber;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.member.cyber.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.common.AppConfig;


import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.TopPointInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.info.GolfPointInfoResetJtProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0
******************************************************************************/
public class GolfMemCyberInsActn extends GolfActn{
	
	public static final String TITLE = "사이버머니  > 등록처리";

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
		String email_id = "";
		String userMobile = "";
		String userMobile1 = "";
		String userMobile2 = "";
		String userMobile3 = "";
		int cyberMoney = 0;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

		try {
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
		 	
			 if(userEtt != null) {
				userNm			= (String)userEtt.getName(); 
				userId			= (String)userEtt.getAccount(); 
				email_id 		= (String)userEtt.getEmail1(); 
				userMobile1 	= (String)userEtt.getMobile1();
				userMobile2 	= (String)userEtt.getMobile2();
				userMobile3 	= (String)userEtt.getMobile3();
				userMobile		= userMobile1+userMobile2+userMobile3;
				cyberMoney 		= (int)userEtt.getCyberMoney();
			}
			 /*
			debug("==========email_id========> " + email_id);
			debug("==========userMobile1========> " + userMobile1);
			debug("==========userMobile2========> " + userMobile2);
			debug("==========userMobile3========> " + userMobile3);
			email_id = "simijoa@hanmail.net";
			userMobile1 = "010";
			userMobile2 = "9192";
			userMobile3 = "4738";
			*/
			
			// 02.입력값 조회	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			int amount 				= parser.getIntParameter("amount", 0);
			String payType 			= parser.getParameter("payType", "");
			debug("GolfMemCyberInsDaoProc =============== amount => " + amount);
			debug("GolfMemCyberInsDaoProc =============== payType => " + payType);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			dataSet.setInt("amount", amount);	
			dataSet.setString("payType", payType);	
						
			// 04.실제 테이블(Proc) 조회
			GolfMemCyberInsDaoProc proc = (GolfMemCyberInsDaoProc)context.getProc("GolfMemCyberInsDaoProc");
			GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
			

			boolean payResult = false;
			int addResult = 0;
			debug("+1+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");	
			
			debug("// STEP 1. 입력값에 대한 세션체크+");
			String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
			if ( st_s == null ) st_s = "";
			request.getSession().removeAttribute("ParameterManipulationProtectKey");

			String st_p = request.getParameter("ParameterManipulationProtectKey");
			if ( st_p == null ) st_p = "";

			if ( !st_p.equals(st_s) ) {
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
			//	throw new EtaxException(msgEtt);
			}

			debug("st_s :: >> " + st_s);
			debug("st_p :: >> " + st_p);
			
				
			// 공통 
			String ip = request.getRemoteAddr();  
			String merMgmtNo = AppConfig.getAppProperty("MBCDHD");		// 가맹점 번호(766559864) //topn : 745300778
			String iniplug = parser.getParameter("KVPpluginData", "");	// ISP 인증값
			String sum		 = parser.getParameter("realPayAmt", "0");	// 결제금액
			if(sum != null && !"".equals(sum)){
				sum = StrUtil.replace(sum,",","");
			}

			String cardNo		= parser.getParameter("card_no", "0");				// 카드번호
			String insTerm		= parser.getParameter("ins_term", "00");			// 할부개월수
			debug("==========insTerm========> " + insTerm);
			String siteType		= parser.getParameter("site_type", "1");			// 사이트 구분 1: 비씨, 2:지자체	
			
			debug("// STEP 1_2. 파라미터 입력");
			HashMap kvpMap = null;
			if(iniplug !=null && !"".equals(iniplug)) {
				kvpMap = payProc.getKvpParameter( iniplug );
			}			

			debug("// STEP 1_3. 공인인증값이 있을 경우 유효성 검사..");
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
			debug("// STEP 5. 승인처리");
			payEtt.setMerMgmtNo(merMgmtNo);
			payEtt.setCardNo(ispCardNo);
			payEtt.setValid(valdlim);			
			payEtt.setAmount(sum);
			payEtt.setInsTerm(insTerm);
			payEtt.setRemoteAddr(ip);

			payResult = payProc.executePayAuth(context, request, payEtt);			// 승인전문 호출
			

			debug("====================GolfMemInsActn =============payResult => " + payResult);
			debug("+4++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
			

			// 결제 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_MTHD_CLSS = "000"+payType;	// 결제 방법 구분코드
			String sttl_GDS_CLSS = "0004";	// 결제 상품 구분 코드
			
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("STTL_MTHD_CLSS", sttl_MTHD_CLSS);
			dataSet.setString("STTL_GDS_CLSS", sttl_GDS_CLSS);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("STTL_AMT", sum);
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());
			dataSet.setString("AUTH_NO", payEtt.getUseNo());
			

			// 04.결제처리	
			if (payResult) { // 꼭~~ 수정해야 할 사항
				addResult = proc.execute(context, dataSet, request);	

				if (addResult == 1) {
					// 결제 저장 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					addResult = addResult + addPayProc.execute(context, dataSet);
				}
			}			
			

			// SMS 관련 셋팅
			HashMap smsMap = new HashMap();
			
			smsMap.put("ip", request.getRemoteAddr());
			smsMap.put("sName", userNm);
			smsMap.put("sPhone1", userMobile1);
			smsMap.put("sPhone2", userMobile2);
			smsMap.put("sPhone3", userMobile3);
			
	        String returnUrlTrue = "GolfMemCyberInsForm.do";
	        String returnUrlFalse =  "GolfMemCyberInsForm.do";
			
			if (addResult == 2) {

				cyberMoney = cyberMoney+Integer.parseInt(sum);
				userEtt.setCyberMoney(cyberMoney);

				//sms발송
				if (!userMobile.equals("")) {
					
					debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "660";
					String message = "[Golf Loun.G] "+userNm+"님 사이버머니 "+GolfUtil.comma(sum)+"원이 충전되었습니다. 감사합니다.";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}				
				
				request.setAttribute("returnUrl", returnUrlTrue);
				request.setAttribute("resultMsg", "구매가 정상적으로 처리 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "구매가 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
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
