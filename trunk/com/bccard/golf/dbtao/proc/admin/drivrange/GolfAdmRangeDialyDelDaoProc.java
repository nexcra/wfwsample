/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeDialyDelDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 일정 삭제 처리
*   적용범위  : golf
*   작성일자  : 2009-07-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

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
public class GolfAdmRangeDialyDelDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 드림 골프레인지 일정 삭제 처리";

	/** *****************************************************************
	 * GolfAdmRangeDialyDelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeDialyDelDaoProc() {}
	
	/**
	 * 관리자 드림 골프레인지 일정 삭제 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {

		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//조회 ----------------------------------------------------------
		
			String sql = this.getDeleteQuery1();
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, data.getLong("RSVTDIALY_SQL_NO") ); 			
			int res1 = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			
			sql = this.getDeleteQuery2();
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, data.getLong("RSVTDIALY_SQL_NO") ); 			
			int res2 = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			
			//getDeleteQuery2 지원가 없을 경우 삭제 결과가 0이 되므로 체크하지 않음.
			//if(res1 == 1 && res2 == 1) {
			if(res1 == 1 && res2 > -1) {
				result = 1;
				conn.commit();
			} else {
				result = 0;
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
    private String getDeleteQuery1(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGRSVTABLESCDMGMT 	\n");
		sql.append("\t  WHERE RSVT_ABLE_SCD_SEQ_NO = ?	\n");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getDeleteQuery2(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("DELETE BCDBA.TBGRSVTABLEBOKGTIMEMGMT 	\n");
 		sql.append("\t  WHERE RSVT_ABLE_SCD_SEQ_NO = ?	\n");
         return sql.toString();
     }
}
