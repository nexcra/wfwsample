/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBcJoinMutiUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 BC GOLF 이벤트 당첨 처리
*   적용범위  : golf
*   작성일자  : 2009-05-27
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBcJoinMutiUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 BC GOLF 이벤트 당첨 처리";

	/** *****************************************************************
	 * GolfAdmEvntBcJoinMutiUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBcJoinMutiUpdDaoProc() {}
	
	/**
	 * 관리자 BC GOLF 이벤트 당첨 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] recv_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int iCount = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);

			sql = this.getUpdateQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < recv_no.length; i++) {				
				if (recv_no[i] != null && recv_no[i].length() > 0) {
					pstmt.setString(1, data.getString("ADMIN_NO") );
					pstmt.setString(2, recv_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}
			
			if(iCount == recv_no.length) {
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return iCount;
	} 
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getUpdateQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("UPDATE BCDBA.TBGAPLCMGMT SET	\n");
 		sql.append("\t  PRZ_WIN_YN='Y', CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS') 	\n");
 		sql.append("\t WHERE APLC_SEQ_NO=?	\n");
 		sql.append("\t AND GOLF_SVC_APLC_CLSS='0009' 	\n");
         return sql.toString();
     }
}
