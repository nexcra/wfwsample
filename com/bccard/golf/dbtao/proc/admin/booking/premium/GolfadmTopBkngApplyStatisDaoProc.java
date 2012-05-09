
/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPreTimeListDaoProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 관리자 프리미엄 티타임 리스트 처리
*   적용범위  : golf
*   작성일자  : 2010-12-29
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.common.mail.EmailEntity;
import com.bccard.golf.common.mail.EmailSend;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스  
 * @version	1.0
 ******************************************************************************/
public class GolfadmTopBkngApplyStatisDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmPreTimeListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmTopBkngApplyStatisDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		GolfAdminEtt userEtt = null;
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		
		try {
			conn = context.getDbConnection("default", null);
		
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			String admClss = userEtt.getAdm_clss();
			 
			//조회 ----------------------------------------------------------
			String diff = data.getString("diff");
			String yyyy = data.getString("yyyy");
			String from = data.getString("from");
			String to   = data.getString("to");
			String objClss = data.getString("bkngObjClss");
			String repMbNo = data.getString("repMbNo");
			
			boolean dInq = "0".equals(diff);
			
			String sql = this.getSelectQuery(dInq, repMbNo, objClss);
			
			int idx = 0;			
			pstmt = conn.prepareStatement(sql.toString());
			
			if (dInq) {
				pstmt.setString( ++idx, yyyy + "01010000000000" );
				pstmt.setString( ++idx, yyyy + "12312359599999" );
			} else {
				pstmt.setString( ++idx, from + "01010000000000" );
				pstmt.setString( ++idx, to   + "12312359599999" );
			}
			if (!"00".equals(repMbNo)) { pstmt.setString( ++idx, repMbNo ); }
			if (!"0".equals(objClss))  { pstmt.setString( ++idx, objClss ); }

			// 입력값 (INPUT)   
			rs = pstmt.executeQuery();			
			
			boolean existdata = false;
			
			if(rs != null) {				
			
				while ( rs.next() ) {
	
					existdata = true;		
					
					result.addString   ("Unit", rs.getString("unit") );
					result.addString   ("Ind1", rs.getString("indiv1") );
					result.addString   ("Ind2", rs.getString("indiv2") );
					result.addString   ("Ind3", rs.getString("indiv3") );
					result.addString   ("Ind4", rs.getString("indiv4") );
					result.addString   ("Corp1", rs.getString("corp1") );
					result.addString   ("Corp2", rs.getString("corp2") );
					result.addString   ("Corp3", rs.getString("corp3") );
					result.addString   ("Corp4", rs.getString("corp4") );
					result.addString   ("T1", rs.getString("T1") );
					result.addString   ("T2", rs.getString("T2") );
					result.addString   ("T3", rs.getString("T3") );
					result.addString   ("T4", rs.getString("T4") );		
					
				}    
				
				if ( existdata ) { result.addString("result","00"); }
				else             { result.addString("result","01"); }
				
//				if(result.size() < 1) {
//					result.addString("RESULT", "01");			
//				}
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
	private String getSelectQuery(boolean dInq, String repMbNo, String objClss){
		
	    StringBuffer sql = new StringBuffer();
		
		sql.append("\n").append("  SELECT V.UNIT,                                                                                        ");
        sql.append("\n").append("         TO_CHAR(V.INDIV1, '999,999,999') INDIV1,                                                       ");
        sql.append("\n").append("         TO_CHAR(V.INDIV2, '999,999,999') INDIV2,                                                       ");
        sql.append("\n").append("         TO_CHAR(V.INDIV3, '999,999,999') INDIV3,                                                       ");
        sql.append("\n").append("         TO_CHAR(V.INDIV4, '999,999,999') INDIV4,                                                       ");
        sql.append("\n").append("         TO_CHAR(V.CORP1, '999,999,999') CORP1,                                                         ");
        sql.append("\n").append("         TO_CHAR(V.CORP2, '999,999,999') CORP2,                                                         ");
        sql.append("\n").append("         TO_CHAR(V.CORP3, '999,999,999') CORP3,                                                         ");
        sql.append("\n").append("         TO_CHAR(V.CORP4, '999,999,999') CORP4,                                                         ");
        sql.append("\n").append("         TO_CHAR(V.INDIV1 + V.CORP1, '999,999,999') T1,                                              	 ");
        sql.append("\n").append("         TO_CHAR(V.INDIV2 + V.CORP2, '999,999,999') T2,                                              	 ");
        sql.append("\n").append("         TO_CHAR(V.INDIV3 + V.CORP3, '999,999,999') T3,                                              	 ");
        sql.append("\n").append("         TO_CHAR(V.INDIV4 + V.CORP4, '999,999,999') T4                                            		 ");
        sql.append("\n").append("     FROM(                                                                                              ");
        sql.append("\n").append("          SELECT                                                                                        ");
		if (dInq) { sql.append("\n").append("SUBSTR(ROUND_DATE, 1, 4)||'.'||SUBSTR(ROUND_DATE, 5, 2)  UNIT,                              "); }
	    else      { sql.append("\n").append("SUBSTR(ROUND_DATE, 1, 4) AS UNIT,                                                			 "); }
        sql.append("\n").append("                COUNT( DECODE(MEMBER_CLSS, '1', 'A')) INDIV1 ,                                          ");
        sql.append("\n").append("                COUNT( DECODE(MEMBER_CLSS||BKNG_STAT, '131', 'A'))  INDIV2 ,                            ");
        sql.append("\n").append("                COUNT( DECODE(MEMBER_CLSS||BKNG_STAT, '135', 'A'))  INDIV3 ,                            ");
        sql.append("\n").append("                COUNT( DECODE(MEMBER_CLSS||BKNG_STAT, '119', 'A', '129', 'A', '139', 'A'))  INDIV4 ,    ");
        sql.append("\n").append("                COUNT( DECODE(MEMBER_CLSS, '3', 'A'))  CORP1 ,                                          ");
        sql.append("\n").append("                COUNT( DECODE(MEMBER_CLSS||BKNG_STAT, '331', 'A'))  CORP2 ,                             ");
        sql.append("\n").append("                COUNT( DECODE(MEMBER_CLSS||BKNG_STAT, '335', 'A'))  CORP3 ,                             ");
        sql.append("\n").append("                COUNT( DECODE(MEMBER_CLSS||BKNG_STAT, '319', 'A', '329', 'A', '339', 'A')) CORP4        ");
        sql.append("\n").append("           FROM BCDBA.TBGFBOOKING                                                                       ");
        sql.append("\n").append("         WHERE ROUND_DATE >= ? AND ROUND_DATE <= ?                ");
        if (!"00".equals(repMbNo)) { sql.append("\n").append("AND SUBSTR(CARD_NO, 5, 2) IN (SELECT MB_NO FROM BCDBA.TBGFMB WHERE REP_MB_NO = ? AND USE_YN = 'Y' ) "); }
	    if (!"0".equals(objClss))  { sql.append("\n").append("AND BKNG_OBJ_CLSS = ? "); }
	    if (dInq) { sql.append("\n").append("GROUP BY SUBSTR(ROUND_DATE, 1, 4)||'.'||SUBSTR(ROUND_DATE, 5, 2)"); }
	    else      { sql.append("\n").append("GROUP BY SUBSTR(ROUND_DATE, 1, 4)"); }
        sql.append("\n").append("         ) V                                                                                            ");
		
        return sql.toString();
        
	}	
	
}