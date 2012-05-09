/*****************************************************************
*	클래스명	:	GolfTaxUnifMgrUpdProc
*	작성자		:	choijaechul
*	내용		:	최근접속일자 시간 저장 하기 DB Proc 
*	적용범위	:	bccard
*	작성일자	:	2008.08.19 
* /BCWEB/WAS/bccorpext2/etax/src/com/bccard/etax/dbtao/proc/login/AdmTaxUnifMgrUpdProc.java
************************** 수정이력 *********************************
*	일자	버전		수정자		변경사항
*
******************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;

public class GolfAdmLogUpdProc extends AbstractProc { 
	public static final String TITLE = "관리자 최정접속 시간 수정";
	/** *****************************************************************
	 * AdmTaxUnifMgrUpdProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */ 
	public GolfAdmLogUpdProc() { 

	}
	
	/** ***********************************************************************
	* Proc 실행.
	* @param WaContext context
	* @param HttpServletRequest request
	* @param DbTaoDataSet data
	* @return DbTaoResult	result 
	************************************************************************ */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		//debug("AdmTaxUnifMgrUpdProc start");
		int result = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {				
			//info("AdmTaxUnifMgrUpdProc >> execute >> ");

			StringBuffer sql = new StringBuffer();
				
			sql.append(" UPDATE BCDBA.TBGMGRINFO  SET ");
			sql.append(" RC_CONN_DATE  = to_char(sysdate,'YYYYMMDD'), ");
			sql.append(" RC_CONN_TIME  = to_char(sysdate,'HH24MISS') ");
			sql.append(" WHERE MGR_ID = ? ");
				
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			
			int idx = 0; 		
			pstmt.setString(++idx, data.getString("account"));
			
			result = pstmt.executeUpdate();
		
			if(result > 0) {
				con.commit();
			} else {
				con.rollback();

			}

        } catch(Exception e) {
			try	{
				con.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}
		//debug("AdmTaxUnifMgrUpdProc end");
		return result;
	}
}
