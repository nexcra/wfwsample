/*******************************************************************************
*   클래스명  : KeyManager
*   작성자    : 
*   내용      : 
*   적용범위  : 
*   작성일자  : 
************************** 수정이력 ********************************************
*    일자      버전   작성자   변경사항
*                                                                               
*******************************************************************************/
package com.bccard.golf.common.security;

import com.bccard.waf.core.WaContext;

public class KeyManager {	
	
	private String preStr = "HSMKEY_";

	/**
	 *  getKey
	 **/
	public String getKey(String alias, WaContext context) {

		return (String)context.getUserDefineObject("bckey", preStr + alias); 

	}
}
