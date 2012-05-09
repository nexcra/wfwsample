/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopBkDateSatisDaoProc
*   작성자    : (주)미디어포스 이경희
*   내용      : 관리자 > 부킹 > TOP골프카드전용부킹 > TOP년월별부킹신청현황
*   적용범위  : golf
*   작성일자  : 2011-01-04
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
public class GolfadmTopBkDateSatisDaoProc extends AbstractProc {
	
	
	private static GolfadmTopBkDateSatisDaoProc topGolfSatis =  null;

	public GolfadmTopBkDateSatisDaoProc(){}

	public synchronized static GolfadmTopBkDateSatisDaoProc getInstance(){
	
		if(topGolfSatis == null){
			topGolfSatis = new GolfadmTopBkDateSatisDaoProc();
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
			String diff = data.getString("diff");
			String yyyy = data.getString("yyyy");
			String from = data.getString("from");
			String to   = data.getString("to");
			String repMbNo = data.getString("repMbNo");
			   
			boolean dInq = "0".equals(diff);
			int idx = 0;   
			
			String sql = this.getSelectQuery(dInq, repMbNo);
			pstmt = conn.prepareStatement(sql.toString());
			   
			if (dInq) {
				pstmt.setString( ++idx, yyyy + "0101" );
				pstmt.setString( ++idx, yyyy + "1231" );
			} else {
			   pstmt.setString( ++idx, from + "0101" );
			   pstmt.setString( ++idx, to   + "1231" );
			}
			   
			if (!"00".equals(repMbNo)) { pstmt.setString( ++idx, repMbNo ); }
			
			// 입력값 (INPUT)   
			rs = pstmt.executeQuery();   
		   
			boolean existdata = false;
		   
			if(rs != null) {    
			   
				while ( rs.next() ) {
			 
					existdata = true;
					 
					result.addString("Unit", rs.getString("unit") );
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



	 /**
	  * Proc 실행.
	  * @param Connection con
	  * @param TaoDataSet dataSet
	  * @return TaoResult
	  */
	public DbTaoResult excelDetail(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {  
	
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		
		try {
			
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String diff = data.getString("diff");
			String repMbNo = data.getString("repMbNo");
			String yyyy = data.getString("yyyy");
			String from = data.getString("from");
			String to   = data.getString("to");	
			String schDate = data.getString("schDate");
			String memberClss = data.getString("memberClss");
			String gubun = data.getString("gubun");
			
			boolean dInq = "0".equals(diff);
			int idx = 0;
			
			String sql = this.getExcDetail(memberClss, gubun, repMbNo);
			pstmt = conn.prepareStatement(sql.toString()); 
			
			idx = 0;
			if (diff.equals("0")) {
				schDate = schDate.substring(0,4)+ schDate.substring(5,7);				
				pstmt.setString( ++idx, schDate + "01" );
				pstmt.setString( ++idx, schDate + "31" );
			} else {
				pstmt.setString( ++idx, schDate + "0101" );
				pstmt.setString( ++idx, schDate + "1231" );
			}	   
			
			if (!"00".equals(repMbNo)) { pstmt.setString( ++idx, repMbNo ); }
			
			rs = pstmt.executeQuery();
			
			boolean existdata = false;
			
			if(rs != null) {   
				while ( rs.next() ) {
					existdata = true;
					
					result.addString("RoundDate", rs.getString("ROUND_DATE") );
					result.addString("GreenNm", rs.getString("GREEN_NM") );
					result.addString("BkngReqTime", "99:99".equals(rs.getString("bkng_req_time"))?"상관없음":rs.getString("BKNG_REQ_TIME") );
					result.addString("BkngOkTime", rs.getString("BKNG_OK_TIME") );
					result.addString("CdhdId", rs.getString("CDHD_ID") );
					result.addString("Name", rs.getString("HG_NM") );
					result.addString("SocId", rs.getString("SOCID") );
					result.addString("CardNo", rs.getString("CARD_NO") );
					result.addString("MbNm", rs.getString("MB_NM") );
					result.addString("MbClssNm", rs.getString("MB_CLSS_NM") );
					result.addString("BkngStatNm", rs.getString("BKNG_STAT_NM") );
					result.addString("GUBUN", rs.getString("GUBUN") );				
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
	private String getSelectQuery(boolean dInq, String repMbNo){
		
	    StringBuffer sql = new StringBuffer();
        
	    sql.append("\n  SELECT V.UNIT,                                                                                        ");
        sql.append("\n         TO_CHAR(V.INDIV1, '999,999,999') INDIV1,                                                       ");
        sql.append("\n         TO_CHAR(V.INDIV2, '999,999,999') INDIV2,                                                       ");
        sql.append("\n         TO_CHAR(V.INDIV3, '999,999,999') INDIV3,                                                       ");
        sql.append("\n         TO_CHAR(V.INDIV1 + V.INDIV2 + V.INDIV3, '999,999,999') INDIV4,                                 ");
        sql.append("\n         TO_CHAR(V.CORP1, '999,999,999') CORP1,                                                         ");
        sql.append("\n         TO_CHAR(V.CORP2, '999,999,999') CORP2,                                                         ");
        sql.append("\n         TO_CHAR(V.CORP3, '999,999,999') CORP3,                                                         ");
        sql.append("\n         TO_CHAR(V.CORP1 + V.CORP2 + V.CORP3, '999,999,999') CORP4,                                     ");
        sql.append("\n         TO_CHAR(V.INDIV1 + V.CORP1, '999,999,999') T1,                                              	  ");
        sql.append("\n         TO_CHAR(V.INDIV2 + V.CORP2, '999,999,999') T2,                                              	  ");
        sql.append("\n         TO_CHAR(V.INDIV3 + V.CORP3, '999,999,999') T3,                                              	  ");
        sql.append("\n         TO_CHAR(V.INDIV1 + V.INDIV2 + V.INDIV3 + V.CORP1 + V.CORP2 + V.CORP3, '999,999,999') T4        ");        
        sql.append("\n  FROM(                                                                                                 ");
        sql.append("\n		SELECT                                                                                            ");
        
		if (dInq) { sql.append("\n	SUBSTR(A.TEOF_DATE, 1, 4)||'.'||SUBSTR(A.TEOF_DATE, 5, 2)  UNIT,                   		  "); }
	    else      { sql.append("\n	SUBSTR(A.TEOF_DATE, 1, 4) UNIT,                                                			  "); }
        
		sql.append("\n		COUNT(DECODE(B.MEMBER_CLSS, '1', 'A', '4', 'A'))INDIV1,											  ");
		sql.append("\n		COUNT(DECODE (B.MEMBER_CLSS||TO_CHAR(TO_DATE(A.TEOF_DATE, 'yyyymmdd'),'D'), 					  ");
		sql.append("\n				'12', 'X', '13', 'X', '14', 'X', '15', 'X', '16', 'X', '42', 'X', '43', 'X', '44', 'X', '45', 'X', '46', 'X'))INDIV2,	");
		sql.append("\n		COUNT(DECODE (B.MEMBER_CLSS||TO_CHAR(TO_DATE(A.TEOF_DATE, 'yyyymmdd'),'D'), 					  ");
		sql.append("\n				'11', 'W', '41', 'W',  '17', 'W', '47', 'W'))INDIV3,									  ");		
		sql.append("\n		COUNT( DECODE(B.MEMBER_CLSS, '5', 'A'))  CORP1,													  ");		
		sql.append("\n		COUNT(DECODE (B.MEMBER_CLSS||TO_CHAR(TO_DATE(A.TEOF_DATE, 'yyyymmdd'),'D'), '52', 'X', '53', 'X', '54', 'X', '55', 'X', '56', 'X'))CORP2,	");
		sql.append("\n		COUNT(DECODE (B.MEMBER_CLSS||TO_CHAR(TO_DATE(A.TEOF_DATE, 'yyyymmdd'),'D'), '51', 'W', '57', 'W'))CORP3		");
		sql.append("\n		FROM BCDBA.TBGAPLCMGMT A, BCDBA.TBGGOLFCDHD B, BCDBA.TBGFTEMPPAY C								  ");
		sql.append("\n		WHERE A.CDHD_ID = B.CDHD_ID																		  ");
		sql.append("\n		AND A.APLC_SEQ_NO = C.BKNG_REQ_NO(+)															  ");
		sql.append("\n		AND A.GOLF_SVC_APLC_CLSS='1000'  	-- 1000: 탑골프카드전용부킹									  ");
		sql.append("\n		AND A.PGRS_YN != 'R'																			  ");
		sql.append("\n		AND (A.TEOF_DATE >= ? AND A.TEOF_DATE <= ?)														  ");
		
        if (!"00".equals(repMbNo)) { sql.append("\n	AND SUBSTR(C.CARD_NO, 5, 2) IN (SELECT MB_NO FROM BCDBA.TBGFMB WHERE REP_MB_NO = ? AND USE_YN = 'Y' ) "); }
        
	    if (dInq) { sql.append("\n	GROUP BY  SUBSTR(A.TEOF_DATE, 1, 4)||'.'||SUBSTR(A.TEOF_DATE, 5, 2)						  "); }
	    else      { sql.append("\n	GROUP BY  SUBSTR(A.TEOF_DATE, 1, 4)														  "); }	
	    
	    sql.append("\n         ) V                                                                                            ");
		
        return sql.toString();
        
	}		


	/** ***********************************************************************
	 * 자세한 엑셀 자료 생성하여 리턴한다.    
	 ************************************************************************ */
	 private String getExcDetail(String memberClss, String gubun, String repMbNo){
	
	   StringBuffer sql = new StringBuffer();
	
	   	sql.append("\n	 SELECT																														");
	    sql.append("\n	 SUBSTR(A.TEOF_DATE, 1, 4)||'.'||SUBSTR(A.TEOF_DATE, 5, 2)  ROUND_DATE,   									-- 라운딩일자	");
	    sql.append("\n	 A.GREEN_NM, 																								-- 골프장명		");
	   	sql.append("\n	 SUBSTRB(A.TEOF_TIME, 1, 2)||':'||SUBSTRB(A.TEOF_TIME, 3, 2) BKNG_REQ_TIME,   								-- 부킹희망시분	");
	    sql.append("\n	 DECODE(A.CHNG_ATON, NULL, NULL, SUBSTRB(A.CHNG_ATON,1,2)||':'||SUBSTRB(A.CHNG_ATON,3,2)) BKNG_OK_TIME, 	-- 부킹확정시분	");
	    sql.append("\n	 A.CDHD_ID,  																								-- 회원번호		");
	   	sql.append("\n	 B.HG_NM,																									-- 회원명		");
	    sql.append("\n	 SUBSTRB(B.JUMIN_NO,1, 7)||'-*******' SOCID, 																-- 주민번호		");
	    sql.append("\n	 SUBSTRB(C.CARD_NO, 1, 4)||'-'||SUBSTRB(C.CARD_NO, 5, 4)||'-****-*****' CARD_NO,							-- 카드번호		");
	   	sql.append("\n	 D.MB_NM,  																									-- 회원사		");
	    sql.append("\n	 DECODE(B.MEMBER_CLSS, '1', '개인', '4', '개인', '5', '법인') MB_CLSS_NM, 									-- 회원구분		");
	    sql.append("\n	 DECODE(A.PGRS_YN, 'A', '예약취소', 'B', '부킹확정', 'F', '실패', 'C', '부킹취소') BKNG_STAT_NM,  			-- 처리결과		");
	    
	    if (memberClss.equals("1")) {//개인 B.MEMBER_CLSS 가 '1','4'인것 -> 파라메터로 받는 memberClss는 '1'로 통일
	    	sql.append("\n	 DECODE (B.MEMBER_CLSS||TO_CHAR(TO_DATE(A.TEOF_DATE, 'yyyymmdd'),'D'),	");
	    	sql.append("\n	 		'11', '주말', '41', '주말',  '17', '주말', '47', '주말',	");
	    	sql.append("\n	 		'12', '주중', '13', '주중', '14', '주중', '15', '주중', '16', '주중',	");
	    	sql.append("\n	 		'42', '주중', '43', '주중', '44', '주중', '45', '주중', '46', '주중')GUBUN  	--구분	");
	    }else if (memberClss.equals("5")) { //법인
	    	sql.append("\n	 DECODE (B.MEMBER_CLSS||TO_CHAR(TO_DATE(A.TEOF_DATE, 'yyyymmdd'),'D'), '51', '주말', '57', '주말',	");
	    	sql.append("\n	  		'52', '주중', '53', '주중', '54', '주중', '55', '주중', '56', '주중')GUBUN  	--구분	");    	
	    }else {
	    	sql.append("\n	 DECODE (B.MEMBER_CLSS||TO_CHAR(TO_DATE(A.TEOF_DATE, 'yyyymmdd'),'D'),	");
	    	sql.append("\n	 		'11', '주말', '41', '주말',  '17', '주말', '47', '주말', '51', '주말', '57', '주말',	");
	    	sql.append("\n	  		'12', '주중', '13', '주중', '14', '주중', '15', '주중', '16', '주중',		");
	    	sql.append("\n	  		'42', '주중', '43', '주중', '44', '주중', '45', '주중', '46', '주중',		");
	    	sql.append("\n	  		'52', '주중', '53', '주중', '54', '주중', '55', '주중', '56', '주중')GUBUN  	--구분	");	    	
	    }
	    
	   	sql.append("\n	 FROM BCDBA.TBGAPLCMGMT A, BCDBA.TBGGOLFCDHD B, BCDBA.TBGFTEMPPAY C,  BCDBA.TBGFMB D 										");
	    sql.append("\n	 WHERE A.CDHD_ID = B.CDHD_ID																								");
	    sql.append("\n	 AND A.APLC_SEQ_NO = C.BKNG_REQ_NO(+)																						");
	   	sql.append("\n	 AND A.GOLF_SVC_APLC_CLSS='1000'   -- 1000: 탑골프카드전용부킹																");
	    sql.append("\n	 AND A.PGRS_YN != 'R'																										");
	    sql.append("\n	 AND (A.TEOF_DATE >= ? AND A.TEOF_DATE <= ?)																				");
	    		
		if (!memberClss.equals("0"))  {
			if (memberClss.equals("1")) {
				sql.append("\n	AND B.MEMBER_CLSS IN ('1','4')                       																");
			}else if (memberClss.equals("5")) {				
					sql.append("\n	AND B.MEMBER_CLSS = '5'                          																");				
			}
		}		
			
		if (gubun.equals("3")){//주말
			sql.append("\n	AND TO_CHAR(TO_DATE(A.TEOF_DATE, 'yyyymmdd'),'D') IN ('1','7')															");
		}else if (gubun.equals("2")){//주중
			sql.append("\n	AND TO_CHAR(TO_DATE(A.TEOF_DATE, 'yyyymmdd'),'D') IN ('2','3','4','5','6')												");
		}
		
		if (!repMbNo.equals("00")) { 
			sql.append("\n		AND SUBSTR(C.CARD_NO, 5, 2) IN (SELECT MB_NO FROM BCDBA.TBGFMB WHERE REP_MB_NO = ? AND USE_YN = 'Y' ) "); 
		}
	    
	   	sql.append("\n	 AND D.MB_NO(+) = SUBSTRB(C.CARD_NO, 5, 2)																						");
	    sql.append("\n	 ORDER BY A.TEOF_DATE, B.HG_NM    																							");

	    return sql.toString();
	
	 }
 

}	