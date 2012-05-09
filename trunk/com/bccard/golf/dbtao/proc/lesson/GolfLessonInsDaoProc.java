/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLessonInsDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ���� > ���� ��û
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.lesson;

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
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfLessonInsDaoProc extends AbstractProc {

	public static final String TITLE = "���� ��û ó��";

	/** *****************************************************************
	 * GolfLessonInsDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfLessonInsDaoProc() {}
	
	/**
	 * ���� ��û ó��
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
			// ��û �ߺ� üũ
			int recvOverLapChk = this.getRecvOverLapChk(conn, data);
			
			if (recvOverLapChk == 0) {
				conn.setAutoCommit(false);
	            /*****************************************************************************/
				
				sql = this.getNextValQuery(); //������û��ȣ ����
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				String aplc_seq_no = "";
				if(rs.next()){
					aplc_seq_no = rs.getString("APLC_SEQ_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            /*****************************************************************************/
	            
				sql = this.getNextValQuery2(); //�������������ȣ ����
	            pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, data.getString("LSN_SEQ_TYPE"));
	            rs = pstmt.executeQuery();			
				String golf_lesn_rsvt_no = "";
				if(rs.next()){
					golf_lesn_rsvt_no = rs.getString("GOLF_LESN_RSVT_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            /*****************************************************************************/
	            
				sql = this.getInsertQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
				
				int idx = 0;
				pstmt.setString(++idx, aplc_seq_no );
				pstmt.setString(++idx, data.getString("LSN_SEQ_TYPE")+golf_lesn_rsvt_no );		
				pstmt.setString(++idx, data.getString("LSN_SEQ_TYPE") );
				pstmt.setLong(++idx, data.getLong("RECV_NO") );	
				pstmt.setString(++idx, data.getString("CSTMR_ID") );		
				pstmt.setString(++idx, data.getString("SEX") ); 			
				pstmt.setString(++idx, data.getString("EMAIL_ID") );
				pstmt.setString(++idx, data.getString("CHG_DDD_NO") );
				pstmt.setString(++idx, data.getString("CHG_TEL_HNO") );			
				pstmt.setString(++idx, data.getString("CHG_TEL_SNO") );		
				pstmt.setString(++idx, data.getString("LSN_EXPC_CLSS") );			
				pstmt.setString(++idx, data.getString("MTTR") );	
				pstmt.setString(++idx, data.getString("STTL_AMT") );		
				
				result = pstmt.executeUpdate();
				
				if(result > 0) {
					conn.commit();
				} else {
					conn.rollback();
				}
			} else {
				result = 9;				
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
	 * ��û �ߺ� üũ
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getRecvOverLapChk(Connection conn, TaoDataSet data) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getSelectQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;	
			pstmt.setLong(++idx, data.getLong("RECV_NO") );	
			pstmt.setString(++idx, data.getString("CSTMR_ID") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
		} catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
		}
		
		return result;
	}	
	

	/**
	 * �����̾� ��ŷ �̺�Ʈ ��û �Ϸù�ȣ
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public String getMaxSeqNo(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String aplc_seq_no = "";
		
		try {
			conn = context.getDbConnection("default", null);	
			
			sql = this.getMaxSeqNo(); //������û��ȣ ����
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
			if(rs.next()){
				aplc_seq_no = rs.getString("APLC_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
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

		return aplc_seq_no;
	}

    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGAPLCMGMT (	\n");
		sql.append("\t  APLC_SEQ_NO, GOLF_LESN_RSVT_NO, GOLF_LESN_RSVT_MAX_VAL, GOLF_SVC_APLC_CLSS, LESN_SEQ_NO, CDHD_ID, SEX_CLSS, EMAIL,  DDD_NO, TEL_HNO,	\n");
		sql.append("\t  TEL_SNO, GOLF_LESN_EXPE_CLSS, MEMO_EXPL, REG_ATON, STTL_AMT	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,'0001',?,?,?,?,?,?,	\n");
		sql.append("\t 	?,?,?,TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),? \n");	
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT	\n");
		sql.append("\t 	APLC_SEQ_NO	\n");
		sql.append("\t FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t WHERE GOLF_SVC_APLC_CLSS = '0001'	\n");
		sql.append("\t AND LESN_SEQ_NO = ?	\n");
		sql.append("\t AND CDHD_ID = ?	\n");
        return sql.toString();
    }
	
    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(APLC_SEQ_NO),0)+1 APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT \n");
		return sql.toString();
    }
    
    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getNextValQuery2(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT LPAD(TO_NUMBER(NVL(MAX(SUBSTR(GOLF_LESN_RSVT_NO,6,7)),0))+1,7,'0') GOLF_LESN_RSVT_NO FROM BCDBA.TBGAPLCMGMT \n");
        sql.append("WHERE GOLF_LESN_RSVT_MAX_VAL = ? \n");
		return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	private String getMaxSeqNo(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT MAX(APLC_SEQ_NO) APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT \n");
		return sql.toString();
    }
}
