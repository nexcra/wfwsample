/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMtScoreUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����Ƽ�ڽ� > �������� > ���ھ� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMtInfoUpdDaoProc extends AbstractProc {

	public static final String TITLE = "���ھ� ���� ó��";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfMtInfoUpdDaoProc() {}
	
	/**
	 * ������ �������α׷� ��� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memID = userEtt.getAccount();
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String email_RECP_YN = data.getString("EMAIL_RECP_YN");
			String sms_RECP_YN = data.getString("SMS_RECP_YN");
            
			String sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);

			int idx = 0;
        	pstmt.setString(++idx, email_RECP_YN ); 
        	pstmt.setString(++idx, sms_RECP_YN ); 
        	pstmt.setString(++idx, memID ); 
        	
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
        	
			
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
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(){
    	
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  	UPDATE BCDBA.TBGGOLFCDHD SET			\n");
		sql.append("\t  	EMAIL_RECP_YN=?, SMS_RECP_YN=?			\n");
		sql.append("\t  	WHERE CDHD_ID=?							\n");
        return sql.toString();
    }
    
}
