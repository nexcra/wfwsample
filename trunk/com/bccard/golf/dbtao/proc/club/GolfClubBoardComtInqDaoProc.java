/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardComtInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 덧글 상세
*   적용범위  : golf
*   작성일자  : 2009-06-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfClubBoardComtInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBoardComtInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubBoardComtInqDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("REPLY_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					result.addLong("REPY_SEQ_NO" 		,rs.getLong("REPY_SEQ_NO") );
					result.addString("BBRD_SEQ_NO" 		,rs.getString("BBRD_SEQ_NO") );
					result.addString("REPY_CTNT" 		,rs.getString("REPY_CTNT") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );
					result.addString("REG_PE_ID" 		,rs.getString("REG_PE_ID") );
					result.addString("CHNG_ATON"		,rs.getString("CHNG_ATON") );
					
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	
    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT");
		sql.append("\n 	REPY_SEQ_NO, BBRD_SEQ_NO, REPY_CTNT, REG_ATON, REG_PE_ID, CHNG_ATON	");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGCLUBBBRDREPY	");
		sql.append("\n WHERE REPY_SEQ_NO = ?	");

		return sql.toString();
    }
}