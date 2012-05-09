/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardDetailInqDaoProc
*   �ۼ���     : (��)�̵������ �ǿ���
*   ����        : ������ �Խ��ǰ��� ��� ��ȸ 
*   �������  : Golf
*   �ۼ�����  : 2009-03-31
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.board;

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
public class GolfAdmBoardDetailInqDaoProc extends AbstractProc {
	
	public static final String TITLE = "�Խ��� ����  ��ȸ";
	
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
		
		//debug("==== GolfAdmBoardDetailInqDaoProc start ===");
		
		try{
			//��ȸ ����

			String p_idx			= dataSet.getString("p_idx"); 
			String sql = this.getSelectQuery(p_idx);
			
			int pidx = 0;
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			pidx = 0;
			pstmt.setString(++pidx, p_idx);
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
							
				result.addLong("BOARDID",				rset.getLong("BBRD_SEQ_NO"));
				result.addString("BOARD_CODE",			rset.getString("BBRD_CLSS"));
				result.addString("BOARD_NM",			rset.getString("BBRD_INFO_NM"));
				result.addString("USE_YN",				rset.getString("USE_YN"));
				result.addString("RG_SEQ_NO",			rset.getString("REG_MGR_SEQ_NO"));
				result.addString("UP_SEQ_NO",			rset.getString("CORR_MGR_SEQ_NO"));
				result.addString("REG_DATE",			rset.getString("REG_DATE"));
				result.addString("MOD_DATE",			rset.getString("CORR_DATE"));
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfAdmBoardDetailInqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardDetailInqDaoProc ERROR ===");
			
			//debug("==== GolfAdmBoardDetailInqDaoProc ERROR ===");
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
	private String getSelectQuery(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 			SELECT								");
		sql.append("\n 				BBRD_SEQ_NO,						");
		sql.append("\n 				BBRD_CLSS,					");
		sql.append("\n 				BBRD_INFO_NM,						");
		sql.append("\n 				USE_YN,						");
		sql.append("\n 				(select ID from TBMGRINFO where MGR_SEQ_NO=TB.REG_MGR_SEQ_NO ) as REG_MGR_SEQ_NO,				");
		sql.append("\n 				(select ID from TBMGRINFO where MGR_SEQ_NO=TB.CORR_MGR_SEQ_NO ) as CORR_MGR_SEQ_NO,				");
		sql.append("\n 				REG_DATE,						");
		sql.append("\n 				CORR_DATE						");
		
		sql.append("\n 			FROM BCDBA.TBBBRDMGMT	 TB		");
		sql.append("\n 			WHERE BBRD_SEQ_NO = ?							");


		return sql.toString();
	}
}
