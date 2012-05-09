/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
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
import com.bccard.golf.dbtao.proc.mania.GolfManiaInsDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfManiaRegActn extends GolfActn{
	
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
		String lsn_nm ="";
		String admin_no = "";
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		String addrtype = "";
		int intMemGrade = 0; 
		
		// 00.���̾ƿ� URL ����
		String layout = super.getActionParam(context, "layout");
		request.setAttribute("layout", layout);

		try {
//			 01.��������üũ
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userNm		= (String)usrEntity.getName(); 
				memClss		= (String)usrEntity.getMemberClss();
				userId		= (String)usrEntity.getAccount(); 
				juminno 	= (String)usrEntity.getSocid(); 
				memGrade 	= (String)usrEntity.getMemGrade(); 
				intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			}

			if(userId != null && !"".equals(userId)){
				isLogin = "1";
			} else {
				isLogin = "0";
				userNm	= "";
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
			String id = userId ;											// ��û�ξ��̵�
			String cp_nm = parser.getParameter("cp_nm", "");				// ��û�� �̸�(�̸������� �����ʵ�)
			String zip1 = parser.getParameter("zipcode1", "");				// �����ȣ1
			String zip2 = parser.getParameter("zipcode2", "");				// �����ȣ2
			String addr = parser.getParameter("zipaddr", "");				// �ּ�1
			String addr2 = parser.getParameter("detailaddr", "");			// �ּ�2
			String addr_clss = parser.getParameter("addr_clss"); 			//�ּұ���(��:1, ��:2)
			String chg_ddd_no = parser.getParameter("chg_ddd_no", "");		// ����ó ����
			String chg_tel_hno = parser.getParameter("chg_tel_hno", "");	// ����ó ���
			String chg_tel_sno = parser.getParameter("chg_tel_sno", "");	// ����ó ��
			String hp_ddd_no = parser.getParameter("hp_ddd_no", "");		// �޴��� ����
			String hp_tel_hno = parser.getParameter("hp_tel_hno", "");		// �޴��� ���
			String hp_tel_sno = parser.getParameter("hp_tel_sno", "");		// �޴��� ��
			String email_1 = parser.getParameter("email_id", "");			// �̸��� ���ڸ�
			String email_2 = parser.getParameter("email_id2", "");			// �̸��� ���ڸ�
			String email_id = email_1+"@"+email_2;							// �̸��� ����
			String str_plc = parser.getParameter("str_plc", "");			// ������
			String price = "000,000��";			// ������

			//	�̸��Ͽ��� ���
			String adress = zip1+"-"+zip2+" "+addr+" "+addr2;
			String phone = chg_ddd_no + chg_tel_hno + chg_tel_sno;
			String tel_no = chg_ddd_no+"-"+chg_tel_hno+"-"+chg_tel_sno;
			String hp_no = hp_ddd_no+"-"+hp_tel_hno+"-"+hp_tel_sno;
			// 	�̸��Ͽ��� ���
			
			String pic_date = parser.getParameter("pic_date", "");						// �Ⱦ���¥
			String toff_date = parser.getParameter("toff_date", "");					// Ƽ������¥
			String start_hh = parser.getParameter("start_hh", "");						// �Ⱦ��ð�
			String start_mi = parser.getParameter("start_mi", "");						// �Ⱦ���
			String end_hh = parser.getParameter("end_hh", "");							// Ƽ�����ð�
			String end_mi = parser.getParameter("end_mi", "");							// Ƽ������
			String gcc_nm = parser.getParameter("gcc_nm", "");							// �������
			String golf_mgz_dlv_clss = parser.getParameter("golf_mgz_dlv_clss", "");	// ����� ����
			if(golf_mgz_dlv_clss.equals("H")) { addrtype ="����"; }
			if(golf_mgz_dlv_clss.equals("O")) { addrtype ="����"; }
			String ckd_code = parser.getParameter("ckd_code", "");						// ����
			int tk_prs = parser.getIntParameter("tk_prs", 0);							// �����ο�
			String memo = parser.getParameter("memo", "");								// ��û/Ư�̻���
			String scoop_cp_cd		= parser.getParameter("scoop_cp_cd", ""); 			//0002:���������� 0003:��������
			
			pic_date = pic_date.length() == 10 ? DateUtil.format(pic_date, "yyyy-MM-dd", "yyyyMMdd"): "";
			toff_date = toff_date.length() == 10 ? DateUtil.format(toff_date, "yyyy-MM-dd", "yyyyMMdd"): "";			
			
			// 03.Proc �� ���� �� ���� (Proc�� dataSet ������ �迭(?)�� request�� �Ǵ� ��ȸ���� ������.)
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			dataSet.setString("ADMIN_NO", admin_no);
			dataSet.setString("ID", id);
			
			dataSet.setString("CP_NM", cp_nm);
			dataSet.setString("APLC_PE_CLSS", aplc_pe_clss);
			dataSet.setString("ZIPCODE", zip1+""+zip2);
			
			dataSet.setString("ADDR", addr);
			dataSet.setString("ADDR2", addr2);
			dataSet.setString("ADDR_CLSS", addr_clss);
			dataSet.setString("CHG_DDD_NO", chg_ddd_no);
			dataSet.setString("CHG_TEL_HNO", chg_tel_hno);
			dataSet.setString("CHG_TEL_SNO", chg_tel_sno);
			dataSet.setString("HP_DDD_NO", hp_ddd_no);
			dataSet.setString("HP_TEL_HNO", hp_tel_hno);
			dataSet.setString("HP_TEL_SNO", hp_tel_sno);
			dataSet.setString("EMAIL_ID", email_id);
			dataSet.setString("STR_PLC", str_plc);
			dataSet.setString("PIC_DATE", pic_date);
			dataSet.setString("TOFF_DATE", toff_date);
			dataSet.setString("PIC_TIME", start_hh+start_mi);
			dataSet.setString("TOFF_TIME", end_hh+end_mi);
			dataSet.setString("GCC_NM", gcc_nm);
			dataSet.setString("GOLF_MGZ_DLV_PL_CLSS", golf_mgz_dlv_clss);
			dataSet.setString("CKD_CODE", ckd_code);			
			dataSet.setInt("TK_PRS", tk_prs);
			dataSet.setString("MEMO", memo);
			
			// 04.���� ���̺�(Proc) ��ȸ
			GolfManiaInsDaoProc proc = (GolfManiaInsDaoProc)context.getProc("GolfManiaInsDaoProc");
			
			// ���α׷� ��� ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
			int addResult = proc.execute(context, dataSet);			

			
			HashMap smsMap = new HashMap();
			
			smsMap.put("ip", request.getRemoteAddr());
			smsMap.put("sName", userNm);
			smsMap.put("sPhone1", hp_ddd_no);
			smsMap.put("sPhone2", hp_tel_hno);
			smsMap.put("sPhone3", hp_tel_sno);
			
	        if (addResult == 1) {
	        	
	        	if (scoop_cp_cd.equals("0003")) {
	        		lsn_nm = "�����Ű��� ����";
	        		
	        		//	���Ϲ߼� 
					if (!email_id.equals("")) {
						String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String emailTitle = userNm +"�� �����Ű��� ���� ��û�� �Ϸ�Ǿ����ϴ�.";
						String emailFileNm = "/email_tpl22.html";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
											
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+nowDay+"|"+userNm+"|"+tel_no+"|"+hp_no+"|"+email_id+"|"+addrtype+"|"+adress);
						emailEtt.setTo(email_id);
						//sender.send(emailEtt);
					}
					
					
//					sms�߼�
					if (!phone.equals("")) {
						
						debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "653";
						String message = "[����������������]"+userNm+"�� ����û�� �Ϸ�Ǿ����ϴ�.- Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = smsProc.send(smsClss, smsMap, message);
						debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
					}

	        	}else{
	        		lsn_nm = "����������";
	        		
//	        		���Ϲ߼�
					if (!email_id.equals("")) {
						String emailAdmin = "\"���������\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String emailTitle = userNm +"�� ���������� ��û�� �Ϸ�Ǿ����ϴ�.";
						String emailFileNm = "/email_tpl23.html";
						String imgPath = "<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
											
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle);
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, userNm+"|"+nowDay+"|"+userNm+"|"+tel_no+"|"+hp_no+"|"+email_id+"|"+str_plc+"|"+pic_date+"|"+toff_date+"|"+gcc_nm+"|"+ckd_code+"|"+tk_prs+"|"+price+"|"+memo);
						//2�̸�,3�ּ�,4����ó,�޴���,E-mail,������,�Ⱦ��ð�,Ƽ�����ð�,�������,����,�����ο�,����,��û/Ư�̻���
						emailEtt.setTo(email_id);
						//sender.send(emailEtt);
					}
					
					
//					sms�߼�
					if (!phone.equals("")) {
						
						debug("SMS����>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
						String smsClss = "649";
						String message = "[����������]"+userNm+"�� ����û�� �Ϸ�Ǿ����ϴ�.- Golf Loun.G";
						SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						String smsRtn = smsProc.send(smsClss, smsMap, message);
						debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
					}
	        	}
	        	

	        	
	        	
	        	
	        	if (scoop_cp_cd.equals("0003")) {
	        		request.setAttribute("returnUrl", "golfMagazineRegEnd.do");
	        	}else{
	        		request.setAttribute("returnUrl", "golfManiaRegEnd.do");
	        	}

				//request.setAttribute("resultMsg", "���� ��û ���α׷� ����� ���������� ó�� �Ǿ����ϴ�.");   
				
	        } else {
				request.setAttribute("returnUrl", "ManiaRegForm.do");
				request.setAttribute("resultMsg", "�����帮�������� ��û ���α׷� ����� ���������� ó�� ���� �ʾҽ��ϴ�.\\n\\n�ݺ������� ��ϵ��� ���� ��� �����ڿ� �����Ͻʽÿ�.");		        		
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
