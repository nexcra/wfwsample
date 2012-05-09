/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntCouponNoCheckActn
*   작성자    : (주)미디어포스 이경희
*   내용      : 이벤트라운지/골프라운지이벤트/진행중인이벤트/그린피할인쿠폰->할인쿠폰 번호 확인
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
public class GolfEvntCouponNoCheckActn extends GolfActn{
	
	public static final String TITLE = "할인쿠폰 번호 확인";

	/***************************************************************************************
	* 쿠폰 주문 결제
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

		// 리턴 변수
		String script = "";
		String userNm = "";
		String userId = "";
		String juminno = ""; 
		String juminno1 = ""; 
		String juminno2 = ""; 
		String mobile = "";
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		String chdh_non_cdhd_clss = "2";	// 1:회원, 2:비회원
		
		try { 
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				juminno1	= juminno.substring(0, 6);
				juminno2	= juminno.substring(6, 13);

				mobile 	= (String)usrEntity.getMobile(); 
				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3(); 
				chdh_non_cdhd_clss = "1";
				
			}
			
			paramMap.put("userNm", userNm); 
			paramMap.put("userId", userId);
			paramMap.put("juminno1", juminno1);
			paramMap.put("juminno2", juminno2);
			paramMap.put("mobile1", mobile1);
			paramMap.put("mobile2", mobile2);
			paramMap.put("mobile3", mobile3);
			paramMap.put("chdh_non_cdhd_clss", chdh_non_cdhd_clss);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
