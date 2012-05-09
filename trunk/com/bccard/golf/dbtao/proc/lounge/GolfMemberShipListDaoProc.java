/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemberShipListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 회원권 시세 리스트
*   적용범위  : golf
*   작성일자  : 2009-07-07
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lounge;

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
public class GolfMemberShipListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfMemberShipListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMemberShipListDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data, String[] gf_price_cd, String[] gf_area_cd ) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		String area_nm = "";
		boolean flag1 = false;
		boolean flag2 = false;
		
		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			
			
			for (int i = 0; i < gf_price_cd.length; i++) { 
				if (gf_price_cd[i] != null && gf_price_cd[i].length() > 0) {
					flag1 = true;
				}
			}
			
			for (int i = 0; i < gf_area_cd.length; i++) { 		
				if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
					flag2 = true;
				}
			}
			
			String sql = this.getSelectQuery(gf_price_cd, gf_area_cd, flag1, flag2);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			/*	
			for (int i = 0; i < gf_price_cd.length; i++) { // 가격별 검색			
				if (gf_price_cd[i] != null && gf_price_cd[i].length() > 0) {
					pstmt.setString(++idx, gf_price_cd[i]);
				}
			}
			*/
			/*
			for (int i = 0; i < gf_area_cd.length; i++) { // 지역별 검색			
				if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
					pstmt.setString(++idx, gf_area_cd[i]);
				}
			}
			*/
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addLong("GREEN_MEMRTK_NM_SEQ_NO" 		,rs.getLong("GREEN_MEMRTK_NM_SEQ_NO") );
					result.addString("GREEN_MEMRTK_NM" 		,rs.getString("GREEN_MEMRTK_NM") );
					result.addLong("PRIOR_QUT" 		,rs.getLong("PRIOR_QUT") );
					result.addLong("QUT" 		,rs.getLong("QUT") );
					result.addString("FLUC_UD" 		,rs.getString("FLUC_UD") );
					result.addString("FLUC" 		,rs.getString("FLUC") );
					result.addString("FLUC_RT" 		,rs.getString("FLUC_RT") );
					result.addLong("STD_MKPR" 		,rs.getLong("STD_MKPR") );
										
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
    private String getSelectQuery(String[] gf_price_cd, String[] gf_area_cd, boolean flag1, boolean flag2){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT 	 	");
        sql.append("\n 	GREEN_MEMRTK_NM_SEQ_NO, GREEN_MEMRTK_NM, PRIOR_QUT, QUT, FLUC_UD, FLUC,	 FLUC_RT, STD_MKPR	 	");
        sql.append("\n FROM (SELECT  	");
        sql.append("\n 			GREEN_MEMRTK_NM_SEQ_NO, GREEN_MEMRTK_NM, PRIOR_QUT, QUT, 	");
        sql.append("\n 	 		DECODE (SIGN (QUT - PRIOR_QUT), -1, '▼', 0, '', 1, '▲') FLUC_UD,	");
        sql.append("\n 	 		DECODE (QUT - PRIOR_QUT, 0, '-',    ' '|| REPLACE (LTRIM (TO_CHAR (QUT - PRIOR_QUT, '999,999')), '-', '')) FLUC,		");
        sql.append("\n 	 		TRIM (TO_CHAR (DECODE (PRIOR_QUT, 0, QUT, ((QUT - PRIOR_QUT) / PRIOR_QUT * 100)), '999990.00')) FLUC_RT, STD_MKPR	");
        sql.append("\n 		FROM (SELECT  	");
        sql.append("\n 	 				TGM.GREEN_MEMRTK_NM_SEQ_NO, TGM.GREEN_MEMRTK_NM,	");
        sql.append("\n 	 				NVL ((SELECT QUT FROM BCDBA.TBGGREENMEMRTKQUTMGMT	");
        sql.append("\n 	 				WHERE GREEN_MEMRTK_NM_SEQ_NO =TGM.GREEN_MEMRTK_NM_SEQ_NO	");
        sql.append("\n 				AND QUT_DATE = TO_CHAR (SYSDATE - 1, 'YYYYMMDD') 	");
		sql.append("\n 						), 0) PRIOR_QUT,  	");
		sql.append("\n 						NVL ((SELECT QUT FROM BCDBA.TBGGREENMEMRTKQUTMGMT 	");
		sql.append("\n 						WHERE GREEN_MEMRTK_NM_SEQ_NO = TGM.GREEN_MEMRTK_NM_SEQ_NO	");
		sql.append("\n 					AND QUT_DATE = TO_CHAR (SYSDATE, 'YYYYMMDD')	");
		sql.append("\n 						), 0) QUT, TGM.STD_MKPR 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGGREENMEMRTKMGMT TGM	");
		sql.append("\n 				ORDER BY TGM.GREEN_MEMRTK_NM_SEQ_NO )	");	
		sql.append("\n 		WHERE GREEN_MEMRTK_NM_SEQ_NO = GREEN_MEMRTK_NM_SEQ_NO		");
		
		if (flag1) { // 가격별 검색	
			sql.append("\n 	AND ( ");
			
			for (int i = 0; i < gf_price_cd.length; i++) { 		
				if (gf_price_cd[i] != null && gf_price_cd[i].length() > 0) {
					if (i > 0) sql.append("\n 	OR ");
					
					if (gf_price_cd[i].equals("5")) sql.append("\n 	QUT < 5000		");
					if (gf_price_cd[i].equals("5.1")) sql.append("\n 	QUT BETWEEN	5000 AND 10000	");
					if (gf_price_cd[i].equals("10")) sql.append("\n 	QUT BETWEEN	10000 AND 19999	");
					if (gf_price_cd[i].equals("20")) sql.append("\n 	QUT BETWEEN	20000 AND 29999	");
					if (gf_price_cd[i].equals("35")) sql.append("\n 	QUT BETWEEN	30000 AND 59999	");
					if (gf_price_cd[i].equals("50")) sql.append("\n 	QUT >= 50000	");
				}
			}
			
			sql.append("\n 		) ");
		}
		
		sql.append("\n 		)	");	
		
		
        /*
        sql.append("\n 	SELECT 	");
		sql.append("\n 		AFFI_GREEN_SEQ_NO, GREEN_NM, STD_MKPR, QUT_DATE, QUT, PRIOR_QUT,  	");
		sql.append("\n 		DECODE (SIGN (QUT - PRIOR_QUT), -1, '▼', 0, '-', 1, '▲') FLUC_UD,  	");
		sql.append("\n 		DECODE (QUT - PRIOR_QUT, 0, '',    ' '|| REPLACE (LTRIM (TO_CHAR (QUT - PRIOR_QUT, '999,999')), '-', '')) FLUC,  	");
		sql.append("\n 		TRIM (TO_CHAR (DECODE (PRIOR_QUT, 0, QUT, ((QUT - PRIOR_QUT) / PRIOR_QUT * 100)), '999990.000')) FLUC_RT  	");
		
		sql.append("\n 	FROM (	SELECT   	");
		sql.append("\n 	  			TGFE.AFFI_GREEN_SEQ_NO, TGF.GREEN_NM, TGFE.STD_MKPR, TGFE.QUT_DATE, TGFE.QUT,		");
		sql.append("\n 	  			NVL ((SELECT QUT 	");

		sql.append("\n 						FROM BCDBA.TBGGREENQUTMGMT 	");
		sql.append("\n 						WHERE AFFI_GREEN_SEQ_NO = TGFE.AFFI_GREEN_SEQ_NO  	");
		sql.append("\n 						AND QUT_DATE = TO_CHAR (SYSDATE - 1, 'YYYYMMDD') 	");
		sql.append("\n 						), 0) PRIOR_QUT 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIGREEN TGF, BCDBA.TBGGREENQUTMGMT TGFE	");
		sql.append("\n 				WHERE TGF.AFFI_GREEN_SEQ_NO = TGFE.AFFI_GREEN_SEQ_NO		");
		sql.append("\n 				AND TGF.AFFI_FIRM_CLSS = '0004'	");
		sql.append("\n 				AND TGFE.QUT_DATE = TO_CHAR (SYSDATE, 'YYYYMMDD')	");
		
		
		if (flag1) { // 가격별 검색	
			sql.append("\n 	AND ( ");
			
			for (int i = 0; i < gf_price_cd.length; i++) { 		
				if (gf_price_cd[i] != null && gf_price_cd[i].length() > 0) {
					if (i > 0) sql.append("\n 	OR ");
					
					if (gf_price_cd[i].equals("5")) sql.append("\n 	QUT < 500		");
					if (gf_price_cd[i].equals("5.1")) sql.append("\n 	QUT BETWEEN	500 AND 1000	");
					if (gf_price_cd[i].equals("10")) sql.append("\n 	QUT BETWEEN	1000 AND 1999	");
					if (gf_price_cd[i].equals("20")) sql.append("\n 	QUT BETWEEN	2000 AND 2999	");
					if (gf_price_cd[i].equals("35")) sql.append("\n 	QUT BETWEEN	3000 AND 5999	");
					if (gf_price_cd[i].equals("50")) sql.append("\n 	QUT >= 5000	");
				}
			}
			
			sql.append("\n 		) ");
		}
		
		if (flag2) { // 지역별 검색	
			sql.append("\n 	AND ( ");
			
			for (int i = 0; i < gf_area_cd.length; i++) { 		
				if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
					if (i > 0) sql.append("\n 	OR ");
					sql.append("\n 	TGF.GREEN_RGN_CLSS = ? ");
				}
			}
			
			sql.append("\n 		) ");
		}
		
		sql.append("\n 			ORDER BY TGFE.AFFI_GREEN_SEQ_NO DESC	");		
		sql.append("\n 	)	");		
		*/	
	
		return sql.toString();
    }
}
