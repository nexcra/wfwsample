/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkTimeUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 부킹 티타임 이벤트 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event;

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
public class GolfAdmEvntBkTimeUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 부킹 티타임 이벤트 등록 처리";

	/** *****************************************************************
	 * GolfAdmEvntBkTimeUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBkTimeUpdDaoProc() {}
	
	/**
	 * 관리자 부킹 티타임 이벤트 등록 처리
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
            /*****************************************************************************/
			
			sql =  this.getNextValQuery(); //이벤트 쿼리
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String seq_no = "";
			if(rs.next()){
				seq_no = rs.getString("EVNT_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;

			pstmt.setString(++idx, seq_no );
			pstmt.setString(++idx, data.getString("TIME_SEQ_NO") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setString(++idx, data.getString("TIME_SEQ_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getUpdateQuery();//Update Query
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("TIME_SEQ_NO") );
			
			result = result + pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/            
			
			if(result > 1) {
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

		return result;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGEVNTMGMT (	\n");
		sql.append("\t  EVNT_SEQ_NO, EVNT_CLSS, BLTN_YN, RSVT_ABLE_BOKG_TIME_SEQ_NO, GREEN_NM, BOKG_ABLE_DATE, BOKG_ABLE_TIME, GOLF_RSVT_CURS_NM,	\n");
		sql.append("\t  INQR_NUM, REG_MGR_ID, REG_ATON 	\n");
		sql.append("\t ) (	\n");	
		sql.append("\t 	SELECT 	\n");	
		sql.append("\t  	?, '0002', 'N', ?, TBG.GREEN_NM, TBD.BOKG_ABLE_DATE, TBT.BOKG_ABLE_TIME, TBD.GOLF_RSVT_CURS_NM,	\n");
		sql.append("\t  	0, ?, TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS')	\n");
		sql.append("\t 	FROM  \n");
		sql.append("\t 	BCDBA.TBGRSVTABLEBOKGTIMEMGMT TBT, BCDBA.TBGRSVTABLESCDMGMT TBD, BCDBA.TBGAFFIGREEN TBG  \n");
		sql.append("\t 	WHERE TBT.RSVT_ABLE_SCD_SEQ_NO = TBD.RSVT_ABLE_SCD_SEQ_NO  \n");
		sql.append("\t 	AND TBD.AFFI_GREEN_SEQ_NO = TBG.AFFI_GREEN_SEQ_NO(+)  \n");
		sql.append("\t 	AND TBT.RSVT_ABLE_BOKG_TIME_SEQ_NO = ?  \n");
		sql.append("\t \n)");			
        return sql.toString();
    }
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getUpdateQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
 		sql.append("\t  EVNT_YN='Y' 	\n");
 		sql.append("\t WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=?	\n");
         return sql.toString();
     }
        
    /** ***********************************************************************
     * Max IDX Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getNextValQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT NVL(MAX(EVNT_SEQ_NO),0)+1 EVNT_SEQ_NO FROM BCDBA.TBGEVNTMGMT \n");
 		return sql.toString();
     }
}
