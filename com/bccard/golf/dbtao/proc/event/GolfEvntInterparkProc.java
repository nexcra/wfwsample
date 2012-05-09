/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntInterparkProc
*   작성자    : E4NET 은장선
*   내용      : BC Golf 인터파크 이벤트
*   적용범위  : golf
*   작성일자  : 2009-08-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

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
 * @author	E4NET
 * @version	1.0
 ******************************************************************************/
public class GolfEvntInterparkProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntInterparkProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntInterparkProc() {}		

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult eventDateCheck(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		String evnt_no = data.getString("evnt_no");

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getEventDateCheck(); 
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1,evnt_no);

			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result.addString("FROM_DATE"				,rs.getString("FROM_DATE") );
					result.addString("TO_DATE"				    ,rs.getString("TO_DATE") );
					result.addString("RESULT", "00"); //정상결과
				}
				if(result.size() < 1) {				
					result.addString("RESULT", "01");
				}
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
	public DbTaoResult cupnNumber(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		String evnt_no = data.getString("evnt_no");

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getCupnNumber(); 
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(1,evnt_no);

			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result.addString("CUPN"				,rs.getString("CUPN") );	
					result.addString("RESULT", "00"); //정상결과
				}
				if(result.size() < 1) {				
					result.addString("RESULT", "01");
				}
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
	public boolean insertCupnNumber(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;	
		PreparedStatement pstmt2 = null;		
		PreparedStatement pstmt3 = null;		
		
		String userNm = data.getString("userNm");
		String socid  = data.getString("socid");
		String email  = data.getString("email");
		String cupn   = data.getString("cupn");	
		String ea_info   = data.getString("ea_info");
		String evnt_no = data.getString("evnt_no");
		if (ea_info==null) ea_info ="";
		
		boolean doUpdate = false;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			String sql = this.getInsert();
			String sql2 = this.getCpnUpdate();
			String sql3 = this.getUpdate();

			pstmt = conn.prepareStatement(sql);
			pstmt2 = conn.prepareStatement(sql2);
			pstmt3 = conn.prepareStatement(sql3);
			
			int idx = 0;
			int idx2 = 0;

			pstmt.setString(++idx, evnt_no);
			pstmt.setString(++idx, socid);
			pstmt.setString(++idx, userNm);
			pstmt.setString(++idx, email);
			pstmt.setString(++idx, cupn);
			pstmt.setString(++idx, ea_info);
			
			pstmt2.setString(++idx2, evnt_no);
			pstmt2.setString(++idx2, cupn);

			pstmt3.setString(1,evnt_no);
			
			

			if ( pstmt.executeUpdate() > 0 && pstmt2.executeUpdate() > 0 && pstmt3.executeUpdate() > 0){
				doUpdate = true;
				conn.commit();
			}			
			conn.setAutoCommit(true);
			
		} catch (Throwable t) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return doUpdate;
	}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getCpnNumber(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		String jumin_no = data.getString("jumin_no");
		String evnt_no = data.getString("evnt_no");

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getCpnNumberSql(); 
			pstmt = conn.prepareStatement(sql.toString());
				
			pstmt.setString(1,jumin_no);
			pstmt.setString(2,evnt_no);

			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result.addString("JUMIN_NO"				,rs.getString("JUMIN_NO") );
					result.addString("PWIN_DATE"			,rs.getString("PWIN_DATE") );
					result.addString("CUPN"				    ,rs.getString("CUPN_NO") );
					result.addString("EMAIL"				,rs.getString("EMAIL") );
					result.addString("RESULT", "00"); //정상결과
				}
				if(result.size() < 1) {				
					result.addString("RESULT", "01");
				}
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
	public boolean setChgEmailAddress(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;		
		
		String jumin_no = data.getString("jumin_no");
		String email    = data.getString("email");
		boolean doUpdate = false;		

		try {
			conn = context.getDbConnection("default", null);

			conn.setAutoCommit(false);

			String sql = this.getEmailUpateSql(); 
			pstmt = conn.prepareStatement(sql.toString());
				
			pstmt.setString(1,    email);
			pstmt.setString(2, jumin_no);	

			if ( pstmt.executeUpdate() > 0){
				doUpdate = true;
				conn.commit();
			}			
			conn.setAutoCommit(true);
			
		} catch (Throwable t) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return doUpdate;
	}

	public int getDplCheck(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		
		String jumin_no = data.getString("socid");
		String evnt_no = data.getString("evnt_no");
		int cnt = 0;

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getDplCheckSql(); 
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(1,evnt_no);
			pstmt.setString(2,jumin_no);

			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					cnt = rs.getInt("CNT");
				}
			}
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return cnt;
	}

	public String getUseYN(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		
		String jumin_no = data.getString("socid");
		String evnt_no = data.getString("evnt_no");
		String useYN = "";

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getUseYNSql(); 
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(1,evnt_no);
			pstmt.setString(2,jumin_no);
			
			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					useYN = rs.getString("USE_NO");
				}
			}
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return useYN;
	}



	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	public String getEventDateCheck(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n     SELECT FROM_DATE,TO_DATE ");
		 sql.append("\n       FROM BCDBA.TBEVNTLOTINFO ");
		 sql.append("\n      WHERE SITE_CLSS='10' AND EVNT_NO = ? ");

		 return sql.toString();
	}

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	public String getDplCheckSql(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n     SELECT COUNT(*) CNT                     ");
		 sql.append("\n       FROM BCDBA.TBEVNTLOTPWIN         ");
		 sql.append("\n      WHERE SITE_CLSS='10' AND EVNT_NO = ? ");
		 sql.append("\n        AND JUMIN_NO = ?                     ");

		 return sql.toString();
	}

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	public String getCupnNumber(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n     SELECT MAX(CUPN_NO) CUPN ");
		 sql.append("\n       FROM BCDBA.TBEVNTUNIFCUPNINFO ");
		 sql.append("\n      WHERE SITE_CLSS='10' AND   EVNT_NO= ?  AND CUPN_PYM_YN='N' ");

		 return sql.toString();
	}

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	public String getInsert(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n     INSERT INTO  BCDBA.TBEVNTLOTPWIN ( ");
		 sql.append("\n       SEQ_NO ,SITE_CLSS,EVNT_NO,PWIN_GRD,PWIN_DATE , ");
		 sql.append("\n       JUMIN_NO,HG_NM,PROC_YN, EMAIL, CUPN_NO, EA_INFO ) ");
		 sql.append("\n       VALUES (EVNTLOTCTNT_SEQ.NEXTVAL,'10',?, 1 ,TO_CHAR(SYSDATE,'yyyyMMdd'), ");
		 sql.append("\n       ?, ?, 1, ?, ? ,? ) ");

		 return sql.toString();
	}
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	public String getCpnUpdate(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n		UPDATE BCDBA.TBEVNTUNIFCUPNINFO ");
		 sql.append("\n        SET CUPN_PYM_YN ='Y' ");
		 sql.append("\n      WHERE SITE_CLSS='10' AND   EVNT_NO= ? ");
		 sql.append("\n        AND CUPN_NO = ? ");

		 return sql.toString();
	}
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	public String getUpdate(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n		UPDATE   BCDBA.TBEVNTLOTCODE  ");
		 sql.append("\n        SET  CUR_GRD_CNT = CUR_GRD_CNT+1 ");
		 sql.append("\n      WHERE SITE_CLSS='10' AND   EVNT_NO= ?  AND  PWIN_GRD = 1 ");

		 return sql.toString();
	}

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	public String getCpnNumberSql(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n		SELECT  JUMIN_NO, PWIN_DATE, CUPN_NO,EMAIL ");
		 sql.append("\n       FROM  BCDBA.TBEVNTLOTPWIN                ");
		 sql.append("\n      WHERE  JUMIN_NO = ?                       ");
		 sql.append("\n        AND  SITE_CLSS='10' AND   EVNT_NO= ?    ");
		 sql.append("\n        AND  PWIN_GRD = 1                       ");

		 return sql.toString();
	}
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	public String getEmailUpateSql(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n		UPDATE  BCDBA.TBEVNTLOTPWIN SET email = ? WHERE JUMIN_NO = ? ");		

		 return sql.toString();
	}

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	public String getUseYNSql(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n		SELECT Trim(DECODE(USE_NO,NULL,'N',USE_NO)) USE_NO FROM BCDBA.TBEVNTLOTPWIN WHERE SITE_CLSS = '10' AND EVNT_NO = ? AND JUMIN_NO = ? ");		

		 return sql.toString();
	}	
}
