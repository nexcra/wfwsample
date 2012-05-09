/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopBkStatisDaoProc
*   �ۼ���    : (��)�̵������ �̰���
*   ����      : ������ > ��ŷ > TOP����ī�������ŷ > TOP��ŷ��û�����
*   �������  : golf
*   �ۼ�����  : 2010-12-29
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
 * @author �̵������  
 * @version 1.0
 ******************************************************************************/
public class GolfadmTopBkStatisDaoProc extends AbstractProc {
	
	
	private static GolfadmTopBkStatisDaoProc topGolfSatis =  null;

	public GolfadmTopBkStatisDaoProc(){}

	public synchronized static GolfadmTopBkStatisDaoProc getInstance(){

		if(topGolfSatis == null){
			topGolfSatis = new GolfadmTopBkStatisDaoProc();
		}

		return topGolfSatis;
	
	}
	

	/**
	 * Proc ����.
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
		    
		   //��ȸ ----------------------------------------------------------
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
		   
		   if (!"00".equals(repMbNo)) { pstmt.setString( ++idx, repMbNo ); }
		
		   // �Է°� (INPUT)   
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
	  * Proc ����.
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
	    
	   //��ȸ ----------------------------------------------------------
	   String diff = data.getString("diff");
	   String repMbNo = data.getString("repMbNo");
	   String yyyy = data.getString("yyyy");
	   String from = data.getString("from");
	   String to   = data.getString("to");	   
	   
	   String memberClss = data.getString("memberClss");
	   String bkngStat   = data.getString("bkngStat");
	   String gubun = data.getString("gubun");
	   
	   boolean dInq = "0".equals(diff);
	   int idx = 0;

	   String sql = this.getExcDetail(memberClss, gubun, repMbNo);
	   pstmt = conn.prepareStatement(sql.toString()); 
	   
	   idx = 0;

		if (diff.equals("0")) {
			bkngStat = bkngStat.substring(0,4)+ bkngStat.substring(5,7);			
			pstmt.setString( ++idx, bkngStat + "01" );
			pstmt.setString( ++idx, bkngStat + "31" );
		} else {
			pstmt.setString( ++idx, bkngStat + "0101" );
			pstmt.setString( ++idx, bkngStat + "1231" );
		}	   
	   
	   if (!"00".equals(repMbNo)) { pstmt.setString( ++idx, repMbNo ); }
	
	   rs = pstmt.executeQuery();
	
	   boolean existdata = false;
	
		if(rs != null) {   
		   while ( rs.next() ) {
			existdata = true;
		    
			result.addString("RoundDate", rs.getString("ROUND_DATE") );
			result.addString("GreenNm", rs.getString("GREEN_NM") );
			result.addString("BkngReqTime", "99:99".equals(rs.getString("bkng_req_time"))?"�������":rs.getString("BKNG_REQ_TIME") );
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
 

 	/** ***********************************************************************
	 * Query�� �����Ͽ� �����Ѵ�.    
	 ************************************************************************ */
	private String getSelectQuery(boolean dInq, String repMbNo, String objClss){
		
	    StringBuffer sql = new StringBuffer();
		
		sql.append("\n  SELECT V.UNIT,                                                                                        ");
        sql.append("\n         TO_CHAR(V.INDIV1, '999,999,999') INDIV1,                                                       ");
        sql.append("\n         TO_CHAR(V.INDIV2, '999,999,999') INDIV2,                                                       ");
        sql.append("\n         TO_CHAR(V.INDIV3, '999,999,999') INDIV3,                                                       ");
        sql.append("\n         TO_CHAR(V.INDIV4, '999,999,999') INDIV4,                                                       ");
        sql.append("\n         TO_CHAR(V.CORP1, '999,999,999') CORP1,                                                         ");
        sql.append("\n         TO_CHAR(V.CORP2, '999,999,999') CORP2,                                                         ");
        sql.append("\n         TO_CHAR(V.CORP3, '999,999,999') CORP3,                                                         ");
        sql.append("\n         TO_CHAR(V.CORP4, '999,999,999') CORP4,                                                         ");
        sql.append("\n         TO_CHAR(V.INDIV1 + V.CORP1, '999,999,999') T1,                                              	 ");
        sql.append("\n         TO_CHAR(V.INDIV2 + V.CORP2, '999,999,999') T2,                                              	 ");
        sql.append("\n         TO_CHAR(V.INDIV3 + V.CORP3, '999,999,999') T3,                                              	 ");
        sql.append("\n         TO_CHAR(V.INDIV4 + V.CORP4, '999,999,999') T4                                            		 ");
        sql.append("\n     FROM(                                                                                              ");
        sql.append("\n          SELECT                                                                                        ");
        
		if (dInq) { sql.append("\nSUBSTR(A.TEOF_DATE, 1, 4)||'.'||SUBSTR(A.TEOF_DATE, 5, 2)  UNIT, 	   -- ��������                         "); }
	    else      { sql.append("\nSUBSTR(A.TEOF_DATE, 1, 4) AS UNIT,                                                			 "); }
		
        sql.append("\n            COUNT( DECODE(B.MEMBER_CLSS, '1', 'A', '4', 'A')) INDIV1 , --��û(ȸ�������ڵ�  :1,4->����)   ");        
        sql.append("\n       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '1B', 'A', '4B', 'A'))  INDIV2 , --���� (ȸ�������ڵ�, ��ŷ��������ڵ�) ");
        sql.append("\n       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '1F', 'A', '4F', 'A'))  INDIV3 , --����             	     ");
        sql.append("\n       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '1A', 'A', '4A', 'A', '1C', 'A', '4C', 'A'))  INDIV4 , --���    ");
        sql.append("\n       	 COUNT( DECODE(B.MEMBER_CLSS, '5', 'A'))  CORP1 ,   --��û(ȸ�������ڵ�  :5->����)          	 ");
        sql.append("\n       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '5B', 'A'))  CORP2 ,    --����                            			 ");
        sql.append("\n       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '5F', 'A'))  CORP3 ,    --����                        			     ");
        sql.append("\n       	 COUNT( DECODE(B.MEMBER_CLSS||A.PGRS_YN, '5A', 'A', '5C', 'A')) CORP4	--���				 ");
        sql.append("\n       	 FROM BCDBA.TBGAPLCMGMT A, BCDBA.TBGGOLFCDHD B, BCDBA.TBGFTEMPPAY C                            ");
        sql.append("\n       	 WHERE A.CDHD_ID = B.CDHD_ID                                 								 ");
        sql.append("\n       	 AND A.APLC_SEQ_NO = C.BKNG_REQ_NO(+)                               						 ");
        sql.append("\n       	 AND A.GOLF_SVC_APLC_CLSS='1000'  	-- 1000: ž����ī�������ŷ     								 ");
        sql.append("\n       	 AND A.PGRS_YN != 'R'                               								 ");
        sql.append("\n       	 AND (A.TEOF_DATE >= ? AND A.TEOF_DATE <= ?)      										     ");
        if (!"00".equals(repMbNo)) { sql.append("\n	AND SUBSTR(C.CARD_NO, 5, 2) IN (SELECT MB_NO FROM BCDBA.TBGFMB WHERE REP_MB_NO = ? AND USE_YN = 'Y' ) "); }
	    if (dInq) { sql.append("\nGROUP BY  SUBSTR(A.TEOF_DATE, 1, 4)||'.'||SUBSTR(A.TEOF_DATE, 5, 2)   "); }
	    else      { sql.append("\nGROUP BY SUBSTR(A.TEOF_DATE, 1, 4)		"); }
        sql.append("\n         ) V                                                                                            ");
		
        return sql.toString();
        
	}	
	


	/** ***********************************************************************
	 * �ڼ��� ���� �ڷ� �����Ͽ� �����Ѵ�.    
	 ************************************************************************ */
	 private String getExcDetail(String memberClss, String gubun, String repMbNo){
	
	   StringBuffer sql = new StringBuffer();
	
	   	sql.append("\n	 SELECT																														");
	    sql.append("\n	 SUBSTR(A.TEOF_DATE, 1, 4)||'.'||SUBSTR(A.TEOF_DATE, 5, 2)  ROUND_DATE,   									-- ��������	");
	    sql.append("\n	 A.GREEN_NM, 																								-- �������		");
	   	sql.append("\n	 SUBSTRB(A.TEOF_TIME, 1, 2)||':'||SUBSTRB(A.TEOF_TIME, 3, 2) BKNG_REQ_TIME,   								-- ��ŷ����ú�	");
	    sql.append("\n	 DECODE(A.CHNG_ATON, NULL, NULL, SUBSTRB(A.CHNG_ATON,1,2)||':'||SUBSTRB(A.CHNG_ATON,3,2)) BKNG_OK_TIME, 	-- ��ŷȮ���ú�	");
	    sql.append("\n	 A.CDHD_ID,  																								-- ȸ����ȣ		");
	   	sql.append("\n	 B.HG_NM,																									-- ȸ����		");
	    sql.append("\n	 SUBSTRB(B.JUMIN_NO,1, 7)||'-*******' SOCID, 																-- �ֹι�ȣ		");
	    sql.append("\n	 SUBSTRB(C.CARD_NO, 1, 4)||'-'||SUBSTRB(C.CARD_NO, 5, 4)||'-****-*****' CARD_NO,							-- ī���ȣ		");
	   	sql.append("\n	 D.MB_NM,  																									-- ȸ����		");
	    sql.append("\n	 DECODE(B.MEMBER_CLSS, '1', '����', '4', '����', '5', '����') MB_CLSS_NM, 									-- ȸ������		");
	    sql.append("\n	 DECODE(A.PGRS_YN, 'A', '�������', 'B', '��ŷȮ��', 'F', '����', 'C', '��ŷ���') BKNG_STAT_NM  				-- ó�����		");
	   	sql.append("\n	 FROM BCDBA.TBGAPLCMGMT A, BCDBA.TBGGOLFCDHD B, BCDBA.TBGFTEMPPAY C,  BCDBA.TBGFMB D 										");
	    sql.append("\n	 WHERE A.CDHD_ID = B.CDHD_ID																								");
	    sql.append("\n	 AND A.APLC_SEQ_NO = C.BKNG_REQ_NO(+)																						");
	   	sql.append("\n	 AND A.GOLF_SVC_APLC_CLSS='1000'   -- 1000: ž����ī�������ŷ																");
	    sql.append("\n	 AND A.PGRS_YN != 'R'																										");
	    sql.append("\n	 AND (A.TEOF_DATE >= ? AND A.TEOF_DATE <= ?)																				");
  
		if(gubun.equals("2")){//����
			sql.append("\n                 AND A.PGRS_YN = 'B'                                                    									");
	    }else if(gubun.equals("3")){//����
		    sql.append("\n                 AND A.PGRS_YN = 'F'                                                     									");
	    }else if(gubun.equals("4")){//���
			sql.append("\n                 AND A.PGRS_YN in ('A', 'C')																				");
	    }
	
		if (!memberClss.equals("0"))  {
			if (memberClss.equals("1")) {
				sql.append("\n	AND B.MEMBER_CLSS IN ('1','4')                       																");
			}else {
				sql.append("\n	AND B.MEMBER_CLSS = '5'                          																	");
			}
		}		
			
		if (!"00".equals(repMbNo)) { sql.append("\n		AND SUBSTR(C.CARD_NO, 5, 2) IN (SELECT MB_NO FROM BCDBA.TBGFMB WHERE REP_MB_NO = ? AND USE_YN = 'Y' ) "); }
	    
	   	sql.append("\n	 AND D.MB_NO(+) = SUBSTRB(C.CARD_NO, 5, 2)																						");
	    sql.append("\n	 ORDER BY A.TEOF_DATE, B.HG_NM    																							");

	    return sql.toString();
	
	 }
	 

}