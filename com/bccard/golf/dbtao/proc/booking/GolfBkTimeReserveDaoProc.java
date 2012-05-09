/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkTimeReserveDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ > xgolf > ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/******************************************************************************
* Golf
* @author	(��)�̵������ 
* @version	1.0 
******************************************************************************/
public class GolfBkTimeReserveDaoProc extends AbstractProc {

	public static final String TITLE = "��ŷ > xgolf > ����ó��";

	public GolfBkTimeReserveDaoProc() {}
	
	/**
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

			String memb_id = data.getString("memb_id");		// ���̵�
			String xFA = data.getString("xFA");				// ���� : 1 / ���� : 2 / �׸��� ���� : 3
			String xPrc = data.getString("xPrc");			// ����Ʈ���� : 1 / Ƚ������ : 2
			String xReser = data.getString("xReser");		// ���� : 1 / ��� : 2
			String rsvt_cdhd_grd_seq_no = data.getInt("intBkGrade")+"";	// ��������
			
			String golf_svc_aplc_clss = "";	//�������񽺽�û�����ڵ� - �ָ� : 0006 / ���� : 0007 / ���߱׸��� : 0008
			String pgrs_yn = "";	// �������� - Y:���� N:���
			String cslt_yn = "";	// �������� - Y:Ƚ�� N:����Ʈ
			int ridg_pers_num = 0;	// �����ݾ�
			String cbmo_use_clss = "";	// ���̹��Ӵϻ�뱸���ڵ� 0001:�Ϲ����ߺ�ŷ 0002:�Ϲ��ָ���ŷ 0003:���߱׸������� 0004:��3��ŷ 0005:Sky72�帲�ὺ 0006:Sky72����̺�������
			
			
			if(xFA.equals("1")){
				golf_svc_aplc_clss = "0006";
				cbmo_use_clss = "0001";
			}else if(xFA.equals("2")){
				golf_svc_aplc_clss = "0007";
				cbmo_use_clss = "0002";
			}else{
				golf_svc_aplc_clss = "0008";
				cbmo_use_clss = "0003";
			}
			
			if(xPrc.equals("1")){
				cslt_yn = "Y";
			}else{
				cslt_yn = "N";
			}
			
			if(xReser.equals("1")){
				pgrs_yn = "Y";
			}else{
				pgrs_yn = "N";
			}
			
			debug("=================GolfBkTimeReserveDaoProc============= =>memb_id " + memb_id);
			debug("=================GolfBkTimeReserveDaoProc============= =>xFA " + xFA);
			debug("=================GolfBkTimeReserveDaoProc============= =>xPrc " + xPrc);
			debug("=================GolfBkTimeReserveDaoProc============= =>xReser " + xReser);
			debug("=================GolfBkTimeReserveDaoProc============= =>golf_svc_aplc_clss " + golf_svc_aplc_clss);
			debug("=================GolfBkTimeReserveDaoProc============= =>pgrs_yn " + pgrs_yn);
			debug("=================GolfBkTimeReserveDaoProc============= =>rsvt_cdhd_grd_seq_no " + rsvt_cdhd_grd_seq_no);

			// 01. ��û���� �������� ������ش�. (����, ��Ҹ� �� �Է����ش�.)
			debug("=================GolfBkTimeReserveDaoProc============= 01. ��û���� �������� ������ش�.");
				
            /**SEQ_NO ��������**************************************************************/
			sql = this.getNextValQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			long max_seq_no = 0L;
			if(rs.next()){
				max_seq_no = rs.getLong("SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**�����ݾ� ��������**************************************************************/
			sql = this.getCyberMoneyInfoQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
			if(rs.next()){
				if(xPrc.equals("1")){
					ridg_pers_num = rs.getInt("GEN_WKD_BOKG_AMT");
				}else if(xPrc.equals("2")){
					ridg_pers_num = rs.getInt("GEN_WKE_BOKG_AMT");
				}else{
					ridg_pers_num = rs.getInt("WKD_GREEN_DC_AMT");
				}
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
        	pstmt.setLong(++idx, max_seq_no );
        	pstmt.setString(++idx, golf_svc_aplc_clss ); 
        	pstmt.setString(++idx, pgrs_yn ); 
        	pstmt.setString(++idx, cslt_yn ); 
        	pstmt.setInt(++idx, ridg_pers_num ); 
        	pstmt.setString(++idx, memb_id); 
        	pstmt.setString(++idx, rsvt_cdhd_grd_seq_no); 
        	
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();      

            // 02. �����̸� ���̹� �Ӵϸ� ������� ��� ���̹� �Ӵϸ� �����Ѵ�.
			if(xReser.equals("1") && xPrc.equals("1")){
				debug("=================GolfBkTimeReserveDaoProc============= 02. �����̸� ���̹� �Ӵϸ� ������� ��� ���̹� �Ӵϸ� �����Ѵ�.");
	            	
            	// 02-0. ���� ���̹� �Ӵ� �ݾ��� �����´�.
    			sql = this.getCyberMoneyQuery(); 
                pstmt = conn.prepareStatement(sql);
            	pstmt.setString(1, memb_id ); 	
                rs = pstmt.executeQuery();			
    			int cyberMoney = 0;
    			if(rs.next()){
    				cyberMoney = rs.getInt("TOT_AMT");
    			}
    			if(rs != null) rs.close();
                if(pstmt != null) pstmt.close();
                
            	// 02-1. ���̹��Ӵ� ��볻�� ���̺� �ݾ��� �־��ش�.
    			debug("=================GolfBkTimeReserveDaoProc============= 02-1. ���̹��Ӵ� ��볻�� ���̺� �ݾ��� �־��ش�.");
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
    			
    			int totCyberMoney = cyberMoney-ridg_pers_num;
    			
    			idx = 0;
    			pstmt3.setLong(++idx, cyber_money_max_seq_no ); 		//COME_SEQ_SEQ_NO
    			pstmt3.setString(++idx, memb_id ); 		//CDHD_ID
    			pstmt3.setInt(++idx, ridg_pers_num );					//ACM_DDUC_AMT
    			pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
    			pstmt3.setString(++idx, cbmo_use_clss );				//cbmo_use_clss : 0001:�Ϲ����ߺ�ŷ 0002:�Ϲ��ָ���ŷ 0003:���߱׸������� 0004:��3��ŷ 0005:Sky72�帲�ὺ 0006:Sky72����̺�������
    			pstmt3.setLong(++idx, max_seq_no );					//GOLF_SVC_RSVT_NO : �������񽺿����ȣ  	

            	
    			result = pstmt3.executeUpdate();
                if(pstmt3 != null) pstmt3.close();
    			
                
            	// 02-2. ȸ�����̺� ���̹��Ӵ� ������ ������Ʈ ���ش�.
    			debug("=================GolfBkTimeReserveDaoProc============= 02-2. ȸ�����̺� ���̹��Ӵ� ������ ������Ʈ ���ش�.");
                sql3 = this.getMemberUpdateQuery(ridg_pers_num);
    			pstmt3 = conn.prepareStatement(sql3);
    			pstmt3.setString(1, memb_id ); 		//CDHD_ID
    			
    			//result = pstmt3.executeUpdate();
                if(pstmt3 != null) pstmt3.close();
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
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	

	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.     
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGAPLCMGMT(												\n");
		sql.append("\t  		APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, CSLT_YN, RIDG_PERS_NUM	\n");
		sql.append("\t  		, CDHD_ID, REG_ATON, RSVT_CDHD_GRD_SEQ_NO							\n");
		sql.append("\t  		) VALUES (															\n");
		sql.append("\t  		?, ?, ?, ?, ?														\n");
		sql.append("\t  		, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?							\n");
		sql.append("\t  		)																	\n");

        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(APLC_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGAPLCMGMT \n");
		return sql.toString();
    }

	/** ***********************************************************************
    * ���̹��Ӵ� �ݾ� ��å ��������    
    ************************************************************************ */ 
    private String getCyberMoneyInfoQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n");  
		sql.append("\t  SELECT 													\n");  
		sql.append("\t  GEN_WKD_BOKG_AMT, GEN_WKE_BOKG_AMT, WKD_GREEN_DC_AMT	\n");  
		sql.append("\t  FROM BCDBA.TBGCBMOPLCYMGMT								\n");
		
		return sql.toString();
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
