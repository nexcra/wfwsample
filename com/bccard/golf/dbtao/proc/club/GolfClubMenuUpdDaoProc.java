/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfClubMenuUpdDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ��ȣȸ �޴� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-04
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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfClubMenuUpdDaoProc extends AbstractProc {

	public static final String TITLE = "��ȣȸ �޴� ���� ó��";

	/** *****************************************************************
	 * GolfBoardUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMenuUpdDaoProc() {}
	
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
		ResultSet rs = null;
		String sql = "";
		int idel = 0;
		int iCount = 0;    
		int idx = 0;
		
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			//****************************************************************

			String club_seq_no = data.getString("CLUB_SEQ_NO");
			String res_value = data.getString("RES_VALUE");
			String res_text = data.getString("RES_TEXT");
			String q_res_value = res_value.replaceAll("@", ",");
			
			String[] res_value_arry =  res_value.split("@");
			String[] res_text_arry =  res_text.split("@");						
			
			// ������ �Խ��� ����ó��
			sql = this.getDeleteQuery1(q_res_value);// �Խù� ���� ����
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, club_seq_no);	
			idel += pstmt.executeUpdate();	
			
			sql = this.getDeleteQuery2(q_res_value);// �Խù� ����
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, club_seq_no);	
			idel += pstmt.executeUpdate();	

			sql = this.getDeleteQuery3(q_res_value);// �Խ���  ����
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, club_seq_no);	
			idel += pstmt.executeUpdate();	
			
			for (int i = 0; i < res_value_arry.length; i++) {

				String n_bbrd_seq_no = "";		
				String club_bbrd_clss =  "";
				
				sql = this.getSelectQuery(); 
				// �Է°� (INPUT)         
				idx = 0;
				pstmt = conn.prepareStatement(sql.toString());
				pstmt.setString(++idx, club_seq_no);
				pstmt.setString(++idx, res_value_arry[i]);
				
				rs = pstmt.executeQuery();
				
				if(rs != null) {	
					while(rs.next())  {					
						n_bbrd_seq_no = rs.getString("BBRD_SEQ_NO");
					}
				}
				
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();

				
				if (res_value_arry[i].equals("100000000")) club_bbrd_clss = "0002";
				if (res_value_arry[i].equals("100000001")) club_bbrd_clss = "0003";		
				
				// ����
				if (!GolfUtil.isNull(n_bbrd_seq_no)) {
					sql = this.getUpdateQuery();//Update Query
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
					pstmt.setString(++idx, res_text_arry[i] );
					pstmt.setInt(++idx, (i+1) );
					pstmt.setString(++idx, club_seq_no);
					pstmt.setString(++idx, res_value_arry[i]);
					
					iCount += pstmt.executeUpdate();
				// ����
				} else {
					sql = this.getInsertQuery();//Insert Query
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
					pstmt.setString(++idx, club_bbrd_clss );
					pstmt.setString(++idx, club_seq_no);
					pstmt.setString(++idx, res_text_arry[i] );
					pstmt.setInt(++idx, (i+1) );
					
					iCount += pstmt.executeUpdate();	
				}
			}			
			//****************************************************************
			
			if(iCount == res_value_arry.length) {
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

		return iCount;
	}
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGCLUBBBRDMGMT SET	\n");
		sql.append("\t  BBRD_INFO=?, SORT_SEQ=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS')  	\n");
		sql.append("\t WHERE CLUB_SEQ_NO=?	\n");
		sql.append("\n 	AND BBRD_SEQ_NO = ?	");
        return sql.toString();
    }
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGCLUBBBRDMGMT		 (	\n");
		sql.append("\t  BBRD_SEQ_NO, CLUB_BBRD_CLSS, CLUB_SEQ_NO, BBRD_INFO, REG_ATON, CHNG_ATON, SORT_SEQ    	\n");
		sql.append("\t ) (	\n");
		sql.append("\t  SELECT	\n");
		sql.append("\t  	NVL(MAX(BBRD_SEQ_NO),0)+1 BBRD_SEQ_NO,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),NULL,?	\n");
		sql.append("\t  FROM BCDBA.TBGCLUBBBRDMGMT	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }    

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n	SELECT	");
		sql.append("\n		BBRD_SEQ_NO 	");
		sql.append("\n 	FROM 	");
		sql.append("\n	BCDBA.TBGCLUBBBRDMGMT	");
		sql.append("\n 	WHERE CLUB_SEQ_NO = ?	");
		sql.append("\n 	AND BBRD_SEQ_NO = ?	");
		return sql.toString();
    }

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getDeleteQuery1(String q_res_value){
        StringBuffer sql = new StringBuffer();		
        sql.append("\t  DELETE FROM BCDBA.TBGCLUBBBRDREPY	\n");
        sql.append("\t  WHERE REPY_SEQ_NO IN (	\n");
        sql.append("\t      SELECT	\n");
        sql.append("\t          REPY_SEQ_NO	\n");
        sql.append("\t      FROM	\n");
        sql.append("\t      BCDBA.TBGCLUBBBRDREPY			\n");
        sql.append("\t      WHERE BBRD_SEQ_NO IN	\n");
        sql.append("\t      (	\n");
        sql.append("\t          SELECT 	\n");
        sql.append("\t              SEQ_NO	\n");
        sql.append("\t          FROM	\n");
        sql.append("\t          BCDBA.TBGCLUBBBRD	\n");
        sql.append("\t          WHERE BBRD_UNIQ_SEQ_NO IN	\n");
        sql.append("\t          (	\n");
        sql.append("\t              SELECT 	\n");
        sql.append("\t                  BBRD_SEQ_NO	\n");
        sql.append("\t              FROM	\n");
        sql.append("\t              BCDBA.TBGCLUBBBRDMGMT	\n");
        sql.append("\t              WHERE CLUB_SEQ_NO = ?	\n");
        sql.append("\t              AND BBRD_SEQ_NO NOT IN ("+q_res_value+")	\n");
        sql.append("\t          )	\n");
        sql.append("\t      )	\n");
        sql.append("\t  )	\n");
		return sql.toString();
    }

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getDeleteQuery2(String q_res_value){
        StringBuffer sql = new StringBuffer();		
        sql.append("\t  DELETE FROM BCDBA.TBGCLUBBBRD	\n");
        sql.append("\t  WHERE SEQ_NO IN (	\n");
        sql.append("\t  	SELECT 	\n");
        sql.append("\t  		SEQ_NO	\n");
        sql.append("\t 		FROM	\n");
        sql.append("\t 		BCDBA.TBGCLUBBBRD	\n");
        sql.append("\t 		WHERE BBRD_UNIQ_SEQ_NO IN	\n");
        sql.append("\t 		(	\n");
        sql.append("\t  		SELECT 	\n");
        sql.append("\t 				BBRD_SEQ_NO	\n");
        sql.append("\t 			FROM	\n");
        sql.append("\t 			BCDBA.TBGCLUBBBRDMGMT	\n");
        sql.append("\t 			WHERE CLUB_SEQ_NO = ?	\n");
        sql.append("\t 			AND BBRD_SEQ_NO NOT IN ("+q_res_value+")	\n");
        sql.append("\t 		)	\n");
        sql.append("\t  )	\n");
		return sql.toString();
    }

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getDeleteQuery3(String q_res_value){
        StringBuffer sql = new StringBuffer();		
        sql.append("\t  DELETE FROM BCDBA.TBGCLUBBBRDMGMT	\n");
        sql.append("\t  WHERE BBRD_SEQ_NO IN (	\n");
        sql.append("\t  	SELECT 	\n");
        sql.append("\t 			BBRD_SEQ_NO	\n");
        sql.append("\t 		FROM	\n");
        sql.append("\t 		BCDBA.TBGCLUBBBRDMGMT	\n");
        sql.append("\t 		WHERE CLUB_SEQ_NO = ?	\n");
        sql.append("\t 		AND BBRD_SEQ_NO NOT IN ("+q_res_value+")	\n");
        sql.append("\t  )	\n");
		return sql.toString();
    }
}