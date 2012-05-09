/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAreaSelInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 공통지역별 셀렉트박스 생성
*   적용범위  : golf
*   작성일자  : 2009-06-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.area;

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
public class GolfAreaSelInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAreaSelInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAreaSelInqDaoProc() {}	

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
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String sido		= data.getString("SIDO");
			String gugun		= data.getString("GUGUN");
			
			if (!GolfUtil.isNull(sido) && GolfUtil.isNull(gugun)){ // 구군 검색
				sql = this.getSelectQuery2();   
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun)){ // 읍면동 검색
				sql = this.getSelectQuery3();   
				
			} else { // 시도 검색
				sql = this.getSelectQuery1();
			}
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			if (!GolfUtil.isNull(sido) && GolfUtil.isNull(gugun)){ // 구군 검색
				pstmt.setString(++idx, sido);
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun)){ // 읍면동 검색
				pstmt.setString(++idx, sido);
				pstmt.setString(++idx, gugun);
			}
			
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("AREA_NM" 				,rs.getString("AREA_NM") );
										
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
    private String getSelectQuery1(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT ");
		sql.append("\n		LG_CTY_NM AS AREA_NM ");									
		sql.append("\n FROM BCDBA.TBGZP ");										
		sql.append("\n WHERE ROWID IN (SELECT   MAX (ROWID) FROM BCDBA.TBGZP GROUP BY LG_CTY_NM) ");								
		sql.append("\n ORDER BY AREA_NM ");						
	
		return sql.toString();
    }
    
    private String getSelectQuery2(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT ");
		sql.append("\n		CTY_RGN_NM AS AREA_NM ");									
		sql.append("\n FROM BCDBA.TBGZP ");										
		sql.append("\n WHERE ROWID IN (SELECT   MAX (ROWID) FROM BCDBA.TBGZP GROUP BY CTY_RGN_NM) ");
		sql.append("\n AND LG_CTY_NM LIKE ? ");	
		sql.append("\n ORDER BY AREA_NM ");						
	
		return sql.toString();
    }
    
    private String getSelectQuery3(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT ");
		sql.append("\n		DONG_NM AS AREA_NM ");									
		sql.append("\n FROM BCDBA.TBGZP ");										
		sql.append("\n WHERE ROWID IN (SELECT   MAX (ROWID) FROM BCDBA.TBGZP GROUP BY DONG_NM) ");
		sql.append("\n AND LG_CTY_NM LIKE ? ");	
		sql.append("\n AND CTY_RGN_NM LIKE ? ");	
		sql.append("\n ORDER BY AREA_NM ");						
	
		return sql.toString();
    }
}
