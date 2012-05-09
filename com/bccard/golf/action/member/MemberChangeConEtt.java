/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemChgFormActn
*   �ۼ���    : �̵������ ������
*   ����      : ȸ�� > ��ȸ�� ��ȯ > ��
*   �������  : golf 
*   �ۼ�����  : 2009-07-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.action.member;

import com.bccard.waf.core.AbstractEntity;

/**
* ��ī�� ����ȸ�� ��ȯ ����Ͻ�Ŭ����
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
    * ������ �Լ�    
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
    * ���̵� �Է�    
    * @param param ���̵�
    * @version 2005.12.19
    */	
    public void setAccount(String param) { this.account = param; }
    
    /**
    * site class �Է�    
    * @param param site class
    * @version 2005.12.19
    */	
    public void setSiteClss(String param) { this.siteClss = param; }
    
    /**
    * mem id �Է�    
    * @param param mem id
    * @version 2005.12.19
    */	
    public void setMemid(String param) { this.memid = param; }
    
    /**
    * �ֹι�ȣ �Է�    
    * @param param �ֹι�ȣ
    * @version 2005.12.19
    */	    
    public void setSocid(String param) { this.socid = param; }
    
    /**
    * ī���ȣ �Է�    
    * @param param ī���ȣ
    * @version 2005.12.19
    */	    
    public void setCardNo(String param) { this.cardNo = param; }
    
    /**
    * ��й�ȣ �Է�    
    * @param param ��й�ȣ
    * @version 2005.12.19
    */	    
    public void setCardPass(String param) { this.cardPass = param; }
    
    /**
    * �̸� �Է�    
    * @param param �̸�
    * @version 2005.12.19
    */	    
    public void setName(String param) { this.name = param; }
    
    /**
    * �ɹ� �̸� �Է�    
    * @param param �ɹ��̸�
    * @version 2005.12.19
    */	    
    public void setMemberName(String param) { this.memberName = param; }
    
    /**
    * ��ȿ�Ⱓ �Է�    
    * @param param ��ȿ�Ⱓ
    * @version 2005.12.19
    */	    
    public void setTerm(String param) { this.term = param; }

    /**
    * cvc �Է�    
    * @param param ��ȿ�Ⱓ
    * @version 2005.12.19
    */	    
    public void setCvc(String param) { this.cvc = param; }

	/**
    * ���̵� ��ȯ    
    * @version 2005.12.19 	
 	* @return ���̵�
 	*/ 	
    public String getAccount() { return this.account; }
    
	/**
    * site class ��ȯ    
    * @version 2005.12.19
 	* @return site class
 	*/    
    public String getSiteClss() { return this.siteClss; }
    
	/**
    * memid ��ȯ    
    * @version 2005.12.19
 	* @return memid
 	*/    
    public String getMemid() { return this.memid; }
    
    /**
    * �ֹι�ȣ ��ȯ    
    * @version 2005.12.19
 	* @return �ֹι�ȣ
 	*/
    public String getSocid() { return this.socid; }

	/**
    * ī���ȣ ��ȯ    
    * @version 2005.12.19
 	* @return ī���ȣ
 	*/    
    public String getCardNo() { return this.cardNo; }
    
	/**
    * ��й�ȣ ��ȯ    
    * @version 2005.12.19
 	* @return ��й�ȣ
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
    * �̸� ��ȯ    
    * @version 2005.12.19
 	* @return �̸�
 	*/   
    public String getName() { return this.name; }
    
	/**
    * �ɹ��̸� ��ȯ    
    * @version 2005.12.19
 	* @return �ɹ��̸�
 	*/    
    public String getMemberName() { return this.memberName; }

	/**
    * ��ȿ�Ⱓ ��ȯ    
    * @version 2005.12.19
 	* @return ��ȿ�Ⱓ
 	*/    
    public String getTerm() { return this.term; }
	
	/**
    * cvc ��ȯ    
    * @version 2005.12.19
 	* @return ��ȿ�Ⱓ
 	*/    
    public String getCvc() { return this.cvc; }
}