/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ȸ������ó��
*   �������  : golf 
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member.cyber;

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfMemCyberInsDaoProc extends AbstractProc {

	public static final String TITLE = "���̹��Ӵ� > ���ó��";

	public GolfMemCyberInsDaoProc() {}
	
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;

				
		try {

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			int amount 				= data.getInt("amount");		
			String payType 			= data.getString("payType");	
			//debug("GolfMemCyberInsDaoProc =============== amount => " + amount);
			//debug("GolfMemCyberInsDaoProc =============== payType => " + payType);
			

			// 01. ���� ���̹� �Ӵ� ��������
			//debug("===========GolfMemCyberInsDaoProc=======01. ���� ���̹� �Ӵ� ��������");
			sql = this.getCyberMoneyQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, userEtt.getAccount() ); 	
            rs = pstmt.executeQuery();			
			int cyberMoney = 0;
			if(rs.next()){
				cyberMoney = rs.getInt("TOT_AMT");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            
            
			
			// 02. ���̹��Ӵ� ����ϱ�
			debug("===========GolfMemCyberInsDaoProc=======02. ���̹��Ӵ� ����ϱ�");
			
            /**SEQ_NO ��������**************************************************************/
			String sql2 = this.getNextValQuery(); 
            PreparedStatement pstmt2 = conn.prepareStatement(sql2);
            ResultSet rs2 = pstmt2.executeQuery();			
			long max_seq_no = 0L;
			if(rs2.next()){
				max_seq_no = rs2.getLong("SEQ_NO");
			}
			if(rs2 != null) rs2.close();
            if(pstmt2 != null) pstmt2.close();
            
            /**Insert************************************************************************/
            
            String sql3 = this.getMemberTmInfoQuery();
			PreparedStatement pstmt3 = conn.prepareStatement(sql3);
			
			int totCyberMoney = cyberMoney+amount;
			
			int idx = 0;
			pstmt3.setLong(++idx, max_seq_no ); 								//COME_SEQ_SEQ_NO
			pstmt3.setString(++idx, userEtt.getAccount() ); 		//CDHD_ID
			pstmt3.setInt(++idx, amount );							//ACM_DDUC_AMT
			pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
        	
			result = pstmt3.executeUpdate();
            if(pstmt3 != null) pstmt3.close();
			
			
            // 03. ȸ�����̺� ���̹� �Ӵ� ������Ʈ �ϱ�
			debug("===========GolfMemCyberInsDaoProc=======03. ȸ�����̺� ���̹� �Ӵ� ������Ʈ �ϱ�");
            sql3 = this.getMemberUpdateQuery(amount);
			pstmt3 = conn.prepareStatement(sql3);
			pstmt3.setString(1, userEtt.getAccount() ); 		//CDHD_ID
			
			result = pstmt3.executeUpdate();
            if(pstmt3 != null) pstmt3.close();
            
            
            
            // 04. ���� ���̺� ����ϱ�
			//debug("===========GolfMemCyberInsDaoProc=======04. ���� ���̺� ����ϱ�");
          
			
			if(result > 0) {

				userEtt.setCyberMoney(totCyberMoney);
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
    private String getNextValQuery(){
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
		sql.append("\t  INSERT INTO BCDBA.TBGCBMOUSECTNTMGMT (														\n");
		sql.append("\t  		COME_SEQ_SEQ_NO, CDHD_ID, ACM_DDUC_CLSS, ACM_DDUC_AMT, REST_AMT, COME_ATON		\n");
		sql.append("\t  		) VALUES (																		\n");
		sql.append("\t  		?, ?, 'Y', ?, ?, TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')							\n");
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
		sql.append("\t  	SET CBMO_ACM_TOT_AMT=CBMO_ACM_TOT_AMT+"+cyberMoney+"	\n");
		sql.append("\t  	WHERE CDHD_ID=?											\n");
		return sql.toString();
	}
    
}
