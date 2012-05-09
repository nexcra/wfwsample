/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtScoreUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 마이티박스 > 골프정보 > 스코어 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.user.entity.UcusrinfoEntity;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMtInfoUpdDaoProc extends AbstractProc {

	public static final String TITLE = "스코어 수정 처리";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMtInfoUpdDaoProc() {}
	
	/**
	 * 관리자 레슨프로그램 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			// 01.세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memID = userEtt.getAccount();
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String email_RECP_YN = data.getString("EMAIL_RECP_YN");
			String sms_RECP_YN = data.getString("SMS_RECP_YN");
            
			String sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);

			int idx = 0;
        	pstmt.setString(++idx, email_RECP_YN ); 
        	pstmt.setString(++idx, sms_RECP_YN ); 
        	pstmt.setString(++idx, memID ); 
        	
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
        	
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
									
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
    	
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  	UPDATE BCDBA.TBGGOLFCDHD SET			\n");
		sql.append("\t  	EMAIL_RECP_YN=?, SMS_RECP_YN=?			\n");
		sql.append("\t  	WHERE CDHD_ID=?							\n");
        return sql.toString();
    }
    
}
