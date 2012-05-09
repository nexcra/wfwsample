/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntKvpDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 월례회 > 등록처리
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.kvp;

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
public class GolfEvntKvpPopDaoProc extends AbstractProc {
	
	public GolfEvntKvpPopDaoProc() {}	

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

			String payAmount = "0";
			String strPayAmount = "0";
			
			String idx = data.getString("idx");
			String sql = this.getMaxIdxQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, GolfUtil.lpad(idx, 4, "0"));
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()){
				
				payAmount = rs.getString("GOLF_CMMN_CODE_NM");
				
				if(!GolfUtil.empty(payAmount)){
					strPayAmount = GolfUtil.comma(payAmount);
				}
				
				result.addString("payAmount", payAmount);
				result.addString("grdName", rs.getString("EXPL"));
				result.addString("strPayAmount", strPayAmount);
				result.addString("RESULT", "00");
			}else{

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
    * 신청테이블의 max_idx 가져오기
    ************************************************************************ */
    private String getMaxIdxQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT GOLF_CMMN_CODE_NM, EXPL FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0065' AND GOLF_CMMN_CODE=?	");		
		return sql.toString();
    }

}
