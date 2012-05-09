/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBenefitInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 회원 혜택정보 가져오기
*   적용범위  : golf
*   작성일자  : 2009-07-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.drivrange;

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
public class GolfBenefitInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBenefitInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBenefitInqDaoProc() {}	

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
		GolfUtil cstr = new GolfUtil();

		try {
			
			conn = context.getDbConnection("default", null);
			 
			//조회 ---------------------------------------------------------- 			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("USERID"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addString("CDHD_SQ2_CTGO" 		,rs.getString("CDHD_SQ2_CTGO") ); // 회원2차분류코드
					result.addString("DRGF_LIMT_YN" 		,rs.getString("DRGF_LIMT_YN") ); // 드림골프제한여부(Y:접근가능 N:접근불가)
					result.addString("DRGF_APO_YN"			,rs.getString("DRGF_APO_YN") ); // 드림골프 접근여부(Y:접근가능 N:접근불가)
					result.addString("CUPN_PRN_NUM"			,rs.getString("CUPN_PRN_NUM") ); // 쿠폰인쇄횟수
					result.addString("DRVR_APO_YN"			,rs.getString("DRVR_APO_YN") ); // 드라이빙레인지접근여부(Y:접근가능 N:접근불가)
					result.addString("DRGF_YR_ABLE_NUM"		,rs.getString("DRGF_YR_ABLE_NUM") ); // 드림골프년가능횟수
					result.addString("DRGF_MO_ABLE_NUM"		,rs.getString("DRGF_MO_ABLE_NUM") ); // 드림골프월가능횟수
					result.addString("ETHS_APO_YN"			,rs.getString("ETHS_APO_YN") ); // 골프장주변맛집접근여부(Y:접근가능 N:접근불가)
					
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

        sql.append("\n SELECT");
		sql.append("\n 	TGB.CDHD_SQ2_CTGO, TGB.DRGF_LIMT_YN, TGB.DRGF_APO_YN, TGB.CUPN_PRN_NUM, TGB.DRVR_APO_YN, TGB.DRGF_YR_ABLE_NUM, TGB.DRGF_MO_ABLE_NUM, ETHS_APO_YN	 ");
		sql.append("\n FROM BCDBA.TBGGOLFCDHDBNFTMGMT TGB, BCDBA.TBGGOLFCDHDGRDMGMT TGUC, BCDBA.TBGGOLFCDHDCTGOMGMT TGUD 	");
		sql.append("\n WHERE TGUC.CDHD_CTGO_SEQ_NO = TGUD.CDHD_CTGO_SEQ_NO(+)	");
		sql.append("\n AND TGB.CDHD_SQ2_CTGO = TGUD.CDHD_SQ2_CTGO	");	
		sql.append("\n AND TGUC.CDHD_ID = ?	");	
		
		return sql.toString();
    }
}
