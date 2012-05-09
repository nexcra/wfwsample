/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : ResponseData
*   작성자     : (주)미디어포스 권영만
*   내용        : XML 상태 클래스
*   적용범위  : Golf
*   작성일자  : 2009-04-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.io.Serializable;
import java.util.Enumeration;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class RuntimeStatus implements Status, Serializable {

	private String title;

	private int statusCode;

	private String message;

	private Throwable rootCause;

	/**
	 * @param title
	 */
	RuntimeStatus(String title) {
		this.title = title;
		this.statusCode = OK;
		this.message = "";
		this.rootCause = null;
	}

	/** 재정의
	 * @see com.bccard.town.actions.Status#getTitle()
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 */
	void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param statusCode
	 * @param message
	 * @param t
	 */
	private void message(int statusCode, String message, Throwable t) {
		this.statusCode = statusCode;
		this.message = message;
		this.rootCause = t;
	}

	/**
	 * @param message
	 */
	public void ok(String message) {
		this.message(OK, message, null);
	}

	/**
	 * @param message
	 */
	public void warn(String message) {
		this.message(WARN, message, null);
	}

	/**
	 * @param message
	 */
	public void info(String message) {
		this.message(INFO, message, null);
	}

	/**
	 * @param message
	 */
	public void error(String message) {
		this.message(ERROR, message, null);
	}

	/**
	 * @param message
	 * @param t
	 */
	public void ok(String message, Throwable t) {
		this.message(OK, message, t);
	}

	/**
	 * @param message
	 * @param t
	 */
	public void warn(String message, Throwable t) {
		this.message(WARN, message, t);
	}

	/**
	 * @param message
	 * @param t
	 */
	public void info(String message, Throwable t) {
		this.message(INFO, message, t);
	}

	/**
	 * @param message
	 * @param t
	 */
	public void error(String message, Throwable t) {
		this.message(ERROR, message, t);
	}

	/** 재정의
	 * @see com.bccard.town.actions.Status#getStatusCode()
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/** 재정의
	 * @see com.bccard.town.actions.Status#getRootCause()
	 */
	public Throwable getRootCause() {
		return this.rootCause;
	}

	/** 재정의
	 * @see com.bccard.town.actions.Status#getMessage()
	 */
	public String getMessage() {
		return message;
	}

	/** 재정의
	 * @see com.bccard.town.actions.Status#getStatusEvent()
	 */
	public Enumeration getStatusEvent() {
		return null;
	}

	/** 재정의
	 * @see com.bccard.town.actions.Status#isOK()
	 */
	public boolean isOK() {
		return (this.statusCode == OK);
	}
}
