/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreTimeRsViewDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ��� Ȯ�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.io.Reader;
import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfLoungPAYAgentDaoProc extends AbstractProc {
	
	public static final String TITLE = "������ ������Ʈ ó��";
	
	/** *****************************************************************
	 * GolfLoungPAYAgentDaoProc ������ ������Ʈ ó��
	 * @param N/A
	 ***************************************************************** */
	public GolfLoungPAYAgentDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 * @throws BaseException 
	 */
	public int execute(WaContext context) throws BaseException  {

		HttpServletRequest requeset = null;
		
		int result = 0;
		ResultSet rs = null;
		ResultSet rs_pay = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// ��������
		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

		try {
			conn = context.getDbConnection("default", null);			
			// ���ȸ�� �˻�

    		boolean payResult = false;

			int idx = 0;
    		String userId = "";
    		String juminNo = "";
    		String aplc_seq_no = "";
			String host_ip = InetAddress.getLocalHost().getHostAddress();
			//String merMgmtNo = AppConfig.getAppProperty("MBCDHD6");	
			String merMgmtNo = "";
			String ispCardNo = "";		// ispī���ȣ
			String valdlim = "";		// ���� ����
			int int_sum = 0;			// ���� �ݾ�
			String sum = "";
			String insTerm = "";		// �Һΰ�����
			String sttl_mthd_clss = "";	// ������������ڵ�
			String sttl_gds_clss = "";	// ������ǰ�����ڵ�
			String odr_no = "";			// �ֹ���ȣ
			String sttl_gds_seq_no = "";// ��û���̺� seq
			String sttl_mins_nm = "";	// ī���
			
			HashMap smsMap = new HashMap();
			String smsClss = "674";
			String message = "";
			String mobile1 = "";
			String mobile2 = "";
			String mobile3 = "";
			String mobileArr[];
			String hg_nm = "";

			String sql = this.getMemberQuery();
			pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();	
            
			while(rs.next()){
				
				if(!GolfUtil.empty(rs.getString("CDHD_ID"))){
					userId = rs.getString("CDHD_ID");
				}else{
					userId = "";
				}	
				
	            juminNo = rs.getString("JUMIN_NO");
	            aplc_seq_no = rs.getString("APLC_SEQ_NO");
				int_sum = rs.getInt("STTL_AMT");
				sum = int_sum+"";
				
				//KVP��ȸ��
				if (rs.getString("GOLF_SVC_APLC_CLSS").equals("1001")){	
					
					merMgmtNo = AppConfig.getAppProperty("MBCDHD");
				
		            if(!GolfUtil.empty(rs.getString("MOBILE"))){
		            	mobileArr = GolfUtil.split(rs.getString("MOBILE"), "-");
		            	mobile1 = mobileArr[0];
		            	mobile2 = mobileArr[1];
		            	mobile3 = mobileArr[2];
		            }else{
		            	mobile1 = rs.getString("HP_DDD_NO");
		            	mobile2 = rs.getString("HP_TEL_HNO");
		            	mobile3 = rs.getString("HP_TEL_SNO");
		            }
					
		            if(!GolfUtil.empty(rs.getString("HG_NM"))){
		            	hg_nm = rs.getString("HG_NM");
		            }else{
		            	hg_nm = rs.getString("CO_NM");
		            }

		        //Smart1000��ȸ��
				}else {
					
					merMgmtNo = AppConfig.getDataCodeProp("MBCDHD6");
					
		            if(!GolfUtil.empty(rs.getString("MOBILE"))){
		            	mobileArr = GolfUtil.split(rs.getString("MOBILE"), "-");
		            	mobile1 = mobileArr[0];
		            	mobile2 = mobileArr[1];
		            	mobile3 = mobileArr[2];
		            }
		            
		            if(!GolfUtil.empty(rs.getString("HG_NM"))){
		            	hg_nm = rs.getString("HG_NM");
		            }
					
				}
				
	            debug("userId : " + userId + " / juminNo : " + juminNo + " / aplc_seq_no : " + aplc_seq_no + " / sum : " + sum + " / mobile1 : " + mobile1 
	            		+ " / mobile2 : " + mobile2+ " / mobile3 : " + mobile3 + " / hg_nm : " + hg_nm + " / merMgmtNo : " + merMgmtNo);
				
				// ���� ���� ���� ��������
	            sql = this.getPayResultQuery(); 
				pstmt = conn.prepareStatement(sql);
				idx = 0;
	        	pstmt.setString(++idx, aplc_seq_no ); 
	        	pstmt.setString(++idx, userId ); 
	        	pstmt.setString(++idx, juminNo ); 
	            rs_pay = pstmt.executeQuery();
	            
				if(rs_pay != null && rs_pay.next()){	
					
					payResult = false;
					
					// ���� ���� ������ ���� ����
					ispCardNo = rs_pay.getString("CARD_NO").trim();
					valdlim = rs_pay.getString("VALD_DATE").trim();
					insTerm = rs_pay.getString("INS_MCNT").trim();	
					insTerm = GolfUtil.lpad(insTerm, 2, "0");	
					sttl_mthd_clss = rs_pay.getString("STTL_MTHD_CLSS").trim();
					sttl_gds_clss = rs_pay.getString("STTL_GDS_CLSS").trim();	
		            sttl_gds_seq_no = rs_pay.getString("STTL_GDS_SEQ_NO").trim();
		            sttl_mins_nm = rs_pay.getString("STTL_MINS_NM");	

					payEtt.setMerMgmtNo(merMgmtNo);	
					payEtt.setCardNo(ispCardNo);	
					payEtt.setValid(valdlim);		
					payEtt.setAmount(sum);	
					payEtt.setInsTerm(insTerm);
					payEtt.setRemoteAddr(host_ip);
					
					if( "211.181.255.40".equals(host_ip)) {
						payResult = payProc.executePayAuth(context, requeset, payEtt);			// �������� ȣ��
					} else {
						payResult = payProc.executePayAuth(context, requeset, payEtt);			// �������� ȣ��	
					}
									
					if(payResult == true){
						
						odr_no = this.getOrderNo(context);
						
						// ���� ���� ���
						sql = this.getInsertQuery();//Insert Query
						pstmt = conn.prepareStatement(sql);
						idx = 0;
						pstmt.setString(++idx, odr_no );
						
						if(GolfUtil.empty(userId)){
							pstmt.setString(++idx, juminNo );
						}else{
							pstmt.setString(++idx, userId );
						}
						
						pstmt.setString(++idx, sttl_mthd_clss );
						pstmt.setString(++idx, sttl_gds_clss );
						pstmt.setString(++idx, "N" );
						pstmt.setString(++idx, sum );	
						pstmt.setString(++idx, merMgmtNo);		
						pstmt.setString(++idx, ispCardNo ); 	
						pstmt.setString(++idx, valdlim ); 			
						pstmt.setString(++idx, insTerm.toString() );
						pstmt.setString(++idx, payEtt.getUseNo() );
						pstmt.setString(++idx, sttl_gds_seq_no );
						pstmt.setString(++idx, sttl_mins_nm );
						pstmt.executeUpdate();
						
						
						// ��û���̺� ���� �������� ����
						sql = this.getUpdTrueQuery();
						pstmt = conn.prepareStatement(sql);
			        	pstmt.setString(1, aplc_seq_no ); 
			            pstmt.executeQuery();
			            
			            //KVP��ȸ���� �ش�, Smart1000�� sms ���� ���� ���� (����� �����������û)
			            if (rs.getString("GOLF_SVC_APLC_CLSS").equals("1001")){	debug("KVP��ȸ���� sms");		
			            
							smsMap.put("ip", host_ip);						
							smsMap.put("sName", rs.getString("HG_NM"));
							smsMap.put("sPhone1", mobile1);
							smsMap.put("sPhone2", mobile2);
							smsMap.put("sPhone3", mobile3);
	
							message = "[Golf Loun.G] "+hg_nm+"�� ��ȸ��" +GolfUtil.comma(sum)+"���� �����Ǿ����ϴ�. �����մϴ�.";
							SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
							smsProc.send(smsClss, smsMap, message);
							//debug(smsRtn + "SMS>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.." + message + "|" + rs.getString("HP_DDD_NO"));
							
			            }
			            
						result++;
			            
					}else{						
						
						sql = this.getUpdFalseQuery();
						pstmt = conn.prepareStatement(sql);
			        	pstmt.setString(1, aplc_seq_no ); 
			        	pstmt.executeQuery();
			        	
			            //KVP��ȸ��
			            if (rs.getString("GOLF_SVC_APLC_CLSS").equals("1001")){			debug("KVP��ȸ����1");	        	

							sql = this.getUpdateQuery();
							pstmt = conn.prepareStatement(sql);
				        	pstmt.setString(1, userId ); 
				        	pstmt.executeQuery();
				        
			        	//Smart1000��ȸ��
			            }else {  debug("Smart1000��ȸ��   1");	
			            	exeGrdAction(conn, userId, "N", aplc_seq_no);	
			            }
						
					}
					
				}

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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}	
	
	
	private int exeGrdAction(Connection con, String cdhdId, String pgrs_yn, String aplc_seq_no) throws DbTaoException {

		int cnt = 0;
	    int idx = 0;
	    int result = 0;
	    ResultSet rs = null;
		PreparedStatement pstmt = null;
		
		try{
			
			//ȸ�� ���� ��� ����
			idx = 0;
			pstmt = con.prepareStatement(getGrdCnt());
			pstmt.setString(++idx, cdhdId );
			rs = pstmt.executeQuery(); 
	 
			if (rs.next()){     
				cnt = rs.getInt("CNT");       
			}
	   
			//���ó��
			if(cnt > 1 ){
	     
				//������ ��� �����丮 ���
				idx = 0;
				pstmt = con.prepareStatement(inGrdHistoryQuery());   
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate();   
				if(pstmt != null) pstmt.close();             
	 
				//��� ����
				idx = 0;
				pstmt = con.prepareStatement(delGrd());
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate(); 
				if(pstmt != null) pstmt.close();
				
				//��ǥ��� ����				
				idx = 0;
				pstmt = con.prepareStatement(exeUpdTopGrade1());
				pstmt.setString(++idx, cdhdId);
				pstmt.setString(++idx, cdhdId);	
				pstmt.executeUpdate();	
				if(pstmt != null) pstmt.close(); 
		        info(" || cdhdId : "+ cdhdId + " | ��ǥ���  ����");						
	     
			}else if( cnt == 1){
	    
				//������ ��� �����丮 ���
				idx = 0;
				pstmt = con.prepareStatement(inGrdHistoryQuery());   
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate();   
				if(pstmt != null) pstmt.close();    
	 
				//ȭ��Ʈ ������� ����
				idx = 0;
				pstmt = con.prepareStatement(updateGrd());
				pstmt.setString(++idx, cdhdId );
				pstmt.executeUpdate(); 
				if(pstmt != null) pstmt.close(); 
				
				//��ǥ��� ����
				idx = 0;
				pstmt = con.prepareStatement(exeUpdTopGrade2());
				pstmt.setInt(++idx, 8);
				pstmt.setString(++idx, cdhdId);	
				pstmt.executeUpdate();		
				if(pstmt != null) pstmt.close(); 
		        info(" || cdhdId : "+ cdhdId + " | ��ǥ��� [8]���� ����");				
	   
			}
			
			if(rs != null) rs.close();   
			if(pstmt != null) pstmt.close();
	   
		} catch(Exception e) {
		
			try {
				con.rollback();
		   }catch (Exception c){
	   
		   		MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, "", "�ý��ۿ����Դϴ�." );
		        throw new DbTaoException(msgEtt,e);
		        
		   }
	        
	  } finally {
	  
		   try { if(rs  != null) rs.close();  } catch (Exception ignored) {}
		   try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
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
	public String getOrderNo(WaContext context) throws DbTaoException  {
		
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
	 * ����ȸ���� �����´�.
	 ************************************************************************ */
	private String getMemberQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t 	SELECT LST.APLC_SEQ_NO, LST.GOLF_LESN_RSVT_NO, LST.GOLF_SVC_APLC_CLSS, LST.PGRS_YN, LST.CDHD_ID, LST.JUMIN_NO	\n");
		sql.append("\t 	, LST.PU_DATE, LST.CHNG_ATON, LST.REG_ATON, LST.STTL_AMT, CDHD.HG_NM, CDHD.MOBILE, LST.CO_NM, LST.HP_DDD_NO, LST.HP_TEL_HNO, LST.HP_TEL_SNO	\n");
		sql.append("\t 	FROM BCDBA.TBGAPLCMGMT LST	\n");
		sql.append("\t 	LEFT JOIN BCDBA.TBGGOLFCDHD CDHD ON LST.CDHD_ID=CDHD.CDHD_ID	\n");
		sql.append("\t 	WHERE LST.GOLF_SVC_APLC_CLSS IN ('1001', '1004') AND LST.PGRS_YN='Y'	\n");
		sql.append("\t 	AND LST.PU_DATE=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	 * ���ΰ�� ��������     
	 ************************************************************************ */
	private String getPayResultQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t 	SELECT ROWNUM, STTL_MTHD_CLSS, STTL_GDS_CLSS, CARD_NO, VALD_DATE, INS_MCNT, STTL_ATON, CDHD_ID, STTL_AMT, STTL_GDS_SEQ_NO, STTL_MINS_NM FROM (	\n");
		sql.append("\t 	 	SELECT STTL_MTHD_CLSS, STTL_GDS_CLSS, CARD_NO, VALD_DATE, INS_MCNT, STTL_ATON, CDHD_ID, STTL_AMT, STTL_GDS_SEQ_NO, STTL_MINS_NM	\n");
		sql.append("\t 	 	FROM BCDBA.TBGSTTLMGMT	\n");
		sql.append("\t 	 	WHERE STTL_GDS_SEQ_NO=?	\n");
		sql.append("\t 	 	AND CDHD_ID IN (?, ?)	\n");
		sql.append("\t 	 	ORDER BY STTL_ATON DESC	\n");
		sql.append("\t 	) WHERE ROWNUM=1	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	 * ���������� ������Ʈ     
	 ************************************************************************ */
	private String getUpdTrueQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t 	UPDATE BCDBA.TBGAPLCMGMT SET GOLF_LESN_RSVT_NO=GOLF_LESN_RSVT_NO+1	\n");
		sql.append("\t 	, PU_DATE=TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD')	\n");
		sql.append("\t 	, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t 	WHERE APLC_SEQ_NO = ?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	 * �������н� ������Ʈ     
	 ************************************************************************ */
	private String getUpdFalseQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t 	UPDATE BCDBA.TBGAPLCMGMT SET PGRS_YN='N'	\n");
		sql.append("\t 	, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t 	WHERE APLC_SEQ_NO = ?	\n");
		
		return sql.toString();
	}

    /** ***********************************************************************
    * ȸ�� ���� ������Ʈ -> ����ȸ�� �Ⱓ�� ���������� �д�.    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD SET								\n");
 		//sql.append("\t	SECE_YN='Y', SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE-1,'YYYYMMDD')	\n");
 		sql.append("\t	WHERE CDHD_ID=?												\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * �������� ��� 
    **************************************************************************** */
	private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGSTTLMGMT (	\n");
		sql.append("\t  ODR_NO, CDHD_ID, STTL_MTHD_CLSS, STTL_GDS_CLSS, STTL_STAT_CLSS, STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT,	\n");
		sql.append("\t  AUTH_NO, STTL_ATON, CNCL_ATON, STTL_GDS_SEQ_NO, STTL_MINS_NM 	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,?,?,?,?,?,	\n");
		sql.append("\t 	?,TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),NULL, ?, ?  \n");	
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
	
	
 	/*************************************************************************
 	* ȸ�� ���� ��� ����
 	************************************************************************ */ 
 	private String getGrdCnt(){

	 	StringBuffer sql = new StringBuffer();
	
	 	sql.append(" \n");
	 	sql.append("\t  SELECT COUNT(*) CNT \n");
	 	sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
	 	sql.append("\t  WHERE CDHD_ID=? \n");
	
	 	return sql.toString();

 	}  
 	
 	
    /*************************************************************************
     * ��� �����丮 ���̺� �μ�Ʈ -> ���泻�� Ȯ��
     ************************************************************************ */    
 	private String inGrdHistoryQuery(){
	   
 		StringBuffer sql = new StringBuffer();
	   
		sql.append(" \n");
		sql.append("\t  INSERT INTO BCDBA.TBGCDHDGRDCHNGHST \n");
		sql.append("\t  SELECT (SELECT MAX(NVL(SEQ_NO,0))+1 FROM BCDBA.TBGCDHDGRDCHNGHST) \n");
		sql.append("\t  , GRD.CDHD_GRD_SEQ_NO, GRD.CDHD_ID, GRD.CDHD_CTGO_SEQ_NO, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') \n");
		sql.append("\n  , B.ACRG_CDHD_JONN_DATE , B.ACRG_CDHD_END_DATE , B.JOIN_CHNL ");
		sql.append("\t  FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD \n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO \n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHD B ON GRD.CDHD_ID=B.CDHD_ID \n");
		sql.append("\t  WHERE GRD.CDHD_ID=? AND GRD.CDHD_CTGO_SEQ_NO = 27 \n");
		   
		return sql.toString();   
		   
	}   
  
 	/*************************************************************************
 	* ����Ʈ��� ����
 	************************************************************************ */    
 	private String delGrd(){

	 	StringBuffer sql = new StringBuffer();
	
	 	sql.append(" \n");
	 	sql.append("\t  DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT \n");
	 	sql.append("\t  WHERE CDHD_ID=?  \n");
	 	sql.append("\t  AND CDHD_CTGO_SEQ_NO = '27' \n");
	
	 	return sql.toString();   

 	}     


 	/*************************************************************************
 	* ����Ʈ�� ȭ��Ʈ�� ����
 	************************************************************************ */    
 	private String updateGrd(){

	 	StringBuffer sql = new StringBuffer();
	
	 	sql.append(" \n");
	 	sql.append("\t  UPDATE BCDBA.TBGGOLFCDHDGRDMGMT \n");
	 	sql.append("\t  SET CDHD_CTGO_SEQ_NO = '8' , CHNG_ATON = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') \n");
	 	sql.append("\t  WHERE CDHD_ID=?  \n");
	
	 	return sql.toString();   

 	}     

 	/*************************************************************************
 	* ��ǥ��� ����
 	************************************************************************ */    	
	private String exeUpdTopGrade1(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD																			\n");				
		sql.append("\t	SET JOIN_CHNL ='0001', CDHD_CTGO_SEQ_NO=(SELECT CDHD_CTGO_SEQ_NO														\n");
		sql.append("\t						  FROM (	 																	\n");
		sql.append("\t								SELECT  GRD.CDHD_CTGO_SEQ_NO											\n");
		sql.append("\t								FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD, BCDBA.TBGGOLFCDHDCTGOMGMT T_CTGO		\n");
		sql.append("\t								WHERE CDHD_ID = ?														\n");
		sql.append("\t								AND  GRD.CDHD_CTGO_SEQ_NO=T_CTGO.CDHD_CTGO_SEQ_NO						\n");
		sql.append("\t								ORDER BY T_CTGO.SORT_SEQ ASC											\n");
		sql.append("\t								 )																		\n");
		sql.append("\t						 WHERE ROWNUM = 1																\n");
		sql.append("\t						 )																				\n");
		sql.append("\t	WHERE CDHD_ID = ?																					\n");
		
		return sql.toString();
		
	} 	 	
 
 	/*************************************************************************
 	* ��ǥ��� ����
 	************************************************************************ */   	
	private String exeUpdTopGrade2(){
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t  SET CDHD_CTGO_SEQ_NO=? , JOIN_CHNL ='0001'	\n");		
		sql.append("\t	WHERE CDHD_ID = ?	\n");  
		
		return sql.toString();
		
	}
	
}
