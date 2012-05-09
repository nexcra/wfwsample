/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMngSmtpDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 동호회 시샵 메일보내기
*   적용범위  : golf
*   작성일자  : 2009-07-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.club;

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
public class GolfAdmMngSmtpDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMngSmtpDaoProc() {}	

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
			 
			// 회원통합관련 수정사항 진행
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					//result.addInt("RECV_NO" 		,rs.getInt("CLUB_SEQ_NO") );
					result.addString("OPN_PE_ID" 	,rs.getString("OPN_PE_ID") ); 			// 개설자 아이디
					result.addString("OPN_PE_NM" 	,rs.getString("OPN_PE_NM") ); 			// 개설자명
					result.addString("EMAIL" 		,rs.getString("EMAIL") ); 				// 이메일

					result.addString("RESULT", "00"); 										// 정상결과
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
                
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT OPN_PE_ID, OPN_PE_NM, EMAIL");
		sql.append("\n	FROM (SELECT 	");
		sql.append("\n OPN_PE_ID, OPN_PE_NM, (SELECT EMAIL FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = 'TGL.OPN_PE_ID') EMAIL ");
		sql.append("\n 	FROM  	");
		sql.append("\n	BCDBA.TBGCLUBMGMT TGL ");
		sql.append("\n	WHERE TGL.CLUB_SEQ_NO = CLUB_SEQ_NO	");
		sql.append("\n 				ORDER BY CLUB_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	)	");

		return sql.toString();
    }
} 
