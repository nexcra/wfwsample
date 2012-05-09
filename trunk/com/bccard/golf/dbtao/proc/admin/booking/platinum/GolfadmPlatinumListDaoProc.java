/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmPlatinumListDaoProc
*   작성자    : 이정규
*   내용      : 관리자> 부킹> 플래티늄 부킹 대행
*   적용범위  : golf
*   작성일자  : 2010-09-13
************************** 수정이력 ****************************************************************
*    일자  작성자   변경사항
* 20110329 이경희  플래티늄 부킹 대행 취소시 업데이트 항목 수정
* 20110523 이경희  플래티늄부킹대행 삭제기능추가 
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.platinum;

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
 * @author	미디어포스    
 * @version	1.0 
 ******************************************************************************/
public class GolfadmPlatinumListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmPreTimeListDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfadmPlatinumListDaoProc() {}	
 
	/** 
	 * Proc 실행 List.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult 
	 */
	public static final String TITLE = "관리자 프리미엄부킹 리스트";
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String sch_Type				= data.getString("SCH_TYPE");		//날짜검색
			String sch_Dir				= data.getString("SCH_DIR");		//직접검색
			String sch_Text				= data.getString("SCH_TEXT");		//텍스트
			String sch_RSVT_YN			= data.getString("SCH_RSVT_YN");	
			String sch_DATE_ST				= data.getString("SCH_DATE_ST");
			String sch_DATE_ED				= data.getString("SCH_DATE_ED");	
			
			String sql = this.getSelectQuery(sch_Type, sch_Dir,sch_Text, sch_RSVT_YN, sch_DATE_ST, sch_DATE_ED);   	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			
				if(!sch_Type.equals("")){
					
					if(sch_Type.equals("REG_ATON")){	//예약일자
						if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
						if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
					}else if (sch_Type.equals("CNCL_ATON")){	//취소일자
						if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
						if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
					}else if (sch_Type.equals("ROUND_HOPE_DATE")){	//부킹일자
						if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
						if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
					}
				}
				if(!sch_Dir.equals("")){
					if(sch_Dir.equals("CDHD_ID")){	// 주민번호
						if(!sch_Text.equals("")) pstmt.setString(++idx, sch_Text);
						
					}else if (sch_Dir.equals("TITL")){	//회원사 성명
						if(!sch_Text.equals("")) pstmt.setString(++idx, sch_Text);
						
					}else if (sch_Dir.equals("NOTE_MTTR_EXPL")){	//카드번호
						if(!sch_Text.equals("")) pstmt.setString(++idx, sch_Text);
						
					}else if (sch_Dir.equals("EMAIL")){	//골프장명
						if(!sch_Text.equals("")) pstmt.setString(++idx, sch_Text);
					}
				}
				
				if (!GolfUtil.isNull(sch_RSVT_YN)) pstmt.setString(++idx, sch_RSVT_YN);  			// 예약구분 검색
			
			pstmt.setLong(++idx, data.getLong("page_no"));
			rs = pstmt.executeQuery();
			String sttl_amt = "";
			int art_num_no = 0;
			String temp_card_no = "";
			if(rs != null) {			 

				while(rs.next())  {	
					if(!GolfUtil.empty(rs.getString("NOTE_MTTR_EXPL"))  && rs.getString("NOTE_MTTR_EXPL").length() == 16 ){
						temp_card_no = rs.getString("NOTE_MTTR_EXPL").substring(0, 4)+"-"+rs.getString("NOTE_MTTR_EXPL").substring(4, 8)+"-****-"+rs.getString("NOTE_MTTR_EXPL").substring(12, 16);
					}
					
					if(!GolfUtil.empty(rs.getString("TOT_PERS_NUM"))){
						sttl_amt = GolfUtil.comma(rs.getString("TOT_PERS_NUM"));
					}else{
						sttl_amt = "";
					}
					result.addString("GOLF_SVC_RSVT_NO"						,rs.getString("GOLF_SVC_RSVT_NO") );
					result.addString("TITL"						,rs.getString("TITL") );
					result.addString("HOPE_RGN_CODE"						,rs.getString("HOPE_RGN_CODE") );
					result.addString("CDHD_ID" 					,rs.getString("CDHD_ID") );
					result.addString("NOTE_MTTR_EXPL" 			,temp_card_no );
					
					String round_hope_date = rs.getString("ROUND_HOPE_DATE");
					String cncl_aton = rs.getString("CNCL_ATON");
					String reg_aton = rs.getString("REG_ATON");
					
					if (!GolfUtil.isNull(round_hope_date)) round_hope_date = DateUtil.format(round_hope_date, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("ROUND_HOPE_DATE"			,round_hope_date);
					if (!GolfUtil.isNull(cncl_aton)) cncl_aton = DateUtil.format(cncl_aton, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("CNCL_ATON"			,cncl_aton);
					if (!GolfUtil.isNull(reg_aton)) reg_aton = DateUtil.format(reg_aton, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("REG_ATON"			,reg_aton);
					
					result.addString("TOT_PERS_NUM" 			,sttl_amt );
					result.addString("EMAIL" 					,rs.getString("EMAIL") );
					result.addString("CTNT" 					,rs.getString("CTNT") );
					result.addString("RSVT_YN" 					,rs.getString("RSVT_YN") );
					result.addString("TOT_CNT"		,rs.getString("TOT_CNT") );
					
					result.addString("RESULT", "00"); //정상결과		
					result.addInt("ART_NUM" 				,rs.getInt("ART_NUM")-art_num_no );
					
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
	
	/**
	 * excel list
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult executeExcel(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String sch_yn					= data.getString("SCH_YN");					// 검색여부
			String sch_Type				= data.getString("SCH_TYPE");		//날짜검색
			String sch_Dir				= data.getString("SCH_DIR");		//직접검색
			String sch_Text				= data.getString("SCH_TEXT");		//
			String sch_RSVT_YN			= data.getString("SCH_RSVT_YN");	
			String sch_DATE_ST				= data.getString("SCH_DATE_ST");
			String sch_DATE_ED				= data.getString("SCH_DATE_ED");	
			String listtype 			= data.getString("LISTTYPE");
			
			
			
			String sql = this.getSelectExcelQuery(sch_yn,sch_Type, sch_Dir,sch_Text, sch_RSVT_YN, sch_DATE_ST, sch_DATE_ED,listtype);   	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			if(!GolfUtil.empty(sch_yn)){
				if(!sch_Type.equals("")){
					if(sch_Type.equals("REG_ATON")){	//예약일자
						if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
						if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
					}else if (sch_Type.equals("CNCL_ATON")){	//취소일자
						if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
						if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
					}else if (sch_Type.equals("ROUND_HOPE_DATE")){	//부킹일자
						if(!sch_DATE_ST.equals("")){	pstmt.setString(++idx, sch_DATE_ST);	}
						if(!sch_DATE_ED.equals("")){	pstmt.setString(++idx, sch_DATE_ED);	}
					}
				}
				if(!sch_Dir.equals("")){
					if(sch_Dir.equals("CDHD_ID")){	// 주민번호
						if(!sch_Text.equals("")) pstmt.setString(++idx, sch_Text);
						
					}else if (sch_Dir.equals("TITL")){	//회원사 성명
						if(!sch_Text.equals("")) pstmt.setString(++idx, sch_Text);
						
					}else if (sch_Dir.equals("NOTE_MTTR_EXPL")){	//카드번호
						if(!sch_Text.equals("")) pstmt.setString(++idx, sch_Text);
						
					}else if (sch_Dir.equals("EMAIL")){	//골프장명
						if(!sch_Text.equals("")) pstmt.setString(++idx, sch_Text);
					}
				}
				
				if (!GolfUtil.isNull(sch_RSVT_YN)) pstmt.setString(++idx, sch_RSVT_YN);  			// 예약구분 검색
			}
			
			rs = pstmt.executeQuery();
			String sttl_amt = "";
			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					
					if(!GolfUtil.empty(rs.getString("TOT_PERS_NUM"))){
						sttl_amt = GolfUtil.comma(rs.getString("TOT_PERS_NUM"));
					}else{
						sttl_amt = "";
					}
					result.addString("FIT_HOPE_CLUB_CLSS"					,rs.getString("FIT_HOPE_CLUB_CLSS") );
					result.addString("TITL"						,rs.getString("TITL") );
					result.addString("HOPE_RGN_CODE"						,rs.getString("HOPE_RGN_CODE") );
					result.addString("CDHD_ID" 					,rs.getString("CDHD_ID") );
					result.addString("NOTE_MTTR_EXPL" 			,rs.getString("NOTE_MTTR_EXPL") );
					
					String round_hope_date = rs.getString("ROUND_HOPE_DATE");
					String cncl_aton = rs.getString("CNCL_ATON");
					String reg_aton = rs.getString("REG_ATON");
					
					if (!GolfUtil.isNull(round_hope_date)) round_hope_date = DateUtil.format(round_hope_date, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("ROUND_HOPE_DATE"			,round_hope_date);
					if (!GolfUtil.isNull(cncl_aton)) cncl_aton = DateUtil.format(cncl_aton, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("CNCL_ATON"			,cncl_aton);
					if (!GolfUtil.isNull(reg_aton)) reg_aton = DateUtil.format(reg_aton, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("REG_ATON"			,reg_aton);
					
					result.addString("TOT_PERS_NUM" 			,sttl_amt );
					result.addString("EMAIL" 					,rs.getString("EMAIL") );
					result.addString("CTNT" 					,rs.getString("CTNT") );
					result.addString("RSVT_YN" 					,rs.getString("RSVT_YN") );
					result.addString("TOT_CNT"		,rs.getString("TOT_CNT") );
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
	
	
	/**
	 * 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int insertBooking(WaContext context, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

            /**SEQ_NO 가져오기**************************************************************/
			sql = this.getNextValQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String max_RSVT_SQL_NO = "";
			if(rs.next()){
				max_RSVT_SQL_NO = rs.getString("RSVT_SQL_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql);
			//debug("pstmt => " + pstmt);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("CHNG_MGR_ID") );
        	pstmt.setString(++idx, "PLATU"+max_RSVT_SQL_NO );
        	
        	pstmt.setString(++idx, data.getString("HOPE_RGN_CODE") ); 
        	pstmt.setString(++idx, data.getString("TITL") ); 
        	pstmt.setString(++idx, data.getString("CDHD_ID") );
        	pstmt.setString(++idx, data.getString("FIT_HOPE_CLUB_CLSS") );
        	pstmt.setString(++idx, data.getString("NOTE_MTTR_EXPL") );
        	pstmt.setString(++idx, data.getString("CNCL_ATON").replaceAll("-", "") );
        	pstmt.setString(++idx, data.getString("ROUND_HOPE_DATE").replaceAll("-","") );
        	pstmt.setString(++idx, data.getString("TOT_PERS_NUM") );
        	pstmt.setString(++idx, data.getString("EMAIL") );
        	
        	pstmt.setString(++idx, data.getString("CTNT") );
        	pstmt.setString(++idx, data.getString("RSVT_YN") );
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
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, "관리자 프리미엄부킹 골프장 등록 처리", "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	
	
	
	/**
	 * 상세보기
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult getPlatinumView(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getPlatinumViewQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("GOLF_SVC_RSVT_NO"));
			
			rs = pstmt.executeQuery();
			String sttl_amt = "";
			String jumin1 = "";
			String jumin2 = "";
			String card_no = "";
			String grade = "";		//등급
					if(rs != null) {
						while(rs.next())  {
							if(!GolfUtil.empty(rs.getString("TOT_PERS_NUM"))){
								sttl_amt = GolfUtil.comma(rs.getString("TOT_PERS_NUM"));
							}else{
								sttl_amt = "";
							}
							
							if(!GolfUtil.empty(rs.getString("FIT_HOPE_CLUB_CLSS"))){
								if(rs.getString("FIT_HOPE_CLUB_CLSS").equals("3")){
									grade = "E플래티늄";
								}else if(rs.getString("FIT_HOPE_CLUB_CLSS").equals("12")){
									grade = "플래티늄";
								}else if(rs.getString("FIT_HOPE_CLUB_CLSS").equals("30")){
									grade = "다이아몬드 시크니처";
								}else if(rs.getString("FIT_HOPE_CLUB_CLSS").equals("91")){
									grade = "인피니트";
								}
							}
							jumin1 = rs.getString("CDHD_ID").substring(0, 6);
							jumin2 = rs.getString("CDHD_ID").substring(6, 13);
							
							result.addString("CHNG_MGR_ID"						,rs.getString("CHNG_MGR_ID") );
							result.addString("FIT_HOPE_CLUB_CLSS"						,grade );
							result.addString("GOLF_SVC_RSVT_NO"						,rs.getString("GOLF_SVC_RSVT_NO") );
							result.addString("TITL"						,rs.getString("TITL") );
							result.addString("HOPE_RGN_CODE"						,rs.getString("HOPE_RGN_CODE") );
							result.addString("CDHD_ID" 					,rs.getString("CDHD_ID"));
							result.addString("JUMIN1" 					,jumin1 );
							result.addString("JUMIN2" 					,jumin2 );
							
							if(!GolfUtil.empty(rs.getString("NOTE_MTTR_EXPL"))  && rs.getString("NOTE_MTTR_EXPL").length() == 16 ){
								card_no = rs.getString("NOTE_MTTR_EXPL").substring(0, 4)+"-"+rs.getString("NOTE_MTTR_EXPL").substring(4, 8)+"-"+rs.getString("NOTE_MTTR_EXPL").substring(8, 12)+"-"+rs.getString("NOTE_MTTR_EXPL").substring(12, 16);
							}
							result.addString("NOTE_MTTR_EXPL" 			,card_no);
							
							String round_hope_date = rs.getString("ROUND_HOPE_DATE");
							String cncl_aton = rs.getString("CNCL_ATON");
							String reg_aton = rs.getString("REG_ATON");
							
							if (!GolfUtil.isNull(round_hope_date)) round_hope_date = DateUtil.format(round_hope_date, "yyyyMMdd", "yyyy-MM-dd");
							result.addString("ROUND_HOPE_DATE"			,round_hope_date);
							if (!GolfUtil.isNull(cncl_aton)) cncl_aton = DateUtil.format(cncl_aton, "yyyyMMdd", "yyyy-MM-dd");
							result.addString("CNCL_ATON"			,cncl_aton);
							if (!GolfUtil.isNull(reg_aton)) reg_aton = DateUtil.format(reg_aton, "yyyyMMdd", "yyyy-MM-dd");
							result.addString("REG_ATON"			,reg_aton);
							
							result.addString("TOT_PERS_NUM" 			,sttl_amt );
							result.addString("EMAIL" 					,rs.getString("EMAIL") );
							result.addString("CTNT" 					,rs.getString("CTNT") );
							result.addString("RSVT_YN" 					,rs.getString("RSVT_YN") );
							
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
	 * 하단 주민등록번호로 리스트 가져오기
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult executeJuminList(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String cdhd_id					= data.getString("CDHD_ID");					// 주민등록 번호
			
			String sql = this.getSelectJuminQuery();   	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			
			rs = pstmt.executeQuery();
			String sttl_amt = "";
			int art_num_no = 0;
			String temp_card_no = "";
			if(rs != null) {			 

				while(rs.next())  {	
					
					if(!GolfUtil.empty(rs.getString("TOT_PERS_NUM"))){
						sttl_amt = GolfUtil.comma(rs.getString("TOT_PERS_NUM"));
					}else{
						sttl_amt = "";
					}
					result.addString("GOLF_SVC_RSVT_NO"						,rs.getString("GOLF_SVC_RSVT_NO") );
					result.addString("HOPE_RGN_CODE"						,rs.getString("HOPE_RGN_CODE") );
					
					if(!GolfUtil.empty(rs.getString("NOTE_MTTR_EXPL"))  && rs.getString("NOTE_MTTR_EXPL").length() == 16 ){
						temp_card_no = rs.getString("NOTE_MTTR_EXPL").substring(0, 4)+"-"+rs.getString("NOTE_MTTR_EXPL").substring(4, 8)+"-****-"+rs.getString("NOTE_MTTR_EXPL").substring(12, 16);
					}
					result.addString("NOTE_MTTR_EXPL" 			,temp_card_no );
					
					String round_hope_date = rs.getString("ROUND_HOPE_DATE");
					String cncl_aton = rs.getString("CNCL_ATON");
					String reg_aton = rs.getString("REG_ATON");
					
					if (!GolfUtil.isNull(round_hope_date)) round_hope_date = DateUtil.format(round_hope_date, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("ROUND_HOPE_DATE"			,round_hope_date);
					if (!GolfUtil.isNull(cncl_aton)) cncl_aton = DateUtil.format(cncl_aton, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("CNCL_ATON"			,cncl_aton);
					if (!GolfUtil.isNull(reg_aton)) reg_aton = DateUtil.format(reg_aton, "yyyyMMdd", "yyyy-MM-dd");
					result.addString("REG_ATON"			,reg_aton);
					
					result.addString("TOT_PERS_NUM" 			,sttl_amt );
					result.addString("EMAIL" 					,rs.getString("EMAIL") );
					result.addString("CTNT" 					,rs.getString("CTNT") );
					result.addString("RSVT_YN" 					,rs.getString("RSVT_YN") );
					result.addString("TOT_CNT"  ,				rs.getString("TOT_CNT"));
					
					result.addString("RESULT", "00"); //정상결과		
					result.addInt("ART_NUM" 				,rs.getInt("ART_NUM")-art_num_no );
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
	
	/**
	 * 수정처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int executeUpdate(WaContext context, TaoDataSet data) throws DbTaoException  {
	
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
	        /*****************************************************************************/
			
			String golf_svc_rsvt_no = data.getString("GOLF_SVC_RSVT_NO");
			String note_mttr_expl = data.getString("NOTE_MTTR_EXPL").replaceAll("-","");
			String cncl_aton = data.getString("CNCL_ATON").replaceAll("-","");
			String rount_hope_date= data.getString("ROUND_HOPE_DATE").replaceAll("-","");
			String hope_rgn_code = data.getString("HOPE_RGN_CODE");
			String email = data.getString("EMAIL");
			String tot_pers_num = data.getString("TOT_PERS_NUM").replaceAll(",","");;
			String ctnt = data.getString("CTNT");
			String rsvt_yn = data.getString("RSVT_YN");
			String chng_mgr_id = data.getString("CHNG_MGR_ID");
			String fit_hope_club_clss = data.getString("FIT_HOPE_CLUB_CLSS");
	        
			sql = this.getUpdateQuery(rsvt_yn);//Insert Query
			pstmt = conn.prepareStatement(sql);
	        
			int idx = 0;
			if (rsvt_yn.equals("Y")){
				pstmt.setString(++idx, note_mttr_expl);			
			}
			
			pstmt.setString(++idx, cncl_aton );
			pstmt.setString(++idx, rount_hope_date  );
			pstmt.setString(++idx, hope_rgn_code  );
			pstmt.setString(++idx, email  );
			pstmt.setString(++idx, tot_pers_num  );
			pstmt.setString(++idx, ctnt  );
			pstmt.setString(++idx, rsvt_yn  );		
			pstmt.setString(++idx, chng_mgr_id );
			pstmt.setString(++idx, fit_hope_club_clss );
			pstmt.setString(++idx, golf_svc_rsvt_no );
			
			
			result = pstmt.executeUpdate();
	        if(pstmt != null) pstmt.close();
	        
	
			if(result>0) {
				conn.commit();
			} else {
				conn.rollback();
			}	
			
			conn.setAutoCommit(true);
		
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
	
		return result;
		
	}
	
	
	/**
	 * 삭제
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int executeDelete(WaContext context, TaoDataSet data) throws DbTaoException  {
	
		int result = 0, reVal = 0;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		String sql = "";
		
		try {
			
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
	        /*****************************************************************************/
			
			String golf_svc_rsvt_no = data.getString("GOLF_SVC_RSVT_NO");
			
			//플래티늄 부킹 내역 삭제 가능여부 체크
			sql = delConfirm();
			pstmt = conn.prepareStatement(sql);	
			pstmt.setString(1, golf_svc_rsvt_no );
			
			String chkVal = "F";
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				chkVal = rs.getString("CHECKVAL");
			}
			
			sql = "";
			rs.close(); pstmt.close();
			
			//플래티늄 부킹 내역 삭제 
			if ( chkVal.equals("T")){
				
				sql = platinumDel();
				pstmt = conn.prepareStatement(sql);			
				pstmt.setString(1, golf_svc_rsvt_no );			
				
				result = pstmt.executeUpdate();
		        if(pstmt != null) pstmt.close();
		
				if(result>0) {
					reVal = 1;
					conn.commit();
				} else {
					reVal = 3;
					conn.rollback();
				}
				
				pstmt.close();
				
			}else {				
				reVal = 2;				
			}
			
			conn.setAutoCommit(true);
		
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
	        MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
	        throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
	        try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	        try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			
	
		return reVal;
	}
		
	
	
	/**
	 * 이름 입력해서 주민번호 가져오기
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult searchName(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String sch_text				= data.getString("SCH_TEXT");		//이름
			
			String sql = this.getSearchNameQuery();   	
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, sch_text);
			
			rs = pstmt.executeQuery();
			int art_num_no = 0; 
			String jumin1 = "";
			String jumin2 = "";
			if(rs != null) {			 
 
				while(rs.next())  {	
					
					jumin1 = rs.getString("JUMIN_NO").substring(0, 6);
					jumin2 = rs.getString("JUMIN_NO").substring(6, 13);
					
					result.addString("JUMIN_NO" 					,rs.getString("JUMIN_NO") );
					result.addString("JUMIN_NO1" 					,jumin1);
					result.addString("JUMIN_NO2" 					,jumin2 );
					result.addString("HG_NM" 					,rs.getString("HG_NM") );
					result.addString("RESULT_VALUE" 					,rs.getString("HG_NM") +"/"+jumin1+"/"+jumin2 );
					result.addString("TOT_CNT"		,rs.getString("TOT_CNT") );
					result.addString("RESULT", "00"); //정상결과
					result.addString("TYPE", "name"); //type
					result.addInt("ART_NUM" 				,rs.getInt("ART_NUM")-art_num_no );
					
					art_num_no++;
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");
				result.addString("TYPE", "name"); //정상결과
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
	 * 골프장 입력해서 검색 리스트 가져오기
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult searchGolfloung(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			String sch_text				= data.getString("SCH_TEXT");		//골프장
			String sql = this.getSearchGolfloungQuery(); 
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, sch_text);
			
			rs = pstmt.executeQuery();
			if(rs != null) {			 
 
				while(rs.next())  {	
					
					result.addString("GREEN_NM" 					,rs.getString("GREEN_NM") );
					result.addString("TOT_CNT"		,rs.getString("TOT_CNT") );
					result.addString("RESULT", "00"); //정상결과
					result.addString("TYPE", "golfloung"); //type
					
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");	
				result.addString("TYPE", "golfloung"); //type
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
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGRSVTMGMT (	\n");
		sql.append("\t  CHNG_MGR_ID,GOLF_SVC_RSVT_NO, GOLF_SVC_RSVT_MAX_VAL, 	\n");
		sql.append("\t  HOPE_RGN_CODE,TITL, CDHD_ID,FIT_HOPE_CLUB_CLSS, NOTE_MTTR_EXPL, CNCL_ATON, ROUND_HOPE_DATE, TOT_PERS_NUM, EMAIL, CTNT, RSVT_YN, REG_ATON	 		\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,'PLATU',?,?,?,?,?,?, \n");
		sql.append("\t  ?,?,?,?,?,	\n");
		sql.append("\t  TO_CHAR(SYSDATE,'YYYYMMDD') \n");
		sql.append("\t  \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT LPAD(TO_NUMBER(NVL(MAX(SUBSTR(GOLF_SVC_RSVT_NO,6,7)),0))+1,7,'0') RSVT_SQL_NO FROM BCDBA.TBGRSVTMGMT \n");
        sql.append("WHERE GOLF_SVC_RSVT_MAX_VAL = 'PLATU' \n");
		return sql.toString();
    }
         
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */   
    private String getSelectQuery(String sch_Type, String sch_Dir,String sch_Text, String sch_RSVT_YN, String sch_DATE_ST, String sch_DATE_ED){
        StringBuffer sql = new StringBuffer();      

        
		sql.append("\n SELECT	*																														\n");
		sql.append("\n FROM (SELECT ROWNUM RNUM																											\n");
		sql.append("\n 		,  GOLF_SVC_RSVT_NO,TITL,HOPE_RGN_CODE, CDHD_ID, NOTE_MTTR_EXPL, REG_ATON, CNCL_ATON, ROUND_HOPE_DATE, TOT_PERS_NUM, EMAIL, CTNT, RSVT_YN											\n");
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE																									\n");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT																									\n");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  																				\n");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM																									\n");
		sql.append("\n 		, T1.GOLF_SVC_RSVT_NO,T1.TITL,T1.HOPE_RGN_CODE, T1.CDHD_ID, T1.NOTE_MTTR_EXPL, NVL(T1.REG_ATON,'') AS REG_ATON, NVL(T1.CNCL_ATON,'') AS CNCL_ATON, NVL(T1.ROUND_HOPE_DATE,'') AS ROUND_HOPE_DATE, T1.TOT_PERS_NUM, T1.EMAIL, T1.CTNT 			\n");
		sql.append("\n 		, (CASE WHEN T1.RSVT_YN='Y' THEN '예약' WHEN T1.RSVT_YN='N' THEN '취소' END) RSVT_YN  \n");
		
		sql.append("\t 		FROM BCDBA.TBGRSVTMGMT T1 																									\n");
		sql.append("\n 		WHERE T1.GOLF_SVC_RSVT_MAX_VAL = 'PLATU' 	");
		
			if(!sch_Type.equals("")){
				if(sch_Type.equals("REG_ATON")){
					if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND T1.REG_ATON>=?   	");			}
					if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND T1.REG_ATON<=?  	");			}			
				}else if (sch_Type.equals("CNCL_ATON")){
					if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND T1.CNCL_ATON>=?   	");			}
					if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND T1.CNCL_ATON<=?  	");			}
				}else if(sch_Type.equals("ROUND_HOPE_DATE")){
					if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND T1.ROUND_HOPE_DATE>=?   	");			}
					if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND T1.ROUND_HOPE_DATE<=?  	");			}
				}
			}
			if(!sch_Dir.equals("")){
				if(sch_Dir.equals("CDHD_ID")){
					if(!sch_Text.equals("")){ sql.append("\n 			AND CDHD_ID LIKE '%'||?||'%'																									\n"); }	
				}else if (sch_Dir.equals("TITL")){
					if(!sch_Text.equals("")){ sql.append("\n 			AND TITL LIKE '%'||?||'%'																									\n"); }
				}else if(sch_Dir.equals("NOTE_MTTR_EXPL")){
					if(!sch_Text.equals("")){ sql.append("\n 			AND NOTE_MTTR_EXPL LIKE ?||'%'																									\n"); }
				}else if(sch_Dir.equals("EMAIL")){
					if(!sch_Text.equals("")){ sql.append("\n 			AND EMAIL LIKE '%'||?||'%'																									\n"); }
				}
			}
			if(!sch_RSVT_YN.equals("")){	
				sql.append("\t 			AND RSVT_YN = ?																									\n");
			}
		sql.append("\n 		ORDER BY T1.REG_ATON DESC  																									\n");		
		
		sql.append("\n 			)																														\n");
		sql.append("\n 	ORDER BY RNUM																													\n");
		sql.append("\n 	)																																\n");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
    
    /** ***********************************************************************
     * EXCEL 리스트    
     ************************************************************************ */   
     private String getSelectExcelQuery(String sch_yn,String sch_Type, String sch_Dir,String sch_Text, String sch_RSVT_YN, String sch_DATE_ST, String sch_DATE_ED,String listtype){
         StringBuffer sql = new StringBuffer();      

         
 		sql.append("\n SELECT	*																														\n");
 		sql.append("\n FROM (SELECT ROWNUM RNUM																											\n");
 		sql.append("\n 		,  TITL,HOPE_RGN_CODE,FIT_HOPE_CLUB_CLSS, CDHD_ID, NOTE_MTTR_EXPL, REG_ATON, CNCL_ATON, ROUND_HOPE_DATE, TOT_PERS_NUM, EMAIL, CTNT, RSVT_YN											\n");
 		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT																									\n");
 		sql.append("\n 		FROM (SELECT ROWNUM RNUM																									\n");
 		sql.append("\n 		, T1.TITL,T1.HOPE_RGN_CODE,	 T1.CDHD_ID, T1.NOTE_MTTR_EXPL, NVL(T1.REG_ATON,'') AS REG_ATON, NVL(T1.CNCL_ATON,'') AS CNCL_ATON, NVL(T1.ROUND_HOPE_DATE,'') AS ROUND_HOPE_DATE, T1.TOT_PERS_NUM, T1.EMAIL, T1.CTNT 			\n");
 		sql.append("\n 		, (CASE WHEN T1.RSVT_YN='Y' THEN '예약' WHEN T1.RSVT_YN='N' THEN '취소' END) RSVT_YN  \n");
 		sql.append("\n 		, (CASE WHEN T1.FIT_HOPE_CLUB_CLSS='03' THEN 'E플래티늄' WHEN T1.FIT_HOPE_CLUB_CLSS='12' THEN '플래티늄' WHEN T1.FIT_HOPE_CLUB_CLSS='30' THEN '다이아몬드시그니처' WHEN T1.FIT_HOPE_CLUB_CLSS='91' THEN '인피니트' END) FIT_HOPE_CLUB_CLSS  \n");
 		sql.append("\t 		FROM BCDBA.TBGRSVTMGMT T1 																									\n");
 		sql.append("\n 		WHERE T1.GOLF_SVC_RSVT_MAX_VAL = 'PLATU' 	");
 		
 		if("Y".equals(sch_yn)){
 			if(!sch_Type.equals("")){
 				if(sch_Type.equals("REG_ATON")){
					if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND T1.REG_ATON>=?   	");			}
					if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND T1.REG_ATON<=?  	");			}			
				}else if (sch_Type.equals("CNCL_ATON")){
					if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND T1.CNCL_ATON>=?   	");			}
					if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND T1.CNCL_ATON<=?  	");			}
				}else if(sch_Type.equals("ROUND_HOPE_DATE")){
					if(!sch_DATE_ST.equals("")){		sql.append("\n 		AND T1.ROUND_HOPE_DATE>=?   	");			}
					if(!sch_DATE_ED.equals("")){		sql.append("\n 		AND T1.ROUND_HOPE_DATE<=?  	");			}
				} 
 			}
 			if(!sch_Dir.equals("")){
 				if(sch_Dir.equals("CDHD_ID")){
 					if(!sch_Text.equals("")){ sql.append("\n 			AND CDHD_ID LIKE '%'||?||'%'																								\n"); }	
 				}else if (sch_Dir.equals("TITL")){
 					if(!sch_Text.equals("")){ sql.append("\n 			AND TITL LIKE '%'||?||'%'																									\n"); }
 				}else if(sch_Dir.equals("NOTE_MTTR_EXPL")){
 					if(!sch_Text.equals("")){ sql.append("\n 			AND NOTE_MTTR_EXPL LIKE ?||'%'																									\n"); }
 				}else if(sch_Dir.equals("EMAIL")){
 					if(!sch_Text.equals("")){ sql.append("\n 			AND EMAIL LIKE '%'||?||'%'																									\n"); }
 				}
 			}
 			if(!sch_RSVT_YN.equals("")){	
 				sql.append("\t 			AND RSVT_YN LIKE ?																									\n");
 			}
 		}
 		sql.append("\n 		ORDER BY T1.REG_ATON DESC  																									\n");		
 		
 		sql.append("\n 			)																														\n");
 		sql.append("\n 	ORDER BY RNUM																													\n");
 		sql.append("\n 	)																																\n");

 		return sql.toString();
     }
     
     /** ***********************************************************************
      * 플래티넘 상세보기 수정폼
      ************************************************************************ */
      private String getPlatinumViewQuery(){
          StringBuffer sql = new StringBuffer();

  		sql.append("\n  SELECT 																									\n");
  		sql.append("\n 		CHNG_MGR_ID,GOLF_SVC_RSVT_NO,TITL,HOPE_RGN_CODE,FIT_HOPE_CLUB_CLSS, CDHD_ID, NOTE_MTTR_EXPL, REG_ATON, CNCL_ATON, ROUND_HOPE_DATE, TOT_PERS_NUM, EMAIL, CTNT											\n");
  		sql.append("\n 		, RSVT_YN																					\n");
  		//sql.append("\n 		, (CASE WHEN FIT_HOPE_CLUB_CLSS='03' THEN 'E플래티늄' WHEN FIT_HOPE_CLUB_CLSS='12' THEN '플래티늄' WHEN FIT_HOPE_CLUB_CLSS='30' THEN '다이아몬드시그니처' WHEN FIT_HOPE_CLUB_CLSS='91' THEN '인피니트' END) FIT_HOPE_CLUB_CLSS  \n");
  		sql.append("\n 		  FROM BCDBA.TBGRSVTMGMT			\n");
  		sql.append("\n 		WHERE GOLF_SVC_RSVT_MAX_VAL = 'PLATU'  \n");
  		sql.append("\t 		AND GOLF_SVC_RSVT_NO  = ?																								\n");
  		
  	    
  		return sql.toString();
      }
      
      /** ***********************************************************************
       * 하단 목록 주민번호로 리스트 검색  
       ************************************************************************ */   
       private String getSelectJuminQuery(){
           StringBuffer sql = new StringBuffer();      

           
   		sql.append("\n SELECT	*																														\n");
   		sql.append("\n FROM (SELECT ROWNUM RNUM																											\n");
   		sql.append("\n 		,  GOLF_SVC_RSVT_NO,TITL,HOPE_RGN_CODE, CDHD_ID, NOTE_MTTR_EXPL, REG_ATON, CNCL_ATON, ROUND_HOPE_DATE, TOT_PERS_NUM, EMAIL, CTNT, RSVT_YN											\n");
   		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE																									\n");
   		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT																									\n");
   		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  																				\n");	
   		sql.append("\n 		FROM (SELECT ROWNUM RNUM																									\n");
   		sql.append("\n 		, T1.GOLF_SVC_RSVT_NO,T1.TITL,T1.HOPE_RGN_CODE, T1.CDHD_ID, T1.NOTE_MTTR_EXPL, NVL(T1.REG_ATON,'') AS REG_ATON, NVL(T1.CNCL_ATON,'') AS CNCL_ATON, NVL(T1.ROUND_HOPE_DATE,'') AS ROUND_HOPE_DATE, T1.TOT_PERS_NUM, T1.EMAIL, T1.CTNT 			\n");
   		sql.append("\n 		, (CASE WHEN T1.RSVT_YN='Y' THEN '예약' WHEN T1.RSVT_YN='N' THEN '취소' END) RSVT_YN  \n");
   		sql.append("\t 		FROM BCDBA.TBGRSVTMGMT T1 																									\n");
   		sql.append("\n 		WHERE T1.GOLF_SVC_RSVT_MAX_VAL = 'PLATU' 	");
   		sql.append("\n 		AND T1.CDHD_ID = ? 	");
   		sql.append("\n 		ORDER BY T1.REG_ATON DESC  																									\n");		
   		sql.append("\n 			)																														\n");
   		sql.append("\n 	ORDER BY RNUM																													\n");
   		sql.append("\n 	)																																\n");
   		sql.append("\n  WHERE PAGE = ?	");
   		return sql.toString();
       }
       
       /** ***********************************************************************
        * 수정처리.    
        ************************************************************************ */
        private String getUpdateQuery(String rsvt_yn){
            StringBuffer sql = new StringBuffer();

            
            if (rsvt_yn.equals("Y")){            	
	    		sql.append("\n");
	    		sql.append("UPDATE BCDBA.TBGRSVTMGMT SET	\n");
	    		sql.append("\t NOTE_MTTR_EXPL=?, CNCL_ATON=?,ROUND_HOPE_DATE=?,HOPE_RGN_CODE=?,EMAIL=?,TOT_PERS_NUM=?, CTNT=?,RSVT_YN=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD'),CHNG_MGR_ID=?,FIT_HOPE_CLUB_CLSS=?	\n");
	    		sql.append("\t WHERE GOLF_SVC_RSVT_NO=?	\n");
	    		sql.append("\t AND GOLF_SVC_RSVT_MAX_VAL='PLATU'  \n");
            }else {            	
	    		sql.append("\n");
	    		sql.append("UPDATE BCDBA.TBGRSVTMGMT SET	\n");
	    		sql.append("\t CNCL_ATON=?, ROUND_HOPE_DATE=?,HOPE_RGN_CODE=?,EMAIL=?,TOT_PERS_NUM=?, CTNT=?,RSVT_YN=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD'),CHNG_MGR_ID=?,FIT_HOPE_CLUB_CLSS=?	\n");
	    		sql.append("\t WHERE GOLF_SVC_RSVT_NO=?	\n");
	    		sql.append("\t AND GOLF_SVC_RSVT_MAX_VAL='PLATU'  \n");            	
            }
            
            return sql.toString();
        }
        
        
        /*************************************************************************
         * 플래티늄 부킹 내역 삭제 가능여부 체크
         * 정산된 데이터는 삭제 불가능 (이전달 부킹내역은 모두 정산됨)
         * 즉, 라운딩희망일이 현재달인 경우만 삭제 가능     
         ************************************************************************ */
        private String delConfirm(){
        	 
           StringBuffer sql = new StringBuffer();
                         	
           sql.append("\n");
           sql.append(" SELECT TO_CHAR(SYSDATE,'YYYYMM'), SUBSTR(ROUND_HOPE_DATE,1,6),		\n");
           sql.append("	CASE WHEN TO_CHAR(SYSDATE,'YYYYMM') > SUBSTR(ROUND_HOPE_DATE,1,6) 	\n"); //현재월이 부킹월보다 크면 삭제불가
           sql.append("		THEN 'F'														\n"); //'F' 삭제불가           
           sql.append("		ELSE 'T'														\n"); //'T' 삭제가능
           sql.append("	END CHECKVAL														\n");
           sql.append("	FROM BCDBA.TBGRSVTMGMT												\n");
           sql.append("	WHERE GOLF_SVC_RSVT_MAX_VAL = 'PLATU'								\n");           
           sql.append("	AND GOLF_SVC_RSVT_NO = ?										    \n");
                     
           return sql.toString();
    		
        }
         
        
        /*************************************************************************
         * 플래티늄 부킹 내역 삭제    
         ************************************************************************ */
         private String platinumDel(){
        	 
            StringBuffer sql = new StringBuffer();
                         	
    		sql.append("\n");
    		sql.append(" DELETE FROM BCDBA.TBGRSVTMGMT	\n");
    		sql.append(" WHERE GOLF_SVC_RSVT_MAX_VAL = 'PLATU'	\n");
    		sql.append(" AND GOLF_SVC_RSVT_NO = ? \n"); 	    		
                     
    		return sql.toString();
    		
         }        
        
        
        /** ***********************************************************************
         * Query를 생성하여 리턴한다.    
         ************************************************************************ */   
         private String getSearchNameQuery(){
             StringBuffer sql = new StringBuffer();      

             sql.append("\n SELECT	*																														\n");
     		sql.append("\n FROM (SELECT ROWNUM RNUM																											\n");
     		sql.append("\n 		,  JUMIN_NO, HG_NM											\n");
     		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE																									\n");
     		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT																									\n");
     		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  																				\n");	
     		sql.append("\n 		FROM (SELECT ROWNUM RNUM																									\n");
     		sql.append("\n 		,  JUMIN_NO, HG_NM			\n");
     		sql.append("\t 		FROM BCDBA.TBGGOLFCDHD  																									\n");
 			sql.append("\n 		WHERE HG_NM LIKE '%'||?||'%' 	");
     		sql.append("\n 		ORDER BY HG_NM DESC  																									\n");		
     		
     		sql.append("\n 			)																														\n");
     		sql.append("\n 	ORDER BY RNUM																													\n");
     		sql.append("\n 	)																																\n");

     		return sql.toString();
         }
         
         /** ***********************************************************************
          * Query를 생성하여 리턴한다.    
          ************************************************************************ */   
          private String getSearchGolfloungQuery(){
              StringBuffer sql = new StringBuffer();      

              sql.append("\n SELECT	*																														\n");
       		sql.append("\n FROM (SELECT ROWNUM RNUM																											\n");
       		sql.append("\n 		,  GREEN_NM											\n");
       		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE																									\n");
       		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT																									\n");
       		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  																				\n");	
       		sql.append("\n 		FROM (SELECT ROWNUM RNUM																									\n");
       		sql.append("\n 		,  GREEN_NM			\n");
       		sql.append("\t 		FROM BCDBA.TBGAFFIGREEN  																									\n");
       		sql.append("\n 		WHERE GREEN_NM LIKE '%'||?||'%' 	");
       		sql.append("\n 		ORDER BY GREEN_NM DESC  																									\n");		
       		sql.append("\n 			)																														\n");
       		sql.append("\n 	ORDER BY RNUM																													\n");
       		sql.append("\n 	)																									\n");		
      		

      		return sql.toString();
          }
    
}
