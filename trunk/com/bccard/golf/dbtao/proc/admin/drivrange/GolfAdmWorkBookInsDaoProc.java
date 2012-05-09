/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmWorkBookInsDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 연습장 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

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
public class GolfAdmWorkBookInsDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 연습장 등록 처리";

	/** *****************************************************************
	 * GolfAdmWorkBookInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmWorkBookInsDaoProc() {}
	
	/**
	 * 관리자 연습장 등록 처리
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

			sql = this.getNextValQuery(); //번호 쿼리
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			long seq_no = 0L;
			if(rs.next()){
				seq_no = rs.getLong("GF_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setLong(++idx, seq_no );
			pstmt.setString(++idx, data.getString("EXEC_TYPE_CD") ); 
			pstmt.setString(++idx, data.getString("GF_NM") ); 
			pstmt.setString(++idx, data.getString("ZIPCODE") );
			pstmt.setString(++idx, data.getString("ZIPADDR") );
			pstmt.setString(++idx, data.getString("DETAILADDR") );
			pstmt.setString(++idx, data.getString("CHG_DDD_NO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_HNO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_SNO") );
			pstmt.setString(++idx, data.getString("IMG_NM") ); 
			pstmt.setString(++idx, data.getString("URL") ); 
			pstmt.setString(++idx, data.getString("GF_SEARCH") ); 
			pstmt.setString(++idx, data.getString("MTTR") ); 
			pstmt.setLong(++idx, data.getLong("CUPN_SEQ_NO") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setString(++idx, data.getString("ADDR_CLSS") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
/*
			sql = this.getSelectForUpdateQuery();//일련번호 쿼리
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, seq_no);
            rs = pstmt.executeQuery();

			if(rs.next()) {
				java.sql.Clob clob = rs.getClob("MTTR");
                //writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
				reader = new CharArrayReader(data.getString("MTTR").toCharArray());
				
				char[] buffer = new char[1024];
				int read = 0;
				while ((read = reader.read(buffer,0,1024)) != -1) {
					writer.write(buffer,0,read);
				}
				writer.flush();
			}
			if (rs  != null) rs.close();
			if (pstmt != null) pstmt.close();
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
		sql.append("INSERT INTO BCDBA.TBGAFFIGREEN (	\n");
		sql.append("\t  AFFI_GREEN_SEQ_NO, AFFI_FIRM_CLSS, GOLF_RNG_CLSS, GREEN_NM, ZP, ADDR, DTL_ADDR, DDD_NO, TEL_HNO, TEL_SNO,   	\n");
		sql.append("\t  ANNX_IMG, GREEN_HPGE_URL, POS_EXPL, CAUT_MTTR_CTNT, CUPN_SEQ_NO, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON, NW_OLD_ADDR_CLSS	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,'0003',?,?,?,?,?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,?,?,0,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),NULL, ?	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(AFFI_GREEN_SEQ_NO),0)+1 GF_SEQ_NO FROM BCDBA.TBGAFFIGREEN \n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * CLOB Query를 생성하여 리턴한다.    
     ************************************************************************ */
    /*
    private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT CAUT_MTTR_CTNT FROM BCDBA.TBGAFFIGREEN \n");
         sql.append("WHERE AFFI_GREEN_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
     */
}
