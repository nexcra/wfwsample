/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntMngListDaoProc.java
*   작성자    : 이정규
*   내용      : 관리자 > 이벤트 > 월례회 > 월례회 관리
*   적용범위  : golf
*   작성일자  : 2010-09-30
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.benest;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
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
import com.bccard.waf.tao.TaoException;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0 
 ******************************************************************************/
public class GolfAdmEvntMngListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmOrdListDaoProc 프로세스 생성자   
	 * @param N/A
	 ***************************************************************** */ 
	public GolfAdmEvntMngListDaoProc() {}	
 
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
			
			// 검색값
			String sch_type				= data.getString("sch_type");			//이벤트/게시 일자 구분
			String sch_date_st			= data.getString("sch_date_st");		//시작날짜
			String sch_date_ed			= data.getString("sch_date_ed");		//마감날짜
			
			String sch_green_nm			= data.getString("sch_green_nm");		//골프장명
			
			String sql = this.getSelectQuery(data);

			// 입력값 (INPUT) 
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, 20);
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			if(!GolfUtil.empty(sch_type)){
				if(sch_type.equals("evnt")){
					if(!GolfUtil.empty(sch_date_st))	pstmt.setString(++idx, sch_date_st+"000000");
					if(!GolfUtil.empty(sch_date_ed))	pstmt.setString(++idx, sch_date_ed+"999999");
				}else if(sch_type.equals("bltn")){
					if(!GolfUtil.empty(sch_date_st))	pstmt.setString(++idx, sch_date_st);
					if(!GolfUtil.empty(sch_date_ed))	pstmt.setString(++idx, sch_date_ed);
				}
				if(!GolfUtil.empty(sch_green_nm)){
						pstmt.setString(++idx, "%"+sch_green_nm+"%");
				}
			}
			
			pstmt.setLong(++idx, data.getLong("page_no"));
						
			rs = pstmt.executeQuery();
			
			String cpo_amt = "";
			String acrg_cdhd_amt = "";
			String free_cdhd_amt = "";
			
			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
				        
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					art_num_no++;
					
					if(!GolfUtil.empty(rs.getString("CPO_AMT"))){
						cpo_amt = GolfUtil.comma(rs.getString("CPO_AMT"));
					}else{
						cpo_amt = "";
					}
					if(!GolfUtil.empty(rs.getString("ACRG_CDHD_AMT"))){
						acrg_cdhd_amt = GolfUtil.comma(rs.getString("ACRG_CDHD_AMT"));
					}else{
						acrg_cdhd_amt = "";
					}
					if(!GolfUtil.empty(rs.getString("FREE_CDHD_AMT"))){
						free_cdhd_amt = GolfUtil.comma(rs.getString("FREE_CDHD_AMT"));
					}else{
						free_cdhd_amt = "";
					}
										
					result.addString("SEQ_NO" 			,rs.getString("SEQ_NO") );
					result.addString("GREEN_NM"			,rs.getString("GREEN_NM") );
					result.addString("EVNT_STRT_DATE" 	,rs.getString("EVNT_STRT_DATE") );
					result.addString("EVNT_END_DATE" 	,rs.getString("EVNT_END_DATE") );
					result.addString("BLTN_STRT_DATE" 	,rs.getString("BLTN_STRT_DATE") );
					result.addString("BLTN_END_DATE" 	,rs.getString("BLTN_END_DATE") );
					
					result.addString("CPO_AMT" 			,cpo_amt );
					result.addString("ACRG_CDHD_AMT" 	,acrg_cdhd_amt );
					result.addString("FREE_CDHD_AMT" 	,free_cdhd_amt );
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
	 * 월례회 상세보기.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	
	public DbTaoResult execute_view(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값

			String seq_no		= data.getString("seq_no");
			String sql = this.getSelectViewQuery();

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, seq_no);
			rs = pstmt.executeQuery();
			
			String sttl_amt = "";
			String green_nm = "";
			String cpo_amt = "";
			String acrg_cdhd_amt = "";
			String free_cdhd_amt = "";
			String img_src = "";
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					if(!GolfUtil.empty(rs.getString("CPO_AMT"))){
						cpo_amt = GolfUtil.comma(rs.getString("CPO_AMT"));
					}else{
						cpo_amt = "";
					}
					if(!GolfUtil.empty(rs.getString("ACRG_CDHD_AMT"))){
						acrg_cdhd_amt = GolfUtil.comma(rs.getString("ACRG_CDHD_AMT"));
					}else{
						acrg_cdhd_amt = "";
					}
					if(!GolfUtil.empty(rs.getString("FREE_CDHD_AMT"))){
						free_cdhd_amt = GolfUtil.comma(rs.getString("FREE_CDHD_AMT"));
					}else{
						free_cdhd_amt = "";
					}
					
					result.addString("SEQ_NO" 				,rs.getString("SEQ_NO") );
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					
					result.addString("EVNT_STRT_DATE" 		,rs.getString("EVNT_STRT_DATE") );
					result.addString("EVNT_END_DATE" 		,rs.getString("EVNT_END_DATE") );
					result.addString("BLTN_STRT_DATE" 		,rs.getString("BLTN_STRT_DATE") );
					result.addString("BLTN_END_DATE" 		,rs.getString("BLTN_END_DATE") );
					result.addString("CPO_AMT" 				,cpo_amt );
					result.addString("ACRG_CDHD_AMT" 		,acrg_cdhd_amt );
					result.addString("FREE_CDHD_AMT" 		,free_cdhd_amt );
					img_src = "http://www.golfloung.com/golf/"+rs.getString("TITL_IMG");
					//타이틀 이미지 경로
					result.addString("TITL_IMG" 			,img_src);
					result.addString("TITL_IMG2" 			,rs.getString("TITL_IMG"));
					//이벤트 혜택
					result.addString("EVNT_BNFT_EXPL__" 		,rs.getString("EVNT_BNFT_EXPL") );
					
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("EVNT_BNFT_EXPL");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("EVNT_BNFT_EXPL", bufferSt.toString());
					
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
	 * 해당 월례회의 날짜 리스트 가져오기
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_datelist(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);	
			
			// 검색값

			String seq_no		= data.getString("seq_no");
			String sql = this.getDateListQuery();

			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, seq_no);
						
			rs = pstmt.executeQuery();

			if(rs != null) {			 

				while(rs.next())  {
					result.addString("REG_DATE" 				,rs.getString("REG_DATE") );
					result.addString("REG_DATE_INDC_INFO" 				,rs.getString("REG_DATE_INDC_INFO") );
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
	 * 월례회 수정 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute_update(WaContext context, TaoDataSet data) throws DbTaoException  {
		String title = "";
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false); 
			String titl_img = data.getString("TITL_IMG");
			
			
            /*****************************************************************************/
            String seq_no = data.getString("SEQ_NO");
			String bltn_strt_date = data.getString("BLTN_STRT_DATE");
            String bltn_end_date = data.getString("BLTN_END_DATE");
            String evnt_strt_date = data.getString("EVNT_STRT_DATE");
            String evnt_end_date = data.getString("EVNT_END_DATE");
            String cpo_amt = data.getString("CPO_AMT"); 
            String acrg_cdhd_amt = data.getString("ACRG_CDHD_AMT");
            String free_cdhd_amt = data.getString("FREE_CDHD_AMT");
            String evnt_bnft_expl = data.getString("EVNT_BNFT_EXPL");
            debug("@@ : "+evnt_bnft_expl);
			sql = this.getUpdateQuery(seq_no, bltn_strt_date, bltn_end_date, evnt_strt_date, evnt_end_date, cpo_amt, acrg_cdhd_amt, free_cdhd_amt, titl_img , evnt_bnft_expl );//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			if (!GolfUtil.isNull(titl_img))	pstmt.setString(++idx, titl_img);
			pstmt.setString(++idx, bltn_strt_date ); 
			pstmt.setString(++idx, bltn_end_date );
			pstmt.setString(++idx, evnt_strt_date );
			pstmt.setString(++idx, evnt_end_date );
			pstmt.setString(++idx, cpo_amt );
			pstmt.setString(++idx, acrg_cdhd_amt );
			pstmt.setString(++idx, free_cdhd_amt );
			pstmt.setString(++idx, evnt_bnft_expl );
			pstmt.setString(++idx, seq_no );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            /*sql = this.getSelectForUpdateQuery();//번호 쿼리
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("SEQ_NO"));
            rs = pstmt.executeQuery();

			if(rs.next()) {
				java.sql.Clob clob = rs.getClob("EVNT_BNFT_EXPL");
                //writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
				reader = new CharArrayReader(data.getString("EVNT_BNFT_EXPL").toCharArray()); 
				
				char[] buffer = new char[1024];
				int read = 0;
				while ((read = reader.read(buffer,0,1024)) != -1) {
					writer.write(buffer,0,read);
				}
				writer.flush();
			}*/
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, "월례회 수정 처리", "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	/**
	 * 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int excute_insert(WaContext context, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		Writer writer = null;
		Reader reader = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

            /**SEQ_NO 가져오기**************************************************************/
			sql = this.getNextValQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String max_SEQ_NO = "";
			if(rs.next()){
				max_SEQ_NO = rs.getString("SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql);
			
			String green_nm = data.getString("GREEN_NM");
			String bltn_strt_date = data.getString("BLTN_STRT_DATE");
            String bltn_end_date = data.getString("BLTN_END_DATE");
            String evnt_strt_date = data.getString("EVNT_STRT_DATE");
            String evnt_end_date = data.getString("EVNT_END_DATE");
            String cpo_amt = data.getString("CPO_AMT"); 
            String acrg_cdhd_amt = data.getString("ACRG_CDHD_AMT");
            String free_cdhd_amt = data.getString("FREE_CDHD_AMT");
			String titl_img = data.getString("TITL_IMG");
            String evnt_bnft_expl = data.getString("EVNT_BNFT_EXPL");
			//debug("pstmt => " + pstmt);
			int idx = 0;
        	pstmt.setString(++idx, max_SEQ_NO );
        	pstmt.setString(++idx, green_nm );
        	pstmt.setString(++idx, bltn_strt_date ); 
			pstmt.setString(++idx, bltn_end_date );
			pstmt.setString(++idx, evnt_strt_date );
			pstmt.setString(++idx, evnt_end_date );
			pstmt.setString(++idx, cpo_amt );
			pstmt.setString(++idx, acrg_cdhd_amt );
			pstmt.setString(++idx, free_cdhd_amt );
			pstmt.setString(++idx, titl_img );
			pstmt.setString(++idx, evnt_bnft_expl );
        	
        	result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            
          /*  sql = this.getSelectForUpdateQuery();//레슨번호 쿼리
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, max_SEQ_NO);
            rs = pstmt.executeQuery();
            String expl = data.getString("EVNT_BNFT_EXPL");
            
			if(rs.next()) {
				
				java.sql.Clob clob = rs.getClob("EVNT_BNFT_EXPL");
				writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
				reader = new CharArrayReader(expl.toCharArray());
				
				char[] buffer = new char[1024];
				int read = 0;
				while ((read = reader.read(buffer,0,1024)) != -1) {
					writer.write(buffer,0,read);
				}
				writer.flush(); 
			}*/
            
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, "월례회 등록 처리", "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	/**
	 * 신청 날짜 저장하기
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int excute_app_insert(WaContext context, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		Writer writer = null;
		Reader reader = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

            /**SEQ_NO 가져오기**************************************************************/
			sql = this.getMaxValQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String max_SEQ_NO = "";
			if(rs.next()){
				max_SEQ_NO = rs.getString("SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getAppInsertQuery();
			pstmt = conn.prepareStatement(sql);
			String reg_date = data.getString("REG_DATE");
            String green_nm = data.getString("GREEN_NM");
            String reg_date_indc_info = data.getString("REG_DATE_INDC_INFO");
            
			int idx = 0;
        	pstmt.setString(++idx, max_SEQ_NO );
        	pstmt.setString(++idx, reg_date.replaceAll("-",""));
			pstmt.setString(++idx, green_nm );
			pstmt.setString(++idx, reg_date_indc_info );
			
        	result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, "월례회 등록 처리", "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
	/**
	 * 신청 날짜 삭제하기
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute_app_delete(WaContext context, TaoDataSet data ) throws Exception, Throwable{
		Connection          conn = null;
		PreparedStatement  pstmt = null;

		int cnt		= 0; 

		try{
			StringBuffer sql = new StringBuffer();	

			sql.append(" DELETE FROM BCDBA.TBGGREENEVNTSCD  \n");
			sql.append(" WHERE   SEQ_NO = ?  \n");

			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(1, data.getString("SEQ_NO"));
			cnt = pstmt.executeUpdate(); 

		}catch(Exception e){
			info("execute_app_delete : ", e);
			throw e;
		}finally{
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(conn != null)  try{ conn.close();  }catch(Exception e){}			
		}
		return cnt;
	}
	
	/**
	 * 신청 날짜 업데이트(실제로는 지우고 추가)
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int excute_app_update(WaContext context, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		Writer writer = null;
		Reader reader = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

            /**SEQ_NO 가져오기**************************************************************
			sql = this.getMaxValQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String max_SEQ_NO = "";
			if(rs.next()){
				max_SEQ_NO = rs.getString("SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getAppInsertQuery();
			pstmt = conn.prepareStatement(sql);
			String max_seq_no = data.getString("SEQ_NO");
			String reg_date = data.getString("REG_DATE");
            String green_nm = data.getString("GREEN_NM");
            String reg_date_indc_info = data.getString("REG_DATE_INDC_INFO");
			//debug("pstmt => " + pstmt);
            
			int idx = 0;
        	pstmt.setString(++idx, max_seq_no );
        	pstmt.setString(++idx, reg_date.replaceAll("-",""));
			pstmt.setString(++idx, green_nm );
			pstmt.setString(++idx, reg_date_indc_info );
			
        	result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, "월례회 등록 처리", "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	/*
	 * QUERY START
	 */
	
	
	
	/**
	 * 월례회 가격정보
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	
	public DbTaoResult get_cost(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);
		String cpo_amt = "";
		String acrg_cdhd_amt = "";
		String free_cdhd_amt = "";
		try {
			conn = context.getDbConnection("default", null);
						 
			String seq_no		= data.getString("trm_unt");
			String sql = this.getCostQuery();

			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, seq_no);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {
					if(!GolfUtil.empty(rs.getString("CPO_AMT"))){
						cpo_amt = GolfUtil.comma(rs.getString("CPO_AMT"));
					}else{
						cpo_amt = "";
					}
					if(!GolfUtil.empty(rs.getString("ACRG_CDHD_AMT"))){
						acrg_cdhd_amt = GolfUtil.comma(rs.getString("ACRG_CDHD_AMT"));
					}else{
						acrg_cdhd_amt = "";
					}
					if(!GolfUtil.empty(rs.getString("FREE_CDHD_AMT"))){
						free_cdhd_amt = GolfUtil.comma(rs.getString("FREE_CDHD_AMT"));
					}else{
						free_cdhd_amt = "";
					}
							
					result.addString("CPO_AMT" 				,cpo_amt );
					result.addString("ACRG_CDHD_AMT" 		,acrg_cdhd_amt );
					result.addString("FREE_CDHD_AMT" 		,free_cdhd_amt );
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
    * 구매 리스트
	 * @throws TaoException 
    ************************************************************************ */
    private String getSelectQuery(TaoDataSet data) throws TaoException{
        StringBuffer sql = new StringBuffer();
        
        String sch_type				= data.getString("sch_type");			//이벤트/게시 일자 구분
		String sch_date_st			= data.getString("sch_date_st");
		String sch_date_ed			= data.getString("sch_date_ed");	
		String sch_green_nm			= data.getString("sch_green_nm");
        
		sql.append("\n	SELECT *	\n");
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE, MAX(RNUM) OVER() TOT_CNT, ((MAX(RNUM) OVER())-(?-1)*20) AS ART_NUM	\n");
		sql.append("\t	        , SEQ_NO, GREEN_NM,BLTN_STRT_DATE, BLTN_END_DATE, EVNT_STRT_DATE, EVNT_END_DATE,CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t	        SELECT ROWNUM RNUM	\n");
		
		sql.append("\t	        , SEQ_NO, GREEN_NM,TO_CHAR(TO_DATE(BLTN_STRT_DATE),'YYYY-MM-DD') BLTN_STRT_DATE, TO_CHAR(TO_DATE(BLTN_END_DATE),'YYYY-MM-DD') BLTN_END_DATE,TO_CHAR(TO_DATE(EVNT_STRT_DATE),'YYYY-MM-DD') EVNT_STRT_DATE,TO_CHAR(TO_DATE(EVNT_END_DATE),'YYYY-MM-DD') EVNT_END_DATE	\n");
		sql.append("\t	       	, CPO_AMT, ACRG_CDHD_AMT,FREE_CDHD_AMT	\n");
		sql.append("\t	       	FROM BCDBA.TBGMMLYMTNG 	\n");
		//sql.append("\t	       	LEFT JOIN BCDBA.TBGGREENEVNTSCD T2 ON SEQ_NO=T2.SEQ_NO	\n");
		sql.append("\t	       	WHERE GOLF_SVC_APLC_CLSS = '9003'	\n");

		if(sch_type.equals("evnt")){
			if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND EVNT_STRT_DATE >= ?	\n");
			if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND EVNT_END_DATE <= ?		\n");
		}else if(sch_type.equals("bltn")){
			if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND BLTN_STRT_DATE >= ?	\n");
			if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND BLTN_END_DATE <= ?		\n");
		}

		if(!GolfUtil.empty(sch_green_nm) && !GolfUtil.empty(sch_green_nm)){
				sql.append("\t	AND (GREEN_NM LIKE '%'||?||'%' )	\n");
		}
		
		sql.append("\t	        ORDER BY SEQ_NO DESC	\n");
		sql.append("\t	    )	\n");
		sql.append("\t	    ORDER BY RNUM	\n");
		sql.append("\t	)	\n");
		sql.append("\t	WHERE PAGE=?	\n");
		
		return sql.toString();
    }

	/** ***********************************************************************
    *  월례회 상세보기
    ************************************************************************ */
    public String getSelectViewQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n 			SELECT SEQ_NO, GREEN_NM, TO_CHAR(TO_DATE(BLTN_STRT_DATE),'YYYY-MM-DD') BLTN_STRT_DATE, TO_CHAR(TO_DATE(BLTN_END_DATE),'YYYY-MM-DD') BLTN_END_DATE,TO_CHAR(TO_DATE(EVNT_STRT_DATE),'YYYY-MM-DD') EVNT_STRT_DATE,TO_CHAR(TO_DATE(EVNT_END_DATE),'YYYY-MM-DD') EVNT_END_DATE, CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT,EVNT_BNFT_EXPL,TITL_IMG " );
		sql.append("\n 			FROM BCDBA.TBGMMLYMTNG ");
		sql.append("\n			WHERE SEQ_NO = ? 	");
		return sql.toString();
	}
    
    /** ***********************************************************************
     *  가격정보 가져오기
     ************************************************************************ */
     public String getCostQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n 			SELECT CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT " );
 		sql.append("\n 			FROM BCDBA.TBGMMLYMTNG ");
 		sql.append("\n			WHERE SEQ_NO = ? 	");
 		return sql.toString();
 	}
    
    
	/** ***********************************************************************
     * 해당 월례회의 참가날짜 가져오기
     ************************************************************************ */
    public String getDateListQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n 			SELECT TO_CHAR(TO_DATE(REG_DATE),'YYYY-MM-DD') REG_DATE ,  REG_DATE_INDC_INFO " );
		sql.append("\n 			FROM BCDBA.TBGGREENEVNTSCD ");
		sql.append("\n 			WHERE SEQ_NO = ? ");
		sql.append("\n 			ORDER BY REG_DATE ");
		return sql.toString();
    }
    
    /** ***********************************************************************
     * 월례회 정보를 업데이트 한다.    
     ************************************************************************ */
     private String getUpdateQuery(String seq_no, String bltn_strt_date, String bltn_end_date, String evnt_strt_date, String evnt_end_date, String cpo_amt, String acrg_cdhd_amt, String free_cdhd_amt, String titl_img , String evnt_bnft_expl){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("UPDATE BCDBA.TBGMMLYMTNG SET	\n");
 		
 		if (!GolfUtil.isNull(titl_img)) sql.append("\t 	 TITL_IMG=?,	");
 		
 		sql.append("\t  BLTN_STRT_DATE=?, BLTN_END_DATE=?, EVNT_STRT_DATE=?, EVNT_END_DATE=?, CPO_AMT=?, ACRG_CDHD_AMT=?, FREE_CDHD_AMT=?, 	\n");
 		sql.append("\t  EVNT_BNFT_EXPL=? 	\n");
 		sql.append("\t WHERE SEQ_NO=?	\n");
         return sql.toString();
     }
     
     /** ***********************************************************************
      * Max IDX Query를 생성하여 리턴한다.    
      ************************************************************************ */
      
      private String getNextValQuery(){
          StringBuffer sql = new StringBuffer();
          sql.append("SELECT TO_NUMBER(MAX(SEQ_NO)+1) SEQ_NO FROM BCDBA.TBGMMLYMTNG \n");
          sql.append("WHERE GOLF_SVC_APLC_CLSS = '9003' \n");
  		return sql.toString();
      }
      
      
      private String getMaxValQuery(){
          StringBuffer sql = new StringBuffer();
          sql.append("SELECT TO_NUMBER(MAX(SEQ_NO)) SEQ_NO FROM BCDBA.TBGMMLYMTNG \n");
          sql.append("WHERE GOLF_SVC_APLC_CLSS = '9003' \n");
  		return sql.toString();
      }
      
      
      /** ***********************************************************************
       * 월례회 정보 추가    
       ************************************************************************ */
       private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
   		sql.append("\n");
   		sql.append("INSERT INTO BCDBA.TBGMMLYMTNG (	\n");
   		sql.append("\t  SEQ_NO, GREEN_NM, BLTN_STRT_DATE, BLTN_END_DATE, EVNT_STRT_DATE 	\n");
   		sql.append("\t  ,EVNT_END_DATE, CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT \n");
   		sql.append("\t	,TITL_IMG, EVNT_BNFT_EXPL,GOLF_SVC_APLC_CLSS	 		\n");
   		sql.append("\t ) VALUES (	\n");	
   		sql.append("\t  ?,?,?,?,? \n"); 
		sql.append("\t  ,?,?,?,?,? \n");
   		sql.append("\t  ,?,'9003'	\n");
   		sql.append("\t  \n)");	
           return sql.toString();
       }
       
       /** ***********************************************************************
        * 신청 날짜 추가 
        ************************************************************************ */
        private String getAppInsertQuery(){
         StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("INSERT INTO BCDBA.TBGGREENEVNTSCD (	\n");
    		sql.append("\t  SEQ_NO, GOLF_SVC_APLC_CLSS, REG_DATE, GREEN_NM, REG_DATE_INDC_INFO 	\n");
    		sql.append("\t ) VALUES (	\n");	
    		sql.append("\t  ?,'9003',?,?,? \n"); 
    		sql.append("\t  \n)");	
            return sql.toString();
        }
        

        /** ***********************************************************************
         * 기존 날짜 삭제 
         ************************************************************************ */
         private String getAppDeleteQuery(){
          StringBuffer sql = new StringBuffer();
     		sql.append("\n");
     		sql.append(" DELETE FROM BCDBA.TBGGREENEVNTSCD 	\n");
     		sql.append("\t  WHERE   SEQ_NO = ? 	\n");
             return sql.toString();
         }
        
       
       /** ***********************************************************************
        * CLOB Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectForUpdateQuery(){
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT EVNT_BNFT_EXPL FROM BCDBA.TBGMMLYMTNG \n");
            sql.append("WHERE SEQ_NO = ? \n");
            sql.append("FOR UPDATE \n");
    		return sql.toString();
        }
     
     
}
