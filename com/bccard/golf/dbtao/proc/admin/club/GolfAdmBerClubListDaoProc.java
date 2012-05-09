/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMngClubListDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 전체동호회 관리 리스트
*   적용범위  : golf
*   작성일자  : 2009-07-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.club;

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
public class GolfAdmBerClubListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmBerClubListDaoProc() {}	

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
			String search_sel		= data.getString("SEARCH_SEL"); 	//검색 직접조건 키워드
			String search_word		= data.getString("SEARCH_WORD");	//검색 직접조건
			
			String sckd_code		= data.getString("SCKD_CODE"); 		// 검색 동호회 분류
			String scnsl_yn			= data.getString("SCNSL_YN"); 		// 검색 개설여부	
			String sprgs_yn			= data.getString("SPRGS_YN"); 		// 검색 진행여부조건
			String search_dt1		= data.getString("SEARCH_DT1"); 	// 검색 시작날짜조건
			String search_dt2		= data.getString("SEARCH_DT2"); 	// 검색 종료날짜조건
			String scoop_cp_cd		= data.getString("SCOOP_CP_CD"); 	// 
			String p_idx			= data.getString("p_idx"); 			// 동호회 일련번호
			
			String sql = this.getSelectQuery(search_sel, search_word, sckd_code, scnsl_yn, sprgs_yn, search_dt1, search_dt2, scoop_cp_cd);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));	
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));	
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setString(++idx, p_idx);
			
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			
			if (!GolfUtil.isNull(scoop_cp_cd))	pstmt.setString(++idx, scoop_cp_cd);
			if (!GolfUtil.isNull(sckd_code))	pstmt.setString(++idx, sckd_code);
			if (!GolfUtil.isNull(scnsl_yn))		pstmt.setString(++idx, scnsl_yn);
			if (!GolfUtil.isNull(sprgs_yn))		pstmt.setString(++idx, sprgs_yn);
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, DateUtil.format(search_dt1, "yyyy-MM-dd", "yyyyMMdd000000"));
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, DateUtil.format(search_dt2, "yyyy-MM-dd", "yyyyMMdd999999"));
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addInt("CLUB_CDHD_SEQ_NO" 	,rs.getInt("CLUB_CDHD_SEQ_NO") ); 	// 일련번호
					result.addString("CLUB_SEQ_NO" 		,rs.getString("CLUB_SEQ_NO") ); 	// 동호회 일련번호
					result.addString("CDHD_ID" 			,rs.getString("CDHD_ID") ); 		// 회원아이디
					result.addString("CDHD_NM" 			,rs.getString("CDHD_NM") ); 		// 한글성명
					result.addString("GREET_CTNT" 		,rs.getString("GREET_CTNT") ); 		// 인사말 내용
					result.addString("JONN_YN" 			,rs.getString("JONN_YN") );  		// 가입여부
					result.addString("SECE_YN" 			,rs.getString("SECE_YN") );			// 탈퇴여부
					result.addString("APLC_ATON" 		,rs.getString("APLC_ATON") );		// 신청일시
					result.addString("JONN_ATON" 		,rs.getString("JONN_ATON") ); 		// 가입일시
					result.addString("CHNG_ATON" 		,rs.getString("CHNG_ATON") ); 		// 변경일시
					result.addString("SECE_ATON" 		,rs.getString("SECE_ATON") ); 		// 탈퇴일시
					result.addString("USERCLSS" 		,rs.getString("USERCLSS") ); 		// 회원등급
					result.addString("MOBILE" 			,rs.getString("MOBILE") ); 			// 휴대전화 
					result.addString("EMAIL" 			,rs.getString("EMAIL") ); 			// 휴대전화 

					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );     	// 총 추출수
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") ); 
					result.addString("RNUM"				,rs.getString("RNUM") );			// 현재 페이지 추출수
					result.addString("RESULT", "00"); 										// 정상결과
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
    private String getSelectQuery(String search_sel, String search_word, String sckd_code, String scnsl_yn, String sprgs_yn, String search_dt1, String search_dt2, String scoop_cp_cd){
        StringBuffer sql = new StringBuffer();
                
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM, CLUB_CDHD_SEQ_NO,CLUB_SEQ_NO,CDHD_ID,CDHD_NM,");
		sql.append("\n GREET_CTNT,JONN_YN,");
		sql.append("\n 	DECODE( SECE_YN 	");
		sql.append("\n 		,'Y', '탈퇴'	");
		sql.append("\n 		,'N', '활동'	");
		sql.append("\n 		,'E', '강퇴')	");
		sql.append("\n SECE_YN,");
		sql.append("\n APLC_ATON,JONN_ATON,CHNG_ATON,SECE_ATON,MOBILE,EMAIL,USERCLSS,");
		sql.append("\n	CEIL(ROWNUM/?) AS PAGE,	");
		
		sql.append("\n	MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n	(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");	//새로운 no값추출 
		sql.append("\n	FROM (SELECT ROWNUM RNUM,	");

		sql.append("\n	CLUB_CDHD_SEQ_NO,CLUB_SEQ_NO,TGL.CDHD_ID,CDHD_NM,GREET_CTNT,JONN_YN,SECE_YN, ");

		sql.append("\n TO_CHAR(TO_DATE(SUBSTR(APLC_ATON,1,8)),'YYYY-MM-DD')||' '||SUBSTR(APLC_ATON,9,2)||':'||SUBSTR(APLC_ATON,11,2) APLC_ATON,");
		sql.append("\n CASE WHEN JONN_ATON IS NULL THEN '' ELSE TO_CHAR(TO_DATE(SUBSTR(JONN_ATON,1,8)),'YYYY-MM-DD')||' '||SUBSTR(JONN_ATON,9,2)||':'||SUBSTR(JONN_ATON,11,2) END JONN_ATON,");
		sql.append("\n CASE WHEN CHNG_ATON IS NULL THEN '' ELSE TO_CHAR(TO_DATE(SUBSTR(CHNG_ATON,1,8)),'YYYY-MM-DD')||' '||SUBSTR(CHNG_ATON,9,2)||':'||SUBSTR(CHNG_ATON,11,2) END CHNG_ATON,");
		sql.append("\n CASE WHEN SECE_ATON IS NULL THEN '' ELSE TO_CHAR(TO_DATE(SUBSTR(SECE_ATON,1,8)),'YYYY-MM-DD')||' '||SUBSTR(SECE_ATON,9,2)||':'||SUBSTR(SECE_ATON,11,2) END SECE_ATON,");
				
		sql.append("\n T2.MOBILE, T2.EMAIL,");

		sql.append("\n	DECODE (T.USERCLSS,'()', '등급없음',T.USERCLSS) USERCLSS 	");
		sql.append("\n	FROM 	");
		sql.append("\n	(	");
		sql.append("\n 	SELECT CDHD_ID, SUBSTR(MAX(SYS_CONNECT_BY_PATH((GOLF_CMMN_CODE_NM1||'('||GOLF_CMMN_CODE_NM2||')'),',')),2) AS USERCLSS 	");
		sql.append("\n 	FROM  	");
		sql.append("\n 	( 	");
		sql.append("\n 		SELECT  	");
		sql.append("\n 			CDHD_ID, GOLF_CMMN_CODE_NM1, GOLF_CMMN_CODE_NM2, 	");
		sql.append("\n 			ROW_NUMBER() OVER(PARTITION BY CDHD_ID ORDER BY CDHD_ID) CNT 	");
		sql.append("\n 		FROM 	");
		sql.append("\n 		( 	");
		
		
		sql.append("\n SELECT T1.CDHD_ID, T5.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM1, T6.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM2 ");
		sql.append("\n FROM BCDBA.TBGCLUBCDHDMGMT T1 ");
		sql.append("\n JOIN BCDBA.TBGGOLFCDHD T2 ON T1.CDHD_ID=T2.CDHD_ID ");
		sql.append("\n JOIN BCDBA.TBGGOLFCDHDGRDMGMT T3 ON T1.CDHD_ID=T3.CDHD_ID ");
		sql.append("\n JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T4 ON T3.CDHD_CTGO_SEQ_NO=T4.CDHD_CTGO_SEQ_NO ");
		sql.append("\n JOIN BCDBA.TBGCMMNCODE T5 ON T4.CDHD_SQ1_CTGO=T5.GOLF_CMMN_CODE AND T5.GOLF_CMMN_CLSS='0001' ");
		sql.append("\n JOIN BCDBA.TBGCMMNCODE T6 ON T4.CDHD_SQ2_CTGO=T6.GOLF_CMMN_CODE AND T6.GOLF_CMMN_CLSS='0005' ");
		sql.append("\n WHERE T1.CLUB_SEQ_NO=? ");
		
		
		
//		sql.append("\n 			SELECT  	"); 
//		sql.append("\n 				TGU.CDHD_ID,TMC1.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM1, TMC2.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM2 	");
//		sql.append("\n 			FROM 	");
//		sql.append("\n 			BCDBA.TBGGOLFCDHD TGU, BCDBA.TBGGOLFCDHDGRDMGMT TGUC, BCDBA.TBGGOLFCDHDCTGOMGMT TGUD,  	");
//		sql.append("\n 			(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0001') TMC1, 	");
//		sql.append("\n 			(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0005') TMC2 	");
//		sql.append("\n 			WHERE TGU.CDHD_ID=TGUC.CDHD_ID(+) 	");
//		sql.append("\n 			AND TGUC.CDHD_CTGO_SEQ_NO=TGUD.CDHD_CTGO_SEQ_NO(+) 	");
//		sql.append("\n 			AND TGUD.CDHD_SQ1_CTGO=TMC1.GOLF_CMMN_CODE(+) 	");
//		sql.append("\n 			AND TGUD.CDHD_SQ2_CTGO=TMC2.GOLF_CMMN_CODE(+) 	");		
		
		sql.append("\n 		) 	");
		sql.append("\n 	) 	");
		sql.append("\n 	START WITH CNT=1 	");
		sql.append("\n 	CONNECT BY PRIOR CNT = CNT-1 	");
		sql.append("\n 	GROUP BY CDHD_ID 	");
		sql.append("\n )T,	");
		sql.append("\n	BCDBA.TBGCLUBCDHDMGMT	TGL");
		sql.append("\n	WHERE TGL.CLUB_CDHD_SEQ_NO = CLUB_CDHD_SEQ_NO	");
		sql.append("\n	AND TGL.CDHD_ID = T.CDHD_ID	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TGL.OPN_PE_ID LIKE  ?	");
				sql.append("\n 				OR TGL.ADDR LIKE ? )	");				
			} else {
				
				
				if (search_sel.equals("HAN_NM")) {
					sql.append("\n 				AND TGL.OPN_PE_ID LIKE  ?	");	
				} else {
					sql.append("\n 				AND "+search_sel+" LIKE ?	");
				}
				
				 
			}
		}

		if (!GolfUtil.isNull(sckd_code))sql.append("\n AND GOLF_CLUB_CTGO = ? ");		
		if (!GolfUtil.isNull(scnsl_yn))	sql.append("\n AND CLUB_OPN_AUTH_YN = ? ");		
		if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	sql.append("\n AND (APLC_ATON >= ? AND APLC_ATON <= ?) ");		

		sql.append("\n 				ORDER BY CLUB_CDHD_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
} 
