/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemberShipInsDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ȸ���� ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-06	
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lounge;

import java.io.CharArrayReader;
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
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmMemberShipInsDaoProc extends AbstractProc {

	public static final String TITLE = "������ ȸ���� ��� ó��";

	/** *****************************************************************
	 * GolfAdmMemberShipInsDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemberShipInsDaoProc() {}
	
	/**
	 * ������ ȸ���� ��� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		int result = 0;
		int iCount = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

			sql = this.getNextValQuery(); //��ȣ ����
            pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();			
			long seq_no = 0L;
			if(rs.next()){
				seq_no = rs.getLong("GREEN_MEMRTK_NM_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setLong(++idx, seq_no );
			pstmt.setString(++idx, data.getString("GREEN_MEMRTK_NM") ); 			
			pstmt.setLong(++idx, data.getLong("STD_MKPR") ); 
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
         
		
			if(result > 0) {
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
	
	
	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGGREENMEMRTKMGMT (	\n");
		sql.append("\t  GREEN_MEMRTK_NM_SEQ_NO, GREEN_MEMRTK_NM, STD_MKPR, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON  	\n");
		sql.append("\t ) VALUES (	\n");
		sql.append("\t  ?,?,?,?,0,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),NULL	\n");
		sql.append("\t \n)");	

	    return sql.toString();
    }
    
    
    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(GREEN_MEMRTK_NM_SEQ_NO),0)+1 GREEN_MEMRTK_NM_SEQ_NO FROM BCDBA.TBGGREENMEMRTKMGMT \n");
		return sql.toString();
    }
    
}
