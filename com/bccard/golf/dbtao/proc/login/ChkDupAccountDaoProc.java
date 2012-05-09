/**********************************************************************************************************************
*   Ŭ������  : ChkDupAccountDaoProc
*   �ۼ���    : ���̼� (ThisLine)
*   ����      : ����� �α��� ���� Entity
*   �������  : BC ���ī�� ���ͳݼ��� �ý���
*   �ۼ�����  : 2003.09.01
************************** �����̷� ***********************************************************************************
*    ����      ����   �ۼ���   �������
*
**********************************************************************************************************************/
package com.bccard.golf.dbtao.proc.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

import com.bccard.waf.core.RequestParser;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.AbstractEntity;
import com.bccard.waf.core.WaContext;

/******************************************************************************
 * User Login Handling Database Access Process
 * @version   2003.09.01
 * @chkDupAccountor    <A href="mailto:thisline@e4net.net">yiseon baek</A>
 *****************************************************************************/

/******************************************************************************
* Golf : ChkDupAccountDaoProc
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
 


public class ChkDupAccountDaoProc extends AbstractProc {
	/** ***********************************************************************
	 * getDuplicateId
	 * @param WaContext
	 * @param RequestParser
	 * @return  returnCode
	 *********************************************************************** */
	public String getDuplicateId(WaContext context, RequestParser parser) {
		String returnCode = "N";
		String account = (String)parser.getParameter("account");

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = null;

		try{
			sql = "SELECT to_char(count(*)) as cnt FROM BCDBA.TBLTAXUNIFMGRINFO WHERE MGR_ID = ? ";

			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1,account);
			rs = pstmt.executeQuery();

			while(rs.next())  {
			 	returnCode = rs.getString("cnt");
			 }
			 
			 // ī��Ʈ�� 0 �̻��̸�  
			 if ("0".equals(returnCode) ) {
			 	returnCode = "N";	
			 }else{ // �ڷᰡ ������ 
			 	returnCode = "Y";
			 }
			 
		}catch(Throwable t){
			error(t.getMessage());
			returnCode = "Err";
		}finally{
			if(rs != null) { try {rs.close();} catch (Throwable e){}}
			if(pstmt != null){ try{ pstmt.close();}catch(Throwable e){}}
			if(conn != null){ try{conn.close();}catch(Throwable ex1){}}
		}
		return returnCode;
	}

}
