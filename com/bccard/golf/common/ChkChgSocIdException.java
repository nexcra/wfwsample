/*******************************************************************************
 *   Ŭ������ : ChkChgSocIdException 
 *   �� �� �� : 
 *   ��    �� : 
 *   ������� : 
 *   �ۼ����� : 
 ********************************** �������� ************************************
 *	����		����		�ۼ���		�������
 *******************************************************************************/
package com.bccard.golf.common;

import com.bccard.waf.common.BaseException;

public class ChkChgSocIdException extends BaseException {
    private final String account;
    private final String result;
    private final String socid;

	/**
	 *  ChkChgSocIdException
	 **/
    public ChkChgSocIdException(String account, String socid, String result) {
        super("�ֹι�ȣ ������ ȸ���Դϴ�");
        this.account  = account;
        this.result = result;
        this.socid  = socid;
    } 

	/**
	 *  getAccount
	 **/
    public String getAccount() { return this.account; }
	/**
	 *  getResult
	 **/
    public String getResult() { return this.result; }
	/**
	 *  getSocId
	 **/
    public String getSocId()  { return this.socid; }
}
