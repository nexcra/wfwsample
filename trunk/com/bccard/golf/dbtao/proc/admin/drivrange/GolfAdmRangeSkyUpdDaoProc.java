/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeSkyUpdDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ��û(sky72) ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmRangeSkyUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ �帲 ���������� ����(sky72) ó��";

	/** *****************************************************************
	 * GolfAdmRangeSkyUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeSkyUpdDaoProc() {}
	
	/**
	 * ������ �帲 ���������� ���� ó��
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
		String sql = "";
				
		try {
			
			String appr_opion = data.getString("APPR_OPION");
            String add_appr_opion = data.getString("ADD_APPR_OPION");
            String cdhd_id = data.getString("CDHD_ID");
            
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("ATD_YN") ); 
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
           
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
			conn.setAutoCommit(true);
			
			//ȸ���� ���� : / �߰�
            if(!add_appr_opion.equals("")){
            	sql = this.getUpdCdhdQuery();
            	pstmt = conn.prepareStatement(sql);
            	
            	//������ �ƴϸ� /�߰�
            	if(!appr_opion.equals("")) add_appr_opion = "/"+ add_appr_opion;
            	
            	pstmt.setString(1, appr_opion + add_appr_opion);
            	pstmt.setString(2, cdhd_id);
            	result = pstmt.executeUpdate();
            	
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
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGRSVTMGMT SET	\n");
		sql.append("\t  ATTD_YN=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t WHERE GOLF_SVC_RSVT_NO=?	\n");
        return sql.toString();
    }
 	/** ***********************************************************************
     * �ش�ȸ�������� �򰡼�������
     ************************************************************************ */
     private String getUpdCdhdQuery(){
       StringBuffer sql = new StringBuffer();
 		sql.append("\n  UPDATE BCDBA.TBGGOLFCDHD   SET						");
 		sql.append("\n   APPR_OPION =  ?									");
 		sql.append("\n  WHERE CDHD_ID = ?									");
 		
     return sql.toString();
     }
}
