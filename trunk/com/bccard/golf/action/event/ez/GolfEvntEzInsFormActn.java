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

import com.bccard.golf.common.AppConfig;
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
public class GolfEvntEzInsFormActn extends GolfActn{
	
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

			String cspCd 	= (String)request.getSession().getAttribute("ezCspCd");		// 제휴사(CP) 업체코드
			String clientCd	= (String)request.getSession().getAttribute("ezClientCd");	// 고객사 코드
			String userKey 	= (String)request.getSession().getAttribute("ezUserKey");	// 유저키
			String userNm 	= (String)request.getSession().getAttribute("ezUserNm");	// 사용자 이름
			String homeTel 	= (String)request.getSession().getAttribute("ezHomeTel");	// 사용자 집 전화번호
			String mobile 	= (String)request.getSession().getAttribute("ezMobile");	// 사용자 휴대폰
			String email 	= (String)request.getSession().getAttribute("ezEmail");		// 사용자 이메일
			

			String idx = "";	// 선택등급
			if(actnKey.equals("GolfEvnt1InsForm")){
				idx = "1";
			}else if(actnKey.equals("GolfEvnt2InsForm")){
				idx = "2";
			}else if(actnKey.equals("GolfEvnt3InsForm")){
				idx = "3";
			}else if(actnKey.equals("GolfEvnt7InsForm")){
				idx = "7";
			}else if(actnKey.equals("GolfEvntInsForm")){
				idx = "";
			}

			
			debug(actnKey + " / cspCd : " + cspCd +" / clientCd : " + clientCd +" / userKey : " + userKey +" / userNm : " + userNm +" / homeTel : " + homeTel
					+" / mobile : " + mobile + " / email : " + email + " / idx : " + idx);

			// 실섭, 태섭 구분
			String serverNm = "";
			String serverip = InetAddress.getLocalHost().getHostAddress();	// 서버아이피
			String devip = AppConfig.getAppProperty("DV_WAS_1ST");		// 개발기 ip 정보
			if(serverip.equals(devip)){
				serverNm = "dev";
			}else{
				serverNm = "real";
			}
			

			paramMap.put("idx", idx); 
			paramMap.put("cspCd", cspCd); 
			paramMap.put("clientCd", clientCd); 
			paramMap.put("userKey", userKey); 
			paramMap.put("userNm", userNm); 
			paramMap.put("homeTel", homeTel); 
			paramMap.put("mobile", mobile); 
			paramMap.put("email", email); 
			paramMap.put("serverNm", serverNm); 
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 

		return super.getActionResponse(context, subpage_key);
		
	}
}
