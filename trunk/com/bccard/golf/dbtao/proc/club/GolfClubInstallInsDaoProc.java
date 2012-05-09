/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfClubInstallInsDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 만들기 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-07-01
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
public class GolfClubInstallInsDaoProc extends AbstractProc {

	public static final String TITLE = "동호회 만들기 등록 처리";

	/** *****************************************************************
	 * GolfClubInstallInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubInstallInsDaoProc() {}
	
	/**
	 * 공통게시판 등록 처리
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
			String club_seq_no = "";
			if(rs.next()){
				club_seq_no = rs.getString("CLUB_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, club_seq_no );
			pstmt.setString(++idx, data.getString("GOLF_CLUB_CTGO") );
			pstmt.setString(++idx, data.getString("CLUB_NM") ); 
			pstmt.setString(++idx, data.getString("OPN_PE_ID") );
			pstmt.setString(++idx, data.getString("OPN_PE_NM") );
			pstmt.setString(++idx, data.getString("HP_DDD_NO") );
			pstmt.setString(++idx, data.getString("HP_TEL_HNO") );
			pstmt.setString(++idx, data.getString("HP_TEL_SNO") );
			pstmt.setString(++idx, data.getString("CLUB_SBJT_CTNT") );
			pstmt.setString(++idx, data.getString("CLUB_IMG") );

			pstmt.setString(++idx, data.getString("CLUB_INTD_CTNT") );
			pstmt.setString(++idx, data.getString("CLUB_OPN_PRPS_CTNT") );
			pstmt.setString(++idx, data.getString("CDHD_NUM_LIMT_YN") );
			pstmt.setString(++idx, data.getString("LIMT_CDHD_NUM") );
			pstmt.setString(++idx, data.getString("CLUB_JONN_MTHD_CLSS") );
			pstmt.setString(++idx, data.getString("CLUB_OPN_AUTH_YN") );
			
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
		sql.append("INSERT INTO BCDBA.TBGCLUBMGMT		 (	\n");
		sql.append("\t  CLUB_SEQ_NO, GOLF_CLUB_CTGO, CLUB_NM, OPN_PE_ID, OPN_PE_NM, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, CLUB_SBJT_CTNT, CLUB_IMG,   	\n");
		sql.append("\t	CLUB_INTD_CTNT, CLUB_OPN_PRPS_CTNT, CDHD_NUM_LIMT_YN, LIMT_CDHD_NUM, CLUB_JONN_MTHD_CLSS, CLUB_OPN_AUTH_YN, CLUB_ACT_YN, APLC_ATON, OPN_ATON, CHNG_MGR_ID, CHNG_ATON 	\n");
		sql.append("\t ) VALUES (	\n");
		sql.append("\t  ?,?,?,?,?,?,?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,?,?,'Y',TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),NULL,NULL,NULL	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(CLUB_SEQ_NO),0)+1 CLUB_SEQ_NO FROM BCDBA.TBGCLUBMGMT	 \n");
		return sql.toString();
    }
}