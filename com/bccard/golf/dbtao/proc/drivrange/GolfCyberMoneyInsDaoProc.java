/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfCyberMoneyInsDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : SKY72드림골프레인지 예약신청시 사이버머니차감 처리
*   적용범위  : golf
*   작성일자  : 2009-07-02
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

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfCyberMoneyInsDaoProc extends AbstractProc {

	public static final String TITLE = "SKY72드림골프레인지 예약신청시 사이버머니차감 처리";

	/** *****************************************************************
	 * GolfCyberMoneyInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfCyberMoneyInsDaoProc() {}
	
	/**
	 * SKY72드림골프레인지 예약신청시 사이버머니차감 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		
			
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
		    /**SEQ_NO 가져오기**************************************************************/
			String sql2 = this.getCyberMoneyNextValQuery(); 
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = pstmt2.executeQuery();			
			long cyber_money_max_seq_no = 0L;
			if(rs2.next()){
				cyber_money_max_seq_no = rs2.getLong("SEQ_NO");
			}
			if(rs2 != null) rs2.close();
            if(pstmt2 != null) pstmt2.close();
            
            /**Insert************************************************************************/
            
            String sql3 = this.getMemberTmInfoQuery();
			PreparedStatement pstmt3 = conn.prepareStatement(sql3);

			
			int totCyberMoney = cyberMoney-Integer.parseInt(data.getString("DRVR_AMT"));
			
			int idx = 0;
			pstmt3.setLong(++idx, cyber_money_max_seq_no ); 		//COME_SEQ_SEQ_NO
			pstmt3.setString(++idx, data.getString("GF_ID") ); 		//CDHD_ID
			pstmt3.setInt(++idx, Integer.parseInt(data.getString("DRVR_AMT")) );					//ACM_DDUC_AMT
			pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
			pstmt3.setString(++idx, "0004" );						//CBMO_USE_CLSS :  0004:파3부킹
			//pstmt3.setString(++idx, RSVT_SQL_NO );					//GOLF_SVC_RSVT_NO : 골프서비스예약번호  			

        	
			//result2 = pstmt3.executeUpdate();
            if(pstmt3 != null) pstmt3.close();
			
            
        	// 02-2. 회원테이블에 사이버머니 내역을 업데이트 해준다.
			debug("=================GolfBkParTimeRsInsDaoProc============= 02-2. 회원테이블에 사이버머니 내역을 업데이트 해준다."); 
            //sql3 = this.getMemberUpdateQuery(RIDG_PERS_NUM);
			pstmt3 = conn.prepareStatement(sql3);
			//pstmt3.setString(1, userEtt.getAccount() ); 		//CDHD_ID
			
			//result2 = pstmt3.executeUpdate();
            if(pstmt3 != null) pstmt3.close();
			
			
	           
				
			if(result > 0) {
				conn.commit();
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
