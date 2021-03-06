/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfClubMenuListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 메뉴 리스트
*   적용범위  : golf
*   작성일자  : 2009-06-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfClubMenuListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfClubMenuListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMenuListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("CLUB_CODE"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {
					result.addInt("RNUM"				,rs.getInt("RNUM") );
					result.addString("BBRD_SEQ_NO"		,rs.getString("BBRD_SEQ_NO") );
					result.addString("CLUB_BBRD_CLSS"	,rs.getString("CLUB_BBRD_CLSS") );
					result.addString("CLUB_SEQ_NO"		,rs.getString("CLUB_SEQ_NO") );
					result.addString("BBRD_INFO"		,rs.getString("BBRD_INFO") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
										
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
		sql.append("\n	SELECT	* ");
		sql.append("\n	FROM	");
		sql.append("\n	(	");
		sql.append("\n		SELECT ROWNUM RNUM, BBRD_SEQ_NO, CLUB_BBRD_CLSS, CLUB_SEQ_NO, BBRD_INFO, REG_ATON, SORT_SEQ	");
		sql.append("\n		FROM");
		sql.append("\n		(	");		
		sql.append("\n			SELECT	");
		sql.append("\n				BBRD_SEQ_NO, CLUB_BBRD_CLSS, CLUB_SEQ_NO, BBRD_INFO, REG_ATON, SORT_SEQ 	");
		sql.append("\n 			FROM 	");
		sql.append("\n			BCDBA.TBGCLUBBBRDMGMT	");
		sql.append("\n 			WHERE CLUB_SEQ_NO = ?	");
		sql.append("\n			ORDER BY BBRD_SEQ_NO ASC	");		
		sql.append("\n		)	");
		sql.append("\n	)	");
		sql.append("\n	ORDER BY SORT_SEQ ASC	");

		return sql.toString();
    }
}