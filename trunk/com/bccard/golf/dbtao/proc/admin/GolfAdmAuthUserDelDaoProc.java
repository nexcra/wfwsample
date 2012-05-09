/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthUserDelDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ���� ó��
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

import com.bccard.golf.common.JoinConEtt;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.initech.dbprotector.CipherClient;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfAdmAuthUserDelDaoProc extends AbstractProc {
	
	public static final String TITLE = "������  ���� ó��";
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
		
		
		//debug("==== GolfAdmAuthUserDelDaoProc start ===");
		
		try{
			con = context.getDbConnection("default", null);
			
			// WHERE�� ��ȸ ����	
			String p_idx		= dataSet.getString("p_idx"); 
			
			//DELETE AUTH
			StringBuffer sql = new StringBuffer();	
			sql.append("\n").append(" delete from BCDBA.TBGSQ3MENUMGRPRIVFIXN  ");
			sql.append("\n").append(" where MGR_ID = ? ");			
			
			pstmt = con.prepareStatement(sql.toString());	
		
			int spidx = 0;
			pstmt.setString(++spidx, p_idx);			
									
			pstmt.executeUpdate();
			
			//DELETE ADMIN
			StringBuffer sql_ad = new StringBuffer();	
			sql_ad.append("\n").append(" delete from BCDBA.TBGMGRINFO  ");
			sql_ad.append("\n").append(" where MGR_ID = ? ");		
			pstmt = con.prepareStatement(sql_ad.toString());	
			spidx = 0;
			pstmt.setString(++spidx, p_idx);						
			int res = pstmt.executeUpdate();
			
			result = new DbTaoResult(TITLE);
			
			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			

			//debug("==== GolfAdmAuthUserDelDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmAuthUserDelDaoProc ERROR ===");
			
			//debug("==== GolfAdmAuthUserDelDaoProc ERROR ===");
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
	        throw new DbTaoException(msgEtt,e);			
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}	

}
