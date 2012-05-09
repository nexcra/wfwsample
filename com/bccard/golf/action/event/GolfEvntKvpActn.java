/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntKvpActn 
*   작성자	: (주)미디어포스 임은혜
*   내용		: KVP 처리
*   적용범위	: golf
*   작성일자	: 2010-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

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

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.GolfEvntKvpDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntKvpActn extends GolfActn{
	
	public static final String TITLE = "KVP 처리";
	private static final String SITEID = "I829";		// 한신평 코드
	private static final String SITEPW = "44463742";	// 한신평 PASSWORD

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();											// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
		
		GolfEvntKvpDaoProc proc = (GolfEvntKvpDaoProc)context.getProc("GolfEvntKvpDaoProc");	// KVP 저장
				

		try {

			String socid = ""; 			// 주민번호
			String social_id_1 = "";
			String social_id_2 = ""; 
			String name = ""; 
			String ddd_no = ""; 
			String tel_hno = ""; 
			String tel_sno = ""; 
			String hp_ddd_no = ""; 
			String hp_tel_hno = ""; 
			String hp_tel_sno = ""; 
			String email = "";  
			String idx = ""; 
			String realPayAmt = "";

			// 후처리
			String resultMsg = "";
			String script = "";
			boolean payResult = false;
			boolean payCancelResult = false;
			int addResult = 0;
			
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

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


			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			String order_no = addPayProc.getOrderNo(context, dataSet);
			debug("order_no : " + order_no);
			
			dataSet.setString("socid", socid);
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

			
//
//			/***주문정보저장 end **결제 start***********************************************/
//			
//			//debug("// STEP 1. 입력값에 대한 세션체크+");
//			String st_s = (String) request.getSession().getAttribute("ParameterManipulationProtectKey");
//			if ( st_s == null ) st_s = "";
//			request.getSession().removeAttribute("ParameterManipulationProtectKey");
//
//			String st_p = request.getParameter("ParameterManipulationProtectKey");
//			if ( st_p == null ) st_p = "";
//
//			if ( !st_p.equals(st_s) ) {
//				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"ETAX.PARAM.PROTECT", null);
//			//	throw new EtaxException(msgEtt);
//			}				
//
			// 결제정보 변수 => 공통
			String payType 			= "1";												// 1:카드 2:카드+포인트 3:타사카드
			String ip 				= request.getRemoteAddr();  
			String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD");				// 가맹점 번호 골프라운지 골프패키지	770119761
			String iniplug 			= parser.getParameter("KVPpluginData", "");			// ISP 인증값

			String cardNo			= parser.getParameter("card_no", "0");				// 카드번호
			String insTerm			= parser.getParameter("ins_term", "00");			// 할부개월수
			String siteType			= parser.getParameter("site_type", "1");			// 사이트 구분 1: 비씨, 2:지자체	
			
						
			// 비씨카드 debug("// STEP 1_2. 파라미터 입력"); 
			HashMap kvpMap = null;
			String user_r      = StrUtil.isNull(parser.getParameter("user_r"),"");			// 사용자 아이디
			String signed_data = StrUtil.isNull(parser.getParameter("signed_data"),"");		// 사인값
			String pcg         = "";														// 개인/법인 구분
			String ispCardNo   = "";														// isp카드번호
			String valdlim	   = "";														// 만료 일자
			String pid = null;																// 개인아이디
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
			
			// 결제정보 저장 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType, 4, "0");
			String sttl_gds_clss = "9"+GolfUtil.lpad(idx, 3, "0");
//			
//			
//			if(iniplug !=null && !"".equals(iniplug)) {
//				kvpMap = payProc.getKvpParameter( iniplug );
//			}	
//			
//			if(kvpMap != null) {
//				pcg         = (String)kvpMap.get("PersonCorpGubun");		// 개인/법인 구분
//				ispCardNo   = (String)kvpMap.get("CardNo");					// isp카드번호
//				valdlim		= (String)kvpMap.get("CardExpire");				// 만료 일자
//				if ( "2".equals(pcg) ) {
//					pid = (String)kvpMap.get("BizId");								// 사업자번호
//				} else {
//					pid = (String)kvpMap.get("Pid");									// 개인 주민번호
//				}
//			} else {
//				ispCardNo = parser.getParameter("isp_card_no","");	// 하나비자카드 경우
//			}
//			
//			if ( valdlim.length() == 6 ) {
//				valdlim = valdlim.substring(2);											
//			}					
//
//			//debug("// STEP 5. 승인처리");
//			payEtt.setMerMgmtNo(merMgmtNo);
//			payEtt.setCardNo(ispCardNo);
//			payEtt.setValid(valdlim);			
//			payEtt.setAmount(realPayAmt);
//			payEtt.setInsTerm(insTerm);
//			payEtt.setRemoteAddr(ip);				 
//
//			payResult = payProc.executePayAuth(context, request, payEtt);			// 승인전문 호출
			dataSet.setString("AUTH_NO", payEtt.getUseNo());
			dataSet.setString("STTL_MINS_NM", "비씨카드");	// 신용카드 이름(계좌이체 은행이름)						  

			
			// 결제 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
			dataSet.setString("ORDER_NO", order_no);
			dataSet.setString("CDHD_ID", socid);
			dataSet.setString("STTL_AMT", realPayAmt);				// 결제 금액
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_GDS_SEQ_NO", idx);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());

			debug("결제관련 변수 셋팅 => merMgmtNo(가맹점번호) : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
					 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip + " / idx : " + idx);

			payResult = true;	// 테스트
			if (payResult) {
				addResult = proc.execute(context, request, dataSet);		
				debug("GolfEvntBnstPayActn : 주문 테이블 업데이트 = addResult2 : " + addResult);	

				if (addResult > 0) {
					addResult = addResult + addPayProc.execute(context, dataSet);					
					debug("GolfEvntBnstPayActn : 결제 저장 = addResult3 : " + addResult);	
				}
			}else{	// 결제실패시 내역 저장 2009.11.27 
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
				debug("GolfEvntBnstPayActn = result_fail : " + result_fail);
			}

			if(addResult > 1 ){
				resultMsg = "결제가 완료 되었습니다.";
			}else{	
	        	if(!GolfUtil.empty(payEtt.getUseNo())){
	        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
	        	}
	        	resultMsg = "결제에 실패했습니다. 다시 시도해 주시기 바랍니다.";
			}		

			script = "top.window.close();";
			request.setAttribute("resultMsg", resultMsg);
			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
