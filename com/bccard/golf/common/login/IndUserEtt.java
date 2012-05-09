/**********************************************************************************************************************
*   Ŭ������  : IndUserEtt
*   �ۼ���    : ä���� (csj007)
*   ����      : ����ȸ�� entity
*   �������  : �α��� �� ȸ������ ����
*   �ۼ�����  : 2004.01.19
************************** �����̷� ***********************************************************************************
*    ����      ����   �ۼ���   �������
*
**********************************************************************************************************************/
package com.bccard.golf.common.login;

import java.util.Vector;
import java.util.List;

/** ****************************************************************************
 * IndUser Entity
 * @version   2004.01.19
 * @author    <A href="mailto:csj007@e4net.net">chae sang jick</A>
 **************************************************************************** */
public class IndUserEtt extends BcUserEtt
{
	/** ****************************************************************************
	 * ������
	 * @param 
	 **************************************************************************** */
	public IndUserEtt(){}
	/** ****************************************************************************
	 * ������
	 * @param 
	 **************************************************************************** */
    public IndUserEtt(BcUserEtt bcUserEtt) { super(bcUserEtt); }

	private String socialId;			// �ֹι�ȣ    
	private Vector bankListNames;		// �����̸�                
	private Vector bankListCodes;		// �����ڵ�                
	private Vector bankSettlement;		// ���������              

	private Vector bankListEmail;		// ����Email���� ��û����
	private Vector bankMemberNo;		// ȸ���� ȸ����ȣ
	private Vector bankAcctNo;			// �� �������¹�ȣ By PWT 20070823
	private Vector bankAcctName;		// �� ������������ By PWT 20070823
	private Vector usedAmt;				// �ֱ� 3������ û�� �ݾ� ��(sort��) By PWT 20070904
	private Vector bankType;			// �������࿩�� 0-����, 1-����
	// ž����Ʈ ������ �����ϱ� ���� ����, Writted By, Lee Eun Ho, 2005-07-29, Friday.
//	private String name;				// ����/ȸ���
//	private String homeTel;				// ���� ��ȭ��ȣ
//	private String homeZip;				// ���� �����ȣ
//	private String homeAddr1;			// ���� �����ȣ �ּ�
//	private String homeAddr2;			// ���� �� ���� �ּ�
//	private String officeTel;			// ���� ��ȭ��ȣ
//	private String officeZip;			// ���� �����ȣ
//	private String officeAddr1;			// ���� �����ȣ �ּ�
//	private String officeAddr2;			// ���� �� ���� �ּ�
//	private String company;				// ȸ���
//	private String department;			// �μ���
//	private String pcsTel;				// �ڵ�����ȣ
//	private int convertableSK;			// ��ȯ���� TOP SK ����Ʈ
//	private int convertableMileage;		// ��ȯ���� TOP Mileage ����Ʈ
//	private TopPointEtt topPoint;		// ž����Ʈ ����
//	private TopPointEtt skPoint;		// SK ����Ʈ ����
//	private TopPointEtt superPoint;		// ���� ����ž ����Ʈ (�߰�.2003.10.01 )
//	private List affiliates;			// ���� ��ü ����Ʈ ����

	/** ****************************************************************************
	 * �ֹι�ȣ
	 * @param 
	 **************************************************************************** */
	public void  setSocialId(String socialId)					{	this.socialId = socialId;						}		
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
	public void  setUsedAmt(Vector usedAmt)						{	this.usedAmt = usedAmt;							}
	/** ****************************************************************************
	 * �������࿩��
	 * @param 
	 **************************************************************************** */
	public void  setBankType(Vector bankType)					{	this.bankType = bankType;						}
	
	// ----
//	public void setName(String name)							{	this.name = name;								}
//	public void setHomeTel(String homeTel)						{	this.homeTel = homeTel;							}
//	public void setHomeZip(String homeZip)						{	this.homeZip = homeZip;							}
//	public void setPcsNo(String pcsTel)							{	this.pcsTel = pcsTel;							}
//	public void setHomeAddr1(String homeAddr1)					{	this.homeAddr1 = homeAddr1;						}
//	public void setHomeAddr2(String homeAddr2)					{	this.homeAddr2 = homeAddr2;						}
//	public void setOfficeTel(String officeTel)					{	this.officeTel = officeTel;						}
//	public void setOfficeZip(String officeZip)					{	this.officeZip = officeZip;						}
//	public void setOfficeAddr1(String officeAddr1)				{	this.officeAddr1 = officeAddr1;					}
//	public void setOfficeAddr2(String officeAddr2)				{	this.officeAddr2 = officeAddr2;					}
//	public void setCompany(String company)						{	this.company = company;							}
//	public void setDepartment(String department)				{	this.department = department;					}
//	public void setConvertableSK(int convertableSK)				{	this.convertableSK = convertableSK;				}
//	public void setConvertableMileage(int convertableMileage)	{	this.convertableMileage = convertableMileage;	}
//	public void setSkPoint(TopPointEtt skPoint)					{	this.skPoint = skPoint;							}
//	public void setNPoint(TopPointEtt superPoint)				{	this.superPoint = superPoint;					}
//	public void setTopPoint(TopPointEtt topPoint)				{	this.topPoint = topPoint;						}
//	public void setAffiliates(List affiliates)					{	this.affiliates = affiliates;					}

	/** ****************************************************************************
	 * �ֹι�ȣ
	 * @return 
	 **************************************************************************** */
	public String getSocialId()			{	return this.socialId;				}
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
	public Vector getUsedAmt()			{	return this.usedAmt;				}
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
	// -----
//	public String getName()				{	return this.name;					}
//	public String getHomeTel()			{	return this.homeTel;				}
//	public String getHomeZip()			{	return this.homeZip;				}
//	public String getPcsTel()			{	return this.pcsTel;					}
//	public String getHomeAddr1()		{	return this.homeAddr1;				}
//	public String getHomeAddr2()		{	return this.homeAddr2;				}
//	public String getOfficeTel()		{	return this.officeTel;				}
//	public String getOfficeZip()		{	return this.officeZip;				}
//	public String getOfficeAddr1()		{	return this.officeAddr1;			}
//	public String getOfficeAddr2()		{	return this.officeAddr2;			}
//	public String getCompany()			{	return this.company;				}
//	public String getDepartment()		{	return this.department;				}
//	public int getConvertableSK()		{	return this.convertableSK;			}
//	public int getConvertableMileage()	{	return this.convertableMileage;		}
//	public TopPointEtt getSkPoint()		{	return this.skPoint;				}
//	public TopPointEtt getNPoint()		{	return this.superPoint;				}
//	public TopPointEtt getTopPoint()	{	return this.topPoint;				}
//	public List getAffiliates()			{	return this.affiliates;				}

}