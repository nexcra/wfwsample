/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeDialyDelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmRangeDialyDelDaoProc extends AbstractProc {

	public static final String TITLE = "������ �帲 ���������� ���� ���� ó��";

	/** *****************************************************************
	 * GolfAdmRangeDialyDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeDialyDelDaoProc() {}
	
	/**
	 * ������ �帲 ���������� ���� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {

		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//��ȸ ----------------------------------------------------------
		
			String sql = this.getDeleteQuery1();
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, data.getLong("RSVTDIALY_SQL_NO") ); 			
			int res1 = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			
			sql = this.getDeleteQuery2();
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, data.getLong("RSVTDIALY_SQL_NO") ); 			
			int res2 = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			
			//getDeleteQuery2 ������ ���� ��� ���� ����� 0�� �ǹǷ� üũ���� ����.
			//if(res1 == 1 && res2 == 1) {
			if(res1 == 1 && res2 > -1) {
				result = 1;
				conn.commit();
			} else {
				result = 0;
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
    private String getDeleteQuery1(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGRSVTABLESCDMGMT 	\n");
		sql.append("\t  WHERE RSVT_ABLE_SCD_SEQ_NO = ?	\n");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getDeleteQuery2(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("DELETE BCDBA.TBGRSVTABLEBOKGTIMEMGMT 	\n");
 		sql.append("\t  WHERE RSVT_ABLE_SCD_SEQ_NO = ?	\n");
         return sql.toString();
     }
}
