/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkFaqUpdDaoProc
*   작성자     : (주)미디어포스 임은혜
*   내용        : 부킹 > 부킹 가이드 > 조회수 업데이트 처리 
*   적용범위  : Golf
*   작성일자  : 2009-05-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.guide;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-16
 **************************************************************************** */
public class GolfBkFaqUpdDaoProc extends AbstractProc {
	
	public static final String TITLE = "조회수 업데이트 처리";
	
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public int execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		int result = 0;
		Connection con = null;
				
		
		try{
			con = context.getDbConnection("default", null);
			//조회 조건
			String idx			= dataSet.getString("idx");
			
			// 01. 조회수 올리기
			String sql = this.getSelectHit(idx);
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, idx);
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();


			if(result > 0) {
				con.commit();
			} else {
				con.rollback();
			}
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
			 
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. - 조회수 올리기
	************************************************************************ */
	private String getSelectHit(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n ");
		sql.append("\t UPDATE BCDBA.TBGBBRD				\n");
		sql.append("\t SET INQR_NUM=INQR_NUM+1 			\n");
		sql.append("\t WHERE BBRD_SEQ_NO=?				\n");

		return sql.toString();
	}
}
