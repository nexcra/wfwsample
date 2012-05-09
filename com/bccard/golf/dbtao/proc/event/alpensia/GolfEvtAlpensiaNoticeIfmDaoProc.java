/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvtAlpensiaNoticeIfmDaoProc
*   작성자	: (주)미디어포스 임은혜
*   내용		: 이벤트 > 알펜시아 > 공지사항
*   적용범위	: Golf
*   작성일자	: 2010-06-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.alpensia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-16 
 **************************************************************************** */
public class GolfEvtAlpensiaNoticeIfmDaoProc extends AbstractProc {
	
	public static final String TITLE = "게시판 관리 목록 조회";
	
	
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
				
		try{
			String sql = this.getSelectQuery();			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}				
				
				result.addString("BBRD_SEQ_NO",				rset.getString("BBRD_SEQ_NO"));
				result.addString("TITL",				rset.getString("TITL"));
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
		sql.append("\n	SELECT * 	");
		sql.append("\n	FROM (	");
		sql.append("\n	    SELECT ROWNUM RNUM, BBRD_SEQ_NO, TITL, EPS_YN, REG_ATON	");
		sql.append("\n	    FROM (	");
		sql.append("\n	        SELECT BBRD_SEQ_NO, TITL, EPS_YN, REG_ATON	");
		sql.append("\n	        FROM BCDBA.TBGBBRD	");
		sql.append("\n	        WHERE BBRD_CLSS='0001' AND EPS_YN='Y' AND GOLF_CLM_CLSS='0001'	");
		sql.append("\n	        ORDER BY BBRD_SEQ_NO DESC	");
		sql.append("\n	    )	");
		sql.append("\n	    ORDER BY RNUM	");
		sql.append("\n	)	");
		sql.append("\n	WHERE RNUM<=10	");
		return sql.toString();
	}
}
