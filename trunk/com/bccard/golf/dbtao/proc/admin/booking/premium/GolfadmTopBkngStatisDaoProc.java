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
public class GolfadmTopBkngStatisDaoProc extends AbstractProc {
	
	
	private static GolfadmTopBkngStatisDaoProc topGolfSatis =  null;

	public GolfadmTopBkngStatisDaoProc(){}

	public synchronized static GolfadmTopBkngStatisDaoProc getInstance(){

		if(topGolfSatis == null){
			topGolfSatis = new GolfadmTopBkngStatisDaoProc();
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
	  
	  GolfAdminEtt userEtt = null;
	  String title = data.getString("TITLE");
	  //public static final String title = "부킹신청대비 결과 비교 신청인원";  
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
		   int idx = 0;   
		
		   String sql = this.getSelectQuery(dInq, repMbNo, objClss);
		   pstmt = conn.prepareStatement(sql.toString());
		   
		   if (dInq) {
			   pstmt.setString( ++idx, yyyy + "0101" );
			   pstmt.setString( ++idx, yyyy + "1231" );
		   } else {
			   pstmt.setString( ++idx, from + "0101" );
			   pstmt.setString( ++idx, to   + "1231" );
		   }
		
		   //pstmt.setString( ++idx, "20090101" );
		   //pstmt.setString( ++idx, "20101231" );   
		   
		   if (!"00".equals(repMbNo)) { pstmt.setString( ++idx, repMbNo ); }
		   //if (!"0".equals(objClss))  { pstmt.setString( ++idx, objClss ); }
		
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
				
				//    if(result.size() < 1) {
				//     result.addString("RESULT", "01");   
				//    }
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
	  //public static final String title = "부킹신청대비 결과 비교 신청인원";
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
	   
	   String memberClss = data.getString("memberClss");
	   String bkngStat   = data.getString("bkngStat");
	   
	   boolean dInq = "0".equals(diff);
	   int idx = 0;

	   String sql = this.getExcDetail(bkngStat, memberClss, repMbNo);
	   pstmt = conn.prepareStatement(sql.toString()); 
	   
	   debug (" @@@@@@@@@@====2 [" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"]");
	   
	   idx = 0;
	   if (dInq) {
		   pstmt.setString( ++idx, yyyy + "0101" );
		   pstmt.setString( ++idx, yyyy + "1231" );
	   } else {
		   pstmt.setString( ++idx, from + "0101" );
		   pstmt.setString( ++idx, to   + "1231" );
	   }		   
	   
	   debug (" test----------------diff1212[" + diff +"], yyyy[" + yyyy +"],from[" + from +"],to[" + to +"],bkngStat[" + bkngStat +"],memberClss[" + memberClss +"],repMbNo[" + repMbNo +"]");	   

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
	 * 회원사 조회
	 * @param Connection conn
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */ 
	public DbTaoResult getGreenList(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		
		try {
			
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getSelectQuery());
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
			
				while(rs.next())  {
					result.addString("MB_NO" 			,rs.getString("MB_NO") );
					result.addString("MB_NM"				,rs.getString("MB_NM") );
				}
			}
			
			if(result.size() < 1) result.addString("RESULT", "01");			
			else result.addString("RESULT", "00"); //정상결과
			 
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
        
		if (dInq) { sql.append("\n").append("SUBSTR(A.TEOF_DATE, 1, 4)||'.'||SUBSTR(A.TEOF_DATE, 5, 2)  UNIT, 	   -- 라운딩일자                         "); }
	    else      { sql.append("\n").append("SUBSTR(A.TEOF_DATE, 1, 4) AS UNIT,                                                			 "); }
		
        sql.append("\n").append("            COUNT( DECODE(B.MEMBER_CLSS, '1', 'A', '4', 'A')) INDIV1 , --신청(회원구분코드  :1,4->개인)   ");        
        sql.append("\n").append("       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '1B', 'A', '4B', 'A'))  INDIV2 , --성공 (회원구분코드, 부킹진행상태코드) ");
        sql.append("\n").append("       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '1F', 'A', '4F', 'A'))  INDIV3 , --실패             	     ");
        sql.append("\n").append("       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '1A', 'A', '4A', 'A', '1C', 'A', '4C', 'A'))  INDIV4 , --취소    ");
        sql.append("\n").append("       	 COUNT( DECODE(B.MEMBER_CLSS, '5', 'A'))  CORP1 ,   --신청(회원구분코드  :5->법인)          	 ");
        sql.append("\n").append("       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '5B', 'A'))  CORP2 ,    --성공                            			 ");
        sql.append("\n").append("       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '5F', 'A'))  CORP3 ,    --실패                        			     ");
        sql.append("\n").append("       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '5A', 'A', '5C', 'A')) CORP4	--취소				 ");
        sql.append("\n").append("       	 FROM BCDBA.TBGAPLCMGMT A, BCDBA.TBGGOLFCDHD B, BCDBA.TBGFTEMPPAY C                            ");
        sql.append("\n").append("       	 WHERE A.CDHD_ID = B.CDHD_ID                                 								 ");
        sql.append("\n").append("       	 AND A.APLC_SEQ_NO = C.BKNG_REQ_NO(+)                               						 ");
        sql.append("\n").append("       	 AND A.GOLF_SVC_APLC_CLSS='1000'  	-- 1000: 탑골프카드전용부킹     								 ");
        sql.append("\n").append("       	 AND A.PGRS_YN not in ('R','S')                               								 ");
        sql.append("\n").append("       	 AND (A.TEOF_DATE >= ? AND A.TEOF_DATE <= ?)      										     ");
        if (!"00".equals(repMbNo)) { sql.append("\n").append("AND SUBSTR(C.CARD_NO, 5, 2) IN (SELECT MB_NO FROM BCDBA.TBGFMB WHERE REP_MB_NO = ? AND USE_YN = 'Y' ) "); }
        //if (!"00".equals(repMbNo)) { sql.append("\n").append("AND SUBSTR(CARD_NO, 5, 2) IN (SELECT MB_NO FROM BCDBA.TBGFMB WHERE REP_MB_NO = ? AND USE_YN = 'Y' ) "); }
	    //if (!"0".equals(objClss))  { sql.append("\n").append("AND BKNG_OBJ_CLSS = ? "); } //부킹대상구분코드 (1:회원제부킹 2:일반부킹 3:주말부킹) ---> 이건 강과장님한테 물어보자
	    if (dInq) { sql.append("\n").append("GROUP BY  SUBSTR(A.TEOF_DATE, 1, 4)||'.'||SUBSTR(A.TEOF_DATE, 5, 2)   "); }
	    else      { sql.append("\n").append("GROUP BY SUBSTR(A.TEOF_DATE, 1, 4)		"); }
        sql.append("\n").append("         ) V                                                                                            ");
		
        return sql.toString();
        
	}	
	


	/** ***********************************************************************
	 * 자세한 엑셀 자료 생성하여 리턴한다.    
	 ************************************************************************ */
	 //private String getExcDetail(String bkngStat, String memberClss, String repMbNo, String objClss){
	 private String getExcDetail(String bkngStat, String memberClss, String repMbNo){
	
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
	    sql.append("\n	 DECODE(A.PGRS_YN, 'A', '예약취소', 'B', '부킹확정', 'F', '실패', 'C', '부킹취소') BKNG_STAT_NM  				-- 처리결과		");
	   	sql.append("\n	 FROM BCDBA.TBGAPLCMGMT A, BCDBA.TBGGOLFCDHD B, BCDBA.TBGFTEMPPAY C,  BCDBA.TBGFMB D 										");
	    sql.append("\n	 WHERE A.CDHD_ID = B.CDHD_ID																								");
	    sql.append("\n	 AND A.APLC_SEQ_NO = C.BKNG_REQ_NO(+)																						");
	   	sql.append("\n	 AND A.GOLF_SVC_APLC_CLSS='1000'   -- 1000: 탑골프카드전용부킹																");
	    sql.append("\n	 AND A.PGRS_YN not in ('R','S')																								");
	    sql.append("\n	 AND (A.TEOF_DATE >= ? AND A.TEOF_DATE <= ?)																				");

		if("2".equals(bkngStat)){//성공
			sql.append("\n                 AND A.PGRS_YN = 'B'                                                    									");
	    }else if("3".equals(bkngStat)){//실패
		    sql.append("\n                 AND A.PGRS_YN = 'F'                                                     									");
	    }else if("4".equals(bkngStat)){//취소
			sql.append("\n                 AND A.PGRS_YN in ('A', 'C')																				");
	    }
	
		if (!"0".equals(memberClss))  {
			if (memberClss.equals("1")) {
				sql.append("\n	AND B.MEMBER_CLSS IN ('1','4')                       																");
			}else {
				sql.append("\n	AND B.MEMBER_CLSS = '5'                          																	");
			}
		}		
			
		if (!"00".equals(repMbNo)) { sql.append("\n		AND SUBSTR(C.CARD_NO, 5, 2) IN (SELECT MB_NO FROM BCDBA.TBGFMB WHERE REP_MB_NO = ? AND USE_YN = 'Y' ) "); }
//	    if (!"0".equals(objClss))  { sql.append("\n		AND A.BKNG_OBJ_CLSS = ? "); }
	    
	   	sql.append("\n	 AND D.MB_NO = SUBSTRB(C.CARD_NO, 5, 2)																						");
	    sql.append("\n	 ORDER BY A.TEOF_DATE, B.HG_NM    																							");

	    return sql.toString();
	
	 }
	 
	 
	/*************************************************************************
    * 회원사 정보를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
    	
        StringBuffer sql = new StringBuffer();        
		
		sql.append("\n SELECT MB_NO, MB_NM	");
		sql.append("\n FROM BCDBA.TBGFMB	");
		sql.append("\n WHERE USE_YN = 'Y'	");
		return sql.toString();
		
    }	 
	 

}