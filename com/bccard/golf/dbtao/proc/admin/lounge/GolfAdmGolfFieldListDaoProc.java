/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGolfFieldListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 골프장 리스트
*   적용범위  : golf
*   작성일자  : 2009-05-26
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
public class GolfAdmGolfFieldListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmGolfFieldListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmGolfFieldListDaoProc() {}	

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
			String gf_area_cd		= data.getString("GF_AREA_CD");
			String gf_clss_cd		= data.getString("GF_CLSS_CD");
			String gf_hole_cd		= data.getString("GF_HOLE_CD");
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			String sql = this.getSelectQuery(gf_area_cd,gf_clss_cd,gf_hole_cd,search_sel,search_word);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (!GolfUtil.isNull(gf_area_cd)) pstmt.setString(++idx, gf_area_cd); // 지역 검색
			
			if (!GolfUtil.isNull(gf_clss_cd)) pstmt.setString(++idx, gf_clss_cd);  // 회원제/퍼블릭 검색
			
			if (!GolfUtil.isNull(gf_hole_cd)) pstmt.setString(++idx, gf_hole_cd); // 홀수 검색
			
			if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
				
				if (search_sel.equals("ALL")){ //전체
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
					result.addLong("GF_SEQ_NO" 			,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("GF_NM" 		,rs.getString("GREEN_NM") );
					result.addString("GF_CLSS_CD" 			,rs.getString("GREEN_CLSS") );
					result.addString("GF_HOLE_CD" 			,rs.getString("GREEN_ODNO_CODE") );
					result.addString("GF_AREA_CD"			,rs.getString("GREEN_RGN_CLSS") );
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
    private String getSelectQuery(String gf_area_cd, String gf_clss_cd, String gf_hole_cd, String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_GREEN_SEQ_NO, GREEN_NM, GREEN_CLSS, GREEN_ODNO_CODE, GREEN_RGN_CLSS, REG_ATON,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_GREEN_SEQ_NO, GREEN_NM, TMC1.GOLF_CMMN_CODE_NM GREEN_CLSS, TMC2.GOLF_CMMN_CODE_NM GREEN_ODNO_CODE, TMC3.GOLF_CMMN_CODE_NM GREEN_RGN_CLSS,   	");
		sql.append("\n 			TO_CHAR (TO_DATE (REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIGREEN TGFF,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0019' AND USE_YN = 'Y') TMC1,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0020' AND USE_YN = 'Y') TMC2,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0021' AND USE_YN = 'Y') TMC3	");
		sql.append("\n 				WHERE TGFF.GREEN_CLSS = TMC1.GOLF_CMMN_CODE(+)	");
		sql.append("\n 				AND TGFF.GREEN_ODNO_CODE = TMC2.GOLF_CMMN_CODE(+)	");
		sql.append("\n 				AND TGFF.GREEN_RGN_CLSS = TMC3.GOLF_CMMN_CODE(+)	");
		sql.append("\t 					AND TGFF.AFFI_FIRM_CLSS = '0004'	");	
		
		
		if (!GolfUtil.isNull(gf_area_cd)) sql.append("\n 	AND TGFF.GREEN_RGN_CLSS = ?	"); // 지역 검색
		
		if (!GolfUtil.isNull(gf_clss_cd)) sql.append("\n 	AND TGFF.GREEN_CLSS = ?	");  // 회원제/퍼블릭 검색
		
		if (!GolfUtil.isNull(gf_hole_cd)) sql.append("\n 	AND TGFF.GREEN_ODNO_CODE = ?	"); // 홀수 검색
		
		if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
			
			if (search_sel.equals("ALL")){ //전체
				sql.append("\n 		AND (TGFF.GREEN_NM LIKE ? 	");
				sql.append("\n 		OR TGFF.CTNT LIKE ?	)	");
				
			} else {
				sql.append("\n 		AND "+search_sel+" LIKE ?	");
			}
		}
	
	
		sql.append("\n 				ORDER BY AFFI_GREEN_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");	
		return sql.toString();
    }
}
