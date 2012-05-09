/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntTmMovieProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : TM 영화 예매권 이벤트 처리 
*   적용범위  : golf
*   작성일자  : 2010-03-17
************************** 수정이력 ****************************************************************
*    일자     작성자   변경사항
*20110401 	이경희 	TM 구분 12, 13에 따른 코드추가
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.tmMovie;

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
public class GolfEvntTmMovieProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntTmMovieProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntTmMovieProc() {}		


	// 2) TM회원인지 확인
	public String isTmCheck(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;  
		String result = "";
		
		try {
			String userSocid = data.getString("userSocid");			
			
			conn = context.getDbConnection("default", null);
			
			String sql = this.getIsTmQuery(); 
			pstmt = conn.prepareStatement(sql.toString());			
			pstmt.setString(1,userSocid);			
			
			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result = rs.getString("CUPN_KIND");
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

	// 2) TM회원인지 확인- 로그인일 경우
	public DbTaoResult isTmCheckLogin(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null; 
		DbTaoResult  result =  new DbTaoResult(title);
		
		try {
			String userSocid = data.getString("userSocid");
			
			conn = context.getDbConnection("default", null);
			
			String sql = this.getIsTmLoginQuery(); 
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,userSocid);
			
			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result.addString("JOIN_CHNL"	,rs.getString("JOIN_CHNL") );
					result.addString("CUPN_KIND"	,rs.getString("CUPN_KIND") );
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

	// 3)	이벤트기간 체크
	public DbTaoResult eventDateCheck(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		String tm_evt_no = data.getString("tm_evt_no");

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getEventDateCheck(); 
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1,tm_evt_no);

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

	// 4) 쿠폰600(가변)개 소진시 이벤트 진행 하지 않음
	public String eventCountYn(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		String tm_evt_no = data.getString("tm_evt_no");
		String evnt_yn = "N";

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getEvtCountYn(); 
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1,tm_evt_no);
			pstmt.setString(2,tm_evt_no);

			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){					
					if(!GolfUtil.empty(rs.getString("EVT_YN"))){
						evnt_yn = rs.getString("EVT_YN");
					}
				}
			}
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return evnt_yn;
	}

	// 4) 영화예매권 지급여부 확인
	public int useEvtCpnCnt(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;

		String tm_evt_no = data.getString("tm_evt_no");
		String userSocid = data.getString("userSocid");
		int useEvtCpnCnt = 0;

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getUseEvtCpnCnt(); 
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1,tm_evt_no);
			pstmt.setString(2,userSocid);

			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){					
					useEvtCpnCnt = rs.getInt("USE_EVT_CPN_CNT");
				}
			}
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return useEvtCpnCnt;
	}
	
	
	// 5-1) 1건이상이면  인증번호 4개 다시 보여줌 
	public DbTaoResult cpnList(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		String tm_evt_no = data.getString("tm_evt_no");
		String userSocid = data.getString("userSocid");

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getCpnList(); 
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1,tm_evt_no);
			pstmt.setString(2,userSocid);

			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result.addString("CUPN_NO"				,rs.getString("CUPN_NO") );	
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
	

	// DM 이벤트 회원유무 - 가입경로 리턴
	public int isCpCheckLogin(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;  
		int result = 0;
		
		try {
			String userAccount = data.getString("userAccount");
			
			conn = context.getDbConnection("default", null);
			
			String sql = this.getIsCpLoginQuery(); 
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,userAccount);
			
			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result = rs.getInt("CNT");
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
	public int cupnNumber(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		String tm_evt_no = data.getString("tm_evt_no");
		int doUpdate = 0;
		int cnt = 0;

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getCupnNumber(); 
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(1,tm_evt_no);

			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){

					cnt++;
					result.addString("cupn", rs.getString("CUPN"));
					result.addString("pwin_grd", cnt+"");
					
				}
			}

			doUpdate = insertCupnNumber(context, request, data, result);
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
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
	public int insertCupnNumber(WaContext context,  HttpServletRequest request ,TaoDataSet data ,DbTaoResult result) throws BaseException {
		Connection conn = null;
		PreparedStatement pstmt = null;	
		PreparedStatement pstmt2 = null;		
		PreparedStatement pstmt3 = null;		
		
		String userNm = data.getString("userNm");
		String socid  = data.getString("socid");
		String email  = data.getString("email");
		String tm_evt_no = data.getString("tm_evt_no");
		String cupn = "";	// 쿠폰번호
		String pwin_grd = "";	// 등수
		
		
		int doUpdate = 0;

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

			
			pstmt2.setString(++idx2, tm_evt_no);

			
			int cnt = 0;
			while(result!=null && result.isNext()){
				result.next();
				
				cupn = result.getString("cupn");
				pwin_grd = result.getString("pwin_grd");
				
				idx = 0;
				pstmt.setString(++idx, tm_evt_no);
				pstmt.setString(++idx, pwin_grd);
				pstmt.setString(++idx, socid);
				pstmt.setString(++idx, userNm);
				pstmt.setString(++idx, email);
				pstmt.setString(++idx, cupn);
				pstmt.setString(++idx, "1");
				
				pstmt.executeUpdate();

				pstmt2.setString(++idx2, cupn);
				cnt++;
			}
			
			pstmt3.setString(1,cnt+"");
			pstmt3.setString(2,tm_evt_no);
			
			if ( pstmt2.executeUpdate() > 0 && pstmt3.executeUpdate() > 0){
				doUpdate = 1;
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
	public int insertCupnNumberSK(WaContext context,  HttpServletRequest request ,TaoDataSet data ,DbTaoResult result) throws BaseException {
		Connection conn = null;
		PreparedStatement pstmt = null;		
		
		String userNm = data.getString("userNm");
		String socid  = data.getString("socid");
		String email  = data.getString("email");
		String tm_evt_no = data.getString("tm_evt_no");
		String cupn = "";	// 쿠폰번호
		String pwin_grd = "";	// 등수
		
		
		int doUpdate = 0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			String sql = this.getInsert();

			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			int idx2 = 0;

			

			
			int cnt = 0;
			while(result!=null && result.isNext()){
				result.next();
				
				cupn = result.getString("cupn");
				pwin_grd = result.getString("pwin_grd");
				
				idx = 0;
				pstmt.setString(++idx, tm_evt_no);
				pstmt.setString(++idx, pwin_grd);
				pstmt.setString(++idx, socid);
				pstmt.setString(++idx, userNm);
				pstmt.setString(++idx, email);
				pstmt.setString(++idx, cupn);
				pstmt.setString(++idx, "2");
				pstmt.executeUpdate();

				cnt++;
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
		String tm_evt_no = data.getString("tm_evt_no");

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getCpnNumberSql(); 
			pstmt = conn.prepareStatement(sql.toString());
				
			pstmt.setString(1,jumin_no);
			pstmt.setString(2,tm_evt_no);

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
		String tm_evt_no = data.getString("tm_evt_no");
		int cnt = 0;

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getDplCheckSql(); 
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(1,tm_evt_no);
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
		String tm_evt_no = data.getString("tm_evt_no");
		String useYN = "";

		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getUseYNSql(); 
			pstmt = conn.prepareStatement(sql.toString());
			
			pstmt.setString(1,tm_evt_no);
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
    * 3)	이벤트기간 체크   
    ************************************************************************ */
	public String getEventDateCheck(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n     SELECT FROM_DATE,TO_DATE ");
		 sql.append("\n       FROM BCDBA.TBEVNTLOTINFO ");
		 sql.append("\n      WHERE SITE_CLSS='10' AND EVNT_NO = ? ");

		 return sql.toString();
	}

	/** ***********************************************************************
    * 쿠폰 발급내역 검사 
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
    * 7-1) 미사용 쿠폰번호 발급 - 4개
    ************************************************************************ */
	public String getCupnNumber(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	SELECT RNUM, CUPN, CUPN_VALD_STRT_DATE, CUPN_VALD_END_DATE FROM (	");
		 sql.append("\n		SELECT ROWNUM RNUM, CUPN_NO CUPN, CUPN_VALD_STRT_DATE, CUPN_VALD_END_DATE FROM (	");
		 sql.append("\n			SELECT CUPN_NO, CUPN_VALD_STRT_DATE, CUPN_VALD_END_DATE	");
		 sql.append("\n			FROM BCDBA.TBEVNTUNIFCUPNINFO	");
		 sql.append("\n			WHERE SITE_CLSS='10' AND EVNT_NO=? AND CUPN_PYM_YN='N'	");
		 sql.append("\n			ORDER BY CUPN_VALD_STRT_DATE, CUPN_NO	");
		 sql.append("\n		)	");
		 sql.append("\n	) WHERE RNUM<5	");
		 return sql.toString();
	}

	/** ***********************************************************************
    * 7-2) 이벤트 당첨테이블에 정보 입력
    ************************************************************************ */
	public String getInsert(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	INSERT INTO  BCDBA.TBEVNTLOTPWIN (SEQ_NO ,SITE_CLSS,EVNT_NO,PWIN_GRD,PWIN_DATE ,JUMIN_NO,HG_NM,PROC_YN, EMAIL, CUPN_NO ,USE_NO) ");
		 sql.append("\n	VALUES (EVNTLOTCTNT_SEQ.NEXTVAL,'10',?, ? ,TO_CHAR(SYSDATE,'yyyyMMdd'),?, ?, 1, ?, ? ,? ) ");
		 return sql.toString();
	}
	
	/** ***********************************************************************
    * 7-3) 할인쿠폰 사용처리
    ************************************************************************ */
	public String getCpnUpdate(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	UPDATE BCDBA.TBEVNTUNIFCUPNINFO SET CUPN_PYM_YN ='Y' WHERE SITE_CLSS='10' AND EVNT_NO=? AND CUPN_NO IN (?,?,?,?) ");
		 return sql.toString();
	}
	
	/** ***********************************************************************
    * 7-4) 이벤트 추첨정보 업데이트
    ************************************************************************ */
	public String getUpdate(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	UPDATE BCDBA.TBEVNTLOTCODE SET CUR_GRD_CNT = CUR_GRD_CNT+? WHERE SITE_CLSS='10' AND EVNT_NO= ? AND PWIN_GRD = 1	");
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

	/** ***********************************************************************
    * 2) TM회원인지 확인   
    ************************************************************************ */
	public String getIsTmQuery(){
		StringBuffer sql = new StringBuffer();
		
		sql.append("\t	SELECT ROWNUM RNUM, JOIN_CHNL,	\n");
		sql.append("\t			DECODE (JOIN_CHNL, '11',	\n");
		sql.append("\t					CASE WHEN WK_DATE>'20100731' THEN 'SK' ELSE 'MOVIE' END 	,	\n");
		sql.append("\t			'12','MOVIE', '13', 'MOVIE') CUPN_KIND	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT WK_DATE, JOIN_CHNL	\n");
		sql.append("\t	    FROM BCDBA.TBLUGTMCSTMR	\n");
		sql.append("\t	    WHERE RND_CD_CLSS='2' AND JOIN_CHNL IN ('11','12','13') AND ACPT_CHNL_CLSS='1' AND TB_RSLT_CLSS ='00' AND JUMIN_NO = ?	\n");
		sql.append("\t	    	AND WK_DATE>=TO_CHAR(SYSDATE-365,'YYYYMMDD')	\n");
		sql.append("\t	    ORDER BY GOLF_CDHD_GRD_CLSS DESC, WK_DATE DESC	\n");
		sql.append("\t	) WHERE ROWNUM=1	\n");
		return sql.toString();
	}	

	/** ***********************************************************************
    * 2) TM회원인지 확인 - 로그인 시  - 이지웰 추가 JOIN_CHNL='EZ', ACPT_CHNL_CLSS='3'
    ************************************************************************ */
	public String getIsTmLoginQuery(){
		 StringBuffer sql = new StringBuffer();
		 		
		 sql.append("\n	SELECT JOIN_CHNL, ACPT_CHNL_CLSS, ");
		 sql.append("\n			DECODE (JOIN_CHNL, '12','MOVIE', '13', 'MOVIE',	");
		 sql.append("\n					CASE WHEN WK_DATE>'20100731' THEN 'SK' ELSE 'MOVIE' END	");
		 sql.append("\n			) CUPN_KIND		");
		 sql.append("\n	FROM BCDBA.TBLUGTMCSTMR	");
		 sql.append("\n	WHERE RND_CD_CLSS='2' AND JOIN_CHNL IN ('11','12','13','EZ') AND ACPT_CHNL_CLSS IN ('1','3') AND TB_RSLT_CLSS IN ('00','01') AND JUMIN_NO = ?	");		
		 return sql.toString();
	}	

	/** ***********************************************************************
    * 2) DM 이벤트 회원인지 확인 - 로그인 시, 가입경로 리턴  
    ************************************************************************ */
	public String getIsCpLoginQuery(){
		 StringBuffer sql = new StringBuffer(); 
		 sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFCDHD WHERE AFFI_FIRM_NM IN ('EVENTGL12345', 'EVENTECHAMP201007', 'EVENTLETTER08') AND CDHD_ID = ?	");
		 return sql.toString();
	}	
	
	/** ***********************************************************************
    * 4) 쿠폰600(가변)개 소진시 이벤트 진행 하지 않음    
    ************************************************************************ */
	public String getEvtCountYn(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	SELECT CASE WHEN USE_CNT>=(SELECT GRD_CNT FROM BCDBA.TBEVNTLOTCODE WHERE EVNT_NO=?) THEN 'N' ELSE 'Y' END EVT_YN	");
		 sql.append("\n	FROM (SELECT COUNT(*) USE_CNT FROM BCDBA.TBEVNTUNIFCUPNINFO WHERE SITE_CLSS='10' AND EVNT_NO=? AND CUPN_PYM_YN='Y')	");
		 return sql.toString();
	}	

	/** ***********************************************************************
    * 5) 영화예매권 지급여부 확인 
    ************************************************************************ */
	public String getUseEvtCpnCnt(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	SELECT  COUNT(*) USE_EVT_CPN_CNT  FROM  BCDBA.TBEVNTLOTPWIN  WHERE SITE_CLSS='10' AND EVNT_NO=? AND JUMIN_NO=?	");
		 return sql.toString();
	}		

	/** ***********************************************************************
    * 5-1) 1건이상이면  인증번호 4개 다시 보여줌 
    ************************************************************************ */
	public String getCpnList(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	SELECT SEQ_NO, CUPN_NO FROM BCDBA.TBEVNTLOTPWIN WHERE SITE_CLSS='10' AND EVNT_NO=? AND JUMIN_NO = ? ORDER BY CUPN_NO	");
		 return sql.toString();
	}	
}
