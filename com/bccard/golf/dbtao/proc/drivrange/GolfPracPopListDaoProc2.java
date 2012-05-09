/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPracListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 드라이빙레인지/스크린 할인쿠폰
*   적용범위  : golf
*   작성일자  : 2009-06-13
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.drivrange;

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
public class GolfPracPopListDaoProc2 extends AbstractProc {
	
	/** *****************************************************************
	 * GolfPracListDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfPracPopListDaoProc2() {}	

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
			//String exec_type_cd		= data.getString("EXEC_TYPE_CD");
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			if (!GolfUtil.isNull(sido) && GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) {
				area_nm = sido;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun ;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && !GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun +" "+ dong ;
			}
			
			String sql = this.getSelectQuery(area_nm,search_sel,search_word);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (!GolfUtil.isNull(area_nm)) pstmt.setString(++idx, "%"+area_nm+"%");  // 지역 검색
			
			//if (!GolfUtil.isNull(exec_type_cd)) pstmt.setString(++idx, exec_type_cd); // 구분 검색
			
			if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ // 직접검색
				
				if (search_sel.equals("ALL")){ //전체
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					
				} else if (search_sel.equals("ADDR")){ //주소
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("CHG_TEL")){ //전화번호
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("DC_RT")){ //할인율
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}	else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}

			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("GF_SEQ_NO" 			,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("EXEC_TYPE_CD" 		,rs.getString("GOLF_RNG_CLSS") );
					result.addString("GF_NM" 			,rs.getString("GREEN_NM") );
					result.addString("ZIPCODE" 			,rs.getString("ZP") );
					result.addString("ZIPADDR" 			,rs.getString("ADDR") );
					result.addString("DETAILADDR" 			,rs.getString("DTL_ADDR") );
					result.addString("CHG_DDD_NO"			,rs.getString("DDD_NO") );
					result.addString("CHG_TEL_HNO"			,rs.getString("TEL_HNO") );
					result.addString("CHG_TEL_SNO"			,rs.getString("TEL_SNO") );
					result.addString("IMG_NM"			,rs.getString("ANNX_IMG") );
					result.addString("MAP_NM"			,rs.getString("OLM_IMG") );
					result.addString("URL"			,rs.getString("GREEN_HPGE_URL") );
					result.addString("GF_SEARCH"			,rs.getString("POS_EXPL") );
					result.addLong("CUPN_SEQ_NO"			,rs.getLong("CUPN_SEQ_NO") );
					result.addString("REG_PE_ID"			,rs.getString("REG_MGR_ID") );
					result.addString("CORR_PE_ID"			,rs.getString("CHNG_MGR_ID") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("CORR_ATON"			,rs.getString("CHNG_ATON") );
					result.addLong("CUPN_DC_RT"			,rs.getLong("DC_RT") );
					
					
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
    private String getSelectQuery(String area_nm, String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_GREEN_SEQ_NO, GOLF_RNG_CLSS, GREEN_NM, ZP, ADDR, DTL_ADDR, DDD_NO,   	");
		sql.append("\n 			TEL_HNO, TEL_SNO, ANNX_IMG, OLM_IMG, GREEN_HPGE_URL, POS_EXPL, 	");
		sql.append("\n 			CUPN_SEQ_NO, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON, DC_RT, 	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			TGF.AFFI_GREEN_SEQ_NO, (SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0008' AND USE_YN = 'Y' AND GOLF_CMMN_CODE = TGF.GOLF_RNG_CLSS) GOLF_RNG_CLSS,   	");
		sql.append("\n 			TGF.GREEN_NM, TGF.ZP, TGF.ADDR, TGF.DTL_ADDR, TGF.DDD_NO,   	");
		sql.append("\n 			TGF.TEL_HNO, TGF.TEL_SNO, TGF.ANNX_IMG, TGF.OLM_IMG, TGF.GREEN_HPGE_URL, TGF.POS_EXPL, 	");
		sql.append("\n 			TGF.CUPN_SEQ_NO, TGF.REG_MGR_ID, TGF.CHNG_MGR_ID,  	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGF.REG_ATON, 'YYYYMMDDHH24MISS'), 'YY-MM-DD') ||'('|| SUBSTR (TO_CHAR (TO_DATE (TGF.REG_ATON, 'YYYYMMDDHH24MISS'), 'DAY'), 1, 1) ||') ' || TO_CHAR (TO_DATE (TGF.REG_ATON, 'YYYYMMDDHH24MISS'), 'HH24:MI') REG_ATON, 	");
		sql.append("\n 			TGF.CHNG_ATON, TGC.DC_RT 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIGREEN TGF, BCDBA.TBGCUPNMGMT TGC	");
		sql.append("\n 				WHERE TGF.AFFI_GREEN_SEQ_NO = TGF.AFFI_GREEN_SEQ_NO AND TGF.CUPN_SEQ_NO = TGC.CUPN_SEQ_NO(+)	");
		sql.append("\t 					AND TGF.AFFI_FIRM_CLSS = '0003'	");	
		sql.append("\n 					AND TGF.GOLF_RNG_CLSS = '0002'	");
		
		
		if (!GolfUtil.isNull(area_nm)) sql.append("\n 	AND TGF.ADDR LIKE ?	"); // 지역 검색
		
		if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ // 직접검색
			
			if (search_sel.equals("ALL")){ //전체
				sql.append("\n 	AND (TGF.GREEN_NM LIKE ?	");
				sql.append("\n 	OR TGF.ADDR LIKE ? 	");
				sql.append("\n 	OR TGF.DDD_NO || TGF.TEL_HNO || TGF.TEL_SNO LIKE ? 	");
				sql.append("\n 	OR TGC.DC_RT LIKE ?)	");
				
			} else if (search_sel.equals("ADDR")){ //주소
				sql.append("\n 	AND TGF.ADDR || TGF.DTL_ADDR LIKE ? 	");
				
			}  else if (search_sel.equals("CHG_TEL")){ //전화번호
				sql.append("\n 	AND TGF.DDD_NO || TGF.TEL_HNO || TGF.TEL_SNO LIKE ?	");
				
			}  else if (search_sel.equals("DC_RT")){ //할인율
				sql.append("\n 	AND TGC.DC_RT LIKE ?	");
				
			}	else {
				sql.append("\n 	AND "+search_sel+" LIKE ?	");
			}
		}
		
		
		sql.append("\n 				ORDER BY TGF.AFFI_GREEN_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		
		
     	return sql.toString();
    }
}
