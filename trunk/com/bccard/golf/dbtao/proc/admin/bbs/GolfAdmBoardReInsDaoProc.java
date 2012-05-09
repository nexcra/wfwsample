/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardReInsDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ����Խ��� �亯 ��� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.bbs;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;
import java.net.InetAddress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmBoardReInsDaoProc extends AbstractProc {

	public static final String TITLE = "������ ����Խ��� �亯 ��� ó��";

	/** *****************************************************************
	 * GolfAdmBoardReInsDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmBoardReInsDaoProc() {}
	
	/**
	 * ����Խ��� �亯 ��� ó��
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
            /*****************************************************************************/
            
			sql = this.getUpdateQuery();//Update Query
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("UPK_SEQ_NO") );
			pstmt.setString(2, data.getString("BBS") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/

			sql = this.getNextValQuery(); //�Ϸù�ȣ
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String seq_no = "";
			if(rs.next()){
				seq_no = rs.getString("BBRD_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, seq_no );
			pstmt.setString(++idx, data.getString("BBS") ); 			
			pstmt.setString(++idx, data.getString("FIELD_CD") ); 
			pstmt.setString(++idx, data.getString("CLSS_CD") ); 
			pstmt.setString(++idx, data.getString("SEC_CD") );			
			pstmt.setString(++idx, data.getString("TITL") );
			pstmt.setString(++idx, data.getString("ID") );
			pstmt.setString(++idx, data.getString("HG_NM") );
			pstmt.setString(++idx, data.getString("EMAIL_ID") );

			pstmt.setString(++idx, data.getString("CHG_DDD_NO") ); 			
			pstmt.setString(++idx, data.getString("CHG_TEL_HNO") ); 
			pstmt.setString(++idx, data.getString("CHG_TEL_SNO") ); 
			pstmt.setString(++idx, data.getString("EPS_YN") );			
			pstmt.setString(++idx, data.getString("FILE_NM") );
			pstmt.setString(++idx, data.getString("PIC_NM") );
			pstmt.setString(++idx, data.getString("HD_YN") );
			pstmt.setString(++idx, data.getString("DEL_YN") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			
			pstmt.setString(++idx, data.getString("REG_IP_ADDR") );
			pstmt.setString(++idx, data.getString("BEST_YN") );
			pstmt.setString(++idx, data.getString("NEW_YN") );
			pstmt.setString(++idx, data.getString("COOP_YN") );
			pstmt.setString(++idx, data.getString("UPK_SEQ_NO") );
			
			result = result + pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
        	/**clob ó��**20100310 ���߱⿡���� �����Ͼȵǵ��� �����Ѵ�.*******************************************************************/
            String serverip = "";  // ����������
            String devip = "";	   // ���߱� ip ����

            try {
            	serverip = InetAddress.getLocalHost().getHostAddress();
            } catch(Throwable t) {}

            try {
            	devip = AppConfig.getAppProperty("DV_WAS_1ST");
            } catch(Throwable t) {}
            
            if(!serverip.equals(devip)){
				sql = this.getSelectForUpdateQuery();//CTNT
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, seq_no);
	            rs = pstmt.executeQuery();
	
//				if(rs.next()) {
//					java.sql.Clob clob = rs.getClob("CTNT");
//	                //writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
//	                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//					reader = new CharArrayReader(data.getString("CTNT").toCharArray());
//					
//					char[] buffer = new char[1024];
//					int read = 0;
//					while ((read = reader.read(buffer,0,1024)) != -1) {
//						writer.write(buffer,0,read);
//					}
//					writer.flush();
//				}
				if (rs  != null) rs.close();
            }
			
			if(result == 2) {
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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGBBRD SET	\n");
		sql.append("\t  PROC_YN='Y'	\n");
		sql.append("\t WHERE BBRD_SEQ_NO=?	\n");
		sql.append("\t AND BBRD_CLSS=?	\n");
        return sql.toString();
    }
	
    /** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGBBRD (	\n");
		sql.append("\t  BBRD_SEQ_NO, BBRD_CLSS, GOLF_CLM_CLSS, GOLF_BOKG_FAQ_CLSS, GOLF_VBL_RULE_PREM_CLSS, TITL, CTNT, ID, HG_NM, EMAIL,  	\n");
		sql.append("\t	DDD_NO, TEL_HNO, TEL_SNO, INQR_NUM, EPS_YN, ANNX_FILE_NM, MVPT_ANNX_FILE_PATH, PROC_YN, DEL_YN, REG_MGR_ID, 	\n");
		sql.append("\t	REG_ATON, REG_IP_ADDR, BEST_YN, ANW_BLTN_ARTC_YN, BC_AFFI_YN, REPY_URNK_SEQ_NO	\n");
		sql.append("\t ) VALUES (	\n");
		sql.append("\t  ?,?,?,?,?,?,EMPTY_CLOB(),?,?,?,	\n");
		sql.append("\t  ?,?,?,0,?,?,?,?,?,?,	\n");
		sql.append("\t  TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),?,?,?,?,?		\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(BBRD_SEQ_NO),0)+1 BBRD_SEQ_NO FROM BCDBA.TBGBBRD \n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * CLOB Query�� �����Ͽ� �����Ѵ�.    
     ************************************************************************ */
     private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT CTNT FROM BCDBA.TBGBBRD \n");
         sql.append("WHERE BBRD_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
}
