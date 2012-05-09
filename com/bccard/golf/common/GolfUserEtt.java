/***************************************************************************************************
*   Ŭ������  : EtaxUserEtt
*   �ۼ���    : csj007
*   ����      : ���漼 ����� ���� ���� Entity
*   �������  : etax
*   �ۼ�����  : 2008.07.28
************************** �����̷� ****************************************************************
*   ����      :	2008.09.01.
*   �������  : extends AbstractEntity 
***************************************************************************************************/
package com.bccard.golf.common;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.bccard.golf.common.login.CardInfoEtt;


public class GolfUserEtt extends GolfSessionEtt {
	private String juminNo;		// �ֹι�ȣ 
	
	private String cardNo;		// ī���ȣ

	protected List cardInfoList;		// ����ī����������
	protected List cardUseInfoList;		// ��밡�ɺ���ī����������
	protected List cardNhInfoList;		// ����ī����������(NH ����)
	protected List cardVipInfoList;		// VIPī��
	protected List topGolfCardInfoList;	// ž����ī��
	protected List richCardInfoList;	// Rich&Richī��
	
	private Vector bankListNames;		// �����̸�                
	private Vector bankListCodes;		// �����ڵ�                
	private Vector bankSettlement;		// ���������              

	private Vector bankListEmail;		// ����Email���� ��û����
	private Vector bankMemberNo;		// ȸ���� ȸ����ȣ
	private Vector bankAcctNo;			// �� �������¹�ȣ By PWT 20070823
	private Vector bankAcctName;		// �� ������������ By PWT 20070823
	private Vector bankType;			// �������࿩�� 0-����, 1-���� 
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
     *  ��ȯ.
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
     *  ��ȯ.
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
     *  ��ȯ.
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
     *  Nh ����ī�� ����Ʈ
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
     *  VIPī�� ����Ʈ
     * @return
     **************************************************************************** */
	public List getCardVipInfoList() {
		return this.cardVipInfoList;
	}
	
	/******************************************************************************
	 * VIPī�� setCardNhInfoList
	 * @param 
	******************************************************************************/
	public void setCardVipInfoList(List cardVipInfoList) { 
		this.cardVipInfoList = cardVipInfoList;
	}	
	

    /** ****************************************************************************
     *  ��ȿȸ������ ī�� ��ȯ. By PWT 20070924
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
				if("22".equals(bankCd))//�츮����(���)
					bankCd = "20";
				else if("24".equals(bankCd))//�츮����(����)
					bankCd = "24";
				else if("33".equals(bankCd))//�ϳ�����(��û����)
					bankCd = "04";
				else if("27".equals(bankCd))//��Ƽ(�ѹ�)
					bankCd = "36";
				else if("12".equals(bankCd) || "13".equals(bankCd) || "14".equals(bankCd))//��������
					bankCd = "11";
				if (bankArray[j].equals(bankCd)) {
					selCardInfoList.add(record);
				}
			}
		}
		return selCardInfoList;
	}
	/** ****************************************************************************
	 * VIPī�� �ְ� ���
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
	 * �����
	 * @param 
	 **************************************************************************** */
	public void  setBankListNames(Vector bankListNames)			{	this.bankListNames = bankListNames;				}

	/** ****************************************************************************
	 * �����ڵ�
	 * @param 
	 **************************************************************************** */
	public void  setBankListCodes(Vector bankListCodes)			{	this.bankListCodes = bankListCodes;				}
	/** ****************************************************************************
	 * ������
	 * @param 
	 **************************************************************************** */
	public void  setBankSettlement(Vector bankSettlement)		{	this.bankSettlement = bankSettlement;			}
	/** ****************************************************************************
	 * �̸���
	 * @param 
	 **************************************************************************** */
	public void  setBankListEmail(Vector bankListEmail)			{	this.bankListEmail = bankListEmail;				}
	/** ****************************************************************************
	 * ȸ����ȣ
	 * @param 
	 **************************************************************************** */
	public void  setBankMemberNo(Vector bankMemberNo)			{	this.bankMemberNo = bankMemberNo;				}
	/** ****************************************************************************
	 * ��������
	 * @param 
	 **************************************************************************** */
	public void  setBankAcctNo(Vector bankAcctNo)				{	this.bankAcctNo = bankAcctNo;					}
	/** ****************************************************************************
	 * ���¹�ȣ
	 * @param 
	 **************************************************************************** */
	public void  setBankAcctName(Vector bankAcctName)			{	this.bankAcctName = bankAcctName;				}
	/** ****************************************************************************
	 * �ֱٻ��ݾ�
	 * @param 
	 **************************************************************************** */
	//public void  setUsedAmt(Vector usedAmt)						{	this.usedAmt = usedAmt;							}
	/** ****************************************************************************
	 * �������࿩��
	 * @param 
	 **************************************************************************** */
	public void  setBankType(Vector bankType)					{	this.bankType = bankType;						}
	/** ****************************************************************************
	 * �����
	 * @return 
	 **************************************************************************** */
	public Vector getBankListNames()	{	return this.bankListNames;			}
	/** ****************************************************************************
	 * �����ڵ�
	 * @return 
	 **************************************************************************** */
	public Vector getBankListCodes()	{	return this.bankListCodes;			}
	/** ****************************************************************************
	 * ������
	 * @return 
	 **************************************************************************** */
	public Vector getBankSettlement()	{	return this.bankSettlement;			}
	/** ****************************************************************************
	 * �̸���
	 * @return 
	 **************************************************************************** */
	public Vector getBankListEmail()	{	return this.bankListEmail;			}
	/** ****************************************************************************
	 * ȸ����ȣ
	 * @return 
	 **************************************************************************** */
	public Vector getBankMemberNo()		{	return this.bankMemberNo;			}
	/** ****************************************************************************
	 * ��������
	 * @return 
	 **************************************************************************** */
	public Vector getBankAcctNo()		{	return this.bankAcctNo;				}
	/** ****************************************************************************
	 * ���¹�ȣ
	 * @return 
	 **************************************************************************** */
	public Vector getBankAcctName()		{	return this.bankAcctName;			}
	/** ****************************************************************************
	 * �ֱٻ��ݾ�
	 * @return 
	 **************************************************************************** */
	//public Vector getUsedAmt()			{	return this.usedAmt;				}
	/** ****************************************************************************
	 * �������࿩��
	 * @return 
	 **************************************************************************** */
	public Vector getBankTypeAmt()		{	return this.bankType;				}


	/** ****************************************************************************
	 * ���õ� �����, By PWT 20070924
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
				if("22".equals(bankCd))//�츮����(���)
					bankCd = "20";
				else if("24".equals(bankCd))//�츮����(����)
					bankCd = "24";
				else if("33".equals(bankCd))//�ϳ�����(��û����)
					bankCd = "04";
				else if("27".equals(bankCd))//��Ƽ(�ѹ�) 
					bankCd = "36";
				else if("12".equals(bankCd) || "13".equals(bankCd) || "14".equals(bankCd))//�������� 
					bankCd = "11";
				if (bankArray[j].equals(bankCd)) {
					selBankNameList.add(this.bankListNames.get(i));
				}
			}
		}

		return selBankNameList;
	}
	/** ****************************************************************************
	 * ���õ� �����ڵ�, By PWT 20070924
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
				if("22".equals(bankCd))//�츮����(���)
					bankCd = "20";
				else if("24".equals(bankCd))//�츮����(����)
					bankCd = "24";
				else if("33".equals(bankCd))//�ϳ�����(��û����)
					bankCd = "04";
				else if("27".equals(bankCd))//��Ƽ(�ѹ�) 
					bankCd = "36";
				else if("12".equals(bankCd) || "13".equals(bankCd) || "14".equals(bankCd))//�������� 
					bankCd = "11";
				if (bankArray[j].equals(bankCd)) {
					selBankCodeList.add(this.bankListCodes.get(i));
				}
			}
		}		

		return selBankCodeList;
	}	

	/** ****************************************************************************
     *  ��ȯ.
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