/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMenuFormChgDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �޴� ���� ��
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
public class GolfAdmMenuFormChgDaoProc extends AbstractProc {
	public static final String TITLE = "������  �޴� ����  ��";
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result = null;
		Connection con = null;
		
		
		//debug("==== GolfAdmMenuFormChgDaoProc start ===");
		
		try{
			// WHERE�� ��ȸ ����
			String str_table_name		= dataSet.getString("str_table_name"); 		//���̺��̸�
			String str_idx_col_name		= dataSet.getString("str_idx_col_name"); 	//�÷��̸�
			String str_col_name			= dataSet.getString("str_col_name"); 	//�÷��̸�
			String mode					= dataSet.getString("mode");
			String idx					= dataSet.getString("idx");
			StringBuffer sql = new StringBuffer();		

			//SEQ �ִ밪 ���ϱ�
			sql.append("\n").append(" SELECT 							");
			sql.append("\n").append("  "+str_idx_col_name+" ,	 		");
			sql.append("\n").append("  "+str_col_name+" 		 		");
			if("m2".equals(mode))
			{
				sql.append("\n").append(" , LINK_URL ");
			}						
			sql.append("\n").append(" FROM BCDBA."+str_table_name+" 							");
			sql.append("\n").append(" WHERE "+str_idx_col_name+"= ? ");
						
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			int pidx = 0;
			pstmt.setInt(++pidx, Integer.parseInt(idx));
									
			rs = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			int ret = 0;
			while (rs.next()) {
				
				result.addString("str_name",			rs.getString(str_col_name));				
				if("m2".equals(mode))
				{
					result.addString("str_url",			rs.getString("LINK_URL"));
				}	
				ret++;				
			}
			if(ret == 0){
				result.addString("RESULT","01");
			
			} else {
				result.addString("RESULT","00");
			
			}
			

			//debug("==== GolfAdmMenuFormChgDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmMenuFormChgDaoProc ERROR ===");
			
			//debug("==== GolfAdmMenuFormChgDaoProc ERROR ===");
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

}
