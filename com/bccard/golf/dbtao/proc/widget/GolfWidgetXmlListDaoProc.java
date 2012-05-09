/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaListDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 리스트
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.widget;

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
 * 
 ******************************************************************************/
public class GolfWidgetXmlListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfWidgetXmlListDaoProc() {}	

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
			String search_sel		= data.getString("SEARCH_SEL"); 	//검색 직접조건 키워드
			String search_word		= data.getString("SEARCH_WORD");	//검색 직접조건
			
			String sckd_code		= data.getString("SCKD_CODE"); 		// 검색 차종조건
			String scnsl_yn			= data.getString("SCNSL_YN"); 		// 검색 상담여부조건	
			String sprgs_yn			= data.getString("SPRGS_YN"); 		// 검색 진행여부조건
			String search_dt1		= data.getString("SEARCH_DT1"); 	// 검색 시작날짜조건
			String search_dt2		= data.getString("SEARCH_DT2"); 	// 검색 종료날짜조건
			String scoop_cp_cd		= "0002";//data.getString("SCOOP_CP_CD"); //리무진 서비스
			
			
			String sql = this.getSelectQuery(search_sel, search_word, sckd_code, scnsl_yn, sprgs_yn, search_dt1, search_dt2, scoop_cp_cd);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));	
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));	
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setString(++idx, data.getString("ID") ); //세션값  받아와 대입
			
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			
			pstmt.setString(++idx, scoop_cp_cd);
			
			if (!GolfUtil.isNull(sckd_code))	pstmt.setString(++idx, sckd_code);
			if (!GolfUtil.isNull(scnsl_yn))		pstmt.setString(++idx, scnsl_yn);
			if (!GolfUtil.isNull(sprgs_yn))		pstmt.setString(++idx, sprgs_yn);
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, DateUtil.format(search_dt1, "yyyy-MM-dd", "yyyyMMdd000000"));
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, DateUtil.format(search_dt2, "yyyy-MM-dd", "yyyyMMdd999999"));
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addInt("RECV_NO" 			,rs.getInt("APLC_SEQ_NO") );
					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") ); 	// 휴대전화 국번호
					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") ); 	// 휴대전화 국번호
					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") ); 	// 휴대전화 일련번호
					result.addString("CSTMR_ID" 		,rs.getString("CDHD_ID") ); 	// 신청자
					
					result.addString("CAR_TYPE_CD" 		,rs.getString("GOLF_LMS_CAR_KND_CLSS") );	// 차종코드
					
					String pu_date = rs.getString("PU_DATE");
					if (!GolfUtil.isNull(pu_date)) pu_date = DateUtil.format(pu_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("PU_DATE"		,pu_date);
					
					result.addString("PU_TIME" 			,rs.getString("PU_TIME") );		// 픽업시간
					
					String tee_date = rs.getString("TEOF_DATE");
					if (!GolfUtil.isNull(tee_date)) tee_date = DateUtil.format(tee_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("TEE_DATE"		,tee_date);
					
					result.addString("TEE_TIME" 		,rs.getString("TEOF_DATE") );
					
					
					result.addString("GF_NM" 		,rs.getString("GREEN_NM") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );	// 등록일자			
					//result.addString("CORR_ATON" 		,DateUtil.format(rs.getString("CORR_ATON"), "yyyyMMdd", "yyyy-MM-dd") );	//수정일자
					//result.addString("STTL_AMT" 		,rs.getString("STTL_AMT") );	// 결제금액
/*
					String STTL_DATE = rs.getString("STTL_DATE");
					if (!GolfUtil.isNull(STTL_DATE)) STTL_DATE = DateUtil.format(STTL_DATE, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("STTL_DATE"		,STTL_DATE);
					
					String REPY_DATE = rs.getString("REPY_DATE");
					if (!GolfUtil.isNull(REPY_DATE)) REPY_DATE = DateUtil.format(REPY_DATE, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("REPY_DATE"		,REPY_DATE);
*/					
					result.addString("HG_NM" 		    ,rs.getString("HAN_NM") );		// 이름
					//result.addString("USERCLSS" 		,rs.getString("USERCLSS") );	// 등급
					result.addString("COUNS_YN" 		,rs.getString("CSLT_YN") );	// 상담여부
					result.addString("PROG_TYPE" 		,rs.getString("PGRS_YN") );	// 진행여부
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );     // 총 추출수
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") ); 
					result.addString("RNUM"				,rs.getString("RNUM") );		// 현재 페이지 추출수
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
    private String getSelectQuery(String search_sel, String search_word, String sckd_code, String scnsl_yn, String sprgs_yn, String search_dt1, String search_dt2, String scoop_cp_cd){
        StringBuffer sql = new StringBuffer();
                
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			APLC_SEQ_NO, ");
		sql.append("\n 	DECODE( PGRS_YN 	");
		sql.append("\n 		,'D', '<font color=red><b>결제완료</b></font>'	");
		sql.append("\n 		,'C', '<font color=red><b>결제취소</b></font>'	"); 
		sql.append("\n 		,'P', '결제신청'	");
		sql.append("\n 		,'W', '<font color=red><b>환불완료</b></font>'	"); 
		sql.append("\n 		,'', '상담대기'	");
		sql.append("\n 	) PGRS_YN,	");
		
		sql.append("\n 	DECODE( CSLT_YN 	");
		sql.append("\n 		,'Y', '완료'	");
		sql.append("\n 		,'N', '<font color=red>미확인</font>'	"); 
		sql.append("\n 	) CSLT_YN,	");
		
		sql.append("\n 			CDHD_ID, EMAIL, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,");
		sql.append("\n 	DECODE( GOLF_LMS_CAR_KND_CLSS 	");
		sql.append("\n 		,'0001', '그랜드카니발'	");
		sql.append("\n 		,'0002', '에쿠스'	"); 
		sql.append("\n 		,'0003', '미니버스카운티'	"); 
		sql.append("\n 		,'0004', '스타크래프트'	"); 
		sql.append("\n 	) GOLF_LMS_CAR_KND_CLSS,	");
		sql.append("\n 			PU_DATE, PU_TIME, TEOF_DATE, TEOF_TIME, GREEN_NM, REG_ATON, CHNG_ATON, HAN_NM, ");//USERCLSS,
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");	//새로운 no값추출 
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		
		sql.append("\n 				APLC_SEQ_NO,  PGRS_YN, CSLT_YN, CDHD_ID, EMAIL, DDD_NO, TEL_HNO,");
		sql.append("\n 				TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, GOLF_LMS_CAR_KND_CLSS, PU_DATE, PU_TIME, TEOF_DATE, ");
		sql.append("\n 				TEOF_TIME, GREEN_NM,  	");
		sql.append("\n 				TO_CHAR(TO_DATE(substr(REG_ATON,1,8)), 'YY-MM-DD')||'('||substr(to_char(to_date(substr(REG_ATON,1,8),'yyyymmdd'),'DAY'),1,1)||')'||' '||substr(substr(REG_ATON,9,10),1,2)||':'||substr(substr(REG_ATON,11,12),1,2) REG_ATON, 	");
		sql.append("\n 				CHNG_ATON, 	");
		
		sql.append("\n  (SELECT HG_NM FROM BCDBA.TBGGOLFCDHD  WHERE CDHD_ID=TGL.CDHD_ID) AS HAN_NM   ");//USER테이블에서 CDHD_ID별 한글이름 조인
		//sql.append("\n  (SELECT '골프회원(VIP)' FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=TGL.CDHD_ID) AS USERCLSS  ");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAPLCMGMT	TGL");
		sql.append("\n 				WHERE APLC_SEQ_NO = APLC_SEQ_NO	");
		sql.append("\n 				AND CDHD_ID = ?	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TGL.CDHD_ID LIKE (SELECT CDHD_ID FROM BCDBA.TBGGOLFCDHD  WHERE HAN_NM like ?)	");
				sql.append("\n 				OR TGL.ZIPADDR LIKE ? )	");				
			} else {
				if (search_sel.equals("HAN_NM")) {
					sql.append("\n 				AND TGL.CDHD_ID LIKE (SELECT CDHD_ID FROM BCDBA.TBGGOLFCDHD  WHERE HAN_NM like ?)	");			
				} else {
					sql.append("\n 				AND "+search_sel+" LIKE ?	");
				}
			}
		}

		sql.append("\n 	AND GOLF_SVC_APLC_CLSS = ?	");
		
		if (!GolfUtil.isNull(sckd_code))sql.append("\n AND GOLF_LMS_CAR_KND_CLSS = ? ");		
		if (!GolfUtil.isNull(scnsl_yn))	sql.append("\n AND CSLT_YN = ? ");		
		if (!GolfUtil.isNull(sprgs_yn))	sql.append("\n AND PGRS_YN = ? ");
		if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	sql.append("\n AND (REG_ATON >= ? AND REG_ATON <= ?) ");		

		sql.append("\n 				ORDER BY APLC_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
