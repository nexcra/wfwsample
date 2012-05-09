/*****************************************************************************
 * 클래스명 : DateFormat
 * 작성자	: 김연길
 * 내용		: 날짜 양식 설정
 * 적용범위 : bccard 
 * 작성일자 : 2005.08.03 
********************************수정이력***************************************
 * 일자			수정자		변경사항 
 * 2005.08.03	김연길		날짜 양식 설정
 ******************************************************************************/
package com.bccard.golf.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.bccard.waf.logging.WaLogger;

/*******************************************************************************
 * 날짜 양식 설정
 * 
 * @author 김연길
 * @version 2005.08.03
 *  
 ******************************************************************************/ 
public class DateFormat{
	WaLogger logger = (WaLogger) WaLogger.getLogger("DateFormat");
	
	private static String dateSeparator;
	private static String dashSeparator;
	private static String dateDotSeparator;
	private static String timeSeparator;
	public static String korean24H = "yyyy년 M월 dd일 kk시 mm분 ss초";
	public static String rowshortFull = "yyMMddkkmmss";
	public static String rowLongFull = "yyyyMMddkkmmss";
	public static String rawshortDate = "yyMMdd";
	public static String rawlongDate = "yyyyMMdd";
	public static String rawShortTime = "kkmm";
	public static String rawLongTime = "kkmmss";
	public static String shortFull;
	public static String longFull;
	public static String shortDate;
	public static String longDate;
	public static String shortTime;
	public static String longTime;
	public static String longDotDate;
	public static String koreaDotCom;
	public static String yyyymm = "yyyyMM";

	static{
	    dateSeparator = "/";
	    dashSeparator = "-";
	    dateDotSeparator = ".";
	    timeSeparator = ":";
	    shortFull = "yy" + dateSeparator + "M" + dateSeparator + "dd kk" + timeSeparator + "mm" + timeSeparator + "ss";
	    longFull = "yyyy" + dateSeparator + "M" + dateSeparator + "dd kk" + timeSeparator + "mm" + timeSeparator + "ss";
	    shortDate = "yy" + dateSeparator + "M" + dateSeparator + "dd";
	    longDate = "yyyy" + dateSeparator + "M" + dateSeparator + "dd";
	    shortTime = "kk" + timeSeparator + "mm";
	    longTime = "kk" + timeSeparator + "mm" + timeSeparator + "ss";
	    longDotDate = "yyyy" + dateDotSeparator + "MM" + dateDotSeparator + "dd";
	    koreaDotCom = "yyyy" + dashSeparator + "MM" + dashSeparator + "dd" + dashSeparator + "kk" + dashSeparator + "mm";
	}
	
	/*************************************************************************
	* DateFormat 기본 생성자
	* @param 
	* @return 
	*************************************************************************/
	public DateFormat(){}
	
	/*************************************************************************
	* 양식 설정
	* @param S 양식형태
	* @return String 해당양식으로 가공된 데이터
	*************************************************************************/
	public static String format(String s){
	    return DateFormat.format(new Date(), s);
	}
	
	/*************************************************************************
	* 양식 설정
	* @param l
	* @param S 양식형태
	* @return String 해당양식으로 가공된 데이터
	*************************************************************************/
	public static String format(long l, String s){
	     return DateFormat.format(new Date(l), s);
	}
	
	/*************************************************************************
	* 양식 설정
	* @param date 날짜
	* @param s 양식 설정
	* @return String 해당양식으로 가공된 데이터
	*************************************************************************/
	public static String format(Date date, String s){
	    SimpleDateFormat simpledateformat = new SimpleDateFormat(s);
	    return simpledateformat.format(date);
	}

	/*************************************************************************
	* 양식 설정
	* @param s 날짜
	* @param s1 날짜
	* @param s2 날짜
	* @return String 해당양식으로 가공된 데이터
	*************************************************************************/
	public static String format(String s, String s1, String s2)	{
	     return DateFormat.format(DateFormat.parse(s, s1), s2);
	}
	
	/*************************************************************************
	* 날짜 구문 분석
	* @param s	
	* @param s1
	* @return String 해당양식으로 가공된 데이터
	*************************************************************************/
	public static Date parse(String s, String s1){
	    SimpleDateFormat simpledateformat = new SimpleDateFormat(s1);	    
	    Date date = null;
	    try{
	        date = simpledateformat.parse(s);
	    }
	    catch(ParseException parseexception) { }
	    return date;
	}
}