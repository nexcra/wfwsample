/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntTmMovieProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : TM 영화 예매권 이벤트 처리 
*   적용범위  : golf
*   작성일자  : 2010-03-17
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
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
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
public class GolfEvntMkMemberProc extends AbstractProc {
	/** *****************************************************************
	 * GolfEvntTmMovieProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntMkMemberProc() {}		


	// 1)	이벤트기간 체크
	public DbTaoResult eventDateCheck(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);


		try {
			conn = context.getDbConnection("default", null);
			String sql = this.getEventDateCheck(); 
			pstmt = conn.prepareStatement(sql.toString());


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
	
	// 2) 마케팅 회원인지 확인
	public DbTaoResult isMkMember(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null; 
		DbTaoResult  result =  new DbTaoResult(title);
		
		try {
			String userSocid = data.getString("userSocid");
			String from_date = data.getString("from_date");
			String to_date = data.getString("to_date");
			
			conn = context.getDbConnection("default", null);
			
			String sql = this.getIsMkLoginQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,userSocid);
			pstmt.setString(2,from_date); 
			pstmt.setString(3,to_date);
			pstmt.setString(4,userSocid);
			
			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result.addString("JUMIN_NO"	,rs.getString("JUMIN_NO") );
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
	
	// 3) 마케팅 회원의 데이터 가져오기
	public DbTaoResult getMkMember(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null; 
		DbTaoResult  result =  new DbTaoResult(title);
		
		try {
			String jumin_no = data.getString("JUMIN_NO");
			
			conn = context.getDbConnection("default", null);
			
			String sql = this.getMkLoginQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,jumin_no);
			pstmt.setString(2,jumin_no);
			
			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next()){
					result.addString("JUMIN_NO"	,rs.getString("JUMIN_NO") );
					result.addString("HG_NM"	,rs.getString("HG_NM") );
					result.addString("RCRU_PATH_NM"	,rs.getString("RCRU_PATH_NM") );
					String start_date = rs.getString("CAMP_STRT_DATE");
					String end_date = rs.getString("CAMP_END_DATE");
					if (!GolfUtil.isNull(start_date)) start_date = DateUtil.format(start_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					if (!GolfUtil.isNull(end_date)) end_date = DateUtil.format(end_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					
					result.addString("CAMP_START_DATE"	,start_date );
					result.addString("CAMP_END_DATE"	,end_date );
					
					result.addString("MER_NO"	,rs.getString("MER_NO") );
					result.addString("MER_NM"	,rs.getString("MER_NM") );
					result.addString("MER_TEL_NO"	,rs.getString("MER_TEL_NO") );
					result.addString("MEMO_EXPL"	,rs.getString("MEMO_EXPL") );
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
	
	// 4) 마케팅 회원의 쿠폰 발급내역 가져오기
	public DbTaoResult getMkMemberAppList(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			String jumin_no = data.getString("JUMIN_NO");
			String card_no = data.getString("CARD_NO");
			
			conn = context.getDbConnection("default", null);
			
			String sql = this.getAppListQuery();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,jumin_no);
			pstmt.setString(2,jumin_no);
			pstmt.setString(3,card_no);
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 
				while(rs.next()){
					result.addString("SEQ_NO" 		,rs.getString("SEQ_NO") );	//발급일자
					result.addString("PWIN_DATE" 		,rs.getString("PWIN_DATE") );	//발급일자
					result.addString("CARD_NO" 		,rs.getString("CARD_NO") );	//상호
					result.addString("MER_NM" 			,rs.getString("MER_NM") );	//쿠폰
					result.addString("CUPN_NO" 			,rs.getString("CUPN_NO") );	//쿠폰
					result.addString("CUPN_PRN_NUM" 			,rs.getString("CUPN_PRN_NUM") );	//쿠폰출력횟수					
					result.addString("RESULT", "00"); //정상결과
				}
			}
			debug("result size = "+result.size());
			if(result.size() < 1) {
				debug("@@@@@@@실패");
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
	

//5) 마케팅 회원의 쿠폰 정보 상세보기
public DbTaoResult getMkMemberAppDetail(WaContext context, TaoDataSet data)
		throws BaseException {

	String title = data.getString("TITLE");
	ResultSet rs = null;
	Connection conn = null;
	PreparedStatement pstmt = null;
	DbTaoResult result = new DbTaoResult(title);

	try {
		conn = context.getDbConnection("default", null);

		// 회원통합테이블 관련 수정사항 진행
		// 조회 ----------------------------------------------------------
		String sql = this.getAppDetailQuery();

		// 입력값 (INPUT)
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, data.getString("SEQ_NO"));

		rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				result.addString("SEQ_NO", rs.getString("SEQ_NO"));
				result.addString("HG_NM", rs.getString("HG_NM"));
				result.addString("CARD_NO", rs.getString("CARD_NO").trim());
				result.addString("CUPN_NO", rs.getString("CUPN_NO"));
				result.addString("RESULT", "00"); // 정상결과
			}
		}

		if (result.size() < 1) {
			result.addString("RESULT", "01");
		}

	} catch (Throwable t) {
		throw new BaseException(t);
	} finally {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception ignored) {
		}
		try {
			if (pstmt != null)
				pstmt.close();
		} catch (Exception ignored) {
		}
		try {
			if (conn != null)
				conn.close();
		} catch (Exception ignored) {
		}
	}

	return result;
}

//6) 쿠폰번호 가져오기 
public DbTaoResult getCouponNum(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
	String title = data.getString("TITLE");
	ResultSet rs = null;
	Connection conn = null;
	PreparedStatement pstmt = null; 
	DbTaoResult  result =  new DbTaoResult(title);
	
	try {
		debug("@@@@@getCouponNum");
		String mer_no = data.getString("MER_NO");
		
		conn = context.getDbConnection("default", null);
		
		String sql = this.getCuponQuery();  
		pstmt = conn.prepareStatement(sql.toString());
		pstmt.setString(1,mer_no);
		
		rs = pstmt.executeQuery();

		if(rs != null) {

			while(rs.next()){
				debug("정상");
				result.addString("CUPN_NO"	,rs.getString("CUPN_NO") );
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

//7) 쿠폰 사용 마킹 처리
public int updateMarking(WaContext context, String mer_no, String cupn_no ) throws Exception, Throwable{
	Connection          conn = null;
	PreparedStatement  pstmt = null;

	int cnt		= 0; 

	try{
		StringBuffer sql = new StringBuffer();	

		sql.append(" UPDATE BCDBA.TBEVNTUNIFCUPNINFO SET CUPN_PYM_YN ='Y'  \n");
		sql.append(" WHERE SITE_CLSS='10' AND EVNT_NO ='120'   \n");
		sql.append(" AND CUPN_DTL_CTNT = ?   \n");
		sql.append(" AND CUPN_NO =?   \n");

		conn = context.getDbConnection("default", null);
		pstmt = conn.prepareStatement(sql.toString());

		pstmt.setString(1, mer_no);
		pstmt.setString(2, cupn_no);
		cnt = pstmt.executeUpdate(); 

	}catch(Exception e){
		info("GolfEvntMkMemberProc|updateMarking Exceptioin : ", e);
		throw e;
	}finally{
		if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
		if(conn != null)  try{ conn.close();  }catch(Exception e){}			
	}
	return cnt;
}

	
//8) 마케팅 회원의 쿠폰 발급하기
public int getMkInsCupon(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
	String title = "마케팅 회원의 쿠폰 발급 처리";
	int result = 0;
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql = "";
			
	try {
		conn = context.getDbConnection("default", null);	
		conn.setAutoCommit(false);		
		
        //rs = pstmt.executeQuery();		
        debug("JUMIN_NO : "+data.getString("JUMIN_NO"));
        debug("HG_NM : "+data.getString("HG_NM"));
        debug("MER_NO : "+data.getString("MER_NO"));
        debug("CUPN_NO : "+data.getString("CUPN_NO"));
        
		
        //이미 같은 주민번호로 쿠폰이 발급됐는지 체크
        pstmt = conn.prepareStatement(getCpnCk());
        pstmt.setString(1, data.getString("JUMIN_NO") ); 	
        pstmt.setString(2, data.getString("CUPN_NO") );
        rs = pstmt.executeQuery();
        String ckValue = "Y";
        if(rs !=null)
        {
        	while(rs.next()){	
        		debug(" 같은 주민번호로 이미 쿠폰이 발급");
        		ckValue = "N";        		       		        		
			}
        }
        
        if("Y".equals(ckValue))
        {
        	sql = this.getInsertQuery();//Insert Query
    		pstmt = conn.prepareStatement(sql);		
    		pstmt.setString(1, data.getString("JUMIN_NO") ); 			
    		pstmt.setString(2, data.getString("HG_NM") ); 
    		pstmt.setString(3, data.getString("MER_NO") ); 
    		pstmt.setString(4, data.getString("CUPN_NO") );
    		debug("@@@@@@@@@쿼리"+sql.toString());
    		result = pstmt.executeUpdate();
        }
        		        		
		if(result > 0) {
			conn.commit();
		} else {
			conn.rollback();
		}
		
	} catch(Exception e) {
		try	{
			conn.rollback();
		}catch (Exception c){}
		
        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "시스템오류입니다." );
        throw new DbTaoException(msgEtt,e);
	} finally {
		try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
		try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
	}			

	return result;
}

//9) 마케팅 오퍼의 발급처리 
public int updateMkOffer(WaContext context, String jumin_no, String mer_no, String user_id ) throws Exception, Throwable{
	Connection          conn = null;
	PreparedStatement  pstmt = null;

	int cnt		= 0; 

	try{
		StringBuffer sql = new StringBuffer();	

		sql.append(" UPDATE BCDBA.TBACRGCDHDLODNTBL set proc_rslt_clss = '00', proc_rslt_ctnt= ?  \n");
		sql.append("WHERE RCRU_PL_CLSS ='4004'  \n");
		sql.append("AND AND JUMIN_NO=? and MER_NO LIKE ?||'%'    \n");


		conn = context.getDbConnection("default", null);
		pstmt = conn.prepareStatement(sql.toString());

		pstmt.setString(1, user_id);
		pstmt.setString(2, mer_no);
		pstmt.setString(3, jumin_no);
		cnt = pstmt.executeUpdate(); 

	}catch(Exception e){
		info("GolfLoungPaymentCancelProc|updateTM Exceptioin : ", e);
		throw e;
	}finally{
		if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
		if(conn != null)  try{ conn.close();  }catch(Exception e){}			
	}
	return cnt;
}

//10) 신청 갯수 구하기
public int getCuponCnt(WaContext context,String mer_no, String jumin_no ) throws Exception, Throwable{
	Connection         conn = null;
	PreparedStatement pstmt = null;
	ResultSet            rs = null;	
	int cnt		= 10; 

	try{
		StringBuffer sql = new StringBuffer();	

		sql.append("SELECT COUNT(*) CNT FROM BCDBA.TBEVNTLOTPWIN WHERE SITE_CLSS ='10'   \n");
		sql.append("  AND EVNT_NO =120 AND JUMIN_NO = ? AND CARD_NO LIKE ?||'%'  \n");  
		sql.append("  AND PROC_YN = '1'  \n");

		conn = context.getDbConnection("default", null);

		pstmt = conn.prepareStatement(sql.toString());
		pstmt.setString(1, jumin_no);
		pstmt.setString(2, mer_no);

		rs = pstmt.executeQuery();            

		if (rs.next())	{	
			
			cnt = rs.getInt(1);	
			debug("## cnt : "+cnt);
		}				

	}catch(Exception e){
		info("GolfLoungPaymentCancelProc|getServiceCnt Exceptioin : ", e);
		throw e;

	}finally{
		if(rs != null)    try{ rs.close();    }catch(Exception e){}
		if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
		if(conn != null)  try{ conn.close();  }catch(Exception e){}
	}

	return cnt;
}
//11) 인쇄횟수 증가
public int updatePrtHit(WaContext context, String mer_no, String cupn_no ) throws Exception, Throwable{
	Connection          conn = null;
	PreparedStatement  pstmt = null;

	int cnt		= 0; 

	try{
		StringBuffer sql = new StringBuffer();	

		sql.append(" UPDATE BCDBA.TBEVNTUNIFCUPNINFO SET CUPN_PRN_NUM =CUPN_PRN_NUM + 1  \n");
		sql.append(" WHERE SITE_CLSS='10' AND EVNT_NO ='120'   \n");
		sql.append(" AND CUPN_NO =?   \n");

		conn = context.getDbConnection("default", null);
		pstmt = conn.prepareStatement(sql.toString());

		pstmt.setString(1, cupn_no);
		cnt = pstmt.executeUpdate(); 

	}catch(Exception e){
		info("GolfEvntMkMemberProc|updateMarking Exceptioin : ", e);
		throw e;
	}finally{
		if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
		if(conn != null)  try{ conn.close();  }catch(Exception e){}			
	}
	return cnt;
}

//12) 골프장 정보 가져오기
public DbTaoResult evntMkPrcGroundDetail(WaContext context, TaoDataSet data)		throws BaseException {

	String title = data.getString("TITLE");
	ResultSet rs = null;
	Connection conn = null;
	PreparedStatement pstmt = null;
	DbTaoResult result = new DbTaoResult(title);

	try {
		conn = context.getDbConnection("default", null);

		// 회원통합테이블 관련 수정사항 진행
		// 조회 ----------------------------------------------------------
		String sql = this.getPrcGroundDetailQuery();

		// 입력값 (INPUT)
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, data.getString("JUMIN_NO"));

		rs = pstmt.executeQuery();

		if (rs != null) {
			while (rs.next()) {
				result.addString("MER_NM", rs.getString("MER_NM"));
				result.addString("MEMO_EXPL", rs.getString("MEMO_EXPL"));
				result.addString("MER_TEL_NO", rs.getString("MER_TEL_NO"));
				result.addString("RESULT", "00"); // 정상결과
			}
		}

		if (result.size() < 1) {
			result.addString("RESULT", "01");
		}

	} catch (Throwable t) {
		throw new BaseException(t);
	} finally {
		try {
			if (rs != null)
				rs.close();
		} catch (Exception ignored) {
		}
		try {
			if (pstmt != null)
				pstmt.close();
		} catch (Exception ignored) {
		}
		try {
			if (conn != null)
				conn.close();
		} catch (Exception ignored) {
		}
	}

	return result;
}

//13) 인쇄 횟수 구하기
public int getCupnPrintCnt(WaContext context,String cupn_no ) throws Exception, Throwable{
	Connection         conn = null;
	PreparedStatement pstmt = null;
	ResultSet            rs = null;	
	int cnt		= 10; 

	try{
		StringBuffer sql = new StringBuffer();	

		sql.append("  SELECT CUPN_PRN_NUM FROM BCDBA.TBEVNTUNIFCUPNINFO   \n");
		sql.append("  WHERE  CUPN_NO = ?  \n");  

		conn = context.getDbConnection("default", null);

		pstmt = conn.prepareStatement(sql.toString());
		pstmt.setString(1, cupn_no);

		rs = pstmt.executeQuery();            

		if (rs.next())	{	
			cnt = Integer.parseInt(rs.getString("CUPN_PRN_NUM"));	
			debug("## cnt : "+cnt);
		}				

	}catch(Exception e){
		info("GolfLoungPaymentCancelProc|getServiceCnt Exceptioin : ", e);
		throw e;

	}finally{
		if(rs != null)    try{ rs.close();    }catch(Exception e){}
		if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
		if(conn != null)  try{ conn.close();  }catch(Exception e){}
	}

	return cnt;
}


	/** ***********************************************************************
    * 1)	이벤트기간 체크   
    ************************************************************************ */
	public String getEventDateCheck(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n     SELECT FROM_DATE,TO_DATE ");
		 sql.append("\n       FROM BCDBA.TBEVNTLOTINFO ");
		 sql.append("\n      WHERE SITE_CLSS='10' AND EVNT_NO = 120 ");

		 return sql.toString();
	}

	

	/** ***********************************************************************
    * 2) 마케팅 회원인지 체크 2010.08.30 jklee
    ************************************************************************ */
	public String getIsMkLoginQuery(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n	SELECT *	");
		 sql.append("\n	FROM  BCDBA.TBACRGCDHDLODNTBL A, 	");
		 sql.append("\n ( 		");
		 sql.append("\n	 SELECT JUMIN_NO  	");
		 sql.append("\n	 FROM (	");
		 sql.append("\n	 	  SELECT  JUMIN_NO 	");
		 sql.append("\n		  FROM   BCDBA.TBGGOLFCDHD USERS,  BCDBA.TBGGOLFCDHDGRDMGMT GRD ");
		 sql.append("\n		  WHERE USERS.JUMIN_NO = ? ");
		 sql.append("\n		  AND   USERS.ACRG_CDHD_JONN_DATE BETWEEN ? AND ?  ");
		 sql.append("\n		  AND   NVL(USERS.SECE_YN,'N') = 'N' ");
		 sql.append("\n		  AND   USERS.CDHD_ID =  GRD.CDHD_ID ");
		 sql.append("\n		  AND   GRD.CDHD_CTGO_SEQ_NO IN ('5','6','7','11','17') ");
		 sql.append("\n		  ) CDHD ");
		 sql.append("\n		 GROUP BY  JUMIN_NO ");
		 sql.append("\n		 ) B");
		 sql.append("\n		 WHERE A.RCRU_PL_CLSS ='4004'  ");
		 sql.append("\n		 AND A.JUMIN_NO= ? ");
		 sql.append("\n		 AND A.JUMIN_NO=B.JUMIN_NO ");
		 
		 
		 return sql.toString();
	}
	
	/** ***********************************************************************
	    * 3) 마케팅 회원의 정보 가져오기 2010.09.01 jklee
	    ************************************************************************ */
		public String getMkLoginQuery(){
			 StringBuffer sql = new StringBuffer();
			 sql.append("\n	SELECT  A.JUMIN_NO,A.HG_NM,A.RCRU_PATH_NM,A.CAMP_STRT_DATE, A.CAMP_END_DATE,A.MER_NO, A.MER_NM, A.MER_TEL_NO,A.MEMO_EXPL	");
			 sql.append("\n	FROM  BCDBA.TBACRGCDHDLODNTBL A, 	");
			 sql.append("\n ( 		");
			 sql.append("\n	 SELECT JUMIN_NO  	");
			 sql.append("\n	 FROM (	");
			 sql.append("\n	 	  SELECT  JUMIN_NO 	");
			 sql.append("\n		  FROM   BCDBA.TBGGOLFCDHD USERS,  BCDBA.TBGGOLFCDHDGRDMGMT GRD ");
			 sql.append("\n		  WHERE USERS.JUMIN_NO = ? ");
			 sql.append("\n		  AND   NVL(USERS.SECE_YN,'N') = 'N' ");
			 sql.append("\n		  AND   USERS.CDHD_ID =  GRD.CDHD_ID ");
			 sql.append("\n		  AND   GRD.CDHD_CTGO_SEQ_NO IN ('5','6','7','11','17') ");
			 sql.append("\n		  ) CDHD ");
			 sql.append("\n		 GROUP BY  JUMIN_NO ");
			 sql.append("\n		 ) B");
			 sql.append("\n		 WHERE A.RCRU_PL_CLSS ='4004'  ");
			 sql.append("\n		 AND A.JUMIN_NO= ? ");
			 sql.append("\n		 AND A.JUMIN_NO=B.JUMIN_NO ");
			 
			 
			 return sql.toString();
		}
	
	/** ***********************************************************************
	    *  4) 마케팅 회원의 쿠폰 발급내역가져오기 2010.09.01 jklee
	    ************************************************************************ */
		public String getAppListQuery(){
	        StringBuffer sql = new StringBuffer();
			sql.append("\n 			SELECT SEQ_NO,JUMIN_NO,SITE_CLSS,PWIN_GRD,PWIN_DATE,HG_NM,CARD_NO,CUPN_NO " );
			sql.append("\n 			,(SELECT NVL(CUPN_PRN_NUM,0)  FROM BCDBA.TBEVNTUNIFCUPNINFO WHERE CUPN_NO = A.CUPN_NO ) AS CUPN_PRN_NUM ");
			sql.append("\n 			,(SELECT MER_NM FROM BCDBA.TBACRGCDHDLODNTBL WHERE JUMIN_NO = ? AND RCRU_PL_CLSS = '4004') AS MER_NM ");
			sql.append("\n 			FROM BCDBA.TBEVNTLOTPWIN A WHERE SITE_CLSS ='10' 	");
			sql.append("\n 			AND EVNT_NO =120 AND JUMIN_NO = ? " );
			sql.append("\n 			AND CARD_NO LIKE ?||'%' " );
			sql.append("\n 			AND PROC_YN = '1' ");
			return sql.toString();
	    }
		
	/** ***********************************************************************
	    * 5) 마케팅 회원의 쿠폰 상세보기 가져오기 2010.09.01 jklee
	    ************************************************************************ */
			public String getAppDetailQuery(){
				StringBuffer sql = new StringBuffer();
				sql.append("\n 			SELECT SEQ_NO,JUMIN_NO,SITE_CLSS,PWIN_GRD,PWIN_DATE,HG_NM,CARD_NO,CUPN_NO " );
				sql.append("\n 			FROM BCDBA.TBEVNTLOTPWIN ");
				sql.append("\n			WHERE SEQ_NO = ? 	");
				return sql.toString();
			}
			
	/** ***********************************************************************
	    * 6) 쿠폰번호가져오기 2010.09.01 jklee
	    ************************************************************************ */
		public String getCuponQuery(){
			 StringBuffer sql = new StringBuffer();
			 sql.append("\n	SELECT MIN(CUPN_NO) CUPN_NO FROM BCDBA.TBEVNTUNIFCUPNINFO 	");
			 sql.append("\n	WHERE  SITE_CLSS='10' AND EVNT_NO ='120'  	");
			 sql.append("\n AND CUPN_PYM_YN ='N' 		");
			 sql.append("\n	 AND CUPN_DTL_CTNT LIKE ?||'%' 	");
			 
			 return sql.toString();
		}
	/** ***********************************************************************
    * 8) 마케팅 회원의 쿠폰 발급   
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBEVNTLOTPWIN  (	\n");
		sql.append("\t  SEQ_NO ,SITE_CLSS,EVNT_NO,PWIN_GRD,PWIN_DATE ,JUMIN_NO,HG_NM,PROC_YN, CARD_NO, CUPN_NO)	\n");
		sql.append("\t  VALUES (EVNTLOTCTNT_SEQ.nextval,'10',120, 1 ,TO_CHAR(SYSDATE,'yyyyMMdd'),?,?,1,?,? )	\n");
        return sql.toString();
    }
    /** ***********************************************************************
     * 8) 마케팅 회원의 쿠폰 발급   
     ************************************************************************ */
     private String getCpnCk(){
         StringBuffer sql = new StringBuffer();

 		sql.append("\t SELECT * FROM  	\n");
 		sql.append("\t  BCDBA.TBEVNTLOTPWIN	\n");
 		sql.append("\t  where  JUMIN_NO = ?   and CUPN_NO = ?	\n");
         return sql.toString();
     }
 /** ***********************************************************************
    * 12) 마케팅 회원의 쿠폰 상세보기 가져오기 2010.09.01 jklee
    ************************************************************************ */
			public String getPrcGroundDetailQuery(){
				StringBuffer sql = new StringBuffer();
				sql.append("\n 			select  MER_NM, MEMO_EXPL, MER_TEL_NO " );
				sql.append("\n 			from BCDBA.TBACRGCDHDLODNTBL ");
				sql.append("\n			WHERE JUMIN_NO = ?	");
				sql.append("\n			AND RCRU_PL_CLSS = '4004'	");
				return sql.toString();
			}
     
}
