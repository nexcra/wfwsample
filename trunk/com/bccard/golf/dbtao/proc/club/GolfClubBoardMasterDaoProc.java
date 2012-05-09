/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardMasterDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 게시판 정보
*   적용범위  : golf
*   작성일자  : 2009-06-23
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
public class GolfClubBoardMasterDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBoardMasterDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubBoardMasterDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult boardInfo(WaContext context,  HttpServletRequest request, TaoDataSet data) throws BaseException {
		
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
			pstmt.setString(++idx, data.getString("SCH_CLUB_SEQ_NO"));
			pstmt.setString(++idx, data.getString("SCH_BBRD_SEQ_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("BBRD_SEQ_NO" 		,rs.getLong("BBRD_SEQ_NO") );	
					result.addString("CLUB_BBRD_CLSS"	,rs.getString("CLUB_BBRD_CLSS") );
					result.addString("CLUB_SEQ_NO"		,rs.getString("CLUB_SEQ_NO") );
					result.addString("BBRD_INFO"		,rs.getString("BBRD_INFO") );					
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					
					result.addString("CLUB_BBRD_CLSS_NM"			,rs.getString("CLUB_BBRD_CLSS_NM") );
					
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
		
		sql.append("\n	SELECT	");
		sql.append("\n 		TCBM.BBRD_SEQ_NO, TCBM.CLUB_BBRD_CLSS, TCBM.CLUB_SEQ_NO, TCBM.BBRD_INFO, TCBM.REG_ATON, 	");
		sql.append("\n 		(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0041' AND GOLF_CMMN_CODE=TCBM.CLUB_BBRD_CLSS) CLUB_BBRD_CLSS_NM,	");
		sql.append("\n 		TC.CLUB_NM, TC.OPN_PE_ID  	");
		sql.append("\n 	FROM 	");
		sql.append("\n	BCDBA.TBGCLUBBBRDMGMT TCBM, BCDBA.TBGCLUBMGMT TC");
		sql.append("\n	WHERE TCBM.CLUB_SEQ_NO = TC.CLUB_SEQ_NO	");
		sql.append("\n	AND TCBM.CLUB_SEQ_NO = ?	");
		sql.append("\n	AND TCBM.BBRD_SEQ_NO = ?	");

		return sql.toString();
    }
}
