/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardComDelDaoProc
*   작성자     : (주)미디어포스 임은혜
*   내용        : 관리자 게시판 삭제 처리
*   적용범위  : Golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-03-31  
 **************************************************************************** */
public class GolfAdmBoardComDelAllDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 게시판 삭제 처리";
	//private String temporary;
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보 
	************************************************************************ */
	public int execute(WaContext context, TaoDataSet data, String[] lsn_seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount = 0;
		int iCount2 = 0;
		
		try{
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
	private String getDeleteQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGBBRD					");
		sql.append("\n	WHERE BBRD_SEQ_NO = ?	");

		return sql.toString();
	}
	
}
