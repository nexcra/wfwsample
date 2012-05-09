/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRangeRsvtUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : SKY72드림골프레인지 예약취소 처리
*   적용범위  : golf
*   작성일자  : 2009-06-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.drivrange;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfRangeRsvtUpdDaoProc extends AbstractProc {

	public static final String TITLE = "SKY72드림골프레인지 예약취소 처리";

	/** *****************************************************************
	 * GolfRangeRsvtUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfRangeRsvtUpdDaoProc() {}
	
	/**
	 * SKY72드림골프레인지 예약취소 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		int result2 = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
		
		String userNm = ""; 
		String memClss ="";
		String userId = "";
		String isLogin = ""; 
		String juminno = ""; 
		String memGrade = ""; 
		int intMemGrade = 0; 
		int intCyberMoney = 0; 
		String email1 = ""; 
		
		UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		
		 if(usrEntity != null) {
			userNm		= (String)usrEntity.getName(); 
			memClss		= (String)usrEntity.getMemberClss();
			userId		= (String)usrEntity.getAccount(); 
			juminno 	= (String)usrEntity.getSocid(); 
			memGrade 	= (String)usrEntity.getMemGrade(); 
			intMemGrade	= (int)usrEntity.getIntMemGrade(); 
			intCyberMoney	= (int)usrEntity.getCyberMoney(); //사이버머니
			email1 	= (String)usrEntity.getEmail1(); 
		}
		
		//debug("email1 =====> "+ email1); 
		 
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			// 사이버머니 사용여부 체크
			//int CyberMoneyChk = this.getCyberMoneyChk(conn, data);
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            /*
            if (CyberMoneyChk > 0){ // 사이버머니 복구 
            	
            	// 현재 사이버 머니 금액을 가져온다.
				sql = this.getCyberMoneyQuery(); 
	            pstmt = conn.prepareStatement(sql);
	        	pstmt.setString(1, data.getString("GF_ID") ); 	
	            rs = pstmt.executeQuery();			
				int cyberMoney = 0;
				if(rs.next()){
					cyberMoney = rs.getInt("TOT_AMT");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
            	
	            // 사이버머니 사용내역 테이블에 금액을 넣어준다.
			    // SEQ_NO 가져오기
				String sql2 = this.getCyberMoneyNextValQuery(); 
	            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
	            ResultSet rs2 = pstmt2.executeQuery();			
				long cyber_money_max_seq_no = 0L;
				if(rs2.next()){
					cyber_money_max_seq_no = rs2.getLong("SEQ_NO");
				}
				if(rs2 != null) rs2.close();
	            if(pstmt2 != null) pstmt2.close();
	            
	            // Insert
	            
	            String sql3 = this.getMemberTmInfoQuery();
				PreparedStatement pstmt3 = conn.prepareStatement(sql3);

				
				int totCyberMoney = cyberMoney+Integer.parseInt(data.getString("DRVR_AMT"));
				
				idx = 0;
				pstmt3.setLong(++idx, cyber_money_max_seq_no ); 		//COME_SEQ_SEQ_NO
				pstmt3.setString(++idx, data.getString("GF_ID") ); 		//CDHD_ID
				pstmt3.setInt(++idx, Integer.parseInt(data.getString("DRVR_AMT")) );					//ACM_DDUC_AMT
				pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
				pstmt3.setString(++idx, "0006" );						//CBMO_USE_CLSS :  0006:Sky72드라이빙레인지
				pstmt3.setString(++idx, data.getString("RSVT_SQL_NO") );					//GOLF_SVC_RSVT_NO : 골프서비스예약번호  			

	        	
				result2 = pstmt3.executeUpdate();
	            if(pstmt3 != null) pstmt3.close();
				
	            
	        	// 회원테이블에 사이버머니 내역을 업데이트 해준다.
		        sql3 = this.getMemberUpdateQuery(Integer.parseInt(data.getString("DRVR_AMT")));
				pstmt3 = conn.prepareStatement(sql3);
				pstmt3.setString(1, data.getString("GF_ID") ); 		//CDHD_ID
				
				result2 = pstmt3.executeUpdate();
	            if(pstmt3 != null) pstmt3.close();
	            
	            if(usrEntity != null) {
	            	usrEntity.setCyberMoney((int)totCyberMoney); //사이버머니
				}
            }
            */
			if(result > 0) {
				conn.commit();
				
				// 예약정보조회 ----------------------------------------------------------			
				sql = this.getRsvtSelectQuery();   
				
				// 입력값 (INPUT)         
				idx = 0;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, data.getString("RSVT_SQL_NO"));
				
				rs = pstmt.executeQuery();
				
				String golf_svc_rsvt_no = "";
				String rsvt_able_year = "";
				String rsvt_able_month = "";
				String rsvt_able_day = "";
				String rsvt_strt_hh = "";
				String rsvt_strt_mi = "";
				String rsvt_end_hh = "";
				String rsvt_end_mi = "";
				String cncl_date  = "";
				String hp_ddd_no  = "";
				String hp_tel_hno  = "";
				String hp_tel_sno  = "";
				String green_nm  = "";
				
				if(rs != null) {
					while(rs.next())  {
						golf_svc_rsvt_no = rs.getString("GOLF_SVC_RSVT_NO");
						rsvt_able_year = rs.getString("RSVT_ABLE_YEAR");
						rsvt_able_month = rs.getString("RSVT_ABLE_MONTH");
						rsvt_able_day = rs.getString("RSVT_ABLE_DAY");
						rsvt_strt_hh = rs.getString("RSVT_STRT_HH");
						rsvt_strt_mi = rs.getString("RSVT_STRT_MI");
						rsvt_end_hh = rs.getString("RSVT_END_HH");
						rsvt_end_mi = rs.getString("RSVT_END_MI");
						cncl_date = rs.getString("CNCL_DATE");
						hp_ddd_no = rs.getString("HP_DDD_NO");
						hp_tel_hno = rs.getString("HP_TEL_HNO");
						hp_tel_sno = rs.getString("HP_TEL_SNO");
						green_nm = rs.getString("GREEN_NM");						
					}
				}
				if(pstmt != null) pstmt.close();
				if(rs != null) rs.close(); 
				
				String phone = hp_ddd_no + hp_tel_hno + hp_tel_sno;
					
				HashMap smsMap = new HashMap();
				
				smsMap.put("ip", request.getRemoteAddr());
				smsMap.put("sName", userNm);
				smsMap.put("sPhone1", hp_ddd_no);
				smsMap.put("sPhone2", hp_tel_hno);
				smsMap.put("sPhone3", hp_tel_sno);
				
				// sms발송
				if (!phone.equals("")) {
					debug("SMS시작>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>..");
					String smsClss = "644";
					//String message = "[드림골프레인지]"+ userNm +"님 "+ rsvt_able_month+"/"+rsvt_able_day +" "+ rsvt_strt_hh +"시~"+ rsvt_end_hh +"시 예약취소 - Golf Loun.G";
					String message = "[드라이빙레인지-"+green_nm+"]"+ userNm +"님 "+ rsvt_able_month+"/"+rsvt_able_day +" 예약취소 - Golf Loun.G";
					SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
					String smsRtn = smsProc.send(smsClss, smsMap, message);
					debug("SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.."+message);
				}	
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}
	
	/**
	 * 사이버머니 사용여부 체크
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getCyberMoneyChk(Connection conn, TaoDataSet data) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getSelectQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;	
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO") );
			pstmt.setString(++idx, data.getString("GF_ID") );
			
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
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGRSVTMGMT SET	\n");
		sql.append("\t  RSVT_YN = 'N', CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t WHERE GOLF_SVC_RSVT_NO=?	\n");
		
        return sql.toString();
    }
 
	 /** ***********************************************************************
	* 현재 사이버머니 총액 가져오기    
	************************************************************************ */
    /*
	private String getCyberMoneyQuery(){
		StringBuffer sql = new StringBuffer();		

		sql.append("\n");
		sql.append("\t  SELECT (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) AS TOT_AMT		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD										\n");
		sql.append("\t  WHERE CDHD_ID=?												\n");
		
		return sql.toString();
	}
	*/
    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다. - 사이버머니 적립 최대 idx    
    ************************************************************************ */
    /*
    private String getCyberMoneyNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(COME_SEQ_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGCBMOUSECTNTMGMT \n");
		return sql.toString();
    }
	*/
 	/** ***********************************************************************
	* 사이버머니 등록하기    
	************************************************************************ */
    /*
	private String getMemberTmInfoQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGCBMOUSECTNTMGMT (													\n");
		sql.append("\t  		COME_SEQ_SEQ_NO, CDHD_ID, ACM_DDUC_CLSS, ACM_DDUC_AMT, REST_AMT, COME_ATON		\n");
		sql.append("\t  		, CBMO_USE_CLSS, GOLF_SVC_RSVT_NO												\n");
		sql.append("\t  		) VALUES (																		\n");
		sql.append("\t  		?, ?, 'Y', ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), ?, ?						\n");
		sql.append("\t  		)																				\n");
		return sql.toString();
	}
	*/
 	/** ***********************************************************************
	* 회원정보 업데이트하기
	************************************************************************ */
    /*
	private String getMemberUpdateQuery(int cyberMoney){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  	UPDATE BCDBA.TBGGOLFCDHD								\n");
		sql.append("\t  	SET CBMO_ACM_TOT_AMT=CBMO_ACM_TOT_AMT+"+cyberMoney+"	\n");
		sql.append("\t  	WHERE CDHD_ID=?											\n");
		return sql.toString();
	} 
	*/
	 /** ***********************************************************************
	* 현재 사이버머니 사용여부 가져오기    
	************************************************************************ */
	private String getSelectQuery(){
		StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("\t  SELECT COME_SEQ_SEQ_NO												\n");
		sql.append("\t  FROM BCDBA.TBGCBMOUSECTNTMGMT								\n");
		sql.append("\t  WHERE CBMO_USE_CLSS = '0006'										\n");
		sql.append("\t  AND GOLF_SVC_RSVT_NO = ?											\n");
		sql.append("\t  AND CDHD_ID = ?											\n");
		
		return sql.toString();
	}
    
    /** ***********************************************************************
	* 예약정보를 리턴한다.    
	************************************************************************ */
	private String getRsvtSelectQuery(){
	    StringBuffer sql = new StringBuffer();
		
	    sql.append("\n SELECT 	");
		sql.append("\n 	GOLF_SVC_RSVT_NO, RSVT_ABLE_YEAR, RSVT_ABLE_MONTH, RSVT_ABLE_DAY,	");
		sql.append("\n 	RSVT_STRT_HH, RSVT_STRT_MI, RSVT_END_HH, RSVT_END_MI, CNCL_DATE || ' 06:00' CNCL_DATE,	");
		sql.append("\n 	 HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, GREEN_NM	");
		sql.append("\n FROM (SELECT TGR.GOLF_SVC_RSVT_NO, 	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'YYYY') RSVT_ABLE_YEAR,		");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'MM') RSVT_ABLE_MONTH,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'DD') RSVT_ABLE_DAY,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24') RSVT_STRT_HH,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'MI') RSVT_STRT_MI,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24') RSVT_END_HH,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'MI') RSVT_END_MI,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD') - 3, 'YYYY/MM/DD') CNCL_DATE	,	");
		sql.append("\n 			TGR.HP_DDD_NO, TGR.HP_TEL_HNO, TGR.HP_TEL_SNO, TGRN.GREEN_NM	");
		sql.append("\n 		FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT	");
		sql.append("\n 			,BCDBA.TBGAFFIGREEN TGRN 	");
		sql.append("\n 		WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
		sql.append("\n 		AND TGRT.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO	");
		sql.append("\n 		AND TGRD.AFFI_GREEN_SEQ_NO = TGRN.AFFI_GREEN_SEQ_NO	");
		sql.append("\n 		AND TGR.GOLF_SVC_RSVT_NO = ?	)	");
		
	    return sql.toString();
	}
}
