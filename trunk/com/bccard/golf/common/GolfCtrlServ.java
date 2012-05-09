/***************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*  Ŭ������		:   GolfCtrlServ
*  �� �� ��		:   M4
*  ��    ��		:   ����� ��Ʈ�ѷ� ���� 
*  �������		:   golfLoung
*  �ۼ�����		:   2006.12.27
************************** �����̷� ***************************************************
*    ����     �ۼ���   �������
*  20110304  �̰���   [http://www.bccard.com/-"Home > VIP���� > ����] ���� ���ӽ� �ڵ���������
*  20110422  �̰���   1ȸ�� n���� ���ν� n���� ���ι�ȣ�� �ش�Ǵ� ī�� ��� ��ȸ�ϵ��� ����*  
*  20110512  �̰���   [http://golfloung.familykorail.com/-> Home > ���縶�� > �������� > �����󷹽�]���� ���ӽ� �ڵ���������
***************************************************************************************/
package com.bccard.golf.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.login.CardInfoEtt;
import com.bccard.golf.common.login.CardNhInfoEtt;
import com.bccard.golf.common.login.CardVipInfoEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.ControllerServlet;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RunActionInfo;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.jolt.JoltInput;
import com.initech.eam.api.NXContext;
import com.initech.eam.nls.CookieManager;
import com.initech.eam.smartenforcer.SECode;
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
import com.initech.vender.bccard.NXBCAuthChecker;

/***************************************************************************************
 * ����� ��Ʈ�ѷ� ���� 
 * @version 2006.12.27
 * @author  e4net
****************************************************************************************/
public class GolfCtrlServ extends ControllerServlet {

	public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";

	public static final String Pattern = ".do";

	private static final String LoginFormURI = "/app/golfloung/loginActn.do";

	private static final String LoginProcessURI = "/app/golfloung/golfLogin.do";

	private static final String PwChangeProcURI = "/app/golfloung/.do";

	private static final String DefaultAdminURI = "golf.do";
	
	/********************************************* �� BCCARD �ҽ� ���� ī��  start *********************************************/
	/********************************************* �� BCCARD �ҽ� ���� ī��  start *********************************************/
	private static boolean isInitialized = false;

	static Server sso01;
	static Server sso02;
	static Cluster cluster;
	static HeartBeatStorage storage;
	static HealthChecker checker;
	static LoadBalancer hashBalancer;
	static LoadBalancer roundrobinBalancer;
	static LoadBalancer randomBalancer;

	private static String	toa = "1";				//SSO 3rd/loginFormPage.jsp�� �̵�
	private static String	sso_domain = ".golfloung.com";
	//private static int		resource_chk = 0;

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

	private static String conSSO = ""; //SSO ����
	
	private static final String BSNINPT = "BSNINPT";					// �����ӿ� ��ȸ����
	
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
			System.out.println("******************************************LOGIN1111"+"\n");	
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

			 conSSO = "DEV";
			 
		} else if ( dr_was_1st.equals(ip) || dr_was_2nd.equals(ip) ) {		//DR
			System.out.println("******************************************LOGIN2222"+"\n");	
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

			// SSO �б�
			if (ip.equals(dr_was_1st)) {
				 conSSO = "SSO1";
			} else if (ip.equals(dr_was_2nd)) {
				 conSSO = "SSO2";
			}
				
		} else {  // ���
			System.out.println("******************************************LOGIN3333"+"\n");	
			sso01 = new ActiveServer(rl_sso_1st, new SimpleConnection(new SocketConnectionImpl()));
			sso02 = new ActiveServer(rl_sso_2nd, new SimpleConnection(new SocketConnectionImpl()));
			cluster = new Cluster();

			// SSO �б�
			if (ip.equals(rl_was_1st)) {
				cluster.add(sso01);
				cluster.add(sso01);
				cluster.add(sso01);
				cluster.add(sso01);
				
				conSSO = "SSO1";
			} else if (ip.equals(rl_was_2nd)) {
				cluster.add(sso02);
				cluster.add(sso02);
				cluster.add(sso02);
				cluster.add(sso02);

				conSSO = "SSO2";
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
	
    /********************************************* �� BCCARD �ҽ� ���� ī��  end *********************************************/ 
	/********************************************* �� BCCARD �ҽ� ���� ī��  end *********************************************/
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doProcess(request, response);
	} 

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doProcess(request, response);
	}
	
	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

//		sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
		
		response.setHeader("P3P","CP='CAO PSA CONi OTR OUR DEM ONL'");
        // Ŭ���̾�Ʈ�� ĳ���� ������ �ʱ� ���� �κ�
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html; charset=euc-kr");
        request.setCharacterEncoding("euc-kr");
        
		
		GolfAdminEtt ett = null;
		boolean isPermiResult = true;
		String defActn = "";
		
		// �α��� ����� üũ
		boolean passed = true;

		String requestURI = request.getRequestURI();
		HttpSession session = request.getSession();
		session.setAttribute("requestURI",requestURI);
				
		String actnkey = "";
		String global = null;                       // �۷ι� �׼� ������
           
        ActionResponse respon = null;               // ���� ������ ActionResponse
        
        String admAccess = "";        
        String actnParamKey = "";
        
     // ��û�� ActnKey set 
		try {
            actnkey = getActionKey(request);								//���� �׼�
            
            //logger.debug("�׼Ǻ��� ����");
			session.setAttribute("actnkey", actnkey);					//�׼Ǻ���
			
			logger.debug("actnkey:"+actnkey);
			          	            	
            	global			= getActionParam(actnkey,"GLOBAL_ACTION");     // �׼Ǽ������� �۷ι� �׼� ���θ� �о�´�.
 	            admAccess 		= getActionParam(actnkey, "ADMIN_ACCESS");			// ���� ����
 	            actnParamKey 	= getActionParam(actnkey,"layout");							// ��������
            	
            	request.setAttribute("layout", actnParamKey);
 				request.setAttribute("actnKey", actnkey);
 	            				
 				//logger.debug("actnParamKey:"+actnParamKey);
 				//logger.debug("admAccess:"+admAccess);
 				//logger.debug("globalActn:"+global);        
            
		} catch (Throwable t) {
            gotoErrorPage(request,response,t);
            return;
        }
		
			
		//������
		if ("Y".equals(admAccess)) {
					 
			//logger.debug("�����ڽ���");
			//logger.debug("requestURI :" + requestURI);
			//logger.debug("LoginFormURI :" + LoginFormURI);
			//logger.debug("LoginProcessURI :" + LoginProcessURI);
			//logger.debug("DefaultAdminURI :" + DefaultAdminURI);
			//logger.debug("PwChangeProcURI :" + PwChangeProcURI);
			ett = (GolfAdminEtt)session.getAttribute("SESSION_ADMIN");	// ������
			
			//logger.debug("ett:"+ett);
			if (ett == null) {
				
				//logger.debug("�α����� �ȵ� ���");
				if (requestURI.endsWith(LoginFormURI)) {	
				} else if (requestURI.endsWith(LoginProcessURI)) {	
				} else if (requestURI.endsWith(DefaultAdminURI)) {	
					//logger.debug("AAAAAAA");
					passed = false;
					defActn = "golf";					
					respon = act(request, response, defActn);		
					super.view(request,response,respon);
					
					
				} else if (requestURI.endsWith(PwChangeProcURI)) {	
				} else {					
					//logger.debug("!!!"+defActn);
					passed = false;
					defActn = "admLogoutMove";
					respon = act(request, response, defActn);	
					view(request,response,respon);
					
				}
				
			}else{
							
				//logger.debug("������ �α����� �� ���");				
							
				passed = false;
				respon = act(request, response, actnkey);		
				view(request,response,respon);
						
			}
		
		}else{
			
			//logger.debug("����ڽ���");
			//����Ʈ�ϰ�� ����. *************************************************************************************************			

			String userAcount = "";
			String sso_id = "";
			
			//[ http://www.bccard.com/->VIP����/����/VIP ������ ]�� 
			//[http://golfloung.familykorail.com ] ���ӽ�
			String in = request.getAttribute("actnKey").toString();			
			boolean inCheck = false;			
			
			if (in.length()>=9){
				if (in.substring(in.length()- 4, in.length()).equals("InBC")
						|| in.substring(in.length()- 8, in.length()).equals("InKorail")){						
					inCheck = true;
					logger.info("## �ܺο��� ���� (�񾾰��� or �ڷ���) : " + in);
				}else { 
					inCheck = false;
				}
			}
			 
	        if ( global == null) global = "false";					//�α��� �ʼ� ó��

	        //logger.debug("����üũ ����");
	       
	        UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
	        
	        UcusrinfoEntity coUserEtt   = (UcusrinfoEntity)session.getAttribute("COEVNT_ENTITY"); 
	        String strEnterCorporation = "";
			String strEnterCorporationMemId = "";
			String strEnterCorporationAccountId = "";
	        if(coUserEtt != null)
			{
				strEnterCorporation = coUserEtt.getStrEnterCorporation();			
				strEnterCorporationMemId = coUserEtt.getStrEnterCorporationMemId();  
				strEnterCorporationAccountId = coUserEtt.getStrEnterCorporationAccountId();  
				System.out.print("## GolfCtrlServ | coUserEtt | strEnterCorporation:"+strEnterCorporation+" |  strEnterCorporationMemId : "+strEnterCorporationMemId+" \n");
			}
			else
			{
				System.out.print("## GolfCtrlServ | coUserEtt null  \n");
			}
	        
	        //ett = (GolfSessionEtt)session.getAttribute("SESSION_USER");	
	        
	        //logger.debug("����üũ��");
	        /*
	        if(ett == null) {
	        	ett = new GolfSessionEtt(); 
	        }else{
	        	userAcount = ett.getMemId();
	        } 
	        */
	        if(usrEntity == null) {
	        	
	        	usrEntity = new UcusrinfoEntity(); 
	        	System.out.print("## GolfCtrlServ | usrEntity null --> ���� �۾� \n");
	        }else{
	        	
	        	userAcount = usrEntity.getAccount();
	        	System.out.print("## GolfCtrlServ | usrEntity not null  \n");
	        } 
	        //logger.debug("userAcount:"+userAcount);
	        
	        //logger.debug("����üũ ��");
	        
	        //logger.debug("SSO ����");
	        
			sso_id = getSsoId(request);;		//SSO ID(����� ID, account) 
			sso_id = GolfUtil.sqlInjectionFilter(sso_id); 
			
			System.out.print("## GolfCtrlServ | SSO ID | sso_id :"+sso_id+" | userAcount : "+userAcount+"\n");
			
			//logger.debug("SSO ID :"+ sso_id);
			String retCode = null;							//SSO ���� ���ϰ�
			//logger.debug("ascp_url :"+ ascp_url);
			String uurl = ascp_url;								//�̵��� URL, �ϴ� SSO �α��� ������
			String queryStr = "";
			int iretCode = 0;
			//SSO ����
			
			//SSO ID�� ������� ���� ����
			if("".equals(sso_id) || sso_id == null) 
			{
				System.out.print("## GolfCtrlServ | sso_id null  | sso_id :"+sso_id+"\n");
				if(!"".equals(userAcount) && !"Y".equals(strEnterCorporation))
				{
					System.out.print("## GolfCtrlServ | FRONT_ENTITY ���� �ı� | sso_id :"+sso_id+" | strEnterCorporation : "+strEnterCorporation+"\n");
					session.setAttribute("FRONT_ENTITY", null);	
					//session.invalidate();
				}
				
			}

			
			
			//logger.debug("getEamSessionCheck ����");
			retCode = getEamSessionCheck(request,response);
			
			//logger.debug("retCode :"+retCode);
			//logger.debug("getEamSessionCheck ��");
			
			if(!retCode.trim().equals(""))	iretCode = Integer.parseInt(retCode);
			//SSO ���ϰ� ����
			boolean isResult = retCode.compareTo("0")==0 ? true : false;
			//logger.debug("SSO ���ϰ� isResult : "+isResult);						
			String reqParm = "";
			
			
			//////////////////////////////////////////////////////////////////////////////////////
			// ����ȸ�� �ڵ��α��� �߰�
			// �����÷��� ������ �����ϰ� ���������� �������� �ʴ� ��� �ڵ��α��� �õ�
			String strMemCk = "N";
			if("Y".equals(strEnterCorporation) && ( "".equals(userAcount) || userAcount == null ) )  //�÷������� ���°�츸 �ڵ��α��� 
			{
				 
				System.out.print("## GolfCtrlServ | �÷������� ���� ��� | strEnterCorporationMemId :"+strEnterCorporationMemId+"  \n");
				
				createFrontUserSessionCo( session, request, strEnterCorporationMemId);	
				
				
				// ���������̸鼭 ����ȸ���� ��� üũ
				strMemCk = getChkMem( session, request, strEnterCorporationMemId);	//6�� ����ī�� 
				
				System.out.print("## GolfCtrlServ | ���� ����ī�� ȸ�� üũ | strEnterCorporationMemId :"+strEnterCorporationMemId+" | strMemCk : "+strMemCk+"  \n");
				
				// ����ȸ���̸鼭 ��������ī��ȸ���� ��� �α�������.
				if("6".equals(strMemCk))
				{					
					System.out.print("## GolfCtrlServ | ����ī���� ����ȸ�� | strEnterCorporationMemId :"+strEnterCorporationMemId+" | strMemCk : "+strMemCk+"  \n");
					session.setAttribute("SYSID", strEnterCorporationMemId);
					String parm = "" + request.getQueryString();
					if(!"".equals(parm))
						session.setAttribute("PARM", parm);
					if("".equals(actnkey) || actnkey == null) actnkey="golfIndex"; 
		
					System.out.print("## GolfCtrlServ | ����ȸ��(����ȸ��) �α������� �̵�  | strEnterCorporationMemId :"+strEnterCorporationMemId+" | strMemCk : "+strMemCk+"  \n");
					System.out.print("## GolfCtrlServ | ����ȸ�� join actnkey : "+actnkey+" | parm : "+parm+"\n");
					if("join_frame2".equals(actnkey))
					{
						System.out.print("## GolfCtrlServ | 11 join_frame2 \n");
						session.setAttribute("UURL", "/app/golfloung/join_frame2.do?"+parm);							
					}
					else
					{
						System.out.print("## GolfCtrlServ | 22 join_frame2 not \n");
						session.setAttribute("UURL", "/app/golfloung/"+actnkey+".do");
					}
					
					session.setAttribute("orgActionKey", actnkey);						
					response.sendRedirect("/app/golfloung/LoginCheck.do");
					return;
				}
				else
				{
					System.out.print("## GolfCtrlServ | ����ī�� �ƴ� ����ȸ�� | strEnterCorporationMemId :"+strEnterCorporationMemId+" | strMemCk : "+strMemCk+"  \n");
					
					//ž���� ����ī�带 ������ �ִ� ȸ������ üũ
					
					GolfUserEtt memUcusrinfo   = (GolfUserEtt)session.getAttribute("GOLF_ENTITY");
					if (memUcusrinfo != null) {
						
						String topGolfCardYn		= (String)memUcusrinfo.getGolfCardCoYn();
						
						System.out.print("## GolfCtrlServ | ����ī�� �ƴ� ����ȸ���� ž���� ī�� ���� ���� üũ | strEnterCorporationMemId :"+strEnterCorporationMemId+" | topGolfCardYn : "+topGolfCardYn+"  \n");
						
						if("Y".equals(topGolfCardYn))
						{
							
							System.out.print("## GolfCtrlServ | ����ī�� �ƴ� ����ȸ���� ž���� ī�� ���� | strEnterCorporationMemId :"+strEnterCorporationMemId+" | strMemCk : "+strMemCk+"  \n");
							
							session.setAttribute("SYSID", strEnterCorporationMemId);
							String parm = "" + request.getQueryString();
							if(!"".equals(parm))
								session.setAttribute("PARM", parm);
							if("".equals(actnkey) || actnkey == null) actnkey="golfIndex"; 
							
							System.out.print("## GolfCtrlServ | ����ī�� �ƴ� ����ȸ���� ž���� ī�� ���� join actnkey : "+actnkey+" | parm : "+parm+"\n");
							if("join_frame2".equals(actnkey))
							{
								System.out.print("## GolfCtrlServ | 11 join_frame2 \n");
								session.setAttribute("UURL", "/app/golfloung/join_frame2.do?"+parm);							
							}
							else
							{
								System.out.print("## GolfCtrlServ | 22 join_frame2 not \n");
								session.setAttribute("UURL", "/app/golfloung/"+actnkey+".do");
							}
							
							session.setAttribute("orgActionKey", actnkey);						
							response.sendRedirect("/app/golfloung/LoginCheck.do");
							return;
							
							
						}
						else
						{
							System.out.print("## GolfCtrlServ | ����ī�� �ƴ� ����ȸ���� ž���� ī�� ���� ���� | �α��κҰ� | strEnterCorporationMemId :"+strEnterCorporationMemId+" | strMemCk : "+strMemCk+"  \n");
						}
						
						
					}
					
					
					
					
					
				}
				
				
				
				
			}	
			

			/*[ http://www.bccard.com/->VIP����/����/VIP ������ ], 
			[http://golfloung.familykorail.com ] -> �ڷ���,			
			���� ���ӽ� user Session �������Ѵ�.
			 */
			if (!inCheck){				

			//////////////////////////////////////////////////////////////////////////////////////
			// ����ȸ�� �ڵ��α��� �߰�
			// SSO id �� �ִµ� ����� ������ ����ȸ���ϰ�� �ڵ��α�
			
				if(!"LoginCheck".equals(actnkey)  && sso_id != "" && sso_id  != null && usrEntity.getIntMemGrade() == 0 && ( "".equals(userAcount) || userAcount == null )  ){
					System.out.print("## GolfCtrlServ | �������� ��� ȸ�� �ڵ��α��� | sso_id : "+sso_id+" | getIntMemGrade : "+usrEntity.getIntMemGrade()+" \n");
					
					createFrontUserSession( session, request, sso_id);
	
					// ���� ȸ���̸鼭 ����ī�尡 �ƴѻ���� ������� �ʴ´�. => createFrontUserSession ���� ���̵� �������� ������ ���� �ܰ�� ������� �ʴ´�.
			        UcusrinfoEntity usrEntity2 = SessionUtil.getFrontUserInfo(request);
		        	String chkUserAcount = "";
		        	
		        	if(usrEntity2 != null){
		        		chkUserAcount = usrEntity2.getAccount();
		        	}
					//System.out.print("## GolfCtrlServ | userAcount" + chkUserAcount + "\n");
					
					//if(!"Y".equals(strCkNum)){
		        	if( !("".equals(chkUserAcount) || chkUserAcount == null) )
		        	{
		        		
			        		
							//logger.debug("��Ű����, ���Ǿ���, SSOID ���� ==> WAS ���� ����, SSO ���� ����");
							String parm = "" + request.getQueryString();
							if(!"".equals(parm))
								session.setAttribute("PARM", parm);
							if("".equals(actnkey) || actnkey == null) actnkey="golfIndex";
							
							System.out.print("## GolfCtrlServ | join actnkey : "+actnkey+" | parm : "+parm+"\n");
							
							if("join_frame2".equals(actnkey))
							{
								System.out.print("## GolfCtrlServ | 11 join_frame2 \n");
								session.setAttribute("UURL", "/app/golfloung/join_frame2.do?"+parm);							
							}
							else
							{
								System.out.print("## GolfCtrlServ | 22 join_frame2 not \n");
								session.setAttribute("UURL", "/app/golfloung/"+actnkey+".do");
							}
							
							
							
							session.setAttribute("orgActionKey", actnkey);						
							response.sendRedirect("/app/golfloung/LoginCheck.do");
							return;
		        		
					}
				}
			
			}
	 		
	 		//logger.debug("global ó�� ���� :"+global);
			//�۷ι� �׼�, ��α��� ó�� Action ����
			if ( "true".equals( global ) ){
							
				//logger.debug("�α��� �ʿ����");				
				//logger.debug("userAcount:"+userAcount);
				//logger.debug("sso_id:"+sso_id);
				
				/*[ http://www.bccard.com/->VIP����/����/VIP ������ ], 
				[http://golfloung.familykorail.com ] -> �ڷ���,				 
				���� ���ӽ� user Session �������Ѵ�.
				 */
				if (!inCheck){
					
					if(userAcount != null)	userAcount=userAcount.trim();
					
					if("".equals(userAcount) && sso_id != null && !sso_id.trim().equals(""))  {
						//logger.debug("���ǻ���");
						createFrontUserSession( session, request, sso_id);
						System.out.print("## GolfCtrlServ | ���ǻ��� | sso_id :"+sso_id+"\n");
						
						//logger.debug("��Ű����, ���Ǿ���, SSOID ���� ==> WAS ���� ����, SSO ���� ����");
						
						// ���� �÷��� ���ؼ� ������ ���� ��츸 ���� 					
						if(!"Y".equals(strEnterCorporation) &&  !"".equals(userAcount) )
						{
							
							System.out.print("## GolfCtrlServ | ���� �÷��� ���ؼ� ������ ���� ��츸 ����  | sso_id :"+sso_id+"\n");
							session.setAttribute("SYSID", sso_id);
							String parm = "" + request.getQueryString();
							if(!"".equals(parm))
								session.setAttribute("PARM", parm);
							if("".equals(actnkey) || actnkey == null) actnkey="golfIndex"; 
							
							System.out.print("## GolfCtrlServ | actnkey :"+actnkey+"\n");
							
							session.setAttribute("UURL", "/app/golfloung/"+actnkey+".do");
							session.setAttribute("orgActionKey", actnkey);					
							response.sendRedirect("/app/golfloung/LoginCheck.do");
							return;
						}
					}
					
				}				
				
				//logger.debug("isResult:"+isResult);
				//logger.debug("userAcount:"+userAcount);
				//logger.debug("actnkey:"+actnkey);
				//logger.debug("sso_id:"+sso_id);
				//logger.debug("��Ű�� ���� �ִ��� üũ"); 
				//��Ű����, ��������, 
				if ( isResult && !"".equals(userAcount) && sso_id != null && !"LoginCheck".equals(actnkey) ) {
					
					//logger.debug("��Ű,���� ����");
					session.setAttribute("SYSID", sso_id);
					session.setAttribute("UURL", "/app/golfloung/"+actnkey+".do");
					passed = false;
					respon = act(request, response, actnkey);
				} else {
					
					//logger.debug("��Ű,���� ���ų� �α��� üũ");
					
					if("LoginCheck".equals(actnkey)) {
						
						//logger.debug("LoginCheck");
						//�α��� �� �����ϴ� �׼��� ������ ������ �Ķ���͸� �ѱ��. By PWT 20070903
						reqParm = "" + CookieManager.getCookieValue("REQ_PARM", request);
						if(!"".equals(reqParm) && reqParm != null && !"null".equals(reqParm)) {
						
							//logger.debug("��ȣȭ ó�� �Ķ����");
							
							//��ȣȭ ó�� �Ķ����
							if(reqParm.indexOf("BCENC") != -1)
								
								//session.setAttribute("PARM", "INIpluginData=" +	URLEncoder.encode(reqParm.substring(5,reqParm.length())));
								session.setAttribute("PARM", "INIpluginData=" +	URLEncoder.encode(reqParm.substring(5,reqParm.length()),"UTF-8"));
							
							else
								session.setAttribute("PARM",  reqParm);//�Ϲ��Ķ����(�ܺ� �α��ε�)
							
						}
			 			CookieManager.removeCookie("REQ_JURL", sso_domain, response);
			 			CookieManager.removeCookie("REQ_PARM", sso_domain, response);
					}
					passed = false;
					respon = act(request, response);
					
					request.setAttribute("actnKey", actnkey );
				}
			}
			// �α��� �ʼ� Action ����
			else {
				
				//logger.debug("�α��� �ʿ�����");
				
				//logger.debug("��Ű ����"+actnkey);
				CookieManager.addCookie("GOLF_REQ_UURL", actnkey, sso_domain, response);
				//logger.debug("��Ű ����"+actnkey);
				session.setAttribute("GOLF_REQ_UURL", actnkey);
				
				String returnPar = StrUtil.isNull(request.getParameter("bbs"),"");
				CookieManager.addCookie("bbs", returnPar, sso_domain, response);
				session.setAttribute("bbs", returnPar);
				
				String returnPar1 = StrUtil.isNull(request.getParameter("slsn_type_cd"),"");
				CookieManager.addCookie("slsn_type_cd", returnPar1, sso_domain, response);
				session.setAttribute("slsn_type_cd", returnPar1);
				
				String returnPar2 = StrUtil.isNull(request.getParameter("svod_clss"),"");
				CookieManager.addCookie("svod_clss", returnPar2, sso_domain, response);
				session.setAttribute("svod_clss", returnPar2);
				
				String returnPar3 = StrUtil.isNull(request.getParameter("scoop_cp_cd"),"");
				CookieManager.addCookie("scoop_cp_cd", returnPar3, sso_domain, response);
				session.setAttribute("scoop_cp_cd", returnPar3);
				
				String returnPar4 = StrUtil.isNull(request.getParameter("s_exec_type_cd"),"");
				CookieManager.addCookie("s_exec_type_cd", returnPar4, sso_domain, response);
				session.setAttribute("s_exec_type_cd", returnPar4);
				
				String returnPar5 = StrUtil.isNull(request.getParameter("p_idx"),"");
				CookieManager.addCookie("p_idx", returnPar5, sso_domain, response);
				session.setAttribute("p_idx", returnPar5);
				
				//logger.debug("returnPar:::::::::::::::::::::::::::"+returnPar); 
				//logger.debug("returnPar1:::::::::::::::::::::::::::"+returnPar1);
				//logger.debug("returnPar2:::::::::::::::::::::::::::"+returnPar2);
				//logger.debug("returnPar3:::::::::::::::::::::::::::"+returnPar3);
				//logger.debug("returnPar4:::::::::::::::::::::::::::"+returnPar4);
				//logger.debug("returnPar5:::::::::::::::::::::::::::"+returnPar5);
				//logger.debug("sso_id:"+sso_id); 				

				session.setAttribute("conSSO", conSSO);
				
				String peCk= selectPeByCkNum(strEnterCorporationAccountId);
				String coCk= selectPeCoByCkNum(strEnterCorporationMemId);
				
				if(userAcount != null)	userAcount=userAcount.trim();				
				
				// ��Ű����, ��������, SSOID�� ACCOUNT �� ����
				if ( isResult && !"".equals(userAcount) && sso_id.equals(userAcount)) {
					
					System.out.print("## GolfCtrlServ | ��Ű����, ��������, SSOID�� ACCOUNT �� ����  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
					//logger.debug("��Ű����, ��������, SSOID�� ACCOUNT �� ����");
					passed = false;
					respon = act(request, response);
				}// ��Ű����, ���Ǿ���, SSOID ���� ==> WAS ���� ����, SSO ���� ����
				else if (  isResult && !"".equals(userAcount) && sso_id != null ) {
					
					System.out.print("## GolfCtrlServ | ��Ű����, ���Ǿ���, SSOID ����  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					
					//logger.debug("��Ű����, ���Ǿ���, SSOID ���� ==> WAS ���� ����, SSO ���� ����");
					session.setAttribute("SYSID", sso_id);
					
					String parm = "" + request.getQueryString();
					if(!"".equals(parm))
						session.setAttribute("PARM", parm);
					session.setAttribute("UURL", "/app/golfloung/"+actnkey+".do");
					session.setAttribute("orgActionKey", actnkey);
					response.sendRedirect("/app/golfloung/LoginCheck.do");
					return;
				}
				// ACCOUNT ����, SSOID ���� ==> �α��� ���ϰ� �α��� �ʼ� �׼� Call�� ���
				else if ( "".equals(userAcount) && sso_id==null ) {
					
					System.out.print("## GolfCtrlServ | ACCOUNT ����, SSOID ����  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
					//logger.debug("ACCOUNT ����, SSOID ���� ==> �α��� ���ϰ� �α��� �ʼ� �׼� Call�� ���");
					createFrontUserSession( session, request, sso_id);					
					String iniTectData = "";
					queryStr = "" + request.getQueryString();
					
					//��ȣȭ �Ķ���� ó��(INIpluginData=�� ��Ű�� �ѱ� �� "="�� �� �ٽ� ���ڵ� ó����)
					if(queryStr.indexOf("INIpluginData=") != -1) {
						iniTectData = request.getParameter("INIpluginData");
						queryStr = "BCENC" + iniTectData;
					}
					try {
						//��û�� �Ķ����  , Jump URL ����
						if(!"".equals(queryStr) && queryStr != null && !"null".equals(queryStr)	&& !"loginActn".equals(actnkey) ) {
							CookieManager.addCookie("REQ_PARM", queryStr, sso_domain, response);
							CookieManager.addCookie("REQ_JURL", actnkey, sso_domain, response);
						}
						CookieManager.addCookie("REQ_UURL", actnkey, sso_domain, response);
							if ("dev".equals(gubunIP)) {
								//goLoginPage(response, uurl);																									
									
									goLoginPage(response, uurl);								
								
							}
							else {
									
									goLoginPage(request, response, uurl);
								
								//goLoginPage(request, response, uurl);
							}
					} catch (Exception nrse) {}
					return;
				}
				// �����÷������� ���ͼ� �ڵ��α��� �Ȱ��
				else if( !"".equals(userAcount) && ( "".equals(sso_id) ||  sso_id==null ) && "Y".equals(strEnterCorporation) ) {
					System.out.print("## GolfCtrlServ | �����÷������� ���ͼ� �ڵ��α��� ��� or ������ �̹� ������  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
					//logger.debug("��Ű����, ��������, SSOID�� ACCOUNT �� ����");
					passed = false;
					respon = act(request, response);
				}											
				// �����÷������� ���ͼ� ����ī���̸鼭 ȸ���� �ƴѰ�� 
				else if( ( "".equals(userAcount) && "".equals(sso_id) ||  sso_id==null ) && "Y".equals(strEnterCorporation) && !"Y".equals(peCk) && "6".equals(coCk)   ) {
					System.out.print("## GolfCtrlServ | �����÷������� ���ͼ� ����ī���̸鼭 ȸ���� �ƴѰ��   | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
				
					createFrontUserSession( session, request, strEnterCorporationAccountId);
					response.sendRedirect("/app/golfloung/LoginCheck.do");
					return;
					
					
					
				}	
				
				// ��Ÿ���� => WAS���Ǹ� ��� ����, SSO ���� ����,
				else {
					System.out.print("## GolfCtrlServ | WAS���Ǹ� ��� ����, SSO ���� ����,  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
					//logger.debug("��Ÿ���� => WAS���Ǹ� ��� ����, SSO ���� ����,");
					//�ϴ� ���ǻ���, ��Ű����
					session.removeAttribute("SYSID");
					session.removeAttribute("FRONT_ENTITY");
					session.setAttribute("FRONT_ENTITY", null);	
					//session.invalidate();
					//session.removeAttribute(ConstVars.FRONT_ENTITY);
					CookieManager.removeCookie("MEM_CLSS", sso_domain, response);

					CookieManager.removeNexessCookie(sso_domain, response);
					//CookieManager.removeNexessCookie(".bccard.com", response);
					try {
						//logger.debug("���� ���� ���� ���");
						/*
						* RESULT : ���� ���� ���� ���
						*	-. RESULT = 0 : ������ �α��� ������ ���� ���ϰ�, �׿ܴ� �����ڵ�
						*	-. RESULT = 100 : �͸����� (�α��� �Ǿ����� ���� ���)
						*	-. RESULT = 200 : ����� ���̵� ���� ���
						*	-. RESULT = 1000 : �� �ʿ��� ��Ű�� �Ϻΰ� ������
						*	-. RESULT = 1001 : ���� Ÿ�Ӿƿ�(LAT�� session time�� ����� ���)
						*	-. RESULT = 1002 : ��Ű������ �� �����ϳ� �ؼ����� ���������� �߱���
					                                  ��Ⱑ �ƴҶ�(HMAC�� Ʋ�� ���)
						*	-. RESULT = 1004 : IP�� ���� ���� ���   */
						if ( retCode.compareTo("200")==0 || retCode.compareTo("100")==0 ) {
							
							//logger.debug("200/100");
							CookieManager.addCookie("REQ_UURL", actnkey, sso_domain, response);
								if ("dev".equals(gubunIP)) {
									goLoginPage(response, uurl);
								}
								else {
									goLoginPage(request, response, uurl);
								}
						}
						//SSO ��������� ��� ó�� ==> SSO �α��� �̵�
						else if(retCode.compareTo("1001") == 0) {
							
							//logger.debug("1001");
							//?�α���(loginActn)�Ϸ��� ���Դµ� ������ ������ ��츦 �����ϰ� ���Ǹ��� ó��

							if(actnkey.equals("loginActn")) {

								passed = false;
								respon = act(request, response, "loginActn");
								super.view(request,response,respon);
							}
							else {
								//response.sendRedirect("/card/view/app/common/jump_login_ssn_page.jsp");
								//response.sendRedirect("/app/golfloung/view/golf/jump_first_page.jsp");
								//return;
								logger.debug("##########  ������ �̵�");
								goErrorPage(response, iretCode);
							}
						}
						else {
							
							//logger.debug("����������");
							goErrorPage(response, iretCode);
						}
				        // ȣ��� �׼� ������ �α����� JSP ���� �ޱ����� Request Attribute�� ����Ѵ�.
				        request.setAttribute("requestActionKey", actnkey );
				        request.setAttribute("requestURI", request.getRequestURI() );
					} catch (Exception e) {}
					return;
				}

			}//End �α��� �� Action ����
			
			if ( respon != null ) {
				
				//logger.debug("respon �� ���� �ƴѰ��");
				
			    // ���������� ������ ���� ������ ���̾ƿ� ������ �о�� Request Attribute�� ��ϵ� ���� �����Ѵ�.
			    String responLayout = respon.getParamProp("layout");
			    //logger.debug("responLayout:"+responLayout);
			    
			    if ( responLayout != null && responLayout.length() > 0 ) {
			        request.setAttribute("layout",responLayout);
			    }
			}
			//logger.debug("respon.getKey():"+respon.getKey());
			if(respon.getKey()==""){
				//logger.debug("respon.getKey() �� ���� ���");
				respon.setKey("default");
				//logger.debug("respon.getKey():"+respon.getKey());
			}else{}
			
			
			if(actnkey == null || "".equals(actnkey)){
				isPermiResult = false;
			}	
			
			golfAccessLog(request, response, respon, "STD", ett, isPermiResult);
			
			super.view(request,response,respon);  // ȭ���� ����Ѵ�.
			
			golfAccessLog(request, response, respon, "END", ett, isPermiResult);
			
			//logger.debug("================��============ ==");
			
			//����Ʈ�ϰ�� ��.***************************************************************************************************
			
		}
		
		byPassed(passed, request, response);
	}
    
	/**
	 * ����� ���� ����
	 * @param session
	 * @param sso_id
	 */
	public void createFrontUserSession(HttpSession session, HttpServletRequest req, String sso_id){
		Connection con = null;
		UcusrinfoEntity ucusrinfo = null;
		try{
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);		
			
			//��ü �������� ó�� 2010.11.05 �ǿ���
			
			// 1. ucusrinfo  ��  member_clss Ȯ��
			logger.debug("## GolfCtrlServ | ucusrinfo  ��  member_clss Ȯ��");
			String strCkClss = proc.selectByCkClss(con, sso_id);
			System.out.println("## GolfCtrlServ | ����ȸ������ üũ ��� | sso_id : "+sso_id+" | strCkClss : "+strCkClss+"\n");
			
			/* 
			   - 1 : ����ȸ��(��ī�������)
			   - 4 : ����ȸ��(��ī��̼�����)
			   - 5 : ����ȸ��
			*/
			
			// 2. ����ȸ���ϰ�� (member_clss in 1,4 ) 
			if("1".equals(strCkClss) || "4".equals(strCkClss))
			{
				// ����� Front Entity ����.
				logger.debug("## ���� ȸ�� | createFrontUserSession ����� Front Entity ����.");				
				ucusrinfo = proc.selectByAccount(con, sso_id);
							
				//logger.debug("ucusrinfo:"+ucusrinfo);
				
				if (ucusrinfo != null) {
					System.out.println("## GolfCtrlServ | ȸ�� ���� ���� ó�� ���� | sso_id : "+sso_id+"\n");
					//logger.debug("������������");
					// ���� ���� ����
					//setPrivateInfo(waContext, ucusrinfo);
																		
					
					
					session.setAttribute("FRONT_ENTITY", ucusrinfo);
					session.setAttribute("SESSION_USER", ucusrinfo);

					GolfUserEtt ett = new GolfUserEtt();

					ett.setLogin(true);
					ett.setMemId(ucusrinfo.getAccount());
					ett.setMemNm(ucusrinfo.getName());
					ett.setMemClss(ucusrinfo.getMemberClss());	// ȸ������ 1=����,2=����
					if(ucusrinfo.getMemberClss() !=null && sso_id!=null) {
						ett.setJuminNo(ucusrinfo.getSocid());

						/** *****************************************************************
						 * checkJolt() Start - �������ī��
						 ***************************************************************** */
						logger.debug("=============> checkJolt() Start <============1" );
						try{
							checkJolt(waContext, req, ett, ucusrinfo);
						}catch (Throwable t){

							ArrayList card_list = new ArrayList();
							ett.setCardInfoList(card_list);
						}
						
						session.setAttribute("GOLF_ENTITY", ett);
						List list 		 = ett.getCardInfoList();
						List listTopGolf = ett.getTopGolfCardInfoList();
						List listRich	 = ett.getRichCardInfoList();
						System.out.println("## GolfCtrlServ | ����ī��������� ��� ID : "+ucusrinfo.getAccount()+" | list.size() : " + list.size()+" | ž����ī�� ������ : "+listTopGolf.size()+" | ��ġī�� ������ : "+listRich.size()+"  \n");

						/** *****************************************************************
						 * checkJoltNh() Start - ����ī�� 
						 ***************************************************************** */
						logger.debug("=============> checkJoltNh() Start <============" );
						try{
							checkJoltNh(waContext, req, ett, ucusrinfo);
						}catch (Throwable t){

							ArrayList card_list = new ArrayList();
							ett.setCardNhInfoList(card_list);
						}
						session.setAttribute("GOLF_ENTITY", ett);
						List listNh = ett.getCardNhInfoList();
						System.out.println("## GolfCtrlServ | ȸ�� ����ī��������� ��� ID : "+ucusrinfo.getAccount()+" | listNh.size() : " + listNh.size()+"\n");
						
						/** *****************************************************************
						 * checkJoltVip() Start - VIPī�� 
						 ***************************************************************** */
						logger.debug("=============> checkJoltVip() Start <============" );
						try{
							checkJoltVip(waContext, req, ett, ucusrinfo);
						}catch (Throwable t){
							ArrayList card_list = new ArrayList();
							ett.setCardVipInfoList(card_list);
						}
						session.setAttribute("GOLF_ENTITY", ett);
						List listVip = ett.getCardVipInfoList();
						System.out.println("## GolfCtrlServ | ȸ�� VIPī��������� ��� ID : "+ucusrinfo.getAccount()+" | listVip.size() : " + listVip.size()+"\n");

					}
				
				}
				
				
			}
			else if("5".equals(strCkClss))
			{
				// 3.����ȸ���� ��� MEM_CLSS Ȯ��
				logger.debug("## GolfCtrlServ | ����ȸ���� ��� MEM_CLSS Ȯ��");
				String strCkCoClss = proc.selectByCkCoMemClss(con, sso_id);
				
				System.out.println("## GolfCtrlServ | ����ȸ������ üũ ��� | sso_id : "+sso_id+" | strCkCoClss : "+strCkCoClss+"\n");
				
				// Y: ���� ����ȸ�� , N: ���� ȸ�� �ƴ�. ����ȸ�� Ȥ�� �̽��� ������ȸ��
				if(!"N".equals(strCkCoClss))
				{
					/*
					MEM_CLSS = 6 : ����ī��ȸ��
					MEM_CLSS in 1,2,3,4,5,7,8 : ����ī��ȸ��
					*/
					
					if("6".equals(strCkCoClss))
					{
						//����ī�� ȸ�� ó��
						// ����� Front Entity ����.
						logger.debug("## ���� ����ī�� | createFrontUserSession ����� Front Entity ����.");				
						ucusrinfo = proc.selectByAccount(con, sso_id);
									
						//logger.debug("ucusrinfo:"+ucusrinfo);
						
						if (ucusrinfo != null) {
							System.out.println("## GolfCtrlServ | ȸ�� ���� ���� ó�� ���� | sso_id : "+sso_id+"\n");
							//logger.debug("������������");
							// ���� ���� ����
							//setPrivateInfo(waContext, ucusrinfo);
																				
							
							
							session.setAttribute("FRONT_ENTITY", ucusrinfo);
							session.setAttribute("SESSION_USER", ucusrinfo);

							GolfUserEtt ett = new GolfUserEtt();

							ett.setLogin(true);
							ett.setMemId(ucusrinfo.getAccount());
							ett.setMemNm(ucusrinfo.getName());
							ett.setMemClss(ucusrinfo.getMemberClss());	// ȸ������ 1=����,2=����
							if(ucusrinfo.getMemberClss() !=null && sso_id!=null) {
								ett.setJuminNo(ucusrinfo.getSocid());

								/** *****************************************************************
								 * checkJolt() Start - �������ī��
								 ***************************************************************** */
								logger.debug("=============> checkJolt() Start <============2" );
								try{
									checkJolt(waContext, req, ett, ucusrinfo);
								}catch (Throwable t){
									logger.debug("=============> checkJolt Error <============2" );
									ArrayList card_list = new ArrayList();
									ett.setCardInfoList(card_list);
									ett.setTopGolfCardInfoList(card_list);
									ett.setRichCardInfoList(card_list);
								}
								
								session.setAttribute("GOLF_ENTITY", ett);
								List list 		 = ett.getCardInfoList();
								List listTopGolf = ett.getTopGolfCardInfoList();
								List listRich	 = ett.getRichCardInfoList();
								System.out.println("## GolfCtrlServ | ����ī��������� ��� ID : "+ucusrinfo.getAccount()+" | list.size() : " + list.size()+" | ž����ī�� ������ : "+listTopGolf.size()+" | ��ġī�� ������ : "+listRich.size()+"  \n");

								/** *****************************************************************
								 * checkJoltNh() Start - ����ī�� 
								 ***************************************************************** */
								logger.debug("=============> checkJoltNh() Start <============" );
								try{
									checkJoltNh(waContext, req, ett, ucusrinfo);
								}catch (Throwable t){

									ArrayList card_list = new ArrayList();
									ett.setCardNhInfoList(card_list);
								}
								session.setAttribute("GOLF_ENTITY", ett);
								List listNh = ett.getCardNhInfoList();
								System.out.println("## GolfCtrlServ | ȸ�� ����ī��������� ��� ID : "+ucusrinfo.getAccount()+" | listNh.size() : " + listNh.size()+"\n");
								
								/** *****************************************************************
								 * checkJoltVip() Start - VIPī�� 
								 ***************************************************************** */
								logger.debug("=============> checkJoltVip() Start <============" );
								try{
									checkJoltVip(waContext, req, ett, ucusrinfo);
								}catch (Throwable t){
									ArrayList card_list = new ArrayList();
									ett.setCardVipInfoList(card_list);
								}
								session.setAttribute("GOLF_ENTITY", ett);
								List listVip = ett.getCardVipInfoList();
								System.out.println("## GolfCtrlServ | ȸ�� VIPī��������� ��� ID : "+ucusrinfo.getAccount()+" | listVip.size() : " + listVip.size()+"\n");

							}
						
						}
						
						
						
						
					}
					else
					{
						//����ī�� ȸ�� ó��
						//����ȸ���ε� ž����ī����������� üũ
						logger.debug("## ���� ����ī���� ����ȸ�� | ����ȸ���ε� ž����ī������ üũ");	
					
						ucusrinfo = proc.selectByAccount(con, sso_id);
						GolfUserEtt ett = new GolfUserEtt();
						
						ett.setLogin(true);
						ett.setMemId(ucusrinfo.getAccount());
						ett.setMemNm(ucusrinfo.getName());
						ett.setMemClss(ucusrinfo.getMemberClss());	// ȸ������ 1=����,2=����
						if(ucusrinfo.getMemberClss() !=null && sso_id!=null) {
						
							ett.setJuminNo(ucusrinfo.getSocid());
							/** *****************************************************************
							 * checkJolt() Start - �������ī��
							 ***************************************************************** */
							logger.debug("=============> checkJoltGolfCardOnly() Start <============" );
							try{
								checkJoltGolfCardOnly(waContext, req, ett, ucusrinfo);
							}catch (Throwable t){
								logger.debug("=============> checkJoltGolfCardOnly Error <============" );
								ArrayList card_list = new ArrayList();
								ett.setCardInfoList(card_list);
								ett.setTopGolfCardInfoList(card_list);
								ett.setRichCardInfoList(card_list);
							}
				
							
							List list 		 = ett.getCardInfoList();
							List listTopGolf = ett.getTopGolfCardInfoList();
							List listRich	 = ett.getRichCardInfoList();
							System.out.println("## GolfCtrlServ | ����ȸ�� ����ī��������� ��� ID : "+ucusrinfo.getAccount()+" | list.size() : " + list.size()+" | ž����ī�� ������ : "+listTopGolf.size()+" | ��ġī�� ������ : "+listRich.size()+"  \n");
							
							//ž����ī�� ������ ���
							if(listTopGolf.size()>0)
							{
								session.setAttribute("FRONT_ENTITY", ucusrinfo);
								session.setAttribute("SESSION_USER", ucusrinfo);
													
								
								System.out.println("## ž����ī�� �������� ID : "+ucusrinfo.getAccount()+"\n");
								
								ett.setGolfCardCoYn("Y");
								session.setAttribute("GOLF_ENTITY", ett);
								
							
								
							}
							else
							{
								System.out.println("## ž����ī�� �������� �ƴ� | ID : "+ucusrinfo.getAccount()+" \n");
							}
									
						}
						
						
						
						
						
						
						
						
					}
						
						
						
					
					
					
					
					
					
					
					
					
				}
				else
				{
					System.out.println("## GolfCtrlServ | ���� ȸ�� �ƴ�. ����ȸ�� Ȥ�� �̽��� ������ȸ�� sso_id : "+sso_id+" \n");
				}
				
				
				
				
			}
			
			
			
			
			
			
		} catch (Throwable t) {
			System.out.println("## createFrontUserSession ���� | ID : "+ucusrinfo.getAccount()+" \n");						
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
	}	
	
	/**
	 * ���� ����� ���� ����
	 * @param session
	 * @param sso_id
	 */
	public void createFrontUserSessionCo(HttpSession session, HttpServletRequest req, String sso_id){
		Connection con = null;
		UcusrinfoEntity ucusrinfo = null;
		try{
			
			// ����� Front Entity ����.
			logger.debug("createFrontUserSessionCo ���� ����� Front Entity ����  | sso_id : "+sso_id);
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);			
			ucusrinfo = proc.selectByAccountCo(con, sso_id);
						
			//logger.debug("ucusrinfo:"+ucusrinfo);
			 
			// �����̸鼭 ����ȸ���� ����� �˻�
			if (ucusrinfo != null) {
				System.out.println("## GolfCtrlServ | createFrontUserSessionCo | ����ȸ�� | sso_id : "+sso_id+"\n");
				//logger.debug("������������");
				// ���� ���� ����
				//setPrivateInfo(waContext, ucusrinfo);
				
				if("6".equals(ucusrinfo.getStrCoMemType() ))
				{
					System.out.println("## GolfCtrlServ | createFrontUserSessionCo | ���� ����ȸ���� ��� | ����ó�� ���� | sso_id : "+sso_id+" | ucusrinfo.getStrCoMemType() : "+ucusrinfo.getStrCoMemType()+"\n");
					session.setAttribute("FRONT_ENTITY", ucusrinfo);
					session.setAttribute("SESSION_USER", ucusrinfo);
					
				
					
					GolfUserEtt ett = new GolfUserEtt();
	
					ett.setLogin(true);
					ett.setMemId(ucusrinfo.getAccount());
					ett.setMemNm(ucusrinfo.getName());
					ett.setMemClss(ucusrinfo.getMemberClss());	// ȸ������ 1=����,2=����
					if(ucusrinfo.getMemberClss() !=null && "1".equals(ucusrinfo.getMemberClss())) 
					{
						ett.setJuminNo(ucusrinfo.getSocid());
	
						/** *****************************************************************
						 * checkJolt() Start
						 ***************************************************************** */
						logger.debug("=============> checkJolt() Start <============3" );
						checkJolt(waContext, req, ett, ucusrinfo);
						session.setAttribute("GOLF_ENTITY", ett);
						List list = ett.getCardInfoList();
	
						System.out.println("## GolfCtrlServ | createFrontUserSessionCo | ȸ�� ����ī��������� | sso_id : "+sso_id+" | list.size() : " + list.size()+"\n");
	
					}
				
				}
				else
				{
					System.out.println("## GolfCtrlServ | createFrontUserSessionCo | ����ȸ�������� ����ī�尡 �ƴѰ�� ž������������ üũ | sso_id : "+sso_id+"\n");
					
					//����ȸ���ε� ž����ī����������� üũ 
					ucusrinfo = proc.selectByAccountCo(con, sso_id);
					GolfUserEtt ett = new GolfUserEtt();					
					ett.setLogin(true);
					ett.setMemId(ucusrinfo.getAccount());
					ett.setMemNm(ucusrinfo.getName());
					ett.setMemClss(ucusrinfo.getMemberClss());	// ȸ������ 1=����,2=����
					if(ucusrinfo.getMemberClss() !=null && sso_id!=null) 
					{
						
						ett.setJuminNo(ucusrinfo.getSocid());
						/** *****************************************************************
						 * checkJolt() Start - �������ī��
						 ***************************************************************** */
						logger.debug("=============> checkJolt() Start <============4" );
						try{
							checkJoltGolfCardOnly(waContext, req, ett, ucusrinfo);
						}catch (Throwable t){
							logger.debug("=============> checkJolt Error <============4" );
							ArrayList card_list = new ArrayList();
							ett.setCardInfoList(card_list);
							ett.setTopGolfCardInfoList(card_list);
							ett.setRichCardInfoList(card_list);
						
						}
			
												
						List list 		 	= ett.getCardInfoList();
						List listTopGolf 	= ett.getTopGolfCardInfoList();
						List listRich	 	= ett.getRichCardInfoList();
						
						System.out.println("## GolfCtrlServ | createFrontUserSessionCo | ����ȸ���ε� ����ī�尡 �ƴѰ�� | ����ī��������� ��� ID : "+ucusrinfo.getAccount()+" | list.size() : " + list.size()+" | ž����ī�� ������ : "+listTopGolf.size()+" | ��ġī�� ������ : "+listRich.size()+"  \n");
						
						//ž����ī�� ������ ���
						if(listTopGolf.size()>0)
						{
							session.setAttribute("FRONT_ENTITY", ucusrinfo);
							session.setAttribute("SESSION_USER", ucusrinfo);
							
														
							System.out.println("## ž����ī�� �������� ID : "+ucusrinfo.getAccount()+" | \n");
							
							ett.setGolfCardCoYn("Y");
							session.setAttribute("GOLF_ENTITY", ett);
							
							
							
						}
						else
						{
							System.out.println("## ž����ī�� �������� �ƴ� | ID : "+ucusrinfo.getAccount()+" | \n");
							session.setAttribute("GOLF_ENTITY", null);
							session.setAttribute("COEVNT_ENTITY", null);							
						}
								
					}
					
					
					
					
					System.out.println("## GolfCtrlServ | ���� ����ȸ���� �ƴ� ���  |  ���� ������ ���� | sso_id : "+sso_id+" | ucusrinfo.getStrCoMemType() : "+ucusrinfo.getStrCoMemType()+"\n");
					
				}
				
			}
			else
			{
				System.out.println("## GolfCtrlServ | createFrontUserSessionCo | �������̺� �������� �ʾ� �α��� �Ұ� | sso_id : "+sso_id+"\n");
			}
			
			
			
			
			
		} catch (Throwable t) {
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
	}	
	/**
	 * �����̸鼭 ����ȸ���� ����� �Ǵ�
	 * @param session
	 * @param sso_id
	 */
	public String getChkMem(HttpSession session, HttpServletRequest req, String mem_id) {
		String strCoMemYn = "N";
		Connection con = null;
		UcusrinfoEntity ucusrinfo = null;
		try{
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);			
			ucusrinfo = proc.selectByAccountCo(con, mem_id);
			
			// ������ ����� �˻�
			if (ucusrinfo != null) 
			{								
				strCoMemYn=ucusrinfo.getStrCoMemType();
			}else
			{
				strCoMemYn="N";
			}
			
		
		}catch (Throwable t) {
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		return strCoMemYn;
	}
	
	/**
	 * ����ȸ���� ����� �Ǵ�
	 * @param session
	 * @param sso_id
	 */
	public String selectPeByCkNum(String account_id) {
		String strMemYn = "N";
		Connection con = null;
		try{
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);			
			strMemYn = proc.selectPeByCkNum(con, account_id);		// Y����ȸ��	
			
		
		}catch (Throwable t) {
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		return strMemYn;
	}
	/**
	 * ����ȸ���� ����� �Ǵ�
	 * @param session
	 * @param sso_id
	 */
	public String selectPeCoByCkNum(String account_id) {
		String strMemYn = "";
		Connection con = null;
		try{
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);			
			strMemYn = proc.selectCoByCkNum(con, account_id);		// Y����ȸ��	
			
		
		}catch (Throwable t) {
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		return strMemYn;
	}

	/**
	 * ���� ȸ���̸鼭 ����ī�� ȸ���� �ƴѻ�� �˾Ƴ���
	 * @param session
	 * @param sso_id
	 */
	public String selectCoNotCard(String account_id) {
		String strCkNum = "";
		Connection con = null;
		try{
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);			
			strCkNum = proc.selectByCkNum(con, account_id);
			
		
		}catch (Throwable t) {
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable ignored) {
			}
		}
		return strCkNum;
	}
	
	/**
	 * ���� ������ ����� ���� ����
	 * @param 	context		WaContext ��ü
	 * @param 	request	HttpServletRequest
	 * @param 	ett			��������� Entity
	 * @return 	void
	 * @TODO	������ �� ����� ���� ������ ��ȸ�Ͽ� ����� ������ �����Ѵ�.
	 */
	protected void checkJoltGolfCardOnly(WaContext context, HttpServletRequest request, GolfUserEtt ett,UcusrinfoEntity ucusrinfo) throws BaseException {
			
		Properties properties = new Properties();

		properties.setProperty("LOGIN", "Y");  					// �ʼ� �α׿� ���� �ش� ������ return RETURN_CODE Ű. ���� ���ϸ� "fml_ret1" ���
		properties.setProperty("RETURN_CODE", "fml_ret1");		// ���� Ư���� pool �� ����ϴ� �����.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");		
		
		properties.setProperty("SOC_ID", ett.getJuminNo()); 	// log ����ִ� �κп��� �ֹι�ȣ ��ü

		try {
						
			HashMap strBizNo = null;
			
			strBizNo = selectBizNo(ucusrinfo.getAccount(), "A");
			
			logger.debug("## checkJoltGolfCardOnly :: strBizNo.size : [" + strBizNo.size() +"]" );
			
			String getValue = (String)strBizNo.get(""+ 0 + "");
			
			logger.debug("## checkJoltGolfCardOnly :: getValue : [" + getValue +"]" );
								
			/** *****************************************************************
			 *Card������ �о����
			 ***************************************************************** */
			System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 ���� ȣ�� <<<<<<<<<<<<"+"\n");
			JoltInput cardInput_pt = new JoltInput(BSNINPT);
			cardInput_pt.setServiceName(BSNINPT);
				
			System.out.println("## GolfCtrlServ | checkJolt ����ī��(����) ����ȸ�� ���� | ID : "+ucusrinfo.getAccount()+" \n");
			cardInput_pt.setString("fml_trcode", "MHL0230R0100");
			cardInput_pt.setString("fml_arg1", "2");				// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
			cardInput_pt.setString("fml_arg2", "");					// �ֹι�ȣ
			cardInput_pt.setString("fml_arg3", getValue);				// ����ڹ�ȣ
			cardInput_pt.setString("fml_arg4", "2");				// 1.���� 2.���

			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult cardinfo_pt = null;
			
			String resultCode_pt = "";		

			ArrayList card_list = new ArrayList();
			ArrayList topGolfcard_list = new ArrayList();
			ArrayList richcard_list = new ArrayList();
			boolean existsData = false;
			String memClass = "4";
			
			String cardType = "";
			String joinName = "";
			String joinNo = "";
				
			cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
	
			resultCode_pt = cardinfo_pt.getString("fml_ret1");
			logger.debug("## resultCode_pt ::  " + resultCode_pt);
				
			if("00".equals(resultCode_pt)){
			
				while( cardinfo_pt.isNext() ) {
					
					if(!existsData){
						//System.out.println(" ī�� ett ���� cardNo : "+cardinfo_pt.getString("fml_ret3"));
						//System.out.println(" ī�� ett ���� JuminNo : "+ett.getJuminNo());
						memClass = "1";
						existsData = true;
					}
					
					cardinfo_pt.next();
					CardInfoEtt cardInfo = new CardInfoEtt();
					CardInfoEtt cardInfoTopGolf = new CardInfoEtt();
					CardInfoEtt cardInfoRich = new CardInfoEtt();
					
					cardType 	= cardinfo_pt.getString("fml_ret4");	//ī������ 1:����ī�� / 2:PTī�� / 3:�Ϲ�ī��
					joinName	= cardinfo_pt.getString("fml_ret7");	//ī���̸� 
					joinNo 		= cardinfo_pt.getString("fml_ret8");	//��ǻ�ڵ�						
								
					
//						- ��ǰ�� :  ���� ���� �÷�Ƽ������ī�� / �����ڵ�
//						 �� ���Ǿ����÷�Ƽ������_ĳ����     / 030478
//						 �� ���Ǿ����÷�Ƽ������_�ƽþƳ�  / 030481
//						 �� ���Ǿ����÷�Ƽ������_�����װ�  / 030494
//						 �� �泲���� Familyī��  / 394033
//					     * IBK APT �����̾�ī��-�Ϲ�(�����ڵ� : 740276) 
//					     * IBK APT �����̾�ī��-��ī���н�(�����ڵ� : 740289) 
//					     * IBK APT �����̾�ī��-�ƽþƳ�(�����ڵ� : 740292) 
					
					
					if("1".equals(cardType)){
						if("030478".equals(joinNo) || "030481".equals(joinNo) || "030494".equals(joinNo) || "030698".equals(joinNo) || "031189".equals(joinNo) || "031176".equals(joinNo) || "394033".equals(joinNo) || "740276".equals(joinNo) || "740289".equals(joinNo) || "740292".equals(joinNo)  )
							{																		
 
								cardInfo.setCardType(cardType);	// ī����������(Ret4)
							cardInfo.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
							cardInfo.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
							cardInfo.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
							cardInfo.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
							cardInfo.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
							cardInfo.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
							cardInfo.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����

//								logger.debug(" Ret4 | ī������ : "+ cardInfo.getCardType());
//								logger.debug(" Ret7 | ī���̸� : "+ cardInfo.getJoinName());
//								logger.debug(" Ret8 | �����ڵ� : "+ cardInfo.getJoinNo());
//								logger.debug(" Ret12 | ī�������� : "+ cardInfo.getAcctDay());
//								logger.debug(" Ret13 | ������ŷ���񽺽������� : "+ cardInfo.getGolfStartDay());
//								logger.debug(" Ret14 | ������ŷ������������ : "+ cardInfo.getGolfYn());

							card_list.add(cardInfo);
						}
						// ž����ī�� ���� ���� üũ 
						else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
						//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //�׽�Ʈ�� �������� �������
						{
							cardInfoTopGolf.setCardType(cardType);	// ī����������(Ret4)
							cardInfoTopGolf.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
							cardInfoTopGolf.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
							cardInfoTopGolf.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
							cardInfoTopGolf.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
							cardInfoTopGolf.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
							cardInfoTopGolf.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
							cardInfoTopGolf.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����
							
							topGolfcard_list.add(cardInfoTopGolf);
							System.out.println("## GolfCtrlServ | ž����ī�� ���� | ID : "+ett.getMemId()+"\n");
						}
						
						// ��ġ�ظ�ġ ī�� ���� ���� üũ 222781     , �׽�Ʈ���� 363271
						else if("222781".equals(joinNo) )
						{
							cardInfoRich.setCardType(cardType);	// ī����������(Ret4)
							cardInfoRich.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
							cardInfoRich.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
							cardInfoRich.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
							cardInfoRich.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
							cardInfoRich.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
							cardInfoRich.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
							cardInfoRich.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����
							
							richcard_list.add(cardInfoRich);
							System.out.println("## GolfCtrlServ | ��ġ�ظ�ġ ī�� ���� | ID : "+ett.getMemId()+"\n");
							
						}						
						
					}					

				}
				
				ucusrinfo.setmemberClssCard(memClass);				
				
			}else if("01".equals(resultCode_pt)){
								
				// ���ϰ��� 01�ϰ�� �ѹ� �� ������ ������.
				
				/** *****************************************************************
				 *Card������ �о����
				 ***************************************************************** */
				System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 ���� ȣ�� <<<<<<<<<<<<"+"\n");
				JoltInput cardInput_pt2 = new JoltInput(BSNINPT);
				cardInput_pt2.setServiceName(BSNINPT);
				
				System.out.println("## GolfCtrlServ | checkJolt ���ϰ��� 01�ϰ�� �ѹ� �� ������ ������. ����ȸ�� ���� | ID : "+ucusrinfo.getAccount()+" \n");
				cardInput_pt2.setString("fml_trcode", "MHL0230R0100");
				cardInput_pt2.setString("fml_arg1", "3");				// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
				cardInput_pt2.setString("fml_arg2", ucusrinfo.getSocid());	// �ֹι�ȣ
				cardInput_pt.setString("fml_arg3", getValue);				// ����ڹ�ȣ
				cardInput_pt2.setString("fml_arg4", "2");				// 1.���� 2.���
				
				cardinfo_pt = jt_pt.call(context, request, cardInput_pt2, prop_pt);			
				
				resultCode_pt = cardinfo_pt.getString("fml_ret1");
				logger.debug("## resultCode_pt ::  " + resultCode_pt);
				
				if("00".equals(resultCode_pt)){				
					
					while( cardinfo_pt.isNext() ) {
						
						if(!existsData){
							//System.out.println(" ī�� ett ���� cardNo : "+cardinfo_pt.getString("fml_ret3"));
							//System.out.println(" ī�� ett ���� JuminNo : "+ett.getJuminNo());
							memClass = "1";
							existsData = true;
						}
						
						cardinfo_pt.next();
						CardInfoEtt cardInfo = new CardInfoEtt();
						CardInfoEtt cardInfoTopGolf = new CardInfoEtt();
						CardInfoEtt cardInfoRich = new CardInfoEtt();
						
						cardType 	= cardinfo_pt.getString("fml_ret4");	//ī������ 1:����ī�� / 2:PTī�� / 3:�Ϲ�ī��
						joinName	= cardinfo_pt.getString("fml_ret7");	//ī���̸� 
						joinNo 		= cardinfo_pt.getString("fml_ret8");	//��ǻ�ڵ�
						
//							- ��ǰ�� :  ���� ���� �÷�Ƽ������ī�� / �����ڵ�
//							 �� ���Ǿ����÷�Ƽ������_ĳ����     / 030478
//							 �� ���Ǿ����÷�Ƽ������_�ƽþƳ�  / 030481
//							 �� ���Ǿ����÷�Ƽ������_�����װ�  / 030494
//							 �� �泲���� Familyī��  / 394033
//						     * IBK APT �����̾�ī��-�Ϲ�(�����ڵ� : 740276) 
//						     * IBK APT �����̾�ī��-��ī���н�(�����ڵ� : 740289) 
//						     * IBK APT �����̾�ī��-�ƽþƳ�(�����ڵ� : 740292) 
						
						
						if("1".equals(cardType)){
							
							if("030478".equals(joinNo) || "030481".equals(joinNo) || "030494".equals(joinNo) 
									|| "030698".equals(joinNo) || "031189".equals(joinNo) || "031176".equals(joinNo) 
									|| "394033".equals(joinNo) || "740276".equals(joinNo) || "740289".equals(joinNo) 
									|| "740292".equals(joinNo)  )
							{																		
 
								cardInfo.setCardType(cardType);	// ī����������(Ret4)
								cardInfo.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
								cardInfo.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
								cardInfo.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
								cardInfo.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
								cardInfo.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
								cardInfo.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
								cardInfo.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����

//									logger.debug(" Ret4 | ī������ : "+ cardInfo.getCardType());
//									logger.debug(" Ret7 | ī���̸� : "+ cardInfo.getJoinName());
//									logger.debug(" Ret8 | �����ڵ� : "+ cardInfo.getJoinNo());
//									logger.debug(" Ret12 | ī�������� : "+ cardInfo.getAcctDay());
//									logger.debug(" Ret13 | ������ŷ���񽺽������� : "+ cardInfo.getGolfStartDay());
//									logger.debug(" Ret14 | ������ŷ������������ : "+ cardInfo.getGolfYn());

								card_list.add(cardInfo);
								
							}
							// ž����ī�� ���� ���� üũ 
							else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
							//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //�׽�Ʈ�� �������� �������
							{
								cardInfoTopGolf.setCardType(cardType);	// ī����������(Ret4)
								cardInfoTopGolf.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
								cardInfoTopGolf.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
								cardInfoTopGolf.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
								cardInfoTopGolf.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
								cardInfoTopGolf.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
								cardInfoTopGolf.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
								cardInfoTopGolf.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����
								
								topGolfcard_list.add(cardInfoTopGolf);
								System.out.println("## GolfCtrlServ | ž����ī�� ���� | ID : "+ett.getMemId()+"\n");
							}
							
							// ��ġ�ظ�ġ ī�� ���� ���� üũ 222781     , �׽�Ʈ���� 363271
							else if("222781".equals(joinNo) )
							{
								cardInfoRich.setCardType(cardType);	// ī����������(Ret4)
								cardInfoRich.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
								cardInfoRich.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
								cardInfoRich.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
								cardInfoRich.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
								cardInfoRich.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
								cardInfoRich.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
								cardInfoRich.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����
								
								richcard_list.add(cardInfoRich);
								System.out.println("## GolfCtrlServ | ��ġ�ظ�ġ ī�� ���� | ID : "+ett.getMemId()+"\n");
								
							}							
							
						}						
	
					}
					
					ucusrinfo.setmemberClssCard(memClass);
					
				}else{
					
					logger.debug("## ���� ���� ���� ����");
					
				}
				
			}else{
				
				logger.debug("## ���� ���� ���� ����");
				
			}
			
			ett.setCardInfoList(card_list);
			ett.setTopGolfCardInfoList(topGolfcard_list);
			ett.setRichCardInfoList(richcard_list);
			
		} catch (TaoException te) {
			throw getErrorException("LOGIN_ERROR_0003",new String[]{"���� ���� ��ȸ ����"},te);     // Jolt ó�� ����
		}

	}
	
	/**
	 * ���� ������ ����� ���� ����
	 * @param 	context		WaContext ��ü
	 * @param 	request	HttpServletRequest
	 * @param 	ett			��������� Entity
	 * @return 	void
	 * @TODO	������ �� ����� ���� ������ ��ȸ�Ͽ� ����� ������ �����Ѵ�.
	 */
	protected void checkJolt(WaContext context, HttpServletRequest request, GolfUserEtt ett,UcusrinfoEntity ucusrinfo) throws BaseException {
			
		Properties properties = new Properties();

		properties.setProperty("LOGIN", "Y");  					// �ʼ� �α׿� ���� �ش� ������ return RETURN_CODE Ű. ���� ���ϸ� "fml_ret1" ���
		properties.setProperty("RETURN_CODE", "fml_ret1");		// ���� Ư���� pool �� ����ϴ� �����.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");
		properties.setProperty("SOC_ID", ett.getJuminNo()); 	// log ����ִ� �κп��� �ֹι�ȣ ��ü
		
		boolean corpJiJeong = false;

		try {
			
			//����ȸ������ üũ (����ī��) 
			String memSiteClss= ucusrinfo.getSiteClss();
			
			System.out.println("## GolfCtrlServ | ����ȸ������ üũ (����ī��) | ID : "+ucusrinfo.getAccount()+" | memSiteClss : "+memSiteClss+"\n");
			
			/*******************************************************************
			 *Card������ �о����
			 ***************************************************************** */
			System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 ���� ȣ�� <<<<<<<<<<<<"+"\n");
			
			JoltInput cardInput_pt = new JoltInput(BSNINPT);
			cardInput_pt.setServiceName(BSNINPT);

			ArrayList card_list = new ArrayList();
			ArrayList topGolfcard_list = new ArrayList();
			ArrayList richcard_list = new ArrayList();

			HashMap strBizNo = new HashMap();
			HashMap getValue = new HashMap();			
			
			/* ���� ���ϴ� �κ��� ������ �޼ҵ�� �����ϸ� ������
			 * ī������ �׽�Ʈ �����͸� ã�Ⱑ �������� �ʰ� ������ �� �� ���� �޼ҵ� ������ ���ϰ�
			 * ���������� ���� ����  ���� �����ϰ�.. �Ʒ�ó�� �ߺ� ó����..by �̰���
			 */
			
			//������ ��쿡�� ��������
			if("5".equals(memSiteClss)){
				
				logger.debug(" ## 11 GolfCtrlServ | ����ȸ���̹Ƿ� ���� | ID : "+ucusrinfo.getAccount()+" \n");
				
				System.out.println("## GolfCtrlServ | ����ȸ���̹Ƿ� ����ڹ�ȣ �������� | ID : "+ucusrinfo.getAccount()+" | memSiteClss : "+memSiteClss+"\n");
				strBizNo = selectBizNo(ucusrinfo.getAccount(), "B");
				
				logger.debug (" ## GolfCtrlServ | strBizNo.size : " + strBizNo.size());
							
				if (strBizNo.size()>0){ 
					
					/*20110422 ����  strBizNo (getValue.get("BUZ_NO"))�� ������ ���������� Ÿ���� ���־���
					 * ���� if ���ǿ� ���ͼ� ���ι�ȣ�� ������ ���������� Ÿ�°��� �ǹ̰� ���  ����������
					 * ���� ������ �־ ������ ���� �����ΰ�..??  ���߿� ���� �߻��ϸ�  ���� if �� ����üũ�ϴ��� �ٽ� ���� by �̰���
					 */
					
					for (int i=0; strBizNo.size()>i; i++){ 
						
						getValue = (HashMap)strBizNo.get(""+ i + "");
						
						logger.debug("## GolfCtrlServ | getValue : "+ i +",  [" + getValue.get("MEM_CLSS") +"], ["+  getValue.get("BUZ_NO")+ "]" );
				
						//����ī�� ��������						
						if("6".equals(getValue.get("MEM_CLSS"))){
						
							System.out.println("## GolfCtrlServ | checkJolt ����ī�� ����ȸ�� ���� | ID : "+ucusrinfo.getAccount()+" \n");
							cardInput_pt.setString("fml_trcode", "MHL0230R0100");
							cardInput_pt.setString("fml_arg1", "3");				// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
							cardInput_pt.setString("fml_arg2", ucusrinfo.getSocid());	// �ֹι�ȣ
							cardInput_pt.setString("fml_arg3", getValue.get("BUZ_NO").toString());				// ����ڹ�ȣ
							cardInput_pt.setString("fml_arg4", "2");				// 1.���� 2.���
							corpJiJeong = true;
							
						}else{
							
							System.out.println("## GolfCtrlServ | checkJolt ����ī�� ����ȸ�� ���� | ID : "+ucusrinfo.getAccount()+" \n");
							cardInput_pt.setString("fml_trcode", "MHL0230R0100");
							cardInput_pt.setString("fml_arg1", "2");				// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
							cardInput_pt.setString("fml_arg2", "");					// �ֹι�ȣ						
							cardInput_pt.setString("fml_arg3",getValue.get("BUZ_NO").toString());				// ����ڹ�ȣ
							cardInput_pt.setString("fml_arg4", "2");				// 1.���� 2.���
							corpJiJeong = false;
							
						}
						
						JtProcess jt_pt = new JtProcess();
						java.util.Properties prop_pt = new java.util.Properties();
						prop_pt.setProperty("RETURN_CODE","fml_ret1");
						
						TaoResult cardinfo_pt = null;
						String resultCode_pt = "";			
						
						boolean existsData = false;
						String memClass = "4";
						
						String cardType = "";
						String joinName = "";
						String joinNo = "";
						
						do {
							
							cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
					
							resultCode_pt = cardinfo_pt.getString("fml_ret1");
							logger.debug("## resultCode_pt ::  " + resultCode_pt);
							

							if ( !"00".equals(resultCode_pt) && !"02".equals(resultCode_pt) ) {		// 00 ����, 02 ������ȸ ����
								logger.debug("## ���� ���� ���� ����");
								//throw new ChkChgSocIdException(ett.getMemId(), ett.getJuminNo(), "02");
							}else{

								while( cardinfo_pt.isNext() ) {
									
									if(!existsData){
										//System.out.println(" ī�� ett ���� cardNo : "+cardinfo_pt.getString("fml_ret3"));
										//System.out.println(" ī�� ett ���� JuminNo : "+ett.getJuminNo());
										memClass = "1";
										existsData = true;
									}
									
									cardinfo_pt.next();
									CardInfoEtt cardInfo = new CardInfoEtt();
									CardInfoEtt cardInfoTopGolf = new CardInfoEtt();
									CardInfoEtt cardInfoRich = new CardInfoEtt();
									
									cardType 	= cardinfo_pt.getString("fml_ret4");	//ī������ 1:����ī�� / 2:PTī�� / 3:�Ϲ�ī��
									joinName	= cardinfo_pt.getString("fml_ret7");	//ī���̸� 
									joinNo 		= cardinfo_pt.getString("fml_ret8");	//��ǻ�ڵ�						
															
									
//									- ��ǰ�� :  ���� ���� �÷�Ƽ������ī�� / �����ڵ�
//									 �� ���Ǿ����÷�Ƽ������_ĳ����     / 030478
//									 �� ���Ǿ����÷�Ƽ������_�ƽþƳ�  / 030481
//									 �� ���Ǿ����÷�Ƽ������_�����װ�  / 030494
//									 �� �泲���� Familyī��  / 394033
//								     * IBK APT �����̾�ī��-�Ϲ�(�����ڵ� : 740276) 
//								     * IBK APT �����̾�ī��-��ī���н�(�����ڵ� : 740289) 
//								     * IBK APT �����̾�ī��-�ƽþƳ�(�����ڵ� : 740292) 
								
									
									if("1".equals(cardType)){
										
										try {
											logger.debug("#---corpJiJeong : " + corpJiJeong + ", joinNo : " + joinNo);
											//����Ʈ ��� ���IBK �����ڵ� (������ �ش�)
											if ( corpJiJeong && (joinNo.equals(AppConfig.getDataCodeProp("Basic"))
																||joinNo.equals(AppConfig.getDataCodeProp("Skypass"))
																||joinNo.equals(AppConfig.getDataCodeProp("AsianaClub")))){
												logger.debug("#---corpJiJeong2 : " + corpJiJeong);
												cardInfo.setCardType(cardType);	// ī����������(Ret4)
												cardInfo.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
												cardInfo.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
												cardInfo.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
												cardInfo.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
												cardInfo.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
												cardInfo.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
												cardInfo.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����

	//											logger.debug(" Ret4 | ī������ : "+ cardInfo.getCardType());
	//											logger.debug(" Ret7 | ī���̸� : "+ cardInfo.getJoinName());
	//											logger.debug(" Ret8 | �����ڵ� : "+ cardInfo.getJoinNo());
	//											logger.debug(" Ret12 | ī�������� : "+ cardInfo.getAcctDay());
	//											logger.debug(" Ret13 | ������ŷ���񽺽������� : "+ cardInfo.getGolfStartDay());
	//											logger.debug(" Ret14 | ������ŷ������������ : "+ cardInfo.getGolfYn());

												card_list.add(cardInfo);	
												
											}else if("030478".equals(joinNo) || "030481".equals(joinNo) 
													|| "030494".equals(joinNo) || "030698".equals(joinNo) 
													|| "031189".equals(joinNo) || "031176".equals(joinNo) 
													|| "394033".equals(joinNo) || "740276".equals(joinNo) 
													|| "740289".equals(joinNo) || "740292".equals(joinNo)  )
											{																		
 
												cardInfo.setCardType(cardType);	// ī����������(Ret4)
												cardInfo.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
												cardInfo.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
												cardInfo.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
												cardInfo.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
												cardInfo.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
												cardInfo.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
												cardInfo.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����

//											logger.debug(" Ret4 | ī������ : "+ cardInfo.getCardType());
//											logger.debug(" Ret7 | ī���̸� : "+ cardInfo.getJoinName());
//											logger.debug(" Ret8 | �����ڵ� : "+ cardInfo.getJoinNo());
//											logger.debug(" Ret12 | ī�������� : "+ cardInfo.getAcctDay());
//											logger.debug(" Ret13 | ������ŷ���񽺽������� : "+ cardInfo.getGolfStartDay());
//											logger.debug(" Ret14 | ������ŷ������������ : "+ cardInfo.getGolfYn());

												card_list.add(cardInfo);
											}
											// ž����ī�� ���� ���� üũ 
											else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
											//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //�׽�Ʈ�� �������� �������
											{
												cardInfoTopGolf.setCardType(cardType);	// ī����������(Ret4)
												cardInfoTopGolf.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
												cardInfoTopGolf.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
												cardInfoTopGolf.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
												cardInfoTopGolf.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
												cardInfoTopGolf.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
												cardInfoTopGolf.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
												cardInfoTopGolf.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����
												
												topGolfcard_list.add(cardInfoTopGolf);
												System.out.println("## GolfCtrlServ | ž����ī�� ���� | ID : "+ett.getMemId()+"\n");
											}
											
											// ��ġ�ظ�ġ ī�� ���� ���� üũ 222781     , �׽�Ʈ���� 363271
											else if("222781".equals(joinNo) )
											{
												cardInfoRich.setCardType(cardType);	// ī����������(Ret4)
												cardInfoRich.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
												cardInfoRich.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
												cardInfoRich.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
												cardInfoRich.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
												cardInfoRich.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
												cardInfoRich.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
												cardInfoRich.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����
												
												richcard_list.add(cardInfoRich);
												System.out.println("## GolfCtrlServ | ��ġ�ظ�ġ ī�� ���� | ID : "+ett.getMemId()+"\n");
												
											}
										} catch (IOException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										
									}
									
				
								}
								ucusrinfo.setmemberClssCard(memClass);
								
								
							}
							
						} while ("02".equals(resultCode_pt));
						
					}
					
				}
				
			}else{
				
				logger.debug(" ## 11 GolfCtrlServ | ����ȸ�� ���� | ID : "+ucusrinfo.getAccount()+" \n");
				
				System.out.println("## GolfCtrlServ | ����ȸ�� ���� | ID : "+ucusrinfo.getAccount()+" \n");
				cardInput_pt.setString("fml_trcode", "MHL0230R0100");
				cardInput_pt.setString("fml_arg1", "1");				// 1.�ֹι�ȣ 2.����ڹ�ȣ 3.��ü(�������ֹι�ȣ+�����)
				cardInput_pt.setString("fml_arg2", ett.getJuminNo());	// �ֹι�ȣ
				cardInput_pt.setString("fml_arg3", " ");				// ����ڹ�ȣ
				cardInput_pt.setString("fml_arg4", "1");				// 1.���� 2.���
				
				
				JtProcess jt_pt = new JtProcess();
				java.util.Properties prop_pt = new java.util.Properties();
				prop_pt.setProperty("RETURN_CODE","fml_ret1");
				
				TaoResult cardinfo_pt = null;
				String resultCode_pt = "";			
				

				boolean existsData = false;
				String memClass = "4";
				
				String cardType = "";
				String joinName = "";
				String joinNo = "";
				
				do {
					
					cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
			
					resultCode_pt = cardinfo_pt.getString("fml_ret1");
					logger.debug("## resultCode_pt ::  " + resultCode_pt);
					

					if ( !"00".equals(resultCode_pt) && !"02".equals(resultCode_pt) ) {		// 00 ����, 02 ������ȸ ����
						logger.debug("## ���� ���� ���� ����");
						//throw new ChkChgSocIdException(ett.getMemId(), ett.getJuminNo(), "02");
					}else{

						while( cardinfo_pt.isNext() ) {
							
							if(!existsData){
								//System.out.println(" ī�� ett ���� cardNo : "+cardinfo_pt.getString("fml_ret3"));
								//System.out.println(" ī�� ett ���� JuminNo : "+ett.getJuminNo());
								memClass = "1";
								existsData = true;
							}
							
							cardinfo_pt.next();
							CardInfoEtt cardInfo = new CardInfoEtt();
							CardInfoEtt cardInfoTopGolf = new CardInfoEtt();
							CardInfoEtt cardInfoRich = new CardInfoEtt();
							
							cardType 	= cardinfo_pt.getString("fml_ret4");	//ī������ 1:����ī�� / 2:PTī�� / 3:�Ϲ�ī��
							joinName	= cardinfo_pt.getString("fml_ret7");	//ī���̸� 
							joinNo 		= cardinfo_pt.getString("fml_ret8");	//��ǻ�ڵ�						
													
							
//							- ��ǰ�� :  ���� ���� �÷�Ƽ������ī�� / �����ڵ�
//							 �� ���Ǿ����÷�Ƽ������_ĳ����     / 030478
//							 �� ���Ǿ����÷�Ƽ������_�ƽþƳ�  / 030481
//							 �� ���Ǿ����÷�Ƽ������_�����װ�  / 030494
//							 �� �泲���� Familyī��  / 394033
//						     * IBK APT �����̾�ī��-�Ϲ�(�����ڵ� : 740276) 
//						     * IBK APT �����̾�ī��-��ī���н�(�����ڵ� : 740289) 
//						     * IBK APT �����̾�ī��-�ƽþƳ�(�����ڵ� : 740292) 
							
							
							if("1".equals(cardType)){
								if("030478".equals(joinNo) || "030481".equals(joinNo) || "030494".equals(joinNo) || "030698".equals(joinNo) || "031189".equals(joinNo) || "031176".equals(joinNo) || "394033".equals(joinNo) || "740276".equals(joinNo) || "740289".equals(joinNo) || "740292".equals(joinNo)  )
								{																		
	 
									cardInfo.setCardType(cardType);	// ī����������(Ret4)
									cardInfo.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
									cardInfo.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
									cardInfo.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
									cardInfo.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
									cardInfo.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
									cardInfo.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
									cardInfo.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����

//									logger.debug(" Ret4 | ī������ : "+ cardInfo.getCardType());
//									logger.debug(" Ret7 | ī���̸� : "+ cardInfo.getJoinName());
//									logger.debug(" Ret8 | �����ڵ� : "+ cardInfo.getJoinNo());
//									logger.debug(" Ret12 | ī�������� : "+ cardInfo.getAcctDay());
//									logger.debug(" Ret13 | ������ŷ���񽺽������� : "+ cardInfo.getGolfStartDay());
//									logger.debug(" Ret14 | ������ŷ������������ : "+ cardInfo.getGolfYn());

									card_list.add(cardInfo);
								}
								// ž����ī�� ���� ���� üũ 
								else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "363271".equals(joinNo) || "111067".equals(joinNo)  )
								//else if("212501".equals(joinNo) || "060419".equals(joinNo) || "220111".equals(joinNo) || "111067".equals(joinNo)  ) //�׽�Ʈ�� �������� �������
								{
									cardInfoTopGolf.setCardType(cardType);	// ī����������(Ret4)
									cardInfoTopGolf.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
									cardInfoTopGolf.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
									cardInfoTopGolf.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
									cardInfoTopGolf.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
									cardInfoTopGolf.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
									cardInfoTopGolf.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
									cardInfoTopGolf.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����
									
									topGolfcard_list.add(cardInfoTopGolf);
									System.out.println("## GolfCtrlServ | ž����ī�� ���� | ID : "+ett.getMemId()+"\n");
								}
								
								// ��ġ�ظ�ġ ī�� ���� ���� üũ 222781     , �׽�Ʈ���� 363271
								else if("222781".equals(joinNo) )
								{
									cardInfoRich.setCardType(cardType);	// ī����������(Ret4)
									cardInfoRich.setJoinName(joinName);	// ���޾�ü��(Ret7)																								
									cardInfoRich.setJoinNo(joinNo);		// ���޾�ü��ȣ(Ret8)																
									cardInfoRich.setCardNo(cardinfo_pt.getString("fml_ret2"));			//ī���ȣ
									cardInfoRich.setBankNo(cardinfo_pt.getString("fml_ret5"));			//�����ڵ�
									cardInfoRich.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//ī��������
									cardInfoRich.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//������ŷ���� ��������
									cardInfoRich.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//������ŷ���� ����
									
									richcard_list.add(cardInfoRich);
									System.out.println("## GolfCtrlServ | ��ġ�ظ�ġ ī�� ���� | ID : "+ett.getMemId()+"\n");
									
								}
								
								
							}
							
		
						}
						ucusrinfo.setmemberClssCard(memClass);
						
						
					}
					
				} while ("02".equals(resultCode_pt));
					
				
			}
			
			ett.setCardInfoList(card_list);
			ett.setTopGolfCardInfoList(topGolfcard_list); 
			ett.setRichCardInfoList(richcard_list); 

//			CardInfoEtt cardInfo = new CardInfoEtt();
//			cardInfo.setCardType("1");	// ī����������(Ret4)
//			cardInfo.setJoinName("IBK APT �����̾�ī��-�Ϲ�");	// ���޾�ü��(Ret7)																								
//			cardInfo.setJoinNo("740276");		// ���޾�ü��ȣ(Ret8)							
//			card_list.add(cardInfo);			
			
		} catch (TaoException te) {
			throw getErrorException("LOGIN_ERROR_0003",new String[]{"���� ���� ��ȸ ����"},te);     // Jolt ó�� ����
		}

	}
	
	
	/**
	 * ���� ������ ����� ���� ����
	 * @param 	context		WaContext ��ü
	 * @param 	request	HttpServletRequest
	 * @param 	ett			��������� Entity
	 * @return 	void
	 * @TODO	������ �� ����� ���� ������ ��ȸ�Ͽ� ����� ������ �����Ѵ�.
	 */
	protected void checkJoltNh(WaContext context, HttpServletRequest request, GolfUserEtt ett,UcusrinfoEntity ucusrinfo) throws BaseException {
		
		Properties properties = new Properties();

		properties.setProperty("LOGIN", "Y");  					// �ʼ� �α׿� ���� �ش� ������ return RETURN_CODE Ű. ���� ���ϸ� "fml_ret1" ���
		properties.setProperty("RETURN_CODE", "fml_ret1");		// ���� Ư���� pool �� ����ϴ� �����.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");
		properties.setProperty("SOC_ID", ett.getJuminNo()); 	// log ����ִ� �κп��� �ֹι�ȣ ��ü

		try {

			/** *****************************************************************
			 *Card������ �о����
			 ***************************************************************** */
			System.out.println("## GolfCtrlServ | 1. Jolt NTC0080R2500 ���� ȣ�� <<<<<<<<<<<<");
			JoltInput cardInput_pt = new JoltInput(BSNINPT);
			cardInput_pt.setServiceName(BSNINPT);
			cardInput_pt.setString("fml_trcode", "NTC0080R2500");
			cardInput_pt.setString("fml_arg1", ett.getJuminNo());	// �ֹι�ȣ
			//cardInput_pt.setString("fml_arg1", "8108212473818");	// �ֹι�ȣ

			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult cardinfo_pt = null;
			//String resultCode_pt = "";

			// ī������ ���� ���� ����
			ArrayList card_list = new ArrayList();
			
			cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
			//resultCode_pt = cardinfo_pt.getString("fml_ret1"); 
			
			if(cardinfo_pt!=null){
				//if(resultCode_pt.equals("02")){ 
					while( cardinfo_pt.isNext() ) {
						
						cardinfo_pt.next();
						CardNhInfoEtt cardNhInfo = new CardNhInfoEtt();
						
						cardNhInfo.setCardGubun(cardinfo_pt.getString("fml_ret1"));
						cardNhInfo.setCardNo(cardinfo_pt.getString("fml_ret2"));
						cardNhInfo.setCardNm(cardinfo_pt.getString("fml_ret3"));
						cardNhInfo.setCardGrade(cardinfo_pt.getString("fml_ret4"));
						cardNhInfo.setCardType(cardinfo_pt.getString("fml_ret5"));
						cardNhInfo.setJuminNo(ett.getJuminNo());
	
//						logger.debug(" Ret1 | ���� ���� : 01:�� 02:ä�� 03:all | "+ cardInfo.getCardGubun());
//						logger.debug(" Ret2 | ī���ȣ | "+ cardInfo.getCardNo());
//						logger.debug(" Ret3 | ����ī��� | "+ cardInfo.getCardNm());
//						logger.debug(" Ret4 | ī���� | "+ cardInfo.getCardGrade());
//						logger.debug(" Ret5 | ī������ : 03:ƼŸ�� 12:�÷�Ƽ�� | "+ cardInfo.getCardType());
	
						card_list.add(cardNhInfo);
	
					} 
					//ucusrinfo.setmemberClssCard(memClass);	// ��ȸ�� ī��� üũ 
				//}
			}
			ett.setCardNhInfoList(card_list);

			
		} catch (TaoException te) {
			throw getErrorException("LOGIN_ERROR_0003",new String[]{"���� ���� ��ȸ ����"},te);     // Jolt ó�� ����
		}

	}
	
	/**
	 * VIPī�� ���� ���� ��ȸ
	 * @param 	context		WaContext ��ü
	 * @param 	request	HttpServletRequest
	 * @param 	ett			��������� Entity
	 * @return 	void
	 * @TODO	VIPī�� üũ
	 */
	protected void checkJoltVip(WaContext context, HttpServletRequest request, GolfUserEtt ett,UcusrinfoEntity ucusrinfo) throws BaseException {

		Properties properties = new Properties();

		properties.setProperty("LOGIN", "Y");  					// �ʼ� �α׿� ���� �ش� ������ return RETURN_CODE Ű. ���� ���ϸ� "fml_ret1" ���
		properties.setProperty("RETURN_CODE", "fml_ret1");		// ���� Ư���� pool �� ����ϴ� �����.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");
		properties.setProperty("SOC_ID", ett.getJuminNo()); 	// log ����ִ� �κп��� �ֹι�ȣ ��ü

		try {

			/** *****************************************************************
			 *Card������ �о����
			 ***************************************************************** */
			System.out.println("## GolfCtrlServ VIPī�� | 1. VIPī�� Jolt MHL0200R0200 ���� ȣ�� <<<<<<<<<<<<"+"\n");
						
			String joltFmlTrCode016 = "MHL0200R0200";
			String joltServiceName = "BSNINPT";
			JoltInput jtInput = new JoltInput(joltServiceName);
			jtInput.setServiceName(joltServiceName);
			jtInput.setString("fml_trcode", joltFmlTrCode016);			
			
			jtInput.setString("fml_arg1",	ett.getJuminNo());	//�ֹι�ȣ ��ȸ
			//jtInput.setString("fml_arg1",	"6709201886675");
			
			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult jtResult = jt_pt.call(context, request, jtInput, properties);			
			String retCode = jtResult.getString("fml_ret1").trim();
			
			//System.out.println("## retCode : "+retCode+"\n");
			
			String vipGrade 	= "";
			String vipMaxGrade	= "";
			String vipCardNo	= "";
			String vipJoinNo	= "";
			String selfCk		= "";
			ArrayList vipCardList = new ArrayList();
			
			// �Ϲ�ī�� �Ǵ� ī�� ����
			if( retCode.equals("01")) {
			    //"PTȸ���� �ƴ� ó��";
			}
			else if( !retCode.equals("00")) {
				//��Ÿ �ٸ� �����ϰ�� Skip ó����;	
			}
			else if( retCode.equals("00")) { //PTī�� �������ϰ��
			
				
				
				while( jtResult.isNext() ) 
				{
					jtResult.next();
					vipGrade 	= jtResult.getString("fml_ret9");
					vipJoinNo 	= jtResult.getString("fml_ret10");
					selfCk	 	= jtResult.getString("fml_ret6");	//���ο��� 1:����,2:����,3:����,4:����
					
					
					
					//�Ϲ�ī��� �����ϰ�
					if(vipGrade.compareTo("00") > 0) {
					
						System.out.println("## VIPCARD ID : "+ett.getMemId()+" | selfCk : "+selfCk+" | vipJoinNo : "+vipJoinNo+" | vipGrade : "+vipGrade+" | vipMaxGrade : "+vipMaxGrade+"\n");
											

						//ī������ 03 / 12 / 30 / 91 �� ��쿡�� VIPī��� �ν�
						if( "03".equals(vipGrade) || "12".equals(vipGrade) || "30".equals(vipGrade) || "91".equals(vipGrade)  )
						{
							// VIP ���� ��ǰ 6��
							// �ϳ��������ī�� 362188, �츮���� Skypass s-Oilī��(����) 243375 , �츮���� Skypass s-Oilī��(����ſ�) 243388 
							// �츮���� Skypass s-Oilī��(���� ��������)e-pt 246246, �츮���� Skypass s-Oilī��(New_���) 202277, �츮���� Skypass s-Oilī��(New_����) 202264
						
							if( "362188".equals(vipJoinNo) || "243375".equals(vipJoinNo) || "243388".equals(vipJoinNo) || "246246".equals(vipJoinNo) || "202277".equals(vipJoinNo) || "202264".equals(vipJoinNo) )
							{
								System.out.println("## VIPCARD ������ ���ܻ�ǰ6���߿� ���� ���ܵ� ID : "+ett.getMemId()+" | vipJoinNo : "+vipJoinNo+"\n");
							}
							else
							{
								
								if("03".equals(vipGrade) || "12".equals(vipGrade))
								{
									if("1".equals(selfCk) || "3".equals(selfCk) )	//����/������ ��� 
									{
										
										CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
										
										cardVipInfo.setBankNo(jtResult.getString("fml_ret5"));
										cardVipInfo.setCardNo(jtResult.getString("fml_ret3"));
										cardVipInfo.setCardType(jtResult.getString("fml_ret6"));
										//cardVipInfo.setJoinNo(jtResult.getString("fml_ret8"));
										cardVipInfo.setJoinName(jtResult.getString("fml_ret7"));
										cardVipInfo.setAcctDay(jtResult.getString("fml_ret11"));
										cardVipInfo.setCardAppType(jtResult.getString("fml_ret17"));
										cardVipInfo.setExpDate(jtResult.getString("fml_ret13"));
										cardVipInfo.setAppDate(jtResult.getString("fml_ret14"));
										cardVipInfo.setLastCardNo(jtResult.getString("fml_ret15"));
										//cardVipInfo.setSocId(jtResult.getString("fml_ret16"));
										cardVipInfo.setCardJoinDate(jtResult.getString("fml_ret18"));		//�߱޳�¥
										cardVipInfo.setVipGrade(vipGrade);
										
										vipCardList.add(cardVipInfo);
										
										
										
									}
									else
									{
										System.out.println("## VIPī�������� ����ī�尡 �ƴ϶� ���ܵ� \n");
									}
									
								}else
								{
									CardVipInfoEtt cardVipInfo = new CardVipInfoEtt();
									
									cardVipInfo.setBankNo(jtResult.getString("fml_ret5"));
									cardVipInfo.setCardNo(jtResult.getString("fml_ret3"));
									cardVipInfo.setCardType(jtResult.getString("fml_ret6"));
									//cardVipInfo.setJoinNo(jtResult.getString("fml_ret8"));
									cardVipInfo.setJoinName(jtResult.getString("fml_ret7"));
									cardVipInfo.setAcctDay(jtResult.getString("fml_ret11"));
									cardVipInfo.setCardAppType(jtResult.getString("fml_ret17"));
									cardVipInfo.setExpDate(jtResult.getString("fml_ret13"));
									cardVipInfo.setAppDate(jtResult.getString("fml_ret14"));
									cardVipInfo.setLastCardNo(jtResult.getString("fml_ret15"));
									//cardVipInfo.setSocId(jtResult.getString("fml_ret16"));
									cardVipInfo.setVipGrade(vipGrade);
									
									vipCardList.add(cardVipInfo);
								}
							
								
								//�ְ��޸�
								if( vipMaxGrade.compareTo(vipGrade) < 0) {
									vipMaxGrade = vipGrade;
									vipCardNo		= jtResult.getString("fml_ret3");							
									//info("vipMaxGrade:"+vipMaxGrade);
								}
								
							}
							
							
							
							
							
						}
						else
						{
							System.out.println("## VIP ī������ �ƴ� ID : "+ett.getMemId()+" | vipGrade : "+vipGrade+"\n");
						}
						
						
						
					}
					
				}
				System.out.println("## vipMaxGrade  ID : "+ett.getMemId()+" | ������ : "+vipMaxGrade+"\n");
								
				//ett.setVipCardExpDate(vipCardExpDate);
			
			}
						
			ett.setCardVipInfoList(vipCardList);
			ett.setVipMaxGrade(vipMaxGrade);
			ett.setVipCardNo(vipCardNo);
			
		} catch (TaoException te) {
			throw getErrorException("LOGIN_ERROR_0003",new String[]{"TOPī�� ���� ��ȸ ����"},te);     // Jolt ó�� ����
		}

	}	
	
	
	
	
	
	
	
	
	
	
	public void byPassed(boolean passed,HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		
		//logger.debug("byPassed psssed :: " + passed);		 
		
		if (passed) {
			
			String includeRequestUriAttribute = (String) request.getAttribute(INCLUDE_REQUEST_URI_ATTRIBUTE);
						
			
			if (includeRequestUriAttribute != null) {
				
				//logger.debug("includeRequestUriAttribute:" + includeRequestUriAttribute);

				String actionKey = getActionKey1(includeRequestUriAttribute);

				ActionResponse actionResponse = act(request, response, actionKey);

				try {
					RunActionInfo info = (RunActionInfo) request.getAttribute("com.bccard.waf.core.RunActionInfo");
					if (info != null) {
						info.setResponse(actionResponse);
					}

					RequestDispatcher rd = request.getRequestDispatcher(actionResponse.getContents());
					rd.include(request, response);
				} catch (Throwable t) {
					gotoErrorPage(request, response, t);
				}
				
			} else {
				
				//logger.debug(":: ���� URL ���� :: ");		
				execute(request, response);
				//ActionResponse actionResponse = act(request, response);
				//view(request,response,actionResponse);
			}

		}

	}
	public String getActionKey1(String requestUrl) {
		String actionkey = "";
		if (requestUrl != null) {
			int i = requestUrl.lastIndexOf("/");
			int k = requestUrl.lastIndexOf("?");

			if (i >= 0) {
				if (k >= 0 && i < k) {
					actionkey = requestUrl.substring(i + 1, k);
				} else {
					actionkey = requestUrl.substring(i + 1);
				}
				int j = actionkey.indexOf(Pattern);
				if (j >= 0) {
					actionkey = actionkey.substring(0, j);
				}
			}
		}
		return actionkey;
	}

	/**
	 * ���� ���� (TOP ����Ʈ, ����� �ּ�)�� �����Ѵ�.
	 * 
	 * @param ucusrinfo ����� ����
	 
	private void setPrivateInfo(WaContext context, UcusrinfoEntity ucusrinfo) throws BaseException {
		String ssn = ucusrinfo.getSocid();
		
		SettlementTao settlementTao = (SettlementTao) context.getProc("SettlementTao");
		
		int topPoint = 0;
		
		// TOP ����Ʈ ��ȸ
		ServiceResult topPointResult = settlementTao.inquireTopPoint(ssn);
		if (topPointResult.isSuccess()) {
			topPoint = Integer.parseInt(topPointResult.getValue1());
		}

		// �ּ� ��ȸ
		UserAddress userAddress = settlementTao.findUserAddress(ssn);
		
		ucusrinfo.setBcTopPoint(topPoint);
		ucusrinfo.setUserAddress(userAddress);
	}
	 */

	// ���߱⿡�� ���
	/** ****************************************************************************
     * goLoginPage
     * @param response      response
     * @param uurl			uurl
     * @param storeLogin     storeLogin
     **************************************************************************** */
	public void goLoginPage(HttpServletResponse response, String uurl) throws Exception {
		CookieManager.addCookie(SECode.USER_URL, uurl, sso_domain, response);
		CookieManager.addCookie(SECode.R_TOA, toa, sso_domain, response);
		//String jumpUrl = "/includeSecure.bc?url=";
		String jumpUrl = "/app/golfloung/includeSecure.do?url=";
			jumpUrl +=  nls_login_url + "&RTOA="+toa+"&UURL=" + uurl;
		response.sendRedirect(jumpUrl);
	}
	// ��⿡�� ���
	/** ****************************************************************************
     * goLoginPage
     * @param response      response
     * @param uurl			uurl
     * @param storeLogin     storeLogin
     **************************************************************************** */
	public void goLoginPage(HttpServletRequest request, HttpServletResponse response, String uurl) throws Exception {
		
		sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
		
		CookieManager.addCookie(SECode.USER_URL, uurl, sso_domain, response);
		CookieManager.addCookie(SECode.R_TOA, toa, sso_domain, response);
		String toNLS = null;
		//String jumpUrl = "/includeSecure.bc?url=";
		String jumpUrl = "/app/golfloung/includeSecure.do?url=";

		try {
			toNLS = (String)hashBalancer.lookup(request);
			jumpUrl += toNLS+"/nls3/cookieSignin.jsp?FORM=10&RTOA="+toa+"&UURL="+uurl;
			response.sendRedirect(jumpUrl);
		} catch (Exception nrse) {

			
		}

	}

	/** ****************************************************************************
     * getEamSessionCheck
     * @param response      request
     * @param error_code     response
     **************************************************************************** */
	public String getEamSessionCheck(HttpServletRequest request, HttpServletResponse response) {

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
	/** ****************************************************************************
     * goErrorPage
     * @param response      response
     * @param error_code     error_code
     **************************************************************************** */
	public void goErrorPage(HttpServletResponse response, int error_code) throws Exception {
//		BcLog.accessLog("error_code:"+error_code);
//		BcLog.accessLog("NLS_ERROR_URL:"+nls_error_url);
		CookieManager.removeNexessCookie(sso_domain, response);
		response.sendRedirect(nls_error_url + "?errorCode=" + error_code);
	}
	/** ****************************************************************************
	 * getSsoId
	 * @param request	  HttpServletRequest
	 **************************************************************************** */
	public String getSsoId(HttpServletRequest request) {
		return CookieManager.getCookieValue(SECode.USER_ID, request);
	}
    
    
	 /** *****************************************************************
	 * etaxAccessLog method : �׼��� �α�
	 * @param request					HttpServletRequest Object
	 * @param response	 				HttpServletResponse Object
	 * @param respon	 				ActionResponse Object
	 * @param resultPermissionCheck 	boolean
	 * @return							N/A
	 * @exception 						N/A
	 ***************************************************************** */
    private void golfAccessLog(HttpServletRequest request
                               ,HttpServletResponse response
                               ,ActionResponse respon
                               ,String type
							   ,GolfSessionEtt ett
							   ,boolean resultPermissionCheck) {
        String actnkey = null;
        String islog = "";
        try {
            actnkey = getActionKey(request);
            islog = getActionParam(actnkey, "ACCESS_LOG_ONOFF");
        } catch (Throwable t) {
           
        }

        if ( "off".equals(islog) ) return;

        String logs = "";   // ���� IP
        try {
            InetAddress iaddress = InetAddress.getLocalHost(); 
            logs = iaddress.getHostAddress();
        } catch(Throwable t) {
            logs = "unknown Server";
        }

		StringBuffer logBuff = new StringBuffer();
        logBuff.append("|").append( type );
		logBuff.append("|").append( logs );

        // Ŭ���̾�Ʈ IP
        logBuff.append("|").append( request.getRemoteAddr() );
        
        // ��Ʈ�ѷ�������
        try {
            logBuff.append("|").append( getServletName() );
        } catch(Throwable t) {
            logBuff.append("|unknown");
        }

       // ���������
        if ( ett == null ) {
            logBuff.append("|unknownUser");
        } else {
            if ( ett.isLogin() ) {                
            	logBuff.append("|").append(ett.getMemId().trim());
                logBuff.append("|").append(StrUtil.isNull(ett.getMemNm(),"").trim());                
            } else {
                logBuff.append("|unknownUser");
            }
        }

        // �׼�Ű
        logBuff.append("|").append( actnkey );

        // ���� ������ ����
        if ( resultPermissionCheck ) {
            logBuff.append("|");
            if ( respon != null ) {
                logBuff.append( respon.getContentInfo());
            } else {
                logBuff.append(getWelcomePage());
            }
           logBuff.append("|");
		} else {
            logBuff.append("|PERMISSION DENY|");
        }
     
       // BcLog.adminLog( logBuff.toString() );
		BcLog.accessLog( logBuff.toString() );

    }
    
    


	 /**
	 * ������ ��ȯ�� Exception ��ȯ
	 * @param 	msgkey	��� �޼��� Key
	 * @param 	args			����޼����� ǥ���� arguement
	 * @param 	t				Throwable
	 * @return 	ResultException
	 * @TODO ����̹��� error �� ����
	 */
	private ResultException getErrorException(String msgkey,String[] args,Throwable t) {
		String msgKey = msgkey;
		String[] msgArgs = args;
		if ( msgKey == null ) msgKey = "LOGIN_ERROR";
		if ( msgArgs == null ) msgArgs = new String[]{};

		ResultException exception= null;
		if ( t != null ) {
			exception = new ResultException(msgKey,msgArgs, t);
		} else {
			exception = new ResultException(msgKey,msgArgs);
		}
		exception.setTitleImage("error");
		exception.setTitleText("��ī�� �α���");
		return exception;
	}

	/**
	 * �÷��� �������� ���θ� üũ | 2009.10.29 | �ǿ���  
	 * 
	 * @param con ����
	 * @param account ����
	 * @return ����� ����
	 * @throws BaseException ���ܰ� �߻��ϴ� ���
	 */
	public HashMap selectBizNo(String account, String gubun) throws BaseException {
		
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			Connection con = null;
			
			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	D.MEM_CLSS, D.BUZ_NO");
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
			sb.append(" WHERE");
			sb.append(" 	A.ACCOUNT = ? AND D.MEM_STAT = '2' AND D.SEC_DATE IS NULL ");

			String query = sb.toString();

			String coChk ="";
			HashMap valueStore = new HashMap();
			
			try {
				
				con = this.waContext.getDbConnection("default", null);		
				pstmt = con.prepareStatement(query);

				int i = 1, j = 0;

				pstmt.setString(i++, account);
				rs = pstmt.executeQuery();
				
				if ( gubun.equals("B")){
					
					if ( rs != null ) {
						
						while (rs.next()) {
							
							HashMap setValue = new HashMap();
							
							setValue.put("MEM_CLSS", rs.getString("MEM_CLSS"));
							setValue.put("BUZ_NO", rs.getString("BUZ_NO"));

							valueStore.put(""+ j++ +"", setValue);
							
						}
						
					}
					
				}else {
					
					if (rs.next()) {
						coChk= rs.getString("BUZ_NO");
						valueStore.put("0", coChk);
					}
					
				}
				
			}catch (Throwable t) {
			} finally {
				try {
					if (con != null)
						con.close();
				} catch (Throwable ignored) {
				}
			}
			
			return valueStore;
			
		}
		
	
}
