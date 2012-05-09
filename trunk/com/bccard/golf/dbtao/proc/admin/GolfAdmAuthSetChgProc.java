/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthSetChgProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ���� ���� 
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
public class GolfAdmAuthSetChgProc extends AbstractProc {

	public static final String TITLE = "������  ���� ����";
	
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
		
		
		//debug("==== GolfAdmAuthSetChgProc start ===");
		
		try{
			//��ȸ ����
			String p_idx	= dataSet.getString("p_idx"); 		//PK
					
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	SQ3_LEV_SEQ_NO, MGR_PRIV_CLSS 			");
			sql.append("\n FROM	BCDBA.TBGSQ3MENUMGRPRIVFIXN						");
			sql.append("\n WHERE MGR_ID = ?						    	");
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			
			int idx = 0; 		
			pstmt.setString(++idx, p_idx);
			
			rs = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			int ret = 0;
			while(rs.next())  {
				
				result.addString("M2_SEQ_NO",				rs.getString("SQ3_LEV_SEQ_NO"));
				result.addString("AUTH_CLSS",				rs.getString("MGR_PRIV_CLSS"));
				
				ret++;
				
			}
			if(ret == 0){
				result.addString("RESULT","01");
			
			} else {
				result.addString("RESULT","00");
			
			}
					
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmAuthSetChgProc ERROR ===");
			
			//debug("==== GolfAdmAuthSetChgProc ERROR ===");
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}
}