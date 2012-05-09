/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntCouponOrdActn
*   작성자    : (주)미디어포스 이경희
*   내용      : 이벤트라운지/골프라운지이벤트/진행중인이벤트/그린피할인쿠폰->결제
*   적용범위  : Golf
*   작성일자  : 2011-04-12
************************** 수정이력 ****************************************************************
*    일자  작성자   변경사항
20110419  이경희   복합결제시 할부기능 제거 및 복합결제 일시불시 60처리
***************************************************************************************************/
package com.bccard.golf.action.event.coupon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.ispCert.ISPCommon;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.coupon.GolfEvntCouponOrdProc;
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
public class GolfEvntCouponOrdActn extends GolfActn{
	
	public static final String TITLE = "쿠폰 주문 결제";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");
		GolfPaymentRegDaoProc addPayProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");
		
		String ispAccessYn  = "N";;
		String veriResCode = "1"; // 검증결과 코드 (1: 정상주문완료   3:주문오류시)		
		String memName = "";			
		String ispCardNo = "";	// isp카드번호
		String cstIP = request.getRemoteAddr(); //접속IP
		String pid = null;						// 개인아이디		
		
		try { 

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);			

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			if(usrEntity != null) {
				memName		= (String)usrEntity.getName();				
			}else {
				memName = (String)parser.getParameter("userNm","").trim();				
			}
			
			// 후처리
			String script = "";
			boolean payResult = false;
			boolean payCancelResult = false;
			int addResult = 0;

			// 상품 정보
			//String order_no			= parser.getParameter("order_no", "");			// 주문코드
			String qty				= parser.getParameter("qty","");				// 수량
			String int_atm			= parser.getParameter("int_atm","");			// 상품금액
			String realPayAmt		= parser.getParameter("realPayAmt", "0");		// 결제 금액
			
			// 고객정보 - 구매자
			String juminno1			= parser.getParameter("juminno1","");	
			String juminno2			= parser.getParameter("juminno2","");	
			String userNm			= parser.getParameter("userNm","");	
			String userId			= parser.getParameter("userId","");	
			String mobile1			= parser.getParameter("mobile1","");	
			String mobile2			= parser.getParameter("mobile2","");	
			String mobile3			= parser.getParameter("mobile3","");						
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			String order_no = addPayProc.getOrderNo(context, dataSet); //주문코드 가져오기

			dataSet.setString("ORDER_NO", order_no);
			dataSet.setString("qty", qty);
			dataSet.setString("int_atm", int_atm);
			dataSet.setString("realPayAmt", realPayAmt);
			
			dataSet.setString("juminno1", juminno1);
			dataSet.setString("juminno2", juminno2);
			dataSet.setString("socid", juminno1+juminno2);
			dataSet.setString("userId", userId);
			dataSet.setString("userNm", userNm);
			dataSet.setString("mobile1", mobile1);
			dataSet.setString("mobile2", mobile2);
			dataSet.setString("mobile3", mobile3);
			
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
			//String merMgmtNo 		= AppConfig.getAppProperty("MBCDHD3");				// 가맹점 번호 769835680-골프라운지용품
			String merMgmtNo 		= "770119761";										// 가맹점 번호 770119761 - 그린피 할인쿠폰  사용시 가맹점번호 - 양혜정계장 할당
			String iniplug 			= parser.getParameter("KVPpluginData", "");			// ISP 인증값			
			String insTerm			= parser.getParameter("ins_term", "00");			// 할부개월수
						
			// 비씨카드 debug("// STEP 1_2. 파라미터 입력"); 
			HashMap kvpMap = null;
			String pcg         = "";														// 개인/법인 구분			
			String valdlim	   = "";														// 만료 일자
			
			String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();


			// 결제정보 저장 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			String sttl_mthd_clss = GolfUtil.lpad(payType+"", 4, "0");
			String sttl_gds_clss = "0020";
			
			if (payType.equals("2")){
				insTerm = "60";
			}		
			
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
			
			// 결제 관련 세팅 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::	
			dataSet.setString("CDHD_ID", userId);
			dataSet.setString("STTL_AMT", realPayAmt);				// 결제 금액
			dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);
			dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);
			dataSet.setString("STTL_STAT_CLSS", "N");
			dataSet.setString("MER_NO", merMgmtNo);
			dataSet.setString("CARD_NO", ispCardNo);
			dataSet.setString("VALD_DATE", valdlim);
			dataSet.setString("INS_MCNT", insTerm.toString());

			debug("결제관련 변수 셋팅 => merMgmtNo : " + merMgmtNo + " / ispCardNo : " + ispCardNo + " / valdlim : " + valdlim
					 + " / realPayAmt : " + realPayAmt + " / insTerm : " + insTerm + " / ip : " + ip);
			
			
			HashMap valMap = new HashMap();
			GolfEvntCouponOrdProc proc = (GolfEvntCouponOrdProc)context.getProc("GolfEvntCouponOrdProc");

			if (payResult) {

				//쿠폰할당				
				valMap = proc.cupnAlloc(context, request, dataSet);	
				debug("GolfEvntCouponOrdProc = valMap : " + valMap);
				
				addResult = Integer.parseInt(valMap.get("resultCnt").toString());
				debug("GolfEvntCouponOrdProc = addResult : " + addResult);
				
				if (addResult == 1) {
					// 결제 저장 ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
					addResult = addResult + addPayProc.execute(context, dataSet);					
					debug("GolfEvntCouponOrdProc = addResult3 : " + addResult);	
				}					
				
			}else{	// 전문 승인 실패시 내역 저장
				
				veriResCode = "3";
				
				int result_fail = addPayProc.failExecute(context, dataSet, request, payEtt);	
				debug("GolfEvntCouponOrdProc = result_fail : " + result_fail);

			}
				
			if(addResult == 2 ){ // 전문승인 ok  &  쿠폰할당 ok &  결제관리테이블 저장 ok
				
				String str_hp = "0000";
				String hp	  = "";				
				Vector cupnV = new Vector();
				
				if (mobile1.length()>=3 && mobile2.length()>=3 && mobile2.length()>=4 )	{
					if ( mobile2.indexOf(str_hp) ==  -1 ) {
						hp = mobile1 + mobile2 + mobile2;
					}
				}
				
				cupnV = (Vector)valMap.get("couponNo");				

				// SMS 관련 셋팅
				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", mobile1);
				smsMap.put("sPhone2", mobile2);
				smsMap.put("sPhone3", mobile3);
				smsMap.put("sCallCenter", "15666578");
				
				String smsClss = "674";				
				String message = "";
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = "";
				
				if (hp.length() > 9) {
					
					for (int i=0; i<cupnV.size(); i++){	
						
						message = "[BC골프라운지] 숲에그린 할인쿠폰  번호 : " + cupnV.elementAt(i).toString();
						
						//SMS발송
						smsRtn = smsProc.send(smsClss, smsMap, message);
						info("[숲에그린 할인쿠폰 SMS 발송] 핸드폰번호 |" + mobile1 +"-"+  mobile2 +"-"+ mobile3 + "|메세지|" + message);
						message = "";
						
					}
				}

				info ("쿠폰주문번호 : " + order_no + "/ 전문승인 ok  &  쿠폰할당 ok &  결제관리테이블 저장 ok");
				script = "alert('구매가 완료 되었습니다.'); location.href='html/event/bcgolf_event/progress_event.jsp';";
			
				
			}else if(addResult == 1 ){ // 전문승인 ok & 쿠폰할당 ok & 결제관리테이블 저장 실패
				
				veriResCode = "3";		
	
				//쿠폰할당 취소	
				proc.cupnAlloCancel(context, request, dataSet, valMap);
				debug("GolfEvntCouponOrdProc = valMap : " + valMap);		

				// 비씨카드 또는 복합결제인 경우
				if(payType.equals("1") || payType.equals("2")){									
		        	if(!GolfUtil.empty(payEtt.getUseNo())){
		        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);	// 취소전문 호출
		        	}
				}
				
				info ("쿠폰주문번호 : " + order_no + "/ 전문승인 ok & 쿠폰할당 ok & 결제관리테이블 저장 실패");
				script = "alert('결제 오류로 인해 구매에 실패했습니다.'); location.href='html/event/bcgolf_event/progress_event.jsp';";
				
							
			}else{ // 전문승인 실패 
				
				veriResCode = "3";

				// 비씨카드 또는 복합결제인 경우
				if(payType.equals("1") || payType.equals("2")){									
		        	if(!GolfUtil.empty(payEtt.getUseNo())){
		        		payCancelResult = payProc.executePayAuthCancel(context, payEtt);	// 취소전문 호출
		        	}
				}
				info ("쿠폰주문번호 : " + order_no + "/ 전문승인 실패 ");
				script = "alert('결제 승인 오류로 인해 구매에 실패했습니다.'); location.href='html/event/bcgolf_event/progress_event.jsp';";
				
			}			
			
			request.setAttribute("script", script);
			request.setAttribute("paramMap", paramMap);
	        
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
