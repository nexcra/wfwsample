/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPreTimeDelDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ�����̾� ƼŸ�� ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfadmPreTimeDelDaoProc extends AbstractProc {

	public static final String TITLE = "������ �����̾���ŷ ƼŸ�� ���� ���� ó��";

	/** *****************************************************************
	 * GolfAdmLessonMutiDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreTimeDelDaoProc() {}
	
	/**
	 * ������ �����̾���ŷ ƼŸ�� ���� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] lsn_seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
				
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
		sql.append("DELETE BCDBA.TBGRSVTABLEBOKGTIMEMGMT 	\n");
		sql.append("\t  WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	\n");
        return sql.toString();
    }
}
