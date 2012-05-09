/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfEvntGolfShowInsDaoProc
*   �ۼ���	: (��)�̵������ ������
*   ����		: �����ڶ�ȸ ���� ��� ���� ����
*   �������	: golf
*   �ۼ�����	: 2010-05-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0
 ******************************************************************************/
public class GolfEvntGolfShowInsDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBcWinInqDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntGolfShowInsDaoProc() {}	

	/**
	 * ���
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int insExecute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            
			sql = this.getInsertQuery();//Update Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("userId"));
			
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
		sql.append("\n INSERT INTO BCDBA.TBGAPLCMGMT (APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, CDHD_ID, REG_ATON) VALUES	");
		sql.append("\n ((SELECT MAX(APLC_SEQ_NO)+1 FROM BCDBA.TBGAPLCMGMT), '0010', ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'))	");
		return sql.toString();
    }

}
