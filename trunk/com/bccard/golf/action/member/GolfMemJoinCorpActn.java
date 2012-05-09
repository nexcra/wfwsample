/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfMemJoinCorpActn
*   작성자	: (주)미디어포스
*   내용		: 사용자 > 회원가입 > 기업회원가입(지정카드)
*   적용범위	: golf 
*   작성일자	: 2009-12-09
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMemJoinCorpActn extends GolfActn { 
	
	public static final String TITLE = "사용자 > 회원가입 > 기업회원가입(지정카드)"; 

	/***************************************************************************************
	* 골프 사용자화면
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
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
			int join_step = parser.getIntParameter("join_step", 1);				// 회원가입 단계
			String user_nm = parser.getParameter("user_nm", "");				// 이름
			String user_jumin_no = parser.getParameter("user_jumin_no", "");	// 주민번호
			String buz_no = parser.getParameter("buz_no", "");					// 사업자등록번호
			String card_no = parser.getParameter("card_no", "");				// 카드번호
			String bk_acct_no = parser.getParameter("bk_acct_no", "");			// 결제계좌번호		
			String is_new_account = parser.getParameter("is_new_account", "Y");	// 새로운 계정 생성 여부 (Y/N)
			String is_new_mem = parser.getParameter("is_new_mem", "Y");			// 새로운 기업회원정보 생성 여부 (TBENTPUSER) (Y/N)
			String addr_clss = parser.getParameter("addr_clss"); //주소구분(구:1, 신:2)
			String result_txt = "";												// 회원확인결과 메세지
			int result = 0;
			int go_step = 0;													// 회원확인결과 메세지 후 이동할 페이지 단계					
					
			debug("## GolfMemJoinCorpActn | join_step : " + join_step + "\n");
			
			// 회원가입단계에 따라 action
            switch( join_step ){
            
	            // 01.사용자 확인 
	            case 1 :           	
					paramMap.put("join_step", Integer.toString(join_step));	
					break;			
	
				// 02.약관동의
	            case 2 :
					user_jumin_no = parser.getParameter("user_jumin_no1", "") + parser.getParameter("user_jumin_no2", "");
	            	
	    			debug("## GolfMemJoinCorpActn | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + "\n");
	    			
	    			if(user_nm.equals("") || user_jumin_no.equals("")){
	    				join_step = 1;
	    				subpage_key = "result_msg";	
	    				result_txt = "입력하신 사용자 정보가 없습니다. <br>다시 한번 확인하여 주십시요.";
	    			}
	    			else{
						paramMap.put("user_nm", user_nm);	
						paramMap.put("user_jumin_no", user_jumin_no);	
	    				subpage_key = "stipulation";	
	    			}
								
					paramMap.put("join_step", Integer.toString(join_step));	
	 				paramMap.put("go_step", Integer.toString(go_step));	
					break;
	
				// 03.카드정보 확인
	            case 3 :

	    			debug("## GolfMemJoinCorpActn | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + "\n");
	
	    			if(user_nm.equals("") || user_jumin_no.equals("")){
	    				join_step = 1;
	    				subpage_key = "result_msg";	
	    				result_txt = "입력하신 사용자 정보가 없습니다. <br>다시 한번 확인하여 주십시요.";
	    			}
	    			else{
						paramMap.put("user_nm", user_nm);	
						paramMap.put("user_jumin_no", user_jumin_no);	   		    
		 	           	subpage_key = "card_confirm";	    				
	    			}

	 				paramMap.put("join_step", Integer.toString(join_step));					
	 				paramMap.put("go_step", Integer.toString(go_step));	
					break;			
	
				// 04.회원정보 입력 (카드정보 및 사용자정보 체크)
	            case 4 :
	            	
	            	buz_no = parser.getParameter("buz_no1", "") + parser.getParameter("buz_no2", "") + parser.getParameter("buz_no3", "");
	            	card_no = parser.getParameter("card_no1", "") + parser.getParameter("card_no2", "") + parser.getParameter("card_no3", "") + parser.getParameter("card_no4", "");

	    			if(user_nm.equals("") || user_jumin_no.equals("") || buz_no.equals("") || card_no.equals("") || bk_acct_no.equals("")){
	    				subpage_key = "result_msg";	
	    				result_txt = "입력하신 사용자 정보가 없습니다. <br>다시 한번 확인하여 주십시요.";
	    	  			
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
					
					// 법인 지정카드 사용자정보 확인 (TBENTPCDHD)
	    			String corpResult = corpProc.execute(context, dataSet, request);
	    
	    			// 가입 가능 
	       			if(corpResult.equals("00")){	
	       				subpage_key = "register";
	       				join_step = 4;
	       				
	       				// 주민번호로 로그인정보 있는지 확인 (TBENTPUSER)
	       				DbTaoResult loginInfoResult = (DbTaoResult) corpProc.getLoginInfo(context, dataSet, request);
	       				if (loginInfoResult != null && loginInfoResult.isNext()) {
	       					loginInfoResult.first();
	       					loginInfoResult.next();
	       					
	       					// 회원아이디
	       					String account = (String)loginInfoResult.getString("ACCOUNT");
	       			        if(account != null){
	       			          if(account.trim().equals("")){
	       			        	is_new_account = "Y";		// 아이디 새로 만듬
	       			        	is_new_mem = "N";			
	       			          }else{
	       			        	is_new_account = "N";		// 아이디 만들지 않음
	       			        	is_new_mem = "N";			
	       			          }
	       			        } 	       					
	       					
	       					// 전화번호 
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
	       					
	       					// 팩스번호 
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
	       					       					
	       					// 핸드폰번호
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
	       					
	       					is_new_account = "N";	// 로그인계정 생성 안함 
	       					paramMap.put("mem_id", (String)loginInfoResult.getString("MEM_ID"));				// 회원번호(TBENTPUSER : MEM_ID)
	       					paramMap.put("pre_account", (String)loginInfoResult.getString("ACCOUNT"));			// 회원아이디(TBENTPUSER : ACCOUNT)
	       					paramMap.put("user_dept_nm", (String)loginInfoResult.getString("USER_DEPT_NM"));	// 부서명 
	       					paramMap.put("user_level", (String)loginInfoResult.getString("USER_LEVEL"));		// 직위
	       					paramMap.put("user_tel_no1", user_tel_no1);	// 전화번호1	 
	       					paramMap.put("user_tel_no2", user_tel_no2);	// 전화번호2	 
	       					paramMap.put("user_tel_no3", user_tel_no3);	// 전화번호3	 
	       					paramMap.put("user_fax_no1", user_fax_no1);	// 팩스번호1	 
	       					paramMap.put("user_fax_no2", user_fax_no2);	// 팩스번호2
	       					paramMap.put("user_fax_no3", user_fax_no3);	// 팩스번호3	 
	       					paramMap.put("user_mob_no1", user_mob_no1);	// 핸드폰번호1	 
	       					paramMap.put("user_mob_no2", user_mob_no2);	// 핸드폰번호2
	       					paramMap.put("user_mob_no3", user_mob_no3);	// 핸드폰번호3	 
	       					paramMap.put("user_email1", user_email1);	// 이메일1	 
	       					paramMap.put("user_email2", user_email2);	// 이메일2

	       				}
	       				else{
	       					is_new_account = "Y";		// 아이디 새로 만듬
	       					is_new_mem = "Y";			// 기업회원정보 새로 만듬
	       				}
	       				
	       				// 기업정보 가져오기(TBENTPINFO)
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
	       					
	       					paramMap.put("firm_nm", (String)corpInfoResult.getString("FIRM_NM"));			// 회사명
	       					paramMap.put("firm_rep_nm", (String)corpInfoResult.getString("FIRM_REP_NM"));	// 대표자명 
	       					paramMap.put("firm_zip1", firm_zip1);											// 우편번호1
	       					paramMap.put("firm_zip2", firm_zip2);											// 우편번호2
	       					paramMap.put("firm_addr1", (String)corpInfoResult.getString("FIRM_ADDR1"));		// 주소1
	       					paramMap.put("firm_addr2", (String)corpInfoResult.getString("FIRM_ADDR2"));		// 주소2
	       					paramMap.put("addr_clss", (String)corpInfoResult.getString("ADDR_CLSS"));		// 주소구분(구:1, 신:2)
	       					paramMap.put("is_corpinfo", "Y");												// 기업정보 있는지 여부
	       					
	       				}
	       				
					}
	       			// 이름 틀림 
		            else if(corpResult.equals("01")){
						subpage_key = "result_msg";
						join_step = 3;	
						go_step = 1;
						result_txt = "입력하신 해당 카드에 대한 지정자 성명이 정확하지 않습니다. <br>다시 한번 확인하여 주십시요.";	
					}
	       			// 주민번호 틀림 
	            	else if(corpResult.equals("02")){
						subpage_key = "result_msg";
						join_step = 3;	
						go_step = 1;
						result_txt = "입력하신 해당 카드에 대한 지정자 주민등록번호가 정확하지 않습니다. <br>다시 한번 확인하여 주십시요.";	
					}				
	       			// 카드번호 없음 
					else if(corpResult.equals("99")){
						subpage_key = "result_msg";
						join_step = 3;	
						result_txt = "입력하신 해당 카드는 기업지정카드로 등록되어 있지 않습니다. <br>다시 한번 확인하여 주십시요.";	
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
	
				// 05.회원가입 완료 (상세정보 입력사항 등록)
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
					dataSet.setString("account", parser.getParameter("account", ""));												// 아이디
					dataSet.setString("passwd", parser.getParameter("passwd", ""));													// 비밀번호
					dataSet.setString("pass_que_clss", parser.getParameter("pass_que_clss", ""));									// 비밀번호 질문
					dataSet.setString("pass_ans", parser.getParameter("pass_ans", ""));												// 비밀번호 답변
					dataSet.setString("firm_nm", parser.getParameter("firm_nm", ""));												// 회사명
					dataSet.setString("firm_rep_nm", parser.getParameter("firm_rep_nm", ""));										// 대표자명
					dataSet.setString("firm_zip", parser.getParameter("firm_zip1", "") + parser.getParameter("firm_zip2", ""));		// 회사 우편번호
					dataSet.setString("firm_addr1", parser.getParameter("firm_addr1", ""));											// 회사 주소1
					dataSet.setString("firm_addr2", parser.getParameter("firm_addr2", ""));											// 회사 주소2
					dataSet.setString("addr_clss", addr_clss);
					dataSet.setString("user_dept_nm", parser.getParameter("user_dept_nm", ""));										// 부서명
					dataSet.setString("user_level", parser.getParameter("user_level", ""));											// 직위
					dataSet.setString("user_tel_no", parser.getParameter("user_tel_no1", "") + "-" + parser.getParameter("user_tel_no2", "") + "-" + parser.getParameter("user_tel_no3", ""));	// 전화번호
					dataSet.setString("user_email", parser.getParameter("user_email1", "") + "@" + parser.getParameter("user_email2", ""));														// 이메일 
					dataSet.setString("user_fax_no", parser.getParameter("user_fax_no1", "") + "-" + parser.getParameter("user_fax_no2", "") + "-" + parser.getParameter("user_fax_no3", ""));	// 팩스번호
					dataSet.setString("user_mob_no", parser.getParameter("user_mob_no1", "") + "-" + parser.getParameter("user_mob_no2", "") + "-" + parser.getParameter("user_mob_no3", ""));	// 핸드폰번호 

	            	// 회원등록	            	
					String regResult = corpProc.registerCorpInfo(context, dataSet, request);					  	

					if(regResult.equals("00")){	// 정상등록 
		              	subpage_key = "result_msg";
		              			              	
/*		              	
  						// 회원가입 메일 발송
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
							
							emailTitle = "BCCARD 회원가입을 축하합니다.";
							emailFileNm = "/corp_join.html";
							emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, user_nm+"|"+account);
							
							emailEtt.setFrom(emailAdmin);
							emailEtt.setSubject(emailTitle);
							emailEtt.setTo(email_id);
							sender.send(emailEtt);
							 
						}	
*/							              	
		              	
					}
					else if(regResult.equals("01")){	// 아이디 중복 
						subpage_key = "result_msg";
						join_step = 4;	
						go_step = 1;
						result_txt = "이미 존재하는 아이디입니다. <br>다시 한번 확인하여 주십시요.";	
					}
					
	 				paramMap.put("join_step", Integer.toString(join_step));	
	 				paramMap.put("go_step", Integer.toString(go_step));	
					paramMap.put("result_txt", result_txt);	
	              	break;
					
	            default :
	                break; 
	            
            }
			 		  
	        request.setAttribute("paramMap", paramMap); //모든 파라미터값을 맵에 담아 반환한다.
	        
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
