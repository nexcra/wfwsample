/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGoodFoodUpdFormDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 맛집 수정
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lounge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmGoodFoodUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmGoodFoodUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmGoodFoodUpdFormDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, data.getLong("FD_SEQ_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					String addrClss = rs.getString("NW_OLD_ADDR_CLSS");
					
					if ( addrClss == null || addrClss.trim().equals("")){
						addrClss = "1";
					}
					
					
					result.addLong("FD_SEQ_NO" 				,rs.getLong("AFFI_ETHS_SEQ_NO") );
					result.addString("FD_NM" 				,rs.getString("ETHS_NM") );
					result.addString("GF_SEQ_NO" 			,rs.getString("AFFI_GREEN_SEQ_NO") );
					result.addString("FD_AREA_CD" 			,rs.getString("ETHS_RGN_CODE") );
					result.addString("FD1_LEV_CD" 			,rs.getString("FOOD_SQ1_CTGO") );
					result.addString("FD2_LEV_CD" 			,rs.getString("FOOD_SQ2_CTGO") );
					result.addString("FD3_LEV_CD" 			,rs.getString("FOOD_SQ3_CTGO") );
					result.addString("IMG_NM" 				,rs.getString("ETHS_IMG") );
					result.addString("BEST_YN" 				,rs.getString("BEST_YN") );
					result.addString("NEW_YN" 				,rs.getString("ANW_BLTN_ARTC_YN") );
					result.addString("ZIPCODE1" 			,rs.getString("ZP1") );
					result.addString("ZIPCODE2" 			,rs.getString("ZP2") );
					result.addString("ZIPADDR" 				,rs.getString("ADDR") );
					result.addString("DETAILADDR" 			,rs.getString("DTL_ADDR") );
					result.addString("ADDRCLSS"				,addrClss );
					result.addString("CHG_DDD_NO" 			,rs.getString("DDD_NO") );
					result.addString("CHG_TEL_HNO" 			,rs.getString("TEL_HNO") );
					result.addString("CHG_TEL_SNO" 			,rs.getString("TEL_SNO") );
					result.addString("ROAD_SRCH" 			,rs.getString("POS_EXPL") );
					result.addString("MAP_NM" 				,rs.getString("OLM_IMG") );
					result.addString("PARKING_YN" 			,rs.getString("PARK_ABLE_YN") );
					result.addString("START_HH" 			,rs.getString("STRT_HH") );
					result.addString("START_MI" 			,rs.getString("STRT_MI") );
					result.addString("END_HH" 				,rs.getString("END_HH") );
					result.addString("END_MI" 				,rs.getString("END_MI") );
					result.addString("SLS_END_DAY" 			,rs.getString("HDAY_INFO") );
					result.addString("URL" 					,rs.getString("WEB_SITE_URL") );
					result.addString("CTNT" 				,rs.getString("CTNT") );
					result.addString("FD_MENU" 				,rs.getString("ETHS_MAI_MENU_EXPL") );
					result.addString("REGION_GREEN_NM" 		,rs.getString("NGHB_GREEN_EXPL") );

					result.addString("MAIN_BANNER_IMG" 			,rs.getString("MAIN_BANNER_IMG") );
					result.addString("MAIN_BANNER_URL" 			,rs.getString("MAIN_BANNER_URL") );
					result.addString("MAIN_EPS_YN" 			,rs.getString("MAIN_EPS_YN") );
					result.addString("MAIN_RPRS_IMG" 			,rs.getString("MAIN_RPRS_IMG") );
					result.addString("MAIN_RPRS_IMG_URL" 			,rs.getString("MAIN_RPRS_IMG_URL") );
					result.addString("MAIN_RPRS_IMG_EPS_YN" 			,rs.getString("MAIN_RPRS_IMG_EPS_YN") );
					/*
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
					*/
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT");
		sql.append("\n 	TGFD.AFFI_ETHS_SEQ_NO, TGFD.ETHS_NM, T.AFFI_GREEN_SEQ_NO, TGFD.ETHS_RGN_CODE, TGFD.FOOD_SQ1_CTGO, TGFD.FOOD_SQ2_CTGO, TGFD.FOOD_SQ3_CTGO, TGFD.ETHS_IMG,	 TGFD.BEST_YN, TGFD.ANW_BLTN_ARTC_YN,	 ");
		sql.append("\n  	SUBSTR (TGFD.ZP, 1, 3) ZP1, SUBSTR (TGFD.ZP, 4, 6) ZP2,	 ");
		sql.append("\n  	TGFD.ADDR, TGFD.DTL_ADDR, TGFD.NW_OLD_ADDR_CLSS, TGFD.DDD_NO, TGFD.TEL_HNO, TGFD.TEL_SNO, TGFD.POS_EXPL, TGFD.OLM_IMG, TGFD.PARK_ABLE_YN,  ");
		sql.append("\n		SUBSTR (TRIM(TGFD.SLS_STRT_TIME), 1, 2) STRT_HH, SUBSTR (TRIM(TGFD.SLS_STRT_TIME), 3, 4) STRT_MI,  ");
		sql.append("\n		SUBSTR (TRIM(TGFD.SLS_END_TIME), 1, 2) END_HH, SUBSTR (TRIM(TGFD.SLS_END_TIME), 3, 4) END_MI,  ");
		sql.append("\n		TGFD.HDAY_INFO, TGFD.WEB_SITE_URL, TGFD.CTNT, TGFD.ETHS_MAI_MENU_EXPL, TGFD.NGHB_GREEN_EXPL,  ");
		sql.append("\n		TGFD.MAIN_BANNER_IMG, TGFD.MAIN_BANNER_URL, TGFD.MAIN_EPS_YN, TGFD.MAIN_RPRS_IMG, TGFD.MAIN_RPRS_IMG_URL, TGFD.MAIN_RPRS_IMG_EPS_YN  ");
		sql.append("\n FROM BCDBA.TBGAFFIETHS TGFD, 	");
		sql.append("\n (SELECT AFFI_ETHS_SEQ_NO, 	");
		sql.append("\n LTRIM (MAX (SYS_CONNECT_BY_PATH (AFFI_GREEN_SEQ_NO, ',')), ',') AFFI_GREEN_SEQ_NO 	");
		sql.append("\n 	FROM (SELECT		");
		sql.append("\n 		AFFI_ETHS_SEQ_NO, AFFI_GREEN_SEQ_NO, ROW_NUMBER () OVER (PARTITION BY AFFI_ETHS_SEQ_NO ORDER BY AFFI_GREEN_SEQ_NO) RN 	");
		sql.append("\n 		FROM (SELECT AFFI_ETHS_SEQ_NO, AFFI_GREEN_SEQ_NO FROM BCDBA.TBGETHSNGHBGREEN)) 	");
		sql.append("\n		START WITH RN = 1 	");
		sql.append("\n 	CONNECT BY PRIOR AFFI_ETHS_SEQ_NO = AFFI_ETHS_SEQ_NO 	");
		sql.append("\n 	AND PRIOR RN = RN - 1 	");
		sql.append("\n 	GROUP BY AFFI_ETHS_SEQ_NO) T 	");
		sql.append("\n WHERE TGFD.AFFI_ETHS_SEQ_NO = T.AFFI_ETHS_SEQ_NO(+) 	");
		sql.append("\n AND TGFD.AFFI_ETHS_SEQ_NO = ? 	");
		return sql.toString();
    }
}
