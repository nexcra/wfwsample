/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfFieldListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 전국골프장 안내 리스트
*   적용범위  : golf
*   작성일자  : 2009-06-04
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
public class GolfFieldListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfFieldListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfFieldListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data, String[] gf_hole_cd, String[] gf_area_cd ) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		String area_nm = "";
		boolean flag1 = false;
		boolean flag2 = false;
		
		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			
			String search_cd		= data.getString("SEARCH_CD");
			String gf_clss_cd		= data.getString("GF_CLSS_CD");
			String grnfee_mb		= data.getString("GRNFEE_MB");
			String grnfee_wk		= data.getString("GRNFEE_WK");
			String grnfee_wkend		= data.getString("GRNFEE_WKEND");
			long grnfee_amt1		= data.getLong("GRNFEE_AMT1");
			long grnfee_amt2		= data.getLong("GRNFEE_AMT2");
			String sido		= data.getString("SIDO");
			String gugun		= data.getString("GUGUN");
			String dong		= data.getString("DONG");
			String gf_nm		= data.getString("GF_NM");
			
			/*
			if (!GolfUtil.isNull(sido) && GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) { 
				area_nm = sido;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun ;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && !GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun +" "+ dong ;
			}
			*/
			
			for (int i = 0; i < gf_hole_cd.length; i++) { 
				if (gf_hole_cd[i] != null && gf_hole_cd[i].length() > 0) {
					flag1 = true;
				}
			}
			
			for (int i = 0; i < gf_area_cd.length; i++) { 		
				if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
					flag2 = true;
				}
			}
			
			String sql = this.getSelectQuery(search_cd, gf_clss_cd, gf_hole_cd, gf_area_cd, grnfee_mb, grnfee_wk, 
					grnfee_wkend, grnfee_amt1, grnfee_amt2, sido, gugun, dong, gf_nm, flag1, flag2);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (search_cd.equals("1")) { // 상세검색
				
				if (!GolfUtil.isNull(gf_clss_cd)) pstmt.setString(++idx, gf_clss_cd); // 구분 검색
				
				for (int i = 0; i < gf_hole_cd.length; i++) { // 홀수 검색			
					if (gf_hole_cd[i] != null && gf_hole_cd[i].length() > 0) {
						pstmt.setString(++idx, gf_hole_cd[i]);
					}
				}
				
				for (int i = 0; i < gf_area_cd.length; i++) { // 지역별 검색			
					if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
						pstmt.setString(++idx, gf_area_cd[i]);
					}
				}
				
				if (grnfee_mb.equals("ALL") && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 그린피 검색
					if ((!GolfUtil.isNull(grnfee_wk) && !GolfUtil.isNull(grnfee_wkend)) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주중 & 주말
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						
					} else if (!GolfUtil.isNull(grnfee_wk) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주중
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						
					} else if (!GolfUtil.isNull(grnfee_wkend) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주말
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
					}
					
				} else {
					if ((!GolfUtil.isNull(grnfee_wk) && !GolfUtil.isNull(grnfee_wkend)) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주중 & 주말
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						
					} else if (!GolfUtil.isNull(grnfee_wk) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주중
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						
					} else if (!GolfUtil.isNull(grnfee_wkend) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주말
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
					}
				}
			} else if (search_cd.equals("2")) { // 지역검색
				
				if (!GolfUtil.isNull(sido)) pstmt.setString(++idx, "%"+sido+"%");  // 시도지역 검색
				
				if (!GolfUtil.isNull(gugun)) pstmt.setString(++idx, "%"+gugun+"%");  // 구군지역 검색
				
				if (!GolfUtil.isNull(dong)) pstmt.setString(++idx, "%"+dong+"%");  // 읍면동지역 검색
			}
			
			if (!GolfUtil.isNull(gf_nm)) pstmt.setString(++idx, gf_nm); // 골프장 검색
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("GF_SEQ_NO" 			,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("IMG_NM" 		,rs.getString("ANNX_IMG") );
					result.addString("GF_NM" 			,rs.getString("GREEN_NM") );
					result.addString("ZIPADDR" 			,rs.getString("ADDR") );
					result.addString("DETAILADDR"			,rs.getString("DTL_ADDR") );
					result.addString("GF_CLSS_CD"			,rs.getString("GREEN_CLSS") );
					result.addString("GF_HOLE_CD"			,rs.getString("GREEN_ODNO_CODE") );
										
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
    private String getSelectQuery(String search_cd, String gf_clss_cd, String[] gf_hole_cd, String[] gf_area_cd, String grnfee_mb, String grnfee_wk, 
    		String grnfee_wkend, long grnfee_amt1, long grnfee_amt2, String sido, String gugun, String dong, String gf_nm, boolean flag1, boolean flag2){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_GREEN_SEQ_NO, ANNX_IMG, GREEN_NM, ADDR, DTL_ADDR, GREEN_CLSS, GREEN_ODNO_CODE,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			TGF.AFFI_GREEN_SEQ_NO, TGF.ANNX_IMG, TGF.GREEN_NM, TGF.ADDR, TGF.DTL_ADDR, TMC1.GOLF_CMMN_CODE_NM GREEN_CLSS, TMC2.GOLF_CMMN_CODE_NM GREEN_ODNO_CODE   	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIGREEN TGF, BCDBA.TBGAFFIGREENROUNDINFO TGFR,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0019' AND USE_YN = 'Y') TMC1,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0020' AND USE_YN = 'Y') TMC2,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0021' AND USE_YN = 'Y') TMC3		");
		sql.append("\n 				WHERE TGF.AFFI_GREEN_SEQ_NO = TGFR.AFFI_GREEN_SEQ_NO(+)	");
		sql.append("\n 				AND TGF.GREEN_CLSS = TMC1.GOLF_CMMN_CODE(+)	");
		sql.append("\n 				AND TGF.GREEN_ODNO_CODE = TMC2.GOLF_CMMN_CODE(+)	");
		sql.append("\n 				AND TGF.GREEN_RGN_CLSS = TMC3.GOLF_CMMN_CODE(+)	");
		sql.append("\t 					AND TGF.AFFI_FIRM_CLSS = '0004'	");	
		
		if (search_cd.equals("1")) { // 상세검색
			if (!GolfUtil.isNull(gf_clss_cd)) sql.append("\n 	AND TGF.GREEN_CLSS = ?	"); // 구분 검색
			
			if (flag1) { // 홀수 검색	
				sql.append("\n 	AND ( ");
				
				for (int i = 0; i < gf_hole_cd.length; i++) { 		
					if (gf_hole_cd[i] != null && gf_hole_cd[i].length() > 0) {
						if (i > 0) sql.append("\n 	OR ");
						sql.append("\n 	TMC2.GOLF_CMMN_CODE = ? ");
					}
				}
				
				sql.append("\n 		) ");
			}
			
			if (flag2) { // 지역별 검색	
				sql.append("\n 	AND ( ");
				
				for (int i = 0; i < gf_area_cd.length; i++) { 		
					if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
						if (i > 0) sql.append("\n 	OR ");
						sql.append("\n 	TMC3.GOLF_CMMN_CODE = ? ");
					}
				}
				
				sql.append("\n 		) ");
			}
		
			if (grnfee_mb.equals("ALL") && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 그린피 검색
				if ((!GolfUtil.isNull(grnfee_wk) && !GolfUtil.isNull(grnfee_wkend)) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주중 & 주말
					sql.append("\n 	AND (NVL(TGFR.GRNFEE_"+grnfee_wk+"_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_NON_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_WKD_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_FMLY_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_NON_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_WKD_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_FMLY_CDHD_CHRG, 0) BETWEEN ? AND ?)	");
					
				} else if (!GolfUtil.isNull(grnfee_wk) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주중
					sql.append("\n 	AND (NVL(TGFR.GREEN_"+grnfee_wk+"_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_NON_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_WKD_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_FMLY_CDHD_CHRG, 0) BETWEEN ? AND ?)	");
					
				} else if (!GolfUtil.isNull(grnfee_wkend) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주말
					sql.append("\n 	AND (NVL(TGFR.GREEN_"+grnfee_wkend+"_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_NON_CDHD_CHRG, 0) BETWEEN ? AND ?		");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_WKD_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_FMLY_CDHD_CHRG, 0) BETWEEN ? AND ?)	");
				} 
				
			} else {
				if ((!GolfUtil.isNull(grnfee_wk) && !GolfUtil.isNull(grnfee_wkend)) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주중 & 주말
					sql.append("\n 	AND (NVL(TGFR.GREEN_"+grnfee_wk+"_"+grnfee_mb+"_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_"+grnfee_mb+"_CHRG, 0) BETWEEN ? AND ?)	");
					
				} else if (!GolfUtil.isNull(grnfee_wk) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주중
					sql.append("\n 	AND NVL(TGFR.GREEN_"+grnfee_wk+"_"+grnfee_mb+"_CHRG, 0) BETWEEN ? AND ?	");
					
				} else if (!GolfUtil.isNull(grnfee_wkend) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // 주말
					sql.append("\n 	AND NVL(TGFR.GREEN_"+grnfee_wkend+"_"+grnfee_mb+"_CHRG, 0) BETWEEN ? AND ?	");
				}
			}
			
		} else if (search_cd.equals("2")) { // 지역검색
			if (!GolfUtil.isNull(sido)) sql.append("\n 	AND REPLACE(TGF.ADDR, ' ', '') LIKE ?	"); // 시도지역 검색
			
			if (!GolfUtil.isNull(gugun)) sql.append("\n 	AND REPLACE(TGF.ADDR, ' ', '') LIKE ?	"); // 구군지역 검색
			
			if (!GolfUtil.isNull(dong)) sql.append("\n 	AND REPLACE(TGF.ADDR, ' ', '') LIKE ?	"); // 읍면동지역 검색
		}
		
		if (!GolfUtil.isNull(gf_nm)) sql.append("\n 	AND TGF.GREEN_NM = ?	"); // 골프장 검색
	
	
		sql.append("\n 				ORDER BY TGF.AFFI_GREEN_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");	
		
    	return sql.toString();
    }
}
