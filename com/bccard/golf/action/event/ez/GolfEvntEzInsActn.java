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
package com.bccard.golf.action.event.ez;

import java.io.IOException;
import java.net.InetAddress;
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

import com.bccard.common.NameCheck;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.ez.GolfEvntEzInsDaoProc;
import com.bccard.golf.dbtao.proc.event.kvp.GolfEvntKvpDaoProc;
import com.bccard.golf.msg.MsgEtt;

import com.bccard.golf.common.security.cryptography.*;
import com.initech.util.Base64Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Date;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntEzInsActn extends GolfActn{
	
	public static final String TITLE = "이지웰 처리";
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
						

		try {

			// 후처리
			String resultMsg = "";
			String script = "";
			
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			

			// 기본 조회 
			String idx = (String)parser.getParameter("idx").trim();
			String ur_name = (String)parser.getParameter("ur_name").trim();
			String jumin_no1 = (String)parser.getParameter("jumin_no1").trim();
			String jumin_no2 = (String)parser.getParameter("jumin_no2").trim();
			String jumin_no = jumin_no1 + jumin_no2; 

			paramMap.put("ur_name", ur_name);
			paramMap.put("jumin_no", jumin_no);

			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");  
			SimpleDateFormat formatter2 = new SimpleDateFormat("hh:mm:ss"); 
			GregorianCalendar cal = new GregorianCalendar();


			
			// 이지웰 셋팅
			String enc_cspCd = "";		// 제휴사(CP) 업체코드
			String enc_command = "";	// 명령어종류
			String enc_clientCd = "";	// 고객사 코드
			String enc_goodsNm = "";	// 상품명
			String enc_goodsCd = "";	// 상품코드(등급번호)
			String enc_unitCost = "";	// 판매 단가
			String enc_buyPrice = "";	// 공급 가격
			String enc_orderCount = "";	// 주문수량
			String enc_orderTotal = "";	// 주문총액
			String enc_payMoney = "";	// 결재금액
			String enc_orderDd = "";	// 주문일
			String enc_orderTm = "";	// 주문시간
			String enc_orderNm = "";	// 주문자이름(생략)
			String enc_userKey = "";	// 주문자유저키
			String enc_orderEmail = "";	// 주문자이메일
			String enc_aspOrderNum = "";// 제휴사 주문번호
			String enc_goUrl = "";		// 제휴사포워딩 URL
			
			String cspCd = (String)parser.getParameter("cspCd");		// 제휴사(CP) 업체코드
			String command = "101";	// 명령어종류
			String clientCd = (String)parser.getParameter("clientCd");	// 고객사 코드
			
			String goodsNm = "";	// 상품명
			String goodsCd = "";	// 상품코드(등급번호)
			String unitCost = "";	// 판매 단가
			String buyPrice = "";	// 공급 가격
			String orderCount = "";	// 주문수량
			String orderTotal = "";	// 주문총액
			String payMoney = "";	// 결재금액
			
			String orderDd = formatter.format(cal.getTime());	// 주문일
			String orderTm = formatter2.format(cal.getTime());	// 주문시간
			String orderNm = ur_name;							// 주문자이름(생략)
			String userKey = (String)parser.getParameter("userKey");	// 주문자유저키
			String orderEmail = (String)parser.getParameter("email");	// 주문자이메일
			String aspOrderNum = "";							// 제휴사 주문번호
			String goUrl = "";					// 제휴사포워딩 URL
			
			
			if(idx.equals("1")){
				goodsNm = "Champion";
				goodsCd = "1";
				unitCost = "170000";
				buyPrice = "170000";
				orderCount = "1";
				orderTotal = "170000";
				payMoney = "170000";
			}else if(idx.equals("2")){
				goodsNm = "Blue";
				goodsCd = "2";
				unitCost = "42500";
				buyPrice = "42500";
				orderCount = "1";
				orderTotal = "42500";
				payMoney = "42500";
			}else if(idx.equals("3")){
				goodsNm = "Gold";
				goodsCd = "3";
				unitCost = "21250";
				buyPrice = "21250";
				orderCount = "1";
				orderTotal = "21250";
				payMoney = "21250";
			}else if(idx.equals("7")){
				goodsNm = "Black";
				goodsCd = "7";
				unitCost = "127500";
				buyPrice = "127500";
				orderCount = "1";
				orderTotal = "127500";
				payMoney = "127500";
			}

			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ur_name", ur_name);
			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("payMoney", payMoney);
			dataSet.setString("goodsCd", goodsCd);

			String rstCode = "";	// 결과 코드
			String msg = "";		// 한신평 오류 메세지
			String end_date = "";	// 유료회원 종료일
			GolfEvntEzInsDaoProc proc = (GolfEvntEzInsDaoProc)context.getProc("GolfEvntEzInsDaoProc");

			// 한신평 실명인증
			NameCheck nm = new NameCheck(); 
			nm.setChkName(ur_name);
			String rtn = nm.setJumin(jumin_no + SITEPW);
			nm.setSiteCode(SITEID);

			if("0".equals(rtn)) {
				nm.setSiteCode(SITEID);
				nm.setTimeOut(30000);
				rtn = nm.getRtn().trim(); 
			} 
			debug(">> 한신평 실명인증 > Return = " + rtn); 
			
			// 실섭, 태섭 구분
			String serverip = InetAddress.getLocalHost().getHostAddress();	// 서버아이피
			String devip = AppConfig.getAppProperty("DV_WAS_1ST");		// 개발기 ip 정보
			if(serverip.equals(devip)){
				goUrl = "http://develop.golfloung.com:13300/app/golfloung/";
				rtn = "1";		// 태섭에서는 통과!!
			}else{
				goUrl = "http://www.golfloung.com/app/golfloung/";
				rtn = "1";		// 태섭에서는 통과!!-> 일단 실섭도 통과
			}
			goUrl += "GolfEvntReturn.do";
			

			if("1".equals(rtn)) { 
				// 정상응답 - 실명인증에 성공한 사람만 검색하도록 넘어간다.

				// 이미 회원인지, 신청내역이 있는지 확인한다.
				// 유료회원 종료일자 검색
				end_date = proc.cntMemFunction(context, request, dataSet);
				
				if(GolfUtil.empty(end_date)){
					
					// 등록내역 확인
					int evntCnt = proc.cntEvntFunction(context, request, dataSet);
					
					if(evntCnt==0){
						
						// 신청내역을 저장한다.
						int insEvntseq = proc.insEvnt(context, request, dataSet);
						aspOrderNum = insEvntseq+"";
						
						if(insEvntseq>0){
							rstCode = "0";	// 등록진행
						}else{
							rstCode = "1";	// 오류
						}
					}else{
						rstCode = "3";	// 이미 신청
					}
				}else{
					rstCode = "2";	// 이미 유료회원
				}
					
			} else if("2".equals(rtn)) { 
				// 본인아님
				msg = "실명인증에 실패했습니다[본인 아님]. 다시  입력해 주십시오";
			} else if("3".equals(rtn)) {
				// 자료 없음
				msg = "실명인증에 실패했습니다[자료 없음]. 다시  입력해 주십시오";
			} else if("4".equals(rtn)) {
				// 시스템장애 (크레딧뱅크 이상)
				msg = "실명인증에 실패했습니다[시스템장애 (크레딧뱅크 이상)]. 다시  입력해 주십시오";
			} else if("5".equals(rtn)) {
				// 주민번호 오류
				msg = "실명인증에 실패했습니다[주민번호 오류]. 다시  입력해 주십시오";
			} else if("50".equals(rtn)) {
				// 정보도용 차단 요청 주민번호
				msg = "실명인증에 실패했습니다[정보도용 차단 요청 주민번호]. 다시  입력해 주십시오";
			} else  {
				// System ERROR
				msg = "실명인증에 실패했습니다[System ERROR]. 다시  입력해 주십시오";
			} 
			
			

			paramMap.put("end_date", end_date);
			paramMap.put("rstCode", rstCode);
			paramMap.put("rtn", rtn);
			paramMap.put("msg", msg);
			

			if(!GolfUtil.empty(cspCd))		enc_cspCd 		= new String(Base64Encoder.encode(cspCd.getBytes()));
			if(!GolfUtil.empty(command))	enc_command 	= new String(Base64Encoder.encode(command.getBytes()));
			if(!GolfUtil.empty(clientCd))	enc_clientCd 	= new String(Base64Encoder.encode(clientCd.getBytes()));
			if(!GolfUtil.empty(goodsNm))	enc_goodsNm 	= new String(Base64Encoder.encode(goodsNm.getBytes()));
			if(!GolfUtil.empty(goodsCd))	enc_goodsCd 	= new String(Base64Encoder.encode(goodsCd.getBytes()));
			if(!GolfUtil.empty(unitCost))	enc_unitCost 	= new String(Base64Encoder.encode(unitCost.getBytes()));
			if(!GolfUtil.empty(buyPrice))	enc_buyPrice 	= new String(Base64Encoder.encode(buyPrice.getBytes()));
			if(!GolfUtil.empty(orderCount))	enc_orderCount 	= new String(Base64Encoder.encode(orderCount.getBytes()));
			if(!GolfUtil.empty(orderTotal))	enc_orderTotal 	= new String(Base64Encoder.encode(orderTotal.getBytes()));
			if(!GolfUtil.empty(payMoney))	enc_payMoney 	= new String(Base64Encoder.encode(payMoney.getBytes()));
			if(!GolfUtil.empty(orderDd))	enc_orderDd 	= new String(Base64Encoder.encode(orderDd.getBytes()));
			if(!GolfUtil.empty(orderTm))	enc_orderTm 	= new String(Base64Encoder.encode(orderTm.getBytes()));
			if(!GolfUtil.empty(orderNm))	enc_orderNm 	= new String(Base64Encoder.encode(orderNm.getBytes()));
			if(!GolfUtil.empty(userKey))	enc_userKey 	= new String(Base64Encoder.encode(userKey.getBytes()));
			if(!GolfUtil.empty(orderEmail))	enc_orderEmail 	= new String(Base64Encoder.encode(orderEmail.getBytes()));
			if(!GolfUtil.empty(aspOrderNum))enc_aspOrderNum = new String(Base64Encoder.encode(aspOrderNum.getBytes()));
			if(!GolfUtil.empty(goUrl))		enc_goUrl 		= new String(Base64Encoder.encode(goUrl.getBytes()));
			
			paramMap.put("cspCd", cspCd);
			paramMap.put("command", command);
			paramMap.put("clientCd", clientCd);
			paramMap.put("goodsNm", goodsNm);
			paramMap.put("goodsCd", goodsCd);
			paramMap.put("unitCost", unitCost);
			paramMap.put("buyPrice", buyPrice);
			paramMap.put("orderCount", orderCount);
			paramMap.put("orderTotal", orderTotal);
			paramMap.put("payMoney", payMoney);
			paramMap.put("orderDd", orderDd);
			paramMap.put("orderTm", orderTm);
			paramMap.put("orderNm", orderNm);
			paramMap.put("userKey", userKey);
			paramMap.put("orderEmail", orderEmail);
			paramMap.put("aspOrderNum", aspOrderNum);
			paramMap.put("goUrl", goUrl);
			
			debug("cspCd : " + cspCd + " / command : " + command + " / clientCd : " + clientCd + " / goodsNm : " + goodsNm + " / goodsCd : " + goodsCd 
					+ " / unitCost : " + unitCost + " / buyPrice : " + buyPrice + " / orderCount : " + orderCount + " / orderTotal : " + orderTotal 
					+ " / payMoney : " + payMoney + " / orderDd : " + orderDd + " / orderTm : " + orderTm + " / orderNm : " + orderNm + " / userKey : " + userKey + " / orderEmail : " + orderEmail
					+ " / aspOrderNum : " + aspOrderNum + " / goUrl : " + goUrl);

			paramMap.put("enc_cspCd", enc_cspCd);
			paramMap.put("enc_command", enc_command);
			paramMap.put("enc_clientCd", enc_clientCd);
			paramMap.put("enc_goodsNm", enc_goodsNm);
			paramMap.put("enc_goodsCd", enc_goodsCd);
			paramMap.put("enc_unitCost", enc_unitCost);
			paramMap.put("enc_buyPrice", enc_buyPrice);
			paramMap.put("enc_orderCount", enc_orderCount);
			paramMap.put("enc_orderTotal", enc_orderTotal);
			paramMap.put("enc_payMoney", enc_payMoney);
			paramMap.put("enc_orderDd", enc_orderDd);
			paramMap.put("enc_orderTm", enc_orderTm);
			paramMap.put("enc_orderNm", enc_orderNm);
			paramMap.put("enc_userKey", enc_userKey);
			paramMap.put("enc_orderEmail", enc_orderEmail);
			paramMap.put("enc_aspOrderNum", enc_aspOrderNum);
			paramMap.put("enc_goUrl", enc_goUrl);
			
//			debug("enc_cspCd : " + enc_cspCd + " / enc_command : " + enc_command + " / enc_clientCd : " + enc_clientCd + " / enc_goodsNm : " + enc_goodsNm + " / enc_goodsCd : " + enc_goodsCd 
//					+ " / enc_unitCost : " + enc_unitCost + " / enc_buyPrice : " + enc_buyPrice + " / enc_orderCount : " + enc_orderCount + " / enc_orderTotal : " + enc_orderTotal 
//					+ " / enc_payMoney : " + enc_payMoney + " / enc_orderDd : " + enc_orderDd + " / enc_orderTm : " + enc_orderTm + " / enc_orderNm : " + enc_orderNm + " / enc_userKey : " + enc_userKey + " / enc_orderEmail : " + enc_orderEmail
//					+ " / enc_aspOrderNum : " + enc_aspOrderNum + " / enc_goUrl : " + enc_goUrl);


			if(!GolfUtil.empty(enc_cspCd)) 		cspCd 		= new String(Base64Encoder.decode(enc_cspCd));
			if(!GolfUtil.empty(enc_clientCd))	clientCd 	= new String(Base64Encoder.decode(enc_clientCd));
			if(!GolfUtil.empty(enc_userKey)) 	userKey 	= new String(Base64Encoder.decode(enc_userKey));
			if(!GolfUtil.empty(enc_goUrl)) 		goUrl 		= new String(Base64Encoder.decode(enc_goUrl));

//			debug("cspCd : " + cspCd + " / command : " + command + " / clientCd : " + clientCd + " / goodsNm : " + goodsNm + " / goodsCd : " + goodsCd 
//					+ " / unitCost : " + unitCost + " / buyPrice : " + buyPrice + " / orderCount : " + orderCount + " / orderTotal : " + orderTotal 
//					+ " / payMoney : " + payMoney + " / orderDd : " + orderDd + " / orderNm : " + orderNm + " / userKey : " + userKey + " / orderEmail : " + orderEmail
//					+ " / aspOrderNum : " + aspOrderNum + " / goUrl : " + goUrl);


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
