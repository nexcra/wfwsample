/***************************************************************************************************		 
*   이 소스는 ㈜비씨카드 소유입니다.		 
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.		 
*   클래스명  : GolfLoungMemAgentDaoProc		 
*   작성자    : 장성재		 
*   내용      : 골프라운지 유료회원 기간연장 및 기간만료 처리		 
*   적용범위  : golf		 
*   업무내용  : 대상기간일경우에만 Agent를 돌린다. 운영서버는 31번에만 돈다		 
*                           1. 대상자 추출 멤버쉽 유료회원 중 기간만료자만 체크		 
*                           2. 등급변경내역 저장		 
*                           3. 대상자 개인별 확인 검증 (중복등급일경우)		 
*                                   3.1  등급이 1개일경우		 
*                                           3.1.1 화이트회원으로 강등		 
*                                   3.2 등급이 N개일경우		 
*                                           3.2.1 멤버쉽 등급 삭제 		 
*                                           3.2.2 최우선등급을 찾는다 (첫번째 데이타가 최우선등급임) 		 
*                                           3.2.3 등급변경 히스토리 저장 및 대표등급변경처리 		 
*                           4. 유료회원 기간 만료 -30일, - 1일 회원에 SMS 문자		 
*                                   4.1 SMS 문자 : OOO님 00월00일일자로 등급이 만료됩니다.로그인후 마이페이지에서 기간연장하세요		 
*                                   4.2 메일 발송		 
*                           5. 유료회원 기간 만료 - 1일 회원에 SMS 문자		 
*                                   5.1 SMS 문자 : OOO님 00월00일일자로 등급이 만료됩니다.로그인후 마이페이지에서 기간연장하세요		 
*                                   5.2 메일 발송		 
*   작성일자  : 2010-10-15		 
************************** 수정이력 ****************************************************************		 
*    일자      버전   작성자   변경사항		 
* 2010/11/04          장성재   골프투어멤버스 블랙등급인 경우 조회 대상에서 제외		 
*                             (TBGGOLFCDHD.CDHD_CTGO_SEQ_NO = 11, TBGGOLFCDHD.JOIN_CHNL ='3000')  		 
* 2010/12/21          장성재   멤버십 기간만료 안내 자동메일의 내용이 추가됨에따라 수정된 메일폼으로 등록 요청 		 
*                              변경된 내용: 유료회원 만기시점에 APT 및 TOP골프 카드 소유시 해당 유료멤버십은 종료되더라도		 
*                              카드회원으로 전환되어 서비스 이용을 계속 할수있다는 내용 공지  		 
***************************************************************************************************/		 
  		 
package com.bccard.golf.dbtao.proc.admin.member;		 
		 
import java.sql.*;		 
import java.util.*;		 
import java.io.*;		 
import java.net.*;		 
		 
//import javax.mail.MessagingException;		 
//import javax.mail.internet.AddressException;		 
import com.bccard.golf.common.AppConfig;		 
import com.bccard.golf.common.SmsSendProc;		 
import com.bccard.golf.common.mail.EmailEntity;		 
import com.bccard.golf.common.mail.EmailSend;		 
import com.bccard.waf.action.AbstractProc;		 
import com.bccard.waf.common.BaseException;		 
import com.bccard.waf.common.DateUtil;		 
import com.bccard.waf.core.WaContext;		 
		 
		 
		 
		 
public class GolfLoungMemAgentDaoProc extends AbstractProc {		 
        		 
    public static final String TITLE = "골프라운지  기간만료 처리"; 		 
    		 
    /*******************************************************************		 
     * GolfLoungMemAgentDaoProc 골프라운지  기간만료 처리 에이전트 처리 		 
     ******************************************************************/		 
    public GolfLoungMemAgentDaoProc() {}        		 
	 
    public boolean execute(WaContext context) throws BaseException {		 
	 
        Connection conn = null;		 
        boolean isNormal = false;		 
 
        try {		 
            conn = context.getDbConnection("default", null);		 
 
            debug("checkDate() 시작");		 
            if( checkDate(conn) == false ) { // 0. 이벤트기간 체크 		 
                closeConnection(conn);		 
                isNormal = true;		 
                return isNormal;                                		 
            }		 
            debug("checkDate() 종료");		 
            		 
            debug("processGolfCdhd() 시작");		 
            processGolfCdhd(context, conn); // 1. 대상자 추출 멤버쉽 유료회원 중 기간만료자만 체크		 
            debug("processGolfCdhd() 종료");		 
            		 
            //멤버쉽만 해당 (스마트 오퍼는 해당 무 - 최종욱차장)		 
            debug("processSmsEmail_30() 시작");		 
            processSmsEmail(context, conn, 30);// 4. 유료회원 기간 만료 -30일, - 1일 회원에 SMS 문자		 
            debug("processSmsEmail_30() 종료");		 
            		 
            //멤버쉽만 해당 (스마트 오퍼는 해당 무 - 최종욱차장)		 
            debug("processSmsEmail_1() 시작");		 
            processSmsEmail(context, conn, 1);// 5. 유료회원 기간 만료 - 1일 회원에 SMS 문자		 
            debug("processSmsEmail_1() 종료");		 
 
            isNormal = true;		 
                		 
        } catch (Exception e) {		 
            error("execute() : " + e.getMessage());		 
        } finally {		 
            closeConnection(conn);		 
                		 
        }		 
 
        return isNormal;		 
	 
    }        		 
	 
    /*************************************************************************		 
     * Connection close		 
     *************************************************************************/		 
    private void closeConnection(Connection conn) {		 
        if( conn != null) {		 
            try {		 
            	conn.close();		 
            } catch (Exception e) {		 
                error("closeConnection() : " + e.getMessage());		 
            }		 
        }		 
    }		 
    		 
    /*************************************************************************		 
     * 0. 이벤트기간 체크(오늘일자가 대상기간일경우만 Agent를 실행한다. 오픈시점을 데이타로 핸들링)		 
     *************************************************************************/		 
    private boolean checkDate(Connection conn ) {		 
        String sqlCheckDate = this.getCheckDateQuery();		 
        debug("sqlCheckDate : " + sqlCheckDate);		 
        		 
        PreparedStatement pstmtCheckDate = null;		 
        ResultSet rsCheckDate = null;		 
        String fromDate = null;		 
        String toDate = null;		 
        String today = null;		 
        int fromDateNum = 99991231;		 
        int toDateNum = 0;		 
        int todayNum = 0;		 
 
        boolean checkData = false; 		 
        		 
        try {		 
            pstmtCheckDate = conn.prepareStatement(sqlCheckDate);		 
            rsCheckDate = pstmtCheckDate.executeQuery();        		 
            if( rsCheckDate.next() ){		 
                fromDate = rsCheckDate.getString(1);		 
                toDate = rsCheckDate.getString(2);		 
                today = rsCheckDate.getString(3);		 
                fromDateNum = Integer.parseInt(fromDate);		 
                toDateNum = Integer.parseInt(toDate);		 
                todayNum = Integer.parseInt(today);		 
            } 		 
            		 
            if( todayNum >= fromDateNum && todayNum <= toDateNum ){		 
                checkData = true;		 
            } 		 
            debug("fromDateNum : " + fromDateNum);		 
            debug("toDateNum : " + toDateNum);		 
            debug("todayNum : " + todayNum);		 
            debug("checkData : " + checkData);		 
        } catch (Exception e) {		 
                error("checkDate() : " + e.getMessage());		 
        } finally {		 
            if(rsCheckDate != null) {try {rsCheckDate.close();} catch (Exception e) {error("rsCheckDate.close() : " + e.getMessage());}}		 
            if(pstmtCheckDate != null){try {pstmtCheckDate.close();} catch (Exception e) {error("pstmtCheckDate.close() : " + e.getMessage());}}		 
        }		 
        		 
        return checkData;		 
    }		 
    		 
    /*************************************************************************		 
     * 1. 대상자 추출 멤버쉽 유료회원 & 스마트등급(회원사오퍼대상) 기간만료자만 체크		 
     *************************************************************************/		 
    private void processGolfCdhd(WaContext context, Connection conn ) {		 
        String sqlGolfCdhd = this.getGolfCdhdQuery();		 
        debug("sqlGolfCdhd : " + sqlGolfCdhd);		 
 
        PreparedStatement pstmtGolfCdhd = null;		 
        ResultSet rsGolfCdhd = null;		 
        ArrayList golfCdhds = new ArrayList();		 
        int golfCdhdCount = 0;		 
        		 
        try {		 
            pstmtGolfCdhd = conn.prepareStatement(sqlGolfCdhd);		 
            rsGolfCdhd = pstmtGolfCdhd.executeQuery();		 
            int idx = 1;		 
            while( rsGolfCdhd.next() ){		 
                idx = 1;		 
                HashMap aGolfCdhd = new HashMap();		 
                aGolfCdhd.put("CDHD_ID", rsGolfCdhd.getString(idx++));		 
                aGolfCdhd.put("HG_NM", rsGolfCdhd.getString(idx++));		 
                aGolfCdhd.put("ACRG_CDHD_JONN_DATE", rsGolfCdhd.getString(idx++));		 
                aGolfCdhd.put("ACRG_CDHD_END_DATE", rsGolfCdhd.getString(idx++));		 
                aGolfCdhd.put("CDHD_CTGO_SEQ_NO", Integer.toString(rsGolfCdhd.getInt(idx++)));		 
                aGolfCdhd.put("JOIN_CHNL", rsGolfCdhd.getString(idx++));		 
                aGolfCdhd.put("JUMIN_NO", rsGolfCdhd.getString(idx++));		 
                aGolfCdhd.put("GRD_CDHD_CTGO_SEQ_NO", Integer.toString(rsGolfCdhd.getInt(idx++)));		 
                aGolfCdhd.put("MOBILE", rsGolfCdhd.getString(idx++));		 
                aGolfCdhd.put("EMAIL", rsGolfCdhd.getString(idx++));		 
 
                debug("processGolfCdhd() CDHD_ID : " + aGolfCdhd.get("CDHD_ID"));		 
                debug("processGolfCdhd() HG_NM : " + aGolfCdhd.get("HG_NM"));		 
                debug("processGolfCdhd() ACRG_CDHD_JONN_DATE : " + aGolfCdhd.get("ACRG_CDHD_JONN_DATE"));		 
                debug("processGolfCdhd() ACRG_CDHD_END_DATE : " + aGolfCdhd.get("ACRG_CDHD_END_DATE"));		 
                debug("processGolfCdhd() CDHD_CTGO_SEQ_NO : " + aGolfCdhd.get("CDHD_CTGO_SEQ_NO"));		 
                debug("processGolfCdhd() JOIN_CHNL : " + aGolfCdhd.get("JOIN_CHNL"));		 
                debug("processGolfCdhd() JUMIN_NO : " + aGolfCdhd.get("JUMIN_NO"));		 
                debug("processGolfCdhd() GRD_CDHD_CTGO_SEQ_NO : " + aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO"));		 
                debug("processGolfCdhd() MOBILE : " + aGolfCdhd.get("MOBILE"));		 
                debug("processGolfCdhd() EMAIL : " + aGolfCdhd.get("EMAIL"));		 
                		 
                golfCdhds.add(aGolfCdhd);		 
                golfCdhdCount++;		 
                    		 
            } // end of while		 
 
        } catch (Exception e) {		 
            error("processGolfCdhd() pstmtGolfCdhd: " + e.getMessage());		 
        } finally { 		 
            if(rsGolfCdhd != null) {try {rsGolfCdhd.close();} catch (Exception e) {error("rsGolfCdhd.close() : " + e.getMessage());}}		 
            if(pstmtGolfCdhd != null) {try {pstmtGolfCdhd.close();} catch (Exception e) {error("pstmtGolfCdhd.close() : " + e.getMessage());}}		 
        }		 
 
        info("기간만료자 수 : " + golfCdhdCount);		 
 
        String sqlInsertHistory = this.insertHistoryQuery();		 
        PreparedStatement pstmtInsertHistory = null;		 
        debug("sqlInsertHistory : " + sqlInsertHistory);		 
        		 
        String sqlGolfCdhdGrd = this.getGolfCdhdGrdQuery();		 
        PreparedStatement pstmtGolfCdhdGrd = null;		 
        debug("sqlGolfCdhdGrd : " + sqlGolfCdhdGrd);		 
		 
        String sqlDegradeGolfCdhd = this.degradeGolfCdhdQuery();		 
        PreparedStatement pstmtDegradeGolfCdhd = null;		 
        debug("sqlDegradeGolfCdhd : " + sqlDegradeGolfCdhd);		 
 
        String sqlDegradeGrd = this.degradeGrdQuery();		 
        PreparedStatement pstmtDegradeGrd = null;		 
        debug("sqlDegradeGrd : " + sqlDegradeGrd);		 
        		 
        String sqlDeleteGolfCdhdGrd = this.deleteGolfCdhdGrdQuery();		 
        PreparedStatement pstmtDeleteGolfCdhdGrd = null;		 
        debug("sqlDeleteGolfCdhdGrd : " + sqlDeleteGolfCdhdGrd);		 
        		 
        String sqlGradeFirst = this.gradeFirstQuery();		 
        PreparedStatement pstmtGradeFirst = null;		 
        debug("sqlGradeFirst : " + sqlGradeFirst);		 
 
        String sqlGradeFirstGrd = this.gradeFirstGrdQuery();		 
        PreparedStatement pstmtGradeFirstGrd = null;		 
        debug("sqlGradeFirstGrd : " + sqlGradeFirstGrd);		 
        		 
        String sqlFirstGrd = this.getFirstGrdQuery();		 
        PreparedStatement pstmtFirstGrd = null;		 
        debug("sqlFirstGrd : " + sqlFirstGrd);		 
		 
        String sqlCodeNm = this.getCodeNmQuery();		 
        PreparedStatement pstmtCodeNm = null;		 
        debug("sqlCodeNm : " + sqlCodeNm);		 
        		 
        try {		 
            pstmtInsertHistory = conn.prepareStatement(sqlInsertHistory);		 
            pstmtGolfCdhdGrd = conn.prepareStatement(sqlGolfCdhdGrd);		 
            pstmtDegradeGolfCdhd = conn.prepareStatement(sqlDegradeGolfCdhd);		 
            pstmtDegradeGrd = conn.prepareStatement(sqlDegradeGrd);		 
            pstmtDeleteGolfCdhdGrd = conn.prepareStatement(sqlDeleteGolfCdhdGrd);		 
            pstmtGradeFirst = conn.prepareStatement(sqlGradeFirst);		 
            pstmtGradeFirstGrd = conn.prepareStatement(sqlGradeFirstGrd);		 
            pstmtFirstGrd = conn.prepareStatement(sqlFirstGrd);		 
            pstmtCodeNm = conn.prepareStatement(sqlCodeNm);		 
 
        } catch (Exception e) {		 
            error("PreparedStatement() : " + e.getMessage());		 
        }		 
        		 
        boolean isCommitSuccess = true;		 
 
        boolean isDev = false;		 
        String serverip = null;		 
        String devip = null;		 
        		 
        try {		 
            serverip = InetAddress.getLocalHost().getHostAddress(); // 서버아이피		 
            devip =AppConfig.getAppProperty("DV_WAS_1ST"); // 개발기 ip 정보		 
            		 
            isDev = devip.equals(serverip);		 
 
        } catch (Exception e) {		 
            error("serverip, devip error : " + e.getMessage());		 
        } 		 
 
        info( "serverip : " + serverip);		 
        info( "devip : " + devip);		 
        info( "isDev : " + isDev);		 
 
        String curDateFormated = DateUtil.currdate("yyyyMMdd");		 
        StringBuffer curDateBuffer = new StringBuffer("");		 
        curDateBuffer.append(curDateFormated.substring(0, 4));		 
        curDateBuffer.append("년 ");		 
        curDateBuffer.append(curDateFormated.substring(4, 6));		 
        curDateBuffer.append("월 ");		 
        curDateBuffer.append(curDateFormated.substring(6));		 
        curDateBuffer.append("일");		 
        String curDate = curDateBuffer.toString();		 
 
        for( int li_i = 0; li_i < golfCdhds.size(); li_i++) {		 
            boolean isProcess = false;		 
            HashMap aGolfCdhd = new HashMap();		 
            aGolfCdhd = (HashMap)golfCdhds.get(li_i);		 
            		 
            isCommitSuccess = transactionProcess(conn, 		 
                                                        pstmtInsertHistory, 		 
                                                        pstmtGolfCdhdGrd,		 
                                                        pstmtDegradeGolfCdhd,		 
                                                        pstmtDegradeGrd,		 
                                                        pstmtDeleteGolfCdhdGrd,		 
                                                        pstmtGradeFirst,		 
                                                        pstmtGradeFirstGrd,		 
                                                        pstmtFirstGrd,		 
                                                        aGolfCdhd		 
                                                        ); // 1.1 트랜잭션 처리		 
            		 
            String currCdhdId = (String)aGolfCdhd.get("CDHD_ID");		 
            String nextCdhdId = null;		 
 
            debug("currCdhdId : " + currCdhdId);		 
            debug("isCommitSuccess : " + isCommitSuccess);		 
 
            if( (li_i+1) <  golfCdhds.size() ) {		 
                nextCdhdId = (String)((HashMap)golfCdhds.get(li_i+1)).get("CDHD_ID");		 
 
                debug("nextCdhdId : " + nextCdhdId);		 
 
                if( !currCdhdId.equals(nextCdhdId) && isCommitSuccess ){		 
                	isProcess = true;		 
                }		 
            }		 
 
            if( (li_i+1) == golfCdhds.size() ) {		 
            	isProcess = true;		 
            }		 
            
            String grd = (String)aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO");
            boolean smartYN = false;
            try {
				if( AppConfig.getDataCodeProp("0052CODE7").equals(grd) || 		 
				        AppConfig.getDataCodeProp("0052CODE8").equals(grd) || 		 
				        AppConfig.getDataCodeProp("0052CODE9").equals(grd) || 		 
				        AppConfig.getDataCodeProp("0052CODE10").equals(grd) ) {
					smartYN = true;
				}else {
					smartYN = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		    if( isProcess && !smartYN ){		 
		        String codeName = getCodeName(pstmtCodeNm, currCdhdId);		 
		        aGolfCdhd.put("GOLF_CMMN_CODE_NM", codeName);		 
		        debug("GOLF_CMMN_CODE_NM : " + codeName);		 

		        sendSms("1", context, aGolfCdhd, isDev, serverip, 0); // SMS 문자  발송		 
		        		 
		        sendEmail("1", aGolfCdhd, isDev, curDate); // 메일 발송		 
		            		 
		    } // end of if
			
 
        } // end of for		 
        		 
        if(pstmtInsertHistory != null) {try {pstmtInsertHistory.close();} catch (Exception e) {error("pstmtInsertHistory.close() : " + e.getMessage());}}		 
        if(pstmtGolfCdhdGrd != null) {try {pstmtGolfCdhdGrd.close();} catch (Exception e) {error("pstmtGolfCdhdGrd.close() : " + e.getMessage());}}		 
        if(pstmtDegradeGolfCdhd != null) {try {pstmtDegradeGolfCdhd.close();} catch (Exception e) {error("pstmtDegradeGolfCdhd.close() : " + e.getMessage());}}		 
        if(pstmtDegradeGrd != null) {try {pstmtDegradeGrd.close();} catch (Exception e) {error("pstmtDegradeGrd.close() : " + e.getMessage());}}		 
        if(pstmtDeleteGolfCdhdGrd != null) {try {pstmtDeleteGolfCdhdGrd.close();} catch (Exception e) {error("pstmtDeleteGolfCdhdGrd.close() : " + e.getMessage());}}		 
        if(pstmtGradeFirst != null) {try {pstmtGradeFirst.close();} catch (Exception e) {error("pstmtGradeFirst.close() : " + e.getMessage());}}		 
        if(pstmtGradeFirstGrd != null) {try {pstmtGradeFirstGrd.close();} catch (Exception e) {error("pstmtGradeFirstGrd.close() : " + e.getMessage());}}		 
        if(pstmtFirstGrd != null) {try {pstmtFirstGrd.close();} catch (Exception e) {error("pstmtFirstGrd.close() : " + e.getMessage());}}		 
        if(pstmtCodeNm != null) {try {pstmtCodeNm.close();} catch (Exception e) {error("pstmtCodeNm.close() : " + e.getMessage());}}		 
            		 
    }		 
	 
    /*************************************************************************		 
     * 1.1  트랜잭션 처리		 
     *************************************************************************/		 
    private boolean transactionProcess(Connection conn, 		 
                                                        PreparedStatement pstmtInsertHistory,		 
                                                        PreparedStatement pstmtGolfCdhdGrd,		 
                                                        PreparedStatement pstmtDegradeGolfCdhd,		 
                                                        PreparedStatement pstmtDegradeGrd,		 
                                                        PreparedStatement pstmtDeleteGolfCdhdGrd,		 
                                                        PreparedStatement pstmtGradeFirst,		 
                                                        PreparedStatement pstmtGradeFirstGrd,		 
                                                        PreparedStatement pstmtFirstGrd,		 
                                                        HashMap aGolfCdhd) {		 
	 
        boolean isInsertSuccess = true;		 
        boolean isIndvSuccess = true;		 
        boolean isCommitSuccess = true;		 
        		 
        try {		 
            conn.setAutoCommit(false);		 
            debug("트랜잭션 시작===============================================");		 
 
            debug("insertHistory() 시작");		 
            isInsertSuccess = insertHistory( pstmtInsertHistory, aGolfCdhd ); // 2. 등급변경내역 저장		 
            debug("insertHistory() 종료");		 
 
            debug("processIndvGolfCdhd() 시작");		 
            isIndvSuccess = processIndvGolfCdhd(pstmtGolfCdhdGrd, 		 
                                                                    pstmtDegradeGolfCdhd, 		 
                                                                    pstmtDegradeGrd,		 
                                                                    pstmtDeleteGolfCdhdGrd,		 
                                                                    pstmtGradeFirst,		 
                                                                    pstmtGradeFirstGrd,		 
                                                                    pstmtFirstGrd,		 
                                                                    aGolfCdhd ); // 3. 대상자 개인별 확인 검증 (중복등급일경우)		 
            debug("processIndvGolfCdhd() 종료");		 
            		 
            if( isInsertSuccess == true && isIndvSuccess == true  ) {		 
                conn.commit();		 
                debug("트랜잭션 commit");		 
            } else {		 
                conn.rollback();		 
                debug("트랜잭션 rollback");		 
                isCommitSuccess = false;		 
            }		 
 
        } catch (Exception e) {		 
            if ( conn != null ) {try {conn.rollback();} catch(Exception ee) {error("conn.rollback() : " + ee.getMessage());}}		 
            debug("트랜잭션  rollback");		 
            isCommitSuccess = false;		 
        } finally { 		 
            if(conn != null) {try{conn.setAutoCommit(true);}catch(Exception ee){error("conn.setAutoCommit(true) : " + ee.getMessage());}}		 
            debug("트랜잭션  종료===============================================");		 
        }                		 
        		 
        return isCommitSuccess;		 
	 
    }		 
    		 
    /*************************************************************************		 
     * 2. 등급변경내역 저장		 
     *************************************************************************/		 
    private boolean insertHistory(PreparedStatement pstmtInsertHistory, HashMap aGolfCdhd) {		 
            		 
        int idx = 1;		 
        int insertSuccessCount = 0;		 
        boolean isInsertSuccess = true;		 
        try {		 
            pstmtInsertHistory.setString( idx++, (String)aGolfCdhd.get("ACRG_CDHD_JONN_DATE" ) );		 
            pstmtInsertHistory.setString( idx++, (String)aGolfCdhd.get("ACRG_CDHD_END_DATE" ) );		 
            pstmtInsertHistory.setString( idx++, (String)aGolfCdhd.get("JOIN_CHNL" ) );		 
            pstmtInsertHistory.setString( idx++, (String)aGolfCdhd.get("CDHD_ID" ) );		 
            pstmtInsertHistory.setInt   ( idx++, Integer.parseInt((String)aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO" )));		 
 
            debug("insertHistory() ACRG_CDHD_JONN_DATE : " + aGolfCdhd.get("ACRG_CDHD_JONN_DATE"));		 
            debug("insertHistory() ACRG_CDHD_END_DATE : " + aGolfCdhd.get("ACRG_CDHD_END_DATE"));		 
            debug("insertHistory() JOIN_CHNL : " + aGolfCdhd.get("JOIN_CHNL"));		 
            debug("insertHistory() CDHD_ID : " + aGolfCdhd.get("CDHD_ID"));		 
            debug("insertHistory() GRD_CDHD_CTGO_SEQ_NO : " + aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO"));		 
 
            insertSuccessCount = pstmtInsertHistory.executeUpdate();		 
                		 
        } catch (Exception e) {		 
            error("insertHistory() : " + e.getMessage());		 
            isInsertSuccess = false;		 
        } finally {		 
        }                		 
 
        if( insertSuccessCount < 1 ) {		 
            isInsertSuccess = false;		 
        }		 
        		 
        debug("isInsertSuccess : " + isInsertSuccess   );		 
        return isInsertSuccess;		 
        		 
    }		 
	 
    		 
    /*************************************************************************		 
     * 3. 대상자 개인별 확인 검증 (중복등급일경우)		 
     *************************************************************************/		 
    private boolean processIndvGolfCdhd(PreparedStatement pstmtGolfCdhdGrd,		 
                                                                            PreparedStatement pstmtDegradeGolfCdhd,		 
                                                                            PreparedStatement pstmtDegradeGrd,		 
                                                                            PreparedStatement pstmtDeleteGolfCdhdGrd,		 
                                                                            PreparedStatement pstmtGradeFirst,		 
                                                                            PreparedStatement pstmtGradeFirstGrd,		 
                                                                            PreparedStatement pstmtFirstGrd,		 
                                                                            HashMap aGolfCdhd ) {		 
	 
        boolean isIndvSuccess = true;		 
        boolean isDeleteSuccess = true;		 
        boolean isFirstGrdSuccess = true;		 
        boolean isGradeFirstSuccess = true;		 
        		 
        ResultSet rsGolfCdhdGrd = null;		 
        int golfCdhdGrdCount = 0;		 
        		 
        try {		 
            pstmtGolfCdhdGrd.setString(1, (String)aGolfCdhd.get("CDHD_ID"));		 
            debug("processIndvGolfCdhd() CDHD_ID : " + (String)aGolfCdhd.get("CDHD_ID"));		 
 
            rsGolfCdhdGrd = pstmtGolfCdhdGrd.executeQuery();		 
            		 
            if( rsGolfCdhdGrd.next() ) {		 
            	golfCdhdGrdCount = rsGolfCdhdGrd.getInt(1);		 
            }		 
            debug("processIndvGolfCdhd() golfCdhdGrdCount : " + golfCdhdGrdCount );		 
 
            if( golfCdhdGrdCount == 1 ) { // 3.1  등급이 1개일경우		 
    		 
            	isIndvSuccess = degradeGolfCdhdGrd( pstmtDegradeGolfCdhd, 		 
                                                                            pstmtDegradeGrd,		 
                                                                            aGolfCdhd ); // 3.1.1 화이트회원으로 강등		 
                    		 
            } else if ( golfCdhdGrdCount >= 2 ) { // 3.2 등급이 N개일경우		 
                    		 
                HashMap aFirstGrd = new HashMap();		 
                isDeleteSuccess = deleteGolfCdhdGrd(pstmtDeleteGolfCdhdGrd,		 
                                                                            aGolfCdhd ); // 3.2.1 멤버쉽 등급 삭제 , 대표등급을 카드등급으로 변경(최우선카드등급으로)		 
                aFirstGrd = getFirstGrd(pstmtFirstGrd, 		 
                                                      aGolfCdhd); // 3.2.2 최우선등급을 찾는다 (첫번째 데이타가 최우선등급임)		 
 
                if( aFirstGrd.size() < 1) {		 
                    isFirstGrdSuccess = false;		 
                }		 
                		 
                isGradeFirstSuccess = gradeFirstGrd(pstmtGradeFirst,		 
                                                                        pstmtGradeFirstGrd,		 
                                                                        aFirstGrd);// 3.2.3 등급변경 히스토리 저장 및 대표등급변경처리 		 
                		 
                if( isDeleteSuccess == false || 		 
                    isFirstGrdSuccess == false || 		 
                    isGradeFirstSuccess == false ) {		 
                    		 
                    isIndvSuccess = false;		 
                }		 
                    		 
            }		 
        } catch (Exception e) {		 
            error("processIndvGolfCdhd() pstmtGolfCdhdGrd: " + e.getMessage());		 
            isIndvSuccess = false;		 
 
        } finally { 		 
            if(rsGolfCdhdGrd != null) {try {rsGolfCdhdGrd.close();} catch (Exception e) {error("rsGolfCdhdGrd.close() : " + e.getMessage());}}		 
 
        }                		 
 
        debug("processIndvGolfCdhd() isIndvSuccess : " + isIndvSuccess );		 
 
        return isIndvSuccess;		 
    }		 
	 
    /*************************************************************************		 
     * 3.1.1 화이트회원으로 강등		 
     *************************************************************************/		 
    private boolean degradeGolfCdhdGrd(PreparedStatement pstmtDegradeGolfCdhd,		 
                                                                            PreparedStatement pstmtDegradeGrd,		 
                                                                            HashMap aGolfCdhd ) {		 
    		 
        boolean isDegradeSuccess = true;		 
        int degradeGolfCdhdCount = 0;		 
        int degradeGrdCount = 0;		 
 
        try {		 
            pstmtDegradeGolfCdhd.setString(1, (String)aGolfCdhd.get("CDHD_ID"));		 
            degradeGolfCdhdCount = pstmtDegradeGolfCdhd.executeUpdate();		 
 
            debug("degradeGolfCdhdGrd() pstmtDegradeGolfCdhd CDHD_ID : " + (String)aGolfCdhd.get("CDHD_ID"));		 
 
            pstmtDegradeGrd.setString(1, (String)aGolfCdhd.get("CDHD_ID"));		 
            pstmtDegradeGrd.setInt(2, Integer.parseInt((String)aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO")));		 
            degradeGrdCount = pstmtDegradeGrd.executeUpdate();		 
            		 
            debug("degradeGolfCdhdGrd() pstmtDegradeGrd CDHD_ID : " + (String)aGolfCdhd.get("CDHD_ID"));		 
            debug("degradeGolfCdhdGrd() pstmtDegradeGrd GRD_CDHD_CTGO_SEQ_NO : " + (String)aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO"));		 
 
        } catch (Exception e) {		 
            error("degradeGolfCdhdGrd() pstmtDegradeGolfCdhd, pstmtDegradeGrd : " + e.getMessage());		 
            isDegradeSuccess = false;		 
 
        } finally { 		 
        		 
        }                		 
		 
        if( degradeGolfCdhdCount < 1 || degradeGrdCount < 1 ){		 
            isDegradeSuccess = false;		 
        }		 
 
        debug("degradeGolfCdhdGrd() degradeGolfCdhdCount : " + degradeGolfCdhdCount );		 
        debug("degradeGolfCdhdGrd() degradeGrdCount : " + degradeGrdCount );		 
        debug("degradeGolfCdhdGrd() isDegradeSuccess : " + isDegradeSuccess );		 
 
        return isDegradeSuccess;		 
    }		 
	 
    /*************************************************************************		 
     * 3.2.1 멤버쉽 등급 삭제 , 대표등급을 카드등급으로 변경(최우선카드등급으로)		 
     *************************************************************************/		 
    private boolean deleteGolfCdhdGrd(PreparedStatement pstmtDeleteGolfCdhdGrd,		 
                                                                            HashMap aGolfCdhd ) {		 
	 
        boolean isDeleteSuccess = true;		 
        int deleteCount = 0;		 
 
        try {		 
                		 
            pstmtDeleteGolfCdhdGrd.setString(1, (String)aGolfCdhd.get("CDHD_ID"));		 
            pstmtDeleteGolfCdhdGrd.setInt(2, Integer.parseInt((String)aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO")));		 
 
            debug("deleteGolfCdhdGrd() CDHD_ID : " + (String)aGolfCdhd.get("CDHD_ID"));		 
            debug("deleteGolfCdhdGrd() GRD_CDHD_CTGO_SEQ_NO : " + (String)aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO"));		 
 
            deleteCount = pstmtDeleteGolfCdhdGrd.executeUpdate();		 
                		 
        } catch (Exception e) {		 
            error("deleteGolfCdhdGrd() : " + e.getMessage());		 
            isDeleteSuccess = false;		 
        } finally { 		 
        		 
        }        		 
 
        if( deleteCount < 1  ){		 
            isDeleteSuccess = false;		 
        }		 
 
        return isDeleteSuccess;		 
	 
    }		 
    		 
    /*************************************************************************		 
     * 3.2.2 최우선등급을 찾는다 (첫번째 데이타가 최우선등급임) 		 
     *************************************************************************/		 
    private HashMap getFirstGrd(PreparedStatement pstmtFirstGrd, HashMap aGolfCdhd) {		 
	 
        ResultSet rsFirstGrd = null;		 
        HashMap aFirstGrd = new HashMap();		 
        		 
        try {		 
            pstmtFirstGrd.setString(1, (String)aGolfCdhd.get("CDHD_ID"));		 
            pstmtFirstGrd.setInt(2, Integer.parseInt((String)aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO")));		 
 
            debug("getFirstGrd() CDHD_ID : " + (String)aGolfCdhd.get("CDHD_ID"));		 
            debug("getFirstGrd() GRD_CDHD_CTGO_SEQ_NO : " + (String)aGolfCdhd.get("GRD_CDHD_CTGO_SEQ_NO"));		 
 
            rsFirstGrd = pstmtFirstGrd.executeQuery();		 
            		 
            if( rsFirstGrd.next()){		 
                int idx = 1;		 
                aFirstGrd.put("FIRST_CDHD_ID", rsFirstGrd.getString(idx++));		 
                aFirstGrd.put("FIRST_CDHD_CTGO_SEQ_NO", Integer.toString(rsFirstGrd.getInt(idx++)) );		 
                aFirstGrd.put("FIRST_GOLF_CMMN_CODE_NM", rsFirstGrd.getString(idx++));		 
                aFirstGrd.put("FIRST_SORT_SEQ", Integer.toString(rsFirstGrd.getInt(idx++)));		 
 
                debug("getFirstGrd() FIRST_CDHD_ID : " + (String)aFirstGrd.get("FIRST_CDHD_ID"));		 
                debug("getFirstGrd() FIRST_CDHD_CTGO_SEQ_NO : " + (String)aFirstGrd.get("FIRST_CDHD_CTGO_SEQ_NO"));		 
                debug("getFirstGrd() FIRST_GOLF_CMMN_CODE_NM : " + (String)aFirstGrd.get("FIRST_GOLF_CMMN_CODE_NM"));		 
                debug("getFirstGrd() FIRST_SORT_SEQ : " + (String)aFirstGrd.get("FIRST_SORT_SEQ"));		 
            }		 
                		 
        } catch (Exception e) {		 
            error("getFirstGrd() : " + e.getMessage());		 
        } finally { 		 
            if(rsFirstGrd != null) {try {rsFirstGrd.close();} catch (Exception e) {error("rsFirstGrd() : " + e.getMessage());}}		 
        }                		 
 
        return aFirstGrd; 		 
    }        		 
    		 
    /*************************************************************************		 
     * 3.2.3 등급변경 히스토리 저장 및 대표등급변경처리 		 
     *************************************************************************/		 
    private boolean gradeFirstGrd(PreparedStatement pstmtGradeFirst,		 
                                                                    PreparedStatement pstmtGradeFirstGrd,		 
                                                                    HashMap aFirstGrd ) {		 
    		 
        boolean isGradeFirstSuccess = true;		 
        int gradeFirstCount = 0;		 
        int gradeFirstGrdCount = 0;		 
 
        try {		 
 
            /* 최우선 등급이  		 
            NH티타늄  12 ,NH플래티늄  13,NH플래티늄  14,경남은행 Family카드  19, APT 프리미엄  20,		 
            Smart150, Smart200, Smart300, Smart500이면		 
            ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE 를  NULL로 UPDATE */		 
                    		 
            String updateCheck = "N";		 
            		 
            //int seqNo = Integer.parseInt((String)aFirstGrd.get("FIRST_CDHD_CTGO_SEQ_NO"));		 
            String seqNo = (String) aFirstGrd.get("FIRST_CDHD_CTGO_SEQ_NO");		 
            		 
            if( AppConfig.getDataCodeProp("0052CODE13").equals(seqNo) || 		 
                            AppConfig.getDataCodeProp("0052CODE14").equals(seqNo) || 		 
                            AppConfig.getDataCodeProp("0052CODE15").equals(seqNo) || 		 
                            AppConfig.getDataCodeProp("0052CODE16").equals(seqNo) || 		 
                            AppConfig.getDataCodeProp("0052CODE4").equals(seqNo) || 		 
                            AppConfig.getDataCodeProp("0052CODE7").equals(seqNo) || 		 
                            AppConfig.getDataCodeProp("0052CODE8").equals(seqNo) || 		 
                            AppConfig.getDataCodeProp("0052CODE9").equals(seqNo) || 		 
                            AppConfig.getDataCodeProp("0052CODE10").equals(seqNo) ||		 
            				AppConfig.getDataCodeProp("0052CODE19").equals(seqNo) ) {
                            updateCheck = "Y";		 
            }		 
                		 
            debug("seqNo : " + seqNo);		 
            debug("updateCheck : [" + updateCheck + "]");		 
            		 
            pstmtGradeFirst.setString(1, seqNo);		 
            pstmtGradeFirst.setString(2, updateCheck);		 
            pstmtGradeFirst.setString(3, updateCheck);		 
            pstmtGradeFirst.setString(4, (String)aFirstGrd.get("FIRST_CDHD_ID"));		 
            gradeFirstCount = pstmtGradeFirst.executeUpdate();		 
 
            debug("gradeFirstGrd() pstmtGradeFirst FIRST_CDHD_CTGO_SEQ_NO : " + (String)aFirstGrd.get("FIRST_CDHD_CTGO_SEQ_NO"));		 
            debug("gradeFirstGrd() pstmtGradeFirst FIRST_CDHD_ID : " + (String)aFirstGrd.get("FIRST_CDHD_ID"));		 
            		 
            pstmtGradeFirstGrd.setString(1, (String)aFirstGrd.get("FIRST_CDHD_ID"));		 
            pstmtGradeFirstGrd.setString(2, seqNo);		 
            gradeFirstGrdCount = pstmtGradeFirstGrd.executeUpdate();		 
 
            debug("gradeFirstGrd() pstmtGradeFirstGrd FIRST_CDHD_ID : " + (String)aFirstGrd.get("FIRST_CDHD_ID"));		 
            debug("gradeFirstGrd() pstmtGradeFirstGrd FIRST_CDHD_CTGO_SEQ_NO : " + (String)aFirstGrd.get("FIRST_CDHD_CTGO_SEQ_NO"));		 
 
        } catch (Exception e) {		 
            error("degradeGolfCdhdGrd() pstmtDegradeGolfCdhd, pstmtDegradeGrd : " + e.getMessage());		 
            isGradeFirstSuccess = false;		 
        } finally { 		 
        }                		 
 
        if( gradeFirstCount < 1 || gradeFirstGrdCount < 1 ){		 
            isGradeFirstSuccess = false;		 
        }		 
 
        debug("gradeFirstGrd() gradeFirstCount : " + gradeFirstCount );		 
        debug("gradeFirstGrd() gradeFirstGrdCount : " + gradeFirstGrdCount );		 
        debug("gradeFirstGrd() isGradeFirstSuccess : " + isGradeFirstSuccess );		 
 
        return isGradeFirstSuccess;        		 
    }		 
    		 
    /*************************************************************************		 
     * 4. 유료회원 기간 만료 -30일, - 1일  회원에 SMS 문자 발송		 
     *************************************************************************/		 
    private void processSmsEmail(WaContext context, Connection conn, int beforeDay){		 
	 
        String sqlSmsEmail = this.getSmsEmailQuery();		 
 
        PreparedStatement pstmtSmsEmail = null;		 
        ResultSet rsSmsEmail = null;		 
        ArrayList smsEmails = new ArrayList();		 
        int smsEmailCount = 0;		 
        		 
        try {		 
            pstmtSmsEmail = conn.prepareStatement(sqlSmsEmail);		 
            pstmtSmsEmail.setInt(1, beforeDay);		 
 
            debug("processSmsEmail() beforeDay : " + beforeDay);		 
 
            rsSmsEmail = pstmtSmsEmail.executeQuery();		 
            		 
            int idx = 1;		 
            while( rsSmsEmail.next() ){		 
                idx = 1;		 
                HashMap aSmsEmail = new HashMap();		 
                aSmsEmail.put("CDHD_ID", rsSmsEmail.getString(idx++));		 
                aSmsEmail.put("HG_NM", rsSmsEmail.getString(idx++));		 
                aSmsEmail.put("ACRG_CDHD_JONN_DATE", rsSmsEmail.getString(idx++));		 
                aSmsEmail.put("ACRG_CDHD_END_DATE", rsSmsEmail.getString(idx++));		 
                aSmsEmail.put("CDHD_CTGO_SEQ_NO", Integer.toString(rsSmsEmail.getInt(idx++)));		 
                aSmsEmail.put("JOIN_CHNL", rsSmsEmail.getString(idx++));		 
                aSmsEmail.put("JUMIN_NO", rsSmsEmail.getString(idx++));		 
                aSmsEmail.put("GRD_CDHD_CTGO_SEQ_NO", Integer.toString(rsSmsEmail.getInt(idx++)));		 
                aSmsEmail.put("MOBILE", rsSmsEmail.getString(idx++));		 
                aSmsEmail.put("EMAIL", rsSmsEmail.getString(idx++));		 
 
                    debug("processSmsEmail() CDHD_ID : " + aSmsEmail.get("CDHD_ID"));		 
                debug("processSmsEmail() HG_NM : " + aSmsEmail.get("HG_NM"));		 
                debug("processSmsEmail() ACRG_CDHD_JONN_DATE : " + aSmsEmail.get("ACRG_CDHD_JONN_DATE"));		 
                debug("processSmsEmail() ACRG_CDHD_END_DATE : " + aSmsEmail.get("ACRG_CDHD_END_DATE"));		 
                debug("processSmsEmail() CDHD_CTGO_SEQ_NO : " + aSmsEmail.get("CDHD_CTGO_SEQ_NO"));		 
                debug("processSmsEmail() JOIN_CHNL : " + aSmsEmail.get("JOIN_CHNL"));		 
                debug("processSmsEmail() GRD_CDHD_CTGO_SEQ_NO : " + aSmsEmail.get("GRD_CDHD_CTGO_SEQ_NO"));		 
                debug("processSmsEmail() MOBILE : " + aSmsEmail.get("MOBILE"));		 
                debug("processSmsEmail() EMAIL : " + aSmsEmail.get("EMAIL"));		 
 
                smsEmails.add(aSmsEmail);		 
                smsEmailCount++;		 
		 
            } // end of while		 
 
        } catch (Exception e) {		 
                error("processSmsEmail() pstmtSmsEmail: " + e.getMessage());		 
        } finally { 		 
                if(rsSmsEmail != null) {try {rsSmsEmail.close();} catch (Exception e) {error("rsSmsEmail.close() : " + e.getMessage());}}		 
                if(pstmtSmsEmail != null) {try {pstmtSmsEmail.close();} catch (Exception e) {error("pstmtSmsEmail.close() : " + e.getMessage());}}		 
        		 
        }                		 
 
        info( beforeDay +"일 전 SMS/EMAIL 건수 : " + smsEmailCount);		 
 
        boolean isDev = false;		 
        String serverip = null;		 
        String devip = null;		 
        		 
        try {		 
            serverip = InetAddress.getLocalHost().getHostAddress(); // 서버아이피		 
            devip =AppConfig.getAppProperty("DV_WAS_1ST"); // 개발기 ip 정보		 
            		 
            isDev = devip.equals(serverip);		 
 
        } catch (Exception e) {		 
            error("serverip, devip error : " + e.getMessage());		 
        } 		 
 
        info( "serverip : " + serverip);		 
        info( "devip : " + devip);		 
        info( "isDev : " + isDev);		 
 
        String curDateFormated = DateUtil.currdate("yyyyMMdd");		 
        StringBuffer curDateBuffer = new StringBuffer("");		 
        curDateBuffer.append(curDateFormated.substring(0, 4));		 
        curDateBuffer.append("년 ");		 
        curDateBuffer.append(curDateFormated.substring(4, 6));		 
        curDateBuffer.append("월 ");		 
        curDateBuffer.append(curDateFormated.substring(6));		 
        curDateBuffer.append("일");		 
        String curDate = curDateBuffer.toString();		 
        		 
        String sqlBeforeCodeNm = this.getBeforeCodeNmQuery();		 
        PreparedStatement pstmtBeforeCodeNm = null;		 
        debug("sqlBeforeCodeNm : " + sqlBeforeCodeNm);		 
 
        try {		 
            pstmtBeforeCodeNm = conn.prepareStatement(sqlBeforeCodeNm);		 
        } catch (Exception e) {		 
            error("PreparedStatement() pstmtBeforeCodeNm : " + e.getMessage());		 
        }		 
 
        for( int li_i = 0; li_i < smsEmails.size(); li_i++) {		 
            HashMap aSmsEmail = new HashMap();		 
            aSmsEmail = (HashMap)smsEmails.get(li_i);		 
 
            String BeforeCodeName = getBeforeCodeName(pstmtBeforeCodeNm, 		 
                                                          (String)aSmsEmail.get("CDHD_ID"), 		 
                                                          (String)aSmsEmail.get("GRD_CDHD_CTGO_SEQ_NO"));		 
            aSmsEmail.put("GOLF_CMMN_CODE_NM", BeforeCodeName);		 
            debug("GOLF_CMMN_CODE_NM : " + BeforeCodeName);		 
            		 
            sendSms("2", context, aSmsEmail, isDev, serverip, beforeDay); // 4.1 SMS 문자  발송		 
            		 
            sendEmail("2", aSmsEmail, isDev, curDate); // 4.2  메일 발송		 
        } // end of for		 
        		 
        if(pstmtBeforeCodeNm != null) {try {pstmtBeforeCodeNm.close();} catch (Exception e) {error("pstmtBeforeCodeNm.close() : " + e.getMessage());}}		 
	 
    } // end of processSmsEmail        		 
    		 
    /*************************************************************************		 
     * 4.1 SMS 문자  발송		 
     *************************************************************************/		 
    private void sendSms(String workClss, WaContext context, HashMap aSmsEmail, boolean isDev, String serverip, int beforeDay) {		 
	 
        HashMap smsMap = new HashMap();		 
		 
        try {		 
 
            String mobile = (String)aSmsEmail.get("MOBILE");		 
            String hp_ddd_no = "";		 
            String hp_tel_hno = "";		 
            String hp_tel_sno = "";		 
 
            if( mobile != null && mobile.length() != 0){		 
                String[] mobilearr = mobile.split("-");		 
                hp_ddd_no = mobilearr[0];		 
                hp_tel_hno = mobilearr[1];		 
                hp_tel_sno = mobilearr[2];		 
            }		 
	 
            String hgnm = (String)aSmsEmail.get("HG_NM");		 
            String codeName = (String)aSmsEmail.get("GOLF_CMMN_CODE_NM");		 
 
            String enddate = (String)aSmsEmail.get("ACRG_CDHD_END_DATE");		 
 
            debug("sendSms hp_ddd_no :  " + hp_ddd_no);		 
            debug("sendSms hp_tel_hno :  " + hp_tel_hno);		 
            debug("sendSms hp_tel_sno :  " + hp_tel_sno);		 
            debug("sendSms hgnm :  " + hgnm);		 
 
            smsMap.put("ip", serverip);		 
            smsMap.put("sName", hgnm);		 
            smsMap.put("sPhone1", hp_ddd_no);		 
            smsMap.put("sPhone2", hp_tel_hno);		 
            smsMap.put("sPhone3", hp_tel_sno);		 
            smsMap.put("sCallCenter", "15666578");		 
            String smsClss = "674";		 
	 
            StringBuffer messageBuffer = new StringBuffer("");		 
 
            if( workClss.equals("1")){		 
                messageBuffer.append("[Golf Loun.G]");                        		 
                messageBuffer.append(hgnm);		 
                messageBuffer.append("님,유효기간 종료로  ");		 
                messageBuffer.append(codeName);		 
                messageBuffer.append(" 등급으로 되셨습니다.");		 
            } else if ( workClss.equals("2")){		 
                    messageBuffer.append("[Golf Loun.G]");                        		 
                    messageBuffer.append(hgnm);		 
                    messageBuffer.append("님,기간종료일이 ");		 
                    messageBuffer.append(beforeDay);		 
                    messageBuffer.append("일남았습니다.마이페이지에서 기간연장하세요 ");		 
            }		 
            		 
            String message = messageBuffer.toString();		 
            SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");		 
            StringBuffer infoBuffer = new StringBuffer();		 
            String smsRtn = "";		 
            		 
            /*SMS발송*/		 
            if (isDev) {  //개발기		 
                infoBuffer.append("[골프라운지  기간만료 SMS 개발기는 발송안됩니다.] 핸드폰번호 |");		 
                infoBuffer.append(hp_ddd_no);		 
                infoBuffer.append("-");		 
                infoBuffer.append(hp_tel_hno);		 
                infoBuffer.append("-");		 
                infoBuffer.append(hp_tel_sno);		 
                infoBuffer.append("|메세지|");		 
                infoBuffer.append(message);		 
                info(infoBuffer.toString()  );		 
 
            } else { //운영기		 
                smsRtn = smsProc.send(smsClss, smsMap, message);		 
 
                infoBuffer.append("[골프라운지 기간만료 SMS 발송] 핸드폰번호 |");		 
                infoBuffer.append(hp_ddd_no);		 
                infoBuffer.append("-");		 
                infoBuffer.append(hp_tel_hno);		 
                infoBuffer.append("-");		 
                infoBuffer.append(hp_tel_sno);		 
                infoBuffer.append("|메세지|");		 
                infoBuffer.append(message);		 
                info(infoBuffer.toString()  );		 
            }        		 
        		 
 
        } catch (Exception e) {		 
            error("sendSms() : " + e.getMessage());		 
        } 		 
	 
    }		 
    		 
    /*************************************************************************		 
     * 4.2  메일 발송		 
     *************************************************************************/		 
    private void sendEmail(String workClss, HashMap aSmsEmail, boolean isDev, String curDate) {		 
            		 
        try {		 
 
            StringBuffer infoBuffer = new StringBuffer();		 
            		 
            StringBuffer emailAdminBuffer = new StringBuffer();		 
            emailAdminBuffer.append("\"골프라운지\" <");		 
            emailAdminBuffer.append(AppConfig.getAppProperty("EMAILADMIN"));		 
            emailAdminBuffer.append(">");		 
            String emailAdmin = emailAdminBuffer.toString();		 
            		 
            StringBuffer imgPathBuffer = new StringBuffer();		 
            imgPathBuffer.append("<img src=\"");		 
            String imgPath = imgPathBuffer.toString();		 
            		 
            StringBuffer hrefPathBuffer = new StringBuffer();		 
            hrefPathBuffer.append("<a href=\"");		 
            String hrefPath = hrefPathBuffer.toString();		 
 
            debug("sendEmail emailAdmin :  " + emailAdmin);		 
            debug("sendEmail imgPath :  " + imgPath);		 
            debug("sendEmail hrefPath :  " + hrefPath);		 
 
            EmailSend sender = new EmailSend();		 
            EmailEntity emailEtt = new EmailEntity("EUC_KR");		 
            		 
            String emailTitle = "";		 
            String emailFileNm = "";		 
            StringBuffer emailContentBuffer = new StringBuffer("");		 
            String enddate = (String)aSmsEmail.get("ACRG_CDHD_END_DATE");		 
 
            if( workClss.equals("1")){		 
                emailTitle = "[Golf Loun.G]골프라운지 등급 안내";		 
                emailFileNm = "/email_tpl29.html"; //http://www.golfloung.com/app/golfloung/html/email/email_tpl29.html (/BCWEB/WAS/bcext/golfloung/html/email/email_tpl29.html)		 
                emailContentBuffer.append((String)aSmsEmail.get("HG_NM")); 		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append(enddate.substring(0, 4));		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append(enddate.substring(4, 6));		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append(enddate.substring(6));		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append((String)aSmsEmail.get("GOLF_CMMN_CODE_NM")); 		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append(curDate);		 
                    		 
            } else if( workClss.equals("2")){		 
                emailTitle = "[Golf Loun.G]골프라운지 멤버십 기간 만료 안내";		 
                emailFileNm = "/email_tpl30.html"; //http://www.golfloung.com/app/golfloung/html/email/email_tpl30.html (/BCWEB/WAS/bcext/golfloung/html/email/email_tpl30.html)		 
                emailContentBuffer.append((String)aSmsEmail.get("HG_NM")); 		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append((String)aSmsEmail.get("GOLF_CMMN_CODE_NM")); 		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append(enddate.substring(0, 4));		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append(enddate.substring(4, 6));		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append(enddate.substring(6));		 
                emailContentBuffer.append("|");		 
                emailContentBuffer.append(curDate);		 
            }		 
 
            debug("emailTitle : " + emailTitle);		 
            debug("emailFileNm : " + emailFileNm);		 
            debug("emailContent : " + emailContentBuffer.toString());		 
 
            emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, emailContentBuffer.toString());		 
            		 
            emailEtt.setFrom(emailAdmin);		 
            emailEtt.setSubject(emailTitle); 		 
            emailEtt.setTo((String)aSmsEmail.get("EMAIL"));		 
                		 
            /*메일발송*/		 
            if (isDev) {  //개발기		 
                infoBuffer.append("[골프라운지  기간만료 메일 개발기는 발송안됩니다.]");		 
                info(infoBuffer.toString());		 
                return;		 
 
            } else {		 
                    //운영기		 
                infoBuffer.append("[골프라운지 기간만료 메일 발송]");		 
                info(infoBuffer.toString()  );		 
                sender.send(emailEtt);		 
            }		 
                		 
        } catch (Exception e) {		 
            error("sendEmail() : " + e.getMessage());		 
        } 		 
            		 
    }		 
	 
    /*************************************************************************		 
     * 골프라운지 등급 안내 골프공통코드명 찾는다 		 
     *************************************************************************/		 
    private String getCodeName(PreparedStatement pstmtCodeNm, String currCdhdId) {		 
	 
        ResultSet rsCodeNm = null;		 
        String codeName = null;		 
        		 
        try {		 
            pstmtCodeNm.setString(1, currCdhdId);		 
            debug("getCodeName() currCdhdId : " + currCdhdId);		 
            rsCodeNm = pstmtCodeNm.executeQuery();		 
            		 
            if( rsCodeNm.next()){		 
                codeName = rsCodeNm.getString(1);		 
                debug("getCodeName() codeName : " + codeName);		 
            }		 
                		 
        } catch (Exception e) {		 
            error("getCodeName() : " + e.getMessage());		 
        } finally { 		 
            if(rsCodeNm != null) {try {rsCodeNm.close();} catch (Exception e) {error("rsCodeNm() : " + e.getMessage());}}		 
        }                		 
 
        return codeName; 		 
    }        		 
	 
    /*************************************************************************		 
     * 골프라운지 멤버십 기간 만료 안내 골프공통코드명 찾는다 		 
     *************************************************************************/		 
    private String getBeforeCodeName(PreparedStatement pstmtBeforeCodeNm, String currCdhdId, String grdCdhdCtgoSeqNo ) {		 
	 
        ResultSet rsBeforeCodeNm = null;		 
        String beforeCodeName = null;		 
        		 
        try {		 
            pstmtBeforeCodeNm.setString(1, currCdhdId);		 
            pstmtBeforeCodeNm.setInt(2, Integer.parseInt(grdCdhdCtgoSeqNo));		 
 
            debug("getBeforeCodeName() currCdhdId : " + currCdhdId);		 
            debug("getBeforeCodeName() grdCdhdCtgoSeqNo : " + Integer.parseInt(grdCdhdCtgoSeqNo));		 
 
            rsBeforeCodeNm = pstmtBeforeCodeNm.executeQuery();		 
                		 
            if( rsBeforeCodeNm.next()){		 
                beforeCodeName = rsBeforeCodeNm.getString(1);		 
                debug("getBeforeCodeName() codeName : " + beforeCodeName);		 
            }		 
                		 
        } catch (Exception e) {		 
            error("getBeforeCodeName() : " + e.getMessage());		 
        } finally { 		 
            if(rsBeforeCodeNm != null) {try {rsBeforeCodeNm.close();} catch (Exception e) {error("rsBeforeCodeNm() : " + e.getMessage());}}		 
        }                		 
 
        return beforeCodeName; 		 
    }        		 
    /*************************************************************************		 
     * 0. 이벤트기간 체크 SQL		 
     *************************************************************************/		 
    private String getCheckDateQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
        sqlBuffer.append("\t  SELECT FROM_DATE, \n");		 
        sqlBuffer.append("\t         TO_DATE, \n");		 
        sqlBuffer.append("\t         TO_CHAR(SYSDATE,'YYYYMMDD') TODAY \n");		 
        sqlBuffer.append("\t  FROM   BCDBA.TBEVNTLOTINFO \n");  		 
        sqlBuffer.append("\t  WHERE  SITE_CLSS='10' \n"); 		 
        sqlBuffer.append("\t  AND    EVNT_NO = 121 \n"); 		 
        return sqlBuffer.toString();		 
    }		 
	 
    /*************************************************************************		 
     * 1. 대상자 추출 멤버쉽 유료회원 중 기간만료자만 체크 SQL 		 
     *************************************************************************/		 
    private String getGolfCdhdQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
	    sqlBuffer.append("\t  SELECT a.CDHD_ID, \n");		 
	    sqlBuffer.append("\t         a.HG_NM, \n");		 
	    sqlBuffer.append("\t         a.ACRG_CDHD_JONN_DATE, \n"); 		 
	    sqlBuffer.append("\t         a.ACRG_CDHD_END_DATE, \n");		 
	    sqlBuffer.append("\t         a.CDHD_CTGO_SEQ_NO, \n");		 
	    sqlBuffer.append("\t         a.JOIN_CHNL, \n"); 		 
	    sqlBuffer.append("\t         a.JUMIN_NO , \n"); 		 
	    sqlBuffer.append("\t         b.CDHD_CTGO_SEQ_NO, \n"); 		 
        sqlBuffer.append("\t         a.MOBILE, \n");		 
        sqlBuffer.append("\t         a.EMAIL \n"); 		 
	    sqlBuffer.append("\t  FROM   BCDBA.TBGGOLFCDHD a , \n");  		 
	    sqlBuffer.append("\t         BCDBA.TBGGOLFCDHDGRDMGMT b \n"); 		 
	    sqlBuffer.append("\t  WHERE  a.ACRG_CDHD_END_DATE < TO_CHAR(SYSDATE,'YYYYMMDD') \n");		 
	    sqlBuffer.append("\t  AND    b.CDHD_CTGO_SEQ_NO IN (SELECT t2.CDHD_CTGO_SEQ_NO  \n");		 
	    sqlBuffer.append("\t                                FROM   BCDBA.TBGCMMNCODE t1 \n"); 		 
	    sqlBuffer.append("\t                                JOIN   BCDBA.TBGGOLFCDHDCTGOMGMT t2 \n"); 		 
	    sqlBuffer.append("\t                                ON     t1.GOLF_CMMN_CODE = t2.CDHD_SQ2_CTGO \n"); 		 
	    sqlBuffer.append("\t                                WHERE  t1.GOLF_URNK_CMMN_CLSS='0000' \n"); 		 
	    sqlBuffer.append("\t                                AND    t1.GOLF_URNK_CMMN_CODE='0005' \n");		 
	    sqlBuffer.append("\t                                AND    t2.CDHD_SQ1_CTGO='0002' \n"); 
	    sqlBuffer.append("\t                                AND    t2.CDHD_CTGO_SEQ_NO != '8' )\n");
	    sqlBuffer.append("\t  AND    a.CDHD_ID = b.CDHD_ID \n");		 
	    sqlBuffer.append("\t  AND   (a.CDHD_CTGO_SEQ_NO <> 11 OR a.JOIN_CHNL <> '3000' ) \n"); 	
	    
	    sqlBuffer.append("\t  UNION        \n");		 
	    sqlBuffer.append("\t  SELECT a.CDHD_ID, \n");		 
	    sqlBuffer.append("\t         a.HG_NM, \n");		 
	    sqlBuffer.append("\t         a.ACRG_CDHD_JONN_DATE, \n"); 		 
	    sqlBuffer.append("\t         a.ACRG_CDHD_END_DATE, \n");		 
	    sqlBuffer.append("\t         a.CDHD_CTGO_SEQ_NO, \n");		 
	    sqlBuffer.append("\t         a.JOIN_CHNL, \n"); 		 
	    sqlBuffer.append("\t         a.JUMIN_NO , \n"); 		 
	    sqlBuffer.append("\t         b.CDHD_CTGO_SEQ_NO, \n"); 		 
        sqlBuffer.append("\t         a.MOBILE, \n");		 
        sqlBuffer.append("\t         a.EMAIL \n"); 		 
	    sqlBuffer.append("\t  FROM   BCDBA.TBGGOLFCDHD a , \n");  		 
	    sqlBuffer.append("\t         BCDBA.TBGGOLFCDHDGRDMGMT b \n"); 		 
	    sqlBuffer.append("\t  WHERE TO_CHAR(ADD_MONTHS(TO_DATE(SUBSTR(B.REG_ATON,1,8),'YYYYMMDD'),3),'YYYYMMDD')  <  TO_CHAR(SYSDATE,'YYYYMMDD')        \n");		 
	    sqlBuffer.append("\t  AND    b.CDHD_CTGO_SEQ_NO IN (   SELECT GOLF_CMMN_CODE \n");		 
	    sqlBuffer.append("\t                                                                          FROM BCDBA.TBGCMMNCODE        \n");		 
	    sqlBuffer.append("\t                                                                          WHERE GOLF_CMMN_CLSS = '0064'        \n");		 
	    sqlBuffer.append("\t                                                                          AND GOLF_CMMN_CODE != '0027'        \n");		 
	    sqlBuffer.append("\t                                                                  )        \n");		 
	    sqlBuffer.append("\t  AND    a.CDHD_ID = b.CDHD_ID         \n");
	    
	    sqlBuffer.append("\t  UNION \n");	    
	    sqlBuffer.append("\t  SELECT a.CDHD_ID, \n");		 
	    sqlBuffer.append("\t         a.HG_NM, \n");		 
	    sqlBuffer.append("\t         a.ACRG_CDHD_JONN_DATE, \n"); 		 
	    sqlBuffer.append("\t         a.ACRG_CDHD_END_DATE, \n");		 
	    sqlBuffer.append("\t         a.CDHD_CTGO_SEQ_NO, \n");		 
	    sqlBuffer.append("\t         a.JOIN_CHNL, \n"); 		 
	    sqlBuffer.append("\t         a.JUMIN_NO , \n"); 		 
	    sqlBuffer.append("\t         b.CDHD_CTGO_SEQ_NO, \n"); 		 
        sqlBuffer.append("\t         a.MOBILE, \n");		 
        sqlBuffer.append("\t         a.EMAIL \n"); 		 
	    sqlBuffer.append("\t  FROM   BCDBA.TBGGOLFCDHD a , \n");  		 
	    sqlBuffer.append("\t         BCDBA.TBGGOLFCDHDGRDMGMT b, \n"); 
	    sqlBuffer.append("\t   		 BCDBA.TBACRGCDHDLODNTBL c	\n");
	    sqlBuffer.append("\t  WHERE  c.CAMP_END_DATE < TO_CHAR(SYSDATE,'YYYYMMDD') \n");	    
	    sqlBuffer.append("\t  AND    b.CDHD_CTGO_SEQ_NO IN (   SELECT GOLF_CMMN_CODE \n");
	    sqlBuffer.append("\t   									FROM BCDBA.TBGCMMNCODE	\n");
	    sqlBuffer.append("\t   									WHERE GOLF_CMMN_CLSS = '0064'\n");
	    sqlBuffer.append("\t   									AND GOLF_CMMN_CODE = '0028'	\n");	    
	    sqlBuffer.append("\t   								) \n");
	    sqlBuffer.append("\t   AND   a.CDHD_ID = b.CDHD_ID	\n");
	    sqlBuffer.append("\t   AND   a.CDHD_ID = c.PROC_RSLT_CTNT \n");
	    sqlBuffer.append("\t   AND   c.MEMO_EXPL = '0028'	\n");	    
	    sqlBuffer.append("\t   ORDER BY CDHD_ID \n");
	      
	    return sqlBuffer.toString();		 
    		 
    }		 
	 
    /*************************************************************************		 
     * 2. 등급변경내역 저장 SQL 		 
     *************************************************************************/		 
    private String insertHistoryQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
	    sqlBuffer.append("\t  INSERT INTO BCDBA.TBGCDHDGRDCHNGHST \n");        		 
	    sqlBuffer.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 \n");		 
	    sqlBuffer.append("\t          FROM   BCDBA.TBGCDHDGRDCHNGHST), \n");         		 
	    sqlBuffer.append("\t                GRD.CDHD_GRD_SEQ_NO, \n"); 		 
	    sqlBuffer.append("\t                GRD.CDHD_ID, \n");		 
	    sqlBuffer.append("\t                GRD.CDHD_CTGO_SEQ_NO, \n"); 		 
	    sqlBuffer.append("\t                TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), \n"); 		 
	    sqlBuffer.append("\t                ?, \n");                                 		 
	    sqlBuffer.append("\t                ?, \n");		 
	    sqlBuffer.append("\t                ? \n");		 
	    sqlBuffer.append("\t  FROM    BCDBA.TBGGOLFCDHDGRDMGMT GRD \n");        		 
	    sqlBuffer.append("\t  WHERE   GRD.CDHD_ID = ? \n");                                                		 
	    sqlBuffer.append("\t  AND     GRD.CDHD_CTGO_SEQ_NO= ? \n");		 
	    return sqlBuffer.toString();		 
    		 
    }		 
	 
    /*************************************************************************		 
     * 3. 대상자 개인별 확인 검증 SQL		 
     *************************************************************************/		 
    private String getGolfCdhdGrdQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
        sqlBuffer.append("\t  SELECT COUNT(*) \n");		 
        sqlBuffer.append("\t  FROM   BCDBA.TBGGOLFCDHDGRDMGMT \n");		 
        sqlBuffer.append("\t  WHERE  CDHD_ID = ?\n");		 
        return sqlBuffer.toString();		 
    		 
    }		 
    		 
    /*************************************************************************		 
     * 3.1.1 화이트회원으로 강등 SQL		 
     *************************************************************************/		 
    private String degradeGolfCdhdQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
        sqlBuffer.append("\t  UPDATE BCDBA.TBGGOLFCDHD \n"); 		 
        sqlBuffer.append("\t  SET    ACRG_CDHD_JONN_DATE = NULL , \n"); 		 
        sqlBuffer.append("\t         ACRG_CDHD_END_DATE = NULL , \n");		 
        sqlBuffer.append("\t         JOIN_CHNL ='0001', \n");  		 
        sqlBuffer.append("\t         CDHD_CTGO_SEQ_NO  = 8 \n"); 		 
        sqlBuffer.append("\t  WHERE  CDHD_ID = ? \n");		 
        return sqlBuffer.toString();		 
    		 
    }		 
	 
    /*************************************************************************		 
     * 3.1.1 등급 화이트회원으로 강등 SQL		 
     *************************************************************************/		 
    private String degradeGrdQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
        sqlBuffer.append("\t UPDATE BCDBA.TBGGOLFCDHDGRDMGMT \n"); 		 
        sqlBuffer.append("\t SET    CDHD_CTGO_SEQ_NO  = 8 , \n"); 		 
        sqlBuffer.append("\t        CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') , \n"); 		 
        sqlBuffer.append("\t        CHNG_RSON_CTNT ='기간만료로인한 Agent대상자'\n");   		 
        sqlBuffer.append("\t WHERE  CDHD_ID = ? \n");  		 
        sqlBuffer.append("\t AND    CDHD_CTGO_SEQ_NO = ? \n"); 		 
        return sqlBuffer.toString();		 
    		 
    }		 
    /*************************************************************************		 
     * 3.2.1 멤버쉽 등급 삭제 SQL		 
     *************************************************************************/		 
    private String deleteGolfCdhdGrdQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
        sqlBuffer.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");  		 
        sqlBuffer.append("\t  WHERE  CDHD_ID = ? \n"); 		 
        sqlBuffer.append("\t  AND    CDHD_CTGO_SEQ_NO = ? \n");		 
        return sqlBuffer.toString();		 
    		 
    }		 
    		 
    /*************************************************************************		 
     * 3.2.2 최우선등급을 찾는 SQL (첫번째 데이타가 최우선등급임)  		 
     *************************************************************************/		 
    private String getFirstGrdQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
        sqlBuffer.append("\t  SELECT a.CDHD_ID, \n");		 
        sqlBuffer.append("\t         a.CDHD_CTGO_SEQ_NO , \n");		 
        sqlBuffer.append("\t         b.GOLF_CMMN_CODE_NM , \n");		 
        sqlBuffer.append("\t         b.SORT_SEQ  \n");		 
        sqlBuffer.append("\t  FROM   BCDBA.TBGGOLFCDHDGRDMGMT a, \n");		 
        sqlBuffer.append("\t        (SELECT t1.GOLF_CMMN_CODE_NM , \n");		 
        sqlBuffer.append("\t                t1.EXPL , \n");		 
        sqlBuffer.append("\t                t2.CDHD_CTGO_SEQ_NO, \n");		 
        sqlBuffer.append("\t                t2.SORT_SEQ, \n");		 
        sqlBuffer.append("\t                t2.ANL_FEE, \n");		 
        sqlBuffer.append("\t                t2.MO_MSHP_FEE, \n");		 
        sqlBuffer.append("\t                t2.CDHD_SQ1_CTGO \n");		 
        sqlBuffer.append("\t         FROM   BCDBA.TBGCMMNCODE t1 \n"); 		 
        sqlBuffer.append("\t         JOIN   BCDBA.TBGGOLFCDHDCTGOMGMT t2 \n");		 
        sqlBuffer.append("\t         ON     t1.GOLF_CMMN_CODE = t2.CDHD_SQ2_CTGO \n");		 
        sqlBuffer.append("\t         WHERE  t1.GOLF_URNK_CMMN_CLSS='0000' \n");		 
        sqlBuffer.append("\t         AND    t1.GOLF_URNK_CMMN_CODE='0005' \n");		 
        sqlBuffer.append("\t         ORDER BY t2.SORT_SEQ ) b \n");		 
        sqlBuffer.append("\t  WHERE  a.CDHD_ID =  ? \n");  		 
        sqlBuffer.append("\t  AND    a.CDHD_CTGO_SEQ_NO NOT IN (?) \n"); 		 
        sqlBuffer.append("\t  AND    a.CDHD_CTGO_SEQ_NO = b.CDHD_CTGO_SEQ_NO \n");		 
        sqlBuffer.append("\t  ORDER BY b.SORT_SEQ \n");		 
        return sqlBuffer.toString();		 
    		 
    }		 
            		 
    /*************************************************************************		 
     * 3.2.3  대표등급변경 SQL		 
     *************************************************************************/		 
    private String gradeFirstQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
        sqlBuffer.append("\t  UPDATE BCDBA.TBGGOLFCDHD \n");		 
        sqlBuffer.append("\t  SET    JOIN_CHNL ='0001', \n"); 		 
        sqlBuffer.append("\t         CDHD_CTGO_SEQ_NO  = ?, \n");		 
        sqlBuffer.append("\t         ACRG_CDHD_JONN_DATE = DECODE(?, 'Y', NULL, ACRG_CDHD_JONN_DATE), \n"); 		 
        sqlBuffer.append("\t         ACRG_CDHD_END_DATE  = DECODE(?, 'Y', NULL, ACRG_CDHD_END_DATE)  \n");		 
        sqlBuffer.append("\t  WHERE  CDHD_ID = ? \n");		 
        return sqlBuffer.toString(); 		 
    		 
    }		 
	 
    /*************************************************************************		 
     * 3.2.3  등급 대표등급변경 SQL		 
     *************************************************************************/		 
    private String gradeFirstGrdQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
        sqlBuffer.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT \n");		 
        sqlBuffer.append("\t  SET    CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') , \n");		 
        sqlBuffer.append("\t         CHNG_RSON_CTNT ='기간만료로인한 Agent대상자' \n");		 
        sqlBuffer.append("\t  WHERE  CDHD_ID = ? \n"); 		 
        sqlBuffer.append("\t  AND    CDHD_CTGO_SEQ_NO = ? \n");		 
        return sqlBuffer.toString();		 
     		 
    }        		 
	 
    /*************************************************************************		 
     * 4. 유료회원 기간 만료 -30일, - 1일 회원에 SMS 문자 SQL 		 
     *************************************************************************/		 
    private String getSmsEmailQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
        sqlBuffer.append("\t  SELECT a.CDHD_ID, \n");		 
        sqlBuffer.append("\t         a.HG_NM, \n");		 
        sqlBuffer.append("\t         a.ACRG_CDHD_JONN_DATE, \n"); 		 
        sqlBuffer.append("\t         a.ACRG_CDHD_END_DATE, \n");		 
        sqlBuffer.append("\t         a.CDHD_CTGO_SEQ_NO, \n");		 
        sqlBuffer.append("\t         a.JOIN_CHNL, \n");		 
        sqlBuffer.append("\t         a.JUMIN_NO , \n");		 
        sqlBuffer.append("\t         b.CDHD_CTGO_SEQ_NO, \n");		 
        sqlBuffer.append("\t         a.MOBILE, \n");		 
        sqlBuffer.append("\t         a.EMAIL \n"); 		 
        sqlBuffer.append("\t  FROM   BCDBA.TBGGOLFCDHD a , \n");  		 
        sqlBuffer.append("\t         BCDBA.TBGGOLFCDHDGRDMGMT b \n");		 
        sqlBuffer.append("\t  WHERE  a.ACRG_CDHD_END_DATE = TO_CHAR(SYSDATE + ?,'YYYYMMDD') \n");		 
        sqlBuffer.append("\t  AND    b.CDHD_CTGO_SEQ_NO IN (SELECT CDHD_CTGO_SEQ_NO \n");		 
        sqlBuffer.append("\t                                FROM   BCDBA.TBGCMMNCODE t1 \n"); 		 
        sqlBuffer.append("\t                                JOIN   BCDBA.TBGGOLFCDHDCTGOMGMT t2 \n"); 		 
        sqlBuffer.append("\t                                ON     t1.GOLF_CMMN_CODE = t2.CDHD_SQ2_CTGO \n"); 		 
        sqlBuffer.append("\t                                WHERE  t1.GOLF_URNK_CMMN_CLSS='0000' \n");		 
        sqlBuffer.append("\t                                AND    t1.GOLF_URNK_CMMN_CODE='0005' \n"); 		 
        sqlBuffer.append("\t                                AND    CDHD_SQ1_CTGO='0002' \n");    
        sqlBuffer.append("\t                                AND    t2.CDHD_CTGO_SEQ_NO != '8' ) \n");
        sqlBuffer.append("\t  AND    a.CDHD_ID = b.CDHD_ID \n");		 
        sqlBuffer.append("\t  AND   (a.CDHD_CTGO_SEQ_NO <> 11 OR a.JOIN_CHNL <> '3000' ) \n");		 
        return sqlBuffer.toString(); 		 
     		 
    } 		 
	 
    /*************************************************************************		 
     * 골프라운지 등급 안내 골프공통코드명 찾는 SQL		 
     *************************************************************************/		 
    private String getCodeNmQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
	    sqlBuffer.append("\t  SELECT c.GOLF_CMMN_CODE_NM \n");		 
	    sqlBuffer.append("\t  FROM   BCDBA.TBGGOLFCDHD a, \n");		 
	    sqlBuffer.append("\t         BCDBA.TBGGOLFCDHDCTGOMGMT b, \n");		 
	    sqlBuffer.append("\t         BCDBA.TBGCMMNCODE c \n"); 		 
	    sqlBuffer.append("\t  WHERE  a.CDHD_ID = ? \n");  		 
	    sqlBuffer.append("\t  AND    b.CDHD_CTGO_SEQ_NO = a.CDHD_CTGO_SEQ_NO \n");		 
	    sqlBuffer.append("\t  AND    c.GOLF_CMMN_CODE = b.CDHD_SQ2_CTGO \n");  		 
	    sqlBuffer.append("\t  AND    c.GOLF_URNK_CMMN_CLSS='0000' \n");  		 
	    sqlBuffer.append("\t  AND    c.GOLF_URNK_CMMN_CODE='0005' \n"); 		 
	    sqlBuffer.append("\t  AND    c.GOLF_CMMN_CODE_NM IS NOT NULL \n"); 		 
	    sqlBuffer.append("\t  AND    ROWNUM = 1  \n");                                		 
        return sqlBuffer.toString();		 
    }		 
    		 
    /*************************************************************************		 
     * 골프라운지 멤버십 기간 만료 안내 골프공통코드명 찾는 SQL		 
     *************************************************************************/		 
    private String getBeforeCodeNmQuery(){		 
        StringBuffer sqlBuffer = new StringBuffer();		 
        sqlBuffer.append("\n");		 
	    sqlBuffer.append("\t  SELECT c.GOLF_CMMN_CODE_NM \n");		 
	    sqlBuffer.append("\t  FROM   BCDBA.TBGGOLFCDHDGRDMGMT a, \n");		 
	    sqlBuffer.append("\t         BCDBA.TBGGOLFCDHDCTGOMGMT b, \n");		 
	    sqlBuffer.append("\t         BCDBA.TBGCMMNCODE c \n");  		 
	    sqlBuffer.append("\t  WHERE  a.CDHD_ID = ? \n");		 
	    sqlBuffer.append("\t  AND    b.CDHD_CTGO_SEQ_NO = ? \n");		 
	    sqlBuffer.append("\t  AND    b.CDHD_CTGO_SEQ_NO = a.CDHD_CTGO_SEQ_NO \n");		 
	    sqlBuffer.append("\t  AND    c.GOLF_CMMN_CODE = b.CDHD_SQ2_CTGO \n");   		 
	    sqlBuffer.append("\t  AND    c.GOLF_URNK_CMMN_CLSS='0000' \n");   		 
	    sqlBuffer.append("\t  AND    c.GOLF_URNK_CMMN_CODE='0005' \n");  		 
	    sqlBuffer.append("\t  AND    c.GOLF_CMMN_CODE_NM IS NOT NULL \n");  		 
	    sqlBuffer.append("\t  AND    ROWNUM = 1 \n");         		 
        return sqlBuffer.toString();		 
    }		 
} //end of class		 
