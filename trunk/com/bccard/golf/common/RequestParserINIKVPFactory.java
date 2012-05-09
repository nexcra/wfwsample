/***************************************************************************************
*  Ŭ������        :   RequestParserINIKVPFactory
*  �� �� ��        :   ���뱹
*  ��    ��        :   INISafeWeb, KVPPlugin(ISP) ��ȣȭ RequestParser
*  �������        :   bccard
*  �ۼ�����        :   2008-08-14
************************** �����̷� ***************************************************
* ����			����		�ۼ���		�������
****************************************************************************************/
package com.bccard.golf.common;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.RequestParserFactory;
import com.inicis.crypto.SymEncDec;
//import com.initech.iniplugin.IniPlugin;

/** ****************************************************************************
 * INISafeWeb, KVPPlugin(ISP) ��ȣȭ RequestParser
 * @version   2008-08-14
 * @author    ���뱹
 **************************************************************************** */
public class RequestParserINIKVPFactory extends RequestParserFactory {
	
	static ResourceBundle config = ResourceBundle.getBundle("golf");
    /** IniPlugin.properties ���� ��� */
	public static final String INIPLUGIN_PATH = config.getString("IniPlugin");
 
    /** INISafeWeb ��ȣȭ�� ���� ���� �Ķ���͸� */
    public static final String INIpluginData = "INIpluginData";
    /** KVPPlugin(ISP) ��ȣȭ�� ���� ���� �Ķ���͸� */
    public static final String KVPpluginData = "KVPpluginData";
    /** INIpluginData �� KVPPlugin(ISP) �� ���� ��ȣȭ�Ǿ����� ������ �� �ִ� �Ķ���͸� */
    public static final String KVP_CARDCODE = "KVP_CARDCODE";

	private transient Logger logger;

	/** Constructor. */
	public RequestParserINIKVPFactory() {
		initLogger();
	}

	/** */
	private void initLogger() {
		if ( this.logger == null ) this.logger = Logger.getLogger(this.getClass().getName());
	}

	/** */
	protected void error(String message, Throwable t) {
		initLogger();
		this.logger.error(message,t);
	}

	/** */
	protected void warn(String message) {
		initLogger();
		this.logger.warn(message);
	}

	/** */
	protected void debug(String message) {
		initLogger();
		this.logger.debug(message);
	}

    /** ************************************************************************
     * ��û������ �м��Ͽ� RequestParser�� ��ȯ.
     * @param  request HttpServletRequest
     * @param  response HttpServletResponse
     * @return RequestParser
     ************************************************************************ */
	public RequestParser getRequestParser(ServletRequest request, ServletResponse response) {
		HashMap pMap = new HashMap();
		boolean isFin = false;

		String inipluginparam = request.getParameter(RequestParserINIKVPFactory.INIpluginData);
		// INIpluginData �Ķ���Ϳ� ���� ������ ���
		if ( inipluginparam != null && inipluginparam.length() > 0 ) {
			String kvp_cardcode = request.getParameter(RequestParserINIKVPFactory.KVP_CARDCODE);
			// INIpluginData �Ķ���Ͱ� KVPPlugin(ISP) �� ���� ��ȣȭ �Ǿ� ���� ���.
			if ( kvp_cardcode != null && kvp_cardcode.length() > 0 ) {
				// KVP ��ȣȭ ������ ���� ��´�.
				isFin = fillKVP(request,inipluginparam,pMap);
			} else {
				isFin = fillINISafeWeb(request,response,pMap);
			}
		}
		if ( isFin ) return new RequestParser( pMap );

        // ��ȣȭ ���н�
		fillNormal(request,response,pMap);

		// �Ϲ� Map �����ʹ� ���ڿ� ���ڵ��� ���� charset�� �Է��ؾ� �Ѵ�.
        // request �� charset �� ������ 8859_1 �� default �� ���Ѵ�.
        String charset = request.getCharacterEncoding();
		if ( charset == null ) charset = "8859_1";
		if ( charset.trim().length() == 0 ) charset = "8859_1";
		return new RequestParser( pMap, charset, response.getCharacterEncoding() );
	}

	/**
	 *
	 */
	protected boolean fillKVP(ServletRequest request, String encKvpData, HashMap pMap) {
		try {
			String decryptedData = SymEncDec.decrypt(1,encKvpData);
			int idx = 0;
			String tok = null;
			String key = null;
			String val = null;
			for(StringTokenizer st = new StringTokenizer(decryptedData,"%"); st.hasMoreTokens(); ) {
				tok = st.nextToken();
				idx = tok.indexOf("=");
				if ( idx != -1 ) {
					key = tok.substring(0,idx);
					val = tok.substring(idx+1);
					pMap.put(key,val);
					debug("INIKVP.KVP:" + key + "=" + val);
				}
			}
			return true;
		} catch (Throwable t) {
			error("INIKVP.KVP Decrypt fail " + ((HttpServletRequest)request).getRequestURI() ,t );
            return false;
		}
    }

	/**
	 *
	 */
	protected boolean fillINISafeWeb(ServletRequest request, ServletResponse response, HashMap pMap) {
		Properties p = null;
		Vector    paramVec = null;
		String    paramName = null;
		String    paramValue = null;
		String[]  paramValues = null;
		String    kvppluginData = null;
		try{
//			IniPlugin iPlugin = new IniPlugin((HttpServletRequest)request, (HttpServletResponse)response, RequestParserINIKVPFactory.INIPLUGIN_PATH); // ��ȣȭ ��ü
//			iPlugin.init();
//			p = iPlugin.getDecryptParameter();

            Object o;
            for (Enumeration e = p.propertyNames(); e.hasMoreElements();) {
                paramName = ((String)e.nextElement()).trim();
				o = p.get(paramName);
				// INISafeWeb �� Ǯ���� KVPpluginData ���� �ִ��� Ȯ���Ѵ�. (���߾�ȣȭ)
				if ( RequestParserINIKVPFactory.KVPpluginData.equals(paramName) ) {
					if ( o instanceof Vector ) {
						paramVec = (Vector)o;
						if ( paramVec.size()>0 ) {
							kvppluginData = (String)paramVec.get(0);
						}
					} else if ( o instanceof String ) {
						kvppluginData = (String)o;
					}
				} else {
					if ( o instanceof Vector ) {
						paramVec = (Vector)o;
						if ( paramVec.size()==1 ) {
							paramValue = (String)paramVec.firstElement();
							if (paramValue!=null) {
								paramValue = paramValue.trim();
							} else {
								paramValue = "";
							}
							if ( paramName != null ) pMap.put(paramName,paramValue);
						} else {
							paramValues = new String[paramVec.size()];
							for (int i=0;i<paramVec.size();i++) {
								paramValues[i] = (String)paramVec.elementAt(i);
								if (paramValues[i]!=null) {
									paramValues[i] = paramValues[i].trim();
								} else {
									paramValues[i] = "";
								}
							}
							if ( paramName != null ) pMap.put(paramName,paramValues);
						}
					} else if ( o instanceof String ) {
						paramValue = (String) o;
						if ( paramName != null ) pMap.put(paramName,paramValue);
					}
				}
			}
		} catch(Throwable t) {
			error("INIKVP.INT Decrypt fail " + ((HttpServletRequest)request).getRequestURI() ,t );
			return false;
		}
		// INISafeWeb �� Ǯ���� KVPpluginData ���� ������ KVP ��ȣȭ ����.
		if ( kvppluginData != null && kvppluginData.length() > 0 ) {
			return fillKVP(request,kvppluginData,pMap);
		} else {
			return true;
		}
	}

	/**
	 *
	 */
	protected void fillNormal(ServletRequest request, ServletResponse response,HashMap pMap) {
		warn("INIKVP.NOR " + ((HttpServletRequest)request).getRequestURI() );
		for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
			String paramName = ((String)e.nextElement());
			String[] paramValues = request.getParameterValues( paramName );
			pMap.put(paramName,paramValues);
		}
	}


}