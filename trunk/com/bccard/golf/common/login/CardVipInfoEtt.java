/***************************************************************************************************
*	Ŭ������ : CardVipInfoEtt.class
*	�� �� �� : �ǿ���
*	��    �� : VIP Card������ �������� ���� Ett
*	������� : com.bccard.common.login
*	�ۼ����� : 2010.09.13
************************** �����̷� ****************************************************************
*	����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.common.login;

/******************************************************************************
* ī������.
* @author ����ȣ
* @version 2005-08-04
******************************************************************************/
public class CardVipInfoEtt implements java.io.Serializable 
{

	
	private String cardGubun;		//���� ���� (2)   01:�� 02:ä��
	private String cardNo;			//ī���ȣ (16)
	private String cardNm;			//����ī���(40)
	private String cardGrade;		//ī����(1)
	private String cardType;		//ī������(2) 		03:ƼŸ�� 12:�÷�Ƽ��	
	private String acctDay;			//ī�������� 
	private String juminNo;			//�ֹε�Ϲ�ȣ
	private String vipGrade;
	private String expDate;
	private String lastCardNo;
	private String cardAppType;
	private String bankNo;
	private String appDate;
	private String joinName;
	private String payAmt;
	private String usedAmt;
	private String cardJoinDate;	//ī��߱޳�¥
	
	
	/** ****************************************************************************
	 * VIP �̿�����ݾ�
	 * @param 
	 **************************************************************************** */
	public void setUsedAmt(String usedAmt)			{	this.usedAmt = usedAmt;			}

	public String getUsedAmt()		{	return usedAmt;		}
	/** ****************************************************************************
	 * ���� ����
	 * @param 
	 **************************************************************************** */
	public void setPayAmt(String payAmt)			{	this.payAmt = payAmt;			}

	public String getPayAmt()		{	return payAmt;		}
	/** ****************************************************************************
	 * ���� ����
	 * @param 
	 **************************************************************************** */
	public void setCardGubun(String cardGubun)			{	this.cardGubun = cardGubun;			}

	public String getCardGubun()		{	return cardGubun;		}
	
	/** ****************************************************************************
	 * ī���ȣ (16)
	 * @param 
	 **************************************************************************** */
	public void setCardNo(String cardNo)			{	this.cardNo = cardNo;			}	

	public String getCardNo()		{	return cardNo;		}	
	
	/** ****************************************************************************
	 * ����ī���(40) 
	 * @param 
	 **************************************************************************** */
	public void setCardNm(String cardNm)			{	this.cardNm = cardNm;			}	

	public String getCardNm()		{	return cardNm;		}	
	
	/** ****************************************************************************
	 * ī����(1)
	 * @param 
	 **************************************************************************** */
	public void setCardGrade(String cardGrade)			{	this.cardGrade = cardGrade;			}	

	public String getCardGrade()		{	return cardGrade;		}	
	
	
	/** ****************************************************************************
	 * ī������(2) 		03:ƼŸ�� 12:�÷�Ƽ��
	 * @param 
	 **************************************************************************** */
	public void setJoinName(String joinName)			{	this.joinName = joinName;			}	

	public String getJoinName()		{	return joinName;		}	
	
	
	/** ****************************************************************************
	 * ī������(2) 		03:ƼŸ�� 12:�÷�Ƽ��
	 * @param 
	 **************************************************************************** */
	public void setCardType(String cardType)			{	this.cardType = cardType;			}	

	public String getCardType()		{	return cardType;		}	
	
	/** ****************************************************************************
	 * ������
	 * @param 
	 **************************************************************************** */
	public void setAcctDay(String acctDay)		{	this.acctDay = acctDay;		}

	public String getAcctDay()		{	return acctDay;	}

	
	/** ****************************************************************************
	 * �ֹε�Ϲ�ȣ
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