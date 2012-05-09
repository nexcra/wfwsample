/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointAdmlnqDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 탑포인트 관리자 상단 메뉴 가져오기 
*   적용범위  : Golf
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

public class GolfAdmlnqDaoProc extends AbstractProc {
	
	public static final String TITLE = "비씨골프 관리자  상단메뉴 가져오기";
	/** *****************************************************************
	 * GolfPointAdmLoginlnqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmlnqDaoProc() { 

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
			//debug("GolfPointAdmlnqDaoProc start ");
			String account		= data.getString("account");	// 세션아이디
			String log_p_idx	= data.getString("log_p_idx");	// PK
			
			StringBuffer sql = new StringBuffer();

			sql.append("\n").append(" select t0.SQ1_LEV_SEQ_NO as m0_idx, max(t0.SQ1_LEV_MENU_NM) as m0_name ");
			sql.append("\n").append(" from BCDBA.TBGSQ1MENUINFO t0 inner join BCDBA.TBGSQ2MENUINFO t1 on t0.SQ1_LEV_SEQ_NO = t1.SQ1_LEV_SEQ_NO ");
			sql.append("\n").append(" inner join BCDBA.TBGSQ3MENUINFO t2 on t1.SQ2_LEV_SEQ_NO = t2.SQ2_LEV_SEQ_NO inner join BCDBA.TBGSQ3MENUMGRPRIVFIXN t3 on t2.SQ3_LEV_SEQ_NO= t3.SQ3_LEV_SEQ_NO ");
			sql.append("\n").append(" where t3.MGR_ID = ? ");
			sql.append("\n").append(" group by t0.SQ1_LEV_SEQ_NO ");

			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	

			int idx = 0; 		
			pstmt.setString(++idx, account);
			
			rs = pstmt.executeQuery();

			int ret = 0;
			if(rs != null )
			{
				while(rs.next())  {
			
				result.addString("m0_idx",		rs.getString("m0_idx"));
				result.addString("m0_name", 	rs.getString("m0_name"));
								
				ret++;
				}
			}
			  
			
			
			//마지막접속날짜				  
			  StringBuffer sql_a = new StringBuffer();
			  sql_a.append("\n SELECT RC_CONN_DATE, RC_CONN_TIME  from BCDBA.TBGMGRINFO  ");
			  sql_a.append("\n WHERE MGR_ID = ?	");		
			  pstmt = con.prepareStatement(sql_a.toString());				
			  pstmt.setString(1, account);  
			  rs = pstmt.executeQuery();
			  if(rs != null) {	
				  while(rs.next())  {
				  result.addString("VISIT_DATE"       	, rs.getString("RC_CONN_DATE"));
				  result.addString("VISIT_TIME"       	, rs.getString("RC_CONN_TIME"));
				  }
			  }
			
			
			
			
			
			if(ret == 0){
				result.addString("RESULT","01");
				
			} else {
				result.addString("RESULT","00");	
			}
			
			//debug("GolfPointAdmlnqDaoProc end");

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
