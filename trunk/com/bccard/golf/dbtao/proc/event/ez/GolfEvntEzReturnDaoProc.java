/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntKvpDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 월례회 > 등록처리
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.ez;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0  
 ******************************************************************************/
public class GolfEvntEzReturnDaoProc extends AbstractProc {
	
	public GolfEvntEzReturnDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int updEvntFunction(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int int_re =  0;
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);		
			conn.setAutoCommit(false);	

			// 리턴변수
			String result 		= data.getString("result");			// 주문처리 결과 : yes:정상, no:오류
			String aspOrderNum 	= data.getString("aspOrderNum");	// 주문번호 (제휴사측) -> 골프라운지 주문번호
			String orderNum 	= data.getString("orderNum");		// 주문번호 (이지웰측)
			String paySummary 	= data.getString("paySummary");		// 결재수단 정보 : 포인트:10000, 카드:50000
			String errDesc 		= data.getString("errDesc");		// 에러내용
			
			// 이벤트 등록
			String pgrs_yn			= "";	// 진행여부 I:등록, Y:결제, N:취소
			String jumin_no			= "";	// 주민등록번호
			String evnt_grd			= "";	// 신청등급
			String sttl_amt			= "";	// 결제 금액

			// 회원정보
			String cdhd_id 			= "";
			String sece_yn 			= "";
			String member_clss 		= "";	// 1: 개인 , 2:법인
			String cdhd_ctgo_seq_no	= "";	// 등급
			String hg_nm 			= "";	// 이름
			
			// 세션정보 가져오기
			String mobileArr[];
			String hp_ddd_no 		= "";	// 휴대전화DDD번호
			String hp_tel_hno 		= "";	// 휴대전화국번호
			String hp_tel_sno 		= "";	// 휴대전화일련번호
			String mobile = (String)request.getSession().getAttribute("ezMobile");
			String email_addr = (String)request.getSession().getAttribute("ezEmail");
			

			String serverip = InetAddress.getLocalHost().getHostAddress();	// 서버아이피
			String devip = AppConfig.getAppProperty("DV_WAS_1ST");		// 개발기 ip 정보
			if(serverip.equals(devip)){
//				mobile = "010-9192-4738";
//				email_addr = "simijoa@naver.com";
			}
			
            if(!GolfUtil.empty(mobile)){
            	mobileArr = GolfUtil.split(mobile, "-");
            	hp_ddd_no = mobileArr[0];
            	hp_tel_hno = mobileArr[1];
            	hp_tel_sno = mobileArr[2];
            }
			
			// TM 등록
			String golf_cdhd_grd_clss 	= "";	// 골프회원등급구분코드 (1:골드 2:블루 3:챔피온 4:블랙)
			int cnt 					= 0;
			
			String grd 					= "";	// 선택한 등급 //7:골드(우량)25,000  6:블루(골드)50,000 5:챔피온(VIP) 200,000 10:블랙 120,000
			String join_chnl 			= "";	// 가입경로
			String memGrade				= "";

			String tb_rslt_clss 		= "01";	// TM결과구분코드 (01:성공 00:기회원 )
			String auth_clss 			= "";	// 결제방법 1:카드승인 2:복합결제 3:포인트
			if(paySummary.equals("10000")){
				auth_clss = "3";
			}else{
				auth_clss = "1";
			}

			
			if(result.equals("yes")){
				pgrs_yn = "Y";
				
				// 이벤트 정보 가져오기
				pstmt = conn.prepareStatement(getEvntInfo());
				pstmt.setString(1, aspOrderNum);
				rs = pstmt.executeQuery(); 
				while (rs.next())	{		
					jumin_no	= rs.getString("JUMIN_NO");
					evnt_grd	= rs.getString("RSVT_CDHD_GRD_SEQ_NO");
					sttl_amt	= rs.getString("STTL_AMT");
					hg_nm		= rs.getString("BKG_PE_NM");
				}

				if(evnt_grd.equals("1")){
					grd = "2";
					join_chnl = "2901";
					golf_cdhd_grd_clss = "3";
					memGrade = "챔피온";
				}else if(evnt_grd.equals("2")){
					grd = "6";
					join_chnl = "2902";
					golf_cdhd_grd_clss = "2";
					memGrade = "블루";
				}else if(evnt_grd.equals("3")){
					grd = "7";
					join_chnl = "2903";
					golf_cdhd_grd_clss = "1";
					memGrade = "골드";
				}else if(evnt_grd.equals("7")){
					grd = "10";
					join_chnl = "2910";
					golf_cdhd_grd_clss = "4";
					memGrade = "블랙";
				}

				// 회원정보 데이타 있는지 조회 (우선순위 : 정상데이타, 유료회원종료일이  최근인 데이타)
				pstmt = conn.prepareStatement(getMemInfo());
				pstmt.setString(1, jumin_no);
				rs = pstmt.executeQuery(); 

				while (rs.next())	{		
					cdhd_id	= rs.getString("CDHD_ID");
					sece_yn	= rs.getString("SECE_YN");
					member_clss	= rs.getString("MEMBER_CLSS");
					cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
					hg_nm = rs.getString("HG_NM");
					mobile = rs.getString("MOBILE");
					cnt ++;
				}

				
				//있다면 UPDATE (유효기간,대표등급,해지여부,해지일자)
				if (cnt > 0) {

					tb_rslt_clss = "00";	// 기회원

					// 유료회원 갱신 처리
					pstmt = conn.prepareStatement(getUpdMem());
					pstmt.setString(1, join_chnl);		// 챔피온 : 2901 , 블루 : 2902 , 골드 : 2903 , 블랙 : 2910
					pstmt.setString(2, grd);
					pstmt.setString(3, cdhd_id);
					int_re = pstmt.executeUpdate();
					if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
					
										
					//등급변경 히스토리 getInsHistory
					if(int_re>0){
						pstmt = conn.prepareStatement(getInsHistory());
						pstmt.setString(1, cdhd_id);
						pstmt.setString(2, cdhd_ctgo_seq_no);				
						int_re = pstmt.executeUpdate(); 
						if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
					}
					

					//등급변경
					if(int_re>0){
						pstmt = conn.prepareStatement(getUpdGrd());
						pstmt.setString(1, grd );
						pstmt.setString(2, cdhd_id);
						pstmt.setString(3, cdhd_ctgo_seq_no);
						int_re = pstmt.executeUpdate(); 
					}
				}else{	// 회원정보 있을경우 업데이트 처리 종료
					int_re = 1;
				}

				
				// TM 자료 입력
				if(int_re>0){
					pstmt = conn.prepareStatement(getInsTm());
					idx = 1;
					pstmt.setString(idx++, tb_rslt_clss);		// TM결과구분코드 (01:성공 00:기회원 )
					pstmt.setString(idx++, hg_nm);				// 성명
					pstmt.setString(idx++, hp_ddd_no);			// 휴대전화DDD번호
					pstmt.setString(idx++, hp_tel_hno);			// 휴대전화국번호
					pstmt.setString(idx++, hp_tel_sno);			// 휴대전화일련번호
					pstmt.setString(idx++, jumin_no);			// 주민등록번호
					pstmt.setString(idx++, golf_cdhd_grd_clss);	// 골프회원등급구분코드 (1:골드 2:블루 3:챔피온 4:블랙)
					pstmt.setString(idx++, "EZ");				// 가입경로구분코드 (EZ:이지웰)
					pstmt.setString(idx++, auth_clss);			// 1:카드승인 2:복합결제 3:포인트
					pstmt.setString(idx++, join_chnl);			// 가입경로 (챔피온 : 2901 , 블루 : 2902 , 골드 : 2903 , 블랙 : 2910
					pstmt.setString(idx++, cdhd_id);			// 기회원일경우 아이디 입력할 것
					int_re = pstmt.executeUpdate(); 
				}
				
				// TM 연회비 내역
				if(int_re>0){
					pstmt = conn.prepareStatement(getInsTmPay());
					idx = 1;
					pstmt.setString(idx++, jumin_no);	// JUMIN_NO
					pstmt.setString(idx++, aspOrderNum);// AUTH_NO
					pstmt.setString(idx++, orderNum);	// CARD_NO
					pstmt.setString(idx++, sttl_amt);	// AUTH_AMT
					pstmt.setString(idx++, auth_clss);	// AUTH_CLSS
					int_re = pstmt.executeUpdate(); 
				}


				// SMS 관련 셋팅
				if(!GolfUtil.empty(mobile)){
					HashMap smsMap = new HashMap();
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", hg_nm);
					smsMap.put("sPhone1", hp_ddd_no);
					smsMap.put("sPhone2", hp_tel_hno);
					smsMap.put("sPhone3", hp_tel_sno);
					smsMap.put("sCallCenter", "15666578");
					String smsClss = "674";
					String message = "[Golf Loun.G]"+hg_nm+"님 골프라운지(www.golfloung.com)회원가입진행해주시기바랍니다" ;
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("message : " + message);
				}
				


				try{
					if(!GolfUtil.empty(email_addr)){

						/*메일발송*/
						String emailAdmin = "\"골프라운지\" <"+ AppConfig.getAppProperty("EMAILADMIN") +">";
						String imgPath = "<img src=\"";   //"<img src=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						String hrefPath = "<a href=\""+AppConfig.getAppProperty("REAL_HOST_DOMAIN");
						
						SimpleDateFormat fmt = new SimpleDateFormat("yyyy년 MM월 dd일");   
						GregorianCalendar cal = new GregorianCalendar();
						Date edDate = cal.getTime();
						String strEdDate = fmt.format(edDate);
						
						EmailSend sender = new EmailSend();
						EmailEntity emailEtt = new EmailEntity("EUC_KR");
						
						// 20100824 SK 주유권으로 변경
						String emailTitle = "[Golf Loun.G]골프라운지  회원가입 SK주유권";
						String emailFileNm = "/eamil_tm_oill.html";
						emailEtt.setHtmlContents(emailFileNm, imgPath, hrefPath, strEdDate);
						
						emailEtt.setFrom(emailAdmin);
						emailEtt.setSubject(emailTitle); 
						emailEtt.setTo(email_addr);
						sender.send(emailEtt);
					}
					
					debug("[골프라운지 TM 메일발송 완료] 주민번호 |" + jumin_no + "|email_addr|" + email_addr);
				}catch (javax.mail.SendFailedException ex) {
					debug("[골프라운지 TM 메일발송 실패] 주민번호 |" + jumin_no + "|email_addr|" + email_addr);
				}

				request.getSession().removeAttribute("ezMobile");
				request.getSession().removeAttribute("ezEmail");
			
			// 결제가 정상적으로 처리 되었을경우 처리 종료
				
			}else{
				pgrs_yn = "N";
				int_re = 1;
			}
			
			
			// 해당 주문정보를 업데이트 한다.
			if(int_re>0){
				pstmt = conn.prepareStatement(getUpdEvnt());
				idx = 1;
				pstmt.setString(idx++, pgrs_yn);
				pstmt.setString(idx++, errDesc);
				pstmt.setString(idx++, result);
				pstmt.setString(idx++, paySummary);
				pstmt.setString(idx++, orderNum);
				pstmt.setString(idx++, aspOrderNum);
				int_re = pstmt.executeUpdate(); 
			}

			if(int_re > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return int_re;
	}

	


	/** ***********************************************************************
    * 이벤트 정보 가져오기
    ************************************************************************ */
    private String getEvntInfo(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT JUMIN_NO, RSVT_CDHD_GRD_SEQ_NO, STTL_AMT, BKG_PE_NM FROM BCDBA.TBGAPLCMGMT WHERE APLC_SEQ_NO = ? \n");

		return sql.toString();
    }

	/** ***********************************************************************
    * 회원정보 가져오기
    ************************************************************************ */
    private String getMemInfo(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT CDHD_ID,NVL(SECE_YN,'N')  SECE_YN ,MEMBER_CLSS,CDHD_CTGO_SEQ_NO, ACRG_CDHD_JONN_DATE, NVL(ACRG_CDHD_END_DATE,'20090701') ACRG_CDHD_END_DATE \n");
		sql.append("\t		, HG_NM, MOBILE \n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD    \n");
		sql.append("\t	WHERE JUMIN_NO = ?  \n"); 
		sql.append("\t	ORDER BY SECE_YN DESC , ACRG_CDHD_END_DATE  \n");

		return sql.toString();
    }

	/** ***********************************************************************
    * 유료회원기간 연장하기
    ************************************************************************ */
    private String getUpdEvnt(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGAPLCMGMT SET	\n");
		sql.append("\t	PGRS_YN=?, MEMO_EXPL=?, ADDR=?, DTL_ADDR=?, REG_MGR_ID=?,	\n");
		sql.append("\t	CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t	WHERE APLC_SEQ_NO=?	\n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * 유료회원 갱신 처리
     ************************************************************************ */
     private String getUpdMem(){
         StringBuffer sql = new StringBuffer();	
 		sql.append("	\n");
 		sql.append(" UPDATE BCDBA.TBGGOLFCDHD  			\n");    
 		sql.append("    SET JOIN_CHNL = ? ,  	 \n");
 		sql.append("        ACRG_CDHD_JONN_DATE = TO_CHAR(SYSDATE,'yyyyMMdd') , 	 \n");
 		sql.append("        ACRG_CDHD_END_DATE = TO_CHAR(ADD_MONTHS(SYSDATE,12),'yyyyMMdd') ,  	 \n");
 		sql.append("        SECE_YN = NULL ,  	 \n");
 		sql.append("        SECE_ATON = NULL ,  	 \n");				
 		sql.append("        CDHD_CTGO_SEQ_NO = ?  	 \n");
 		sql.append("  WHERE CDHD_ID IN ( ? )	\n");
 		return sql.toString();
     }

 	/** ***********************************************************************
 	 * 등급변경 히스토리
     ************************************************************************ */
     private String getInsHistory(){
    	 StringBuffer sql = new StringBuffer();	
    	sql.append("	\n");
		sql.append("\t	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
		sql.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST)	\n");
		sql.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD, BCDBA.TBGGOLFCDHD B	\n");
		sql.append("\t  WHERE A.CDHD_ID = B.CDHD_ID AND GRD.CDHD_ID=? AND GRD.CDHD_CTGO_SEQ_NO= ?	\n");
		
		
		
		
  		return sql.toString();
      }

   	/** ***********************************************************************
	* 등급변경
	************************************************************************ */
	private String getUpdGrd(){
		StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append(" UPDATE BCDBA.TBGGOLFCDHDGRDMGMT \n");
		sql.append("    SET CDHD_CTGO_SEQ_NO = ?  , 	 \n");
		sql.append("        CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),   	 \n");	
		sql.append("        CHNG_RSON_CTNT = '이지웰등급변경'   	 \n");				 	
		sql.append("  WHERE CDHD_ID = ? 	");
		sql.append("  AND   CDHD_CTGO_SEQ_NO = ? 	");
		return sql.toString();
	}

   	/** ***********************************************************************
	* 기존 골프라운지 회원가입 (TM테이블에 저장)
	************************************************************************ */
	private String getInsTm(){
		StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	INSERT INTO BCDBA.TBLUGTMCSTMR (RND_CD_CLSS,TB_RSLT_CLSS,MB_CDHD_NO,HG_NM,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO	\n");
		sql.append("\t			,RECP_DATE,JUMIN_NO,GOLF_CDHD_GRD_CLSS,JOIN_CHNL,WK_DATE,WK_TIME,AUTH_CLSS,ACPT_CHNL_CLSS,RCRU_PL_CLSS,REJ_RSON)	\n");
		sql.append("\t	VALUES('2',?,'ezwel',?,?,?,?,TO_CHAR(SYSDATE,'yyyymmdd'),?,?,?,TO_CHAR(SYSDATE,'yyyymmdd'),TO_CHAR(SYSDATE,'hh24miss'),?,'3',?,? )	\n");
		return sql.toString();
	}

   	/** ***********************************************************************
	* TM 연회비 내역
	************************************************************************ */
	private String getInsTmPay(){
		StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	INSERT INTO  BCDBA.TBGLUGANLFEECTNT	\n");
		sql.append("\t	(JUMIN_NO,AUTH_NO,CARD_NO, AUTH_DATE,AUTH_TIME,AUTH_AMT,AUTH_CLSS,RND_CD_CLSS,MB_CDHD_NO)	\n");
		sql.append("\t	VALUES ( ?, ?,?, TO_CHAR(SYSDATE,'yyyyMMdd'), TO_CHAR(SYSDATE,'hh24miss') , ? , ?, '2', 'ezwel' )	\n");
		return sql.toString();
	}

}
