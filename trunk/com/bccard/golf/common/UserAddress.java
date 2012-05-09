/*
 * �� �ҽ��� �ߺ�ī�� �����Դϴ�.
 * �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
 * �ۼ� ���� : 2008. 01. 16 [bgwoo@intermajor.com]
 */
package com.bccard.golf.common;

import com.bccard.waf.core.AbstractEntity;

/**
 * ����� �ּ�.
 * 
 * @author woozoo73
 * @version 2008. 01. 16
 */
public class UserAddress extends AbstractEntity {

	/** ���� �����ȣ */
	private String homeZipCode;

	/** ���� �ּ�1 */
	private String homeAddr1;

	/** ���� �ּ�2 */
	private String homeAddr2;

	/** ���� �����ȣ */
	private String officeZipCode;

	/** ���� �ּ�1 */
	private String officeAddr1; 

	/** ���� �ּ�2 */
	private String officeAddr2;

	/**
	 * ��ȿ�� ���� �ּ����� �Ǵ��Ѵ�.
	 * 
	 * @return ��ȿ�� ��� true, �׷��� ������ fasle
	 */
	public boolean isValidHomeAddress() {
		return isValidAddress(getHomeZipCode1(), getHomeZipCode2(), homeAddr1, homeAddr2);
	}

	/**
	 * ��ȿ�� ���� �ּ����� �Ǵ��Ѵ�.
	 * 
	 * @return ��ȿ�� ��� true, �׷��� ������ fasle
	 */
	public boolean isValidOfficeAddress() {
		return isValidAddress(getOfficeZipCode1(), getOfficeZipCode2(), officeAddr1, officeAddr2);
	}

	/**
	 * ��ȿ�� �ּ����� �Ǵ��Ѵ�.
	 * 
	 * @param zipCode1 �����ȣ1
	 * @param zipCode2 �����ȣ2
	 * @param addr1 �ּ�1
	 * @param addr2 �ּ�1
	 * @return ��ȿ�� ��� true, �׷��� ������ false;
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
	 * ���� �����ȣ1�� ��ȯ�Ѵ�.
	 * 
	 * @return ���� �����ȣ1
	 */
	public String getHomeZipCode1() {
		if (homeZipCode != null && homeZipCode.length() == 7) {
			return homeZipCode.substring(0, 3);
		}

		return null;
	}

	/**
	 * ���� �����ȣ2�� ��ȯ�Ѵ�.
	 * 
	 * @return ���� �����ȣ2
	 */
	public String getHomeZipCode2() {
		if (homeZipCode != null && homeZipCode.length() == 7) {
			return homeZipCode.substring(4);
		}

		return null;
	}

	/**
	 * ���� �����ȣ1�� ��ȯ�Ѵ�.
	 * 
	 * @return ���� �����ȣ1
	 */
	public String getOfficeZipCode1() {
		if (officeZipCode != null && officeZipCode.length() == 7) {
			return officeZipCode.substring(0, 3);
		}

		return null;
	}

	/**
	 * ���� �����ȣ2�� ��ȯ�Ѵ�.
	 * 
	 * @return ���� �����ȣ2
	 */
	public String getOfficeZipCode2() {
		if (officeZipCode != null && officeZipCode.length() == 7) {
			return officeZipCode.substring(4);
		}

		return null;
	}

	/**
	 * ���� �ּ�1�� ��ȯ�Ѵ�.
	 * 
	 * @return ���� �ּ�1
	 */
	public String getHomeAddr1() {
		return homeAddr1;
	}

	/**
	 * ���� �ּ�1�� �����Ѵ�.
	 * 
	 * @param homeAddr1 ���� �ּ�1
	 */
	public void setHomeAddr1(String homeAddr1) {
		this.homeAddr1 = homeAddr1;
	}

	/**
	 * ���� �ּ�2�� ��ȯ�Ѵ�.
	 * 
	 * @return ���� �ּ�2
	 */
	public String getHomeAddr2() {
		return homeAddr2;
	}

	/**
	 * ���� �ּ�2�� �����Ѵ�.
	 * 
	 * @param homeAddr2 ���� �ּ�2
	 */
	public void setHomeAddr2(String homeAddr2) {
		this.homeAddr2 = homeAddr2;
	}

	/**
	 * ���� �����ȣ�� ��ȯ�Ѵ�.
	 * 
	 * @return ���� �����ȣ
	 */
	public String getHomeZipCode() {
		return homeZipCode;
	}

	/**
	 * ���� �����ȣ�� �����Ѵ�.
	 * 
	 * @param homeZipCode ���� �����ȣ
	 */
	public void setHomeZipCode(String homeZipCode) {
		this.homeZipCode = homeZipCode;
	}

	/**
	 * ȸ�� �ּ�1�� ��ȯ�Ѵ�.
	 * 
	 * @return ȸ�� �ּ�1
	 */
	public String getOfficeAddr1() {
		return officeAddr1;
	}

	/**
	 * ȸ�� �ּ�1�� �����Ѵ�.
	 * 
	 * @param officeAddr1 ȸ�� �ּ�1
	 */
	public void setOfficeAddr1(String officeAddr1) {
		this.officeAddr1 = officeAddr1;
	}

	/**
	 * ȸ�� �ּ�2�� ��ȯ�Ѵ�.
	 * 
	 * @return ȸ�� �ּ�2
	 */
	public String getOfficeAddr2() {
		return officeAddr2;
	}

	/**
	 * ȸ�� �ּ�2�� �����Ѵ�.
	 * 
	 * @param officeAddr2 ȸ�� �ּ�2
	 */
	public void setOfficeAddr2(String officeAddr2) {
		this.officeAddr2 = officeAddr2;
	}

	/**
	 * ȸ�� �����ȣ�� ��ȯ�Ѵ�.
	 * 
	 * @return ȸ�� �����ȣ
	 */
	public String getOfficeZipCode() {
		return officeZipCode;
	}

	/**
	 * ȸ�� �����ȣ�� �����Ѵ�.
	 * 
	 * @param officeZipCode ȸ�� �����ȣ
	 */
	public void setOfficeZipCode(String officeZipCode) {
		this.officeZipCode = officeZipCode;
	}

}
