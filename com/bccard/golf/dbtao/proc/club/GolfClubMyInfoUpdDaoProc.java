/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBoardUpdDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ����Խ��� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.club;

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
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfClubMyInfoUpdDaoProc extends AbstractProc {

	public static final String TITLE = "����Խ��� ���� ó��";

	/** *****************************************************************
	 * GolfBoardUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMyInfoUpdDaoProc() {}
	
	/**
	 * ����Խ��� ��� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            
			String sql = this.getUpdateQuery();//Update Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("GREET_CTNT") );
			pstmt.setString(++idx, data.getString("CLUB_SEQ_NO") );
			pstmt.setString(++idx, data.getString("CDHD_ID") ); 
			
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
	

	/**
	 * ����Խ��� ��� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getJoinOut(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            
			String sql = this.getUpdateQuery2();//Update Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("CLUB_SEQ_NO") );
			pstmt.setString(++idx, data.getString("CDHD_ID") ); 
			
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
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGCLUBCDHDMGMT SET	\n");
		sql.append("\t  GREET_CTNT=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS')  	\n");
		sql.append("\t WHERE CLUB_SEQ_NO=?	\n");
		sql.append("\t AND CDHD_ID=?	\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getUpdateQuery2(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGCLUBCDHDMGMT SET	\n");
		sql.append("\t  SECE_YN='Y', SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS')  	\n");
		sql.append("\t WHERE CLUB_SEQ_NO=?	\n");
		sql.append("\t AND CDHD_ID=?	\n");
        return sql.toString();
    }    
}