/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmGoodFoodMutiDelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ���� ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-29
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lounge;

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
public class GolfAdmGoodFoodMutiDelDaoProc extends AbstractProc {

	public static final String TITLE = "������ ���� ���� ���� ó��";

	/** *****************************************************************
	 * GolfAdmGoodFoodMutiDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmGoodFoodMutiDelDaoProc() {}
	
	/**
	 * ������ ���� ���� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount1 = 0;
		int iCount2 = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//��ȸ ----------------------------------------------------------
		
			String sql = this.getDeleteQuery1();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < seq_no.length; i++) {				
				if (seq_no[i] != null && seq_no[i].length() > 0) {
					pstmt.setString(1, seq_no[i]); 

					iCount1 += pstmt.executeUpdate();
				}
			}
			if(pstmt != null) pstmt.close();
			
			sql = this.getDeleteQuery2();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < seq_no.length; i++) {				
				if (seq_no[i] != null && seq_no[i].length() > 0) {
					pstmt.setString(1, seq_no[i]); 

					iCount2 += pstmt.executeUpdate();
				}
			}
			if(pstmt != null) pstmt.close();
			
			
			//getDeleteQuery2 ������ ���� ��� ���� ����� 0�� �ǹǷ� üũ���� ����.
			if(iCount2 > 0) {
				if(iCount1 == seq_no.length && iCount2 >= seq_no.length) {
					conn.commit();
				} else {
					conn.rollback();
				}
			} else {
				if(iCount1 == seq_no.length) {
					conn.commit();
				} else {
					conn.rollback();
				}
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

		return iCount1;
	}
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	private String getDeleteQuery1(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGAFFIETHS 	\n");
		sql.append("\t  WHERE AFFI_ETHS_SEQ_NO = ?	\n");	
        return sql.toString();
    }
    
    private String getDeleteQuery2(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGETHSNGHBGREEN 	\n");
		sql.append("\t  WHERE AFFI_ETHS_SEQ_NO = ?	\n");
        return sql.toString();
    }       
}
