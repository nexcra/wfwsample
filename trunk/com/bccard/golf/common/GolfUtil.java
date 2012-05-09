/** ****************************************************************************
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 작성 : 2006.11.08 [박성우(ultteky@e4net.net)]
 * 내용 : WELCO 공통유틸.
 ************************** 수정이력 *******************************************
 *    일자      버전   작성자   변경사항
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
* Welco 공통유틸.
* @author JYJUNG
* @version 2006.11.08
******************************************************************************/
public class GolfUtil {

	private String temporary;
	
	 /**
	 * XSS 필러링 2009.09.15
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
	 * form 파라메터중 select의 코드;코드명의 분리자
	 * @param src 분리할문자열
	 * @param wh 분리된 문자의 순번 0:코드, 1:코드명
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
	 * 지정 테이블의 다음PK값을 따온다<br>
	 * 가져온후에 키값 갱신함
	 * @param con Connection
	 * @param tabName 테이블명
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
				// 0(코드명+순번 자리수길이)
				rv = keyCode + (GolfUtil.reSizeLen(Integer.toString(seqNo), "0", lenNo - keyCode.length()));
			} else if (flagDiv.equals("1")) {
				// 1(날짜 + 순번 자리수길이)
				rv = curDate + (GolfUtil.reSizeLen(Integer.toString(seqNo), "0", lenNo - curDate.length()));
			} else if (flagDiv.equals("2")) {
				// 2(순번 자리수길이)
				rv = GolfUtil.reSizeLen(Integer.toString(seqNo), "0", lenNo);
			} else {
				// 테이블 순번 가져올수 없음..에러..!!!!
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
	 * 지정 테이블의 지정일련번호의  다음PK값을 따온다
	 * @param con Connection
	 * @param tabName 테이블명
	 * @param seqName 일련번호 컬럼명
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
	 * 지정 테이블의 지정일련번호의  다음PK값을 따온다
	 * @param con Connection
	 * @param tabName 테이블명
	 * @param seqName 일련번호 컬럼명
	 * @param strWhere 조건문
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
	 * 주어진 문자를 길이만큼 앞부분에 지정자를 삽입하여 맞춘다.
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
	 * 검색쿼리를 페이징 처리할수 있는 쿼리로 변환한다.<br>
	 * PreparedStatement 상에서 마지막에 두개의 파라메터 추가됨<br>
	 * ORDER BY 반드시 대분자로 해야함..!!!<br>
	 * 1 : 페이지당갯수 * 나타날페이지<br>
	 * 2 : 페이지당갯수<br>
	 * @param oriSql 변환시킬 SQL문장 : 파라메터 포함
	 * @return rownum 앞뒤로 결합된 SQL문장
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
	 * form에서 건내받은 모든 값들을 화면에 출력한다.<br>
	 * 절대 paramMap처럼 모든 작업후에 써야만함..한글깨짐
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
	 * ResultSet의 컬럼명과 데이터를 일괄적으로 DbTaoResult에 세팅한다.
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
	
					if(rsmd.getColumnTypeName(i+1).equals("CLOB")){ /* CLOB	타입 데이타	Select Start */
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
	 * 8자리날짜(20050101)를 구분(/,-,.)에 맞춰 캐스팅 한다<br>
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
	 * YYYY-MM-DD 형식의 값을 YYYYMMDD로 변환
	 * @param strDate
	 * @return
	 */
	public static String toDateFormat (String strDate) {
		String rv = "";
		// 10자리일때만 분석 아닐경우에는 ""
		if (strDate.length() == 10) {
			rv = strDate.substring(0,4) + strDate.substring(5,7) + strDate.substring(8);
		}

		return rv;
	}

	/**
	 * 문자의 좌측에서 지정한 길이만큼 잘라서 반환
	 * @param source 입력문자
	 * @param len 잘라서 보일 길이
	 * @return 지정한 길이만큼 잘라진 문자열
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
	 * 문자변환
	 * @param source 입력문자
	 * @return String
	 */	
	public static String getUrl(String str){
		
		if(str==null) return "";
//		str = FilterUtil.getXSSFilter(str);
		return str;
	}
	
	
	/**
	 * 문자변환
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
	 * 숫자에 콤마 출력하기
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
	* 원화를 스트링 파라미터 치환. 
	 * @param amt	원본
	 * @return		변환된 문자열
	 *********************************************** */
	public static String getPausedAmt(double amt){//원화를 스트링 파라미터로
		long longValue = Double.doubleToLongBits(amt);
		return Long.toString(longValue);
	}

	
	/************************************************
	 * 카드번호를 페턴변경 1111-22222-****-1111. 
	 * @param amt	원본
	 * @return		변환된 문자열
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
	 * 토큰값 체크 1 
	 * @param amt	원본
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
	 * 토큰값 체크 
	 * @param amt	원본
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
	 * 파일 이동처리
	 * @param amt	원본
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
	 * 파일이동처리
	 * @param amt	원본
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
	* i번째 문자열을 리턴
	* @param s 문자열1
	* @param i 인덱스
	* @param s1 문자열2
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
	* 문자열 앞에 공백 문자 추가
	* @param s 문자열1
	* @param i 인덱스
	* @return String
	*************************************************************************/
    public static String lpad(String s, int i)
    {
        return lpad(s, i, " ");
    }
	
	/*************************************************************************
	* 문자열 앞에 문자 추가
	* @param s 문자열1
	* @param i 인덱스
	* @param i 추가될 문자열
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
	* 문자열 치환
	* @param s 문자열1
	* @param s1 문자열2
	* @param s2 문자열3
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
	* 문자열 끝에 문자열 추가
	* @param s 문자열
	* @param i 인덱스
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
	* 문자열 끝에 공백문자 추가
	* @param s 문자열
	* @param i 인덱스
	* @return String
	*************************************************************************/
    public static String rpad(String s, int i)
    {
        return rpad(s, i, " ");
    }
	
	/*************************************************************************
	* 문자열 끝에 문자 추가
	* @param s 문자열
	* @param i 인덱스
	* @param s1 추가될 문자열
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
	* 문자열 앞과 끝에 공백 제거
	* @param s 문자열
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
	* 문자열이 비었는지 확인
	* @param s 문자열
	* @return boolean
	*************************************************************************/
    public static boolean empty(String s)
    {
        return s == null || "".equals(s);
    }

	/*************************************************************************
	* Double 값이 NaN 인지 확인
	* @param d1 double
	* @return 문자열
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
	* Double 값이 NaN 인지 확인
	* @param d1 double
	* @return 문자열
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
	 * @param orig_word		String 객체.
	 * @param final_word	String 객체.
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
	 * 스트링이 널값인지 확인한다.
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
	* HTML 태그 제거 함수
	* @param content String
	* @return 문자열
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
	 * 한글 자르기
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
		int cnt=0, index=0;   // 각각 길이증가, 인덱스, 잘라줄 길이

		while (index < len && cnt < len2) {
			if (str.charAt(index++) < 256) // 1바이트 문자라면...
				cnt++;     // 길이 1 증가
			else { // 2바이트 문자라면...
				if(cnt < len2-3) 
					cnt += 2;  // 길이 2 증가
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
