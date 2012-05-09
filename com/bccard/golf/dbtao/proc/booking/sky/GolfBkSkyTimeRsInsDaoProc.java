/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrRegDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �����̾���ŷ ������ ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.sky;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.booking.GolfBkBenefitTimesDaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Topn
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfBkSkyTimeRsInsDaoProc extends AbstractProc {

	public static final String TITLE = "������ �����̾���ŷ ������ ��� ó��";

	/** *****************************************************************
	 * GolfadmGrRegDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBkSkyTimeRsInsDaoProc() {}
	
	/**
	 * ������ �����̾���ŷ ������ ��� ó��
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException  {

		String title = data.getString("TITLE");
		int resultUp = 0;
		int result2 = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String userID = "";
		DbTaoResult result =  new DbTaoResult(title);
		DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);
				
		try {
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				userID = userEtt.getAccount();
			}
			String payType = data.getString("payType");
			data.setString("CDHD_ID",userID);
			
			conn = context.getDbConnection("default", null);

			// 1��1ȸ ��û �ߺ� üũ
			int recvOverLapChk = this.getRecvOverLapChk(conn, data);

			if (recvOverLapChk == 0) {
				conn.setAutoCommit(false);		
	
	            /**SEQ_NO ��������**************************************************************/
				sql = this.getMaxNoQuery(); 
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				String max_IDX = "";
				String max_RSVT_CLSS = "";
				String rsvt_SQL_NO = "";
				if(rs.next()){
					max_IDX = GolfUtil.reSizeLen(rs.getString("MAX_IDX"),"0",7);    				
					max_RSVT_CLSS = rs.getString("MAX_RSVT_CLSS");
					rsvt_SQL_NO = max_RSVT_CLSS + "" + max_IDX;
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            
				/**����ȸ���������Ϸù�ȣ �������� 20090907**************************************************************/
	
				GolfBkBenefitTimesDaoProc proc_times = (GolfBkBenefitTimesDaoProc)context.getProc("GolfBkBenefitTimesDaoProc");
				DbTaoResult skyTimeView = proc_times.getSkyBenefit(context, dataSet, request);
				skyTimeView.next();
				int rsvt_cdhd_grd_seq_no = skyTimeView.getInt("intBkGrade");
				
	            
	            /**Insert************************************************************************/
	            
				debug("=================GolfBkParTimeRsInsDaoProc============= 01. ������ ó��");
	            // 01. insert
	            sql = this.getInsertQuery();
				pstmt = conn.prepareStatement(sql);
				
				int idx = 0;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, rsvt_SQL_NO); 
				pstmt.setString(++idx, max_RSVT_CLSS); 
				pstmt.setString(++idx, userID); 
				pstmt.setString(++idx, data.getString("TOT_PERS_NUM")); 
				pstmt.setString(++idx, data.getString("TIME_SEQ_NO")); 
				pstmt.setString(++idx, data.getString("HP_DDD_NO")); 
				pstmt.setString(++idx, data.getString("HP_TEL_HNO")); 
				pstmt.setString(++idx, data.getString("HP_TEL_SNO")); 
				pstmt.setString(++idx, data.getString("EMAIL_ID")); 
				pstmt.setInt(++idx, rsvt_cdhd_grd_seq_no); 
				
				resultUp = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	            
	
	            // 02. update
	            sql = this.getUpdateQuery();
				pstmt = conn.prepareStatement(sql);
				
				idx = 0;
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++idx, data.getString("TIME_SEQ_NO")); 
				
				resultUp = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
	           
	
	            debug("=================GolfBkSkyTimeRsInsDaoProc============= payType =>  " + payType);
				debug("=================GolfBkSkyTimeRsInsDaoProc============= 02. �����̸� ���̹� �Ӵϸ� ������� ��� ���̹� �Ӵϸ� �����Ѵ�.");
				if(payType.equals("cyber")){
	
					debug("=================GolfBkSkyTimeRsInsDaoProc=============  02-0. ���� ���̹� �Ӵ� �ݾ��� �����´�.");
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
					debug("=================GolfBkSkyTimeRsInsDaoProc=============  02-0. ���� ���̹� �Ӵ� �ݾ��� �����´�.-1");
	                
	            	// 02-1. ���̹��Ӵ� ��볻�� ���̺� �ݾ��� �־��ش�.
	    			debug("=================GolfBkSkyTimeRsInsDaoProc============= 02-1. ���̹��Ӵ� ��볻�� ���̺� �ݾ��� �־��ش�.");
	                //**SEQ_NO ��������*******************************************************
	    			String sql2 = this.getCyberMoneyNextValQuery(); 
	                PreparedStatement pstmt2 = conn.prepareStatement(sql2);
	                ResultSet rs2 = pstmt2.executeQuery();			
	    			long cyber_MONEY_MAX_SEQ_NO = 0L;
	    			if(rs2.next()){
	    				cyber_MONEY_MAX_SEQ_NO = rs2.getLong("SEQ_NO");
	    			}
	    			if(rs2 != null) rs2.close();
	                if(pstmt2 != null) pstmt2.close();
	                
	                //**Insert******************************************************************
	                
	                String sql3 = this.getMemberTmInfoQuery();
	    			PreparedStatement pstmt3 = conn.prepareStatement(sql3);
	
	    			int ridg_PERS_NUM = 5000;	// �����ݾ�
	    			int totCyberMoney = cyberMoney-ridg_PERS_NUM;
	    			
	    			idx = 0;
	    			pstmt3.setLong(++idx, cyber_MONEY_MAX_SEQ_NO ); 		//COME_SEQ_SEQ_NO
	    			pstmt3.setString(++idx, userEtt.getAccount() ); 		//CDHD_ID
	    			pstmt3.setInt(++idx, ridg_PERS_NUM );					//ACM_DDUC_AMT
	    			pstmt3.setInt(++idx, totCyberMoney );					//REST_AMT
	    			pstmt3.setString(++idx, "0005" );						//CBMO_USE_CLSS :  0004:��3��ŷ
	    			pstmt3.setString(++idx, rsvt_SQL_NO );					//GOLF_SVC_RSVT_NO : �������񽺿����ȣ  			
	
	            	
	    			result2 = pstmt3.executeUpdate();
	                if(pstmt3 != null) pstmt3.close();
	    			
	                
	            	// 02-2. ȸ�����̺� ���̹��Ӵ� ������ ������Ʈ ���ش�.
	    			debug("=================GolfBkSkyTimeRsInsDaoProc============= 02-2. ȸ�����̺� ���̹��Ӵ� ������ ������Ʈ ���ش�.");
	                sql3 = this.getMemberUpdateQuery(ridg_PERS_NUM);
	    			pstmt3 = conn.prepareStatement(sql3);
	    			pstmt3.setString(1, userEtt.getAccount() ); 		//CDHD_ID
	    			
	    			result2 = pstmt3.executeUpdate();
	                if(pstmt3 != null) pstmt3.close();
	            }
				            
	
				result.addString("RSVT_SQL_NO" 		,rsvt_SQL_NO);
				
				// ����
	            if(resultUp > 0) {
					conn.commit();
					result.addString("RESULT", "00"); //������
				} else {
					conn.rollback();
					result.addString("RESULT", "01"); //
				}
			} else {
				//�������
				result.addString("RESULT" ,"02" );
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
	
	/**
	 * 1�� 1ȸ ��û �ߺ� üũ
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getRecvOverLapChk(Connection conn, TaoDataSet data) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getSelectOverQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, "S" );	
			pstmt.setString(++idx, data.getString("CDHD_ID") );
			pstmt.setString(++idx, data.getString("BK_DATE") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
		} catch(Exception e) {
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}

		}
		
		return result;
	}

    /** ***********************************************************************
    * 1�� 1ȸ �ߺ���û üũ Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
	private String getSelectOverQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("SELECT	\n");
		sql.append("\t 	AFFI_GREEN_SEQ_NO	\n");
		sql.append("\t FROM BCDBA.TBGRSVTMGMT a	\n");
		sql.append("\t 		inner join BCDBA.TBGRSVTABLEBOKGTIMEMGMT b on a.RSVT_ABLE_BOKG_TIME_SEQ_NO=b.RSVT_ABLE_BOKG_TIME_SEQ_NO		\n");
		sql.append("\t 		inner join BCDBA.TBGRSVTABLESCDMGMT c on b.RSVT_ABLE_SCD_SEQ_NO=c.RSVT_ABLE_SCD_SEQ_NO						\n");

		sql.append("\t WHERE a.GOLF_SVC_RSVT_MAX_VAL LIKE '%' || ? 	\n"); //�����̾���ŷ : 2009M0000001 ��3��ŷ : 2009P0000001 ��ī��72�帲���������� : 2009D0000001 ��ī��72�帲�ὺ : 2009S0000001 ���ְ�������� : 2009J0000001 ���ü��� : 2009F0000001
		sql.append("\t AND a.CDHD_ID = ?	\n");
		sql.append("\t AND c.BOKG_ABLE_DATE = ?	\n");
		sql.append("\t AND a.RSVT_YN = 'Y'	\n");

        return sql.toString();
    }
    
	/** ***********************************************************************
    * Insert Query�� �����Ͽ� �����Ѵ�.    
    * 20090907 ����ȸ���������Ϸù�ȣ �߰� (RSVT_CDHD_GRD_SEQ_NO)
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("INSERT INTO BCDBA.TBGRSVTMGMT (															\n");
		sql.append("\t  GOLF_SVC_RSVT_NO, GOLF_SVC_RSVT_MAX_VAL, CDHD_ID, TOT_PERS_NUM, RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t  , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, RSVT_YN, REG_ATON, RSVT_CDHD_GRD_SEQ_NO	\n");
		sql.append("\t																		\n");
		sql.append("\t  ) VALUES (															\n");
		sql.append("\t  ?, ?, ?, ?, ?														\n");
		sql.append("\t  , ?, ?, ?, ?, 'Y', TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?			\n");
		sql.append("\t  )																	\n");
        return sql.toString();
    }
         
     /** ***********************************************************************
      * �����ȣ MAX���� �����´�.    
      ************************************************************************ */
	private String getMaxNoQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT NVL(SUBSTR(MAX(GOLF_SVC_RSVT_NO),6,12)+1,1) AS MAX_IDX,  (TO_CHAR(SYSDATE, 'YYYY')||'S') AS MAX_RSVT_CLSS \n");
		sql.append("\t  FROM BCDBA.TBGRSVTMGMT 														\n");
		sql.append("\t  WHERE GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'S')     						\n");      
		return sql.toString();
	}


    
	/** ***********************************************************************
     * Update Query�� �����Ͽ� �����Ѵ�.    - �ش� ƼŸ���� ����ó�� ���ش�.
     ************************************************************************ */
     private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();
        
  		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET		\n");
 		sql.append("\t  BOKG_RSVT_STAT_CLSS='0002'			\n");
 		sql.append("\t  WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?			\n");    
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
