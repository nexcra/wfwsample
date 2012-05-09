/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntCouponOrdFormActn
*   작성자    : (주)미디어포스 이경희
*   내용      : 이벤트라운지/골프라운지이벤트/진행중인이벤트/그린피할인쿠폰
*   적용범위  : Golf
*   작성일자  : 2011-04-08
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

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopListDaoProc;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopViewDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntCouponOrdFormActn extends GolfActn{
	
	public static final String TITLE = "쿠폰 주문 페이지";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		
		String userNm = "";
		String userId = "";
		String juminno = ""; 
		String juminno1 = ""; 
		String juminno2 = ""; 
		String mobile1 = "";
		String mobile2 = "";
		String mobile3 = "";
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try { 
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				juminno1	= juminno.substring(0, 6);
				juminno2	= juminno.substring(6, 13);
				mobile1 	= (String)usrEntity.getMobile1(); 
				mobile2 	= (String)usrEntity.getMobile2(); 
				mobile3 	= (String)usrEntity.getMobile3();

				paramMap.put("userNm", userNm);
				paramMap.put("userId", userId);
				paramMap.put("juminno1", juminno1);
				paramMap.put("juminno2", juminno2);
				paramMap.put("mobile1", mobile1);
				paramMap.put("mobile2", mobile2);
				paramMap.put("mobile3", mobile3);

			}
			
			debug("userNm : " + userNm + " / userId : " + userId + " / juminno : " + juminno + " / juminno1 : " + juminno1 + " / juminno2 : " + juminno2
					+ " / mobile1 : " + mobile1 + " / mobile2 : " + mobile2 + " / mobile3 : " + mobile3	);

	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
