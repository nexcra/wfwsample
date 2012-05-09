/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardComListDaoProc
*   작성자     : (주)미디어포스 임은혜
*   내용        : 관리자 게시판관리 목록 조회 
*   적용범위  : Golf
*   작성일자  : 2009-05-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.board;

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
public class GolfAdmBoardComSelectListDaoProc extends AbstractProc {
	
	public static final String TITLE = "게시판 관리 셀렉트 목록 조회";
	
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보 
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		//debug("==== GolfAdmBoardComListDaoProc start ===");
		
		GolfConfig config = GolfConfig.getInstance();
		
		try{
			//조회 조건
			String boardCode	= dataSet.getString("boardCode"); 			
			
			String sql = this.getSelectQuery();			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			pstmt.setString(++pidx, boardCode);

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
								
				result.addString("GOLF_CMMN_CODE",				rset.getString("GOLF_CMMN_CODE"));
				result.addString("GOLF_CMMN_CODE_NM",			rset.getString("GOLF_CMMN_CODE_NM"));
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
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
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery() throws Exception{
		StringBuffer sql = new StringBuffer();	
		sql.append("\n	SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM 	\n");
		sql.append("\t	FROM BCDBA.TBGCMMNCODE						\n");
		sql.append("\t	WHERE GOLF_CMMN_CLSS=?	AND USE_YN='Y'		\n");
		return sql.toString();
	}
}
