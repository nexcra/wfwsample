/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.sky;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0   
******************************************************************************/
public class GolfadmSkyRsUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ ��ŷ ������ ���� ó��";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc ���μ��� ������ 
	 * @param N/A
	 ***************************************************************** */
	public GolfadmSkyRsUpdDaoProc() {}
	
	/**
	 * ������ �������α׷� ��� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
			String rsvt_YN = data.getString("RSVT_YN");
			String cdhd_id = data.getString("CDHD_ID");
			String appr_opion = data.getString("APPR_OPION");
			String add_appr_opion = data.getString("ADD_APPR_OPION");
            
			sql = this.getInsertQuery(rsvt_YN);//Insert Query
			pstmt = conn.prepareStatement(sql);

            
			int idx = 0;
			pstmt.setString(++idx, data.getString("RSVT_YN") );
			pstmt.setString(++idx, data.getString("CTNT") );
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO") );
			
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            

			if(result>0) {
				conn.commit();
			} else {
				conn.rollback();
			}	
			
			
			conn.setAutoCommit(true);
			//ȸ������ ���� ����
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
    private String getInsertQuery(String rsvt_YN){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGRSVTMGMT SET	\n");
		sql.append("\t  RSVT_YN=?, CTNT=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");

        if(rsvt_YN.equals("N") || rsvt_YN.equals("I")){
        	sql.append("\t  , CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
        }else{
        	sql.append("\t  , CNCL_ATON=''	\n");
        }
		
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
