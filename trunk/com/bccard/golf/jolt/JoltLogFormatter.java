/* *************************************************************************************************
* CLASS NAME  : JoltLogFormatter
* CREATED BY  : csj007
* DESCRIPTION : Page Navigation Buffer Class for JOLT System
* APP. SCOPE  : BEA JOLT Packages for BC ${BC_SITE} under WATRIX FrameWork
* CREATED IN  : 2008-07-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*    20080520  1.0    csj007   졸트로그에 사업자번호 추가
***************************************************************************************************/

/******************************************************************************************
BGN|서비스명|{회원고유번호 or 카드번호}|사업자번호|IP|시작시간
END|서비스명|{회원고유번호 or 카드번호}|사업자번호|IP|종료시간|응답시간|RetCode
******************************************************************************************/
package com.bccard.golf.jolt;

import java.util.Date;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;

//import com.bccard.golf.common.GolfUserEtt; 
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;


/** ***************************************
* renewal 
* @version 2008.07.28 
* @author csj007
********************************************** */
public class JoltLogFormatter {
	private static final String FETCH_ERROR = "FETCH_ERROR";
	private static final String BGN = "[JOLT]BGN";
	private static final String END = "[JOLT]END";
	private static final String SEPARATOR = "|";
	private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	private static final String RETURN_CODE = "RETURN_CODE";
	private static final String RETURN_CASH = "RETURN_CASH";
	private static final String NATIVE = "NATIVE";
/** ********************************************************************************
* 기업
* @version 2008.07.28 
* @author csj007
* @param req HttpServletRequest 객체.
* @param jin JoltInput객체.
* @param prop Properties객체.
* @param beginPoint Date객체.
* @return   
********************************************************************************** */ 
	public static String getBeginningJLog(HttpServletRequest req
										  , JoltInput jin
										  , Properties prop
										  , Date beginPoint ) {
		String header = BGN;
		String serviceName = jin.getServiceName();
		String memId = "unKnownUser";
		//String buzNo = JoltLogFormatter.getBuzNo(req, jin);
		String buzNo = "9999999999";
		String ipAddress = JoltLogFormatter.getRemoteAddr(req);
		String timeStamp = DateUtil.format(beginPoint, DATE_FORMAT);

		StringBuffer buff = new StringBuffer();
		buff.append(header).append(SEPARATOR)
			.append(serviceName).append(SEPARATOR)
			.append(memId).append(SEPARATOR)
			.append(buzNo).append(SEPARATOR)
			.append(ipAddress).append(SEPARATOR)
			.append(timeStamp);
		return buff.toString();
	}
/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param req HttpServletRequest 객체.
* @param jin JoltInput객체.
* @param prop Properties객체.
* @param beginPoint Date객체.
* @param endPoint Date객체.
* @return   
********************************************************************************** */ 
	public static String getEndingJLog(HttpServletRequest req
									  , JoltInput jin
									  , JoltOutput jout
									  , Properties prop
									  , Date beginPoint
									  , Date endPoint ) {
		String header = END;
		String serviceName = jin.getServiceName();
		String memId = "unKnownUser";;
		//String buzNo = JoltLogFormatter.getBuzNo(req, jin);
		String buzNo = "9999999999";
		String ipAddress = JoltLogFormatter.getRemoteAddr(req);
		String timeStamp = DateUtil.format(endPoint, DATE_FORMAT);
		long duration = endPoint.getTime() - beginPoint.getTime();
		String returnCode = JoltLogFormatter.getReturnCode(prop, jout);

		StringBuffer buff = new StringBuffer();
		buff.append(header).append(SEPARATOR)
			.append(serviceName).append(SEPARATOR)
			.append(memId).append(SEPARATOR)
			.append(buzNo).append(SEPARATOR)
			.append(ipAddress).append(SEPARATOR)
			.append(timeStamp).append(SEPARATOR)
			.append(duration).append(SEPARATOR)
			.append(returnCode);
		return buff.toString();
	}


/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param req HttpServletRequest객체.
* @param jin JoltInput객체.
* @return  String 
********************************************************************************** */ 
/*
	private static String getUserId(HttpServletRequest req, JoltInput jin) {
        try {
            HttpSession sess = req.getSession(false);
            GolfUserEtt wickedObj = (GolfUserEtt) sess.getAttribute( "SESSION_USER" );
            return wickedObj.getMemId();
		} catch (NullPointerException ne){
            try {
                return jin.getString("fml_arg2");
            } catch (Exception e) {
                return "0";
            }
		}
	}
*/
/** ******************************************************************************** 
* 기업
* @version 2008 05 19 
* @author csj007
* @param req HttpServletRequest객체.
* @param jin JoltInput객체.
* @return  String 
********************************************************************************** */ 
/*	private static String getBuzNo(HttpServletRequest req, JoltInput jin) {
        try {
            HttpSession sess = req.getSession(false);
            EtaxUserEtt wickedObj = (EtaxUserEtt) sess.getAttribute( "SESSION_USER" );
            return wickedObj.getBuzNo();
		} catch (NullPointerException ne){
            try {
                return jin.getString("fml_arg2");
            } catch (Exception e) {
                return "0";
            }
		}
	}
*/
/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param req HttpServletRequest객체.
* @return  String 
********************************************************************************** */ 
    private static String getRemoteAddr(HttpServletRequest req) {
        try {
            return req.getRemoteAddr();
		} catch (NullPointerException e){
			return "000.000.000.000";
		}
    }


/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param prop Properties객체.
* @param jout JoltOutput객체.
* @return  String 
***********************************************************************************/ 
	private static String getReturnCode(Properties prop, JoltOutput jout) {
		try {
			if (prop.containsKey( RETURN_CODE )) {
				String value = prop.getProperty( RETURN_CODE );
				if (NATIVE.equalsIgnoreCase(value)) {
					return "" + jout.getApplicationCode();
				} else {
					return jout.getString( value );
				}
			} else if (prop.containsKey( RETURN_CASH )) {
				return JoltLogFormatter.getCashReturnCode (jout);
			} else {
                try {
                    return jout.getString("fml_ret1");
                } catch (Exception e) {
                    return FETCH_ERROR;
                }
			}
		} catch (Exception e) {
			
			return FETCH_ERROR;
		}
	}
	

/** ******************************************************************************** 
* 기업
* @version 2008.07.28 
* @author csj007
* @param jout JoltOutput객체.
* @return  getCashReturnCode 
********************************************************************************** */ 
	private static String getCashReturnCode (JoltOutput jout) {
		String result_code = "";
		try {
			Object obj = jout.getObject("data");
			byte[] rtnByte = (byte[])obj;
			result_code = new String(rtnByte,33,2);
		} catch (Exception e) {
			result_code = "CASH_JOLT_ERROR";
		}
		return result_code;
	}
	
}
