/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardUpdDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ����Խ��� ���� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.bbs;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Golf
* @author	(��)����Ŀ�´����̼�
* @version	1.0
******************************************************************************/
public class GolfAdmBoardUpdDaoProc extends AbstractProc {

	public static final String TITLE = "������ ����Խ��� ���� ó��";

	/** *****************************************************************
	 * GolfAdmBoardUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmBoardUpdDaoProc() {}
	
	/**
	 * ������ ����Խ��� ��� ó��
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
			
			int idx = 0;
			
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
			pstmt.setString(++idx, data.getString("DEL_YN") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );

			pstmt.setString(++idx, data.getString("REG_IP_ADDR") );
			pstmt.setString(++idx, data.getString("BEST_YN") );
			pstmt.setString(++idx, data.getString("NEW_YN") );
			pstmt.setString(++idx, data.getString("COOP_YN") );

			pstmt.setString(++idx, data.getString("SEQ_NO") );
			pstmt.setString(++idx, data.getString("BBS") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getSelectForUpdateQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("SEQ_NO"));
            rs = pstmt.executeQuery();

//			if(rs.next()) {
//				java.sql.Clob clob = rs.getClob("CTNT");
//                //writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
//                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//				reader = new CharArrayReader(data.getString("CTNT").toCharArray());
//				
//				char[] buffer = new char[1024];
//				int read = 0;
//				while ((read = reader.read(buffer,0,1024)) != -1) {
//					writer.write(buffer,0,read);
//				}
//				writer.flush();
//			}
			if (rs  != null) rs.close();
			
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
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGBBRD SET	\n");
		sql.append("\t  GOLF_CLM_CLSS=?, GOLF_BOKG_FAQ_CLSS=?, GOLF_VBL_RULE_PREM_CLSS=?, TITL=?, CTNT=EMPTY_CLOB(), ID=?, HG_NM=?, EMAIL=?,  	\n");
		sql.append("\t	DDD_NO=?, TEL_HNO=?, TEL_SNO=?, EPS_YN=?, ANNX_FILE_NM=?, MVPT_ANNX_FILE_PATH=?, DEL_YN=?, CHNG_MGR_ID=?, 	\n");
		sql.append("\t	CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'), REG_IP_ADDR=?, BEST_YN=?, ANW_BLTN_ARTC_YN=?, BC_AFFI_YN=?	\n");
		sql.append("\t WHERE BBRD_SEQ_NO=?	\n");		
		sql.append("\t AND BBRD_CLSS=?	\n");
		
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
