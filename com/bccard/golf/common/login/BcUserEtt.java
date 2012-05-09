/*******************************************************************************
*   클래스명  : 비씨카드닷컴 세션빈의 최상위 클래스
*   작성자    : 조용국
*   내용      : 로그인 처리를 위한 사용자 정보를 담은 엔티티
*   적용범위  : 비씨카드닷컴
*   작성일자  : 2004.02.17
************************** 수정이력 ********************************************
*    일자      버전   작성자   변경사항
*
*******************************************************************************/
package com.bccard.golf.common.login;

import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.*;
import com.bccard.waf.core.AbstractEntity;

/** ****************************************************************************
 * 비씨카드닷컴 세션빈의 최상위 클래스
 * @version 2004.02.17
 * @author  조용국
 **************************************************************************** */
public class BcUserEtt extends AbstractEntity implements HttpSessionBindingListener {
    // 매번 갱신되는 값들
    /** 접근한 메뉴를 이용할 사용자 분류 정보 "IndMember","StroeMember",""*/
    protected String skind;
    /** 탑메뉴(헤더)를 표시하기 위한 사용자 분류 정보 "IndMember","StroeMember",""*/
    protected String menu_member_clss;

    // 이하 사용자별 고정값

    /** 로그인여부 */
    protected boolean login;
    /** 로그인시각 */
    protected String logintime;
    /** 회원아이디 */
    protected String account;
	/** Member Id*/
	protected String memid;
    /** 회원이름 */
    protected String memberName;
    /** 회원구분 */
    protected String memberType;
    /** 환영페이지 */
    protected String memberService;
    /** 접속IP */
    protected String memberIP;

    // 여기서부터 database 추가 정보
    /** 패스워드 */
    protected byte[] passwd;
    /** 사이트클래스 */
    protected String site_clss;
    /** 사용자분류 */
    protected String member_clss;
    /** 사업자등록번호 */
    protected String bizregno;
    /** 주민등록번호 */
    protected String socid;
    /** 패스원드 질문 */
    protected String passwdq;
    /** 패스워드 답변 */
    protected String passwda;
    /** 패스워드 불일치 횟수 */
    protected int pswderr_cnt;
    /** 로그인 횟수 */
    protected long logcount;
    /** Db존재여부 **/
    protected boolean exist;
    /** 패스워드일치여부 **/
    protected boolean checkPasswd;
    /** 인터넷DB 성명  **/
    protected String interName;

// ********************************************************************
// TOP POINT관련 Session값 추가, Writted By Lee Eun Ho, 2005-07-08, Fri
// ********************************************************************
	// TOP POINT의 UserInfo값 추가,
	private String lastAccessDate;	// 마지막 접속 날짜 (lastaccess)
	private String email;					// 이메일 (email1)
	private String phone;				// 전화번호 (phone)
	private String mobile;				// 핸드폰 번호 (mobile)
	private String joinDate;			// 가입일 (regdate)

	// TOP POINT의 CardInfo값 추가,
//	private String bankNo;			// 회원사번호
//	private String cardNo;			// 카드번호
//	private String cardType;		// 카드구분 1:본인, 2:가족
//	private String bankName;		// 회원사명
//	private String joinNo;			// 제휴업체번호번호
//	private String joinName;		// 제휴카드명

	protected List cardInfoList;		// 보유카드정보값들
	private List myFavorList;		// 즐겨찾기 정보값들
// ********************************************************************

// ********************************************************************
// TOWN 관련
// ********************************************************************
	private String townAddress;		// 가입일 (regdate)

// ********************************************************************
// VIP 관련 Appended By PWT 20070907
// ********************************************************************
	protected List vipInfoList;		// VIP 카드 정보
	protected boolean vipLoaded;	// VIP 정보 로드 여부
	protected String vipMaxGrade;	// VIP 최고 등급, 00:일반, 03:e-PT 12:PT12, 30:다이아몬드, 91:인피니티

	/******************************************************************************
	 * 생성자.
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
        this.menu_member_clss = "IndMember";    // 기본적으로 IndMember의 탑메뉴를 표시하기 위한 기본 설정
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
// TOP POINT관련 Session값 추가, Writted By Lee Eun Ho, 2005-07-08, Fri
// ********************************************************************
        // TOP POINT의 UserInfo값 추가,
		this.lastAccessDate = "";
        this.email = "";
        this.phone = "";
        this.mobile = "";
        this.joinDate = "";

		// TOP POINT의 CardInfo값 추가,
//        this.bankNo = "";
//        this.cardNo = "";
//        this.cardType = "";
//        this.bankName = "";
//        this.joinNo = "";
//        this.joinName = "";
    }
	/******************************************************************************
	 * 생성자.
	 * @param cardInfoList 카드정보
	******************************************************************************/
	public BcUserEtt(List cardInfoList) {
		this.cardInfoList = cardInfoList;
	}

//	public BcUserEtt(List myFavorList) {
//		this.myFavorList = myFavorList;
//	}

// ********************************************************************
	/******************************************************************************
	 * 생성자.
	 * @param BcUserEtt 사용자정보
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
	 * @param boolean 패스워드여부
	******************************************************************************/
    public void setCheckPasswd(boolean checkPasswd) { this.checkPasswd = checkPasswd; }

	/******************************************************************************
	 * isCheckPasswd
	 * @param
	******************************************************************************/
    public boolean isCheckPasswd() { return this.checkPasswd; }

    /** ****************************************************************************
     * 로그인여부 입력.
     * @param login 로그인여부
     **************************************************************************** */
    public void setLogin(boolean login) {
        this.login = login;
    }

    /** ****************************************************************************
     * 로그인시각 입력.
     * @param logintime 로그인시각
     **************************************************************************** */
    public void setLogintime(String logintime) {
        this.logintime = logintime;
    }

    /** ****************************************************************************
     * 회원아이디 입력.
     * @param account 회원아이디
     **************************************************************************** */
    public void setAccount(String account) {
        this.account = account;
    }

    /** ****************************************************************************
     * 회원memid 입력.
     * @param account 회원아이디
     **************************************************************************** */
    public void setMemid(String memid) {
        this.memid = memid;
    }

    /** ****************************************************************************
     * 회원이름 입력.
     * @param memberName 회원이름
     **************************************************************************** */
    public void setMemberName(String memberName) {
        this.memberName = memberName.trim();
    }

    /** ****************************************************************************
     * 회원구분 입력.
     * @param memberType 회원구분
     **************************************************************************** */
    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    /** ****************************************************************************
     * 환영페이지 입력.
     * @param memberService 환영페이지
     **************************************************************************** */
    public void setMemberService(String memberService) {
        this.memberService = memberService;
    }

    /** ****************************************************************************
     * 접속IP 입력.
     * @param memberIP 접속IP
     **************************************************************************** */
    public void setMemberIP(String memberIP) {
        this.memberIP = memberIP;
    }

    /** ****************************************************************************
     * 패스워드 입력.
     * @param site_clss 패스워드
     **************************************************************************** */
    public void setPasswd(byte[] passwd) {
        this.passwd = passwd;
    }

    /** ****************************************************************************
     * 사이트클래스 입력.
     * @param site_clss 사이트클래스
     **************************************************************************** */
    public void setSite_clss(String site_clss) {
        this.site_clss = site_clss;
    }

    /** ****************************************************************************
     * 사용자분류 입력.
     * @param member_clss 사용자분류
     **************************************************************************** */
    public void setMember_clss(String member_clss) {
        this.member_clss = member_clss;
    }

	/******************************************************************************
	 * setMenu_member_clss
	 * @param String 메뉴접근권한
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
     * 사업자등록번호 입력.
     * @param bizregno 사업자등록번호
     **************************************************************************** */
    public void setBizregno(String bizregno) {
        this.bizregno = bizregno;
    }

    /** ****************************************************************************
     * 주민등록번호 입력.
     * @param socid 주민등록번호
     **************************************************************************** */
    public void setSocid(String socid) {
        this.socid = socid;
    }

    /** ****************************************************************************
     * 패스원드 질문 입력.
     * @param passwdq 패스원드 질문
     **************************************************************************** */
    public void setPasswdq(String passwdq) {
        this.passwdq = passwdq;
    }

    /** ****************************************************************************
     * 패스워드 답변 입력.
     * @param passwda 패스워드 답변
     **************************************************************************** */
    public void setPasswda(String passwda) {
        this.passwda = passwda;
    }

    /** ****************************************************************************
     * 패스워드 불일치 횟수 입력.
     * @param pawderr_cnt 패스워드 불일치 횟수
     **************************************************************************** */
    public void setPswderr_cnt(int pawderr_cnt) {
        this.pswderr_cnt = pswderr_cnt;
    }

    /** ****************************************************************************
     * 로그인 횟수 입력.
     * @param logcount 로그인 횟수
     **************************************************************************** */
    public void setLogcount(long logcount) {
        this.logcount = logcount;
    }

    /** ****************************************************************************
     * Db존재여부 입력.
     * @param exist Db존재여부
     **************************************************************************** */
    public void setExist(boolean exist) {
        this.exist = exist;
    }

    /** ****************************************************************************
     * 인터넷 DB 성명 입력 2005/02/22
     * @param 인터넷DB 성명 입력
     **************************************************************************** */
    public void setInterName(String interName) {
        this.interName = interName;
    }


// ********************************************************************
// TOP POINT관련 Session값 추가, Writted By Lee Eun Ho, 2005-07-08, Fri
// ********************************************************************
	// TOP POINT의 UserInfo값 추가,
	/******************************************************************************
	 * setLastAccessDate
	 * @param String 마지막접근일자
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

	// TOP POINT의 CardInfo값 추가,
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
     * 인터넷 DB 성명 반환 2005/02/22
     * @param 인터넷DB 성명 반환
     **************************************************************************** */
    public String getInterName() {
        return this.interName;
    }

    /** ****************************************************************************
     * 로그인여부 반환.
     * @return 로그인여부
     **************************************************************************** */
    public boolean isLogin() {
        return this.login;
    }

    /** ****************************************************************************
     * 로그인시각 반환.
     * @return 로그인시각
     **************************************************************************** */
    public String getLogintime() {
        return this.logintime;
    }

    /** ****************************************************************************
     * 회원아이디 반환.
     * @return 회원아이디
     **************************************************************************** */
    public String getAccount() {
        return this.account;
    }

    /** ****************************************************************************
     * 회원아이디 반환.
     * @return 회원아이디
     **************************************************************************** */
    public String getMemid() {
        return this.memid;
    }

    /** ****************************************************************************
     * 회원이름 반환.
     * @return 회원이름
     **************************************************************************** */
    public String getMemberName() {
        return this.memberName;
    }

    /** ****************************************************************************
     * 회원구분 반환.
     * @return 회원구분
     **************************************************************************** */
    public String getMemberType() {
        return this.memberType;
    }

    /** ****************************************************************************
     * 환영페이지 반환.
     * @return 환영페이지
     **************************************************************************** */
    public String getMemberService() {
        return this.memberService;
    }

    /** ****************************************************************************
     * 접속IP 반환.
     * @return 접속IP
     **************************************************************************** */
    public String getMemberIP() {
        return this.memberIP;
    }

    /** ****************************************************************************
     * 패스워드 반환.
     * @return 패스워드
     **************************************************************************** */
    public byte[] getPasswd() {
        return this.passwd;
    }

    /** ****************************************************************************
     * 사이트클래스 반환.
     * @return 사이트클래스
     **************************************************************************** */
    public String getSite_clss() {
        return this.site_clss;
    }

    /** ****************************************************************************
     * 사용자분류 반환.
     * @return 사용자분류
     **************************************************************************** */
    public String getMember_clss() {
        return this.member_clss;
    }

    /** ****************************************************************************
     * 사업자등록번호 반환.
     * @return 사업자등록번호
     **************************************************************************** */
    public String getBizregno() {
        return this.bizregno;
    }

    /** ****************************************************************************
     * 주민등록번호 반환.
     * @return 주민등록번호
     **************************************************************************** */
    public String getSocid() {
        return this.socid;
    }

    /** ****************************************************************************
     * 패스원드 질문 반환.
     * @return 패스원드 질문
     **************************************************************************** */
    public String getPasswdq() {
        return this.passwdq;
    }

    /** ****************************************************************************
     * 패스워드 답변 반환.
     * @return 패스워드 답변
     **************************************************************************** */
    public String getPasswda() {
        return this.passwda;
    }

    /** ****************************************************************************
     * 패스워드 불일치 횟수 반환.
     * @return 패스워드 불일치 횟수
     **************************************************************************** */
    public int getPswderr_cnt() {
        return this.pswderr_cnt;
    }

    /** ****************************************************************************
     * 로그인 횟수 반환.
     * @return 로그인 횟수
     **************************************************************************** */
    public long getLogcount() {
        return this.logcount;
    }

    /** ****************************************************************************
     * Db존재여부 반환.
     * @return Db존재여부
     **************************************************************************** */
    public boolean isExist() {
        return this.exist;
    }

// ********************************************************************
// TOP POINT관련 Session값 추가, Writted By Lee Eun Ho, 2005-07-08, Fri
// ********************************************************************
	// TOP POINT의 UserInfo값 추가,
    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public String getLastAccessDate() {
		return lastAccessDate;
	}
    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public String getEmail() {
		return email;
	}
    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public String getPhone() {
		return phone;
	}
    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public String getMobile() {
		return mobile;
	}
    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public String getJoinDate() {
		return joinDate;
	}
	// TOP POINT의 CardInfo값 추가,
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
     *  반환.
     * @return
     **************************************************************************** */
	public List getCardInfoList() {
		return this.cardInfoList;
	}

    /** ****************************************************************************
     *  유효회원은행 카드 반환. By PWT 20070924
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
					selCardInfoList.add(record);
				}
			}
		}
		return selCardInfoList;
	}

    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public List getMyFavorList() {
		return this.myFavorList;
	}

    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public List getVipInfoList() {
		return this.vipInfoList;
	}

    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public String getTownAddress() {
		return this.townAddress;
	}

    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public String getVipMaxGrade() {
		return this.vipMaxGrade;
	}

    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
	public boolean getVipLoaded() {
		return this.vipLoaded;
	}


    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
    public void valueBound(HttpSessionBindingEvent event) {
        // 추후 멀티로그인 방지 정책이 들어가면 여기서 제어할 수 있다.
    }

    /** ****************************************************************************
     *  반환.
     * @return
     **************************************************************************** */
    public void valueUnbound(HttpSessionBindingEvent event) {
        // 추후 멀티로그인 방지 정책이 들어가면 여기서 제어할 수 있다.
    }
}
