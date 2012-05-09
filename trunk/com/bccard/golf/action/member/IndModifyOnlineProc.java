/************************** 수정이력 **************************************************************
*   클래스명	: IndModifyOnlineProc
*   작성자		: 현광준
*   내용		: 인터넷회원정보수정 처리부분
*   적용범위	: bccard전체
*   작성일자	: 2004.02.03
************************** 수정이력 ***************************************************************
* 2006.05.10	khko			getEmailCnt 메소드 추가
* 2006.08.09					본사-DR소스싱크일치화
* 2008.09.11					메일수신(getMemberInfoUpdate) 회원사별 메일수신으로 수정
*==================================================================================================
* 수정시작일	적용예정일	수정완료일	적용완료일	작성자	변경사항
* 2008.10.28	2008.10.29	2008.11.17	2008.11.18	조용국	회원은행 이메일 뉴스레터 수신부 변경
* 2008.11.18	2008.11.18	2008.11.18	2008.11.24	조용국	회원은행 이메일 뉴스레터 수신부 변경
* 2008.11.24	2008.11.25	2008.11.24	2008.11.24	hklee	모바일 서비스 한글문제
* 2008.12.09	2008.12.10	2008.12.09	2008.12.11	조용국	부가정보 결혼 여부,기념일 추가
* 2008.12.11	2008.12.11	2008.12.11	2008.12.11	조용국	프라운지 회원인 경우 팝업 안띄우도록 조회
* 2009.02.04	2009.02.06	2009.02.04	2009.02.12  hjsung	프라운지 테이블 변경
* 2009.02.18	2009.02.19	2009.02.19	2009.02.19  hklee	이메일 명세서 수신 변경 ( 00 => 99)
************************** 수정이력 ***************************************************************/
package com.bccard.golf.action.member;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.SSOdbprotectorWrap;
import com.bccard.golf.common.login.BcUserEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.RequestParser;
import com.bccard.waf.core.WaContext;
import com.initech.dbprotector.CipherClient;
import com.initech.util.Base64Util;

/**
 * 인터넷회원정보수정 처리부분
 * @version 2004 02 03
 * @author  현광준
**/
public class IndModifyOnlineProc extends AbstractProc {

	/**
	* 인터넷회원정보수정
	* @param 	context		WaContext 객체.
	* @param 	String		String 객체.
	* @return 	Hashtable	정회원 회원정보.
	**/
	public Hashtable getInfoChange(WaContext context,String account1,String memClss){

		boolean retTF = false;
		Connection conn 			= 	null;
		PreparedStatement ps 		= 	null;
		ResultSet rs = 	null;
		String sql_card =	null;
		String sql_card5 =	null;	// 법인
		Hashtable hash =	null;

		sql_card  = "\n select	ACCOUNT, MEMID, SITE_CLSS, H_PASSWD, PASSWDQ, PASSWDA,	";
		sql_card += "\n	  DECODE(MEMBER_CLSS,'1','개인회원','4','개인회원','5','기업회원') AS MEMBER_CLSS, SOCID,	";
		sql_card += "\n	  substr(SOCID,1,6) as SOCID_1 ,substr(SOCID,7,1) || '******' as SOCID_2,	";
		sql_card += "\n	  MOBILE, PHONE, NAME, EMAIL1, MAILING,";
		sql_card += "\n	  NVL(JOB,'99') AS JOB,NVL(JOBTYPE,'99') AS JOBTYPE, NVL(SEX,'1') AS SEX ,NVL(SOLAR,'1') AS SOLAR,	";
		sql_card += "\n	  SUBSTR(BIRTH,1,4) as BIRTH_1 ,SUBSTR(BIRTH,5,2) as BIRTH_2, SUBSTR(BIRTH,7,2) as BIRTH_3,	";
		sql_card += "\n   NVL(WEDYN,'0') AS WEDYN , substr(WEDANNIV,1,4) as WEDANNIV_1,substr(WEDANNIV,5,2) as WEDANNIV_2,	";
		sql_card += "\n	  substr(WEDANNIV,7,2) as WEDANNIV_3, REGDATE , ENAME, ";
		sql_card += "\n	  RECOMM_ACCOUNT, RECV_YN, NVL(ZIPCODE, '') as ZIPCODE , NVL(ZIPADDR, '') as ZIPADDR, NVL(DETAILADDR, '') as DETAILADDR,  ";
		sql_card += "\n	  NW_OLD_ADDR_CLSS, DONG_OVR_NEW_ADDR, DONG_BLW_NEW_ADDR  ";
		sql_card += "\n from bcdba.ucusrinfo	";
		sql_card += "\n where account = ?	";
		//sql_card += "\n where account = ?  and member_clss = '1' and site_clss IN ( '0', '1' )";

		sql_card5  = "\n select	T2.ACCOUNT, T2.MEMID, T2.SITE_CLSS, T2.H_PASSWD, T2.PASSWDQ, T2.PASSWDA,	";
		sql_card5 += "\n	  DECODE(T2.MEMBER_CLSS,'1','개인회원','2','가맹점회원','3','기업회원') AS MEMBER_CLSS, B2.USER_JUMIN_NO as SOCID,	";
		sql_card5 += "\n	  substr(B2.USER_JUMIN_NO,1,6) as SOCID_1 ,substr(B2.USER_JUMIN_NO,7,1) || '******' as SOCID_2,	";
		sql_card5 += "\n	  NVL(B2.USER_MOB_NO, '') as MOBILE, B2.USER_TEL_NO as PHONE, B2.USER_NM as NAME, B2.USER_EMAIL as EMAIL1, T2.MAILING,";
		sql_card5 += "\n	  NVL(T2.JOB,'99') AS JOB,NVL(T2.JOBTYPE,'99') AS JOBTYPE, NVL(T2.SEX,'1') AS SEX ,NVL(T2.SOLAR,'1') AS SOLAR,	";
		sql_card5 += "\n	  CASE NVL(T2.BIRTH, '-') WHEN '-' THEN (CASE SUBSTR(B2.USER_JUMIN_NO,7,1) WHEN '1' THEN '19' WHEN'2' THEN '19' ELSE '20' END)||SUBSTR(B2.USER_JUMIN_NO,1,2) ELSE SUBSTR(T2.BIRTH, 1, 4) END AS BIRTH_1, ";
		sql_card5 += "\n	  CASE NVL(T2.BIRTH, '-') WHEN '-' THEN SUBSTR(B2.USER_JUMIN_NO,3,2) ELSE SUBSTR(T2.BIRTH, 5, 2) END AS BIRTH_2, ";
		sql_card5 += "\n	  CASE NVL(T2.BIRTH, '-') WHEN '-' THEN SUBSTR(B2.USER_JUMIN_NO,5,2) ELSE SUBSTR(T2.BIRTH, 7, 2) END AS BIRTH_3, ";
		sql_card5 += "\n   NVL(T2.WEDYN,'0') AS WEDYN , substr(T2.WEDANNIV,1,4) as WEDANNIV_1,substr(T2.WEDANNIV,5,2) as WEDANNIV_2,	";
		sql_card5 += "\n	  substr(T2.WEDANNIV,7,2) as WEDANNIV_3, T2.REGDATE , T2.ENAME, ";
		sql_card5 += "\n	  T2.RECOMM_ACCOUNT, T2.RECV_YN, NVL(T2.ZIPCODE, '') as ZIPCODE , NVL(T2.ZIPADDR, '') as ZIPADDR, NVL(T2.DETAILADDR, '') as DETAILADDR,  ";
		sql_card5 += "\n	  T2.NW_OLD_ADDR_CLSS, T2.DONG_OVR_NEW_ADDR, T2.DONG_BLW_NEW_ADDR  ";
		sql_card5 += "\n from bcdba.ucusrinfo T2	";
		sql_card5 += "\n JOIN BCDBA.TBENTPUSER B2 ON T2.ACCOUNT=B2.ACCOUNT	";
		sql_card5 += "\n JOIN BCDBA.TBENTPMEM  B3 ON B3.mem_id=B2.mem_id	";
		sql_card5 += "\n where T2.account = ?  and T2.member_clss = '5'";
		sql_card5 += "\n and B3.mem_stat='2' and B3.mem_CLSS='6' and B3.sec_Date is null";
		
		//sql_card5  = "\n select	ACCOUNT, MEM_ID as MEMID, H_PASSWD,	";
		//sql_card5 += "\n	  '기업회원' AS MEMBER_CLSS, USER_JUMIN_NO as SOCID,	";
		//sql_card5 += "\n	  substr(USER_JUMIN_NO,1,6) as SOCID_1 ,substr(USER_JUMIN_NO,7,1) || '******' as SOCID_2,	";
		//sql_card5 += "\n	  USER_MOB_NO as MOBILE, USER_NM as NAME, USER_EMAIL as EMAIL1,";
		//sql_card5 += "\n	  '' AS JOB, '' AS JOBTYPE, substr(USER_JUMIN_NO,1,1) AS SEX	";
		//sql_card5 += "\n	  , USER_TEL_NO as PHONE	";
		//sql_card5 += "\n from BCDBA.TBENTPUSER	";
		//sql_card5 += "\n where account = ? ";
		try{

			conn = context.getDbConnection("default", null);	// 새로 추가부분

			if("5".equals(memClss)) {	// 법인
				ps = conn.prepareStatement( sql_card5 );
			} else {
				ps = conn.prepareStatement( sql_card );
			}

			ps.setString(1,account1);
			rs = ps.executeQuery();
			hash = new Hashtable();

			if(rs.next()){

				String account			= rs.getString("ACCOUNT");
				if(account == null){ account="";}

				String site_clss	= rs.getString("SITE_CLSS");
				if(site_clss == null){ site_clss="";}

				String memid		= Integer.toString( rs.getInt("MEMID") );
				byte[] encpass = rs.getBytes("H_PASSWD");
				String passwd  = new String(Base64Util.encode(encpass));

				String passwdq			= StrUtil.isNull(rs.getString("passwdq"), "");
				String passwda			= StrUtil.isNull(rs.getString("passwda"), "");;
				String member_clss		= StrUtil.isNull(rs.getString("member_clss"), "");
				String socid			= StrUtil.isNull(rs.getString("socid"), "");
				String socid_1			= StrUtil.isNull(rs.getString("socid_1"), "");
				String socid_2			= StrUtil.isNull(rs.getString("socid_2"), "");
				String name				= StrUtil.isNull(rs.getString("name"), "");
				String email			= StrUtil.isNull(rs.getString("email1"), "");
				String mailing			= StrUtil.isNull(rs.getString("mailing"), "");
				String job				= StrUtil.isNull(rs.getString("job"), "");
				String jobtype			= StrUtil.isNull(rs.getString("jobtype"), "");
				String sex				= StrUtil.isNull(rs.getString("sex"), "");
				String solar			= StrUtil.isNull(rs.getString("solar"), "");
				String birth_1			= StrUtil.isNull(rs.getString("birth_1"), "");
				String birth_2			= StrUtil.isNull(rs.getString("birth_2"), "");
				String birth_3			= StrUtil.isNull(rs.getString("birth_3"), "");
				String wedyn			= StrUtil.isNull(rs.getString("wedyn"), "");
				String wedanniv_1		= StrUtil.isNull(rs.getString("wedanniv_1"), "");
				String wedanniv_2		= StrUtil.isNull(rs.getString("wedanniv_2"), "");
				String wedanniv_3		= StrUtil.isNull(rs.getString("wedanniv_3"), "");
				String regdate			= StrUtil.isNull(rs.getString("regdate"), "");
				String ename			= StrUtil.isNull(rs.getString("ename"), "");
				String recomm_account	= StrUtil.isNull(rs.getString("recomm_account"), "");
				String recv_yn			= StrUtil.isNull(rs.getString("recv_yn"), "");
/*
				String card_recv_yn		= StrUtil.isNull(rs.getString("card_recv_yn"), "");
				String tour_recv_yn		= StrUtil.isNull(rs.getString("tour_recv_yn"), "");
				String shopping_recv_yn	= StrUtil.isNull(rs.getString("shopping_recv_yn"), "");
				String ctnt_recv_yn		= StrUtil.isNull(rs.getString("ctnt_recv_yn"), "");
*/
				
				String addrClss = rs.getString("NW_OLD_ADDR_CLSS");
				
				if ( addrClss == null || addrClss.trim().equals("")){
					addrClss = "1";
				}

				hash.put("MOBILE",StrUtil.isNull(rs.getString("MOBILE"), ""));
				hash.put("PHONE",StrUtil.isNull(rs.getString("PHONE"), ""));
				hash.put("ZIPCODE",StrUtil.isNull(rs.getString("ZIPCODE"), ""));

				if ( !addrClss.equals("2") ){  
					hash.put("ZIPADDR",StrUtil.isNull(rs.getString("ZIPADDR"), ""));
					hash.put("DETAILADDR",StrUtil.isNull(rs.getString("DETAILADDR"), ""));					
				}else { 
					hash.put("ZIPADDR",StrUtil.isNull(rs.getString("DONG_OVR_NEW_ADDR"), ""));
					hash.put("DETAILADDR",StrUtil.isNull(rs.getString("DONG_BLW_NEW_ADDR"), ""));
				}
				
				hash.put("ADDR_CLSS", addrClss);

				//hash.put("ADDR1",StrUtil.isNull(rs.getString("ADDR1"), ""));
				//hash.put("ADDR2",StrUtil.isNull(rs.getString("ADDR2"), ""));

				hash.put("SOCID",socid);
				hash.put("MEMBER_CLSS",member_clss);
				hash.put("NAME",name);
				hash.put("SOCID_1",socid_1);
				hash.put("SOCID_2",socid_2);
				hash.put("SEX",sex);
				hash.put("ACCOUNT",account);
				hash.put("PASSWD",passwd);
				hash.put("PASSWDQ",passwdq);
				hash.put("PASSWDA",passwda);
				hash.put("ENAME",ename);
     			hash.put("BIRTH_1",birth_1);
				hash.put("BIRTH_2",birth_2);
				hash.put("BIRTH_3",birth_3);
				hash.put("SOLAR",solar);
				hash.put("JOB",job);
				hash.put("JOBTYPE",jobtype);
				hash.put("WEDYN",wedyn);
				hash.put("WEDANNIV_1",wedanniv_1);
				hash.put("WEDANNIV_2",wedanniv_2);
				hash.put("WEDANNIV_3",wedanniv_3);
				hash.put("RECOMM_ACCOUNT",recomm_account);
				hash.put("MAILING",mailing);
				hash.put("RECV_YN",recv_yn);
/*
				hash.put("CARD_RECV_YN",card_recv_yn);
				hash.put("TOUR_RECV_YN",tour_recv_yn);
				hash.put("SHOPPING_RECV_YN",shopping_recv_yn);
				hash.put("CTNT_RECV_YN",ctnt_recv_yn);
*/

				//-------- E-mail 분리   ----------->
				String address = "";
				String id = "";

				if (!email.equals("")){
					int index = email.indexOf('@');
					if (index != -1) {
						id = email.substring(0, index);
						address = email.substring(index+1);
					}
				}
				hash.put("ADDRESS", address);
				hash.put("EMAIL", id);
				hash.put("EMAIL_ID",id);
				hash.put("EMAIL_URL",address );

			}
			
		}catch(Exception se){
			debug(se.toString());
		}finally{
			if(rs != null) { try{ rs.close(); }catch(Throwable ex){}  } else {}
			if(ps != null) { try{ ps.close(); }catch(Throwable ex){}  } else {}
			if(conn != null) { try{conn.close();}catch(Throwable ex){}  } else {}
		}
		return hash;
	}



	/**
	* 인터넷회원정보수정
	* @param 	context		WaContext 객체.
	* @param 	String		String 객체.
	* @return 	Hashtable	준회원 회원정보.
	**/// 새주소 작업하면서 확인, 쓰이는 곳 없음
	protected Hashtable getNoCardChange(WaContext context, String account1){

		boolean retTF = false;
		Connection conn 	= 	null;
		PreparedStatement ps = null;
		ResultSet rs			= 	null;
		String sql_card		=	null;
		Hashtable hash	=	null;

		sql_card  = "\n	select	u.ACCOUNT, u.MEMID, u.SITE_CLSS, u.H_PASSWD, u.PASSWDQ, u.PASSWDA,	";
		sql_card += "\n		DECODE(u.MEMBER_CLSS,'1','개인회원','2','가맹점회원','3','기업회원','4','미소지카드회원') AS MEMBER_CLSS, SOCID,	";
		sql_card += "\n		substr(u.SOCID,1,6) as SOCID_1 ,substr(u.SOCID,7,1) || '******' as SOCID_2,	";
		sql_card += "\n		u.MOBILE, u.NAME, u.EMAIL1, u.MAILING, ";
		sql_card += "\n		NVL(u.JOB,'99') AS JOB,NVL(u.JOBTYPE,'99') AS JOBTYPE, NVL(u.SEX,'1') AS SEX ,NVL(u.SOLAR,'1') AS SOLAR,	";
		sql_card += "\n		SUBSTR(u.BIRTH,1,4) as BIRTH_1 ,SUBSTR(u.BIRTH,5,2) as BIRTH_2, SUBSTR(u.BIRTH,7,2) as BIRTH_3,	";
		sql_card += "\n		NVL(u.WEDYN,'0') AS WEDYN , substr(u.WEDANNIV,1,4) as WEDANNIV_1,substr(u.WEDANNIV,5,2) as WEDANNIV_2,	";
		sql_card += "\n		substr(u.WEDANNIV,7,2) as WEDANNIV_3, u.REGDATE , u.ENAME, ";
		sql_card += "\n		u.RECOMM_ACCOUNT, u.RECV_YN, u.ZIPCODE, u.ZIPADDR, u.DETAILADDR, u.MOBILE, u.PHONE,  	";
		sql_card += "\n	  m.LAST_CHNG_DATE, m.CARD_RECV_YN, m.CARD_CORR_DATE, m.TOUR_RECV_YN, m.TOUR_CORR_DATE, ";
		sql_card += "\n	  m.SHOPPING_RECV_YN, m.SHOPPING_CORR_DATE, m.CTNT_RECV_YN, m.CTNT_CORR_DATE ";
		sql_card += "\n	from bcdba.ucusrinfo u, bcdba.tbmailrecvyn m	";
		sql_card += "\n	where account = ?  and member_clss = '4' and site_clss = '2' ";
		sql_card += "\n and u.memid  = m.memid(+)	";

		try{

			conn = context.getDbConnection("default", null);	// 새로 추가부분

			ps = conn.prepareStatement( sql_card );
			ps.setString(1, account1);
			rs = ps.executeQuery();
			hash = new Hashtable();

			while (rs.next()) {

				String account		= StrUtil.isNull(rs.getString("ACCOUNT"), "");
				String site_clss	= StrUtil.isNull(rs.getString("SITE_CLSS"), "");
				String memid		= Integer.toString( rs.getInt("MEMID") );
				byte[] encpass		= rs.getBytes("H_PASSWD");
				String passwd		= new String(Base64Util.encode(encpass));
				String passwdq		= StrUtil.isNull(rs.getString("passwdq"), "");
				String passwda		= StrUtil.isNull(rs.getString("passwda"), "");
				String member_clss	= StrUtil.isNull(rs.getString("member_clss"), "");
				String socid		= StrUtil.isNull(rs.getString("socid"), "");
				String socid_1		= StrUtil.isNull(rs.getString("socid_1"), "");
				String socid_2		= StrUtil.isNull(rs.getString("socid_2"), "");
				String name			= StrUtil.isNull(rs.getString("name"), "");
				String email		= StrUtil.isNull(rs.getString("email1"), "");
				String mailing		= StrUtil.isNull(rs.getString("mailing"), "");
				String job			= StrUtil.isNull(rs.getString("job"), "");
				String jobtype		= StrUtil.isNull(rs.getString("jobtype"), "");
				String sex			= StrUtil.isNull(rs.getString("sex"), "");
				String solar		= StrUtil.isNull(rs.getString("solar"), "");
				String birth_1		= StrUtil.isNull(rs.getString("birth_1"), "");
				String birth_2		= StrUtil.isNull(rs.getString("birth_2"), "");
				String birth_3		= StrUtil.isNull(rs.getString("birth_3"), "");
				String wedyn		= StrUtil.isNull(rs.getString("wedyn"), "");
				String wedanniv_1	= StrUtil.isNull(rs.getString("wedanniv_1"), "");
				String wedanniv_2	= StrUtil.isNull(rs.getString("wedanniv_2"), "");
				String wedanniv_3	= StrUtil.isNull(rs.getString("wedanniv_3"), "");
				String regdate		= StrUtil.isNull(rs.getString("regdate"), "");
				String ename		= StrUtil.isNull(rs.getString("ename"), "");
				String recomm_account	= StrUtil.isNull(rs.getString("recomm_account"), "");
				String recv_yn		= StrUtil.isNull(rs.getString("RECV_YN"), "");
				String zipcode		= StrUtil.isNull(rs.getString("ZIPCODE"), "");
				String zipaddr		= StrUtil.isNull(rs.getString("ZIPADDR"), "");
				String detailaddr	= StrUtil.isNull(rs.getString("DETAILADDR"), "");
				String mobile		= StrUtil.isNull(rs.getString("MOBILE"), "");
				String phone		= StrUtil.isNull(rs.getString("PHONE"), "");
				String card_recv_yn	 = StrUtil.isNull(rs.getString("CARD_RECV_YN"), "");
				String ctnt_recv_yn	= StrUtil.isNull(rs.getString("CTNT_RECV_YN"), "");
				String tour_recv_yn	= StrUtil.isNull(rs.getString("TOUR_RECV_YN"), "");
				String shopping_recv_yn = StrUtil.isNull(rs.getString("SHOPPING_RECV_YN"), "");

				//-------- E-mail 분리   ----------->
				String address = "";
				String id = "";

				if ( !"".equals(email) ) {
					int index = email.indexOf('@');
					if (index != -1) {
						id = email.substring(0,index);
						address = email.substring(index+1);
					}
				}

				hash.put("ADDRESS",			address);
				hash.put("EMAIL",			email);
				hash.put("EMAIL_ID",		id);
				hash.put("EMAIL_URL",		address );
				hash.put("MEMBER_CLSS",		member_clss);
				hash.put("NAME",			name);
				hash.put("SOCID_1",			socid_1);
				hash.put("SOCID_2",			socid_2);
				hash.put("SEX",				sex);
				hash.put("ACCOUNT",			account);
				hash.put("PASSWD",			passwd);
				hash.put("PASSWDQ",			passwdq);
				hash.put("PASSWDA",			passwda);
				hash.put("ENAME",			ename);
     			hash.put("BIRTH_1",			birth_1);
				hash.put("BIRTH_2",			birth_2);
				hash.put("BIRTH_3",			birth_3);
				hash.put("SOLAR",			solar);
				hash.put("WEDYN",			wedyn);
				hash.put("WEDANNIV_1",		wedanniv_1);
				hash.put("WEDANNIV_2",		wedanniv_2);
				hash.put("WEDANNIV_3",		wedanniv_3);
				hash.put("RECOMM_ACCOUNT",	recomm_account);
				hash.put("MAILING",			mailing);
				hash.put("RECV_YN",			recv_yn);
				hash.put("ZIPCODE",			zipcode);
				hash.put("ZIPADDR",			zipaddr);
				hash.put("DETAILADDR",		detailaddr);
				hash.put("MOBILE",			mobile);
				hash.put("PHONE",			phone);
				hash.put("CARD_RECV_YN",	card_recv_yn);
				hash.put("CTNT_RECV_YN",    ctnt_recv_yn);
				hash.put("TOUR_RECV_YN",    tour_recv_yn);
				hash.put("SHOPPING_RECV_YN",shopping_recv_yn);

			}

		} catch(Exception e) {
			///e.printStackTrace();
			System.out.println(e.getMessage());
debug("Exception : " + e.getMessage());

		} finally {
			if(rs != null) { try{ rs.close(); }catch(Throwable ex){} } else {}
			if(ps != null) { try{ ps.close(); }catch(Throwable ex){} } else {}
			if(conn != null) { try{conn.close();}catch(Throwable ex){} } else {}
		}

		return hash;
	}

	/**
	* 인터넷회원정보수정 팝업 이벤트 20091022 진현구
	* @param 	context		WaContext 객체.
	* @param 	parser		RequestParser 객체.
	* @param 	session		HttpSession 객체.
	* @return 	boolean		정보 수정 UPDATE 처리 결과. 
	**/
	protected boolean getMemberInfoUpdate2 (	WaContext context, RequestParser parser,
												HttpSession session, HttpServletRequest request, String cardUser) {
		
		UcusrinfoEntity bcuser = SessionUtil.getFrontUserInfo(request);
		
		String account1 = bcuser.getAccount();
		String socialid = bcuser.getSocid();

		Connection conn = null;		
		PreparedStatement ps = null;
		PreparedStatement ps_person = null;
		String sql = null;
		boolean isSuccess	= false;

		String appip = "";
		String devip = "";
		
		String addr_clss = parser.getParameter("addrClss", "");
		
		try {
			appip = InetAddress.getLocalHost().getHostAddress();
			devip = AppConfig.getAppProperty("DEV_APP_IP");
		} catch(Throwable t) {
		}
		if (appip == null) appip = "unknown";
		if ( devip == null ) devip = "";

		//parser.getParameter("addr_clss", ""); 

		// UPDATE문 만드는것 테스트
		sql =  "\n UPDATE BCDBA.UCUSRINFO ";
		sql += "\n	  SET	ZIPCODE = ?,";
		
		if ( !addr_clss.equals("2") ){//구주소
			sql += "\n			ZIPADDR = ?, DETAILADDR = ?, ";
		}else { //새주소
			sql += "\n			DONG_OVR_NEW_ADDR = ?, DONG_BLW_NEW_ADDR = ?, ";
		}
		
		sql += "\n			PHONE=?, MOBILE=?, EMAIL1 = ?, JOB =?, JOBTYPE =?, BIRTH=?, SOLAR=?, NW_OLD_ADDR_CLSS=? ";
		sql += "\n  WHERE account = ?  ";
		
		try{
			String email_id = parser.getParameter("email_id","");
		    String bcline = parser.getParameter("select1","");

			boolean mailChange = (parser.getParameter("email_id")+"@"+parser.getParameter("select")).equals(parser.getParameter("EMAIL")) ? false : true;

			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);

			
			// WafService (모바일을 통한 입력 : 한글문제 )
			String detailAddr = parser.getParameter("detailaddr", "");
			String zipaddr = parser.getParameter("zipaddr", "");
			if ( request instanceof com.bccard.waf.action.ServiceRequest ) {
				detailAddr = new String(detailAddr.getBytes("ISO-8859-1"), "UTF-8");
				zipaddr = new String(zipaddr.getBytes("ISO-8859-1"), "UTF-8");
			}
			
			String mobile1 = parser.getParameter("mobile_1","");
			int i = 1;		

			//골프라운지에서 법인은 회사 주소 변경이 아닌 자택 주소 변경이므로 기존에 아래 if 문  제거
			//if(!cardUser.equals("5")) {
			
				ps = conn.prepareStatement(sql);				
				ps.setString(i++,parser.getParameter("zipcode1","")+"-"+parser.getParameter("zipcode2",""));
				ps.setString(i++, zipaddr);
				ps.setString(i++, detailAddr);
				ps.setString(i++,parser.getParameter("phone_0","")+"-"+parser.getParameter("phone_1","")+"-"+parser.getParameter("phone_2",""));
	
				if (mobile1 == null || mobile1.equals("")) {
					ps.setString(i++,"");
				} else {
					ps.setString(i++,parser.getParameter("mobile_0","")+"-"+parser.getParameter("mobile_1","")+"-"+parser.getParameter("mobile_2",""));
				}
	
				ps.setString(i++,email_id+"@"+bcline);
				
				ps.setString(i++,parser.getParameter("job",""));
				ps.setString(i++,parser.getParameter("jobtype",""));
				ps.setString(i++,parser.getParameter("birth",""));
				ps.setString(i++,parser.getParameter("solar",""));
				ps.setString(i++,addr_clss);
				ps.setString(i++, account1);
				ps.executeUpdate();
				
			//}
			
			// 20100104 - 추가된 회원정보 컬럼 업데이트 
			String sql_person =  "\n UPDATE BCDBA.TBGGOLFCDHD ";
			sql_person += "\n	  SET MOBILE = ?, PHONE = ?, EMAIL = ?, ZIP_CODE = ?, ZIPADDR = ?, DETAILADDR = ?, NW_OLD_ADDR_CLSS = ?	";
			sql_person += "\n  WHERE CDHD_ID = ?  ";

			ps_person = conn.prepareStatement(sql_person);
			i = 1;			
			if (mobile1 == null || mobile1.equals("")) {
				ps_person.setString(i++,"");
			} else {
				ps_person.setString(i++,parser.getParameter("mobile_0","")+"-"+parser.getParameter("mobile_1","")+"-"+parser.getParameter("mobile_2",""));
			}
			ps_person.setString(i++,parser.getParameter("phone_0","")+"-"+parser.getParameter("phone_1","")+"-"+parser.getParameter("phone_2",""));
			ps_person.setString(i++,email_id+"@"+bcline);
			ps_person.setString(i++,parser.getParameter("zipcode1","")+"-"+parser.getParameter("zipcode2",""));
			ps_person.setString(i++, zipaddr);
			ps_person.setString(i++, detailAddr);
			ps_person.setString(i++, addr_clss);
			ps_person.setString(i++, account1);
			ps_person.executeUpdate();
			
			int j = 1;
			String site_clss = "1";			

			isSuccess = true;

			SSOdbprotectorWrap ssodbprotector = new SSOdbprotectorWrap();

			boolean ssocheck = ssodbprotector.changeUserInfo(account1, 3, email_id+"@"+bcline);
			if(ssocheck){
				conn.commit();
			}else{
				conn.rollback();
				throw new Exception("SSO update실패.");
			}

			try{
				if( mailChange ) {		
					
					if( (site_clss.equals("0") || site_clss.equals("2")) ) {
						//LUCKYBC에 POST 방식 URL CALL을 하여 LUCKYBC 비밀번호, E_mail 변경을 시도한다.
						String socid = bcuser.getSocid();

						byte[] hash_socid = CipherClient.hash(socid.getBytes());
						String lucky_socid = new String(Base64Util.encode(hash_socid));
						String luckyEmail = email_id+"@"+bcline;

						

						lucky_socid = java.net.URLEncoder.encode(lucky_socid.trim());
						luckyEmail = java.net.URLEncoder.encode(luckyEmail.trim());
						String paramStr = "id=" + account1
										  + "&email=" + luckyEmail
										  + "&socid=" + lucky_socid;

						String urlStr = "";

						if ( devip.equals(appip) )  {
							urlStr = "http://test.luckybc.com/member/member_update.asp";  // 개발기  URL
						} else {
							urlStr = "http://www.luckybc.com/member/member_update.asp";  // 운영기  URL
						}

						String luckyResult = openURL(urlStr, paramStr);

						if (luckyResult == null || !luckyResult.equals("0")) {

							

							throw new Exception("럭키비씨 사용자 정보 수정 오류");
						}
					}
				}
			}catch(Exception ex){
				isSuccess = false;

			}finally{				
				try { if( ps != null ){ ps.close(); } else {} } catch(Throwable ignore) {}
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
												
			}
		}catch(SQLException se){
			//se.printStackTrace();
			System.out.println(se.getMessage());
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception e){
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}catch(Exception ex){
			//ex.printStackTrace();
			System.out.println(ex.getMessage());
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception exx){
			}
		}finally{
			
			try { if( ps != null ){ ps.close(); } else {} } catch(Throwable ignore) {}
			try { if( ps_person != null ){ ps_person.close(); } else {} } catch(Throwable ignore) {}
			try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
		}
		return isSuccess;
	 }
	
	/**
	* 인터넷법인회원정보수정 팝업 이벤트 20091101 진현구
	* @param 	context		WaContext 객체.
	* @param 	parser		RequestParser 객체.
	* @param 	session		HttpSession 객체.
	* @return 	boolean		정보 수정 UPDATE 처리 결과.
	**/ 
	protected boolean getMemberInfoUpdate5 (	WaContext context, RequestParser parser,
																					HttpSession session, HttpServletRequest request) {
		
		UcusrinfoEntity bcuser = SessionUtil.getFrontUserInfo(request);
		
		String account1 = bcuser.getAccount();
		String socialid = bcuser.getSocid();
		int mem_id = bcuser.getMemid();

		Connection conn = null;		
		PreparedStatement ps = null;				
		String sql = null;
		boolean isSuccess	= false;

		String appip = "";
		String devip = "";

		
		try {
			appip = InetAddress.getLocalHost().getHostAddress();
			devip = AppConfig.getAppProperty("DEV_APP_IP");
		} catch(Throwable t) {
		}
		if (appip == null) appip = "unknown";
		if ( devip == null ) devip = "";


		// UPDATE문 만드는것 테스트
		sql =  "\n UPDATE BCDBA.TBENTPUSER ";
		sql += "\n	  SET USER_TEL_NO=?, USER_MOB_NO=?, USER_EMAIL = ? ";
		sql += "\n  WHERE account = ? and USER_JUMIN_NO=? and mem_id=? ";
		
		try{
			String email_id = parser.getParameter("email_id","");
		    String bcline = parser.getParameter("select1","");

			boolean mailChange = (parser.getParameter("email_id")+"@"+parser.getParameter("select")).equals(parser.getParameter("EMAIL")) ? false : true;

			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);

			
			// WafService (모바일을 통한 입력 : 한글문제 )
			String detailAddr = parser.getParameter("detailaddr", "");
			String zipaddr = parser.getParameter("zipaddr", "");
			if ( request instanceof com.bccard.waf.action.ServiceRequest ) {
				detailAddr = new String(detailAddr.getBytes("ISO-8859-1"), "UTF-8");
				zipaddr = new String(zipaddr.getBytes("ISO-8859-1"), "UTF-8");
			}

			ps = conn.prepareStatement(sql);
			int i = 1;
			
			String tel_ddd_no = parser.getParameter("tel_ddd_no","");
			
			if(!"".equals(tel_ddd_no)) {
				ps.setString(i++,parser.getParameter("tel_ddd_no","")+"-"+parser.getParameter("tel_tel_hno","")+"-"+parser.getParameter("tel_tel_sno",""));
				
				String mobile1 = parser.getParameter("hp_ddd_no","");

				if (mobile1 == null || mobile1.equals("")) {
					ps.setString(i++,"");
				} else {
					ps.setString(i++,parser.getParameter("hp_ddd_no","")+"-"+parser.getParameter("hp_tel_hno","")+"-"+parser.getParameter("hp_tel_sno",""));
				}
			} else {
				ps.setString(i++,parser.getParameter("phone_0","")+"-"+parser.getParameter("phone_1","")+"-"+parser.getParameter("phone_2",""));

				String mobile1 = parser.getParameter("mobile_1","");

				if (mobile1 == null || mobile1.equals("")) {
					ps.setString(i++,"");
				} else {
					ps.setString(i++,parser.getParameter("mobile_0","")+"-"+parser.getParameter("mobile_1","")+"-"+parser.getParameter("mobile_2",""));
				}			
			}


			ps.setString(i++,email_id+"@"+bcline);

			ps.setString(i++, account1);
			ps.setString(i++, socialid);
			ps.setInt(i++, mem_id);
			ps.executeUpdate();

			isSuccess = true;
		}catch(SQLException se){
			//se.printStackTrace();
			System.out.println(se.getMessage());
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception e){
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}catch(Exception ex){
			//ex.printStackTrace();
			System.out.println(ex.getMessage());
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception exx){
			}
		}finally{
			
			try { if( ps != null ){ ps.close(); } else {} } catch(Throwable ignore) {}
			try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
		}
		return isSuccess;
	 }
	
	/**
	* 인터넷회원정보수정 팝업 이벤트 20091015 권영만
	* @param 	context		WaContext 객체.
	* @param 	parser		RequestParser 객체.
	* @param 	session		HttpSession 객체.
	* @return 	boolean		정보 수정(준회원) UPDATE 처리 결과. 
	* 
	* 기존  GolfMemPopUpdActn.java에서 콜함 
	* -> 현재는 사용 하지 않음 차후에 사용시 재 테스트 후 사용해야함
	**/ 
	protected boolean getMemberInfoUpdatePop (	WaContext context, RequestParser parser,
																					HttpSession session, HttpServletRequest request) {
		
		UcusrinfoEntity bcuser = SessionUtil.getFrontUserInfo(request);
		
		String account1 = bcuser.getAccount();
		String socialid = bcuser.getSocid();

		Connection conn = null;		
		PreparedStatement ps = null;
		PreparedStatement ps_person = null;
		String sql = null;
		boolean isSuccess	= false;

		String appip = "";
		String devip = "";
		
		String addr_clss = parser.getParameter("addr_clss", "");

		
		try {
			appip = InetAddress.getLocalHost().getHostAddress();
			devip = AppConfig.getAppProperty("DEV_APP_IP");
		} catch(Throwable t) {
		}
		if (appip == null) appip = "unknown";
		if ( devip == null ) devip = "";


		// UPDATE문 만드는것 테스트
		sql =  "\n UPDATE BCDBA.UCUSRINFO ";
		sql += "\n	  SET	ZIPCODE = ?,";
		
		if ( !addr_clss.equals("2") ){//구주소
			sql += "\n			ZIPADDR = ?, DETAILADDR = ?, ";
		}else { //새주소
			sql += "\n			DONG_OVR_NEW_ADDR = ?, DONG_BLW_NEW_ADDR = ?, ";
		}		
		
		sql += "\n			PHONE=?, MOBILE=?, EMAIL1 = ?, ADDR_CLSS=? ";
		sql += "\n  WHERE account = ?  ";
		
		try{
			String email_id = parser.getParameter("email_id","");
		    String bcline = parser.getParameter("select1","");

			boolean mailChange = (parser.getParameter("email_id")+"@"+parser.getParameter("select")).equals(parser.getParameter("EMAIL")) ? false : true;

			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);

			
			// WafService (모바일을 통한 입력 : 한글문제 )
			String detailAddr = parser.getParameter("detailaddr", "");
			String zipaddr = parser.getParameter("zipaddr", "");
			if ( request instanceof com.bccard.waf.action.ServiceRequest ) {
				detailAddr = new String(detailAddr.getBytes("ISO-8859-1"), "UTF-8");
				zipaddr = new String(zipaddr.getBytes("ISO-8859-1"), "UTF-8");
			}

			ps = conn.prepareStatement(sql);
			int i = 1;			
			ps.setString(i++,parser.getParameter("zipcode1","")+"-"+parser.getParameter("zipcode2",""));
			ps.setString(i++, zipaddr);
			ps.setString(i++, detailAddr);
			ps.setString(i++,parser.getParameter("tel_ddd_no","")+"-"+parser.getParameter("tel_tel_hno","")+"-"+parser.getParameter("tel_tel_sno",""));


			String mobile1 = parser.getParameter("hp_ddd_no","");

			if (mobile1 == null || mobile1.equals("")) {
				ps.setString(i++,"");
			} else {
				ps.setString(i++,parser.getParameter("hp_ddd_no","")+"-"+parser.getParameter("hp_tel_hno","")+"-"+parser.getParameter("hp_tel_sno",""));
			}

			ps.setString(i++,email_id+"@"+bcline);
			
			ps.setString(i++,addr_clss);
			ps.setString(i++, account1);
			ps.executeUpdate();

			// 20100104 - 추가된 회원정보 컬럼 업데이트 
			String sql_person =  "\n UPDATE BCDBA.TBGGOLFCDHD ";			
			sql_person += "\n	  SET MOBILE = ?, PHONE = ?, EMAIL = ?, ZIP_CODE = ?, ZIPADDR = ?, DETAILADDR = ?, ADDR_CLSS = ?	";
			sql_person += "\n  WHERE CDHD_ID = ?  ";

			ps_person = conn.prepareStatement(sql_person);
			i = 1;			
			if (mobile1 == null || mobile1.equals("")) {
				ps_person.setString(i++,"");
			} else {
				ps_person.setString(i++,parser.getParameter("hp_ddd_no","")+"-"+parser.getParameter("hp_tel_hno","")+"-"+parser.getParameter("hp_tel_sno",""));
			}
			ps_person.setString(i++,parser.getParameter("tel_ddd_no","")+"-"+parser.getParameter("tel_tel_hno","")+"-"+parser.getParameter("tel_tel_sno",""));
			ps_person.setString(i++,email_id+"@"+bcline);
			ps_person.setString(i++,parser.getParameter("zipcode1","")+"-"+parser.getParameter("zipcode2",""));
			ps_person.setString(i++, zipaddr);
			ps_person.setString(i++, detailAddr);
			ps_person.setString(i++, addr_clss);			
			ps_person.setString(i++, account1);
			ps_person.executeUpdate();
			
			int j = 1;
			String site_clss = "1";			

			isSuccess = true;

			SSOdbprotectorWrap ssodbprotector = new SSOdbprotectorWrap();

			boolean ssocheck = ssodbprotector.changeUserInfo(account1, 3, email_id+"@"+bcline);
			if(ssocheck){
				conn.commit();
			}else{
				conn.rollback();
				throw new Exception("SSO update실패.");
			}

			try{
				if( mailChange ) {		
					
					if( (site_clss.equals("0") || site_clss.equals("2")) ) {
						//LUCKYBC에 POST 방식 URL CALL을 하여 LUCKYBC 비밀번호, E_mail 변경을 시도한다.
						String socid = bcuser.getSocid();

						byte[] hash_socid = CipherClient.hash(socid.getBytes());
						String lucky_socid = new String(Base64Util.encode(hash_socid));
						String luckyEmail = email_id+"@"+bcline;

						

						lucky_socid = java.net.URLEncoder.encode(lucky_socid.trim());
						luckyEmail = java.net.URLEncoder.encode(luckyEmail.trim());
						String paramStr = "id=" + account1
										  + "&email=" + luckyEmail
										  + "&socid=" + lucky_socid;

						String urlStr = "";

						if ( devip.equals(appip) )  {
							urlStr = "http://test.luckybc.com/member/member_update.asp";  // 개발기  URL
						} else {
							urlStr = "http://www.luckybc.com/member/member_update.asp";  // 운영기  URL
						}

						String luckyResult = openURL(urlStr, paramStr);

						if (luckyResult == null || !luckyResult.equals("0")) {

							

							throw new Exception("럭키비씨 사용자 정보 수정 오류");
						}
					}
				}
			}catch(Exception ex){
				isSuccess = false;

			}finally{				
				try { if( ps != null ){ ps.close(); } else {} } catch(Throwable ignore) {}
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
												
			}
		}catch(SQLException se){
			//se.printStackTrace();
			System.out.println(se.getMessage());
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception e){
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}catch(Exception ex){
			//ex.printStackTrace();
			System.out.println(ex.getMessage());
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception exx){
			}
		}finally{
			try { if( ps != null ){ ps.close(); } else {} } catch(Throwable ignore) {}
			try { if( ps_person != null ){ ps_person.close(); } else {} } catch(Throwable ignore) {}
			try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
		}
		return isSuccess;
	 }


	/**
	* 인터넷회원정보수정
	* @param 	context		WaContext 객체.
	* @param 	parser		RequestParser 객체.
	* @param 	session		HttpSession 객체.
	* @return 	boolean		정보 수정(정회원) UPDATE 처리 결과.
	**/// 새주소 작업하면서 확인, 쓰이는 곳 없음
	protected boolean getMemberInfoUpdate(	WaContext context, RequestParser parser,
																		HttpSession session, HttpServletRequest request) {

		BcUserEtt bcuser = (BcUserEtt)session.getAttribute("LOGIN_USER");

		String account = bcuser.getAccount();
		String socid = bcuser.getSocid();

		Connection conn = null;
		PreparedStatement pstmt1 = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet rs = null;
		String sql = null;		
		boolean isSuccess	= false;

		// 서버 IP
		String appip = "";
		String devip = "";

		try {
			appip = InetAddress.getLocalHost().getHostAddress();
			devip = AppConfig.getAppProperty("DEV_APP_IP");
		} catch(Throwable t) {
		}
		if (appip == null) appip = "unknown";
		if ( devip == null ) devip = "";


		int i = 1;	// pstmt의 index 변수

		try{
			String email_id = parser.getParameter("email_id","");
			String select = parser.getParameter("select","");
		    String bcline = parser.getParameter("select","");
			String email = parser.getParameter("EMAIL");

			boolean mailChange = (email_id+"@"+select).equals(parser.getParameter("EMAIL")) ? false : true;

			if (mailChange){
				email = email_id+"@"+select;
			}

			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);

			String mail_recv_chk = parser.getParameter("mail_recv_chk", "false");
			String [] mb_check = parser.getParameterValues("mb_no");
			String [] card_recv_yn = parser.getParameterValues("card_recv_yn","0");
			String [] shopping_recv_yn = parser.getParameterValues("shopping_recv_yn","0");
			String [] tour_recv_yn = parser.getParameterValues("tour_recv_yn","0");
			String [] ctnt_recv_yn = parser.getParameterValues("ctnt_recv_yn","0");
			String recv_yn = parser.getParameter("recv_yn","");

			String mailing = ("0".equals(card_recv_yn[0]))?"1":"0";

			String cur_date = DateUtil.currdate("yyyyMMdd");
			String sms_recp_rej_date = "";
			if ("0".equals(recv_yn)) sms_recp_rej_date = DateUtil.currdate("yyyyMMdd");

			int memid = Integer.parseInt(bcuser.getMemid());
			String old_mailing = null;
			String old_last_chng_date = cur_date;
			String old_card_recv_yn = null;
			String old_card_corr_date = cur_date;
			String old_tour_recv_yn = null;
			String old_tour_corr_date = cur_date;
			String old_shopping_recv_yn = null;
			String old_shopping_corr_date = cur_date;
			String old_ctnt_recv_yn = null;
			String old_ctnt_corr_date = cur_date;
			String todate = null;
			String mb_no = null;

			// WafService (모바일을 통한 입력 : 한글문제 )
			String detailAddr = parser.getParameter("detailaddr", "");
			if ( request instanceof com.bccard.waf.action.ServiceRequest ) {
				detailAddr = new String(detailAddr.getBytes("ISO-8859-1"), "UTF-8");
			}

			// 회원정보 수정쿼리
			sql	= "\n UPDATE BCDBA.UCUSRINFO "
				+ "\n	 SET PASSWDQ = ?, PASSWDA = ?,"
				+ "\n		 ENAME = ?, ZIPCODE=?, ZIPADDR=?, DETAILADDR=?, PHONE = ?, "
				+ "\n		 MOBILE =?, BIRTH = ?, SOLAR =?, EMAIL1 =?, "
				+ "\n		 JOB =?, JOBTYPE =?, WEDYN=?, WEDANNIV=?, RECOMM_ACCOUNT = ?, MAILING = ?,  "
				+ "\n		 RECV_YN = ?, SMS_RECP_REJ_DATE = ?,	"
				+ "\n		 EMAIL_CORR_DATE = TO_CHAR(SYSDATE, 'YYYYMMDD') "
				+ "\n  WHERE account = ? and member_clss = '1' and ( site_clss = '0' or site_clss = '1' ) ";

			i = 1;	// 변수 초기화
			pstmt1 = conn.prepareStatement(sql);
			pstmt1.setString(i++,	parser.getParameter("passwdq",""));
			pstmt1.setString(i++,	parser.getParameter("passwda",""));
			pstmt1.setString(i++,	parser.getParameter("ename",""));
			pstmt1.setString(i++,	parser.getParameter("zipcode1","")+"-"+
									parser.getParameter("zipcode2",""));
			pstmt1.setString(i++,	parser.getParameter("zipaddr",""));

//모바일서비스용 한글 문제 수정
//			pstmt1.setString(i++,	parser.getParameter("detailaddr",""));
			pstmt1.setString(i++,	detailAddr);

			pstmt1.setString(i++,	parser.getParameter("phone_0","")+"-"+
									parser.getParameter("phone_1","")+"-"+
									parser.getParameter("phone_2",""));

			String mobile1 = parser.getParameter("mobile_1","");
			if (mobile1 != null && !"".equals(mobile1)) {
				pstmt1.setString(i++,	parser.getParameter("mobile_0","")+"-"+
										parser.getParameter("mobile_1","")+"-"+
										parser.getParameter("mobile_2",""));
			}

			pstmt1.setString(i++,parser.getParameter("birth1","")+parser.getParameter("birth2","")+parser.getParameter("birth3",""));
			pstmt1.setString(i++,parser.getParameter("solar",""));
			pstmt1.setString(i++,email_id+"@"+bcline);
			pstmt1.setString(i++,parser.getParameter("job",""));
			pstmt1.setString(i++,parser.getParameter("jobtype",""));
			pstmt1.setString(i++,parser.getParameter("wedyn",""));
			pstmt1.setString(i++,parser.getParameter("wedanniv1","")+parser.getParameter("wedanniv2","")+parser.getParameter("wedanniv3",""));
			pstmt1.setString(i++,parser.getParameter("recomm_account",""));
			pstmt1.setString(i++,mailing);
			pstmt1.setString(i++,recv_yn);
			pstmt1.setString(i++,sms_recp_rej_date);
			pstmt1.setString(i++, account);

			pstmt1.executeUpdate();

			String site_clss = "1";
			String oldpasswd = parser.getParameter("passwd","");
			String oldpasswd1 = parser.getParameter("passwd","");
			isSuccess = true;

// DB trigger 작업으로 SSO 부분 삭제 2005.10.19
			SSOdbprotectorWrap ssodbprotector = new SSOdbprotectorWrap();
			boolean ssocheck = ssodbprotector.changeUserInfo(account, 3, email_id+"@"+bcline);
			if(ssocheck){
				conn.commit();
			}else{
				conn.rollback();
				throw new Exception("SSO update실패.");
			}


			if( mailChange ) {		// password는 체크안하기로 했음
				// 라인, 카드공동회원과 라인회원인 경우에 럭키에 회원정보 수정시 일려주어야 한다.

				if( (site_clss.equals("0") || site_clss.equals("2")) ) {
					//LUCKYBC에 POST 방식 URL CALL을 하여 LUCKYBC 비밀번호, E_mail 변경을 시도한다.
					byte[] hash_socid = CipherClient.hash(socid.getBytes());
					String lucky_socid = new String(Base64Util.encode(hash_socid));
					String luckyEmail = email_id+"@"+bcline;

					if(bcline.equals("bcline.com")){
						String stringUrl1 = null;

						if(oldpasswd!=null && !oldpasswd.equals("")) {
							stringUrl1="http://mail.bcline.com/cgi-bin/changemailquota.cgi?id="	+ account+ "&pwd=" + URLEncoder.encode(oldpasswd.trim());
						} else {
							stringUrl1="http://mail.bcline.com/cgi-bin/changemailquota.cgi?id=" + account+ "&pwd=" + URLEncoder.encode(oldpasswd1.trim());
						}

						String mailRes1 = openURL( stringUrl1 );
						// mailRes = "1";	// test용
						//웹메일 서버에서 실패했다는 메시지가 출력되면 Exception을 만들어 rollback을 유도. "1"성공

						if (mailRes1 == null || mailRes1.equals("-1") || !mailRes1.equals("1")) throw new Exception("메일비밀번호수정	실패");

						// 계정 용량 50M 로 늘려준다.
						String receUrl = "http://mail.bcline.com/cgi-bin/uregi50.cgi?id=" + account + "&pwd=" + URLEncoder.encode(oldpasswd1.trim()) + "&name=";
						 //+ URLEncoder.encode(bcuser.getMemberName());

						openURL(receUrl);

						//이벤트를 위해 추가
						//EventTransaction et22 = new EventTransaction(socid, "4");
						//et22.execute();
					}

					lucky_socid = java.net.URLEncoder.encode(lucky_socid.trim());
					luckyEmail = java.net.URLEncoder.encode(luckyEmail.trim());
					String paramStr = "id=" + account
									  + "&email=" + luckyEmail
									  + "&socid=" + lucky_socid;


					String urlStr = "";

					if ( devip.equals(appip) ) {
						urlStr = "http://test.luckybc.com/member/member_update.asp";  // 개발기  URL
					} else {
						urlStr = "http://www.luckybc.com/member/member_update.asp";  // 운영기  URL
					}

					String luckyResult = openURL(urlStr, paramStr);

					if (luckyResult == null || !luckyResult.equals("0")) {

						//LUCKYBC의 처리가 성공적으로 종료될 경우 문자열 "0"이 넘어오는데
						//비정상 리턴값        : 2
						//주민등록 중복될 경우 : 3
						//그렇지 않을 경우 실패로 간주하고 Exception을 invoke하여 rollback을 유도한다.
						//errorLog( account, "1", "1" );
//							debug(account);
						// getBizHash( hash table ), account( 사용자계정 ), "1"(update error), "1"(에러서버 럭키비씨)

						throw new Exception("럭키비씨 사용자 정보 수정 오류");
					}
				}
			}

		}catch(Exception se){
			//se.printStackTrace();
			System.out.println(se.getMessage());
			isSuccess = false;
			if(conn != null) { try{ conn.rollback(); }catch(Exception e){} } else {}
		}finally{
			if(rs != null) { try{ rs.close(); }catch(Exception ex){} } else {}
			if(pstmt2 != null) { try{ pstmt2.close(); }catch(Exception ex){} } else {}
			if(pstmt3 != null) { try{ pstmt3.close(); }catch(Exception ex){} } else {}
			if(pstmt1 != null) { try{ pstmt1.close(); }catch(Exception ex){} } else {}
			if(conn != null) { try{ conn.setAutoCommit(true); }catch(Exception e){} } else {}
			if(conn != null) { try{ conn.close(); }catch(Exception ex){} } else {}
			return isSuccess;
		}
	 }



	/**
	* 인터넷회원정보수정
	* @param 	context		WaContext 객체.
	* @param 	parser		RequestParser 객체.
	* @param 	session		HttpSession 객체.
	* @return 	boolean		정보 수정(준회원) UPDATE 처리 결과.
	**/// 새주소 작업하면서 확인, 쓰이는 곳 없음
	protected boolean getMemberInfoUpdateNoCard(	WaContext context, RequestParser parser,
																					HttpSession session, HttpServletRequest request) {

		BcUserEtt bcuser = (BcUserEtt)session.getAttribute("LOGIN_USER");

		String account1 = bcuser.getAccount();
		String socialid = bcuser.getSocid();

		Connection conn = null;
		Statement stmt = null;
		PreparedStatement ps = null;
		PreparedStatement ps2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs = null;
		String sql = null;
		boolean isSuccess	= false;

		String appip = "";
		String devip = "";

		try {
			appip = InetAddress.getLocalHost().getHostAddress();
			devip = AppConfig.getAppProperty("DEV_APP_IP");
		} catch(Throwable t) {
		}
		if (appip == null) appip = "unknown";
		if ( devip == null ) devip = "";


		// UPDATE문 만드는것 테스트
		sql =  "\n UPDATE BCDBA.UCUSRINFO ";
		sql += "\n	  SET	PASSWDQ = ?, PASSWDA = ?,";
		sql += "\n			ENAME = ?, ZIPCODE=?, ZIPADDR=?, DETAILADDR=?, PHONE = ?, ";
		sql += "\n			MOBILE =?, BIRTH = ?, SOLAR =?, EMAIL1 =?, ";
		sql += "\n			WEDYN =?, WEDANNIV =?,   MAILING = ?,  RECV_YN = ?";
		sql += "\n  WHERE account = ? and member_clss = '4' and site_clss = '2' ";


		try{
			String email_id = parser.getParameter("email_id","");
		    String bcline = parser.getParameter("select","");

			boolean mailChange = (parser.getParameter("email_id")+"@"+parser.getParameter("select")).equals(parser.getParameter("EMAIL")) ? false : true;

			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);

			String mailing = parser.getParameter("mailing","");
			//String card_recv_yn = parser.getParameter("card_recv_yn","");
			String shopping_recv_yn = parser.getParameter("shopping_recv_yn","");
			String tour_recv_yn = parser.getParameter("tour_recv_yn","");
			String ctnt_recv_yn = parser.getParameter("ctnt_recv_yn","");
			String recv_yn = parser.getParameter("recv_yn","");

			// WafService (모바일을 통한 입력 : 한글문제 )
			String detailAddr = parser.getParameter("detailaddr", "");
			String zipaddr = parser.getParameter("zipaddr", "");
			if ( request instanceof com.bccard.waf.action.ServiceRequest ) {
				detailAddr = new String(detailAddr.getBytes("ISO-8859-1"), "UTF-8");
				zipaddr = new String(zipaddr.getBytes("ISO-8859-1"), "UTF-8");
			}

			ps = conn.prepareStatement(sql);
			int i = 1;
			ps.setString(i++,parser.getParameter("passwdq",""));
			ps.setString(i++,parser.getParameter("passwda",""));
			ps.setString(i++,parser.getParameter("ename",""));
			ps.setString(i++,parser.getParameter("zipcode1","")+"-"+parser.getParameter("zipcode2",""));
//			ps.setString(i++,parser.getParameter("zipaddr",""));
//			ps.setString(i++,parser.getParameter("detailaddr",""));
ps.setString(i++, zipaddr);
ps.setString(i++, detailAddr);
			ps.setString(i++,parser.getParameter("phone_0","")+"-"+parser.getParameter("phone_1","")+"-"+parser.getParameter("phone_2",""));

			String mobile1 = parser.getParameter("mobile_1","");

			if (mobile1 == null || mobile1.equals("")) {
				ps.setString(i++,"");
			} else {
				ps.setString(i++,parser.getParameter("mobile_0","")+"-"+parser.getParameter("mobile_1","")+"-"+parser.getParameter("mobile_2",""));
			}

			ps.setString(i++,parser.getParameter("birth1","")+parser.getParameter("birth2","")+parser.getParameter("birth3",""));
			ps.setString(i++,parser.getParameter("solar",""));
			ps.setString(i++,email_id+"@"+bcline);
			ps.setString(i++,parser.getParameter("wedyn",""));
			ps.setString(i++,parser.getParameter("wedanniv1","")+parser.getParameter("wedanniv2","")+parser.getParameter("wedanniv3",""));
			ps.setString(i++,mailing);
			ps.setString(i++,recv_yn);
			ps.setString(i++, account1);

			ps.executeUpdate();

			int j = 1;

			String site_clss = "1";
			String oldpasswd = parser.getParameter("passwd","");
			String oldpasswd1 = parser.getParameter("passwd","");

			isSuccess = true;
//			conn.commit();

// DB trigger 작업으로 SSO 부분 삭제 2005.10.19
			SSOdbprotectorWrap ssodbprotector = new SSOdbprotectorWrap();

			boolean ssocheck = ssodbprotector.changeUserInfo(account1, 3, email_id+"@"+bcline);
			if(ssocheck){
				conn.commit();
			}else{
				conn.rollback();
				throw new Exception("SSO update실패.");
			}

			try{
				if( mailChange ) {		// password는 체크안하기로 했음
					// 라인, 카드공동회원과 라인회원인 경우에 럭키에 회원정보 수정시 일려주어야 한다.

					if( (site_clss.equals("0") || site_clss.equals("2")) ) {
						//LUCKYBC에 POST 방식 URL CALL을 하여 LUCKYBC 비밀번호, E_mail 변경을 시도한다.
						String socid = bcuser.getSocid();

						byte[] hash_socid = CipherClient.hash(socid.getBytes());
						String lucky_socid = new String(Base64Util.encode(hash_socid));
						String luckyEmail = email_id+"@"+bcline;

						if(bcline.equals("bcline.com")){
							String stringUrl1 = null;

							if(oldpasswd!=null && !oldpasswd.equals("")) {
								stringUrl1="http://mail.bcline.com/cgi-bin/changemailquota.cgi?id="	+ account1+ "&pwd=" + URLEncoder.encode(oldpasswd.trim());
							} else {
								stringUrl1="http://mail.bcline.com/cgi-bin/changemailquota.cgi?id=" + account1+ "&pwd=" + URLEncoder.encode(oldpasswd1.trim());
							}

							String mailRes1 = openURL( stringUrl1 );
							// mailRes = "1";	// test용
							//웹메일 서버에서 실패했다는 메시지가 출력되면 Exception을 만들어 rollback을 유도. "1"성공

							if (mailRes1 == null || mailRes1.equals("-1") || !mailRes1.equals("1")) throw new Exception("메일비밀번호수정	실패");

							// 계정 용량 50M 로 늘려준다.
							String receUrl = "http://mail.bcline.com/cgi-bin/uregi50.cgi?id=" + account1 + "&pwd=" + URLEncoder.encode(oldpasswd1.trim()) + "&name=";
							 //+ URLEncoder.encode(bcuser.getMemberName());

							openURL(receUrl);

							//이벤트를 위해 추가
							//EventTransaction et22 = new EventTransaction(socialid, "4");
							//et22.execute();
						}

						lucky_socid = java.net.URLEncoder.encode(lucky_socid.trim());
						luckyEmail = java.net.URLEncoder.encode(luckyEmail.trim());
						String paramStr = "id=" + account1
										  + "&email=" + luckyEmail
										  + "&socid=" + lucky_socid;

						String urlStr = "";

						if ( devip.equals(appip) )  {
							urlStr = "http://test.luckybc.com/member/member_update.asp";  // 개발기  URL
						} else {
							urlStr = "http://www.luckybc.com/member/member_update.asp";  // 운영기  URL
						}

						String luckyResult = openURL(urlStr, paramStr);

						if (luckyResult == null || !luckyResult.equals("0")) {

							//LUCKYBC의 처리가 성공적으로 종료될 경우 문자열 "0"이 넘어오는데
							//비정상 리턴값        : 2
							//주민등록 중복될 경우 : 3
							//그렇지 않을 경우 실패로 간주하고 Exception을 invoke하여 rollback을 유도한다.
							//errorLog( account1, "1", "1" );
							// getBizHash( hash table ), account1( 사용자계정 ), "1"(update error), "1"(에러서버 럭키비씨)

							throw new Exception("럭키비씨 사용자 정보 수정 오류");
						}
					}
				}
			}catch(Exception ex){
				isSuccess = false;

			}finally{
				 if(rs != null) { try{ rs.close(); }catch(Exception ex){} } else {}
				 if(stmt != null) { try{ stmt.close(); }catch(Exception ex){} } else {}
				 if(ps2 != null) { try{ ps2.close(); }catch(Exception ex){} } else {}
				 if(ps3 != null) { try{ ps3.close(); }catch(Exception ex){} } else {}
				 if(ps != null) { try{ ps.close(); }catch(Exception ex){} } else {}
				 if(conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch(Exception ex){} } else {}
				return isSuccess;
			}
		}catch(SQLException se){
			//se.printStackTrace();
			System.out.println(se.getMessage());
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception e){
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}
		}catch(Exception ex){
			//ex.printStackTrace();
			System.out.println(ex.getMessage());
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception exx){
			}
		}finally{
			if(rs != null) { try{ rs.close(); }catch(Exception ex){} } else {}
			if(stmt != null) { try{ stmt.close(); }catch(Exception ex){} } else {}
			if(ps2 != null) { try{ ps2.close(); }catch(Exception ex){} } else {}
			if(ps3 != null) { try{ ps3.close(); }catch(Exception ex){} } else {}
			if(ps != null) { try{ ps.close(); }catch(Exception ex){} } else {}
			if(conn != null) { try{ conn.close(); }catch(Exception ex){} } else {}
			return isSuccess;
		}
	 }

	/**
	* 인터넷회원정보수정 - 문자열 분리
	* @param 	str		String 객체.
	* @param 	delimeter		String 객체.
	* @return 	String[]	분리된 문자열.
	**/
	public String[] phoneMobile(String str, String delimeter){

		int count = 0;
		int index = 0;

		if(str == null) {
			str = "";
		}

		do {
			++count;
			++index;
			index = str.indexOf(delimeter,index);
		}
		while(index != -1);

		String[] subStr = new String[count];
		index = 0;
		int endIndex = 0;

		for(int i = 0; i < count; i++) {

			endIndex = str.indexOf(delimeter,index);

			if(endIndex == -1) {
				subStr[i] = str.substring(index);
			}else{
				subStr[i] = str.substring(index,endIndex);
				index = endIndex + 1;
			}
		}

		return subStr;
	}



	/**
	* 인터넷회원정보수정 - 문자열 분리
	* @param 	Str		String 객체.
	* @param 	delimeter		String 객체.
	* @return 	String[]	분리된 문자열.
	**/
	public String[] spliteString(String str, String delimeter) {

		if(str == null) {
			str = "";
		}
		String[] ss = new String[3];

		StringTokenizer parser = new StringTokenizer(str, delimeter);

		int i = 0;
		while (parser.hasMoreTokens()) {  //아직 토큰이 남아있는 동안 다음 토큰을 출력하라
			ss[i] = parser.nextToken();
			i++;
		}

		if (i == 0 || i == 1) {
			ss[0] = "";
			ss[1] = "";
			ss[2] = "";
		}

		return ss;
	}



	/**
	* 인터넷회원정보수정 - 회원가입 후 비씨라인 메일서버로 호출을 위한 메소드 생성
	* @param 	urlStr		String 객체.
	* @return 	String		URL.
	**/
	protected String openURL(String urlStr) throws Exception {

		String buf = "", data = "";
		BufferedReader br = null;
		try{
			URL url = new URL(urlStr);
			URLConnection urlCon = url.openConnection();
			urlCon.setUseCaches(false);
			br = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
			while ((buf = br.readLine()) != null) {
				data += buf ;
			}
		}catch (Exception e){
			throw e;
		}finally{
			if (br!=null) { br.close(); } else {}
		}
		return data ;
	}



	/**
	* 인터넷회원정보수정 - 회원가입 후 비씨라인 메일서버로 호출을 위한 메소드 생성
	* @param 	urlStr		String 객체.
	* @param 	paramStr	String 객체.
	* @return 	String		URL.
	**/
	protected String openURL(String urlStr, String paramStr) throws Exception {

		URL url = null;
		URLConnection urlCon=null;

		try {
			url = new URL(urlStr);
		} catch (java.net.MalformedURLException mre) {
			throw mre;
		}

		try {
			urlCon = url.openConnection();
		} catch (IOException ioe) {
			throw ioe;
		}

		urlCon.setDoOutput(true);
		urlCon.setDoInput(true);
		urlCon.setAllowUserInteraction(false);
		DataOutputStream dos=null;
		String data = new String();
		String buf = new String();

		try {
			dos = new DataOutputStream(urlCon.getOutputStream());
			dos.writeBytes(paramStr);
			dos.flush();
		} catch (Exception ioe) {
			throw ioe;

		} finally {
			try
			{ if (dos!=null) {  dos.close(); } else {}
			} catch (IOException ioe) { }
		}

		BufferedReader br = null;
		try {
			br = new BufferedReader( new InputStreamReader(urlCon.getInputStream()));

			while ((buf = br.readLine()) != null) {
				data += buf ;
			}
		} catch (Exception ioe) {
			throw ioe;
		} finally {
			try {
				if (br!=null) { br.close(); } else {}
			} catch (IOException ioe) {}
		}
		return data;
	}



	/**
	* 인터넷회원정보수정
	* @param 	context		WaContext 객체.
	* @param 	parser		RequestParser 객체.
	* @return 	int			존재하는 메일 주소 갯수.
	**/
	protected int getEmailCnt (WaContext context, RequestParser parser){
		int retVal = 0;

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuffer sql = new StringBuffer();

		try{

			conn = context.getDbConnection("default", null);

			String mailId = parser.getParameter("email_id", "");
		    String mailDomain = parser.getParameter("select", "");

			sql.append(" SELECT COUNT(*) FROM BCDBA.UCUSRINFO \n");
			sql.append(" WHERE  EMAIL1 = '" + mailId + "@" + mailDomain + "'  \n");
			sql.append(" AND    MEMBER_CLSS = '1' \n");

			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();

			if (rs.next()) {
				retVal = rs.getInt(1);
			}

		}catch(Exception e){
			//e.printStackTrace();
			System.out.println(e.getMessage());
		}finally{
			if(rs != null) { try{ rs.close(); }catch(Exception e){} } else {}
			if(pstmt != null) { try{ pstmt.close(); }catch(Exception e){} } else {}
			if(conn != null) { try{ conn.close(); }catch(Exception e){} } else {}
		}

		return retVal;
	}



	/**
	* 인터넷, SSO 회원정보수정
	* @param 	context		WaContext 객체.
	* @param 	request		HttpServletRequest 객체.
	* @return 	boolean		회원정보 수정 처리 결과.
	**/
	public boolean setMemberInfo(WaContext context, HttpServletRequest request) throws SQLException, Exception {
		
		Connection conn = null;

		PreparedStatement ps = null;
		PreparedStatement ps3 = null;		

		boolean isSuccess	= false;
	
		String addr_clss = (String)request.getAttribute("homeAddrClcd");
		
		// UCUSRINFO 수정
		StringBuffer sb01 = new StringBuffer();
		sb01.append(" UPDATE BCDBA.UCUSRINFO	\n");
		sb01.append(" SET	\n");
			//.append(" EMAIL1 = ?, ZIPCODE=?, ZIPADDR=?, DETAILADDR=?, MOBILE=?, PHONE = ? ")
			
		sb01.append(" EMAIL1 = ?, ZIPCODE=?, ");
			
		if ( !addr_clss.equals("2") ){//구주소
			sb01.append ("ZIPADDR = ?, DETAILADDR = ?, ");
		}else { //새주소
			sb01.append ("DONG_OVR_NEW_ADDR = ?, DONG_BLW_NEW_ADDR = ?, ");
		}			
			
		sb01.append(" MOBILE=?, PHONE = ?, NW_OLD_ADDR_CLSS=? ");			
		sb01.append(" WHERE account = ? and member_clss = '1' and ( site_clss = '0' or site_clss = '1' ) ");		
		
		// IDENTITY 수정
		StringBuffer sb02 = new StringBuffer();
		sb02.append(" UPDATE BCDBA.IDENTITY	\n")
			.append(" SET	\n")
			.append(" EMAIL = ?, ZIPCODE=?, ZIPADDR=?, DETAILADDR=?, MOBILE=?, PHONE = ?, BIRTH = ? ")
			.append(" WHERE USERID = ? ");

		try {
			conn = context.getDbConnection("default", null);	// 새로 추가부분
			conn.setAutoCommit(false);

			ps = conn.prepareStatement(sb01.toString());
			int i = 1;
			ps.setString(i++, (String)request.getAttribute("EMAIL") + "@" + (String)request.getAttribute("EMAIL_URL"));
			ps.setString(i++, (String)request.getAttribute("zipcode_0") + "-" + (String)request.getAttribute("zipcode_1"));
			ps.setString(i++, (String)request.getAttribute("zipaddr"));
			ps.setString(i++, (String)request.getAttribute("detailaddr"));
			ps.setString(i++, (String)request.getAttribute("mobile_0") + "-" + (String)request.getAttribute("mobile_1") + "-" + (String)request.getAttribute("mobile_2"));
			ps.setString(i++, (String)request.getAttribute("phone_0") + "-" + (String)request.getAttribute("phone_1") + "-" + (String)request.getAttribute("phone_2"));
			ps.setString(i++, (String)request.getAttribute("addr_clss"));
			ps.setString(i++, (String)request.getAttribute("ACCOUNT"));

			//ps.executeUpdate();

			ps3 = conn.prepareStatement(sb02.toString());
			int j = 1;
			ps3.setString(j++, (String)request.getAttribute("EMAIL") + "@" + (String)request.getAttribute("EMAIL_URL"));
			ps3.setString(j++, (String)request.getAttribute("zipcode_0") + "-" + (String)request.getAttribute("zipcode_1"));
			ps3.setString(j++, (String)request.getAttribute("zipaddr"));
			ps3.setString(j++, (String)request.getAttribute("detailaddr"));
			ps3.setString(j++, (String)request.getAttribute("mobile_0") + "-" + (String)request.getAttribute("mobile_1") + "-" + (String)request.getAttribute("mobile_2"));
			ps3.setString(j++, (String)request.getAttribute("phone_0") + "-" + (String)request.getAttribute("phone_1") + "-" + (String)request.getAttribute("phone_2"));
			ps3.setString(j++, (String)request.getAttribute("BIRTH_1") + (String)request.getAttribute("BIRTH_2") + (String)request.getAttribute("BIRTH_3"));
			ps3.setString(j++, (String)request.getAttribute("ACCOUNT"));

			/////////////////////추후 수정할것 
			//ps3.executeUpdate();
			
			isSuccess = true;
			conn.commit();

		} catch(SQLException se) {
			//se.printStackTrace();
			System.out.println(se.getMessage());
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception e){
				//e.printStackTrace();
				System.out.println(e.getMessage());
			}

			throw se;
		} catch(Exception ex) {
			//ex.printStackTrace();
			System.out.println(ex.getMessage());
			
			try{
				conn.rollback();
				isSuccess = false;
			}catch(Exception exx){
			}

			throw ex;
		} finally {
			if(ps3 != null) { try { ps3.close(); } catch(Exception ex){} } else {}
			if(ps != null) { try { ps.close(); } catch(Exception ex){} } else {}
			if(conn != null) { try { conn.setAutoCommit(true); conn.close(); } catch(Exception ex){} } else {}
		}

		return isSuccess;
	 }




	/**
	* 회원사별 메일 수신정보 조회
	* @param 	context		WaContext 객체.
	* @param 	String		String 객체.
	* @return 	Hashtable	정회원 회원정보.
	**/
	public HashMap getMailRecvYn(WaContext context,String memid){

		Connection conn 	= 	null;
		PreparedStatement ps = 	null;
		ResultSet rs = 	null;
		HashMap result = new HashMap();
		Vector vector = null;

		String sql =	"  SELECT NVL(mb_no,'99') AS mb_no ,NVL(card_recv_yn, '0') ,NVL(ctnt_recv_yn, '0')" +
						"\n		 ,NVL(shopping_recv_yn, '0') ,NVL(tour_recv_yn, '0')" +
						"\n  FROM bcdba.tbmailrecvyn WHERE memid = ? "+
						"\n	ORDER BY mb_no";

		try{

			conn = context.getDbConnection("default", null);	// 새로 추가부분

			ps = conn.prepareStatement(sql);
			ps.setInt(1,Integer.parseInt(memid));
			rs = ps.executeQuery();
			while (rs.next()){
				vector = new Vector();
				vector.add(0, rs.getString(2));
				vector.add(1, rs.getString(3));
				vector.add(2, rs.getString(4));
				vector.add(3, rs.getString(5));
				result.put(rs.getString(1), vector);
			}

		}catch(Exception se){
			debug(se.toString());
		}finally{
			if(rs != null) { try{ rs.close(); }catch(Throwable ex){} } else {}
			if(ps != null) { try{ ps.close(); }catch(Throwable ex){} } else {}
			if(conn != null) { try{conn.close();}catch(Throwable ex){} } else {}
		}
		return result;
	}


	/**
	 * 프라운지 계정 반환.
	 * @param context WaContext
	 * @param socid 주민번호
	 * @return 프라운지 계정이 존재하지 않으면 null 을 반환.
	 */
	public String getPlounzAccount(WaContext context, String socid) throws Throwable {
		String account = null;
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rset = null;
		try {
			conn = context.getDbConnection("default", null);
			stmt = conn.prepareStatement(" SELECT ID FROM BCDBA.TBLUGCDHD WHERE JUMIN_NO = ? AND SCSS_DATE IS NULL ");
			stmt.setString(1, socid );
			rset = stmt.executeQuery();

			if ( rset.next() ) {
				account = rset.getString("ID");
			}
		} catch (Throwable t) {
			error("PlounzJoinProc.getPlounzAccount",t);
			throw t;
		} finally {
			try { if ( rset != null ) { rset.close(); } else {} } catch ( Throwable ignored) {}
			try { if ( stmt != null ) { stmt.close(); } else {} } catch ( Throwable ignored) {}
			try { if ( conn != null ) { conn.close(); } else {} } catch ( Throwable ignored) {}
		}
		return account;
	}

}
