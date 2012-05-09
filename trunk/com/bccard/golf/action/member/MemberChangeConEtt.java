/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemChgFormActn
*   작성자    : 미디어포스 임은혜
*   내용      : 회원 > 정회원 전환 > 폼
*   적용범위  : golf 
*   작성일자  : 2009-07-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import com.bccard.waf.core.AbstractEntity;

/**
* 비씨카드 개인회원 전환 비즈니스클래스
* @version 2005.12.19
*/ 
public class MemberChangeConEtt extends AbstractEntity {
    private String account;
    private String siteClss;
    private String memid;
    private String cardNo;
    private String cardPass;
    private String socid;
    private String name;
    private String memberName;
    
    private String term;

	 private String cvc;

	/**
    * 생성자 함수    
    * @version 2005.12.19
    */	
    public MemberChangeConEtt() {
        this.account = "";
        this.siteClss = "";
        this.memid = "";
        this.cardNo = "";
        this.cardPass = "";
        this.socid = "";
        this.name = "";
        this.memberName = "";
        
        this.term = "";

		this.cvc = "";
    }

	/**
    * 아이디 입력    
    * @param param 아이디
    * @version 2005.12.19
    */	
    public void setAccount(String param) { this.account = param; }
    
    /**
    * site class 입력    
    * @param param site class
    * @version 2005.12.19
    */	
    public void setSiteClss(String param) { this.siteClss = param; }
    
    /**
    * mem id 입력    
    * @param param mem id
    * @version 2005.12.19
    */	
    public void setMemid(String param) { this.memid = param; }
    
    /**
    * 주민번호 입력    
    * @param param 주민번호
    * @version 2005.12.19
    */	    
    public void setSocid(String param) { this.socid = param; }
    
    /**
    * 카드번호 입력    
    * @param param 카드번호
    * @version 2005.12.19
    */	    
    public void setCardNo(String param) { this.cardNo = param; }
    
    /**
    * 비밀번호 입력    
    * @param param 비밀번호
    * @version 2005.12.19
    */	    
    public void setCardPass(String param) { this.cardPass = param; }
    
    /**
    * 이름 입력    
    * @param param 이름
    * @version 2005.12.19
    */	    
    public void setName(String param) { this.name = param; }
    
    /**
    * 맴버 이름 입력    
    * @param param 맴버이름
    * @version 2005.12.19
    */	    
    public void setMemberName(String param) { this.memberName = param; }
    
    /**
    * 유효기간 입력    
    * @param param 유효기간
    * @version 2005.12.19
    */	    
    public void setTerm(String param) { this.term = param; }

    /**
    * cvc 입력    
    * @param param 유효기간
    * @version 2005.12.19
    */	    
    public void setCvc(String param) { this.cvc = param; }

	/**
    * 아이디 반환    
    * @version 2005.12.19 	
 	* @return 아이디
 	*/ 	
    public String getAccount() { return this.account; }
    
	/**
    * site class 반환    
    * @version 2005.12.19
 	* @return site class
 	*/    
    public String getSiteClss() { return this.siteClss; }
    
	/**
    * memid 반환    
    * @version 2005.12.19
 	* @return memid
 	*/    
    public String getMemid() { return this.memid; }
    
    /**
    * 주민번호 반환    
    * @version 2005.12.19
 	* @return 주민번호
 	*/
    public String getSocid() { return this.socid; }

	/**
    * 카드번호 반환    
    * @version 2005.12.19
 	* @return 카드번호
 	*/    
    public String getCardNo() { return this.cardNo; }
    
	/**
    * 비밀번호 반환    
    * @version 2005.12.19
 	* @return 비밀번호
 	*/    
    public String getCardPass() { return this.cardPass; }
    /*public String getCardPassS2() {
        if ( this.cardPass != null && this.cardPass.length() > 2 ) {
            return this.cardPass.substring(0, 2);
        } else {
            return null;
        }
    }*/
    
 	/**
    * 이름 반환    
    * @version 2005.12.19
 	* @return 이름
 	*/   
    public String getName() { return this.name; }
    
	/**
    * 맴버이름 반환    
    * @version 2005.12.19
 	* @return 맴버이름
 	*/    
    public String getMemberName() { return this.memberName; }

	/**
    * 유효기간 반환    
    * @version 2005.12.19
 	* @return 유효기간
 	*/    
    public String getTerm() { return this.term; }
	
	/**
    * cvc 반환    
    * @version 2005.12.19
 	* @return 유효기간
 	*/    
    public String getCvc() { return this.cvc; }
}