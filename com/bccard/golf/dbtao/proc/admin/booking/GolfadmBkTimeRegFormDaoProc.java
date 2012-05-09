/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admTitimeRegFormDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹티타임 입력 페이지 처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking;

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
 * Golf
 * @author	미디어포스 
 * @version	1.0
 ******************************************************************************/
public class GolfadmBkTimeRegFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmBkTimeRegFormDaoProc 프로세스 생성자  
	 * @param N/A
	 ***************************************************************** */
	public GolfadmBkTimeRegFormDaoProc() {}	

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
			 
			//조회 ----------------------------------------------------------
			String sort				= data.getString("SORT");
			String drivR			= data.getString("DrivR");

			String sql = this.getSelectQuery(sort, drivR);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, sort);
			
			if (!GolfUtil.isNull(drivR)) { //
				pstmt.setString(++idx, drivR);
			}
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addInt("SEQ_NO" 				,rs.getInt("SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("RNUM"				,rs.getString("RNUM") );
										
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
    private String getSelectQuery(String sort, String drivR){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			SEQ_NO, GR_NM		 	");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				AFFI_GREEN_SEQ_NO AS SEQ_NO, GREEN_NM AS GR_NM 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIGREEN	");
		sql.append("\n 				WHERE AFFI_GREEN_SEQ_NO = AFFI_GREEN_SEQ_NO	");
		sql.append("\n 				AND AFFI_FIRM_CLSS = ?	");
        if (!GolfUtil.isNull(drivR)) { 
        	sql.append("\n 			AND GOLF_RNG_CLSS = ?	");
        }		
		sql.append("\n 				ORDER BY AFFI_GREEN_SEQ_NO ASC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
	

		return sql.toString();
    }
}
