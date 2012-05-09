/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntReportActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 최저가 신고 처리 
*   적용범위  : Golf
*   작성일자  : 2010-03-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event.shop;

import java.io.IOException;
import java.net.InetAddress;
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
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.shop.GolfEvntShopOrdDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntReportActn extends GolfActn{
	
	public static final String TITLE = "쇼핑 리스트";

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
		
		try { 
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			// 후처리
			String script = "";
			
			// 신고내용
			String userNm			= parser.getParameter("userNm","");	
			String juminno1			= parser.getParameter("juminno1","");	
			String juminno2			= parser.getParameter("juminno2","");	
			String mobile1			= parser.getParameter("mobile1","");	
			String mobile2			= parser.getParameter("mobile2","");	
			String mobile3			= parser.getParameter("mobile3","");	
			String regDate			= parser.getParameter("regDate","");	
			String product			= parser.getParameter("product","");	
			String pro_price		= parser.getParameter("pro_price","");	
			String price			= parser.getParameter("price","");	
			String siteurl			= parser.getParameter("siteurl","");		

			StringBuffer resultDesc_desc = new StringBuffer();
			StringBuffer resultDesc_html_desc = new StringBuffer();
			StringBuffer resultDesc = new StringBuffer();
			StringBuffer resultDesc_html = new StringBuffer(); 

			String resultString = "";
			resultString += "성명 : " + userNm + "\n";
			resultString += "주민번호 : " + juminno1 + "-" + juminno2 + "\n";
			resultString += "연락처 : "  + mobile1 + "-" + mobile2 + "-" + mobile3 +  "\n";
			resultString += "접수일 : " + regDate + "\n";
			resultString += "신고물품 : " + product + "\n";
			resultString += "물품가격 : " + pro_price + "\n";
			resultString += "금액차이 : " + price + "\n";
			resultString += "비교 대상 사이트 (URL 주소) : " + siteurl + "\n";
			
			resultDesc.append(resultString+ "\n");
			resultDesc_html.append(resultString+ "<br>");

			debug(resultDesc.toString());

			String serverip = "";  // 서버아이피
			String devip = "";	   // 개발기 ip 정보

			try {
				serverip = InetAddress.getLocalHost().getHostAddress();
			} catch(Throwable t) {}

			try {
				devip = AppConfig.getAppProperty("DV_WAS_1ST");
			} catch(Throwable t) {}
			
			String emailTitle = "" ;
			String emailAdmin = "" ;
			String toMail =""; 
			
			debug("[골프라운지 TM 서버IP="  + serverip );	
			debug("[골프라운지 TM 개발IP="  + devip );

			/*메일발송*/
			if (devip.equals(serverip)) {  //개발기
				emailTitle = "[개발기 테스트 서버 - 최저가 신고센터] " + userNm + " 고객님 ";
				emailAdmin = "\"DEV골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
				resultDesc.append("\n추신>현재 이 메일은 개발기에서 테스트용으로 보내는 것 이므로 삭제하여 주십시요.");
				resultDesc_html.append("<br>현재 이 메일은 개발기에서 테스트용으로 보내는 것이므로 삭제하여 주십시요.");

				toMail="20109028@bcnuri.com;simijoa@naver.com;simijoa@hanmail.net;beagopa9@nate.com";
			} else {	// 운영기
				emailTitle = "[골프라운지 - 최저가 신고센터] " + userNm + " 고객님 ";
				emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
				toMail="dunk2000@bccard.com;yskkang@bccard.com;kshwan@bccard.com;bbongs7@bccard.com;bcgolf@bccard.com";
			}

			info("[골프라운지 TM emailTitle="  + emailTitle );	
			info("[골프라운지 TM emailAdmin="  + emailAdmin );
			info("[골프라운지 TM toMail="  + toMail );

			
			EmailSend sender = new EmailSend();
			EmailEntity emailEtt = new EmailEntity("EUC_KR");
			
//			sender.setHost("211.181.255.38"); //메일 host변경 (기존 211.181.255.109)
			
			emailEtt.setFrom(emailAdmin);
			emailEtt.setSubject(emailTitle);
			emailEtt.setContents(resultDesc.toString(),resultDesc_html.toString());
			emailEtt.setTo(toMail);
			sender.send(emailEtt);
			
			script = "alert('최저가 신고가 등록 되었습니다.'); self.close();";

			request.setAttribute("script", script);
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}

}
