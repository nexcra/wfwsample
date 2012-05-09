/***************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*  클래스명		:   GolfCtrlServ
*  작 성 자		:   M4
*  내    용		:   사용자 컨트롤러 서블릿 
*  적용범위		:   golfLoung
*  작성일자		:   2006.12.27 
************************** 수정이력 ***************************************************
* 일자			버전		작성자		변경사항 
****************************************************************************************/
package com.bccard.golf.action.login;

import java.io.IOException;
import java.net.InetAddress;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfCtrlServ;
import com.bccard.waf.action.ControllerServlet;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.initech.eam.nls.CookieManager;
import com.initech.iam.loadbalancer.algorithm.ActiveActiveStrategy;
import com.initech.iam.loadbalancer.algorithm.HashAlgorithm;
import com.initech.iam.loadbalancer.algorithm.IpHashFunction;
import com.initech.iam.loadbalancer.algorithm.RandomAlgorithm;
import com.initech.iam.loadbalancer.algorithm.RoundRobinAlgorithm;
import com.initech.iam.loadbalancer.health.HealthChecker;
import com.initech.iam.loadbalancer.health.HeartBeatStorage;
import com.initech.iam.loadbalancer.health.LimitedHeartBeatStorage;
import com.initech.iam.loadbalancer.health.PeriodicHealthChecker;
import com.initech.iam.loadbalancer.server.ActiveServer;
import com.initech.iam.loadbalancer.server.Cluster;
import com.initech.iam.loadbalancer.server.LoadBalancer;
import com.initech.iam.loadbalancer.server.Server;
import com.initech.iam.loadbalancer.server.SimpleConnection;
import com.initech.iam.loadbalancer.server.SocketConnectionImpl;

/***************************************************************************************
 * 사용자 컨트롤러 서블릿 
 * @version 2006.12.27
 * @author  e4net
****************************************************************************************/
public class FrontLogout extends ControllerServlet {
	
	
	private static boolean isInitialized = false;

	static Server sso01;
	static Server sso02;
	static Cluster cluster;
	static HeartBeatStorage storage;
	static HealthChecker checker;
	static LoadBalancer hashBalancer;
	static LoadBalancer roundrobinBalancer;
	static LoadBalancer randomBalancer;

	private static String nls_url		= "";
	private static String nls_login_url	= "";
	private static String nls_login_url5	= "";
	private static String nls_logout_url	= "";
	private static String nls_error_url	= "";
	private static String nd_url			= "";
	private static String nd_url2		= "";
	private static String server_url		= "";
	private static String ascp_url		= "";
	private static String gubunIP		= "";
	
	
	/** ***************************************************************************
	 * 서블릿 init().
	 **************************************************************************** */
	public void init() throws ServletException {
		super.init();
		// SSO 사용시 개발기인지 / 운영기 인지를 확인한다.
		if(!isInitialized){
			checkServer();
			GolfCtrlServ.checkServerSSO(this.gubunIP);
			isInitialized = true;
		}
	}
	
	 /** ****************************************************************************
     * 서버 검사. Coded By PWT, 20070830
     * @param request      HttpServletRequest
     * @param response     HttpServletResponse
     **************************************************************************** */
	public String checkServer()
	{
		String serverip = "";  // 서버아이피
		String devip = "";	   // 개발기 ip 정보

		try {
			serverip = InetAddress.getLocalHost().getHostAddress();
		} catch(Throwable t) {}

		try {
			devip = AppConfig.getAppProperty("DV_WAS_1ST");
		} catch(Throwable t) {}

		//logger.debug("serverip:" + serverip);
		//logger.debug("devip:" + devip);
		
		if (devip.equals(serverip)) {  //개발기
			this.gubunIP = "dev";
		} else {	// 운영기
			this.gubunIP = "ser";
		}
		return this.gubunIP;
	} 
	
	/** ***************************************************************************
     * SSO 연결시 개발기와 운영기에 따라서 설정을 달리한다.
     * @param    개발기/운영기 구분을 가져온다.
     **************************************************************************** */
    public static void checkServerSSO(String gubunIP) {

		String ip = "";
		String dv_was_1st = "";	//개발기 WAS IP
		String dr_was_1st = "";	//DR 1호기 WAS IP
		String dr_was_2nd = "";//DR 2호기 WAS IP
		String rl_was_1st = "";	//운영 1호기 WAS IP
		String rl_was_2nd = "";	//운경 2호기 WAS IP

		String dv_sso_1st = ""; //개발 SSO URL
		String dr_sso_1st = ""; //DR 1호기 SSO URL
		String dr_sso_2nd = ""; //DR 2호기 SSO URL
		String rl_sso_1st = ""; //운영 1호기 SSO URL
		String rl_sso_2nd = ""; //운영 2호기 SSO URL

		String dv_host_domain = "";	//개발기 도메인 URL http://develop.bccard.com
		String dr_host_domain = ""; //DR
		String rl_host_domain = ""; //운영기 도메인 URL http://www.bccard.com:80

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
			//Appended By PWT 20070929
			dv_host_domain = AppConfig.getAppProperty("DV_HOST_DOMAIN");
			dr_host_domain = AppConfig.getAppProperty("DR_HOST_DOMAIN");
			rl_host_domain = AppConfig.getAppProperty("RL_HOST_DOMAIN");
			 

			// 로컬개발에서 테스트시에 호스트파일에 아이피 URL 세팅값으로 테스트할수 있다.
			//dr_host_domain = "http://shoprd.bccard.com";
			//rl_host_domain = "http://shoprd.bccard.com"; 
			
		} catch (java.io.IOException ignore) {}
		//System.out.println("******************************************dv_was_1st:  ("+dv_was_1st+")");
		//System.out.println("******************************************ip:  ("+ip+")");
		if ( dv_was_1st.equals(ip) ) {										//개발기
			//System.out.println("******************************************LOGIN1111");	
			 nls_url				= dv_sso_1st + ":9611"; 
			 
			 nls_login_url		= nls_url + "/nls3/cookieSignin.jsp?FORM=10";  // SSO로그인창
			 nls_login_url5		= nls_url + "/nls3/cookieSignin.jsp?FORM=10"; // long

			 nls_logout_url	= nls_url + "/nls3/ssologout.jsp";
			 nls_error_url		= nls_url + "/nls3/error_golf.jsp";
			 //nls_error_url		= nls_url + "/nls3/errorSignin.jsp";
			 //nd_url				= dv_sso_1st + ":5480/";
			 //nd_url2				= dv_sso_1st + ":5480/";
			 nd_url				= nls_url;
			 nd_url2				= nls_url;
			 
			 server_url			= dv_host_domain;
			 
			 if("211.181.255.40".equals(dv_was_1st)) 
			 {
				 ascp_url			= server_url + ":13300/app/golfloung/view/initech/sso/login_exec.jsp";
			 }
			 else
			 {
				 ascp_url			= server_url + ":7001/view/initech/sso/login_exec.jsp";
			 }
			 			
			
		} else if ( dr_was_1st.equals(ip) || dr_was_2nd.equals(ip) ) {		//DR
			//System.out.println("******************************************LOGIN2222");	
			sso01 = new ActiveServer(dr_sso_1st, new SimpleConnection(new SocketConnectionImpl()));
			sso02 = new ActiveServer(dr_sso_2nd, new SimpleConnection(new SocketConnectionImpl()));
			cluster = new Cluster();

			cluster.add(sso01);
			cluster.add(sso02);

			storage = new LimitedHeartBeatStorage(10);
			checker = new PeriodicHealthChecker(cluster, 30 * 1000);
			checker.setHeartBeatStorage(storage);
			hashBalancer = new LoadBalancer(storage);
			hashBalancer.setFailOverStrategy(new ActiveActiveStrategy());
			hashBalancer.setLoadBalancingAlgorithm(new HashAlgorithm(new IpHashFunction()));
			roundrobinBalancer = new LoadBalancer(storage);
			roundrobinBalancer.setFailOverStrategy(new ActiveActiveStrategy());
			roundrobinBalancer.setLoadBalancingAlgorithm(new RoundRobinAlgorithm());
			randomBalancer = new LoadBalancer(storage);
			randomBalancer.setFailOverStrategy(new ActiveActiveStrategy());
			randomBalancer.setLoadBalancingAlgorithm(new RandomAlgorithm());

			 nls_url		= dr_sso_1st + ":80";
			 nd_url			= dr_sso_1st + ":5480/";
			 nd_url2		= dr_sso_2nd + ":5480/";
			 server_url		= dr_host_domain;
			 nls_error_url	= nls_url	 + "/nls3/error_golf.jsp";
			 
			 nls_login_url		= nls_url + "/nls3/cookieSignin.jsp?FORM=10";  // SSO로그인창
			 nls_login_url5 	= nls_url	 + "/nls3/cookieSignin.jsp?FORM=10"; // long
			 //ascp_url		= server_url + "/app/card/view/initech/sso/login_exec.jsp";
			 ascp_url		= server_url + ":13300/app/golfloung/view/initech/sso/login_exec.jsp";		 

		} else {  // 운영기
			//System.out.println("******************************************LOGIN3333");	
			sso01 = new ActiveServer(rl_sso_1st, new SimpleConnection(new SocketConnectionImpl()));
			sso02 = new ActiveServer(rl_sso_2nd, new SimpleConnection(new SocketConnectionImpl()));
			cluster = new Cluster();

			// SSO 분기
			if (ip.equals(rl_was_1st)) {
				cluster.add(sso01);
				cluster.add(sso01);
				cluster.add(sso01);
				cluster.add(sso01);
			} else if (ip.equals(rl_was_2nd)) {
				cluster.add(sso02);
				cluster.add(sso02);
				cluster.add(sso02);
				cluster.add(sso02);
			}

			cluster.add(sso01);
			cluster.add(sso02);

			storage = new LimitedHeartBeatStorage(10);
			checker = new PeriodicHealthChecker(cluster, 30 * 1000);
			checker.setHeartBeatStorage(storage);
			hashBalancer = new LoadBalancer(storage);
			hashBalancer.setFailOverStrategy(new ActiveActiveStrategy());
			hashBalancer.setLoadBalancingAlgorithm(new HashAlgorithm(new IpHashFunction()));
			roundrobinBalancer = new LoadBalancer(storage);
			roundrobinBalancer.setFailOverStrategy(new ActiveActiveStrategy());
			roundrobinBalancer.setLoadBalancingAlgorithm(new RoundRobinAlgorithm());
			randomBalancer = new LoadBalancer(storage);
			randomBalancer.setFailOverStrategy(new ActiveActiveStrategy());
			randomBalancer.setLoadBalancingAlgorithm(new RandomAlgorithm());

			 nls_url		= rl_sso_1st + ":80";
			 nd_url			= rl_sso_1st + ":5480/";
			 nd_url2		= rl_sso_2nd + ":5480/";
			 server_url		= rl_host_domain;
			 nls_error_url	= nls_url	 + "/nls3/error_golf.jsp";
			 //nls_login_url5 = nls_url	 + "/nls3/cookieLogin5.jsp"; // long
			 nls_login_url		= nls_url + "/nls3/cookieSignin.jsp?FORM=10";  // SSO로그인창 
			 nls_login_url5 	= nls_url	 + "/nls3/cookieSignin.jsp?FORM=10"; // long
			 ascp_url		= server_url + "/app/golfloung/view/initech/sso/login_exec.jsp";
		}
    }
    
    /**
	 * Front logout   
	 * 
	 * @param context
	 * @param request
	 * @param response
	 * @return ActionResponse
	 * @throws IOException
	 * @throws ServletException
	 * @throws BaseException
	 */
	public ActionResponse frontLogout1(WaContext context, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException, BaseException {
		RequestParser parser = context.getRequestParser("default", request, response);

		HttpSession session = request.getSession(true);	       
		session.setAttribute("FRONT_ENTITY", null);	
		session.invalidate();
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~로그 아웃~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		String sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
		CookieManager.removeCookie("MEM_CLSS", sso_domain, response);
		CookieManager.removeNexessCookie(sso_domain, response);
		
		ActionResponse actionResponse = act(request, response, "");
		return actionResponse;
	}	
	
}	
