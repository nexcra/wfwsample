/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthPassUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��й�ȣ ���� ó��
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

import com.initech.dbprotector.CipherClient; //DB ��ȣȭ ����
import com.bccard.golf.common.BcLog;
import com.bccard.golf.common.JoinConEtt;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0 
******************************************************************************/
public class GolfAdmAuthPassUpdDaoProc extends AbstractProc {
	
	public static final String TITLE = "������  ��й�ȣ  ���� ó��";
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

		
		try{
			JoinConEtt cEtt = new JoinConEtt();
			
			// WHERE�� ��ȸ ����
			String p_passwd		= dataSet.getString("p_passwd"); 		//��й�ȣ
			String p_idx		= dataSet.getString("p_idx"); 
			
			
			
			
			//UPDATE 
			StringBuffer sql = new StringBuffer();	
			sql.append("\n").append(" update TBGMGRINFO		 set ");
			//sql.append("\n").append(" HASH_PASWD = ? , ");	
			sql.append("\n").append(" PASWD = ? ");			
			sql.append("\n").append(" where MGR_ID = ? ");			
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
		
			int spidx = 0;

			/*//�Ǽ��� ���� ����
			cEtt.setLogin_passwd(p_passwd.trim()); 			
			String hashpass = cEtt.getLogin_passwd();
			byte[] encData = CipherClient.encrypt(CipherClient.MASTERKEY1,hashpass.getBytes());
			pstmt.setBytes(++spidx, encData);
			*/
			
			
			pstmt.setString(++spidx, p_passwd);
			pstmt.setString(++spidx, p_idx);			
									
			int res = pstmt.executeUpdate();
			
			result = new DbTaoResult(TITLE);
			
			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			
		}catch ( Exception e ) {
			
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}	
}
