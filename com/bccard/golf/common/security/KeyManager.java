/*******************************************************************************
*   Ŭ������  : KeyManager
*   �ۼ���    : 
*   ����      : 
*   �������  : 
*   �ۼ�����  : 
************************** �����̷� ********************************************
*    ����      ����   �ۼ���   �������
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
