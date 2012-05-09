/*******************************************************************************
 *   클래스명 : ChkChgSocIdException 
 *   작 성 자 : 
 *   내    용 : 
 *   적용범위 : 
 *   작성일자 : 
 ********************************** 수정사항 ************************************
 *	일자		버전		작성자		변경사항
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
