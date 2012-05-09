/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntCouponNoSchProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 이벤트라운지/골프라운지이벤트/진행중인이벤트/그린피할인쿠폰->할인쿠폰 번호 확인 검색 
*   적용범위  : Golf
*   작성일자  : 2011-04-13
************************** 수정이력 ****************************************************************
*    일자   작성자   변경사항
*20110425   이경희   결제취소된 쿠폰 조회 안되도록 수정
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.coupon;

import java.io.Reader;
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
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfEvntCouponNoSchProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntCouponNoSchProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntCouponNoSchProc() {}	

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

		String jumin_no	= data.getString("jumin_no");			
		String hp_ddd_no	= data.getString("hp_ddd_no");
		String hp_tel_hno	= data.getString("hp_tel_hno");
		String hp_tel_sno	= data.getString("hp_tel_sno");

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			
			String sql = this.getCouponNoQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++, jumin_no);

			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);

			rs = pstmt.executeQuery();
			
			if ( rs != null ) {
				
				while(rs.next())  {
					result.addString("CUPN_NO" 	,rs.getString("CUPN_NO") );
					result.addString("RESULT", "00"); //정상결과 
				}
				
			}

			if (result.size() < 1) {
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
    * 구매한 쿠폰내역 확인
    ************************************************************************ */
    private String getCouponNoQuery(){
    	
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT B.CUPN_NO");
		sql.append("\n FROM BCDBA.TBGSTTLMGMT A, BCDBA.TBEVNTLOTPWIN B");
		sql.append("\n WHERE A.ODR_NO = B.EA_INFO");
		sql.append("\n AND B.JUMIN_NO = ? ");
		sql.append("\n AND B.HP_DDD_NO = ? ");
		sql.append("\n AND B.HP_TEL_NO1 = ? ");
		sql.append("\n AND B.HP_TEL_NO2 = ? ");
		sql.append("\n AND A.STTL_STAT_CLSS = 'N' ");
		
		return sql.toString(); 
    }
}
