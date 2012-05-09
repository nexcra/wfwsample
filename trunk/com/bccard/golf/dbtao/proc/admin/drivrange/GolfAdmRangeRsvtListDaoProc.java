/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeRsvtListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 예약 리스트
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.drivrange;

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
public class GolfAdmRangeRsvtListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmRangeRsvtListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeRsvtListDaoProc() {}	

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
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String start_dt		= data.getString("START_DT");
			String end_dt		= data.getString("END_DT");
			String start_time	= data.getString("START_TIME");
			String end_time		= data.getString("END_TIME");			
			String sch_gr 		= data.getString("SCH_GR_SEQ_NO");
			
			sql = this.getCntValQuery(); //휴장 갯수 쿼리
            pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();			
			long resm_cnt = 0L;
			if(rs.next()){
				resm_cnt = rs.getLong("RESM_CNT");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getSelectQuery(start_dt,end_dt,start_time,end_time, sch_gr);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			
			if (!GolfUtil.isNull(sch_gr)){
				pstmt.setString(++idx, sch_gr);
			}
			
			if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // 날짜 검색
				pstmt.setString(++idx, start_dt);
				pstmt.setString(++idx, end_dt);
			}
			
			if (!GolfUtil.isNull(start_time) && !GolfUtil.isNull(end_time)) { // 시간 검색
				pstmt.setString(++idx, start_time);
				pstmt.setString(++idx, end_time);
			}
			
			pstmt.setLong(++idx, resm_cnt+15 );
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {
					
					result.addLong("RSVTTIME_SQL_NO" 		,rs.getLong("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
					result.addString("RSVT_DATE"		,rs.getString("RSVT_DATE"));
					result.addString("RSVT_TIME" 		,rs.getString("RSVT_TIME") );
					result.addLong("RSVT_TOTAL_NUM"			,rs.getLong("DLY_RSVT_ABLE_PERS") );
					result.addLong("RSVT_CNT"			,rs.getLong("RSVT_CNT") );
					result.addLong("RSVT_TOTAL"			,rs.getLong("RSVT_TOTAL") );
										
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("GR_NM"		,rs.getString("GR_NM") );
										
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
    private String getSelectQuery(String start_dt, String end_dt, String start_time, String end_time, String sch_gr){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
        sql.append("\n FROM (SELECT ROWNUM RNUM,		");
        sql.append("\n 			RSVT_ABLE_BOKG_TIME_SEQ_NO, RSVT_DATE, RSVT_TIME, DAY_CNT, RESM_YN, DLY_RSVT_ABLE_PERS, NVL (RSVT_CNT, 0) RSVT_CNT, GR_NM, 	");
        sql.append("\n 			(DLY_RSVT_ABLE_PERS - RSVT_CNT) RSVT_TOTAL, CEIL (ROWNUM / ?) AS PAGE, MAX (ROWNUM) OVER () TOT_CNT		");
        sql.append("\n 		FROM (SELECT ROWNUM RNUM, TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO,		");
        sql.append("\n 					TO_CHAR (TO_DATE (T.DAYS, 'YYYYMMDD'), 'YYYY') ||'년 '|| TO_CHAR (TO_DATE (T.DAYS, 'YYYYMMDD'), 'MM') ||'월 '|| TO_CHAR (TO_DATE (T.DAYS, 'YYYYMMDD'), 'DD') ||'일' RSVT_DATE,	");
        sql.append("\n 					TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24:MI') ||'~'|| TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24:MI') RSVT_TIME,		");
        sql.append("\n 					TO_DATE (T.DAYS, 'YYYYMMDD') - TRUNC (SYSDATE) DAY_CNT,		");
        sql.append("\n 					NVL (TGRA.RESM_YN, 'N') RESM_YN,	");
        sql.append("\n 					TGRA.DLY_RSVT_ABLE_PERS,	");
        sql.append("\n 					(SELECT COUNT (TGR.GOLF_SVC_RSVT_NO) RSVT_CNT	");
        sql.append("\n 					FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRTM	");
        sql.append("\n 					WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRTM.RSVT_ABLE_BOKG_TIME_SEQ_NO(+)	");
        sql.append("\n 					AND TGRTM.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO	");
        sql.append("\n 					AND TGR.RSVT_YN = 'Y'	");
        sql.append("\n 					AND TGRD.GOLF_RSVT_DAY_CLSS = 'D'	");
        sql.append("\n 					AND TGRD.RSVT_ABLE_DATE = T.DAYS		");
        sql.append("\n 					AND TGRTM.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO) RSVT_CNT, TGRN.GREEN_NM GR_NM 	");
        sql.append("\n 				FROM (SELECT TO_CHAR (BASE_DAY + LEVEL - 1, 'YYYYMMDD') DAYS	");
        sql.append("\n 						FROM (SELECT TO_DATE(TO_CHAR(SYSDATE - (TO_CHAR(SYSDATE, 'D') - 1), 'YYYYMMDD')) BASE_DAY	");
        sql.append("\n 								 FROM DUAL)	");
        sql.append("\n 						CONNECT BY BASE_DAY + LEVEL - 1 <= BASE_DAY + 27) T,		");
        sql.append("\n 						(SELECT RSVT_ABLE_SCD_SEQ_NO, RESM_YN, DLY_RSVT_ABLE_PERS, RSVT_ABLE_DATE, AFFI_GREEN_SEQ_NO	");
        sql.append("\n 						FROM BCDBA.TBGRSVTABLESCDMGMT	");
        sql.append("\n 						WHERE GOLF_RSVT_DAY_CLSS = 'D') TGRA, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT, BCDBA.TBGAFFIGREEN TGRN	");
        sql.append("\n 				WHERE T.DAYS = TGRA.RSVT_ABLE_DATE(+)		");
        sql.append("\n 				AND TGRA.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO	");
        sql.append("\n 				AND TGRA.AFFI_GREEN_SEQ_NO = TGRN.AFFI_GREEN_SEQ_NO	");
        
        if (!GolfUtil.isNull(sch_gr)) { //골프장
			sql.append("\n 			AND TGRA.AFFI_GREEN_SEQ_NO = ?	");
		}
        
        if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // 날짜 검색
			sql.append("\n 			AND T.DAYS BETWEEN ? AND ?	");
		}
		
		if (!GolfUtil.isNull(start_time) && !GolfUtil.isNull(end_time)) { // 시간 검색
			sql.append("\n 			AND (TGRT.RSVT_STRT_TIME = ? ");
			sql.append("\n 				AND TGRT.RSVT_END_TIME = ?	) ");
		}
		
        sql.append("\n 				ORDER BY T.DAYS ASC)		");
        sql.append("\n 		WHERE DAY_CNT BETWEEN 3 AND ?		");
        sql.append("\n 		ORDER BY RNUM)	");
        sql.append("\n WHERE PAGE = ?		");
      
       return sql.toString();
    }
    
    /** ***********************************************************************
     *예약기간안에 휴장여부 카운터를 출력하여 리턴한다.    
     ************************************************************************ */
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
}
