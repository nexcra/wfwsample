/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreGrListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 골프장 리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking;

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
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스  
 * @version	1.0 
 ******************************************************************************/
public class GolfWidgetBkPreXmlDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLessonListDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfWidgetBkPreXmlDaoProc() {}	

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
			pstmt = conn.prepareStatement(sql.toString());
			
			rs = pstmt.executeQuery();

			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("AFFI_GREEN_SEQ_NO" 	,rs.getString("AFFI_GREEN_SEQ_NO") );
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

		sql.append("\n");
		sql.append("\t     SELECT AFFI_GREEN_SEQ_NO, GREEN_NM FROM (\n");
		sql.append("\t     	     SELECT T4.AFFI_GREEN_SEQ_NO, T5.GREEN_NM FROM (\n");
		sql.append("\t     	         SELECT T1.AFFI_GREEN_SEQ_NO\n");
		sql.append("\t     	         FROM BCDBA.TBGAFFIGREEN T1\n");
		sql.append("\t     	         JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.AFFI_GREEN_SEQ_NO=T2.AFFI_GREEN_SEQ_NO\n");
		sql.append("\t     	         JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO\n");
		sql.append("\t     	         WHERE T1.AFFI_FIRM_CLSS='0001'\n");
		sql.append("\t     	         AND T2.BOKG_ABLE_DATE IS NOT NULL\n");
		sql.append("\t     	         AND T3.BOKG_RSVT_STAT_CLSS='0001'\n");
		sql.append("\t     	         AND T2.BOKG_ABLE_DATE BETWEEN TO_CHAR(SYSDATE+7,'YYYYMMDD') AND TO_CHAR(SYSDATE+14,'YYYYMMDD')\n");
		sql.append("\t     	         GROUP BY T1.AFFI_GREEN_SEQ_NO\n");
		sql.append("\t     	         ) T4\n");
		sql.append("\t     	         JOIN BCDBA.TBGAFFIGREEN T5 ON T4.AFFI_GREEN_SEQ_NO=T5.AFFI_GREEN_SEQ_NO\n");
		sql.append("\t     	         ORDER BY AFFI_GREEN_SEQ_NO)\n");

		return sql.toString();
    }
}
