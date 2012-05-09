/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRangeRsvtMainDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : SKY72예약가능일 메인 리스트
*   적용범위  : golf
*   작성일자  : 2009-07-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfRangeRsvtMainDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfRangeRsvtMainDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfRangeRsvtMainDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data, int ListCnt, int TitleLen) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			/*
			sql = this.getCntValQuery(); //휴장 갯수 쿼리
            pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();			
			long resm_cnt = 0L;
			if(rs.next()){
				resm_cnt = rs.getLong("RESM_CNT");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            */
            /*****************************************************************************/
            
            sql = this.getSelectQuery();  
            
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			//pstmt.setLong(++idx, resm_cnt+15 );
			pstmt.setLong(++idx, 15 );
			pstmt.setInt(++idx, ListCnt);
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {

					result.addLong("RSVTTIME_SQL_NO" 				,rs.getLong("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
					result.addString("TO_YEAR", 	rs.getString("TO_YEAR") ); 	
					result.addString("TO_MONTH1", 	rs.getString("TO_MONTH") ); 
					result.addString("TO_MONTH2", 	GolfUtil.lpad(rs.getString("TO_MONTH"), 2, "0") ); 	
					result.addString("TO_DAY", 	rs.getString("TO_DAY") ); 
					result.addString("RSVT_TIME_NUM", 	rs.getString("RSVT_TIME_NUM") ); 
					result.addLong("DAY_CNT", 	rs.getLong("DAY_CNT") ); 
					
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO,	");
		sql.append("\n			 	TO_YEAR, DECODE(SUBSTRB(TO_MONTH, 1, 1), 0, SUBSTRB(TO_MONTH, 2, 2), TO_MONTH) TO_MONTH, TO_DAY, 	");
		sql.append("\n 			RSVT_TIME || ' (' || (DLY_RSVT_ABLE_PERS_NUM - RSVT_CNT) || ')' RSVT_TIME_NUM, DAY_CNT	");
		sql.append("\n 		FROM (SELECT TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO,	");
		sql.append("\n 					TO_CHAR (TO_DATE (T.DAYS, 'YYYYMMDD'), 'YYYY') TO_YEAR, TO_CHAR (TO_DATE (T.DAYS, 'YYYYMMDD'), 'MM') TO_MONTH, TO_CHAR (TO_DATE (T.DAYS, 'YYYYMMDD'), 'DD') TO_DAY,	");
		sql.append("\n 					TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24:MI') ||'~'|| TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24:MI') RSVT_TIME, TGRT.DLY_RSVT_ABLE_PERS_NUM,		");
		sql.append("\n 					(SELECT COUNT (GOLF_SVC_RSVT_NO) RSVT_CNT FROM BCDBA.TBGRSVTMGMT TGR WHERE TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO(+) AND TGR.RSVT_YN = 'Y') RSVT_CNT,	");
		sql.append("\n 					TO_DATE (T.DAYS, 'YYYYMMDD') - TRUNC (SYSDATE) DAY_CNT	");
		sql.append("\n 				FROM (SELECT TO_CHAR (BASE_DAY + LEVEL - 1, 'YYYYMMDD') DAYS	");
        sql.append("\n 						FROM (SELECT TO_DATE(TO_CHAR(SYSDATE - (TO_CHAR(SYSDATE, 'D') - 1), 'YYYYMMDD')) BASE_DAY	");
        sql.append("\n 								 FROM DUAL)	");
        sql.append("\n 						CONNECT BY BASE_DAY + LEVEL - 1 <= BASE_DAY + 27) T,		");
        sql.append("\n 						(SELECT RSVT_ABLE_SCD_SEQ_NO, RESM_YN, DLY_RSVT_ABLE_PERS, RSVT_ABLE_DATE	");
        sql.append("\n 						FROM BCDBA.TBGRSVTABLESCDMGMT	");
        sql.append("\n 						WHERE GOLF_RSVT_DAY_CLSS = 'D') TGRA, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT	");
        sql.append("\n 				WHERE T.DAYS = TGRA.RSVT_ABLE_DATE(+)		");
        sql.append("\n 				AND TGRA.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO	");
        sql.append("\n 				ORDER BY T.DAYS ASC, RSVT_TIME ASC)		");
        sql.append("\n 		WHERE DAY_CNT BETWEEN 3 AND ?	)	");
		sql.append("\n 	WHERE ROWNUM <= ?	");	
      
       return sql.toString();
    }
    
    /** ***********************************************************************
     *예약기간안에 휴장여부 카운터를 출력하여 리턴한다.    
     ************************************************************************ */
    /*
     private String getCntValQuery(){
         StringBuffer sql = new StringBuffer();
         
         sql.append("\n SELECT COUNT (RESM_YN) RESM_CNT	");
         sql.append("\n FROM (SELECT T.DAYS,	");
         sql.append("\n			TO_DATE (T.DAYS, 'YYYYMMDD') - TRUNC (SYSDATE) DAY_CNT, NVL (TGRA.RESM_YN, 'N') RESM_YN	");
         sql.append("\n		FROM (SELECT TO_CHAR (BASE_DAY + LEVEL - 1, 'YYYYMMDD') DAYS	");
         sql.append("\n				FROM (SELECT TO_DATE (TO_CHAR (SYSDATE - (TO_CHAR (SYSDATE, 'D') - 1), 'YYYYMMDD')) BASE_DAY ");
         sql.append("\n						FROM DUAL)	");
         sql.append("\n				CONNECT BY BASE_DAY + LEVEL - 1 <= BASE_DAY + 27) T,		");
         sql.append("\n				(SELECT RESM_YN, DLY_RSVT_ABLE_PERS, RSVT_ABLE_DATE	");
         sql.append("\n				FROM BCDBA.TBGRSVTABLESCDMGMT	");
         sql.append("\n				WHERE GOLF_RSVT_DAY_CLSS = 'D') TGRA	");
         sql.append("\n		WHERE T.DAYS = TGRA.RSVT_ABLE_DATE(+)		");
         sql.append("\n		ORDER BY T.DAYS ASC)		");
         sql.append("\n WHERE DAY_CNT BETWEEN 3 AND 15		");
         sql.append("\n AND RESM_YN = 'Y'	");
         
 		return sql.toString();
     }
     */

}
