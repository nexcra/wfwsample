/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admSkyTimeChgActn
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �����̾���ŷ ƼŸ�� ���⿩�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfadmSkyRsUpdActn extends GolfActn{
	
	public static final String TITLE = "������ �����̾���ŷ ƼŸ�� ���⿩�� ó��"; 

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		int lessonDelResult = 0;
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

	        // ���� ��¥ ��������
			GregorianCalendar today = new GregorianCalendar ( );
	        String [] dayOfWeek = {"","��","��","ȭ","��","��","��","��"};	        

	        int nYear = today.get ( today.YEAR );
	        int nMonth = today.get ( today.MONTH ) + 1;
	        int nDay = today.get ( today.DAY_OF_MONTH ); 
	        int nYoil = today.get ( today.DAY_OF_WEEK );
	        int hour = today.get(today.HOUR);
	        int minute = today.get(today.MINUTE);
	        
			// Request �� ����
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
			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			dataSet.setString("RSVT_SQL_NO", rsvt_SQL_NO);
			dataSet.setString("RSVT_YN", rsvt_YN);
			dataSet.setString("CTNT", ctnt);
			dataSet.setString("CDHD_ID", id);
			dataSet.setString("APPR_OPION", appr_opion);
			dataSet.setString("ADD_APPR_OPION", add_appr_opion);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfadmSkyRsUpdDaoProc proc = (GolfadmSkyRsUpdDaoProc)context.getProc("GolfadmSkyRsUpdDaoProc");		
			int editResult = proc.execute(context, dataSet);
			
	        if (editResult == 1) {
	        	
	        	if(rsvt_YN.equals("I")){
	        		debug("===========GolfadmSkyRsUpdActn=======�ӹ�����ϰ�� ���Ϻ�����.");
	        		if (!email1.equals("")) {

						String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String emailTitle = "";
						String emailFileNm = "";
						
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");

						emailTitle = "[Golf Loun.G] SKY72 �帲�ὺ ��ŷ �ӹ���� �˷��帳�ϴ�.";
						emailFileNm = "/email_tpl05.html";						
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, name+"|"+id+"|"+hp_NO+"|"+socid+"|"+reg_DATE+"|"+cancel_DATE+"|"+hole+"|"+bk_DATE+"|"+bk_TIME+"|"+tot_PERS_NUM+"|"+ctnt);
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setTo(email1);
						//sender.send(emailEtt);
					}
	        	}
	        	
				request.setAttribute("returnUrl", "admSkyRsList.do");
				request.setAttribute("resultMsg", "���� �������� ���� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", "admSkyRsList.do");
				request.setAttribute("resultMsg", "���� ������ ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
