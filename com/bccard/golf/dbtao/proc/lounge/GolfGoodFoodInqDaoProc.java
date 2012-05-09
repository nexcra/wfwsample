/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfGoodFoodInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 골프장 주변맛집 상세보기
*   적용범위  : golf
*   작성일자  : 2009-06-10
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lounge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfGoodFoodInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfGoodFoodInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfGoodFoodInqDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public HashMap execute(WaContext context, HashMap resultMap, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		GolfUtil cstr = new GolfUtil();

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, data.getLong("FD_SEQ_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					resultMap.put("FD_SEQ_NO" 			,rs.getString("AFFI_ETHS_SEQ_NO") );
					resultMap.put("FD_NM" 		,rs.getString("ETHS_NM") );
					resultMap.put("GF_SEQ_NO" 			,rs.getString("AFFI_GREEN_SEQ_NO") );
					resultMap.put("FD_AREA_CD" 			,rs.getString("ETHS_RGN_CODE") );
					resultMap.put("FD1_LEV_CD" 			,rs.getString("FOOD_SQ1_CTGO") );
					resultMap.put("FD2_LEV_CD" 			,rs.getString("FOOD_SQ2_CTGO") );
					resultMap.put("FD3_LEV_CD" 			,rs.getString("FOOD_SQ3_CTGO") );
					resultMap.put("IMG_NM" 			,rs.getString("ETHS_IMG") );
					resultMap.put("BEST_YN" 			,rs.getString("BEST_YN") );
					resultMap.put("NEW_YN" 			,rs.getString("ANW_BLTN_ARTC_YN") );
					resultMap.put("ZIPCODE1" 			,rs.getString("ZP1") );
					resultMap.put("ZIPCODE2" 			,rs.getString("ZP2") );
					resultMap.put("ZIPADDR" 			,rs.getString("ADDR") );
					resultMap.put("DETAILADDR" 			,rs.getString("DTL_ADDR") );
					resultMap.put("CHG_DDD_NO" 			,rs.getString("DDD_NO") );
					resultMap.put("CHG_TEL_HNO" 			,rs.getString("TEL_HNO") );
					resultMap.put("CHG_TEL_SNO" 			,rs.getString("TEL_SNO") );
					resultMap.put("ROAD_SRCH" 			,rs.getString("POS_EXPL") );
					resultMap.put("MAP_NM" 			,rs.getString("OLM_IMG") );
					resultMap.put("PARKING_YN" 			,rs.getString("PARK_ABLE_YN") );
					resultMap.put("START_HH" 			,rs.getString("STRT_HH") );
					resultMap.put("START_MI" 			,rs.getString("STRT_MI") );
					resultMap.put("END_HH" 			,rs.getString("END_HH") );
					resultMap.put("END_MI" 			,rs.getString("END_MI") );
					resultMap.put("SLS_END_DAY" 			,rs.getString("HDAY_INFO") );
					resultMap.put("URL" 			,rs.getString("WEB_SITE_URL") );
					resultMap.put("CTNT" 			,cstr.nl2br(rs.getString("CTNT")) );
					resultMap.put("FD_MENU" 			,rs.getString("ETHS_MAI_MENU_EXPL") );
					resultMap.put("REGION_GREEN_NM" 			,rs.getString("NGHB_GREEN_EXPL") );
					resultMap.put("REG_ATON" 			,rs.getString("REG_ATON") );
					resultMap.put("CORR_ATON" 			,rs.getString("CHNG_ATON") );
					resultMap.put("COMT_CNT" 			,rs.getString("COMT_CNT") );
			
					resultMap.put("RESULT", "00"); //정상결과
				}
			}

			if(resultMap.size() < 1) {
				resultMap.put("RESULT", "01");			
			}

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return resultMap;
	}	
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getPreNextInfo(WaContext context, TaoDataSet data) throws BaseException {
		
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
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			if (!GolfUtil.isNull(sido) && GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) {
				area_nm = sido;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun ;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && !GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun +" "+ dong ;
			}

			String sql = this.getSelectQuery(area_nm,fd1_lev_cd,fd2_lev_cd,fd3_lev_cd,search_sel,search_word);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			/*
			if (!GolfUtil.isNull(area_nm)) pstmt.setString(++idx, "%"+area_nm+"%");  // 지역 검색
			
			if (!GolfUtil.isNull(fd1_lev_cd)) pstmt.setString(++idx, fd1_lev_cd); // 음식1차분류  검색
			
			if (!GolfUtil.isNull(fd2_lev_cd)) pstmt.setString(++idx, fd2_lev_cd); // 음식2차분류  검색
			
			if (!GolfUtil.isNull(fd3_lev_cd)) pstmt.setString(++idx, fd3_lev_cd); // 음식3차분류  검색
			
			if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
				
				if (search_sel.equals("ALL")){ // 전체
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			*/
			
			pstmt.setLong(++idx, data.getLong("FD_SEQ_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {	
					result.addString("PRE_SEQ_NO" 				,rs.getString("PRE_SEQ_NO") );
					result.addString("PRE_TITL" 				,rs.getString("PRE_TITL") );
					result.addString("NEXT_SEQ_NO" 				,rs.getString("NEXT_SEQ_NO") );
					result.addString("NEXT_TITL" 				,rs.getString("NEXT_TITL") );
				}
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
        
        sql.append("\n SELECT");
		sql.append("\n 	TGFD.AFFI_ETHS_SEQ_NO, TGFD.ETHS_NM, T.AFFI_GREEN_SEQ_NO, TGFD.ETHS_RGN_CODE, 	 ");
		sql.append("\n  	(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ1_CTGO = FOOD_SQ1_CTGO) FOOD_SQ1_CTGO,	 ");
		sql.append("\n  	(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ2_CTGO = FOOD_SQ1_CTGO) FOOD_SQ2_CTGO,	 ");
		sql.append("\n  	(SELECT CODE_NM FROM BCDBA.TBGFOODCTGO WHERE TGFD.FOOD_SQ3_CTGO = FOOD_SQ1_CTGO) FOOD_SQ3_CTGO,	 ");
		sql.append("\n  	TGFD.ETHS_IMG, TGFD.BEST_YN, TGFD.ANW_BLTN_ARTC_YN, ");
		sql.append("\n  	SUBSTR (TGFD.ZP, 1, 3) ZP1, SUBSTR (TGFD.ZP, 4, 6) ZP2,	 ");
		sql.append("\n  	TGFD.ADDR, TGFD.DTL_ADDR, TGFD.DDD_NO, TGFD.TEL_HNO, TGFD.TEL_SNO, TGFD.POS_EXPL, TGFD.OLM_IMG,  ");
		sql.append("\n		DECODE(TGFD.PARK_ABLE_YN,'Y','가능','N','불가능') PARK_ABLE_YN,  ");
		sql.append("\n		SUBSTR (TRIM(TGFD.SLS_STRT_TIME), 1, 2) STRT_HH, SUBSTR (TRIM(TGFD.SLS_STRT_TIME), 3, 4) STRT_MI,  ");
		sql.append("\n		SUBSTR (TRIM(TGFD.SLS_END_TIME), 1, 2) END_HH, SUBSTR (TRIM(TGFD.SLS_END_TIME), 3, 4) END_MI,  ");
		sql.append("\n		TGFD.HDAY_INFO, TGFD.WEB_SITE_URL, TGFD.CTNT, TGFD.ETHS_MAI_MENU_EXPL, TGFD.NGHB_GREEN_EXPL,  ");
		sql.append("\n		TO_CHAR (TO_DATE (TGFD.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD AM HH24:MI:SS')REG_ATON,   ");
		sql.append("\n		TO_CHAR (TO_DATE (TGFD.CHNG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD AM HH24:MI:SS') CHNG_ATON,  ");
		sql.append("\n		(SELECT COUNT (REPY_SEQ_NO) FROM BCDBA.TBGBBRDREPY WHERE TGFD.AFFI_ETHS_SEQ_NO = BBRD_SEQ_NO(+) AND REPY_CLSS = '0003') COMT_CNT  ");
		sql.append("\n FROM BCDBA.TBGAFFIETHS TGFD, 	");
		sql.append("\n (SELECT AFFI_ETHS_SEQ_NO, 	");
		sql.append("\n LTRIM (MAX (SYS_CONNECT_BY_PATH (AFFI_GREEN_SEQ_NO, ',')), ',') AFFI_GREEN_SEQ_NO 	");
		sql.append("\n 	FROM (SELECT		");
		sql.append("\n 		AFFI_ETHS_SEQ_NO, AFFI_GREEN_SEQ_NO, ROW_NUMBER () OVER (PARTITION BY AFFI_ETHS_SEQ_NO ORDER BY AFFI_GREEN_SEQ_NO) RN 	");
		sql.append("\n 		FROM (SELECT AFFI_ETHS_SEQ_NO, AFFI_GREEN_SEQ_NO FROM BCDBA.TBGETHSNGHBGREEN)) 	");
		sql.append("\n		START WITH RN = 1 	");
		sql.append("\n 	CONNECT BY PRIOR AFFI_ETHS_SEQ_NO = AFFI_ETHS_SEQ_NO 	");
		sql.append("\n 	AND PRIOR RN = RN - 1 	");
		sql.append("\n 	GROUP BY AFFI_ETHS_SEQ_NO) T 	");
		sql.append("\n WHERE TGFD.AFFI_ETHS_SEQ_NO = T.AFFI_ETHS_SEQ_NO(+) 	");
		sql.append("\n AND TGFD.AFFI_ETHS_SEQ_NO = ? 	");
		
		return sql.toString();
    }
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectQuery(String area_nm, String fd1_lev_cd, String fd2_lev_cd, String fd3_lev_cd, String search_sel, String search_word){
         StringBuffer sql = new StringBuffer();

         
         sql.append("\n SELECT 	");
         sql.append("\n     T.PRE_SEQ_NO, 	");
         sql.append("\n     NVL ((SELECT ETHS_NM FROM BCDBA.TBGAFFIETHS WHERE AFFI_ETHS_SEQ_NO = T.PRE_SEQ_NO), '') PRE_TITL, 		");
         sql.append("\n     T.NEXT_SEQ_NO,  	");
         sql.append("\n     NVL ((SELECT ETHS_NM FROM BCDBA.TBGAFFIETHS WHERE AFFI_ETHS_SEQ_NO = T.NEXT_SEQ_NO), '') NEXT_TITL    	");
         sql.append("\n FROM    ( 	");
         sql.append("\n             SELECT AFFI_ETHS_SEQ_NO, 	");
         sql.append("\n             NVL ((LEAD (AFFI_ETHS_SEQ_NO, 1) OVER (ORDER BY AFFI_ETHS_SEQ_NO ASC)), '') PRE_SEQ_NO, 	");
         sql.append("\n             NVL ((LAG (AFFI_ETHS_SEQ_NO, 1) OVER (ORDER BY AFFI_ETHS_SEQ_NO ASC)), '') NEXT_SEQ_NO 	");
         sql.append("\n             FROM BCDBA.TBGAFFIETHS 	");
         sql.append("\n             WHERE AFFI_ETHS_SEQ_NO = AFFI_ETHS_SEQ_NO	");
         
        /*
         if (!GolfUtil.isNull(area_nm)) sql.append("\n 	AND ADDR LIKE ?	"); // 지역 검색
		
         if (!GolfUtil.isNull(fd1_lev_cd)) sql.append("\n 	AND FOOD_SQ1_CTGO = ?	"); // 음식1차분류  검색
		
         if (!GolfUtil.isNull(fd2_lev_cd)) sql.append("\n 	AND FOOD_SQ2_CTGO = ?	"); // 음식2차분류  검색
		
         if (!GolfUtil.isNull(fd3_lev_cd)) sql.append("\n 	AND FOOD_SQ3_CTGO = ?	"); // 음식3차분류  검색
		
         if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //직접검색
		
			if (search_sel.equals("ALL")){ // 전체
				sql.append("\n 	AND (ETHS_NM LIKE ? 	");
				sql.append("\n 	OR CTNT LIKE ?		");
				sql.append("\n 	OR T.GREEN_NM LIKE ?	)	");
				
			} else {
				sql.append("\n 	AND "+search_sel+" LIKE ?	");
				}
         }
         */
         
         sql.append("\n         ) T      ");                                                             
         sql.append("\n WHERE AFFI_ETHS_SEQ_NO=? 	");
         
 		return sql.toString();
     }  
}
