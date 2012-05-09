/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfClubListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 동호회 리스트
*   적용범위  : golf
*   작성일자  : 2009-07-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

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
public class GolfClubMemListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfClubListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMemListDaoProc() {}	

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
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			String join_type		= data.getString("JOIN_TYPE");
			
			String sql = this.getSelectQuery(search_sel, search_word, join_type);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setString(++idx, data.getString("CLUB_CODE"));
			if (!GolfUtil.isNull(join_type)) pstmt.setString(++idx, join_type);

			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
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
					result.addLong("CLUB_CDHD_SEQ_NO" 		,rs.getLong("CLUB_CDHD_SEQ_NO") );
					result.addString("CLUB_SEQ_NO" 			,rs.getString("CLUB_SEQ_NO") );
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("CDHD_NM" 				,rs.getString("CDHD_NM") );
					result.addString("GREET_CTNT" 			,rs.getString("GREET_CTNT") );
					result.addString("JONN_YN"				,rs.getString("JONN_YN") );
					result.addString("SECE_YN"				,rs.getString("SECE_YN") );					
					result.addString("APLC_ATON"			,rs.getString("APLC_ATON") );					
					result.addString("JONN_ATON"			,rs.getString("JONN_ATON") );
					result.addString("CHNG_ATON"			,rs.getString("CHNG_ATON") );
					result.addString("SECE_ATON"			,rs.getString("SECE_ATON") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
					result.addString("RNUM"				,rs.getString("RNUM") );
										
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
    private String getSelectQuery(String search_sel, String search_word, String join_type){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			CLUB_CDHD_SEQ_NO, CLUB_SEQ_NO, CDHD_ID, CDHD_NM, GREET_CTNT, JONN_YN, SECE_YN, TO_CHAR(TO_DATE(APLC_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') APLC_ATON, TO_CHAR(TO_DATE(JONN_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') JONN_ATON, CHNG_ATON, SECE_ATON,	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				CLUB_CDHD_SEQ_NO, CLUB_SEQ_NO, CDHD_ID, CDHD_NM, GREET_CTNT, JONN_YN, SECE_YN, APLC_ATON, JONN_ATON, CHNG_ATON, SECE_ATON 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGCLUBCDHDMGMT TCM	");
		sql.append("\n 				WHERE CLUB_SEQ_NO = ?	");
		sql.append("\n 				AND SECE_YN = 'N'	");
		if (!GolfUtil.isNull(join_type)) sql.append("\n 				AND JONN_YN = ?	");		

		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (CDHD_NM LIKE ?	");
				sql.append("\n 				OR GREET_CTNT LIKE ? )	");	
			} else {
				sql.append("\n 				AND "+search_sel+" LIKE ?	");
			}
		}
		
		sql.append("\n 				ORDER BY CLUB_CDHD_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}