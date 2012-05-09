/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardMutiDelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ��ȣȸ �Խ��� ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.club;

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
public class GolfBoardMutiDelDaoProc extends AbstractProc {

	public static final String TITLE = "��ȣȸ �Խ��� ���� ���� ó��";

	/** *****************************************************************
	 * GolfBoardMutiDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBoardMutiDelDaoProc() {}
	
	/**
	 * ��ȣȸ �Խ��� ���� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		int iCount2 = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//��ȸ ----------------------------------------------------------
		
			String sql = this.getDeleteQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < seq_no.length; i++) {				
				if (seq_no[i] != null && seq_no[i].length() > 0) {
					pstmt.setString(1, data.getString("SCH_CLUB_SEQ_NO") ); 
					pstmt.setString(2, data.getString("SCH_BBRD_SEQ_NO") ); 
					pstmt.setString(3, seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}
		    /** ***********************************************************************/
			sql = this.getDeleteQuery2();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < seq_no.length; i++) {				
				if (seq_no[i] != null && seq_no[i].length() > 0) {
					pstmt.setString(1, seq_no[i]); 

					iCount2 += pstmt.executeUpdate();
				}
			}
		    /** ***********************************************************************/

			if(iCount == seq_no.length) {
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

		return iCount;
	}
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGCLUBBBRD 	\n");
		sql.append("\t  WHERE CLUB_CDHD_SEQ_NO = ?	\n");
		sql.append("\t  AND BBRD_UNIQ_SEQ_NO = ?	\n");
		sql.append("\t  AND SEQ_NO = ?	\n");
        return sql.toString();
    }
    
    private String getDeleteQuery2(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGCLUBBBRDREPY 	\n");
		sql.append("\t  WHERE BBRD_SEQ_NO = ?	\n");
        return sql.toString();
    }
}