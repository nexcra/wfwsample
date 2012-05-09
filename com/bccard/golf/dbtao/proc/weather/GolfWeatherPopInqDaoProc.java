/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfWeatherPopInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 날씨코드 검색
*   적용범위  : golf
*   작성일자  : 2009-06-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.weather;

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
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfWeatherPopInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfWeatherPopInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfWeatherPopInqDaoProc() {}	

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
			 
			//조회 ----------------------------------------------------------
			String gf_nm		= data.getString("GF_NM");
			String rgn_nm		= data.getString("RGN_NM");
			
			String sql = this.getSelectQuery(gf_nm, rgn_nm);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			
			if (!GolfUtil.isNull(gf_nm)) pstmt.setString(++idx, "%"+gf_nm+"%"); // 골프장명 검색
			
			if (!GolfUtil.isNull(rgn_nm)) pstmt.setString(++idx, "%"+rgn_nm+"%"); // 지역명 검색

			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("GREEN_WEATH_CLSS" 		,rs.getString("GREEN_WEATH_CLSS") );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("RGN_NM"			,rs.getString("RGN_NM") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
										
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
    private String getSelectQuery(String gf_nm, String rgn_nm){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			GREEN_WEATH_CLSS, GREEN_NM, RGN_NM,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT	");
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			GREEN_WEATH_CLSS, GREEN_NM, RGN_NM 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGWEATHCODE		");
		sql.append("\n 				WHERE GREEN_WEATH_CLSS = GREEN_WEATH_CLSS	");
		
		
		if (!GolfUtil.isNull(gf_nm)) sql.append("\n 	AND GREEN_NM LIKE ?	"); // 골프장명 검색
		
		if (!GolfUtil.isNull(rgn_nm)) sql.append("\n 	AND RGN_NM LIKE ?	"); //지역명 검색
	
		sql.append("\n 				ORDER BY GREEN_WEATH_CLSS	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
