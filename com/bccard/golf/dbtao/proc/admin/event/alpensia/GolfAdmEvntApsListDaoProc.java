/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntApsListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 알펜시아 > 리스트
*   적용범위  : golf
*   작성일자  : 2010-06-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.alpensia;

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
public class GolfAdmEvntApsListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmOrdListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntApsListDaoProc() {}	

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
			String sch_yn					= data.getString("sch_yn");					// 검색여부
			String sch_date_st				= data.getString("sch_date_st");			// 신청일자 - 시작
			String sch_date_ed				= data.getString("sch_date_ed");			// 신청일자 - 종료
			String sch_type					= data.getString("sch_type");				// 직접검색 종류
			String sch_text					= data.getString("sch_text");				// 직접검색 텍스트
			String sch_golf_svc_aplc_clss	= data.getString("sch_golf_svc_aplc_clss");	// 프로그램
			String sch_evnt_pgrs_clss		= data.getString("sch_evnt_pgrs_clss");		// 참가등록 구분코드
			String sch_rsvt_date			= data.getString("sch_rsvt_date");			// 예약일자
			String sch_rsv_time				= data.getString("sch_rsv_time");			// 예약시간
						
			String sql = this.getSelectQuery(data);

			// 입력값 (INPUT) 
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, 20);
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			if(!GolfUtil.empty(sch_yn)){
				if(!GolfUtil.empty(sch_date_st))			pstmt.setString(++idx, sch_date_st+"000000");
				if(!GolfUtil.empty(sch_date_ed))			pstmt.setString(++idx, sch_date_ed+"999999");
				if(!GolfUtil.empty(sch_text)){
					if("MOBILE".equals(sch_type)){
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
					}else{
						pstmt.setString(++idx, "%"+sch_text+"%");
					}
				}
				if(!GolfUtil.empty(sch_golf_svc_aplc_clss))	pstmt.setString(++idx, sch_golf_svc_aplc_clss);
				if(!GolfUtil.empty(sch_evnt_pgrs_clss))		pstmt.setString(++idx, sch_evnt_pgrs_clss);
				if(!GolfUtil.empty(sch_rsvt_date))			pstmt.setString(++idx, sch_rsvt_date);
				if(!GolfUtil.empty(sch_rsv_time))			pstmt.setString(++idx, sch_rsv_time);
			}
			
			pstmt.setLong(++idx, data.getLong("page_no"));
			rs = pstmt.executeQuery();
			
			String sttl_amt = "";
			String opt_yn = "";
			String evt_pnum = "";
			int rsv_time = 0;
			String str_rsv_time = "";
			
			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
				        
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
															
					if(!GolfUtil.empty(rs.getString("OPT_YN"))){
						opt_yn = GolfUtil.comma(rs.getString("OPT_YN"));
						if(opt_yn.equals("N")){
							opt_yn = "4인1실";
						}else if(opt_yn.equals("Y")){
							opt_yn = "2인1실";
						}
					}else{
						opt_yn = "";
					}
					
					if(!GolfUtil.empty(rs.getString("STTL_AMT"))){
						sttl_amt = GolfUtil.comma(rs.getString("STTL_AMT"));
					}else{
						sttl_amt = "";
					}
					
					if(rs.getString("GOLF_SVC_APLC_CLSS").equals("8001")){
						evt_pnum = rs.getString("CDHD_GRD_SEQ_NO");
					}else{
						evt_pnum = rs.getString("EVT_PNUM");
					}
					
					if(!GolfUtil.empty(rs.getString("RSV_TIME"))){
						rsv_time = rs.getInt("RSV_TIME")/100;
						str_rsv_time = "<br>"+rsv_time+"시대";
					}else{
						str_rsv_time = "";
					}
					
					
										
					result.addString("APLC_SEQ_NO" 		,rs.getString("APLC_SEQ_NO") );
					result.addString("STR_STAT"			,rs.getString("STR_STAT") );
					result.addString("GUBN_STAT"		,rs.getString("GUBN_STAT") );
					result.addString("RSVT_DATE" 		,rs.getString("RSVT_DATE") );
					result.addString("RSV_TIME" 		,str_rsv_time );
					result.addString("BKG_PE_NM" 		,rs.getString("BKG_PE_NM") );
					result.addString("HP" 				,rs.getString("HP") );
					result.addString("EVT_PNUM"			,evt_pnum );
					result.addString("OPT_YN"			,opt_yn );
					result.addString("STTL_AMT" 		,sttl_amt );
					result.addString("REG_DATE" 		,rs.getString("REG_DATE") );
					result.addString("GOLF_SVC_APLC_CLSS",rs.getString("GOLF_SVC_APLC_CLSS") );
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

		String sch_yn					= data.getString("sch_yn");					// 검색여부
		String sch_date_st				= data.getString("sch_date_st");			// 신청일자 - 시작
		String sch_date_ed				= data.getString("sch_date_ed");			// 신청일자 - 종료
		String sch_type					= data.getString("sch_type");				// 직접검색 종류
		String sch_text					= data.getString("sch_text");				// 직접검색 텍스트
		String sch_golf_svc_aplc_clss	= data.getString("sch_golf_svc_aplc_clss");	// 프로그램
		String sch_evnt_pgrs_clss		= data.getString("sch_evnt_pgrs_clss");		// 참가등록 구분코드
		String sch_rsvt_date			= data.getString("sch_rsvt_date");			// 예약일자
		String sch_rsv_time				= data.getString("sch_rsv_time");			// 예약시간
		
		sql.append("\n	SELECT *	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE, MAX(RNUM) OVER() TOT_CNT, ((MAX(RNUM) OVER())-(?-1)*20) AS ART_NUM	\n");
		sql.append("\t	        , APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, STR_STAT, GUBN_STAT, RSVT_DATE, RSV_TIME, BKG_PE_NM, HP	\n");
		sql.append("\t	        , CDHD_GRD_SEQ_NO, EVT_PNUM, OPT_YN, STTL_AMT, REG_DATE	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT ROWNUM RNUM	\n");
		sql.append("\t			, EVT.APLC_SEQ_NO, EVT.GOLF_SVC_APLC_CLSS, STAT.GOLF_CMMN_CODE_NM STR_STAT , GUBN.GOLF_CMMN_CODE_NM GUBN_STAT	\n");
		sql.append("\t	       	, TO_CHAR(TO_DATE(EVT.RSVT_DATE),'YYYY-MM-DD') RSVT_DATE, EVT.RSV_TIME	\n");
		sql.append("\t	       	, EVT.BKG_PE_NM, (EVT.HP_DDD_NO||'-'||EVT.HP_TEL_HNO||'-'||EVT.HP_TEL_SNO) HP	\n");
		sql.append("\t	       	, EVT.CDHD_GRD_SEQ_NO	\n");
		sql.append("\t	       	, (SELECT COUNT(*) FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=EVT.APLC_SEQ_NO) EVT_PNUM	\n");
		sql.append("\t	       	, EVT.OPT_YN, EVT.STTL_AMT	\n");
		sql.append("\t	       	, TO_CHAR(TO_DATE(SUBSTR(EVT.APLC_ATON,1,8)),'YYYY-MM-DD') REG_DATE	\n");
		
		sql.append("\t	       	FROM BCDBA.TBGGOLFEVNTAPLC EVT	\n");
		sql.append("\t	       	JOIN BCDBA.TBGCMMNCODE STAT ON EVT.EVNT_PGRS_CLSS=STAT.GOLF_CMMN_CODE AND STAT.GOLF_CMMN_CLSS='0057'	\n");
		sql.append("\t	       	JOIN BCDBA.TBGCMMNCODE GUBN ON EVT.GOLF_SVC_APLC_CLSS=GUBN.GOLF_CMMN_CODE AND GUBN.GOLF_CMMN_CLSS='0048'	\n");
		sql.append("\t	       	WHERE EVT.GOLF_SVC_APLC_CLSS IN ('8001', '8002', '8003')	\n");
		
		if(!GolfUtil.empty(sch_yn)){
			if(!GolfUtil.empty(sch_date_st))			sql.append("\t	            AND EVT.APLC_ATON>=?	\n");
			if(!GolfUtil.empty(sch_date_ed))			sql.append("\t	            AND EVT.APLC_ATON<=?	\n");
			if(!GolfUtil.empty(sch_type) && !GolfUtil.empty(sch_text)){
				if("MOBILE".equals(sch_type))			sql.append("\t	            AND (EVT.HP_DDD_NO LIKE ? OR EVT.HP_TEL_HNO LIKE ? OR EVT.HP_TEL_SNO LIKE ?)	\n");
				else									sql.append("\t	            AND "+sch_type+" LIKE ?	\n");
			}
			if(!GolfUtil.empty(sch_golf_svc_aplc_clss))	sql.append("\t	            AND EVT.GOLF_SVC_APLC_CLSS=?	\n");	
			if(!GolfUtil.empty(sch_evnt_pgrs_clss))		sql.append("\t	            AND EVT.EVNT_PGRS_CLSS=?	\n");
			if(!GolfUtil.empty(sch_rsvt_date))			sql.append("\t	            AND EVT.RSVT_DATE=?	\n");
			if(!GolfUtil.empty(sch_rsv_time))			sql.append("\t	            AND EVT.RSV_TIME=?	\n");
		}
		
		sql.append("\t	        ORDER BY APLC_ATON DESC	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	    ORDER BY RNUM	\n");
		sql.append("\t	)	\n");
		sql.append("\t	WHERE PAGE=?	\n");
		
		return sql.toString();
    }

    
	/** ***********************************************************************
     * 검색 - 예약일 리스트
     ************************************************************************ */
     private String getSchDateQuery(){
         StringBuffer sql = new StringBuffer();
  		sql.append("\n	SELECT RSVT_DATE, TO_CHAR(TO_DATE(RSVT_DATE),'YYYY-MM-DD') RSVT_DATE_STR	\n");
 		sql.append("\t	FROM (SELECT DISTINCT(RSVT_DATE) RSVT_DATE FROM BCDBA.TBGGOLFEVNTAPLC WHERE GOLF_SVC_APLC_CLSS IN ('8001', '8002', '8003'))	\n");
 		sql.append("\t	ORDER BY RSVT_DATE DESC	\n");
 		return sql.toString();
     }
}
