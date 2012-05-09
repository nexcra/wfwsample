/*
 * 이 소스는 ㈜비씨카드 소유입니다.
 * 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
 * 작성 일자 : 2008. 01. 16 [bgwoo@intermajor.com]
 */
package com.bccard.golf.common;

import com.bccard.waf.core.AbstractEntity;

/**
 * 사용자 주소.
 * 
 * @author woozoo73
 * @version 2008. 01. 16
 */
public class UserAddress extends AbstractEntity {

	/** 자택 우편번호 */
	private String homeZipCode;

	/** 자택 주소1 */
	private String homeAddr1;

	/** 자택 주소2 */
	private String homeAddr2;

	/** 직장 우편번호 */
	private String officeZipCode;

	/** 직장 주소1 */
	private String officeAddr1; 

	/** 직장 주소2 */
	private String officeAddr2;

	/**
	 * 유효한 자택 주소인지 판단한다.
	 * 
	 * @return 유효한 경우 true, 그렇지 않으면 fasle
	 */
	public boolean isValidHomeAddress() {
		return isValidAddress(getHomeZipCode1(), getHomeZipCode2(), homeAddr1, homeAddr2);
	}

	/**
	 * 유효한 직장 주소인지 판단한다.
	 * 
	 * @return 유효한 경우 true, 그렇지 않으면 fasle
	 */
	public boolean isValidOfficeAddress() {
		return isValidAddress(getOfficeZipCode1(), getOfficeZipCode2(), officeAddr1, officeAddr2);
	}

	/**
	 * 유효한 주소인지 판단한다.
	 * 
	 * @param zipCode1 우편번호1
	 * @param zipCode2 우편번호2
	 * @param addr1 주소1
	 * @param addr2 주소1
	 * @return 유효한 경우 true, 그렇지 않으면 false;
	 */
	protected boolean isValidAddress(String zipCode1, String zipCode2, String addr1, String addr2) {
		if (zipCode1 == null
				|| zipCode1.length() != 3
				|| zipCode2 == null
				|| zipCode2.length() != 3
				|| addr1 == null
				|| addr2 == null) {
			return false;
		}

		try {
			Integer.parseInt(zipCode1);
			Integer.parseInt(zipCode2);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}

	/**
	 * 자택 우편번호1을 반환한다.
	 * 
	 * @return 자택 우편번호1
	 */
	public String getHomeZipCode1() {
		if (homeZipCode != null && homeZipCode.length() == 7) {
			return homeZipCode.substring(0, 3);
		}

		return null;
	}

	/**
	 * 자택 우편번호2를 반환한다.
	 * 
	 * @return 자택 우편번호2
	 */
	public String getHomeZipCode2() {
		if (homeZipCode != null && homeZipCode.length() == 7) {
			return homeZipCode.substring(4);
		}

		return null;
	}

	/**
	 * 직장 우편번호1을 반환한다.
	 * 
	 * @return 직장 우편번호1
	 */
	public String getOfficeZipCode1() {
		if (officeZipCode != null && officeZipCode.length() == 7) {
			return officeZipCode.substring(0, 3);
		}

		return null;
	}

	/**
	 * 직장 우편번호2를 반환한다.
	 * 
	 * @return 직장 우편번호2
	 */
	public String getOfficeZipCode2() {
		if (officeZipCode != null && officeZipCode.length() == 7) {
			return officeZipCode.substring(4);
		}

		return null;
	}

	/**
	 * 자택 주소1을 반환한다.
	 * 
	 * @return 자택 주소1
	 */
	public String getHomeAddr1() {
		return homeAddr1;
	}

	/**
	 * 자택 주소1을 설정한다.
	 * 
	 * @param homeAddr1 자택 주소1
	 */
	public void setHomeAddr1(String homeAddr1) {
		this.homeAddr1 = homeAddr1;
	}

	/**
	 * 자택 주소2를 반환한다.
	 * 
	 * @return 자택 주소2
	 */
	public String getHomeAddr2() {
		return homeAddr2;
	}

	/**
	 * 자택 주소2를 설정한다.
	 * 
	 * @param homeAddr2 자택 주소2
	 */
	public void setHomeAddr2(String homeAddr2) {
		this.homeAddr2 = homeAddr2;
	}

	/**
	 * 자택 우편번호를 반환한다.
	 * 
	 * @return 자택 우편번호
	 */
	public String getHomeZipCode() {
		return homeZipCode;
	}

	/**
	 * 자택 우편번호를 설정한다.
	 * 
	 * @param homeZipCode 자택 우편번호
	 */
	public void setHomeZipCode(String homeZipCode) {
		this.homeZipCode = homeZipCode;
	}

	/**
	 * 회사 주소1을 반환한다.
	 * 
	 * @return 회사 주소1
	 */
	public String getOfficeAddr1() {
		return officeAddr1;
	}

	/**
	 * 회사 주소1을 설정한다.
	 * 
	 * @param officeAddr1 회사 주소1
	 */
	public void setOfficeAddr1(String officeAddr1) {
		this.officeAddr1 = officeAddr1;
	}

	/**
	 * 회사 주소2를 반환한다.
	 * 
	 * @return 회사 주소2
	 */
	public String getOfficeAddr2() {
		return officeAddr2;
	}

	/**
	 * 회사 주소2를 설정한다.
	 * 
	 * @param officeAddr2 회사 주소2
	 */
	public void setOfficeAddr2(String officeAddr2) {
		this.officeAddr2 = officeAddr2;
	}

	/**
	 * 회사 우편번호를 반환한다.
	 * 
	 * @return 회사 우편번호
	 */
	public String getOfficeZipCode() {
		return officeZipCode;
	}

	/**
	 * 회사 우편번호를 설정한다.
	 * 
	 * @param officeZipCode 회사 우편번호
	 */
	public void setOfficeZipCode(String officeZipCode) {
		this.officeZipCode = officeZipCode;
	}

}
