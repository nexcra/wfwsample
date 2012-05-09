/***************************************************************************************************
*	클래스명 : CardInfoEtt.class
*	작 성 자 : 이은호(李銀浩;Lee Eun Ho)
*	내    용 : 개인회원 로그인처리시 TOP포인트의 Card정보를 가져오기 위한 Ett
*	적용범위 : com.bccard.common.login
*	작성일자 : 2005-08-04, Thursday
************************** 수정이력 ****************************************************************
*  일자	      	작성자	변경사항
*  2011.01.20 	이경희 	NH패밀리 주석 추가 (48:패밀리카드)
***************************************************************************************************/
package com.bccard.golf.common.login;

/******************************************************************************
* 카드정보.
* @author 이은호
* @version 2005-08-04
******************************************************************************/
public class CardNhInfoEtt implements java.io.Serializable 
{

	
	private String cardGubun;		//서비스 구분 (2)   01:블랙 02:채움
	private String cardNo;			//카드번호 (16)
	private String cardNm;			//제휴카드명(40)
	private String cardGrade;		//카드등급(1)
	private String cardType;		//카드종류(2) 		03:티타늄 12:플래티늄	48:패밀리카드
	private String acctDay;			//카드등록일자 
	private String juminNo;			//주민등록번호
	
	
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
	

	
}