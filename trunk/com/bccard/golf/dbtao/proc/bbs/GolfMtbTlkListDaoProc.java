/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLsnVodListDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : Home > 마이티박스 > 골프토크 스크랩 
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.bbs;

import java.io.Reader;
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
public class GolfMtbTlkListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBoardListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMtbTlkListDaoProc() {}	

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
			String sch_field_cd		= data.getString("SCH_FIELD_CD");
			String sch_clss_cd		= data.getString("SCH_CLSS_CD");
			String sch_sec_cd		= data.getString("SCH_SEC_CD");
			String sch_hd_yn		= data.getString("SCH_HD_YN");	
			String sreg_sdate		= data.getString("SREG_SDATE");
			String sreg_edate		= data.getString("SREG_EDATE");	
			
			String userId		= data.getString("USERID");	
			
			String gkey		= data.getString("GKEY");	

			String sql = this.getSelectQuery(search_sel, search_word, sch_field_cd, sch_clss_cd, sch_sec_cd, sch_hd_yn, sreg_sdate, sreg_edate, gkey);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			pstmt.setString(++idx, data.getString("BBS"));
			pstmt.setString(++idx, data.getString("USERID"));		// 스크랩한 토크 추출시
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");		
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			if (!GolfUtil.isNull(sch_field_cd))	pstmt.setString(++idx, sch_field_cd);
			if (!GolfUtil.isNull(sch_clss_cd))	pstmt.setString(++idx, sch_clss_cd);
			if (!GolfUtil.isNull(sch_sec_cd))	pstmt.setString(++idx, sch_sec_cd);
			if (!GolfUtil.isNull(sch_hd_yn))	pstmt.setString(++idx, sch_hd_yn);
			if (!GolfUtil.isNull(sreg_sdate) && !GolfUtil.isNull(sreg_edate)) {
				pstmt.setString(++idx, sreg_sdate+"000000");
				pstmt.setString(++idx, sreg_edate+"235959");
			}
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 				,rs.getLong("BBRD_SEQ_NO") );
					result.addString("BBRD_UNIQ_SEQ_NO" 	,rs.getString("BBRD_CLSS") );
					result.addString("FIELD_CD" 			,rs.getString("GOLF_CLM_CLSS") );
					result.addString("CLSS_CD" 				,rs.getString("GOLF_BOKG_FAQ_CLSS") );
					result.addString("SEC_CD" 				,rs.getString("GOLF_VBL_RULE_PREM_CLSS") );
					result.addString("TITL" 				,rs.getString("TITL") );
					//result.addString("CTNT" 				,rs.getString("CTNT") );
					result.addString("ID"					,rs.getString("ID") );
					result.addString("HG_NM"				,rs.getString("HG_NM") );
					result.addString("EMAIL_ID"				,rs.getString("EMAIL") );
					result.addLong("INOR_NUM"				,rs.getLong("INQR_NUM") );
					result.addString("EPS_YN"				,rs.getString("EPS_YN") );
					result.addString("FILE_NM"				,rs.getString("ANNX_FILE_NM") ); 
					result.addString("PIC_NM"				,rs.getString("MVPT_ANNX_FILE_PATH") );
					result.addString("HD_YN"				,rs.getString("PROC_YN") );
					result.addString("DEL_YN"				,rs.getString("DEL_YN") );
					result.addString("REG_MGR_SEQ_NO"		,rs.getString("REG_MGR_ID") );
					result.addString("REG_ATON"				,rs.getString("REG_ATON") );
					result.addString("BEST_YN"				,rs.getString("BEST_YN") );
					result.addString("NEW_YN"				,rs.getString("ANW_BLTN_ARTC_YN") );
					result.addLong("REPY_URNK_SEQ_NO"		,rs.getLong("REPY_URNK_SEQ_NO") );
					result.addString("RE_CNT"				,rs.getString("RE_CNT") );
					
					result.addString("TOTAL_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"			,rs.getString("PAGE") );
					result.addString("LIST_NO"				,rs.getString("LIST_NO") );
					result.addString("RNUM"					,rs.getString("RNUM") );
					
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					} 
					result.addString("CTNT", bufferSt.toString());
					
					String ctnt_list = GolfUtil.getAnsiCode(GolfUtil.removeTag(bufferSt.toString()));
					if (ctnt_list.getBytes().length > 300)	ctnt_list = GolfUtil.getCutKSCString(ctnt_list,300,"..."); 
					
					result.addString("CTNT_LIST", ctnt_list);
										
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
    private String getSelectQuery(String search_sel, String search_word, String sch_field_cd, String sch_clss_cd, String sch_sec_cd, String sch_hd_yn, String sreg_sdate, String sreg_edate, String gkey){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			BBRD_SEQ_NO, BBRD_CLSS, GOLF_CLM_CLSS, GOLF_BOKG_FAQ_CLSS, GOLF_VBL_RULE_PREM_CLSS, TITL, CTNT, ID, NVL(HG_NM,'관리자') HG_NM, EMAIL, INQR_NUM, EPS_YN,  	");
		sql.append("\n 			ANNX_FILE_NM, MVPT_ANNX_FILE_PATH, DECODE(NVL(PROC_YN,'N'),'Y','처리','미처리') PROC_YN, DEL_YN, REG_MGR_ID, REG_ATON, BEST_YN, ANW_BLTN_ARTC_YN, REPY_URNK_SEQ_NO, 	");
		sql.append("\n 			RE_CNT,	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TBB.BBRD_SEQ_NO, TBB.BBRD_CLSS, TBB.GOLF_CLM_CLSS, TBB.GOLF_BOKG_FAQ_CLSS, TBB.GOLF_VBL_RULE_PREM_CLSS, TBB.TITL, TBB.CTNT, TBB.ID, TBB.HG_NM, TBB.EMAIL, TBB.INQR_NUM, TBB.EPS_YN, TBB.ANNX_FILE_NM, TBB.MVPT_ANNX_FILE_PATH, TBB.PROC_YN, TBB.DEL_YN, TBB.REG_MGR_ID, TO_CHAR(TO_DATE(TBB.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, TBB.BEST_YN, TBB.ANW_BLTN_ARTC_YN, TBB.REPY_URNK_SEQ_NO, 	");
		sql.append("\n 				(SELECT COUNT(REPY_SEQ_NO) FROM BCDBA.TBGBBRDREPY WHERE REPY_CLSS='0001' AND BBRD_SEQ_NO=TBB.BBRD_SEQ_NO) RE_CNT 	");
		sql.append("\n 				FROM 	");
		
		if (GolfUtil.isNull(gkey)) 	sql.append("\n 				BCDBA.TBGBBRD TBB,  BCDBA.TBGSCRAP TGS	");	// 스크랩한 토크 추출시 
		if (!GolfUtil.isNull(gkey)) sql.append("\n 				BCDBA.TBGBBRD TBB	"); 					// ※내가쓴 토크 추출시
		
		sql.append("\n 				WHERE TBB.BBRD_CLSS = ?	");
		sql.append("\n 				AND TBB.DEL_YN = 'N'	");
		
		if (GolfUtil.isNull(gkey)) 	sql.append("\t AND TBB.BBRD_SEQ_NO = TGS.BOD_SEQ_NO	");	// 스크랩한 토크 추출시 
		if (GolfUtil.isNull(gkey)) 	sql.append("\t AND TGS.CDHD_ID = ?	");					// 스크랩한 토크 추출시
		if (!GolfUtil.isNull(gkey)) sql.append("\t AND TBB.ID = ?	");						// ※내가쓴 토크 추출시
		if (GolfUtil.isNull(gkey)) 	sql.append("\t AND TGS.GOLF_SCRAP_CLSS = '0002'	");		// 스크랩한 토크 추출시
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TBB.TITL LIKE ?	");
				sql.append("\n 				OR TBB.TBB.CTNT LIKE ? 	");	
				sql.append("\n 				OR TBB.HG_NM LIKE ? )	");	 
			} else {
				sql.append("\n 				AND "+search_sel+" LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(sch_field_cd)) sql.append("\n 		AND GOLF_CLM_CLSS = ?	");
		if (!GolfUtil.isNull(sch_clss_cd)) sql.append("\n 		AND GOLF_BOKG_FAQ_CLSS = ?	");
		if (!GolfUtil.isNull(sch_sec_cd)) sql.append("\n 		AND GOLF_VBL_RULE_PREM_CLSS = ?	");
		if (!GolfUtil.isNull(sch_hd_yn)) sql.append("\n 		AND PROC_YN = ?	");
		if (!GolfUtil.isNull(sreg_sdate) && !GolfUtil.isNull(sreg_edate)) {
			sql.append("\n 				AND TBB.REG_ATON BETWEEN ? AND ?	");
		}

		sql.append("\n 				ORDER BY REPY_URNK_SEQ_NO DESC, BBRD_SEQ_NO ASC	");		
		sql.append("\n 			)	"); 

		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}