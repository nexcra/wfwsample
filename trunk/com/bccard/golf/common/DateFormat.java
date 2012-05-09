/*****************************************************************************
 * Ŭ������ : DateFormat
 * �ۼ���	: �迬��
 * ����		: ��¥ ��� ����
 * ������� : bccard 
 * �ۼ����� : 2005.08.03 
********************************�����̷�***************************************
 * ����			������		������� 
 * 2005.08.03	�迬��		��¥ ��� ����
 ******************************************************************************/
package com.bccard.golf.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.bccard.waf.logging.WaLogger;

/*******************************************************************************
 * ��¥ ��� ����
 * 
 * @author �迬��
 * @version 2005.08.03
 *  
 ******************************************************************************/ 
public class DateFormat{
	WaLogger logger = (WaLogger) WaLogger.getLogger("DateFormat");
	
	private static String dateSeparator;
	private static String dashSeparator;
	private static String dateDotSeparator;
	private static String timeSeparator;
	public static String korean24H = "yyyy�� M�� dd�� kk�� mm�� ss��";
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
	* DateFormat �⺻ ������
	* @param 
	* @return 
	*************************************************************************/
	public DateFormat(){}
	
	/*************************************************************************
	* ��� ����
	* @param S �������
	* @return String �ش������� ������ ������
	*************************************************************************/
	public static String format(String s){
	    return DateFormat.format(new Date(), s);
	}
	
	/*************************************************************************
	* ��� ����
	* @param l
	* @param S �������
	* @return String �ش������� ������ ������
	*************************************************************************/
	public static String format(long l, String s){
	     return DateFormat.format(new Date(l), s);
	}
	
	/*************************************************************************
	* ��� ����
	* @param date ��¥
	* @param s ��� ����
	* @return String �ش������� ������ ������
	*************************************************************************/
	public static String format(Date date, String s){
	    SimpleDateFormat simpledateformat = new SimpleDateFormat(s);
	    return simpledateformat.format(date);
	}

	/*************************************************************************
	* ��� ����
	* @param s ��¥
	* @param s1 ��¥
	* @param s2 ��¥
	* @return String �ش������� ������ ������
	*************************************************************************/
	public static String format(String s, String s1, String s2)	{
	     return DateFormat.format(DateFormat.parse(s, s1), s2);
	}
	
	/*************************************************************************
	* ��¥ ���� �м�
	* @param s	
	* @param s1
	* @return String �ش������� ������ ������
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