/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBnstListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 베네스트 > 리스트
*   적용범위  : golf
*   작성일자  : 2010-03-23
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.benest;

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
import com.bccard.waf.tao.TaoException;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0 
 ******************************************************************************/
public class GolfAdmEvntBnstListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmOrdListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBnstListDaoProc() {}	

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
			
			// 검색값

			String sch_yn				= data.getString("sch_yn");
			String sch_date				= data.getString("sch_date");
			String sch_date_st			= data.getString("sch_date_st");
			String sch_date_ed			= data.getString("sch_date_ed");	
			String sch_type				= data.getString("sch_type");
			String sch_text				= data.getString("sch_text");
			String sch_sttl_stat_clss	= data.getString("sch_sttl_stat_clss");		// 결제상태 구분코드
			String sch_evnt_pgrs_clss	= data.getString("sch_evnt_pgrs_clss");		// 참가등록 구분코드
			String sch_green_nm			= data.getString("sch_green_nm");
			String sch_rsvt_date		= data.getString("sch_rsvt_date");
			String sch_rsv_time			= data.getString("sch_rsv_time"); 
			
			String sql = this.getSelectQuery(data);

			// 입력값 (INPUT) 
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, 20);
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			if(!GolfUtil.empty(sch_yn)){
				if(sch_date.equals("aplc_aton")){
					if(!GolfUtil.empty(sch_date_st))	pstmt.setString(++idx, sch_date_st+"000000");
					if(!GolfUtil.empty(sch_date_ed))	pstmt.setString(++idx, sch_date_ed+"999999");
				}else{
					if(!GolfUtil.empty(sch_date_st))	pstmt.setString(++idx, sch_date_st);
					if(!GolfUtil.empty(sch_date_ed))	pstmt.setString(++idx, sch_date_ed);
				}
				if(!GolfUtil.empty(sch_sttl_stat_clss))	pstmt.setString(++idx, sch_sttl_stat_clss);
				if(!GolfUtil.empty(sch_evnt_pgrs_clss))	pstmt.setString(++idx, sch_evnt_pgrs_clss);
				if(!GolfUtil.empty(sch_green_nm))	pstmt.setString(++idx, sch_green_nm);
				if(!GolfUtil.empty(sch_rsvt_date))	pstmt.setString(++idx, sch_rsvt_date);
				if(!GolfUtil.empty(sch_text)){
					if("MOBILE".equals(sch_type)){
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
					}else{
						pstmt.setString(++idx, "%"+sch_text+"%");
					}
				}
			}
			
			pstmt.setLong(++idx, data.getLong("page_no"));
						
			rs = pstmt.executeQuery();
			
			String evnt_pgrs_clss = "";
			String sttl_stat_clss = "";
			String sttl_amt = "";
			
			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
				        
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
					
					evnt_pgrs_clss = rs.getString("EVNT_PGRS_CLSS");
					sttl_stat_clss = rs.getString("STTL_STAT_CLSS");
					
					if(evnt_pgrs_clss.equals("R")){
						evnt_pgrs_clss = "신청";
					}else if(evnt_pgrs_clss.equals("A")){ 
						evnt_pgrs_clss = "대기";
					}else if(evnt_pgrs_clss.equals("P")){
						evnt_pgrs_clss = "결제진행";
					}else if(evnt_pgrs_clss.equals("B")){
						evnt_pgrs_clss = "확정";
					}else if(evnt_pgrs_clss.equals("C")){
						evnt_pgrs_clss = "예약취소";
					}else if(evnt_pgrs_clss.equals("E")){
						evnt_pgrs_clss = "결제취소"; 
					}
					
					if(sttl_stat_clss.equals("0")){
						sttl_stat_clss = "미결제";
					}else if(sttl_stat_clss.equals("1")){
						sttl_stat_clss = "결제완료";
					}else if(sttl_stat_clss.equals("2")){
						sttl_stat_clss = "결제취소";
					}
					
					if(!GolfUtil.empty(rs.getString("STTL_AMT"))){
						sttl_amt = GolfUtil.comma(rs.getString("STTL_AMT"));
					}else{
						sttl_amt = "";
					}
										
					result.addString("APLC_SEQ_NO" 			,rs.getString("APLC_SEQ_NO") );
					result.addString("TRM_UNT" 			,rs.getString("TRM_UNT") );
					result.addString("CDHD_ID"				,rs.getString("CDHD_ID") );
					result.addString("BKG_PE_NM" 			,rs.getString("BKG_PE_NM") );
					result.addString("HP_DDD_NO" 			,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO" 			,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO" 			,rs.getString("HP_TEL_SNO") );
					result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );
					result.addString("REG_DATE" 			,rs.getString("REG_DATE") );
					result.addString("GRD_NM" 				,rs.getString("GRD_NM") );
					result.addString("STTL_AMT" 			,sttl_amt );
					result.addString("EVNT_PGRS_CLSS" 		,evnt_pgrs_clss );
					result.addString("STTL_STAT_CLSS" 		,sttl_stat_clss );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("RSVT_DATE" 			,rs.getString("RSVT_DATE") );
					result.addString("RSV_TIME" 			,rs.getString("RSV_TIME") );
					result.addString("HADC_NUM" 			,rs.getString("HADC_NUM") );
					result.addString("EVT_PNUM" 			,rs.getString("EVT_PNUM") );
					result.addString("VIEW_TYPE" 			,rs.getString("VIEW_TYPE") ); //새로이 적용  날짜에 따라 VIEW_TYPE적용
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

	// 검색 - 골프장리스트
	public DbTaoResult execute_schGreen(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			String sql = this.getSchGreenQuery();
			pstmt = conn.prepareStatement(sql.toString());			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {	
					result.addString("GREEN_NM"			,rs.getString("GREEN_NM") );
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

	// 검색 - 예약일 리스트
	public DbTaoResult execute_schDate(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			String sql = this.getSchDateQuery();
			pstmt = conn.prepareStatement(sql.toString());			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {	
					result.addString("RSVT_DATE"			,rs.getString("RSVT_DATE") );
					result.addString("RSVT_DATE_STR"		,rs.getString("RSVT_DATE_STR") );
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
    * 구매 리스트
	 * @throws TaoException 
    ************************************************************************ */
    private String getSelectQuery(TaoDataSet data) throws TaoException{
        StringBuffer sql = new StringBuffer();

		String sch_yn				= data.getString("sch_yn");
		String sch_date				= data.getString("sch_date");
		String sch_date_st			= data.getString("sch_date_st");
		String sch_date_ed			= data.getString("sch_date_ed");	
		String sch_type				= data.getString("sch_type");
		String sch_text				= data.getString("sch_text");
		String sch_sttl_stat_clss	= data.getString("sch_sttl_stat_clss");		// 결제상태 구분코드
		String sch_evnt_pgrs_clss	= data.getString("sch_evnt_pgrs_clss");		// 참가등록 구분코드
		String sch_green_nm			= data.getString("sch_green_nm");
		String sch_rsvt_date		= data.getString("sch_rsvt_date");
		String sch_rsv_time			= data.getString("sch_rsv_time");
		debug("sch_rsv_time : " + sch_rsv_time);
        
		sql.append("\n	SELECT *	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE, MAX(RNUM) OVER() TOT_CNT, ((MAX(RNUM) OVER())-(?-1)*20) AS ART_NUM	\n");
		sql.append("\t	        , APLC_SEQ_NO, TRM_UNT,  CDHD_ID, BKG_PE_NM, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, JUMIN_NO, HADC_NUM	\n");
		sql.append("\t	        , REG_DATE, CDHD_GRD_SEQ_NO, EVNT_PGRS_CLSS, GRD_NM, STTL_AMT, STTL_STAT_CLSS, GREEN_NM, RSVT_DATE, RSV_TIME, EVT_PNUM	\n");
		sql.append("\t	        , CASE WHEN REG_DATE > '2010-10-01' THEN 'SAVE' ELSE 'HARD' END VIEW_TYPE	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT ROWNUM RNUM	\n");
		
		sql.append("\t	        , EVT.APLC_SEQ_NO,EVT.TRM_UNT, EVT.CDHD_ID, EVT.BKG_PE_NM, EVT.HP_DDD_NO, EVT.HP_TEL_HNO, EVT.HP_TEL_SNO, EVT.JUMIN_NO	\n");
		sql.append("\t	       	, TO_CHAR(TO_DATE(SUBSTR(EVT.APLC_ATON,1,8)),'YYYY-MM-DD') REG_DATE, EVT.CDHD_GRD_SEQ_NO, EVT.EVNT_PGRS_CLSS, EVT.HADC_NUM	\n");
		sql.append("\t	       	, CODE.GOLF_CMMN_CODE_NM GRD_NM, EVT.STTL_AMT, EVT.STTL_STAT_CLSS, EVT.GREEN_NM, TO_CHAR(TO_DATE(EVT.RSVT_DATE),'YYYY-MM-DD') RSVT_DATE	\n");
		sql.append("\t	        , CASE WHEN SUBSTR(EVT.RSV_TIME,1,2)>12 THEN '오후' ELSE '오전' END RSV_TIME	\n");
		sql.append("\t	        , (SELECT COUNT(*) FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=EVT.APLC_SEQ_NO) EVT_PNUM	\n");
		sql.append("\t	       	FROM BCDBA.TBGGOLFEVNTAPLC EVT	\n");
		sql.append("\t	       	LEFT JOIN BCDBA.TBGGOLFCDHD CDHD ON EVT.CDHD_ID=CDHD.CDHD_ID	\n");
		sql.append("\t	       	LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON CDHD.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	       	LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND GOLF_CMMN_CLSS='0005'	\n");
		
		 
		sql.append("\t	        WHERE APLC_SEQ_NO IS NOT NULL AND GOLF_SVC_APLC_CLSS='9003'	\n");

		if(!GolfUtil.empty(sch_yn)){
			if(sch_date.equals("aplc_aton")){
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND EVT.APLC_ATON>=?	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND EVT.APLC_ATON<=?	\n");
			}else{
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND EVT.RSVT_DATE>=?	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND EVT.RSVT_DATE<=?	\n");
			}
			if(!GolfUtil.empty(sch_sttl_stat_clss))	sql.append("\t	            AND EVT.STTL_STAT_CLSS=?	\n");
			if(!GolfUtil.empty(sch_evnt_pgrs_clss))	sql.append("\t	            AND EVT.EVNT_PGRS_CLSS=?	\n");
			if(!GolfUtil.empty(sch_green_nm))		sql.append("\t	            AND EVT.GREEN_NM=?	\n");
			if(!GolfUtil.empty(sch_rsvt_date))		sql.append("\t	            AND EVT.RSVT_DATE=?	\n");
			if(!GolfUtil.empty(sch_rsv_time)){
				if(sch_rsv_time.equals("1")){
					sql.append("\t	            AND SUBSTR(EVT.RSV_TIME,1,2)<=12	\n");
				}else if(sch_rsv_time.equals("2")){
					sql.append("\t	            AND SUBSTR(EVT.RSV_TIME,1,2)>12	\n");
				}
			}

			if(!GolfUtil.empty(sch_type) && !GolfUtil.empty(sch_text)){
				if("MOBILE".equals(sch_type)){
					sql.append("\t	            AND (EVT.HP_DDD_NO LIKE ? OR EVT.HP_TEL_HNO LIKE ? OR EVT.HP_TEL_SNO LIKE ?)	\n");
				}else{
					sql.append("\t	            AND "+sch_type+" LIKE ?	\n");
				}
			}
		}
		
		sql.append("\t	        ORDER BY APLC_ATON DESC	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	    ORDER BY RNUM	\n");
		sql.append("\t	)	\n");
		sql.append("\t	WHERE PAGE=?	\n");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * 검색 - 골프장리스트
    ************************************************************************ */
    private String getSchGreenQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT DISTINCT(GREEN_NM) GREEN_NM FROM BCDBA.TBGGOLFEVNTAPLC	\n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * 검색 - 예약일 리스트
     ************************************************************************ */
     private String getSchDateQuery(){
         StringBuffer sql = new StringBuffer();
  		sql.append("\n	SELECT RSVT_DATE, TO_CHAR(TO_DATE(RSVT_DATE),'YYYY-MM-DD') RSVT_DATE_STR	\n");
 		sql.append("\t	FROM (SELECT DISTINCT(RSVT_DATE) RSVT_DATE FROM BCDBA.TBGGOLFEVNTAPLC)	\n");
 		sql.append("\t	ORDER BY RSVT_DATE DESC	\n");
 		return sql.toString();
     }
}
