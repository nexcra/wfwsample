/***************************************************************************************
*  클래스명        :   GolfConfig
*  작 성 자        :   csj007
*  내    용        :   통합 지방세 수납 처리를 위한 설정 정보
*  적용범위        :   Golf
*  작성일자        :   2008.06.27
************************** 수정이력 ***************************************************
* 일자            수정자         변경사항
* 2008.03.19     csj007          사용자구분에 따르는 가맹점번호 출력 메소드 getMbcdhd(userClss) 추가
****************************************************************************************/
package com.bccard.golf.common;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import com.bccard.waf.action.AbstractObject;
//import com.bccard.waf.common.StrUtil; 

/**
 * .
 * WEB-INF/classes 밑에 golf.properties 파일이 반드시 존재해야 한다.
 * @author csj007
 * @version 20070912
 */
public class GolfConfig extends AbstractObject {
	private static final String CONFIG_FILE = "golf.properties";
	private static GolfConfig singleton;
	
	/** 
	 * 설정 정보 반환.
	 * @return 설정 정보.
	 */
	public synchronized static GolfConfig getInstance() {
		if ( singleton == null ) singleton = new GolfConfig();
		return singleton;
	}

	private Properties prop;
	/*
	private String xc3host;
	private String xc3port;
	private String xc3pass;
	private String xc3conf;
	private String behost;
	private String beport;
	private String joltXA;
	private String joltNon;
	private String joltTao;
	private String dbFactory;
	*/
	private String mbcdhd;
	private String teMbcdhd;
	/*
	private String npki;
	private String boardImgUrl;
	private String boardImgPath;
	private String boardAtcPath;
	private String boardTmpPath;
	*/
	private String emailTemplate;
	private String emailTemp1;
	
	private String dvWas1ST;
	//Sms발송정보
	private String smsHost;
	private String smsPort;

	/**
	 * Constructor.
	 */
	private GolfConfig() {
		this.prop = new Properties();
		InputStream is = null;
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			if ( classLoader == null ) classLoader = this.getClass().getClassLoader();
			is = classLoader.getResourceAsStream(CONFIG_FILE);
			prop.load(is);
		} catch(Throwable t) {
			error(CONFIG_FILE + " load failed.",t);
		} finally {
			try { is.close(); } catch(Throwable ignore) {}
		}

		String addr = "";
		try {
			InetAddress inet = InetAddress.getLocalHost();
			addr = inet.getHostAddress();
		} catch (UnknownHostException e) {
			error("GOLF Configuration warning : Unknown Local Address",e);
		}
		
		this.dvWas1ST		 	= prop.getProperty("DV_WAS_1ST","");
		//Sms발송정보
		this.smsHost = prop.getProperty("SMS.HOST");
		this.smsPort = prop.getProperty("SMS.PORT");
		
		//이메일 템플릿 경로
		this.emailTemplate = prop.getProperty("EMAILDIR","");
		
		//이메일 템플릿 파일명
		this.emailTemp1 = prop.getProperty("EMAILTEMP1","");

		/*
		this.xc3host   = getPropertyAddr("XC3.HOST.",addr);
		this.xc3port   = getPropertyAddr("XC3.PORT.",addr);
		this.xc3pass   = getPropertyAddr("XC3.PASS.",addr);
		this.xc3conf   = getPropertyAddr("XC3.CONF.",addr);
		this.behost    = getPropertyAddr("BE.HOST." ,addr);
		this.beport    = getPropertyAddr("BE.PORT." ,addr);
		this.npki      = getPropertyAddr("NPKI."    ,addr);
		this.boardImgUrl  = getPropertyAddr("IMG_URL.",addr);
		this.joltXA    = prop.getProperty("JOLT.XA" ,"");
		this.joltNon   = prop.getProperty("JOLT.NON","");
		this.joltTao   = prop.getProperty("JOLT.TAO","");
		this.dbFactory = prop.getProperty("DB.FACTORY","");
		this.mbcdhd    = prop.getProperty("MBCDHD","");
		this.teMbcdhd  = prop.getProperty("TE.MBCDHD","");
		this.boardImgPath  = prop.getProperty("CONT_IMG_PATH","");
		this.boardAtcPath  = prop.getProperty("CONT_ATC_PATH","");
		this.boardTmpPath  = prop.getProperty("CONT_TMP_PATH","");
		
		StringBuffer buf = new StringBuffer();
		buf.append("GOLF Configuration");
		buf.append("\n    XC3.HOST  :").append(xc3host);
		buf.append("\n    XC3.PORT  :").append(xc3port);
		buf.append("\n    XC3.CONF  :").append(xc3conf);
		//buf.append("\n    XC3.PASS  :").append(EtaxEtt.lpad("",xc3pass.length(),'*'));
		buf.append("\n    NPKI      :").append(npki);
		buf.append("\n    JOLT.XA   :").append(joltXA);
		buf.append("\n    JOLT.NON  :").append(joltNon);
		buf.append("\n    JOLT.TAO  :").append(joltTao);
		buf.append("\n    DB.FACTORY:").append(dbFactory);
		buf.append("\n    MBCDHD    :").append(mbcdhd);
		buf.append("\n    BE.HOST   :").append(behost);
		buf.append("\n    BE.PORT   :").append(beport);
		buf.append("\n    TE.MBCDHD :").append(teMbcdhd);
		info(buf.toString());
		*/
	}

	/**
	 * 
	 * @param prefix
	 * @param addr
	 * @return
	 */
	/*
	private String getPropertyAddr(String prefix, String addr) {
		return getPropertyAddr(prefix,addr,null);
	}
	*/
	
	/**
	 * 
	 * @param prefix
	 * @param addr
	 * @param def
	 * @return
	 */
	/*
	private String getPropertyAddr(String prefix, String addr, String def) {
		String val = prop.getProperty(prefix + addr);
		if ( val == null ) {
			val = prop.getProperty(prefix + "REAL");
			if ( val == null )	val = def;
		}
		return val;
	}
	*/
	
	/**
	 * 가맹점 번호 반환.
	 * @param userClss 사용자구분
	 * @return 가맹점 번호
	 */
	public String getMbcdhd(String userClss) {
		if ( "SE".equalsIgnoreCase(userClss) ) {
			return mbcdhd;
		} else if ( "TE".equalsIgnoreCase(userClss) ) {
			return teMbcdhd;
		} else {
			return "";
		}
	}
	/**
	  * 이메일 템플릿 경로
	  * @return String
	  */ 
	 public String getEmailTempt(){
	  return this.emailTemplate;
	 }	
	 
	 /**
	  * 이메일 템플릿 경로(쇼핑몰 문의사항)
	  * @return String
	  */ 
	 public String getEmailTemp1(){
	  return this.emailTemp1;
	 }	
	 

	/**
	 * 개발기 아이피
	 * @return String
	 */
	public String getDvWas1ST() {
		return this.dvWas1ST;
	}
	

	public String getSmsHost() {
		return this.smsHost;
	}

	public String getSmsPort() {
		return this.smsPort;
	}


	


}
