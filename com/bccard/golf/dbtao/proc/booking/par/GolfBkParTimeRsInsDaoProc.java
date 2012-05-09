/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrRegDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄부킹 골프장 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.par;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfBkParTimeRsInsDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 프리미엄부킹 골프장 등록 처리";

	/** *****************************************************************
	 * GolfadmGrRegDaoProc 프로세스 생성자
	 * @param N/A 
	 ***************************************************************** */
	public GolfBkParTimeRsInsDaoProc() {}
	
	/** 
	 * 관리자 프리미엄부킹 골프장 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		int resultUp = 0;
		int result2 = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		DbTaoResult result =  new DbTaoResult(title);
		DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
		String userID = "";
				
		try {
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				userID = userEtt.getAccount();
			}
			String payType = data.getString("payType");
			String affi_green_seq_no = data.getString("AFFI_GREEN_SEQ_NO");
			
			dataSet.setString("AFFI_GREEN_SEQ_NO", affi_green_seq_no);
			data.setString("CDHD_ID",userID);
			conn = context.getDbConnection("default", null);	
			// 1일1회 신청 중복 체크
			int recvOverLapChk = this.getRecvOverLapChk(conn, data);

			if (recvOverLapChk == 0) {

				conn.setAutoCommit(false);		

				/**SEQ_NO 가져오기**************************************************************/
				sql = this.getMaxNoQuery(); 
				pstmt = conn.prepareStatement(sql);
				rs = pstmt.executeQuery();			
				String max_IDX = "";
				String max_RSVT_CLSS = "";
				String rsvt_SQL_NO = "";
				if(rs.next()){
					max_IDX = GolfUtil.reSizeLen(rs.getString("MAX_IDX"),"0",7);    				
					max_RSVT_CLSS = rs.getString("MAX_RSVT_CLSS");
					rsvt_SQL_NO = max_RSVT_CLSS + "" + max_IDX;
				}
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();

				/**골프회원예약등급일련번호 가져오기 20090907**************************************************************/

				GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
				DbTaoResult parTimeView = proc_times.getParBenefit(context, dataSet, request);
				parTimeView.next();
				int rsvt_cdhd_grd_seq_no = parTimeView.getInt("intBkGrade");
				
				/**Insert************************************************************************/
					
				// 01. insert
				sql = this.getInsertQuery();
				pstmt = conn.prepareStatement(sql); 
				
				int idx = 0;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, rsvt_SQL_NO); 
				pstmt.setString(++idx, max_RSVT_CLSS); 
				pstmt.setString(++idx, userID); 
				pstmt.setString(++idx, affi_green_seq_no); 
				pstmt.setString(++idx, data.getString("BK_DATE")); 
				pstmt.setString(++idx, data.getString("HP_DDD_NO")); 
				pstmt.setString(++idx, data.getString("HP_TEL_HNO")); 
				pstmt.setString(++idx, data.getString("HP_TEL_SNO")); 
				pstmt.setString(++idx, data.getString("EMAIL_ID")); 
				pstmt.setInt(++idx, rsvt_cdhd_grd_seq_no); 
				
				resultUp = pstmt.executeUpdate();
				if(pstmt != null) pstmt.close();
				
				// 02. 예약이며 사이버 머니를 사용했을 경우 사이버 머니를 차감한다.
				debug("=================GolfBkParTimeRsInsDaoProc============= 02. 예약이며 사이버 머니를 사용했을 경우 사이버 머니를 차감한다.");
				
				/* 9월 이벤트 동안 제외
				if(payType.equals("cyber")){
						
					// 02-0. 현재 사이버 머니 금액을 가져온다.
					sql = this.getCyberMoneyQuery(); 
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, userEtt.getAccount() ); 	
					rs = pstmt.executeQuery();			
					int cyberMoney = 0;
					if(rs.next()){
						cyberMoney = rs.getInt("TOT_AMT");
					}
					if(rs != null) rs.close();
					if(pstmt != null) pstmt.close();
					
					// 02-1. 사이버머니 사용내역 테이블에 금액을 넣어준다.
					debug("=================GolfBkParTimeRsInsDaoProc============= 02-1. 사이버머니 사용내역 테이블에 금액을 넣어준다.");
					//SEQ_NO 가져오기************************************************************
					String sql2 = this.getCyberMoneyNextValQuery(); 
					PreparedStatement pstmt2 = conn.prepareStatement(sql2);
					ResultSet rs2 = pstmt2.executeQuery();			
					long cyber_MONEY_MAX_SEQ_NO = 0L;
					if(rs2.next()){
						cyber_MONEY_MAX_SEQ_NO = rs2.getLong("SEQ_NO");
					}
					if(rs2 != null) rs2.close();
					if(pstmt2 != null) pstmt2.close();
					
					//*Insert**********************************************************************
					
					String sql3 = this.getMemberTmInfoQuery();
					PreparedStatement pstmt3 = conn.prepareStatement(sql3);

					int ridg_PERS_NUM = 5000;	// 결제금액     
					int totCyberMoney = cyberMoney-ridg_PERS_NUM;
					
					idx = 0;
					pstmt3.setLong(++idx, cyber_MONEY_MAX_SEQ_NO ); 		//COME_SEQ_SEQ_NO
					pstmt3.setString(++idx, userEtt.getAccount() ); 		//CDHD_ID
					pstmt3.setInt(++idx, ridg_PERS_NUM );					//ACM_DDUC_AMT
					pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
					pstmt3.setString(++idx, "0004" );						//CBMO_USE_CLSS :  0004:파3부킹
					pstmt3.setString(++idx, rsvt_SQL_NO );					//GOLF_SVC_RSVT_NO : 골프서비스예약번호  			

					
					result2 = pstmt3.executeUpdate();
					if(pstmt3 != null) pstmt3.close();
					
					
					// 02-2. 회원테이블에 사이버머니 내역을 업데이트 해준다. 
					debug("=================GolfBkParTimeRsInsDaoProc============= 02-2. 회원테이블에 사이버머니 내역을 업데이트 해준다.");
					sql3 = this.getMemberUpdateQuery(ridg_PERS_NUM);
					pstmt3 = conn.prepareStatement(sql3);
					pstmt3.setString(1, userEtt.getAccount() ); 		//CDHD_ID
					
					result2 = pstmt3.executeUpdate();
					if(pstmt3 != null) pstmt3.close();
				}
				*/

				result.addString("RSVT_SQL_NO" 		,rsvt_SQL_NO);
//				debug("=================예약하는곳 => rsvt_SQL_NO : " + rsvt_SQL_NO);
				
				// 리턴
				if(resultUp > 0) {
					conn.commit();
					result.addString("RESULT", "00"); //정상결과
				} else {
					conn.rollback();
					result.addString("RESULT", "01"); //
				}
				

			} else {
				//있을경우
				result.addString("RESULT" ,"02" );
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}		

		return result;
	}
	
	/**
	 * 1일 1회 신청 중복 체크
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getRecvOverLapChk(Connection conn, TaoDataSet data) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getSelectOverQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, "P" );	
			pstmt.setString(++idx, data.getString("CDHD_ID") );
			pstmt.setString(++idx, data.getString("BK_DATE") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
		} catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}

		}
		
		return result;
	}	

    
	/** ***********************************************************************
    * Insert Query를 생성하여 리턴한다.   (예약테이블)
    * 20090907 골프회원예약등급일련번호 추가 (RSVT_CDHD_GRD_SEQ_NO)
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO BCDBA.TBGRSVTMGMT (											\n");
		sql.append("\t  GOLF_SVC_RSVT_NO, GOLF_SVC_RSVT_MAX_VAL, CDHD_ID, AFFI_GREEN_SEQ_NO, ROUND_HOPE_DATE				\n");
		sql.append("\t  , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, RSVT_YN, REG_ATON, RSVT_CDHD_GRD_SEQ_NO		\n");
		sql.append("\t																		\n");
		sql.append("\t  ) VALUES (															\n");
		sql.append("\t  ?, ?, ?, ?, ?														\n");
		sql.append("\t  , ?, ?, ?, ?, 'Y', TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?			\n");
		sql.append("\t  )																	\n");
        return sql.toString();
    }
         
    /** ***********************************************************************
    * 1일 1회 중복신청 체크 Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getSelectOverQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("SELECT	\n");
		sql.append("\t 	AFFI_GREEN_SEQ_NO	\n");
		sql.append("\t FROM BCDBA.TBGRSVTMGMT	\n");
		sql.append("\t WHERE GOLF_SVC_RSVT_MAX_VAL LIKE '%' || ? 	\n"); //프리미엄부킹 : 2009M0000001 파3부킹 : 2009P0000001 스카이72드림골프레인지 : 2009D0000001 스카이72드림듄스 : 2009S0000001 제주골프라운지 : 2009J0000001 피팅서비스 : 2009F0000001
		sql.append("\t AND CDHD_ID = ?	\n");
		sql.append("\t AND ROUND_HOPE_DATE = ?	\n");
		sql.append("\t AND RSVT_YN = 'Y'	\n");
		
        return sql.toString();
    }

	 /** ***********************************************************************
      * 예약번호 MAX값을 가져온다.    
      ************************************************************************ */
	private String getMaxNoQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT NVL(SUBSTR(MAX(GOLF_SVC_RSVT_NO),6,12)+1,1) AS MAX_IDX,  (TO_CHAR(SYSDATE, 'YYYY')||'P') AS MAX_RSVT_CLSS \n");
		sql.append("\t  FROM BCDBA.TBGRSVTMGMT 														\n");
		sql.append("\t  WHERE GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')     						\n");      
		return sql.toString();
	}


 	/** ***********************************************************************
	* 현재 사이버머니 총액 가져오기    
	************************************************************************ */
	private String getCyberMoneyQuery(){
		StringBuffer sql = new StringBuffer();		

		sql.append("\n");
		sql.append("\t  SELECT (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) AS TOT_AMT		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD										\n");
		sql.append("\t  WHERE CDHD_ID=?												\n");
		
		return sql.toString();
	}

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다. - 사이버머니 적립 최대 idx    
    ************************************************************************ */
    private String getCyberMoneyNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(COME_SEQ_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGCBMOUSECTNTMGMT \n");
		return sql.toString();
    }
	
 	/** ***********************************************************************
	* 사이버머니 등록하기    
	************************************************************************ */
	private String getMemberTmInfoQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGCBMOUSECTNTMGMT (													\n");
		sql.append("\t  		COME_SEQ_SEQ_NO, CDHD_ID, ACM_DDUC_CLSS, ACM_DDUC_AMT, REST_AMT, COME_ATON		\n");
		sql.append("\t  		, CBMO_USE_CLSS, GOLF_SVC_RSVT_NO												\n");
		sql.append("\t  		) VALUES (																		\n");
		sql.append("\t  		?, ?, 'N', ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), ?, ?						\n");
		sql.append("\t  		)																				\n");
		return sql.toString();
	}
	
 	/** ***********************************************************************
	* 회원정보 업데이트하기
	************************************************************************ */
	private String getMemberUpdateQuery(int cyberMoney){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  	UPDATE BCDBA.TBGGOLFCDHD								\n");
		sql.append("\t  	SET CBMO_DDUC_TOT_AMT=CBMO_DDUC_TOT_AMT+"+cyberMoney+"	\n");
		sql.append("\t  	WHERE CDHD_ID=?											\n");
		return sql.toString();
	}	
}
