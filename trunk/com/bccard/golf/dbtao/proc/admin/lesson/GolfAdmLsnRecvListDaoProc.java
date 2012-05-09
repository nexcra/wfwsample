/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLsnRecvListDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨신청 리스트
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lesson;

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
public class GolfAdmLsnRecvListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLsnRecvListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLsnRecvListDaoProc() {}	

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

			// 회원통합테이블 관련 수정사항 진행
			//조회 ----------------------------------------------------------
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");

			String ssex				= data.getString("SSEX");
			String scoop_cp_cd		= data.getString("SCOOP_CP_CD");
			String slsn_type_cd		= data.getString("SLSN_TYPE_CD");
			String suser_clss		= data.getString("SUSER_CLSS");
			String slsn_expc_clss	= data.getString("SLSN_EXPC_CLSS");

			debug("slsn_expc_clss :: > " + slsn_expc_clss);
			
			String sql = this.getSelectQuery(search_sel, search_word, ssex, scoop_cp_cd, slsn_type_cd, suser_clss, slsn_expc_clss);
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			if (!GolfUtil.isNull(ssex))	pstmt.setString(++idx, ssex);
			if (!GolfUtil.isNull(scoop_cp_cd))	pstmt.setString(++idx, scoop_cp_cd);
			if (!GolfUtil.isNull(slsn_type_cd))	pstmt.setString(++idx, slsn_type_cd);
			if (!GolfUtil.isNull(suser_clss))	pstmt.setString(++idx, "%"+suser_clss+"%");
			if (!GolfUtil.isNull(slsn_expc_clss))	pstmt.setString(++idx, slsn_expc_clss);
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("LSN_TYPE_CD" 		,rs.getString("LESN_CLSS") );
					result.addString("LSN_NM" 			,rs.getString("LESN_NM") );
					result.addString("COOP_CP_CD" 		,rs.getString("GOLF_LESN_AFFI_FIRM_CLSS") );
					result.addLong("RECV_NO" 			,rs.getLong("APLC_SEQ_NO") );
					result.addString("CSTMR_ID" 		,rs.getString("CDHD_ID") );
					result.addString("SEX" 				,rs.getString("SEX") );
					result.addString("EMAIL_ID" 		,rs.getString("EMAIL") );
					result.addString("TEL" 				,rs.getString("TEL") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("HAN_NM" 			,rs.getString("HG_NM") );
					result.addString("USERCLSS" 		,rs.getString("USERCLSS") );
					result.addString("LSN_TYPE_NM" 		,rs.getString("LSN_TYPE_NM") );
					result.addString("COOP_CP_NM" 		,rs.getString("COOP_CP_NM") );
					result.addString("LSN_EXPC_NM" 		,rs.getString("LSN_EXPC_NM") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
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
    private String getSelectQuery(String search_sel, String search_word, String ssex, String scoop_cp_cd, String slsn_type_cd, String suser_clss, String slsn_expc_clss){
        StringBuffer sql = new StringBuffer();		
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			LESN_CLSS, LESN_NM, GOLF_LESN_AFFI_FIRM_CLSS, 	");
		sql.append("\n 			APLC_SEQ_NO, CDHD_ID, SEX, EMAIL, TEL, REG_ATON,	");
		sql.append("\n 			HG_NM, USERCLSS, 	");
		sql.append("\n 			LSN_TYPE_NM, COOP_CP_NM, LSN_EXPC_NM, 	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 						TGL.LESN_CLSS, TGL.LESN_NM, TGL.GOLF_LESN_AFFI_FIRM_CLSS, 	");
		sql.append("\n 						TGR.APLC_SEQ_NO, TGR.CDHD_ID, DECODE(TGR.SEX_CLSS, 'M', '남', 'F', '여') SEX, TGR.EMAIL, TGR.DDD_NO||'-'||TGR.TEL_HNO||'-'||TGR.TEL_SNO TEL, TO_CHAR(TO_DATE(TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON,	");
		sql.append("\n 						TGU.HG_NM, 	");
		sql.append("\n 						TMC4.GOLF_CMMN_CODE_NM USERCLSS, 	");
		sql.append("\n 						TMC1.GOLF_CMMN_CODE_NM LSN_TYPE_NM, 	");
		sql.append("\n 						TMC2.GOLF_CMMN_CODE_NM COOP_CP_NM, 	");
		sql.append("\n 						TMC3.GOLF_CMMN_CODE_NM LSN_EXPC_NM, 	");
		sql.append("\n 						TGS.STTL_STAT_CLSS, TGS.STTL_AMT, TO_CHAR(TO_DATE(TGS.STTL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') STTL_ATON, TO_CHAR(TO_DATE(TGS.CNCL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') CNCL_ATON 	");
		sql.append("\n 						FROM 	");
		
		
		sql.append("\n	BCDBA.TBGAPLCMGMT TGR	");
		sql.append("\n	JOIN BCDBA.TBGLESNMGMT TGL ON TGR.LESN_SEQ_NO=TGL.LESN_SEQ_NO	");
		sql.append("\n	JOIN BCDBA.TBGGOLFCDHD TGU ON TGR.CDHD_ID=TGU.CDHD_ID	");
		sql.append("\n	JOIN BCDBA.TBGCMMNCODE TMC1 ON TGL.LESN_CLSS=TMC1.GOLF_CMMN_CODE AND TMC1.GOLF_CMMN_CLSS='0003'	");
		sql.append("\n	JOIN BCDBA.TBGCMMNCODE TMC2 ON TGL.GOLF_LESN_AFFI_FIRM_CLSS=TMC2.GOLF_CMMN_CODE AND TMC2.GOLF_CMMN_CLSS='0004'	");
		sql.append("\n	JOIN BCDBA.TBGCMMNCODE TMC3 ON TGR.GOLF_LESN_EXPE_CLSS=TMC3.GOLF_CMMN_CODE AND TMC3.GOLF_CMMN_CLSS='0006'	");
		sql.append("\n	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT TGUD ON TGUD.CDHD_CTGO_SEQ_NO=TGU.CDHD_CTGO_SEQ_NO	");
		sql.append("\n	JOIN BCDBA.TBGCMMNCODE TMC4 ON TGUD.CDHD_SQ2_CTGO=TMC4.GOLF_CMMN_CODE AND TMC4.GOLF_CMMN_CLSS='0005'	");
		sql.append("\n	LEFT JOIN BCDBA.TBGSTTLMGMT TGS ON TGR.APLC_SEQ_NO=TGS.STTL_GDS_SEQ_NO	");
		sql.append("\n	WHERE TGR.GOLF_SVC_APLC_CLSS = '0001'	");
				
				
				
				
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TGL.LESN_NM LIKE ?	");
				sql.append("\n 				OR TGL.LESN_CTNT LIKE ? )	");				
			} else {
				sql.append("\n 				AND "+search_sel+" LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(ssex)) sql.append("\n 						AND TGR.SEX_CLSS = ?	");
		if (!GolfUtil.isNull(scoop_cp_cd)) sql.append("\n 				AND TGL.GOLF_LESN_AFFI_FIRM_CLSS = ?	");
		if (!GolfUtil.isNull(slsn_type_cd)) sql.append("\n 				AND TGL.LESN_CLSS = ?	");
		if (!GolfUtil.isNull(suser_clss)) sql.append("\n 				AND TMC4.GOLF_CMMN_CODE_NM LIKE ?	");
		if (!GolfUtil.isNull(slsn_expc_clss)) sql.append("\n 			AND TGR.GOLF_LESN_EXPE_CLSS = ?	");
		
		sql.append("\n 						ORDER BY TGR.APLC_SEQ_NO DESC ");
				
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		debug("sql : >> " + sql.toString());
		
		return sql.toString();
    }
}
