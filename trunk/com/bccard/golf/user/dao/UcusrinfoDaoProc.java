/*
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 일자 : 2008. 01. 21 [bgwoo@intermajor.com]
*/
package com.bccard.golf.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/**
 * 사용자 정보 DAO.
 * 
 * @author woozoo73
 * @version 2008. 01. 21
 */
public class UcusrinfoDaoProc extends DbTaoProc {

	/**
	 * 계정으로 사용자 정보를 찾아 반환한다. 
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public UcusrinfoEntity selectByAccount(Connection con, String account) throws BaseException {
		
		UcusrinfoEntity ucusrinfo = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;

		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT");
		sb.append(" 	BMEM.MEMID,");
		sb.append(" 	BMEM.ACCOUNT,");
		sb.append(" 	BMEM.SITE_CLSS,");
		sb.append(" 	BMEM.PASSWD,");
		sb.append(" 	BMEM.PASSWDQ,");
		sb.append(" 	BMEM.PASSWDA,");
		sb.append(" 	BMEM.SOCID,");
		sb.append(" 	BMEM.NAME,");
		sb.append(" 	BMEM.ENAME,");
		sb.append(" 	BMEM.EMAIL1,");
		sb.append(" 	BMEM.MAILING,");
		sb.append(" 	BMEM.ZIPCODE,");
		sb.append(" 	NVL(BMEM.ZIPADDR, ' ')ZIPADDR,");
		sb.append(" 	NVL(BMEM.DETAILADDR, ' ')DETAILADDR,");
		sb.append(" 	BMEM.MOBILE,");
		sb.append(" 	BMEM.JOB,");
		sb.append(" 	BMEM.PHONE,");
		sb.append(" 	BMEM.RECOMM_ACCOUNT,");
		sb.append(" 	BMEM.JOBTYPE,");
		sb.append(" 	BMEM.SEX,");
		sb.append(" 	BMEM.SOLAR,");
		sb.append(" 	BMEM.BIRTH,");
		sb.append(" 	BMEM.WEDYN,");
		sb.append(" 	BMEM.WEDANNIV,");
		sb.append(" 	BMEM.REGDATE,");
		sb.append(" 	BMEM.LASTACCESS,");
		sb.append(" 	BMEM.LOGCOUNT,");
		sb.append(" 	BMEM.LOGHOST,");
		sb.append(" 	BMEM.MEM_TP,");
		sb.append(" 	BMEM.CHKNAME,");
		sb.append(" 	BMEM.BIZREGNO,");
		sb.append(" 	BMEM.RPRS_NM,");
		sb.append(" 	BMEM.CHARGE_NM,");
		sb.append(" 	BMEM.DEPT,");
		sb.append(" 	BMEM.POSTN,");
		sb.append(" 	BMEM.MEMBER_CLSS,");
		sb.append(" 	BMEM.IDENTIFED,");
		sb.append(" 	BMEM.H_PASSWD,");
		sb.append(" 	BMEM.E_PASSWD,");
		sb.append(" 	BMEM.REGHOST,");
		sb.append(" 	BMEM.MAIL_OPEN_CLSS,");
		sb.append(" 	BMEM.PSWD_REG_DATE,");
		sb.append(" 	BMEM.PSWDERR_CNT,");
		sb.append(" 	BMEM.CARD_CLSS,");
		sb.append(" 	BMEM.ADMI_CTY_NO,");
		sb.append(" 	BMEM.X_AXIS,");
		sb.append(" 	BMEM.Y_AXIS,");
		sb.append(" 	BMEM.RECV_YN,");
		sb.append(" 	BMEM.WKPL_X_AXIS,");
		sb.append(" 	BMEM.WKPL_Y_AXIS,");
		sb.append(" 	BMEM.EMAIL_CORR_DATE,");
		sb.append(" 	NVL(BMEM.IPINDI_VAL, ' ') IPINDI_VAL,");
		sb.append(" 	GMEM.JUMIN_NO,");
		sb.append(" 	BMEM.VRTL_JUMIN_NO,");		
		sb.append(" 	NVL(BMEM.NW_OLD_ADDR_CLSS, ' ') NW_OLD_ADDR_CLSS,");
		sb.append(" 	NVL(BMEM.DONG_OVR_NEW_ADDR, ' ') DONG_OVR_NEW_ADDR,");
		sb.append(" 	NVL(BMEM.DONG_BLW_NEW_ADDR, ' ') DONG_BLW_NEW_ADDR");	
		sb.append(" FROM ");
		sb.append("	BCDBA.UCUSRINFO BMEM LEFT JOIN BCDBA.TBGGOLFCDHD GMEM ON BMEM.ACCOUNT=GMEM.CDHD_ID  ");
		sb.append(" WHERE");
		sb.append(" 	ACCOUNT = ?");
		String query = sb.toString();
		
		StringBuffer sbCo= new StringBuffer();
		sbCo.append(" SELECT");
		sbCo.append(" 	B.MEM_ID,");
		sbCo.append(" 	B.ACCOUNT,");
		sbCo.append(" 	B.USER_NM,");
		sbCo.append(" 	B.USER_JUMIN_NO,");
		sbCo.append(" 	B.PASSWD,");
		sbCo.append(" 	B.USER_EMAIL,");		
		sbCo.append(" 	B.USER_TEL_NO,");
		sbCo.append(" 	B.USER_MOB_NO,");		
		sbCo.append(" 	B.BUZ_NO,");
		sbCo.append(" 	D.MEM_CLSS,");
		sbCo.append(" 	B.MAIL_RCV_YN");		
		sbCo.append(" FROM ");
		sbCo.append("	BCDBA.TBENTPUSER B INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
		sbCo.append(" WHERE");
		sbCo.append(" B.ACCOUNT = ? AND D.MEM_STAT = '2' AND D.SEC_DATE is null ");
			
		String queryCo = sbCo.toString();
		
		try {
			pstmt = con.prepareStatement(query);

			int i = 1;

			pstmt.setString(i++, account);

			rs = pstmt.executeQuery();

			if (rs.next()) {
				
				if("5".equals(rs.getString("MEMBER_CLSS")))
				{
					
					pstmt = con.prepareStatement(queryCo);
					i = 1;
					pstmt.setString(i++, account);
					rs = pstmt.executeQuery();
					String coChk = "";
					if(rs != null) {			 
						
						while(rs.next())  {	
							
							if("6".equals(rs.getString("MEM_CLSS")))
							{
								coChk = rs.getString("MEM_CLSS");
								System.out.print("## UcusrinfoDaoProc | 법인 | TBENTPUSER에서 정보 가져와서 ett insert | selectByAccount | account :"+account+" | coChk : "+coChk+"\n");
								ucusrinfo = new UcusrinfoEntity();				
								ucusrinfo.setMemid(rs.getInt("MEM_ID"));	// 법인회원은 TBENTPUSER의 MEM_ID를 가져온다.
								ucusrinfo.setAccount(rs.getString("ACCOUNT"));
								ucusrinfo.setSiteClss("5");
								ucusrinfo.setPasswd(rs.getString("PASSWD"));
								ucusrinfo.setSocid(rs.getString("USER_JUMIN_NO"));
								ucusrinfo.setName(rs.getString("USER_NM"));
								ucusrinfo.setEmail1(rs.getString("USER_EMAIL"));
								ucusrinfo.setMailing(rs.getString("MAIL_RCV_YN"));				
								ucusrinfo.setMobile(rs.getString("USER_MOB_NO"));
								ucusrinfo.setPhone(rs.getString("USER_TEL_NO"));				
								ucusrinfo.setStrCoNum(rs.getString("BUZ_NO"));	
								ucusrinfo.setMemberClss("5");			
								ucusrinfo.setStrCoMemType(rs.getString("MEM_CLSS"));
								
								break;
							}
							else
							{
								if(!"6".equals(coChk))
								{
									coChk = rs.getString("MEM_CLSS");
									System.out.print("## UcusrinfoDaoProc | 법인 | TBENTPUSER에서 정보 가져와서 ett insert | selectByAccount | account :"+account+" | coChk : "+coChk+"\n");
									ucusrinfo = new UcusrinfoEntity();				
									ucusrinfo.setMemid(rs.getInt("MEM_ID"));	// 법인회원은 TBENTPUSER의 MEM_ID를 가져온다.
									ucusrinfo.setAccount(rs.getString("ACCOUNT"));
									ucusrinfo.setSiteClss("5");
									ucusrinfo.setPasswd(rs.getString("PASSWD"));
									ucusrinfo.setSocid(rs.getString("USER_JUMIN_NO"));
									ucusrinfo.setName(rs.getString("USER_NM"));
									ucusrinfo.setEmail1(rs.getString("USER_EMAIL"));
									ucusrinfo.setMailing(rs.getString("MAIL_RCV_YN"));				
									ucusrinfo.setMobile(rs.getString("USER_MOB_NO"));
									ucusrinfo.setPhone(rs.getString("USER_TEL_NO"));				
									ucusrinfo.setStrCoNum(rs.getString("BUZ_NO"));	
									ucusrinfo.setMemberClss("5");			
									ucusrinfo.setStrCoMemType(rs.getString("MEM_CLSS"));
									
								}
							}													
						}
					}					
				}
				else
				{
				
					System.out.print("## UcusrinfoDaoProc | 개인 | UCUSRINFO에서 정보 가져와서 ett insert | selectByAccount | account :"+account+"\n");
					
					String socid = "";
					if(!GolfUtil.empty(rs.getString("SOCID"))){
						socid = rs.getString("SOCID");
					}else{
						socid = rs.getString("JUMIN_NO");
					}
					
					ucusrinfo = new UcusrinfoEntity();				
					ucusrinfo.setMemid(rs.getInt("MEMID"));
					ucusrinfo.setAccount(rs.getString("ACCOUNT"));
					ucusrinfo.setSiteClss(rs.getString("SITE_CLSS"));
					ucusrinfo.setPasswd(rs.getString("PASSWD"));
					ucusrinfo.setPasswdq(rs.getString("PASSWDQ"));
					ucusrinfo.setPasswda(rs.getString("PASSWDA"));
					ucusrinfo.setSocid(socid);
					ucusrinfo.setName(rs.getString("NAME"));
					ucusrinfo.setEname(rs.getString("ENAME"));
					ucusrinfo.setEmail1(rs.getString("EMAIL1"));
					ucusrinfo.setMailing(rs.getString("MAILING"));
					ucusrinfo.setZipcode(rs.getString("ZIPCODE"));
					ucusrinfo.setZipaddr(rs.getString("ZIPADDR"));
					ucusrinfo.setDetailaddr(rs.getString("DETAILADDR"));
					ucusrinfo.setMobile(rs.getString("MOBILE"));
					ucusrinfo.setJob(rs.getString("JOB"));
					ucusrinfo.setPhone(rs.getString("PHONE"));
					ucusrinfo.setRecommAccount(rs.getString("RECOMM_ACCOUNT"));
					ucusrinfo.setJobtype(rs.getString("JOBTYPE"));
					ucusrinfo.setSex(rs.getString("SEX"));
					ucusrinfo.setSolar(rs.getString("SOLAR"));
					ucusrinfo.setBirth(rs.getString("BIRTH"));
					ucusrinfo.setWedyn(rs.getString("WEDYN"));
					ucusrinfo.setWedanniv(rs.getString("WEDANNIV"));
					ucusrinfo.setRegdate(rs.getString("REGDATE"));
					ucusrinfo.setLastaccess(rs.getString("LASTACCESS"));
					ucusrinfo.setLogcount(rs.getInt("LOGCOUNT"));
					ucusrinfo.setLoghost(rs.getString("LOGHOST"));
					ucusrinfo.setMemTp(rs.getString("MEM_TP"));
					ucusrinfo.setChkname(rs.getString("CHKNAME"));
					ucusrinfo.setBizregno(rs.getString("BIZREGNO"));
					ucusrinfo.setRprsNm(rs.getString("RPRS_NM"));
					ucusrinfo.setChargeNm(rs.getString("CHARGE_NM"));
					ucusrinfo.setDept(rs.getString("DEPT"));
					ucusrinfo.setPostn(rs.getString("POSTN"));
					ucusrinfo.setMemberClss(rs.getString("MEMBER_CLSS"));
					ucusrinfo.setIdentifed(rs.getString("IDENTIFED"));
					ucusrinfo.setHPasswd(rs.getString("H_PASSWD"));
					ucusrinfo.setEPasswd(rs.getString("E_PASSWD"));
					ucusrinfo.setReghost(rs.getString("REGHOST"));
					ucusrinfo.setMailOpenClss(rs.getString("MAIL_OPEN_CLSS"));
					ucusrinfo.setPswdRegDate(rs.getString("PSWD_REG_DATE"));
					ucusrinfo.setPswderrCnt(rs.getInt("PSWDERR_CNT"));
					ucusrinfo.setCardClss(rs.getString("CARD_CLSS"));
					ucusrinfo.setAdmiCtyNo(rs.getString("ADMI_CTY_NO"));
					ucusrinfo.setXAxis(rs.getInt("X_AXIS"));
					ucusrinfo.setYAxis(rs.getInt("Y_AXIS"));
					ucusrinfo.setRecvYn(rs.getString("RECV_YN"));
					ucusrinfo.setWkplXAxis(rs.getInt("WKPL_X_AXIS"));
					ucusrinfo.setWkplYAxis(rs.getInt("WKPL_Y_AXIS"));
					ucusrinfo.setEmailCorrDate(rs.getString("EMAIL_CORR_DATE"));
					ucusrinfo.setIpindiVal(rs.getString("IPINDI_VAL"));
					ucusrinfo.setVrtlJuminNo(rs.getString("VRTL_JUMIN_NO"));					
					ucusrinfo.setNwOldAddrClss(rs.getString("NW_OLD_ADDR_CLSS"));
					ucusrinfo.setDongOvrNewAddr(rs.getString("DONG_OVR_NEW_ADDR"));
					ucusrinfo.setDongBlwNewAddr(rs.getString("DONG_BLW_NEW_ADDR"));	
				}
			}
			
		} catch (SQLException e) {
			error(this.getClass().getName(), e);
			throw new BaseException("java.sql.SQLException", null, e);
		} finally {
			
			try {
				if (rs != null)
					rs.close();
			} catch (Throwable ignored) {
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Throwable ignored) {
			}
		}

		return ucusrinfo;
	}
	
	
	/**
	 * 계정으로 법인 정보를 찾아 반환한다.  | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public UcusrinfoEntity selectByCoAccount(Connection con, String account) throws BaseException {
		
		UcusrinfoEntity ucusrinfo = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;		

		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT");
		sb.append(" 	B.MEM_ID,");
		sb.append(" 	B.ACCOUNT,");
		sb.append(" 	B.USER_NM,");
		sb.append(" 	B.USER_JUMIN_NO,");
		sb.append(" 	B.PASSWD,");
		sb.append(" 	B.USER_EMAIL,");		
		sb.append(" 	B.USER_TEL_NO,");
		sb.append(" 	B.USER_MOB_NO,");		
		sb.append(" 	B.BUZ_NO,");
		sb.append(" 	B.MAIL_RCV_YN,");	
		sb.append(" 	D.MEM_CLSS");
		sb.append(" FROM ");
		sb.append("	BCDBA.TBENTPUSER B INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID");
		sb.append(" WHERE");
		sb.append(" 	B.ACCOUNT = ? AND D.MEM_STAT = '2' AND D.SEC_DATE is null ");

		String query = sb.toString();

		try {
			
			System.out.println("## UcusrinfoDaoProc | selectByCoAccount | 법인회원테이블 조회시작 | ID : "+account+"\n");
			
			pstmt = con.prepareStatement(query);
			int i = 1;
			pstmt.setString(i++, account);
			rs = pstmt.executeQuery();
			String coChk = "";
			if(rs != null) {			 
				
				while(rs.next())  {	
					
					if("6".equals(rs.getString("MEM_CLSS")))
					{
						coChk = rs.getString("MEM_CLSS");
						ucusrinfo = new UcusrinfoEntity();				
						ucusrinfo.setMemid(rs.getInt("MEM_ID"));	
						ucusrinfo.setAccount(rs.getString("ACCOUNT"));
						ucusrinfo.setSiteClss("5");
						ucusrinfo.setPasswd(rs.getString("PASSWD"));
						ucusrinfo.setSocid(rs.getString("USER_JUMIN_NO"));
						ucusrinfo.setName(rs.getString("USER_NM"));
						ucusrinfo.setEmail1(rs.getString("USER_EMAIL"));
						ucusrinfo.setMailing(rs.getString("MAIL_RCV_YN"));				
						ucusrinfo.setMobile(rs.getString("USER_MOB_NO"));
						ucusrinfo.setPhone(rs.getString("USER_TEL_NO"));				
						ucusrinfo.setStrCoNum(rs.getString("BUZ_NO"));	
						ucusrinfo.setMemberClss("5");			
						ucusrinfo.setStrCoMemType(rs.getString("MEM_CLSS"));				

						System.out.println("## UcusrinfoDaoProc | selectByCoAccount | 법인회원테이블 조회 성공 | ID : "+account+" | coChk : "+coChk+"\n");
						
						break;
					}
					else
					{
						if(!"6".equals(coChk))
						{
							coChk = rs.getString("MEM_CLSS");
							ucusrinfo = new UcusrinfoEntity();				
							ucusrinfo.setMemid(rs.getInt("MEM_ID"));	
							ucusrinfo.setAccount(rs.getString("ACCOUNT"));
							ucusrinfo.setSiteClss("5");
							ucusrinfo.setPasswd(rs.getString("PASSWD"));
							ucusrinfo.setSocid(rs.getString("USER_JUMIN_NO"));
							ucusrinfo.setName(rs.getString("USER_NM"));
							ucusrinfo.setEmail1(rs.getString("USER_EMAIL"));
							ucusrinfo.setMailing(rs.getString("MAIL_RCV_YN"));				
							ucusrinfo.setMobile(rs.getString("USER_MOB_NO"));
							ucusrinfo.setPhone(rs.getString("USER_TEL_NO"));				
							ucusrinfo.setStrCoNum(rs.getString("BUZ_NO"));	
							ucusrinfo.setMemberClss("5");			
							ucusrinfo.setStrCoMemType(rs.getString("MEM_CLSS"));				

							System.out.println("## UcusrinfoDaoProc | selectByCoAccount | 법인회원테이블 조회 성공 | ID : "+account+" | coChk : "+coChk+"\n");
							
						}
					}						
				}
			}			
			else
			{
				System.out.println("## UcusrinfoDaoProc | selectByCoAccount | 법인회원테이블 조회 실패| ID : "+account+"\n");
			}
		} catch (SQLException e) {
			error(this.getClass().getName(), e);
			throw new BaseException("java.sql.SQLException", null, e);
		} finally {
			
			try {
				if (rs != null)
					rs.close();
			} catch (Throwable ignored) {
			}
			try {
				if (pstmt != null)
					pstmt.close();
			} catch (Throwable ignored) {
			}
		}

		return ucusrinfo;
	}
	

	/**
	 * 플랫폼 계정으로 법인이면서 개인테이블에 존재하는 정보를 찾아 반환한다.  | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */	
	public UcusrinfoEntity selectByAccountCo(Connection con, String account) throws BaseException {
		
			UcusrinfoEntity ucusrinfo = null;

			PreparedStatement pstmt = null;
			ResultSet rs = null;

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.MEMID,");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	A.SITE_CLSS,");
			sb.append(" 	A.PASSWD,");
			sb.append(" 	A.PASSWDQ,");
			sb.append(" 	A.PASSWDA,");
			sb.append(" 	A.SOCID,");
			sb.append(" 	A.NAME,");
			sb.append(" 	A.ENAME,");
			sb.append(" 	A.EMAIL1,");
			sb.append(" 	A.MAILING,");
			sb.append(" 	A.ZIPCODE,");
			sb.append(" A.ZIPADDR,");
			sb.append(" A.DETAILADDR,");
			sb.append(" 	A.MOBILE,");
			sb.append(" 	A.JOB,");
			sb.append(" 	A.PHONE,");
			sb.append(" A.	RECOMM_ACCOUNT,");
			sb.append(" 	A.JOBTYPE,");
			sb.append(" A.	SEX,");
			sb.append(" 	A.SOLAR,");
			sb.append(" 	A.BIRTH,");
			sb.append(" 	A.WEDYN,");
			sb.append(" 	A.WEDANNIV,");
			sb.append(" A.	REGDATE,");
			sb.append(" 	A.LASTACCESS,");
			sb.append(" 	A.LOGCOUNT,");
			sb.append(" 	A.LOGHOST,");
			sb.append(" 	A.MEM_TP,");
			sb.append(" 	A.CHKNAME,");
			sb.append(" 	A.BIZREGNO,");
			sb.append(" 	A.RPRS_NM,");
			sb.append(" 	A.CHARGE_NM,");
			sb.append(" 	A.DEPT,");
			sb.append(" 	A.POSTN,");
			sb.append(" 	A.MEMBER_CLSS,");
			sb.append(" 	A.IDENTIFED,");
			sb.append(" 	A.H_PASSWD,");
			sb.append(" 	A.E_PASSWD,");
			sb.append(" 	A.REGHOST,");
			sb.append(" 	A.MAIL_OPEN_CLSS,");
			sb.append(" 	A.PSWD_REG_DATE,");
			sb.append(" 	A.PSWDERR_CNT,");
			sb.append(" 	A.CARD_CLSS,");
			sb.append(" 	A.ADMI_CTY_NO,");
			sb.append(" 	A.X_AXIS,");
			sb.append(" 	A.Y_AXIS,");
			sb.append(" 	A.RECV_YN,");
			sb.append(" 	A.WKPL_X_AXIS,");
			sb.append(" 	A.WKPL_Y_AXIS,");
			sb.append(" 	A.EMAIL_CORR_DATE,");
			sb.append(" 	D.MEM_CLSS,");
			
			sb.append(" 	B.MEM_ID,");	// 법인회원은 TBENTPUSER의 MEM_ID를 가져온다.
			sb.append(" 	B.USER_JUMIN_NO,");
			sb.append(" 	B.USER_NM,");
			sb.append(" 	B.USER_EMAIL,");
			sb.append(" 	B.MAIL_RCV_YN,");
			sb.append(" 	B.USER_MOB_NO,");
			sb.append(" 	B.USER_TEL_NO,");
			sb.append(" 	B.BUZ_NO");
			
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
			sb.append("	INNER JOIN BCDBA.TBGGOLFCDHD C ON A.ACCOUNT = C.CDHD_ID ");
			sb.append(" WHERE");
			sb.append(" 	B.MEM_ID = ? AND D.MEM_STAT = '2' AND D. SEC_DATE is null ");
			//AND D.MEM_CLSS = '6' 

			String query = sb.toString();

			try {
				
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				
				String coChk = "";
				if(rs != null) {			 
					
					while(rs.next())  {	
						
						if("6".equals(rs.getString("MEM_CLSS")))
						{
							coChk = rs.getString("MEM_CLSS");
							if("5".equals(rs.getString("MEMBER_CLSS")))
							{
									System.out.print("## UcusrinfoDaoProc | 법인 |  TBENTPUSER에서 정보 가져와서 ett insert | selectByAccountCo | account :"+account+" | coChk : "+coChk+" \n");					
									ucusrinfo = new UcusrinfoEntity();				
									ucusrinfo.setMemid(rs.getInt("MEM_ID"));
									ucusrinfo.setAccount(rs.getString("ACCOUNT"));
									ucusrinfo.setSiteClss("5");
									ucusrinfo.setPasswd(rs.getString("PASSWD"));
									ucusrinfo.setSocid(rs.getString("USER_JUMIN_NO"));
									ucusrinfo.setName(rs.getString("USER_NM"));
									ucusrinfo.setEmail1(rs.getString("USER_EMAIL"));
									ucusrinfo.setMailing(rs.getString("MAIL_RCV_YN"));				
									ucusrinfo.setMobile(rs.getString("USER_MOB_NO"));
									ucusrinfo.setPhone(rs.getString("USER_TEL_NO"));				
									ucusrinfo.setStrCoNum(rs.getString("BUZ_NO"));	
									ucusrinfo.setMemberClss("5");																	
									ucusrinfo.setStrCoMemType(rs.getString("MEM_CLSS"));
								
							}
							else
							{
							
							System.out.print("## UcusrinfoDaoProc | 개인 | UCUSRINFO에서 정보 가져와서 ett insert | selectByAccountCo | account :"+account+" | coChk : "+coChk+"\n");
							ucusrinfo = new UcusrinfoEntity();				
							ucusrinfo.setMemid(rs.getInt("MEMID"));
							ucusrinfo.setAccount(rs.getString("ACCOUNT"));
							ucusrinfo.setSiteClss(rs.getString("SITE_CLSS"));
							ucusrinfo.setPasswd(rs.getString("PASSWD"));
							ucusrinfo.setPasswdq(rs.getString("PASSWDQ"));
							ucusrinfo.setPasswda(rs.getString("PASSWDA"));
							ucusrinfo.setSocid(rs.getString("SOCID"));
							ucusrinfo.setName(rs.getString("NAME"));
							ucusrinfo.setEname(rs.getString("ENAME"));
							ucusrinfo.setEmail1(rs.getString("EMAIL1"));
							ucusrinfo.setMailing(rs.getString("MAILING"));
							ucusrinfo.setZipcode(rs.getString("ZIPCODE"));
							ucusrinfo.setZipaddr(rs.getString("ZIPADDR"));
							ucusrinfo.setDetailaddr(rs.getString("DETAILADDR"));
							ucusrinfo.setMobile(rs.getString("MOBILE"));
							ucusrinfo.setJob(rs.getString("JOB"));
							ucusrinfo.setPhone(rs.getString("PHONE"));
							ucusrinfo.setRecommAccount(rs.getString("RECOMM_ACCOUNT"));
							ucusrinfo.setJobtype(rs.getString("JOBTYPE"));
							ucusrinfo.setSex(rs.getString("SEX"));
							ucusrinfo.setSolar(rs.getString("SOLAR"));
							ucusrinfo.setBirth(rs.getString("BIRTH"));
							ucusrinfo.setWedyn(rs.getString("WEDYN"));
							ucusrinfo.setWedanniv(rs.getString("WEDANNIV"));
							ucusrinfo.setRegdate(rs.getString("REGDATE"));
							ucusrinfo.setLastaccess(rs.getString("LASTACCESS"));
							ucusrinfo.setLogcount(rs.getInt("LOGCOUNT"));
							ucusrinfo.setLoghost(rs.getString("LOGHOST"));
							ucusrinfo.setMemTp(rs.getString("MEM_TP"));
							ucusrinfo.setChkname(rs.getString("CHKNAME"));
							ucusrinfo.setBizregno(rs.getString("BIZREGNO"));
							ucusrinfo.setRprsNm(rs.getString("RPRS_NM"));
							ucusrinfo.setChargeNm(rs.getString("CHARGE_NM"));
							ucusrinfo.setDept(rs.getString("DEPT"));
							ucusrinfo.setPostn(rs.getString("POSTN"));
							ucusrinfo.setMemberClss(rs.getString("MEMBER_CLSS"));
							ucusrinfo.setIdentifed(rs.getString("IDENTIFED"));
							ucusrinfo.setHPasswd(rs.getString("H_PASSWD"));
							ucusrinfo.setEPasswd(rs.getString("E_PASSWD"));
							ucusrinfo.setReghost(rs.getString("REGHOST"));
							ucusrinfo.setMailOpenClss(rs.getString("MAIL_OPEN_CLSS"));
							ucusrinfo.setPswdRegDate(rs.getString("PSWD_REG_DATE"));
							ucusrinfo.setPswderrCnt(rs.getInt("PSWDERR_CNT"));
							ucusrinfo.setCardClss(rs.getString("CARD_CLSS"));
							ucusrinfo.setAdmiCtyNo(rs.getString("ADMI_CTY_NO"));
							ucusrinfo.setXAxis(rs.getInt("X_AXIS"));
							ucusrinfo.setYAxis(rs.getInt("Y_AXIS"));
							ucusrinfo.setRecvYn(rs.getString("RECV_YN"));
							ucusrinfo.setWkplXAxis(rs.getInt("WKPL_X_AXIS"));
							ucusrinfo.setWkplYAxis(rs.getInt("WKPL_Y_AXIS"));
							ucusrinfo.setEmailCorrDate(rs.getString("EMAIL_CORR_DATE"));
							ucusrinfo.setStrCoMemType(rs.getString("MEM_CLSS"));
							
							}				
							
							
							break;
						}
						else
						{
							if(!"6".equals(coChk))
							{
								coChk = rs.getString("MEM_CLSS");
								if("5".equals(rs.getString("MEMBER_CLSS")))
								{
										System.out.print("## UcusrinfoDaoProc | 법인 | TBENTPUSER에서 정보 가져와서 ett insert | selectByAccountCo | account :"+account+"\n");					
										ucusrinfo = new UcusrinfoEntity();				
										ucusrinfo.setMemid(rs.getInt("MEM_ID"));
										ucusrinfo.setAccount(rs.getString("ACCOUNT"));
										ucusrinfo.setSiteClss("5");
										ucusrinfo.setPasswd(rs.getString("PASSWD"));
										ucusrinfo.setSocid(rs.getString("USER_JUMIN_NO"));
										ucusrinfo.setName(rs.getString("USER_NM"));
										ucusrinfo.setEmail1(rs.getString("USER_EMAIL"));
										ucusrinfo.setMailing(rs.getString("MAIL_RCV_YN"));				
										ucusrinfo.setMobile(rs.getString("USER_MOB_NO"));
										ucusrinfo.setPhone(rs.getString("USER_TEL_NO"));				
										ucusrinfo.setStrCoNum(rs.getString("BUZ_NO"));	
										ucusrinfo.setMemberClss("5");																	
										ucusrinfo.setStrCoMemType(rs.getString("MEM_CLSS"));
									
								}
								else
								{
								
								System.out.print("## UcusrinfoDaoProc | 개인 | UCUSRINFO에서 정보 가져와서 ett insert | selectByAccountCo | account :"+account+"\n");
								ucusrinfo = new UcusrinfoEntity();				
								ucusrinfo.setMemid(rs.getInt("MEMID"));
								ucusrinfo.setAccount(rs.getString("ACCOUNT"));
								ucusrinfo.setSiteClss(rs.getString("SITE_CLSS"));
								ucusrinfo.setPasswd(rs.getString("PASSWD"));
								ucusrinfo.setPasswdq(rs.getString("PASSWDQ"));
								ucusrinfo.setPasswda(rs.getString("PASSWDA"));
								ucusrinfo.setSocid(rs.getString("SOCID"));
								ucusrinfo.setName(rs.getString("NAME"));
								ucusrinfo.setEname(rs.getString("ENAME"));
								ucusrinfo.setEmail1(rs.getString("EMAIL1"));
								ucusrinfo.setMailing(rs.getString("MAILING"));
								ucusrinfo.setZipcode(rs.getString("ZIPCODE"));
								ucusrinfo.setZipaddr(rs.getString("ZIPADDR"));
								ucusrinfo.setDetailaddr(rs.getString("DETAILADDR"));
								ucusrinfo.setMobile(rs.getString("MOBILE"));
								ucusrinfo.setJob(rs.getString("JOB"));
								ucusrinfo.setPhone(rs.getString("PHONE"));
								ucusrinfo.setRecommAccount(rs.getString("RECOMM_ACCOUNT"));
								ucusrinfo.setJobtype(rs.getString("JOBTYPE"));
								ucusrinfo.setSex(rs.getString("SEX"));
								ucusrinfo.setSolar(rs.getString("SOLAR"));
								ucusrinfo.setBirth(rs.getString("BIRTH"));
								ucusrinfo.setWedyn(rs.getString("WEDYN"));
								ucusrinfo.setWedanniv(rs.getString("WEDANNIV"));
								ucusrinfo.setRegdate(rs.getString("REGDATE"));
								ucusrinfo.setLastaccess(rs.getString("LASTACCESS"));
								ucusrinfo.setLogcount(rs.getInt("LOGCOUNT"));
								ucusrinfo.setLoghost(rs.getString("LOGHOST"));
								ucusrinfo.setMemTp(rs.getString("MEM_TP"));
								ucusrinfo.setChkname(rs.getString("CHKNAME"));
								ucusrinfo.setBizregno(rs.getString("BIZREGNO"));
								ucusrinfo.setRprsNm(rs.getString("RPRS_NM"));
								ucusrinfo.setChargeNm(rs.getString("CHARGE_NM"));
								ucusrinfo.setDept(rs.getString("DEPT"));
								ucusrinfo.setPostn(rs.getString("POSTN"));
								ucusrinfo.setMemberClss(rs.getString("MEMBER_CLSS"));
								ucusrinfo.setIdentifed(rs.getString("IDENTIFED"));
								ucusrinfo.setHPasswd(rs.getString("H_PASSWD"));
								ucusrinfo.setEPasswd(rs.getString("E_PASSWD"));
								ucusrinfo.setReghost(rs.getString("REGHOST"));
								ucusrinfo.setMailOpenClss(rs.getString("MAIL_OPEN_CLSS"));
								ucusrinfo.setPswdRegDate(rs.getString("PSWD_REG_DATE"));
								ucusrinfo.setPswderrCnt(rs.getInt("PSWDERR_CNT"));
								ucusrinfo.setCardClss(rs.getString("CARD_CLSS"));
								ucusrinfo.setAdmiCtyNo(rs.getString("ADMI_CTY_NO"));
								ucusrinfo.setXAxis(rs.getInt("X_AXIS"));
								ucusrinfo.setYAxis(rs.getInt("Y_AXIS"));
								ucusrinfo.setRecvYn(rs.getString("RECV_YN"));
								ucusrinfo.setWkplXAxis(rs.getInt("WKPL_X_AXIS"));
								ucusrinfo.setWkplYAxis(rs.getInt("WKPL_Y_AXIS"));
								ucusrinfo.setEmailCorrDate(rs.getString("EMAIL_CORR_DATE"));
								ucusrinfo.setStrCoMemType(rs.getString("MEM_CLSS"));
								
								}				

								System.out.println("## UcusrinfoDaoProc | selectByCoAccount | 법인회원테이블 조회 성공 | ID : "+account+" | coChk : "+coChk+"\n");
								
							}
						}
						
						
												
					}
				}
				
				
				
			} catch (SQLException e) {
				error(this.getClass().getName(), e);
				throw new BaseException("java.sql.SQLException", null, e);
			} finally {
				
				try {
					if (rs != null)
						rs.close();
				} catch (Throwable ignored) {
				}
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Throwable ignored) {
				}
			}

			return ucusrinfo;
		}
	
	
	/**
	 * 플랫폼 계정으로 법인인 정보를 찾아 반환한다.  | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public UcusrinfoEntity selectByAccountCot(Connection con, String account) throws BaseException {
			UcusrinfoEntity ucusrinfo = null;

			PreparedStatement pstmt = null;
			ResultSet rs = null;

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.MEMID,");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	A.SITE_CLSS,");
			sb.append(" 	A.PASSWD,");
			sb.append(" 	A.PASSWDQ,");
			sb.append(" 	A.PASSWDA,");
			sb.append(" 	A.SOCID,");
			sb.append(" 	A.NAME,");
			sb.append(" 	A.ENAME,");
			sb.append(" 	A.EMAIL1,");
			sb.append(" 	A.MAILING,");
			sb.append(" 	A.ZIPCODE,");
			sb.append(" 	A.ZIPADDR,");
			sb.append(" 	A.DETAILADDR,");
			sb.append(" 	A.MOBILE,");
			sb.append(" 	A.JOB,");
			sb.append(" 	A.PHONE,");
			sb.append(" 	A.RECOMM_ACCOUNT,");
			sb.append(" 	A.JOBTYPE,");
			sb.append(" 	A.SEX,");
			sb.append(" 	A.SOLAR,");
			sb.append(" 	A.BIRTH,");
			sb.append(" 	A.WEDYN,");
			sb.append(" 	A.WEDANNIV,");
			sb.append(" 	A.REGDATE,");
			sb.append(" 	A.LASTACCESS,");
			sb.append(" 	A.LOGCOUNT,");
			sb.append(" 	A.LOGHOST,");
			sb.append(" 	A.MEM_TP,");
			sb.append(" 	A.CHKNAME,");
			sb.append(" 	A.BIZREGNO,");
			sb.append(" 	A.RPRS_NM,");
			sb.append(" 	A.CHARGE_NM,");
			sb.append(" 	A.DEPT,");
			sb.append(" 	A.POSTN,");
			sb.append(" 	A.MEMBER_CLSS,");
			sb.append(" 	A.IDENTIFED,");
			sb.append(" 	A.H_PASSWD,");
			sb.append(" 	A.E_PASSWD,");
			sb.append(" 	A.REGHOST,");
			sb.append(" 	A.MAIL_OPEN_CLSS,");
			sb.append(" 	A.PSWD_REG_DATE,");
			sb.append(" 	A.PSWDERR_CNT,");
			sb.append(" 	A.CARD_CLSS,");
			sb.append(" 	A.ADMI_CTY_NO,");
			sb.append(" 	A.X_AXIS,");
			sb.append(" 	A.Y_AXIS,");
			sb.append(" 	A.RECV_YN,");
			sb.append(" 	A.WKPL_X_AXIS,");
			sb.append(" 	A.WKPL_Y_AXIS,");
			sb.append(" 	A.EMAIL_CORR_DATE,");
			sb.append(" 	D.MEM_CLSS,");
			sb.append(" 	B.MEM_ID");	// 법인회원은 TBENTPUSER의 MEM_ID를 가져온다.
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
			//sb.append("	INNER JOIN BCDBA.TBGGOLFCDHD C ON A.ACCOUNT = C.CDHD_ID ");
			sb.append(" WHERE");
			sb.append(" 	B.MEM_ID = ? AND D.MEM_STAT = '2' AND D.SEC_DATE is null ");
			//AND D.MEM_CLSS = '6' 

			String query = sb.toString();

			try {
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				if (rs.next()) {
					ucusrinfo = new UcusrinfoEntity();				
					ucusrinfo.setMemid(rs.getInt("MEM_ID"));	// 법인회원은 TBENTPUSER의 MEM_ID를 가져온다.
					ucusrinfo.setAccount(rs.getString("ACCOUNT"));
					ucusrinfo.setSiteClss(rs.getString("SITE_CLSS"));
					ucusrinfo.setPasswd(rs.getString("PASSWD"));
					ucusrinfo.setPasswdq(rs.getString("PASSWDQ"));
					ucusrinfo.setPasswda(rs.getString("PASSWDA"));
					ucusrinfo.setSocid(rs.getString("SOCID"));
					ucusrinfo.setName(rs.getString("NAME"));
					ucusrinfo.setEname(rs.getString("ENAME"));
					ucusrinfo.setEmail1(rs.getString("EMAIL1"));
					ucusrinfo.setMailing(rs.getString("MAILING"));
					ucusrinfo.setZipcode(rs.getString("ZIPCODE"));
					ucusrinfo.setZipaddr(rs.getString("ZIPADDR"));
					ucusrinfo.setDetailaddr(rs.getString("DETAILADDR"));
					ucusrinfo.setMobile(rs.getString("MOBILE"));
					ucusrinfo.setJob(rs.getString("JOB"));
					ucusrinfo.setPhone(rs.getString("PHONE"));
					ucusrinfo.setRecommAccount(rs.getString("RECOMM_ACCOUNT"));
					ucusrinfo.setJobtype(rs.getString("JOBTYPE"));
					ucusrinfo.setSex(rs.getString("SEX"));
					ucusrinfo.setSolar(rs.getString("SOLAR"));
					ucusrinfo.setBirth(rs.getString("BIRTH"));
					ucusrinfo.setWedyn(rs.getString("WEDYN"));
					ucusrinfo.setWedanniv(rs.getString("WEDANNIV"));
					ucusrinfo.setRegdate(rs.getString("REGDATE"));
					ucusrinfo.setLastaccess(rs.getString("LASTACCESS"));
					ucusrinfo.setLogcount(rs.getInt("LOGCOUNT"));
					ucusrinfo.setLoghost(rs.getString("LOGHOST"));
					ucusrinfo.setMemTp(rs.getString("MEM_TP"));
					ucusrinfo.setChkname(rs.getString("CHKNAME"));
					ucusrinfo.setBizregno(rs.getString("BIZREGNO"));
					ucusrinfo.setRprsNm(rs.getString("RPRS_NM"));
					ucusrinfo.setChargeNm(rs.getString("CHARGE_NM"));
					ucusrinfo.setDept(rs.getString("DEPT"));
					ucusrinfo.setPostn(rs.getString("POSTN"));
					ucusrinfo.setMemberClss(rs.getString("MEMBER_CLSS"));
					ucusrinfo.setIdentifed(rs.getString("IDENTIFED"));
					ucusrinfo.setHPasswd(rs.getString("H_PASSWD"));
					ucusrinfo.setEPasswd(rs.getString("E_PASSWD"));
					ucusrinfo.setReghost(rs.getString("REGHOST"));
					ucusrinfo.setMailOpenClss(rs.getString("MAIL_OPEN_CLSS"));
					ucusrinfo.setPswdRegDate(rs.getString("PSWD_REG_DATE"));
					ucusrinfo.setPswderrCnt(rs.getInt("PSWDERR_CNT"));
					ucusrinfo.setCardClss(rs.getString("CARD_CLSS"));
					ucusrinfo.setAdmiCtyNo(rs.getString("ADMI_CTY_NO"));
					ucusrinfo.setXAxis(rs.getInt("X_AXIS"));
					ucusrinfo.setYAxis(rs.getInt("Y_AXIS"));
					ucusrinfo.setRecvYn(rs.getString("RECV_YN"));
					ucusrinfo.setWkplXAxis(rs.getInt("WKPL_X_AXIS"));
					ucusrinfo.setWkplYAxis(rs.getInt("WKPL_Y_AXIS"));
					ucusrinfo.setEmailCorrDate(rs.getString("EMAIL_CORR_DATE"));
					ucusrinfo.setStrCoMemType(rs.getString("MEM_CLSS"));
					
					System.out.print("## UcusrinfoEntity selectByAccountCot : "+rs.getString("ACCOUNT")+"\n");
				}
			} catch (SQLException e) {
				error(this.getClass().getName(), e);
				throw new BaseException("java.sql.SQLException", null, e);
			} finally {
			
				try {
					if (rs != null)
						rs.close();
				} catch (Throwable ignored) {
				}
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Throwable ignored) {
				}
			}

			return ucusrinfo;
		}
	
	
	/**
	 * 플랫폼 계정으로 법인만 체크 | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public String selectByAccountCoChk(Connection con, String account) throws BaseException {
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	D.MEM_CLSS");
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
			sb.append(" WHERE");
			sb.append(" 	A.ACCOUNT = ? AND D.MEM_STAT = '2' AND D.SEC_DATE is null  ");

			String query = sb.toString();

			String coChk = "N";
			try {
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				if (rs.next()) {
					coChk = "Y";

				}
				
			} catch (SQLException e) {
				error(this.getClass().getName(), e);
				throw new BaseException("java.sql.SQLException", null, e);
			} finally {
				
				try {
					if (rs != null)
						rs.close();
				} catch (Throwable ignored) {
				}
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Throwable ignored) {
				}
			}

			return coChk;
		}
	/**
	 * ucusrinfo  의  member_clss  | 2010.11.08 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public String selectByCkClss(Connection con, String account) throws BaseException {
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.MEMBER_CLSS");
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  ");
			sb.append(" WHERE");
			sb.append(" 	A.ACCOUNT = ?  ");

			String query = sb.toString();

			String coChk = ""; 
			try {
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				if (rs.next()) {					
					coChk = rs.getString("MEMBER_CLSS");	
				}
				
			} catch (SQLException e) {
				error(this.getClass().getName(), e);
				throw new BaseException("java.sql.SQLException", null, e);
			} finally {
			
				try {
					if (rs != null)
						rs.close();
				} catch (Throwable ignored) {
				}
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Throwable ignored) {
				}
			}

			return coChk;
		}	

	/**
	 * 플랫폼 계정으로 법인만 체크 | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public String selectByCkCoMemClss(Connection con, String account) throws BaseException {
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	D.MEM_CLSS");
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT  ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID ");
			sb.append(" WHERE");
			sb.append(" 	A.ACCOUNT = ? AND D.MEM_STAT = '2' AND D.SEC_DATE IS NULL ");
			
			/*
			- 1건이상일 경우 (법인 정상회원)
			- 0건일경우 (법인 회원 아님. 해지회원 혹은 미승인 비정상회원)
			*/
			
			
			String query = sb.toString();

			String coChk = "N"; 
			try {
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();
				
				if(rs != null) {			 
					
					while(rs.next())  {	
						
						if("6".equals(rs.getString("MEM_CLSS")))
						{
							coChk = rs.getString("MEM_CLSS");
						}
						else
						{
							if(!"6".equals(coChk)) coChk = rs.getString("MEM_CLSS");
						}
						
						
												
					}
				}
				
				
				
				
				if (rs.next()) {
						
					//coChk = "Y";
				}
				
			} catch (SQLException e) {
				error(this.getClass().getName(), e);
				throw new BaseException("java.sql.SQLException", null, e);
			} finally {
			
				try {
					if (rs != null)
						rs.close();
				} catch (Throwable ignored) {
				}
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Throwable ignored) {
				}
			}

			return coChk;
		}
	
	/**
	 * 플랫폼 계정으로 법인만 체크 | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public String selectByCkNum(Connection con, String account) throws BaseException {
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	D.MEM_CLSS");
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
			sb.append(" WHERE");			
			sb.append(" 	A.ACCOUNT = ? AND D.MEM_STAT = '2' AND D.SEC_DATE IS NULL AND D.MEM_CLSS <> '6' AND D.MEM_CLSS <> '8' ");

			String query = sb.toString();

			String coChk = "N"; 
			try {
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				if (rs.next()) {
					coChk = "Y";
				}
				
			} catch (SQLException e) {
				error(this.getClass().getName(), e);
				throw new BaseException("java.sql.SQLException", null, e);
			} finally {
			
				try {
					if (rs != null)
						rs.close();
				} catch (Throwable ignored) {
				}
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Throwable ignored) {
				}
			}

			return coChk;
		}
	/**
	 * 플랫폼 계정으로 법인만 체크 | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public String selectBizNo(Connection con, String account) throws BaseException {
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	D.MEM_CLSS, B.BUZ_NO");
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
			sb.append(" WHERE");			
			sb.append(" 	A.ACCOUNT = ? AND D.MEM_STAT = '2' AND D.SEC_DATE IS NULL AND D.MEM_CLSS <> '6' AND D.MEM_CLSS <> '8' ");

			String query = sb.toString();

			String coChk = "N"; 
			try {
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				if (rs.next()) {
					coChk = rs.getString("BUZ_NO");
				}
				
			} catch (SQLException e) {
				error(this.getClass().getName(), e);
				throw new BaseException("java.sql.SQLException", null, e);
			} finally {
			
				try {
					if (rs != null)
						rs.close();
				} catch (Throwable ignored) {
				}
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Throwable ignored) {
				}
			}

			return coChk;
		}
	/**
	 * 플랫폼 계정으로 골프회원 / 법인 체크 | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public String selectPeByCkNum(Connection con, String account) throws BaseException {
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.CDHD_ID ");
			sb.append(" FROM ");
			sb.append("	BCDBA.TBGGOLFCDHD A  ");			
			sb.append(" WHERE");
			sb.append(" 	A.CDHD_ID = ?   ");

			String query = sb.toString();

			String coChk = "N";
			try {
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				if (rs.next()) {
															
					coChk = "Y";		//

				}
				
			} catch (SQLException e) {
				error(this.getClass().getName(), e);
				throw new BaseException("java.sql.SQLException", null, e);
			} finally {
			
				try {
					if (rs != null)
						rs.close();
				} catch (Throwable ignored) {
				}
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Throwable ignored) {
				}
			}

			return coChk;
		}
	/**
	 * 플랫폼 계정으로 법인만 체크 | 2009.10.29 | 권영만  
	 * 
	 * @param con 연결
	 * @param account 계정
	 * @return 사용자 정보
	 * @throws BaseException 예외가 발생하는 경우
	 */
	public String selectCoByCkNum(Connection con, String account) throws BaseException {
			 
			PreparedStatement pstmt = null;
			ResultSet rs = null;

			StringBuffer sb = new StringBuffer();
			sb.append(" SELECT");
			sb.append(" 	A.ACCOUNT,");
			sb.append(" 	D.MEM_CLSS");
			sb.append(" FROM ");
			sb.append("	BCDBA.UCUSRINFO A  INNER JOIN BCDBA.TBENTPUSER B ON A.ACCOUNT = B.ACCOUNT ");
			sb.append("	INNER JOIN BCDBA.TBENTPMEM D ON D.MEM_ID = B.MEM_ID	 ");
			sb.append(" WHERE");
			sb.append(" 	B.MEM_ID = ? AND D.MEM_STAT = '2' AND D.SEC_DATE is null  ");

			String query = sb.toString();

			String coChk = "";
			try {
				pstmt = con.prepareStatement(query);

				int i = 1;

				pstmt.setString(i++, account);

				rs = pstmt.executeQuery();

				if (rs.next()) {
					coChk = rs.getString("MEM_CLSS");

				}
				
			} catch (SQLException e) {
				error(this.getClass().getName(), e);
				throw new BaseException("java.sql.SQLException", null, e);
			} finally {
				
				try {
					if (rs != null)
						rs.close();
				} catch (Throwable ignored) {
				}
				try {
					if (pstmt != null)
						pstmt.close();
				} catch (Throwable ignored) {
				}
			}

			return coChk;
		}
	
	public TaoResult execute(Connection con, TaoDataSet dataSet)
			throws TaoException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
