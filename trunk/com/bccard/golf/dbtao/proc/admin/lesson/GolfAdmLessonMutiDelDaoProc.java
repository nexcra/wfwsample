/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLessonMutiDelDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨프로그램 다중 삭제 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lesson;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmLessonMutiDelDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 레슨프로그램 다중 삭제 처리";

	/** *****************************************************************
	 * GolfAdmLessonMutiDelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLessonMutiDelDaoProc() {}
	
	/**
	 * 관리자 레슨프로그램 다중 삭제 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] lsn_seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		int iCount2 = 0;
		int iCount3 = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//조회 ----------------------------------------------------------
		
			String sql = this.getDeleteQuery();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < lsn_seq_no.length; i++) {				
				if (lsn_seq_no[i] != null && lsn_seq_no[i].length() > 0) {
					pstmt.setString(1, lsn_seq_no[i]); 

					iCount += pstmt.executeUpdate();
				}
			}			
		    /** ***********************************************************************/	

//			sql = this.getDeleteQuery2();			
//			pstmt = conn.prepareStatement(sql);
//			for (int i = 0; i < lsn_seq_no.length; i++) {				
//				if (lsn_seq_no[i] != null && lsn_seq_no[i].length() > 0) {
//					pstmt.setString(1, lsn_seq_no[i]); 
//
//					iCount2 += pstmt.executeUpdate();
//				}
//			}	
		    /** ***********************************************************************/	

			//getDeleteQuery2 지원가 없을 경우 삭제 결과가 0이 되므로 체크하지 않음.
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
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGLESNMGMT 	\n");
		sql.append("\t  WHERE LESN_SEQ_NO = ?	\n");
        return sql.toString();
    }
    /*
    private String getDeleteQuery2(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGAPLCMGMT 	\n");
		sql.append("\t  WHERE LESN_SEQ_NO = ?	\n");
		sql.append("\t  AND GOLF_SVC_APLC_CLSS = '0001' \n");
        return sql.toString();
    }
    */
}
