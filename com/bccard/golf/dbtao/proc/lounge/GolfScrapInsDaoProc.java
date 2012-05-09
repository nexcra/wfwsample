/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfScrapInsDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ��ũ�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.lounge;

import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfScrapInsDaoProc extends AbstractProc {

	public static final String TITLE = "������ ��ũ�� ó��";

	/** *****************************************************************
	 * GolfScrapInsDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfScrapInsDaoProc() {}
	
	/**
	 * ������ ��ũ�� ó��
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
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);	
			
			// �ߺ� üũ
			int scrapChk = this.getScrapChk(conn, data);

			if (scrapChk == 0) {
				sql = this.getNextValQuery(); //��ȣ ����
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				long seq_no = 0L;
				if(rs.next()){
					seq_no = rs.getLong("SCRAP_SEQ_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            /*****************************************************************************/
	            
				sql = this.getInsertQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
				
				int idx = 0;
				pstmt.setLong(++idx, seq_no );
				pstmt.setString(++idx, data.getString("SCRAP_CLSS") );
				pstmt.setLong(++idx, data.getLong("GF_SEQ_NO") );
				pstmt.setString(++idx, data.getString("ADMIN_NO") );
				
				result = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	            
				if(result > 0) {
					conn.commit();
				} else {
					conn.rollback();
				}
			} else {
				result = 9;				
			}
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}
	
	/**
	 * �ߺ� üũ
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getScrapChk(Connection conn, TaoDataSet data) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getSelectQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;	
			pstmt.setString(++idx, data.getString("SCRAP_CLSS") );
			pstmt.setLong(++idx, data.getLong("GF_SEQ_NO") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
		} catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
		}
		
		return result;
	}	
	
	
	 /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGSCRAP (	\n");
		sql.append("\t  SCRAP_SEQ_NO, GOLF_SCRAP_CLSS, BOD_SEQ_NO, CDHD_ID, REG_ATON 	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }
    
    /** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.    
	************************************************************************ */
 	private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("SELECT	\n");
 		sql.append("\t 	SCRAP_SEQ_NO	\n");
 		sql.append("\t FROM BCDBA.TBGSCRAP	\n");
 		sql.append("\t WHERE GOLF_SCRAP_CLSS = ?	\n");
 		sql.append("\t AND BOD_SEQ_NO = ?	\n");
 		sql.append("\t AND CDHD_ID = ?	\n");
         return sql.toString();
     }

    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(SCRAP_SEQ_NO),0)+1 SCRAP_SEQ_NO FROM BCDBA.TBGSCRAP \n");
		return sql.toString();
    }
}
