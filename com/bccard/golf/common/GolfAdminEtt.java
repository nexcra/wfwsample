/***************************************************************************************************
*   클래스명  : EtaxAdminEtt
*   작성자    : csj007
*   내용      : 비씨관리자 세션정보 Entity
*   적용범위  : 기업카드 인터넷서비스
*   작성일자  : 2008.07.28
************************** 수정이력 ****************************************************************
*   일자      :	2008.09.01.
*   변경사항  : extends AbstractEntity
***************************************************************************************************/
package com.bccard.golf.common;

/******************************************************************************
* Golf : GolfAdminEtt.
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/

public class GolfAdminEtt extends GolfSessionEtt {
	private String jumin_no;			// 주민번호
	private String join_date;			// 가입일
	private String mgr_clss;			// 01: 비씨관리자,02: 회원은행관리자, 03: 지방세 관리자, 04: 국세 관리자
	private String usr_blon_code;		// 지자체관리자:기관코드, 회원은행관리자:영업점번호
	private String usr_part_nm;			// 소속명
	private String topMenu;				// 관리자탑메뉴 세션처리 
	private String adm_clss;			// 관리자 클래스 -> master => 최고관리자 

	/*************************************************************/
	private String enc_key;		// ENCRYPTED MASTER KEY
	//	private String admin_class;	// EXACTLY SAME FOR admin_class DECLARED ABOVE
	/*************************************************************/

	/** ***********************************************************************
	 * EtaxUserEtt
	 * @param 
	 * @return  
	 *********************************************************************** */
	public GolfAdminEtt() {
		this.jumin_no 		="";
		this.join_date 		="";
		this.mgr_clss 		="";
		this.usr_blon_code 	="";
		this.usr_part_nm 	="";
		this.enc_key		= "";
		this.adm_clss		= "";
	}

	/**
	 * @return the adm_clss
	 */ 
	public void setAdm_clss(String adm_clss) {
		this.adm_clss = adm_clss;
	}
	
	/**
	 * @return the adm_clss
	 */
	public String getAdm_clss() {
		return adm_clss;
	}

	/**
	 * @return the enc_key
	 */
	public String getEnc_key() {
		return enc_key;
	}

	/**
	 * @param enc_key the enc_key to set
	 */
	public void setEnc_key(String enc_key) {
		this.enc_key = enc_key;
	}


	/**
	 * @return the join_date
	 */
	public String getJoin_date() {
		return join_date;
	}

	/**
	 * @param join_date the join_date to set
	 */
	public void setJoin_date(String join_date) {
		this.join_date = join_date;
	}

	/**
	 * @return the jumin_no
	 */
	public String getJumin_no() {
		return jumin_no;
	}

	/**
	 * @param jumin_no the jumin_no to set
	 */
	public void setJumin_no(String jumin_no) {
		this.jumin_no = jumin_no;
	}



	/**
	 * @return the mgr_clss
	 */
	public String getMgr_clss() {
		return mgr_clss;
	}

	/**
	 * @param mgr_clss the mgr_clss to set
	 */
	public void setMgr_clss(String mgr_clss) {
		this.mgr_clss = mgr_clss;
	}


	/**
	 * @return the usr_blon_code
	 */
	public String getUsr_blon_code() {
		return usr_blon_code;
	}

	/**
	 * @param usr_blon_code the usr_blon_code to set
	 */
	public void setUsr_blon_code(String usr_blon_code) {
		this.usr_blon_code = usr_blon_code;
	}

	/**
	 * @return the usr_part_nm
	 */
	public String getUsr_part_nm() {
		return usr_part_nm;
	}

	/**
	 * @param usr_part_nm the usr_part_nm to set
	 */
	public void setUsr_part_nm(String usr_part_nm) {
		this.usr_part_nm = usr_part_nm;
	}
	
	/**
	 * @param usr_part_nm the usr_part_nm to set	 mall
	 */
	public void setTopMenu(String topMenu) { this.topMenu = topMenu; }
	/**
	 * @param usr_part_nm the usr_part_nm to set mall
	 */
	public String getTopMenu()    { return topMenu   ; }	
	
	
}