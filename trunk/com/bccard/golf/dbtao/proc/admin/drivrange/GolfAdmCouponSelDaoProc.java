/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCouponSelDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 쿠폰 셀렉트 박스 생성
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
public class GolfAdmCouponSelDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 쿠폰 셀렉트 박스 생성";
	
	/** *****************************************************************
	 * GolfAdmCouponSelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmCouponSelDaoProc() {}	

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
			
			long cupn_seq_no		= data.getLong("cupn_seq_no");
			
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	");
			sql.append("\n 	CUPN_SEQ_NO, CUPN_NM	");
			sql.append("\n FROM 	");
			sql.append("\n BCDBA.TBGCUPNMGMT	");
			sql.append("\n WHERE GOLF_RNG_CUPN_CLSS =?	");
			if (cupn_seq_no != 0L) sql.append("\n 	AND CUPN_SEQ_NO = "+cupn_seq_no+"	"); 
			sql.append("\n ORDER BY CUPN_SEQ_NO	");			
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("EXEC_TYPE_CD"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("CUPN_SEQ_NO" ,rs.getLong("CUPN_SEQ_NO") );
					result.addString("CUPN_NM" ,rs.getString("CUPN_NM") );
					
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
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}
}
