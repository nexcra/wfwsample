/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemCyberInfoDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 멤버십 클럽하우스 > 사이버 머니 안내 > 사이버 머니 안내
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.member.cyber;

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
public class GolfMemCyberInfoDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPreGrViewDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMemCyberInfoDaoProc() {}	

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
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("GEN_WKD_BOKG_AMT" 		,GolfUtil.comma(rs.getString("GEN_WKD_BOKG_AMT")) );
					result.addString("GEN_WKE_BOKG_AMT" 		,GolfUtil.comma(rs.getString("GEN_WKE_BOKG_AMT")) );
					result.addString("WKD_GREEN_DC_AMT" 		,GolfUtil.comma(rs.getString("WKD_GREEN_DC_AMT")) );
					result.addString("PAR_3_BOKG_AMT" 			,GolfUtil.comma(rs.getString("PAR_3_BOKG_AMT")) );
					result.addString("DRDS_AMT" 				,GolfUtil.comma(rs.getString("DRDS_AMT")) );
					result.addString("DRVR_AMT" 				,GolfUtil.comma(rs.getString("DRVR_AMT")) );
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

		sql.append("\n");  
		sql.append("\t  SELECT 													\n");  
		sql.append("\t  GEN_WKD_BOKG_AMT, GEN_WKE_BOKG_AMT, WKD_GREEN_DC_AMT	\n");  
		sql.append("\t  , PAR_3_BOKG_AMT, DRDS_AMT, DRVR_AMT					\n");  
		sql.append("\t  FROM BCDBA.TBGCBMOPLCYMGMT								\n");
		
		return sql.toString();
    }
}
