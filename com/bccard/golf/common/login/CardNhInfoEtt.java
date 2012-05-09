/***************************************************************************************************
*	Ŭ������ : CardInfoEtt.class
*	�� �� �� : ����ȣ(�����;Lee Eun Ho)
*	��    �� : ����ȸ�� �α���ó���� TOP����Ʈ�� Card������ �������� ���� Ett
*	������� : com.bccard.common.login
*	�ۼ����� : 2005-08-04, Thursday
************************** �����̷� ****************************************************************
*  ����	      	�ۼ���	�������
*  2011.01.20 	�̰��� 	NH�йи� �ּ� �߰� (48:�йи�ī��)
***************************************************************************************************/
package com.bccard.golf.common.login;

/******************************************************************************
* ī������.
* @author ����ȣ
* @version 2005-08-04
******************************************************************************/
public class CardNhInfoEtt implements java.io.Serializable 
{

	
	private String cardGubun;		//���� ���� (2)   01:�� 02:ä��
	private String cardNo;			//ī���ȣ (16)
	private String cardNm;			//����ī���(40)
	private String cardGrade;		//ī����(1)
	private String cardType;		//ī������(2) 		03:ƼŸ�� 12:�÷�Ƽ��	48:�йи�ī��
	private String acctDay;			//ī�������� 
	private String juminNo;			//�ֹε�Ϲ�ȣ
	
	
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
	

	
}