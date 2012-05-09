/**********************************************************************************************************************
*   클래스명  : IndUserEtt
*   작성자    : 채상직 (csj007)
*   내용      : 개인회원 entity
*   적용범위  : 로그인 및 회원정보 관리
*   작성일자  : 2004.01.19
************************** 수정이력 ***********************************************************************************
*    일자      버전   작성자   변경사항
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
	 * 생성자
	 * @param 
	 **************************************************************************** */
	public IndUserEtt(){}
	/** ****************************************************************************
	 * 생성자
	 * @param 
	 **************************************************************************** */
    public IndUserEtt(BcUserEtt bcUserEtt) { super(bcUserEtt); }

	private String socialId;			// 주민번호    
	private Vector bankListNames;		// 은행이름                
	private Vector bankListCodes;		// 은행코드                
	private Vector bankSettlement;		// 은행결제일              

	private Vector bankListEmail;		// 은행Email명세서 신청여부
	private Vector bankMemberNo;		// 회원사 회원번호
	private Vector bankAcctNo;			// 현 결제계좌번호 By PWT 20070823
	private Vector bankAcctName;		// 현 결제계좌은행 By PWT 20070823
	private Vector usedAmt;				// 최근 3개월간 청구 금액 합(sort용) By PWT 20070904
	private Vector bankType;			// 독자은행여부 0-비독자, 1-독자
	// 탑포인트 정보를 저장하기 위한 값들, Writted By, Lee Eun Ho, 2005-07-29, Friday.
//	private String name;				// 성명/회사명
//	private String homeTel;				// 자택 전화번호
//	private String homeZip;				// 자택 우편번호
//	private String homeAddr1;			// 자택 우편번호 주소
//	private String homeAddr2;			// 자택 동 이하 주소
//	private String officeTel;			// 직장 전화번호
//	private String officeZip;			// 직장 우편번호
//	private String officeAddr1;			// 직장 우편번호 주소
//	private String officeAddr2;			// 직장 동 이하 주소
//	private String company;				// 회사명
//	private String department;			// 부서명
//	private String pcsTel;				// 핸드폰번호
//	private int convertableSK;			// 전환가능 TOP SK 포인트
//	private int convertableMileage;		// 전환가능 TOP Mileage 포인트
//	private TopPointEtt topPoint;		// 탑포인트 정보
//	private TopPointEtt skPoint;		// SK 포인트 정보
//	private TopPointEtt superPoint;		// 농협 슈퍼탑 포인트 (추가.2003.10.01 )
//	private List affiliates;			// 제휴 업체 포인트 정보

	/** ****************************************************************************
	 * 주민번호
	 * @param 
	 **************************************************************************** */
	public void  setSocialId(String socialId)					{	this.socialId = socialId;						}		
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
	public void  setUsedAmt(Vector usedAmt)						{	this.usedAmt = usedAmt;							}
	/** ****************************************************************************
	 * 독자은행여부
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
	 * 주민번호
	 * @return 
	 **************************************************************************** */
	public String getSocialId()			{	return this.socialId;				}
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
	public Vector getUsedAmt()			{	return this.usedAmt;				}
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