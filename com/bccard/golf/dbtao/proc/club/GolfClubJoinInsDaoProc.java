/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfClubJoinInsDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 가입 처리
*   적용범위  : golf
*   작성일자  : 2009-07-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfClubJoinInsDaoProc extends AbstractProc {

	public static final String TITLE = "동호회 가입 처리";

	/** *****************************************************************
	 * GolfClubJoinInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubJoinInsDaoProc() {}
	
	/**
	 * 동호회 가입 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

			sql = this.getNextValQuery(); //일련번호
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String club_cdhd_seq_no = "";
			if(rs.next()){
				club_cdhd_seq_no = rs.getString("CLUB_CDHD_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, club_cdhd_seq_no );
			pstmt.setString(++idx, data.getString("CLUB_SEQ_NO") );
			pstmt.setString(++idx, data.getString("CDHD_ID") ); 
			pstmt.setString(++idx, data.getString("CDHD_NM") );
			pstmt.setString(++idx, data.getString("GREET_CTNT") );
			pstmt.setString(++idx, data.getString("JONN_YN") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			
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
		sql.append("INSERT INTO BCDBA.TBGCLUBCDHDMGMT		 (	\n");
		sql.append("\t  CLUB_CDHD_SEQ_NO, CLUB_SEQ_NO, CDHD_ID, CDHD_NM, GREET_CTNT, JONN_YN, SECE_YN, APLC_ATON, JONN_ATON, CHNG_ATON,    	\n");
		sql.append("\t	SECE_ATON 	\n");
		sql.append("\t ) VALUES (	\n");
		sql.append("\t  ?,?,?,?,?,?,'N',TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),NULL,NULL,	\n");
		sql.append("\t  NULL	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(CLUB_CDHD_SEQ_NO),0)+1 CLUB_CDHD_SEQ_NO FROM BCDBA.TBGCLUBCDHDMGMT		 \n");
		return sql.toString();
    }
}