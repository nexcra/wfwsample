/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkSelDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 프리미엄 부킹 이벤트 셀렉트 박스
*   적용범위  : golf
*   작성일자  : 2009-05-26
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event;

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
public class GolfAdmEvntBkSelDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBkSelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBkSelDaoProc() {}	

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
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 			,rs.getLong("EVNT_SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GREEN_NM") );
					result.addString("COURSE" 			,rs.getString("GOLF_RSVT_CURS_NM") );
					result.addString("BKPS_DATE" 		,rs.getString("BOKG_ABLE_DATE") );
					result.addString("BKPS_TIME" 		,rs.getString("BOKG_ABLE_TIME") );
				}
			}
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT 	");
		sql.append("\n 		EVNT_SEQ_NO, GREEN_NM, TO_CHAR(TO_DATE(BOKG_ABLE_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') BOKG_ABLE_DATE, GOLF_RSVT_CURS_NM,  	");
		sql.append("\n 		TO_CHAR (TO_DATE (BOKG_ABLE_TIME, 'HH24MI'), 'HH24:MI') BOKG_ABLE_TIME	");
		sql.append("\n 	FROM 	");
		sql.append("\n 	BCDBA.TBGEVNTMGMT	");
		sql.append("\n 	WHERE EVNT_CLSS = '0002' 	");
		sql.append("\n  ORDER BY REG_ATON DESC	");
		return sql.toString();
    }
}
