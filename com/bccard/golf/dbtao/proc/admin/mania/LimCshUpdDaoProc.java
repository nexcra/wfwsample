/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : LimCshUpdDaoProc
*   �ۼ���	: (��)����Ŀ�´����̼� ���ΰ�
*   ����		: ������ > �������Ͼ� > �����帮�������� > �ݾװ���
*   �������	: golf
*   �ۼ�����	: 2009-06-24
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.mania; 

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
public class LimCshUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ �����帮�������ν�û���� ����ó��";

	/** *****************************************************************
	 * GolfAdmManiaChgDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public LimCshUpdDaoProc() {}
	
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
			
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			   int idx = 0;
			   pstmt.setString(++idx, data.getString("NAME") );
			   pstmt.setString(++idx, data.getString("PRICE") );
			   pstmt.setString(++idx, data.getString("PRICE2") );
			   pstmt.setString(++idx, data.getString("PRICE3") );
			   pstmt.setString(++idx, data.getString("NO") );
			   
			   
			 				
				
				
			   
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
		sql.append("UPDATE BCDBA.TBGAMTMGMT SET	\n");
		sql.append("\t  CAR_KND_NM=?, 	\n");
		sql.append("\t  NORM_PRIC=?, 	\n");
		sql.append("\t  PCT20_DC_PRIC=?, 	\n");
		sql.append("\t  PCT30_DC_PRIC=? 	\n");
		//sql.append("\t  CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD') 	\n");
		sql.append("\t WHERE SEQ_NO=?	\n");
        return sql.toString();
    }
    
}
