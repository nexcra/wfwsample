/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLessonMutiDelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ �������α׷� ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lesson;

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
public class GolfAdmLessonMutiDelDaoProc extends AbstractProc {

	public static final String TITLE = "������ �������α׷� ���� ���� ó��";

	/** *****************************************************************
	 * GolfAdmLessonMutiDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLessonMutiDelDaoProc() {}
	
	/**
	 * ������ �������α׷� ���� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] lsn_seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		int iCount2 = 0;
		int iCount3 = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//��ȸ ----------------------------------------------------------
		
			String sql = this.getDeleteQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < lsn_seq_no.length; i++) {				
				if (lsn_seq_no[i] != null && lsn_seq_no[i].length() > 0) {
					pstmt.setString(1, lsn_seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}			
		    /** ***********************************************************************/	

//			sql = this.getDeleteQuery2();			
//			pstmt = conn.prepareStatement(sql);
//			for (int i = 0; i < lsn_seq_no.length; i++) {				
//				if (lsn_seq_no[i] != null && lsn_seq_no[i].length() > 0) {
//					pstmt.setString(1, lsn_seq_no[i]); 
//
//					iCount2 += pstmt.executeUpdate();
//				}
//			}	
		    /** ***********************************************************************/	

			//getDeleteQuery2 ������ ���� ��� ���� ����� 0�� �ǹǷ� üũ���� ����.
			if(iCount == lsn_seq_no.length) {
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

		return iCount;
	}
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGLESNMGMT 	\n");
		sql.append("\t  WHERE LESN_SEQ_NO = ?	\n");
        return sql.toString();
    }
    /*
    private String getDeleteQuery2(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGAPLCMGMT 	\n");
		sql.append("\t  WHERE LESN_SEQ_NO = ?	\n");
		sql.append("\t  AND GOLF_SVC_APLC_CLSS = '0001' \n");
        return sql.toString();
    }
    */
}
