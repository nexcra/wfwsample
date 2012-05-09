/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfMemJoinCorpActn
*   �ۼ���	: (��)�̵������
*   ����		: ����� > ȸ������ > ���ȸ������(����ī��)
*   �������	: golf 
*   �ۼ�����	: 2009-12-09
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.BaseAction;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.StringUtil;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.member.GolfMemJoinCorpDaoProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;


/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMemJoinCorpActn extends GolfActn { 
	
	public static final String TITLE = "����� > ȸ������ > ���ȸ������(����ī��)"; 

	/***************************************************************************************
	* ���� �����ȭ��
	* @param context		WaContext ��ü. 
	* @param request		HttpServletRequest ��ü. 
	* @param response		HttpServletResponse ��ü. 
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����. 
	***************************************************************************************/
	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {

		String subpage_key = "default";				
		Connection con = null;
		try {
			Map paramMap = BaseAction.getParamToMap(request);
			RequestParser parser = context.getRequestParser(subpage_key, request, response);
			GolfMemJoinCorpDaoProc corpProc = (GolfMemJoinCorpDaoProc)context.getProc("GolfMemJoinCorpDaoProc");
			DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
			
			// parameter  
			int join_step = parser.getIntParameter("join_step", 1);				// ȸ������ �ܰ�
			String user_nm = parser.getParameter("user_nm", "");				// �̸�
			String user_jumin_no = parser.getParameter("user_jumin_no", "");	// �ֹι�ȣ
			String buz_no = parser.getParameter("buz_no", "");					// ����ڵ�Ϲ�ȣ
			String card_no = parser.getParameter("card_no", "");				// ī���ȣ
			String bk_acct_no = parser.getParameter("bk_acct_no", "");			// �������¹�ȣ		
			String is_new_account = parser.getParameter("is_new_account", "Y");	// ���ο� ���� ���� ���� (Y/N)
			String is_new_mem = parser.getParameter("is_new_mem", "Y");			// ���ο� ���ȸ������ ���� ���� (TBENTPUSER) (Y/N)
			String addr_clss = parser.getParameter("addr_clss"); //�ּұ���(��:1, ��:2)
			String result_txt = "";												// ȸ��Ȯ�ΰ�� �޼���
			int result = 0;
			int go_step = 0;													// ȸ��Ȯ�ΰ�� �޼��� �� �̵��� ������ �ܰ�					
					
			debug("## GolfMemJoinCorpActn | join_step : " + join_step + "\n");
			
			// ȸ�����Դܰ迡 ���� action
            switch( join_step ){
            
	            // 01.����� Ȯ�� 
	            case 1 :           	
					paramMap.put("join_step", Integer.toString(join_step));	
					break;			
	
				// 02.�������
	            case 2 :
					user_jumin_no = parser.getParameter("user_jumin_no1", "") + parser.getParameter("user_jumin_no2", "");
	            	
	    			debug("## GolfMemJoinCorpActn | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + "\n");
	    			
	    			if(user_nm.equals("") || user_jumin_no.equals("")){
	    				join_step = 1;
	    				subpage_key = "result_msg";	
	    				result_txt = "�Է��Ͻ� ����� ������ �����ϴ�. <br>�ٽ� �ѹ� Ȯ���Ͽ� �ֽʽÿ�.";
	    			}
	    			else{
						paramMap.put("user_nm", user_nm);	
						paramMap.put("user_jumin_no", user_jumin_no);	
	    				subpage_key = "stipulation";	
	    			}
								
					paramMap.put("join_step", Integer.toString(join_step));	
	 				paramMap.put("go_step", Integer.toString(go_step));	
					break;
	
				// 03.ī������ Ȯ��
	            case 3 :

	    			debug("## GolfMemJoinCorpActn | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + "\n");
	
	    			if(user_nm.equals("") || user_jumin_no.equals("")){
	    				join_step = 1;
	    				subpage_key = "result_msg";	
	    				result_txt = "�Է��Ͻ� ����� ������ �����ϴ�. <br>�ٽ� �ѹ� Ȯ���Ͽ� �ֽʽÿ�.";
	    			}
	    			else{
						paramMap.put("user_nm", user_nm);	
						paramMap.put("user_jumin_no", user_jumin_no);	   		    
		 	           	subpage_key = "card_confirm";	    				
	    			}

	 				paramMap.put("join_step", Integer.toString(join_step));					
	 				paramMap.put("go_step", Integer.toString(go_step));	
					break;			
	
				// 04.ȸ������ �Է� (ī������ �� ��������� üũ)
	            case 4 :
	            	
	            	buz_no = parser.getParameter("buz_no1", "") + parser.getParameter("buz_no2", "") + parser.getParameter("buz_no3", "");
	            	card_no = parser.getParameter("card_no1", "") + parser.getParameter("card_no2", "") + parser.getParameter("card_no3", "") + parser.getParameter("card_no4", "");

	    			if(user_nm.equals("") || user_jumin_no.equals("") || buz_no.equals("") || card_no.equals("") || bk_acct_no.equals("")){
	    				subpage_key = "result_msg";	
	    				result_txt = "�Է��Ͻ� ����� ������ �����ϴ�. <br>�ٽ� �ѹ� Ȯ���Ͽ� �ֽʽÿ�.";
	    	  			
						paramMap.put("join_step", "1");	
						paramMap.put("result_txt", result_txt);	
						break;		    			
					}

	    			debug("## GolfMemJoinCorpActn | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + " | buz_no : " + buz_no + " | card_no : " + card_no + " | bk_acct_no : " + bk_acct_no + "\n");
	    			 			
					dataSet.setString("user_nm", user_nm);	
					dataSet.setString("user_jumin_no", user_jumin_no);	
					dataSet.setString("buz_no", buz_no);	
					dataSet.setString("card_no", card_no);	
					dataSet.setString("bk_acct_no", bk_acct_no);	
					
					// ���� ����ī�� ��������� Ȯ�� (TBENTPCDHD)
	    			String corpResult = corpProc.execute(context, dataSet, request);
	    
	    			// ���� ���� 
	       			if(corpResult.equals("00")){	
	       				subpage_key = "register";
	       				join_step = 4;
	       				
	       				// �ֹι�ȣ�� �α������� �ִ��� Ȯ�� (TBENTPUSER)
	       				DbTaoResult loginInfoResult = (DbTaoResult) corpProc.getLoginInfo(context, dataSet, request);
	       				if (loginInfoResult != null && loginInfoResult.isNext()) {
	       					loginInfoResult.first();
	       					loginInfoResult.next();
	       					
	       					// ȸ�����̵�
	       					String account = (String)loginInfoResult.getString("ACCOUNT");
	       			        if(account != null){
	       			          if(account.trim().equals("")){
	       			        	is_new_account = "Y";		// ���̵� ���� ����
	       			        	is_new_mem = "N";			
	       			          }else{
	       			        	is_new_account = "N";		// ���̵� ������ ����
	       			        	is_new_mem = "N";			
	       			          }
	       			        } 	       					
	       					
	       					// ��ȭ��ȣ 
	       					String user_tel_no = (String)loginInfoResult.getString("USER_TEL_NO");
	       					String user_tel_no1 = "";
	       					String user_tel_no2 = "";
	       					String user_tel_no3 = "";
	       					
	       					if (user_tel_no != null) {
	       						String[] user_tel_nos = StringUtil.stringToArray(user_tel_no, "-");
	       						if (user_tel_nos != null && user_tel_nos.length == 3) {
	       							user_tel_no1 = user_tel_nos[0];
	       							user_tel_no2 = user_tel_nos[1];
	       							user_tel_no3 = user_tel_nos[2];
	       						}
	       					}
	       					
	       					// �ѽ���ȣ 
	       					String user_fax_no = (String)loginInfoResult.getString("USER_FAX_NO");
	       					String user_fax_no1 = "";
	       					String user_fax_no2 = "";
	       					String user_fax_no3 = "";
	       					
	       					if (user_fax_no != null) {
	       						String[] user_fax_nos = StringUtil.stringToArray(user_fax_no, "-");
	       						if (user_fax_nos != null && user_fax_nos.length == 3) {
	       							user_fax_no1 = user_fax_nos[0];
	       							user_fax_no2 = user_fax_nos[1];
	       							user_fax_no3 = user_fax_nos[2];
	       						}
	       					}
	       					       					
	       					// �ڵ�����ȣ
	       					String user_mob_no = (String)loginInfoResult.getString("USER_MOB_NO");
	       					String user_mob_no1 = "";
	       					String user_mob_no2 = "";
	       					String user_mob_no3 = "";
	       					
	       					if (user_mob_no != null) {
	       						String[] user_mob_nos = StringUtil.stringToArray(user_mob_no, "-");
	       						if (user_mob_nos != null && user_mob_nos.length == 3) {
	       							user_mob_no1 = user_mob_nos[0];
	       							user_mob_no2 = user_mob_nos[1];
	       							user_mob_no3 = user_mob_nos[2];
	       						}
	       					}	       					
	       					
	       					String user_email = (String)loginInfoResult.getString("USER_EMAIL");
	       					String user_email1 = "";
	       					String user_email2 = "";

	       					if (user_email != null) {
	       						String[] user_emails = StringUtil.stringToArray(user_email, "@");
	       						if (user_emails != null && user_emails.length == 2) {
	       							user_email1 = user_emails[0];
	       							user_email2 = user_emails[1];
	       						}
	       					}
	       					
	       					is_new_account = "N";	// �α��ΰ��� ���� ���� 
	       					paramMap.put("mem_id", (String)loginInfoResult.getString("MEM_ID"));				// ȸ����ȣ(TBENTPUSER : MEM_ID)
	       					paramMap.put("pre_account", (String)loginInfoResult.getString("ACCOUNT"));			// ȸ�����̵�(TBENTPUSER : ACCOUNT)
	       					paramMap.put("user_dept_nm", (String)loginInfoResult.getString("USER_DEPT_NM"));	// �μ��� 
	       					paramMap.put("user_level", (String)loginInfoResult.getString("USER_LEVEL"));		// ����
	       					paramMap.put("user_tel_no1", user_tel_no1);	// ��ȭ��ȣ1	 
	       					paramMap.put("user_tel_no2", user_tel_no2);	// ��ȭ��ȣ2	 
	       					paramMap.put("user_tel_no3", user_tel_no3);	// ��ȭ��ȣ3	 
	       					paramMap.put("user_fax_no1", user_fax_no1);	// �ѽ���ȣ1	 
	       					paramMap.put("user_fax_no2", user_fax_no2);	// �ѽ���ȣ2
	       					paramMap.put("user_fax_no3", user_fax_no3);	// �ѽ���ȣ3	 
	       					paramMap.put("user_mob_no1", user_mob_no1);	// �ڵ�����ȣ1	 
	       					paramMap.put("user_mob_no2", user_mob_no2);	// �ڵ�����ȣ2
	       					paramMap.put("user_mob_no3", user_mob_no3);	// �ڵ�����ȣ3	 
	       					paramMap.put("user_email1", user_email1);	// �̸���1	 
	       					paramMap.put("user_email2", user_email2);	// �̸���2

	       				}
	       				else{
	       					is_new_account = "Y";		// ���̵� ���� ����
	       					is_new_mem = "Y";			// ���ȸ������ ���� ����
	       				}
	       				
	       				// ������� ��������(TBENTPINFO)
	       				DbTaoResult corpInfoResult = (DbTaoResult) corpProc.getCorpInfo(context, dataSet, request);
	       				if (corpInfoResult != null && corpInfoResult.isNext()) {
	       					corpInfoResult.first();
	       					corpInfoResult.next();
	       					
	       					String firm_zip = (String)corpInfoResult.getString("FIRM_ZIP");
	       					String firm_zip1 = "";
	       					String firm_zip2 = "";
	       				  	if(!"".equals(firm_zip) && firm_zip.length() == 6) {
	       				  		firm_zip1 = firm_zip.substring(0,3);
	       				  		firm_zip2 = firm_zip.substring(3,6);
	       				  	}
	       					
	       					paramMap.put("firm_nm", (String)corpInfoResult.getString("FIRM_NM"));			// ȸ���
	       					paramMap.put("firm_rep_nm", (String)corpInfoResult.getString("FIRM_REP_NM"));	// ��ǥ�ڸ� 
	       					paramMap.put("firm_zip1", firm_zip1);											// �����ȣ1
	       					paramMap.put("firm_zip2", firm_zip2);											// �����ȣ2
	       					paramMap.put("firm_addr1", (String)corpInfoResult.getString("FIRM_ADDR1"));		// �ּ�1
	       					paramMap.put("firm_addr2", (String)corpInfoResult.getString("FIRM_ADDR2"));		// �ּ�2
	       					paramMap.put("addr_clss", (String)corpInfoResult.getString("ADDR_CLSS"));		// �ּұ���(��:1, ��:2)
	       					paramMap.put("is_corpinfo", "Y");												// ������� �ִ��� ����
	       					
	       				}
	       				
					}
	       			// �̸� Ʋ�� 
		            else if(corpResult.equals("01")){
						subpage_key = "result_msg";
						join_step = 3;	
						go_step = 1;
						result_txt = "�Է��Ͻ� �ش� ī�忡 ���� ������ ������ ��Ȯ���� �ʽ��ϴ�. <br>�ٽ� �ѹ� Ȯ���Ͽ� �ֽʽÿ�.";	
					}
	       			// �ֹι�ȣ Ʋ�� 
	            	else if(corpResult.equals("02")){
						subpage_key = "result_msg";
						join_step = 3;	
						go_step = 1;
						result_txt = "�Է��Ͻ� �ش� ī�忡 ���� ������ �ֹε�Ϲ�ȣ�� ��Ȯ���� �ʽ��ϴ�. <br>�ٽ� �ѹ� Ȯ���Ͽ� �ֽʽÿ�.";	
					}				
	       			// ī���ȣ ���� 
					else if(corpResult.equals("99")){
						subpage_key = "result_msg";
						join_step = 3;	
						result_txt = "�Է��Ͻ� �ش� ī��� �������ī��� ��ϵǾ� ���� �ʽ��ϴ�. <br>�ٽ� �ѹ� Ȯ���Ͽ� �ֽʽÿ�.";	
					}
	       			
	       			
	       			String buz_no_txt = buz_no.substring(0,3) + "-" + buz_no.substring(3,5) + "-*****";
	       			String card_no_txt = GolfUtil.getFmtCardNo(card_no);	       			
	       			String bk_acct_no_txt = bk_acct_no.substring(0,bk_acct_no.length()-5) + "*****";
	       			String user_jumin_no_txt1 = user_jumin_no.substring(0,6);
	       			String user_jumin_no_txt2 = user_jumin_no.substring(6);

	    			debug("## GolfMemJoinCorpActn | is_new_account : " + is_new_account + " | is_new_mem : " + is_new_mem + "\n");

   					paramMap.put("is_new_account", is_new_account);
  					paramMap.put("is_new_mem", is_new_mem);
					paramMap.put("user_nm", user_nm);	
					paramMap.put("user_jumin_no", user_jumin_no);	   		    
					paramMap.put("user_jumin_no_txt1", user_jumin_no_txt1);	   		    
					paramMap.put("user_jumin_no_txt2", user_jumin_no_txt2);	   		    
					paramMap.put("buz_no", buz_no);	
					paramMap.put("buz_no_txt", buz_no_txt);	
					paramMap.put("card_no", card_no);	   		    
					paramMap.put("card_no_txt", card_no_txt);	   		    
					paramMap.put("bk_acct_no", bk_acct_no);	   		    
					paramMap.put("bk_acct_no_txt", bk_acct_no_txt);	   		    
					paramMap.put("join_step", Integer.toString(join_step));	
	 				paramMap.put("go_step", Integer.toString(go_step));	
					paramMap.put("result_txt", result_txt);	
					break;			
	
				// 05.ȸ������ �Ϸ� (������ �Է»��� ���)
	            case 5 :

	    			debug("## GolfMemJoinCorpActn | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + " | buz_no : " + buz_no + " | card_no : " + card_no + " | bk_acct_no : " + bk_acct_no + "\n");
	    			debug("## GolfMemJoinCorpActn | is_new_account : " + is_new_account + " | is_new_mem : " + is_new_mem + "\n");

					dataSet.setString("is_new_account", is_new_account);	
					dataSet.setString("is_new_mem", is_new_mem);	
					dataSet.setString("mem_id",  parser.getParameter("mem_id", ""));	
					dataSet.setString("user_nm", user_nm);	
					dataSet.setString("user_jumin_no", user_jumin_no);	
					dataSet.setString("buz_no", buz_no);	
					dataSet.setString("card_no", card_no);	
					dataSet.setString("bk_acct_no", bk_acct_no);	
					dataSet.setString("account", parser.getParameter("account", ""));												// ���̵�
					dataSet.setString("passwd", parser.getParameter("passwd", ""));													// ��й�ȣ
					dataSet.setString("pass_que_clss", parser.getParameter("pass_que_clss", ""));									// ��й�ȣ ����
					dataSet.setString("pass_ans", parser.getParameter("pass_ans", ""));												// ��й�ȣ �亯
					dataSet.setString("firm_nm", parser.getParameter("firm_nm", ""));												// ȸ���
					dataSet.setString("firm_rep_nm", parser.getParameter("firm_rep_nm", ""));										// ��ǥ�ڸ�
					dataSet.setString("firm_zip", parser.getParameter("firm_zip1", "") + parser.getParameter("firm_zip2", ""));		// ȸ�� �����ȣ
					dataSet.setString("firm_addr1", parser.getParameter("firm_addr1", ""));											// ȸ�� �ּ�1
					dataSet.setString("firm_addr2", parser.getParameter("firm_addr2", ""));											// ȸ�� �ּ�2
					dataSet.setString("addr_clss", addr_clss);
					dataSet.setString("user_dept_nm", parser.getParameter("user_dept_nm", ""));										// �μ���
					dataSet.setString("user_level", parser.getParameter("user_level", ""));											// ����
					dataSet.setString("user_tel_no", parser.getParameter("user_tel_no1", "") + "-" + parser.getParameter("user_tel_no2", "") + "-" + parser.getParameter("user_tel_no3", ""));	// ��ȭ��ȣ
					dataSet.setString("user_email", parser.getParameter("user_email1", "") + "@" + parser.getParameter("user_email2", ""));														// �̸��� 
					dataSet.setString("user_fax_no", parser.getParameter("user_fax_no1", "") + "-" + parser.getParameter("user_fax_no2", "") + "-" + parser.getParameter("user_fax_no3", ""));	// �ѽ���ȣ
					dataSet.setString("user_mob_no", parser.getParameter("user_mob_no1", "") + "-" + parser.getParameter("user_mob_no2", "") + "-" + parser.getParameter("user_mob_no3", ""));	// �ڵ�����ȣ 

	            	// ȸ�����	            	
					String regResult = corpProc.registerCorpInfo(context, dataSet, request);					  	

					if(regResult.equals("00")){	// ������ 
		              	subpage_key = "result_msg";
		              			              	
/*		              	
  						// ȸ������ ���� �߼�
		              	String email_id = parser.getParameter("user_email1", "") + "@" + parser.getParameter("user_email2", "");
		              	String account = parser.getParameter("account", "");
		              	String pre_account = parser.getParameter("pre_account", "");		              	
		              	if(account == null || account.equals(""))	account = pre_account;
		              	
						if (!email_id.equals("")) {

							String emailAdmin = "bcadmin@bccard.com";
							String imgPath = "<img src=\"";
							String hrefPath = "<a href=\"";
							String emailTitle = "";
							String emailFileNm = "";
							
							EmailSend sender = new EmailSend();
							EmailEntity emailEtt = new EmailEntity("EUC_KR");
							
							emailTitle = "BCCARD ȸ�������� �����մϴ�.";
							emailFileNm = "/corp_join.html";
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, user_nm+"|"+account);
							
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							emailEtt.setTo(email_id);
							sender.send(emailEtt);
							 
						}	
*/							              	
		              	
					}
					else if(regResult.equals("01")){	// ���̵� �ߺ� 
						subpage_key = "result_msg";
						join_step = 4;	
						go_step = 1;
						result_txt = "�̹� �����ϴ� ���̵��Դϴ�. <br>�ٽ� �ѹ� Ȯ���Ͽ� �ֽʽÿ�.";	
					}
					
	 				paramMap.put("join_step", Integer.toString(join_step));	
	 				paramMap.put("go_step", Integer.toString(go_step));	
					paramMap.put("result_txt", result_txt);	
	              	break;
					
	            default :
	                break; 
	            
            }
			 		  
	        request.setAttribute("paramMap", paramMap); //��� �Ķ���Ͱ��� �ʿ� ��� ��ȯ�Ѵ�.
	        
		} catch(Throwable t) {
			debug(TITLE, t);
			//t.printStackTrace();
			throw new GolfException(TITLE, t); 
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		
		return super.getActionResponse(context, subpage_key);
		
	}
		
	
}
