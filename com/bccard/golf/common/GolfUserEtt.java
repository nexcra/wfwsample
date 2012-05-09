/***************************************************************************************************
*   클래스명  : EtaxUserEtt
*   작성자    : csj007
*   내용      : 지방세 사용자 정보 세션 Entity
*   적용범위  : etax
*   작성일자  : 2008.07.28
************************** 수정이력 ****************************************************************
*   일자      :	2008.09.01.
*   변경사항  : extends AbstractEntity 
***************************************************************************************************/
package com.bccard.golf.common;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.bccard.golf.common.login.CardInfoEtt;


public class GolfUserEtt extends GolfSessionEtt {
	private String juminNo;		// 주민번호 
	
	private String cardNo;		// 카드번호

	protected List cardInfoList;		// 보유카드정보값들
	protected List cardUseInfoList;		// 사용가능보유카드정보값들
	protected List cardNhInfoList;		// 보유카드정보값들(NH 농협)
	protected List cardVipInfoList;		// VIP카드
	protected List topGolfCardInfoList;	// 탑골프카드
	protected List richCardInfoList;	// Rich&Rich카드
	
	private Vector bankListNames;		// 은행이름                
	private Vector bankListCodes;		// 은행코드                
	private Vector bankSettlement;		// 은행결제일              

	private Vector bankListEmail;		// 은행Email명세서 신청여부
	private Vector bankMemberNo;		// 회원사 회원번호
	private Vector bankAcctNo;			// 현 결제계좌번호 By PWT 20070823
	private Vector bankAcctName;		// 현 결제계좌은행 By PWT 20070823
	private Vector bankType;			// 독자은행여부 0-비독자, 1-독자 
	private String vipMaxGrade;
	private String vipCardNo;
	private String vipCardExpDate;
	
	/** ***********************************************************************
	 * EtaxUserEtt 
	 * @param 
	 * @return 
	 *********************************************************************** */
	public GolfUserEtt() {
		this.juminNo		= "";
		this.cardNo			= "";
	}

	/** ***********************************************************************
	 * getJuminNo
	 * @param 
	 * @return 
	 *********************************************************************** */
	
	public String getJuminNo()	{ return this.juminNo;	}

	/** ***********************************************************************
	 * setJuminNo
	 * @param 
	 * @return 
	 *********************************************************************** */
	public void setJuminNo(String s)	{ this.juminNo	= s; }
	/** ***********************************************************************
	 * getJuminNo
	 * @param 
	 * @return 
	 *********************************************************************** */
	
	public String getCardNo()	{ return this.cardNo;	}

	/** ***********************************************************************
	 * setJuminNo
	 * @param 
	 * @return 
	 *********************************************************************** */
	public void setCardNo(String s)	{ this.cardNo	= s; }

    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public List getTopGolfCardInfoList() {
		return this.topGolfCardInfoList;
	}
	
	/******************************************************************************
	 * setCardInfoList
	 * @param
	******************************************************************************/
	public void setTopGolfCardInfoList(List topGolfCardInfoList) {
		this.topGolfCardInfoList = topGolfCardInfoList;
	}
	
    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public List getRichCardInfoList() {
		return this.richCardInfoList;
	}
	
	/******************************************************************************
	 * setCardInfoList
	 * @param
	******************************************************************************/
	public void setRichCardInfoList(List richCardInfoList) {
		this.richCardInfoList = richCardInfoList;
	}
	
	
    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public List getCardInfoList() {
		return this.cardInfoList;
	}
	
	/******************************************************************************
	 * setCardInfoList
	 * @param
	******************************************************************************/
	public void setCardInfoList(List cardInfoList) {
		this.cardInfoList = cardInfoList;
	}

    /** ****************************************************************************
     *  Nh 농협카드 리스트
     * @return
     **************************************************************************** */
	public List getCardNhInfoList() {
		return this.cardNhInfoList;
	}
	
	/******************************************************************************
	 * setCardNhInfoList
	 * @param 
	******************************************************************************/
	public void setCardNhInfoList(List cardNhInfoList) { 
		this.cardNhInfoList = cardNhInfoList;
	}
	
    /** ****************************************************************************
     *  VIP카드 리스트
     * @return
     **************************************************************************** */
	public List getCardVipInfoList() {
		return this.cardVipInfoList;
	}
	
	/******************************************************************************
	 * VIP카드 setCardNhInfoList
	 * @param 
	******************************************************************************/
	public void setCardVipInfoList(List cardVipInfoList) { 
		this.cardVipInfoList = cardVipInfoList;
	}	
	

    /** ****************************************************************************
     *  유효회원은행 카드 반환. By PWT 20070924
     * @return
     **************************************************************************** */
	public List getCardInfoList2(String banks) {

//		String banks = "23:25:31:32:36:39:50";
		CardInfoEtt record;
		List selCardInfoList = new ArrayList();
		String bankCd = "";
		if(banks == null || "".equals(banks))
			return selCardInfoList;
		String[] bankArray = banks.split(":");
		for(int i = 0 ; i < this.cardInfoList.size(); i++) {
			record = (CardInfoEtt)this.cardInfoList.get(i);
			bankCd = record.getBankNo();
			for(int j = 0 ; j < bankArray.length; j++) {
				if("22".equals(bankCd))//우리은행(상업)
					bankCd = "20";
				else if("24".equals(bankCd))//우리은행(한일)
					bankCd = "24";
				else if("33".equals(bankCd))//하나은행(충청은행)
					bankCd = "04";
				else if("27".equals(bankCd))//씨티(한미)
					bankCd = "36";
				else if("12".equals(bankCd) || "13".equals(bankCd) || "14".equals(bankCd))//단위농협
					bankCd = "11";
				if (bankArray[j].equals(bankCd)) {
					selCardInfoList.add(record);
				}
			}
		}
		return selCardInfoList;
	}
	/** ****************************************************************************
	 * VIP카드 최고 등급
	 * @param 
	 **************************************************************************** */
	public void  setVipMaxGrade(String vipMaxGrade)			{	this.vipMaxGrade = vipMaxGrade;				}
	/** ***********************************************************************
	 * getVipMaxGrade
	 * @param 
	 * @return 
	 *********************************************************************** */	
	public String getVipMaxGrade()	{ return this.vipMaxGrade;	}
	
	/** ****************************************************************************
	 * setVipCardNo
	 * @param 
	 **************************************************************************** */
	public void  setVipCardNo(String vipCardNo)			{	this.vipCardNo = vipCardNo;				}
	/** ***********************************************************************
	 * getVipMaxGrade
	 * @param 
	 * @return 
	 *********************************************************************** */	
	public String getVipCardNo()	{ return this.vipCardNo;	}
	
	/** ****************************************************************************
	 * setvipCardExpDate
	 * @param 
	 **************************************************************************** */
	public void  setVipCardExpDate(String vipCardExpDate)			{	this.vipCardExpDate = vipCardExpDate;				}
	/** ***********************************************************************
	 * getVipMaxGrade
	 * @param 
	 * @return 
	 *********************************************************************** */	
	public String getVipCardExpDate()	{ return this.vipCardExpDate;	}
	
	/** ****************************************************************************
	 * 은행명
	 * @param 
	 **************************************************************************** */
	public void  setBankListNames(Vector bankListNames)			{	this.bankListNames = bankListNames;				}

	/** ****************************************************************************
	 * 은행코드
	 * @param 
	 **************************************************************************** */
	public void  setBankListCodes(Vector bankListCodes)			{	this.bankListCodes = bankListCodes;				}
	/** ****************************************************************************
	 * 결제일
	 * @param 
	 **************************************************************************** */
	public void  setBankSettlement(Vector bankSettlement)		{	this.bankSettlement = bankSettlement;			}
	/** ****************************************************************************
	 * 이메일
	 * @param 
	 **************************************************************************** */
	public void  setBankListEmail(Vector bankListEmail)			{	this.bankListEmail = bankListEmail;				}
	/** ****************************************************************************
	 * 회원번호
	 * @param 
	 **************************************************************************** */
	public void  setBankMemberNo(Vector bankMemberNo)			{	this.bankMemberNo = bankMemberNo;				}
	/** ****************************************************************************
	 * 결제계좌
	 * @param 
	 **************************************************************************** */
	public void  setBankAcctNo(Vector bankAcctNo)				{	this.bankAcctNo = bankAcctNo;					}
	/** ****************************************************************************
	 * 계좌번호
	 * @param 
	 **************************************************************************** */
	public void  setBankAcctName(Vector bankAcctName)			{	this.bankAcctName = bankAcctName;				}
	/** ****************************************************************************
	 * 최근사용금액
	 * @param 
	 **************************************************************************** */
	//public void  setUsedAmt(Vector usedAmt)						{	this.usedAmt = usedAmt;							}
	/** ****************************************************************************
	 * 독자은행여부
	 * @param 
	 **************************************************************************** */
	public void  setBankType(Vector bankType)					{	this.bankType = bankType;						}
	/** ****************************************************************************
	 * 은행명
	 * @return 
	 **************************************************************************** */
	public Vector getBankListNames()	{	return this.bankListNames;			}
	/** ****************************************************************************
	 * 은행코드
	 * @return 
	 **************************************************************************** */
	public Vector getBankListCodes()	{	return this.bankListCodes;			}
	/** ****************************************************************************
	 * 결제일
	 * @return 
	 **************************************************************************** */
	public Vector getBankSettlement()	{	return this.bankSettlement;			}
	/** ****************************************************************************
	 * 이메일
	 * @return 
	 **************************************************************************** */
	public Vector getBankListEmail()	{	return this.bankListEmail;			}
	/** ****************************************************************************
	 * 회원번호
	 * @return 
	 **************************************************************************** */
	public Vector getBankMemberNo()		{	return this.bankMemberNo;			}
	/** ****************************************************************************
	 * 결제계좌
	 * @return 
	 **************************************************************************** */
	public Vector getBankAcctNo()		{	return this.bankAcctNo;				}
	/** ****************************************************************************
	 * 계좌번호
	 * @return 
	 **************************************************************************** */
	public Vector getBankAcctName()		{	return this.bankAcctName;			}
	/** ****************************************************************************
	 * 최근사용금액
	 * @return 
	 **************************************************************************** */
	//public Vector getUsedAmt()			{	return this.usedAmt;				}
	/** ****************************************************************************
	 * 독자은행여부
	 * @return 
	 **************************************************************************** */
	public Vector getBankTypeAmt()		{	return this.bankType;				}


	/** ****************************************************************************
	 * 선택된 은행명, By PWT 20070924
	 * @return 
	 **************************************************************************** */
	public Vector getBankListNames2(String banks)	{	
//		String banks = "23:25:31:32:36:39:50";
		Vector selBankNameList = new Vector();
		String bankCd = "";
		if(banks == null || "".equals(banks)) 
			return selBankNameList;
		String[] bankArray = banks.split(":");
		for(int i = 0 ; i < this.bankListCodes.size(); i++) {
			bankCd = (String)bankListCodes.get(i);
			for(int j = 0 ; j < bankArray.length; j++) {
				if("22".equals(bankCd))//우리은행(상업)
					bankCd = "20";
				else if("24".equals(bankCd))//우리은행(한일)
					bankCd = "24";
				else if("33".equals(bankCd))//하나은행(충청은행)
					bankCd = "04";
				else if("27".equals(bankCd))//씨티(한미) 
					bankCd = "36";
				else if("12".equals(bankCd) || "13".equals(bankCd) || "14".equals(bankCd))//단위농협 
					bankCd = "11";
				if (bankArray[j].equals(bankCd)) {
					selBankNameList.add(this.bankListNames.get(i));
				}
			}
		}

		return selBankNameList;
	}
	/** ****************************************************************************
	 * 선택된 은행코드, By PWT 20070924
	 * @return 
	 **************************************************************************** */
	public Vector getBankListCodes2(String banks)	{	
//		String banks = "23:25:31:32:36:39:50";
		Vector selBankCodeList = new Vector();
		String bankCd = "";
		if(banks == null || "".equals(banks)) 
			return selBankCodeList;
		String[] bankArray = banks.split(":");
		for(int i = 0 ; i < this.bankListCodes.size(); i++) {
			bankCd = (String)bankListCodes.get(i);
			for(int j = 0 ; j < bankArray.length; j++) {
				if("22".equals(bankCd))//우리은행(상업)
					bankCd = "20";
				else if("24".equals(bankCd))//우리은행(한일)
					bankCd = "24";
				else if("33".equals(bankCd))//하나은행(충청은행)
					bankCd = "04";
				else if("27".equals(bankCd))//씨티(한미) 
					bankCd = "36";
				else if("12".equals(bankCd) || "13".equals(bankCd) || "14".equals(bankCd))//단위농협 
					bankCd = "11";
				if (bankArray[j].equals(bankCd)) {
					selBankCodeList.add(this.bankListCodes.get(i));
				}
			}
		}		

		return selBankCodeList;
	}	

	/** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public List getCardUseInfoList() {
		return this.cardUseInfoList;
	}
	/******************************************************************************
	 * setCardUseInfoList
	 * @param
	******************************************************************************/
	public void setCardUseInfoList(List cardInfoList) {
		this.cardUseInfoList = cardInfoList;
	}
}