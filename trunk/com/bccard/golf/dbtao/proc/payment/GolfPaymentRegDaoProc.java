/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLsnRecvRegDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ���� ����
*   �������  : golf
*   �ۼ�����  : 2009-06-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.payment;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfPaymentRegDaoProc extends AbstractProc {

	public static final String TITLE = "���� ����";

	/** *****************************************************************
	 * GolfLsnRecvRegDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfPaymentRegDaoProc() {}
	
	/**
	 * ���� ����
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
		String odr_no = "";		// �ֹ���ȣ
				
		try {
			conn = context.getDbConnection("default", null);				
			conn.setAutoCommit(false);
						
			odr_no = data.getString("ORDER_NO");
			
			// �ֹ���ȣ ���� ��� ���� ���ϱ�- �þ����� ���� �߰�
 			if(odr_no == null || odr_no.equals("")){
				odr_no = this.getOrderNo(context, data);
			}
             
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, odr_no );
			pstmt.setString(++idx, data.getString("CDHD_ID") );		
			pstmt.setString(++idx, data.getString("STTL_MTHD_CLSS") );
			pstmt.setString(++idx, data.getString("STTL_GDS_CLSS") );
			pstmt.setString(++idx, data.getString("STTL_STAT_CLSS") );
			pstmt.setString(++idx, data.getString("STTL_AMT") );	
			pstmt.setString(++idx, data.getString("MER_NO") );		
			pstmt.setString(++idx, data.getString("CARD_NO") ); 	
			pstmt.setString(++idx, data.getString("VALD_DATE") ); 			
			pstmt.setString(++idx, data.getString("INS_MCNT") );
			pstmt.setString(++idx, data.getString("AUTH_NO") );
			pstmt.setString(++idx, data.getString("STTL_GDS_SEQ_NO") );		
			
			/*2009.09.30 �߰� */
			pstmt.setString(++idx, data.getString("DC_AMT") );
			pstmt.setString(++idx, data.getString("NORM_AMT") );
			pstmt.setString(++idx, data.getString("CUPN_CTNT") );		

			// �ſ�ī���̸�(������ü �����̸�)
			pstmt.setString(++idx, data.getString("STTL_MINS_NM") );		

			result = pstmt.executeUpdate();
			
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
	 * �������г��� ����
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int failExecute(WaContext context, TaoDataSet data,	HttpServletRequest request, GolfPayAuthEtt ett) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);				
			conn.setAutoCommit(false);
			
			// �������� ��������
			String card_no = ett.getCardNo();		// ī���ȣ 
			String sttl_amt = ett.getAmount();		// ���αݾ� 
			String res_code = ett.getResCode();		// �����ڵ� 
			String res_msg = ett.getResMsg();		// ����޼��� 
			          
			// ȸ������ ��������
			String userNm = "";
			String mobile1 = "";
			String mobile2 = "";
			String mobile3 = "";
			String phone1 = "";
			String phone2 = "";
			String phone3 = "";
			
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);				
			
			if(usrEntity != null) {
				userNm		= (String)usrEntity.getName();
				mobile1		= (String)usrEntity.getMobile1();
				mobile2		= (String)usrEntity.getMobile2();
				mobile3		= (String)usrEntity.getMobile3();
				phone1		= (String)usrEntity.getPhone1();
				phone2		= (String)usrEntity.getPhone2();
				phone3		= (String)usrEntity.getPhone3();
			}			
			 
			// SEQ_NO ��������
			sql = this.getNextValFailQuery();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
            long max_seq_no = 0L;
			if(rs.next()){
				max_seq_no = rs.getLong("SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close(); 
          
            // ���г��� DB����
			sql = this.getInsertFailQuery();
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setLong(++idx, max_seq_no );
			pstmt.setString(++idx, data.getString("CDHD_ID") );		
			pstmt.setString(++idx, userNm );		
			pstmt.setString(++idx, mobile1 );		
			pstmt.setString(++idx, mobile2 );		
			pstmt.setString(++idx, mobile3 );		
			pstmt.setString(++idx, phone1 );		
			pstmt.setString(++idx, phone2 );		
			pstmt.setString(++idx, phone3 );		
			pstmt.setString(++idx, data.getString("STTL_MTHD_CLSS") );
			pstmt.setString(++idx, data.getString("STTL_GDS_CLSS") );
			pstmt.setString(++idx, data.getString("STTL_STAT_CLSS") );
			pstmt.setString(++idx, card_no ); 	
			pstmt.setString(++idx, sttl_amt );		
			pstmt.setString(++idx, res_code ); 			
			pstmt.setString(++idx, res_msg );		

			result = pstmt.executeUpdate();
			
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
	 * ���� �ֹ���ȣ ���ϱ�
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public String getOrderNo(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String odr_no = "";
				
		try {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date toDay = new Date(); 
			String nowDate = dateFormat.format(toDay);

			conn = context.getDbConnection("default", null);				
			
			sql = this.getOrderSeqQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();	
            
			if(rs.next()){
				String odr_seq = rs.getString("ORDER_SEQ");
				odr_no = nowDate + GolfUtil.lpad(odr_seq, 6, "0");
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
		} catch(Exception e) {
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return odr_no;
	}
	
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    **************************************************************************** */
	private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGSTTLMGMT (	\n");
		sql.append("\t  ODR_NO, CDHD_ID, STTL_MTHD_CLSS, STTL_GDS_CLSS, STTL_STAT_CLSS, STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT,	\n");
		sql.append("\t  AUTH_NO, STTL_GDS_SEQ_NO, STTL_ATON, CNCL_ATON ,DC_AMT, NORM_AMT, CUPN_CTNT, STTL_MINS_NM	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,?,?,?,?,?,	\n");
		sql.append("\t 	?,?,TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),NULL , ?, ?, ?, ? \n");	
		sql.append("\t \n)");	
        return sql.toString();
    }
	 
    /** *****************************************************************************************************
    * �ֹ���ȣ ���� ������ ��ȣ ��������   
	************************************************************************ */
	private String getOrderSeqQuery(){
		StringBuffer sql = new StringBuffer();           
		sql.append("\n").append(" SELECT BCDBA.SEQ_GSTTLMGMT.NEXTVAL ORDER_SEQ FROM DUAL   ");   
		return sql.toString();
	} 
	
    /** *****************************************************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�. - ���� ��� ���� 2009.12.28   
    ************************************************************************ */
	private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT TO_CHAR(SYSDATE,'YYYYMMDD')||LPAD(TO_NUMBER(NVL(MAX(SUBSTR(ODR_NO,9,6)),0))+1,6,'0') ODR_NO FROM BCDBA.TBGSTTLMGMT \n");
		return sql.toString();
    }
	
    /** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. - �������г��� - 2009.11.26
	************************************************************************ */
	private String getInsertFailQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGSTTLFAILCTNT (		\n");
		sql.append("\t		SEQ_NO, CDHD_ID, NM, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, DDD_NO, TEL_HNO, TEL_SNO,	\n");
		sql.append("\t		STTL_MTHD_CLSS, STTL_GDS_CLSS, STTL_STAT_CLSS, CARD_NO, STTL_AMT, RSPN_CODE, RSPN_MSG_CTNT, STTL_ATON	\n");
		sql.append("\t 		) VALUES (	\n");
		sql.append("\t 		?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t 		)	\n");
		return sql.toString();
	}	
	
    /** ***********************************************************************
	* Max IDX Query�� �����Ͽ� �����Ѵ�. - �������г��� 2009.11.26
	************************************************************************ */
	private String getNextValFailQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT NVL(MAX(SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGSTTLFAILCTNT \n");
		return sql.toString();
	}	
}
