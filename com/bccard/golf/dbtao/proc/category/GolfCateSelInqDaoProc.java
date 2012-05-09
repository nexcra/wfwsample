/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfCateSelInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통카테고리 셀렉트박스 생성
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.category;

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
public class GolfCateSelInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfCateSelInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfCateSelInqDaoProc() {}	

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
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("PT_CATEGORY_ID"));
			pstmt.setString(++idx, data.getString("CTG_CLSS"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("CTG_ID" 				,rs.getString("FOOD_SQ1_CTGO") );
					result.addString("URNK_CTG_ID" 			,rs.getString("FOOD_SQ2_CTGO") );
					result.addString("CTG_NM" 				,rs.getString("CODE_NM") );
					result.addString("USE_YN" 				,rs.getString("USE_YN") );
					result.addString("CTG_CLSS" 			,rs.getString("FOOD_CLSS") );
										
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
        
        sql.append("\n SELECT ");
		sql.append("\n	FOOD_SQ1_CTGO, FOOD_SQ2_CTGO, CODE_NM, USE_YN, FOOD_CLSS ");									
		sql.append("\n FROM BCDBA.TBGFOODCTGO ");										
		sql.append("\n WHERE FOOD_SQ2_CTGO = ? ");								
		sql.append("\n AND FOOD_CLSS = ? ");						
		sql.append("\n AND USE_YN = 'Y' ");				
		sql.append("\n ORDER BY FOOD_SQ1_CTGO ");
		
        /*
		sql.append("\n SELECT ");
		sql.append("\n	CTG_ID, URNK_CTG_ID, CTG_NM, USE_YN, CTG_CLSS ");									
		sql.append("\n FROM BCDBA.TBGFCTG ");										
		sql.append("\n WHERE URNK_CTG_ID = ? ");								
		sql.append("\n AND CTG_CLSS = ? ");						
		sql.append("\n AND USE_YN = 'Y' ");				
		sql.append("\n ORDER BY CTG_ID ");
		*/
		return sql.toString();
    }
}
