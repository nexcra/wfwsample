/*******************************************************************************
 *   Ŭ������ : TopPointInfoEtt 
 *   �� �� �� : 
 *   ��    �� : 
 *   ������� : 
 *   �ۼ����� : 
 ********************************** �������� ************************************
 *	����		����		�ۼ���		�������
 *******************************************************************************/
package com.bccard.golf.common.login;

/**
 * ���� ������� TOP ����Ʈ ���� 
 */

import java.util.List;



/******************************************************************************
* Golf : TopPointInfoEtt
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

 
public class TopPointInfoEtt implements java.io.Serializable {

	private String name;				// ����/ȸ���
	private String homeTel;				// ���� ��ȭ��ȣ
	private String homeZip;				// ���� �����ȣ
	private String homeAddr1;			// ���� �����ȣ �ּ�
	private String homeAddr2;			// ���� �� ���� �ּ�
	private String officeTel;			// ���� ��ȭ��ȣ
	private String officeZip;			// ���� �����ȣ
	private String officeAddr1;			// ���� �����ȣ �ּ�
	private String officeAddr2;			// ���� �� ���� �ּ�
	private String company;				// ȸ���
	private String department;			// �μ���
	private String pcsTel;				// �ڵ�����ȣ
	private int convertableSK;			// ��ȯ���� TOP SK ����Ʈ
	private int convertableMileage;		// ��ȯ���� TOP Mileage ����Ʈ
	private TopPointEtt topPoint;		// ž����Ʈ ����
	private TopPointEtt skPoint;		//  SK ����Ʈ ����
	private TopPointEtt superPoint;		// ���� ����ž ����Ʈ (�߰�.2003.10.01 )
	private List affiliates;			// ���� ��ü ����Ʈ ����

	/**
	 *  setName
	 **/
	public void setName(String name)							{	this.name = name;								}
	/**
	 *  setHomeTel
	 **/
	public void setHomeTel(String homeTel)						{	this.homeTel = homeTel;							}
	/**
	 *  setHomeZip
	 **/
	public void setHomeZip(String homeZip)						{	this.homeZip = homeZip;							}
	/**
	 *  setPcsNo
	 **/
	public void setPcsNo(String pcsTel)							{	this.pcsTel = pcsTel;							}
	/**
	 *  setHomeAddr1
	 **/
	public void setHomeAddr1(String homeAddr1)					{	this.homeAddr1 = homeAddr1;						}
	/**
	 *  setHomeAddr2
	 **/
	public void setHomeAddr2(String homeAddr2)					{	this.homeAddr2 = homeAddr2;						}
	/**
	 *  setOfficeTel
	 **/
	public void setOfficeTel(String officeTel)					{	this.officeTel = officeTel;						}
	/**
	 *  setOfficeZip
	 **/
	public void setOfficeZip(String officeZip)					{	this.officeZip = officeZip;						}
	/**
	 *  setOfficeAddr1
	 **/
	public void setOfficeAddr1(String officeAddr1)				{	this.officeAddr1 = officeAddr1;					}
	/**
	 *  setOfficeAddr2
	 **/
	public void setOfficeAddr2(String officeAddr2)				{	this.officeAddr2 = officeAddr2;					}
	/**
	 *  setCompany
	 **/
	public void setCompany(String company)						{	this.company = company;							}
	/**
	 *  setDepartment
	 **/
	public void setDepartment(String department)				{	this.department = department;					}
	/**
	 *  setConvertableSK
	 **/
	public void setConvertableSK(int convertableSK)				{	this.convertableSK = convertableSK;				}
	/**
	 *  setConvertableMileage
	 **/
	public void setConvertableMileage(int convertableMileage)	{	this.convertableMileage = convertableMileage;	}
	/**
	 *  setSkPoint
	 **/
	public void setSkPoint(TopPointEtt skPoint)					{	this.skPoint = skPoint;							}
	/**
	 *  setNPoint
	 **/
	public void setNPoint(TopPointEtt superPoint)				{	this.superPoint = superPoint;					}
	/**
	 *  setTopPoint
	 **/
	public void setTopPoint(TopPointEtt topPoint)				{	this.topPoint = topPoint;						}
	/**
	 *  setAffiliates
	 **/
	public void setAffiliates(List affiliates)					{	this.affiliates = affiliates;					}

	/**
	 *  getName
	 **/
	public String getName()				{	return name;				}
	/**
	 *  getHomeTel
	 **/
	public String getHomeTel()			{	return homeTel;				}
	/**
	 *  getHomeZip
	 **/
	public String getHomeZip()			{	return homeZip;				}
	/**
	 *  getPcsTel
	 **/
	public String getPcsTel()			{	return pcsTel;				}
	/**
	 *  getHomeAddr1
	 **/
	public String getHomeAddr1()		{	return homeAddr1;			}
	/**
	 *  getHomeAddr2
	 **/
	public String getHomeAddr2()		{	return homeAddr2;			}
	/**
	 *  getOfficeTel
	 **/
	public String getOfficeTel()		{	return officeTel;			}
	/**
	 *  getOfficeZip
	 **/
	public String getOfficeZip()		{	return officeZip;			}
	/**
	 *  getOfficeAddr1
	 **/
	public String getOfficeAddr1()		{	return officeAddr1;			}
	/**
	 *  getOfficeAddr2
	 **/
	public String getOfficeAddr2()		{	return officeAddr2;			}
	/**
	 *  getCompany
	 **/
	public String getCompany()			{	return company;				}
	/**
	 *  getDepartment
	 **/
	public String getDepartment()		{	return department;			}
	/**
	 *  getConvertableSK
	 **/
	public int getConvertableSK()		{	return convertableSK;		}
	/**
	 *  getConvertableMileage
	 **/
	public int getConvertableMileage()	{	return convertableMileage;	}
	/**
	 *  getSkPoint
	 **/
	public TopPointEtt getSkPoint()		{	return skPoint;				}
	/**
	 *  getNPoint
	 **/
	public TopPointEtt getNPoint()		{	return superPoint;			}
	/**
	 *  getTopPoint
	 **/
	public TopPointEtt getTopPoint()	{	return topPoint;			}
	/**
	 *  getAffiliates
	 **/
	public List getAffiliates()			{	return affiliates;			}

	/**
	 *  toString
	 **/
	public String toString() {
		return "";
/*
		StringBuffer buf = new StringBuffer();
		buf.append("{TopPoints:");
		for (int i=0; i<toppoint.length; i++) buf.append(toppoint[i]);
		buf.append("}\n");
		buf.append("{Affiliated:");
		for (int i=0; i<affiliated.length; i++) buf.append(affiliated[i]);
		buf.append("}\n");
		return buf.toString();
*/
	}
}

