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
package com.bccard.golf.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.sql.Connection;
import java.util.*;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.waf.common.StrUtil;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.user.dao.UcusrinfoDaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import com.bccard.waf.action.ControllerServlet;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.ActionResponse;
import com.bccard.waf.core.RunActionInfo;
import com.bccard.waf.core.WaContext;



import com.bccard.waf.common.*;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.golf.jolt.JtProcess;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;
import com.bccard.golf.common.GolfUserEtt;
import com.bccard.golf.common.login.*;


import com.bccard.golf.common.BcLog;
import com.bccard.golf.common.GolfSessionEtt;
import com.bccard.golf.common.GolfAdminEtt;

import com.bccard.golf.common.loginAction.SessionUtil;


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
 * 사용자 컨트롤러 서블릿 
 * @version 2006.12.27
 * @author  e4net
****************************************************************************************/
public class GolfAdmCtrlServ extends ControllerServlet {

	public static final String INCLUDE_REQUEST_URI_ATTRIBUTE = "javax.servlet.include.request_uri";

	public static final String Pattern = ".adm";

	private static final String LoginFormURI = "/app/golfloung/loginActn.adm";

	private static final String LoginProcessURI = "/app/golfloung/golfLogin.adm";

	private static final String PwChangeProcURI = "/app/golfloung/.adm";

	private static final String DefaultAdminURI = "golf.adm";
	
	/********************************************* 기 BCCARD 소스 패턴 카피  start *********************************************/
	/********************************************* 기 BCCARD 소스 패턴 카피  start *********************************************/
	private static boolean isInitialized = false;

	static Server sso01;
	static Server sso02;
	static Cluster cluster;
	static HeartBeatStorage storage;
	static HealthChecker checker;
	static LoadBalancer hashBalancer;
	static LoadBalancer roundrobinBalancer;
	static LoadBalancer randomBalancer;

	private static String	toa = "1";				//SSO 3rd/loginFormPage.jsp로 이동
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

	private static String conSSO = ""; //SSO 구분
	
	private static final String BSNINPT = "BSNINPT";					// 프레임웍 조회서비스
	private final String INIT_PAGE	= "9999999999999999";		//조회순번, 최초: 9*22.
	
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
			System.out.println("******************************************LOGIN1111");	
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

			 conSSO = "DEV";
			 
		} else if ( dr_was_1st.equals(ip) || dr_was_2nd.equals(ip) ) {		//DR
			System.out.println("******************************************LOGIN2222");	
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

			// SSO 분기
			if (ip.equals(dr_was_1st)) {
				 conSSO = "SSO1";
			} else if (ip.equals(dr_was_2nd)) {
				 conSSO = "SSO2";
			}
				
		} else {  // 운영기
			System.out.println("******************************************LOGIN3333");	
			sso01 = new ActiveServer(rl_sso_1st, new SimpleConnection(new SocketConnectionImpl()));
			sso02 = new ActiveServer(rl_sso_2nd, new SimpleConnection(new SocketConnectionImpl()));
			cluster = new Cluster();

			// SSO 분기
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
			 nls_login_url		= nls_url + "/nls3/cookieSignin.jsp?FORM=10";  // SSO로그인창 
			 nls_login_url5 	= nls_url	 + "/nls3/cookieSignin.jsp?FORM=10"; // long
			 ascp_url		= server_url + "/app/golfloung/view/initech/sso/login_exec.jsp";
		}
    }
	
    /********************************************* 기 BCCARD 소스 패턴 카피  end *********************************************/ 
	/********************************************* 기 BCCARD 소스 패턴 카피  end *********************************************/
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doProcess(request, response);
	} 

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		doProcess(request, response);
	}
	
	public void doProcess(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

		sso_domain = com.initech.eam.nls.NLSHelper.getCookieDomain(request);
		
		response.setHeader("P3P","CP='CAO PSA CONi OTR OUR DEM ONL'");
        // 클라이언트에 캐쉬를 남기지 않기 위한 부분
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/html; charset=euc-kr");
        request.setCharacterEncoding("euc-kr");
        
		
		GolfAdminEtt ett = null;
		boolean isPermiResult = true;
		String defActn = "";
		
		// 로그인 사용자 체크
		boolean passed = true;

		String requestURI = request.getRequestURI();
		HttpSession session = request.getSession();
		session.setAttribute("requestURI",requestURI);
				
		String actnkey = "";
		String global = null;                       // 글로발 액션 설정값
           
        ActionResponse respon = null;               // 괄과 응답할 ActionResponse
        
        String admAccess = "";        
        String actnParamKey = "";
        
     // 요청한 ActnKey set 
		try {
            actnkey = getActionKey(request);								//들어온 액션
            
            //logger.debug("액션보관 시작");
			session.setAttribute("actnkey", actnkey);					//액션보관
			
			logger.debug("actnkey:"+actnkey);
			          	            	
            	global			= getActionParam(actnkey,"GLOBAL_ACTION");     // 액션설정에서 글로발 액션 여부를 읽어온다.
 	            admAccess 		= getActionParam(actnkey, "ADMIN_ACCESS");			// 어드민 유무
 	            actnParamKey 	= getActionParam(actnkey,"layout");							// 공통파일
            	
            	request.setAttribute("layout", actnParamKey);
 				request.setAttribute("actnKey", actnkey);
 	            				
 				//logger.debug("actnParamKey:"+actnParamKey);
 				//logger.debug("admAccess:"+admAccess);
 				//logger.debug("globalActn:"+global);        
            
		} catch (Throwable t) {
            gotoErrorPage(request,response,t);
            return;
        }
		
			
		//관리자
		if ("Y".equals(admAccess)) {
					 
			//logger.debug("관리자실행");
			//logger.debug("requestURI :" + requestURI);
			//logger.debug("LoginFormURI :" + LoginFormURI);
			//logger.debug("LoginProcessURI :" + LoginProcessURI);
			//logger.debug("DefaultAdminURI :" + DefaultAdminURI);
			//logger.debug("PwChangeProcURI :" + PwChangeProcURI);
			ett = (GolfAdminEtt)session.getAttribute("SESSION_ADMIN");	// 관리자
			
			//logger.debug("ett:"+ett);
			if (ett == null) {
				
				//logger.debug("로그인이 안된 경우");
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
							
				//logger.debug("관리자 로그인이 된 경우");				
							
				passed = false;
				respon = act(request, response, actnkey);		
				view(request,response,respon);
						
			}
		
		}else{
			
			//logger.debug("사용자실행");
			//프론트일경우 시작. *************************************************************************************************			

			 String userAcount = "";
			 String sso_id = "";
			 
	        if ( global == null) global = "false";					//로그인 필수 처리

	        //logger.debug("세션체크 시작");
	       
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
	        
	        //logger.debug("세션체크중");
	        /*
	        if(ett == null) {
	        	ett = new GolfSessionEtt(); 
	        }else{
	        	userAcount = ett.getMemId();
	        } 
	        */
	        if(usrEntity == null) {
	        	
	        	usrEntity = new UcusrinfoEntity(); 
	        	System.out.print("## GolfCtrlServ | usrEntity null --> 생성 작업 \n");
	        }else{
	        	
	        	userAcount = usrEntity.getAccount();
	        	System.out.print("## GolfCtrlServ | usrEntity not null  \n");
	        } 
	        //logger.debug("userAcount:"+userAcount);
	        
	        //logger.debug("세션체크 끝");
	        
	        //logger.debug("SSO 시작");
	        
			sso_id = getSsoId(request);;		//SSO ID(사용자 ID, account) 
			sso_id = GolfUtil.sqlInjectionFilter(sso_id); 
			
			System.out.print("## GolfCtrlServ | SSO ID | sso_id :"+sso_id+"\n");
			
			//logger.debug("SSO ID :"+ sso_id);
			String retCode = null;							//SSO 세션 리턴값
			//logger.debug("ascp_url :"+ ascp_url);
			String uurl = ascp_url;								//이동할 URL, 일단 SSO 로그인 페이지
			String queryStr = "";
			int iretCode = 0;
			//SSO 조사
			
			//SSO ID가 없을경우 세션 삭제
			if("".equals(sso_id) || sso_id == null) 
			{
				System.out.print("## GolfCtrlServ | sso_id null  | sso_id :"+sso_id+"\n");
				if(!"".equals(userAcount) && !"Y".equals(strEnterCorporation))
				{
					System.out.print("## GolfCtrlServ | FRONT_ENTITY 세션 파괴 | sso_id :"+sso_id+" | strEnterCorporation : "+strEnterCorporation+"\n");
					session.setAttribute("FRONT_ENTITY", null);	
					//session.invalidate();
				}
				
			}

			
			
			//logger.debug("getEamSessionCheck 시작");
			retCode = getEamSessionCheck(request,response);
			
			//logger.debug("retCode :"+retCode);
			//logger.debug("getEamSessionCheck 끝");
			
			if(!retCode.trim().equals(""))	iretCode = Integer.parseInt(retCode);
			//SSO 리턴값 조사
			boolean isResult = retCode.compareTo("0")==0 ? true : false;
			//logger.debug("SSO 리턴값 isResult : "+isResult);						
			String reqParm = "";
			
			
			//////////////////////////////////////////////////////////////////////////////////////
			// 법인회원 자동로그인 추가
			// 법인플랫폼 세션이 존재하고 골프세션이 존재하지 않는 경우 자동로그인 시도
			String strMemCk = "N";
			if("Y".equals(strEnterCorporation) && ( "".equals(userAcount) || userAcount == null ) )  //플랫폼으로 들어온경우만 자동로그인 
			{
				 
				createFrontUserSessionCo( session, request, strEnterCorporationMemId);	
				
				
				// 법인지정이면서 골프회원인 경우 체크
				strMemCk = getChkMem( session, request, strEnterCorporationMemId);	//6이 지정카드 
				
				System.out.print("## GolfCtrlServ | 법인 지정카드 회원 체크 | strEnterCorporationMemId :"+strEnterCorporationMemId+" | strMemCk : "+strMemCk+"  \n");
				
				// 골프회원이면서 법인지정카드회원인 경우 로그인으로.
				if("6".equals(strMemCk))
				{					
					//logger.debug("쿠키정상, 세션없음, SSOID 있음 ==> WAS 세션 종료, SSO 세션 정상");
					session.setAttribute("SYSID", strEnterCorporationMemId);
					String parm = "" + request.getQueryString();
					if(!"".equals(parm))
						session.setAttribute("PARM", parm);
					if("".equals(actnkey) || actnkey == null) actnkey="golfIndex"; 
		
					System.out.print("## GolfCtrlServ | 법인회원(지정회원) 로그인으로 이동  | strEnterCorporationMemId :"+strEnterCorporationMemId+" | strMemCk : "+strMemCk+"  \n");
					
					session.setAttribute("UURL", "/app/golfloung/"+actnkey+".do");
					session.setAttribute("orgActionKey", actnkey);						
					response.sendRedirect("/app/golfloung/LoginCheck.do");
					return;
				}
				
			}		
			
			
	 		
	 		//logger.debug("global 처리 시작 :"+global);
			//글로벌 액션, 비로그인 처리 Action 진입
			if ( "true".equals( global ) ){
							
				//logger.debug("로그인 필요없음");
				
				//logger.debug("userAcount:"+userAcount);
				//logger.debug("sso_id:"+sso_id);
				
				if(userAcount != null)	userAcount=userAcount.trim();	
				
				
				if("".equals(userAcount) && sso_id != null && !sso_id.trim().equals(""))  {
					//logger.debug("세션생성");
					createFrontUserSession( session, request, sso_id);
					System.out.print("## GolfCtrlServ | 세션생성 | sso_id :"+sso_id+"\n");
					
					//logger.debug("쿠키정상, 세션없음, SSOID 있음 ==> WAS 세션 종료, SSO 세션 정상");
					
					// 법인 플랫폼 통해서 들어오지 않은 경우만 실행 					
					if(!"Y".equals(strEnterCorporation) &&  !"".equals(userAcount) )
					{
						
						System.out.print("## GolfCtrlServ | 법인 플랫폼 통해서 들어오지 않은 경우만 실행  | sso_id :"+sso_id+"\n");
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
				
			
				
								
				
				//logger.debug("isResult:"+isResult);
				//logger.debug("userAcount:"+userAcount);
				//logger.debug("actnkey:"+actnkey);
				//logger.debug("sso_id:"+sso_id);
				//logger.debug("쿠키나 세션 있는지 체크"); 
				//쿠키정상, 세션정상, 
				if ( isResult && !"".equals(userAcount) && sso_id != null && !"LoginCheck".equals(actnkey) ) {
					
					//logger.debug("쿠키,세션 있음");
					session.setAttribute("SYSID", sso_id);
					session.setAttribute("UURL", "/app/golfloung/"+actnkey+".do");
					passed = false;
					respon = act(request, response, actnkey);
				} else {
					
					//logger.debug("쿠키,세션 없거나 로그인 체크");
					
					if("LoginCheck".equals(actnkey)) {
						
						//logger.debug("LoginCheck");
						//로그인 후 점프하는 액션이 같은면 저장한 파라메터를 넘긴다. By PWT 20070903
						reqParm = "" + CookieManager.getCookieValue("REQ_PARM", request);
						if(!"".equals(reqParm) && reqParm != null && !"null".equals(reqParm)) {
						
							//logger.debug("암호화 처리 파라메터");
							
							//암호화 처리 파라메터
							if(reqParm.indexOf("BCENC") != -1)
								
								//session.setAttribute("PARM", "INIpluginData=" +	URLEncoder.encode(reqParm.substring(5,reqParm.length())));
								session.setAttribute("PARM", "INIpluginData=" +	URLEncoder.encode(reqParm.substring(5,reqParm.length()),"UTF-8"));
							
							else
								session.setAttribute("PARM",  reqParm);//일반파라메터(외부 로그인등)
							
						}
			 			CookieManager.removeCookie("REQ_JURL", sso_domain, response);
			 			CookieManager.removeCookie("REQ_PARM", sso_domain, response);
					}
					passed = false;
					respon = act(request, response);
					
					request.setAttribute("actnKey", actnkey );
				}
			}
			// 로그인 필수 Action 진입
			else {
				
				//logger.debug("로그인 필요있음");
				
				//logger.debug("쿠키 굽기"+actnkey);
				CookieManager.addCookie("GOLF_REQ_UURL", actnkey, sso_domain, response);
				//logger.debug("쿠키 굽기"+actnkey);
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
				
				// 쿠키정상, 세션정상, SSOID와 ACCOUNT 비교 정상
				if ( isResult && !"".equals(userAcount) && sso_id.equals(userAcount)) {
					
					System.out.print("## GolfCtrlServ | 쿠키정상, 세션정상, SSOID와 ACCOUNT 비교 정상  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
					//logger.debug("쿠키정상, 세션정상, SSOID와 ACCOUNT 비교 정상");
					passed = false;
					respon = act(request, response);
				}// 쿠키정상, 세션없음, SSOID 있음 ==> WAS 세션 종료, SSO 세션 정상
				else if (  isResult && !"".equals(userAcount) && sso_id != null ) {
					
					System.out.print("## GolfCtrlServ | 쿠키정상, 세션없음, SSOID 있음  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					
					//logger.debug("쿠키정상, 세션없음, SSOID 있음 ==> WAS 세션 종료, SSO 세션 정상");
					session.setAttribute("SYSID", sso_id);
					
					String parm = "" + request.getQueryString();
					if(!"".equals(parm))
						session.setAttribute("PARM", parm);
					session.setAttribute("UURL", "/app/golfloung/"+actnkey+".do");
					session.setAttribute("orgActionKey", actnkey);
					response.sendRedirect("/app/golfloung/LoginCheck.do");
					return;
				}
				// ACCOUNT 없음, SSOID 없음 ==> 로그인 안하고 로그인 필수 액션 Call한 경우
				else if ( "".equals(userAcount) && sso_id==null ) {
					
					System.out.print("## GolfCtrlServ | ACCOUNT 없음, SSOID 없음  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
					//logger.debug("ACCOUNT 없음, SSOID 없음 ==> 로그인 안하고 로그인 필수 액션 Call한 경우");
					createFrontUserSession( session, request, sso_id);					
					String iniTectData = "";
					queryStr = "" + request.getQueryString();
					
					//암호화 파라메터 처리(INIpluginData=의 쿠키로 넘길 시 "="를 또 다시 인코딩 처리됨)
					if(queryStr.indexOf("INIpluginData=") != -1) {
						iniTectData = request.getParameter("INIpluginData");
						queryStr = "BCENC" + iniTectData;
					}
					try {
						//요청한 파라메터  , Jump URL 저장
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
				// 법인플렛폼으로 들어와서 자동로그인 된경우
				else if( !"".equals(userAcount) && ( "".equals(sso_id) ||  sso_id==null ) && "Y".equals(strEnterCorporation) ) {
					System.out.print("## GolfCtrlServ | 법인플렛폼으로 들어와서 자동로그인 경우 or 세션을 이미 구운경우  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
					//logger.debug("쿠키정상, 세션정상, SSOID와 ACCOUNT 비교 정상");
					passed = false;
					respon = act(request, response);
				}											
				// 법인플렛폼으로 들어와서 지정카드이면서 회원이 아닌경우 
				else if( ( "".equals(userAcount) && "".equals(sso_id) ||  sso_id==null ) && "Y".equals(strEnterCorporation) && !"Y".equals(peCk) && "6".equals(coCk)   ) {
					System.out.print("## GolfCtrlServ | 법인플렛폼으로 들어와서 지정카드이면서 회원이 아닌경우   | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
				
					createFrontUserSession( session, request, strEnterCorporationAccountId);
					response.sendRedirect("/app/golfloung/LoginCheck.do");
					return;
					
					
					
				}	
				
				// 기타사항 => WAS세션만 살아 있음, SSO 리턴 에러,
				else {
					System.out.print("## GolfCtrlServ | WAS세션만 살아 있음, SSO 리턴 에러,  | sso_id :"+sso_id+" | isResult : "+isResult+" | userAcount : "+userAcount+"\n");
					 
					//logger.debug("기타사항 => WAS세션만 살아 있음, SSO 리턴 에러,");
					//일단 세션삭제, 쿠키삭제
					session.removeAttribute("SYSID");
					session.removeAttribute("FRONT_ENTITY");
					session.setAttribute("FRONT_ENTITY", null);	
					//session.invalidate();
					//session.removeAttribute(ConstVars.FRONT_ENTITY);
					CookieManager.removeCookie("MEM_CLSS", sso_domain, response);

					CookieManager.removeNexessCookie(sso_domain, response);
					//CookieManager.removeNexessCookie(".bccard.com", response);
					try {
						//logger.debug("인증 정보 검증 결과");
						/*
						* RESULT : 인증 정보 검증 결과
						*	-. RESULT = 0 : 정상적 로그인 절차에 따른 리턴값, 그외는 오류코드
						*	-. RESULT = 100 : 익명사용자 (로그인 되어있지 않은 경우)
						*	-. RESULT = 200 : 사용자 아이디가 없는 경우
						*	-. RESULT = 1000 : 꼭 필요한 쿠키중 일부가 없을때
						*	-. RESULT = 1001 : 세션 타임아웃(LAT가 session time을 벗어났을 경우)
						*	-. RESULT = 1002 : 쿠키값들은 다 존재하나 넥세스가 정상적으로 발급한
					                                  쿠기가 아닐때(HMAC이 틀릴 경우)
						*	-. RESULT = 1004 : IP가 같지 않을 경우   */
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
						//SSO 세션종료된 경우 처리 ==> SSO 로그인 이동
						else if(retCode.compareTo("1001") == 0) {
							
							//logger.debug("1001");
							//?로그인(loginActn)하려고 들어왔는데 세션이 만료료된 경우를 제외하고 세션만료 처리

							if(actnkey.equals("loginActn")) {

								passed = false;
								respon = act(request, response, "loginActn");
								super.view(request,response,respon);
							}
							else {
								//response.sendRedirect("/card/view/app/common/jump_login_ssn_page.jsp");
								//response.sendRedirect("/app/golfloung/view/golf/jump_first_page.jsp");
								//return;
								logger.debug("##########  에러로 이동");
								goErrorPage(response, iretCode);
							}
						}
						else {
							
							//logger.debug("에러페이지");
							goErrorPage(response, iretCode);
						}
				        // 호출된 액션 정보를 로그인폼 JSP 에서 받기위해 Request Attribute에 기록한다.
				        request.setAttribute("requestActionKey", actnkey );
				        request.setAttribute("requestURI", request.getRequestURI() );
					} catch (Exception e) {}
					return;
				}

			}//End 로그인 후 Action 진입
			
			if ( respon != null ) {
				
				//logger.debug("respon 이 널이 아닌경우");
				
			    // 응답정보가 있으면 응답 정보에 레이아웃 정보를 읽어와 Request Attribute에 기록된 값을 갱신한다.
			    String responLayout = respon.getParamProp("layout");
			    //logger.debug("responLayout:"+responLayout);
			    
			    if ( responLayout != null && responLayout.length() > 0 ) {
			        request.setAttribute("layout",responLayout);
			    }
			}
			//logger.debug("respon.getKey():"+respon.getKey());
			if(respon.getKey()==""){
				//logger.debug("respon.getKey() 이 널인 경우");
				respon.setKey("default");
				//logger.debug("respon.getKey():"+respon.getKey());
			}else{}
			
			
			if(actnkey == null || "".equals(actnkey)){
				isPermiResult = false;
			}	
			
			golfAccessLog(request, response, respon, "STD", ett, isPermiResult);
			
			super.view(request,response,respon);  // 화면을 출력한다.
			
			golfAccessLog(request, response, respon, "END", ett, isPermiResult);
			
			//logger.debug("================끝============ ==");
			
			//프론트일경우 끝.***************************************************************************************************
			
		}
		
		byPassed(passed, request, response);
	}
    
	/**
	 * 사용자 세션 생성
	 * @param session
	 * @param sso_id
	 */
	public void createFrontUserSession(HttpSession session, HttpServletRequest req, String sso_id){
		Connection con = null;
		UcusrinfoEntity ucusrinfo = null;
		try{
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);		
			
			//법인 회원이면서 지정카드인 사람 또는 개인회원
			String strCkNum = proc.selectByCkNum(con, sso_id);
			System.out.println("## GolfCtrlServ | 법인 회원이면서 지정카드 외인 사람 | sso_id : "+sso_id+" | strCkNum : "+strCkNum+"\n");
			
			//법인 회원이면서 지정카드인 사람 또는 개인회원
			if(!"Y".equals(strCkNum))
			{
				// 사용자 Front Entity 설정.
				logger.debug("createFrontUserSession 사용자 Front Entity 설정.");				
				ucusrinfo = proc.selectByAccount(con, sso_id);
							
				//logger.debug("ucusrinfo:"+ucusrinfo);
				
				if (ucusrinfo != null) {
					System.out.println("## GolfCtrlServ | 회원 세션처리 시작 | sso_id : "+sso_id+"\n");
					//logger.debug("개인정보설정");
					// 개인 정보 설정
					//setPrivateInfo(waContext, ucusrinfo);
																		
					
					
					session.setAttribute("FRONT_ENTITY", ucusrinfo);
					session.setAttribute("SESSION_USER", ucusrinfo);

					GolfUserEtt ett = new GolfUserEtt();

					ett.setLogin(true);
					ett.setMemId(ucusrinfo.getAccount());
					ett.setMemNm(ucusrinfo.getName());
					ett.setMemClss(ucusrinfo.getMemberClss());	// 회원구분 1=개인,2=법인
					if(ucusrinfo.getMemberClss() !=null && "1".equals(ucusrinfo.getMemberClss())) {
						ett.setJuminNo(ucusrinfo.getSocid());

						/** *****************************************************************
						 * checkJolt() Start - 기업은행카드
						 ***************************************************************** */
						logger.debug("=============> checkJolt() Start <============" );
						checkJolt(waContext, req, ett, ucusrinfo);
						session.setAttribute("GOLF_ENTITY", ett);
						List list = ett.getCardInfoList();
						System.out.println("## GolfCtrlServ | 회원 골프카드정보통신 | list.size() : " + list.size());

						/** *****************************************************************
						 * checkJoltNh() Start - 농협카드 
						 ***************************************************************** */
						logger.debug("=============> checkJoltNh() Start <============" );
						checkJoltNh(waContext, req, ett, ucusrinfo);
						session.setAttribute("GOLF_ENTITY", ett);
						List listNh = ett.getCardNhInfoList();
						System.out.println("## GolfCtrlServ | 회원 농협카드정보통신 | listNh.size() : " + listNh.size());
						
					}
				
			}
			
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
	 * 법인 사용자 세션 생성
	 * @param session
	 * @param sso_id
	 */
	public void createFrontUserSessionCo(HttpSession session, HttpServletRequest req, String sso_id){
		Connection con = null;
		UcusrinfoEntity ucusrinfo = null;
		try{
			
			// 사용자 Front Entity 설정.
			logger.debug("createFrontUserSessionCo 법인 사용자 Front Entity 설정.");
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);			
			ucusrinfo = proc.selectByAccountCo(con, sso_id);
						
			//logger.debug("ucusrinfo:"+ucusrinfo);
			 
			// 법인인 사람만 검색
			if (ucusrinfo != null) {
				System.out.println("## GolfCtrlServ | 법인회원 |  | sso_id : "+sso_id+"\n");
				//logger.debug("개인정보설정");
				// 개인 정보 설정
				//setPrivateInfo(waContext, ucusrinfo);
				
				if("6".equals(ucusrinfo.getStrCoMemType() ))
				{
					System.out.println("## GolfCtrlServ | 법인 지정회원인 경우 |  세션처리 시작 | sso_id : "+sso_id+" | ucusrinfo.getStrCoMemType() : "+ucusrinfo.getStrCoMemType()+"\n");
					session.setAttribute("FRONT_ENTITY", ucusrinfo);
					session.setAttribute("SESSION_USER", ucusrinfo);
					
				
					
					GolfUserEtt ett = new GolfUserEtt();
	
					ett.setLogin(true);
					ett.setMemId(ucusrinfo.getAccount());
					ett.setMemNm(ucusrinfo.getName());
					ett.setMemClss(ucusrinfo.getMemberClss());	// 회원구분 1=개인,2=법인
					if(ucusrinfo.getMemberClss() !=null && "1".equals(ucusrinfo.getMemberClss())) {
						ett.setJuminNo(ucusrinfo.getSocid());
	
						/** *****************************************************************
						 * checkJolt() Start
						 ***************************************************************** */
						logger.debug("=============> checkJolt() Start <============" );
						checkJolt(waContext, req, ett, ucusrinfo);
						session.setAttribute("GOLF_ENTITY", ett);
						List list = ett.getCardInfoList();
	
						System.out.println("## GolfCtrlServ | 회원 골프카드정보통신 | list.size() : " + list.size());
	
					}
				
				}
				else
				{
					System.out.println("## GolfCtrlServ | 법인 지정회원이 아닌 경우  |  세션 만들지 않음 | sso_id : "+sso_id+" | ucusrinfo.getStrCoMemType() : "+ucusrinfo.getStrCoMemType()+"\n");
					
				}
				
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
	 * 법인이면서 골프회원인 사용자 판단
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
			
			// 법인인 사람만 검색
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
	 * 골프회원인 사용자 판단
	 * @param session
	 * @param sso_id
	 */
	public String selectPeByCkNum(String account_id) {
		String strMemYn = "N";
		Connection con = null;
		try{
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);			
			strMemYn = proc.selectPeByCkNum(con, account_id);		// Y골프회원	
			
		
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
	 * 법인회원인 사용자 판단
	 * @param session
	 * @param sso_id
	 */
	public String selectPeCoByCkNum(String account_id) {
		String strMemYn = "";
		Connection con = null;
		try{
			UcusrinfoDaoProc proc = (UcusrinfoDaoProc) this.waContext.getProc("UcusrinfoDao");			
			con = this.waContext.getDbConnection("default", null);			
			strMemYn = proc.selectCoByCkNum(con, account_id);		// Y골프회원	
			
		
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
	 * 전문 정보로 사용자 정보 설정
	 * @param 	context		WaContext 객체
	 * @param 	request	HttpServletRequest
	 * @param 	ett			사용자정보 Entity
	 * @return 	void
	 * @TODO	개인의 각 사용자 별로 전문을 조회하여 사용자 정보를 설정한다.
	 */
	protected void checkJolt(WaContext context, HttpServletRequest request, GolfUserEtt ett,UcusrinfoEntity ucusrinfo) throws BaseException {
		HttpSession session = request.getSession(true);
		Properties properties = new Properties();

		properties.setProperty("LOGIN", "Y");  					// 필수 로그에 남길 해당 전문의 return RETURN_CODE 키. 설정 안하면 "fml_ret1" 사용
		properties.setProperty("RETURN_CODE", "fml_ret1");		// 만약 특정한 pool 을 사용하는 경우라면.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");
		properties.setProperty("SOC_ID", ett.getJuminNo()); 	// log 찍어주는 부분에서 주민번호 대체

		try {

			/** *****************************************************************
			 *Card정보를 읽어오기
			 ***************************************************************** */
			System.out.println("## GolfCtrlServ | 1. Jolt MHL0230R0100 전문 호출 <<<<<<<<<<<<");
			JoltInput cardInput_pt = new JoltInput(BSNINPT);
			cardInput_pt.setServiceName(BSNINPT);
			cardInput_pt.setString("fml_trcode", "MHL0230R0100");
			cardInput_pt.setString("fml_arg1", "1");	// 1.주민번호 2.사업자번호 3.전체(지정자주민번호+사업자)
			cardInput_pt.setString("fml_arg2", ett.getJuminNo());	// 주민번호
			//cardInput_pt.setString("fml_arg2", "6002041090498");	// 임시테스트용
			cardInput_pt.setString("fml_arg3", " ");	// 사업자번호
			cardInput_pt.setString("fml_arg4", "1");	// 1.개인 2.기업
					

			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult cardinfo_pt = null;
			String resultCode_pt = "";			
			

			ArrayList card_list = new ArrayList();
			boolean existsData = false;
			String memClass = "4";
			
			String cardType = "";
			String joinName = "";
			String joinNo = "";
			
			do {
				
				cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
		
				resultCode_pt = cardinfo_pt.getString("fml_ret1");
//				logger.debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`resultCode_pt ::  " + resultCode_pt);
				

				if ( !"00".equals(resultCode_pt) && !"02".equals(resultCode_pt) ) {		// 00 정상, 02 다음조회 있음
					throw new ChkChgSocIdException(ett.getMemId(), ett.getJuminNo(), "02");
				}else{

					while( cardinfo_pt.isNext() ) {
						
						if(!existsData){
							//System.out.println(" 카드 ett 생성 cardNo : "+cardinfo_pt.getString("fml_ret3"));
							//System.out.println(" 카드 ett 생성 JuminNo : "+ett.getJuminNo());
							memClass = "1";
							existsData = true;
						}
						
						cardinfo_pt.next();
						CardInfoEtt cardInfo = new CardInfoEtt();
						
						cardType 	= cardinfo_pt.getString("fml_ret4");	//카드종류 1:골프카드 / 2:PT카드 / 3:일반카드
						joinName	= cardinfo_pt.getString("fml_ret7");	//카드이름 
						joinNo 		= cardinfo_pt.getString("fml_ret8");	//제퓨코드						
						
//						System.out.print("임시조치 ID:"+ett.getMemId());
//						if("msj9529".equals(ett.getMemId()))	//골프카드인 유저
//						{
//							joinNo = "030478";
//						}
//						else if("nibeat".equals(ett.getMemId()))	//VIP특별부킹인 유저
//						{
//							joinNo = "030698";
//						} 
//						System.out.print("## GolfCtrlServ | 제휴코드 joinNo : "+joinNo+"\n");
						
						
//						- 상품명 :  나의 알파 플래티늄골프카드 / 제휴코드
//						 ㅇ 나의알파플래티늄골프_캐쉬백     / 030478
//						 ㅇ 나의알파플래티늄골프_아시아나  / 030481
//						 ㅇ 나의알파플래티늄골프_대한항공  / 030494
						
						
						if("1".equals(cardType)){
							if("030478".equals(joinNo) || "030481".equals(joinNo) || "030494".equals(joinNo) || "030698".equals(joinNo) || "031189".equals(joinNo) || "031176".equals(joinNo)  )
							{																		
 
								cardInfo.setCardType(cardType);	// 카드종류구분(Ret4)
								cardInfo.setJoinName(joinName);	// 제휴업체명(Ret7)																								
								cardInfo.setJoinNo(joinNo);		// 제휴업체번호(Ret8)																
								cardInfo.setCardNo(cardinfo_pt.getString("fml_ret2"));			//카드번호
								cardInfo.setBankNo(cardinfo_pt.getString("fml_ret5"));			//은행코드
								cardInfo.setAcctDay(cardinfo_pt.getString("fml_ret12"));		//카드등록일자
								cardInfo.setGolfStartDay(cardinfo_pt.getString("fml_ret13"));	//골프부킹서비스 시작일자
								cardInfo.setGolfYn(cardinfo_pt.getString("fml_ret14"));			//골프부킹서비스 유무

//								logger.debug(" Ret4 | 카드종류 : "+ cardInfo.getCardType());
//								logger.debug(" Ret7 | 카드이름 : "+ cardInfo.getJoinName());
//								logger.debug(" Ret8 | 제휴코드 : "+ cardInfo.getJoinNo());
//								logger.debug(" Ret12 | 카드등록일자 : "+ cardInfo.getAcctDay());
//								logger.debug(" Ret13 | 골프부킹서비스시작일자 : "+ cardInfo.getGolfStartDay());
//								logger.debug(" Ret14 | 골프부킹서비스제공구분 : "+ cardInfo.getGolfYn());

								card_list.add(cardInfo);
							}
						}
						
	
					} 
					ucusrinfo.setmemberClssCard(memClass);
					ett.setCardInfoList(card_list);
				}
				
			} while ("02".equals(resultCode_pt));
			
		} catch (TaoException te) {
			throw getErrorException("LOGIN_ERROR_0003",new String[]{"개인 정보 조회 실패"},te);     // Jolt 처리 에러
		}

	}
	/**
	 * 전문 정보로 사용자 정보 설정
	 * @param 	context		WaContext 객체
	 * @param 	request	HttpServletRequest
	 * @param 	ett			사용자정보 Entity
	 * @return 	void
	 * @TODO	개인의 각 사용자 별로 전문을 조회하여 사용자 정보를 설정한다.
	 */
	protected void checkJoltNh(WaContext context, HttpServletRequest request, GolfUserEtt ett,UcusrinfoEntity ucusrinfo) throws BaseException {
		HttpSession session = request.getSession(true);
		Properties properties = new Properties();

		properties.setProperty("LOGIN", "Y");  					// 필수 로그에 남길 해당 전문의 return RETURN_CODE 키. 설정 안하면 "fml_ret1" 사용
		properties.setProperty("RETURN_CODE", "fml_ret1");		// 만약 특정한 pool 을 사용하는 경우라면.
		//properties.setProperty("POOL_NAME", "SPECIFIC_POOL");
		properties.setProperty("SOC_ID", ett.getJuminNo()); 	// log 찍어주는 부분에서 주민번호 대체

		try {

			/** *****************************************************************
			 *Card정보를 읽어오기
			 ***************************************************************** */
			System.out.println("## GolfCtrlServ | 1. Jolt NTC0080R2500 전문 호출 <<<<<<<<<<<<");
			JoltInput cardInput_pt = new JoltInput(BSNINPT);
			cardInput_pt.setServiceName(BSNINPT);
			cardInput_pt.setString("fml_trcode", "NTC0080R2500");
			cardInput_pt.setString("fml_arg1", ett.getJuminNo());	// 주민번호
			//cardInput_pt.setString("fml_arg1", "8108212473818");	// 주민번호

			JtProcess jt_pt = new JtProcess();
			java.util.Properties prop_pt = new java.util.Properties();
			prop_pt.setProperty("RETURN_CODE","fml_ret1");
			
			TaoResult cardinfo_pt = null;
			String resultCode_pt = "";

			// 카드정보 담을 변수 선언
			ArrayList card_list = new ArrayList();
			
			cardinfo_pt = jt_pt.call(context, request, cardInput_pt, prop_pt);			
			resultCode_pt = cardinfo_pt.getString("fml_ret1");
			logger.debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`resultCode_pt ::  " + resultCode_pt); 
			
			if(cardinfo_pt!=null){
				if(resultCode_pt.equals("02")){
					logger.debug("=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`=`  결과 있음"); 
					while( cardinfo_pt.isNext() ) {
						
						cardinfo_pt.next();
						CardNhInfoEtt cardNhInfo = new CardNhInfoEtt();
						
						cardNhInfo.setCardGubun(cardinfo_pt.getString("fml_ret1"));
						cardNhInfo.setCardNo(cardinfo_pt.getString("fml_ret2"));
						cardNhInfo.setCardNm(cardinfo_pt.getString("fml_ret3"));
						cardNhInfo.setCardGrade(cardinfo_pt.getString("fml_ret4"));
						cardNhInfo.setCardType(cardinfo_pt.getString("fml_ret5"));
	
	//					logger.debug(" Ret1 | 서비스 구분 : 01:블랙 02:채움 | "+ cardInfo.getCardGubun());
	//					logger.debug(" Ret2 | 카드번호 | "+ cardInfo.getCardNo());
	//					logger.debug(" Ret3 | 제휴카드명 | "+ cardInfo.getCardNm());
	//					logger.debug(" Ret4 | 카드등급 | "+ cardInfo.getCardGrade());
	//					logger.debug(" Ret5 | 카드종류 : 03:티타늄 12:플래티늄 | "+ cardInfo.getCardType());
	
						card_list.add(cardNhInfo);
	
					} 
					//ucusrinfo.setmemberClssCard(memClass);	// 정회원 카드로 체크
				}
			}
			ett.setCardNhInfoList(card_list);

			
		} catch (TaoException te) {
			throw getErrorException("LOGIN_ERROR_0003",new String[]{"개인 정보 조회 실패"},te);     // Jolt 처리 에러
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
				
				//logger.debug(":: 리턴 URL 없음 :: ");		
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
	 * 개인 정보 (TOP 포인트, 사용자 주소)를 설정한다.
	 * 
	 * @param ucusrinfo 사용자 정보
	 
	private void setPrivateInfo(WaContext context, UcusrinfoEntity ucusrinfo) throws BaseException {
		String ssn = ucusrinfo.getSocid();
		
		SettlementTao settlementTao = (SettlementTao) context.getProc("SettlementTao");
		
		int topPoint = 0;
		
		// TOP 포인트 조회
		ServiceResult topPointResult = settlementTao.inquireTopPoint(ssn);
		if (topPointResult.isSuccess()) {
			topPoint = Integer.parseInt(topPointResult.getValue1());
		}

		// 주소 조회
		UserAddress userAddress = settlementTao.findUserAddress(ssn);
		
		ucusrinfo.setBcTopPoint(topPoint);
		ucusrinfo.setUserAddress(userAddress);
	}
	 */

	// 개발기에서 사용
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
	// 운영기에서 사용
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
	 * etaxAccessLog method : 액세스 로그
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

        String logs = "";   // 서버 IP
        try {
            InetAddress iaddress = InetAddress.getLocalHost(); 
            logs = iaddress.getHostAddress();
        } catch(Throwable t) {
            logs = "unknown Server";
        }

		StringBuffer logBuff = new StringBuffer();
        logBuff.append("|").append( type );
		logBuff.append("|").append( logs );

        // 클라이언트 IP
        logBuff.append("|").append( request.getRemoteAddr() );
        
        // 컨트롤러서블릿명
        try {
            logBuff.append("|").append( getServletName() );
        } catch(Throwable t) {
            logBuff.append("|unknown");
        }

       // 사용자정보
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

        // 액션키
        logBuff.append("|").append( actnkey );

        // 리턴 페이지 정보
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
	 * 오류시 반환할 Exception 반환
	 * @param 	msgkey	결과 메세지 Key
	 * @param 	args			결과메세지에 표시할 arguement
	 * @param 	t				Throwable
	 * @return 	ResultException
	 * @TODO 배경이미지 error 로 지정
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
		exception.setTitleText("비씨카드 로그인");
		return exception;
	}
}
