/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntChgEmailActn
*   작성자    : E4NET 은장선
*   내용      : 이벤트 > 인터파크이벤트 > 인터파크유입 체크
*   적용범위  : Golf
*   작성일자  : 2009-08-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.event;

import java.io.IOException;
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

import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.event.GolfEvntBkWinListDaoProc;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;

import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;
/******************************************************************************
* Golf
* @author	JSEUN
* @version	1.0
******************************************************************************/
public class GolfEvntChgEmailActn extends GolfActn{
	
	public static final String TITLE = "프리미엄 부킹 이벤트 당첨자 리스트";

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		
		
		try {
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);

			// 01.세션정보체크			
			
			String front_mail	= parser.getParameter("front_mail","");  //e-mail
			String last_mail	= parser.getParameter("last_mail","");  //e-mail
			String userId		= "";  //ID
			String jumin_no		= "";  //주민등록번호		
			String cupn         = "";  //쿠폰번호
			String userNm       = "";  //사용자이름
			String email        = front_mail + "@" + last_mail;			

			//메일전송을 위한 파라미터
			EmailSend sender = new EmailSend();
			EmailEntity emailEtt = new EmailEntity("EUC_KR");
			String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
			String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
			String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
			String emailTitle = "";
			String emailFileNm = "";
			String useYN       = "";

			boolean doUpdate = false;

			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);

			if(usrEntity != null) {				
				userId          = (String)usrEntity.getAccount(); 
				jumin_no        = (String)usrEntity.getSocid();
				userNm			= (String)usrEntity.getName(); 
			}

			dataSet.setString("jumin_no", jumin_no);
			dataSet.setString("socid"   , jumin_no);
			dataSet.setString("email"   , email   );
			dataSet.setString("evnt_no" , "109"   );

			GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
			DbTaoResult cpnInfo = (DbTaoResult) inter.getCpnNumber(context, request, dataSet);

			if (cpnInfo != null && cpnInfo.isNext()) {
				cpnInfo.first();
				cpnInfo.next();
				if(cpnInfo.getString("RESULT").equals("00")){
					cupn		= cpnInfo.getString("CUPN");								
				}
			}

			doUpdate =  (boolean) inter.setChgEmailAddress(context, request, dataSet);

			if(doUpdate == true){							
				useYN = (String) inter.getUseYN(context, request, dataSet);

				emailTitle = "골프라운지 회원가입 인터파크 할인쿠폰";

				if(useYN.equals("Y")){
					emailFileNm = "/email_interpark1.html";
				}else{
					emailFileNm = "/email_interpark.html";
				}
				emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);							
				emailEtt.setFrom(emailAdmin);
				emailEtt.setSubject(emailTitle);
				emailEtt.setTo(email);
				sender.send(emailEtt);
			}
			request.setAttribute("useYN"    ,       useYN);
			request.setAttribute("email"    ,		email);

		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
