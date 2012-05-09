/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfCardUpdActn.java
*   �ۼ���    : ������
*   ����      : ������ > ��ŷ > ž����ī������ ��ŷ ����
*   �������  : Golf
*   �ۼ�����  : 2010-10-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*	2010-11-03
***************************************************************************************************/
package com.bccard.golf.action.admin.booking.premium;

import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.proc.admin.booking.premium.GolfAdmTopGolfCardDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;

/******************************************************************************
* Golf
* @author	������ ���弱  
* @version	1.0
******************************************************************************/
public class GolfadmTopGolfCardUpdActn extends GolfActn{
	
	public static final String TITLE = "������ > ��ŷ > ž����ī������ ��ŷ ����";

	/***************************************************************************************
	* ���� ������ȭ��
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
			String green_nm				= parser.getParameter("GREEN_NM","");           //��û�������            
			String aplc_seq_no          = parser.getParameter("aplc_seq_no","");        //�����ȣ
			String pgrs_yn				= parser.getParameter("PGRS_YN","");    //����
			String teof_date              = parser.getParameter("TEOF_DATE","");            //��ŷ����
			String teof_time              = parser.getParameter("TEOF_TIME","");            //��ŷ�ð�
			String chng_aton              = parser.getParameter("CHNG_ATON","");            //Ȯ���ð�
			String temp_aton = "";
			String temp_time = "";
			
			String golf_lesn_rsvt_no = parser.getParameter("golf_lesn_rsvt_no",""); 	//ƼŸ�� ��Ϲ�ȣ
			
			//���������� �Ѿ�°��� Ȯ��
			String type             	 = parser.getParameter("type","");
			
			
			if(pgrs_yn.equals("B") && !chng_aton.equals("")){
				temp_aton = chng_aton.substring(0,2) + ":"+ chng_aton.substring(2,4);
			}
			if(teof_time.length() >= 4){
				temp_time = teof_time.substring(0,2) + ":"+ teof_time.substring(2,4);
			}
			
			String userNm = parser.getParameter("u_name","");            //�̸�
			String userId = parser.getParameter("u_id","");            //id
			
			String hp_DDD_NO = parser.getParameter("hp_no1","");            //����1
			String hp_TEL_HNO = parser.getParameter("hp_no2","");            //����2
			String hp_TEL_SNO = parser.getParameter("hp_no3","");            //����3
			String email = parser.getParameter("email","");					//email
			
			GregorianCalendar today = new GregorianCalendar ( );
	        int nYear = today.get ( today.YEAR );
	        int nMonth = today.get ( today.MONTH ) + 1;
	        int nDay = today.get ( today.DAY_OF_MONTH ); 
	        String strToday = nYear+"�� "+nMonth+"�� "+nDay+"��";
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("aplc_seq_no",aplc_seq_no);
			dataSet.setString("green_nm",green_nm);
			dataSet.setString("pgrs_yn",pgrs_yn);
			dataSet.setString("teof_date",teof_date);
			dataSet.setString("teof_time",teof_time);
			dataSet.setString("chng_aton",chng_aton);
			
			dataSet.setString("golf_lesn_rsvt_no",golf_lesn_rsvt_no);
			
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfAdmTopGolfCardDaoProc proc = (GolfAdmTopGolfCardDaoProc)context.getProc("GolfAdmTopGolfCardDaoProc");
			
			int evntUpd = proc.execute_update(context, request, dataSet);
			int ttimeUpd = 0;
			
			
			if("B".equals(pgrs_yn)){
				ttimeUpd = proc.execute_epsYn(context, request, dataSet);
			}
			
	        if (evntUpd == 1 ) {
	        	request.setAttribute("returnUrl", "admTopGolfCardList.do");
				request.setAttribute("resultMsg", "��ŷ���� �������� ���� �Ǿ����ϴ�.");      	
	        } else {
				request.setAttribute("returnUrl", "admTopGolfCardView.do");
				request.setAttribute("resultMsg", "��ŷ���� �������� ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	        }
	        
	        if("2".equals(type))
	        {
	        	 if (evntUpd == 1) {
	 				request.setAttribute("returnUrl", "admTopGolfTargetPpList.do");
	 				request.setAttribute("resultMsg", "��ŷ���� �������� ���� �Ǿ����ϴ�.");      	
	 	        } else {
	 				request.setAttribute("returnUrl", "admTopGolfTargetPpView.do");
	 				request.setAttribute("resultMsg", "��ŷ���� ������ ������ ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� �������� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
	 	        }
	        }
	
	        request.setAttribute("paramMap", paramMap);

			paramMap.put("green_nm",green_nm);			
			paramMap.put("aplc_seq_no",aplc_seq_no);
	        request.setAttribute("paramMap", paramMap);
	        
	        
	        if(pgrs_yn.equals("B")){
				// SMS ���� ����
	        	try {
				HashMap smsMap = new HashMap(); 
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", hp_DDD_NO);
				smsMap.put("sPhone2", hp_TEL_HNO);
				smsMap.put("sPhone3", hp_TEL_SNO);
				
				String smsClss = "637";
				String message = "[Golf Loun.G] "+userNm+"��,"+green_nm+ " "+teof_date+" "+temp_aton +" ��ŷȮ���Ǿ����ϴ�";
				//String message = "[VIP��ŷ] "+userNm+"�� "+gl_green_nm+" "+course+" "+bk_DATE+" "+bkps_TIME+":"+bkps_MINUTE+" ����Ϸ�- Golf Loun.G";
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = smsProc.send(smsClss, smsMap, message);
	        	} catch(Throwable t) {}
				// �̸��� ������
				if(!email.equals("")){
					try {
					String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
					String imgPath = "<img src=\"";
					String hrefPath = "<a href=\"";
					String emailTitle = "";
					String emailFileNm = "";
					
					EmailSend sender = new EmailSend();
					EmailEntity emailEtt = new EmailEntity("EUC_KR");
					
					emailTitle = "[Golf Loun.G] TOP����ī�� �����ŷ Ȯ�� �ȳ�";
					emailFileNm = "/email_tpl28.html";						
					emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+userId+"|"+green_nm + "|" + teof_date + "|" + temp_aton + "|" + strToday);
					
					emailEtt.setFrom(emailAdmin);
					emailEtt.setSubject(emailTitle); 
					emailEtt.setTo(email);
					sender.send(emailEtt);
					} catch(Throwable t) {}
				}
			
			}else if(pgrs_yn.equals("F")){
				// SMS ���� ����
				try {
				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", hp_DDD_NO);
				smsMap.put("sPhone2", hp_TEL_HNO);
				smsMap.put("sPhone3", hp_TEL_SNO);
				
				String smsClss = "637";
				String message = "[Golf Loun.G] "+userNm+"��,"+green_nm+ " "+teof_date+" "+temp_time +" ���� �Ǿ����ϴ�.";
				//String message = "[VIP��ŷ] "+userNm+"�� "+gl_green_nm+" "+course+" "+bk_DATE+" "+bkps_TIME+":"+bkps_MINUTE+" ����Ϸ�- Golf Loun.G";
				SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
				String smsRtn = smsProc.send(smsClss, smsMap, message);
			} catch(Throwable t) {}
			}
	        
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t);
		} 
		
		return super.getActionResponse(context, subpage_key);
		
	}
}
