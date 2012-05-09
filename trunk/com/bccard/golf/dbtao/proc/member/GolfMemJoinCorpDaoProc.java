/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfMemJoinCorpDaoProc
*   작성자	: (주)미디어포스
*   내용		: 사용자 > 회원가입 > 기업회원가입(지정카드) 
*   적용범위	: golf 
*   작성일자	: 2009-12-10
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;
import java.net.InetAddress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.initech.dbprotector.CipherClient;

import javax.servlet.http.HttpServletRequest;


/******************************************************************************
* Golf
* @author	(주)미디어포스 
* @version	1.0  
******************************************************************************/
public class GolfMemJoinCorpDaoProc extends AbstractProc {

	public static final String TITLE = "사용자 > 회원가입 > 기업회원가입(지정카드)";

	public GolfMemJoinCorpDaoProc() {}
	
	// 카드번호, 사업자번호, 결제계좌번호로 지정카드 기업회원 확인  
	public String execute(WaContext context, TaoDataSet data,HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			String user_nm = data.getString("user_nm").trim();					// 이름
			String user_jumin_no = data.getString("user_jumin_no").trim();		// 주민번호
			String card_no = data.getString("card_no").trim();					// 카드번호
			String buz_no = data.getString("buz_no").trim();					// 사업자번호
			String bk_acct_no = data.getString("bk_acct_no").trim();			// 결제계좌번호

			// 카드번호, 사업자번호, 결제계좌번호로 지정카드회원정보 조회 
            sql = this.getChkCardInfo();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, card_no );
           	pstmt.setString(2, buz_no );                  	
           	pstmt.setString(3, bk_acct_no );                  	
           	rs = pstmt.executeQuery();
           	
           	// 카드정보 있을 경우 
           	if(rs.next()){
           		result = "00";
           		String card_cry_hgnm = rs.getString("CARD_CRY_HGNM");
          		String jumin_no = rs.getString("JUMIN_NO");
          		
          		// 입력받은 이름과 다른 경우 
          		if(!user_nm.equals(card_cry_hgnm)){
           			result = "01";
           		}
          		// 입력받은 주민번호와 다른 경우
           		else if(!user_jumin_no.equals(jumin_no)){
           			result = "02";
           		}
			}
        	// 카드정보 없을 경우 
           	else{
				result = "99";
			}
            
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();         
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	// 주민번호로 회원 로그인정보, 담당자정보  확인  
	public DbTaoResult getLoginInfo(WaContext context, TaoDataSet data,HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		DbTaoResult result = new DbTaoResult(title);				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			String user_jumin_no = data.getString("user_jumin_no").trim();		// 주민번호
			
            sql = this.getChkCorpMem();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, user_jumin_no );                	
           	rs = pstmt.executeQuery();
           	
           	// 회원정보 있을 경우 
           	if(rs.next()){        		
        		result.addString("ACCOUNT", rs.getString("ACCOUNT"));
        		result.addString("MEM_ID", rs.getString("MEM_ID"));
        		result.addString("USER_DEPT_NM", rs.getString("USER_DEPT_NM"));
        		result.addString("USER_LEVEL", rs.getString("USER_LEVEL"));
        		result.addString("USER_TEL_NO", rs.getString("USER_TEL_NO"));
        		result.addString("USER_FAX_NO", rs.getString("USER_FAX_NO"));
        		result.addString("USER_MOB_NO", rs.getString("USER_MOB_NO"));
        		result.addString("USER_EMAIL", rs.getString("USER_EMAIL"));
			}
            
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();         
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	// 사업자번호로 기업정보 가져오기
	public DbTaoResult getCorpInfo(WaContext context, TaoDataSet data,HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String addr_clss = "";
		
		DbTaoResult result = new DbTaoResult(title);				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			String buz_no = data.getString("buz_no").trim();		// 사업자번호
			
            sql = this.getCorpInfo();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, buz_no );                	
           	rs = pstmt.executeQuery();
           	
           	// 기업정보 있을 경우 
           	if(rs.next()){

           		addr_clss = rs.getString("NW_OLD_ADDR_CLSS");
           		
    			if ( addr_clss == null || addr_clss.trim().equals("")){
    				addr_clss = "1";
				}             		
           			
        		result.addString("FIRM_NM", rs.getString("FIRM_NM"));			// 회사명
        		result.addString("FIRM_REP_NM", rs.getString("FIRM_REP_NM"));	// 대표자명
        		result.addString("FIRM_ZIP", rs.getString("FIRM_ZIP"));			// 우편번호
        		
        		if (!addr_clss.equals("2")){
	           		result.addString("FIRM_ADDR1", rs.getString("FIRM_ADDR1"));	// 구주소1
	          		result.addString("FIRM_ADDR2", rs.getString("FIRM_ADDR2"));	// 구주소2
        		}else {        			
	           		result.addString("FIRM_ADDR1", rs.getString("DONG_OVR_NEW_ADDR"));		// 새주소1
	          		result.addString("FIRM_ADDR2", rs.getString("DONG_BLW_NEW_ADDR"));		// 새주소2
        		}
          		
          		result.addString("ADDR_CLSS", addr_clss);		// 주소구분(구:1, 신:2)
          		
			}
            
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();         
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	// 중복 아이디 체크
	public String chkIdDuplicate(WaContext context, TaoDataSet data,HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";	
		
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			String account = data.getString("account").trim();		// 회원아이디
			
            sql = this.getChkIdDuplicate();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, account );                	
           	rs = pstmt.executeQuery();
           	
           	// 중복 아이디 있을 경우
           	if(rs != null && rs.next()){        		
           		result = "Y";
			}
            
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();         
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	// 회원정보 최종등록
	public String registerCorpInfo(WaContext context, TaoDataSet data,HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String result = "00";
		int upd_result = 0;
		int i = 0;
		 		
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

		    String is_new_account = data.getString("is_new_account");
		    String is_new_mem = data.getString("is_new_mem");
		    
		    String mem_id        = data.getString("mem_id");	// TBENTPUSER - MEM_ID
		    String user_nm       = data.getString("user_nm");
		    String user_jumin_no = data.getString("user_jumin_no");
		    String user_dept_nm  = data.getString("user_dept_nm");
		    String user_level    = data.getString("user_level");
		    String user_tel_no   = data.getString("user_tel_no");
		    String user_fax_no   = data.getString("user_fax_no");
		    String user_mob_no   = data.getString("user_mob_no");
		    String user_email    = data.getString("user_email");
		    String uc_mem_id     = data.getString("uc_mem_id");
		    String account       = data.getString("account");
		    String passwd        = data.getString("passwd");		         
		    String pass_que_clss = data.getString("pass_que_clss");
		    String pass_ans      = data.getString("pass_ans");
		    String addr_clss     = data.getString("addr_clss");
		    String mail_rcv_yn   = "";		    
		    String rep_id_clss	 = "0";
		    String last_data_yn  = "Y";
		    String bcams_stat	 = "";
		    String site_clss	 = "1";		// UCUSRINFO에 들어갈 사이트구분(1:카드, 2:라인)
		    String member_clss	 = "5";		// UCUSRINFO에 들어갈 회원구분(1:개인, 2:가맹점, 3:기업(구), 4:미소지회원, 5:기업)
		    String mem_stat		 = "2";		// TBENTPMEM에 들어갈 회원상태 (1:회원등록, 2:회원가입, 3:회원탈회)

			String buz_no = data.getString("buz_no").trim();				// 사업자번호
			String card_no = data.getString("card_no").trim();				// 카드번호
			String bk_acct_no = data.getString("bk_acct_no").trim();		// 계좌번호
			String entp_info_clss = "1";									// 1:기업, 2:그룹사
			String firm_nm = data.getString("firm_nm").trim();				// 회사명
			String firm_rep_nm = data.getString("firm_rep_nm").trim();		// 대표자명
			String firm_zip = data.getString("firm_zip").trim();			// 우편번호
			String firm_addr1 = data.getString("firm_addr1").trim();		// 주소1
			String firm_addr2 = data.getString("firm_addr2").trim();		// 주소2
			String logo_disp_yn   = "N";	    
		    
		    // 비밀번호 암호화 
	        byte[] h_passwd = null;
	        byte[] e_passwd = null;
	        h_passwd = CipherClient.hash(passwd.getBytes());
	        e_passwd = CipherClient.encrypt(CipherClient.MASTERKEY1, passwd.getBytes());	    
			
	        // 새로운 계정 생성
	        if(is_new_account.equals("Y") && !(account == null || account.equals(""))){ 
	        	
    			debug("## GolfMemJoinCorpDaoProc | 아이디 새로 만들기 시작 | account : " + account + "\n");

	        	// UCUSRINFO 테이블 새로운 MEM_ID값 구하기
	        	sql = this.getSelectUcusrinfoSeq();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, account);  
				
				rs = pstmt.executeQuery();
               	if(rs.next()){        		
               		uc_mem_id = rs.getString("UC_MEM_ID");			
    			}   
               
               	// 중복 아이디 있음
               	if(uc_mem_id == null || uc_mem_id.equals("")){	 
           			debug("## GolfMemJoinCorpDaoProc | UCUSRINFO 중복 아이디 있음 | account : " + account + "\n");             		
               		result = "01";
               	}
               	
               	// 로그인정보 insert 
               	else{

    	        	sql = this.getInsertUcusrinfo();
    				pstmt = conn.prepareStatement(sql);
    				
    				i = 0;
    				pstmt.setString(++i, uc_mem_id);  
    				pstmt.setString(++i, account);  
    				pstmt.setString(++i, site_clss);  	
    				pstmt.setString(++i, member_clss);  	
    				pstmt.setBytes(++i, h_passwd);  
    				pstmt.setBytes(++i, e_passwd);  
    				pstmt.setString(++i, " ");  
    				pstmt.setString(++i, account);  
    				
    				upd_result = pstmt.executeUpdate();    
    				
    				if(upd_result == 1){
    					
              			debug("## GolfMemJoinCorpDaoProc | UCUSRINFO 에 INSERT 성공 | account : " + account + "\n");  
              			
    					//SSO DB 동기화
        	        	sql = this.getInsertIdentity();
        				pstmt = conn.prepareStatement(sql);
   					
        				i = 0;
        				pstmt.setString(++i, account);  
        				pstmt.setString(++i, "T");  
        				pstmt.setString(++i, user_nm);  	
        				pstmt.setBytes(++i, h_passwd);  	
        				pstmt.setString(++i, user_email);  
        				pstmt.setString(++i, user_jumin_no);  
        				pstmt.setString(++i, "5");  
        				pstmt.setString(++i, request.getRemoteAddr());  
        				
        				upd_result = pstmt.executeUpdate();    
        				
        				if(upd_result == 1){
                  			debug("## GolfMemJoinCorpDaoProc | IDENTITY 에 INSERT 성공 | account : " + account + "\n");             		
        				}
        				else{
                  			debug("## GolfMemJoinCorpDaoProc | IDENTITY 에 INSERT 실패 | account : " + account + "\n");        
                       		result = "01";      					
        				}

    				}
    				else{
               			debug("## GolfMemJoinCorpDaoProc | UCUSRINFO 에 INSERT 실패. 중복 아이디 있음 | account : " + account + "\n");             		
                   		result = "01";
    				}
               	}
               	
	        }
	        
			// 기업정보 등록
	        if(result.equals("00")){
	        	
				debug("## GolfMemJoinCorpDaoProc | 기업정보 등록 시작 (TBENTPINFO) | buz_no : " + buz_no + "\n");       
	 			
	            sql = this.getInsertCorpInfo(addr_clss);
				pstmt = conn.prepareStatement(sql);
				
				i = 0;
				pstmt.setString(++i, buz_no );     
	        	pstmt.setString(++i, entp_info_clss );     
	        	pstmt.setString(++i, firm_nm );     
	        	pstmt.setString(++i, firm_rep_nm );     
	        	pstmt.setString(++i, firm_zip );     
	        	pstmt.setString(++i, firm_addr1 );     
	           	pstmt.setString(++i, firm_addr2 );     
	           	pstmt.setString(++i, logo_disp_yn );
	           	pstmt.setString(++i, addr_clss );
	           	pstmt.setString(++i, buz_no );     
	           	pstmt.setString(++i, entp_info_clss );	           	
	         	
	           	upd_result = pstmt.executeUpdate();
	           	
				if(upd_result == 1){
					debug("## GolfMemJoinCorpDaoProc | 기업정보 INSERT 성공 (TBENTPINFO) | buz_no : " + buz_no + "\n");          		
				}
				else{
					debug("## GolfMemJoinCorpDaoProc | 기업정보 이미 있음 (TBENTPINFO) | buz_no : " + buz_no + "\n");          					
				}		      		 
	        	
	        }
            	
	       
           	// 기업회원정보 등록
	        if(result.equals("00")){
	        	
	          	if(is_new_mem.equals("Y")){	// insert 
	           		
					debug("## GolfMemJoinCorpDaoProc | 기업회원정보 INSERT 시작 (TBENTPUSER) \n");          					
	           		
	    			// TBENTPUSER : 새로운 MEM_ID 구하기
	                sql = this.getSelectCorpUserSeq();
	    			pstmt = conn.prepareStatement(sql);
	    			rs = pstmt.executeQuery();
	               	if(rs.next()){        		
	               		mem_id = rs.getString("MEM_ID");			
	    			}              
	     			
	    			// TBENTPUSER insert 쿼리 구하기
	                sql = this.getInsertCorpUser();
	      			pstmt = conn.prepareStatement(sql);
	              
	       			i = 0;
	       		    pstmt.setString(++i, mem_id);
	    		    pstmt.setString(++i, user_nm);
	    		    pstmt.setString(++i, user_jumin_no);
	    		    pstmt.setString(++i, user_dept_nm);
	    		    pstmt.setString(++i, user_level);
	    		    pstmt.setString(++i, user_tel_no);
	    		    pstmt.setString(++i, user_fax_no);
	    		    pstmt.setString(++i, user_mob_no);
	    		    pstmt.setString(++i, user_email);
	    		    pstmt.setString(++i, buz_no);
	    		    pstmt.setString(++i, uc_mem_id);
	    		    pstmt.setString(++i, account);
	    		    pstmt.setBytes(++i, h_passwd);
	    		    pstmt.setBytes(++i, e_passwd);
	    		    pstmt.setString(++i, passwd);
	    		    pstmt.setString(++i, pass_que_clss);
	    		    pstmt.setString(++i, pass_ans);
	    		    pstmt.setString(++i, mail_rcv_yn);
	    		    pstmt.setString(++i, rep_id_clss);
	    		    pstmt.setString(++i, last_data_yn);
	    		    pstmt.setString(++i, bcams_stat);
	    		    
	              	upd_result = pstmt.executeUpdate();
	               	
	    			if(upd_result == 1){
	    				debug("## GolfMemJoinCorpDaoProc | 기업회원정보 INSERT 성공 (TBENTPUSER) \n");          		
	    			}
	    			else{
	    				debug("## GolfMemJoinCorpDaoProc | 기업회원정보 INSERT 실패 (TBENTPUSER) | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + "\n");          					
	    				result = "02";
	    			}		      		    		    
	   			
	           	}
	           	else{	// update
	           		
					debug("## GolfMemJoinCorpDaoProc | 기업회원정보 UPDATE 시작 (TBENTPUSER) | mem_id : " + mem_id + " \n");          									           		
	           		
	           		sql = this.getUpdateCorpUser(is_new_account);
	           		pstmt = conn.prepareStatement(sql);
	            
		   			i = 0;
		   		    if(is_new_account.equals("Y")){
		   		      pstmt.setString(++i, account);
		   		      pstmt.setBytes(++i, h_passwd);
		   		      pstmt.setBytes(++i, e_passwd);
		   		      pstmt.setString(++i, uc_mem_id);
		   		    }
			   		    
		   		    pstmt.setString(++i, user_dept_nm);
		   		    pstmt.setString(++i, user_level);
		   		    pstmt.setString(++i, user_tel_no);
		   		    pstmt.setString(++i, user_fax_no);
		   		    pstmt.setString(++i, user_mob_no);
		   		    pstmt.setString(++i, user_email);
		   		    
		   		    if(is_new_account.equals("Y")){
		   		      pstmt.setString(++i, buz_no);
		   		      pstmt.setString(++i, pass_que_clss);
		   		      pstmt.setString(++i, pass_ans);
		   		      pstmt.setString(++i, mail_rcv_yn);
		   		    }
		   		    
		   		    pstmt.setString(++i, mem_id);
		   		    pstmt.setString(++i, last_data_yn);    
		   		    
	             	upd_result = pstmt.executeUpdate();
	             	
	    			if(upd_result == 1){
	    				debug("## GolfMemJoinCorpDaoProc | 기업회원정보 UPDATE 성공 (TBENTPUSER) \n");          		
	    			}
	    			else{
	    				debug("## GolfMemJoinCorpDaoProc | 기업회원정보 UPDATE 실패 (TBENTPUSER) | mem_id : " + mem_id + " | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + "\n");          					
	       				result = "02";
	    			}		      		    		                	
		   		    
	           	}		    
	        	
	        }
	        	
           	// 담당역할정보 INSERT 
	        if(result.equals("00")){
	        	
	           	debug("## GolfMemJoinCorpDaoProc | 담당역할정보 INSERT (TBENTPMEM) 시작 | mem_id : " + mem_id + " \n");          		
					            	
	           	String seq_no = "";
	      		sql = this.getSelectTbentpmemSeq();
	       		pstmt = conn.prepareStatement(sql);
	       		pstmt.setString(1, mem_id);
	       		
				rs = pstmt.executeQuery();
	           	if(rs.next()){        		
	           		seq_no = rs.getString("SEQ_NO");			
				}
	           	
	      		sql = this.getInsertTbentpmem();
	       		pstmt = conn.prepareStatement(sql);
	       		
	   			i = 0;
	   			pstmt.setString(++i, mem_id);
	   			pstmt.setString(++i, seq_no);
	   			pstmt.setString(++i, "6");
	   			pstmt.setString(++i, buz_no);
	   			pstmt.setString(++i, card_no);
	   			pstmt.setString(++i, bk_acct_no);
	   			pstmt.setString(++i, mem_stat);		         	
	  			pstmt.setString(++i, mem_id);
	  			pstmt.setString(++i, mem_id);
	   			pstmt.setString(++i, "6");
	   			pstmt.setString(++i, buz_no);
	   			pstmt.setString(++i, card_no);
	   			pstmt.setString(++i, bk_acct_no);
	   			pstmt.setString(++i, mem_stat);		         	
				  			   			           	
	   			upd_result = pstmt.executeUpdate();

	          	debug("## GolfMemJoinCorpDaoProc | 담당역할정보 INSERT (TBENTPMEM) 결과 | upd_result : " + upd_result + " \n");          		
	        	
	        }		    

       		// close
   			if(rs != null) rs.close();          	
            if(pstmt != null) pstmt.close();   
                      
            if(result.equals("00")){  // DB처리 성공
            	conn.commit();
            }          
            else{ // DB처리 실패
            	conn.rollback();
            }
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
   /** ***********************************************************************
    * 지정카드 회원정보 확인   
	************************************************************************ */
    private String getChkCardInfo(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	SELECT CARD_CRY_HGNM, JUMIN_NO 							\n");
		sql.append("\t	FROM BCDBA.TBENTPCDHD									\n");
		sql.append("\t	WHERE CARD_NO = ? 										\n");
		sql.append("\t		AND BUZ_NO = ?										\n");
		sql.append("\t		AND BK_ACCT_NO = ? 									\n");
		sql.append("\t		AND CARD_CRY_CLSS IN ('3','5') 						\n");
		sql.append("\t		AND FNL_CARD_CLSS = '1'								\n");
		return sql.toString();
    }

    /** ***********************************************************************
     * 주민번호로 로그인정보, 담당자정보 가져오기   
     ************************************************************************ */
     private String getChkCorpMem(){
         StringBuffer sql = new StringBuffer();
         sql.append("\n");
         sql.append("\t	SELECT MEM_ID, NVL(ACCOUNT, ' ') ACCOUNT, USER_DEPT_NM, USER_LEVEL, USER_TEL_NO, USER_FAX_NO, USER_MOB_NO, USER_EMAIL 	\n");
         sql.append("\t	FROM BCDBA.TBENTPUSER 						\n");
         sql.append("\t	WHERE USER_JUMIN_NO = ?						\n");
         sql.append("\t		AND LAST_DATA_YN = 'Y' 					\n");
         sql.append("\t		AND REP_ID_CLSS <> '1' 					\n");
         sql.append("\t		AND REP_ID_CLSS <> '2' 					\n");
         return sql.toString();
     }

	/** ***********************************************************************
	* 사업자번호로 기업정보 가져오기
	************************************************************************ */
	private String getCorpInfo(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT FIRM_NM, FIRM_REP_NM, FIRM_ZIP, FIRM_ADDR1, FIRM_ADDR2, DONG_OVR_NEW_ADDR, DONG_BLW_NEW_ADDR, NW_OLD_ADDR_CLSS		\n");
		sql.append("\t	FROM BCDBA.TBENTPINFO														\n");
		sql.append("\t	WHERE ENTP_INFO_CLSS = '1'												\n");
		sql.append("\t		AND BUZ_NO = ?														\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 중복 아이디 체크
	************************************************************************ */
	private String getChkIdDuplicate(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	SELECT A.ACCOUNT						\n");
		sql.append("\t	FROM BCDBA.UCUSRINFO A					\n");
		sql.append("\t	WHERE A.ACCOUNT = ?						\n");
		return sql.toString();
	} 
	
	/*************************************************************************
	* 기업정보 INSERT - TBENTPINFO 
	************************************************************************ */
	private String getInsertCorpInfo(String addr_clss){
		
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" INSERT INTO BCDBA.TBENTPINFO ( ");
		sql.append("\n").append(" BUZ_NO, ENTP_INFO_CLSS, FIRM_NM, FIRM_REP_NM, FIRM_ZIP, ");
		
		if ( addr_clss.equals("1") ){ //구주소
			sql.append("\n").append(" FIRM_ADDR1, FIRM_ADDR2,  ");
		}else { //새주소
			sql.append("\n").append(" DONG_OVR_NEW_ADDR, DONG_BLW_NEW_ADDR,  ");
		}
		sql.append("\n").append(" LOGO_DISP_YN,  NW_OLD_ADDR_CLSS ");
		sql.append("\n").append(" ) ");
		sql.append("\n").append(" ( ");
		sql.append("\n").append(" SELECT ?, ?, ?, ?, ?, ");
		sql.append("\n").append(" ?, ?, ");		
		sql.append("\n").append(" ?, ? ");
		
		sql.append("\n").append(" FROM DUAL ");
		sql.append("\n").append(" WHERE NOT EXISTS ( ");
		sql.append("\n").append(" SELECT BUZ_NO FROM BCDBA.TBENTPINFO WHERE BUZ_NO = ? AND ENTP_INFO_CLSS = ? ");
		sql.append("\n").append(" ) ");
		sql.append("\n").append(" ) ");          
		return sql.toString();
	} 
     
	/** ***********************************************************************
	* UCUSRINFO 테이블에 새로운 MEM_ID 구하기 - UCUSRINFO
	************************************************************************ */
	private String getSelectUcusrinfoSeq(){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" SELECT UCUSRINFO_SEQ.NEXTVAL UC_MEM_ID FROM DUAL                  ");
		sql.append("\n").append(" WHERE NOT EXISTS (                                                ");
		sql.append("\n").append("                   SELECT ACCOUNT FROM BCDBA.UCUSRINFO WHERE ACCOUNT = ? ");
		sql.append("\n").append("                  )                                                ");
		return sql.toString();
	} 

	/** ***********************************************************************
	* UCUSRINFO 테이블에 로그인정보 insert - UCUSRINFO
	************************************************************************ */
	private String getInsertUcusrinfo(){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" INSERT INTO BCDBA.UCUSRINFO (                                                                      ");
		sql.append("\n").append("                        MEMID, ACCOUNT, SITE_CLSS, MEMBER_CLSS, H_PASSWD, E_PASSWD, PASSWD, REGDATE ");
		sql.append("\n").append("                       )                                                                            ");
		sql.append("\n").append(" (                                                                                                  ");
		sql.append("\n").append("  SELECT ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')                                  ");
		sql.append("\n").append("  FROM DUAL                                                                                         ");
		sql.append("\n").append("  WHERE NOT EXISTS (                                                                                ");
		sql.append("\n").append("                    SELECT ACCOUNT FROM BCDBA.UCUSRINFO WHERE ACCOUNT = ?                           ");
		sql.append("\n").append("                   )                                                                                ");
		sql.append("\n").append(" )                                                                                                  ");
		return sql.toString();
	} 
  
	/** ***********************************************************************
	* IDENTITY 테이블에 insert - IDENTITY
	************************************************************************ */
	private String getInsertIdentity(){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" INSERT INTO BCDBA.IDENTITY                                                                            ");
		sql.append("\n").append("        (USERID, ENABLE, NAME, ENCPASSWD, EMAIL, REGISTCODE, MEM_CLSS, LASTLOGINTIME, LASTLOGINIP)     ");
		sql.append("\n").append(" VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE, ?)                                                              ");
		return sql.toString();
	} 
          
	/** ***********************************************************************
	* 기업회원정보 새로운 MEM_ID 구하기 - TBENTPUSER
	************************************************************************ */
	private String getSelectCorpUserSeq(){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" SELECT ENTPUSER_SEQ.NEXTVAL MEM_ID FROM DUAL                  ");   
		return sql.toString();
	} 
        
	/** ***********************************************************************
	* 기업회원정보 INSERT - TBENTPUSER
	************************************************************************ */
	private String getInsertCorpUser(){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" INSERT INTO BCDBA.TBENTPUSER (                                    ");
		sql.append("\n").append(" MEM_ID, USER_NM, USER_JUMIN_NO, USER_DEPT_NM, USER_LEVEL,         ");
		sql.append("\n").append(" USER_TEL_NO, USER_FAX_NO, USER_MOB_NO, USER_EMAIL, BUZ_NO,        ");
		sql.append("\n").append(" UC_MEM_ID, ACCOUNT, H_PASSWD, E_PASSWD, PASSWD,                   ");
		sql.append("\n").append(" PASS_QUE_CLSS, PASS_ANS, MAIL_RCV_YN, REP_ID_CLSS, LAST_DATA_YN   ");
		sql.append("\n").append(" ,BCAMS_STAT                                                       ");  
		sql.append("\n").append(" )                                                                 ");
		sql.append("\n").append(" (                                                                 ");
		sql.append("\n").append(" SELECT ?, ?, ?, ?, ?,                                             ");
		sql.append("\n").append(" ?, ?, ?, ?, ?,                                                    ");
		sql.append("\n").append(" ?, ?, ?, ?, ?,                                                    ");
		sql.append("\n").append(" ?, ?, ?, ?, ?                                                     ");
		sql.append("\n").append(" ,?                                                                ");   
		sql.append("\n").append(" FROM DUAL                                                         ");
		sql.append("\n").append(" )                                                                 ");        
		return sql.toString();
	} 
 
	/** ***********************************************************************
	* 기업회원정보 UPDATE - TBENTPUSER
	************************************************************************ */
	private String getUpdateCorpUser(String new_account){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" UPDATE BCDBA.TBENTPUSER SET ");
             
		if(new_account.equals("Y")) {
			sql.append("\n").append(" ACCOUNT = ?,      ");
			sql.append("\n").append(" H_PASSWD = ?,     ");
			sql.append("\n").append(" E_PASSWD = ?,     ");
			sql.append("\n").append(" UC_MEM_ID = ?,    ");
		}
             
		sql.append("\n").append(" USER_DEPT_NM = ?,     ");
		sql.append("\n").append(" USER_LEVEL = ?,       ");
		sql.append("\n").append(" USER_TEL_NO = ?,      ");
		sql.append("\n").append(" USER_FAX_NO = ?,      ");
		sql.append("\n").append(" USER_MOB_NO = ?,      ");
		sql.append("\n").append(" USER_EMAIL = ?        ");
             
		if(new_account.equals("Y")){
			sql.append("\n").append(" , BUZ_NO = ?,     ");
			sql.append("\n").append(" PASS_QUE_CLSS = ?,");
			sql.append("\n").append(" PASS_ANS = ?,     ");
			sql.append("\n").append(" MAIL_RCV_YN = ?   ");
		}   
             
		sql.append("\n").append(" WHERE MEM_ID = ?      ");
		sql.append("\n").append(" AND LAST_DATA_YN = ?  ");       
		return sql.toString();
	} 
 
	/** ***********************************************************************
	* 역할담당정보 새로운 SEQ_NO 구하기 - TBENTPMEM
	************************************************************************ */
	private String getSelectTbentpmemSeq(){
		StringBuffer sql = new StringBuffer();           
	    sql.append("\n").append(" SELECT NVL(MAX(SEQ_NO), 0)+1 SEQ_NO FROM BCDBA.TBENTPMEM WHERE MEM_ID = ? ");
		return sql.toString();
	} 
	
	/** ***********************************************************************
	* 역할담당정보 INSERT - TBENTPMEM 
	************************************************************************ */
	private String getInsertTbentpmem(){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" INSERT INTO BCDBA.TBENTPMEM ( ");
		sql.append("\n").append(" MEM_ID, SEQ_NO, MEM_CLSS, BUZ_NO, CARD_NO, BK_ACCT_NO, ");
		sql.append("\n").append(" AFI_DATE, MEM_STAT, INP_MEM_ID, INP_DATE ");
		sql.append("\n").append(" ) ");
		sql.append("\n").append(" ( ");
		sql.append("\n").append(" SELECT ?, ?, ?, ?, ?, ?, ");
		sql.append("\n").append(" TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS') ");
		sql.append("\n").append(" FROM DUAL ");
		sql.append("\n").append(" WHERE NOT EXISTS ( ");
		sql.append("\n").append(" SELECT SEQ_NO ");
		sql.append("\n").append(" FROM BCDBA.TBENTPMEM ");
		sql.append("\n").append(" WHERE MEM_ID = ? ");
		sql.append("\n").append(" AND MEM_CLSS = ? ");
		sql.append("\n").append(" AND BUZ_NO = ? ");
		sql.append("\n").append(" AND CARD_NO = ? ");
		sql.append("\n").append(" AND BK_ACCT_NO = ? ");
		sql.append("\n").append(" AND MEM_STAT = ?");
		sql.append("\n").append(" ) ");
		sql.append("\n").append(" ) ");           
		return sql.toString();
	}          
		
}
