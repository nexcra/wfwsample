/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLessonRegActn
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨프로그램 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lesson;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmLessonInsDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 레슨프로그램 등록 처리";

	/** *****************************************************************
	 * GolfAdmLessonInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLessonInsDaoProc() {}
	
	/**
	 * 관리자 레슨프로그램 등록 처리
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
			debug("===============1");
			sql = this.getNextValQuery(); //레슨번호 쿼리
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String lsn_seq_no = "";
			if(rs.next()){
				lsn_seq_no = rs.getString("LESN_SEQ_NO");
			}
			debug("===============2");
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			debug("===============3");
			int idx = 0;
			pstmt.setString(++idx, lsn_seq_no );		
			pstmt.setString(++idx, data.getString("LSN_TYPE_CD") ); 
			pstmt.setString(++idx, data.getString("LSN_NM") ); 
			pstmt.setString(++idx, data.getString("EVNT_YN") );
			pstmt.setString(++idx, data.getString("IMG_NM") );
			pstmt.setString(++idx, data.getString("LSN_PRD_CLSS") );
			pstmt.setString(++idx, data.getString("LSN_START_DT") );
			pstmt.setString(++idx, data.getString("LSN_END_DT") );
			pstmt.setString(++idx, data.getString("APLC_END_DT") );
			pstmt.setString(++idx, data.getString("LSN_PRD_INFO") );
			
			pstmt.setInt(++idx, data.getInt("LSN_STTL_CST") ); 
			pstmt.setInt(++idx, data.getInt("LSN_DC_CST") ); 
			pstmt.setString(++idx, data.getString("LSN_PL") ); 
			pstmt.setString(++idx, data.getString("MAP_NM") );
			pstmt.setString(++idx, data.getString("CHG_DDD_NO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_HNO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_SNO") );
			pstmt.setString(++idx, data.getString("APLC_MTHD") );
			pstmt.setInt(++idx, data.getInt("APLC_LETE_NUM") );
			pstmt.setString(++idx, data.getString("LSN_INTD") );

			pstmt.setString(++idx, data.getString("COOP_CP_CD") );
			pstmt.setString(++idx, data.getString("COOP_RMRK") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setInt(++idx, data.getInt("LSN_DC_RT") );
			pstmt.setString(++idx, data.getString("MAIN_BANNER_IMG") );
			pstmt.setString(++idx, data.getString("MAIN_BANNER_URL") );
			pstmt.setString(++idx, data.getString("MAIN_EPS_YN") );
			debug("===============4");
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            debug("===============5");
			sql = this.getSelectForUpdateQuery();//레슨번호 쿼리
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, lsn_seq_no);
			debug("===============6");
            rs = pstmt.executeQuery();
            debug("===============7");
            String ctnt = data.getString("LSN_CTNT");
            
//			if(rs.next()) {
//				
//				java.sql.Clob clob = rs.getClob("LESN_CTNT");
//				writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//				reader = new CharArrayReader(ctnt.toCharArray());
//				
//				char[] buffer = new char[1024];
//				int read = 0;
//				while ((read = reader.read(buffer,0,1024)) != -1) {
//					writer.write(buffer,0,read);
//				}
//				writer.flush(); 
//				/*
//				java.sql.Clob clob = rs.getClob("LESN_CTNT");
//                //writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
//                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//				reader = new CharArrayReader(data.getString("LSN_CTNT").toCharArray());
//				
//				char[] buffer = new char[1024];
//				int read = 0;
//				while ((read = reader.read(buffer,0,1024)) != -1) {
//					writer.write(buffer,0,read);
//				}
//				writer.flush();
//				*/
//			}
			debug("===============8");
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			debug("===============9");
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) { rs.close(); } else  {;} } catch (Exception ignored) {}
            try { if(pstmt != null) { pstmt.close(); } else  {;} } catch (Exception ignored) {}
            try { if(conn  != null) { conn.close(); } else  {;}  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGLESNMGMT (	\n");
		sql.append("\t  LESN_SEQ_NO, LESN_CLSS, LESN_NM, EVNT_YN, LESN_IMG, LESN_TRM_INP_TP_CLSS, LESN_STRT_DATE, LESN_END_DATE, APLC_FIN_DATE, LESN_TRM_INFO, 	\n");
		sql.append("\t  LESN_NORM_COST, LESN_DC_COST, LESN_PL_INFO, OLM_IMG, DDD_NO, TEL_HNO, TEL_SNO, LESN_APLC_MTHD_INFO, APLC_LIMT_PERS_NUM, LESN_EXPL, 	\n");
		sql.append("\t  GOLF_LESN_AFFI_FIRM_CLSS, LESN_AFFI_EXPL, LESN_CTNT, INQR_NUM, CMD_NUM, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON, LESN_DC_RT, 	\n");
		sql.append("\t  MAIN_BANNER_IMG, MAIN_BANNER_URL, MAIN_EPS_YN 	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,?,?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,?,?,?,?,?,?,	\n");
		sql.append("\t 	?,?,EMPTY_CLOB(),0,0,?,NULL,TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),NULL,?, \n");	
		sql.append("\t  ?,?,?	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(LESN_SEQ_NO),0)+1 LESN_SEQ_NO FROM BCDBA.TBGLESNMGMT \n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * CLOB Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT LESN_CTNT FROM BCDBA.TBGLESNMGMT \n");
         sql.append("WHERE LESN_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
}
