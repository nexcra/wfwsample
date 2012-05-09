/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMtFieldMutiDelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ����Ƽ�ڽ�(����������) ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.golf;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
public class GolfMtbTlkDelDaoProc extends AbstractProc {

	public static final String TITLE = "����Ƽ�ڽ�(����������) ���� ���� ó��";

	/** *****************************************************************
	 * GolfMtFieldMutiDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfMtbTlkDelDaoProc() {}
	
	/**
	 * ����Ƽ�ڽ�(����������) ���� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount1 = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//��ȸ ----------------------------------------------------------
		
			String sql = this.getDeleteQuery1();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < seq_no.length; i++) {				
				if (seq_no[i] != null && seq_no[i].length() > 0) {
					pstmt.setString(1, seq_no[i]); 
					pstmt.setString(2, data.getString("ADMIN_NO"));

					iCount1 += pstmt.executeUpdate();
				}
			}
			if(pstmt != null) pstmt.close();
			
			//getDeleteQuery2 ������ ���� ��� ���� ����� 0�� �ǹǷ� üũ���� ����.
			if(iCount1 == seq_no.length) {
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
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return iCount1;
	}
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getDeleteQuery1(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGSCRAP 	\n");
		sql.append("\t  WHERE BOD_SEQ_NO = ?	");	
		sql.append("\t 	 AND CDHD_ID = ?	");	
		sql.append("\t 	 AND GOLF_SCRAP_CLSS = '0002'	");	
        return sql.toString();
    }
    
}
