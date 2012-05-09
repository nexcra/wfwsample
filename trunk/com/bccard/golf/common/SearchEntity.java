/*
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 일자 : 2008. 01. 28 [sjjo@intermajor.com]
*/
package com.bccard.golf.common;

import com.bccard.waf.core.AbstractEntity;


public class SearchEntity extends AbstractEntity {

	/** 날짜 포멧 */
	protected String dateFormat = "yyyy-MM-dd";

	/** 문자열 갯수 해달길이를 초과할경우 "strLength + ...으로 출력" */
	protected int strLength = 25;

	protected String searchColumn;

	protected String searchWord;

	protected String orderColumn;

	protected String orderSort;

	/** 페이징을 무시하고 모두 검색할 것인지의 여부 */
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
