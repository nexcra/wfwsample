/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGoodFoodListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 맛집 리스트
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lounge;

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
public class GolfAdmGoodFoodListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmGoodFoodListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmGoodFoodListDaoProc() {}	

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
			String fd_area_cd		= data.getString("FD_AREA_CD");
			String fd1_lev_cd		= data.getString("FD1_LEV_CD");
			String fd2_lev_cd		= data.getString("FD2_LEV_CD");
			String fd3_lev_cd		= data.getString("FD3_LEV_CD");
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			String sql = this.getSelectQuery(fd_area_cd,fd1_lev_cd,fd2_lev_cd,fd3_lev_cd,search_sel,search_word);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (!GolfUtil.isNull(fd_area_cd)) pstmt.setString(++idx, fd_area_cd); // 지역 검색
			
			if (!GolfUtil.isNull(fd1_lev_cd)) pstmt.setString(++idx, fd1_lev_cd); // 음식1차분류  검색
			
			if (!GolfUtil.isNull(fd2_lev_cd)) pstmt.setString(++idx, fd2_lev_cd); // 음식2차분류  검색
			
			if (!GolfUtil.isNull(fd3_lev_cd)) pstmt.setString(++idx, fd3_lev_cd); // 음식3차분류  검색
			
			if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
				
				if (search_sel.equals("ALL")){ // 전체
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}

			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("FD_SEQ_NO" 			,rs.getLong("AFFI_ETHS_SEQ_NO") );
					result.addString("FD_NM" 		,rs.getString("ETHS_NM") );
					result.addString("FD_AREA_CD" 			,rs.getString("ETHS_RGN_CODE") );
					result.addString("FD1_LEV_CD" 			,rs.getString("FOOD_SQ1_CTGO") );
					result.addString("FD2_LEV_CD"			,rs.getString("FOOD_SQ2_CTGO") );
					result.addString("FD3_LEV_CD"			,rs.getString("FOOD_SQ3_CTGO") );
					result.addLong("CMD_NUM"			,rs.getLong("CMD_NUM") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
										
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
										
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
    private String getSelectQuery(String fd_area_cd, String fd1_lev_cd, String fd2_lev_cd, String fd3_lev_cd, String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_ETHS_SEQ_NO, ETHS_NM, ETHS_RGN_CODE, FOOD_SQ1_CTGO, FOOD_SQ2_CTGO, FOOD_SQ3_CTGO, CMD_NUM, REG_ATON,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_ETHS_SEQ_NO, ETHS_NM,   	");
		sql.append("\n 			(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0021' AND USE_YN = 'Y' AND TGFD.ETHS_RGN_CODE = GOLF_CMMN_CODE) ETHS_RGN_CODE, 	");
		sql.append("\n 			(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ1_CTGO = FOOD_SQ1_CTGO) FOOD_SQ1_CTGO, 	");
		sql.append("\n 			(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ2_CTGO = FOOD_SQ1_CTGO) FOOD_SQ2_CTGO, 	");
		sql.append("\n 			(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ3_CTGO = FOOD_SQ1_CTGO) FOOD_SQ3_CTGO, 	");
		sql.append("\n 			TGFD.CMD_NUM, 	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGFD.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIETHS TGFD		");
		sql.append("\n 				WHERE TGFD.AFFI_ETHS_SEQ_NO = TGFD.AFFI_ETHS_SEQ_NO	");
		
		
		if (!GolfUtil.isNull(fd_area_cd)) sql.append("\n 	AND ETHS_RGN_CODE = ?	"); // 지역 검색
		
		if (!GolfUtil.isNull(fd1_lev_cd)) sql.append("\n 	AND FOOD_SQ1_CTGO = ?	"); // 음식1차분류  검색
		
		if (!GolfUtil.isNull(fd2_lev_cd)) sql.append("\n 	AND FOOD_SQ2_CTGO = ?	"); // 음식2차분류  검색
		
		if (!GolfUtil.isNull(fd3_lev_cd)) sql.append("\n 	AND FOOD_SQ3_CTGO = ?	"); // 음식3차분류  검색
		
		if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
			
			if (search_sel.equals("ALL")){ // 전체
				sql.append("\n 	AND (ETHS_NM LIKE ? 	");
				sql.append("\n 	OR CTNT LIKE ?	)	");
				
			} else {
				sql.append("\n 	AND "+search_sel+" LIKE ?	");
			}
		}
	
		
		sql.append("\n 				ORDER BY AFFI_ETHS_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");	
		return sql.toString();
    }
}
