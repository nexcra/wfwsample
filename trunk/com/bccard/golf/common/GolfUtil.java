/** ****************************************************************************
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 * �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 * �ۼ� : 2006.11.08 [�ڼ���(ultteky@e4net.net)]
 * ���� : WELCO ������ƿ.
 ************************** �����̷� *******************************************
 *    ����      ����   �ۼ���   �������
 *
 **************************************************************************** */
package com.bccard.golf.common;
 
import java.io.File;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

//import com.bccard.fortify.FilterUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.core.RequestParser;


/******************************************************************************
* Welco ������ƿ.
* @author JYJUNG
* @version 2006.11.08
******************************************************************************/
public class GolfUtil {

	private String temporary;
	
	 /**
	 * XSS �ʷ��� 2009.09.15
	 * @param str
	 * @param delim  
	 * @return String Array 
	 */	
	public static String sqlInjectionFilter (String sInvalid){
		 
		String sValid = sInvalid;

		if (sValid == null || sValid.equals("")) 
			return "";

        //sValid = FilterUtil.getXSSFilter(sValid);
        
		return sValid;
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
	 * form �Ķ������ select�� �ڵ�;�ڵ���� �и���
	 * @param src �и��ҹ��ڿ�
	 * @param wh �и��� ������ ���� 0:�ڵ�, 1:�ڵ��
	 * @return
	 */
	public static String getSelectParse(String src, int wh) {
		String rv = "";
		//String str1 = "";
		//String str2 = "";
		try {
			String[] str = GolfUtil.split(src,";");
			rv = str[wh];
		} catch (Exception e) {
			rv = "";
		}
		return rv;
	}

	/**
	 * ���� ���̺��� ����PK���� ���´�<br>
	 * �������Ŀ� Ű�� ������
	 * @param con Connection
	 * @param tabName ���̺��
	 * @return Primary Key Value
	 * @throws Exception
	 */
	public static String getTabPK(Connection con, String tabName) throws Exception {
		String rv ="";
		Statement stmt = null;
		ResultSet rset = null;

		try {
			String sql = "select table_nm, seq_no+1 seq_no," +
					"to_char(sysdate, 'YYYYMMDD') curdate, " +
					"	keycode_nm, len_no, flag_div " +
					"from BCDBA.TBCPNKEYCODE " +
					"where TABLE_NM = '" + tabName + "' ";
			stmt = con.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
			rset = stmt.executeQuery(sql);
			int seqNo = -1;
			String curDate = null;
			String keyCode = null;
			int lenNo = 0;
			String flagDiv = "";
			if (rset.next()) {
				seqNo = rset.getInt("seq_no");
				curDate = rset.getString("curdate");
				keyCode = rset.getString("keycode_nm");
				lenNo = rset.getInt("len_no");
				flagDiv = rset.getString("flag_div").trim();
			}

			if (flagDiv.equals("0")) {
				// 0(�ڵ��+���� �ڸ�������)
				rv = keyCode + (GolfUtil.reSizeLen(Integer.toString(seqNo), "0", lenNo - keyCode.length()));
			} else if (flagDiv.equals("1")) {
				// 1(��¥ + ���� �ڸ�������)
				rv = curDate + (GolfUtil.reSizeLen(Integer.toString(seqNo), "0", lenNo - curDate.length()));
			} else if (flagDiv.equals("2")) {
				// 2(���� �ڸ�������)
				rv = GolfUtil.reSizeLen(Integer.toString(seqNo), "0", lenNo);
			} else {
				// ���̺� ���� �����ü� ����..����..!!!!
				throw new Exception();
			}
			sql = "update BCDBA.TBCPNKEYCODE set " +
					"	seq_no = " + seqNo +
					" where TABLE_NM = '" + tabName + "' ";
			stmt = con.createStatement();
			stmt.executeUpdate(sql);
		} catch(Exception e){
            throw e;
        } finally {
            try { if(rset  != null) rset.close(); } catch( Exception ignored){}
            try { if(stmt != null) stmt.close(); } catch( Exception ignored){}
        }

		return rv;
	}

	/**
	 * ���� ���̺��� �����Ϸù�ȣ��  ����PK���� ���´�
	 * @param con Connection
	 * @param tabName ���̺��
	 * @param seqName �Ϸù�ȣ �÷���
	 * @return SEQ Value
	 * @throws Exception
	 */
	public static long getSeqPK(Connection con, String tabName, String seqName) throws Exception {
		Statement stmt = null;
		ResultSet rset = null;
		long seqNo;

		try {
			String sql = " SELECT MAX(" + seqName + ") AS "+seqName+" FROM "+ tabName;
			stmt = con.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
			rset = stmt.executeQuery(sql);

			if (rset.next()) {
				seqNo = rset.getLong(seqName) + 1;
			}else{
				seqNo = 1;
			}
		} catch(Exception e){
            throw e;
        } finally {
            try { if(rset  != null) rset.close(); } catch( Exception ignored){}
            try { if(stmt != null) stmt.close(); } catch( Exception ignored){}
        }

		return seqNo;
	}


	/**
	 * ���� ���̺��� �����Ϸù�ȣ��  ����PK���� ���´�
	 * @param con Connection
	 * @param tabName ���̺��
	 * @param seqName �Ϸù�ȣ �÷���
	 * @param strWhere ���ǹ�
	 * @return SEQ Value
	 * @throws Exception
	 */
	public static long getSeqWherePK(Connection con, String tabName, String seqName, String strWhere) throws Exception {
		Statement stmt = null;
		ResultSet rset = null;
		long seqNo;

		try {
			String sql = " SELECT MAX(" + seqName + ") AS "+seqName+" FROM "+ tabName+" WHERE "+ strWhere;
			stmt = con.createStatement(
				ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_READ_ONLY);
			rset = stmt.executeQuery(sql);

			if (rset.next()) {
				seqNo = rset.getLong(seqName) + 1;
			}else{
				seqNo = 1;
			}
		} catch(Exception e){
            throw e;
        } finally {
            try { if(rset  != null) rset.close(); } catch( Exception ignored){}
            try { if(stmt != null) stmt.close(); } catch( Exception ignored){}
        }

		return seqNo;
	}


	/**
	 * �־��� ���ڸ� ���̸�ŭ �պκп� �����ڸ� �����Ͽ� �����.
	 * @param ori
	 * @param pre
	 * @param len
	 * @return
	 */
	public static String reSizeLen(String ori, String pre, int len) {
		String rv = "";
		if (ori.length() <= len) {
			int addLen = len - ori.length();
			String strTmp = "";
			for (int i = 0; i < addLen; i++) {
				strTmp += pre;
			}
			rv = strTmp + ori;
		} else {
			rv = "";
		}
		return rv;
	}
	/**
	 * �˻������� ����¡ ó���Ҽ� �ִ� ������ ��ȯ�Ѵ�.<br>
	 * PreparedStatement �󿡼� �������� �ΰ��� �Ķ���� �߰���<br>
	 * ORDER BY �ݵ�� ����ڷ� �ؾ���..!!!<br>
	 * 1 : �������簹�� * ��Ÿ��������<br>
	 * 2 : �������簹��<br>
	 * @param oriSql ��ȯ��ų SQL���� : �Ķ���� ����
	 * @return rownum �յڷ� ���յ� SQL����
	 */
	public static String changeRowNumSql(String oriSql) {
		StringBuffer sql = new StringBuffer();
		String orderBy = "";
		int idxOrder = oriSql.lastIndexOf("ORDER BY");
		if (idxOrder >= 0) {
			orderBy = oriSql.substring(idxOrder);
		}
		sql.append("SELECT * FROM ( \n");
		sql.append(" SELECT * FROM ( \n");
		sql.append("  SELECT * FROM ( \n");
		sql.append("   SELECT * FROM ( \n");
		sql.append(oriSql+"\n");
		sql.append("   ) " + orderBy +"\n");
		sql.append("  ) WHERE ROWNUM <= ? ORDER BY ROWNUM DESC \n");
		sql.append(" ) WHERE ROWNUM <= ? ORDER BY ROWNUM ASC \n");
		sql.append(") ");
		sql.append(orderBy);
		return sql.toString();
	}
	/**
	 * form���� �ǳ����� ��� ������ ȭ�鿡 ����Ѵ�.<br>
	 * ���� paramMapó�� ��� �۾��Ŀ� ��߸���..�ѱ۱���
	 * @param parser
	 */
	public static void showAllParameters(RequestParser parser) {
		Map map = parser.getParameterMap();
		Iterator it =  map.keySet().iterator();
		while (it.hasNext()) {
			String cd = (String) it.next();
			parser.getParameter(cd);
		}

	}
	/**
	 * ResultSet�� �÷���� �����͸� �ϰ������� DbTaoResult�� �����Ѵ�.
	 * @param result DbTaoResult
	 * @param rset ResultSet
	 * @throws Exception
	 */
	public static void toTaoResult(DbTaoResult result, ResultSet rset) throws Exception {
		try {
			ResultSetMetaData rsmd = rset.getMetaData();

			StringBuffer output	= new StringBuffer();
			Reader input = null;
			char[] buffer = null;
			int byteRead = 0;

			do {
				for (int i = 0; i < rsmd.getColumnCount(); i++) {
	
					if(rsmd.getColumnTypeName(i+1).equals("CLOB")){ /* CLOB	Ÿ�� ����Ÿ	Select Start */
						input = rset.getCharacterStream(rsmd.getColumnName(i+1).toUpperCase());
						buffer =	new	char[1024];
						byteRead = 0;
						if (input != null){
							while((byteRead=input.read(buffer,0,1024))!=-1){
								output.append(buffer,0,byteRead);
							}
							input.close();
							result.addString(rsmd.getColumnName(i+1).toUpperCase(),output.toString());
						} else {
							result.addString(rsmd.getColumnName(i+1).toUpperCase(), "");
	 					}
	
					}else{
						result.addString(rsmd.getColumnName(i+1).toUpperCase(), rset.getString(i+1));
					}
	
				}
			} while ( rset.next() );
		} catch (Exception e) {
			
			throw e;
		}
	}
	/**
	 * 8�ڸ���¥(20050101)�� ����(/,-,.)�� ���� ĳ���� �Ѵ�<br>
	 * (ex. getDateFormat("20050101","-") return 2005-01-01
	 */
	public static String getDateFormat (String strDate,String strGubun) {
		try {
			String	value = strDate;
			if( value == null ) return "-1";
			if( value.length() == 4 ) return value;
			if( value.length() == 6 ) return value.substring(0,4) + strGubun + value.substring(4,6);
			if( value.length() == 8 ) return value.substring(0,4) + strGubun + value.substring(4,6) + strGubun + value.substring(6,8);
			return value;
		} catch( Exception err ) {
			return "-1";
		}
	}
	/**
	 * YYYY-MM-DD ������ ���� YYYYMMDD�� ��ȯ
	 * @param strDate
	 * @return
	 */
	public static String toDateFormat (String strDate) {
		String rv = "";
		// 10�ڸ��϶��� �м� �ƴҰ�쿡�� ""
		if (strDate.length() == 10) {
			rv = strDate.substring(0,4) + strDate.substring(5,7) + strDate.substring(8);
		}

		return rv;
	}

	/**
	 * ������ �������� ������ ���̸�ŭ �߶� ��ȯ
	 * @param source �Է¹���
	 * @param len �߶� ���� ����
	 * @return ������ ���̸�ŭ �߶��� ���ڿ�
	 */
	public static String left(String source, int len) {
		String rv = "";
		int s_len = source.length();
		//int s_len = StringUtil.lenBytes(source);
		int i = 0;
		while ((s_len >i) && (len > 0)) {
			rv += source.substring(i,i+1);
			try {
				len -= source.substring(i,i+1).getBytes("EUC_KR").length;
			} catch (UnsupportedEncodingException e) {}

			i++;
		}
		if (s_len > i) {
			rv += "..";
		}
		return rv;
	}

	/**
	 * ���ں�ȯ
	 * @param source �Է¹���
	 * @return String
	 */	
	public static String getUrl(String str){
		
		if(str==null) return "";
//		str = FilterUtil.getXSSFilter(str);
		return str;
	}
	
	
	/**
	 * ���ں�ȯ
	 * @param str String
	 * @param pattern String
	 * @param replace String
	 * @return String
	 */
	public static String rplc(String str, String pattern, String replace) {
		int s = 0;
		int e = 0;
		StringBuffer result = new StringBuffer();

		while ((e = str.toLowerCase().indexOf(pattern, s)) >= 0) {
		    result.append(str.substring(s, e));
		    result.append(replace);
		    s = e + pattern.length();
		}
		result.append(str.substring(s));
		return result.toString();
	}
	
	/**
	 * ���ڿ� �޸� ����ϱ�
	 * @param num
	 * @return
	 */
	public static String comma(String num) {
		String str = num;
		str = "" + str +""; 
        String  retValue = ""; 
        for(int i = 0; i < str.length(); i++) { 
                if (i > 0 && (i%3) == 0) { 
                        retValue = str.charAt(str.length() - i -1) + "," + retValue; 
                 } else { 
                        retValue = str.charAt(str.length() - i -1) + retValue; 
                } 
        } 
		return retValue;
	}
	
	/************************************************
	* ��ȭ�� ��Ʈ�� �Ķ���� ġȯ. 
	 * @param amt	����
	 * @return		��ȯ�� ���ڿ�
	 *********************************************** */
	public static String getPausedAmt(double amt){//��ȭ�� ��Ʈ�� �Ķ���ͷ�
		long longValue = Double.doubleToLongBits(amt);
		return Long.toString(longValue);
	}

	
	/************************************************
	 * ī���ȣ�� ���Ϻ��� 1111-22222-****-1111. 
	 * @param amt	����
	 * @return		��ȯ�� ���ڿ�
	 *********************************************** */
	public static String getFmtCardNo(String cardno) {
        StringBuffer strBuff = new StringBuffer();
        if ( cardno.length() == 16 ) {
            strBuff.append( cardno.substring( 0, 4) ).append("-");
            strBuff.append( cardno.substring( 4, 8) ).append("-");
            strBuff.append( "****" ).append("-");
            strBuff.append( cardno.substring(12) );
        }
        return strBuff.toString();
    }

	/************************************************
	 * ��ū�� üũ 1 
	 * @param amt	����
	 * @return		boolean
	 *********************************************** */
	 public static boolean isValid(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		String sessionToken = null;
		String requestToken = req.getParameter("token");
		if(session != null) {
			sessionToken = (String)session.getAttribute("token");
		}
		if(sessionToken == null || "".equals(sessionToken)) {
			return false;
		}	else {
			session.setAttribute("token","");
			return requestToken.equals(sessionToken);
		}
	 }
	
	/************************************************
	 * ��ū�� üũ 
	 * @param amt	����
	 * @return		boolean
	 *********************************************** */
	 public static boolean isValid(HttpServletRequest req, RequestParser parser) {
		HttpSession session = req.getSession(false);
		String sessionToken = null;
		String requestToken = parser.getParameter("token","");
		if(session != null) {
			sessionToken = (String)session.getAttribute("token");
		}
		if(sessionToken == null || "".equals(sessionToken)) {
			return false;
		}	else {
			session.setAttribute("token","");
			return requestToken.equals(sessionToken);
		}
	 }
	

	/************************************************
	 * ���� �̵�ó��
	 * @param amt	����
	 * @return		boolean
	 *********************************************** */
    public static String getWritableFileName(String path, String fileName) {
        String writableName = fileName;
        String name = null;
        String ext = null;
        File writeFile = new File(path,writableName);
        int i = 0;

        name = fileName.substring(0, fileName.lastIndexOf('.'));
        ext = fileName.substring(fileName.lastIndexOf('.'));

        while(writeFile.exists() == true) {
            writableName = name + "[" + Integer.toString(i) + "]" + ext;
            writeFile = null;
            writeFile = new File(path,writableName);
            i++;
        }
        return writableName;
    }	
	
	/************************************************
	 * �����̵�ó��
	 * @param amt	����
	 * @return		boolean
	 *********************************************** */
	public static String moveFile(String srcDir, String srcName, String targetDir, String targetName) {
		File srcFile = new File(srcDir,srcName);
		if ( srcFile.exists() && srcFile.isFile() ) {
			File tgdFile = new File(targetDir);
			if ( !tgdFile.exists() ) tgdFile.mkdirs();
			File tgnFile = new File(tgdFile,GolfUtil.getWritableFileName(targetDir,targetName));
			if ( srcFile.renameTo(tgnFile) ) {
				return tgnFile.getName();
			}
		}
		return null;
	}
	
	
	/*************************************************************************
	* i��° ���ڿ��� ����
	* @param s ���ڿ�1
	* @param i �ε���
	* @param s1 ���ڿ�2
	* @return String
	*************************************************************************/
    public static String left(String s, int i, String s1)
    {
        try
        {
            byte abyte0[] = s.getBytes(s1);
            int j = abyte0.length;
            if(j <= i)
                return s;
            for(; i > 0; i--)
            {
                String s2 = new String(abyte0, 0, i, s1);
                if(!"".equals(s2))
                    return s2;
            }

            return "";
        }
        catch(UnsupportedEncodingException unsupportedencodingexception)
        {
            return "";
        }
    }
	
	/*************************************************************************
	* ���ڿ� �տ� ���� ���� �߰�
	* @param s ���ڿ�1
	* @param i �ε���
	* @return String
	*************************************************************************/
    public static String lpad(String s, int i)
    {
        return lpad(s, i, " ");
    }
	
	/*************************************************************************
	* ���ڿ� �տ� ���� �߰�
	* @param s ���ڿ�1
	* @param i �ε���
	* @param i �߰��� ���ڿ�
	* @return String
	*************************************************************************/
    public static String lpad(String s, int i, String s1)
    {
        if(s.length() >= i)
            return s;
        StringBuffer stringbuffer = new StringBuffer(i);
        int j = 0;
        for(int k = i - s.length(); j < k; j++)
            stringbuffer.append(s1);

        stringbuffer.append(s);
        return stringbuffer.toString();
    }
	
	/*************************************************************************
	* ���ڿ� ġȯ
	* @param s ���ڿ�1
	* @param s1 ���ڿ�2
	* @param s2 ���ڿ�3
	* @return String
	*************************************************************************/
    public static String replace(String s, String s1, String s2)
    {
        if(s != null)
        {
            for(int i = 0; (i = s.indexOf(s1, i)) >= 0; i += s2.length())
                s = s.substring(0, i) + s2 + s.substring(i + s1.length());

            return s;
        } else
        {
            return "";
        }
    }
	
	/*************************************************************************
	* ���ڿ� ���� ���ڿ� �߰�
	* @param s ���ڿ�
	* @param i �ε���
	* @return String
	*************************************************************************/
    public static String right(String s, int i)
    {
        if(s.length() <= i)
            return s;
        else
            return s.substring(s.length() - i);
    }
	
	/*************************************************************************
	* ���ڿ� ���� ���鹮�� �߰�
	* @param s ���ڿ�
	* @param i �ε���
	* @return String
	*************************************************************************/
    public static String rpad(String s, int i)
    {
        return rpad(s, i, " ");
    }
	
	/*************************************************************************
	* ���ڿ� ���� ���� �߰�
	* @param s ���ڿ�
	* @param i �ε���
	* @param s1 �߰��� ���ڿ�
	* @return String
	*************************************************************************/
    public static String rpad(String s, int i, String s1)
    {
        if(s.length() >= i)
            return s;
        StringBuffer stringbuffer = new StringBuffer(i);
        stringbuffer.append(s);
        int j = 0;
        for(int k = i - s.length(); j < k; j++)
            stringbuffer.append(s1);

        return stringbuffer.toString();
    }
	
	/*************************************************************************
	* ���ڿ� �հ� ���� ���� ����
	* @param s ���ڿ�
	* @return String
	*************************************************************************/
    public static String trim(String s)
    {
        if(s != null)
            return s.trim();
        else
            return "";
    }
	
	/*************************************************************************
	* ���ڿ��� ������� Ȯ��
	* @param s ���ڿ�
	* @return boolean
	*************************************************************************/
    public static boolean empty(String s)
    {
        return s == null || "".equals(s);
    }

	/*************************************************************************
	* Double ���� NaN ���� Ȯ��
	* @param d1 double
	* @return ���ڿ�
	*************************************************************************/
	public static String checkDoubleNaN(double d1) {
		String rtn = "0";
		Double ddd = new Double(d1);
		if (!ddd.isNaN()) {
			rtn = String.valueOf(d1);
		}
		return rtn;
	}

	/*************************************************************************
	* Double ���� NaN ���� Ȯ��
	* @param d1 double
	* @return ���ڿ�
	*************************************************************************/
	public static double checkDoubleNaN_returnDouble(double d1) {
		double rtn = 0;
		Double ddd = new Double(d1);
		if (!ddd.isNaN()) {
			rtn = d1;
		}
		return rtn;
	}	
	
	
	public String nl2br(String source)  {	// Console Input into HTML TAG Parsed format.
		
		if(source == null) return "";
//		this.temporary = FilterUtil.getXSSFilter(source);
		return this.temporary;
		
	}	
	
	
	/** *****************************************************************
	 * replaces original word into target word in applicated string.
	 * @param orig_word		String ��ü.
	 * @param final_word	String ��ü.
	 ***************************************************************** */
	private void replaceStr(String orig_word, String final_word)  {
		int idx;
		for( idx = 0 ; (idx = this.temporary.indexOf(orig_word, idx)) >= 0; idx += final_word.length() )  {
			StringBuffer sBuff = new StringBuffer();
			sBuff.append(this.temporary.substring(0, idx) );
			sBuff.append(final_word);
			sBuff.append(this.temporary.substring(idx + orig_word.length() ) );
			this.temporary = sBuff.toString();
		}
	}
	

	/** *****************************************************************
	 * ��Ʈ���� �ΰ����� Ȯ���Ѵ�.
	 * @param str
	 * @return
	 ***************************************************************** */
	public static boolean isNull(String str) {
		return (str == null || "".equals(str.trim()));
	}
	
	/**
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isNull(Object obj) {
		return (obj == null);
	}

	/*************************************************************************
	* HTML �±� ���� �Լ�
	* @param content String
	* @return ���ڿ�
	*************************************************************************/
	public static String removeTag(String content) {
		Pattern scripts = Pattern.compile("<(no)?script[^>]*>.*?</(no)?script>",Pattern.DOTALL);   
	    Pattern style = Pattern.compile("<style[^>]*>.*</style>",Pattern.DOTALL);   
	    Pattern tags = Pattern.compile("<(\"[^\"]*\"|\'[^\']*\'|[^\'\">])*>");   
	    Pattern nTAGS = Pattern.compile("<\\w+\\s+[^<]*\\s*>");   
	    Pattern entity_refs = Pattern.compile("&[^;]+;");   
	    Pattern whiteSpace = Pattern.compile("\\s\\s+");   
	       
	    Matcher m;   
	       
	    m = scripts.matcher(content);   
	    content = m.replaceAll("");   
	    m = style.matcher(content);   
	    content = m.replaceAll("");   
	    m = tags.matcher(content);   
	    content = m.replaceAll("");   
	    m = entity_refs.matcher(content);   
	    content = m.replaceAll("");   
	    m = whiteSpace.matcher(content);   
	    content = m.replaceAll(" ");           
	   
	    return content;   
    }

	
    /**
     * getAnsiCode
     * @param src
     * @return
     */
	public static String getAnsiCode(String src){
		
		if(src==null) return "";
//		src = FilterUtil.getXSSFilter(src);
		return src;
		
	}	
	
	/**
	 * �ѱ� �ڸ���
	 * @param str2
	 * @param len2
	 * @param tail
	 * @return
	 */
	public static String getCutKSCString(String str2, int len2,String tail){ 
		String str = ""; 
		try{ 
			str =  new String (str2.getBytes("KSC5601"), "EUC-KR"); 
		}catch(UnsupportedEncodingException uEE){ 
			System.out.println(uEE.toString()); 
			str = str2; 
		} 

		int len = str.length();
		int cnt=0, index=0;   // ���� ��������, �ε���, �߶��� ����

		while (index < len && cnt < len2) {
			if (str.charAt(index++) < 256) // 1����Ʈ ���ڶ��...
				cnt++;     // ���� 1 ����
			else { // 2����Ʈ ���ڶ��...
				if(cnt < len2-3) 
					cnt += 2;  // ���� 2 ����
				else
					break;
			}
		}
		if (index < len){
			str = str.substring(0, index);
			str = str + tail;
		}

		try{ 
			str =  new String(str.getBytes("EUC-KR"),"KSC5601"); 
		}catch(UnsupportedEncodingException uEE){ 
			System.out.println(uEE.toString()); 
			str = str2; 
		} 

		return str;	
	} 
	
}
