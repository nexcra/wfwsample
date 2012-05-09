/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLoginProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ����� ���� üũ
*   �������  : Golf
*   �ۼ�����  : 2009-05-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.login;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.dbtao.DbTaoResult;

/******************************************************************************
* Golf
* @author	(��)�̵������
* @version	1.0
******************************************************************************/
public class GolfCorpCheckInqProc extends AbstractProc {
	
	private static final String TITLE = "���� ����� ��ȸ";
	
	/** *****************************************************************
	 * GolfCorpCheckInqProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfCorpCheckInqProc() {
	}
	 
 
	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(Connection con, TaoDataSet data) throws BaseException {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		//debug("=====================>GolfCorpCheckInqProc start");
			
		try {
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT  MEM_ID			");
			sql.append("\n		,ACCOUNT			");
			sql.append("\n		,H_PASSWD			");
			sql.append("\n		,E_PASSWD			");
			sql.append("\n		,USER_JUMIN_NO		");
			sql.append("\n		,BUZ_NO				");
			sql.append("\n		,USER_NM			");
			sql.append("\n		,PSWD_ERR_CNT       ");
			sql.append("\n FROM   BCDBA.TBENTPUSER	"); 
			sql.append("\n WHERE 1=1				");
			 if("1".equals(data.getString("mode"))){
				sql.append("\n AND ACCOUNT = ?		");
			 } else {
				sql.append("\n AND CERTI_USER_DN = ? ");
			 }
			sql.append("\n  AND  LAST_DATA_YN = 'Y'	"); 
	    
			
			 pstmt = con.prepareStatement(sql.toString());
			 if("1".equals(data.getString("mode"))){
				pstmt.setString(1, data.getString("account")); 
			 } else {
				pstmt.setString(1, data.getString("userDn")); 
			 }
			 
			 
			 rs = pstmt.executeQuery();
			 while(rs.next())  {
			 	result.addString("MEMID",		rs.getString("MEM_ID"));
				result.addString("ACCOUNT",		rs.getString("ACCOUNT"));
				result.addObject("H_PASSWD",	rs.getBytes("H_PASSWD"));
				result.addObject("E_PASSWD",	rs.getBytes("E_PASSWD"));
				result.addString("JUMIN_NO",	rs.getString("USER_JUMIN_NO"));
				result.addString("BUZ_NO",		rs.getString("BUZ_NO"));
				result.addString("USER_NM",		rs.getString("USER_NM"));
				result.addString("MEM_CLSS",		"2");						// ����ڱ��� 1=����, 2=����

				result.addString("RESULT", "00");
			 }	
//debug("=============333333333333333 ");			 
//debug("=============result.size " + result.size() );
			 if(result.size() < 1) {
				result.addString("RESULT", "01");
			 }
			//-----------------------------------------------------------------
			
			 

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { rs.close(); } catch (Exception ignored) {}
            try { pstmt.close(); } catch (Exception ignored) {}
		}
		//debug("=====================>GolfCorpCheckInqProc end");
		return result;
	}
 	
	
}

