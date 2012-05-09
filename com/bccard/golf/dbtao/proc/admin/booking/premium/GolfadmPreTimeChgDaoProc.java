/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmPreTimeChgDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ�����̾� ƼŸ�� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-21
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfadmPreTimeChgDaoProc extends AbstractProc {

	public static final String TITLE = "������ ��ŷ�����̾� ƼŸ�� ���� ó��";

	/** *****************************************************************
	 * admPreTimeChgDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreTimeChgDaoProc() {}
	
	/**
	 * ������ ��ŷ�����̾� ƼŸ�� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException 
	 */
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request, String[] lsn_seq_no, String VIEW_YN) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		GolfAdminEtt userEtt = null;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			
			//��ȸ ----------------------------------------------------------		
			String sql = this.getChgQuery();	
			
			for (int i = 0; i < lsn_seq_no.length; i++) {				
				if (lsn_seq_no[i] != null && lsn_seq_no[i].length() > 0) {		
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, VIEW_YN);
					pstmt.setString(2, admId);
					pstmt.setString(3, lsn_seq_no[i]); 

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
    private String getChgQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT 	\n");
		sql.append("	SET EPS_YN = ?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t  WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	\n");
        return sql.toString();
    }
}
