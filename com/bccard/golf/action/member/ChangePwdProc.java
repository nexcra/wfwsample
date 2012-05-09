/********************************************************************************************
*	Ŭ������  : ChangePwdProc
*	�ۼ���     : ������
*	����        : ���ͳ�ȸ���н����庯�� ó��
*	�������  : bccard��ü
*	�ۼ�����  : 2004.02.3
************************** �����̷� **********************************************************
*	 ����          ����   �ۼ���             �������
* 20041110            LimKeonKuk      mail ��й�ȣ mysql ����
********************************************************************************************/

package com.bccard.golf.action.member;

import java.sql.*;
import java.util.*;
import java.util.Properties;


import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.initech.util.Base64Util;
import com.initech.dbprotector.CipherClient; //DB ��ȣȭ ����

import com.bccard.golf.common.BcLog;
import com.bccard.golf.common.SSOdbprotectorWrap;

public class ChangePwdProc extends AbstractProc{
/*
	protected JoltOutput execute(   WaContext context
								, HttpServletRequest request
								, JoltInput input ) {
		JoltOutput output = null;
		try {
			output = super.call(context,request,input);
		} catch (BaseException e) {
		}
		finally {
		  return output;
		}
	}
*/
	//DB�� �ִ� �ֹι�ȣ...
	/**
	 * DB�� �ִ� �ֹι�ȣ...
	*/
	protected Hashtable getSelectCheck1(WaContext context, String account1,String socid1) {

		Hashtable selectData = new Hashtable();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try{
			String sql = "\n SELECT account, passwdq, passwda, h_passwd, site_clss, name, mobile, phone" +
						 "\n FROM BCDBA.UCUSRINFO " +
						 "\n WHERE account = ? AND socid = ?" +
						 "\n AND member_clss = '1' AND site_clss in('0' , '1' ) " ;

			conn = context.getDbConnection("default", null);	// ���� �߰��κ�

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,account1);
			pstmt.setString(2,socid1);
			rs = pstmt.executeQuery();

			if(rs.next()){

				String account = rs.getString("account");
				if(account == null){
					selectData.put("account","");
				}else{
					selectData.put("account",rs.getString("account"));
				}
				String passwdq = rs.getString("passwdq");
				if(passwdq == null){
					selectData.put("passwdq","");
				}else{
					selectData.put("passwdq",rs.getString("passwdq"));
				}
				String passwda = rs.getString("passwda");
				if(passwda == null){
					selectData.put("passwda","");
				}else{
					selectData.put("passwda",rs.getString("passwda"));
				}

				byte[] encpass = rs.getBytes("h_passwd");
				String passwd_old = new String(Base64Util.encode(encpass));
				if(passwd_old == null){
					selectData.put("passwd_old","");
				}else{
					selectData.put("passwd_old",passwd_old);
				}

				String site_clss = rs.getString("site_clss");
				if(site_clss == null){
					selectData.put("site_clss","");
				}else{
					selectData.put("site_clss",rs.getString("site_clss"));
				}

				String name = rs.getString("name");
				if(name == null){
					selectData.put("name","");
				}else{
					selectData.put("name",rs.getString("name"));
				}

				String mobile = rs.getString("mobile");
				if(mobile == null){
					selectData.put("mobile","");
				}else{
					selectData.put("mobile",rs.getString("mobile"));
				}

				String phone = rs.getString("phone");
				if(phone == null){
					selectData.put("phone","");
				}else{
					selectData.put("phone",rs.getString("phone"));
				}
			}
		}catch(SQLException e){
			debug(e.toString());
		}catch(Exception ex){
			debug(ex.toString());
		}finally{
			if(rs != null) try{ rs.close(); }catch(Exception e){}
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(conn != null) try{ conn.close(); }catch(Exception e){}
		}
		return selectData;
	}

	//��й�ȣupdate
	/**
	 * ��й�ȣupdate
	*/
	protected boolean addMember(WaContext context, RequestParser parser, String memClss){
		boolean ret = false;

		PreparedStatement pstmt = null;
		PreparedStatement pstmt5 = null;
		Connection conn = null ;
		String sql = null;
		String sql5 = null;

		SSOdbprotectorWrap ssodb = new SSOdbprotectorWrap();
		try{
			int i=0;

			String str = "";
			if(!memClss.equals("5")) {
				str = "\n WHERE socid = ? AND account = ? " ;
			}else {
				str = "\n WHERE account = ? " ;
			}
			
			sql = "\n UPDATE BCDBA.UCUSRINFO " +
			  "\n SET h_passwd = ?, e_passwd = ?" + str +
			  "\n AND site_clss in('0' , '1' ) " ;			

			sql5 = "\n UPDATE BCDBA.TBENTPUSER " +
			  "\n SET h_passwd = ?, e_passwd = ?" +
			  "\n WHERE USER_JUMIN_NO = ? AND account = ? ";
			
			conn = context.getDbConnection("default", null);	// ���� �߰��κ�
			conn.setAutoCommit(false);
			
			String hashpass = parser.getParameter("passwd");
			String socid = parser.getParameter("socid").trim();
			String account = (parser.getParameter("account")).trim();
			
			byte[] encData = CipherClient.encrypt(CipherClient.MASTERKEY1,hashpass.getBytes());  //K1 : ��ȣȭ �ʵ�(���θ� ����)  K2 : ��ȣȭ,��ȣȭ ����
			byte[] hashData = CipherClient.hash(hashpass.getBytes());     // �α��� �� ���� passwd ���� /* hashData �� �����  h_passwd �� ��  */

			
			/*  DB update  */
			i = 0;
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setBytes(++i, hashData); //hash data
			pstmt.setBytes(++i, encData);  //enc data

			if(!memClss.equals("5")) {
				pstmt.setString(++i, (socid));
			}
			pstmt.setString(++i, account);

			int passUpResult = pstmt.executeUpdate();
			int passUpResult5 = 0;
			debug("======= UCUSRINFO ��ȣ���� ��� : " + passUpResult);

			if("5".equals(memClss)) {
				i = 0;
				pstmt5 = conn.prepareStatement(sql5);
				pstmt5.setBytes(++i, hashData); //hash data
				pstmt5.setBytes(++i, encData);  //enc data
				pstmt5.setString(++i, (socid));
				pstmt5.setString(++i, account);
				
				passUpResult5 = pstmt5.executeUpdate();
				debug("======= TBENTPUSER ��ȣ���� ��� : " + passUpResult5);
			}
			
			String site_clss = parser.getParameter("site_clss" , "");
			String account1 = parser.getParameter("account");
			String name = parser.getParameter("name" , "");
			String passwd = parser.getParameter("passwd");
			byte[] hash_newpasswd = CipherClient.hash(passwd.getBytes());
			String newpasswd = new String(Base64Util.encode(hash_newpasswd));

			String oldpasswd = parser.getParameter("passwd_old" , "");

			oldpasswd = oldpasswd.trim();
			newpasswd = newpasswd.trim();

			boolean chkPasswd = false;

			if(!oldpasswd.equals(newpasswd)){
				chkPasswd = true;
			}
			
			chkPasswd = true; //������ Ÿ�� ��.

			int mailUpResult = 0;
			if(site_clss.equals("0")){
				if( chkPasswd ) {
					// ���Ϻ�к�ȣ ����
					mailUpResult = mailPassUpdate(context, account, newpasswd);
				} else {
					mailUpResult = 1;
				}
			}

			if (passUpResult == 1 && (mailUpResult > -1) ){
				String msg = "CHPASSWD|"+ account + "|" + socid + "|"+passUpResult+"|"+mailUpResult+"|��й�ȣ���漺��";
				BcLog.memberLog(msg);
				ret = true;
				conn.commit();
			} else {
				ret = false;
				conn.rollback();
				String msg = "CHPASSWD|"+ account + "|" + socid + "|"+passUpResult+"|"+mailUpResult+"|��й�ȣ�������";
				BcLog.memberLog(msg);
			}

// DB trigger �۾����� SSO �κ� ���� 2005.10.19
			if (passUpResult == 1 && (mailUpResult > -1) ){
				if (ssodb.changePasswordByAdmin(account, parser.getParameter("passwd")) ) {
					String msg = "CHPASSWD|"+ account + "|" + socid + "|"+passUpResult+"|"+mailUpResult+"|��й�ȣ���漺��";
					BcLog.memberLog(msg);
					ret = true;
					conn.commit();
				} else {
					ret = false;
					conn.rollback();
					String msg = "CHPASSWD|"+ account + "|" + socid + "|"+passUpResult+"|"+mailUpResult+"|��й�ȣ�������";
					BcLog.memberLog(msg);
				}
			}

		}catch(Exception e){
			if(conn != null) try{ conn.rollback(); }catch(Exception ex1){}
			ret = false;
			debug(e.toString());
		}finally{
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(pstmt5 != null) try{ pstmt5.close(); }catch(Exception e){}
			if(conn != null) try{ conn.rollback(); }catch(Exception ex1){}
			try{ conn.close(); }catch(Exception ex){}
			return ret;
		}
	}


	//��й�ȣupdate
	/**
	 * ��й�ȣupdate
	*/
	protected boolean addMemberNoCard(WaContext context, RequestParser parser){
		boolean ret = false;

		PreparedStatement pstmt = null;
		Connection conn = null ;
		String sql = null;
		SSOdbprotectorWrap ssodb = new SSOdbprotectorWrap();
		try{
			int i=0;
			/*
			sql = " UPDATE BCDBA.UCUSRINFO " +
				  " SET h_passwd = ?, e_passwd = ?, passwdq = ?, passwda = ? " +
				  " WHERE socid = ? AND account = ? " +
				  " AND member_clss = '1' AND site_clss in('0' , '1' ) " ;
			*/
			sql = "\n UPDATE BCDBA.UCUSRINFO " +
			  "\n SET h_passwd = ?, e_passwd = ?" +
			  "\n WHERE socid = ? AND account = ? " +
			  "\n AND member_clss = '4' AND site_clss in ('2') " ;

			conn = context.getDbConnection("default", null);	// ���� �߰��κ�
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(sql);

			String hashpass = parser.getParameter("passwd");

			byte[] encData = CipherClient.encrypt(CipherClient.MASTERKEY1,hashpass.getBytes());  //K1 : ��ȣȭ �ʵ�(���θ� ����)  K2 : ��ȣȭ,��ȣȭ ����
			byte[] hashData = CipherClient.hash(hashpass.getBytes());     // �α��� �� ���� passwd ���� /* hashData �� �����  h_passwd �� ��  */

			/*  DB update  */
			
			i = 0;

			pstmt.setBytes(++i, hashData); //hash data
			pstmt.setBytes(++i, encData);  //enc data
			
			/*
			pstmt.setString(++i,(parser.getParameter("passwdq")).trim());
			pstmt.setString(++i,(parser.getParameter("passwda")).trim());
			*/

			String socid = parser.getParameter("socid").trim();
			pstmt.setString(++i, (socid));

			String account = (parser.getParameter("account")).trim();
			pstmt.setString(++i, account);

			int passUpResult = pstmt.executeUpdate();

			String site_clss = parser.getParameter("site_clss", "");
			String account1 = parser.getParameter("account", "");
			String name = parser.getParameter("name", "");
			String passwd = parser.getParameter("passwd", "");
			byte[] hash_newpasswd = CipherClient.hash(passwd.getBytes());
			String newpasswd = new String(Base64Util.encode(hash_newpasswd));

			String oldpasswd = parser.getParameter("passwd_old", "");

			oldpasswd = oldpasswd.trim();
			newpasswd = newpasswd.trim();

			boolean chkPasswd = false;

			if(!oldpasswd.equals(newpasswd)){
				chkPasswd = true;
			}
			
			chkPasswd = true; //������ Ÿ�� ��.

			int mailUpResult = 0;
			if(site_clss.equals("0")){
				if( chkPasswd ) {
					// ���Ϻ�к�ȣ ����
					mailUpResult = mailPassUpdate(context, account, newpasswd);
				} else {
					mailUpResult = 1;
				}
			}
		
			if (passUpResult == 1 && (mailUpResult > -1)) {
				String msg = "CHPASSWD|"+ account + "|" + socid + "|"+passUpResult+"|"+mailUpResult+"|��й�ȣ���漺��";
				BcLog.memberLog(msg);
				ret = true;
				conn.commit();
//				debug("���������������� ��й�ȣ ���� >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			} else {
				ret = false;
				conn.rollback();
				String msg = "CHPASSWD|"+ account + "|" + socid + "|"+passUpResult+"|"+mailUpResult+"|��й�ȣ�������";
				BcLog.memberLog(msg);
//				debug("���н��н��н��� ��й�ȣ ���� >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			}

// DB trigger �۾����� SSO �κ� ���� 2005.10.19
			if (passUpResult == 1 && (mailUpResult > -1)) {
				if (ssodb.changePasswordByAdmin(account, hashpass)) {

					String msg = "CHPASSWD|"+ account + "|" + socid + "|"+passUpResult+"|"+mailUpResult+"|��й�ȣ���漺��";
					BcLog.memberLog(msg);
					ret = true;
					conn.commit();
//					debug("���������������� ��й�ȣ ���� >>>>>>>>>>>>>>>>>>>>>>>>>>>>");

				} else {

					ret = false;
					conn.rollback();
					String msg = "CHPASSWD|"+ account + "|" + socid + "|"+passUpResult+"|"+mailUpResult+"|��й�ȣ�������";
					BcLog.memberLog(msg);
//					debug("���н��н��н��� ��й�ȣ ���� >>>>>>>>>>>>>>>>>>>>>>>>>>>>");

				}

			}  else {
				
				conn.rollback();
//				debug("��й�ȣ ���� ���н��н��н��� >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
				ret = false;
//				debug("��й�ȣ ���� ���н��н��н��� >>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			}

		} catch(Exception e) {
			if(conn != null) try{ conn.rollback(); }catch(Exception ex1){}
			ret = false;
			debug(e.toString());
		}finally{
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(conn != null) try{ conn.rollback(); }catch(Exception ex1){}
			try{ conn.close(); }catch(Exception ex){}
			return ret;
		}
	}

	/***************************************************************
	* ���� ��й�ȣ update  LimKeonKuk 20041110 �߰�
	***************************************************************/
	protected int mailPassUpdate(WaContext context, String account, String EncPass){
		Connection con = null ;
		PreparedStatement pstmt = null;
		String sql = null;
		int resint = 0;

		try{

			//sql = " UPDATE bcpwd.user_info1 SET password ='" + EncPass + "' WHERE ID = '" + account + "'" ;
			sql = "\n UPDATE UserDB.userinfo SET password ='" + EncPass + "' WHERE ID = '" + account + "'" ;

			con = context.getDbConnection("MYSQL", null);	// ���� �߰��κ�

			pstmt = con.prepareStatement(sql);

			resint = pstmt.executeUpdate(sql);

		} catch(Exception e) {
			if(con != null) try{ con.rollback(); }catch(Exception ex1){}
			resint = -1;
			e.printStackTrace();
		} finally {
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(con != null) try{ con.rollback(); }catch(Exception ex1){}
			try{ con.close(); }catch(Exception ex){}
			return resint;
		}
	}

	/**
	 * getTokenizer
	*/
	protected String getTokenizer(String ht){
		StringTokenizer st = new StringTokenizer(ht,"-");
		String token_value = "";
		 while (st.hasMoreTokens()) {
			 token_value += new String(st.nextToken());
		 }
		 return token_value;
	}

	/**
	 * getUserInfo
	*/
	public String getUserInfo(String strpwd){
		String result = "";
		Properties pro = new Properties();

		SSOdbprotectorWrap ssodb = new SSOdbprotectorWrap();
		
		try {
			pro = ssodb.getUserInfo(strpwd);

			result = (String)pro.getProperty("ENCPASSWD");

			debug("122212121221212 >> " + result);

		} catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}