
/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfElecAauthProcess
*   작성자    : (주)미디어포스 이경희
*   내용      : 공인인증서 검증
*   적용범위  : Golf
*   작성일자  : 2011.10.28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항 
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractObject;
//import com.initech.iniplugin.IniPlugin;
//import com.initech.iniplugin.crl.CheckCRL;
//import com.initech.iniplugin.crl.exception.CertificatePolicyException;
//import com.initech.iniplugin.crl.exception.LdapConnectException;
//import com.initech.iniplugin.crl.exception.ValidCANotFoundException;
//import com.initech.iniplugin.oid.CertOIDUtil;
//import com.initech.iniplugin.oid.ConstDef;
//import com.initech.iniplugin.vid.IDVerifier;


public class GolfElecAauthProcess extends AbstractObject{

	public static final String TITLE = "비씨골프 공인인증서 검증";
	
	private String validCertMsg ="";	//인증서 검증결과 오류 메세지. 
	private String userDn ="";			//인증서 검증 성공시 추출해낸 사용자 DN값

	public String[] semiCert(HttpServletRequest request,	HttpServletResponse response){		
			
//		IniPlugin m_IP = null ;
		
		String[] semiCertVal = new String[2]; 
		
		try {
//			m_IP = new IniPlugin(request,response, AppConfig.getAppProperty("IniPlugin"));
//			m_IP.init();
			semiCertVal[0] = "true";		
		} catch(Exception e) {
			semiCertVal[0] = "false";
			error("semiCert :: Exception Err => " + e.getMessage());
			error("semiCert :: Exception printStackTrace", e);
		}
		
//		if (m_IP.isClientAuth() == false) {		
//			semiCertVal[1] = "false";	
//		}else {
//			semiCertVal[1] = "true";
//		}
		
		return semiCertVal;
		
	}
	
	/*******************************************************************
	* 개발기관.집행내역상세조회(등록)
	* @param context		WaContext 객체.
	* @param request		HttpServletRequest 객체.
	* @param response		HttpServletResponse 객체.
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보.
	 * @throws DbTaoException 
	******************************************************************/
/*
	public boolean isValidCert(HttpServletRequest request,	HttpServletResponse response, String pid, String gubun) throws GolfException, DbTaoException {
	
//		boolean result = true;
			
		//인증 모듈을 위한 변수   

		String juminOrBizNO = null;		
		String vid = null;
		IDVerifier idv = null;
		
		X509Certificate cert = null;
		CheckCRL ccrl = null;
		CertOIDUtil cou = null;
		boolean returnFlag = false;		
			
		String crlConfig = "";//유효성 체크
		String oidConfig = "";//정책번호 체크(별도 구성)
	
		IniPlugin m_IP = null ;
		
		try {
			
			crlConfig = AppConfig.getAppProperty("CRL");		//유효성 체크
			oidConfig = AppConfig.getAppProperty("jCERTOID");	//정책번호 체크(별도 구성)
		
			m_IP = new IniPlugin(request,response, AppConfig.getAppProperty("IniPlugin"));
			m_IP.init();
			
		} catch(Exception e) {
			
			validCertMsg = "올바른 인증서가 아닙니다.";
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "올바른 인증서가 아닙니다." );
            throw new DbTaoException(msgEtt,e);
		
		}	
		
		//2.
		if(m_IP == null){//서명값 제대로 안넘어왔을경우				
			
			if ( gubun.equals("3") ) {
				validCertMsg = "전자서명 값을 확인할 수 없습니다.\n\n브라우져의 새로고침 버튼을 누르신 후 다시 시도해주십시오.\n\n문제가 계속 발생시에는 비씨카드 관리자에게 문의 바랍니다.";	
			}else {
				validCertMsg = "전자서명 값을 확인할 수 없습니다.\\n\\n브라우져의 새로고침 버튼을 누르신 후 다시 시도해주십시오.\\n\\n문제가 계속 발생시에는 비씨카드 관리자에게 문의 바랍니다.";	
			}				
			
			debug("##====전자서명 값을 확인할 수 없습니다 ");
			result = false;
			return result;
		}			
		
		if (m_IP.isClientAuth() == false) {
			debug("##====인증이 필요없는 페이지 입니다.");
			validCertMsg = "인증이 필요없는 페이지 입니다.";
			result = false;	
			return result;
		}
	
		
		try{			
			
			//3. 사용자 인증서를 얻음.
			cert = m_IP.getClientCertificate();

			juminOrBizNO = pid;

			/* 테스트시 주민번호나 사업자등록번호 하드 코딩하여 테스트
			 * 테스트 법인의 사업자등록번호 : 1234567890
			 * juminOrBizNO = "1234567890";  
			 */

/*
			debug (" ******** gubun : " + gubun + ", pid : " + pid);
			
			userDn = cert.getSubjectDN().toString(); // 사용자 DN 값		
				
			debug("##====juminNO [" + juminOrBizNO + "]");
			debug("##====userDn [" + userDn + "]");
			
			cou = new CertOIDUtil(oidConfig);
		
			//4. OID 인증서 용도 검증
			cou = new CertOIDUtil(oidConfig);
			
			if (cou.checkOID(cert) == true) {

				debug("##====확인에 성공했습니다.!! ");
				debug("##====UserCertOID : [" + cou.getCertOID() + "]");

			} else {
					
				debug("##====확인에 실패했습니다. 정책 번호가 맞지  않습니다.");				
				debug("##====UserCertOID : [" + cou.getCertOID() + "]");				
				validCertMsg = "정책 번호가 맞지  않습니다.";
				result = false;
				return result;
				
//			}		
			
			//5. 인증기관 구분		
			String issuer = null;
			issuer = cou.getCertIssuer(cou.getCertOID());
			
			if (issuer != null) {

				if (issuer.equals(ConstDef.KICA)) {
					debug("##====인증서 발급자	: [ 한국정보인증 ]");
				} else if (issuer.equals(ConstDef.SIGNKOREA)) {
					debug("##====인증서 발급자	: [ 한국증권전산 ]");
				} else if (issuer.equals(ConstDef.YESSIGN)) {
					debug("##====인증서 발급자	: [ 금융결제원 ]");
				} else if (issuer.equals(ConstDef.NCA)) {
					debug("##====인증서 발급자	: [ 한국전산원 ]");
				} else if (issuer.equals(ConstDef.CROSSCERT)) {
					debug("##====인증서 발급자	: [ 한국전자인증 ]");
				} else if (issuer.equals(ConstDef.TRADESIGN)) {
					debug("##====인증서 발급자	: [ 한국무역정보통신 ]");
				} else {
					debug("##====인증서 발급자	: [ 기타 ]");
				}
			}	
			
			//6. 인증서 본인확인
			vid = m_IP.getVIDRandom();	// 본인확인을 위한 키 값
			idv = new IDVerifier();	

			if ((idv.checkVID(cert, juminOrBizNO, vid.getBytes())) == true) {
				
				debug("##====본인확인에 성공했습니다.!!");
				debug("##====RANDOM Number	: [" + m_IP.getVIDRandom() + "]");
				debug("##====userDn [" + userDn + "]");
				debug("##====issuer [" + issuer + "]");
				debug("##====UserCertOID [" + cou.getCertOID() + "]");				

			} else {
				
				if ( gubun.equals("1") ) {
					debug("##====본인확인에 실패했습니다.주민등록번호를 확인해주세요.");								
					debug("##====주민등록번호는 [" + juminOrBizNO + "] 입니다.<br>");
					debug("RANDOM Number : [" + m_IP.getVIDRandom() + "]");				
					validCertMsg += "본인확인에 실패했습니다. \\n\\nISP 결제된 카드의  주민등록번호와 공인인증서의 주민등록 번호가 일치하지  않습니다.";
					//validCertMsg += "\\n\\nISP결제된 카드의 주민번호는 [" + juminOrBizNO + "] 입니다.  ";
				}else if ( gubun.equals("2") ) {
					debug("##====본인확인에 실패했습니다.사업자등록번호를 확인해주세요.");								
					debug("##====사업자등록번호는 [" + juminOrBizNO + "] 입니다.<br>");
					debug("RANDOM Number : [" + m_IP.getVIDRandom() + "]");				
					validCertMsg += "본인확인에 실패했습니다. \\n\\nISP 결제된 카드의  사업자등록번호와 공인인증서의 사업자등록번호가 일치하지  않습니다.";
					//validCertMsg += "\\n\\nISP결제된 카드의 사업자등록번호는 [" + juminOrBizNO + "] 입니다.  ";	
				}else if ( gubun.equals("3") ) {
					debug("##====본인확인에 실패했습니다.주민등록번호를 확인해주세요.");								
					debug("##====주민등록번호는 [" + juminOrBizNO + "] 입니다.<br>");
					debug("RANDOM Number : [" + m_IP.getVIDRandom() + "]");				
					validCertMsg += "본인확인에 실패했습니다. \n\n입력한  주민등록번호와 공인인증서의 주민등록 번호가 일치하지  않습니다.";
					//validCertMsg += "\n\n입력한  주민번호는 [" + juminOrBizNO + "] 입니다.  ";
				}
				
				result = false;
				return result;
			}
			
			
			//7. 인증서 유효성 검증 ==> 인증기관별로 방화벽 열려 있는지 확인할것
			try { 
				
				ccrl = new CheckCRL();
				ccrl.init(crlConfig);
				returnFlag = ccrl.isValid(cert);		 
				
				if(!returnFlag){
					debug("##====제출한 인증서는 폐기된 인증서 입니다.");
					validCertMsg = "제출한 인증서는 폐기된 인증서 입니다.";
					result = false;
					return result;
				}else{
					debug("##====제출한 인증서는 사용가능한 유효한 인증서 입니다.");
				}

			} catch (IllegalArgumentException e) {
				debug("##====인증서내의 LDAP DP 형식이 잘 못 됐습니다.");
				debug("##====(or LDAP에서 수신한 CRL 데이터에 오류인 경우)");
				if ( gubun.equals("3") ) {
					validCertMsg = "인증서내의 LDAP DP 형식이 잘 못 됐습니다. \n\n(or LDAP에서 수신한 CRL 데이터에 오류인 경우) ";
				}else {
					validCertMsg = "인증서내의 LDAP DP 형식이 잘 못 됐습니다. \\n\\n(or LDAP에서 수신한 CRL 데이터에 오류인 경우) ";
				}
				e.printStackTrace();
				result = false;			
				// 인증서 내의 LDAP Distribution Point 의 형식이 잘못 된 경우
				// LDAP 으로부터 수신한 CRL 의 데이터에 오류가 있을 경우
			} catch (LdapConnectException e) {
				debug("##====LDAP 데이터 수신 중에 오류가 발생하였습니다.");
				e.printStackTrace();
				result = false;			
				// LDAP 으로 데이터를 수신하다가 오류가 발생한 경우
			} catch (CertificateExpiredException e) {
				debug("##====만료된 인증서 입니다..");
				validCertMsg = "만료된 인증서 입니다.";
				e.printStackTrace();
				result = false;			
				// 폐기된 인증서를 검증하려고 한 경우
			} catch (CertificateNotYetValidException e) {
				debug("##====인증서 유효기간의 범주에 도달하지 못했습니다.");
				validCertMsg = "인증서 유효기간의 범주에 도달하지 못했습니다.";
				e.printStackTrace();
				result = false;			
				// 아직 인증서 유효기간의 범주에 도달하지 못한 경우
			} catch (ValidCANotFoundException e) {
				debug("##====인증서가 caList 목록에 존재하지 않습니다. 또는 인증서 경로검증 에러 입니다.");				
				if ( gubun.equals("3") ) {
					validCertMsg = "인증서가 caList 목록에 존재하지 않습니다. \n\n또는 인증서 경로검증 에러 입니다. ";
				}else {
					validCertMsg = "인증서가 caList 목록에 존재하지 않습니다. \\n\\n또는 인증서 경로검증 에러 입니다. ";
				}				
				e.printStackTrace();
				result = false;			
				// 제출한 인증서가 환경설정 파일의 caList 목록에 있지 않은 경우	 
			} catch (CertificatePolicyException e) {
				debug("##====인증서의 Certificate Policy가 환경파일에 등록되지 않았습니다.");
				validCertMsg = "인증서의 Certificate Policy가 환경파일에 등록되지 않았습니다.";
				e.printStackTrace();
				result = false;			
				// 제출한 인증서의 Certificate Policy 가 환경설정 파일에 등록되지 않은 경우
			} catch (Exception e) {
				debug("##====예상치 못한 오류입니다.");				
				if ( gubun.equals("3") ) {
					validCertMsg = "예상치 못한 오류입니다. \n\n문제가 계속 발생시에는 비씨카드 관리자에게 문의 바랍니다.";
				}else {
					validCertMsg = "예상치 못한 오류입니다. \\n\\n문제가 계속 발생시에는 비씨카드 관리자에게 문의 바랍니다.";
				}					
				e.printStackTrace();
				result = false;			
				// 기타 예상치 못한 오류
			}		
			

		}catch (Exception e) {
			debug("##====예상치 못한 오류입니다.");
			if ( gubun.equals("3") ) {
				validCertMsg = "예상치 못한 오류입니다. \n\n문제가 계속 발생시에는 비씨카드 관리자에게 문의 바랍니다.";
			}else {
				validCertMsg = "예상치 못한 오류입니다. \\n\\n문제가 계속 발생시에는 비씨카드 관리자에게 문의 바랍니다.";
			}			
			e.printStackTrace();
			result = false;
			// 기타 예상치 못한 오류
		}	
		
		return result;
	}
 */

	/*******************************************************************
	* 세션에 에러메세지 셋팅 
	* @param session		HttpSession 객체.
	******************************************************************/
	public void setValidCertMsg(HttpSession session){
		String sessionMsg = (String)session.getAttribute("validCertMsg");
		if(sessionMsg!=null){
			session.removeAttribute("validCertMsg");
		}
		session.setAttribute("validCertMsg", this.validCertMsg);
	}
	
	/*******************************************************************
	* 사용자DN값을 리턴
	* @return String		String 정보.
	******************************************************************/
	public String getUserDn(){
		return this.userDn;
	}
}
