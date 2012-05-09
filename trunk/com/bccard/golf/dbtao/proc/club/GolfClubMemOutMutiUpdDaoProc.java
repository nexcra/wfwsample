/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfClubMemOutMutiUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 회원 강퇴 처리
*   적용범위  : golf
*   작성일자  : 2009-07-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.club;

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
public class GolfClubMemOutMutiUpdDaoProc extends AbstractProc {

	public static final String TITLE = "동호회 회원 강퇴 처리";

	/** *****************************************************************
	 * GolfClubMemOutMutiUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMemOutMutiUpdDaoProc() {}
	
	/**
	 * 동호회 회원 강퇴 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] club_cdhd_seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//조회 ----------------------------------------------------------
		
			String sql = this.getDeleteQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < club_cdhd_seq_no.length; i++) {				
				if (club_cdhd_seq_no[i] != null && club_cdhd_seq_no[i].length() > 0) {
					pstmt.setString(1, data.getString("CLUB_SEQ_NO") ); 
					pstmt.setString(2, club_cdhd_seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}

			if(iCount == club_cdhd_seq_no.length) {
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

		return iCount;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("UPDATE BCDBA.TBGCLUBCDHDMGMT SET	\n");
		sql.append("\t  SECE_YN='E',  SECE_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS')  	\n");
		sql.append("\t  WHERE CLUB_SEQ_NO = ?	\n");
		sql.append("\t  AND CLUB_CDHD_SEQ_NO = ?	\n");
        return sql.toString();
    }
}