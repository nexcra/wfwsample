/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������	: GolfLessonUccUpdDaoProc
*   �ۼ���	: (��)�̵������ õ����
*   ����		: ģ���� ucc ���� ó��
*   �������	: golf
*   �ۼ�����	: 2009-07-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.lesson;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0 
 ******************************************************************************/
public class GolfLessonUccUpdDaoProc extends AbstractProc {
	public static final String TITLE = "ģ���� ucc ���� ó��";
	
	/** ***********************************************************************
	* Proc ����. 
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		Writer writer = null;
		Reader reader = null;
		
		
		try{ 
			//01. ��ȸ ����
			String sql 	 	 			= "";
			String bbrd_clss 			= dataSet.getString("bbrd_clss");
			String idx					= dataSet.getString("idx");
			String mode					= dataSet.getString("mode");
			String titl					= dataSet.getString("titl");
			String ctnt					= dataSet.getString("ctnt");
			String userId				= dataSet.getString("userId");
			String userIp				= dataSet.getString("userIp");
			String email				= dataSet.getString("email");
			String mvpt_annx_file_path	= dataSet.getString("mvpt_annx_file_path");
			String annx_file_nm			= dataSet.getString("annx_file_nm");

			// 02. ȯ�漳��
			int pidx = 0;
			int rs = 0;
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			con = context.getDbConnection("default", null);
			
			
			if("ins".equals(mode)){
			
				// 01.insert
				long maxVal = this.selectArticleNo(context);
				con.setAutoCommit(false);
				
				sql = this.getInsertQuery();
				pstmt = con.prepareStatement(sql);
				
				pstmt.setLong(++pidx,maxVal);
				pstmt.setString(++pidx, bbrd_clss);
				pstmt.setString(++pidx, titl);
				pstmt.setString(++pidx, userId);
				pstmt.setString(++pidx, email);
				pstmt.setString(++pidx, annx_file_nm);
				pstmt.setString(++pidx, mvpt_annx_file_path);
				pstmt.setString(++pidx, userIp);
				
				rs = pstmt.executeUpdate();
				if(pstmt != null) pstmt.close();
				
				// 02.Clob ó��
				if (ctnt.length() > 0){
					
					sql = this.getSelectForUpdateQuery();
					pstmt = con.prepareStatement(sql);
					pstmt.setLong(1, maxVal);
					rset = pstmt.executeQuery();
		
//					if(rset.next()) {
//						java.sql.Clob clob = rset.getClob("CTNT");
//		                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//						reader = new CharArrayReader(ctnt.toCharArray());
//						
//						char[] buffer = new char[1024];
//						int read = 0;
//						while ((read = reader.read(buffer,0,1024)) != -1) {
//							writer.write(buffer,0,read);
//						}
//						writer.flush();
//					}
					if (rset  != null) rset.close();
					if (pstmt != null) pstmt.close();
				}
			
				
				// 03.ó��
				if(rs > 0) {
					con.commit();
					existsData = true;
				} else {
					con.rollback();
					existsData = false;
				}
			}else if("upd".equals(mode)){
				
				// 01.����
				sql = this.getUpdateQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, titl);
				pstmt.setString(++pidx, ctnt);
				pstmt.setString(++pidx, annx_file_nm);
				pstmt.setString(++pidx, mvpt_annx_file_path);
				pstmt.setString(++pidx, bbrd_clss);
				pstmt.setString(++pidx, idx);
				
				rs = pstmt.executeUpdate();
				
				
				// 03.ó��
				if(rs > 0) {
					con.commit();
					existsData = true;
				} else {
					con.rollback();
					existsData = false;
				}
				
				
			}else if("del".equals(mode)){
				
				sql = this.getDeleteQuery();
				pstmt = con.prepareStatement(sql);
				
				pstmt.setString(++pidx, bbrd_clss);
				pstmt.setString(++pidx, idx);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					existsData = true;
				}else{
					existsData = false;
				}
				
			}
			
			
			
			
			// 04.��ȯ ��ų result�� ó�� ��Ȳ ����
			if(existsData){
				result.addString("RESULT","00");
			}else{
				result.addString("RESULT", "01");
			}
		
						
			
		}catch ( Exception e ) {
			
			
			
		}finally{
			try { if(rset != null) {rset.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return result;	
	}
 
		
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getInsertQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 		INSERT INTO BCDBA.TBGBBRD(						");
		sql.append("\n 			BBRD_SEQ_NO									");
		sql.append("\n 			,BBRD_CLSS									");
		sql.append("\n 			,TITL										");
		sql.append("\n 			,CTNT										");
		sql.append("\n 			,ID											");
		sql.append("\n 			,EMAIL										");
		sql.append("\n 			,ANNX_FILE_NM								");
		sql.append("\n 			,MVPT_ANNX_FILE_PATH						");
		sql.append("\n 			,INQR_NUM									");
		sql.append("\n 			,REG_IP_ADDR								");
		sql.append("\n 			,REG_ATON									");
		sql.append("\n 			,EPS_YN										");
		sql.append("\n 			,DEL_YN										");
		sql.append("\n 		)VALUES(										");
		sql.append("\n 			?,?,?,										");
		sql.append("\n 			EMPTY_CLOB(),								");
		sql.append("\n 			?,?,?,?,									");
		sql.append("\n 			0,?,TO_CHAR(SYSDATE, 'YYYYMMDD'),'Y','N'	");
		sql.append("\n 		)												");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	    * CLOB Query�� �����Ͽ� �����Ѵ�.    
	************************************************************************ */
    private String getSelectForUpdateQuery() throws Exception{
        StringBuffer sql = new StringBuffer();
        sql.append("\n  SELECT CTNT FROM BCDBA.TBGBBRD WHERE BBRD_SEQ_NO = ? FOR UPDATE  ");
		return sql.toString();
    }
    
	/** ***********************************************************************
	    * DELETE Query�� �����Ͽ� �����Ѵ�.  
	************************************************************************ */
    private String getDeleteQuery() throws Exception{
     StringBuffer sql = new StringBuffer();
     sql.append("\n UPDATE   BCDBA.TBGBBRD 	SET								");
     sql.append("\n		DEL_YN = 'Y'										");
     sql.append("\n		,EPS_YN = 'N'										");
     sql.append("\n WHERE BBRD_CLSS = ? AND  BBRD_SEQ_NO = ? 				");
		return sql.toString();
    } 
    
    /** ***********************************************************************
	    * UPDATE Query�� �����Ͽ� �����Ѵ�.  
	************************************************************************ */
	 private String getUpdateQuery() throws Exception{
	  StringBuffer sql = new StringBuffer();
	  sql.append("\n UPDATE   BCDBA.TBGBBRD  SET									");
	  sql.append("\n 		 TITL = ?												");
	  sql.append("\n 		,CTNT = ?												");
	  sql.append("\n 		,ANNX_FILE_NM = ?										");
	  sql.append("\n 		,MVPT_ANNX_FILE_PATH = ?								");
	  sql.append("\n WHERE BBRD_CLSS = ? AND  BBRD_SEQ_NO = ? 						");
			return sql.toString();
	 } 
		    
	    
	/** ***********************************************************************
	 * �Խ��ǹ�ȣ ��������
	************************************************************************ */
	private long selectArticleNo(WaContext context) throws BaseException {

		Connection con = null;
        PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "select nvl(max(BBRD_SEQ_NO),'0')+1 as BBRD_SEQ_NO from BCDBA.TBGBBRD";
        long pidx = 0;
        try {
        	con = context.getDbConnection("default", null);
            pstmt1 = con.prepareStatement(sql);
            rset1 = pstmt1.executeQuery();   
			if (rset1.next()) {				
                pidx = rset1.getLong(1);
			}
        } catch (Throwable t) {          // SQLException �� ���� ó�� : ������ ������ ������ �߻�
        	BaseException exception = new BaseException(t);          
            throw exception;
        } finally {
        	try { if(rset1 != null) {rset1.close();} else{} } catch (Exception ignored) {}
        	try { if(pstmt1 != null) {pstmt1.close();} else{} } catch (Exception ignored) {}
        	try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
        }
        return pidx;

	}
	
	
}
