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
public class GolfClubMyInfoInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfClubMenuListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMyInfoInqDaoProc() {}	

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
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("CLUB_CODE"));
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {					
					result.addLong("CLUB_CDHD_SEQ_NO"		,rs.getLong("CLUB_CDHD_SEQ_NO") );
					result.addString("GREET_CTNT"			,rs.getString("GREET_CTNT") );
					
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
		sql.append("\n 		CLUB_CDHD_SEQ_NO, GREET_CTNT 	");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGCLUBCDHDMGMT	");
		sql.append("\n WHERE CLUB_SEQ_NO = ?	");
		sql.append("\n AND CDHD_ID = ?	");
		sql.append("\n AND SECE_YN = 'N'	");
		
		return sql.toString();
    }
}