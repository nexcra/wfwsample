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
package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0  
******************************************************************************/
public class GolfMemExtendInsDaoProc extends AbstractProc {

	public static final String TITLE = "���������� > ����ȸ�� ����ó�� Proc";

	public GolfMemExtendInsDaoProc() {}


	/** ***********************************************************************
	* �˾ƺ���    
	*********************************************************************** */
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		int idx = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
		ResultSet rs4 = null;
		PreparedStatement userInfoPstmt = null;
		ResultSet userInfoRs = null;
		int insJoinChnlHistoryResult = 0;		// ȸ�����԰�� �����丮 �μ�Ʈ ó�����
		int insGradeHistoryResult = 0;			// ȸ����� �����丮 �μ�Ʈ ó�����
				
		try {
			
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String memId				= userEtt.getAccount();
			int intMemGrade				= userEtt.getIntMemGrade();
			String payWay				= data.getString("payWay").trim();		// yr:��ȸ��, mn:��ȸ��
			
			
			// ȸ���Ⱓ�� ����Ǿ����� ���ú��� �ٽ� �����Ѵ�. // ���� ���� ȸ���Ⱓ�� ���Ҿ ���ú��� ����ȸ���Ⱓ ����
    		sql = this.getReYnQuery();
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, memId);
    		rs = pstmt.executeQuery();
    		String re_yn = "N";					// �Ⱓ ���Ῡ��
    		String acrg_cdhd_end_date = "";		// ����ȸ�� ������
    		String join_chnl = "";				// ���԰��
    		String cdhd_ctgo_seq_no = "";		// ��ǥ���
    		String tour_black_yn = "N";			// ����� ȸ�� ����
    		String ibk_gold_yn = "N";			// IBK ������ ȸ�� ����

			if(rs != null) {			 
				while(rs.next())  {	
					re_yn = rs.getString("RE_YN");
					acrg_cdhd_end_date = rs.getString("ACRG_CDHD_END_DATE");
					join_chnl = rs.getString("JOIN_CHNL");
					cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
					
					// ����� ȸ���� �Ⱓ�� ���Ҿ ���ú��� ����ȸ���Ⱓ ����
					if(join_chnl.equals("3000") && cdhd_ctgo_seq_no.equals("11")){
						tour_black_yn = "Y";
						re_yn = "N";
					}
					
					// IBK ������ ȸ���� �Ⱓ�� ���Ҿ ���ú��� ����ȸ���Ⱓ ����
					if(cdhd_ctgo_seq_no.equals("18")){
						ibk_gold_yn = "Y";
						re_yn = "N";
					}
				}
			}
			
			// ����� ȸ���� ���԰�� �����丮 ������ �� ���԰�� ����
			if(tour_black_yn.equals("Y") || ibk_gold_yn.equals("Y")){
	            insJoinChnlHistoryResult = insJoinChnlHistoryExecute(context, data, request, conn);
			}
			
			// IBK ������ ȸ���� ����� �������ش�. - ��޺��� �����丮 ����
			if(ibk_gold_yn.equals("Y")){
	            insGradeHistoryResult = insGradeHistoryExecute(context, data, request, conn);
			}
			
			debug("re_yn : " + re_yn + " / insJoinChnlHistoryResult : " + insJoinChnlHistoryResult + " / insGradeHistoryResult : " + insGradeHistoryResult 
					+ " / tour_black_yn : " + tour_black_yn + " / ibk_gold_yn : " + ibk_gold_yn + " / intMemGrade : " + intMemGrade);
			
            sql = this.getExtendQuery(re_yn, insJoinChnlHistoryResult, ibk_gold_yn, intMemGrade);
			pstmt = conn.prepareStatement(sql);
			
			idx = 0;
			if(re_yn.equals("Y")){
	        	pstmt.setString(++idx, acrg_cdhd_end_date );
	        	pstmt.setString(++idx, acrg_cdhd_end_date );
			}
        	pstmt.setString(++idx, memId );
			result = pstmt.executeUpdate();
						
            // ������ �ϰ�� 
            if("mn".equals(payWay)){
	            mnInsExecute(context, data, request, "ins", "");
            }
			
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}


	/** ***********************************************************************
	* ������ �����ϱ�    
	*********************************************************************** */
	public int mnInsExecute(WaContext context, TaoDataSet data, HttpServletRequest request, String memSort, String paySort) throws BaseException {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		
		// memSort => ins:�űԵ��, upd:������Ʈ / paySort => all:����, half:�ݾ�

		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String sql = "";
			int idx = 0;

			String memId				= userEtt.getAccount();
			String sttl_amt				= data.getString("STTL_AMT").trim();		// �����ݾ�

            sql = this.getPayMonthQuery(memSort, paySort);
			pstmt = conn.prepareStatement(sql);
			idx = 0;
			
			if("ins".equals(memSort)){
	        	pstmt.setString(++idx, memId ); 
	        	pstmt.setString(++idx, sttl_amt );
			}else{
	        	pstmt.setString(++idx, sttl_amt );
	        	pstmt.setString(++idx, memId ); 
			}
		
			
        	
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
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}	


	/** ***********************************************************************
	* ���԰�� �����丮 �μ�Ʈ
	*********************************************************************** */
	public int insJoinChnlHistoryExecute(WaContext context, TaoDataSet data, HttpServletRequest request, Connection conn) throws BaseException {

		PreparedStatement pstmt = null;
		int result =  0;
		
		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			
			String sql = "";
			String memId = userEtt.getAccount();
			int idx = 0;

            sql = this.getJoinChnlHistoryInsQuery();
			pstmt = conn.prepareStatement(sql);	
	        pstmt.setString(++idx, memId ); 
			result = pstmt.executeUpdate();
			//debug("���԰�� �����丮 �μ�Ʈ result : " + result);


		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}			

		return result;
	}	

	/** ***********************************************************************
	* ȸ����� �����丮 �μ�Ʈ
	*********************************************************************** */
	public int insGradeHistoryExecute(WaContext context, TaoDataSet data, HttpServletRequest request, Connection conn) throws BaseException {

		PreparedStatement pstmt = null;
		int result =  0;
		
		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			
			String sql = "";
			String memId = userEtt.getAccount();
			int idx = 0;

            sql = this.setHistoryQuery();
			pstmt = conn.prepareStatement(sql);	
	        pstmt.setString(++idx, memId ); 
			result = pstmt.executeUpdate();
			
			if(result>0){
				// ����� ������Ʈ �մϴ�.
				sql = this.updGradeQuery();
				pstmt = conn.prepareStatement(sql);
				idx = 0;
		        pstmt.setString(++idx, memId ); 
				result = pstmt.executeUpdate();
			}
			
			//debug("���԰�� �����丮 �μ�Ʈ result : " + result);


		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}			

		return result;
	}	
	
	

 	/** ***********************************************************************
	* ������ ����ϱ�
	************************************************************************ */
	private String getPayMonthQuery(String memSort, String paySort){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		if("ins".equals(memSort)){
			
			sql.append("\t  INSERT INTO BCDBA.TBGAPLCMGMT (APLC_SEQ_NO, GOLF_LESN_RSVT_NO	\n");
			sql.append("\t  , GOLF_SVC_APLC_CLSS, PGRS_YN, CDHD_ID, PU_DATE, CHNG_ATON, REG_ATON, STTL_AMT)	\n");
			sql.append("\t  (SELECT MAX(APLC_SEQ_NO)+1, 1, '1001', 'Y', ?, TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
			sql.append("\t  , TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),?	\n");
			sql.append("\t	FROM BCDBA.TBGAPLCMGMT)	\n");	
			
		}else{
			if("all".equals(paySort)){
				
				sql.append("\t  UPDATE BCDBA.TBGAPLCMGMT SET 	\n");
				sql.append("\t  GOLF_LESN_RSVT_NO = 1, PU_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
				sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), STTL_AMT=?	\n");
				sql.append("\t  WHERE CDHD_ID=? AND PGRS_YN='Y'	\n");
				
			}else{
				
				sql.append("\t  UPDATE BCDBA.TBGAPLCMGMT SET 	\n");
				sql.append("\t  , CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), STTL_AMT=?	\n");
				sql.append("\t  WHERE CDHD_ID=? AND PGRS_YN='Y'	\n");
				
			}
		}
		
		return sql.toString();
	}

 	/** ***********************************************************************
	* ����ȸ���Ⱓ �����ϱ�
	************************************************************************ */
	private String getExtendQuery(String re_yn, int insJoinChnlHistoryResult, String ibk_gold_yn, int intMemGrade){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		
		sql.append("\t  UPDATE BCDBA.TBGGOLFCDHD SET 	\n");
		
		// e-champ ȸ���� 3���� 
		if(intMemGrade==12){
		
			if(re_yn.equals("Y")){	// �Ⱓ�� �������� �����Ϻ��� �ϳ�
				sql.append("\t  ACRG_CDHD_JONN_DATE=?	\n");
				sql.append("\t  , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(?),3),'YYYYMMDD')	\n");
			}else{					// �Ⱓ�� ���� �Ǿ����� ���ú��� �ϳ�
				sql.append("\t  ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
				sql.append("\t  , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,3),'YYYYMMDD') 	\n");
			}
		}else{
		
			if(re_yn.equals("Y")){	// �Ⱓ�� �������� �����Ϻ��� �ϳ�
				sql.append("\t  ACRG_CDHD_JONN_DATE=?	\n");
				sql.append("\t  , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(TO_DATE(?),12),'YYYYMMDD')	\n");
			}else{					// �Ⱓ�� ���� �Ǿ����� ���ú��� �ϳ�
				sql.append("\t  ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
				sql.append("\t  , ACRG_CDHD_END_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,12),'YYYYMMDD') 	\n");
			}
		}
		
		// ����� ȸ���� ���԰�θ� �Ϲ����� �������ش�.
		if(insJoinChnlHistoryResult>0){
			sql.append("\t  , JOIN_CHNL='0001' 	\n");
		}
		
		// IBK��� ��� ȸ���� �Ϲݰ�� ȸ������ �������ش�.
		if(ibk_gold_yn.equals("Y")){
			sql.append("\t  , CDHD_CTGO_SEQ_NO='7' 	\n");
		}
		
		
		sql.append("\t  WHERE CDHD_ID=?	\n");
		
		return sql.toString();
	}

 	/** ***********************************************************************
	* ����ȸ���Ⱓ�� ���Ҵ��� Ȯ��
	************************************************************************ */
	private String getReYnQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT CASE WHEN ACRG_CDHD_END_DATE>TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'Y' ELSE 'N' END RE_YN, ACRG_CDHD_END_DATE, JOIN_CHNL, CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	WHERE CDHD_ID=?	\n");
		
		return sql.toString();
	}

    /** ***********************************************************************
    * ���԰�� �����丮 �μ�Ʈ ����
    ************************************************************************ */
    private String getJoinChnlHistoryInsQuery(){
    	StringBuffer sql = new StringBuffer();
    	sql.append("	\n");
 		sql.append("\t	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST	\n");
 		sql.append("\t	(SEQ_NO, CDHD_GRD_SEQ_NO, CDHD_ID, CDHD_CTGO_SEQ_NO, REG_ATON)	\n");
 		sql.append("\t	(SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST), JOIN_CHNL, CDHD_ID, '0'	\n");
 		sql.append("\t	, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ACRG_CDHD_JONN_DATE , ACRG_CDHD_END_DATE , JOIN_CHNL FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=?)	\n");
   		 		
 		
        return sql.toString();
    }

	/** ***********************************************************************
	* ��� �����丮 ���̺� �����Ѵ�.
	************************************************************************ */
	private String setHistoryQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n");
		sql.append("\t	INSERT INTO BCDBA.TBGCDHDGRDCHNGHST 	\n");
		sql.append("\t 	SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST)	\n");
		sql.append("\t	, GRD.CDHD_CTGO_SEQ_NO, CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, to_char(sysdate,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\n	, B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL	");
		sql.append("\t 	FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT CTGO ON GRD.CDHD_CTGO_SEQ_NO=CTGO.CDHD_CTGO_SEQ_NO 	\n");
		sql.append("\t 	JOIN BCDBA.TBGGOLFCDHD B ON GRD.CDHD_ID = B.CDHD_ID 	\n");
		sql.append("\t 	WHERE GRD.CDHD_ID=? AND CTGO.CDHD_SQ1_CTGO='0002'	\n");
				
		
					
		return sql.toString();
	}
	   
	/** ***********************************************************************
	* ����� ������Ʈ �Ѵ�.
	************************************************************************ */
	private String updGradeQuery(){
		StringBuffer sql = new StringBuffer();

 		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT  	\n");
		sql.append("\t 	SET CDHD_CTGO_SEQ_NO='7', CHNG_ATON=to_char(sysdate,'YYYYMMDDHH24MISS'), CHNG_RSON_CTNT='IBK���Goldȸ�� Gold�� �Ⱓ����' 	\n");
		sql.append("\t	WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO='18'	\n");
				
		return sql.toString();
	}
}
