/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMtGradeUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����Ƽ�ڽ� > ���� ���� > ȸ����� ���׷��̵�
*   �������  : golf 
*   �ۼ�����  : 2009-07-04 
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* golfloung		20100514	������	IBK������� Gold ȸ�� ���׷��̵� => ���װ���, ����ȸ���Ⱓ 14���� ����
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
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
public class GolfMtGradeUpdDaoProc extends AbstractProc {

	public static final String TITLE = "����Ƽ�ڽ� > ���� ���� > ȸ����� ���׷��̵�";

	public GolfMtGradeUpdDaoProc() {}
	
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		int iidx = 0;
		String memId = "";		// ȸ�����̵�
		int intMemGrade = 0;	// ����� ���

				
		try {

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				memId = userEtt.getAccount();
				intMemGrade = userEtt.getIntMemGrade();
			}
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);

			int idx = data.getInt("idx");	
			String upd_pay = data.getString("UPD_PAY");					// �Ѵ޿���  half : ���� �Ѵ޾ȵȻ���	
			String payWay = data.getString("payWay");					// ���� Ÿ�� => mn:������	
			String realPayAmt_old = data.getString("realPayAmt_old");	// �ݾ�	
			String cdhd_SQ1_CTGO = "0002";								// ȸ��1���з��ڵ� => 0001:����ī��� 0002������� 0003:���̹���
			String cdhd_SQ2_CTGO = GolfUtil.lpad(idx+"", 4, "0");		// ȸ��2���з��ڵ� => 0001:VIP 0002:��� 0003:�췮 , ���� �ڵ� ���̺�� ����
			String cdhd_CTGO_SEQ_NO = "";								// ȸ���з��Ϸù�ȣ
			
			int grade_seq = 0;				// ����� ���
			String is_charged_mem = "";		// ����ȸ��(����ȸ�� ����Ⱓ�� ���� ���� ȸ��)  

			debug("GolfMtGradeUpdDaoProc : idx : " + idx + " | upd_pay : " + upd_pay + " | payWay : " + payWay + " | realPayAmt_old : " + realPayAmt_old 
					+ " | is_charged_mem : " + is_charged_mem + " | cdhd_SQ2_CTGO : " + cdhd_SQ2_CTGO);
            
			sql = this.getMemberLevelQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, cdhd_SQ1_CTGO );
        	pstmt.setString(2, cdhd_SQ2_CTGO );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				cdhd_CTGO_SEQ_NO = rs.getString("CDHD_CTGO_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();	
            

    		// ���� ����� ����� �ִ��� Ȯ��
    		sql = this.getMembershipGradeQuery();
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, memId);
    		rs = pstmt.executeQuery();
    		
    		if(rs.next()){
    			//debug("===GolfMemInsDaoProc======= ����� ����� ������ �ش� ����� �����ͼ� �����丮 ���̺� ����ϰ� ȸ�� ����� ������Ʈ �Ѵ�.");
            	grade_seq = rs.getInt("GRADE_SEQ");
            	is_charged_mem = rs.getString("IS_CHARGED_MEM");
            	
            	if(grade_seq > 0){
            		
            		//debug("===GolfMemInsDaoProc======= �����丮 �μ�Ʈ");
					/**SEQ_NO ��������**************************************************************/
					sql = this.getMaxHistoryQuery(); 
		            pstmt = conn.prepareStatement(sql);
		            rs = pstmt.executeQuery();			
					long max_seq_no = 0L;
					if(rs.next()){
						max_seq_no = rs.getLong("MAX_SEQ_NO");
					}
					if(rs != null) rs.close();
		            if(pstmt != null) pstmt.close();
		            
		            /**Insert************************************************************************/
		            sql = this.setHistoryQuery();
					pstmt = conn.prepareStatement(sql);
					
					iidx = 0;
					pstmt.setLong(++iidx, max_seq_no );	// �����丮 ���̺� �Ϸù�ȣ
					pstmt.setLong(++iidx, grade_seq );	// ����ȸ����ް��� ���̺� �Ϸù�ȣ
					
					int result2 = pstmt.executeUpdate();
					
					if(result2 > 0){
		            
						//debug("===GolfMemInsDaoProc======= ��� ������Ʈ");
	        			iidx = 0;
	        			sql = this.updGradeQuery();
	        			pstmt = conn.prepareStatement(sql);
	        			
	        			pstmt.setString(++iidx,  cdhd_CTGO_SEQ_NO);
	        			pstmt.setInt(++iidx,  grade_seq);
	        			
	        			result = pstmt.executeUpdate();
	        			
	        			if(result > 0){
	        				
    						//debug("===GolfMtGradeUpdDaoProc======= �Ѵ��� ���� ȸ���� ����ȸ�� �Ⱓ�� ������Ʈ ���ش�. e-champ->champ �Ⱓ �ٽ� ����"); 
	        				debug("upd_pay : " + upd_pay + " / intMemGrade : " + intMemGrade + " / is_charged_mem : " + is_charged_mem + " / cdhd_CTGO_SEQ_NO : " + cdhd_CTGO_SEQ_NO);
    						sql = this.getMemberUpdateQuery(upd_pay, intMemGrade, is_charged_mem, cdhd_CTGO_SEQ_NO);
    						pstmt = conn.prepareStatement(sql);
    			        	pstmt.setString(1, cdhd_CTGO_SEQ_NO );
    			        	pstmt.setString(2, memId );
    			        	
    						result = pstmt.executeUpdate();

	        			}
	        			
						if("mn".equals(payWay)){
    						//debug("===GolfMtGradeUpdDaoProc======= ��û���� ���̺� �ݾ׾�����Ʈ, �Ѵ޳��� ȸ���� Ƚ�� ������Ʈ");
    						sql = this.updMnPayQuery(upd_pay);
    						pstmt = conn.prepareStatement(sql);
    						iidx = 0;
    			        	pstmt.setString(++iidx, realPayAmt_old );	// �ݾ�
    			        	pstmt.setString(++iidx, idx+"" );	// ����Ǵ� ���No 1:champion, 2:blue, 3:gold 
    			        	pstmt.setString(++iidx, memId );
    						result = pstmt.executeUpdate();
						}  
			        	
			            if(pstmt != null) pstmt.close();
					}
            		
            	}
    		}
    	


			
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

	public int execute_mnSeq(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		String memId = "";			// ȸ�����̵�
		String socId = "";			// �ֹε�Ϲ�ȣ
		int aplc_seq_no = 0;	// ��û���̺�(������) seq_no

				
		try {
			conn = context.getDbConnection("default", null);

			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				memId = userEtt.getAccount();
				socId = userEtt.getSocid();
			}	

			sql = this.getAplQuery(); 
            pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, memId );
        	pstmt.setString(2, socId );
            rs = pstmt.executeQuery();	
			if(rs.next()){
				aplc_seq_no = rs.getInt("APLC_SEQ_NO");
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
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return aplc_seq_no;
	}

	

   	/** ***********************************************************************
    * ȸ�� �з� ���� �������� - TBGGOLFCDHDCTGOMGMT    
    ************************************************************************ */
    private String getMemberLevelQuery(){
      StringBuffer sql = new StringBuffer();
      sql.append("\n");
      sql.append("\t  SELECT CDHD_CTGO_SEQ_NO FROM					\n");
      sql.append("\t    BCDBA.TBGGOLFCDHDCTGOMGMT					\n");
      sql.append("\t    WHERE CDHD_SQ1_CTGO=? AND CDHD_SQ2_CTGO=?	\n");
      return sql.toString();
    }

    
    /** ***********************************************************************
    * ȸ�� ������Ʈ - ����ȸ������    upd_pay = half �̸� ����������ڸ� ������Ʈ ���� �ʴ´�.
    ************************************************************************ */
    private String getMemberUpdateQuery(String upd_pay, int intMemGrade, String is_charged_mem, String cdhd_CTGO_SEQ_NO){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET	\n");
 		
 		if(intMemGrade==13){	// IBK������� Gold ȸ���� ����ȸ�� �Ⱓ�� ����Ǳ� ���� ������Ʈ�ϸ� ����ȸ���Ⱓ �������� '����ȸ���Ⱓ������+14���� ���ش�. 
 			if(is_charged_mem.equals("Y")){
 				if(cdhd_CTGO_SEQ_NO.equals("17")){
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_JONN_DATE),5),'YYYYMMDD'), 	\n");
 				}else{
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_JONN_DATE),14),'YYYYMMDD'), 	\n");
 				}
 			}else{
		 		sql.append("\t		ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'),	\n");
 				if(cdhd_CTGO_SEQ_NO.equals("17")){
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,3),'YYYYMMDD'), 	\n");
 				}else{
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD'), 	\n");
 				}
 			}
 		}else{
	 		if("half".equals(upd_pay)){	// ���װ��� ȸ������ ����ȸ���Ⱓ�� ������Ʈ ���� �ʴ´�.
	 			if(intMemGrade==12){	// e-champ ȸ���� champ�� ������Ʈ �Ҷ��� �Ⱓ�� 1������ �������ش�.
	 				sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(ACRG_CDHD_JONN_DATE),12),'YYYYMMDD'), 	\n");
	 			}
	 		}else{	// ���װ��� ȸ���� ����ȸ���Ⱓ�� ������Ʈ ���ش�.
		 		sql.append("\t		ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'),	\n");

		 		if(cdhd_CTGO_SEQ_NO.equals("17")){
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,3),'YYYYMMDD'), 	\n");
 				}else{
 					sql.append("\t		ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD'), 	\n");
 				}
	 		}
 		}
 		 		
 		sql.append("\t		CDHD_CTGO_SEQ_NO = ?	\n");
 		sql.append("\t		WHERE CDHD_ID=?	\n");
        return sql.toString();
    }
	 
	/** ***********************************************************************
	* �ش�ȸ���� ����� ����� ������ �ִ��� �˻� (����ʸ�)
	************************************************************************ */
	private String getMembershipGradeQuery(){
		StringBuffer sql = new StringBuffer();

 		sql.append("\n");
		sql.append("\t	SELECT T2.CDHD_CTGO_SEQ_NO GRADE_NO, T1.CDHD_GRD_SEQ_NO GRADE_SEQ	\n");
		sql.append("\t	, CASE WHEN (TO_DATE(T4.ACRG_CDHD_END_DATE)+1-SYSDATE)>0 THEN 'Y' ELSE 'N' END IS_CHARGED_MEM	\n");
		sql.append("\t 	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t 	JOIN BCDBA.TBGCMMNCODE T3 ON T2.CDHD_SQ2_CTGO=T3.GOLF_CMMN_CODE AND T3.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHD T4 ON T1.CDHD_ID=T4.CDHD_ID	\n");
		sql.append("\t 	WHERE T1.CDHD_ID = ? AND T2.CDHD_SQ1_CTGO='0002'	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* �����丮 ���̺� �Ϸù�ȣ �ִ밪 �������� 
	************************************************************************ */
	private String getMaxHistoryQuery(){
		StringBuffer sql = new StringBuffer();

 		sql.append("\n");
		sql.append("\t	SELECT MAX(NVL(SEQ_NO,0))+1 MAX_SEQ_NO FROM BCDBA.TBGCDHDGRDCHNGHST 	\n");
					
		return sql.toString();
	}
	  
	/** ***********************************************************************
	* �����丮 ���̺� �����Ѵ�. 
	************************************************************************ */
	private String setHistoryQuery(){
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n	");
		sql.append("\n	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	");
		sql.append("\n	SELECT ?, A.CDHD_CTGO_SEQ_NO, A.CDHD_ID, A.CDHD_CTGO_SEQ_NO, to_char(sysdate,'YYYYMMDDHH24MISS')	");
		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		sql.append("\n	FROM BCDBA.TBGGOLFCDHDGRDMGMT A , BCDBA.TBGGOLFCDHD B	");
		sql.append("\n	WHERE A.CDHD_GRD_SEQ_NO= ?	");
		sql.append("\n	AND A.CDHD_ID = B.CDHD_ID	");
		
					
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* ����� ������Ʈ �Ѵ�.
	************************************************************************ */
	private String updGradeQuery(){
		StringBuffer sql = new StringBuffer();

 		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT  	\n");
		sql.append("\t 	SET CDHD_CTGO_SEQ_NO=?, CHNG_ATON=to_char(sysdate,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t	WHERE CDHD_GRD_SEQ_NO=?	\n");
				
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* ��ȸ����� ������Ʈ
	************************************************************************ */
	private String updMnPayQuery(String upd_pay){
		StringBuffer sql = new StringBuffer();
		
 		sql.append("\n");
		sql.append("\t 	UPDATE BCDBA.TBGAPLCMGMT SET	\n");
		
		if(!"half".equals(upd_pay)){
			sql.append("\t	GOLF_LESN_RSVT_NO = 1, PU_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD'), 	\n");
		}
		
		sql.append("\t 	CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), STTL_AMT=?, RSVT_CDHD_GRD_SEQ_NO=?	\n");
		sql.append("\t 	WHERE CDHD_ID=? AND PGRS_YN='Y'	\n");
		
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* ��ȸ�� ���� ���� ��������
	************************************************************************ */
	private String getAplQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n 	SELECT APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='1001' AND CDHD_ID=? AND JUMIN_NO=? AND PGRS_YN='Y'	\n");		
		return sql.toString();
	}
                          
}
