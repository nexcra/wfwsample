/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmManiaChgDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.club;

import java.io.Reader; 
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmClubUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ �����帮�������ν�û���� ����ó��";

	/** *****************************************************************
	 * GolfAdmManiaChgDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmClubUpdDaoProc() {}
	
	/**
	 * ������ ���������ν�û ���α׷� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			// Ű�������� ��������/���/ �Ϲݼ��� ���� ---------------------------------------------
			String prize_yn		= data.getString("PRIZE_YN");
		
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			   int idx = 0;
			   
			   
			  pstmt.setString(++idx, data.getString("PRIZE_YN") 	);	//���࿩�� (����/���) ������

			   
			   pstmt.setString(++idx, data.getString("ADMIN_NO") );
			   pstmt.setLong(++idx, data.getLong("RECV_NO") );
			   
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			
			if(result > 0) {
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

		return result;
	}
	
	//9���߰���
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGCLUBMGMT SET	\n");
		
		sql.append("\t  CLUB_OPN_AUTH_YN=?,  \n"); 		

		sql.append("\t  CHNG_MGR_ID=?, 	\n");
		sql.append("\t  CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD') 	\n");
		sql.append("\t WHERE CLUB_SEQ_NO=?	\n");
        return sql.toString();
    }
    
}
