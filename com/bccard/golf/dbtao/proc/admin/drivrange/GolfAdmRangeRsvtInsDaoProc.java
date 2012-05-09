/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeRsvtInsDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 예약 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

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
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmRangeRsvtInsDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 드림 골프레인지 예약 등록 처리";

	/** *****************************************************************
	 * GolfAdmRangeRsvtInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeRsvtInsDaoProc() {}
	
	/**
	 * 관리자 드림 골프레인지 예약 등록 처리
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
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

			sql = this.getNextValQuery(); //번호 쿼리
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("RSVT_CLSS"));
            rs = pstmt.executeQuery();			
			String seq_no = "";
			if(rs.next()){
				seq_no = rs.getString("RSVT_SQL_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("RSVT_CLSS")+seq_no );
			pstmt.setString(++idx, data.getString("RSVT_CLSS") ); 
			pstmt.setString(++idx, data.getString("GF_ID") ); 
			pstmt.setLong(++idx, data.getLong("RSVTTIME_SQL_NO") );
			pstmt.setString(++idx, data.getString("HP_DDD_NO") );
			pstmt.setString(++idx, data.getString("HP_TEL_HNO") );
			pstmt.setString(++idx, data.getString("HP_TEL_SNO") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			
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
		
		sql.append("INSERT INTO BCDBA.TBGRSVTMGMT (	\n");
		sql.append("\t  GOLF_SVC_RSVT_NO, GOLF_SVC_RSVT_MAX_VAL, CDHD_ID, RSVT_ABLE_BOKG_TIME_SEQ_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, RSVT_YN, ATTD_YN, REG_MGR_ID,   	\n");
		sql.append("\t  CHNG_MGR_ID, REG_ATON, CHNG_ATON	\n");	
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,?,?,'Y','Y',?	, \n");
		sql.append("\t  0,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),NULL	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }
    
    /** ***********************************************************************
     * Max IDX Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getNextValQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT LPAD(TO_NUMBER(NVL(MAX(SUBSTR(GOLF_SVC_RSVT_NO,6,7)),0))+1,7,'0') RSVT_SQL_NO FROM BCDBA.TBGRSVTMGMT \n");
         sql.append("WHERE GOLF_SVC_RSVT_MAX_VAL = ? \n");
 		return sql.toString();
     }

}
