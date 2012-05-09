/*
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� ���� : 2008. 01. 28 [sjjo@intermajor.com]
*/
package com.bccard.golf.common;

import com.bccard.waf.core.AbstractEntity;


public class SearchEntity extends AbstractEntity {

	/** ��¥ ���� */
	protected String dateFormat = "yyyy-MM-dd";

	/** ���ڿ� ���� �شޱ��̸� �ʰ��Ұ�� "strLength + ...���� ���" */
	protected int strLength = 25;

	protected String searchColumn;

	protected String searchWord;

	protected String orderColumn;

	protected String orderSort;

	/** ����¡�� �����ϰ� ��� �˻��� �������� ���� */
	protected boolean searchAllResults;


 
	/**
	 * @return
	 */
	public String getOrderColumn() {
		return orderColumn;
	}

	/**
	 * @param orderColumn
	 */
	public void setOrderColumn(String orderColumn) {
		this.orderColumn = orderColumn;
	}

	/**
	 * @return
	 */
	public String getOrderSort() {
		return orderSort;
	}

	/**
	 * @param orderSort
	 */
	public void setOrderSort(String orderSort) {
		this.orderSort = orderSort;
	}

	/**
	 * @return
	 */
	public String getSearchColumn() {
		return searchColumn;
	}

	/**
	 * @param searchColumn
	 */
	public void setSearchColumn(String searchColumn) {
		this.searchColumn = searchColumn;
	}

	/**
	 * @return
	 */
	public String getSearchWord() {
		return searchWord;
	}

	/**
	 * @param searchWord
	 */
	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}





	/**
	 * @return
	 */
	public String getDateFormat() {
		return dateFormat;
	}

	/**
	 * @param dateFormat
	 */
	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	/**
	 * @return
	 */
	public int getStrLength() {
		return strLength;
	}

	/**
	 * @param strLength
	 */
	public void setStrLength(int strLength) {
		this.strLength = strLength;
	}

	/**
	 * @param strLength
	 */
	public void setStrLength(String strLength) {
		this.strLength = Integer.parseInt(strLength);
	}

	/**
	 * @return
	 */
	public boolean isSearchAllResults() {
		return searchAllResults;
	}

	/**
	 * @param searchAllResults
	 */
	public void setSearchAllResults(boolean searchAllResults) {
		this.searchAllResults = searchAllResults;
	}

}
