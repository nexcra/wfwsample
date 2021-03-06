/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCouponUpdFormDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 연습장 수정
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.drivrange;

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
public class GolfAdmCouponUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmCouponUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmCouponUpdFormDaoProc() {}	

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
			pstmt.setLong(++idx, data.getLong("CUPN_SEQ_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addLong("CUPN_SEQ_NO" 		,rs.getLong("CUPN_SEQ_NO") );
					result.addString("CUPN_CLSS" 		,rs.getString("GOLF_RNG_CUPN_CLSS") );
					result.addString("CUPN_NM" 			,rs.getString("CUPN_NM") );
					result.addLong("CUPN_DC_RT" 		,rs.getLong("DC_RT") );
					result.addString("IMG_NM"			,rs.getString("CUPN_IMG") );
					result.addString("REG_PE_ID"			,rs.getString("REG_MGR_ID") );
					result.addString("CORR_PE_ID"			,rs.getString("CHNG_MGR_ID") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON"));
					result.addString("CORR_ATON"		,rs.getString("CHNG_ATON"));

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
		sql.append("\n 	CUPN_SEQ_NO, GOLF_RNG_CUPN_CLSS, CUPN_NM, DC_RT, CUPN_IMG, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON  ");
		sql.append("\n FROM BCDBA.TBGCUPNMGMT 	");
		sql.append("\n WHERE CUPN_SEQ_NO = ?	");		

		return sql.toString();
    }
}
