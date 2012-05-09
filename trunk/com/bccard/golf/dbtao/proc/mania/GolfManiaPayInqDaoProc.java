/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfLsnRecvInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 레슨신청 상세조회
*   적용범위  : golf
*   작성일자  : 2009-06-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.mania;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException; 
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfManiaPayInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfLsnRecvInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfManiaPayInqDaoProc() {}	

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
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);

			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("ODR_NO")); //p_idx 상품번호
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	

					result.addLong("APLC_SEQ_NO" 		,rs.getLong("APLC_SEQ_NO") );
					result.addString("CDHD_ID" 			,rs.getString("CDHD_ID") );
					
					result.addString("ODR_NO"			,rs.getString("ODR_NO") );
					result.addString("STTL_MTHD_CLSS"	,rs.getString("STTL_MTHD_CLSS") );					
					result.addString("STTL_STAT_CLSS" 	,rs.getString("STTL_STAT_CLSS") );			
					result.addString("STTL_STAT_CLSS_NM" 	,rs.getString("STTL_STAT_CLSS_NM") );
					
					result.addLong("STTL_AMT" 			,rs.getLong("STTL_AMT") );
					result.addString("MER_NO"			,rs.getString("MER_NO") );
					result.addString("CARD_NO"			,rs.getString("CARD_NO") );

					result.addString("AUTH_NO"			,rs.getString("AUTH_NO") );
					result.addString("STTL_ATON"		,rs.getString("STTL_ATON") );
					result.addString("CNCL_ATON"		,rs.getString("CNCL_ATON") );
					result.addString("POSS_CNCL_ATON"	,rs.getString("POSS_CNCL_ATON") );
					result.addString("POSS_CNCL_CLSS"	,rs.getString("POSS_CNCL_CLSS") );

										
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
		
		sql.append("\n SELECT	");
		sql.append("\n 		TGA.APLC_SEQ_NO, TGA.CDHD_ID,	");
		sql.append("\n 		TGS.ODR_NO, TGS.STTL_MTHD_CLSS, TGS.STTL_STAT_CLSS, DECODE(TGS.STTL_STAT_CLSS,'N','결제완료','Y','취소완료') STTL_STAT_CLSS_NM, TGS.STTL_AMT, TGS.MER_NO, TGS.CARD_NO, 	");
		sql.append("\n 		TGS.AUTH_NO, TO_CHAR(TO_DATE(TGS.STTL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') STTL_ATON, TO_CHAR(TO_DATE(TGS.CNCL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') CNCL_ATON,	");

		sql.append("\n 		NVL(TO_CHAR(TO_DATE(TGA.PU_DATE, 'YYYYMMDDHH24MISS')-2, 'YYYY-MM-DD'),'-') POSS_CNCL_ATON, 	");
		sql.append("\n 		CASE NVL(TO_CHAR(TO_DATE(TGA.PU_DATE, 'YYYYMMDDHH24MISS')-2, 'YYYY-MM-DD'),'-') WHEN '-' THEN 'N' 	");
		sql.append("\n 		ELSE 	");
		sql.append("\n 			CASE WHEN NVL(TO_CHAR(TO_DATE(TGA.PU_DATE, 'YYYYMMDDHH24MISS')-3, 'YYYYMMDD'),0) < TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'N' ELSE 'Y' END 	");
		sql.append("\n 		END 	");
		sql.append("\n 		POSS_CNCL_CLSS 	");
		sql.append("\n 	FROM 	");
		sql.append("\n 	BCDBA.TBGAPLCMGMT TGA, BCDBA.TBGSTTLMGMT TGS	");
		sql.append("\n 	WHERE TGA.APLC_SEQ_NO=TGS.STTL_GDS_SEQ_NO	");
		sql.append("\n 	AND TGS.STTL_GDS_CLSS = '0005'		");
		sql.append("\n 	AND TGA.GOLF_SVC_APLC_CLSS = '0002'		");	
		sql.append("\n 	AND TGS.STTL_GDS_SEQ_NO = ?		");	
		sql.append("\n 	AND TGA.CDHD_ID = ?		");

		return sql.toString();
    } 
}
