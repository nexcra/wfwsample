/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeRsvtSelDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 예약 등록
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmRangeRsvtSelDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmRangeRsvtSelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeRsvtSelDaoProc() {}	

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
			pstmt.setLong(++idx, data.getLong("RSVTTIME_SQL_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addString("RSVT_DATE" 		,rs.getString("RSVT_ABLE_DATE") );
					result.addString("RSVT_TIME" 			,rs.getString("RSVT_TIME") );
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
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
        
        sql.append("\n SELECT");
		sql.append("\n 	SUBSTR (TGRD.RSVT_ABLE_DATE, 1, 4) ||'-'|| SUBSTR (SUBSTR (TGRD.RSVT_ABLE_DATE, 5, 6), 1, 2) ||'-'|| SUBSTR (TGRD.RSVT_ABLE_DATE, 7, 8) RSVT_ABLE_DATE,	 ");
		sql.append("\n 	TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24:MI') || '~' || TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24:MI') RSVT_TIME  ");
		sql.append("\n FROM BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT 	");
		sql.append("\n WHERE TGRT.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO 	");
		sql.append("\n AND TGRD.GOLF_RSVT_DAY_CLSS = 'D'	");
		sql.append("\n AND TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO = ?	");	
		return sql.toString();
    }
}
