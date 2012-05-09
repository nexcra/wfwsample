/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeRsViewDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 결과 확인 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	미디어포스 
 * @version	1.0
 ******************************************************************************/
public class GolfMemChgFormDaoProc extends AbstractProc {
	
	public static final String TITLE = "부킹 결과 확인 처리";
	
	/** *****************************************************************
	 * GolfBkPreTimeRsViewDaoProc 부킹 결과 확인 처리
	 * @param N/A
	 ***************************************************************** */
	public GolfMemChgFormDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public String execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws DbTaoException  {

		String result = "";
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String userId = "";	

		try {
			conn = context.getDbConnection("default", null);

			// 01.세션정보체크
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
		 	
			 if(usrEntity != null) {
				userId		= (String)usrEntity.getAccount(); 
			}
			
            // 02. 멤버클래스 확인
			String sql = this.getMemberClssQuery(); 
            
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, userId );
            rs = pstmt.executeQuery();	
            
			if(rs.next()){
				result = rs.getString("MEMBER_CLSS");
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
                        

		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}	
	

    
	/** ***********************************************************************
	 * 회원여부를 리턴한다.    
	 ************************************************************************ */
	private String getMemberClssQuery(){
		StringBuffer sql = new StringBuffer();
		
		sql.append("	SELECT MEMBER_CLSS FROM BCDBA.UCUSRINFO WHERE ACCOUNT=?	\n");
		
		return sql.toString();
	}
 
}
