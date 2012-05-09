/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeReserveActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��û ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-29
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.booking.jeju;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.booking.jeju.*;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfBkJjTimeReserveActn extends GolfActn{
	
	public static final String TITLE = "��ŷ ��û ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		String userNm = ""; 
		String userId = "";
		String email_id = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		String rsvt_SQL_NO = "";

		try {
			// 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				userId		= (String)usrEntity.getAccount(); 
				email_id 	= (String)usrEntity.getEmail1(); 
			}
			 /*
			debug("==========email_id========> " + email_id);
			email_id = "simijoa@hanmail.net";
			*/
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����
			String round_HOPE_DATE			= parser.getParameter("ROUND_HOPE_DATE", "");
			round_HOPE_DATE = GolfUtil.replace(round_HOPE_DATE, "-", "");
			String hope_RGN_CODE			= parser.getParameter("HOPE_RGN_CODE", "");
			String team_NUM					= parser.getParameter("TEAM_NUM", "");
			String tot_PERS_NUM				= parser.getParameter("TOT_PERS_NUM", "");
			String round_HOPE_TIME_CLSS		= parser.getParameter("ROUND_HOPE_TIME_CLSS", "");
			String round_HOPE_TIMEA			= parser.getParameter("ROUND_HOPE_TIMEA", "");
			String round_HOPE_TIMEP			= parser.getParameter("ROUND_HOPE_TIMEP", "");
			String cdhd_NM					= parser.getParameter("CDHD_NM", "");
			String hp_DDD_NO				= parser.getParameter("HP_DDD_NO", "");
			String hp_TEL_HNO				= parser.getParameter("HP_TEL_HNO", "");
			String hp_TEL_SNO				= parser.getParameter("HP_TEL_SNO", "");
			String ctct_ABLE_TIME			= parser.getParameter("CTCT_ABLE_TIME", "");
			
			// SMS ���� 
			String hp_TEL				= hp_DDD_NO+hp_TEL_HNO+hp_TEL_SNO;
			/*
			debug("==========userMobile1========> " + hp_DDD_NO);
			debug("==========userMobile2========> " + hp_TEL_HNO);
			debug("==========userMobile3========> " + hp_TEL_SNO);
			*/
			
			// �̸��� ����
			String mail_BK_DATE = "";	
			String mail_HOPE_RGN_CODE = "";	

	        GregorianCalendar today = new GregorianCalendar ( );
	        int nYear = today.get ( today.YEAR );
	        int nMonth = today.get ( today.MONTH ) + 1;
	        int nDay = today.get ( today.DAY_OF_MONTH ); 
	        String strToday = nYear+"�� "+nMonth+"�� "+nDay+"��";
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("ROUND_HOPE_DATE", round_HOPE_DATE);
			dataSet.setString("HOPE_RGN_CODE", hope_RGN_CODE);
			dataSet.setString("TEAM_NUM", team_NUM);
			dataSet.setString("TOT_PERS_NUM", tot_PERS_NUM);
			dataSet.setString("ROUND_HOPE_TIME_CLSS", round_HOPE_TIME_CLSS);
			dataSet.setString("ROUND_HOPE_TIMEA", round_HOPE_TIMEA);
			dataSet.setString("ROUND_HOPE_TIMEP", round_HOPE_TIMEP);
			dataSet.setString("CDHD_NM", cdhd_NM);
			dataSet.setString("HP_DDD_NO", hp_DDD_NO);
			dataSet.setString("HP_TEL_HNO", hp_TEL_HNO);
			dataSet.setString("HP_TEL_SNO", hp_TEL_SNO);
			dataSet.setString("CTCT_ABLE_TIME", ctct_ABLE_TIME);
					

			// 04.���� ���̺�(Proc) ��ȸ 1. ��Ͽ��� Ȯ��
			GolfBkJjTimeReserveDaoProc proc = (GolfBkJjTimeReserveDaoProc)context.getProc("GolfBkJjTimeReserveDaoProc");
			DbTaoResult addResult = proc.execute(context, dataSet, request);

			if (addResult != null && addResult.isNext()) {
				addResult.first();
				addResult.next();
				rsvt_SQL_NO = (String) addResult.getObject("RSVT_SQL_NO");
				//debug("GolfBkJjTimeReserveActn=====================rsvt_SQL_NO => " + rsvt_SQL_NO);
				paramMap.put("RSVT_SQL_NO", rsvt_SQL_NO);
				
				// ���Ϸ� ���� ������ �̾ƿ���, ������ ǥ�� ����
				dataSet.setString("RSVT_SQL_NO",	rsvt_SQL_NO);
				GolfBkJjTimeRsViewDaoProc proc_mail = (GolfBkJjTimeRsViewDaoProc)context.getProc("GolfBkJjTimeRsViewDaoProc");
				DbTaoResult rsView = proc_mail.execute(context, dataSet);
				rsView.first();
				rsView.next();
				mail_BK_DATE = (String) rsView.getObject("BK_DATE");	
				mail_BK_DATE = mail_BK_DATE + " " + (String) rsView.getObject("BK_TIME");	
				mail_HOPE_RGN_CODE = (String) rsView.getObject("HOPE_RGN_CODE");	
				
				/*
				debug("====================GolfMemInsActn === ���Ϲ߼� ===userNm => " + userNm);
				debug("====================GolfMemInsActn === ���Ϲ߼� ===strToday => " + strToday);
				debug("====================GolfMemInsActn === ���Ϲ߼� ===mail_HOPE_RGN_CODE => " + mail_HOPE_RGN_CODE);
				debug("====================GolfMemInsActn === ���Ϲ߼� ===mail_BK_DATE => " + mail_BK_DATE);
				debug("====================GolfMemInsActn === ���Ϲ߼� ===TEAM_NUM => " + TEAM_NUM);
				debug("====================GolfMemInsActn === ���Ϲ߼� ===TOT_PERS_NUM => " + TOT_PERS_NUM);
				*/
				
				//debug("====================GolfMemInsActn === ���Ϲ߼� ===");
				if (!email_id.equals("")) {

					String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String emailTitle = "";
					String emailFileNm = "";
					
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");

					emailTitle = "[Golf Loun.G] ���ֱ׸��� ���� ��� ��û�� �Ϸ�Ǿ����ϴ�.";
					emailFileNm = "/email_tpl03.html";						
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+strToday+"|"+mail_HOPE_RGN_CODE+"|"+mail_BK_DATE+"|"+team_NUM+"|"+tot_PERS_NUM);
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setTo(email_id);
					//sender.send(emailEtt);
				}
				
				//sms�߼�
				if (!hp_TEL.equals("")) {

					// SMS ���� ����
					HashMap smsMap = new HashMap();
					
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", userNm);
					smsMap.put("sPhone1", hp_DDD_NO);
					smsMap.put("sPhone2", hp_TEL_HNO);
					smsMap.put("sPhone3", hp_TEL_SNO);
					
					//debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "639";
					String message = "[���ֱ׸�������] "+userNm+"�� ����û�� �Ϸ�Ǿ����ϴ� - Golf Loun.G";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					//debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}			
		        
				debug("=================GolfBkJjTimeReserveActn==============rsvt_SQL_NO : " + rsvt_SQL_NO);
				//request.setAttribute("returnUrl", "GolfBkJjTimeRsView.do?RSVT_SQL_NO="+rsvt_SQL_NO);
				//request.setAttribute("resultMsg", "������ �Ϸ�Ǿ����ϴ�.");  
		        request.setAttribute("RsView", rsView); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.	
	        } else {
				request.setAttribute("returnUrl", "GolfBkJjTimeView.do");
				request.setAttribute("resultMsg", "������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
