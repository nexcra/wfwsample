/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkMutiDelDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 프리미엄 부킹 이벤트 다중 삭제 처리
*   적용범위  : golf
*   작성일자  : 2009-05-26
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event;

import java.sql.Connection;
import java.sql.PreparedStatement;

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
public class GolfAdmEvntBkMutiDelDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 프리미엄 부킹 이벤트 다중 삭제 처리";

	/** *****************************************************************
	 * GolfAdmEvntBkMutiDelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBkMutiDelDaoProc() {}
	
	/**
	 * 관리자 프리미엄 부킹 이벤트 다중 삭제 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] time_seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
            /*****************************************************************************/
		
			String sql = this.getDeleteQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < time_seq_no.length; i++) {				
				if (time_seq_no[i] != null && time_seq_no[i].length() > 0) {
					pstmt.setString(1, time_seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}
            /*****************************************************************************/

			sql = this.getUpdateQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < time_seq_no.length; i++) {				
				if (time_seq_no[i] != null && time_seq_no[i].length() > 0) {
					pstmt.setString(1, time_seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}
            /*****************************************************************************/
			
			if(iCount == (time_seq_no.length*2)) {
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
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGEVNTMGMT 	\n");
		sql.append("\t  WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	\n");
		sql.append("\t  AND EVNT_CLSS = '0002'	\n");
        return sql.toString();
    }
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getUpdateQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
 		sql.append("\t  EVNT_YN=NULL 	\n");
 		sql.append("\t WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?	\n");
         return sql.toString();
     }
}
