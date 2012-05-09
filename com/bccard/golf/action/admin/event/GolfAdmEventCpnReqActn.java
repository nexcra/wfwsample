/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEventCpnReqActn
*   �ۼ���    : E4NET ���弱
*   ����      : ������ > ���ΰ��� > ȸ������ > ȸ������Ʈ
*   �������  : Golf
*   �ۼ�����  : 2009-08-05
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.admin.event;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.event.*;
import com.bccard.golf.dbtao.proc.event.GolfEvntInterparkProc;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;

/******************************************************************************
* Topn
* @author	E4NET
* @version	1.0
******************************************************************************/
public class GolfAdmEventCpnReqActn extends GolfActn{
	
	public static final String TITLE = "������ > �̺�Ʈ���� > �����̺�Ʈ > ����";

	/***************************************************************************************
	* ��ž����Ʈ ������ȭ��
	* @param context		WaContext ��ü.  
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.  
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";	
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);
		
		try {
			// 01.��������üũ
			
			// 02.�Է°� ��ȸ		
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			Map paramMap = BaseAction.getParamToMap(request);
			paramMap.put("title", TITLE);

			// Request �� ����			
			String evnt_no      = parser.getParameter("evnt_no" ,"");               // �̺�Ʈ ��ȣ
			String cupn         = parser.getParameter("cupn" ,"");                // ���� ��ȣ		
			String userNm       = parser.getParameter("userNm" ,"");                 // �̸�	
			String socid        = parser.getParameter("socid" ,"");              // �ֹε�Ϲ�ȣ
			String email        = parser.getParameter("email" ,"");                 // �̸�	
			String useYN        = "N";
						
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);			
			dataSet.setString("evnt_no"  , evnt_no);
			dataSet.setString("cupn"     , cupn);		
			dataSet.setString("userNm"   , userNm);	
			dataSet.setString("socid"    , socid);	
			dataSet.setString("email"    , email);	
			dataSet.setString("evnt_no"  , "109");

			GolfEvntInterparkProc inter = (GolfEvntInterparkProc)context.getProc("GolfEvntInterparkProc");
			
			if(evnt_no.equals("109")){	// ������ũ �̺�Ʈ

				boolean doUpdate = false;
				int cnt = 1;			
	
				cnt = (int)inter.getDplCheck(context, request, dataSet);
	
				if(cnt == 0){
					debug("������ũ �̺�Ʈ���� �μ�Ʈ or ������Ʈ");
					doUpdate = (boolean) inter.insertCupnNumber(context, request, dataSet);
				}else{
					request.getSession().removeAttribute("isInterpark");
					request.setAttribute("msg","�̹� ������ �߱޹����̽��ϴ�."); 
				}					
	
				if(cnt == 0){
	
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
					String emailTitle = "";
					String emailFileNm = "";
	
					if(doUpdate == true){
						request.setAttribute("msg","������ ���������� �߱�ó�� �Ǿ����ϴ�."); 
						
						useYN = (String) inter.getUseYN(context, request, dataSet);	
	
						if(useYN.equals("Y")){
							emailFileNm = "/email_interpark1.html";
						}else{
							emailFileNm = "/email_interpark.html";
						}
	
						emailTitle = "��������� ȸ������ ������ũ ��������";
						emailFileNm = "/email_interpark.html";
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+cupn);
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setTo(email);
						sender.send(emailEtt);
					}else{
						request.setAttribute("msg","������ �߱�ó���� �����Ͽ����ϴ�."); 
					}	
				}			
				
			}else if(evnt_no.equals("119")){		// TM ȸ�� ��ȭ���ű� �̺�Ʈ

				EmailSend sender = new EmailSend();
				EmailEntity emailEtt = new EmailEntity("EUC_KR");
				String emailAdmin = "\"golfloung\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
				String imgPath = "<img src=\"";
				String hrefPath = "<a href=\"";
				String emailTitle = "";
				String emailFileNm = "";

				request.setAttribute("msg","������ ���������� �߱�ó�� �Ǿ����ϴ�."); 

				emailTitle = "[Golf Loun.G] ��������� TM ��ȭ���ű�";
				emailFileNm = "/eamil_tm_movie.html";
				emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, "");
				
				emailEtt.setFrom(emailAdmin);
				emailEtt.setSubject(emailTitle);
				emailEtt.setTo(email);
				sender.send(emailEtt);
			}

			paramMap.put("evnt_no",evnt_no);
			paramMap.put("cpn_no", cupn);						
			
	        request.setAttribute("paramMap", paramMap);
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}