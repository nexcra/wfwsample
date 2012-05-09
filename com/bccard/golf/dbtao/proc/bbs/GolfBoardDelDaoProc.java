/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardDelDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 삭제 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.bbs;

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
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfBoardDelDaoProc extends AbstractProc {

	public static final String TITLE = "공통게시판 삭제 처리";

	/** *****************************************************************
	 * GolfBoardDelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBoardDelDaoProc() {}
	
	/**
	 * 공통게시판 삭제 처리
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
		
			String sql = this.getDeleteQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("SEQ_NO") );
			pstmt.setString(2, data.getString("BBS") ); 
			int res1 = pstmt.executeUpdate();
		    /** ***********************************************************************/

			sql = this.getDeleteQuery2();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("SEQ_NO") ); 			
			res1 = res1 + pstmt.executeUpdate();
		    /** ***********************************************************************/
			
			if(res1 >= 1) {
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
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGBBRD 	\n");
		sql.append("\t  WHERE REPY_URNK_SEQ_NO = ?	\n");
		sql.append("\t  AND BBRD_CLSS = ?	\n");
        return sql.toString();
    }
    
    private String getDeleteQuery2(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGBBRDREPY 	\n");
		sql.append("\t  WHERE BBRD_SEQ_NO = ?	\n");
        return sql.toString();
    }
}
