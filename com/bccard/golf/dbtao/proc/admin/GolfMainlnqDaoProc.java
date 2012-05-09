/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPointMainlnqDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ���� �����Ͻ� ����
*   �������  : Golf
*   �ۼ�����  : 2009-05-06  
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.*;


public class GolfMainlnqDaoProc extends AbstractProc {
		
		private static final String TITLE = "GOLF ���� ������";
		
		private String sStrSql;	
	
		/** *****************************************************************
		 * GolfPointMainlnqDaoProc ���μ��� ������
		 * @param N/A
		 ***************************************************************** */
		public GolfMainlnqDaoProc() {}

			
		/**
		 * Proc ����.
		 * @param Connection con
		 * @param TaoDataSet dataSet
		 * @return TaoResult 
		 */
		public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
			
			ResultSet rs = null;
			String title = data.getString("TITLE");
			Connection con = null;
			PreparedStatement pstmt = null;
			int count = 0;
			DbTaoResult  result =  new DbTaoResult(title);
				
			try {
				  con = context.getDbConnection("default", null);
				   
			 
				 //��ȸ ----------------------------------------------------------
				  String	page_no		= data.getString("page_no");
				  
				  StringBuffer sql = new StringBuffer();
				  sql.append("\n SELECT * from BCDBA.bbs  ");
				  sql.append("\n WHERE 1 = ?	");		
			    
				  sStrSql = sql.toString();    
						 
						
				  //debug("=============sql=" + sql.toString());
				
				                      
				 pstmt = con.prepareStatement(sStrSql);
				 // �Է°� (INPUT)  
				 pstmt.setString(1, "1");  
				 rs = pstmt.executeQuery();
				 
				 if(rs != null) {			 
				 
					 while(rs.next())  {
	
					 	
						result.addString("SEQ"       	, rs.getString(1));
						result.addString("TITLE"       	, rs.getString(2));
						
						////debug(rs.getString(1));
						////debug(rs.getString(2));
						
						//result.addString("CURR_PAGE", StrUtil.isNull(rs.getString("curr_page"),""));
						result.addString("STR_SQL", StrUtil.isNull(sStrSql,""));
						result.addString("RESULT"        , "00"); //������
					 }
				 }
				 if(result.size() < 1) {
						result.addString("RESULT", "01");
				 }
				
				 
			} catch (Throwable t) {
				throw new BaseException(t);
			} finally {
				try { if(rs != null) rs.close(); } catch (Exception ignored) {}
	            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	            try { if(con  != null) con.close();  } catch (Exception ignored) {}
			}
			//debug("GolfPointMainlnqDaoProc end");	
			
			return result;
			
	
			
		}
}


