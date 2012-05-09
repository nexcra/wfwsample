/*
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 일자 : 2007. 12. 06 [jwjeong@intermajor.com]
*/
package com.bccard.golf.common;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
	public static boolean debug = true;
	/**
	 * 문자열이 비었는지 검사합니다.
	 * @param input 검사할 문자열
	 * @return 문자열이 null이거나 빈 문자열("")이면 true, 그렇지 않으면 false.
	 */
	public static boolean isEmpty(String input)
	{
		if(input == null || input.trim().equals("")) { return true; }
		return false;
	}
	
	/**
	 * 문자열 처리가 가능하도록 문자열을 변경합니다.
	 * @param input 검사할 문자열
	 * @return 문자열이 null인 경우는 빈 문자열(""), 그렇지 않으면 좌우 공백이 제거된 문자열 
	 */
	public static String nullToEmpty(Object input)
	{
        if (input == null)
        {
            return ""; 
        }
        if ("null".equals(input.toString().toLowerCase()))
        {
            return "";
        }
        else
        {
            return input.toString().trim();
        }
	}
    /**
     * @param str
     * @param empty
     * @return
     */
    public static String nullToEmpty(Object str, String empty)
    {
        if (str == null|| "".equals(str.toString().trim()))
        {
            return empty;
        }
        if ("null".equals(str.toString().toLowerCase()))
        {
            return empty;
        }
        else
        {
            return str.toString().trim();
        }
    }
    
    /**
     * @param arr
     * @param delim
     * @return
     */
    public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (arr == null || arr.length == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}

	/**
	 * String 값을 구분자에 따라
     * 배열로 바꾼다.
	 * @param  pmsSource, pmsDelim
 	 * @return String[]
	 */
	public static String[] stringToArray(String pmsSource, String pmsDelim) {
		String[] lmoArray = null;
		StringTokenizer lmoToken = null;
		lmoToken = new StringTokenizer(pmsSource, pmsDelim);
		lmoArray = new String[lmoToken.countTokens()];
		for(int i = 0; lmoToken.hasMoreTokens(); i++) {
			lmoArray[i] = lmoToken.nextToken();
		}
		return lmoArray;
	}

    /**
     * 해당 Text에 대해 좌측에 character문자를 추가하여 지정한 공간만큼 맞춤
     * @param text String
     * @param space int
     * @param character char
     * @return String
     */
    public static String lpad(String text, int space, char character)
    {
        if (text == null)
            text="";
        String tmp = "";
        try
        {
            if (space - text.length() < 0)
            {
                return text.substring(0, space);
            }
            for (int i = 0, n = space - text.length(); i < n; i++)
            {
                tmp += character;
            }
        }
        catch (Exception e)
        {
            
        }
        return tmp + text;
    }

    /**
     * 해당 Text에 대해 우측에 character문자를 추가하여 지정한 공간만큼 맞춤
     * @param text String
     * @param space int
     * @param character char
     * @return String
     */
    public static String rpad(String text, int space, char character)
    {
        if (text == null)
            text="";
        try
        {
            if (space - text.length() < 0)
            {
                return text.substring(0, space);
            }
            for (int i = 0, n = space - text.length(); i < n; i++)
            {
                text += character;
            }
        }
        catch (Exception e)
        {
            
        }
        return text;
    }
    /**
	 * 문자열 내의 특정 문자를열을 다른 문자열로 바꾼다
	 * @param src
	 * @param oldstr
	 * @param newstr
	 */
    public static String replace(String src, String oldstr, String newstr)
    {
        StringBuffer dest = null;
        try
        {
            if (src == null) return null;
            
            dest = new StringBuffer("");
            int  len = oldstr.length();
            int  srclen = src.length();
            int  pos = 0;
            int  oldpos = 0;

            while ((pos = src.indexOf(oldstr, oldpos)) >= 0) 
            {
                dest.append(src.substring(oldpos, pos));
                dest.append(newstr);
                oldpos = pos + len;
            }

            if (oldpos < srclen)
                dest.append(src.substring(oldpos, srclen));
        }
        catch (Exception e)
        {
            return src;
        }
        return dest.toString();
    } 
    
    /**
     * 시간이 미래인지 체크
     * @param time
     * @return
     */
    public static boolean isFuture(String time)
    {
    	return StringUtil.isFuture(time, "yyyyMMddHHmm");
    }
	/**
	 * 시간이 미래인지 체크
	 * @param time
	 * @param format
	 * @return
	 */
	public static boolean isFuture(String time, String format)
	{
		try
		{
			Date now = new Date();

			SimpleDateFormat sdf=new SimpleDateFormat(format);
			
			Date input = sdf.parse(time);

			String nowtime = sdf.format(now);

			Date curtime = sdf.parse(nowtime);

			if(curtime.compareTo(input)<0) 
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			return false;
		}
	}

    /**
	 * 문자열이 너무 길경우 알맞은 길이로 cut + "..." 하여 출력한다.
	 * 한글과 영문자일 경우 길이가 달라질 수 있으므로 좀더 적당하게 표현한다.
     * @param  pmsSource, pmsDelim
     * @return String
     */
    public static String getCutString(String pmsStr, int pmiSize) {
    	
    	Pattern p = Pattern.compile("\\<(\\/?)(\\w+)*([^<>]*)>");
        Matcher m = p.matcher(pmsStr);
        pmsStr = m.replaceAll("");

    	
        int length = 0 ;
        StringBuffer result = new StringBuffer() ;
        for (int k = 0 ; k < pmsStr.length() ; k++){
            if (Character.getType(pmsStr.charAt(k)) == 5) {
                length += 2 ;
                result.append(pmsStr.charAt(k)) ;
            } else {
                length++ ;
                result.append(pmsStr.charAt(k)) ;
            }

            if(length > pmiSize) {
                result.append("...") ;
                break ;
            }
        }
        return result.toString();
    }	
    /**
     * String을 int값으로 변환한다.           <BR>
     *     * @param    str     int값으로 변환될 String문자열.
     * @return   변환된 int 값.
     */
    public static int stoi(String str)
    {
        if(str == null ) return 0;        
        return (Integer.valueOf(str).intValue());
    }
	/**
	 * jdk1.3 용 문자분리 함수
	 * @param str
	 * @param delim
	 * @return String Array
	 */
	public static String[] split(String str, String delim) {
		StringTokenizer st = new StringTokenizer(str, delim);
		String[] strarr = new String[st.countTokens()];
		int i = 0;
		while (st.hasMoreTokens()) {
			//al.add(st.nextToken());
			strarr[i++] = st.nextToken();
		}
		
		return strarr;
	}
	
    /**
     * @param s
     * @return
     */
    public static String convertHtml(String s){
    	if(s == null || s.length() == 0){
    		return "";
    	}
    	return s.replaceAll("&", "&amp")
    	  		.replaceAll("<", "&lt")
    	  		.replaceAll(">", "&gt")
    	  		.replaceAll("\r\n", "<br>")
    	  		.replaceAll("  ", "&nbsp;&nbsp;");
    }	

	/**
	* 금액한자리씩 잘라서 리턴하는 함수
	* @param : 금액한자리씩 잘라서 리턴하기 위해 받는 변수값(NowLen, PriceLen)
	* @return : 받은 @param에 대해 금액한자리씩 잘라서 리턴해줌.
	*/
	public static String priceOne(int NowLen, int PriceLen, String Price){
		String priceOne = "";

		//if(PriceLen >= NowLen){			//가격총길이가 현재 반환값보다 클때
		//		PriceOne = Price.substring(PriceLen - NowLen, PriceLen - NowLen + 1);
		//}else {
		//	PriceOne = "&nbsp;";
		//}
		return Price;
	}

	/**
	* 금액한자리씩 잘라서 리턴하는 함수
	* @param : 금액한자리씩 잘라서 리턴하기 위해 받는 변수값(NowLen, PriceLen)
	* @return : 받은 @param에 대해 금액한자리씩 잘라서 리턴해줌.
	*/
	public static String getPriceOne(int NowLen, int PriceLen, String Price){
		String priceOne = "";

		if(PriceLen >= NowLen){			//가격총길이가 현재 반환값보다 클때
			priceOne = Price.substring(PriceLen - NowLen, PriceLen - NowLen + 1);
		}else {
			priceOne = "&nbsp;";
		}
		
		return priceOne;
	}
	
	/**
	 * @param ln
	 * @param str
	 * @return
	 */
	public static String priceOne(int ln, String str){
		int sLn = str.length();
		if(ln > sLn){
			return "&nbsp;";
		}else{
			return str.substring(sLn-ln, sLn-ln+1);
		}
	}
	
	
	
    /**
	 * <pre>
	 * 문자열을 받아서 Enter Key를 특정문자열(<BR>)로 변환하거나
	 * 특정문자열을 Enter key로 변환함...
	 * - Informix thin driver Bug 때문에 SQL문을 생성시 사용키 위함.
	 * </pre>
	 * @param	String		변환 대상
	 * @param	nFlag		변환 방향
	 * @return	'String'			
	 */
	public static String convertBR(String str, int	nFlag)
	{
		StringBuffer fileStr = new StringBuffer();
		
		int i = 0;
		int lasti = 0;
		
		if(str == null)
		{
			return "";	
		}
		else
		{
			if( nFlag > 0)	//양수이면 Enter Key를 `<BR>`로 변환
	    	{	    			    		

				for(; i < str.length(); i++)
				{
					
					if( str.charAt(i) == '\r' )
					{
						fileStr.append("<BR>");
					}	
					else if( str.charAt(i) == '\n' )		{ ;}	
					else
					{
						fileStr.append(str.charAt(i));	
					}
									
				}//end for
								
				return fileStr.toString();
	    	}
	    	else			//음수이면 <BR>를 Enter Key로 변환
	    	{
				
				i = str.indexOf("<BR>"); 
				//DEBUG.TRACE("br == " + i);

				while( (i != -1) && (i < str.length()) )
				{
					
					fileStr.append(str.substring(lasti, i));
					fileStr.append("\r\n");
					
					i += 4;
					lasti = i;
					
					i = str.indexOf("<BR>", lasti); 
					//DEBUG.TRACE("br == " + i);
				}
				
				if(i < str.length())
				{
					fileStr.append( str.substring( lasti, str.length() ) );						
				}				
								
	    	}//end sub if-else
	    	
	    }//end main if-else
		
		//DEBUG.TRACE(" convertBR : " +  fileStr.toString() + "==");
		return  fileStr.toString();
	}//end convertRN method
	

	/**
	* Use this method like this:<br>
	* <font color="#0000ff">String formatted = Util.formatIntoCurr(123456789.123, 2);<br>
	* formatted ==> "123,456,789.12"
	* </font>
	* @param number the number to truncate digits 
	* @param digits the number of count to be remained after the fraction point
	* @return formatted double String
	*/		
	public static String formatIntoCurr(double number, int digits) {
		return StringUtil.formatIntoCurr(""+ number, digits);
	}


	/**
	* Use this method like this:<br>
	* <font color="#0000ff">String formatted = Util.formatIntoCurr("123456789.123", 2);<br>
	* formatted ==> "123,456,789.12"
	* </font>
	* @param str_numer String representation of the number to truncate digits 
	* @param digits the number of count to be remained after the fraction point
	* @return formatted double
	*/	
	public static String formatIntoCurr(String str_number, int digits) {
		String pattern = "###,###";
		double value = -1.;
		for(int i = 0; i < digits; i++) {
			if(i == 0) pattern +=".";
			pattern += "0";
		}
		try {
			value = Double.parseDouble(str_number);
		} catch(NumberFormatException e) {
			StringUtil.print("Bad Number String!! -> " + str_number);
			e.printStackTrace();
		}
		return StringUtil.toCurrencyFormat(pattern, value);
	}

	/**
	* This method actually does all for number formatting into Currency 
	* @param pattern pattern to apply to the given double value
	* @param value number to be formatted
	* @return formatted currency String
	*/	
	private static String toCurrencyFormat (String pattern , double value) {
		DecimalFormat formatter = new DecimalFormat(pattern);
		return formatter.format(value);	
	}
	
//	private static String prefix = "";
	/**
	 * @param msg
	 */
	private static void print(String msg) {
		
	}

    /**
     * int값을 String으로 변환한다.           <BR>
     * 
     * @param    i   String으로 변환될 int 값.
     * @return   변환된 String 값.
     */
    public static String itos(int i)
    {
        return (new Integer(i).toString());
    }





}
