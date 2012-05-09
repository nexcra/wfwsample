/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfTimeSelInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 예약시간대 셀렉트박스 생성
*   적용범위  : golf
*   작성일자  : 2009-06-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfTimeSelInqDaoProc extends AbstractProc {

	public static final String TITLE = "예약시간대 셀렉트박스 생성";
	
	/** *****************************************************************
	 * GolfTimeSelInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfTimeSelInqDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			con = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("greenSeq"));
			pstmt.setString(++idx, data.getString("DATE"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {			 
				while(rs.next())  {

					
					result.addLong("RSVTTIME_SQL_NO" ,rs.getLong("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
					result.addString("RSVT_TIME_NUM" ,rs.getString("RSVT_TIME_NUM") );
					
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
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}
	
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
    	StringBuffer sql = new StringBuffer();
		
    	sql.append("\n SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO, RSVT_TIME ||' ('|| (DLY_RSVT_ABLE_PERS_NUM - RSVT_CNT) ||')' RSVT_TIME_NUM	");
    	sql.append("\n FROM (SELECT	");
		sql.append("\n 			TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO,	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24:MI') ||'~'|| TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24:MI') RSVT_TIME, 	");
		sql.append("\n 			TGRT.DLY_RSVT_ABLE_PERS_NUM, 	");
		sql.append("\n 			(SELECT COUNT (GOLF_SVC_RSVT_NO) RSVT_CNT 	");
		sql.append("\n 				FROM BCDBA.TBGRSVTMGMT TGR 	");
		sql.append("\n 				WHERE TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO(+) 	");
		sql.append("\n 				AND TGR.RSVT_YN = 'Y' 	");
		sql.append("\n 			) RSVT_CNT 	");
		sql.append("\n 		FROM 	");
		sql.append("\n			BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT	");
		sql.append("\n 		WHERE TGRT.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO	");
		sql.append("\n 		AND TGRD.GOLF_RSVT_DAY_CLSS = 'D'	");
		sql.append("\n 		AND TGRD.AFFI_GREEN_SEQ_NO = ?	");
		sql.append("\n 		AND TGRD.RSVT_ABLE_DATE = ?	)	");
		
		return sql.toString();
    }
}
