/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmLsnUccUpdDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  레슨 > UCC 레슨 처리
*   적용범위	: golf
*   작성일자	: 2009-07-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lesson.ucc;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmLsnUccUpdDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 >  레슨 > UCC 레슨  처리";
	/** **************************************************************************
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		String sql  = "";
		int rs = 0;

		try {
			int pidx = 0;
			conn = context.getDbConnection("default", null);
			
			String bbrd_clss 	= data.getString("bbrd_clss");
			String idx 			= data.getString("idx");
			String mode 		= data.getString("mode");
			String admId 		= data.getString("admId");
			String ctnt 		= data.getString("ctnt");
			String answ_ctnt 	= data.getString("answ_ctnt");
			String hg_nm 		= data.getString("hg_nm");
			String eps_yn 		= data.getString("eps_yn");
			
			
			
			if("upd".equals(mode)){
				pidx = 0;
				sql = this.getUpdateQuery(eps_yn);
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(++pidx,ctnt);
				pstmt.setString(++pidx,answ_ctnt);
				pstmt.setString(++pidx,hg_nm);
				pstmt.setString(++pidx,eps_yn);
				pstmt.setString(++pidx,admId);
				pstmt.setString(++pidx,bbrd_clss);
				pstmt.setString(++pidx,idx);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					result.addString("RESULT","00");
				}else{
					result.addString("RESULT","01");
				}
			
			 
			}else if("del".equals(mode)){
				pidx = 0;
				sql = this.getDeleteQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx,bbrd_clss);
				pstmt.setString(++pidx,idx);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					result.addString("RESULT","00");
				}else{
					result.addString("RESULT","01");
				}
				
				
			}
		} catch ( Exception e ) {			
			
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getUpdateQuery(String eps_yn) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n UPDATE BCDBA.TBGBBRD SET									");
		sql.append("\n 		CTNT = ?											");
		sql.append("\n 		,ANSW_CTNT = ?										");
		sql.append("\n 		,HG_NM	= ?											");		
		sql.append("\n 		,EPS_YN	= ?											");
		if("N".equals(eps_yn)){
			sql.append("\n 		,DEL_YN = 'Y'									");
		}else if("Y".equals(eps_yn)){
			sql.append("\n      ,DEL_YN = 'N'									");
		}
		sql.append("\n 		,CHNG_MGR_ID = ?									");
		sql.append("\n 		,CHNG_ATON = TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')	");
		sql.append("\n 	WHERE BBRD_CLSS = ?	AND  BBRD_SEQ_NO = ?				");

		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getDeleteQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n UPDATE BCDBA.TBGBBRD SET									");	
		sql.append("\n 		EPS_YN	= 'N'										");
		sql.append("\n 		,DEL_YN = 'Y'										");
		sql.append("\n 	WHERE BBRD_CLSS = ?	AND  BBRD_SEQ_NO = ?				");

		return sql.toString();
	}
}
