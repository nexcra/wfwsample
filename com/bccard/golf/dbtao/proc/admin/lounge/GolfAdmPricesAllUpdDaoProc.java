/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmPricesAllUpdDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �ü� ��ü���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lounge;

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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmPricesAllUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ �ü� ��ü���� ó��";

	/** *****************************************************************
	 * GolfAdmPricesAllUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmPricesAllUpdDaoProc() {}
	
	/**
	 * ������ �ü� ��ü���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] green_memrtk_nm_seq_no, String[] qut) throws DbTaoException  {
		
		int iCount = 0;
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
			/*****************************************************************************/
			
			for (int i = 0; i < green_memrtk_nm_seq_no.length; i++) {				
				if (green_memrtk_nm_seq_no[i] != null && green_memrtk_nm_seq_no[i].length() > 0) {
					
					//	���/���� ���� üũ
					int pricesChk = this.getPricesChk(conn, data, green_memrtk_nm_seq_no[i]);
					
					if (pricesChk == 0) { //���
						
						sql = this.getInsertQuery();//Insert Query
						pstmt = conn.prepareStatement(sql);
						
						int idx = 0;
						pstmt.setLong(++idx, Integer.parseInt(green_memrtk_nm_seq_no[i]) );
						pstmt.setString(++idx, data.getString("QUT_DATE") ); 			
						pstmt.setLong(++idx, Integer.parseInt(qut[i]) ); 
						pstmt.setString(++idx, data.getString("ADMIN_NO") );		
						
						iCount += pstmt.executeUpdate();
			            if(pstmt != null) pstmt.close();
			            
					} else { //����
						
						sql = this.getUpdateQuery();//Update Query
						pstmt = conn.prepareStatement(sql);
						
						int idx = 0;
						pstmt.setLong(++idx, Integer.parseInt(qut[i]) ); 
						pstmt.setString(++idx, data.getString("ADMIN_NO") );	
						pstmt.setLong(++idx, Integer.parseInt(green_memrtk_nm_seq_no[i]) );
						pstmt.setString(++idx, data.getString("QUT_DATE") ); 		
						
						iCount += pstmt.executeUpdate();
			            if(pstmt != null) pstmt.close();
			            
					}
				}
			}
			
			if(iCount == green_memrtk_nm_seq_no.length) {
				 result = 1;
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	/**
	 * ���/���� ���� üũ
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getPricesChk(Connection conn, TaoDataSet data, String green_memrtk_nm_seq_no) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getSelectQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;	
			pstmt.setLong(++idx, Integer.parseInt(green_memrtk_nm_seq_no) );
			pstmt.setString(++idx, data.getString("QUT_DATE") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
			if(rs != null) rs.close();
			if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
	}	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGGREENMEMRTKQUTMGMT (	\n");
		sql.append("\t  GREEN_MEMRTK_NM_SEQ_NO, QUT_DATE, QUT, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON  	\n");
		sql.append("\t ) VALUES (	\n");
		sql.append("\t  ?,?,?,?,0,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),NULL	\n");
		sql.append("\t \n)");	
	
	    return sql.toString();
    }
	
	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGGREENMEMRTKQUTMGMT SET	\n");
		sql.append("\t  	QUT=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')  	\n");
		sql.append("\t WHERE GREEN_MEMRTK_NM_SEQ_NO = ?	\n");
 		sql.append("\t AND QUT_DATE = ?	\n");
	
	    return sql.toString();
    }

	 /** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.    
	************************************************************************ */
 	private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("SELECT	\n");
 		sql.append("\t 	QUT	\n");
 		sql.append("\t FROM BCDBA.TBGGREENMEMRTKQUTMGMT	\n");
 		sql.append("\t WHERE GREEN_MEMRTK_NM_SEQ_NO = ?	\n");
 		sql.append("\t AND QUT_DATE = ?	\n");
 	    return sql.toString();
     }
 	
}
