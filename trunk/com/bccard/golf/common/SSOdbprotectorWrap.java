/***************************************************************************************************
*   클래스명  : SSOdbprotectorWrap
*   작성자    : 
*   내용      : 
*   적용범위  : bccard.com
*   작성일자  : 2005.7.21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
	
//	 속성 정의	
	private String[] attr_name
		= {"부서", "직위", "직급"}; 
	private boolean[] attr_type
		= {true, false, false};
	
	// ORACLE DB 개발기
	static private String sDB_URL = "";
	static private String sDB_ID = "";
	static private String sDB_PW = "";

	static{

		String ip = "";
		String dv_was_1st = "";	//개발기 WAS IP
		String dr_was_1st = "";	//DR 1호기 WAS IP
		String dr_was_2nd = "";	//DR 2호기 WAS IP
		String rl_was_1st = "";	//운영 1호기 WAS IP
		String rl_was_2nd = "";	//운경 2호기 WAS IP

		String dv_sso_1st = ""; //개발 SSO URL
		String dr_sso_1st = ""; //DR 1호기 SSO URL
		String dr_sso_2nd = ""; //DR 2호기 SSO URL
		String rl_sso_1st = ""; //운영 1호기 SSO URL
		String rl_sso_2nd = ""; //운영 2호기 SSO URL
		
		String dv_ora_1st = ""; //개발 DB접속		"jdbc:oracle:thin:@211.181.255.91:1521:WEBDEV";
		String dr_ora_1st = ""; //DR DB접속		"jdbc:oracle:thin:@211.181.255.56:1521:WEB01";
		String rl_ora_1st = ""; //운영 DB접속		"jdbc:oracle:thin:@61.98.69.56:1521:WEB01";
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

		if ( dv_was_1st.equals(ip) ) {										//개발기
			
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

		} else {  // 운영기
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
	
	// 2005.12.07 SSO 세션체크 패치모듈 - kyyou
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
			* 넥세스 쿠키가 valid한지를 검사하여 로그인 상태를 검사한다. 
			* 
			* @param request                 Servlet Request
			* @param response                Servlet Response
			* @param sessionReuseTime        Session Reuse Time을 지정 (0이면10초)
			* @param sessionTime             Session Time을 지정 (0이면 1200초)
			* @return 로그인상태 코드 
			*/
			retCode = authChecker.readNexessCookie(request, response, 0, 0);
		} catch (com.initech.eam.base.APIException apie) {
			
		} catch (IllegalArgumentException ie) {
			
		} catch (Exception e) {
			
		}
		return retCode;	
	   }
	/*  리턴 코드 설명
	* 인증 정보에 대한 유효성 검증 및 세션 갱신 처리을 수행한다.
	* Vector Value  
	* ULAT : 로그인시간 (갱신된 정보를 다시 받음)
	* RESULT : 인증 정보 검증 결과
	*	-. RESULT = 0 : 정상적 로그인 절차에 따른 리턴값, 그외는 오류코드
	*	-. RESULT = 100 : 익명사용자 (로그인 되어있지 않은 경우)
	*	-. RESULT = 200 : 사용자 아이디가 없는 경우
	*	-. RESULT = 1000 : 꼭 필요한 쿠키중 일부가 없을때
	*	-. RESULT = 1001 : 세션 타임아웃(LAT가 session time을 벗어났을 경우)
	*	-. RESULT = 1002 : 쿠키값들은 다 존재하나 넥세스가 정상적으로 발급한 
        *                          쿠기가 아닐때(HMAC이 틀릴 경우)
	*	-. RESULT = 1004 : IP가 같지 않을 경우
	*/
	
	// 기존 함수 주석처리
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
	 * SSO ID 가 존재하는지 검사
	 * 1. true : 존재
	 * 2. false : 존재안함
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
	 * 사용자 기본정보 조회
	 * return : Properties
	 *			userid, email, enable, startvalid, endvalid, name,
	 *			lastpasswdchange
	 * 사용자가 존재하지 않다면 return null
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
	 		// 사용자가 존재하지 않거나 외부계정이 없음
		} catch (APIException e) {
			//throw e;
			
		}
		//System.out.println("getUserInfo:[" + prop + "]");
		return prop;
	}

	/**
	 * 전체 시스템 계정 정보 조회
	 * service name, account name, account password
	 * account name, account password 는 String[] 로 저장
	 * 사용자가 존재하지 않거나 외부계정이 없다면 return null
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

				/* TODO : 임시로 사용하는 것이니 꼭 지워 주세요 */
				/*
				prop.put("[" + serviceName + ":" + accountData[0]
					+ "." + accountData[1] + "]", "");
				*/
				/* TODO : 임시로 사용하는 것이니 꼭 지워 주세요 */

				prop.put(serviceName, accountData);
				//System.out.println("getUserAccounts[service name:"
				//+ account.getServiceName() + "], "
				//+ "[account name:" + account.getAccountName() + "], "
				//+ "[account password:" + account.getAccountPassword() + "]");
			}
		} catch (EmptyResultException e) {
	 		// 사용자가 존재하지 않거나 외부계정이 없음
		} catch (APIException e) {
			//throw e;
			//e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		return prop;
	}

	/**
	 * 시스템 계정 정보 조회
	 * account name, account password
	 * account name, account password 는 String[] 로 저장
	 * 사용자가 존재하지 않거나 외부계정이 없다면 return null
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
	 		// 사용자가 존재하지 않거나 외부계정이 없음
		} catch (APIException e) {
			//throw e;
	 		// 사용자가 존재하지 않거나 외부계정이 없음
			// TODO : 버그
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		return accountData;
	}

	/**
	 * 사용자가 존재하지 않다면 return null
	 * Reference=LG CNS, 조직=LCD 2공장, 직위=감독, 직렬=LPL, 직책=부장
	 * TODO : 조회횟수 검사 필요
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
	 		// 사용자가 존재하지 않음
		} catch (APIException e) {
			//throw e;
			
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		return prop;
	}

	/**
	 * 사용자가 존재하지 않다면 return null
	 * 속성을 갖지 않는다면 IllegalArgumentException
	 * multi value 는 없음
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
	 		// 사용자가 존재하지 않음
			e.printStackTrace();
		} catch (APIException e) {
			//throw e;
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			//throw e;
	 		// 속성을 갖지 않는다면 IllegalArgumentException
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
	 * 사용자 확장정보 조회
	 * return : Properties
	 * 사용자가 존재하지 않거나 확장필드가 없다면 return null
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
					//System.out.println("확장 필드 " + nxef.getName()
					//	+ " : " + nxef.getValue());
				}
			} 
		} catch (EmptyResultException e) {
	 		// 사용자가 존재하지 않거나 확장필드가 없음
		} catch (APIException e) {
			//throw e;
			
		}
		//System.out.println("getUserExFields:[" + prop + "]");
		return prop;
	}

	/**
	 * 사용자의 특정 확장 필드 정보 조회
	 * return : String
	 * 사용자가 존재하지 않거나 확장필드가 없다면 return null
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
	 		// 사용자가 존재하지 않거나 확장필드가 없음
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
	 * SSO ID 배열 반환
	 */ 
	public String[] getUserListByAttribute(String attrName, String attrValue)
	throws Exception {
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		NXUserInfo[] userInfoList = null;
		int cnt = 0;
		String[] returnData = null;

		// 임시 사용initialize
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
	 		// 사용자가 존재하지 않거나 확장필드가 없음
			
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
	 * SSO ID 배열 반환
	 */	 
	public String[] getUserListByExField(String exName, String exValue)
	throws Exception {
		//NXContext context = new NXContext(nd_url);
		NXContext context = null;

		NXUserInfo[] userInfoList = null;
		int cnt = 0;
		String[] returnData = null;

		// 임시 사용
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
	 		// 사용자가 존재하지 않거나 확장필드가 없음
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
	 * 사용자가 존재한다면 return false
	 * 성공한다면 return true
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
		
		//System.out.println("확장필드>> prop:["+prop+"]");
		
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
				
				/*********** 확장필드값이 NOT NULL 인 경우 사용
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
				// 확장필드가 있는경우.
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
	 		// 사용자가 존재하지 않음
			
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
	 * 사용자가 존재하지 않다면 return false
	 * 외부계정이 이미 존재한다면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
			
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
	 * 사용자가 존재하지 않다면 return false
	 * 이미 등록된 속성이라면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
	 		
		} catch (APIException e) {
			//throw e;
			
			// 이미 할당된 경우
		} catch (IllegalArgumentException e) {
			//throw e;
			
		}
		//System.out.println("addAttributeToUser[userid:" + userid
		//	+ ",attrName:" + attrName + ",attrValue:" + attrValue + "]");
		return returnFlag;
	}

	/**
	 * 사용자가 존재하지 않다면 return false
	 * 이미 등록된 확장필드라면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
	 		
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
	 * 사용자가 존재하지 않는다면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
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
	 * 사용자가 존재하지 않다면 return false
	 * 외부계정이 존재하지 않다면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
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
	 * 사용자가 존재하지 않다면 return false
	 * 등록된 속성이 아니라면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
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
	 * 사용자가 존재하지 않다면 return false
	 * 등록되지 않은 확장필드라면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
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
	 * 이전 비밀번호, 새로운 비밀번호를 입력받아서 갱신 처리
	 * 비밀번호가 틀린경우 APIException
	 * 에러코드 = ErrorCode.ID_NOT_FOUND
	 * 메시지 : userId [xxx] is not exist!!
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
	 * 이전 비밀번호, 새로운 비밀번호를 입력받아서 갱신 처리
	 * 사용자가 존재하지 않다면 return false
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
	 		// 사용자가 존재하지 않음
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
	 * 사용자가 존재하지 않는다면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
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
	 * 사용자가 존재하지 않는다면 return false
	 * 성공한다면 return true
	 * NXUserInfo.USER_ID, ENABLE, NAME, EMAIL, PASSWORD, START_VALID, END_VALID
	 * 0, 1, 2, 3, 4, 5, 6 (0은 사용 불가능)
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
	 		// 사용자가 존재하지 않음
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
	 * 사용자가 존재하지 않다면 return false
	 * 외부계정이 존재하지 않다면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
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
	 * 사용자가 존재하지 않다면 return false
	 * 등록된 속성이 아니라면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
		} catch (APIException e) {
			//throw e;
		} catch (IllegalArgumentException e) {
			//throw e;
			// Attribute 가 존재하지 않다면
		}
		/*
		System.out.println("changeUserAttribute[userid:" + userid
			+ ",attrName:" + attrName + ",attrValue_old:"
			+ attrValue_old + ",attrValue_new:" + attrValue_new + "]");
		*/
		return returnFlag;
	}

	/**
	 * 사용자가 존재하지 않다면 return false
	 * 등록되지 않은 확장필드라면 return false
	 * 성공한다면 return true
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
	 		// 사용자가 존재하지 않음
			
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
     * 사용자가 존재하지 않다면 return false
     * 등록되지 않은 확장필드라면 return false
     * 성공한다면 return true
     * 패스워드 5회이상 오류시 SSO DB PW_CNT '0'으로 업데이트 
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
                  // 사용자가 존재하지 않음
                  
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
	 * Attribute Value 검사
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
	 * Attribute Value 생성
	 * Reference : partition type
	 * 조직 : partition type
	 * 직렬 : tree type
	 * 직위 : partition type
	 * 직책 : partition type
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


	// EAM All Roles 가져오기
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
			//System.out.println("모든 역할의 목록을 가져오는데 실패했습니다. ["
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
				// userid에 해당하는 사용자가 없을 수도 있고,
				// 진짜로 해당하는 역할이 하나도 없을 수도 있다.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println("[config.jsp]"+roleinfo);
					//System.out.println("[config.jsp]"+roleinfo.getRoleName());	
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]의 역할의 목록을 가져오는데 실패했습니다. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}
		return	roleList;
	}
	
	//1. 직접할당된 역할
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
				
		//System.out.println("\n=========== 직접할당된 역할 " 
		//		+ userid + " ==========");
		try {
			roleList = roleAPI.getDirectlyAssignedRoles(userid);
			if (roleList.size() == 0) {
				// userid에 해당하는 사용자가 없을 수도 있고,
				// 진짜로 해당하는 역할이 하나도 없을 수도 있다.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println(roleinfo);
					//System.out.println(roleinfo.getRoleName());	
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]의 직접할당된 역할의 목록을 가져오는데 실패했습니다. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}
		return	roleList;
	}
	
	//2. 직접할당된 역할의 자식
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
		//2. 직접할당된 역할의 자식
		//System.out.println("\n=========== 직접할당된 역할의 하위역할 " 
		//		+ userid + " ==========");
		try {
			roleList = roleAPI.getDerivedDirectlyAssignedRoles(userid);
			if (roleList.size() == 0) {
				// userid에 해당하는 사용자가 없을 수도 있고,
				// 진짜로 해당하는 역할이 하나도 없을 수도 있다.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println(roleinfo);	
					//System.out.println(roleinfo.getRoleName());				
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]의 직접할당된 역할의 자식 목록을 가져오는데 실패했습니다. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR")+ "]");
		}
		return	roleList;
	}
	
	
	//3. 승격되어 할당된 역할
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
		//System.out.println("\n=========== 속성승격으로 할당된 역할 " + userid + 
		//		" ==========");
		try {
			roleList = roleAPI.getPromotedRoles(userid);
			if (roleList.size() == 0) {
				// userid에 해당하는 사용자가 없을 수도 있고,
				// 진짜로 해당하는 역할이 하나도 없을 수도 있다.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println(roleinfo);
					//System.out.println(roleinfo.getRoleName());	
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]의 승격할당된 역할을 가져오는데 실패했습니다. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}
		
		return	roleList;
	}

	//4. 승격되어 할당된 역할의 자식
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
		//System.out.println("\n=========== 속성승격으로 할당된 역할의 하위역할 " 
		//		+ userid + " ==========");
		try {
			roleList = roleAPI.getDerivedPromotedRoles(userid);
			if (roleList.size() == 0) {
				// userid에 해당하는 사용자가 없을 수도 있고,
				// 진짜로 해당하는 역할이 하나도 없을 수도 있다.
			} else {
				for (Iterator i = roleList.iterator(); i.hasNext(); ) {
					final NXRoleInfo roleinfo = (NXRoleInfo) i.next();
					//System.out.println(roleinfo);
					//System.out.println(roleinfo.getRoleName());	
				}
			}
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]의 승격할당된 역할의 자식을 가져오는데 실패했습니다. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR") + "]");
		}
		return	roleList;
	}
		
	//5. 사용자에게 특정 역할 할당하기
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
		//System.out.println("\n=========== 사용자에게 특정 역할 할당하기 " 
		//		+ userid + " ==========");
		try {
			setRoleFlag=roleAPI.assignUser(rolename, userid);
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]의 역할을 추가하는데 실패 했습니다. ["
			//	+ apie.getCode() + "] [" + new String(apie.getMessage().getBytes("ISO8859-1"), "EUC-KR")+ "]");
		}
		return setRoleFlag;
	}
	
	//6. 사용자에게 특정 역할 해제하기
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
		//System.out.println("\n=========== 사용자에게 특정 역할 해제하기"+userid+" ==========");
		try {
			setRoleFlag=roleAPI.deAssignUser(rolename, userid);			
		} catch (APIException apie) {
			//System.out.println("[" + userid
			//	+ "]의 역할을 해제하는데 실패했습니다. ["
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
		// LDAP 서비스 리스트 가져오기
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
	
	// 속성 값 모두 가져오기
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
