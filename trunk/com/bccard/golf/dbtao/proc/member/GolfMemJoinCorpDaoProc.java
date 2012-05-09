/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfMemJoinCorpDaoProc
*   �ۼ���	: (��)�̵������
*   ����		: ����� > ȸ������ > ���ȸ������(����ī��) 
*   �������	: golf 
*   �ۼ�����	: 2009-12-10
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
* @author	(��)�̵������ 
* @version	1.0  
******************************************************************************/
public class GolfMemJoinCorpDaoProc extends AbstractProc {

	public static final String TITLE = "����� > ȸ������ > ���ȸ������(����ī��)";

	public GolfMemJoinCorpDaoProc() {}
	
	// ī���ȣ, ����ڹ�ȣ, �������¹�ȣ�� ����ī�� ���ȸ�� Ȯ��  
	public String execute(WaContext context, TaoDataSet data,HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";
				
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			String user_nm = data.getString("user_nm").trim();					// �̸�
			String user_jumin_no = data.getString("user_jumin_no").trim();		// �ֹι�ȣ
			String card_no = data.getString("card_no").trim();					// ī���ȣ
			String buz_no = data.getString("buz_no").trim();					// ����ڹ�ȣ
			String bk_acct_no = data.getString("bk_acct_no").trim();			// �������¹�ȣ

			// ī���ȣ, ����ڹ�ȣ, �������¹�ȣ�� ����ī��ȸ������ ��ȸ 
            sql = this.getChkCardInfo();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, card_no );
           	pstmt.setString(2, buz_no );                  	
           	pstmt.setString(3, bk_acct_no );                  	
           	rs = pstmt.executeQuery();
           	
           	// ī������ ���� ��� 
           	if(rs.next()){
           		result = "00";
           		String card_cry_hgnm = rs.getString("CARD_CRY_HGNM");
          		String jumin_no = rs.getString("JUMIN_NO");
          		
          		// �Է¹��� �̸��� �ٸ� ��� 
          		if(!user_nm.equals(card_cry_hgnm)){
           			result = "01";
           		}
          		// �Է¹��� �ֹι�ȣ�� �ٸ� ���
           		else if(!user_jumin_no.equals(jumin_no)){
           			result = "02";
           		}
			}
        	// ī������ ���� ��� 
           	else{
				result = "99";
			}
            
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();         
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	// �ֹι�ȣ�� ȸ�� �α�������, ���������  Ȯ��  
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

			String user_jumin_no = data.getString("user_jumin_no").trim();		// �ֹι�ȣ
			
            sql = this.getChkCorpMem();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, user_jumin_no );                	
           	rs = pstmt.executeQuery();
           	
           	// ȸ������ ���� ��� 
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
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	// ����ڹ�ȣ�� ������� ��������
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

			String buz_no = data.getString("buz_no").trim();		// ����ڹ�ȣ
			
            sql = this.getCorpInfo();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, buz_no );                	
           	rs = pstmt.executeQuery();
           	
           	// ������� ���� ��� 
           	if(rs.next()){

           		addr_clss = rs.getString("NW_OLD_ADDR_CLSS");
           		
    			if ( addr_clss == null || addr_clss.trim().equals("")){
    				addr_clss = "1";
				}             		
           			
        		result.addString("FIRM_NM", rs.getString("FIRM_NM"));			// ȸ���
        		result.addString("FIRM_REP_NM", rs.getString("FIRM_REP_NM"));	// ��ǥ�ڸ�
        		result.addString("FIRM_ZIP", rs.getString("FIRM_ZIP"));			// �����ȣ
        		
        		if (!addr_clss.equals("2")){
	           		result.addString("FIRM_ADDR1", rs.getString("FIRM_ADDR1"));	// ���ּ�1
	          		result.addString("FIRM_ADDR2", rs.getString("FIRM_ADDR2"));	// ���ּ�2
        		}else {        			
	           		result.addString("FIRM_ADDR1", rs.getString("DONG_OVR_NEW_ADDR"));		// ���ּ�1
	          		result.addString("FIRM_ADDR2", rs.getString("DONG_BLW_NEW_ADDR"));		// ���ּ�2
        		}
          		
          		result.addString("ADDR_CLSS", addr_clss);		// �ּұ���(��:1, ��:2)
          		
			}
            
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();         
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	// �ߺ� ���̵� üũ
	public String chkIdDuplicate(WaContext context, TaoDataSet data,HttpServletRequest request) throws BaseException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String result = "";	
		
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			String account = data.getString("account").trim();		// ȸ�����̵�
			
            sql = this.getChkIdDuplicate();
			pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, account );                	
           	rs = pstmt.executeQuery();
           	
           	// �ߺ� ���̵� ���� ���
           	if(rs != null && rs.next()){        		
           		result = "Y";
			}
            
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();         
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	// ȸ������ �������
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
		    String site_clss	 = "1";		// UCUSRINFO�� �� ����Ʈ����(1:ī��, 2:����)
		    String member_clss	 = "5";		// UCUSRINFO�� �� ȸ������(1:����, 2:������, 3:���(��), 4:�̼���ȸ��, 5:���)
		    String mem_stat		 = "2";		// TBENTPMEM�� �� ȸ������ (1:ȸ�����, 2:ȸ������, 3:ȸ��Żȸ)

			String buz_no = data.getString("buz_no").trim();				// ����ڹ�ȣ
			String card_no = data.getString("card_no").trim();				// ī���ȣ
			String bk_acct_no = data.getString("bk_acct_no").trim();		// ���¹�ȣ
			String entp_info_clss = "1";									// 1:���, 2:�׷��
			String firm_nm = data.getString("firm_nm").trim();				// ȸ���
			String firm_rep_nm = data.getString("firm_rep_nm").trim();		// ��ǥ�ڸ�
			String firm_zip = data.getString("firm_zip").trim();			// �����ȣ
			String firm_addr1 = data.getString("firm_addr1").trim();		// �ּ�1
			String firm_addr2 = data.getString("firm_addr2").trim();		// �ּ�2
			String logo_disp_yn   = "N";	    
		    
		    // ��й�ȣ ��ȣȭ 
	        byte[] h_passwd = null;
	        byte[] e_passwd = null;
	        h_passwd = CipherClient.hash(passwd.getBytes());
	        e_passwd = CipherClient.encrypt(CipherClient.MASTERKEY1, passwd.getBytes());	    
			
	        // ���ο� ���� ����
	        if(is_new_account.equals("Y") && !(account == null || account.equals(""))){ 
	        	
    			debug("## GolfMemJoinCorpDaoProc | ���̵� ���� ����� ���� | account : " + account + "\n");

	        	// UCUSRINFO ���̺� ���ο� MEM_ID�� ���ϱ�
	        	sql = this.getSelectUcusrinfoSeq();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, account);  
				
				rs = pstmt.executeQuery();
               	if(rs.next()){        		
               		uc_mem_id = rs.getString("UC_MEM_ID");			
    			}   
               
               	// �ߺ� ���̵� ����
               	if(uc_mem_id == null || uc_mem_id.equals("")){	 
           			debug("## GolfMemJoinCorpDaoProc | UCUSRINFO �ߺ� ���̵� ���� | account : " + account + "\n");             		
               		result = "01";
               	}
               	
               	// �α������� insert 
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
    					
              			debug("## GolfMemJoinCorpDaoProc | UCUSRINFO �� INSERT ���� | account : " + account + "\n");  
              			
    					//SSO DB ����ȭ
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
                  			debug("## GolfMemJoinCorpDaoProc | IDENTITY �� INSERT ���� | account : " + account + "\n");             		
        				}
        				else{
                  			debug("## GolfMemJoinCorpDaoProc | IDENTITY �� INSERT ���� | account : " + account + "\n");        
                       		result = "01";      					
        				}

    				}
    				else{
               			debug("## GolfMemJoinCorpDaoProc | UCUSRINFO �� INSERT ����. �ߺ� ���̵� ���� | account : " + account + "\n");             		
                   		result = "01";
    				}
               	}
               	
	        }
	        
			// ������� ���
	        if(result.equals("00")){
	        	
				debug("## GolfMemJoinCorpDaoProc | ������� ��� ���� (TBENTPINFO) | buz_no : " + buz_no + "\n");       
	 			
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
					debug("## GolfMemJoinCorpDaoProc | ������� INSERT ���� (TBENTPINFO) | buz_no : " + buz_no + "\n");          		
				}
				else{
					debug("## GolfMemJoinCorpDaoProc | ������� �̹� ���� (TBENTPINFO) | buz_no : " + buz_no + "\n");          					
				}		      		 
	        	
	        }
            	
	       
           	// ���ȸ������ ���
	        if(result.equals("00")){
	        	
	          	if(is_new_mem.equals("Y")){	// insert 
	           		
					debug("## GolfMemJoinCorpDaoProc | ���ȸ������ INSERT ���� (TBENTPUSER) \n");          					
	           		
	    			// TBENTPUSER : ���ο� MEM_ID ���ϱ�
	                sql = this.getSelectCorpUserSeq();
	    			pstmt = conn.prepareStatement(sql);
	    			rs = pstmt.executeQuery();
	               	if(rs.next()){        		
	               		mem_id = rs.getString("MEM_ID");			
	    			}              
	     			
	    			// TBENTPUSER insert ���� ���ϱ�
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
	    				debug("## GolfMemJoinCorpDaoProc | ���ȸ������ INSERT ���� (TBENTPUSER) \n");          		
	    			}
	    			else{
	    				debug("## GolfMemJoinCorpDaoProc | ���ȸ������ INSERT ���� (TBENTPUSER) | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + "\n");          					
	    				result = "02";
	    			}		      		    		    
	   			
	           	}
	           	else{	// update
	           		
					debug("## GolfMemJoinCorpDaoProc | ���ȸ������ UPDATE ���� (TBENTPUSER) | mem_id : " + mem_id + " \n");          									           		
	           		
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
	    				debug("## GolfMemJoinCorpDaoProc | ���ȸ������ UPDATE ���� (TBENTPUSER) \n");          		
	    			}
	    			else{
	    				debug("## GolfMemJoinCorpDaoProc | ���ȸ������ UPDATE ���� (TBENTPUSER) | mem_id : " + mem_id + " | user_nm : " + user_nm + " | user_jumin_no : " + user_jumin_no + "\n");          					
	       				result = "02";
	    			}		      		    		                	
		   		    
	           	}		    
	        	
	        }
	        	
           	// ��翪������ INSERT 
	        if(result.equals("00")){
	        	
	           	debug("## GolfMemJoinCorpDaoProc | ��翪������ INSERT (TBENTPMEM) ���� | mem_id : " + mem_id + " \n");          		
					            	
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

	          	debug("## GolfMemJoinCorpDaoProc | ��翪������ INSERT (TBENTPMEM) ��� | upd_result : " + upd_result + " \n");          		
	        	
	        }		    

       		// close
   			if(rs != null) rs.close();          	
            if(pstmt != null) pstmt.close();   
                      
            if(result.equals("00")){  // DBó�� ����
            	conn.commit();
            }          
            else{ // DBó�� ����
            	conn.rollback();
            }
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
   /** ***********************************************************************
    * ����ī�� ȸ������ Ȯ��   
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
     * �ֹι�ȣ�� �α�������, ��������� ��������   
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
	* ����ڹ�ȣ�� ������� ��������
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
	* �ߺ� ���̵� üũ
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
	* ������� INSERT - TBENTPINFO 
	************************************************************************ */
	private String getInsertCorpInfo(String addr_clss){
		
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" INSERT INTO BCDBA.TBENTPINFO ( ");
		sql.append("\n").append(" BUZ_NO, ENTP_INFO_CLSS, FIRM_NM, FIRM_REP_NM, FIRM_ZIP, ");
		
		if ( addr_clss.equals("1") ){ //���ּ�
			sql.append("\n").append(" FIRM_ADDR1, FIRM_ADDR2,  ");
		}else { //���ּ�
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
	* UCUSRINFO ���̺� ���ο� MEM_ID ���ϱ� - UCUSRINFO
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
	* UCUSRINFO ���̺� �α������� insert - UCUSRINFO
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
	* IDENTITY ���̺� insert - IDENTITY
	************************************************************************ */
	private String getInsertIdentity(){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" INSERT INTO BCDBA.IDENTITY                                                                            ");
		sql.append("\n").append("        (USERID, ENABLE, NAME, ENCPASSWD, EMAIL, REGISTCODE, MEM_CLSS, LASTLOGINTIME, LASTLOGINIP)     ");
		sql.append("\n").append(" VALUES (?, ?, ?, ?, ?, ?, ?, SYSDATE, ?)                                                              ");
		return sql.toString();
	} 
          
	/** ***********************************************************************
	* ���ȸ������ ���ο� MEM_ID ���ϱ� - TBENTPUSER
	************************************************************************ */
	private String getSelectCorpUserSeq(){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" SELECT ENTPUSER_SEQ.NEXTVAL MEM_ID FROM DUAL                  ");   
		return sql.toString();
	} 
        
	/** ***********************************************************************
	* ���ȸ������ INSERT - TBENTPUSER
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
	* ���ȸ������ UPDATE - TBENTPUSER
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
	* ���Ҵ������ ���ο� SEQ_NO ���ϱ� - TBENTPMEM
	************************************************************************ */
	private String getSelectTbentpmemSeq(){
		StringBuffer sql = new StringBuffer();           
	    sql.append("\n").append(" SELECT NVL(MAX(SEQ_NO), 0)+1 SEQ_NO FROM BCDBA.TBENTPMEM WHERE MEM_ID = ? ");
		return sql.toString();
	} 
	
	/** ***********************************************************************
	* ���Ҵ������ INSERT - TBENTPMEM 
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
