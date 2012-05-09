/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : TpAdmLoginlnqDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 탑포인트 관리자 로그인 로직
*   적용범위  : Topn
*   작성일자  : 2009-05-06 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항 
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;

public class GolfAdmLoginlnqTestDaoProc extends AbstractProc {

	public static final String TITLE = "비씨골프 관리자 조회";
	/** *****************************************************************
	 * TpAdmLoginlnqDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLoginlnqTestDaoProc() { 

	}

	/** ***********************************************************************
	* Proc 실행. 
	* @param WaContext context
	* @param HttpServletRequest request
	* @param DbTaoDataSet data
	* @return DbTaoResult	result 
	************************************************************************ */
	public TaoResult execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		DbTaoResult result = null;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {				
	
			result = new DbTaoResult(TITLE);
			debug("GolfAdmLoginlnqDaoProc start ");
			String account	= data.getString("account");	// 입력한 관리자 아이디
			
			StringBuffer sql = new StringBuffer();
			sql.append("\n").append("SELECT");
			sql.append("\n").append(" MGR_ID as ID");
			sql.append("\n").append(", HG_NM");
			sql.append("\n").append(", HASH_PASWD");
			sql.append("\n").append(", PASWD");
			sql.append("\n").append(" FROM BCDBA.TBGMGRINFO");
			sql.append("\n").append(" WHERE ");
			sql.append("\n").append(" MGR_ID = ? ");
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			
			int idx = 0; 		
			pstmt.setString(++idx, account);
			
			rs = pstmt.executeQuery();
			
			int ret = 0;
			if(rs != null && rs.next()) {
				result.addString("ACCOUNT", 	rs.getString("ID"));
				result.addString("NAME",		rs.getString("HG_NM"));
				result.addObject("HASH_PASWD",	rs.getBytes("HASH_PASWD"));
				result.addObject("PASWD",	rs.getString("PASWD"));
				
				
				ret++;
			}
			if(ret == 0){
				result.addString("RESULT","01");
				
			} else {
				result.addString("RESULT","00");
				
			}
						
			//debug("TpAdmLoginlnqDaoProc end ");
        } catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}



}
