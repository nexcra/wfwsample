/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : ResponseData
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : ���� �������̽�.
*   �������  : Golf
*   �ۼ�����  : 2009-04-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.common;

import java.util.Enumeration;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public interface Status {
	public static final int OK = 0;
	
	public static final int INFO = 100;
	
	public static final int WARN = 200;
	
	public static final int ERROR = 300;
	
	/**
	 * @return
	 */
	String getTitle();
	
	/**
	 * @return
	 */
	int getStatusCode();
	
	/**
	 * @return
	 */
	Throwable getRootCause();
	
	/**
	 * @return
	 */
	String getMessage();
	
	/**
	 * @return
	 */
	Enumeration getStatusEvent();
	
	/**
	 * @return
	 */
	boolean isOK();
}
