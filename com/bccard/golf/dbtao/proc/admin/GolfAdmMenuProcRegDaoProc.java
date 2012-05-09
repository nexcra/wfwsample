/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMenuProcRegDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �޴� ��� ó��
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

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmMenuProcRegDaoProc extends AbstractProc {
	public static final String TITLE = "������  �޴� ��� ó��";
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		PreparedStatement pstmt_in = null;
		ResultSet rs = null;
		DbTaoResult result = null;
		Connection con = null;
		
		
		//debug("==== GolfAdmMenuProcRegDaoProc start ===");
		 
		try{
			// WHERE�� ��ȸ ����
			String str_table_name		= dataSet.getString("str_table_name"); 		//���̺��̸�
			String str_idx_col_name		= dataSet.getString("str_idx_col_name"); 	//�÷��̸�
			String str_pidx_col_name	= dataSet.getString("str_pidx_col_name"); 	//�÷��̸�			
			String str_ord_name			= dataSet.getString("str_ord_name"); 
			String str_where			= dataSet.getString("str_where"); 
			String str_col_name			= dataSet.getString("str_col_name"); 
			String idx					= dataSet.getString("idx"); 
			String str_name				= dataSet.getString("str_name"); 
			String str_url				= dataSet.getString("str_url"); 
			String pidx					= dataSet.getString("pidx"); 
			String mode					= dataSet.getString("mode"); 
			String str_ord				=	"";
			
			StringBuffer sql = new StringBuffer();		

			//SEQ �ִ밪 ���ϱ�	
			sql.append("\n").append(" SELECT NVL(MAX("+str_ord_name+"),0)+1 AS ord		");
			sql.append("\n").append(" FROM BCDBA."+str_table_name+str_where+" 				");
						
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
				
			rs = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			int ret = 0;
			while (rs.next()) {
				
				str_ord = rs.getString("ord");
			
			}
			
			if(str_ord == null || str_ord.equals("")) str_ord = "0";
		
			//insert 
			StringBuffer sql_insert = new StringBuffer();	
			sql_insert.append("\n").append(" insert into BCDBA."+str_table_name+" ( ");
			sql_insert.append("\n").append(" "+str_idx_col_name+","+str_col_name+" ");
			
			if("m2".equals(mode)) 
			{
				sql_insert.append("\n").append(" , LINK_URL "); 	
			}
			if("m1".equals(mode) || "m2".equals(mode)) 
			{
				sql_insert.append("\n").append(" , "+str_pidx_col_name+ " "); 	
			}
			sql_insert.append("\n").append(" , "+str_ord_name+" ) ");	
				
			sql_insert.append("\n").append(" values ( ? ,?");			

			if("m2".equals(mode)) 
			{
				sql_insert.append("\n").append(" ,? ");	
			}
			if("m1".equals(mode) || "m2".equals(mode)) 
			{
				sql_insert.append("\n").append(" ,? ");		
			}
			sql_insert.append("\n").append(" ,? ) ");
			pstmt_in = con.prepareStatement(sql_insert.toString());	
		
			int spidx = 0;
			pstmt_in.setInt(++spidx, Integer.parseInt(idx));
			pstmt_in.setString(++spidx, str_name);
			if("m2".equals(mode)) 
			{
			pstmt_in.setString(++spidx, str_url);
			}
			if("m1".equals(mode) || "m2".equals(mode)) 
			{
			pstmt_in.setInt(++spidx, Integer.parseInt(pidx));
			}
			pstmt_in.setString(++spidx, str_ord);

			int res = pstmt_in.executeUpdate();
			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			

			//debug("==== GolfAdmMenuProcRegDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmMenuProcRegDaoProc ERROR ===");
			
			//debug("==== GolfAdmMenuProcRegDaoProc ERROR ===");
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(pstmt_in != null) pstmt_in.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}	
}
