/***************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*  Ŭ������		:   GolfCtrlServ
*  �� �� ��		:   M4
*  ��    ��		:   ����� ��Ʈ�ѷ� ���� 
*  �������		:   golfLoung
*  �ۼ�����		:   2006.12.27 
************************** �����̷� ***************************************************
* ����			����		�ۼ���		������� 
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
 * ����� ��Ʈ�ѷ� ���� 
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
	 * ���� init().
	 **************************************************************************** */
	public void init() throws ServletException {
		super.init();
		// SSO ���� ���߱����� / ��� ������ Ȯ���Ѵ�.
		if(!isInitialized){
			checkServer();
			GolfCtrlServ.checkServerSSO(this.gubunIP);
			isInitialized = true;
		}
	}
	
	 /** ****************************************************************************
     * ���� �˻�. Coded By PWT, 20070830
     * @param request      HttpServletRequest
     * @param response     HttpServletResponse
     **************************************************************************** */
	public String checkServer()
	{
		String serverip = "";  // ����������
		String devip = "";	   // ���߱� ip ����

		try {
			serverip = InetAddress.getLocalHost().getHostAddress();
		} catch(Throwable t) {}

		try {
			devip = AppConfig.getAppProperty("DV_WAS_1ST");
		} catch(Throwable t) {}

		//logger.debug("serverip:" + serverip);
		//logger.debug("devip:" + devip);
		
		if (devip.equals(serverip)) {  //���߱�
			this.gubunIP = "dev";
		} else {	// ���
			this.gubunIP = "ser";
		}
		return this.gubunIP;
	} 
	
	/** ***************************************************************************
     * SSO ����� ���߱�� ��⿡ ���� ������ �޸��Ѵ�.
     * @param    ���߱�/��� ������ �����´�.
     **************************************************************************** */
    public static void checkServerSSO(String gubunIP) {

		String ip = "";
		String dv_was_1st = "";	//���߱� WAS IP
		String dr_was_1st = "";	//DR 1ȣ�� WAS IP
		String dr_was_2nd = "";//DR 2ȣ�� WAS IP
		String rl_was_1st = "";	//� 1ȣ�� WAS IP
		String rl_was_2nd = "";	//��� 2ȣ�� WAS IP

		String dv_sso_1st = ""; //���� SSO URL
		String dr_sso_1st = ""; //DR 1ȣ�� SSO URL
		String dr_sso_2nd = ""; //DR 2ȣ�� SSO URL
		String rl_sso_1st = ""; //� 1ȣ�� SSO URL
		String rl_sso_2nd = ""; //� 2ȣ�� SSO URL

		String dv_host_domain = "";	//���߱� ������ URL http://develop.bccard.com
		String dr_host_domain = ""; //DR
		String rl_host_domain = ""; //��� ������ URL http://www.bccard.com:80

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
			 

			// ���ð��߿��� �׽�Ʈ�ÿ� ȣ��Ʈ���Ͽ� ������ URL ���ð����� �׽�Ʈ�Ҽ� �ִ�.
			//dr_host_domain = "http://shoprd.bccard.com";
			//rl_host_domain = "http://shoprd.bccard.com"; 
			
		} catch (java.io.IOException ignore) {}
		//System.out.println("******************************************dv_was_1st:  ("+dv_was_1st+")");
		//System.out.println("******************************************ip:  ("+ip+")");
		if ( dv_was_1st.equals(ip) ) {										//���߱�
			//System.out.println("******************************************LOGIN1111");	
			 nls_url				= dv_sso_1st + ":9611"; 
			 
			 nls_login_url		= nls_url + "/nls3/cookieSignin.jsp?FORM=10";  // SSO�α���â
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
			 
			 nls_login_url		= nls_url + "/nls3/cookieSignin.jsp?FORM=10";  // SSO�α���â
			 nls_login_url5 	= nls_url	 + "/nls3/cookieSignin.jsp?FORM=10"; // long
			 //ascp_url		= server_url + "/app/card/view/initech/sso/login_exec.jsp";
			 ascp_url		= server_url + ":13300/app/golfloung/view/initech/sso/login_exec.jsp";		 

		} else {  // ���
			//System.out.println("******************************************LOGIN3333");	
			sso01 = new ActiveServer(rl_sso_1st, new SimpleConnection(new SocketConnectionImpl()));
			sso02 = new ActiveServer(rl_sso_2nd, new SimpleConnection(new SocketConnectionImpl()));
			cluster = new Cluster();

			// SSO �б�
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
			 nls_login_url		= nls_url + "/nls3/cookieSignin.jsp?FORM=10";  // SSO�α���â 
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
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~�α� �ƿ�~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		String sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
		CookieManager.removeCookie("MEM_CLSS", sso_domain, response);
		CookieManager.removeNexessCookie(sso_domain, response);
		
		ActionResponse actionResponse = act(request, response, "");
		return actionResponse;
	}	
	
}	
