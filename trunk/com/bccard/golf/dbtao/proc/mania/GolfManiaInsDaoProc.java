/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaRegActn
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 등록처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mania;

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
* @author (주)만세커뮤니케이션
* @version 1.0
******************************************************************************/
public class GolfManiaInsDaoProc extends AbstractProc {

	public static final String TITLE = "골프장리무진할인신청관리 등록처리";

	/*******************************************************************
	* GolfAdmManiaInsDaoProc 프로세스 생성자
	* @param N/A
	***************************************************************** */
	public GolfManiaInsDaoProc() {}

	/**
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
		
			sql = this.getNextValQuery(); //
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();			
			long recv_no = 0L;
			
			if(rs.next()){
				recv_no = rs.getLong("APLC_SEQ_NO");
			}
			
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			/*****************************************************************************/
		
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
		
			int idx = 0;
			pstmt.setLong(++idx, recv_no );
			pstmt.setString(++idx, data.getString("APLC_PE_CLSS") ); 
			pstmt.setString(++idx, data.getString("ID") );
			pstmt.setString(++idx, data.getString("CP_NM") );
			pstmt.setString(++idx, data.getString("EMAIL_ID") ); 
			pstmt.setString(++idx, data.getString("CHG_DDD_NO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_HNO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_SNO") );
			pstmt.setString(++idx, data.getString("HP_DDD_NO") );
		
			pstmt.setString(++idx, data.getString("HP_TEL_HNO") );
			pstmt.setString(++idx, data.getString("HP_TEL_SNO") );
			pstmt.setString(++idx, data.getString("CKD_CODE") );
			pstmt.setString(++idx, data.getString("STR_PLC") ); 
			pstmt.setString(++idx, data.getString("PIC_DATE") ); 
			pstmt.setString(++idx, data.getString("PIC_TIME") );
			pstmt.setString(++idx, data.getString("TOFF_DATE") );
			pstmt.setString(++idx, data.getString("TOFF_TIME") );
			pstmt.setString(++idx, data.getString("GCC_NM") );
			pstmt.setInt(++idx, 	  data.getInt("TK_PRS") );
			pstmt.setString(++idx, data.getString("MEMO") );
			pstmt.setString(++idx, data.getString("GOLF_MGZ_DLV_PL_CLSS") );
			pstmt.setString(++idx, data.getString("ZIPCODE") ); 
		
			pstmt.setString(++idx, data.getString("ADDR") ); 
			pstmt.setString(++idx, data.getString("ADDR2") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );			
			pstmt.setString(++idx, data.getString("ADDR_CLSS") );
		
			result = pstmt.executeUpdate();
		
			if(pstmt != null) pstmt.close();
		
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
		
		} catch(Exception e) {
			
			try {
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
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
    	
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGAPLCMGMT ( \n");
		sql.append("\t  APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, CSLT_YN, CDHD_ID, CO_NM, EMAIL, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, \n");
		sql.append("\t  HP_TEL_HNO, HP_TEL_SNO, GOLF_LMS_CAR_KND_CLSS, DPRT_PL_INFO, PU_DATE, PU_TIME, TEOF_DATE, TEOF_TIME,  \n");
		sql.append("\t  GREEN_NM, RIDG_PERS_NUM, MEMO_EXPL, \n");
		sql.append("\t  GOLF_MGZ_DLV_PL_CLSS, ZP, ADDR, DTL_ADDR, REG_MGR_ID, REG_ATON, NW_OLD_ADDR_CLSS \n");		  
		sql.append("\t ) VALUES ( \n"); 		  
		sql.append("\t  ?,?,'N',?,?,?,?,?,?,?, \n");
		sql.append("\t  ?,?,?,?,?,?,?,?, \n");
		sql.append("\t  ?,?,?, \n");
		sql.append("\t  ?,?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ? \n");
		sql.append("\t \n)");
		
		return sql.toString();

    }
    

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getNextValQuery(){
	    StringBuffer sql = new StringBuffer();
		sql.append("\n");
	    sql.append("SELECT NVL(MAX(APLC_SEQ_NO),0)+1 APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT \n");
		return sql.toString();
	}
    
}

