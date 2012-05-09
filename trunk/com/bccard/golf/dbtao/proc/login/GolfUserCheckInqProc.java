/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfLoginProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ���λ���� ��ȸ
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
public class GolfUserCheckInqProc extends AbstractProc {
	
	private static final String TITLE = "���� ����� ��ȸ";
	
	/** *****************************************************************
	 * GolfUserCheckInqProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfUserCheckInqProc() {
	    
	
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
		
		debug("=====================>GolfUserCheckInqProc start");
		
				
		try {

			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT  MEMID			");
			sql.append("\n		,ACCOUNT			");
			sql.append("\n		,H_PASSWD			");
			sql.append("\n		,E_PASSWD			");
			sql.append("\n		,SOCID				");
			sql.append("\n		,NAME				");
			sql.append("\n		,PSWDERR_CNT        ");
			sql.append("\n FROM   BCDBA.UCUSRINFO	"); 
			
			sql.append("\n WHERE 1=1				"); 
			if("1".equals(data.getString("mode"))){
				sql.append("\n AND ACCOUNT = ?		");
			} else {
				sql.append("\n AND SOCID = ? ");
			}
			sql.append("\n  AND  PSWDERR_CNT < 5	"); 
		
		//debug("=====================>sql end!!!");
			
			pstmt = con.prepareStatement(sql.toString());
			 if("1".equals(data.getString("mode"))){
				pstmt.setString(1, data.getString("account")); 
			 } else {
				pstmt.setString(1, data.getString("juminNo")); 
			 }
		//debug("=====================>pstmt end!!!");
			
			 rs = pstmt.executeQuery();
		//debug("=====================>ResultSet end!!!");
			
			if(rs != null) {
		//debug("=====================>ResultSet is not null!!!");
				 while(rs.next())  {
					result.addString("MEMID",		rs.getString("MEMID"));
					result.addString("ACCOUNT" ,	rs.getString("ACCOUNT"));
					result.addObject("H_PASSWD",	rs.getBytes("H_PASSWD"));
					result.addObject("E_PASSWD",	rs.getBytes("E_PASSWD"));
					result.addString("JUMIN_NO",	rs.getString("SOCID"));
					result.addString("BUZ_NO",		"");
					result.addString("USER_NM",		rs.getString("NAME"));
					result.addString("MEM_CLSS",		"1");					// ����ڱ��� 1=����, 2=����

					result.addString("RESULT",		"00");
				 }	
			}
			 if(result.size() < 1) {
				result.addString("RESULT", "01");
			 }
			//-----------------------------------------------------------------
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try {if(rs !=null) { rs.close();} } catch (Exception ignored) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ignored) {}
		}
		debug("=====================>GolfUserCheckInqProc end");
		return result;
	}
 	
	
}

