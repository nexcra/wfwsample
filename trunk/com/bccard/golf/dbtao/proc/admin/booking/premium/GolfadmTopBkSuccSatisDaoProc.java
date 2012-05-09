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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author 미디어포스  
 * @version 1.0
 ******************************************************************************/
public class GolfadmTopBkSuccSatisDaoProc extends AbstractProc {
	
	
	private static GolfadmTopBkSuccSatisDaoProc topGolfSatis =  null;

	public GolfadmTopBkSuccSatisDaoProc(){}

	public synchronized static GolfadmTopBkSuccSatisDaoProc getInstance(){

		if(topGolfSatis == null){
			topGolfSatis = new GolfadmTopBkSuccSatisDaoProc();
		}

		return topGolfSatis;
	
	}
	
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
	  
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult  result =  new DbTaoResult(title);
	  
	  	try {
	  		
	  		conn = context.getDbConnection("default", null);	 
		    
		   	//조회 ----------------------------------------------------------
			String from    = data.getString("from");
			String to      = data.getString("to");
			String repMbNo = data.getString("repMbNo");
			int idx = 0;   
		
			String sql = this.getSelectQuery(repMbNo);
			pstmt = conn.prepareStatement(sql.toString());		   

			pstmt.setString( ++idx, from + "0101" );
			pstmt.setString( ++idx, to   + "1231" );
			
			if (!"00".equals(repMbNo)) { pstmt.setString( ++idx, repMbNo ); }
		
			// 입력값 (INPUT)   
			rs = pstmt.executeQuery();   
		   
			boolean existdata = false;
		   
			if(rs != null) {    
		   
				while ( rs.next() ) {
			 
					existdata = true;  
 
						result.addString   ("Success", rs.getString("success") );
						result.addString   ("Ind1", rs.getString("indiv1") );
						result.addString   ("Ind2", rs.getString("indiv2") );
						result.addString   ("Ind3", rs.getString("indiv3") );				 
						result.addString   ("Corp1", rs.getString("corp1") );
						result.addString   ("Corp2", rs.getString("corp2") );
						result.addString   ("Corp3", rs.getString("corp3") );				 
						result.addString   ("T1", rs.getString("T1") );
						result.addString   ("T2", rs.getString("T2") );
						result.addString   ("T3", rs.getString("T3") );						
						
				}    
		
				if ( existdata ) { result.addString("result","00"); }
				else             { result.addString("result","01"); }	
				
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
	private String getSelectQuery(String repMbNo){
		
	    StringBuffer sql = new StringBuffer();	
	
		sql.append("\n  SELECT V.SUCCESS, 	");
	    sql.append("\n  TO_CHAR(V.INDIV1, '999,999,999') INDIV1,																	");
	    sql.append("\n  TO_CHAR(V.INDIV2, '999,999,999') INDIV2,																	");
	    sql.append("\n  TO_CHAR(V.INDIV3, '999,999,999') INDIV3,																	");
	    sql.append("\n  TO_CHAR(V.CORP1, '999,999,999') CORP1,																		");
	    sql.append("\n  TO_CHAR(V.CORP2, '999,999,999') CORP2,																		");
	    sql.append("\n  TO_CHAR(V.CORP3, '999,999,999') CORP3,																		");
	    sql.append("\n  TO_CHAR(V.INDIV1 + V.CORP1, '999,999,999') T1,																");
	    sql.append("\n  TO_CHAR(V.INDIV2 + V.CORP2, '999,999,999') T2,																");
	    sql.append("\n  TO_CHAR(V.INDIV3 + V.CORP3, '999,999,999') T3																");
		sql.append("\n  FROM( 																										");
	    sql.append("\n  	SELECT																									");
	    sql.append("\n  	A.CDHD_ID,																								");
	    sql.append("\n  	COUNT( DECODE(A.PGRS_YN, 'B', 'A')) SUCCESS, --성공(개인, 법인)								");
	    sql.append("\n  	COUNT( DECODE(B.MEMBER_CLSS, '1', 'A', '4', 'A')) INDIV1 , --신청(회원구분코드  :1,4->개인)				");
	    sql.append("\n  	COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '1F', 'A', '4F', 'A'))  INDIV2 , --실패							");
	    sql.append("\n  	COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '1A', 'A', '4A', 'A', '1C', 'A', '4C', 'A'))  INDIV3 , --취소	");
	    sql.append("\n  	COUNT( DECODE(B.MEMBER_CLSS, '5', 'A'))  CORP1 ,   --신청(회원구분코드  :5->법인)							");
	    sql.append("\n  	COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '5F', 'A'))  CORP2 ,    --실패									");
	    sql.append("\n  	COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '5A', 'A', '5C', 'A')) CORP3	--취소							");
		sql.append("\n  	FROM BCDBA.TBGAPLCMGMT A, BCDBA.TBGGOLFCDHD B, BCDBA.TBGFTEMPPAY C										");
	    sql.append("\n  	WHERE A.CDHD_ID = B.CDHD_ID																				");
	    sql.append("\n  	AND A.APLC_SEQ_NO = C.BKNG_REQ_NO(+)																	");	    
	    sql.append("\n  	AND A.GOLF_SVC_APLC_CLSS='1000'  	-- 1000: 탑골프카드전용부킹											");
	    sql.append("\n  	AND A.PGRS_YN != 'R'																					");
	    sql.append("\n  	AND (A.TEOF_DATE >= ? AND A.TEOF_DATE <= ? )															");
	    if (!"00".equals(repMbNo)) { sql.append("\n	AND SUBSTR(C.CARD_NO, 5, 2) IN (SELECT MB_NO FROM BCDBA.TBGFMB WHERE REP_MB_NO = ? AND USE_YN = 'Y' ) "); }	    
	    sql.append("\n  	GROUP BY A.CDHD_ID																						");
	    sql.append("\n  ) V																											");
	    sql.append("\n  ORDER BY V.SUCCESS DESC																						");	    
		
        return sql.toString();
        
	}	

	
}


