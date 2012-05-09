/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkJoinListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 > 이벤트 > 프리미엄 부킹 이벤트 > 신청 관리
*   적용범위  : golf
*   작성일자  : 2009-05-27
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event;

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
public class GolfAdmEvntBkJoinListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBkJoinListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBkJoinListDaoProc() {}	

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
			String sgr_nm			= data.getString("SGR_NM");
			String sprize_yn		= data.getString("SPRIZE_YN");
			String sbkps_sdate		= data.getString("SBKPS_SDATE");
			String sbkps_edate		= data.getString("SBKPS_EDATE");
			String sevnt_from		= data.getString("SEVNT_FROM");
			String sevnt_to			= data.getString("SEVNT_TO");

			String sql = this.getSelectQuery(search_sel, search_word, sgr_nm, sprize_yn, sbkps_sdate, sbkps_edate, sevnt_from, sevnt_to);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));

			if (!GolfUtil.isNull(search_word)) {

				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}

			if (!GolfUtil.isNull(sgr_nm))		pstmt.setString(++idx, sgr_nm);
			if (!GolfUtil.isNull(sprize_yn))	pstmt.setString(++idx, sprize_yn);
			if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
				pstmt.setString(++idx, sbkps_sdate);
				pstmt.setString(++idx, sbkps_edate);
			}
			if (!GolfUtil.isNull(sevnt_from) && !GolfUtil.isNull(sevnt_to)) {
				pstmt.setString(++idx, sevnt_to);
				pstmt.setString(++idx, sevnt_from);
			}
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("GR_NM" 			,rs.getString("GREEN_NM") );
					result.addString("BKPS_DATE" 		,rs.getString("BOKG_ABLE_DATE") );
					result.addString("BKPS_TIME" 		,rs.getString("BOKG_ABLE_TIME") );
					result.addString("DIPY_BKPS_TIME" 	,rs.getString("DIPY_BOKG_ABLE_TIME") );
					result.addString("ING_YN" 			,rs.getString("ING_YN") );
					result.addString("HAN_NM" 			,rs.getString("HG_NM") );	
					result.addString("RECV_NO" 			,rs.getString("APLC_SEQ_NO") );
					result.addString("PRIZE_YN" 		,rs.getString("PRZ_WIN_YN") );
					result.addString("CSTMR_ID" 		,rs.getString("CDHD_ID") );
					result.addString("HP" 				,rs.getString("HP") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );				
					
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
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	
    private String getSelectQuery(String search_sel, String search_word, String sgr_nm, String sprize_yn, String  sbkps_sdate, String  sbkps_edate, String  sevnt_from,  String sevnt_to){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			GREEN_NM, BOKG_ABLE_DATE, BOKG_ABLE_TIME, DIPY_BOKG_ABLE_TIME, ING_YN,	");
		sql.append("\n 			HG_NM, 	");
		sql.append("\n 			APLC_SEQ_NO, PRZ_WIN_YN, CDHD_ID, HP, REG_ATON, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TGE.GREEN_NM, TO_CHAR(TO_DATE(TGE.BOKG_ABLE_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') BOKG_ABLE_DATE,   	");
		sql.append("\n 				TGE.BOKG_ABLE_TIME, TO_CHAR (TO_DATE (TGE.BOKG_ABLE_TIME, 'HH24MI'), 'HH24:MI') DIPY_BOKG_ABLE_TIME,	");
		sql.append("\n 				CASE WHEN TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN TGE.EVNT_STRT_DATE AND TGE.EVNT_END_DATE THEN '진행' ELSE '<FONT COLOR=RED>마감</FONT>' END ING_YN,	");
		sql.append("\n 				TGU.HG_NM,   	");
		sql.append("\n 				TGR.APLC_SEQ_NO, DECODE(NVL(TGR.PRZ_WIN_YN,'N'),'Y','당첨','N','미당첨') PRZ_WIN_YN, TGR.CDHD_ID, TGR.DDD_NO||TGR.TEL_HNO||TGR.TEL_SNO HP, TO_CHAR(TO_DATE(TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI:SS') REG_ATON   	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAPLCMGMT TGR, BCDBA.TBGEVNTMGMT TGE, BCDBA.TBGGOLFCDHD TGU	");
		sql.append("\n 				WHERE TGR.LESN_SEQ_NO = TGE.EVNT_SEQ_NO 	");
		sql.append("\n 				AND TGR.CDHD_ID = TGU.CDHD_ID 	");
		sql.append("\n 				AND TGR.GOLF_SVC_APLC_CLSS = '0004' 	");
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 			AND (TGU.HG_NM LIKE ?	");
				sql.append("\n 			OR TGR.CDHD_ID LIKE ?)	");				
			} else {
				sql.append("\n 			AND "+search_sel+" LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(sgr_nm)) {
			sql.append("\n 				AND TGE.HG_NM = ?	");
		}
		if (!GolfUtil.isNull(sprize_yn)) {
			sql.append("\n 				AND TGR.EVNT_BNFT_EXPL = ?	");
		}
		if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
			sql.append("\n 				AND TGE.BOKG_ABLE_DATE BETWEEN ? AND ?	");
		}
		if (!GolfUtil.isNull(sevnt_from) && !GolfUtil.isNull(sevnt_to)) {
			sql.append("\n 				AND TGE.EVNT_STRT_DATE <= ?	");
			sql.append("\n 				AND TGE.EVNT_END_DATE >=  ?	");
		}

		sql.append("\n 				ORDER BY TGR.APLC_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }


	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute2 (WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
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
			String sgr_nm			= data.getString("SGR_NM");
			String sprize_yn		= data.getString("SPRIZE_YN");
			String sbkps_sdate		= data.getString("SBKPS_SDATE");
			String sbkps_edate		= data.getString("SBKPS_EDATE");
		//	String sevnt_from		= data.getString("SEVNT_FROM");
		//	String sevnt_to			= data.getString("SEVNT_TO");

			String lesn_seq_no		= data.getString("LESN_SEQ_NO");



			String sql = this.getSelectQuery2(search_sel, search_word, sgr_nm, sprize_yn, sbkps_sdate, sbkps_edate);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));


			pstmt.setString(++idx, data.getString("LESN_SEQ_NO"));
			if (!GolfUtil.isNull(search_word)) {

				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}

			if (!GolfUtil.isNull(sgr_nm))		pstmt.setString(++idx, sgr_nm);
			if (!GolfUtil.isNull(sprize_yn))	pstmt.setString(++idx, sprize_yn);
			if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
				pstmt.setString(++idx, sbkps_sdate);
				pstmt.setString(++idx, sbkps_edate);
			}
			
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO")); 
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 
	
				while(rs.next())  {	
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("TEOF_TIME" 		,rs.getString("TEOF_TIME") );
					result.addString("TEOF_DATE" 		,rs.getString("TEOF_DATE") );
					result.addString("PRZ_WIN_YN" 	,rs.getString("PRZ_WIN_YN") );
					result.addString("CDHD_ID" 			,rs.getString("CDHD_ID") );
					result.addString("CO_NM" 			,rs.getString("CO_NM") );	
					result.addString("EMAIL" 			,rs.getString("EMAIL") );
					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") );
					result.addString("MEMO_EXPL" 		,rs.getString("MEMO_EXPL") );
					result.addString("MEMO_EXPL2" 		,rs.getString("MEMO_EXPL2") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );
					result.addString("APLC_SEQ_NO" 		,rs.getString("APLC_SEQ_NO") );
					result.addString("RECV_NO" 			,rs.getString("APLC_SEQ_NO") );
					
	
					result.addString("GRD" 		,rs.getString("GRD") );

					
					
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
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	
    private String getSelectQuery2(String search_sel, String search_word, String sgr_nm, String sprize_yn, String  sbkps_sdate, String  sbkps_edate){
        StringBuffer sql = new StringBuffer();
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM, APLC_SEQ_NO,	");
		sql.append("\n 			GREEN_NM, TEOF_TIME, TEOF_DATE, PRZ_WIN_YN, CDHD_ID,MEMO_EXPL2,	");
		sql.append("\n 			CO_NM, EMAIL, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, MEMO_EXPL, REG_ATON, DECODE(GRD ,'8','화이트','7','골드','6','블루','5','챔피온','9','블랙') GRD , 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				APLC_SEQ_NO, CO_NM, GREEN_NM,  TO_NUMBER(SUBSTR(TEOF_TIME,0,2)) TEOF_TIME , TO_CHAR(TO_DATE(TEOF_DATE,'YYYYMMDD'),'YYYY-MM-DD') TEOF_DATE,   	");
		sql.append("\n 				DECODE(NVL(PRZ_WIN_YN,'N'),'Y','당첨','N','미당첨') PRZ_WIN_YN,  CDHD_ID, EMAIL ,	");
		sql.append("\n 				HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, MEMO_EXPL, REG_ATON ,SUBSTR(MEMO_EXPL,1,20) MEMO_EXPL2,	");
		sql.append("\n 				(SELECT MIN(CDHD_CTGO_SEQ_NO)  FROM  BCDBA.TBGGOLFCDHDGRDMGMT  WHERE  CDHD_ID = A.CDHD_ID)   GRD	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAPLCMGMT A	");
		sql.append("\n 				WHERE GOLF_SVC_APLC_CLSS = '0004' 	");
		sql.append("\n 				AND LESN_SEQ_NO = ? 	");
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 			AND (CO_NM LIKE ?	");
				sql.append("\n 			OR CDHD_ID LIKE ? )	");				
			} else {
				sql.append("\n 			AND "+search_sel+" LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(sgr_nm)) {
			sql.append("\n 				AND GREEN_NM = ?	");
		}
		if (!GolfUtil.isNull(sprize_yn)) {
			sql.append("\n 				AND PRZ_WIN_YN = ?	");
		}
		if (!GolfUtil.isNull(sbkps_sdate) && !GolfUtil.isNull(sbkps_edate)) {
			sql.append("\n 				AND TEOF_DATE BETWEEN ? AND ?	");
		}

		sql.append("\n 				ORDER BY TEOF_DATE DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }

}
