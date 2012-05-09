/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfClubJoinInsDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ��ȣȸ ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-03
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
public class GolfClubJoinInsDaoProc extends AbstractProc {

	public static final String TITLE = "��ȣȸ ���� ó��";

	/** *****************************************************************
	 * GolfClubJoinInsDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfClubJoinInsDaoProc() {}
	
	/**
	 * ��ȣȸ ���� ó��
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
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

			sql = this.getNextValQuery(); //�Ϸù�ȣ
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String club_cdhd_seq_no = "";
			if(rs.next()){
				club_cdhd_seq_no = rs.getString("CLUB_CDHD_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, club_cdhd_seq_no );
			pstmt.setString(++idx, data.getString("CLUB_SEQ_NO") );
			pstmt.setString(++idx, data.getString("CDHD_ID") ); 
			pstmt.setString(++idx, data.getString("CDHD_NM") );
			pstmt.setString(++idx, data.getString("GREET_CTNT") );
			pstmt.setString(++idx, data.getString("JONN_YN") );
			
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
		sql.append("INSERT INTO BCDBA.TBGCLUBCDHDMGMT		 (	\n");
		sql.append("\t  CLUB_CDHD_SEQ_NO, CLUB_SEQ_NO, CDHD_ID, CDHD_NM, GREET_CTNT, JONN_YN, SECE_YN, APLC_ATON, JONN_ATON, CHNG_ATON,    	\n");
		sql.append("\t	SECE_ATON 	\n");
		sql.append("\t ) VALUES (	\n");
		sql.append("\t  ?,?,?,?,?,?,'N',TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),NULL,NULL,	\n");
		sql.append("\t  NULL	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(CLUB_CDHD_SEQ_NO),0)+1 CLUB_CDHD_SEQ_NO FROM BCDBA.TBGCLUBCDHDMGMT		 \n");
		return sql.toString();
    }
}