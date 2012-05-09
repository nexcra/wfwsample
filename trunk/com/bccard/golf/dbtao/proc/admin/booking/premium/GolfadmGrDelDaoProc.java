/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admGrDelDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �������α׷� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

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
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfadmGrDelDaoProc extends AbstractProc {

	public static final String TITLE = "������ ��ŷ������ ���� ó��";

	/** *****************************************************************
	 * GolfAdmLessonDelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmGrDelDaoProc() {}
	
	/**
	 * ������ �������α׷� ���� ó��
	 * @param conn
	 * @param data 
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context,
	                   HttpServletRequest request,
	                   TaoDataSet data) throws DbTaoException  {

		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//��ȸ ----------------------------------------------------------
		
			String sql1 = this.getDeleteQuery1();
			pstmt = conn.prepareStatement(sql1);
			pstmt.setLong(1, data.getLong("SEQ_NO") ); 			
			int res1 = pstmt.executeUpdate();			

			String sql2 = this.getDeleteQuery2();
			pstmt = conn.prepareStatement(sql2);
			pstmt.setLong(1, data.getLong("SEQ_NO") ); 			
			int res2 = pstmt.executeUpdate();			

			String sql3 = this.getDeleteQuery3();
			pstmt = conn.prepareStatement(sql3);
			pstmt.setLong(1, data.getLong("SEQ_NO") ); 			
			int res3 = pstmt.executeUpdate();

			String sql = this.getDeleteQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, data.getLong("SEQ_NO") ); 			
			int res = pstmt.executeUpdate();			
		    /** ***********************************************************************/
			
			if(res == 1) {
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
    * Query�� �����Ͽ� �����Ѵ�.    - ������
    ************************************************************************ */
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGAFFIGREEN 	\n");
		sql.append("\t  WHERE AFFI_GREEN_SEQ_NO = ?	\n");
        return sql.toString();
    }
     /** ***********************************************************************
      * Query�� �����Ͽ� �����Ѵ�.    - �������
      ************************************************************************ */
      private String getDeleteQuery1(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("DELETE  	\n");
  		sql.append("\t  FROM BCDBA.TBGRSVTMGMT	\n");
  		sql.append("\t  WHERE AFFI_GREEN_SEQ_NO = ?	\n");
          return sql.toString();
      }
      /** ***********************************************************************
       * Query�� �����Ͽ� �����Ѵ�.    - ��������(��¥)
       ************************************************************************ */
       private String getDeleteQuery2(){
   		StringBuffer sql = new StringBuffer();
   		sql.append("DELETE 	\n");
   		sql.append("\t  FROM BCDBA.TBGRSVTABLESCDMGMT T1	\n");
   		sql.append("\t  WHERE RSVT_ABLE_SCD_SEQ_NO IN (SELECT RSVT_ABLE_SCD_SEQ_NO FROM BCDBA.TBGRSVTABLESCDMGMT WHERE AFFI_GREEN_SEQ_NO = ?)	\n");
           return sql.toString();
       }
       /** ***********************************************************************
        * Query�� �����Ͽ� �����Ѵ�.    - ��������(�ð�)
        ************************************************************************ */
        private String getDeleteQuery3(){
    		StringBuffer sql = new StringBuffer();
    		sql.append("DELETE 	\n");
    		sql.append("\t  FROM BCDBA.TBGRSVTABLESCDMGMT T1	\n");
    		sql.append("\t  WHERE AFFI_GREEN_SEQ_NO = ? 		\n");
            return sql.toString();
        }
}
