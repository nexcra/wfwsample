/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardDelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ����Խ��� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.bbs;

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
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfBoardDelDaoProc extends AbstractProc {

	public static final String TITLE = "����Խ��� ���� ó��";

	/** *****************************************************************
	 * GolfBoardDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBoardDelDaoProc() {}
	
	/**
	 * ����Խ��� ���� ó��
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
		
			String sql = this.getDeleteQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("SEQ_NO") );
			pstmt.setString(2, data.getString("BBS") ); 
			int res1 = pstmt.executeUpdate();
		    /** ***********************************************************************/

			sql = this.getDeleteQuery2();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("SEQ_NO") ); 			
			res1 = res1 + pstmt.executeUpdate();
		    /** ***********************************************************************/
			
			if(res1 >= 1) {
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
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGBBRD 	\n");
		sql.append("\t  WHERE REPY_URNK_SEQ_NO = ?	\n");
		sql.append("\t  AND BBRD_CLSS = ?	\n");
        return sql.toString();
    }
    
    private String getDeleteQuery2(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGBBRDREPY 	\n");
		sql.append("\t  WHERE BBRD_SEQ_NO = ?	\n");
        return sql.toString();
    }
}
