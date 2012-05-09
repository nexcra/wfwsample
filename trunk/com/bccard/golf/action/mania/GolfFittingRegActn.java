/***************************************************************************************************
*   �� �ҽ��� �߰�������� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmManiaRegActn
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ���ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.mania;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
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
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.common.namo.MimeData;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.mania.GolfFittingInsDaoProc;
import com.bccard.golf.common.GolfUtil;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfFittingRegActn extends GolfActn{
	
	public static final String TITLE = "�����帮�������ν�û���� ���ó��";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(	WaContext context,	HttpServletRequest request,	HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
			// 01.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no	= (String)userEtt.getMemNo(); 							
			}
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			String nowYear = String.valueOf(cal.get(Calendar.YEAR));
			String nowMonth = String.valueOf(cal.get(Calendar.MONTH)+1);
			String nowDate = String.valueOf(cal.get(Calendar.DATE));
			String nowDay = nowYear +"�� "+ nowMonth +"�� "+ nowDate +"��";
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			String aplc_pe_clss = parser.getParameter("aplc_pe_clss", "");	// ������,���� ��ϱ���Ÿ��
			String id = parser.getParameter("id", "");						// ��û�ξ��̵�
			String note = parser.getParameter("note", "");						// ��û�� �̸� ������ NOTE�ʵ��
			
			String hp_ddd_no = parser.getParameter("hp_ddd_no", "");		// �޴���
			String hp_tel_hno = parser.getParameter("hp_tel_hno", "");		// 
			String hp_tel_sno = parser.getParameter("hp_tel_sno", "");		// 
			
			String phone = hp_ddd_no +"-"+ hp_tel_hno +"-"+ hp_tel_sno;
			
			String email1 = parser.getParameter("email_id", "");			// �̸���1
			String email2 = parser.getParameter("email_id2", "");			// �̸���2
			String email_id = email1+"@"+email2;
			
			String pic_date = parser.getParameter("pic_date", "");			// �׽�Ʈ �������
			String start_hh = parser.getParameter("start_hh", "");			// �׽�Ʈ ����ð�
						
			String ckd_code = parser.getParameter("ckd_code", "");			// �������Ŭ������
			String wclub ="";
			if(ckd_code.equals("0001"))wclub = "����̹�";
			if(ckd_code.equals("0002"))wclub = "������ ���";
			if(ckd_code.equals("0003"))wclub = "���̾�";
			if(ckd_code.equals("0004"))wclub = "����";
			
			String gcc_nm = parser.getParameter("gcc_nm", "");				// ���� 			
			String memo = parser.getParameter("memo", "");					// ����
			
			//pic_date = pic_date.length() == 10 ? DateUtil.format(pic_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			pic_date = GolfUtil.toDateFormat(pic_date);
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			//SEQ_NO, APLC_PE_CLSS, 
			dataSet.setString("ADMIN_NO", admin_no);
			dataSet.setString("ID", id);
			dataSet.setString("NOTE", note);
			dataSet.setString("APLC_PE_CLSS", aplc_pe_clss);

			dataSet.setString("HP_DDD_NO", hp_ddd_no);
			dataSet.setString("HP_TEL_HNO", hp_tel_hno);
			dataSet.setString("HP_TEL_SNO", hp_tel_sno);
			
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("PIC_DATE", pic_date);
			dataSet.setString("PIC_TIME", start_hh);
			dataSet.setString("GCC_NM", gcc_nm);
			dataSet.setString("CKD_CODE", ckd_code);			
			dataSet.setString("MEMO", memo);
			
			//ZP, REG_DATE
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfFittingInsDaoProc proc = (GolfFittingInsDaoProc)context.getProc("GolfFittingInsDaoProc");
			
			// ���� ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int addResult = proc.execute(context, dataSet);		
			
			HashMap smsMap = new HashMap();
			
			smsMap.put("ip", request.getRemoteAddr());
			smsMap.put("sName", note);
			smsMap.put("sPhone1", hp_ddd_no);
			smsMap.put("sPhone2", hp_tel_hno);
			smsMap.put("sPhone3", hp_tel_sno);
			
	        if (addResult == 1) {

	        	request.setAttribute("returnUrl", "FittingRegEnd.do");

				//request.setAttribute("resultMsg", "Ŭ������ �¶��ο�����  ���������� ó�� �Ǿ����ϴ�."); 
	        	
	        	//	���Ϲ߼� 
				if (!email_id.equals("")) {
					String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String emailTitle = note +"�� �¶��� ���� ��û�� �Ϸ�Ǿ����ϴ�.";
					String emailFileNm = "/email_tpl25.html";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
										
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle);
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, note+"|"+nowDay+"|"+note+"|"+phone+"|"+email_id+"|"+pic_date+"|"+wclub+"|"+gcc_nm+"|"+memo);
					//0�̸�1��¥02�̸�,3�޴���4�̸���5�׽�Ʈ�����6���Ŭ��7����8����
					emailEtt.setTo(email_id);
					//sender.send(emailEtt);
				}
				
				
				//	sms�߼�
				if (!phone.equals("")) {
					
					debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "654";
					String message = "[�¶�������]"+note+"�� ����û�� �Ϸ�Ǿ����ϴ�.- Golf Loun.G";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}
	        	
	        	
	        	
				
	        } else {
				request.setAttribute("returnUrl", "golfFittingRegFormPag.do");
				request.setAttribute("resultMsg", "Ŭ������ �¶��ο����� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
			
			// 05. Return �� ����			
			paramMap.put("addResult", String.valueOf(addResult));			
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.			
			
		} catch(Throwable t) {
			//debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}