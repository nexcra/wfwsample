/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemTmInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ȸ�� > ȸ������ó�� > TM
*   �������  : golf 
*   �ۼ�����  : 2009-07-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

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
import com.bccard.waf.tao.TaoConnection;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import javax.servlet.http.HttpServletRequest;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfMemEvtInsDaoProc extends AbstractProc {

	public static final String TITLE = "ȸ������ó�� > EVT�� ȸ�� ���� ó��";

	public GolfMemEvtInsDaoProc() {}
	
	public int execute_ibkGold(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int resultExecute = 0;
		int intResult = 0;
						
		try {
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId 				= userEtt.getSocid();

			String joinChnl				= "";	// ����ȸ�����̺� ���԰�α��� �ڵ�
			String cdhd_ctgo_seq_no		= "";	// ȸ���з��Ϸù�ȣ = ��ǥ���
			String email1 				= "";	// �̸����ּ�
			String zipcode 				= "";	// �����ȣ
			String zipaddr 				= "";	// �ּ�1
			String detailaddr 			= "";	// �ּ�2
			String mobile 				= "";	// �����
			String phone 				= "";	// ����ȭ
			
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");   
			GregorianCalendar cal = new GregorianCalendar(); 
	        Date stdate = cal.getTime();
	        String strStDate = fmt.format(stdate);	// ����ȸ���Ⱓ ������
	        
	        cal.add(cal.MONTH, 2);
	        Date edDate = cal.getTime();
	        String strEdDate = fmt.format(edDate);	// ����ȸ���Ⱓ ������
	        
	        
			sql = getIbkGoldQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){

				joinChnl 	= rs.getString("RCRU_PL_CLSS");
				email1 		= rs.getString("EMAIL1");
				zipcode 	= rs.getString("ZIPCODE");
				zipaddr 	= rs.getString("ZIPADDR");
				detailaddr 	= rs.getString("DETAILADDR");
				mobile 		= rs.getString("MOBILE");
				phone 		= rs.getString("PHONE");
				cdhd_ctgo_seq_no 	= "18";

				// �̹� ��ϵǾ� �ִ��� �˾ƺ���.
				int isMem = execute_isMem(context, socId);
				if(isMem==0){
			        
					// ȸ�����̺� �μ�Ʈ - ��ǥ���, ȸ������, �ֱ���������
					data.setString("memId", memId);
					data.setString("memNm", memNm);
					data.setString("socId", socId);
					data.setString("joinChnl", joinChnl);
					data.setString("cdhd_ctgo_seq_no", cdhd_ctgo_seq_no);
					data.setString("mobile", mobile);
					data.setString("phone", phone);
					data.setString("email1", email1);
					data.setString("zipcode", zipcode);
					data.setString("zipaddr", zipaddr);
					data.setString("detailaddr", detailaddr);
					data.setString("strStDate", strStDate);
					data.setString("strEdDate", strEdDate);
					
					// ȸ�� ���̺� �μ�Ʈ
					resultExecute = execute_insMem(context, data);
					debug("ȸ�� ���̺� �μ�Ʈ ��� :: resultExecute : " + resultExecute);
		            if(resultExecute>0){
		            	// ȸ�� ��� ���̺� �μ�Ʈ
		            	resultExecute = execute_insGrd(context, data);
						debug("ȸ�� ��� ���̺� �μ�Ʈ :: resultExecute : " + resultExecute);
		            }
		            
	            }
		        
				// ���� �� �̺�Ʈ ���̺� ������Ʈ
		        if(resultExecute>0){
		            sql = this.getUpdIbkGoldEndQuery();
					pstmt = conn.prepareStatement(sql);
					idx = 0;
		        	pstmt.setString(++idx, memId );
		        	pstmt.setString(++idx, socId );
		        					        	
		        	resultExecute = pstmt.executeUpdate();
					debug("���� �� �̺�Ʈ ���̺� ������Ʈ :: resultExecute : " + resultExecute);
		        }
			}

				        
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
						
			if(resultExecute > 0) {				
				conn.commit();
				intResult = 1;
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

		return intResult;
	}
	
	// ��������� ȸ������ �˾ƺ���.
	public int execute_isMem(WaContext context, String socId) throws TaoException {

		String title				= "ȸ������ �˾ƺ���.";	
		Connection conn 			= null;	
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		
		try {
			conn = context.getDbConnection("default", null);
			
			// �̹� ��ϵǾ� �ִ��� �˾ƺ���.
			sql = this.getMemberedCheckQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, socId );
            rs = pstmt.executeQuery();	
			if(rs != null && rs.next()){
				result = 1;
			}


			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
		return result;
		
	}
	
	// ��������� ȸ�����̺� �μ�Ʈ
	public int execute_insMem(WaContext context, TaoDataSet data) throws TaoException {

		String title				= "ȸ�����̺� ���";	
		Connection conn 			= null;	
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;
		int returnResult			= 0;
		int idx						= 0;
		
		try {

			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			sql = this.getInsertMemQuery();
			pstmt = conn.prepareStatement(sql);
			idx = 0;
        	pstmt.setString(++idx, data.getString("memId") );
        	pstmt.setString(++idx, data.getString("memNm") );
        	pstmt.setString(++idx, data.getString("socId") );
        	pstmt.setString(++idx, data.getString("joinChnl") );
        	pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
        	pstmt.setString(++idx, data.getString("mobile") );
        	pstmt.setString(++idx, data.getString("phone") );
        	pstmt.setString(++idx, data.getString("email1") );
        	pstmt.setString(++idx, data.getString("zipcode") );
        	pstmt.setString(++idx, data.getString("zipaddr") );
        	pstmt.setString(++idx, data.getString("detailaddr") );
        	pstmt.setString(++idx, data.getString("strStDate") );
        	pstmt.setString(++idx, data.getString("strEdDate") );
        					        	
        	result = pstmt.executeUpdate();

			if(result > 0) {				
				conn.commit();
				returnResult = 1;
			} else {
				conn.rollback();
			}

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
		return returnResult;
		
	}

	// ��������� ������̺� �μ�Ʈ
	public int execute_insGrd(WaContext context, TaoDataSet data) throws TaoException {

		String title				= "ȸ�����̺� ���";	
		Connection conn 			= null;	
		String sql 					= "";
		PreparedStatement pstmt		= null;
		ResultSet rs 				= null;
		int result					= 0;	
		int returnResult			= 0;
		int idx						= 0;
		
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			// ȸ����� ���̺� ������Ʈ
			sql = this.getChkGradeQuery(); 
			pstmt = conn.prepareStatement(sql);
			idx = 0;
			pstmt.setString(++idx, data.getString("memId") ); 
			pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
			rs = pstmt.executeQuery();	
			if(!rs.next()){
			
			    sql = this.getInsertGradeQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
				pstmt.setString(++idx, data.getString("memId") ); 
				pstmt.setString(++idx, data.getString("cdhd_ctgo_seq_no") );
				
				result = pstmt.executeUpdate();

				if(result > 0) {				
					conn.commit();
					returnResult = 1;
				} else {
					conn.rollback();
				}
			}
			

			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
		return returnResult;
		
	}
    /** ***********************************************************************
    * ������ �̺�Ʈ ȸ������ �˾ƺ���.
    ************************************************************************ */
    private String getIbkGoldQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT TM_IBK.JUMIN_NO, TM_IBK.RCRU_PL_CLSS	\n");
 		sql.append("\t	, TB_INFO.EMAIL1, TB_INFO.ZIPCODE, TB_INFO.ZIPADDR, TB_INFO.DETAILADDR, TB_INFO.MOBILE, TB_INFO.PHONE	\n");
 		sql.append("\t	FROM  BCDBA.TBACRGCDHDLODNTBL TM_IBK	\n");
 		sql.append("\t	JOIN BCDBA.UCUSRINFO TB_INFO ON TM_IBK.JUMIN_NO = TB_INFO.SOCID	\n");
 		sql.append("\t	WHERE TM_IBK.SITE_CLSS='02' AND TM_IBK.RCRU_PL_CLSS='4003'	\n");
 		sql.append("\t	AND TM_IBK.CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  TM_IBK.CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND TM_IBK.JUMIN_NO = ?	\n");
        return sql.toString();
    }

 	/** ***********************************************************************
	* �����ϵ� ���̵����� �˾ƺ���    
	************************************************************************ */
	private String getMemberedCheckQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  SELECT CDHD_ID, NVL(SECE_YN,'N') AS SECE_YN	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  WHERE JUMIN_NO=?	\n");
		return sql.toString();
	}

    /** ***********************************************************************
    * ����ȸ�������� �μ�Ʈ - TBGGOLFCDHD    
    ************************************************************************ */
    private String getInsertMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHD (	\n");
		sql.append("\t  	CDHD_ID, HG_NM, JUMIN_NO, JOIN_CHNL	\n");
		sql.append("\t  	, JONN_ATON, EMAIL_RECP_YN, SMS_RECP_YN	\n");
		sql.append("\t  	, CBMO_ACM_TOT_AMT, CBMO_DDUC_TOT_AMT , MEMBER_CLSS	\n");
		sql.append("\t  	, CDHD_CTGO_SEQ_NO, MOBILE, PHONE, EMAIL, ZIP_CODE, ZIPADDR, DETAILADDR, LASTACCESS	\n");
		sql.append("\t  	, ACRG_CDHD_JONN_DATE, ACRG_CDHD_END_DATE	\n");
		sql.append("\t  ) VALUES (	\n");
		sql.append("\t  	?, ?, ?, ?	\n");
		sql.append("\t  	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y'	\n");
		sql.append("\t  	, 0, 0, '1'	\n");
		sql.append("\t  	, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  	, ?, ?	\n");
		sql.append("\t  )	\n");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * ���� ����� ��ϵǾ� �ִ��� Ȯ��    
     ************************************************************************ */
     private String getChkGradeQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
        sql.append("\t  SELECT CDHD_GRD_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? \n");
 		return sql.toString();
     }

     
     /** ***********************************************************************
     * ����ȸ����ް��� �μ�Ʈ - TBGGOLFCDHDGRDMGMT    
     ************************************************************************ */
     private String getInsertGradeQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t  INSERT INTO BCDBA.TBGGOLFCDHDGRDMGMT (	\n");
 		sql.append("\t  	CDHD_GRD_SEQ_NO, CDHD_ID, CDHD_CTGO_SEQ_NO, REG_ATON	\n");
 		sql.append("\t  ) VALUES (	\n");
 		sql.append("\t  	(SELECT MAX(NVL(CDHD_GRD_SEQ_NO,0))+1 FROM BCDBA.TBGGOLFCDHDGRDMGMT), ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t  )	\n");
         return sql.toString();
     }   
 	
	/** ***********************************************************************
	* ������ȸ�� �Ϸ� �� ������Ʈ    
	************************************************************************ */
	private String getUpdIbkGoldEndQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBACRGCDHDLODNTBL	\n");
		sql.append("\t  SET JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), PROC_RSLT_CLSS='01', PROC_RSLT_CTNT=?	\n");
		sql.append("\t  WHERE SITE_CLSS='02' AND RCRU_PL_CLSS='4003' AND JUMIN_NO=?	\n");
		return sql.toString();
	}     
}
