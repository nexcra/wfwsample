/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkCheckUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ > ��ŷ ���� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
* 20091117			  ������   ������� �ϰ�� CNCL_ATON �÷��� �߰�
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.check;

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

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfBkCheckUpdDaoProc extends AbstractProc {

	public static final String TITLE = "��ŷ ���� ���� ó��";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBkCheckUpdDaoProc() {}
	
	/**
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
			String type = data.getString("type");
			String idx = data.getString("idx");
            
			// ������¸� ��ҷ� ������.
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
            
			pstmt.setString(1, idx );			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            
            // �ٽ� ��ŷ �� �� �ִ� ���·� �����ش�. ������� : N / �ӹ���� : I
            if(type.equals("M") || type.equals("S")){
				sql = this.getUpdateQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, idx );
				int result2 = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
            }
            /////////////////////////////////////////////

			if(result>0) {
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
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.     
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGRSVTMGMT SET	\n");
		sql.append("\t  RSVT_YN='N', CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  , CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')				\n");
		sql.append("\t WHERE GOLF_SVC_RSVT_NO=?	\n");
        return sql.toString();
    }
     
 	/** ***********************************************************************
      * CLOB Query�� �����Ͽ� �����Ѵ�.    - �ٽ� ��ŷ�� �� �ִ� ���·� ������.
      ************************************************************************ */
      private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("\n");
  		sql.append("\t	UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
  		sql.append("\t	BOKG_RSVT_STAT_CLSS='0001'					\n");
  		sql.append("\t	WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=(			\n");
  		sql.append("\t		SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO		\n");
  		sql.append("\t		FROM BCDBA.TBGRSVTMGMT					\n");
  		sql.append("\t		WHERE GOLF_SVC_RSVT_NO=?)	\n");
        	  
  		return sql.toString();
      }
     
}
