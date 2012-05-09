/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfGoodFoodListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 골프장 주변 리스트
*   적용범위  : golf
*   작성일자  : 2009-06-09
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
public class GolfGoodFoodListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfGoodFoodListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfGoodFoodListDaoProc() {}	

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
		String area_nm = "";

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String sido		= data.getString("SIDO");
			String gugun		= data.getString("GUGUN");
			String dong		= data.getString("DONG");
			String fd1_lev_cd		= data.getString("FD1_LEV_CD");
			String fd2_lev_cd		= data.getString("FD2_LEV_CD");
			String fd3_lev_cd		= data.getString("FD3_LEV_CD");
			String gf_area_cd		= data.getString("GF_AREA_CD");
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			/*
			if (!GolfUtil.isNull(sido) && GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) {
				area_nm = sido;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun ;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && !GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun +" "+ dong ;
			}
			*/
			
			String sql = this.getSelectQuery(sido,gugun,dong,fd1_lev_cd,fd2_lev_cd,fd3_lev_cd,gf_area_cd,search_sel,search_word);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (!GolfUtil.isNull(sido)) pstmt.setString(++idx, "%"+sido+"%");  // 시도지역 검색
			
			if (!GolfUtil.isNull(gugun)) pstmt.setString(++idx, "%"+gugun+"%");  // 구군지역 검색
			
			if (!GolfUtil.isNull(dong)) pstmt.setString(++idx, "%"+dong+"%");  // 읍면동지역 검색
			
			if (!GolfUtil.isNull(fd1_lev_cd)) pstmt.setString(++idx, fd1_lev_cd); // 음식1차분류  검색
			
			if (!GolfUtil.isNull(fd2_lev_cd)) pstmt.setString(++idx, fd2_lev_cd); // 음식2차분류  검색
			
			if (!GolfUtil.isNull(fd3_lev_cd)) pstmt.setString(++idx, fd3_lev_cd); // 음식3차분류  검색
			
			if (!GolfUtil.isNull(gf_area_cd)) pstmt.setString(++idx, gf_area_cd); // 지역  검색
			
			if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
				
				if (search_sel.equals("ALL")){ // 전체
					pstmt.setString(++idx, "%"+search_word+"%");
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
					result.addString("IMG_NM" 		,rs.getString("ETHS_IMG") );
					result.addString("FD_NM" 			,rs.getString("ETHS_NM") );
					result.addString("ZIPADDR" 			,rs.getString("ADDR") );
					result.addString("DETAILADDR"			,rs.getString("DTL_ADDR") );
					
					String nghb_green_expl = rs.getString("NGHB_GREEN_EXPL");
					//result.addString("REGION_GREEN_NM"			,rs.getString("NGHB_GREEN_EXPL") );
					
					result.addString("FD1_LEV_CD"			,rs.getString("FOOD_SQ1_CTGO") );
					result.addString("FD2_LEV_CD"			,rs.getString("FOOD_SQ2_CTGO") );
					result.addString("FD3_LEV_CD"			,rs.getString("FOOD_SQ3_CTGO") );
					result.addLong("CMD_NUM"			,rs.getLong("CMD_NUM") );
					result.addString("BEST_YN"			,rs.getString("BEST_YN") );
					result.addString("NEW_YN"			,rs.getString("ANW_BLTN_ARTC_YN") );
										
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
					
					String green_nm = this.execute2(context, rs.getLong("AFFI_ETHS_SEQ_NO"));
					
					String gf_nm = green_nm + (!GolfUtil.isNull(nghb_green_expl) ? ","+ nghb_green_expl : "");
					
					if (gf_nm.getBytes().length > 40)	gf_nm = GolfUtil.getCutKSCString(gf_nm,40,"..."); 
					
					result.addString("GF_NM"			, gf_nm);
										
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
	
	public String execute2(WaContext context, long seq_no) throws BaseException {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String green_nm = "";
		
		try {
			conn = context.getDbConnection("default", null);
			 
			String sql = this.getSelectQuery2();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, seq_no);
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 
				while(rs.next())  {	
					green_nm = rs.getString("GREEN_NM");
				}
			}

		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return green_nm;
	}
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(String sido, String gugun, String dong, String fd1_lev_cd, String fd2_lev_cd, String fd3_lev_cd, String gf_area_cd, String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_ETHS_SEQ_NO, ETHS_IMG, ETHS_NM, ADDR, DTL_ADDR, NGHB_GREEN_EXPL, FOOD_SQ1_CTGO, FOOD_SQ2_CTGO, FOOD_SQ3_CTGO, CMD_NUM, BEST_YN, ANW_BLTN_ARTC_YN,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(ROWNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(ROWNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ");
		
		sql.append("\n 			DISTINCT TGFD.AFFI_ETHS_SEQ_NO, TGFD.ETHS_IMG, TGFD.ETHS_NM, TGFD.ADDR, TGFD.DTL_ADDR, TGFD.NGHB_GREEN_EXPL,   	");
		sql.append("\n 			(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0021' AND USE_YN = 'Y' AND TGFD.ETHS_RGN_CODE = GOLF_CMMN_CODE) ETHS_RGN_CODE, 	");
		sql.append("\n 			(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ1_CTGO = FOOD_SQ1_CTGO) FOOD_SQ1_CTGO, 	");
		sql.append("\n 			(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ2_CTGO = FOOD_SQ1_CTGO) FOOD_SQ2_CTGO, 	");
		sql.append("\n 			(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ3_CTGO = FOOD_SQ1_CTGO) FOOD_SQ3_CTGO, 	");
		sql.append("\n 			NVL (TGFD.CMD_NUM, 0) CMD_NUM, TGFD.BEST_YN, TGFD.ANW_BLTN_ARTC_YN, TGFD.ETHS_RGN_CODE 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIETHS TGFD, BCDBA.TBGETHSNGHBGREEN TGFDI, BCDBA.TBGAFFIGREEN TGF		");
		sql.append("\n 				WHERE TGFD.AFFI_ETHS_SEQ_NO = TGFDI.AFFI_ETHS_SEQ_NO(+)	");
		sql.append("\n 				AND TGFDI.AFFI_GREEN_SEQ_NO = TGF.AFFI_GREEN_SEQ_NO	");
		
		
		if (!GolfUtil.isNull(sido)) sql.append("\n 	AND REPLACE(TGFD.ADDR, ' ', '') LIKE ?	"); // 시도지역 검색
		
		if (!GolfUtil.isNull(gugun)) sql.append("\n 	AND REPLACE(TGFD.ADDR, ' ', '') LIKE ?	"); // 구군지역 검색
		
		if (!GolfUtil.isNull(dong)) sql.append("\n 	AND REPLACE(TGFD.ADDR, ' ', '') LIKE ?	"); // 읍면동지역 검색
		
		if (!GolfUtil.isNull(fd1_lev_cd)) sql.append("\n 	AND TGFD.FOOD_SQ1_CTGO = ?	"); // 음식1차분류  검색
		
		if (!GolfUtil.isNull(fd2_lev_cd)) sql.append("\n 	AND TGFD.FOOD_SQ2_CTGO = ?	"); // 음식2차분류  검색
		
		if (!GolfUtil.isNull(fd3_lev_cd)) sql.append("\n 	AND TGFD.FOOD_SQ3_CTGO = ?	"); // 음식3차분류  검색
		
		if (!GolfUtil.isNull(gf_area_cd)) sql.append("\n 	AND TGFD.ETHS_RGN_CODE = ?	"); // 지역  검색
		
		if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
			
			if (search_sel.equals("ALL")){ // 전체
				sql.append("\n 	AND (TGFD.ETHS_NM LIKE ? 	");
				sql.append("\n 	OR TGFD.CTNT LIKE ?		");
				sql.append("\n 	OR TGF.GREEN_NM LIKE ?	)	");
				
			} else {
				sql.append("\n 	AND "+search_sel+" LIKE ?	");
			}
		}
	
		
		sql.append("\n 				ORDER BY TGFD.ETHS_RGN_CODE	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");	
		
        return sql.toString();
    }
    
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectQuery2(){
         StringBuffer sql = new StringBuffer();
 		
        sql.append("\n SELECT AFFI_ETHS_SEQ_NO, LTRIM (MAX (SYS_CONNECT_BY_PATH (GREEN_NM, ',')), ',') GREEN_NM		");
 		sql.append("\n FROM (SELECT AFFI_ETHS_SEQ_NO	,	");
 		sql.append("\n 			GREEN_NM, ROW_NUMBER () OVER (PARTITION BY AFFI_ETHS_SEQ_NO ORDER BY GREEN_NM) CNT		");	
 		
 		sql.append("\n 			FROM (SELECT TGFDI.AFFI_ETHS_SEQ_NO, TGF.GREEN_NM		");	
 		sql.append("\n 					FROM BCDBA.TBGETHSNGHBGREEN TGFDI, BCDBA.TBGAFFIGREEN TGF	");	
 		sql.append("\n 					WHERE TGFDI.AFFI_GREEN_SEQ_NO = TGF.AFFI_GREEN_SEQ_NO	");	
 		sql.append("\n 					AND TGFDI.AFFI_ETHS_SEQ_NO = ?		");	
 		sql.append("\n 					)	");	
 		sql.append("\n 		)	");	
 		sql.append("\n START WITH CNT = 1	");	
 		sql.append("\n CONNECT BY PRIOR CNT = CNT - 1	");	
 		sql.append("\n GROUP BY AFFI_ETHS_SEQ_NO	");	
 		
         return sql.toString();
     }
}
