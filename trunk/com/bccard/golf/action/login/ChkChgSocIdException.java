/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : ChkChgSocIdException
*   작성자    : (주)미디어포스 조은미
*   내용      : 
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.login;

import com.bccard.waf.common.BaseException;



/******************************************************************************
* Golf : TpMainRegFormActn
* @author	(주)미디어포스
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
        super("주민번호 비정상 회원입니다");
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
