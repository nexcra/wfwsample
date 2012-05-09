/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : ResponseData
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : XML ���� Ŭ����
*   �������  : Golf
*   �ۼ�����  : 2009-04-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common;

import java.io.Serializable;
import java.util.Enumeration;

/******************************************************************************
* Golf
* @author	(��)�̵������
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

	/** ������
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

	/** ������
	 * @see com.bccard.town.actions.Status#getStatusCode()
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/** ������
	 * @see com.bccard.town.actions.Status#getRootCause()
	 */
	public Throwable getRootCause() {
		return this.rootCause;
	}

	/** ������
	 * @see com.bccard.town.actions.Status#getMessage()
	 */
	public String getMessage() {
		return message;
	}

	/** ������
	 * @see com.bccard.town.actions.Status#getStatusEvent()
	 */
	public Enumeration getStatusEvent() {
		return null;
	}

	/** ������
	 * @see com.bccard.town.actions.Status#isOK()
	 */
	public boolean isOK() {
		return (this.statusCode == OK);
	}
}
