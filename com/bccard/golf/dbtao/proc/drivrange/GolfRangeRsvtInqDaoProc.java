/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRangeRsvtInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : SKY72드림골프레인지 예약신청
*   적용범위  : golf
*   작성일자  : 2009-06-11
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
public class GolfRangeRsvtInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfRangeRsvtInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfRangeRsvtInqDaoProc() {}	

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
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			
			String sch_gr = data.getString("SCH_GR_SEQ_NO");
			
			sql = this.getCntValQuery(); //휴장 갯수 쿼리
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sch_gr);
            
			rs = pstmt.executeQuery();			
			long resm_cnt = 0L;
			if(rs.next()){
				resm_cnt = rs.getLong("RESM_CNT");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			             
			//조회 ----------------------------------------------------------            
			sql = this.getSelectQuery(sch_gr);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, resm_cnt+15 );
			
			if (!GolfUtil.isNull(sch_gr)){
				pstmt.setString(++idx, sch_gr);
				pstmt.setString(++idx, sch_gr);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {
					
					result.addString("TO_YEAR", rs.getString("TO_YEAR") ); 	
					result.addString("TO_MONTH", rs.getString("TO_MONTH") ); 	
					result.addString("TO_DAY", rs.getString("TO_DAY") ); 
					result.addString("DAY_CNT", rs.getString("DAY_CNT") ); // 현재 날짜 차이
					result.addString("RESM_YN", rs.getString("RESM_YN") ); // 휴장 여부
					result.addString("DLY_RSVT_ABLE_PERS", rs.getString("DLY_RSVT_ABLE_PERS") ); //일일예약가능인원
					result.addString("RSVT_CNT", rs.getString("RSVT_CNT") ); // 현재 예약인원
					result.addString("RSVT_TOTAL", rs.getString("RSVT_TOTAL") ); // 현재 남은인원
					result.addString("RSVT_YN", rs.getString("RSVT_YN") ); //예약가능일 여부
					
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
    private String getSelectQuery(String sch_gr){
    	
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT	");
        sql.append("\t 	TO_YEAR, TO_MONTH, TO_DAY, DAY_CNT, RESM_YN, DLY_RSVT_ABLE_PERS,	\n");
        sql.append("\t 	NVL (RSVT_CNT, 0) RSVT_CNT, (DLY_RSVT_ABLE_PERS - RSVT_CNT) RSVT_TOTAL,		\n");
        sql.append("\t 	CASE WHEN DAY_CNT BETWEEN 2 AND ? THEN 'Y' END AS RSVT_YN	\n"); //2일전부터 받기 2009.10.07 변경 
        sql.append("\n FROM (SELECT");
        sql.append("\t  			TO_CHAR (TO_DATE (T.DAYS, 'YYYYMMDD'), 'YYYY') TO_YEAR,   \n");
        sql.append("\t  			TO_CHAR (TO_DATE (T.DAYS, 'YYYYMMDD'), 'MM') TO_MONTH,   \n");
        sql.append("\t  			TO_CHAR (TO_DATE (T.DAYS, 'YYYYMMDD'), 'DD') TO_DAY,   \n");
        sql.append("\t  			TO_DATE (T.DAYS, 'YYYYMMDD') - TRUNC (SYSDATE) DAY_CNT,   \n");
        sql.append("\t  			NVL (TGRA.RESM_YN, 'N') RESM_YN, TGRA.DLY_RSVT_ABLE_PERS,    \n");
        sql.append("\t  			(SELECT COUNT (TGR.GOLF_SVC_RSVT_NO) RSVT_CNT  \n");
        sql.append("\t  			FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT  \n");
        sql.append("\t  			WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO(+)  \n");
        sql.append("\t  			AND TGRT.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO  \n");
        sql.append("\t  			AND TGRD.GOLF_RSVT_DAY_CLSS = 'D'  \n");
        
        if (!GolfUtil.isNull(sch_gr)) { //골프장
        	sql.append("\t  			AND TGRD.AFFI_GREEN_SEQ_NO = ?   \n");
		}        
        
        sql.append("\t  			AND TGR.RSVT_YN = 'Y'  \n");
        sql.append("\t  			AND TGRD.RSVT_ABLE_DATE = T.DAYS) RSVT_CNT  \n");
        sql.append("\n 		FROM (SELECT TO_CHAR (BASE_DAY + LEVEL - 1, 'YYYYMMDD') DAYS	\n");
		sql.append("\t	 				FROM (SELECT TO_DATE (TO_CHAR (SYSDATE - (TO_CHAR (SYSDATE, 'D') - 1), 'YYYYMMDD')) BASE_DAY   \n");
		sql.append("\n 						FROM DUAL)	\n");
		sql.append("\n 				CONNECT BY BASE_DAY + LEVEL - 1 <= BASE_DAY + 27) T,		\n");
		sql.append("\n 				(SELECT RESM_YN, DLY_RSVT_ABLE_PERS, RSVT_ABLE_DATE	\n");
		sql.append("\n 				FROM BCDBA.TBGRSVTABLESCDMGMT	\n");
		sql.append("\n 				WHERE GOLF_RSVT_DAY_CLSS = 'D'	\n");
		
        if (!GolfUtil.isNull(sch_gr)) { //골프장
			sql.append("\n 			AND AFFI_GREEN_SEQ_NO = ? 	");
		}
        
		sql.append("\n 				) TGRA	\n");
		sql.append("\n 		WHERE T.DAYS = TGRA.RSVT_ABLE_DATE(+)		\n");
		sql.append("\n 		ORDER BY T.DAYS ASC		\n");
		sql.append("\n 		)	\n");
		
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
         sql.append("\n				WHERE GOLF_RSVT_DAY_CLSS = 'D'	");
         sql.append("\n				AND AFFI_GREEN_SEQ_NO = ? ) TGRA	");
         sql.append("\n		WHERE T.DAYS = TGRA.RSVT_ABLE_DATE(+)		");
         sql.append("\n		ORDER BY T.DAYS ASC)		");
         sql.append("\n WHERE DAY_CNT BETWEEN 3 AND 15		");
         sql.append("\n AND RESM_YN = 'Y'	");
         
 		return sql.toString();
     }
}
