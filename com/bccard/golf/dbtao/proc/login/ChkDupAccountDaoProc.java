/**********************************************************************************************************************
*   클래스명  : ChkDupAccountDaoProc
*   작성자    : 백이선 (ThisLine)
*   내용      : 사용자 로그인 조건 Entity
*   적용범위  : BC 기업카드 인터넷서비스 시스템
*   작성일자  : 2003.09.01
************************** 수정이력 ***********************************************************************************
*    일자      버전   작성자   변경사항
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
* @author	(주)미디어포스
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
			 
			 // 카운트가 0 이상이면  
			 if ("0".equals(returnCode) ) {
			 	returnCode = "N";	
			 }else{ // 자료가 있으면 
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
