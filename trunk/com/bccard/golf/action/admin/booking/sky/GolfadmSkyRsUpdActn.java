/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admSkyTimeChgActn
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄부킹 티타임 노출여부 처리
*   적용범위  : golf
*   작성일자  : 2009-05-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.sky;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.sky.GolfadmSkyRsUpdDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0 
******************************************************************************/
public class GolfadmSkyRsUpdActn extends GolfActn{
	
	public static final String TITLE = "관리자 프리미엄부킹 티타임 노출여부 처리"; 

	/***************************************************************************************
	* 비씨탑포인트 관리자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int lessonDelResult = 0;
		
		// 00.레이아웃 URL 저장
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.세션정보체크
			
			// 02.입력값 조회		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

	        // 오늘 날짜 가져오기
			GregorianCalendar today = new GregorianCalendar ( );
	        String [] dayOfWeek = {"","일","월","화","수","목","금","토"};	        

	        int nYear = today.get ( today.YEAR );
	        int nMonth = today.get ( today.MONTH ) + 1;
	        int nDay = today.get ( today.DAY_OF_MONTH ); 
	        int nYoil = today.get ( today.DAY_OF_WEEK );
	        int hour = today.get(today.HOUR);
	        int minute = today.get(today.MINUTE);
	        
			// Request 값 저장
			String rsvt_SQL_NO = parser.getParameter("RSVT_SQL_NO", "");
			String rsvt_YN = parser.getParameter("RSVT_YN", "");
			String ctnt = parser.getParameter("CTNT", "");
			String email1 = parser.getParameter("EMAIL1", "");
			String name = parser.getParameter("NAME", "");
			String id = parser.getParameter("ID", "");
			String hp_NO = parser.getParameter("HP_NO", "");
			String socid = parser.getParameter("SOCID", "");
			String reg_DATE = parser.getParameter("REG_DATE", "");
	        String cancel_DATE = nYear+"-"+nMonth+"-"+nDay+"("+dayOfWeek[nYoil]+") "+hour+":"+minute; 
			String hole = parser.getParameter("HOLE", "");
			String bk_DATE = parser.getParameter("BK_DATE", "");
			String bk_TIME = parser.getParameter("BK_TIME", "");
			String tot_PERS_NUM = parser.getParameter("TOT_PERS_NUM", "");
			String appr_opion = parser.getParameter("APPR_OPION","");
			String add_appr_opion = parser.getParameter("ADD_APPR_OPION","");
			
			//email1 = "simijoa@hanmail.net";
			
			
			// 03.Proc 에 던질 값 세팅 (Proc에 dataSet 형태의 배열(?)로 request값 또는 조회값을 던진다.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_SQL_NO);
			dataSet.setString("RSVT_YN", rsvt_YN);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("CDHD_ID", id);
			dataSet.setString("APPR_OPION", appr_opion);
			dataSet.setString("ADD_APPR_OPION", add_appr_opion);
			
			// 04.실제 테이블(Proc) 조회
			GolfadmSkyRsUpdDaoProc proc = (GolfadmSkyRsUpdDaoProc)context.getProc("GolfadmSkyRsUpdDaoProc");		
			int editResult = proc.execute(context, dataSet);
			
	        if (editResult == 1) {
	        	
	        	if(rsvt_YN.equals("I")){
	        		debug("===========GolfadmSkyRsUpdActn=======임박취소일경우 메일보낸다.");
	        		if (!email1.equals("")) {

						String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String emailTitle = "";
						String emailFileNm = "";
						
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");

						emailTitle = "[Golf Loun.G] SKY72 드림듄스 부킹 임박취소 알려드립니다.";
						emailFileNm = "/email_tpl05.html";						
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, name+"|"+id+"|"+hp_NO+"|"+socid+"|"+reg_DATE+"|"+cancel_DATE+"|"+hole+"|"+bk_DATE+"|"+bk_TIME+"|"+tot_PERS_NUM+"|"+ctnt);
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setTo(email1);
						//sender.send(emailEtt);
					}
	        	}
	        	
				request.setAttribute("returnUrl", "admSkyRsList.do");
				request.setAttribute("resultMsg", "예약 상세정보가 수정 되었습니다.");      	
	        } else {
				request.setAttribute("returnUrl", "admSkyRsList.do");
				request.setAttribute("resultMsg", "예약 상세정보 수정이 정상적으로 처리 되지 않았습니다.\\n\\n반복적으로 수정되지 않을 경우 관리자에 문의하십시오.");		        		
	        }
	
	        request.setAttribute("paramMap", paramMap);
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
