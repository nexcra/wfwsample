/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPreTimeChgDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹프리미엄 티타임 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfadmPreTimeChgDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 부킹프리미엄 티타임 수정 처리";

	/** *****************************************************************
	 * admPreTimeChgDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPreTimeChgDaoProc() {}
	
	/**
	 * 관리자 부킹프리미엄 티타임 수정 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException 
	 */
	public int execute(WaContext context, TaoDataSet data, HttpServletRequest request, String[] lsn_seq_no, String VIEW_YN) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		GolfAdminEtt userEtt = null;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			
			//조회 ----------------------------------------------------------		
			String sql = this.getChgQuery();	
			
			for (int i = 0; i < lsn_seq_no.length; i++) {				
				if (lsn_seq_no[i] != null && lsn_seq_no[i].length() > 0) {		
					
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, VIEW_YN);
					pstmt.setString(2, admId);
					pstmt.setString(3, lsn_seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}			
		    /** ***********************************************************************/	


			if(iCount == lsn_seq_no.length) {
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
    private String getChgQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT 	\n");
		sql.append("	SET EPS_YN = ?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t  WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	\n");
        return sql.toString();
    }
}
