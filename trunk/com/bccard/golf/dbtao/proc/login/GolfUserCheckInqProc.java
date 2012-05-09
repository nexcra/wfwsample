/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLoginProc
*   작성자     : (주)미디어포스 조은미
*   내용        : 개인사용자 조회
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfUserCheckInqProc extends AbstractProc {
	
	private static final String TITLE = "개인 사용자 조회";
	
	/** *****************************************************************
	 * GolfUserCheckInqProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfUserCheckInqProc() {
	    
	
	}
	  

	/**
	 * Proc 실행.
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
					result.addString("MEM_CLSS",		"1");					// 사용자구분 1=개인, 2=법인

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

