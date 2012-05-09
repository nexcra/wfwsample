/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfGoodFoodSelDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 맛집 인근골프장 다중 셀렉트
*   적용범위  : golf
*   작성일자  : 2009-06-22
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
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfGoodFoodSelDaoProc extends AbstractProc {

	public static final String TITLE = "맛집 인근골프장 다중 셀렉트";
	
	/** *****************************************************************
	 * GolfGoodFoodSelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfGoodFoodSelDaoProc() {}	

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
			
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	");
			sql.append("\n 	AFFI_GREEN_SEQ_NO, LTRIM (MAX (SYS_CONNECT_BY_PATH (GREEN_NM, ',')), ',') GREEN_NM	");
			sql.append("\n FROM (SELECT AFFI_GREEN_SEQ_NO, 	");
			sql.append("\n 			GREEN_NM, ROW_NUMBER () OVER (PARTITION BY AFFI_GREEN_SEQ_NO ORDER BY GREEN_NM) CNT 	");
			sql.append("\n 		  FROM (SELECT TGFDI.AFFI_GREEN_SEQ_NO, TGF.GREEN_NM 	");
			sql.append("\n 					FROM BCDBA.TBGETHSNGHBGREEN TGFDI, BCDBA.TBGAFFIGREEN TGF 	");
			sql.append("\n 					WHERE TGFDI.AFFI_GREEN_SEQ_NO = TGF.AFFI_GREEN_SEQ_NO 	");
			sql.append("\n 					AND TGFDI.AFFI_ETHS_SEQ_NO = ? 	");
			sql.append("\n 					) ");
			sql.append("\n 			) ");
			sql.append("\n START WITH CNT = 1	");
			sql.append("\n CONNECT BY PRIOR CNT = CNT - 1	");
			sql.append("\n GROUP BY AFFI_GREEN_SEQ_NO	");
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("FD_SEQ_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("GF_SEQ_NO" ,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("GF_NM" ,rs.getString("GREEN_NM") );
					
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
}
