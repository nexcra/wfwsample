/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBcInsDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 BC Golf 이벤트 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event;

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
* Golf
* @author	(주)만세커뮤니케이션 
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBcInsDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 BC Golf 이벤트 등록 처리";

	/** *****************************************************************
	 * GolfAdmEvntBcInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBcInsDaoProc() {}
	
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

			sql = this.getNextValQuery(); //레슨번호 쿼리
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String seq_no = "";
			if(rs.next()){
				seq_no = rs.getString("EVNT_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, seq_no );
			pstmt.setString(++idx, data.getString("EVNT_NM") ); 			
			pstmt.setString(++idx, data.getString("EVNT_FROM") ); 
			pstmt.setString(++idx, data.getString("EVNT_TO") ); 
			pstmt.setString(++idx, data.getString("TITL") );
			pstmt.setString(++idx, data.getString("DISP_YN") );
			pstmt.setString(++idx, data.getString("IMG_NM") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setString(++idx, data.getString("BLTN_STRT_DATE") );
			pstmt.setString(++idx, data.getString("BLTN_END_DATE") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			sql = this.getSelectForUpdateQuery();//레슨번호 쿼리 
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, seq_no);
            rs = pstmt.executeQuery();

			/*
            if(rs.next()) {
				java.sql.Clob clob = rs.getClob("CTNT");
                writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
				reader = new CharArrayReader(data.getString("CTNT").toCharArray());
				
				char[] buffer = new char[1024];
				int read = 0;
				while ((read = reader.read(buffer,0,1024)) != -1) {
					writer.write(buffer,0,read);
				}
				writer.flush();
				writer.close();
			}
			*/			
			
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGEVNTMGMT (	\n");
		sql.append("\t  EVNT_SEQ_NO, EVNT_CLSS, EVNT_NM, EVNT_STRT_DATE, EVNT_END_DATE, TITL, CTNT, BLTN_YN, RCRU_PE_ORG_NUM, LESN_STRT_DATE, 	\n");
		sql.append("\t  LESN_END_DATE, LESN_NORM_COST, LESN_DC_COST, EVNT_BNFT_EXPL, AFFI_FIRM_EXPL, INQR_NUM, IMG_FILE_PATH, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, 	\n");
		sql.append("\t  CHNG_ATON, BLTN_STRT_DATE, BLTN_END_DATE 	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,'0001',?,?,?,?,EMPTY_CLOB(),?,NULL,NULL,	\n");
		sql.append("\t  NULL,NULL,NULL,NULL,NULL,0,?,?,NULL,TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),	\n");
		sql.append("\t 	NULL, ?, ? \n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(EVNT_SEQ_NO),0)+1 EVNT_SEQ_NO FROM BCDBA.TBGEVNTMGMT \n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * CLOB Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT CTNT FROM BCDBA.TBGEVNTMGMT \n");
         sql.append("WHERE EVNT_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
}
