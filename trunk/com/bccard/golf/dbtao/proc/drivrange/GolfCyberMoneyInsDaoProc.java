/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfCyberMoneyInsDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : SKY72�帲���������� �����û�� ���̹��Ӵ����� ó��
*   �������  : golf
*   �ۼ�����  : 2009-07-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.drivrange;

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
* Topn
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfCyberMoneyInsDaoProc extends AbstractProc {

	public static final String TITLE = "SKY72�帲���������� �����û�� ���̹��Ӵ����� ó��";

	/** *****************************************************************
	 * GolfCyberMoneyInsDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfCyberMoneyInsDaoProc() {}
	
	/**
	 * SKY72�帲���������� �����û�� ���̹��Ӵ����� ó��
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
			
			// ���� ���̹� �Ӵ� �ݾ��� �����´�.
			sql = this.getCyberMoneyQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, data.getString("GF_ID") ); 	
            rs = pstmt.executeQuery();			
			int cyberMoney = 0;
			if(rs.next()){
				cyberMoney = rs.getInt("TOT_AMT");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
        	// ���̹��Ӵ� ��볻�� ���̺� �ݾ��� �־��ش�.
		    /**SEQ_NO ��������**************************************************************/
			String sql2 = this.getCyberMoneyNextValQuery(); 
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = pstmt2.executeQuery();			
			long cyber_money_max_seq_no = 0L;
			if(rs2.next()){
				cyber_money_max_seq_no = rs2.getLong("SEQ_NO");
			}
			if(rs2 != null) rs2.close();
            if(pstmt2 != null) pstmt2.close();
            
            /**Insert************************************************************************/
            
            String sql3 = this.getMemberTmInfoQuery();
			PreparedStatement pstmt3 = conn.prepareStatement(sql3);

			
			int totCyberMoney = cyberMoney-Integer.parseInt(data.getString("DRVR_AMT"));
			
			int idx = 0;
			pstmt3.setLong(++idx, cyber_money_max_seq_no ); 		//COME_SEQ_SEQ_NO
			pstmt3.setString(++idx, data.getString("GF_ID") ); 		//CDHD_ID
			pstmt3.setInt(++idx, Integer.parseInt(data.getString("DRVR_AMT")) );					//ACM_DDUC_AMT
			pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
			pstmt3.setString(++idx, "0004" );						//CBMO_USE_CLSS :  0004:��3��ŷ
			//pstmt3.setString(++idx, RSVT_SQL_NO );					//GOLF_SVC_RSVT_NO : �������񽺿����ȣ  			

        	
			//result2 = pstmt3.executeUpdate();
            if(pstmt3 != null) pstmt3.close();
			
            
        	// 02-2. ȸ�����̺� ���̹��Ӵ� ������ ������Ʈ ���ش�.
			debug("=================GolfBkParTimeRsInsDaoProc============= 02-2. ȸ�����̺� ���̹��Ӵ� ������ ������Ʈ ���ش�."); 
            //sql3 = this.getMemberUpdateQuery(RIDG_PERS_NUM);
			pstmt3 = conn.prepareStatement(sql3);
			//pstmt3.setString(1, userEtt.getAccount() ); 		//CDHD_ID
			
			//result2 = pstmt3.executeUpdate();
            if(pstmt3 != null) pstmt3.close();
			
			
	           
				
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
	* ���� ���̹��Ӵ� �Ѿ� ��������    
	************************************************************************ */
	private String getCyberMoneyQuery(){
		StringBuffer sql = new StringBuffer();		

		sql.append("\n");
		sql.append("\t  SELECT (CBMO_ACM_TOT_AMT-CBMO_DDUC_TOT_AMT) AS TOT_AMT		\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD										\n");
		sql.append("\t  WHERE CDHD_ID=?												\n");
		
		return sql.toString();
	}

    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�. - ���̹��Ӵ� ���� �ִ� idx    
    ************************************************************************ */
    private String getCyberMoneyNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(COME_SEQ_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGCBMOUSECTNTMGMT \n");
		return sql.toString();
    }
	
 	/** ***********************************************************************
	* ���̹��Ӵ� ����ϱ�    
	************************************************************************ */
	private String getMemberTmInfoQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGCBMOUSECTNTMGMT (													\n");
		sql.append("\t  		COME_SEQ_SEQ_NO, CDHD_ID, ACM_DDUC_CLSS, ACM_DDUC_AMT, REST_AMT, COME_ATON		\n");
		sql.append("\t  		, CBMO_USE_CLSS, GOLF_SVC_RSVT_NO												\n");
		sql.append("\t  		) VALUES (																		\n");
		sql.append("\t  		?, ?, 'N', ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), ?, ?						\n");
		sql.append("\t  		)																				\n");
		return sql.toString();
	}
	
 	/** ***********************************************************************

	* ȸ������ ������Ʈ�ϱ�
	************************************************************************ */
	private String getMemberUpdateQuery(int cyberMoney){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  	UPDATE BCDBA.TBGGOLFCDHD								\n");
		sql.append("\t  	SET CBMO_DDUC_TOT_AMT=CBMO_DDUC_TOT_AMT+"+cyberMoney+"	\n");
		sql.append("\t  	WHERE CDHD_ID=?											\n");
		return sql.toString();
	}	

}
