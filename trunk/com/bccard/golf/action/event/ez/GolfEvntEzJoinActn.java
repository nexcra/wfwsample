/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntEzInsFormActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 이지웰 > 등록폼
*   적용범위  : Golf
*   작성일자  : 2010-08-10
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* 
***************************************************************************************************/
package com.bccard.golf.action.event.ez;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.benest.GolfEvntBnstPayFormDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.security.cryptography.Base64Encoder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntEzJoinActn extends GolfActn{
	
	public static final String TITLE = "이벤트 > 이지웰 > 등록폼";

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

		String actnKey = super.getActionKey(context);
		
		try { 
			// 01.세션정보체크
			HttpSession session = request.getSession(true);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			String cspCd = "";		// 제휴사(CP) 업체코드
			String clientCd = "";	// 고객사 코드
			String userKey = "";	// 유저키
			String userNm = "";		// 사용자 이름
			String homeTel = "";	// 사용자 집 전화번호
			String mobile = "";		// 사용자 휴대폰
			String email = "";		// 사용자 이메일
			String goUrl = "";		// 제휴사 로그인후 이동 URL , 이벤트 및 기타용도로 이용
						
			String enc_cspCd 	= (String)parser.getParameter("cspCd");		// 제휴사(CP) 업체코드
			String enc_clientCd	= (String)parser.getParameter("clientCd");	// 고객사 코드
			String enc_userKey 	= (String)parser.getParameter("userKey");	// 유저키
			String enc_userNm 	= (String)parser.getParameter("userNm");	// 사용자 이름
			String enc_homeTel 	= (String)parser.getParameter("homeTel");	// 사용자 집 전화번호
			String enc_mobile 	= (String)parser.getParameter("mobile");	// 사용자 휴대폰
			String enc_email 	= (String)parser.getParameter("email");		// 사용자 이메일
			String enc_goUrl 	= (String)parser.getParameter("goUrl");		// 제휴사 로그인후 이동 URL , 이벤트 및 기타용도로 이용

			if(!GolfUtil.empty(enc_cspCd)) 		cspCd 		= new String(Base64Encoder.decode(enc_cspCd));
			if(!GolfUtil.empty(enc_clientCd))	clientCd 	= new String(Base64Encoder.decode(enc_clientCd));
			if(!GolfUtil.empty(enc_userKey)) 	userKey 	= new String(Base64Encoder.decode(enc_userKey));
			if(!GolfUtil.empty(enc_userNm)) 	userNm 		= new String(Base64Encoder.decode(enc_userNm));
			if(!GolfUtil.empty(enc_homeTel)) 	homeTel 	= new String(Base64Encoder.decode(enc_homeTel));
			if(!GolfUtil.empty(enc_mobile)) 	mobile 		= new String(Base64Encoder.decode(enc_mobile));
			if(!GolfUtil.empty(enc_email)) 		email 		= new String(Base64Encoder.decode(enc_email));
			if(!GolfUtil.empty(enc_goUrl)) 		goUrl 		= new String(Base64Encoder.decode(enc_goUrl));
			
			
			debug(actnKey + " / cspCd : " + cspCd +" / clientCd : " + clientCd +" / userKey : " + userKey +" / userNm : " + userNm +" / homeTel : " + homeTel
					+" / mobile : " + mobile + " / email : " + email + " / goUrl : " + goUrl);
			

			request.getSession().removeAttribute("ezCspCd");
			request.getSession().removeAttribute("ezClientCd");
			request.getSession().removeAttribute("ezUserKey");
			request.getSession().removeAttribute("ezUserNm");
			request.getSession().removeAttribute("ezHomeTel");
			request.getSession().removeAttribute("ezMobile");
			request.getSession().removeAttribute("ezEmail");
			session.setAttribute("ezCspCd",cspCd);
			session.setAttribute("ezClientCd",clientCd);
			session.setAttribute("ezUserKey",userKey);
			session.setAttribute("ezUserNm",userNm);
			session.setAttribute("ezHomeTel",homeTel);
			session.setAttribute("ezMobile",mobile);
			session.setAttribute("ezEmail",email);

			
			paramMap.put("cspCd", cspCd); 
			paramMap.put("clientCd", clientCd); 
			paramMap.put("userKey", userKey); 
			paramMap.put("userNm", userNm); 
			paramMap.put("homeTel", homeTel); 
			paramMap.put("mobile", mobile); 
			paramMap.put("email", email); 
			paramMap.put("goUrl", goUrl); 
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
