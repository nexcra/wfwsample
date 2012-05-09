/*******************************************************************************
*   Ŭ������  : ��ī����� ���Ǻ��� �ֻ��� Ŭ����
*   �ۼ���    : ���뱹
*   ����      : �α��� ó���� ���� ����� ������ ���� ��ƼƼ
*   �������  : ��ī�����
*   �ۼ�����  : 2004.02.17
************************** �����̷� ********************************************
*    ����      ����   �ۼ���   �������
*
*******************************************************************************/
package com.bccard.golf.common.login;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.*;
import com.bccard.waf.core.AbstractEntity;

/** ****************************************************************************
 * ��ī����� ���Ǻ��� �ֻ��� Ŭ����
 * @version 2004.02.17
 * @author  ���뱹
 **************************************************************************** */
public class BcUserEtt extends AbstractEntity implements HttpSessionBindingListener {
    // �Ź� ���ŵǴ� ����
    /** ������ �޴��� �̿��� ����� �з� ���� "IndMember","StroeMember",""*/
    protected String skind;
    /** ž�޴�(���)�� ǥ���ϱ� ���� ����� �з� ���� "IndMember","StroeMember",""*/
    protected String menu_member_clss;

    // ���� ����ں� ������

    /** �α��ο��� */
    protected boolean login;
    /** �α��νð� */
    protected String logintime;
    /** ȸ�����̵� */
    protected String account;
	/** Member Id*/
	protected String memid;
    /** ȸ���̸� */
    protected String memberName;
    /** ȸ������ */
    protected String memberType;
    /** ȯ�������� */
    protected String memberService;
    /** ����IP */
    protected String memberIP;

    // ���⼭���� database �߰� ����
    /** �н����� */
    protected byte[] passwd;
    /** ����ƮŬ���� */
    protected String site_clss;
    /** ����ںз� */
    protected String member_clss;
    /** ����ڵ�Ϲ�ȣ */
    protected String bizregno;
    /** �ֹε�Ϲ�ȣ */
    protected String socid;
    /** �н����� ���� */
    protected String passwdq;
    /** �н����� �亯 */
    protected String passwda;
    /** �н����� ����ġ Ƚ�� */
    protected int pswderr_cnt;
    /** �α��� Ƚ�� */
    protected long logcount;
    /** Db���翩�� **/
    protected boolean exist;
    /** �н�������ġ���� **/
    protected boolean checkPasswd;
    /** ���ͳ�DB ����  **/
    protected String interName;

// ********************************************************************
// TOP POINT���� Session�� �߰�, Writted By Lee Eun Ho, 2005-07-08, Fri
// ********************************************************************
	// TOP POINT�� UserInfo�� �߰�,
	private String lastAccessDate;	// ������ ���� ��¥ (lastaccess)
	private String email;					// �̸��� (email1)
	private String phone;				// ��ȭ��ȣ (phone)
	private String mobile;				// �ڵ��� ��ȣ (mobile)
	private String joinDate;			// ������ (regdate)

	// TOP POINT�� CardInfo�� �߰�,
//	private String bankNo;			// ȸ�����ȣ
//	private String cardNo;			// ī���ȣ
//	private String cardType;		// ī�屸�� 1:����, 2:����
//	private String bankName;		// ȸ�����
//	private String joinNo;			// ���޾�ü��ȣ��ȣ
//	private String joinName;		// ����ī���

	protected List cardInfoList;		// ����ī����������
	private List myFavorList;		// ���ã�� ��������
// ********************************************************************

// ********************************************************************
// TOWN ����
// ********************************************************************
	private String townAddress;		// ������ (regdate)

// ********************************************************************
// VIP ���� Appended By PWT 20070907
// ********************************************************************
	protected List vipInfoList;		// VIP ī�� ����
	protected boolean vipLoaded;	// VIP ���� �ε� ����
	protected String vipMaxGrade;	// VIP �ְ� ���, 00:�Ϲ�, 03:e-PT 12:PT12, 30:���̾Ƹ��, 91:���Ǵ�Ƽ

	/******************************************************************************
	 * ������.
	 * @param
	******************************************************************************/
    public BcUserEtt() {
        this.login = false;
        this.logintime = null;
        this.account = null;
		this.memid = null;
        this.memberName = null;
        this.memberType = null;
        this.memberService = null;
        this.memberIP = null;
        this.passwd = null;
        this.skind = null;
        this.site_clss = null;
        this.member_clss = null;
        this.menu_member_clss = "IndMember";    // �⺻������ IndMember�� ž�޴��� ǥ���ϱ� ���� �⺻ ����
        this.bizregno = null;
        this.socid = null;
        this.passwdq = null;
        this.passwda = null;
        this.pswderr_cnt = 0;
        this.logcount = 0;
        this.exist = false;
        this.checkPasswd = false;
        this.interName = "";
		this.townAddress = "";
		this.vipLoaded = false;
		this.vipMaxGrade = "00";

		//this.test = null;

// ********************************************************************
// TOP POINT���� Session�� �߰�, Writted By Lee Eun Ho, 2005-07-08, Fri
// ********************************************************************
        // TOP POINT�� UserInfo�� �߰�,
		this.lastAccessDate = "";
        this.email = "";
        this.phone = "";
        this.mobile = "";
        this.joinDate = "";

		// TOP POINT�� CardInfo�� �߰�,
//        this.bankNo = "";
//        this.cardNo = "";
//        this.cardType = "";
//        this.bankName = "";
//        this.joinNo = "";
//        this.joinName = "";
    }
	/******************************************************************************
	 * ������.
	 * @param cardInfoList ī������
	******************************************************************************/
	public BcUserEtt(List cardInfoList) {
		this.cardInfoList = cardInfoList;
	}

//	public BcUserEtt(List myFavorList) {
//		this.myFavorList = myFavorList;
//	}

// ********************************************************************
	/******************************************************************************
	 * ������.
	 * @param BcUserEtt ���������
	******************************************************************************/
	public BcUserEtt(BcUserEtt ett) {}

	/******************************************************************************
	 * setSkind
	 * @param String
	******************************************************************************/
    public void setSkind(String skind) { this.skind = skind; }

	/******************************************************************************
	 * getSkind
	 * @param
	******************************************************************************/
    public String getSkind() { return this.skind; }

	/******************************************************************************
	 * setCheckPasswd
	 * @param boolean �н����忩��
	******************************************************************************/
    public void setCheckPasswd(boolean checkPasswd) { this.checkPasswd = checkPasswd; }

	/******************************************************************************
	 * isCheckPasswd
	 * @param
	******************************************************************************/
    public boolean isCheckPasswd() { return this.checkPasswd; }

    /** ****************************************************************************
     * �α��ο��� �Է�.
     * @param login �α��ο���
     **************************************************************************** */
    public void setLogin(boolean login) {
        this.login = login;
    }

    /** ****************************************************************************
     * �α��νð� �Է�.
     * @param logintime �α��νð�
     **************************************************************************** */
    public void setLogintime(String logintime) {
        this.logintime = logintime;
    }

    /** ****************************************************************************
     * ȸ�����̵� �Է�.
     * @param account ȸ�����̵�
     **************************************************************************** */
    public void setAccount(String account) {
        this.account = account;
    }

    /** ****************************************************************************
     * ȸ��memid �Է�.
     * @param account ȸ�����̵�
     **************************************************************************** */
    public void setMemid(String memid) {
        this.memid = memid;
    }

    /** ****************************************************************************
     * ȸ���̸� �Է�.
     * @param memberName ȸ���̸�
     **************************************************************************** */
    public void setMemberName(String memberName) {
        this.memberName = memberName.trim();
    }

    /** ****************************************************************************
     * ȸ������ �Է�.
     * @param memberType ȸ������
     **************************************************************************** */
    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    /** ****************************************************************************
     * ȯ�������� �Է�.
     * @param memberService ȯ��������
     **************************************************************************** */
    public void setMemberService(String memberService) {
        this.memberService = memberService;
    }

    /** ****************************************************************************
     * ����IP �Է�.
     * @param memberIP ����IP
     **************************************************************************** */
    public void setMemberIP(String memberIP) {
        this.memberIP = memberIP;
    }

    /** ****************************************************************************
     * �н����� �Է�.
     * @param site_clss �н�����
     **************************************************************************** */
    public void setPasswd(byte[] passwd) {
        this.passwd = passwd;
    }

    /** ****************************************************************************
     * ����ƮŬ���� �Է�.
     * @param site_clss ����ƮŬ����
     **************************************************************************** */
    public void setSite_clss(String site_clss) {
        this.site_clss = site_clss;
    }

    /** ****************************************************************************
     * ����ںз� �Է�.
     * @param member_clss ����ںз�
     **************************************************************************** */
    public void setMember_clss(String member_clss) {
        this.member_clss = member_clss;
    }

	/******************************************************************************
	 * setMenu_member_clss
	 * @param String �޴����ٱ���
	******************************************************************************/
    public void setMenu_member_clss(String menu_member_clss) {
        this.menu_member_clss = menu_member_clss;
    }

	/******************************************************************************
	 * setMenu_member_clss
	 * @param
	******************************************************************************/
    public String getMenu_member_clss() {
        return this.menu_member_clss;
    }

    /** ****************************************************************************
     * ����ڵ�Ϲ�ȣ �Է�.
     * @param bizregno ����ڵ�Ϲ�ȣ
     **************************************************************************** */
    public void setBizregno(String bizregno) {
        this.bizregno = bizregno;
    }

    /** ****************************************************************************
     * �ֹε�Ϲ�ȣ �Է�.
     * @param socid �ֹε�Ϲ�ȣ
     **************************************************************************** */
    public void setSocid(String socid) {
        this.socid = socid;
    }

    /** ****************************************************************************
     * �н����� ���� �Է�.
     * @param passwdq �н����� ����
     **************************************************************************** */
    public void setPasswdq(String passwdq) {
        this.passwdq = passwdq;
    }

    /** ****************************************************************************
     * �н����� �亯 �Է�.
     * @param passwda �н����� �亯
     **************************************************************************** */
    public void setPasswda(String passwda) {
        this.passwda = passwda;
    }

    /** ****************************************************************************
     * �н����� ����ġ Ƚ�� �Է�.
     * @param pawderr_cnt �н����� ����ġ Ƚ��
     **************************************************************************** */
    public void setPswderr_cnt(int pawderr_cnt) {
        this.pswderr_cnt = pswderr_cnt;
    }

    /** ****************************************************************************
     * �α��� Ƚ�� �Է�.
     * @param logcount �α��� Ƚ��
     **************************************************************************** */
    public void setLogcount(long logcount) {
        this.logcount = logcount;
    }

    /** ****************************************************************************
     * Db���翩�� �Է�.
     * @param exist Db���翩��
     **************************************************************************** */
    public void setExist(boolean exist) {
        this.exist = exist;
    }

    /** ****************************************************************************
     * ���ͳ� DB ���� �Է� 2005/02/22
     * @param ���ͳ�DB ���� �Է�
     **************************************************************************** */
    public void setInterName(String interName) {
        this.interName = interName;
    }


// ********************************************************************
// TOP POINT���� Session�� �߰�, Writted By Lee Eun Ho, 2005-07-08, Fri
// ********************************************************************
	// TOP POINT�� UserInfo�� �߰�,
	/******************************************************************************
	 * setLastAccessDate
	 * @param String ��������������
	******************************************************************************/
	public void setLastAccessDate(String lastAccessDate) {
		this.lastAccessDate = lastAccessDate;
	}

	/******************************************************************************
	 * setEmail
	 * @param
	******************************************************************************/
	public void setEmail(String email) {
		this.email = email;
	}

	/******************************************************************************
	 * setPhone
	 * @param
	******************************************************************************/
	public void setPhone(String phone) {
		this.phone = phone;
	}

	/******************************************************************************
	 * setMobile
	 * @param
	******************************************************************************/
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/******************************************************************************
	 * setJoinDate
	 * @param
	******************************************************************************/
	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}

	// TOP POINT�� CardInfo�� �߰�,
//	public void setBankNo(String bankNo) {
//		this.bankNo = bankNo;
//	}
//	public void setCardNo(String cardNo) {
//		this.cardNo = cardNo;
//	}
//	public void setCardType(String cardType) {
//		this.cardType = cardType;
//	}
//	public void setBankName(String bankName) {
//		this.bankName = bankName;
//	}
//	public void setJoinNo(String joinNo) {
//		this.joinNo = joinNo;
//	}
//	public void setJoinName(String joinName) {
//		this.joinName = joinName;
//	}
// *******************************************************************

	/******************************************************************************
	 * setCardInfoList
	 * @param
	******************************************************************************/
	public void setCardInfoList(List cardInfoList) {
		this.cardInfoList = cardInfoList;
	}

	/******************************************************************************
	 * setMyFavorList
	 * @param
	******************************************************************************/
	public void setMyFavorList(List myFavorList) {
		this.myFavorList = myFavorList;
	}

	/******************************************************************************
	 * setVipInfoList
	 * @param
	******************************************************************************/
	public void setVipInfoList(List vipInfoList) {
		this.vipInfoList = vipInfoList;
	}


	/******************************************************************************
	 * setTownAddress
	 * @param
	******************************************************************************/
	public void setTownAddress(String townAddress) {
		this.townAddress = townAddress;
	}

	/******************************************************************************
	 * setVipLoaded
	 * @param
	******************************************************************************/
	public void setVipLoaded(boolean vipLoaded) {
		this.vipLoaded = vipLoaded;
	}
	/******************************************************************************
	 * setVipMaxGrade
	 * @param
	******************************************************************************/
	public void setVipMaxGrade(String vipMaxGrade) {
		this.vipMaxGrade = vipMaxGrade;
	}

    /** ****************************************************************************
     * ���ͳ� DB ���� ��ȯ 2005/02/22
     * @param ���ͳ�DB ���� ��ȯ
     **************************************************************************** */
    public String getInterName() {
        return this.interName;
    }

    /** ****************************************************************************
     * �α��ο��� ��ȯ.
     * @return �α��ο���
     **************************************************************************** */
    public boolean isLogin() {
        return this.login;
    }

    /** ****************************************************************************
     * �α��νð� ��ȯ.
     * @return �α��νð�
     **************************************************************************** */
    public String getLogintime() {
        return this.logintime;
    }

    /** ****************************************************************************
     * ȸ�����̵� ��ȯ.
     * @return ȸ�����̵�
     **************************************************************************** */
    public String getAccount() {
        return this.account;
    }

    /** ****************************************************************************
     * ȸ�����̵� ��ȯ.
     * @return ȸ�����̵�
     **************************************************************************** */
    public String getMemid() {
        return this.memid;
    }

    /** ****************************************************************************
     * ȸ���̸� ��ȯ.
     * @return ȸ���̸�
     **************************************************************************** */
    public String getMemberName() {
        return this.memberName;
    }

    /** ****************************************************************************
     * ȸ������ ��ȯ.
     * @return ȸ������
     **************************************************************************** */
    public String getMemberType() {
        return this.memberType;
    }

    /** ****************************************************************************
     * ȯ�������� ��ȯ.
     * @return ȯ��������
     **************************************************************************** */
    public String getMemberService() {
        return this.memberService;
    }

    /** ****************************************************************************
     * ����IP ��ȯ.
     * @return ����IP
     **************************************************************************** */
    public String getMemberIP() {
        return this.memberIP;
    }

    /** ****************************************************************************
     * �н����� ��ȯ.
     * @return �н�����
     **************************************************************************** */
    public byte[] getPasswd() {
        return this.passwd;
    }

    /** ****************************************************************************
     * ����ƮŬ���� ��ȯ.
     * @return ����ƮŬ����
     **************************************************************************** */
    public String getSite_clss() {
        return this.site_clss;
    }

    /** ****************************************************************************
     * ����ںз� ��ȯ.
     * @return ����ںз�
     **************************************************************************** */
    public String getMember_clss() {
        return this.member_clss;
    }

    /** ****************************************************************************
     * ����ڵ�Ϲ�ȣ ��ȯ.
     * @return ����ڵ�Ϲ�ȣ
     **************************************************************************** */
    public String getBizregno() {
        return this.bizregno;
    }

    /** ****************************************************************************
     * �ֹε�Ϲ�ȣ ��ȯ.
     * @return �ֹε�Ϲ�ȣ
     **************************************************************************** */
    public String getSocid() {
        return this.socid;
    }

    /** ****************************************************************************
     * �н����� ���� ��ȯ.
     * @return �н����� ����
     **************************************************************************** */
    public String getPasswdq() {
        return this.passwdq;
    }

    /** ****************************************************************************
     * �н����� �亯 ��ȯ.
     * @return �н����� �亯
     **************************************************************************** */
    public String getPasswda() {
        return this.passwda;
    }

    /** ****************************************************************************
     * �н����� ����ġ Ƚ�� ��ȯ.
     * @return �н����� ����ġ Ƚ��
     **************************************************************************** */
    public int getPswderr_cnt() {
        return this.pswderr_cnt;
    }

    /** ****************************************************************************
     * �α��� Ƚ�� ��ȯ.
     * @return �α��� Ƚ��
     **************************************************************************** */
    public long getLogcount() {
        return this.logcount;
    }

    /** ****************************************************************************
     * Db���翩�� ��ȯ.
     * @return Db���翩��
     **************************************************************************** */
    public boolean isExist() {
        return this.exist;
    }

// ********************************************************************
// TOP POINT���� Session�� �߰�, Writted By Lee Eun Ho, 2005-07-08, Fri
// ********************************************************************
	// TOP POINT�� UserInfo�� �߰�,
    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public String getLastAccessDate() {
		return lastAccessDate;
	}
    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public String getEmail() {
		return email;
	}
    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public String getPhone() {
		return phone;
	}
    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public String getMobile() {
		return mobile;
	}
    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public String getJoinDate() {
		return joinDate;
	}
	// TOP POINT�� CardInfo�� �߰�,
//	public String getBankNo() {
//		return bankNo;
//	}
//	public String getCardNo() {
//		return cardNo;
//	}
//	public String getCardType() {
//		return cardType;
//	}
//	public String getBankName() {
//		return bankName;
//	}
//	public String getJoinNo() {
//		return joinNo;
//	}
//	public String getJoinName() {
//		return joinName;
//	}
    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public List getCardInfoList() {
		return this.cardInfoList;
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
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public List getMyFavorList() {
		return this.myFavorList;
	}

    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public List getVipInfoList() {
		return this.vipInfoList;
	}

    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public String getTownAddress() {
		return this.townAddress;
	}

    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public String getVipMaxGrade() {
		return this.vipMaxGrade;
	}

    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
	public boolean getVipLoaded() {
		return this.vipLoaded;
	}


    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
    public void valueBound(HttpSessionBindingEvent event) {
        // ���� ��Ƽ�α��� ���� ��å�� ���� ���⼭ ������ �� �ִ�.
    }

    /** ****************************************************************************
     *  ��ȯ.
     * @return
     **************************************************************************** */
    public void valueUnbound(HttpSessionBindingEvent event) {
        // ���� ��Ƽ�α��� ���� ��å�� ���� ���⼭ ������ �� �ִ�.
    }
}
