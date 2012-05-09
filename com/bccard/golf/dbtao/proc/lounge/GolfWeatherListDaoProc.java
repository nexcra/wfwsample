/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfWeatherListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 골프장 날씨 
*   적용범위  : golf
*   작성일자  : 2009-06-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lounge;

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
public class GolfWeatherListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfWeatherListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfWeatherListDaoProc() {}	

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
			String rgn_nm		= data.getString("RGN_NM");
			
			String sql = this.getSelectQuery(rgn_nm);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			if (!GolfUtil.isNull(rgn_nm)) pstmt.setString(++idx, "%"+rgn_nm+"%"); //지역명 검색
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("GREEN_WEATH_CLSS" 		,rs.getString("GREEN_WEATH_CLSS") );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("RGN_NM"			,rs.getString("RGN_NM") );
			
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
	
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute2(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String rgn_nm		= data.getString("RGN_NM");
			
			String sql = this.getSelectQuery2(rgn_nm);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			if (!GolfUtil.isNull(rgn_nm)) pstmt.setString(++idx, "%"+rgn_nm+"%"); //지역명 검색
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("GREEN_WEATH_CLSS" 		,rs.getString("GREEN_WEATH_CLSS") );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
				
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
    private String getSelectQuery(String rgn_nm){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT 	");
		sql.append("\n		GREEN_WEATH_CLSS, GREEN_NM, RGN_NM   	");
		sql.append("\n FROM 	");
		sql.append("\n	BCDBA.TBGWEATHCODE		");
		sql.append("\n WHERE GREEN_WEATH_CLSS = GREEN_WEATH_CLSS	");
		
		if (!GolfUtil.isNull(rgn_nm)) sql.append("\n 	AND RGN_NM LIKE ?	"); //지역명 검색
		
		sql.append("\n ORDER BY GREEN_NM	");		
		
		return sql.toString();
    }
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectQuery2(String rgn_nm){
        StringBuffer sql = new StringBuffer();
 		
 		sql.append("\n SELECT * 	");
 		sql.append("\n FROM ( 	");
 		sql.append("\n 		SELECT GREEN_WEATH_CLSS, GREEN_NM 	");
 		sql.append("\n 		FROM BCDBA.TBGWEATHCODE 	");
 		sql.append("\n 		WHERE GREEN_WEATH_CLSS = GREEN_WEATH_CLSS	");
 		
 		if (!GolfUtil.isNull(rgn_nm)) sql.append("\n 	AND RGN_NM LIKE ?	"); //지역명 검색
 		
 		sql.append("\n 		 ORDER BY GREEN_NM)		");
 		sql.append("\n WHERE ROWNUM <= 1	");
 		
 		return sql.toString();
     }

}
