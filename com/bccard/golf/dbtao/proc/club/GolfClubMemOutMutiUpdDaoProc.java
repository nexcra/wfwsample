/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfClubMemOutMutiUpdDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ��ȣȸ ȸ�� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-06
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
public class GolfClubMemOutMutiUpdDaoProc extends AbstractProc {

	public static final String TITLE = "��ȣȸ ȸ�� ���� ó��";

	/** *****************************************************************
	 * GolfClubMemOutMutiUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMemOutMutiUpdDaoProc() {}
	
	/**
	 * ��ȣȸ ȸ�� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] club_cdhd_seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//��ȸ ----------------------------------------------------------
		
			String sql = this.getDeleteQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < club_cdhd_seq_no.length; i++) {				
				if (club_cdhd_seq_no[i] != null && club_cdhd_seq_no[i].length() > 0) {
					pstmt.setString(1, data.getString("CLUB_SEQ_NO") ); 
					pstmt.setString(2, club_cdhd_seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}

			if(iCount == club_cdhd_seq_no.length) {
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
		sql.append("UPDATE BCDBA.TBGCLUBCDHDMGMT SET	\n");
		sql.append("\t  SECE_YN='E',  SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS')  	\n");
		sql.append("\t  WHERE CLUB_SEQ_NO = ?	\n");
		sql.append("\t  AND CLUB_CDHD_SEQ_NO = ?	\n");
        return sql.toString();
    }
}