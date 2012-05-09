/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemCardInsActn
*   작성자    : 미디어포스 권영만
*   내용      : 가입 > 등록처리 > 카드회원
*   적용범위  : golf 
*   작성일자  : 2009-08-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.login.CardNhInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemCardInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	미디어포스
* @version	1.0 
******************************************************************************/
public class GolfMemCardInsActn extends GolfActn{
	
	public static final String TITLE = "가입 > 카드회원 등록";

	/***************************************************************************************
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";
		String userNm = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int intMemberGrade = 0; 
		int intCardGrade = 0; 
		String email_id = "";
		String golfCardYn = "N";
		String golfCardNhYn = "N";
		String strCardNhType = "";
		
		try {
			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
			GolfUserEtt mbr = SessionUtil.getTopnUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				email_id 	= (String)usrEntity.getEmail1(); 
			}
			String strCardJoinNo	= "";
			
			if (mbr != null) 
			{	
				List cardList = mbr.getCardInfoList();
				CardInfoEtt cardInfo = new CardInfoEtt();
				
				
				if( cardList.size() > 0 )
				{
					cardInfo = (CardInfoEtt)cardList.get(0);
					strCardJoinNo = cardInfo.getJoinNo();	// 제휴코드
					golfCardYn = "Y";
				}
				
				// 농협 골프카드 유무 체크List cardList = mbr.getCardInfoList();
				List cardNhList = mbr.getCardNhInfoList();
				CardNhInfoEtt cardNhInfo = new CardNhInfoEtt();
				
				if( cardNhList.size() > 0 )
				{
					cardNhInfo = (CardNhInfoEtt)cardNhList.get(0);
					strCardNhType = cardNhInfo.getCardType();	// 카드종류
					golfCardNhYn = "Y";
				}
			}
			
						
			// 02.입력값 조회		
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);
			
			RequestParser parser = context.getRequestParser("default",request,response); 
			String strCode = parser.getParameter("strCode");
			
			// 03.Proc 에 던질 값 세팅 
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("strCode", strCode);	
						
			// 04.실제 테이블 조회
			GolfMemCardInsDaoProc proc = (GolfMemCardInsDaoProc)context.getProc("GolfMemCardInsDaoProc");
			int addResult = proc.execute(context, dataSet, request);		

        	String returnUrlTrue = "GolfMemJoinEnd.do";
        	String returnUrlFalse =  "GolfMemJoinNoCard.do";
        	String script = "parent.location.href='/app/golfloung/html/common/member_join_finish.jsp'";

			if (addResult == 1) {
				
				DbTaoResult tmView = proc.cardExecute(context, dataSet, request);

				if (tmView != null && tmView.isNext()) {
					tmView.first();
					tmView.next();
					memGrade = (String) tmView.getString("memGrade");	
					intMemGrade = (int) tmView.getInt("intMemGrade");	
					intMemberGrade = (int) tmView.getInt("intMemberGrade");	
					intCardGrade = (int) tmView.getInt("intCardGrade");	
					
				}

				usrEntity.setMemGrade(memGrade);				//등급명
				usrEntity.setIntMemGrade(intMemGrade);		//공통등급
				usrEntity.setIntMemberGrade(intMemberGrade);	//멤버쉽등급처리
				usrEntity.setIntCardGrade(intCardGrade);		//카드등급처리
				usrEntity.setCyberMoney(0);
				

				if (email_id != null && !email_id.equals("")) {

					String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String emailTitle = "";
					String emailFileNm = "";
					
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailTitle = "[Golf Loun.G] 골프라운지 서비스 가입을 축하드립니다.";
					emailFileNm = "/email_tpl19.html";
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+memGrade);
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setTo(email_id);
					//sender.send(emailEtt);  
				}
				
				request.setAttribute("script", script);
				request.setAttribute("returnUrl", returnUrlTrue);
				//request.setAttribute("resultMsg", "등록이 정상적으로 처리 되었습니다.");      	
				
	        } else {

				request.setAttribute("returnUrl", returnUrlFalse);
				request.setAttribute("resultMsg", "등록이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 등록되지 않을 경우 관리자에 문의하십시오.");		        		
	        }	
			
			// 05. Return 값 세팅			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
		
	}
}
