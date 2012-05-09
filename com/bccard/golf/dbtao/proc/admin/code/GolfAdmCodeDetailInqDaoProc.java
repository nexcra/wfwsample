/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCodeDetailInqDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �����ڵ���� ��� ��ȸ 
*   �������  : Golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.code;

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

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-03-31
 **************************************************************************** */
public class GolfAdmCodeDetailInqDaoProc extends AbstractProc {
	
	public static final String TITLE = "�����ڵ� ����  ��ȸ";
	
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
		
		//debug("==== GolfAdmCodeDetailInqDaoProc start ===");
		
		try{
			//��ȸ ����

			String p_idx			= dataSet.getString("p_idx");
			String s_idx			= dataSet.getString("s_idx"); 
			String sql = this.getSelectQuery(p_idx, s_idx);
			
			int pidx = 0;
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			pidx = 0;
			pstmt.setString(++pidx, p_idx);
			pstmt.setString(++pidx, s_idx);
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
							
				result.addString("CD_CLSS",				rset.getString("GOLF_CMMN_CLSS"));
				result.addString("CD",			rset.getString("GOLF_CMMN_CODE"));
				result.addString("CD_NM",			rset.getString("GOLF_CMMN_CODE_NM"));
				result.addString("CD_DESC",			rset.getString("EXPL"));
				result.addString("USE_YN",				rset.getString("USE_YN"));
				result.addString("REG_MGR_SEQ_NO",			rset.getString("REG_MGR_ID"));
				result.addString("CORR_MGR_SEQ_NO",			rset.getString("CHNG_MGR_ID"));
				result.addString("REG_DATE",			rset.getString("REG_ATON"));
				result.addString("CORR_DATE",			rset.getString("CHNG_ATON"));
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfAdmCodeDetailInqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmCodeDetailInqDaoProc ERROR ===");
			
			//debug("==== GolfAdmCodeDetailInqDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery(String p_idx, String s_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 			SELECT								");
		sql.append("\n 				GOLF_CMMN_CLSS,						");
		sql.append("\n 				GOLF_CMMN_CODE,					");
		sql.append("\n 				GOLF_CMMN_CODE_NM,						");
		sql.append("\n 				EXPL,						");
		sql.append("\n 				USE_YN,						");
		sql.append("\n 				(select MGR_ID from BCDBA.TBGMGRINFO where MGR_ID=TB.REG_MGR_ID ) as REG_MGR_ID,				");
		sql.append("\n 				(select MGR_ID from BCDBA.TBGMGRINFO where MGR_ID=TB.CHNG_MGR_ID ) as CHNG_MGR_ID,				");
		sql.append("\n 				to_char(to_date(REG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS REG_ATON,						");
		sql.append("\n 				to_char(to_date(CHNG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS CHNG_ATON						");
		
		sql.append("\n 			FROM BCDBA.TBGCMMNCODE	 TB		");
		sql.append("\n 			WHERE GOLF_CMMN_CLSS = ?							");
		sql.append("\n 			AND GOLF_CMMN_CODE = ?							");


		return sql.toString();
	}
}
