/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmUNEventInfoProc
*   작성자    : E4NET 은장선
*   내용      : 관리자 부킹 골프장 리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-08-05
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.waf.common.BcUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	E4NET
 * @version	1.0 
 ******************************************************************************/
public class GolfAdmUNEventInfoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmUNEventInfoProc() {}	
	public static final String TITLE = "무료 골프 연습장";
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
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
										
					result.addString("SITE_CLSS" 			,rs.getString("SITE_CLSS") );
					result.addString("EVNT_NO" 				,rs.getString("EVNT_NO") );
					result.addString("EVNT_NM" 				,rs.getString("EVNT_NM") );
					result.addString("TO_DATE"				,DateUtil.format(rs.getString("TO_DATE"),"yyyymmdd","yyyy/mm/dd") );
					result.addString("FROM_DATE" 			,DateUtil.format(rs.getString("FROM_DATE"),"yyyymmdd","yyyy/mm/dd") );
					result.addString("REG_DATE" 			,DateUtil.format(rs.getString("REG_DATE"), "yyyymmdd","yyyy/mm/dd") );	
					
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

	public DbTaoResult detResult(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title		= data.getString("TITLE");
		String evnt_no		= data.getString("evnt_no");		//이벤트번호
		String use_no		= data.getString("use_no");		//이벤트번호
		String sch_state	= data.getString("sch_state");		//검색조건 
		String sch_text		= data.getString("sch_text");       //검색어
		String sch_date_st	= data.getString("sch_date_st");    //시작일
		String sch_date_ed	= data.getString("sch_date_ed");    //종료일

		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		try {
			conn = context.getDbConnection("default", null);						
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectDetQuery(use_no,sch_state,sch_date_st,sch_date_ed);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, evnt_no);
			if(sch_state.equals("name")){
				pstmt.setString(++idx, sch_text);
			}else if(sch_state.equals("socid")){
				pstmt.setString(++idx, sch_text);
			}else if(sch_state.equals("email")){
				pstmt.setString(++idx, sch_text);
			}
			if(!(use_no.equals("") )){
				pstmt.setString(++idx, use_no);
			}
			if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
				pstmt.setString(++idx, sch_date_st.replaceAll("-",""));
				pstmt.setString(++idx, sch_date_ed.replaceAll("-",""));
			}

			pstmt.setLong(++idx, data.getLong("page_no"));
			
			rs	 = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 
				while(rs.next())  {	
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
					result.addString("HG_NM" 			,rs.getString("HG_NM") );
					result.addString("EMAIL" 			,rs.getString("EMAIL") );
					result.addString("JUMIN_NO" 		,BcUtil.getCryptJuminNo(rs.getString("JUMIN_NO"),true) ); 
					result.addString("JUMIN_NO1" 		,rs.getString("JUMIN_NO")); 
					result.addString("CUPN_NO"			,rs.getString("CUPN_NO"));
					result.addString("USE_NO" 			,rs.getString("USE_NO"));
					result.addString("PWIN_DATE" 		,DateUtil.format(rs.getString("PWIN_DATE"),"yyyymmdd","yyyy/mm/dd") );					
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
	
	//엑셀 리스트
public DbTaoResult detExcelResult(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title		= data.getString("TITLE");
		String evnt_no		= data.getString("evnt_no");		//이벤트번호
		String use_no		= data.getString("use_no");		//상품구분
		String sch_state	= data.getString("sch_state");		//검색조건 
		String sch_text		= data.getString("sch_text");       //검색어
		String sch_date_st	= data.getString("sch_date_st");    //시작일
		String sch_date_ed	= data.getString("sch_date_ed");    //종료일

		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);						
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectDetExcelQuery(use_no, sch_state,sch_date_st,sch_date_ed);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, evnt_no);
			
			if(sch_state.equals("name")){
				pstmt.setString(++idx, sch_text);
			}else if(sch_state.equals("socid")){
				pstmt.setString(++idx, sch_text);
			}else if(sch_state.equals("email")){
				pstmt.setString(++idx, sch_text);
			}
			if(!(use_no.equals("") )){
				pstmt.setString(++idx, use_no);
			}
			
			if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
				pstmt.setString(++idx, sch_date_st.replaceAll("-",""));
				pstmt.setString(++idx, sch_date_ed.replaceAll("-",""));
			}
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 
		
				while(rs.next())  {	

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
										
					result.addString("HG_NM" 			,rs.getString("HG_NM") );
					result.addString("EMAIL" 			,rs.getString("EMAIL") );
					result.addString("USE_NO" 			,rs.getString("USE_NO") );
					result.addString("JUMIN_NO" 		,BcUtil.getCryptJuminNo(rs.getString("JUMIN_NO"),true) ); 
					result.addString("JUMIN_NO1" 		,rs.getString("JUMIN_NO")); 
					result.addString("CUPN_NO"			,rs.getString("CUPN_NO"));
					result.addString("PWIN_DATE" 		,DateUtil.format(rs.getString("PWIN_DATE"),"yyyymmdd","yyyy/mm/dd") );					
					
					result.addString("RESULT", "00"); //정상결과 
					debug("@@@@@정상");
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");
				debug("@@@@@실패");
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
	
	//무료 쿠폰 2회 이벤트 상세보기
public DbTaoResult getNochargeView(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title		= data.getString("TITLE");
		String evnt_no		= data.getString("evnt_no");		//이벤트번호
		String sch_state	= data.getString("sch_state");		//검색조건 
		String sch_text		= data.getString("sch_text");       //검색어
		String sch_date_st	= data.getString("sch_date_st");    //시작일
		String sch_date_ed	= data.getString("sch_date_ed");    //종료일
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);						
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectMkDetQuery(sch_state,sch_date_st,sch_date_ed);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, evnt_no);

			if(sch_state.equals("name")){
				pstmt.setString(++idx, sch_text);
			}else if(sch_state.equals("socid")){
				pstmt.setString(++idx, sch_text);
			}
			
			if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
				pstmt.setString(++idx, sch_date_st.replaceAll("-",""));
				pstmt.setString(++idx, sch_date_ed.replaceAll("-",""));
			}

			pstmt.setLong(++idx, data.getLong("page_no"));
			
			rs = pstmt.executeQuery();
 
			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					result.addString("HG_NM" 			,rs.getString("HG_NM") );
					result.addString("EMAIL" 			,rs.getString("EMAIL") );
					result.addString("JUMIN_NO" 		,BcUtil.getCryptJuminNo(rs.getString("JUMIN_NO"),true) ); 
					result.addString("JUMIN_NO1" 		,rs.getString("JUMIN_NO")); 
					result.addString("CUPN_NO"			,rs.getString("CUPN_NO"));
					result.addString("CUPN_PRN_NUM" 	,rs.getString("CUPN_PRN_NUM") );
					result.addString("MER_NM" 	,rs.getString("MER_NM") );
					result.addString("GRADE_NM" 	,rs.getString("GRADE_NM") );
					result.addString("PWIN_DATE" 		,DateUtil.format(rs.getString("PWIN_DATE"),"yyyymmdd","yyyy/mm/dd") );					
					result.addString("RESULT", "00"); //정상결과 
					art_num_no++;
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

//무료 쿠폰 2회 이벤트 상세보기
public DbTaoResult getNochargeExcelView(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title		= data.getString("TITLE");
		String evnt_no		= data.getString("evnt_no");		//이벤트번호
		String sch_state	= data.getString("sch_state");		//검색조건 
		String sch_text		= data.getString("sch_text");       //검색어
		String sch_date_st	= data.getString("sch_date_st");    //시작일
		String sch_date_ed	= data.getString("sch_date_ed");    //종료일
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);						
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectMkDetExcelQuery(sch_state,sch_date_st,sch_date_ed);   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, evnt_no);

			if(sch_state.equals("name")){
				pstmt.setString(++idx, sch_text);
			}else if(sch_state.equals("socid")){
				pstmt.setString(++idx, sch_text);
			}else if(sch_state.equals("email")){
				pstmt.setString(++idx, sch_text); 
			}
			
			if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
				pstmt.setString(++idx, sch_date_st.replaceAll("-",""));
				pstmt.setString(++idx, sch_date_ed.replaceAll("-",""));
			}

			
			rs = pstmt.executeQuery();
 
			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
					result.addString("HG_NM" 			,rs.getString("HG_NM") );
					result.addString("JUMIN_NO" 		,BcUtil.getCryptJuminNo(rs.getString("JUMIN_NO"),true) ); 
					result.addString("JUMIN_NO1" 		,rs.getString("JUMIN_NO")); 
					result.addString("CUPN_NO"			,rs.getString("CUPN_NO"));
					result.addString("CUPN_PRN_NUM" 	,rs.getString("CUPN_PRN_NUM") );
					result.addString("MER_NM" 			,rs.getString("MER_NM") );
					result.addString("GRADE_NM" 		,rs.getString("GRADE_NM") );
					
					result.addString("PWIN_DATE" 		,DateUtil.format(rs.getString("PWIN_DATE"),"yyyymmdd","yyyy/mm/dd") );					
					
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




/**************************************************************
*  무표쿠폰 인쇄횟수 0으로 수정하기
**************************************************************/
public int updatePrintCnt(WaContext context, String cupn_no) throws Exception, Throwable{
	Connection          conn = null;
	PreparedStatement  pstmt = null;

	int cnt		= 0; 

	try{
		StringBuffer sql = new StringBuffer();	

		sql.append(" UPDATE BCDBA.TBEVNTUNIFCUPNINFO    \n");
		sql.append(" SET CUPN_PRN_NUM = 0  \n");
		sql.append(" WHERE CUPN_NO = ?   \n");
		sql.append(" AND site_clss= '10' AND evnt_no= '120' \n"); 

		conn = context.getDbConnection("default", null);
		pstmt = conn.prepareStatement(sql.toString());
		
		pstmt.setString(1, cupn_no);
		cnt = pstmt.executeUpdate(); 

	}catch(Exception e){
		info("GolfLoungPaymentCancelProc|updatePrintCnt Exceptioin : ", e);
		throw e;
	}finally{
		if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
		if(conn != null)  try{ conn.close();  }catch(Exception e){}			
	}
	return cnt;
}

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getAllCpn(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		
		try {
			conn = context.getDbConnection("default", null);						
			 
			//조회 ----------------------------------------------------------
			String sql = this.getAllCpnSql();   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			/*
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			*/
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, data.getString("evnt_no"));	
			pstmt.setLong(++idx, data.getLong("page_no"));

			rs = pstmt.executeQuery();
			int art_num_no = 0;
			String cupn_issu_date = "";
			String cupn_vald_strt_date = "";
			String cupn_vald_end_date = "";
			
			if(rs != null) {			 
				while(rs.next())  {		
					
					if(GolfUtil.empty(rs.getString("CUPN_ISSU_DATE"))){
						cupn_issu_date = "";
					}else{
						cupn_issu_date = DateUtil.format(rs.getString("CUPN_ISSU_DATE"),"yyyymmdd","yyyy/mm/dd");
					}
					
					if(GolfUtil.empty(rs.getString("CUPN_VALD_STRT_DATE"))){
						cupn_vald_strt_date = "";
					}else{
						cupn_vald_strt_date = DateUtil.format(rs.getString("CUPN_VALD_STRT_DATE"),"yyyymmdd","yyyy/mm/dd");
					}
					
					if(GolfUtil.empty(rs.getString("CUPN_VALD_END_DATE"))){
						cupn_vald_end_date = "";
					}else{
						cupn_vald_end_date = DateUtil.format(rs.getString("CUPN_VALD_END_DATE"), "yyyymmdd","yyyy/mm/dd");
					}
					
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );	
					art_num_no++;
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CUPN_NO" 				,rs.getString("CUPN_NO") );
					result.addString("RM"                   ,rs.getString("RM"));
					result.addString("CUPN_ISSU_DATE"		,cupn_issu_date );
					result.addString("CUPN_VALD_STRT_DATE"	,cupn_vald_strt_date );
					result.addString("CUPN_VALD_END_DATE"	,cupn_vald_end_date );	
					
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

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getIssue(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		
		try {
			conn = context.getDbConnection("default", null);						
			 
			//조회 ----------------------------------------------------------
			String sql = this.getIssueSql();   

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());			
			
			pstmt.setString(++idx, data.getString("sch_text"));

			rs = pstmt.executeQuery();
			
			if(rs != null) {			 
				while(rs.next())  {		
					result.addString("RM"			        ,rs.getString("RM") );
					result.addString("CDHD_ID"			    ,rs.getString("CDHD_ID") );
					result.addString("HG_NM" 				,rs.getString("HG_NM") );
					result.addString("STATE"                ,rs.getString("STATE"));
					result.addString("JONN_ATON"		    ,rs.getString("JONN_ATON"));
					result.addString("SECE_ATON"	        ,rs.getString("SECE_ATON"));
					result.addString("JUMIN_NO"	            ,rs.getString("JUMIN_NO"));					
					result.addString("JOIN_CHNL"	        ,rs.getString("JOIN_CHNL"));	
					result.addString("CDHD_CTGO_SEQ_NO"     ,rs.getString("CDHD_CTGO_SEQ_NO"));	
					result.addString("ACCOUNT"	            ,rs.getString("ACCOUNT"));						
					result.addString("EAMIL"			    ,rs.getString("EAMIL") );
					result.addString("DES_JUMIN_NO" 		,BcUtil.getCryptJuminNo(rs.getString("JUMIN_NO"),true) ); 
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
        
		sql.append("\n SELECT	*														    ");
		sql.append("\n FROM (SELECT ROWNUM RNUM											    ");
		sql.append("\n 			    , CEIL(ROWNUM/?) AS PAGE								");
		sql.append("\n 			    , MAX(RNUM) OVER() TOT_CNT								");
		sql.append("\n 			    , ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  			");		
		sql.append("\n 			    , SITE_CLSS, EVNT_NO,EVNT_NM                 			");
		sql.append("\n 			    , TO_DATE ,FROM_DATE  ,REG_DATE                     	");		
		sql.append("\n 		   FROM (SELECT ROWNUM RNUM,SITE_CLSS, EVNT_NO,EVNT_NM          ");
		sql.append("\n                      ,TO_DATE ,FROM_DATE  ,REG_DATE                  ");
		sql.append("\n                 FROM BCDBA.TBEVNTLOTINFO                             ");
		sql.append("\n                WHERE SITE_CLSS='10'                                  ");
		sql.append("\n                ORDER BY REG_DATE                                     ");		
		sql.append("\n 			    )		                                                ");
		sql.append("\n 	      ORDER BY RNUM	                                                ");
		sql.append("\n 	     )				                                                ");		
		sql.append("\n WHERE PAGE = ?	                                                    ");		

		return sql.toString();
    }

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectDetQuery(String use_no,String sch_state,String sch_date_st,String sch_date_ed){
        StringBuffer sql = new StringBuffer();        
        
		sql.append("\n SELECT	*														    ");
		sql.append("\n FROM (SELECT ROWNUM RNUM											    ");
		sql.append("\n 			    , CEIL(ROWNUM/?) AS PAGE								");
		sql.append("\n 			    , MAX(RNUM) OVER() TOT_CNT								");
		sql.append("\n 			    , ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  			");		
		sql.append("\n 			    , HG_NM ,PWIN_DATE                                      ");
		sql.append("\n 			    ,JUMIN_NO,  CUPN_NO, EMAIL,USE_NO                          	");		
		sql.append("\n 		   FROM (SELECT ROWNUM RNUM,HG_NM ,PWIN_DATE                    ");
		sql.append("\n                      ,JUMIN_NO,  CUPN_NO, EMAIL                      ");
		sql.append("\n                      ,CASE USE_NO WHEN '1' THEN '영화 할인권' WHEN '2' THEN 'SK 주유권' ELSE '기타' END AS USE_NO                      ");
		sql.append("\n                 FROM BCDBA.TBEVNTLOTPWIN                             ");
		sql.append("\n                WHERE SITE_CLSS='10'                                  ");
		sql.append("\n                  AND EVNT_NO = ?                                     ");

		if(sch_state.equals("name")){
			sql.append("\n                  AND HG_NM LIKE '%'||?||'%'                         ");
		}else if(sch_state.equals("socid")){
			sql.append("\n                  AND JUMIN_NO LIKE '%'||?||'%'                      ");
		}else if(sch_state.equals("email")){
			sql.append("\n                  AND EMAIL LIKE '%'||?||'%'                         ");
		}
		if(!(use_no.equals("") )){
			sql.append("\n                  AND USE_NO LIKE '%'||?||'%'                 ");
		}
		
		if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
			sql.append("\n                  AND PWIN_DATE BETWEEN ? AND ?                   ");
		}

		sql.append("\n                ORDER BY PWIN_DATE DESC                               ");		
		sql.append("\n 			    )		                                                ");
		sql.append("\n 	      ORDER BY RNUM	                                                ");
		sql.append("\n 	     )				                                                ");		
		sql.append("\n WHERE PAGE = ?	                                                    ");		
 
		return sql.toString();
    }
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectDetExcelQuery(String use_no,String sch_state,String sch_date_st,String sch_date_ed){
         StringBuffer sql = new StringBuffer();        
         
 		sql.append("\n SELECT	*														    ");
 		sql.append("\n FROM (SELECT ROWNUM RNUM											    ");
 		sql.append("\n 			    , CEIL(ROWNUM/?) AS PAGE								");
 		sql.append("\n 			    , MAX(RNUM) OVER() TOT_CNT								");
 		sql.append("\n 			    , ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  			");		
 		sql.append("\n 			    , HG_NM ,PWIN_DATE                                      ");
 		sql.append("\n 			    ,JUMIN_NO,  CUPN_NO, EMAIL ,USE_NO                        	");		
 		sql.append("\n 		   FROM (SELECT ROWNUM RNUM,HG_NM ,PWIN_DATE                    ");
 		sql.append("\n                      ,JUMIN_NO,  CUPN_NO, EMAIL,USE_NO                      ");
 		sql.append("\n                 FROM BCDBA.TBEVNTLOTPWIN                             ");
 		sql.append("\n                WHERE SITE_CLSS='10'                                  ");
 		sql.append("\n                  AND EVNT_NO = ?                                     ");

 		if(sch_state.equals("name")){
 			sql.append("\n                  AND HG_NM LIKE '%'||?||'%'                         ");
 		}else if(sch_state.equals("socid")){
 			sql.append("\n                  AND JUMIN_NO LIKE '%'||?||'%'                      ");
 		}else if(sch_state.equals("email")){
 			sql.append("\n                  AND EMAIL LIKE '%'||?||'%'                         ");
 		}
 		if(!(use_no.equals("") )){
			sql.append("\n                  AND USE_NO  LIKE '%'||?||'%'                   ");
		}
 		if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
 			sql.append("\n                  AND PWIN_DATE BETWEEN ? AND ?                   ");
 		}

 		sql.append("\n                ORDER BY PWIN_DATE DESC                               ");		
 		sql.append("\n 			    )		                                                ");
 		sql.append("\n 	      ORDER BY RNUM	                                                ");
 		sql.append("\n 	     )				                                                ");		
  
 		return sql.toString();
     }
    
    /** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectMkDetQuery(String sch_state,String sch_date_st,String sch_date_ed){
         StringBuffer sql = new StringBuffer();        
         
 		sql.append("\n SELECT	*														    ");
 		sql.append("\n FROM (SELECT ROWNUM RNUM											    ");
 		sql.append("\n 			    , CEIL(ROWNUM/?) AS PAGE								");
 		sql.append("\n 			    , MAX(RNUM) OVER() TOT_CNT								");
 		sql.append("\n 			    , ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  			");		
 		sql.append("\n 			    , HG_NM ,PWIN_DATE                                      ");
 		sql.append("\n 			    ,JUMIN_NO,  CUPN_NO, EMAIL ,CUPN_PRN_NUM ,MER_NM,GRADE_NM       	");		
 		sql.append("\n 		   FROM (SELECT ROWNUM RNUM,A.HG_NM ,A.PWIN_DATE,E.MER_NM AS MER_NM,H.GOLF_CMMN_CODE_NM AS GRADE_NM       ");
 		sql.append("\n                  ,A.JUMIN_NO,  A.CUPN_NO, A.EMAIL , B.CUPN_PRN_NUM AS CUPN_PRN_NUM                      ");
 		sql.append("\n                 FROM BCDBA.TBEVNTLOTPWIN A , BCDBA.TBEVNTUNIFCUPNINFO B ,                            ");
 		sql.append("\n                      ( SELECT D.MER_NM AS MER_NM, C.CUPN_NO AS CUPN_NO FROM BCDBA.TBEVNTLOTPWIN C, BCDBA.TBACRGCDHDLODNTBL D  ");
 		sql.append("\n                          WHERE C.JUMIN_NO = D.JUMIN_NO                   ");
 		sql.append("\n                          AND C.EVNT_NO = '120'  AND C.SITE_CLSS='10' AND D.RCRU_PL_CLSS = '4004'  ) E,                   ");
 		sql.append("\n                      ( SELECT  F.GOLF_CMMN_CODE_NM,JUMIN_NO  FROM BCDBA.TBGCMMNCODE F ,  BCDBA.TBGGOLFCDHD G  ");
 		sql.append("\n                        WHERE GOLF_CMMN_CLSS='0052' AND G.CDHD_CTGO_SEQ_NO = GOLF_CMMN_CODE )H                    ");
 		sql.append("\n                WHERE A.CUPN_NO = B.CUPN_NO                           ");
 		sql.append("\n                  AND A.CUPN_NO = E.CUPN_NO               ");
 		sql.append("\n                  AND A.JUMIN_NO = H.JUMIN_NO               ");
 		sql.append("\n                  AND A.EVNT_NO = ?  AND A.SITE_CLSS='10'               ");

 		if(sch_state.equals("name")){
 			sql.append("\n                  AND HG_NM LIKE '%'||?||'%'                         ");
 		}else if(sch_state.equals("socid")){
 			sql.append("\n                  AND JUMIN_NO LIKE '%'||?||'%'                      ");
 		}
 		
 		if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
 			sql.append("\n                  AND PWIN_DATE BETWEEN ? AND ?                   ");
 		}

 		sql.append("\n                ORDER BY PWIN_DATE DESC                               ");		
 		sql.append("\n 			    )		                                                ");
 		sql.append("\n 	      ORDER BY RNUM	                                                ");
 		sql.append("\n 	     )				                                                ");		
 		sql.append("\n WHERE PAGE = ?	                                                    ");		
  
 		return sql.toString();
     }
     
     /** ***********************************************************************
      * Query를 생성하여 리턴한다.    
      ************************************************************************ */
      private String getSelectMkDetExcelQuery(String sch_state,String sch_date_st,String sch_date_ed){
          StringBuffer sql = new StringBuffer();        
          
  		sql.append("\n SELECT	*														    ");
  		sql.append("\n FROM (SELECT ROWNUM RNUM											    ");
  		sql.append("\n 			    , CEIL(ROWNUM/?) AS PAGE								");
  		sql.append("\n 			    , MAX(RNUM) OVER() TOT_CNT								");
  		sql.append("\n 			    , ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  			");		
  		sql.append("\n 			    , HG_NM ,PWIN_DATE                                      ");
  		sql.append("\n 			    ,JUMIN_NO,  CUPN_NO, CUPN_PRN_NUM ,MER_NM,GRADE_NM       	");		
  		sql.append("\n 		   FROM (SELECT ROWNUM RNUM,A.HG_NM ,A.PWIN_DATE,E.MER_NM AS MER_NM,H.GOLF_CMMN_CODE_NM AS GRADE_NM       ");
  		sql.append("\n                  ,A.JUMIN_NO,  A.CUPN_NO, B.CUPN_PRN_NUM AS CUPN_PRN_NUM                      ");
  		sql.append("\n                 FROM BCDBA.TBEVNTLOTPWIN A , BCDBA.TBEVNTUNIFCUPNINFO B ,                            ");
  		sql.append("\n                      ( SELECT D.MER_NM AS MER_NM, C.CUPN_NO AS CUPN_NO FROM BCDBA.TBEVNTLOTPWIN C, BCDBA.TBACRGCDHDLODNTBL D  ");
  		sql.append("\n                          WHERE C.JUMIN_NO = D.JUMIN_NO                   ");
  		sql.append("\n                          AND C.EVNT_NO = '120'  AND C.SITE_CLSS='10' AND D.RCRU_PL_CLSS = '4004'  ) E,                   ");
  		sql.append("\n                      ( SELECT  F.GOLF_CMMN_CODE_NM,JUMIN_NO  FROM BCDBA.TBGCMMNCODE F ,  BCDBA.TBGGOLFCDHD G ");
  		sql.append("\n                        WHERE GOLF_CMMN_CLSS='0052' AND G.CDHD_CTGO_SEQ_NO = GOLF_CMMN_CODE )H                    ");
  		sql.append("\n                WHERE A.CUPN_NO = B.CUPN_NO                           ");
  		sql.append("\n                  AND A.CUPN_NO = E.CUPN_NO               ");
  		sql.append("\n                  AND A.JUMIN_NO = H.JUMIN_NO               ");
  		sql.append("\n                  AND A.EVNT_NO = ?  AND A.SITE_CLSS='10'               ");

  		if(sch_state.equals("name")){
  			sql.append("\n                  AND HG_NM LIKE '%'||?||'%'                         ");
  		}else if(sch_state.equals("socid")){
  			sql.append("\n                  AND JUMIN_NO LIKE '%'||?||'%'                      ");
  		}else if(sch_state.equals("email")){
  			sql.append("\n                  AND EMAIL LIKE '%'||?||'%'                         ");
  		}
  		
  		if(!(sch_date_st.equals("") || sch_date_ed.equals(""))){
  			sql.append("\n                  AND PWIN_DATE BETWEEN ? AND ?                   ");
  		}

  		sql.append("\n                ORDER BY PWIN_DATE DESC                               ");		
  		sql.append("\n 			    )		                                                ");
  		sql.append("\n 	      ORDER BY RNUM	                                                ");
  		sql.append("\n 	     )				                                                ");		
   
  		return sql.toString();
      }
	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getAllCpnSql(){
		 StringBuffer sql = new StringBuffer();
		 sql.append("\n SELECT	*															                          ");
		 sql.append("\n   FROM (SELECT  MAX(RM) OVER() TOT_CNT ,CUPN_NO, CUPN_ISSU_DATE, CUPN_VALD_STRT_DATE,RM       ");
		 sql.append("\n 			       , CEIL(RM/?) AS PAGE,CUPN_VALD_END_DATE                                    ");
		 sql.append("\n 		           , ((MAX(RM) OVER())-(?-1)*10) AS ART_NUM  			                      ");		
		 sql.append("\n           FROM   (SELECT  CUPN_NO, CUPN_ISSU_DATE,ROWNUM RM,				                  ");
		 sql.append("\n                           CUPN_VALD_STRT_DATE, CUPN_VALD_END_DATE                             ");
		 sql.append("\n                     FROM  BCDBA.TBEVNTUNIFCUPNINFO                                            ");
		 sql.append("\n                    WHERE  SITE_CLSS ='10' AND EVNT_NO = ? AND CUPN_PYM_YN='N')                ");
		 sql.append("\n 	      ORDER BY RM	                                                                      ");
		 sql.append("\n 	     )				                                                                      ");		
		 sql.append("\n WHERE PAGE = ?	                                                                              ");		

		 return sql.toString();
	
	}

	/*************************************************************************
    * Query를 생성하여 리턴한다.    
    *************************************************************************/
	private String getIssueSql(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n     SELECT CDHD_ID,HG_NM, CASE SECE_YN WHEN 'Y' THEN '탈퇴' ELSE '정상' END AS STATE,  ");
		sql.append("\n            TO_CHAR(TO_DATE(SUBSTR(JONN_ATON,1,8)),'YYYY-MM-DD') JONN_ATON,             ");
		sql.append("\n            TO_CHAR(TO_DATE(SUBSTR(SECE_ATON,1,8)),'YYYY-MM-DD') SECE_ATON,             ");
		sql.append("\n            JUMIN_NO, JOIN_CHNL,ROWNUM RM,                                              ");
		sql.append("\n            (SELECT MIN(CDHD_CTGO_SEQ_NO)                                               ");
		sql.append("\n               FROM   BCDBA.TBGGOLFCDHDGRDMGMT                                          ");
		sql.append("\n              WHERE CDHD_ID=A.CDHD_ID ) CDHD_CTGO_SEQ_NO ,                              ");
		sql.append("\n            (SELECT ACCOUNT FROM   UCUSRINFO  WHERE ACCOUNT=A.CDHD_ID ) ACCOUNT,         ");
		sql.append("\n            (SELECT EMAIL1 FROM   UCUSRINFO  WHERE ACCOUNT=A.CDHD_ID ) EAMIL            ");
		sql.append("\n       FROM BCDBA.TBGGOLFCDHD A                                                         ");
		sql.append("\n      WHERE  HG_NM LIKE '%'||?||'%'                                                     ");

		return sql.toString();
	}
}	 
	 
