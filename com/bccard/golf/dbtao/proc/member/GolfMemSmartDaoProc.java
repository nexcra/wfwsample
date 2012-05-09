/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemSmartDaoProc
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ���� > ���ó�� 
*   �������  : golf 
*   �ۼ�����  : 20110608
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfMemSmartDaoProc extends AbstractProc {

	public static final String TITLE = "���� > ����Ʈī�� ���� Proc";
	// �켱 4100 �� ���� ���߿� ���� ä�� �� ���� ���� (������ � �������� ��� ���� �𸣴�..)

	public GolfMemSmartDaoProc() {}
	
	public int[] execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {

		int idx = 0;
		Connection conn = null;
		
		ResultSet rs = null;
		ResultSet rs2 = null;		
		PreparedStatement pstmt = null;
		PreparedStatement pstmt2 = null;
		PreparedStatement pstmt3 = null;
		
		int resultExecute = 0;
		int intResult = 0;		
		int retVals[]				= new int[2];

		boolean chk = false;
		int cnt = 0;
		
		try {
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memId				= userEtt.getAccount();
			String memNm				= userEtt.getName();
			String socId 				= userEtt.getSocid();
			String strMemClss			= userEtt.getMemberClss();
			
			String joinChnl				= "";	// ����ȸ�����̺� ���԰�α��� �ڵ�
			String cdhd_ctgo_seq_no		= "";	// ȸ���з��Ϸù�ȣ = ��ǥ���
			String email1 				= "";	// �̸����ּ�
			String zipcode 				= "";	// �����ȣ
			String zipaddr 				= "";	// �ּ�1
			String detailaddr 			= "";	// �ּ�2
			String mobile 				= "";	// �����
			String phone 				= "";	// ����ȭ

			pstmt = conn.prepareStatement(getSmartMem());
			pstmt.setString(1, socId);	
			rs = pstmt.executeQuery();
			
			while(rs.next()){

				joinChnl 	= rs.getString("RCRU_PL_CLSS");
				email1 		= rs.getString("EMAIL1");
				zipcode 	= rs.getString("ZIPCODE");
				zipaddr 	= rs.getString("ZIPADDR");
				detailaddr 	= rs.getString("DETAILADDR");
				mobile 		= rs.getString("MOBILE");
				phone 		= rs.getString("PHONE");
				cdhd_ctgo_seq_no 		= rs.getString("GRADE");
				
				// 1�ܰ� �̹� ���Ե� ID���� üũ
				pstmt2 = conn.prepareStatement(getMemberedCheckQuery());
				pstmt2.setString(1, socId );
				pstmt2.setString(2, memId );
	        	rs2 = pstmt2.executeQuery();
				
	            if(!rs2.next()){// �ű԰���ó��
	            	
	            	chk = true;
			        
					// ȸ�����̺� �μ�Ʈ - ��ǥ���, ȸ������, �ֱ���������
	            	pstmt3 = conn.prepareStatement(getInsertMemQuery());
					idx = 0;
					pstmt3.setString(++idx, memId );
					pstmt3.setString(++idx, memNm );
					pstmt3.setString(++idx, socId );
					pstmt3.setString(++idx, joinChnl );
					pstmt3.setString(++idx, strMemClss );
					pstmt3.setString(++idx, cdhd_ctgo_seq_no );
					pstmt3.setString(++idx, mobile );
					pstmt3.setString(++idx, phone );
					pstmt3.setString(++idx, email1 );
					pstmt3.setString(++idx, zipcode );
					pstmt3.setString(++idx, zipaddr );
					pstmt3.setString(++idx, detailaddr );		        					        	
		        	resultExecute = pstmt3.executeUpdate();
		        	
		        	debug("�ű�ȸ�� ���̺� �μ�Ʈ ��� :: resultExecute : " + resultExecute);
		        	resultExecute = 0;
		            
	            }else { //�簡�� ó��
	            	
	            	if (!chk){ //����� 1row�϶�
	            		
	            		chk = true;
	            	
		            	 // �簡���� ��� ������ ������ ��� �����Ѵ�.
		            	pstmt3 = conn.prepareStatement(exeGradeDel());
						idx = 0;
						pstmt3.setString(++idx, memId ); 
						pstmt3.executeUpdate();
	
		            	// 2�ܰ� ȸ�����̺� Update	            	
			            pstmt3 = conn.prepareStatement(exeReJoin());
		 				
		 				idx = 0;
		 				pstmt3.setString(++idx, joinChnl );
		 				pstmt3.setString(++idx, cdhd_ctgo_seq_no );
		 				pstmt3.setString(++idx, mobile );
		 				pstmt3.setString(++idx, phone );
		 				pstmt3.setString(++idx, email1 );
		 				pstmt3.setString(++idx, zipcode );
		 				pstmt3.setString(++idx, zipaddr );
		 				pstmt3.setString(++idx, detailaddr );
		 				pstmt3.setString(++idx, memId );
		 	        	resultExecute = pstmt3.executeUpdate();
		 	            
		 	            debug("�簡��ȸ�� ���̺� ������Ʈ ��� :: resultExecute : " + resultExecute);
		 	            resultExecute = 0;
	 	            
	            	}else{ //����� n���϶� (1row �̻��϶�)	            		
	            		cnt++;
	            	}
	            	
	            }
	            
				// ȸ����� ���̺� ������Ʈ
				//���� ���̵� ���� ������ �ٽ� ��ϵ��� �ʵ��� ���´�.
	            idx = 0;
	            pstmt2 = conn.prepareStatement(getChkGradeQuery());				
	            pstmt2.setString(++idx, memId ); 
	            pstmt2.setString(++idx, cdhd_ctgo_seq_no );
	            rs2 = pstmt2.executeQuery();	
				
				if(!rs2.next()){
				    
					idx = 0;
				    pstmt3 = conn.prepareStatement(getInsertGradeQuery());
				    pstmt3.setString(++idx, memId );
				    pstmt3.setString(++idx, cdhd_ctgo_seq_no );					
				    resultExecute = pstmt3.executeUpdate();
				    debug("����ȸ����ް��� �μ�Ʈ  ��� :: resultExecute : " + resultExecute);
				    resultExecute = 0;
				    
			        if ( cnt >0 ) {
			            //��ǥ ��� ����
			            topGradeChange(conn, memId, cdhd_ctgo_seq_no, joinChnl);
			        }				    
		
				}
		        
				// ���� �� �̺�Ʈ ���̺� ������Ʈ
	            idx = 0;
	            pstmt3 = conn.prepareStatement(exeUpdOfferEnd());				
	            pstmt3.setString(++idx, memId );
	            pstmt3.setString(++idx, joinChnl );
	            pstmt3.setString(++idx, socId );	        					        	
	        	resultExecute = pstmt3.executeUpdate();
				debug("���� �� TM���� ���̺� ������Ʈ  ��� :: resultExecute : " + resultExecute);
		        
			}	
						
			if(resultExecute > 0) {				
				conn.commit();
				intResult = 1;
			} else {
				conn.rollback();
			}
			
			cdhd_ctgo_seq_no = cdhd_ctgo_seq_no.trim().length() == 0 ? "0" : cdhd_ctgo_seq_no;
			retVals[0] = intResult;
			retVals[1] = Integer.parseInt(cdhd_ctgo_seq_no);;
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(rs2 != null) rs2.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
            try { if(pstmt3 != null) pstmt3.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
            
		}			

		return retVals;
	}
	

	/**
	 *	<pre>
	 * 	<li> ��ǥ ��� ������Ʈ
	 * 	</pre>
	 * @throws DbTaoException 
	 */
	private void topGradeChange(Connection con, String cdhdId, String grade, String joinChnl) throws DbTaoException {
	
		String title				= "��ǥ ��� ������Ʈ";		
		ResultSet rs 				= null;
		PreparedStatement pstmt		= null;		
		PreparedStatement pstmt2		= null;
		
		int idx = 0;
		
		try {
        	
        	// ���� �ڱ��޺��� ���� ����� �ִ��� �˾ƺ���.
            idx = 0;
			pstmt = con.prepareStatement(getGrdChgYN());
			pstmt.setString(++idx, grade);	
			pstmt.setString(++idx, cdhdId);	
			rs = pstmt.executeQuery();
		
			if(rs.next()){
				
				if("Y".equals(rs.getString("CHG_YN"))){
					idx = 0;
					//����Ʈ��� - ����ȸ�� ���̺� ������Ʈ
					pstmt2 = con.prepareStatement(exeUpdTopGrade());
					pstmt2.setString(++idx, grade);
					pstmt2.setString(++idx, cdhdId);	
					pstmt2.executeUpdate();					
				}					
				
			}
			
		} catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "�ý��ۿ����Դϴ�." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}            
	        try { if(pstmt2 != null) pstmt2.close(); } catch (Exception ignored) {}
		}
		
	}
	
	
    /**
   	 *	<pre>
  	 * 	<li>  ��ǥ��� ���濩�� ���
   	 * 	</pre>
  	 *  @return String ����
  	 */   	
  	private String getGrdChgYN(){
  		
  		StringBuffer sql = new StringBuffer();
  		
  		sql.append("	\n");
		sql.append("\t  SELECT (CASE WHEN T_CTGO.SORT_SEQ>(SELECT SORT_SEQ FROM BCDBA.TBGGOLFCDHDCTGOMGMT WHERE CDHD_CTGO_SEQ_NO=?) THEN 'Y' ELSE 'N' END) CHG_YN	\n");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHD T_CDHD	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO ON T_CDHD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t  WHERE CDHD_ID=?	\n");
		
  		return sql.toString();
  		
  	}
  	
    /**
   	 *	<pre>
  	 * 	<li>  ��ǥ��� ������Ʈ
   	 * 	</pre>
  	 *  @return String ����
  	 */     	
	private String exeUpdTopGrade(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO = ?	\n");
		sql.append("\t	WHERE CDHD_ID = ?	\n");
		
		return sql.toString();
		
	}  	  	

    /** ***********************************************************************
    * ����Ʈ ��� ȸ���� �ִ��� ��ȸ
    ************************************************************************ */
    private String getSmartMem(){
    	
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
 		sql.append("\t	SELECT SMART.JUMIN_NO, SMART.RCRU_PL_CLSS	\n");
 		sql.append("\t	, TB_INFO.EMAIL1, TB_INFO.ZIPCODE, TB_INFO.ZIPADDR, TB_INFO.DETAILADDR, TB_INFO.MOBILE, TB_INFO.PHONE, SMART.MEMO_EXPL GRADE	\n");
 		sql.append("\t	FROM  BCDBA.TBACRGCDHDLODNTBL SMART	\n");
 		sql.append("\t	JOIN BCDBA.UCUSRINFO TB_INFO ON SMART.JUMIN_NO = TB_INFO.SOCID	\n");
 		sql.append("\t	WHERE SMART.SITE_CLSS='02' AND PROC_RSLT_CLSS<>'01'	\n");
 		sql.append("\t	AND SMART.MEMO_EXPL IN (	\n");
 		sql.append("\t							SELECT GOLF_CMMN_CODE	\n");
 		sql.append("\t							FROM BCDBA.TBGCMMNCODE	\n");
 		sql.append("\t							WHERE GOLF_CMMN_CLSS='0064'	\n");
 		sql.append("\t							AND GOLF_CMMN_CODE != '0027'	\n");
 		sql.append("\t							)\n");	
 		sql.append("\t	AND SMART.CAMP_STRT_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND  SMART.CAMP_END_DATE >= TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
 		sql.append("\t	AND SMART.JUMIN_NO = ?	\n"); 		

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
		sql.append("\t  WHERE JUMIN_NO=? AND CDHD_ID = ?	\n");
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
		sql.append("\t  ) VALUES (	\n");
		sql.append("\t  	?, ?, ?, ?	\n");
		sql.append("\t  	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), 'Y', 'Y'	\n");
		sql.append("\t  	, 0, 0, ?	\n");
		sql.append("\t  	, ?, ?, ?, ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");		
		sql.append("\t  )	\n");
        return sql.toString();
    }
    
    /*************************************************************************
     * ���� ����� ��ϵǾ� �ִ��� Ȯ��    
     ************************************************************************ */
     private String getChkGradeQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("	\n");
        sql.append("\t  SELECT CDHD_GRD_SEQ_NO FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? \n");
 		return sql.toString();
     }

     
     /*************************************************************************
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
	* �Ϸ� �� ������Ʈ    
	************************************************************************ */
	private String exeUpdOfferEnd(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  UPDATE BCDBA.TBACRGCDHDLODNTBL	\n");
		sql.append("\t  SET JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), PROC_RSLT_CLSS='01', PROC_RSLT_CTNT=?	\n");
		sql.append("\t  WHERE SITE_CLSS='02' AND RCRU_PL_CLSS=? AND JUMIN_NO=?	\n");
		return sql.toString();
	}   
	
 	/** ***********************************************************************
	* ����ȸ����� ����
	************************************************************************ */
	private String exeGradeDel(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID=?	\n");
		return sql.toString();
	}	
	
    /** ***********************************************************************
     * ����ȸ�������� ������Ʈ - TBGGOLFCDHD => �簡��    
     ************************************************************************ */
     private String exeReJoin(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET CBMO_ACM_TOT_AMT='0', CBMO_DDUC_TOT_AMT='0'	\n");
 		sql.append("\t  , ACRG_CDHD_JONN_DATE=null, ACRG_CDHD_END_DATE=null			\n");	// ����ȸ�� �Է�
 		sql.append("\t  , SECE_YN='N', SECE_ATON='', JONN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')		\n");
 		sql.append("\t  , EMAIL_RECP_YN='Y', SMS_RECP_YN='Y', JOIN_CHNL= ?		\n");
 		sql.append("\t  , CDHD_CTGO_SEQ_NO=?, MOBILE=?, PHONE=?, EMAIL=?, ZIP_CODE=?	\n");
 		sql.append("\t  , ZIPADDR=?, DETAILADDR=?, LASTACCESS=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t  WHERE CDHD_ID=?		\n");
 		
         return sql.toString();
     }
     
}