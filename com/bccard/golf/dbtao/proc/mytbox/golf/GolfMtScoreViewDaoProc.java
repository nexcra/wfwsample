/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtScoreViewDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 마이티박스 > 골프정보 > 스코어 보기 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19  
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.mytbox.golf;

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
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfMtScoreViewDaoProc extends AbstractProc {
	
	public GolfMtScoreViewDaoProc() {}	

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
			pstmt.setLong(++idx, data.getLong("SEQ_NO"));
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {

					result.addInt("SEQ_NO" 				,rs.getInt("SEQ_NO") );
					result.addString("GREEN_NM" 		,rs.getString("GREEN_NM") );
					result.addString("ROUND_DATE_VIEW" 	,rs.getString("ROUND_DATE_VIEW") );
					result.addString("ROUND_DATE_UPD" 	,rs.getString("ROUND_DATE_UPD") );
					result.addString("ROUND_DATE" 		,rs.getString("ROUND_DATE") );
					result.addString("CURS_NM" 		,rs.getString("CURS_NM") );
					result.addString("HADC_NUM" 		,rs.getString("HADC_NUM") );
					
					result.addInt("HOLE01_SCOR" 		,rs.getInt("HOLE01_SCOR") );
					result.addInt("HOLE02_SCOR" 		,rs.getInt("HOLE02_SCOR") );
					result.addInt("HOLE03_SCOR" 		,rs.getInt("HOLE03_SCOR") );
					result.addInt("HOLE04_SCOR" 		,rs.getInt("HOLE04_SCOR") );
					result.addInt("HOLE05_SCOR" 		,rs.getInt("HOLE05_SCOR") );
					result.addInt("HOLE06_SCOR" 		,rs.getInt("HOLE06_SCOR") );
					result.addInt("HOLE07_SCOR" 		,rs.getInt("HOLE07_SCOR") );
					result.addInt("HOLE08_SCOR" 		,rs.getInt("HOLE08_SCOR") );
					result.addInt("HOLE09_SCOR" 		,rs.getInt("HOLE09_SCOR") );
					result.addInt("HOLE10_SCOR" 		,rs.getInt("HOLE10_SCOR") );
					result.addInt("HOLE11_SCOR" 		,rs.getInt("HOLE11_SCOR") );
					result.addInt("HOLE12_SCOR" 		,rs.getInt("HOLE12_SCOR") );
					result.addInt("HOLE13_SCOR" 		,rs.getInt("HOLE13_SCOR") );
					result.addInt("HOLE14_SCOR" 		,rs.getInt("HOLE14_SCOR") );
					result.addInt("HOLE15_SCOR" 		,rs.getInt("HOLE15_SCOR") );
					result.addInt("HOLE16_SCOR" 		,rs.getInt("HOLE16_SCOR") );
					result.addInt("HOLE17_SCOR" 		,rs.getInt("HOLE17_SCOR") );
					result.addInt("HOLE18_SCOR" 		,rs.getInt("HOLE18_SCOR") );
					
					result.addInt("HIT_CNT" 			,rs.getInt("HIT_CNT") );
					result.addInt("EG_NUM" 				,rs.getInt("EG_NUM") );
					result.addInt("BID_NUM" 			,rs.getInt("BID_NUM") );
					result.addInt("PAR_NUM" 			,rs.getInt("PAR_NUM") );
					result.addInt("BOG_NUM" 			,rs.getInt("BOG_NUM") );
					result.addInt("DOB_BOG_NUM" 		,rs.getInt("DOB_BOG_NUM") );
					result.addInt("TRP_BOG_NUM" 		,rs.getInt("TRP_BOG_NUM") );
					result.addInt("ETC_BOG_NUM" 		,rs.getInt("ETC_BOG_NUM") );
					result.addString("ROUND_MEMO_CTNT" 	,rs.getString("ROUND_MEMO_CTNT") );
					result.addString("ROUND_YEAR" 		,rs.getString("ROUND_YEAR") );
					result.addString("ROUND_MONTH" 		,rs.getString("ROUND_MONTH") );
					result.addString("ROUND_DAY" 		,rs.getString("ROUND_DAY") );
					
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
        		
		sql.append("\n ");
		sql.append("\t  SELECT  																											\n");
		sql.append("\t  	SEQ_NO, GREEN_NM, CURS_NM, HADC_NUM, ROUND_DATE																\n");
		sql.append("\t  	, SUBSTR(ROUND_DATE,1,4)||'년 '||SUBSTR(ROUND_DATE,5,2)||'월 '||SUBSTR(ROUND_DATE,7,2)||'일' ROUND_DATE_VIEW		\n");
		sql.append("\t  	, SUBSTR(ROUND_DATE,1,4) AS ROUND_YEAR, SUBSTR(ROUND_DATE,5,2) AS ROUND_MONTH, SUBSTR(ROUND_DATE,7,2) AS ROUND_DAY	\n");
		sql.append("\t  	, SUBSTR(ROUND_DATE,1,4)||'-'||SUBSTR(ROUND_DATE,5,2)||'-'||SUBSTR(ROUND_DATE,7,2) ROUND_DATE_UPD				\n");
		sql.append("\t  	, HOLE01_SCOR, HOLE02_SCOR, HOLE03_SCOR, HOLE04_SCOR, HOLE05_SCOR, HOLE06_SCOR, HOLE07_SCOR, HOLE08_SCOR 		\n");
		sql.append("\t  	, HOLE09_SCOR, HOLE10_SCOR, HOLE11_SCOR, HOLE12_SCOR, HOLE13_SCOR, HOLE14_SCOR, HOLE15_SCOR, HOLE16_SCOR 		\n");
		sql.append("\t  	, HOLE17_SCOR, HOLE18_SCOR																				 		\n");
		sql.append("\t  	, HIT_CNT, EG_NUM, BID_NUM, PAR_NUM, BOG_NUM, DOB_BOG_NUM, TRP_BOG_NUM, ETC_BOG_NUM, ROUND_MEMO_CTNT		 	\n");
		sql.append("\t  	FROM BCDBA.TBGSCORINFO 																							\n");
		sql.append("\t  	WHERE SEQ_NO=? 																									\n");
		
		return sql.toString();
    }
}
