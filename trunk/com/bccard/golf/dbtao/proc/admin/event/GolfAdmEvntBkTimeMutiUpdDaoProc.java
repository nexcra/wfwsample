/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBkMutiUpdDaoProc
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
import java.sql.ResultSet;

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
public class GolfAdmEvntBkTimeMutiUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ �����̾� ��ŷ �̺�Ʈ ���� ���� ó��";

	/** *****************************************************************
	 * GolfAdmEvntBkMutiDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBkTimeMutiUpdDaoProc() {}
	
	/**
	 * ������ �������α׷� ���� ���� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] time_seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int iCount = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
            /*****************************************************************************/
			
			sql =  this.getNextValQuery(); //�̺�Ʈ ����
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			long seq_no = 0L;
			if(rs.next()){
				seq_no = rs.getLong("EVNT_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
		
			sql = this.getInsertQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < time_seq_no.length; i++) {				
				if (time_seq_no[i] != null && time_seq_no[i].length() > 0) {
					pstmt.setLong(1, seq_no );
					pstmt.setString(2, time_seq_no[i]); 
					pstmt.setString(3, data.getString("ADMIN_NO") );
					pstmt.setString(4, time_seq_no[i]); 

					iCount += pstmt.executeUpdate();
					++seq_no;
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
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGEVNTMGMT (	\n");
		sql.append("\t  EVNT_SEQ_NO, EVNT_CLSS, BLTN_YN, RSVT_ABLE_BOKG_TIME_SEQ_NO, GREEN_NM, BOKG_ABLE_DATE, BOKG_ABLE_TIME, GOLF_RSVT_CURS_NM,	\n");
		sql.append("\t  INQR_NUM, REG_MGR_ID, REG_ATON 	\n");
		sql.append("\t ) (	\n");	
		sql.append("\t 	SELECT 	\n");	
		sql.append("\t  	?, '0002', 'N', ?, TBG.GREEN_NM, TBD.BOKG_ABLE_DATE, TBT.BOKG_ABLE_TIME, TBD.GOLF_RSVT_CURS_NM,	\n");
		sql.append("\t  	0, ?, TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS')	\n");
		sql.append("\t 	FROM  \n");
		sql.append("\t 	BCDBA.TBGRSVTABLEBOKGTIMEMGMT TBT, BCDBA.TBGRSVTABLESCDMGMT TBD, BCDBA.TBGAFFIGREEN TBG  \n");
		sql.append("\t 	WHERE TBT.RSVT_ABLE_SCD_SEQ_NO = TBD.RSVT_ABLE_SCD_SEQ_NO  \n");
		sql.append("\t 	AND TBD.AFFI_GREEN_SEQ_NO = TBG.AFFI_GREEN_SEQ_NO(+)  \n");
		sql.append("\t 	AND TBT.RSVT_ABLE_BOKG_TIME_SEQ_NO = ?  \n");
		sql.append("\t \n)");	
        return sql.toString();
    }    
    
    /** ***********************************************************************
     * Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getUpdateQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
 		sql.append("\t  EVNT_YN='Y' 	\n");
 		sql.append("\t WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?	\n");
         return sql.toString();
     }
        
    /** ***********************************************************************
     * Max IDX Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getNextValQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT NVL(MAX(EVNT_SEQ_NO),0)+1 EVNT_SEQ_NO FROM BCDBA.TBGEVNTMGMT \n");
 		return sql.toString();
     }
}
