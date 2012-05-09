/***************************************************************************************************
*	클래스명 : CardVipInfoEtt.class
*	작 성 자 : 권영만
*	내    용 : VIP Card정보를 가져오기 위한 Ett
*	적용범위 : com.bccard.common.login
*	작성일자 : 2010.09.13
************************** 수정이력 ****************************************************************
*	일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.common.login;

/******************************************************************************
* 카드정보.
* @author 이은호
* @version 2005-08-04
******************************************************************************/
public class CardVipInfoEtt implements java.io.Serializable 
{

	
	private String cardGubun;		//서비스 구분 (2)   01:블랙 02:채움
	private String cardNo;			//카드번호 (16)
	private String cardNm;			//제휴카드명(40)
	private String cardGrade;		//카드등급(1)
	private String cardType;		//카드종류(2) 		03:티타늄 12:플래티늄	
	private String acctDay;			//카드등록일자 
	private String juminNo;			//주민등록번호
	private String vipGrade;
	private String expDate;
	private String lastCardNo;
	private String cardAppType;
	private String bankNo;
	private String appDate;
	private String joinName;
	private String payAmt;
	private String usedAmt;
	private String cardJoinDate;	//카드발급날짜
	
	
	/** ****************************************************************************
	 * VIP 이용실적금액
	 * @param 
	 **************************************************************************** */
	public void setUsedAmt(String usedAmt)			{	this.usedAmt = usedAmt;			}

	public String getUsedAmt()		{	return usedAmt;		}
	/** ****************************************************************************
	 * 서비스 구분
	 * @param 
	 **************************************************************************** */
	public void setPayAmt(String payAmt)			{	this.payAmt = payAmt;			}

	public String getPayAmt()		{	return payAmt;		}
	/** ****************************************************************************
	 * 서비스 구분
	 * @param 
	 **************************************************************************** */
	public void setCardGubun(String cardGubun)			{	this.cardGubun = cardGubun;			}

	public String getCardGubun()		{	return cardGubun;		}
	
	/** ****************************************************************************
	 * 카드번호 (16)
	 * @param 
	 **************************************************************************** */
	public void setCardNo(String cardNo)			{	this.cardNo = cardNo;			}	

	public String getCardNo()		{	return cardNo;		}	
	
	/** ****************************************************************************
	 * 제휴카드명(40) 
	 * @param 
	 **************************************************************************** */
	public void setCardNm(String cardNm)			{	this.cardNm = cardNm;			}	

	public String getCardNm()		{	return cardNm;		}	
	
	/** ****************************************************************************
	 * 카드등급(1)
	 * @param 
	 **************************************************************************** */
	public void setCardGrade(String cardGrade)			{	this.cardGrade = cardGrade;			}	

	public String getCardGrade()		{	return cardGrade;		}	
	
	
	/** ****************************************************************************
	 * 카드종류(2) 		03:티타늄 12:플래티늄
	 * @param 
	 **************************************************************************** */
	public void setJoinName(String joinName)			{	this.joinName = joinName;			}	

	public String getJoinName()		{	return joinName;		}	
	
	
	/** ****************************************************************************
	 * 카드종류(2) 		03:티타늄 12:플래티늄
	 * @param 
	 **************************************************************************** */
	public void setCardType(String cardType)			{	this.cardType = cardType;			}	

	public String getCardType()		{	return cardType;		}	
	
	/** ****************************************************************************
	 * 결제일
	 * @param 
	 **************************************************************************** */
	public void setAcctDay(String acctDay)		{	this.acctDay = acctDay;		}

	public String getAcctDay()		{	return acctDay;	}

	
	/** ****************************************************************************
	 * 주민등록번호
	 * @param 
	 **************************************************************************** */
	public void setJuminNo(String juminNo)			{	this.juminNo = juminNo;			}	

	public String getJuminNo()		{	return juminNo;		}	
	
	/** ****************************************************************************
	 * vipGrade
	 * @param 
	 **************************************************************************** */
	public void setVipGrade(String vipGrade)			{	this.vipGrade = vipGrade;			}	

	public String getVipGrade()		{	return vipGrade;		}	
	
	/** ****************************************************************************
	 * expDate
	 * @param 
	 **************************************************************************** */
	public void setExpDate(String expDate)			{	this.expDate = expDate;			}	

	public String getExpDate()		{	return expDate;		}	
	/** ****************************************************************************
	 * setLastCardNo
	 * @param 
	 **************************************************************************** */
	public void setLastCardNo(String lastCardNo)			{	this.lastCardNo = lastCardNo;			}	

	public String getLastCardNo()		{	return lastCardNo;		}	
	
	/** ****************************************************************************
	 * setCardJoinDate
	 * @param  
	 **************************************************************************** */
	public void setCardJoinDate(String cardJoinDate)			{	this.cardJoinDate = cardJoinDate;			}	

	public String getCardJoinDate()		{	return cardJoinDate;		}	
	
	/** ****************************************************************************
	 * setCardAppType
	 * @param 
	 **************************************************************************** */
	public void setCardAppType(String cardAppType)			{	this.cardAppType = cardAppType;			}	

	public String getCardAppType()		{	return cardAppType;		}	
	
	/** ****************************************************************************
	 * setBankNo
	 * @param 
	 **************************************************************************** */
	public void setBankNo(String bankNo)			{	this.bankNo = bankNo;			}	

	public String getBankNo()		{	return bankNo;		}	
	/** ****************************************************************************
	 * setAppDate
	 * @param 
	 **************************************************************************** */
	public void setAppDate(String appDate)			{	this.appDate = appDate;			}	

	public String getAppDate()		{	return appDate;		}	
	
}