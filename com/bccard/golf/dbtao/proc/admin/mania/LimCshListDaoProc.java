/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : LimCshListDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 금액관리 리스트
*   적용범위  : golf
*   작성일자  : 2009-06-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
 
package com.bccard.golf.dbtao.proc.admin.mania; 

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
public class LimCshListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public LimCshListDaoProc() {}	

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
			 
			String sql = this.getSelectQuery();   
			
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addInt("SEQ_NO" 			,rs.getInt("SEQ_NO") );
					result.addString("NAME" 		,rs.getString("CAR_KND_NM") ); 	// 차종
					result.addString("PRICE" 		,rs.getString("NORM_PRIC") ); 	// 정상가
					result.addString("PRICE2" 		,rs.getString("PCT20_DC_PRIC") ); 	// 20%할인가
					result.addString("PRICE3" 		,rs.getString("PCT30_DC_PRIC") ); 	// 30%할인가
					result.addString("CODE" 		,rs.getString("CAR_KND_CLSS") ); 	// 코드 
	
					result.addString("RESULT", "00"); //정상결과
				}
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
		sql.append("\n SELECT	*	");
		sql.append("\n FROM BCDBA.TBGAMTMGMT ");
		return sql.toString();
    }
}
