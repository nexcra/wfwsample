/*
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� ���� : 2007. 12. 06 [jwjeong@intermajor.com]
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
	 * ���ڿ��� ������� �˻��մϴ�.
	 * @param input �˻��� ���ڿ�
	 * @return ���ڿ��� null�̰ų� �� ���ڿ�("")�̸� true, �׷��� ������ false.
	 */
	public static boolean isEmpty(String input)
	{
		if(input == null || input.trim().equals("")) { return true; }
		return false;
	}
	
	/**
	 * ���ڿ� ó���� �����ϵ��� ���ڿ��� �����մϴ�.
	 * @param input �˻��� ���ڿ�
	 * @return ���ڿ��� null�� ���� �� ���ڿ�(""), �׷��� ������ �¿� ������ ���ŵ� ���ڿ� 
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
	 * String ���� �����ڿ� ����
     * �迭�� �ٲ۴�.
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
     * �ش� Text�� ���� ������ character���ڸ� �߰��Ͽ� ������ ������ŭ ����
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
     * �ش� Text�� ���� ������ character���ڸ� �߰��Ͽ� ������ ������ŭ ����
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
	 * ���ڿ� ���� Ư�� ���ڸ����� �ٸ� ���ڿ��� �ٲ۴�
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
     * �ð��� �̷����� üũ
     * @param time
     * @return
     */
    public static boolean isFuture(String time)
    {
    	return StringUtil.isFuture(time, "yyyyMMddHHmm");
    }
	/**
	 * �ð��� �̷����� üũ
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
	 * ���ڿ��� �ʹ� ���� �˸��� ���̷� cut + "..." �Ͽ� ����Ѵ�.
	 * �ѱ۰� �������� ��� ���̰� �޶��� �� �����Ƿ� ���� �����ϰ� ǥ���Ѵ�.
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
     * String�� int������ ��ȯ�Ѵ�.           <BR>
     *     * @param    str     int������ ��ȯ�� String���ڿ�.
     * @return   ��ȯ�� int ��.
     */
    public static int stoi(String str)
    {
        if(str == null ) return 0;        
        return (Integer.valueOf(str).intValue());
    }
	/**
	 * jdk1.3 �� ���ںи� �Լ�
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
	* �ݾ����ڸ��� �߶� �����ϴ� �Լ�
	* @param : �ݾ����ڸ��� �߶� �����ϱ� ���� �޴� ������(NowLen, PriceLen)
	* @return : ���� @param�� ���� �ݾ����ڸ��� �߶� ��������.
	*/
	public static String priceOne(int NowLen, int PriceLen, String Price){
		String priceOne = "";

		//if(PriceLen >= NowLen){			//�����ѱ��̰� ���� ��ȯ������ Ŭ��
		//		PriceOne = Price.substring(PriceLen - NowLen, PriceLen - NowLen + 1);
		//}else {
		//	PriceOne = "&nbsp;";
		//}
		return Price;
	}

	/**
	* �ݾ����ڸ��� �߶� �����ϴ� �Լ�
	* @param : �ݾ����ڸ��� �߶� �����ϱ� ���� �޴� ������(NowLen, PriceLen)
	* @return : ���� @param�� ���� �ݾ����ڸ��� �߶� ��������.
	*/
	public static String getPriceOne(int NowLen, int PriceLen, String Price){
		String priceOne = "";

		if(PriceLen >= NowLen){			//�����ѱ��̰� ���� ��ȯ������ Ŭ��
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
	 * ���ڿ��� �޾Ƽ� Enter Key�� Ư�����ڿ�(<BR>)�� ��ȯ�ϰų�
	 * Ư�����ڿ��� Enter key�� ��ȯ��...
	 * - Informix thin driver Bug ������ SQL���� ������ ���Ű ����.
	 * </pre>
	 * @param	String		��ȯ ���
	 * @param	nFlag		��ȯ ����
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
			if( nFlag > 0)	//����̸� Enter Key�� `<BR>`�� ��ȯ
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
	    	else			//�����̸� <BR>�� Enter Key�� ��ȯ
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
     * int���� String���� ��ȯ�Ѵ�.           <BR>
     * 
     * @param    i   String���� ��ȯ�� int ��.
     * @return   ��ȯ�� String ��.
     */
    public static String itos(int i)
    {
        return (new Integer(i).toString());
    }





}
