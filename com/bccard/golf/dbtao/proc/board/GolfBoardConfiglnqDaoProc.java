/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardConfiglnqDaoProc
*   작성자     : (주)미디어포스 권영만
*   내용        : 게시판 환경 가져오기
*   적용범위  : Golf
*   작성일자  : 2009-04-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.board;

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

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-04-01
 **************************************************************************** */
public class GolfBoardConfiglnqDaoProc extends AbstractProc {

	
	public static final String TITLE = "게시판 세팅 조회";
	
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
		
		//debug("==== GolfBoardConfiglnqDaoProc Start :"+TITLE+" ===");
		
		try{
			//조회 조건
			String boardid			= dataSet.getString("boardid"); 		//게시판번호					

			String sql = this.getSelectQuery(boardid);		
			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			
			pstmt.setString(++pidx, boardid);			

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
			
				result.addString("BOARD_CODE",				rset.getString("BOARD_CODE"));
				result.addString("BOARD_NM",				rset.getString("BOARD_NM"));
				result.addString("USE_YN",					rset.getString("USE_YN"));
				result.addString("boardid",					boardid);

				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfBoardConfiglnqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfBoardConfiglnqDaoProc ERROR ===");
			e.printStackTrace();
			//debug("==== GolfBoardConfiglnqDaoProc ERROR ===");
		}finally{
			try { if(rset != null) {rset.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return result;	
	}	
	
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery(String boardid) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT		BOARD_CODE,								");
		sql.append("\n 					BOARD_NM,							");
		sql.append("\n 					USE_YN								");
		sql.append("\n 	FROM TPBOARDINFO	 TBI							");
		sql.append("\n 	WHERE BOARDID = ? 									");
	

		return sql.toString();
	}
	
	
	
	
	
	
}
