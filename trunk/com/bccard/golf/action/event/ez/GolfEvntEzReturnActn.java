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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.BcUtil;
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
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.event.ez.GolfEvntEzReturnDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import com.bccard.golf.common.loginAction.SessionUtil;
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
public class GolfEvntEzReturnActn extends GolfActn{
	
	public static final String TITLE = "이지웰 리턴 처리";

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
			String rstCode = "";
			int updEvnt			= 0;	// 리턴정보 저장결과

			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
						
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String result 		= "";
			String aspOrderNum 	= "";
			String orderNum 	= "";
			String paySummary 	= "";
			String errDesc 		= "";
			String goUrl 		= "";
			String command 		= "";
			
			// 기본 조회 
			String enc_result 		= (String)parser.getParameter("result");		// 주문처리 결과 : yes:정상, no:오류
			String enc_aspOrderNum 	= (String)parser.getParameter("aspOrderNum");	// 주문번호 (제휴사측) -> 골프라운지 주문번호
			String enc_orderNum 	= (String)parser.getParameter("orderNum");		// 주문번호 (이지웰측)
			String enc_paySummary 	= (String)parser.getParameter("paySummary");	// 결재수단 정보 : 포인트:10000, 카드:50000
			String enc_errDesc 		= (String)parser.getParameter("errDesc");		// 에러내용
			String enc_goUrl 		= (String)parser.getParameter("goUrl");			// 리턴URL
			String enc_command 		= "";
			

			if(!GolfUtil.empty(enc_result)) 		result 		= new String(Base64Encoder.decode(enc_result));
			if(!GolfUtil.empty(enc_aspOrderNum))	aspOrderNum = new String(Base64Encoder.decode(enc_aspOrderNum));
			if(!GolfUtil.empty(enc_orderNum)) 		orderNum 	= new String(Base64Encoder.decode(enc_orderNum));
			if(!GolfUtil.empty(enc_paySummary)) 	paySummary 	= new String(Base64Encoder.decode(enc_paySummary));
			if(!GolfUtil.empty(enc_errDesc)) 		errDesc 	= new String(Base64Encoder.decode(enc_errDesc));
			if(!GolfUtil.empty(enc_goUrl)) 			goUrl 		= new String(Base64Encoder.decode(enc_goUrl));
			
						
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("result", result);
			dataSet.setString("aspOrderNum", aspOrderNum);
			dataSet.setString("orderNum", orderNum);
			dataSet.setString("paySummary", paySummary);
			dataSet.setString("errDesc", errDesc);			

			
			GolfEvntEzReturnDaoProc proc = (GolfEvntEzReturnDaoProc)context.getProc("GolfEvntEzReturnDaoProc");
			
			
			if(GolfUtil.empty(result)){
				// 정상적이지 않은 값이 넘어온경우
				rstCode = "1";
				command = "102";
			}else{
				rstCode = "0";
				
				// 상태 저장
				updEvnt = proc.updEvntFunction(context, request, dataSet);
				
				if(updEvnt>0){
					if(result.equals("yes")){
						command = "103";
					}else{
						command = "102";
					}
				}else{
					command = "102";
				}
			}

			paramMap.put("updEvnt", updEvnt+"");
			paramMap.put("rstCode", rstCode);
			
			if(!GolfUtil.empty(result))			enc_result 		= new String(Base64Util.encode(result.getBytes()));
			if(!GolfUtil.empty(aspOrderNum))	enc_aspOrderNum	= new String(Base64Util.encode(aspOrderNum.getBytes()));
			if(!GolfUtil.empty(orderNum))		enc_orderNum 	= new String(Base64Util.encode(orderNum.getBytes()));
			if(!GolfUtil.empty(paySummary))		enc_paySummary 	= new String(Base64Util.encode(paySummary.getBytes()));
			if(!GolfUtil.empty(errDesc))		enc_errDesc 	= new String(Base64Util.encode(errDesc.getBytes()));
			if(!GolfUtil.empty(command))		enc_command 	= new String(Base64Util.encode(command.getBytes()));
			
			paramMap.put("result", result);
			paramMap.put("aspOrderNum", aspOrderNum);
			paramMap.put("orderNum", orderNum);
			paramMap.put("paySummary", paySummary);
			paramMap.put("errDesc", errDesc);
			paramMap.put("goUrl", goUrl);
			paramMap.put("command", command);

			
			paramMap.put("enc_result", enc_result);
			paramMap.put("enc_aspOrderNum", enc_aspOrderNum);
			paramMap.put("enc_orderNum", enc_orderNum);
			paramMap.put("enc_paySummary", enc_paySummary);
			paramMap.put("enc_errDesc", enc_errDesc);
			paramMap.put("enc_command", enc_command);
			paramMap.put("enc_goUrl", enc_goUrl);
			
			String cspCd = (String)request.getSession().getAttribute("ezCspCd");
			String enc_cspCd = "";
			if(!GolfUtil.empty(cspCd))			enc_cspCd 		= new String(Base64Util.encode(cspCd.getBytes()));
			paramMap.put("cspCd", cspCd);
			paramMap.put("enc_cspCd", enc_cspCd);
			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
