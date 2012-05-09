/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : golfAdmMenuMoveChgDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �޴� ���� ó��
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
public class GolfAdmMenuMoveChgDaoProc extends AbstractProc { 
	public static final String TITLE = "������  �޴� �̵� ó��";
	
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
		
		
		//debug("==== golfAdmMenuChgDaoProc start ===");
		
		try{
			// WHERE�� ��ȸ ����
			String str_t_name		= dataSet.getString("str_t_name"); 		//���̺��̸�
			String tmp_mode			= dataSet.getString("mode"); 	//�÷��̸�
			String idx					= dataSet.getString("idx"); 
			String mode					= dataSet.getString("mode"); 	
			String state						= dataSet.getString("state"); 
			String pidx			 	= "";
			String str_ord 			= "";
			String str_max_ord = "";
			
			String msg				= "";
			String proc = "true";
			
			StringBuffer sql = new StringBuffer();	
			con = context.getDbConnection("default", null);
			result = new DbTaoResult(TITLE);
			int res = 0;
			int spidx = 0;

			// -1. ���� IDX ���ϱ�.
			if(!"SQ1".equals(mode))
			{
				sql.append("\n").append(" select "+tmp_mode+"_LEV_SEQ_NO from BCDBA. "+str_t_name+" ");
				sql.append("\n").append(" where "+mode+"_LEV_SEQ_NO = ? ");
				pstmt = con.prepareStatement(sql.toString());	
				
				spidx = 0;
				pstmt.setString(++spidx, idx);
				
				rs = pstmt.executeQuery();
				
				
				if(rs != null && rs.next()) {
										
					pidx = rs.getString(tmp_mode+"_LEV_SEQ_NO");					

				}
			} else {
				if("UP".equals(state)){
					sql.append("\n").append(" select SQ1_LEV_SEQ_NO from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where EPS_SEQ = (");
					sql.append("\n").append(" select max(EPS_SEQ) as EPS_SEQ from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where EPS_SEQ<(");
					sql.append("\n").append(" select EPS_SEQ from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where SQ1_LEV_SEQ_NO = ?");
					sql.append("\n").append(" ))");
				}else{
					sql.append("\n").append(" select SQ1_LEV_SEQ_NO from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where EPS_SEQ = (");
					sql.append("\n").append(" select min(EPS_SEQ) as EPS_SEQ from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where EPS_SEQ>(");
					sql.append("\n").append(" select EPS_SEQ from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where SQ1_LEV_SEQ_NO = ?");
					sql.append("\n").append(" ))");
				}
				pstmt = con.prepareStatement(sql.toString());	
				
				spidx = 0;
				pstmt.setString(++spidx, idx);
				
				rs = pstmt.executeQuery();
				
				if(rs != null && rs.next()) {
					pidx = rs.getString(tmp_mode+"_LEV_SEQ_NO");
				}
				
			}

			// 0. ���� idx �� ���� ��ġ�� �����´�.
			StringBuffer sql_2 = new StringBuffer();	
			sql_2.append("\n").append(" select EPS_SEQ from BCDBA."+str_t_name+ " ");
			sql_2.append("\n").append(" where "+mode+"_LEV_SEQ_NO= ? ");
			pstmt = con.prepareStatement(sql_2.toString());	
			spidx = 0;
			pstmt.setString(++spidx, idx);		
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()) {
				str_ord = rs.getString("EPS_SEQ");						
			}
			
			// 1. �ְ� ���� ord ���� �����´�.
			StringBuffer sql_3 = new StringBuffer();	
			sql_3.append("\n").append(" select NVL(MAX(EPS_SEQ),0) as ord from BCDBA."+str_t_name+ " ");
			if(!"".equals(pidx))
			{
			sql_3.append("\n").append(" where "+tmp_mode+"_LEV_SEQ_NO = ? ");
			}
			
			pstmt = con.prepareStatement(sql_3.toString());	
			spidx = 0;
			if(!"".equals(pidx))
			{
			pstmt.setString(++spidx, pidx);	
			}
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()) {
				str_max_ord = rs.getString("ord");							
			}
	

			if(str_ord.equals("") || str_max_ord.equals("")){
				msg = "�߸��� ������ �Դϴ�.";
				proc = "false";				
				result.addString("RESULT", "01");
				result.addString("msg", msg);
				result.addString("proc", proc);
			}
			
			if(state.equals("UP") && str_ord.equals("1")){
				
				msg = "���� ��ġ�� ���� �ֻ��� �Դϴ�.\\n \\n���̻� ������ �ø� �� �����ϴ�.";
				proc = "false";
				result.addString("RESULT", "01");
				result.addString("msg", msg);
				result.addString("proc", proc);
			}
			
			if(state.equals("DOWN") && str_ord.equals(str_max_ord)){
				msg = "���� ��ġ�� ���� ������ �Դϴ�.\\n \\n���̻� ������ ���� �� �����ϴ�.";
				proc = "false";
				result.addString("RESULT", "01");
				result.addString("msg", msg);
				result.addString("proc", proc);
				
			}
			
			if("true".equals(proc)){
				
				if(state.equals("UP")){					

					// 2. idx �� ���� ord -1�� �ش� idx�� ord ���� +1 �Ѵ�.		
					StringBuffer sql_4 = new StringBuffer();	
					sql_4.append("\n").append(" update BCDBA."+str_t_name+" set  ");
					sql_4.append("\n").append(" EPS_SEQ = ?  ");
					sql_4.append("\n").append(" Where EPS_SEQ = ( "+str_ord+" - 1  )  ");
					if(!"M0".equals(mode)){
						sql_4.append("\n").append(" AND "+tmp_mode+"_LEV_SEQ_NO = ?  ");
					}
					pstmt = con.prepareStatement(sql_4.toString());	
					spidx = 0;
					pstmt.setString(++spidx, str_ord);				
					if(!"M0".equals(mode)){
					pstmt.setString(++spidx, pidx);
					}
					pstmt.executeUpdate(); 

					// 3. idx �� ���� ord�� -1 �Ѵ�.
					StringBuffer sql_5 = new StringBuffer();	
					sql_5.append("\n").append(" update BCDBA."+str_t_name+" set  ");
					sql_5.append("\n").append(" EPS_SEQ =  ( EPS_SEQ - 1 ) ");
					sql_5.append("\n").append(" Where "+mode+"_LEV_SEQ_NO = ?  ");
					pstmt = con.prepareStatement(sql_5.toString());	
					spidx = 0;
					pstmt.setString(++spidx, idx);
					res = pstmt.executeUpdate();
				
				}
				else if(state.equals("DOWN")){			
					
					// 2. idx �� ���� ord +1�� �ش� idx�� ord ���� -1 �Ѵ�.
					StringBuffer sql_6 = new StringBuffer();	
					sql_6.append("\n").append(" update BCDBA."+str_t_name+" set  ");
					sql_6.append("\n").append(" EPS_SEQ = ?  ");
					sql_6.append("\n").append(" Where EPS_SEQ = ( "+str_ord+" + 1  )  ");
					if(!"M0".equals(mode)){
						sql_6.append("\n").append(" AND "+tmp_mode+"_LEV_SEQ_NO = ?  ");
					}
					pstmt = con.prepareStatement(sql_6.toString());	
					spidx = 0;
					pstmt.setString(++spidx, str_ord);
					
					if(!"M0".equals(mode)){
					pstmt.setString(++spidx, pidx);
					}
					pstmt.executeUpdate();
					
					// 3. idx �� ���� ord�� -1 �Ѵ�.
					StringBuffer sql_7 = new StringBuffer();	
					sql_7.append("\n").append(" update BCDBA."+str_t_name+" set  ");
					sql_7.append("\n").append(" EPS_SEQ =  ( EPS_SEQ + 1 ) ");
					sql_7.append("\n").append(" Where "+mode+"_LEV_SEQ_NO = ?  ");
					pstmt = con.prepareStatement(sql_7.toString());	
					spidx = 0;
					pstmt.setString(++spidx, idx);
					res = pstmt.executeUpdate();
				
				}
				proc = "true";
				msg = "���� �Ǿ����ϴ�.";
				result.addString("msg", msg);
				result.addString("proc", proc);
			}
	
			//debug("proc:"+proc);
			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			

			//debug("==== golfAdmMenuChgDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== golfAdmMenuChgDaoProc ERROR ===");
			
			//debug("==== golfAdmMenuChgDaoProc ERROR ===");
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}	

}
