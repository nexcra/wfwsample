/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBkMutiDelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ �����̾� ��ŷ �̺�Ʈ ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-26
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
public class GolfAdmEvntBkMutiDelDaoProc extends AbstractProc {

	public static final String TITLE = "������ �����̾� ��ŷ �̺�Ʈ ���� ���� ó��";

	/** *****************************************************************
	 * GolfAdmEvntBkMutiDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBkMutiDelDaoProc() {}
	
	/**
	 * ������ �����̾� ��ŷ �̺�Ʈ ���� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] time_seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
            /*****************************************************************************/
		
			String sql = this.getDeleteQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < time_seq_no.length; i++) {				
				if (time_seq_no[i] != null && time_seq_no[i].length() > 0) {
					pstmt.setString(1, time_seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}
            /*****************************************************************************/

			sql = this.getUpdateQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < time_seq_no.length; i++) {				
				if (time_seq_no[i] != null && time_seq_no[i].length() > 0) {
					pstmt.setString(1, time_seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}
            /*****************************************************************************/
			
			if(iCount == (time_seq_no.length*2)) {
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
		sql.append("DELETE BCDBA.TBGEVNTMGMT 	\n");
		sql.append("\t  WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	\n");
		sql.append("\t  AND EVNT_CLSS = '0002'	\n");
        return sql.toString();
    }
    /** ***********************************************************************
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getUpdateQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
 		sql.append("\t  EVNT_YN=NULL 	\n");
 		sql.append("\t WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?	\n");
         return sql.toString();
     }
}
