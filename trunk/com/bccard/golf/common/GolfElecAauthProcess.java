
/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfElecAauthProcess
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ���������� ����
*   �������  : Golf
*   �ۼ�����  : 2011.10.28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   ������� 
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

	public static final String TITLE = "�񾾰��� ���������� ����";
	
	private String validCertMsg ="";	//������ ������� ���� �޼���. 
	private String userDn ="";			//������ ���� ������ �����س� ����� DN��

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
	* ���߱��.���೻������ȸ(���)
	* @param context		WaContext ��ü.
	* @param request		HttpServletRequest ��ü.
	* @param response		HttpServletResponse ��ü.
	* @return ActionResponse	Action ó���� ȭ�鿡 ���÷����� ����.
	 * @throws DbTaoException 
	******************************************************************/
/*
	public boolean isValidCert(HttpServletRequest request,	HttpServletResponse response, String pid, String gubun) throws GolfException, DbTaoException {
	
//		boolean result = true;
			
		//���� ����� ���� ����   

		String juminOrBizNO = null;		
		String vid = null;
		IDVerifier idv = null;
		
		X509Certificate cert = null;
		CheckCRL ccrl = null;
		CertOIDUtil cou = null;
		boolean returnFlag = false;		
			
		String crlConfig = "";//��ȿ�� üũ
		String oidConfig = "";//��å��ȣ üũ(���� ����)
	
		IniPlugin m_IP = null ;
		
		try {
			
			crlConfig = AppConfig.getAppProperty("CRL");		//��ȿ�� üũ
			oidConfig = AppConfig.getAppProperty("jCERTOID");	//��å��ȣ üũ(���� ����)
		
			m_IP = new IniPlugin(request,response, AppConfig.getAppProperty("IniPlugin"));
			m_IP.init();
			
		} catch(Exception e) {
			
			validCertMsg = "�ùٸ� �������� �ƴմϴ�.";
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ùٸ� �������� �ƴմϴ�." );
            throw new DbTaoException(msgEtt,e);
		
		}	
		
		//2.
		if(m_IP == null){//���� ����� �ȳѾ�������				
			
			if ( gubun.equals("3") ) {
				validCertMsg = "���ڼ��� ���� Ȯ���� �� �����ϴ�.\n\n�������� ���ΰ�ħ ��ư�� ������ �� �ٽ� �õ����ֽʽÿ�.\n\n������ ��� �߻��ÿ��� ��ī�� �����ڿ��� ���� �ٶ��ϴ�.";	
			}else {
				validCertMsg = "���ڼ��� ���� Ȯ���� �� �����ϴ�.\\n\\n�������� ���ΰ�ħ ��ư�� ������ �� �ٽ� �õ����ֽʽÿ�.\\n\\n������ ��� �߻��ÿ��� ��ī�� �����ڿ��� ���� �ٶ��ϴ�.";	
			}				
			
			debug("##====���ڼ��� ���� Ȯ���� �� �����ϴ� ");
			result = false;
			return result;
		}			
		
		if (m_IP.isClientAuth() == false) {
			debug("##====������ �ʿ���� ������ �Դϴ�.");
			validCertMsg = "������ �ʿ���� ������ �Դϴ�.";
			result = false;	
			return result;
		}
	
		
		try{			
			
			//3. ����� �������� ����.
			cert = m_IP.getClientCertificate();

			juminOrBizNO = pid;

			/* �׽�Ʈ�� �ֹι�ȣ�� ����ڵ�Ϲ�ȣ �ϵ� �ڵ��Ͽ� �׽�Ʈ
			 * �׽�Ʈ ������ ����ڵ�Ϲ�ȣ : 1234567890
			 * juminOrBizNO = "1234567890";  
			 */

/*
			debug (" ******** gubun : " + gubun + ", pid : " + pid);
			
			userDn = cert.getSubjectDN().toString(); // ����� DN ��		
				
			debug("##====juminNO [" + juminOrBizNO + "]");
			debug("##====userDn [" + userDn + "]");
			
			cou = new CertOIDUtil(oidConfig);
		
			//4. OID ������ �뵵 ����
			cou = new CertOIDUtil(oidConfig);
			
			if (cou.checkOID(cert) == true) {

				debug("##====Ȯ�ο� �����߽��ϴ�.!! ");
				debug("##====UserCertOID : [" + cou.getCertOID() + "]");

			} else {
					
				debug("##====Ȯ�ο� �����߽��ϴ�. ��å ��ȣ�� ����  �ʽ��ϴ�.");				
				debug("##====UserCertOID : [" + cou.getCertOID() + "]");				
				validCertMsg = "��å ��ȣ�� ����  �ʽ��ϴ�.";
				result = false;
				return result;
				
//			}		
			
			//5. ������� ����		
			String issuer = null;
			issuer = cou.getCertIssuer(cou.getCertOID());
			
			if (issuer != null) {

				if (issuer.equals(ConstDef.KICA)) {
					debug("##====������ �߱���	: [ �ѱ��������� ]");
				} else if (issuer.equals(ConstDef.SIGNKOREA)) {
					debug("##====������ �߱���	: [ �ѱ��������� ]");
				} else if (issuer.equals(ConstDef.YESSIGN)) {
					debug("##====������ �߱���	: [ ���������� ]");
				} else if (issuer.equals(ConstDef.NCA)) {
					debug("##====������ �߱���	: [ �ѱ������ ]");
				} else if (issuer.equals(ConstDef.CROSSCERT)) {
					debug("##====������ �߱���	: [ �ѱ��������� ]");
				} else if (issuer.equals(ConstDef.TRADESIGN)) {
					debug("##====������ �߱���	: [ �ѱ������������ ]");
				} else {
					debug("##====������ �߱���	: [ ��Ÿ ]");
				}
			}	
			
			//6. ������ ����Ȯ��
			vid = m_IP.getVIDRandom();	// ����Ȯ���� ���� Ű ��
			idv = new IDVerifier();	

			if ((idv.checkVID(cert, juminOrBizNO, vid.getBytes())) == true) {
				
				debug("##====����Ȯ�ο� �����߽��ϴ�.!!");
				debug("##====RANDOM Number	: [" + m_IP.getVIDRandom() + "]");
				debug("##====userDn [" + userDn + "]");
				debug("##====issuer [" + issuer + "]");
				debug("##====UserCertOID [" + cou.getCertOID() + "]");				

			} else {
				
				if ( gubun.equals("1") ) {
					debug("##====����Ȯ�ο� �����߽��ϴ�.�ֹε�Ϲ�ȣ�� Ȯ�����ּ���.");								
					debug("##====�ֹε�Ϲ�ȣ�� [" + juminOrBizNO + "] �Դϴ�.<br>");
					debug("RANDOM Number : [" + m_IP.getVIDRandom() + "]");				
					validCertMsg += "����Ȯ�ο� �����߽��ϴ�. \\n\\nISP ������ ī����  �ֹε�Ϲ�ȣ�� ������������ �ֹε�� ��ȣ�� ��ġ����  �ʽ��ϴ�.";
					//validCertMsg += "\\n\\nISP������ ī���� �ֹι�ȣ�� [" + juminOrBizNO + "] �Դϴ�.  ";
				}else if ( gubun.equals("2") ) {
					debug("##====����Ȯ�ο� �����߽��ϴ�.����ڵ�Ϲ�ȣ�� Ȯ�����ּ���.");								
					debug("##====����ڵ�Ϲ�ȣ�� [" + juminOrBizNO + "] �Դϴ�.<br>");
					debug("RANDOM Number : [" + m_IP.getVIDRandom() + "]");				
					validCertMsg += "����Ȯ�ο� �����߽��ϴ�. \\n\\nISP ������ ī����  ����ڵ�Ϲ�ȣ�� ������������ ����ڵ�Ϲ�ȣ�� ��ġ����  �ʽ��ϴ�.";
					//validCertMsg += "\\n\\nISP������ ī���� ����ڵ�Ϲ�ȣ�� [" + juminOrBizNO + "] �Դϴ�.  ";	
				}else if ( gubun.equals("3") ) {
					debug("##====����Ȯ�ο� �����߽��ϴ�.�ֹε�Ϲ�ȣ�� Ȯ�����ּ���.");								
					debug("##====�ֹε�Ϲ�ȣ�� [" + juminOrBizNO + "] �Դϴ�.<br>");
					debug("RANDOM Number : [" + m_IP.getVIDRandom() + "]");				
					validCertMsg += "����Ȯ�ο� �����߽��ϴ�. \n\n�Է���  �ֹε�Ϲ�ȣ�� ������������ �ֹε�� ��ȣ�� ��ġ����  �ʽ��ϴ�.";
					//validCertMsg += "\n\n�Է���  �ֹι�ȣ�� [" + juminOrBizNO + "] �Դϴ�.  ";
				}
				
				result = false;
				return result;
			}
			
			
			//7. ������ ��ȿ�� ���� ==> ����������� ��ȭ�� ���� �ִ��� Ȯ���Ұ�
			try { 
				
				ccrl = new CheckCRL();
				ccrl.init(crlConfig);
				returnFlag = ccrl.isValid(cert);		 
				
				if(!returnFlag){
					debug("##====������ �������� ���� ������ �Դϴ�.");
					validCertMsg = "������ �������� ���� ������ �Դϴ�.";
					result = false;
					return result;
				}else{
					debug("##====������ �������� ��밡���� ��ȿ�� ������ �Դϴ�.");
				}

			} catch (IllegalArgumentException e) {
				debug("##====���������� LDAP DP ������ �� �� �ƽ��ϴ�.");
				debug("##====(or LDAP���� ������ CRL �����Ϳ� ������ ���)");
				if ( gubun.equals("3") ) {
					validCertMsg = "���������� LDAP DP ������ �� �� �ƽ��ϴ�. \n\n(or LDAP���� ������ CRL �����Ϳ� ������ ���) ";
				}else {
					validCertMsg = "���������� LDAP DP ������ �� �� �ƽ��ϴ�. \\n\\n(or LDAP���� ������ CRL �����Ϳ� ������ ���) ";
				}
				e.printStackTrace();
				result = false;			
				// ������ ���� LDAP Distribution Point �� ������ �߸� �� ���
				// LDAP ���κ��� ������ CRL �� �����Ϳ� ������ ���� ���
			} catch (LdapConnectException e) {
				debug("##====LDAP ������ ���� �߿� ������ �߻��Ͽ����ϴ�.");
				e.printStackTrace();
				result = false;			
				// LDAP ���� �����͸� �����ϴٰ� ������ �߻��� ���
			} catch (CertificateExpiredException e) {
				debug("##====����� ������ �Դϴ�..");
				validCertMsg = "����� ������ �Դϴ�.";
				e.printStackTrace();
				result = false;			
				// ���� �������� �����Ϸ��� �� ���
			} catch (CertificateNotYetValidException e) {
				debug("##====������ ��ȿ�Ⱓ�� ���ֿ� �������� ���߽��ϴ�.");
				validCertMsg = "������ ��ȿ�Ⱓ�� ���ֿ� �������� ���߽��ϴ�.";
				e.printStackTrace();
				result = false;			
				// ���� ������ ��ȿ�Ⱓ�� ���ֿ� �������� ���� ���
			} catch (ValidCANotFoundException e) {
				debug("##====�������� caList ��Ͽ� �������� �ʽ��ϴ�. �Ǵ� ������ ��ΰ��� ���� �Դϴ�.");				
				if ( gubun.equals("3") ) {
					validCertMsg = "�������� caList ��Ͽ� �������� �ʽ��ϴ�. \n\n�Ǵ� ������ ��ΰ��� ���� �Դϴ�. ";
				}else {
					validCertMsg = "�������� caList ��Ͽ� �������� �ʽ��ϴ�. \\n\\n�Ǵ� ������ ��ΰ��� ���� �Դϴ�. ";
				}				
				e.printStackTrace();
				result = false;			
				// ������ �������� ȯ�漳�� ������ caList ��Ͽ� ���� ���� ���	 
			} catch (CertificatePolicyException e) {
				debug("##====�������� Certificate Policy�� ȯ�����Ͽ� ��ϵ��� �ʾҽ��ϴ�.");
				validCertMsg = "�������� Certificate Policy�� ȯ�����Ͽ� ��ϵ��� �ʾҽ��ϴ�.";
				e.printStackTrace();
				result = false;			
				// ������ �������� Certificate Policy �� ȯ�漳�� ���Ͽ� ��ϵ��� ���� ���
			} catch (Exception e) {
				debug("##====����ġ ���� �����Դϴ�.");				
				if ( gubun.equals("3") ) {
					validCertMsg = "����ġ ���� �����Դϴ�. \n\n������ ��� �߻��ÿ��� ��ī�� �����ڿ��� ���� �ٶ��ϴ�.";
				}else {
					validCertMsg = "����ġ ���� �����Դϴ�. \\n\\n������ ��� �߻��ÿ��� ��ī�� �����ڿ��� ���� �ٶ��ϴ�.";
				}					
				e.printStackTrace();
				result = false;			
				// ��Ÿ ����ġ ���� ����
			}		
			

		}catch (Exception e) {
			debug("##====����ġ ���� �����Դϴ�.");
			if ( gubun.equals("3") ) {
				validCertMsg = "����ġ ���� �����Դϴ�. \n\n������ ��� �߻��ÿ��� ��ī�� �����ڿ��� ���� �ٶ��ϴ�.";
			}else {
				validCertMsg = "����ġ ���� �����Դϴ�. \\n\\n������ ��� �߻��ÿ��� ��ī�� �����ڿ��� ���� �ٶ��ϴ�.";
			}			
			e.printStackTrace();
			result = false;
			// ��Ÿ ����ġ ���� ����
		}	
		
		return result;
	}
 */

	/*******************************************************************
	* ���ǿ� �����޼��� ���� 
	* @param session		HttpSession ��ü.
	******************************************************************/
	public void setValidCertMsg(HttpSession session){
		String sessionMsg = (String)session.getAttribute("validCertMsg");
		if(sessionMsg!=null){
			session.removeAttribute("validCertMsg");
		}
		session.setAttribute("validCertMsg", this.validCertMsg);
	}
	
	/*******************************************************************
	* �����DN���� ����
	* @return String		String ����.
	******************************************************************/
	public String getUserDn(){
		return this.userDn;
	}
}
