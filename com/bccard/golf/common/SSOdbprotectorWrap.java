/***************************************************************************************************
*   Ŭ������  : SSOdbprotectorWrap
*   �ۼ���    : 
*   ����      : 
*   �������  : bccard.com
*   �ۼ�����  : 2005.7.21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.initech.eam.api.NXAccount;
import com.initech.eam.api.NXAccountSet;
import com.initech.eam.api.NXAttributeAPI;
import com.initech.eam.api.NXAttributePath;
import com.initech.eam.api.NXAttributeTypeSet;
import com.initech.eam.api.NXAttributeValue;
import com.initech.eam.api.NXContext;
import com.initech.eam.api.NXExternalField;
import com.initech.eam.api.NXExternalFieldMetaAPI;
import com.initech.eam.api.NXExternalFieldMetaSet;
import com.initech.eam.api.NXExternalFieldSet;
import com.initech.eam.api.NXRole;
import com.initech.eam.api.NXRoleInfo;
import com.initech.eam.api.NXUserAPI;
import com.initech.eam.api.NXUserInfo;
import com.initech.eam.base.APIException;
import com.initech.eam.base.EmptyResultException;
import com.initech.eam.nls.CookieManager;
import com.initech.eam.smartenforcer.SECode;
import com.initech.vender.bccard.NXBCAuthChecker;
public class SSOdbprotectorWrap {

	
	static private String nd_url = "";
	static private String nd_url2 = "";
	static private String nls_login_url = "";
	static private String nls_error_url = "";
	
// ID/PW type
	private String toa = "1";
	
	// domain (.kang.com)
	private String sso_domain = ".bccard.com";
	
   	static List urlList = new ArrayList();
	static {
    		urlList.add(nd_url);
    		urlList.add(nd_url2);
	}
	
//	 �Ӽ� ����	
	private String[] attr_name
		= {"�μ�", "����", "����"}; 
	private boolean[] attr_type
		= {true, false, false};
	
	// ORACLE DB ���߱�
	static private String sDB_URL = "";
	static private String sDB_ID = "";
	static private String sDB_PW = "";

	static{

		String ip = "";
		String dv_was_1st = "";	//���߱� WAS IP
		String dr_was_1st = "";	//DR 1ȣ�� WAS IP
		String dr_was_2nd = "";	//DR 2ȣ�� WAS IP
		String rl_was_1st = "";	//� 1ȣ�� WAS IP
		String rl_was_2nd = "";	//��� 2ȣ�� WAS IP

		String dv_sso_1st = ""; //���� SSO URL
		String dr_sso_1st = ""; //DR 1ȣ�� SSO URL
		String dr_sso_2nd = ""; //DR 2ȣ�� SSO URL
		String rl_sso_1st = ""; //� 1ȣ�� SSO URL
		String rl_sso_2nd = ""; //� 2ȣ�� SSO URL
		
		String dv_ora_1st = ""; //���� DB����		"jdbc:oracle:thin:@211.181.255.91:1521:WEBDEV";
		String dr_ora_1st = ""; //DR DB����		"jdbc:oracle:thin:@211.181.255.56:1521:WEB01";
		String rl_ora_1st = ""; //� DB����		"jdbc:oracle:thin:@61.98.69.56:1521:WEB01";
		try {
			ip = java.net.InetAddress.getLocalHost().getHostAddress();
	        dv_was_1st = AppConfig.getAppProperty("DV_WAS_1ST");
			dr_was_1st = AppConfig.getAppProperty("DR_WAS_1ST");
			dr_was_2nd = AppConfig.getAppProperty("DR_WAS_2ND");
			rl_was_1st = AppConfig.getAppProperty("RL_WAS_1ST");
			rl_was_2nd = AppConfig.getAppProperty("RL_WAS_2ND");
			
			dv_sso_1st = AppConfig.getAppProperty("DV_SSO_1ST");
			dr_sso_1st = AppConfig.getAppProperty("DR_SSO_1ST");
			dr_sso_2nd = AppConfig.getAppProperty("DR_SSO_2ND");
			rl_sso_1st = AppConfig.getAppProperty("RL_SSO_1ST");
			rl_sso_2nd = AppConfig.getAppProperty("RL_SSO_2ND");

			dv_ora_1st = AppConfig.getAppProperty("DV_ORA_1ST");
			dr_ora_1st = AppConfig.getAppProperty("DR_ORA_1ST");
			rl_ora_1st = AppConfig.getAppProperty("RL_ORA_1ST");

		} catch (java.io.IOException ignore) {}

		if ( dv_was_1st.equals(ip) ) {										//���߱�
			
			nd_url = dv_sso_1st + ":5480/";
			nd_url2 = dv_sso_1st + ":5480/";
			nls_login_url = dv_sso_1st + ":9611";
			nls_error_url = dv_sso_1st + ":9611/nls3/error.jsp";

			sDB_URL = dv_ora_1st;
			sDB_ID = "bcsso";
			sDB_PW = "bcsso";

		} else if ( dr_was_1st.equals(ip) || dr_was_2nd.equals(ip) ) {		//DR

			nd_url			= dr_sso_1st + ":5480/";
			nd_url2			= dr_sso_2nd + ":5480/";
			nls_login_url	= dr_sso_1st;
			nls_error_url	= dr_sso_1st + "/nls3/error.jsp";

			sDB_URL = dr_ora_1st;
			sDB_ID = "bcsso";
			sDB_PW = "bcsso";

		} else {  // ���
			nd_url			= rl_sso_1st + ":5480/";
			nd_url2			= rl_sso_2nd + ":5480/";
			nls_login_url	= rl_sso_1st;
			nls_error_url	= rl_sso_1st + "/nls3/error.jsp";

			sDB_URL = rl_ora_1st;
			sDB_ID = "bcsso";
			sDB_PW = "bcsso";
		}
	}	
	//public void RoleCheck(HttpServletResponse response,int ieam,int irole)
	/** ****************************************************************************
	 * roleCheck
	 **************************************************************************** */
	public void roleCheck(HttpServletResponse response,int ieam,int irole)
	throws Exception {		
		if(irole<ieam){
			response.sendRedirect(nls_error_url + "?errorCode=1200");
			return;    
		}	
	}
	
	/** ****************************************************************************
	 * goLoginPage
	 **************************************************************************** */
	public void goLoginPage(HttpServletResponse response, String uurl)
	throws Exception {
		CookieManager.addCookie(SECode.USER_URL, uurl, sso_domain, response);
		CookieManager.addCookie(SECode.R_TOA, toa, sso_domain, response);
		response.sendRedirect(nls_login_url+"/nls3/clientLogin.jsp");
	}

	/** ****************************************************************************
	 * goErrorPage
	 **************************************************************************** */
	public void goErrorPage(HttpServletResponse response, int error_code)
	throws Exception {
		CookieManager.removeNexessCookie(sso_domain, response);
		response.sendRedirect(nls_error_url + "?errorCode=" + error_code);
	}

	/** ****************************************************************************
	 * getSsoId
	 **************************************************************************** */
	public String getSsoId(HttpServletRequest request) {
		String sso_id = null;

		sso_id = CookieManager.getCookieValue(SECode.USER_ID, request);
		return sso_id;
	}	
	
	/** ****************************************************************************
	 * getContext
	 **************************************************************************** */
	public NXContext getContext()
	{
		NXContext context = null;
		try
		{
			List serverurlList = new ArrayList();
			serverurlList.add(nd_url);
			serverurlList.add(nd_url2);
			context = new NXContext(serverurlList);
		}
		catch (Exception e)
		{
			
		}		
		return context;
	}
	
	// 2005.12.07 SSO ����üũ ��ġ��� - kyyou
	/** ****************************************************************************
	 * getEamSessionCheck
	 **************************************************************************** */
	public String getEamSessionCheck(HttpServletRequest request, HttpServletResponse response)
	   {
		String retCode = "";
		NXContext context = getContext();
		NXBCAuthChecker authChecker = new NXBCAuthChecker(context);
  
		try {
			/**
			* �ؼ��� ��Ű�� valid������ �˻��Ͽ� �α��� ���¸� �˻��Ѵ�. 
			* 
			* @param request                 Servlet Request
			* @param response                Servlet Response
			* @param sessionReuseTime        Session Reuse Time�� ���� (0�̸�10��)
			* @param sessionTime             Session Time�� ���� (0�̸� 1200��)
			* @return �α��λ��� �ڵ� 
			*/
			retCode = authChecker.readNexessCookie(request, response, 0, 0);
		} catch (com.initech.eam.base.APIException apie) {
			
		} catch (IllegalArgumentException ie) {
			
		} catch (Exception e) {
			
		}
		return retCode;	
	   }
	/*  ���� �ڵ� ����
	* ���� ������ ���� ��ȿ�� ���� �� ���� ���� ó���� �����Ѵ�.
	* Vector Value  
	* ULAT : �α��νð� (���ŵ� ������ �ٽ� ����)
	* RESULT : ���� ���� ���� ���
	*	-. RESULT = 0 : ������ �α��� ������ ���� ���ϰ�, �׿ܴ� �����ڵ�
	*	-. RESULT = 100 : �͸����� (�α��� �Ǿ����� ���� ���)
	*	-. RESULT = 200 : ����� ���̵� ���� ���
	*	-. RESULT = 1000 : �� �ʿ��� ��Ű�� �Ϻΰ� ������
	*	-. RESULT = 1001 : ���� Ÿ�Ӿƿ�(LAT�� session time�� ����� ���)
	*	-. RESULT = 1002 : ��Ű������ �� �����ϳ� �ؼ����� ���������� �߱��� 
        *                          ��Ⱑ �ƴҶ�(HMAC�� Ʋ�� ���)
	*	-. RESULT = 1004 : IP�� ���� ���� ���
	*/
	
	// ���� �Լ� �ּ�ó��
	/*****
	public String getEamSessionCheck
	(HttpServletRequest request,HttpServletResponse response)
	{
		String retCode = "";
		NXContext context = null;
		try
		{
			context = getContext();						
			NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
			retCode = nxNLSAPI.readNexessCookie(request, response, 0, 0); 
		}		
		catch(Exception npe) 
		{	
			npe.printStackTrace();
		}	
		return retCode;		
	}
	*****/


	
	/* **************************************************************** */
	/* ***************************** SELECT *************************** */
	/* **************************************************************** */

	/**
	 * SSO ID �� �����ϴ��� �˻�
	 * 1. true : ����
	 * 2. false : �������
	 * 3. Exception : network
	 */
	public boolean existUser(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		
		boolean returnFlag = false;

		try {
			context = getContext();
			NXUserAPI userAPI = new NXUserAPI(context);
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);

			returnFlag= userAPI.existUser(userid);
		} catch (Exception e) {
			//e.printStackTrace();
		}
		//System.out.println("existUser:[" + userid + ":" + returnFlag + "]");
		return returnFlag;
	}

	/**
	 * ����� �⺻���� ��ȸ
	 * return : Properties
	 *			userid, email, enable, startvalid, endvalid, name,
	 *			lastpasswdchange
	 * ����ڰ� �������� �ʴٸ� return null
	 */
	public Properties getUserInfo(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return null;

		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
				
		Properties prop = null;
		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
			NXUserAPI userAPI = new NXUserAPI(context);

			NXUserInfo userInfo = userAPI.getUserInfo(userid);
			prop = new Properties();

			prop.setProperty("USERID", userInfo.getUserId());
			prop.setProperty("EMAIL", userInfo.getEmail());
			prop.setProperty("ENABLE", String.valueOf(userInfo.getEnable()));
			prop.setProperty("STARTVALID", userInfo.getStartValid());
			prop.setProperty("ENDVALID", userInfo.getEndValid());
			prop.setProperty("NAME", userInfo.getName());
			prop.setProperty("ENCPASSWD", userInfo.getEncpasswd());
			prop.setProperty("LASTPASSWDCHANGE",
				userInfo.getLastpasswdchange());
			prop.setProperty("LastLoginIP", userInfo.getLastLoginIp());
			prop.setProperty("LastLoginTime", userInfo.getLastLoginTime());
			prop.setProperty("LastLoginAuthLevel",	userInfo.getLastLoginAuthLevel());

		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� �ʰų� �ܺΰ����� ����
		} catch (APIException e) {
			//throw e;
			
		}
		//System.out.println("getUserInfo:[" + prop + "]");
		return prop;
	}

	/**
	 * ��ü �ý��� ���� ���� ��ȸ
	 * service name, account name, account password
	 * account name, account password �� String[] �� ����
	 * ����ڰ� �������� �ʰų� �ܺΰ����� ���ٸ� return null
	 */
	public Properties getUserAccounts(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return null;

		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
				
		Properties prop = null;
		NXAccountSet accounts = null;
		NXAccount account = null;
		String serviceName = null;
		String[] accountData = null;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);			
			NXUserAPI userAPI = new NXUserAPI(context);
			
			accounts = userAPI.getUserAccounts(userid);
			prop = new Properties();
			Iterator accountsIter = accounts.iterator();
			while (accountsIter.hasNext()) {
				account = (NXAccount) accountsIter.next();
				accountData = new String[2];
				serviceName = account.getServiceName();
				accountData[0] = account.getAccountName();
				accountData[1] = account.getAccountPassword();

				/* TODO : �ӽ÷� ����ϴ� ���̴� �� ���� �ּ��� */
				/*
				prop.put("[" + serviceName + ":" + accountData[0]
					+ "." + accountData[1] + "]", "");
				*/
				/* TODO : �ӽ÷� ����ϴ� ���̴� �� ���� �ּ��� */

				prop.put(serviceName, accountData);
				//System.out.println("getUserAccounts[service name:"
				//+ account.getServiceName() + "], "
				//+ "[account name:" + account.getAccountName() + "], "
				//+ "[account password:" + account.getAccountPassword() + "]");
			}
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� �ʰų� �ܺΰ����� ����
		} catch (APIException e) {
			//throw e;
			//e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		return prop;
	}

	/**
	 * �ý��� ���� ���� ��ȸ
	 * account name, account password
	 * account name, account password �� String[] �� ����
	 * ����ڰ� �������� �ʰų� �ܺΰ����� ���ٸ� return null
	 */
	public String[] getUserAccount(String userid, String serviceName)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		NXAccount account = null;
		String[] accountData = null;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
			NXUserAPI userAPI = new NXUserAPI(context);
			
			account = userAPI.getUserAccount(userid, serviceName);
			accountData = new String[2];

			accountData[0] = account.getAccountName();
			accountData[1] = account.getAccountPassword();
/*
			System.out.println("getUserAccount[service name:"
				+ account.getServiceName() + "], "
				+ "[account name:" + account.getAccountName() + "], "
				+ "[account password:" + account.getAccountPassword() + "]");
*/
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� �ʰų� �ܺΰ����� ����
		} catch (APIException e) {
			//throw e;
	 		// ����ڰ� �������� �ʰų� �ܺΰ����� ����
			// TODO : ����
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		return accountData;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return null
	 * Reference=LG CNS, ����=LCD 2����, ����=����, ����=LPL, ��å=����
	 * TODO : ��ȸȽ�� �˻� �ʿ�
	 */
	public Properties getUserAttributes(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		
		Properties prop = null;
		Map userAttributeMap = null;
		String attrName = null;
		String attrValue = null;
		List valueList = null;
		NXAttributeValue nxav = null;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);
			
			userAttributeMap = userAPI.getUserAttributes(userid);

			prop = new Properties();

			Iterator typeNameIter = userAttributeMap.keySet().iterator();
			while (typeNameIter.hasNext()) {
				attrName = (String) typeNameIter.next();
				valueList = (List) userAttributeMap.get(attrName);
				for (int i = 0, j = valueList.size(); i < j; i++) {
					nxav = (NXAttributeValue) valueList.get(i);
					attrValue = nxav.toString();
					prop.put(attrName, attrValue);
					//System.out.println("getUserAttributes[attrName:" + attrName
					//	+ ",attrValue:" + attrValue+ "]");
				}
			}
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		return prop;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return null
	 * �Ӽ��� ���� �ʴ´ٸ� IllegalArgumentException
	 * multi value �� ����
	 */
	/* 
	public String getUserAttribute(String userid, String attrName)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		String attrValue = null;
		List valueList = null;
		NXAttributeValue nxav = null;

		//test
		//NXAttributeAPI nxaa = NXAttributeAPI.getInstance(context);
		//NXAttributeTypeSet attributeTypeSet = nxaa.getAllAttributes();
		//System.out.println("attributeTypeSet : " + attributeTypeSet);
		//

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			valueList = userAPI.getUserAttributes(userid, attrName);
			System.out.println("getUserAttribute[userid:" + userid
				+ ",attrName:" + attrValue+ "]");
			for (int i = 0, j = valueList.size(); i < j; i++) {
				nxav = (NXAttributeValue) valueList.get(i);
				attrValue = nxav.toString();
				System.out.println("getUserAttribute[attrName:" + attrName
					+ ",attrValue:" + attrValue+ "]");
			}
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//throw e;
	 		// �Ӽ��� ���� �ʴ´ٸ� IllegalArgumentException
			e.printStackTrace();
		}
		return attrValue;
	}
	*/
	// by Kang
	/**
	 * getUserAttribute
	 */
	public String getUserAttribute(String userid, String attrName)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();
		
		String attrValue = null;
		List valueList = null;
		NXAttributeValue nxav = null;               
		
		NXAttributeAPI nxaa = NXAttributeAPI.getInstance(context);
		NXAttributeTypeSet attributeTypeSet = nxaa.getAllAttributes();
		//System.out.println("attributeTypeSet : " + attributeTypeSet);
		
		try {		     
		     NXUserAPI userAPI = new NXUserAPI(context);
		     valueList = userAPI.getUserAttributes(userid, attrName);
		     //System.out.println("getUserAttribute[userid:" + userid
		     //        + ",attrName:" + attrValue+ "]");
		
		     for (int i = 0, j = valueList.size(); i < j; i++) {
		             nxav = (NXAttributeValue) valueList.get(i);
		             Vector path = attributeTypeSet.getAttributeValuePath(attrName, nxav.getID()); 
		             attrValue = nxav.toString();
		             //attrValue = path.toString();
					 /*
		             System.out.println("getUserAttribute[attrName:" + attrName
		                    + ",attrValue:" + path.toString()+ "]");
					*/
		     }
		} catch (IllegalArgumentException e) { 
		     
		}
		
		return attrValue;
	}     
	/**
	 * getUserAttributePath
	 */
	public Properties getUserAttributePath(String userid, String attrName)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();
		
		String attrValue = "";
		List valueList = null;
		NXAttributeValue nxav = null;               
		Properties prop = null;
		
		NXAttributeAPI nxaa = NXAttributeAPI.getInstance(context);
		NXAttributeTypeSet attributeTypeSet = nxaa.getAllAttributes();
		//System.out.println("attributeTypeSet : " + attributeTypeSet);
		
		try {		     
		     NXUserAPI userAPI = new NXUserAPI(context);
		     valueList = userAPI.getUserAttributes(userid, attrName);
		     /*
			 System.out.println("getUserAttribute[userid:" + userid
		             + ",attrName:" + attrValue+ "]");
			*/
		     prop = new Properties();
		     for (int i = 0, j = valueList.size(); i < j; i++) {
		             nxav = (NXAttributeValue) valueList.get(i);
		             Vector path = attributeTypeSet.getAttributeValuePath(attrName, nxav.getID()); 
		             //attrValue = path.toString();
		             
		             // by Kang
		             //System.out.println("***************count="+path.size());
		             for (int y = 0; y < path.size(); y++) {
		             	//System.out.println("***************value="+path.elementAt(y));
		             	//((String)((Vector)GList.elementAt(i)).elementAt(0)).trim();
		             	attrValue = attrValue + "/" + path.elementAt(y);
		             }
		             
		             prop.put(attrName, attrValue);
		             //System.out.println("getUserAttribute[attrName:" + attrName
		             //       + ",attrValue:" + path.toString()+ "]");
		     }
		} catch (IllegalArgumentException e) { 
		     
		}
		
		return prop;
	}     
	
	
	/**
	 * ����� Ȯ������ ��ȸ
	 * return : Properties
	 * ����ڰ� �������� �ʰų� Ȯ���ʵ尡 ���ٸ� return null
	 */
	public Properties getUserExFields(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		Properties prop = null;
		NXExternalFieldSet nxefs = null;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);
		
			nxefs = userAPI.getUserExternalFields(userid);
			prop = new Properties();

			if (nxefs != null) {
				Iterator iter = nxefs.iterator();
				while(iter.hasNext()) {
					NXExternalField nxef = (NXExternalField) iter.next();
					prop.setProperty(nxef.getName(), (String) nxef.getValue());
					//System.out.println("Ȯ�� �ʵ� " + nxef.getName()
					//	+ " : " + nxef.getValue());
				}
			} 
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� �ʰų� Ȯ���ʵ尡 ����
		} catch (APIException e) {
			//throw e;
			
		}
		//System.out.println("getUserExFields:[" + prop + "]");
		return prop;
	}

	/**
	 * ������� Ư�� Ȯ�� �ʵ� ���� ��ȸ
	 * return : String
	 * ����ڰ� �������� �ʰų� Ȯ���ʵ尡 ���ٸ� return null
	 */
	public String getUserExField(String userid, String exName)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		
		NXExternalField nxef = null;
		String returnValue = null;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);
			//System.out.println("[config.jsp] #####################>>>>userAPI=["+userAPI+"]");
			//System.out.println("[config.jsp] #####################>>>>userid=["+userid+"],exName=["+exName+"]");
			
			nxef = userAPI.getUserExternalField(userid, exName);
			//System.out.println("[config.jsp] #####################>>>>nxef=["+nxef+"]");
			
			returnValue = (String) nxef.getValue();
			//System.out.println("[config.jsp] #####################>>>>returnValue=["+returnValue+"]");
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� �ʰų� Ȯ���ʵ尡 ����
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		//System.out.println("getUserExField:[" + userid + ":"
		//	+ exName + ":" + returnValue+ "]");
		return returnValue;
	}

	/**
	 * SSO ID �迭 ��ȯ
	 */ 
	public String[] getUserListByAttribute(String attrName, String attrValue)
	throws Exception {
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		NXUserInfo[] userInfoList = null;
		int cnt = 0;
		String[] returnData = null;

		// �ӽ� ���initialize
		//userAPI.initialize("jdbc:ldap://kalsse.knoc.co.kr:59812/ou=users,dc=kang,dc=com?SEARCH_SCOPE:=subTreeScope", "cn=Directory Manager", "directory_1234");

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userInfoList = userAPI.getUserListByAttribute(attrName, attrValue);
			cnt = userInfoList.length;
			//System.out.println("***************cnd="+cnt);
			
			returnData = new String[cnt];
			for (int i = 0; i < cnt; i++) {
				returnData[i] = userInfoList[i].getUserId();
			}
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� �ʰų� Ȯ���ʵ尡 ����
			
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		//System.out.println("getUserListByAttribute:[attrName:" + attrName
		//	+ ",attrValue:" + attrValue + ",cnt:" + cnt + "]");
		return returnData;
	}

	/**
	 * SSO ID �迭 ��ȯ
	 */	 
	public String[] getUserListByExField(String exName, String exValue)
	throws Exception {
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		NXUserInfo[] userInfoList = null;
		int cnt = 0;
		String[] returnData = null;

		// �ӽ� ���
		//userAPI.initialize("jdbc:ldap://kalsse.knoc.co.kr:59812/ou=users,dc=kang,dc=com?SEARCH_SCOPE:=subTreeScope", "cn=Directory Manager", "22222222");

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userInfoList = userAPI.getUserListByExternalField(exName, exValue);
			cnt = userInfoList.length;
			returnData = new String[cnt];
			for (int i = 0; i < cnt; i++) {
				returnData[i] = userInfoList[i].getUserId();
			}
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� �ʰų� Ȯ���ʵ尡 ����
			//e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		//System.out.println("getUserListByAttribute:[exName:" + exName
		//	+ ",exValue:" + exValue + ",cnt:" + cnt + "]");
		return returnData;
	}

	/* **************************************************************** */
	/* ***************************** INSERT *************************** */
	/* **************************************************************** */

	/**
	 * ����ڰ� �����Ѵٸ� return false
	 * �����Ѵٸ� return true
	 */
	public boolean addUser(String userid, String enable, String name,
	String email, String passwd, Properties prop)
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		//System.out.println("start addUser#######");				
		
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		
		boolean returnFlag = false;
		
		NXExternalFieldMetaAPI nxefma = null;
		NXExternalFieldMetaSet nxefms = null; 
		
		NXExternalFieldSet nxefs = null;
		NXExternalField nxef = null;
		
		Enumeration enm = null;
		String exName = null;
		String exValue = null;
		
		//System.out.println("Ȯ���ʵ�>> prop:["+prop+"]");
		
		try {						
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			if (prop != null) {
				nxefma = NXExternalFieldMetaAPI.getInstance(context);
                		nxefms = nxefma.getExternalFieldMetaSet();
               			 //System.out.println("nxefms : [" + nxefms + "]");
                
                		nxefs = nxefms.getMustFields();                               
                		//nxefs = nxefms.getAllFields();
                		//System.out.println("nxefs : [" + nxefs + "]");							
				
				/*********** Ȯ���ʵ尪�� NOT NULL �� ��� ���
				enm = prop.propertyNames();
				while (enm.hasMoreElements()) {
					exName = (String) enm.nextElement();
					exValue = prop.getProperty(exName);
					System.out.println("exName = " + exName);
					System.out.println("exValue = " + exValue);
					nxef = nxefs.getExternalField(exName);
					nxef.setValue(((Object) exValue));
				}
				*************/
			} else {
				nxefma = NXExternalFieldMetaAPI.getInstance(context);
				nxefms = nxefma.getExternalFieldMetaSet();
				nxefs = nxefms.getMustFields();
			}
/*
			System.out.println("userid="+userid);
			System.out.println("enable="+enable);
			System.out.println("name="+name);
			System.out.println("email="+email);
			System.out.println("passwd="+passwd);
			System.out.println("nxefs=["+nxefs+"]");
*/			
			userAPI.addUser(userid, enable, name, email, passwd, nxefs);
			returnFlag = true;
			
			if(returnFlag){
				// Ȯ���ʵ尡 �ִ°��.
				if (prop != null) {
					enm = prop.propertyNames();
					while (enm.hasMoreElements()) {
						exName = (String) enm.nextElement();
						exValue = prop.getProperty(exName);
						//System.out.println("exName = " + exName);
						//System.out.println("exValue = " + exValue);
						returnFlag = addExFieldValueToUser(userid, exName, exValue);	
						//System.out.println("addExFieldValueToUser[return:" + returnFlag + "]");
					}				
				}
			}
			
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			
		} catch (APIException e) {
			
			//throw e;
		} catch (IllegalArgumentException e) {
			
			//throw e;
		}
		/*
		System.out.println("addUser[userid:" + userid + ",enable:"
			+ enable + ",name:" + name + ",email:" + email
			+ ",passwd:" + passwd + ",prop:" + prop + "]");
		*/
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return false
	 * �ܺΰ����� �̹� �����Ѵٸ� return false
	 * �����Ѵٸ� return true
	 */
	public boolean addAccountToUser(String userid, String serviceName,
	String accountName, String accountPasswd)
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		if(serviceName==null||serviceName.length()<1) return false;
		if(accountName==null||accountName.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		NXExternalFieldSet nxefs = null;
		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.addAccountToUser(userid, serviceName,
				accountName, accountPasswd);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		/*
		System.out.println("addAccountToUer[userid:" + userid
			+ ",serviceName" + serviceName + ",accountName:" + accountName
			+ ",accountPasswd" + accountPasswd + "]");
		*/
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return false
	 * �̹� ��ϵ� �Ӽ��̶�� return false
	 * �����Ѵٸ� return true
	 */
	public boolean addAttributeToUser(String userid, String attrName,String attrValue) 
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.addAttributeToUser(userid, attrName, attrValue);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
	 		
		} catch (APIException e) {
			//throw e;
			
			// �̹� �Ҵ�� ���
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		//System.out.println("addAttributeToUser[userid:" + userid
		//	+ ",attrName:" + attrName + ",attrValue:" + attrValue + "]");
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return false
	 * �̹� ��ϵ� Ȯ���ʵ��� return false
	 * �����Ѵٸ� return true
	 */
	public boolean addExFieldValueToUser(String userid, String exName,
	String exValue) throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;
		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.addExternalFieldValueToUser(userid, exName, exValue);
			returnFlag = true;			
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
	 		
		} catch (APIException e) {
			//throw e;
			//System.out.println("addExFieldValueToUser1[error:"+e.toString()+"]");
			
		} catch (IllegalArgumentException e) {		
			//throw e;
			//System.out.println("addExFieldValueToUser2[error:"+e.toString()+"]");
			
		}
		/*
		System.out.println("addExFieldValueToUser[userid:" + userid
			+ ",exName:" + exName + ",exValue:" + exValue + "]");			
		*/
		return returnFlag;
				
	}

	/* **************************************************************** */
	/* ***************************** DELETE *************************** */
	/* **************************************************************** */

	/**
	 * ����ڰ� �������� �ʴ´ٸ� return false
	 * �����Ѵٸ� return true
	 */
	public boolean removeUser(String userid) throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.removeUser(userid);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			//e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		//System.out.println("removeUser[userid:" + userid + "]");
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return false
	 * �ܺΰ����� �������� �ʴٸ� return false
	 * �����Ѵٸ� return true
	 */
	public boolean removeAccountFromUser(String userid, String serviceName)
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.removeAccountFromUser(userid, serviceName);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			//e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		//System.out.println("removeAccountFromUser[userid:" + userid
		//	+ ",serviceName:" + serviceName + "]");
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return false
	 * ��ϵ� �Ӽ��� �ƴ϶�� return false
	 * �����Ѵٸ� return true
	 */
	public boolean removeAttributeFromUser(String userid, String attrName,
	String attrValue) throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.removeAttributeFromUser(userid, attrName, attrValue);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			//e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		/*
		System.out.println("removeAttributeFromUser[userid:" + userid
			+ ",attrName:" + attrName + ",attrValue:"
			+ attrValue + "]");
		*/
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return false
	 * ��ϵ��� ���� Ȯ���ʵ��� return false
	 * �����Ѵٸ� return true
	 */
	public boolean removeExFieldValueFromUser(String userid, String exName, String exValue) 
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;
		
		//System.out.println("[removeExFieldValueFromUser()] userid=["+userid+"]"
		//		+ ",exName=["+exName+"],exValue=["+exValue+"]");
		
		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.removeExternalFieldValueFromUser(userid,exName,exValue);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			//e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		//System.out.println("changeUserExFieldValue[userid:" + userid
		//	+ ",exName:" + exName + ",exValue:" + exValue + "]");
		return returnFlag;
	}

	/* **************************************************************** */
	/* ***************************** UPDATE *************************** */
	/* **************************************************************** */

	/**
	 * ���� ��й�ȣ, ���ο� ��й�ȣ�� �Է¹޾Ƽ� ���� ó��
	 * ��й�ȣ�� Ʋ����� APIException
	 * �����ڵ� = ErrorCode.ID_NOT_FOUND
	 * �޽��� : userId [xxx] is not exist!!
	 */
	public boolean changePassword(String userid, String oldPw, String newPw)
	throws Exception {
		//System.out.println("changePassword() -- userid ="+userid);
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.changePassword(userid, oldPw, newPw);
			returnFlag = true;
		} catch (APIException e) {
			//throw e;
			
		}
		/*
		System.out.println("changePassword:[" + userid + ":" + oldPw + ":"
			+ newPw + ":" + returnFlag + "]");
		*/
		return returnFlag;
	}

	/**
	 * ���� ��й�ȣ, ���ο� ��й�ȣ�� �Է¹޾Ƽ� ���� ó��
	 * ����ڰ� �������� �ʴٸ� return false
	 */
	public boolean changePasswordByAdmin(String userid, String newPw)
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		
		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.changePasswordByAdmin(userid, newPw);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
		} catch (APIException e) {
			//throw e;
			
		}
		/*
		System.out.println("changePasswordByAdmin:[" + userid + ":"
			+ newPw + ":" + returnFlag + "]");
		*/
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴ´ٸ� return false
	 * �����Ѵٸ� return true
	 */
	public boolean changeUserInfo(String userid, String enable, String name,
	String email, String passwd) throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.changeUserInfo(userid, enable, name, email, passwd);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			//e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		/*
		System.out.println("changeUserInfo[userid:" + userid + ",enable:"
			+ enable + ",name:" + name + ",email:" + email
			+ ",passwd:" + passwd + "]");
		*/
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴ´ٸ� return false
	 * �����Ѵٸ� return true
	 * NXUserInfo.USER_ID, ENABLE, NAME, EMAIL, PASSWORD, START_VALID, END_VALID
	 * 0, 1, 2, 3, 4, 5, 6 (0�� ��� �Ұ���)
	 */
	public boolean changeUserInfo(String userid, int idx, String newValue)
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.changeUserInfo(userid, idx, newValue);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			//e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		//System.out.println("changeUserInfo[userid:" + userid + ",idx:"
		//	+ idx + ",newValue:" + newValue + "]");
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return false
	 * �ܺΰ����� �������� �ʴٸ� return false
	 * �����Ѵٸ� return true
	 */
	public boolean changeUserAccount(String userid, String serviceName,
	String accountName, String accountPasswd)
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		NXExternalFieldSet nxefs = null;
		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.changeUserAccount(userid, serviceName, accountName,
				accountPasswd);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			//e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		/*
		System.out.println("changeUserAccount[userid:" + userid
			+ ",serviceName" + serviceName + ",accountName:" + accountName
			+ ",accountPasswd" + accountPasswd + "]");
		*/
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return false
	 * ��ϵ� �Ӽ��� �ƴ϶�� return false
	 * �����Ѵٸ� return true
	 */
	public boolean changeUserAttribute(String userid, String attrName,
	String attrValue_old, String attrValue_new)
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.changeUserAttribute(userid, attrName, 
				attrValue_old, attrValue_new);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
		} catch (APIException e) {
			//throw e;
		} catch (IllegalArgumentException e) {
			//throw e;
			// Attribute �� �������� �ʴٸ�
		}
		/*
		System.out.println("changeUserAttribute[userid:" + userid
			+ ",attrName:" + attrName + ",attrValue_old:"
			+ attrValue_old + ",attrValue_new:" + attrValue_new + "]");
		*/
		return returnFlag;
	}

	/**
	 * ����ڰ� �������� �ʴٸ� return false
	 * ��ϵ��� ���� Ȯ���ʵ��� return false
	 * �����Ѵٸ� return true
	 */
	public boolean changeUserExFieldValue(String userid, String exName,
	String exValue_old, String exValue_new) throws Exception {
		if(userid==null||userid.length()<1) return false;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		boolean returnFlag = false;

		try {
			context = getContext();
			//NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
		    	NXUserAPI userAPI = new NXUserAPI(context);

			userAPI.changeUserExternalFieldValue(userid, exName, 
				exValue_old, exValue_new);
			returnFlag = true;
		} catch (EmptyResultException e) {
	 		// ����ڰ� �������� ����
			
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		/*
		System.out.println("changeUserExFieldValue[userid:" + userid
			+ ",exName:" + exName + ",exValue_old:" + exValue_old
			+ ",exValue_new:" + exValue_new + "]");
		*/
		return returnFlag;
	}

	/* **************************************************************** */
	/* ************************** ATTRIBUTE *************************** */
	/* **************************************************************** */
	
	/**
     * ����ڰ� �������� �ʴٸ� return false
     * ��ϵ��� ���� Ȯ���ʵ��� return false
     * �����Ѵٸ� return true
     * �н����� 5ȸ�̻� ������ SSO DB PW_CNT '0'���� ������Ʈ 
     */
	
    public boolean changeUserExFieldValue2(String userid, String exName, String exValue_new) throws Exception {
         if(userid==null||userid.length()<1) return false;
         //NXContext context = new NXContext(nd_url);
         NXContext context = null;

         boolean returnFlag = false;

         try {
                  context = getContext();
                  //NXNLSAPI nxNLSAPI = new NXNLSAPI(context);
                  NXUserAPI userAPI = new NXUserAPI(context);

                  //userAPI.changeUserExternalFieldValue(userid, exName, 
                  //       exValue_old, exValue_new);
                  userAPI.changeUserExternalFieldValue(userid, exName, exValue_new);

                  returnFlag = true;
         } catch (EmptyResultException e) {
                  // ����ڰ� �������� ����
                  
         } catch (APIException e) {
                  //throw e;
                  
         } catch (IllegalArgumentException e) {
                  //throw e;
                  
         }
         //System.out.println("changeUserExFieldValue[userid:" + userid
         //       + ",exName:" + exName + ",exValue_old:" + exValue_old
         //       + ",exValue_new:" + exValue_new + "]");
         return returnFlag;
    }

	/**
	 * Attribute Value �˻�
	 */
	public boolean containsAttributeValue(String attrName, String attrValue)
	throws Exception {
		//System.out.println("containsAttributeValue()##########attrName="+attrName);
		//System.out.println("containsAttributeValue()##########attrValue="+attrValue);
		
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();
		
		NXAttributeAPI nxaa = NXAttributeAPI.getInstance(context);
		NXAttributeTypeSet nxats = null;
 		NXAttributePath nxap = null;
		boolean returnFlag = false;

		try {
			nxats = nxaa.getAllAttributes();
			
			nxap = new NXAttributePath(attrValue);
			returnFlag = nxats.containsValue(attrName, nxap); 
			//System.out.println("containsAttributeValue()##########returnFlag="+returnFlag);
			/*
			nxats = nxaa.getAllAttributes();
			returnFlag = nxats.containsValue(attrName, attrValue); 
			*/
		} catch (EmptyResultException e) {
			
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		return returnFlag;
	}

	/**
	 * Attribute Value ����
	 * Reference : partition type
	 * ���� : partition type
	 * ���� : tree type
	 * ���� : partition type
	 * ��å : partition type
	 */	 
	public boolean addAttributeValue(String attrName, String attrValue)
	throws Exception {
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();

		NXAttributeAPI nxaa = NXAttributeAPI.getInstance(context);
		NXAttributeTypeSet nxats = null;
 		NXAttributePath nxap = null;
		boolean returnFlag = false;
		List attrList = null;
		String attrToken = null;
		String attrData = "";
		boolean typeFlag = false;
		
		// check attribute type
		for (int i = 0, j = attr_name.length; i < j; i++) {
			if (attr_name[i].equals(attrName)) {
				typeFlag = attr_type[i];
				break;
			}
		}
		if (typeFlag) {
			if (attrValue.indexOf("/") != -1) {
				throw new IllegalArgumentException(attrName
					+ " is partition type. " + attrValue + " is tree type");
			}
		}

		try {
			nxats = nxaa.getAllAttributes();
			nxap = new NXAttributePath(attrValue);

			attrList = nxap.getTokens();

			if (typeFlag) {
				nxaa.addAttributeValue(attrName, attrValue);
			} else {
				for (int i = 0, j = attrList.size(); i < j; i++) {
					attrToken = (String) attrList.get(i);
					attrData = attrData + "/" + attrToken;
					returnFlag = nxats.containsValue(attrName,
							new NXAttributePath(attrData));
					if (returnFlag) {
						continue;
					} else {
						nxaa.addAttributeValue(attrName, attrData);
					}
				}
			}
			returnFlag = true;
		} catch (EmptyResultException e) {
			
		} catch (APIException e) {
			returnFlag = false;
		} catch (IllegalArgumentException e) {
			
		}
		//System.out.println("addAttributeValue[attrName:" + attrName
		//	+ ",attrValue:" + attrValue + "]");
		return returnFlag;
	}


	// EAM All Roles ��������
	/**
	 * getRoles
	 */
	public List getRoles()
	throws Exception {		
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();

		NXRole roleAPI = new NXRole(context);
		List roleList = null;
		//System.out.println("=========== ALL ROLES ==========");
		try {
			roleList = roleAPI.getRoles();
			for (Iterator i = roleList.iterator(); i.hasNext(); ) {
				final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
				//System.out.println(roleinfo);
				//System.out.println(roleinfo.getRoleName());	
			}
		} catch (APIException apie) {
			//System.out.println("��� ������ ����� �������µ� �����߽��ϴ�. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}	
		return	roleList;	
	}
	
	/**
	 * getRole
	 */
	public List getRole(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();

		NXRole roleAPI = new NXRole(context);
		List roleList = null;
		try {
			roleList = roleAPI.getRoles(userid);
			if (roleList.size() == 0) {
				// userid�� �ش��ϴ� ����ڰ� ���� ���� �ְ�,
				// ��¥�� �ش��ϴ� ������ �ϳ��� ���� ���� �ִ�.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println("[config.jsp]"+roleinfo);
					//System.out.println("[config.jsp]"+roleinfo.getRoleName());	
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]�� ������ ����� �������µ� �����߽��ϴ�. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}
		return	roleList;
	}
	
	//1. �����Ҵ�� ����
	/**
	 * getDirectlyAssignedRoles
	 */
	public List getDirectlyAssignedRoles(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();

		NXRole roleAPI = new NXRole(context);
		List roleList = null;
				
		//System.out.println("\n=========== �����Ҵ�� ���� " 
		//		+ userid + " ==========");
		try {
			roleList = roleAPI.getDirectlyAssignedRoles(userid);
			if (roleList.size() == 0) {
				// userid�� �ش��ϴ� ����ڰ� ���� ���� �ְ�,
				// ��¥�� �ش��ϴ� ������ �ϳ��� ���� ���� �ִ�.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println(roleinfo);
					//System.out.println(roleinfo.getRoleName());	
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]�� �����Ҵ�� ������ ����� �������µ� �����߽��ϴ�. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}
		return	roleList;
	}
	
	//2. �����Ҵ�� ������ �ڽ�
	/**
	 * getDerivedDirectlyAssignedRoles
	 */
	public List getDerivedDirectlyAssignedRoles(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();

		NXRole roleAPI = new NXRole(context);
		List roleList = null;		
		//2. �����Ҵ�� ������ �ڽ�
		//System.out.println("\n=========== �����Ҵ�� ������ �������� " 
		//		+ userid + " ==========");
		try {
			roleList = roleAPI.getDerivedDirectlyAssignedRoles(userid);
			if (roleList.size() == 0) {
				// userid�� �ش��ϴ� ����ڰ� ���� ���� �ְ�,
				// ��¥�� �ش��ϴ� ������ �ϳ��� ���� ���� �ִ�.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println(roleinfo);	
					//System.out.println(roleinfo.getRoleName());				
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]�� �����Ҵ�� ������ �ڽ� ����� �������µ� �����߽��ϴ�. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR")+ "]");
		}
		return	roleList;
	}
	
	
	//3. �°ݵǾ� �Ҵ�� ����
	/**
	 * getPromotedRoles
	 */
	public List getPromotedRoles(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();

		NXRole roleAPI = new NXRole(context);
		List roleList = null;		
		//System.out.println("\n=========== �Ӽ��°����� �Ҵ�� ���� " + userid + 
		//		" ==========");
		try {
			roleList = roleAPI.getPromotedRoles(userid);
			if (roleList.size() == 0) {
				// userid�� �ش��ϴ� ����ڰ� ���� ���� �ְ�,
				// ��¥�� �ش��ϴ� ������ �ϳ��� ���� ���� �ִ�.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println(roleinfo);
					//System.out.println(roleinfo.getRoleName());	
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]�� �°��Ҵ�� ������ �������µ� �����߽��ϴ�. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}
		
		return	roleList;
	}

	//4. �°ݵǾ� �Ҵ�� ������ �ڽ�
	/**
	 * getDerivedPromotedRoles
	 */
	public List getDerivedPromotedRoles(String userid)
	throws Exception {
		if(userid==null||userid.length()<1) return null;
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();

		NXRole roleAPI = new NXRole(context);
		List roleList = null;		
		//System.out.println("\n=========== �Ӽ��°����� �Ҵ�� ������ �������� " 
		//		+ userid + " ==========");
		try {
			roleList = roleAPI.getDerivedPromotedRoles(userid);
			if (roleList.size() == 0) {
				// userid�� �ش��ϴ� ����ڰ� ���� ���� �ְ�,
				// ��¥�� �ش��ϴ� ������ �ϳ��� ���� ���� �ִ�.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println(roleinfo);
					//System.out.println(roleinfo.getRoleName());	
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]�� �°��Ҵ�� ������ �ڽ��� �������µ� �����߽��ϴ�. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}
		return	roleList;
	}
		
	//5. ����ڿ��� Ư�� ���� �Ҵ��ϱ�
	/**
	 * assignUser
	 */
	public boolean assignUser(String userid, String rolename)
	throws Exception {
		//System.out.println("userid="+userid+",rolename="+rolename);
		if(userid==null||userid.length()<1) return false;
		if(rolename==null||rolename.length()<1) return false;
		
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();

		NXRole roleAPI = new NXRole(context);
		boolean setRoleFlag = false;		
		//System.out.println("\n=========== ����ڿ��� Ư�� ���� �Ҵ��ϱ� " 
		//		+ userid + " ==========");
		try {
			setRoleFlag=roleAPI.assignUser(rolename, userid);
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]�� ������ �߰��ϴµ� ���� �߽��ϴ�. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR")+ "]");
		}
		return setRoleFlag;
	}
	
	//6. ����ڿ��� Ư�� ���� �����ϱ�
	/**
	 * deAssignUser
	 */
	public boolean deAssignUser(String userid, String rolename)
	throws Exception {
		if(userid==null||userid.length()<1) return false;
		if(rolename==null||rolename.length()<1) return false;
		
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;
		context = getContext();

		NXRole roleAPI = new NXRole(context);

		boolean setRoleFlag = false;		
		//System.out.println("\n=========== ����ڿ��� Ư�� ���� �����ϱ�"+userid+" ==========");
		try {
			setRoleFlag=roleAPI.deAssignUser(rolename, userid);			
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]�� ������ �����ϴµ� �����߽��ϴ�. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}
		return setRoleFlag;
	}
	
	// by Kang Method
	/**
	 * getAttributeLists
	 */
	public Properties getAttributeLists()throws Exception 
	{
		/****************
		// by LDAP
		Class.forName("com.octetstring.jdbcLdap.sql.JdbcLdapDriver");
			
		String ldapConnectString ="jdbc:ldap://kalsse.knoc.co.kr:59812/ou=eamConfig,dc=kang,dc=com?SEARCH_SCOPE:=oneLevelScope";
		
		java.sql.Connection con;
		con = DriverManager.getConnection(ldapConnectString, "cn=Directory Manager", "22222222");
		
		Properties prop = null;
		
	    	Statement stmt = null;
		ResultSet rs = null;
		
		String eamAttributeTypeId = null;
		String eamDescription = null;				
		
		// Query List
		//String SQL = "SELECT * FROM ou=users WHERE (uid=t*) AND (birthday=19*)";	
		String SQL = "SELECT * FROM ou=attribute WHERE (&(objectclass=eamAttributeType)(eamAttributeTypeId=*))";			
		*************/
		
		Properties prop = null;
		String eamAttributeId = null;
		String eamDescription = null;
		
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		Connection conn = DriverManager.getConnection(sDB_URL, sDB_ID, sDB_PW);
		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		
		String sql = "select attributeid,description from attrtype where attributeid != '000000000000000000'";
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			prop = new Properties();
			while(rs.next()) {		
				eamAttributeId = rs.getString("AttributeId");
				eamDescription = rs.getString("Description");
				prop.setProperty(eamDescription, eamDescription);	
			}
		} catch (SQLException se) {			
			//System.out.println("SQLException:"+se.getMessage());
					
		} catch (Exception e) {
			//System.out.println("Exception::"+e.getMessage());
						
		} finally {
			if (rs!=null)	rs.close();
			if (stmt!=null) stmt.close();
			if (conn!=null)	conn.close();
		}					
		return prop;
	}	
	
	// by Kang Method
	/**
	 * getServiceNameLists
	 */
	public Properties getServiceNameLists() throws Exception 
	{
		/**********************
		// LDAP ���� ����Ʈ ��������
		Class.forName("com.octetstring.jdbcLdap.sql.JdbcLdapDriver");
		
		String ldapConnectString ="jdbc:ldap://kalsse.knoc.co.kr:59812/ou=eamConfig,dc=kang,dc=com?SEARCH_SCOPE:=oneLevelScope";
		
		java.sql.Connection con;
		con = DriverManager.getConnection(ldapConnectString, "cn=Directory Manager", "22222222");
		
		Properties prop = null;
		
	    	java.sql.Statement stmt = null;
		java.sql.ResultSet rs = null;
		
		String eamServiceNameId = null;				
		
		// Query List
		String SQL = "SELECT * FROM ou=application WHERE (&(objectclass=eamApplication)(eamServiceNameId=*))";
		********************/
					
		Properties prop = null;
		String eamServiceNameId = null;		
		
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		Connection conn = DriverManager.getConnection(sDB_URL, sDB_ID, sDB_PW);
		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		
		String sql = "select servicename from application";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			prop = new Properties();
			while(rs.next()) {		
				eamServiceNameId = rs.getString("ServiceName");
				prop.setProperty(eamServiceNameId, eamServiceNameId);	
			}
		} catch (SQLException se) {			
			//System.out.println("SQLException:"+se.getMessage());
					
		} catch (Exception e) {
			//System.out.println("Exception::"+e.getMessage());
					
		} finally {
			if (rs!=null)	rs.close();
			if (stmt!=null) stmt.close();
			if (conn!=null)	conn.close();
		}					
		return prop;
	}
	
	// �Ӽ� �� ��� ��������
	/**
	 * getAttributeValueLists
	 */
	public Properties getAttributeValueLists()throws Exception 
	{
		/********************
		Class.forName("com.octetstring.jdbcLdap.sql.JdbcLdapDriver");
		
		String ldapConnectString ="jdbc:ldap://kalsse.knoc.co.kr:59812/ou=eamConfig,dc=kang,dc=com?SEARCH_SCOPE:=oneLevelScope";
		
		java.sql.Connection con;
		con = DriverManager.getConnection(ldapConnectString, "cn=Directory Manager", "22222222");
		
		Properties prop = null;
		
	    	Statement stmt = null;
		ResultSet rs = null;
		
		String eamAttributeValueId = null;
		String eamDescription = null;				
		String eamSuperAttrValueRef = null;
		// Query List
		//String SQL = "SELECT * FROM ou=attribute WHERE (&(objectclass=eamAttributeType)(eamAttributeTypeId=*))";
		String SQL = "SELECT * FROM ou=attribute WHERE (&(objectclass=eamAttributeValue)(eamAttributeValueId=*))";			
		*************************/
					
		Properties prop = null;
		String eamAttributeValueId = null;
		String eamDescription = null;			
		
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		Connection conn = DriverManager.getConnection(sDB_URL, sDB_ID, sDB_PW);
//System.out.println("getConnection sDB_URL :: " + sDB_URL);

		Statement stmt = conn.createStatement();
		ResultSet rs = null;
		
		String sql = "select attributeid,description from attrtype";
		
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(sql);
			
			prop = new Properties();
			while(rs.next()) {		
				eamAttributeValueId = rs.getString("eamAttributeValueId");
				eamDescription = rs.getString("eamDescription");
				prop.setProperty(eamDescription, eamDescription);	
			}			
		} catch (SQLException se) {			
			//System.out.println("SQLException:"+se.getMessage());
					
		} catch (Exception e) {
			//System.out.println("Exception::"+e.getMessage());
						
		} finally {
			if (rs!=null)	rs.close();
			if (stmt!=null) stmt.close();
			if (conn!=null)	conn.close();
		}	
						
		return prop;
	}			
	
	
	/* **************************************************************** */
	/* * *************************** Cookie *************************** */
	/* **************************************************************** */
	
	/**
	 * saveLocalCookie
	 */
	public void saveLocalCookie(HttpServletResponse response,
		String ckName, String ckValue) {
		Cookie ck = new Cookie(ckName, ckValue);
		ck.setDomain(".bccard.com");
		ck.setPath("/");
		ck.setMaxAge(60*60*24*365*10);
		response.addCookie(ck);
	}
	
	/**
	 * saveCookie
	 */
	public void saveCookie(HttpServletResponse response,
		String ckName, String ckValue) {
		Cookie ck = new Cookie(ckName, ckValue);
		ck.setDomain(".bccard.com");
		ck.setPath("/");
		response.addCookie(ck);
	}

	/**
	 * saveTodayCookie
	 */
	public void saveTodayCookie(HttpServletResponse response,
	String ckName, String ckValue) {
			java.util.Date todayDate = new java.util.Date();
			Calendar cal = Calendar.getInstance();
			int today_time = cal.get(Calendar.HOUR_OF_DAY);
			int today_remain = 0;
			
			today_remain = 24 - today_time;
			today_remain = today_remain * 60 * 60;
		
		Cookie ck = new Cookie(ckName, ckValue);
		ck.setDomain(".bccard.com");
		ck.setPath("/");
			ck.setMaxAge(today_remain);
		response.addCookie(ck);
	
	}
	
	/**
	 * removeCookie
	 */
	public void removeCookie(HttpServletResponse response, String ckName) {
		Cookie ck = new Cookie(ckName, "");
		ck.setDomain(".bccard.com");
		ck.setPath("/");
		ck.setMaxAge(0);
		response.addCookie(ck);
	}
	
	/**
	 * getCookieData
	 */
	public String getCookieData(HttpServletRequest request, String ckName) {
		Cookie[] ckA = request.getCookies();
		Cookie ck = null;
		String ckValue = null;
		if (ckA != null) {
		    for (int i = 0; i < ckA.length; i++) {
		        ck = ckA[i];
		        if (ck.getName().equals(ckName)) {
		            ckValue = ck.getValue();
		            break;
		        }
		    }
		}
		return ckValue;
	}

	/**
	 * getAcceptLang
	 */
	public String getAcceptLang(HttpServletRequest request) {
	    	String acceptLang = null;
	    	String returnData = null;
	    	String[] langA = {"ko", "EUC-KR", "zh-cn", "zh-hk",
				"zh-sg", "zh-tw", "GB2312"};
	    	String[] codeA = {"0", "0", "2", "2", "2", "2", "2"};
	    	acceptLang = request.getHeader("Accept-Language");
		
	    	for (int i = 0; i < langA.length; i++) {
	        	if (langA[i].equals(acceptLang)) {
	            	returnData = codeA[i];
					break;
	        	}
	    	}

		return returnData;
	}

}
