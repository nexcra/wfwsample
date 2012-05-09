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

package com.bccard.golf.dbtao.proc.booking.sky;

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
public class GolfBkSkyTimeRegFormDaoProc extends AbstractProc {
	
	String title = "";
	
	/** *****************************************************************
	 * GolfBkPreTimeRsViewDaoProc 부킹 결과 확인 처리
	 * @param N/A
	 ***************************************************************** */
	public GolfBkSkyTimeRegFormDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("TIME_SEQ_NO"));
			rs = pstmt.executeQuery();			
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("TIME_SEQ_NO" 			,rs.getString("TIME_SEQ_NO") );
					result.addString("BK_DATE" 				,rs.getString("BK_DATE") );
					result.addString("BK_DATE_REAL" 		,rs.getString("BK_DATE_REAL") );
					result.addString("BK_TIME" 				,rs.getString("BK_TIME") );
					result.addString("HOLE"					,rs.getString("HOLE") );
					result.addString("ROUND"				,rs.getString("ROUND") );
					result.addString("CANCEL_DATE"			,rs.getString("CANCEL_DATE") );
					
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
        		
		sql.append("\n SELECT																						\n");
		sql.append("\t  	T2.RSVT_ABLE_BOKG_TIME_SEQ_NO AS TIME_SEQ_NO											\n");
		sql.append("\t  	, TO_CHAR(TO_DATE(T1.BOKG_ABLE_DATE),'YY.MM.DD(DY)') AS BK_DATE							\n");
		sql.append("\t  	, TO_CHAR(TO_DATE(T1.BOKG_ABLE_DATE),'YYYYMMDD') AS BK_DATE_REAL						\n");
		sql.append("\t  	, SUBSTR(T2.BOKG_ABLE_TIME,1,2)||':'||SUBSTR(T2.BOKG_ABLE_TIME,3,4) AS BK_TIME			\n");
		sql.append("\t  	, CASE WHEN T1.SKY72_HOLE_CODE='0001' THEN '7' ELSE '14' END AS HOLE					\n");
		sql.append("\t  	, CASE WHEN T1.SKY72_HOLE_CODE='0001' THEN '1' ELSE '2' END AS ROUND					\n");
		sql.append("\t  	, TO_CHAR(TO_DATE(T1.BOKG_ABLE_DATE)-7,'YY.MM.DD(DY)') AS CANCEL_DATE					\n");
		sql.append("\t  	FROM BCDBA.TBGRSVTABLESCDMGMT T1														\n");
		sql.append("\t  	JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO		\n");
		sql.append("\t  	WHERE T2.RSVT_ABLE_BOKG_TIME_SEQ_NO=?													\n");
		
		return sql.toString();
    }
 
}
