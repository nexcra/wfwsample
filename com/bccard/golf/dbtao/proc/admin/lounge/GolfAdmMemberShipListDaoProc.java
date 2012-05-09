/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemberShipListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 회원권 리스트
*   적용범위  : golf
*   작성일자  : 2009-07-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lounge;

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
public class GolfAdmMemberShipListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemberShipListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemberShipListDaoProc() {}	

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
			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
						
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("GREEN_MEMRTK_NM_SEQ_NO" 			,rs.getLong("GREEN_MEMRTK_NM_SEQ_NO") );
					result.addString("GREEN_MEMRTK_NM" 		,rs.getString("GREEN_MEMRTK_NM") );
					result.addString("STD_MKPR" 			,rs.getString("STD_MKPR") );
					
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
		
        sql.append("\n SELECT GREEN_MEMRTK_NM_SEQ_NO, GREEN_MEMRTK_NM, STD_MKPR, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON	");
        sql.append("\n FROM BCDBA.TBGGREENMEMRTKMGMT		");
        sql.append("\n ORDER BY GREEN_MEMRTK_NM_SEQ_NO		");
         
		return sql.toString();
    }
}
