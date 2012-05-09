/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmSysWidgetUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > ���� > ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.system.widget;

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

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfadmSysWidgetUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ > ���� > ����ó��";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmSysWidgetUpdDaoProc() {}
	
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
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			//debug("seq -> " + data.getLong("SEQ_NO"));
		
			int idx = 0;
        	pstmt.setString(++idx, data.getString("EPS_YN") ); 
        	pstmt.setString(++idx, data.getString("ANNX_FILE_NM") ); 
        	pstmt.setString(++idx, data.getString("MVPT_ANNX_FILE_PATH") );
			pstmt.setLong(++idx, data.getLong("BBRD_SEQ_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();

						
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
		sql.append("\t  UPDATE BCDBA.TBGBBRD SET							\n");
		sql.append("\t  EPS_YN=?, ANNX_FILE_NM=?, MVPT_ANNX_FILE_PATH=?		\n");
		sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')		\n");
		sql.append("\t  WHERE BBRD_SEQ_NO=?									\n");
        return sql.toString();
    }
    
}
