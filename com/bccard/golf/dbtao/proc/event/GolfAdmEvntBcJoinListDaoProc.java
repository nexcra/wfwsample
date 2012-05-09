/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBkJoinListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 > 이벤트 > BC GOLF 이벤트 > 신청 관리
*   적용범위  : golf
*   작성일자  : 2009-05-27
************************** 수정이력 ***************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

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
public class GolfAdmEvntBcJoinListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBkJoinListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBcJoinListDaoProc() {}	

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
			String sevnt_seq_no		= data.getString("SEVNT_SEQ_NO");
			String sprize_yn		= data.getString("SPRIZE_YN");
			String sevnt_from		= data.getString("SEVNT_FROM");
			String sevnt_to			= data.getString("SEVNT_TO");

			String sql = this.getSelectQuery(search_sel, search_word, sevnt_seq_no, sprize_yn, sevnt_from, sevnt_to);   
			
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

			if (!GolfUtil.isNull(sevnt_seq_no))		pstmt.setString(++idx, sevnt_seq_no);
			if (!GolfUtil.isNull(sprize_yn))		pstmt.setString(++idx, sprize_yn);
			if (!GolfUtil.isNull(sevnt_from) && !GolfUtil.isNull(sevnt_to)) {
				pstmt.setString(++idx, sevnt_to);
				pstmt.setString(++idx, sevnt_from);
			}
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO")); 
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("EVNT_NM" 			,rs.getString("EVNT_NM") );
					result.addString("EVNT_STRT_DATE" 	,rs.getString("EVNT_STRT_DATE") );
					result.addString("EVNT_END_DATE" 	,rs.getString("EVNT_END_DATE") );
					
					result.addString("ING_YN" 			,rs.getString("ING_YN") );
					result.addString("HAN_NM" 			,rs.getString("HG_NM") );	
					result.addString("RECV_NO" 			,rs.getString("APLC_SEQ_NO") );
					result.addString("PRIZE_YN" 		,rs.getString("PRZ_WIN_YN") );
					result.addString("CSTMR_ID" 		,rs.getString("CDHD_ID") );
					result.addString("HP" 				,rs.getString("HP") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );
					result.addString("ADDR" 			,rs.getString("ADDR") );
					result.addString("MGRADE" 			,rs.getString("MGRADE") );
					result.addString("TNUM" 			,rs.getString("TNUM") );
					
					debug("======GolfAdmEvntBcJoinListDaoProc========ADDR=> " + rs.getString("ADDR"));
					debug("======GolfAdmEvntBcJoinListDaoProc========MGRADE=> " + rs.getString("MGRADE"));
					debug("======GolfAdmEvntBcJoinListDaoProc========TNUM=> " + rs.getString("TNUM"));
										
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
	
    private String getSelectQuery(String search_sel, String search_word, String sevnt_seq_no, String sprize_yn, String  sevnt_from,  String sevnt_to){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			EVNT_NM, TO_CHAR(TO_DATE(EVNT_STRT_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_STRT_DATE, TO_CHAR(TO_DATE(EVNT_END_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') EVNT_END_DATE, ING_YN,	");
		sql.append("\n 			HG_NM, ADDR, MGRADE, TNUM, 	");
		sql.append("\n 			APLC_SEQ_NO, PRZ_WIN_YN, CDHD_ID, HP, REG_ATON, 	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TGE.EVNT_NM, TGE.EVNT_STRT_DATE, TGE.EVNT_END_DATE, ");
		sql.append("\n 				CASE WHEN TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN TGE.EVNT_STRT_DATE AND TGE.EVNT_END_DATE THEN '진행' ELSE '<FONT COLOR=RED>마감</FONT>' END ING_YN,	");
		sql.append("\n 				TGU.HG_NM,   	");
		sql.append("\n 				TGR.APLC_SEQ_NO, DECODE(NVL(TGR.PRZ_WIN_YN,'N'),'Y','당첨','N','미당첨') PRZ_WIN_YN, TGR.CDHD_ID, TGR.DDD_NO||TGR.TEL_HNO||TGR.TEL_SNO HP, TO_CHAR(TO_DATE(TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD HH24:MI:SS') REG_ATON   	");
		sql.append("\n 				, TGR.RIDG_PERS_NUM AS TNUM, UCU.ZIPADDR||UCU.DETAILADDR AS ADDR, T4.GOLF_CMMN_CODE_NM AS MGRADE   	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAPLCMGMT TGR, BCDBA.TBGEVNTMGMT TGE, BCDBA.TBGGOLFCDHD TGU	");
		sql.append("\n 				, BCDBA.TBGGOLFCDHDGRDMGMT T2, BCDBA.TBGGOLFCDHDCTGOMGMT T3, BCDBA.TBGCMMNCODE T4	");
		sql.append("\n 				WHERE TGR.LESN_SEQ_NO = TGE.EVNT_SEQ_NO 	");
		sql.append("\n 				AND TGR.CDHD_ID = TGU.CDHD_ID 	");
		sql.append("\n 				AND TGR.GOLF_SVC_APLC_CLSS = '0009' 	");
		sql.append("\n 				AND TGR.CDHD_ID = UCU.ACCOUNT 	");
		sql.append("\n 				AND TGR.CDHD_ID=T2.CDHD_ID 	");
		sql.append("\n 				AND T2.CDHD_CTGO_SEQ_NO=T3.CDHD_CTGO_SEQ_NO 	");
		sql.append("\n 				AND T3.CDHD_SQ2_CTGO=T4.GOLF_CMMN_CODE AND T4.GOLF_CMMN_CLSS='0005' 	");
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 			AND (TGU.HG_NM LIKE ?	");
				sql.append("\n 			OR TGR.CDHD_ID LIKE ?)	");				
			} else {
				sql.append("\n 			AND "+search_sel+" LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(sevnt_seq_no)) {
			sql.append("\n 				AND TGE.EVNT_SEQ_NO = ?	");
		}
		if (!GolfUtil.isNull(sprize_yn)) {
			sql.append("\n 				AND TGR.PRZ_WIN_YN = ?	");
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
}
