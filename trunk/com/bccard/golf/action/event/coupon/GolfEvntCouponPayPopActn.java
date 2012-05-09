/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntCouponPayPopActn
*   작성자    : (주)미디어포스 이경희
*   내용      : 이벤트라운지/골프라운지이벤트/진행중인이벤트/그린피할인쿠폰->결제팝업
*   적용범위  : Golf
*   작성일자  : 2011-04-11
************************** 수정이력 ****************************************************************
*    일자    작성자   변경사항
***************************************************************************************************/
package com.bccard.golf.action.event.coupon;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopOrdDaoProc;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopViewDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntCouponPayPopActn extends GolfActn{
	
	public static final String TITLE = "쿠폰 주문 결제 팝업";

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
		
		try { 

			// 02.입력값 조회	
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			int int_atm				= 0;											// 상품금액
			int total_atm			= 0;											// 결제금액			

			int productPrice		= parser.getIntParameter("productPrice",0);		// 상품가격
			int qty					= parser.getIntParameter("qty",0);				// 수량
			String userNm 			= parser.getParameter("userNm", "");			// 회원이름

			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			total_atm = productPrice * qty;			
			
			paramMap.put("total_atm", Integer.toString(total_atm));
			paramMap.put("userNm", userNm);
			paramMap.put("productPrice", Integer.toString(productPrice));
			paramMap.put("qty", Integer.toString(qty));
			
		
			debug("GolfEvntShopPayPopActn ::  productPrice : "+productPrice+" / qty : " + qty+ " / total_atm : " + total_atm	);

	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
