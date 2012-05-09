/******************************************************************************************
개인회원 로그인 
STR|UHL016_Ind_Ret|null|CardNumber unused call|NOT_MEMBER|IP|시작시간
 => STR|UHL016_Ind_Ret|null|주민/사업자/가맹점번호|NOT_MEMBER|IP|시작시간
CNN|UHL016_Ind_Ret|null|CardNumber unused call|NOT_MEMBER|IP|종료시간|응답시간|RetCode
=> CNN|UHL016_Ind_Ret|null|주민/사업자/가맹점번호|NOT_MEMBER|IP|종료시간|응답시간|RetCode

STR|SVC_NM|회원구분(1,2)|주민/사업자/가맹점번호|이름|IP|시작시간
CNN|SVC_NM|회원구분(1,2)|주민/사업자/가맹점번호|이름|IP|종료시간|응답시간|RetCode

BGN|SVC_NM|회원구분(1,2)|주민/사업자/가맹점번호|이름|IP|시작시간
END|SVC_NM|회원구분(1,2)|주민/사업자/가맹점번호|이름|IP|종료시간|응답시간|RetCode

BGN|SVC_NM|회원구분(1,2)|주민/사업자/가맹점번호|이름|IP|시작시간
END|SVC_NM|회원구분(1,2)|주민/사업자/가맹점번호|이름|IP|종료시간|응답시간|RetCode
******************************************************************************************/
/*******************************************************************************
*  클래스명		:   BcJoltLogFormatter.java
*  작성자		:   조용국
*  내용			:  
*  적용범위		:   bccard
*  작성일자		:   2004.03.24
************************** 수정이력 ********************************************
* 일자			수정자		변경사항 
* 2006.11.23. 	khko		HOST FRAMEWORK 변환에 의한 전문 수정 적용 시작
*******************************************************************************/
package com.bccard.golf.common;

import java.util.Properties;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.tao.jolt.JoltInput;
import com.bccard.waf.tao.jolt.JoltOutput;
import com.bccard.golf.common.login.BcUserEtt;

/**
* log formatter
* @version 2004.03.24
* @author 조용국
*/ 
public class BcJoltLogFormatter {
	private static final String NO_CARD_ACCESS = "CardNumber unused call";
	//private static final String UNIDENT_CLASS = "UNIDENTIFIED_USER_CLASS";
	private static final String UNIDENT_CLASS = "NOT_LOGIN_USER_CLASS";
	private static final String NOT_MEMBER = "NOT_MEMBER";
	private static final String JUDGE_ERROR = "JUDGE_ERROR";
	private static final String UNIDENTIFIABLE = "UNIDENTIFIABLE";
	private static final String UNDETERMINED = "UNDETERMINED";
	private static final String FETCH_ERROR = "FETCH_ERROR";
	private static final String LOGIN = "LOGIN";
	private static final String STR = "STR";
	private static final String CNN = "CNN";
	private static final String BGN = "BGN";
	private static final String END = "END";
	private static final String SEPARATOR = "|";
	private static final String DATE_FORMAT = "yyyy/MM/dd HH:mm:ss";
	private static final String RETURN_CODE = "RETURN_CODE";
	private static final String RETURN_CASH = "RETURN_CASH";
	private static final String NATIVE = "NATIVE";
	private static final String LOGIN_USER = "LOGIN_USER";
	private static final String CARD_NO = "CARD_NO";
	private static final String CARD_CRYPT = "****";
	private static final String ILLEGAL_CARD_NO_LENGTH = "card number illegal:";
	private static final String SOC_ID = "SOC_ID";
	
	/**
    * getBeginningJLog
    * @param req HttpServletRequest
    * @param jin JoltInput
    * @param prop Properties 
    * @param beginPoint Date 
    * @version 2004.03.24
 	* @author 조용국
    * @return String
    */
	public static String getBeginningJLog(HttpServletRequest req
										  , JoltInput jin
										  , Properties prop
										  , Date beginPoint ) {
		String header = prop.containsKey(LOGIN)? STR : BGN;
		String serviceName = jin.getServiceName();
		
		// 2006.11.27. HOST FRAMEWORK 변환에 의한 전문 수정 적용 시작
		String serviceNameNew = "";
		try {
			serviceNameNew = jin.getString("fml_trcode");
		} catch (Exception te) {
					
		}
		String fml_sec50 = "";
		try {
			fml_sec50 = jin.getString("fml_sec50");
		} catch (Exception te) {
				
		}
		// 2006.11.27. HOST FRAMEWORK 변환에 의한 전문 수정 적용 끝
		
		String memberType = BcJoltLogFormatter.getMemberType(req);
		String memberCode = BcJoltLogFormatter.getMemberCode(req, prop, memberType);	// 주민/사업자/가맹점번호
		String memberName = BcJoltLogFormatter.getMemberName(req, memberType);
		String ipAddress = req.getRemoteAddr();
		String timeStamp = DateUtil.format(beginPoint, DATE_FORMAT);
		//	String unit;	// 응답시간 단위

		StringBuffer buff = new StringBuffer();
		buff.append(header);		buff.append(SEPARATOR);
		buff.append(serviceName);	buff.append(SEPARATOR);
		buff.append(serviceNameNew);	buff.append(SEPARATOR);
		buff.append(memberType);	buff.append(SEPARATOR);
		buff.append(memberCode);	buff.append(SEPARATOR);
		buff.append(memberName);	buff.append(SEPARATOR);
		buff.append(fml_sec50);	    buff.append(SEPARATOR);
		buff.append(ipAddress);		buff.append(SEPARATOR);
		buff.append(timeStamp);
		return buff.toString();
	}

	/**
    * getEndingJLog
    * @param req HttpServletRequest
    * @param jin JoltInput
    * @param jout JoltOutput
    * @param prop Properties 
    * @param beginPoint Date 
    * @param endPoint Date 
    * @version 2004.03.24
 	* @author 조용국
    * @return String
    */
	public static String getEndingJLog(HttpServletRequest req
									  , JoltInput jin
									  , JoltOutput jout
									  , Properties prop
									  , Date beginPoint
									  , Date endPoint ) {
		String header = prop.containsKey(LOGIN)? CNN : END;
		String serviceName = jin.getServiceName();
		
//		 2006.11.27. HOST FRAMEWORK 변환에 의한 전문 수정 적용 시작
		String serviceNameNew = "";
		try {
			serviceNameNew = jin.getString("fml_trcode");
		} catch (Exception te) {
					
		}
		String fml_sec50 = "";
		try {
			fml_sec50 = jin.getString("fml_sec50");
		} catch (Exception te) {
				
		}
		// 2006.11.27. HOST FRAMEWORK 변환에 의한 전문 수정 적용 끝
		
		String memberType = BcJoltLogFormatter.getMemberType(req);
		String memberCode = BcJoltLogFormatter.getMemberCode(req, prop, memberType);	// 주민/사업자/가맹점번호
		String memberName = BcJoltLogFormatter.getMemberName(req, memberType);
		String ipAddress = req.getRemoteAddr();
		String timeStamp = DateUtil.format(endPoint, DATE_FORMAT);
		long duration = endPoint.getTime() - beginPoint.getTime();
		//	String unit;	// 응답시간 단위
		String returnCode = BcJoltLogFormatter.getReturnCode(prop, jout);

		StringBuffer buff = new StringBuffer();
		buff.append(header);		buff.append(SEPARATOR);
		buff.append(serviceName);	buff.append(SEPARATOR);
		buff.append(serviceNameNew);	buff.append(SEPARATOR);
		buff.append(memberType);	buff.append(SEPARATOR);
		buff.append(memberCode);	buff.append(SEPARATOR);
		buff.append(memberName);	buff.append(SEPARATOR);
		buff.append(fml_sec50);	    buff.append(SEPARATOR);
		buff.append(ipAddress);		buff.append(SEPARATOR);
		buff.append(timeStamp);		buff.append(SEPARATOR);
		buff.append(duration);		buff.append(SEPARATOR);
		buff.append(returnCode);
		return buff.toString();
	}

	/**
    * getMemberType
    * @param req HttpServletRequest    
    * @version 2004.03.24
 	* @author 조용국
    * @return String
    */
	private static String getMemberType(HttpServletRequest req) {
		HttpSession sess = req.getSession(false);
		BcUserEtt wickedObj;
		try {
			wickedObj = (BcUserEtt)sess.getAttribute( LOGIN_USER );
			return wickedObj.getMemberType();
		} catch (NullPointerException e){
			return "0";
		}
	}

	/**
    * getMemberCode
    * @param req HttpServletRequest 
    * @param prop Properties 
    * @param memberType String 
    * @version 2004.03.24
 	* @author 조용국
    * @return String
    */
	private static String getMemberCode(HttpServletRequest req, Properties prop, String memberType) {
		HttpSession sess = req.getSession(false);
		BcUserEtt wickedObj;
		try {
			wickedObj = (BcUserEtt)sess.getAttribute( LOGIN_USER );

			if ("1".equals(memberType)) {
				return wickedObj.getSocid();
			} else if ("2".equals(memberType)) {
				return wickedObj.getBizregno();
//			} else if ("3".equals(memberType)) {
//				return ((StoreUserEtt)wickedObj).getStoreNo();
			} else {
				if (prop.containsKey(CARD_NO) ) {
					String card = prop.getProperty( CARD_NO );
					return BcJoltLogFormatter.replaceCardNo( card );
				} else if (prop.containsKey(SOC_ID)) {
					return prop.getProperty( SOC_ID );
				}{
					return UNIDENT_CLASS;
				}
			}
		} catch (NullPointerException e){
			return UNIDENTIFIABLE;
		} catch (Exception e){
			return JUDGE_ERROR;
		}
	}

	/**
    * getMemberName
    * @param req HttpServletRequest 
    * @param memberType String 
    * @version 2004.03.24
 	* @author 조용국
    * @return String
    */
	private static String getMemberName(HttpServletRequest req, String memberType) {
		HttpSession sess = req.getSession(false);
		BcUserEtt wickedObj;
		try {
			wickedObj = (BcUserEtt)sess.getAttribute( LOGIN_USER );
			if ("0".equals(memberType)) { return UNDETERMINED; }
			else if ("1".equals(memberType)) { return wickedObj.getMemberName(); }
			else if ("2".equals(memberType)) { return wickedObj.getMemberName(); }
			else if ("3".equals(memberType)) { return wickedObj.getMemberName(); }
			else { return NOT_MEMBER; }
		} catch (NullPointerException e){
			return UNIDENTIFIABLE;
		} catch (Exception e){
			return JUDGE_ERROR;
		}
	}

	/**
    * getReturnCode
    * @param prop Properties 
    * @param jout JoltOutput 
    * @version 2004.03.24
 	* @author 조용국
    * @return String
    */
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
				return BcJoltLogFormatter.getCashReturnCode (jout);
			} else {
				return jout.getString("fml_ret1");
			}
		} catch (Exception e) {
			
			return FETCH_ERROR;
		}
	}

	/**
    * replaceCardNo
    * @param cardNo String 
    * @version 2004.03.24
 	* @author 조용국
    * @return String
    */
	private static String replaceCardNo(String cardNo) {
		if (16 == cardNo.length()) {
			return cardNo.substring(0,8) + CARD_CRYPT + cardNo.substring(12, 16);
		} else {
			return ILLEGAL_CARD_NO_LENGTH + cardNo;
		}
	}
	
	/**
    * getCashReturnCode 현금서비스 jolt return code 를위한 메소드 추가 20050121
    * @param jout JoltOutput 
    * @version 2004.03.24
 	* @author 조용국
    * @return String
    */
	private static String getCashReturnCode (JoltOutput jout) {
		String result_Code = "";
		try {
			Object obj = jout.getObject("data");
			byte[] rtnByte = (byte[])obj;
			result_Code = new String(rtnByte,33,2);
		} catch (Exception e) {
			result_Code = "CashJoltError";
		}
		return result_Code;
	}
}