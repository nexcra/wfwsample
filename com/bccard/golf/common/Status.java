/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : ResponseData
*   작성자     : (주)미디어포스 권영만
*   내용        : 상태 인터페이스.
*   적용범위  : Golf
*   작성일자  : 2009-04-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.common;

import java.util.Enumeration;

/******************************************************************************
* Golf
* @author	(주)미디어포스
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
