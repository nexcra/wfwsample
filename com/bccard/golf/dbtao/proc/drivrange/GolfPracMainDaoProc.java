/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPracMainDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 할인 골프연습장 메인 리스트
*   적용범위  : golf
*   작성일자  : 2009-07-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfPracMainDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfPracMainDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfPracMainDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			
		    sql = this.getSelectQuery();  
            
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addLong("GF_SEQ_NO" 				,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("EXEC_TYPE_CD", 	rs.getString("GOLF_RNG_CLSS") ); 	
					result.addString("GF_NM", 	rs.getString("GREEN_NM") ); 
					result.addString("REG_ATON", 	rs.getString("REG_ATON") ); 
					
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
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,		");
		sql.append("\n				AFFI_GREEN_SEQ_NO, GOLF_RNG_CLSS, GREEN_NM, 	");
		sql.append("\n 			TO_CHAR (TO_DATE (REG_ATON, 'YYYYMMDDHH24MISS'), 'MM/DD') REG_ATON	");
		sql.append("\n 		FROM BCDBA.TBGAFFIGREEN 	");
		sql.append("\n 		WHERE AFFI_GREEN_SEQ_NO = AFFI_GREEN_SEQ_NO	");	
		sql.append("\n 		AND AFFI_FIRM_CLSS = '0003'	");	
		sql.append("\n 		ORDER BY AFFI_GREEN_SEQ_NO DESC)		");	
		sql.append("\n 	WHERE ROWNUM <= ?	");	
      
       return sql.toString();
    }
 
}
