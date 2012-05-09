/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemberShipAllUpdFormDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 회원권 시세 전체수정
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
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
public class GolfAdmMemberShipAllUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemberShipAllUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemberShipAllUpdFormDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("FEE_DATE"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addLong("GF_SEQ_NO" 			,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("FEE_YEAR" 		,rs.getString("QUT_YEAR") );
					result.addString("FEE_MONTH" 		,rs.getString("QUT_MONTH") );
					result.addString("FEE_DAY" 		,rs.getString("QUT_DAY") );
					result.addString("GF_NM"			,rs.getString("GREEN_NM") );
					result.addLong("TODAY_FEE" 			,rs.getLong("QUT") );
					
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
		sql.append("\n 	TGFE.AFFI_GREEN_SEQ_NO,   ");
		sql.append("\n 	TO_CHAR (TO_DATE (TGFE.QUT_DATE, 'YYYYMMDDHH'), 'YYYY') QUT_YEAR,  ");
		sql.append("\n 	TO_CHAR (TO_DATE (TGFE.QUT_DATE, 'YYYYMMDDHH'), 'MM') QUT_MONTH,  ");
		sql.append("\n 	TO_CHAR (TO_DATE (TGFE.QUT_DATE, 'YYYYMMDDHH'), 'DD') QUT_DAY,  ");
		sql.append("\n 	TGF.GREEN_NM, TGFE.QUT  ");
		sql.append("\n FROM BCDBA.TBGGREENQUTMGMT TGFE, BCDBA.TBGAFFIGREEN TGF 	");
		sql.append("\n WHERE TGFE.AFFI_GREEN_SEQ_NO = TGF.AFFI_GREEN_SEQ_NO		");	
		sql.append("\n AND TGFE.QUT_DATE = ?	");
		sql.append("\n AND TGF.AFFI_FIRM_CLSS = '0004'	");
		return sql.toString();
    }
}
