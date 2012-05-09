/*
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 작성 일자 : 2008. 01. 21 [bgwoo@intermajor.com]
 */
package com.bccard.golf.user.entity;

import com.bccard.golf.common.SearchEntity;
import com.bccard.golf.common.StringUtil;
import com.bccard.golf.common.UserAddress;

/**
 * 사용자 정보
 * 
 * @author woozoo73
 * @version 2008. 01. 21
 */
public class UcusrinfoEntity extends SearchEntity {

	private int memid;

	private String account;

	private String siteClss;

	private String passwd;

	private String passwdq;
 
	private String passwda;

	private String socid;

	private String name;

	private String ename;

	private String email1;

	private String mailing;

	private String zipcode;

	private String zipaddr;

	private String detailaddr;

	private String mobile;

	private String job;

	private String phone;

	private String recommAccount;

	private String jobtype;

	private String sex;

	private String solar;

	private String birth;

	private String wedyn;

	private String wedanniv;

	private String regdate;

	private String lastaccess;

	private int logcount;

	private String loghost;

	private String memTp;

	private String chkname;

	private String bizregno;

	private String rprsNm;

	private String chargeNm;

	private String dept;

	private String postn;

	private String memberClss;

	private String identifed;

	private String hPasswd;

	private String ePasswd;

	private String reghost;

	private String mailOpenClss;

	private String pswdRegDate;

	private int pswderrCnt;

	private String cardClss;

	private String admiCtyNo;

	private int xAxis;

	private int yAxis;

	private String recvYn;

	private int wkplXAxis;

	private int wkplYAxis;

	private String emailCorrDate;
	
	/** TOP 포인트 */
	private int bcTopPoint;
	
	/** 사용자 주소 */
	private UserAddress userAddress;
	
	/** 사용자 회원등급 */ 
	private String memGrade;
	private int intMemGrade;
	
	/** 멤버쉽 회원등급 */ 
	private int intMemberGrade;
	
	/** 카드 회원등급 */ 
	private int intCardGrade;
	 
	/** 사이버머니 */
	private int cyberMoney;
	
	/** 정회원 카드로 체크 **/
	private String memberClssCard;
	
	/** 법인회원 사업자등록번호 **/
	private String strCoNumber;
	
	/** 개인회원 / 법인회원 체크 ( 1:정회원 / 4: 비회원 / 5:법인회원 ) **/
	private String strMemChkNum;
	
	/** 법인플랫폼 통해서 들어왔는지 여부 (Y) **/
	private String strEnterCorporation;

	/** 법인플랫폼 통해서 들어올때 MEM_ID값 **/
	private String strEnterCorporationMemId;
	
	/** 법인테이블 카드담당구분 회원등급 */ 
	private String strCoMemType;

	/** 법인플랫폼 통해서 들어올때 Account_ID값 */ 
	private String strEnterCorporationAccountId;

	/** i-PIN 회원용 */ 
	private String ipindi_val;	
	private String vrtl_jumin_no;
	
	//주소구분 1:구주소, 2: 새주소
	private String nwOldAddrClss;
	
	//새주소 동이상
	private String dongOvrNewAddr;
	
	//새주소 동이하
	private String dongBlwNewAddr;		
	
	
	/**
	 * @param 법인플랫폼 통해서 들어왔는지 여부 저장
	 */
	public void setStrCoMemType(String strCoMemType) {
		this.strCoMemType = strCoMemType;
	} 
	/**
	 * @return 법인플랫폼 통해서 들어왔는지 여부 가져오기
	 */
	public String getStrCoMemType() {
		return strCoMemType;
	}
	
	
	
	
	
	/**
	 * @param 법인플랫폼 통해서 들어올때 Account_ID값 저장
	 */
	public void setStrEnterCorporationAccountId(String StrEnterCorporationAccountId) {
		this.strEnterCorporationAccountId = StrEnterCorporationAccountId;
	} 
	/**
	 * @return 법인플랫폼 통해서 들어올때 Account_ID값 가져오기
	 */
	public String getStrEnterCorporationAccountId() {
		return strEnterCorporationAccountId;
	}
	
	/**
	 * @param 법인플랫폼 통해서 들어왔는지 여부 저장
	 */
	public void setStrEnterCorporation(String strEnterCorporation) {
		this.strEnterCorporation = strEnterCorporation;
	} 
	/**
	 * @return 법인플랫폼 통해서 들어왔는지 여부 가져오기
	 */
	public String getStrEnterCorporation() {
		return strEnterCorporation;
	}

	/**
	 * @param 법인플랫폼 통해서 들어왔는지 여부 저장
	 */
	public void setStrEnterCorporationMemId(String strEnterCorporationMemId) {
		this.strEnterCorporationMemId = strEnterCorporationMemId;
	}
	/**
	 * @return 법인플랫폼 통해서 들어왔는지 여부 가져오기
	 */
	public String getStrEnterCorporationMemId() {
		return strEnterCorporationMemId;
	}
	
	/**
	 * @param 개인회원 / 법인회원 체크 저장
	 */
	public void setStrMemChkNum(String strMemChkNum) {
		this.strMemChkNum = strMemChkNum;
	}
	/**
	 * @return 개인회원 / 법인회원 체크 가져오기
	 */
	public String getStrMemChkNum() {
		return strMemChkNum;
	}
		
	/**
	 * @param 법인회원 사업자등록번호 저장
	 */
	public void setStrCoNum(String strCoNumber) {
		this.strCoNumber = strCoNumber;
	}
	/**
	 * @return 법인회원 사업자등록번호 가져오기
	 */
	public String getStrCoNum() {
		return strCoNumber;
	}
	

	
	/**
	 * @return
	 */
	public int getIntMemberGrade() {
		return intMemberGrade;
	}
	/**
	 * @return
	 */
	public int getIntCardGrade() {
		return intCardGrade;
	}
	/**
	 * @param 멤버쉽 등급저장
	 */
	public void setIntMemberGrade(int intMemberGrade) {
		this.intMemberGrade = intMemberGrade;
	}
	/**
	 * @param 카드 등급저장
	 */
	public void setIntCardGrade(int intCardGrade) {
		this.intCardGrade = intCardGrade;
	}
	/**
	 * @return 
	 */
	public String getmemberClssCard() {
		return memberClssCard;
	}
	/**
	 * @param memberClssCard
	 */
	public void setmemberClssCard(String memberClssCard) {
		this.memberClssCard = memberClssCard;
	}
	/**
	 * 자택 주소가 유효한지 판단한다.
	 * 
	 * @return 자택 주소가 유효한 경우 true, 그렇지 않으면 false
	 */
	public boolean isValidHomeAddress() {
		if (userAddress == null) {
			return false;
		}
		
		return userAddress.isValidHomeAddress();
	}
	
	/**
	 * 직장 주소가 유효한지 판단한다.
	 * 
	 * @return 직장 주소가 유효한 경우 true, 그렇지 않으면 false
	 */
	public boolean getValidOfficeAddress() {
		if (userAddress == null) {
			return false;
		}
		
		return userAddress.isValidOfficeAddress();
	}
	
	/**
	 * @return
	 */
	public int getBcTopPoint() {
		return bcTopPoint;
	}

	/**
	 * @param bcTopPoint
	 */
	public void setBcTopPoint(int bcTopPoint) {
		this.bcTopPoint = bcTopPoint;
	}

	/**
	 * @return
	 */
	public UserAddress getUserAddress() {
		return userAddress;
	}

	/**
	 * @param userAddress
	 */
	public void setUserAddress(UserAddress userAddress) {
		this.userAddress = userAddress;
	}

	/**
	 * @return
	 */
	public String getMobile1() {
		if (mobile != null) {
			String[] mobiles = StringUtil.stringToArray(mobile, "-");
			if (mobiles != null && mobiles.length == 3) {
				return mobiles[0];
			}
		}

		return null;
	}
	
	/**
	 * @return
	 */
	public String getMobile2() {
		if (mobile != null) {
			String[] mobiles = StringUtil.stringToArray(mobile, "-");
			if (mobiles != null && mobiles.length == 3) {
				return mobiles[1];
			}
		}

		return null;
	}
	
	/**
	 * @return
	 */
	public String getMobile3() {
		if (mobile != null) {
			String[] mobiles = StringUtil.stringToArray(mobile, "-");
			if (mobiles != null && mobiles.length == 3) {
				return mobiles[2];
			}
		}

		return null;
	}
	
	/**
	 * @return
	 */
	public String getPhone1() {
		if (phone != null) {
			String[] phones = StringUtil.stringToArray(phone, "-");
			if (phones != null && phones.length == 3) {
				return phones[0];
			}
		}

		return null;
	}
	
	/**
	 * @return
	 */
	public String getPhone2() {
		if (phone != null) {
			String[] phones = StringUtil.stringToArray(phone, "-");
			if (phones != null && phones.length == 3) {
				return phones[1];
			}
		}

		return null;
	}
	
	/**
	 * @return
	 */
	public String getPhone3() {
		if (phone != null) {
			String[] phones = StringUtil.stringToArray(phone, "-");
			if (phones != null && phones.length == 3) {
				return phones[2];
			}
		}

		return null;
	}
	
	/**
	 * @return
	 */
	public String getAccount() {
		return account;
	}

	/**
	 * @param account
	 */
	public void setAccount(String account) {
		this.account = account;
	}

	/**
	 * @return
	 */
	public String getAdmiCtyNo() {
		return admiCtyNo;
	}

	/**
	 * @param admiCtyNo
	 */
	public void setAdmiCtyNo(String admiCtyNo) {
		this.admiCtyNo = admiCtyNo;
	}

	/**
	 * @return
	 */
	public String getBirth() {
		return birth;
	}

	/**
	 * @param birth
	 */
	public void setBirth(String birth) {
		this.birth = birth;
	}

	/**
	 * @return
	 */
	public String getBizregno() {
		return bizregno;
	}

	/**
	 * @param bizregno
	 */
	public void setBizregno(String bizregno) {
		this.bizregno = bizregno;
	}

	/**
	 * @return
	 */
	public String getCardClss() {
		return cardClss;
	}

	/**
	 * @param cardClss
	 */
	public void setCardClss(String cardClss) {
		this.cardClss = cardClss;
	}

	/**
	 * @return
	 */
	public String getChargeNm() {
		return chargeNm;
	}

	/**
	 * @param chargeNm
	 */
	public void setChargeNm(String chargeNm) {
		this.chargeNm = chargeNm;
	}

	/**
	 * @return
	 */
	public String getChkname() {
		return chkname;
	}

	/**
	 * @param chkname
	 */
	public void setChkname(String chkname) {
		this.chkname = chkname;
	}

	/**
	 * @return
	 */
	public String getDept() {
		return dept;
	}

	/**
	 * @param dept
	 */
	public void setDept(String dept) {
		this.dept = dept;
	}

	/**
	 * @return
	 */
	public String getDetailaddr() {
		return detailaddr;
	}

	/**
	 * @param detailaddr
	 */
	public void setDetailaddr(String detailaddr) {
		this.detailaddr = detailaddr;
	}

	/**
	 * @return
	 */
	public String getEmail1() {
		return email1;
	}

	/**
	 * @param email1
	 */
	public void setEmail1(String email1) {
		this.email1 = email1;
	}

	/**
	 * @return
	 */
	public String getEmailCorrDate() {
		return emailCorrDate;
	}

	/**
	 * @param emailCorrDate
	 */
	public void setEmailCorrDate(String emailCorrDate) {
		this.emailCorrDate = emailCorrDate;
	}

	/**
	 * @return
	 */
	public String getEname() {
		return ename;
	}

	/**
	 * @param ename
	 */
	public void setEname(String ename) {
		this.ename = ename;
	}

	/**
	 * @return
	 */
	public String getEPasswd() {
		return ePasswd;
	}

	/**
	 * @param passwd
	 */
	public void setEPasswd(String passwd) {
		ePasswd = passwd;
	}

	/**
	 * @return
	 */
	public String getHPasswd() {
		return hPasswd;
	}

	/**
	 * @param passwd
	 */
	public void setHPasswd(String passwd) {
		hPasswd = passwd;
	}

	/**
	 * @return
	 */
	public String getIdentifed() {
		return identifed;
	}

	/**
	 * @param identifed
	 */
	public void setIdentifed(String identifed) {
		this.identifed = identifed;
	}

	/**
	 * @return
	 */
	public String getJob() {
		return job;
	}

	/**
	 * @param job
	 */
	public void setJob(String job) {
		this.job = job;
	}

	/**
	 * @return
	 */
	public String getJobtype() {
		return jobtype;
	}

	/**
	 * @param jobtype
	 */
	public void setJobtype(String jobtype) {
		this.jobtype = jobtype;
	}

	/**
	 * @return
	 */
	public String getLastaccess() {
		return lastaccess;
	}

	/**
	 * @param lastaccess
	 */
	public void setLastaccess(String lastaccess) {
		this.lastaccess = lastaccess;
	}

	/**
	 * @return
	 */
	public int getLogcount() {
		return logcount;
	}

	/**
	 * @param logcount
	 */
	public void setLogcount(int logcount) {
		this.logcount = logcount;
	}

	/**
	 * @return
	 */
	public String getLoghost() {
		return loghost;
	}

	/**
	 * @param loghost
	 */
	public void setLoghost(String loghost) {
		this.loghost = loghost;
	}
	
	/**
	 * @param 멤버
	 */
	public void setMemGrade(String memGrade) {
		this.memGrade = memGrade;
	}
	
	/**
	 * @param 멤버
	 */
	public void setIntMemGrade(int intMemGrade) {
		this.intMemGrade = intMemGrade;
	}
	
	/**
	 * @param 사이버머니
	 */
	public void setCyberMoney(int cyberMoney) {
		this.cyberMoney = cyberMoney;
	}

	/**
	 * @return
	 */
	public String getMailing() {
		return mailing;
	}

	/**
	 * @param mailing
	 */
	public void setMailing(String mailing) {
		this.mailing = mailing;
	}

	/**
	 * @return
	 */
	public String getMailOpenClss() {
		return mailOpenClss;
	}

	/**
	 * @param mailOpenClss
	 */
	public void setMailOpenClss(String mailOpenClss) {
		this.mailOpenClss = mailOpenClss;
	}

	/**
	 * @return
	 */
	public String getMemTp() {
		return memTp;
	}

	/**
	 * @param memTp
	 */
	public void setMemTp(String memTp) {
		this.memTp = memTp;
	}

	/**
	 * @return
	 */
	public String getMemberClss() {
		return memberClss;
	}

	/**
	 * @param memberClss
	 */
	public void setMemberClss(String memberClss) {
		this.memberClss = memberClss;
	}

	/**
	 * @return
	 */
	public int getMemid() {
		return memid;
	}

	/**
	 * @param memid
	 */
	public void setMemid(int memid) {
		this.memid = memid;
	}

	/**
	 * @return
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return
	 */
	public String getPasswd() {
		return passwd;
	}

	/**
	 * @param passwd
	 */
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	/**
	 * @return
	 */
	public String getPasswda() {
		return passwda;
	}

	/**
	 * @param passwda
	 */
	public void setPasswda(String passwda) {
		this.passwda = passwda;
	}

	/**
	 * @return
	 */
	public String getPasswdq() {
		return passwdq;
	}

	/**
	 * @param passwdq
	 */
	public void setPasswdq(String passwdq) {
		this.passwdq = passwdq;
	}

	/**
	 * @return
	 */
	public String getPhone() {
		return phone;
	}

	/**
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/**
	 * @return
	 */
	public String getPostn() {
		return postn;
	}

	/**
	 * @param postn
	 */
	public void setPostn(String postn) {
		this.postn = postn;
	}

	/**
	 * @return
	 */
	public int getPswderrCnt() {
		return pswderrCnt;
	}

	/**
	 * @param pswderrCnt
	 */
	public void setPswderrCnt(int pswderrCnt) {
		this.pswderrCnt = pswderrCnt;
	}

	/**
	 * @return
	 */
	public String getPswdRegDate() {
		return pswdRegDate;
	}

	/**
	 * @param pswdRegDate
	 */
	public void setPswdRegDate(String pswdRegDate) {
		this.pswdRegDate = pswdRegDate;
	}

	/**
	 * @return
	 */
	public String getRecommAccount() {
		return recommAccount;
	}

	/**
	 * @param recommAccount
	 */
	public void setRecommAccount(String recommAccount) {
		this.recommAccount = recommAccount;
	}

	/**
	 * @return
	 */
	public String getRecvYn() {
		return recvYn;
	}

	/**
	 * @param recvYn
	 */
	public void setRecvYn(String recvYn) {
		this.recvYn = recvYn;
	}

	/**
	 * @return
	 */
	public String getRegdate() {
		return regdate;
	}

	/**
	 * @param regdate
	 */
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	/**
	 * @return
	 */
	public String getReghost() {
		return reghost;
	}

	/**
	 * @param reghost
	 */
	public void setReghost(String reghost) {
		this.reghost = reghost;
	}

	/**
	 * @return
	 */
	public String getRprsNm() {
		return rprsNm;
	}

	/**
	 * @param rprsNm
	 */
	public void setRprsNm(String rprsNm) {
		this.rprsNm = rprsNm;
	}

	/**
	 * @return
	 */
	public String getSex() {
		return sex;
	}

	/**
	 * @param sex
	 */
	public void setSex(String sex) {
		this.sex = sex;
	}

	/**
	 * @return
	 */
	public String getSiteClss() {
		return siteClss;
	}

	/**
	 * @param siteClss
	 */
	public void setSiteClss(String siteClss) {
		this.siteClss = siteClss;
	}

	/**
	 * @return
	 */
	public String getSocid() {
		return socid;
	}

	/**
	 * @param socid
	 */
	public void setSocid(String socid) {
		this.socid = socid;
	}

	/**
	 * @return
	 */
	public String getSolar() {
		return solar;
	}

	/**
	 * @param solar
	 */
	public void setSolar(String solar) {
		this.solar = solar;
	}

	/**
	 * @return
	 */
	public String getWedanniv() {
		return wedanniv;
	}

	/**
	 * @param wedanniv
	 */
	public void setWedanniv(String wedanniv) {
		this.wedanniv = wedanniv;
	}

	/**
	 * @return
	 */
	public String getWedyn() {
		return wedyn;
	}

	/**
	 * @param wedyn
	 */
	public void setWedyn(String wedyn) {
		this.wedyn = wedyn;
	}

	/**
	 * @return
	 */
	public int getWkplXAxis() {
		return wkplXAxis;
	}

	/**
	 * @param wkplXAxis
	 */
	public void setWkplXAxis(int wkplXAxis) {
		this.wkplXAxis = wkplXAxis;
	}

	/**
	 * @return
	 */
	public int getWkplYAxis() {
		return wkplYAxis;
	}

	/**
	 * @param wkplYAxis
	 */
	public void setWkplYAxis(int wkplYAxis) {
		this.wkplYAxis = wkplYAxis;
	}

	/**
	 * @return
	 */
	public int getXAxis() {
		return xAxis;
	}

	/**
	 * @param axis
	 */
	public void setXAxis(int axis) {
		xAxis = axis;
	}

	/**
	 * @return
	 */
	public int getYAxis() {
		return yAxis;
	}

	/**
	 * @param axis
	 */
	public void setYAxis(int axis) {
		yAxis = axis;
	}

	/**
	 * @return
	 */
	public String getZipaddr() {
		return zipaddr;
	}

	/**
	 * @param zipaddr
	 */
	public void setZipaddr(String zipaddr) {
		this.zipaddr = zipaddr;
	}

	/**
	 * @return
	 */
	public String getZipcode() {
		return zipcode;
	}

	/**
	 * @return
	 */
	public String getMemGrade() {
		return memGrade;
	}
	
	/**
	 * @return
	 */
	public int getIntMemGrade() {
		return intMemGrade;
	}
	
	/**
	 * @return
	 */
	public int getCyberMoney() {
		return cyberMoney;
	}

	/**
	 * @param zipcode
	 */
	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	/**
	 * @param ipindi_val
	 */
	public void setIpindiVal(String ipindi_val) {
		this.ipindi_val = ipindi_val;
	}
	/**
	 * @param ipindi_val
	 */
	public String getIpindiVal() {
		return ipindi_val;
	}
	/**
	 * @param vrtl_jumin_no
	 */
	public void setVrtlJuminNo(String vrtl_jumin_no) {
		this.vrtl_jumin_no = vrtl_jumin_no;
	}
	/**
	 * @param ipindi_val
	 */
	public String getVrtlJuminNo() {
		return vrtl_jumin_no;
	}
	
	/**
	 * @param nwOldAddrClss
	 */
	public void setNwOldAddrClss(String nwOldAddrClss) {
		this.nwOldAddrClss = nwOldAddrClss;
	}
	/**
	 * @return nwOldAddrClss
	 */
	public String getNwOldAddrClss() {
		return nwOldAddrClss;
	}
	
	/**
	 * @param dongOvrNewAddr
	 */
	public void setDongOvrNewAddr(String dongOvrNewAddr) {
		this.dongOvrNewAddr = dongOvrNewAddr;
	}
	/**
	 * @return dongOvrNewAddr
	 */
	public String getDongOvrNewAddr() {
		return dongOvrNewAddr;
	}	
	
	/**
	 * @param dongBlwNewAddr
	 */
	public void setDongBlwNewAddr(String dongBlwNewAddr) {
		this.dongBlwNewAddr = dongBlwNewAddr;
	}
	/**
	 * @return dongBlwNewAddr
	 */
	public String getDongBlwNewAddr() {
		return dongBlwNewAddr;
	}	
	
	
}

