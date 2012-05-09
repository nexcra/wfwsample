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
public class GolfAdmBoardComDelDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 게시판 삭제 처리";
	//private String temporary;
	
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
		
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		//debug("==== GolfAdmBoardComDelDaoProc start ===");
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no		= (String)userEtt.getMemNo(); 							
			}
			
			//조회 조건
			String search_yn			= dataSet.getString("search_yn"); 		//검색여부
			
			String search_clss			= "";									//검색어구분
			String search_word			= "";									//검색어
			if("Y".equals(search_yn)){
				search_clss		= dataSet.getString("search_clss"); 		// 검색어
				search_word		= dataSet.getString("search_word"); 		// 제목검색여부
			}
			long page_no 	= dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size 	= dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");

			String idx				= dataSet.getString("idx");			
			int res = 0;	
			

			// 쿼리가져오기
			String sql = this.getSelectDelQuery("");
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			
			pstmt.setString(++pidx, idx);
			res = pstmt.executeUpdate();
			
			
			result = new DbTaoResult(TITLE);

			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
						
			//debug("==== GolfAdmBoardComDelDaoProc end ===");	
			
		}catch ( Exception e ) {
			
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
	private String getSelectDelQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGBBRD					");
		sql.append("\n	WHERE BBRD_SEQ_NO = ?	");

		return sql.toString();
	}
	
}
