/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : ChkChgSocIdException
*   �ۼ���    : (��)�̵������ ������
*   ����      : 
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.login;

import com.bccard.waf.common.BaseException;



/******************************************************************************
* Golf : TpMainRegFormActn
* @author	(��)�̵������
* @version	1.0
******************************************************************************/


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
