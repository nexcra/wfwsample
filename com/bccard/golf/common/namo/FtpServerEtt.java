/******************************************************************************
* 이 소스는 ㈜비씨카드 소유입니다.
* 이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
* 작성 : 2004.08.16 [조용국(ykcho@e4net.net)]
* 내용 : FTP 서버 정보 보관 Ett
* 수정 : 
* 내용 : 
******************************************************************************/
package com.bccard.golf.common.namo;

import com.bccard.waf.core.AbstractEntity;

/** ****************************************************************************
 * FTP 서버 정보 보관 엔티티.
 * @author 조용국(ykcho@e4net.net)
 * @version 2004.08.16
 **************************************************************************** */
public class FtpServerEtt extends AbstractEntity {
    /** 서버     */ private final String server;
    /** 사용자   */ private final String user;
    /** 패스워드 */ private final String pwd;
    /** 디렉토리 */ private final String dir;

    /** ***********************************************************************
    * FTP 서버 정보 생성.
    * @param server 서버
    * @param user 사용자
    * @param pwd 패스워드
    * @param dir 디렉토리
    ************************************************************************ */
    public FtpServerEtt(String server, String user, String pwd, String dir) {
        this.server = server;
        this.user = user;
        this.pwd = pwd;
        this.dir = dir;
    }

    /** ***********************************************************************
    * FTP 서버 정보 반환.
    * @return 서버
    ************************************************************************ */
    public String getServer() { return this.server; }

    /** ***********************************************************************
    * FTP 서버 사용자 정보 반환.
    * @return 서버
    ************************************************************************ */
    public String getUser() { return this.user; }

    /** ***********************************************************************
    * FTP 서버 패스워드 정보 반환.
    * @return 서버
    ************************************************************************ */
    public String getPwd() { return this.pwd; }

    /** ***********************************************************************
    * FTP 서버 디렉토리 정보 반환.
    * @return 서버
    ************************************************************************ */
    public String getDir() { return this.dir; }

}