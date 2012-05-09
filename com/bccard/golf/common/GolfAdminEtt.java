/***************************************************************************************************
*   Ŭ������  : EtaxAdminEtt
*   �ۼ���    : csj007
*   ����      : �񾾰����� �������� Entity
*   �������  : ���ī�� ���ͳݼ���
*   �ۼ�����  : 2008.07.28
************************** �����̷� ****************************************************************
*   ����      :	2008.09.01.
*   �������  : extends AbstractEntity
***************************************************************************************************/
package com.bccard.golf.common;

/******************************************************************************
* Golf : GolfAdminEtt.
* @author	(��)�̵������
* @version	1.0
******************************************************************************/

public class GolfAdminEtt extends GolfSessionEtt {
	private String jumin_no;			// �ֹι�ȣ
	private String join_date;			// ������
	private String mgr_clss;			// 01: �񾾰�����,02: ȸ�����������, 03: ���漼 ������, 04: ���� ������
	private String usr_blon_code;		// ����ü������:����ڵ�, ȸ�����������:��������ȣ
	private String usr_part_nm;			// �ҼӸ�
	private String topMenu;				// ������ž�޴� ����ó�� 
	private String adm_clss;			// ������ Ŭ���� -> master => �ְ������ 

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