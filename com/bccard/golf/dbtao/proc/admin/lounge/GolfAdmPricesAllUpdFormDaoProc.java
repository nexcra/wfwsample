/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmPricesAllUpdFormDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 시세 전체수정
*   적용범위  : golf
*   작성일자  : 2009-07-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*2009-11-24   1.1    정종오   시세값 이전내용중 0 초과인 값으로 가져와 세팅
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
public class GolfAdmPricesAllUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmPricesAllUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmPricesAllUpdFormDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("QUT_DATE"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addLong("GREEN_MEMRTK_NM_SEQ_NO" 			,rs.getLong("GREEN_MEMRTK_NM_SEQ_NO") );
					result.addString("GREEN_MEMRTK_NM" 			,rs.getString("GREEN_MEMRTK_NM") );
					result.addLong("QUT" 		,rs.getLong("QUT") );
					
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
		sql.append("\n 	TGM.GREEN_MEMRTK_NM_SEQ_NO, TGM.GREEN_MEMRTK_NM,	 ");
		sql.append("\n 	NVL ((SELECT QUT	 			");
		sql.append("\n 	      FROM (SELECT * 			"); 
		sql.append("\n 	      		FROM BCDBA.TBGGREENMEMRTKQUTMGMT  "); 
		sql.append("\n 	        	WHERE QUT_DATE <= ? AND QUT > 0 ORDER BY QUT_DATE DESC) "); 
		sql.append("\n 	      WHERE ROWNUM < 2 AND TGM.GREEN_MEMRTK_NM_SEQ_NO = GREEN_MEMRTK_NM_SEQ_NO) , 0) QUT "); 
		sql.append("\n FROM BCDBA.TBGGREENMEMRTKMGMT TGM 	");
		sql.append("\n ORDER BY TGM.GREEN_MEMRTK_NM_SEQ_NO 	");
		
		return sql.toString();
    }
}
