/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeRsViewDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 결과 확인 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.jeju;

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
 * Golf
 * @author	미디어포스 
 * @version	1.0
 ******************************************************************************/
public class GolfBkJjTimeRsViewDaoProc extends AbstractProc {
	
	String title = "";
	
	/** *****************************************************************
	 * GolfBkPreTimeRsViewDaoProc 부킹 결과 확인 처리
	 * @param N/A
	 ***************************************************************** */
	public GolfBkJjTimeRsViewDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {

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
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO"));
			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("GOLF_SVC_RSVT_NO" 	,rs.getString("GOLF_SVC_RSVT_NO") );
					result.addString("BK_DATE" 				,rs.getString("BK_DATE") );
					result.addString("TEAM_NUM" 			,rs.getString("TEAM_NUM") );
					result.addString("TOT_PERS_NUM" 		,rs.getString("TOT_PERS_NUM") );
					
					result.addString("BK_TIME" 				,rs.getString("BK_TIME") );
					result.addString("HOPE_RGN_CODE" 		,rs.getString("HOPE_RGN_CODE") );
					result.addString("HP_TEL" 				,rs.getString("HP_TEL") );
					result.addString("CTCT_ABLE_TIME" 		,rs.getString("CTCT_ABLE_TIME") );
					
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
		
		sql.append("\n SELECT GOLF_SVC_RSVT_NO																							\n");
		sql.append("\t  	, TO_CHAR(TO_DATE(ROUND_HOPE_DATE), 'YY.MM.DD(DY)') BK_DATE													\n");
		sql.append("\t  	, TEAM_NUM, TOT_PERS_NUM																					\n");
		sql.append("\t  	, (CASE WHEN ROUND_HOPE_TIME='000000' THEN '전체' ELSE SUBSTR(ROUND_HOPE_TIME,1,2)||':00' END) AS BK_TIME	\n");
		sql.append("\t  	, (CASE WHEN HOPE_RGN_CODE='0001' THEN '전체'																\n");
		sql.append("\t  		WHEN HOPE_RGN_CODE='0002' THEN '제주시'																	\n");
		sql.append("\t  		WHEN HOPE_RGN_CODE='0003' THEN '서귀포시'																\n");
		sql.append("\t  		WHEN HOPE_RGN_CODE='0004' THEN '동부'																	\n");
		sql.append("\t  		WHEN HOPE_RGN_CODE='0005' THEN '서부' END) AS HOPE_RGN_CODE												\n");
		sql.append("\t  	, (HP_DDD_NO||'-'||HP_TEL_HNO||'-'||HP_TEL_SNO) AS HP_TEL													\n");
		sql.append("\t  	, (CASE WHEN CTCT_ABLE_STRT_TIME='000000' THEN '전체'														\n");
		sql.append("\t  	    WHEN CTCT_ABLE_STRT_TIME='170000' THEN '17:00시 이후'													\n");
		sql.append("\t  	    ELSE SUBSTR(CTCT_ABLE_STRT_TIME,1,2)||':00~'||SUBSTR(CTCT_ABLE_END_TIME,1,2)||':00' END) AS CTCT_ABLE_TIME	\n");
		sql.append("\t  	FROM BCDBA.TBGRSVTMGMT																						\n");
		sql.append("\t  	WHERE GOLF_SVC_RSVT_NO=?																					\n");
		
		return sql.toString();
    }
 
}
