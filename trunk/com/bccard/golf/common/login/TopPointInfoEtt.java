/*******************************************************************************
 *   클래스명 : TopPointInfoEtt 
 *   작 성 자 : 
 *   내    용 : 
 *   적용범위 : 
 *   작성일자 : 
 ********************************** 수정사항 ************************************
 *	일자		버전		작성자		변경사항
 *******************************************************************************/
package com.bccard.golf.common.login;

/**
 * 개인 사용자의 TOP 포인트 정보 
 */

import java.util.List;



/******************************************************************************
* Golf : TopPointInfoEtt
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/

 
public class TopPointInfoEtt implements java.io.Serializable {

	private String name;				// 성명/회사명
	private String homeTel;				// 자택 전화번호
	private String homeZip;				// 자택 우편번호
	private String homeAddr1;			// 자택 우편번호 주소
	private String homeAddr2;			// 자택 동 이하 주소
	private String officeTel;			// 직장 전화번호
	private String officeZip;			// 직장 우편번호
	private String officeAddr1;			// 직장 우편번호 주소
	private String officeAddr2;			// 직장 동 이하 주소
	private String company;				// 회사명
	private String department;			// 부서명
	private String pcsTel;				// 핸드폰번호
	private int convertableSK;			// 전환가능 TOP SK 포인트
	private int convertableMileage;		// 전환가능 TOP Mileage 포인트
	private TopPointEtt topPoint;		// 탑포인트 정보
	private TopPointEtt skPoint;		//  SK 포인트 정보
	private TopPointEtt superPoint;		// 농협 슈퍼탑 포인트 (추가.2003.10.01 )
	private List affiliates;			// 제휴 업체 포인트 정보

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

