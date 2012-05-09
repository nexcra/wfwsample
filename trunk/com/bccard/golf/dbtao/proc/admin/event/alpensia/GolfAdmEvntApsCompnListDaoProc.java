/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntApsCompnListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 알펜시아 > 상세보기
*   적용범위  : golf
*   작성일자  : 2010-06-24
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
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
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0 
 ******************************************************************************/
public class GolfAdmEvntApsCompnListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmOrdListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntApsCompnListDaoProc() {}	

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

			String aplc_seq_no		= data.getString("aplc_seq_no");
			String sql = this.getSelectQuery();

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, aplc_seq_no);
			rs = pstmt.executeQuery();

			String sttl_amt = "";
			String golf_svc_aplc_clss = "";

			if(rs != null) {			 

				while(rs.next())  {

					if(!GolfUtil.empty(rs.getString("STTL_AMT"))){
						sttl_amt = GolfUtil.comma(rs.getString("STTL_AMT"));
					}else{
						sttl_amt = "";
					}

					if(!GolfUtil.empty(rs.getString("GOLF_SVC_APLC_CLSS"))){
						golf_svc_aplc_clss = GolfUtil.comma(rs.getString("GOLF_SVC_APLC_CLSS"));
					}else{
						golf_svc_aplc_clss = "";
					}
					
					result.addString("APLC_SEQ_NO" 			,rs.getString("APLC_SEQ_NO") );
					result.addString("SVC_NM" 				,rs.getString("SVC_NM") );
					result.addString("CDHD_ID"				,rs.getString("CDHD_ID") );
					result.addString("GRD_NM" 				,rs.getString("GRD_NM") );
					result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );
					result.addString("BKG_PE_NM" 			,rs.getString("BKG_PE_NM") );
					result.addString("HP" 					,rs.getString("HP") );
					result.addString("PHONE" 				,rs.getString("PHONE") );
					result.addString("REG_DATE" 			,rs.getString("REG_DATE") );
					result.addString("RSVT_DATE" 			,rs.getString("RSVT_DATE") );
					result.addString("RSV_TIME" 			,rs.getString("RSV_TIME") );
					result.addString("TRM_UNT" 				,rs.getString("TRM_UNT") );
					result.addString("RSVT_END_DATE" 		,rs.getString("RSVT_END_DATE") );
					result.addString("STAT_NM" 				,rs.getString("STAT_NM") );
					result.addString("STTL_AMT" 			,sttl_amt );
					result.addString("TNUM" 				,rs.getString("TNUM") );
					result.addString("PNUM" 				,rs.getString("PNUM") );
					result.addString("ORD_CNT" 				,rs.getString("ORD_CNT") );
					result.addString("MGR_MEMO" 			,rs.getString("MGR_MEMO") );
					result.addString("CUS_RMRK" 			,rs.getString("CUS_RMRK") );
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

	public DbTaoResult execute_list(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값

			String aplc_seq_no		= data.getString("aplc_seq_no");
			String sql = this.getSelectQuery();

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, aplc_seq_no);
						
			rs = pstmt.executeQuery();


			String aplc_pe_clss = "";		// 신청자구분코드
			String cdhd_non_cdhd_clss = "";	// 회원비회원구분코드 
			String sttl_stat_clss = "";
			String sttl_amt = "";
			String cdhd_grd_seq_no = "";
			String max_seq_no = "";
			
			if(rs != null) {			 

				while(rs.next())  {	

					aplc_pe_clss = rs.getString("APLC_PE_CLSS");
					cdhd_non_cdhd_clss = rs.getString("CDHD_NON_CDHD_CLSS");
					sttl_stat_clss = rs.getString("STTL_STAT_CLSS");
					
					if(aplc_pe_clss.equals("1")){
						aplc_pe_clss = "신청자";
					}else if(aplc_pe_clss.equals("2")){
						aplc_pe_clss = "동반자";
					}
					
					if(!GolfUtil.empty(cdhd_non_cdhd_clss)){
						if(cdhd_non_cdhd_clss.equals("1")){
							cdhd_non_cdhd_clss = "회원";
						}else if(cdhd_non_cdhd_clss.equals("2")){
							cdhd_non_cdhd_clss = "비회원";
						}
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
					
					if(!GolfUtil.empty(rs.getString("CDHD_GRD_SEQ_NO"))){
						cdhd_grd_seq_no = rs.getString("CDHD_GRD_SEQ_NO");
//						if(!(cdhd_grd_seq_no.equals("0") || cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("4") || cdhd_grd_seq_no.equals("99"))){
//							cdhd_grd_seq_no = "0";
//						}
					}else{
						cdhd_grd_seq_no = "";
					}
					
					max_seq_no = rs.getString("SEQ_NO");
										
					result.addString("SEQ_NO" 				,rs.getString("SEQ_NO") );
					result.addString("BKG_PE_NM" 			,rs.getString("BKG_PE_NM") );
					result.addString("CDHD_ID" 				,rs.getString("CDHD_ID") );
					result.addString("JUMIN_NO" 			,rs.getString("JUMIN_NO") );
					result.addString("CDHD_GRD_SEQ_NO" 		,cdhd_grd_seq_no );
					result.addString("HP_DDD_NO" 			,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO" 			,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO" 			,rs.getString("HP_TEL_SNO") );
					result.addString("EMAIL" 				,rs.getString("EMAIL") );
					result.addString("GRD_NM" 				,rs.getString("GRD_NM") );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("STTL_AMT" 			,sttl_amt );
					result.addString("STTL_STAT_CLSS" 		,sttl_stat_clss );
					result.addString("APLC_PE_CLSS" 		,aplc_pe_clss );
					result.addString("CDHD_NON_CDHD_CLSS" 	,cdhd_non_cdhd_clss );
					result.addString("NOTE" 				,rs.getString("NOTE") );
					result.addString("MONTHS" 				,rs.getString("MONTHS") );
					result.addString("max_seq_no" 			,max_seq_no );
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


	// 팀 테이블
	public DbTaoResult execute_team(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값

			String aplc_seq_no		= data.getString("aplc_seq_no");
			String sql = this.getTeamListQuery();

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, aplc_seq_no);
			rs = pstmt.executeQuery();
						
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addString("TEAM_NUM" 			,rs.getString("TEAM_NUM") );
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
	
	
	public DbTaoResult execute_pay(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값

			String aplc_seq_no		= data.getString("aplc_seq_no");
			String sql = this.getPayListQuery();

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, aplc_seq_no);
			rs = pstmt.executeQuery();
			
			String sttl_amt = "";
			String card_no = "";
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					if(!GolfUtil.empty(rs.getString("STTL_AMT"))){
						sttl_amt = GolfUtil.comma(rs.getString("STTL_AMT"));
					}else{
						sttl_amt = "";
					}
					
					if(!GolfUtil.empty(rs.getString("CARD_NO"))){
						card_no = GolfUtil.getFmtCardNo(rs.getString("CARD_NO"));
					}else{
						card_no = "";
					}
					
					result.addString("ODR_NO" 			,rs.getString("ODR_NO") );
					result.addString("CARD_NO"			,card_no );
					result.addString("VALD_DATE" 		,rs.getString("VALD_DATE") );
					result.addString("INS_MCNT" 		,rs.getString("INS_MCNT") );
					result.addString("STTL_ATON" 		,rs.getString("STTL_ATON_VIEW") );
					result.addString("CNCL_ATON" 		,rs.getString("CNCL_ATON_VIEW") );
					result.addString("STTL_MINS_NM" 	,rs.getString("STTL_MINS_NM") );
					result.addString("AUTH_NO" 			,rs.getString("AUTH_NO") );
					result.addString("STTL_AMT" 		,sttl_amt );
					result.addString("STTL_STAT_CLSS" 	,rs.getString("STTL_STAT_CLSS") );
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

	public DbTaoResult execute_amt(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값

			String code		= data.getString("code");
			String sql = this.getCodeListQuery();

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, code);
			rs = pstmt.executeQuery();
			
			String sel_value = "0";
			String sel_text = "";
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					sel_text = rs.getString("EXPL");
					
					if(!GolfUtil.empty(rs.getString("GOLF_CMMN_CODE_NM"))){
						sel_value = rs.getString("GOLF_CMMN_CODE_NM");
						sel_text = sel_text + " " + GolfUtil.comma(rs.getString("GOLF_CMMN_CODE_NM"));
					}else{
						sel_value = "0";
					}
					
					result.addString("sel_value"		,sel_value );
					result.addString("sel_text"			,sel_text );
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

	public DbTaoResult execute_opt(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값

			String code		= data.getString("code");
			String sql = this.getCodeListQuery();

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, code);
			rs = pstmt.executeQuery();
			
			String sel_value = "0";
			String sel_text = "";
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					sel_text = rs.getString("EXPL");
					
					if(!GolfUtil.empty(rs.getString("GOLF_CMMN_CODE_NM"))){
						sel_value = sel_text + "" + rs.getString("GOLF_CMMN_CODE_NM");
						sel_text = sel_text + " " + GolfUtil.comma(rs.getString("GOLF_CMMN_CODE_NM"));
					}else{
						sel_value = "0";
					}
					
					result.addString("sel_value"		,sel_value );
					result.addString("sel_text"			,sel_text );
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
     * 예약상태 
     ************************************************************************ */
     private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("\n	 SELECT EVT.APLC_SEQ_NO, SVC.GOLF_CMMN_CODE_NM SVC_NM, EVT.GOLF_SVC_APLC_CLSS	\n");
 		sql.append("\t	 , EVT.CDHD_ID, CODE.GOLF_CMMN_CODE_NM GRD_NM, EVT.JUMIN_NO, EVT.BKG_PE_NM	\n");
 		sql.append("\t	 , (EVT.HP_DDD_NO||'-'||EVT.HP_TEL_HNO||'-'||EVT.HP_TEL_SNO) HP, EVT.NOTE PHONE	\n");
 		sql.append("\t	 , TO_CHAR(TO_DATE(SUBSTR(EVT.APLC_ATON,1,8)),'YYYY-MM-DD') REG_DATE	\n");
 		sql.append("\t	 , TO_CHAR(TO_DATE(EVT.RSVT_DATE),'YYYY-MM-DD') RSVT_DATE, EVT.RSV_TIME, TRM_UNT	\n");
 		sql.append("\t	 , CASE TRM_UNT WHEN '1' THEN TO_CHAR(TO_DATE(EVT.RSVT_DATE)+1,'YYYY-MM-DD')	\n");
 		sql.append("\t	 	WHEN '2' THEN TO_CHAR(TO_DATE(EVT.RSVT_DATE)+2,'YYYY-MM-DD') END RSVT_END_DATE	\n");
 		sql.append("\t	 , STAT.GOLF_CMMN_CODE_NM STAT_NM, EVT.STTL_AMT, EVT.STTL_STAT_CLSS	\n");
 		sql.append("\t	 , (SELECT MAX(TEAM_NUM) FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=EVT.APLC_SEQ_NO) TNUM	\n");
 		sql.append("\t	 , (SELECT COUNT(*) FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=EVT.APLC_SEQ_NO) PNUM	\n");
 		sql.append("\t	 , (SELECT COUNT(*) FROM BCDBA.TBGSTTLMGMT WHERE STTL_GDS_SEQ_NO=EVT.APLC_SEQ_NO AND STTL_GDS_CLSS='0010' AND STTL_STAT_CLSS='N') ORD_CNT	\n");
 		sql.append("\t	 , EVT.MGR_MEMO, CUS_RMRK	\n");
 		sql.append("\t	 FROM BCDBA.TBGGOLFEVNTAPLC EVT	\n");
 		sql.append("\t	 LEFT JOIN BCDBA.TBGGOLFCDHD CDHD ON EVT.CDHD_ID=CDHD.CDHD_ID	\n");
 		sql.append("\t	 LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON CDHD.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	 LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND CODE.GOLF_CMMN_CLSS='0005'	\n");
 		sql.append("\t	 LEFT JOIN BCDBA.TBGCMMNCODE SVC ON EVT.GOLF_SVC_APLC_CLSS=SVC.GOLF_CMMN_CODE AND SVC.GOLF_CMMN_CLSS='0048'	\n");
 		sql.append("\t	 LEFT JOIN BCDBA.TBGCMMNCODE STAT ON EVT.EVNT_PGRS_CLSS=STAT.GOLF_CMMN_CODE AND STAT.GOLF_CMMN_CLSS='0057'	\n");
 		sql.append("\t	 WHERE EVT.APLC_SEQ_NO=?	\n");
 		return sql.toString();
     }

 	/** ***********************************************************************
     * 동반자 리스트
     ************************************************************************ */
     private String getSelectListQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n	SELECT EVT.SEQ_NO, EVT.GOLF_SVC_APLC_CLSS, EVT.APLC_PE_CLSS, EVT.BKG_PE_NM, EVT.CDHD_NON_CDHD_CLSS, EVT.CDHD_ID, EVT.JUMIN_NO, EVT.CDHD_GRD_SEQ_NO	\n");
 		sql.append("\t	, EVT.HP_DDD_NO, EVT.HP_TEL_HNO, EVT.HP_TEL_SNO, EVT.EMAIL, EVT.HADC_NUM, EVT.STTL_STAT_CLSS, EVT.STTL_AMT, EVT.CNCL_YN, EVT.CNCL_ATON, APLC.NOTE	\n");
 		sql.append("\t	, CODE.GOLF_CMMN_CODE_NM GRD_NM, APLC.GREEN_NM, SUBSTR(APLC.RSVT_DATE, 5, 2) MONTHS	\n");
 		sql.append("\t	FROM BCDBA.TBGGOLFEVNTAPLCPE EVT	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGGOLFCDHD CDHD ON EVT.CDHD_ID=CDHD.CDHD_ID	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRD ON CDHD.CDHD_CTGO_SEQ_NO=GRD.CDHD_CTGO_SEQ_NO	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGCMMNCODE CODE ON GRD.CDHD_SQ2_CTGO=CODE.GOLF_CMMN_CODE AND GOLF_CMMN_CLSS='0005'	\n");
 		sql.append("\t	LEFT JOIN BCDBA.TBGGOLFEVNTAPLC APLC ON APLC.APLC_SEQ_NO=EVT.APLC_SEQ_NO	\n");
 		sql.append("\t	WHERE APLC_SEQ_NO=?	\n");
 		sql.append("\t	ORDER BY SEQ_NO	\n");
 		
 		return sql.toString();
     }
     
 	/** ***********************************************************************
      * 결제리스트 
      ************************************************************************ */
      private String getPayListQuery(){
          StringBuffer sql = new StringBuffer();
   		sql.append("\n	 SELECT ODR_NO, CARD_NO, VALD_DATE, INS_MCNT, STTL_MINS_NM, STTL_AMT, AUTH_NO	\n");
  		sql.append("\t	 , TO_CHAR(TO_DATE(SUBSTR(STTL_ATON,1,8)),'YYYY-MM-DD') STTL_ATON_VIEW	\n");
  		sql.append("\t	 , TO_CHAR(TO_DATE(SUBSTR(CNCL_ATON,1,8)),'YYYY-MM-DD') CNCL_ATON_VIEW	\n");
  		sql.append("\t	 , CASE STTL_STAT_CLSS WHEN 'N' THEN '결제완료' ELSE '결제취소' END STTL_STAT_CLSS	\n");
  		sql.append("\t	 FROM BCDBA.TBGSTTLMGMT WHERE STTL_GDS_CLSS='0010' AND STTL_GDS_SEQ_NO=?	\n");
  		sql.append("\t	 ORDER BY STTL_ATON	\n");
  		return sql.toString();
      }
      
   	/** ***********************************************************************
    * 팀리스트 
    ************************************************************************ */
    private String getTeamListQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n	 SELECT DISTINCT(TEAM_NUM) TEAM_NUM	\n");
		sql.append("\t	 FROM BCDBA.TBGGOLFEVNTAPLCPE	\n");
		sql.append("\t	 WHERE APLC_SEQ_NO=?	\n");
		sql.append("\t	 ORDER BY TEAM_NUM	\n");
		return sql.toString();
    }
    
   	/** ***********************************************************************
    * 코드리스트
    ************************************************************************ */
    private String getCodeListQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n	 SELECT GOLF_CMMN_CODE_NM, EXPL FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS=? ORDER BY SORT_SEQ	\n");
		return sql.toString();
    }

}
