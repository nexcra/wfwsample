/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfFieldGofdListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 전국골프장 안내 상세보기(맛집정보)
*   적용범위  : golf
*   작성일자  : 2009-06-05
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
public class GolfFieldGofdListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfFieldGofdListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfFieldGofdListDaoProc() {}	

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
			long gf_seq_no		= data.getLong("GF_SEQ_NO");
			
			String sql = this.getSelectQuery(gf_seq_no);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (gf_seq_no != 0L) pstmt.setLong(++idx, gf_seq_no);
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("FD_SEQ_NO" 			,rs.getLong("AFFI_ETHS_SEQ_NO") );
					result.addString("IMG_NM" 		,rs.getString("ETHS_IMG") );
					result.addString("FD_NM" 			,rs.getString("ETHS_NM") );
					result.addString("ZIPADDR" 			,rs.getString("ADDR") );
					result.addString("DETAILADDR" 			,rs.getString("DTL_ADDR") );
					result.addString("FD1_LEV_CD" 			,rs.getString("FOOD_SQ1_CTGO") );
					result.addString("FD2_LEV_CD"			,rs.getString("FOOD_SQ2_CTGO") );
					result.addString("FD3_LEV_CD"			,rs.getString("FOOD_SQ3_CTGO") );
					result.addLong("CMD_NUM"			,rs.getLong("CMD_NUM") );
					result.addString("BEST_YN"			,rs.getString("BEST_YN") );
					result.addString("NEW_YN"			,rs.getString("ANW_BLTN_ARTC_YN") );
										
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
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.     
    ************************************************************************ */
    private String getSelectQuery(long gf_seq_no){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_ETHS_SEQ_NO, ETHS_IMG, ETHS_NM, ADDR, DTL_ADDR, FOOD_SQ1_CTGO, FOOD_SQ2_CTGO, FOOD_SQ3_CTGO, CMD_NUM, BEST_YN, ANW_BLTN_ARTC_YN,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			TGFD.AFFI_ETHS_SEQ_NO, TGFD.ETHS_IMG, TGFD.ETHS_NM, TGFD.ADDR, TGFD.DTL_ADDR,   	");
		sql.append("\n 			(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ1_CTGO = FOOD_SQ1_CTGO) FOOD_SQ1_CTGO, 	");
		sql.append("\n 			(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ2_CTGO = FOOD_SQ1_CTGO) FOOD_SQ2_CTGO, 	");
		sql.append("\n 			(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ3_CTGO = FOOD_SQ1_CTGO) FOOD_SQ3_CTGO, 	");
		sql.append("\n 			NVL (TGFD.CMD_NUM, 0) CMD_NUM, TGFD.BEST_YN, TGFD.ANW_BLTN_ARTC_YN 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIETHS TGFD, BCDBA.TBGETHSNGHBGREEN TGFDI	");
		sql.append("\n 				WHERE TGFD.AFFI_ETHS_SEQ_NO = TGFDI.AFFI_ETHS_SEQ_NO(+)	");
		
		if (gf_seq_no != 0L) sql.append("\n 				AND TGFDI.AFFI_GREEN_SEQ_NO = ?	");
		
		sql.append("\n 				ORDER BY TGFD.AFFI_ETHS_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");	
		
       return sql.toString();
    }
}
