/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemInsDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 회원 > 회원가입처리
*   적용범위  : golf 
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member.cyber;

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMemCyberInsDaoProc extends AbstractProc {

	public static final String TITLE = "사이버머니 > 등록처리";

	public GolfMemCyberInsDaoProc() {}
	
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;

				
		try {

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			int amount 				= data.getInt("amount");		
			String payType 			= data.getString("payType");	
			//debug("GolfMemCyberInsDaoProc =============== amount => " + amount);
			//debug("GolfMemCyberInsDaoProc =============== payType => " + payType);
			

			// 01. 현재 사이버 머니 가져오기
			//debug("===========GolfMemCyberInsDaoProc=======01. 현재 사이버 머니 가져오기");
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
            
            
            
			
			// 02. 사이버머니 등록하기
			debug("===========GolfMemCyberInsDaoProc=======02. 사이버머니 등록하기");
			
            /**SEQ_NO 가져오기**************************************************************/
			String sql2 = this.getNextValQuery(); 
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = pstmt2.executeQuery();			
			long max_seq_no = 0L;
			if(rs2.next()){
				max_seq_no = rs2.getLong("SEQ_NO");
			}
			if(rs2 != null) rs2.close();
            if(pstmt2 != null) pstmt2.close();
            
            /**Insert************************************************************************/
            
            String sql3 = this.getMemberTmInfoQuery();
			PreparedStatement pstmt3 = conn.prepareStatement(sql3);
			
			int totCyberMoney = cyberMoney+amount;
			
			int idx = 0;
			pstmt3.setLong(++idx, max_seq_no ); 								//COME_SEQ_SEQ_NO
			pstmt3.setString(++idx, userEtt.getAccount() ); 		//CDHD_ID
			pstmt3.setInt(++idx, amount );							//ACM_DDUC_AMT
			pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
        	
			result = pstmt3.executeUpdate();
            if(pstmt3 != null) pstmt3.close();
			
			
            // 03. 회원테이블에 사이버 머니 업데이트 하기
			debug("===========GolfMemCyberInsDaoProc=======03. 회원테이블에 사이버 머니 업데이트 하기");
            sql3 = this.getMemberUpdateQuery(amount);
			pstmt3 = conn.prepareStatement(sql3);
			pstmt3.setString(1, userEtt.getAccount() ); 		//CDHD_ID
			
			result = pstmt3.executeUpdate();
            if(pstmt3 != null) pstmt3.close();
            
            
            
            // 04. 결제 테이블 등록하기
			//debug("===========GolfMemCyberInsDaoProc=======04. 결제 테이블 등록하기");
          
			
			if(result > 0) {

				userEtt.setCyberMoney(totCyberMoney);
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
    private String getNextValQuery(){
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
		sql.append("\t  INSERT INTO BCDBA.TBGCBMOUSECTNTMGMT (														\n");
		sql.append("\t  		COME_SEQ_SEQ_NO, CDHD_ID, ACM_DDUC_CLSS, ACM_DDUC_AMT, REST_AMT, COME_ATON		\n");
		sql.append("\t  		) VALUES (																		\n");
		sql.append("\t  		?, ?, 'Y', ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')							\n");
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
		sql.append("\t  	SET CBMO_ACM_TOT_AMT=CBMO_ACM_TOT_AMT+"+cyberMoney+"	\n");
		sql.append("\t  	WHERE CDHD_ID=?											\n");
		return sql.toString();
	}
    
}
