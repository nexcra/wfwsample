/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeListDaoProc 
*   작성자    : (주)미디어포스 임은혜
*   내용      : 티타임리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-05-27
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.SmsSendProc;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf 
 * @author	미디어포스 
 * @version	1.0
 ******************************************************************************/
public class GolfTopGolfCardListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPreTimeListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfTopGolfCardListDaoProc() {}	

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
			int defaultDate				= data.getInt("defaultDate");
			int nHour					= data.getInt("nHour");
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery(defaultDate, nHour);   

			// 입력값 (INPUT)
			pstmt = conn.prepareStatement(sql.toString());
			
			rs = pstmt.executeQuery();
			
			int total_0 = 0;
			int total_1 = 0;
			int total_2 = 0;
			int total_3 = 0;
			int total_4 = 0;
			int total_5 = 0;
			int total_6 = 0;
			int total_7 = 0;
			int total_8 = 0;
			int total_9 = 0;
			int total_10 = 0;
			int total_11 = 0;
			int total_12 = 0;
			int total_13 = 0;
			
			String isTOTAL_0 = "";
			String isTOTAL_1 = "";
			String isTOTAL_2 = "";
			String isTOTAL_3 = "";
			String isTOTAL_4 = "";
			String isTOTAL_5 = "";
			String isTOTAL_6 = "";
			String isTOTAL_7 = "";
			String isTOTAL_8 = "";
			String isTOTAL_9 = "";
			String isTOTAL_10 = "";
			String isTOTAL_11 = "";
			String isTOTAL_12 = "";
			String isTOTAL_13 = "";

			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("SEQ_NO" 			,rs.getString("AFFI_GREEN_SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					
					// 남아있는 부킹 수 가져오기
					total_0 = rs.getInt("TOTAL_0");
					total_1 = rs.getInt("TOTAL_1");
					total_2 = rs.getInt("TOTAL_2");
					total_3 = rs.getInt("TOTAL_3");
					total_4 = rs.getInt("TOTAL_4");
					total_5 = rs.getInt("TOTAL_5");
					total_6 = rs.getInt("TOTAL_6");
					total_7 = rs.getInt("TOTAL_7");
					total_8 = rs.getInt("TOTAL_8");
					total_9 = rs.getInt("TOTAL_9");
					total_10 = rs.getInt("TOTAL_10");
					total_11 = rs.getInt("TOTAL_11");
					total_12 = rs.getInt("TOTAL_12");
					total_13 = rs.getInt("TOTAL_13");
					
					// 부킹이 남아있는지 여부 가져오기
					if(total_0>0){isTOTAL_0="Y";} else {isTOTAL_0="N";}
					if(total_1>0){isTOTAL_1="Y";} else {isTOTAL_1="N";}
					if(total_2>0){isTOTAL_2="Y";} else {isTOTAL_2="N";}
					if(total_3>0){isTOTAL_3="Y";} else {isTOTAL_3="N";}
					if(total_4>0){isTOTAL_4="Y";} else {isTOTAL_4="N";}
					if(total_5>0){isTOTAL_5="Y";} else {isTOTAL_5="N";}
					if(total_6>0){isTOTAL_6="Y";} else {isTOTAL_6="N";}
					if(total_7>0){isTOTAL_7="Y";} else {isTOTAL_7="N";}
					if(total_8>0){isTOTAL_8="Y";} else {isTOTAL_8="N";}
					if(total_9>0){isTOTAL_9="Y";} else {isTOTAL_9="N";}
					if(total_10>0){isTOTAL_10="Y";} else {isTOTAL_10="N";}
					if(total_11>0){isTOTAL_11="Y";} else {isTOTAL_11="N";}
					if(total_12>0){isTOTAL_12="Y";} else {isTOTAL_12="N";}
					if(total_13>0){isTOTAL_13="Y";} else {isTOTAL_13="N";}
					
					result.addInt("TOTAL_0" 			,total_0 );
					result.addInt("TOTAL_1" 			,total_1 );
					result.addInt("TOTAL_2" 			,total_2 );
					result.addInt("TOTAL_3" 			,total_3 );
					result.addInt("TOTAL_4" 			,total_4 );
					result.addInt("TOTAL_5" 			,total_5 );
					result.addInt("TOTAL_6" 			,total_6 );
					result.addInt("TOTAL_7" 			,total_7 );
					result.addInt("TOTAL_8" 			,total_8 );
					result.addInt("TOTAL_9" 			,total_9 );
					result.addInt("TOTAL_10" 			,total_10 );
					result.addInt("TOTAL_11" 			,total_11 );
					result.addInt("TOTAL_12" 			,total_12 );
					result.addInt("TOTAL_13" 			,total_13 );
					
					result.addString("isTOTAL_0" 			,isTOTAL_0 );
					result.addString("isTOTAL_1" 			,isTOTAL_1 );
					result.addString("isTOTAL_2" 			,isTOTAL_2 );
					result.addString("isTOTAL_3" 			,isTOTAL_3 );
					result.addString("isTOTAL_4" 			,isTOTAL_4 );
					result.addString("isTOTAL_5" 			,isTOTAL_5 );
					result.addString("isTOTAL_6" 			,isTOTAL_6 );
					result.addString("isTOTAL_7" 			,isTOTAL_7 );
					result.addString("isTOTAL_8" 			,isTOTAL_8 );
					result.addString("isTOTAL_9" 			,isTOTAL_9 );
					result.addString("isTOTAL_10" 			,isTOTAL_10 );
					result.addString("isTOTAL_11" 			,isTOTAL_11 );
					result.addString("isTOTAL_12" 			,isTOTAL_12 );
					result.addString("isTOTAL_13" 			,isTOTAL_13 );
				}
				result.addString("RESULT", "00"); //정상결과
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
	 * 위약금 가져오기 .
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	
	public DbTaoResult getPanalty(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
						 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectViewQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("affi_green_seq_no"));
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {
					result.addString("DTL_ADDR" 		,rs.getString("DTL_ADDR") );
					result.addString("CURS_SCAL_INFO" 		,rs.getString("CURS_SCAL_INFO") );
					result.addString("AFFI_GREEN_SEQ_NO" 		,rs.getString("AFFI_GREEN_SEQ_NO") );
					result.addString("RESULT", "00");	
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
	public int app_insert(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			String green_nm						= data.getString("GREEN_NM");
			String teof_date					= data.getString("TEOF_DATE");
			String teof_time					= data.getString("TEOF_TIME");
			String co_nm						= data.getString("CO_NM");
			String cdhd_id						= data.getString("CDHD_ID");
			String email_id						= data.getString("EMAIL_ID");
			
			String hp_ddd_no					= data.getString("HP_DDD_NO");
			String hp_tel_hno					= data.getString("HP_TEL_HNO");
			String hp_tel_sno					= data.getString("HP_TEL_SNO");
			String memp_expl					= data.getString("MEMO_EXPL");
			String bkg_pe_nm					= data.getString("BKG_PE_NM");	//동반자
			String green_no					= data.getString("GREEN_NO");	//
			String golf_rsvt_curs_nm					= data.getString("GOLF_RSVT_CURS_NM");	//코스
			
			String rsvt_able_bokg_time_seq_no = "";
			String sql = this.getBootkingRsvtSeqQuery();
			pstmt = conn.prepareStatement(sql.toString());
			int rdx = 1;
			pstmt.setString(rdx++, green_no);
			pstmt.setString(rdx++, teof_time);
			pstmt.setString(rdx++, teof_date);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				rsvt_able_bokg_time_seq_no = rs.getString("RSVT_ABLE_BOKG_TIME_SEQ_NO");
			}
			
			int aplc_seq_no = 0;
			sql = this.getBootkingSeqQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				aplc_seq_no = rs.getInt("MAX_SEQ_NO");
			}
			debug("rsvt_able_bokg_time_seq_no"+rsvt_able_bokg_time_seq_no);
			// 이벤트 등록
			sql = this.getBootkingQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;	// 11
			pstmt.setInt(idx++, aplc_seq_no);
			pstmt.setString(idx++, green_nm);
			pstmt.setString(idx++, teof_date);
			pstmt.setString(idx++, teof_time);
			pstmt.setString(idx++, co_nm);
			pstmt.setString(idx++, cdhd_id);
			pstmt.setString(idx++, email_id);
			
			pstmt.setString(idx++, bkg_pe_nm);
			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);
			pstmt.setString(idx++, memp_expl);
			pstmt.setString(idx++, green_no);
			pstmt.setString(idx++, rsvt_able_bokg_time_seq_no);
			pstmt.setString(idx++, golf_rsvt_curs_nm);			//코스
			
			
			
			result = pstmt.executeUpdate();
			
			//예약 구분 Y로 체크
			/*sql = this.updEvntYnQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			idx = 1;	// 11
			pstmt.setString(idx++, teof_date);
			pstmt.setString(idx++, teof_time);
			result = pstmt.executeUpdate();*/
			
			
			if(pstmt != null) pstmt.close();
			conn.setAutoCommit(false);
			

			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
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
	 * Proc 실행.가결제 테이블에 데이터 인서트
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int inputTemp(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String green_no							= data.getString("GREEN_NO");
			String temp_pay_date					= data.getString("TEMP_PAY_DATE");
			String card_no							= data.getString("CARD_NO");
			String breach_amt						= data.getString("BREACH_AMT");
			String round_date						=data.getString("TEOF_DATE");	//
			int memId							=data.getInt("MEMNO");			//

			int bkng_req_no = 0; 
			String sql = this.getBootkingISeqQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				bkng_req_no = rs.getInt("MAX_SEQ_NO");
			}

			// 이벤트 등록
			sql = this.getTempQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;	// 11
			pstmt.setString(idx++, round_date);
			pstmt.setInt(idx++, bkng_req_no);
			pstmt.setString(idx++, green_no);
			pstmt.setInt(idx++, memId);
			pstmt.setString(idx++, breach_amt);
			pstmt.setString(idx++, card_no);
			
			
			result = pstmt.executeUpdate();
			
			if(pstmt != null) pstmt.close();
			conn.setAutoCommit(false);
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
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
	 * Proc 실행.확인/취소
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute_status(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String sql = this.getStatusQuery(data);   

			long page_no			= data.getLong("PAGE_NO");               //페이지번호
			long record_size		= data.getLong("RECORD_SIZE");           //페이지당 출력될 갯수

			String cdhd_id			= data.getString("CDHD_ID");            //예약골프장명
			     
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);
			pstmt.setLong(++idx, record_size);
			pstmt.setLong(++idx, page_no);
			pstmt.setString(++idx, cdhd_id);
			pstmt.setLong(++idx, page_no);
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("APLC_SEQ_NO",rs.getString("APLC_SEQ_NO"));
					result.addString("PGRS_YN",rs.getString("PGRS_YN"));		//예약상태
					result.addString("CODE_PGRS_YN",rs.getString("CODE_PGRS_YN"));		//예약코드
					result.addString("GREEN_NM",rs.getString("GREEN_NM"));	//골프장명
					result.addString("TEOF_DATE",DateUtil.format(rs.getString("TEOF_DATE"),"yyyyMMdd","yyyy-MM-dd"));	//부킹일자
					//result.addString("real_TEOF_TIME" , rs.getString("TEOF_TIME"));
					
					String teof_time = rs.getString("TEOF_TIME");
					String real_teof_time = teof_time.substring(0,2) +":"+teof_time.substring(2,4);
					teof_time = teof_time.substring(0,2) + "시대";
					
					result.addString("TEOF_TIME",teof_time);	
					result.addString("real_TEOF_TIME",real_teof_time);
										
					
					result.addString("BKG_PE_NM",rs.getString("BKG_PE_NM"));
					result.addString("ADDR",rs.getString("ADDR"));
					result.addString("REG_ATON",DateUtil.format(rs.getString("REG_ATON"),"yyyyMMdd","yyyy-MM-dd"));	//신청일
					
					if(rs.getString("CHNG_ATON") == null){
						result.addString("CHNG_ATON","");	//확정일
						
					}else{ 
						result.addString("CHNG_ATON",rs.getString("c_HOUR")+":"+rs.getString("c_MIN") );	//확정일 
					}
					result.addString("IS_CANCEL"		,rs.getString("IS_CANCEL") );
					result.addString("UPD_BTN"		,rs.getString("UPD_BTN") );
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					result.addString("RESULT", "00"); //정상결과*/
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
	 * Proc 실행.취소 처리
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int app_upd_pro(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			String aplc_seq_no						= data.getString("APLC_SEQ_NO");
			String pgrs_yn						= data.getString("PGRS_YN");
			
			
			/*if(pgrs_yn.equals("R")){
				pgrs_yn_cancel = "A";
			}else if(pgrs_yn.equals("B")){
				pgrs_yn_cancel = "C";
			}*/

			// 취소 처리
			String sql = this.getCanProcess();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;	// 11
			pstmt.setString(idx++, pgrs_yn);
			pstmt.setString(idx++, aplc_seq_no);
			
			result = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			conn.setAutoCommit(false);
			

			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
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
	 * 탑골프 부킹확정취소시 담당자에 sms 발송
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult cancelSmsExe(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {

			String hpDdd = "", hpTelHno = "", hpTelSno = "";
			String sql = this.getTopGolfResp();	
			String green_nm		= data.getString("green_nm");
			String teof_date	= data.getString("teof_date");
			String teof_time	= data.getString("teof_time");
			String userNm		= data.getString("userNm");
			
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(sql);			
			rs = pstmt.executeQuery();
			
			SmsSendProc smsProc = (SmsSendProc)context.getProc("SmsSendProc");
						
			if(rs != null) {
				
				while(rs.next())  {
					
					String hp [] = rs.getString("EXPL").split("-");

					HashMap smsMap = new HashMap();
					
					smsMap.put("ip", request.getRemoteAddr());
					smsMap.put("sName", userNm);
					smsMap.put("sPhone1", hp[0]);
					smsMap.put("sPhone2", hp[1]);
					smsMap.put("sPhone3", hp[2]);
					 
					String smsClss = "637";
					String message = "[TOP부킹취소] "+userNm+", "+green_nm+" "+teof_date+" "+teof_time+" [관리자전용]";
					
					smsProc.send(smsClss, smsMap, message);
				
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
	public DbTaoResult getTtimelist(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String sql = this.getSelectTtimeQuery();   

			String affi_green_seq_no	= data.getString("affi_green_seq_no");     //seq 
			String teof_date	= data.getString("teof_date"); 	 //date
			
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());

			pstmt.setString(++idx, affi_green_seq_no);
			pstmt.setString(++idx, teof_date);

				
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					if(!rs.getString("BOKG_ABLE_TIME").equals("")){
						result.addString("GOLF_RSVT_CURS_NM",rs.getString("GOLF_RSVT_CURS_NM"));
						result.addString("BOKG_ABLE_TIME",rs.getString("BOKG_ABLE_TIME"));
						result.addString("BOKG_ABLE_TIME_YN",rs.getString("BOKG_ABLE_TIME")+":"+rs.getString("EVNT_YN")+":"+rs.getString("GOLF_RSVT_CURS_NM"));
						result.addString("RESULT", "00"); //정상결과*/
					}
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
	
	/*
	 * 중목 아이디 확인
	 * 
	 * */
	public int execute_idsubmit(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String memb_id 	= data.getString("MEMB_ID");
			String green_nm 	= data.getString("GREEN_NM");
			String teof_date 	= data.getString("TEOF_DATE");
			String teof_time 	= data.getString("TEOF_TIME");
			
			String sql = this.getCntIdQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, memb_id);
			pstmt.setString(++idx, green_nm);
			pstmt.setString(++idx, teof_date);
			pstmt.setString(++idx, teof_time);

			rs = pstmt.executeQuery();

			if ( rs != null ) {
				while(rs.next())  {	
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
	
	/*
	 *티타임 신청자수
	 * 
	 * */
	public int execute_appCnt(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String green_nm 	= data.getString("GREEN_NM");
			String teof_date 	= data.getString("TEOF_DATE");
			String teof_time 	= data.getString("TEOF_TIME");
			
			String sql = this.getCntAppQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, green_nm);
			pstmt.setString(++idx, teof_date);
			pstmt.setString(++idx, teof_time);

			rs = pstmt.executeQuery();

			if ( rs != null ) {
				while(rs.next())  {	
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
	 * Proc 실행.	ttime 마감으로 업데이트
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute_epsYn(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			
			
			String green_no					= data.getString("GREEN_NO");	
			String teof_date					= data.getString("TEOF_DATE");
			String teof_time					= data.getString("TEOF_TIME");
			String rsvt_able_bokg_time_seq_no = "";
			
			
			String sql = this.getBootkingRsvtSeqQuery();
			pstmt = conn.prepareStatement(sql.toString());
			int rdx = 0;
			pstmt.setString(++rdx, green_no);
			pstmt.setString(++rdx, teof_time);
			pstmt.setString(++rdx, teof_date);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				rsvt_able_bokg_time_seq_no = rs.getString("RSVT_ABLE_BOKG_TIME_SEQ_NO");
			}
			debug("rsvt_able_bokg_time_seq_no"+rsvt_able_bokg_time_seq_no);
			// 이벤트 등록
			
			int idx = 0;
			sql = this.getUpdateTtimeSQL();
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, rsvt_able_bokg_time_seq_no);
			result = pstmt.executeUpdate();
			if(pstmt != null) pstmt.close();
			conn.setAutoCommit(false);
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {			
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}
	
	
	/*
	 * 코스구하기
	 * 
	 * */
	public String getCourse(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String result =  "";
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String bokg_able_date 	= data.getString("BOKG_ABLE_DATE");
			String affi_green_seq_no 	= data.getString("AFFI_GREEN_SEQ_NO");
			String sql = this.getCourseQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			
			debug("@@ affi_green_seq_no : "+affi_green_seq_no);
			pstmt.setString(++idx, bokg_able_date);
			pstmt.setString(++idx, affi_green_seq_no);

			rs = pstmt.executeQuery();

			if ( rs != null ) {
				while(rs.next())  {	
					result = rs.getString("GOLF_RSVT_CURS_NM");
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
	
	 /*
 	 *골프 라운지에서 탑골프 회원인지 체크한다
 	 * 
 	 * */
 	public int is_topMember(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
 		
 		ResultSet rs = null;
 		Connection conn = null;
 		PreparedStatement pstmt = null;
 		int result =  0;
 		int idx = 0;

 		try {
 			conn = context.getDbConnection("default", null); 
 			
 			String memId 	= data.getString("memId");
 			
 			String sql = this.getisTopCntQuery();   
 			pstmt = conn.prepareStatement(sql.toString());
 			pstmt.setString(++idx, memId);

 			rs = pstmt.executeQuery();

 			if ( rs != null ) {
 				while(rs.next())  {	
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
 	
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.     
    ************************************************************************ */
    private String getSelectQuery(int cnt_day, int nHour){
        StringBuffer sql = new StringBuffer();

		String tb_name = "";
		
		sql.append("\n 	SELECT * FROM   "); 
		sql.append("\n 	    (SELECT T1.AFFI_GREEN_SEQ_NO, T1.GREEN_NM AS GR_NM FROM BCDBA.TBGAFFIGREEN T1 WHERE AFFI_FIRM_CLSS='1000' AND MAIN_EPS_YN='Y') T1");
	    
		for (int i=0; i<14; i++){
			tb_name = "TT"+i;
			sql.append("\n 	    LEFT JOIN");
			sql.append("\n 	    (");
			sql.append("\n 	    	SELECT T3.AFFI_GREEN_SEQ_NO AS AFFI_GREEN_SEQ_NO, COUNT(*) TOTAL_"+i+"");
			sql.append("\n 	        FROM BCDBA.TBGRSVTABLESCDMGMT T3");
			sql.append("\n 	        JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T4 ON T3.RSVT_ABLE_SCD_SEQ_NO=T4.RSVT_ABLE_SCD_SEQ_NO ");
			sql.append("\n 	        JOIN BCDBA.TBGAFFIGREEN T5 ON T3.AFFI_GREEN_SEQ_NO=T5.AFFI_GREEN_SEQ_NO");
			sql.append("\n 	        WHERE T3.BOKG_ABLE_DATE=TO_CHAR(SYSDATE+"+ (cnt_day+i) +",'YYYYMMDD')");
			sql.append("\n 	        AND T4.EPS_YN='Y'");
			sql.append("\n 	        AND T4.BOKG_RSVT_STAT_CLSS='1000' ");
			
			if(nHour>=17){
				sql.append("\n 	        AND T3.BOKG_ABLE_DATE>=TO_CHAR(SYSDATE+T5.BOKG_TIME_COLL_TRM+1,'YYYYMMDD')");
			}else{
				sql.append("\n 	        AND T3.BOKG_ABLE_DATE>=TO_CHAR(SYSDATE+T5.BOKG_TIME_COLL_TRM,'YYYYMMDD')");
			}
			
			sql.append("\n 	        AND T3.GOLF_RSVT_DAY_CLSS='T' AND T5.GREEN_ID='springhills'");
			sql.append("\n 	        GROUP BY T3.AFFI_GREEN_SEQ_NO");
			sql.append("\n 	    	UNION ALL");
			
			sql.append("\n 	    	SELECT T3.AFFI_GREEN_SEQ_NO AS AFFI_GREEN_SEQ_NO, COUNT(*) TOTAL_"+i+"");
			sql.append("\n 	        FROM BCDBA.TBGRSVTABLESCDMGMT T3");
			sql.append("\n 	        JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T4 ON T3.RSVT_ABLE_SCD_SEQ_NO=T4.RSVT_ABLE_SCD_SEQ_NO ");
			sql.append("\n 	        JOIN BCDBA.TBGAFFIGREEN T5 ON T3.AFFI_GREEN_SEQ_NO=T5.AFFI_GREEN_SEQ_NO");
			sql.append("\n 	        WHERE T3.BOKG_ABLE_DATE=TO_CHAR(SYSDATE+"+ (cnt_day+i) +",'YYYYMMDD')");
			sql.append("\n 	        AND T4.EPS_YN='Y'");
			sql.append("\n 	        AND T4.BOKG_RSVT_STAT_CLSS='1000' ");
			sql.append("\n 	        AND T3.BOKG_ABLE_DATE>=TO_CHAR(SYSDATE+T5.BOKG_TIME_COLL_TRM,'YYYYMMDD')");
			sql.append("\n 	        AND T3.GOLF_RSVT_DAY_CLSS='T' AND T5.GREEN_ID<>'springhills'");
			sql.append("\n 	        GROUP BY T3.AFFI_GREEN_SEQ_NO");
			sql.append("\n 	    )"+tb_name+" ");
			sql.append("\n 	    ON T1.AFFI_GREEN_SEQ_NO="+tb_name+".AFFI_GREEN_SEQ_NO");
		}
	    
		sql.append("\n 	ORDER BY T1.AFFI_GREEN_SEQ_NO ");
	

		return sql.toString();
    }
    
    /** ***********************************************************************
     * ttime 취소처리
     ************************************************************************ */
     private String getCanProcess(){
         StringBuffer sql = new StringBuffer();	

 		sql.append("\n	UPDATE  BCDBA.TBGAPLCMGMT SET 	");
 		sql.append("\n		PGRS_YN = ? 	");
 		sql.append("\n	    WHERE APLC_SEQ_NO = ?	");
 		sql.append("\n		");

 		return sql.toString();
     }
    
    /** ***********************************************************************
     *  월례회 상세보기
     ************************************************************************ */
     public String getSelectViewQuery(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n 			SELECT AFFI_GREEN_SEQ_NO, DTL_ADDR,CURS_SCAL_INFO " );
 		sql.append("\n 			FROM BCDBA.TBGAFFIGREEN ");
 		sql.append("\n			WHERE AFFI_GREEN_SEQ_NO = ? 	");
 		return sql.toString();
 	}
     
     /** ***********************************************************************
      * 이벤트 max_seq 가져오기
      ************************************************************************ */
      private String getBootkingSeqQuery(){
          StringBuffer sql = new StringBuffer();
          
  		sql.append("\n SELECT NVL(MAX(APLC_SEQ_NO),0)+1 MAX_SEQ_NO FROM BCDBA.TBGAPLCMGMT	\n");
  		
  		return sql.toString();
      }
      
      /** ***********************************************************************
       * 이벤트 max_seq 가져오기
       ************************************************************************ */
       private String getBootkingRsvtSeqQuery(){
           StringBuffer sql = new StringBuffer();
           
   		sql.append("\n SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO from BCDBA.TBGRSVTABLESCDMGMT A 	\n");
   		sql.append("\n JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT B ON A.RSVT_ABLE_SCD_SEQ_NO=B.RSVT_ABLE_SCD_SEQ_NO 	\n");
   		sql.append("\n WHERE A.AFFI_GREEN_SEQ_NO = ? 	\n");
   		sql.append("\n AND B.BOKG_ABLE_TIME = ? 	\n");
   		sql.append("\n AND A.BOKG_ABLE_DATE = ? 	\n");
   		sql.append("\n AND B.BOKG_RSVT_STAT_CLSS='1000' 	\n");
   		
   		return sql.toString();
       }
      
      /** ***********************************************************************
       * 이벤트 max_seq 가져오기
       ************************************************************************ */
       private String getBootkingISeqQuery(){
           StringBuffer sql = new StringBuffer();
           
   		sql.append("\n SELECT NVL(MAX(APLC_SEQ_NO),0) MAX_SEQ_NO FROM BCDBA.TBGAPLCMGMT	\n");
   		
   		return sql.toString();
       }
      
      /** ***********************************************************************
       * 이벤트 max_seq 가져오기
       ************************************************************************ */
       private String getTempSeqQuery(){
           StringBuffer sql = new StringBuffer();
           
   		sql.append("\n SELECT NVL(MAX(BKNG_REQ_NO),0)+1 BKNG_REQ_NO FROM BCDBA.TBGFTEMPPAY	\n");
   		
   		return sql.toString();
       }
      
      /** ***********************************************************************
       * 이벤트 등록 
       ************************************************************************ */
       private String updEvntYnQuery(){
           StringBuffer sql = new StringBuffer();	

   		sql.append("\n	UPDATE  BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET 	");
   		sql.append("\n		EVNT_YN = 'N' 	");
   		sql.append("\n	   WHERE  RSVT_ABLE_SCD_SEQ_NO IN(select RSVT_ABLE_SCD_SEQ_NO from BCDBA.TBGRSVTABLESCDMGMT WHERE BOKG_ABLE_DATE=  ?)	");
   		sql.append("\n	    AND  BOKG_ABLE_TIME =?	");
   		sql.append("\n		");

   		return sql.toString();
       }
       
       /** ***********************************************************************
        * Query를 생성하여 리턴한다.  (부킹 확인/취소   
        ************************************************************************ */
    	
        private String getStatusQuery(TaoDataSet data) throws BaseException{
            StringBuffer sql = new StringBuffer();

            String green_nm			= data.getString("green_nm");               
    		String pgrs_yn	= data.getString("pgrs_yn");
    		
    		String sch_date_gubun	= data.getString("sch_date_gubun");
    		String sch_reg_aton_st	= data.getString("sch_reg_aton_st"); 
    		String sch_reg_aton_ed	= data.getString("sch_reg_aton_ed");
    		
    		String sch_type			= data.getString("sch_type");
    		String search_word		= data.getString("search_word");   
    		 
    		sql.append("\n     SELECT E.*                                                                                                 ");
    		sql.append("\n       FROM (SELECT D.*,ROWNUM RNUM, CEIL(ROWNUM/?) AS PAGE,MAX(RN) OVER() TOT_CNT,                           ");
    		sql.append("\n                    (MAX(RN) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO                              ");
    		sql.append("\n               FROM (SELECT ROWNUM RN,APLC_SEQ_NO,           ");
    		sql.append("\n                            (CASE WHEN PGRS_YN ='A' THEN '예약취소'  WHEN PGRS_YN = 'R' THEN '예약신청' WHEN PGRS_YN = 'W' THEN '부킹대기' WHEN PGRS_YN = 'B' THEN '부킹확정' WHEN PGRS_YN = 'F' THEN '실패'  WHEN PGRS_YN = 'C' THEN '부킹취소' END) AS PGRS_YN,               ");
    		//sql.append("\n                            (CASE WHEN PGRS_YN ='A' THEN '부킹신청'  WHEN PGRS_YN = 'S' THEN '부킹확정' ELSE  '부킹취소' END) AS PGRS_YN,               ");
    		sql.append("\n                            (CASE WHEN PGRS_YN ='S' THEN 'is'  WHEN PGRS_YN = 'R' THEN 'is'  ELSE  'not' END) AS UPD_BTN,               ");
    		sql.append("\n                            (CASE WHEN TEOF_DATE >= TO_CHAR(SYSDATE+4,'YYYYMMDD') THEN 'PO' ELSE 'IMPO' END) IS_CANCEL,               ");
    		sql.append("\n                            PGRS_YN AS CODE_PGRS_YN,               ");
    		sql.append("\n                            GREEN_NM,TEOF_DATE,TEOF_TIME,CO_NM,CDHD_ID,BKG_PE_NM,                ");
    		sql.append("\n                            HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,ADDR,                ");
    		sql.append("\n                            SUBSTR(MEMO_EXPL,1,20) MEMO_EXPL,SUBSTR(REG_ATON,1,8) REG_ATON,SUBSTR(CHNG_ATON,1,8) CHNG_ATON , SUBSTR(CHNG_ATON,1,2) as c_HOUR,SUBSTR(CHNG_ATON,3,2) as c_MIN           ");
    		sql.append("\n                       FROM BCDBA.TBGAPLCMGMT								                                 ");
    		sql.append("\n                      WHERE GOLF_SVC_APLC_CLSS = '1000'                                                     ");
    		sql.append("\n                      AND CDHD_ID = ?                                                                   ");
    		sql.append("\n                                                                                         ");
    		sql.append("\n                ORDER BY APLC_SEQ_NO DESC          ) D                                                     ");
    		sql.append("\n                                                                                                            ");
    		sql.append("\n             ) E                                                                                             ");
    		sql.append("\n      WHERE PAGE = ?   ");
    		return sql.toString();
        } 
        
        
        /** ***********************************************************************
         * 신청테이블 등록 
         ************************************************************************ */
         private String getBootkingQuery(){
             StringBuffer sql = new StringBuffer();	

     		sql.append("\n	INSERT INTO BCDBA.TBGAPLCMGMT (	");
     		sql.append("\n		GOLF_SVC_APLC_CLSS, PGRS_YN, APLC_SEQ_NO, GREEN_NM, TEOF_DATE, TEOF_TIME, CO_NM, CDHD_ID,EMAIL, BKG_PE_NM 	");
     		sql.append("\n	    ,HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,MEMO_EXPL, REG_ATON, LESN_SEQ_NO,GOLF_LESN_RSVT_NO, ADDR	");
     		sql.append("\n	) VALUES (	");
     		sql.append("\n	    '1000', 'R'  , ?, ?,  ?, ?, ?, ?, ?, ? , ?	");
     		sql.append("\n	    , ?, ?, ? ,TO_CHAR(SYSDATE,'YYYYMMDD'), ?,? ,?	");
     		sql.append("\n	)	");

     		return sql.toString();
         }
         
         /** ***********************************************************************
          * 가결제 등록
          ************************************************************************ */
          private String getTempQuery(){
              StringBuffer sql = new StringBuffer();	
      		
      		sql.append("\n insert into BCDBA.TBGFTEMPPAY ");
    		sql.append("\n (ROUND_DATE, BKNG_OBJ_NO, BKNG_REQ_NO, GREEN_NO, MEMID, BREACH_AMT, ");
    		sql.append("\n CARD_NO, TEMP_PAY_DATE) ");
            sql.append("\n values (?, 100, ?, ?, ?, ?, ");
    		sql.append("\n ?, to_char(sysdate,'YYYYMMDDHH24MISS')) ");
      		
    		return sql.toString();
          }
         
         
         /** ***********************************************************************
          * Query를 생성하여 리턴한다.  (Ttime)
          ************************************************************************ */ 
      	
          private String getSelectTtimeQuery() throws BaseException{
              StringBuffer sql = new StringBuffer();

      		 
      		sql.append("\n     SELECT B.BOKG_ABLE_TIME,B.EVNT_YN,A.GOLF_RSVT_CURS_NM as GOLF_RSVT_CURS_NM from BCDBA.TBGRSVTABLESCDMGMT A                                                                                                ");
      		sql.append("\n     JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT B ON A.RSVT_ABLE_SCD_SEQ_NO=B.RSVT_ABLE_SCD_SEQ_NO                           ");
      		sql.append("\n     WHERE A.AFFI_GREEN_SEQ_NO = ?                             ");
      		sql.append("\n     AND A.BOKG_ABLE_DATE = ?           ");
      		sql.append("\n     AND B.BOKG_RSVT_STAT_CLSS='1000'           "); 
      		sql.append("\n     AND B.EPS_YN = 'Y'           ");
      		 
      		return sql.toString();
          } 
        
          
          /** ***********************************************************************
           * 같은 ID로 등록된 신청내역이 있는지 알아본다.
           ************************************************************************ */
           private String getCntIdQuery(){
               StringBuffer sql = new StringBuffer();

       		sql.append("\n	SELECT COUNT(*) as CNT  FROM BCDBA.TBGAPLCMGMT 	\n");
       		sql.append("\n	WHERE CDHD_ID =? AND GREEN_NM=? AND TEOF_DATE=? AND TEOF_TIME LIKE '%' || ? || '%'	\n");
       		sql.append("\n	AND GOLF_SVC_APLC_CLSS = '1000'	\n");
       		
       		return sql.toString();
           }
           
           /** ***********************************************************************
            * 같은ttile신청자수를 구한다
            ************************************************************************ */
            private String getCntAppQuery(){
                StringBuffer sql = new StringBuffer();

        		sql.append("\n	SELECT COUNT(*) as CNT  FROM BCDBA.TBGAPLCMGMT 	\n");
        		sql.append("\n	WHERE GREEN_NM=? AND TEOF_DATE=? AND TEOF_TIME LIKE '%' || ? || '%'	\n");
        		sql.append("\n	AND GOLF_SVC_APLC_CLSS = '1000'	\n");
        		
        		return sql.toString();
            }
           
           /** ***********************************************************************
            * 코스 알아오기.
            ************************************************************************ */
            private String getCourseQuery(){
                StringBuffer sql = new StringBuffer();

        		sql.append("\n	SELECT GOLF_RSVT_CURS_NM   FROM BCDBA.TBGRSVTABLESCDMGMT 	\n");
        		sql.append("\n	 where GOLF_RSVT_DAY_CLSS = 'T' AND BOKG_ABLE_DATE = ?  AND AFFI_GREEN_SEQ_NO = ?	\n");
        		
        		return sql.toString();
            }
            
            /** ***********************************************************************
             * Query를 생성하여 리턴한다.    
             ************************************************************************ */
         	
             private String getUpdateTtimeSQL() throws BaseException{
         		StringBuffer sql = new StringBuffer();
         		sql.append("\n     UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT            ");
         		sql.append("\n        SET EPS_YN = 'S'           ");
         		sql.append("\n      WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO = ?                  ");
         		return sql.toString();
         	}
             
             /** ***********************************************************************
              * 같은ttile신청자수를 구한다
              ************************************************************************ */
              private String getisTopCntQuery(){
                  StringBuffer sql = new StringBuffer();

          		sql.append("\n	select count(*) CNT from  BCDBA.TBGGOLFCDHDGRDMGMT 	\n");
          		sql.append("\n	where  cdhd_id=   ? \n");
          		sql.append("\n  and cdhd_ctgo_seq_no =21	\n");
          		
          		return sql.toString();
              }
             
             
            
        
        /** 점수 받아오기 시작**/
        
        public DbTaoResult get_score(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException {
        	String title = dataSet.getString("점수받아오기");        
            String message_key = "CommonProc_0000";
            PreparedStatement pstmt = null;
            ResultSet rset = null;
            DbTaoResult result = null; 
            Connection con = null;
    		
    		//카드이용실적
    		double useAmt = 0.0;					//이용금액
    		double evalScoreUseAmt = 0.0;			//평가점수(이용금액합계)
    		double useAmtApply = 0.0;				//반영율(이용금액)
    		double useAmtScore = 0.0;				//이용금액 평가점수
    		String useAmtMemo = "";					//이용금액 평가 메모
    		
    		//신규가입
    		double monthsFromJoin = 0.0;			//신규가입 경과 월수
    		double bookingCount = 0.0;				//부킹횟수
    		double evalScoreMonths = 0.0;			//평가점수(신규가입 경과 월수)
    		double monthsApply = 0.0;				//반영율(신규가입)
    		double monthsScore = 0.0;				//신규가입 평가점수
    		String monthsMemo = "";					//신규가입 평가 메모

    		//부킹실적
    		double bookingCount6M = 0.0;			//최근 6개월 간 부킹횟수
    		double evalScoreBookingCount = 0.0;		//평가점수(최근 6개월 간 부킹횟수)
    		double bookingCountApply = 0.0;			//반영율(부킹실적)
    		double bookingCountScore = 0.0;			//부킹실적 평가점수
    		String bookingCountMemo = "";			//부킹실적 평가 메모
    		
    		//부킹취소실적
    		double cancelCount6M = 0.0;				//최근 6개월 간 부킹취소횟수
    		double evalScoreCancelCount = 0.0;		//평가점수(최근 6개월 간 부킹취소횟수)
    		double cancelCountApply = 0.0;			//반영율(부킹취소횟수)
    		double cancelCountScore = 0.0;			//부킹취소실적 평가점수
    		String cancelCountMemo = "";			//부킹취소실적 평가 메모

    		//사이트활동
    		double pointCountPlus6M = 0.0;			//최근 6개월 간 사이트활동(글쓰기)
    		double pointCountMinus6M = 0.0;			//최근 6개월 간 사이트활동(비방글쓰기)
    		double evalScorePointCountPlus = 0.0;	//평가점수(최근 6개월 간 사이트활동-글쓰기)
    		double pointCountPlusScore = 0.0;		//평가점수(최근 6개월 간 사이트활동-글쓰기)
    		String pointCountPlusMemo = "";			//사이트활동(글쓰기) 평가 메모
    		double evalScorePointCountMinus = 0.0;	//평가점수(최근 6개월 간 사이트활동-비방글쓰기)
    		double pointCountMinusScore = 0.0;		//평가점수(최근 6개월 간 사이트활동-비방글쓰기)
    		String pointCountMinusMemo = "";		//사이트활동(비방글쓰기) 평가 메모
    		double pointCountApply = 0.0;			//반영율(사이트활동)
    		double pointCountScore = 0.0;			//사이트활동 평가점수		
    		
    		//골프매너불량		
    		double evalScoreNoManner = 0.0;			//평가점수(최근 6개월 간 골프매너불량)
    		double noMannerApply = 0.0;				//반영율(골프매너불량)
    		double noMannerScore = 0.0;				//골프매너불량 평가점수
    		String noMannerMemo = "";				//골프매너불량 평가 메모
    		double noMannerScoreSum = 0.0;			//골프매너불량 평가점수 합계

    		//평가점수 총합계
    		double evalScore = 0.0;		

            try{
            	con = context.getDbConnection("default", null);
                //조회 조건 Validation
    			String memId = dataSet.getString("memId");
    			long memNo = dataSet.getLong("memNo");			
    			String memSocId = dataSet.getString("memSocId");			//주민등록번호, 사업자 등록번호
    			//String golfJoinDate = dataSet.getString("golfJoinDate"); 	//
    			String roundDate = dataSet.getString("roundDate");			//비교일자(오늘일자 또는 라운딩일자) => 옵션(디폴트:SYSDATE)
    			String memberClss = dataSet.getString("memberClss");		//법인(3, 5), 개인(1) 구분 => 디폴트 : 1
    			
    			debug("@@@ memId :" + memId);
				debug("@@@@@@ memSocId :" + memSocId);
				debug("@@@@@@@@ roundDate :" + roundDate);
				debug("@@@@@@@@@ memberClss :" + memberClss);

				 if (memNo == 0L){
					 message_key = "MyGolfProc_0001";
                    throw new Exception(message_key);
                } 
    			if(memSocId.equals("")){
    				message_key = "MyGolfProc_0011";
                    throw new Exception(message_key);
    			}
    			//if(golfJoinDate.equals("")){
    			//	MESSAGE_KEY = "MyGolfProc_0010";
                //    throw new Exception(MESSAGE_KEY);
    			//}
    			if(roundDate.equals("")){ 
    				roundDate = DateUtil.currdate("yyyyMMdd");
    			}
    			if(memberClss.equals("")){
    				memberClss = "1";
    			}

                //-------------
    			
    			result = new DbTaoResult(title);
    //이용실적반영기준----------------------------------------------------
    			String currMonth = DateUtil.currdate("yyyyMM");
    			String appliedMonth = currMonth;
    			String basicMonth = currMonth;		 

    			String sql = this.getSelectQueryBasicMonth();//이용금액
    			pstmt = con.prepareStatement(sql);
    			rset = pstmt.executeQuery();
    			if(rset.next()){
    				appliedMonth = rset.getString("applied_month");
    				debug("@@@@@appliedMonth"+appliedMonth);
    			}
    			
    			basicMonth = DateUtil.dateAdd('M', -5, appliedMonth, "yyyyMM");

    			debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ currMonth:" + currMonth);
    			debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ appliedMonth:" + appliedMonth);
    			debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ basicMonth:" + basicMonth);
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();
                
                
    //카드이용실적-------------------------------------------------------
                con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery(dataSet);//이용금액
                pstmt = con.prepareStatement(sql);
    			int pidx = 0;
    			//pstmt.setString(++pidx, cardNo);
    			pstmt.setString(++pidx, memSocId);
    			pstmt.setString(++pidx, basicMonth);
    			pstmt.setString(++pidx, appliedMonth);
                rset = pstmt.executeQuery();
                if(rset.next()){
                    useAmt = rset.getDouble("use_amt");
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();

    			useAmtMemo = "이용실적 반기 " + StrUtil.parseMoney(useAmt+"", ",") + "원";//

    			/*2005.05.03*/
    			if("3".equals(memberClss) || "5".equals(memberClss)){
    				useAmt = (double)useAmt/2.0;
    			}
    			/*2005.05.03*/
    			con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery2();//평가점수(이용금액합계)
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
    			pstmt.setDouble(++pidx, useAmt);
    			pstmt.setDouble(++pidx, useAmt);
                rset = pstmt.executeQuery();
                if(rset.next()){
                    evalScoreUseAmt = rset.getDouble("eval_score");
                    useAmtApply = rset.getDouble("eval_apply_psnt");
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();
                
    			useAmtScore = useAmtApply * evalScoreUseAmt;//이용금액 평가점수

    			result.addString("class", "이용실적");
    			result.addString("memo", useAmtMemo);
    			result.addLong("score", Math.round(useAmtScore));
    			result.addLong("apply", Math.round(useAmtApply*100));
    			debug("useAmtScore(" + useAmtScore + ") = useAmtApply(" + useAmtApply + ") * evalScoreUseAmt(" + evalScoreUseAmt + ")");
    //신규가입-----------------------------------------------------------
    				con = context.getDbConnection("default", null);
    				sql = this.getSelectQuery5();//평가점수(신규가입 경과 월수)
    				pstmt = con.prepareStatement(sql);
    				pidx = 0;
    				pstmt.setDouble(++pidx, monthsFromJoin);
    				pstmt.setDouble(++pidx, monthsFromJoin);
    				rset = pstmt.executeQuery();
    				if(rset.next()){
    					evalScoreMonths = rset.getDouble("eval_score");
    					monthsApply = rset.getDouble("eval_apply_psnt");
    				}
    				if(rset  != null) rset.close();
    				if(pstmt != null) pstmt.close();
    				if(con != null) con.close();
    				monthsMemo = "골프카드 회원가입 기본 점수";
    				monthsScore = monthsApply * evalScoreMonths;//회원가입 평가점수

    				result.addString("class", "신규가입");
    				result.addString("memo", monthsMemo);
    				result.addLong("score", Math.round(monthsScore));
    				result.addLong("apply", Math.round(monthsApply*100));
    			/*2005.05.03
    			}
    			*/
    			debug("monthsScore(" + monthsScore + ") = monthsApply(" + monthsApply + ") * evalScoreMonths(" + evalScoreMonths + ")");
    //부킹실적-----------------------------------------------------------
    			con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery6();//최근 6개월 간 부킹횟수
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
    			pstmt.setLong(++pidx, memNo);
    			pstmt.setString(++pidx, memId);
                rset = pstmt.executeQuery();

    			int simpleBookingCount = 0;
                while(rset.next()){
                	
                    bookingCount6M = rset.getDouble("booking_count");
                    debug("@@@@@@@부킹카운트"+bookingCount6M);
    				double singleBookingScore = this.getBookingScore(rset.getString("green_no"), rset.getString("bkng_obj_clss"));
    				evalScoreBookingCount += bookingCount6M * singleBookingScore;
    				simpleBookingCount += bookingCount6M;
    				debug("evalScoreBookingCount=" + evalScoreBookingCount);
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();

                con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery7();//평가점수(최근 6개월 간 부킹횟수)
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
                rset = pstmt.executeQuery();
                if(rset.next()){
    				bookingCountApply = rset.getDouble("eval_apply_psnt");
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();
    			
    			bookingCountMemo = "부킹 횟수 반기 " + (int)simpleBookingCount + "회";
    			bookingCountScore = bookingCountApply * evalScoreBookingCount;//부킹실적 평가점수

    			result.addString("class", "예약실적");
    			result.addString("memo", bookingCountMemo);
    			result.addLong("score", Math.round(bookingCountScore));
    			result.addLong("apply", Math.round(bookingCountApply*100));

    			debug("bookingCountScore(" + bookingCountScore + ") = bookingCountApply(" + bookingCountApply + ") * evalScoreBookingCount(" + evalScoreBookingCount + ")");
    			
    			
    //부킹취소횟수-----------------------------------------------------------
    			con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery8();//최근 6개월 간 부킹취소횟수
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
    			pstmt.setLong(++pidx, memNo);
    			pstmt.setString(++pidx, memId);
                rset = pstmt.executeQuery();

    			int simpleCancelCount = 0;
                while(rset.next()){
                    cancelCount6M = rset.getDouble("booking_count");
    				double singleCancelScore = this.getCancelScore(rset.getString("green_no"), rset.getString("bkng_obj_clss"));
    				evalScoreCancelCount += cancelCount6M * singleCancelScore;
    				simpleCancelCount += cancelCount6M;
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();

                con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery9();//평가점수(최근 6개월 간 부킹취소횟수)
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
                rset = pstmt.executeQuery();
                if(rset.next()){
    				cancelCountApply = rset.getDouble("eval_apply_psnt");
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();
                

    			cancelCountMemo = "부킹 취소 횟수 반기 " + (int)simpleCancelCount + "회";
    			cancelCountScore = cancelCountApply * evalScoreCancelCount;//부킹취소횟수 평가점수

    			result.addString("class", "예약취소");
    			result.addString("memo", cancelCountMemo);
    			result.addLong("score", Math.round(cancelCountScore));
    			result.addLong("apply", Math.round(cancelCountApply*100));
    			debug("cancelCountScore(" + cancelCountScore + ") = cancelCountApply(" + cancelCountApply + ") * evalScoreCancelCount(" + evalScoreCancelCount + ")");
    //사이트활동-----------------------------------------------------------
    			con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery10();//최근 6개월 간 사이트활동(글쓰기)
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
    			pstmt.setLong(++pidx, memNo);
                rset = pstmt.executeQuery();
                if(rset.next()){
                    pointCountPlus6M = rset.getDouble("point_count");
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();

                con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery11();//평가점수(최근 6개월 간 사이트활동-글쓰기)
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
    			pstmt.setDouble(++pidx, pointCountPlus6M);
    			pstmt.setDouble(++pidx, pointCountPlus6M);
                rset = pstmt.executeQuery();
                if(rset.next()){
    				pointCountApply = rset.getDouble("eval_apply_psnt");
    				evalScorePointCountPlus = pointCountPlus6M * rset.getDouble("eval_score");
    				pointCountPlusScore = pointCountApply * evalScorePointCountPlus;				
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();

    			pointCountPlusMemo = "라운드후기/글쓰기 추천글 반기 " + (int)pointCountPlus6M + "건";

    			result.addString("class", "사이트활동");
    			result.addString("memo", pointCountPlusMemo);
    			result.addLong("score", Math.round(pointCountPlusScore));
    			result.addLong("apply", Math.round(pointCountApply*100));

    			con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery12();//최근 6개월 간 사이트활동(비방글쓰기)
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
    			pstmt.setLong(++pidx, memNo);
                rset = pstmt.executeQuery();
                if(rset.next()){
                    pointCountMinus6M = rset.getDouble("point_count");
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();

                con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery13();//평가점수(최근 6개월 간 사이트활동-비방글쓰기)
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
    			pstmt.setDouble(++pidx, pointCountMinus6M);
    			pstmt.setDouble(++pidx, pointCountMinus6M);
                rset = pstmt.executeQuery();
                if(rset.next()){
    				pointCountApply = rset.getDouble("eval_apply_psnt");
    				evalScorePointCountMinus = pointCountMinus6M * rset.getDouble("eval_score");
    				pointCountMinusScore = pointCountApply * evalScorePointCountMinus;
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();

    			pointCountMinusMemo = "비방 글쓰기 반기 " + (int)pointCountMinus6M + "건";

    			result.addString("class", "사이트활동");
    			result.addString("memo", pointCountMinusMemo);
    			result.addLong("score", Math.round(pointCountMinusScore));
    			result.addLong("apply", Math.round(pointCountApply*100));

    			pointCountScore = evalScorePointCountPlus + evalScorePointCountMinus;//사이트활동 평가점수
    			debug("pointCountScore(" + pointCountScore + ") = evalScorePointCountPlus(" + evalScorePointCountPlus + ")" + " + evalScorePointCountMinus(" + evalScorePointCountMinus + ")");

    //골프매너불량-----------------------------------------------------------
    			con = context.getDbConnection("default", null);
    			sql = this.getSelectQuery14();//평가점수(최근 6개월 간 골프매너불량)
                pstmt = con.prepareStatement(sql);
    			pidx = 0;
    			pstmt.setLong(++pidx, memNo);
                rset = pstmt.executeQuery();
                while(rset.next()){
                    evalScoreNoManner = rset.getDouble("eval_score");
    				noMannerApply = rset.getDouble("eval_apply_psnt");
    				
    				noMannerScore = noMannerApply * evalScoreNoManner;//골프매너불량 평가점수
    				noMannerMemo = rset.getString("bkng_eval_memo");

    				noMannerScoreSum += noMannerScore;//골프매너불량 평가점수 합계	
    				
    				result.addString("class", "60");
    				result.addString("memo", noMannerMemo);
    				result.addLong("score", Math.round(noMannerScore));
    				result.addLong("apply", Math.round(noMannerApply*100));
    				debug("noMannerScore(" + noMannerScore + ") = noMannerApply(" + noMannerApply + ") * evalScoreNoManner(" + evalScoreNoManner + ")");
                }
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();
    			
    //==========================================================================================================
                con = context.getDbConnection("default", null);
    			sql = this.getSelectQueryStd();//기준일자 조회(이용실적 기준월, 평가 기준 월)
    			pstmt = con.prepareStatement(sql);
    			pidx = 0;
    			pstmt.setString(++pidx, basicMonth);
                rset = pstmt.executeQuery();

    			String maxMonth = "";
    			String sixMonth = "";
    			String sixMonth2 = "";//이용실적의 경우
    			String thisMonth = "";
    			if(rset.next()){
    				maxMonth = rset.getString("max_month");
    				sixMonth = rset.getString("six_month");
    				sixMonth2 = rset.getString("six_month2");
    				thisMonth = rset.getString("this_month");
    			}
    			result.addString("maxMonth",maxMonth);
    			result.addString("sixMonth",sixMonth);
    			result.addString("sixMonth2",sixMonth2);
    			result.addString("thisMonth",thisMonth);
    			if(rset  != null) rset.close();
                if(pstmt != null) pstmt.close();
                if(con != null) con.close();
    //==========================================================================================================


    			//평가점수 총합계
    			evalScore += useAmtScore;
    			evalScore += monthsScore;
    			evalScore += bookingCountScore;
    			evalScore += cancelCountScore;
    			evalScore += pointCountScore;
    			evalScore += noMannerScoreSum;
    			debug("evalScore = " + evalScore);
                
    			result.addString("result","00");
    			result.addLong("evalScore", Math.round(evalScore));

            }catch(Exception e){
                MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, message_key, null );
                throw new DbTaoException(msgEtt,e);
            }finally{
                try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
                try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
                try{ if(con != null) con.close(); }catch( Exception ignored){}
            }
            return result;
        }

    //이용실적반영기준-------------------------------------------------------
        /** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQueryBasicMonth() throws Exception{//이용금액
    		StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT MAX(use_yyyymm) applied_month").append("\n");		
    		sql.append("FROM bcdba.tbgfuseamt").append("\n");
            return sql.toString();
        }

    //카드이용실적-------------------------------------------------------
        /** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery(TaoDataSet dataSet) throws Exception{//이용금액
    		//String cardNo = dataSet.getString("cardNo");
    		String memSocId = dataSet.getString("memSocId");
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT NVL(SUM(NVL(cntry_crdt_amt, 0) + NVL(cntry_cash_amt, 0) +  NVL(ovsea_crdt_amt, 0) +  NVL(ovsea_cash_amt, 0)), 0) use_amt").append("\n");		
    		sql.append("FROM bcdba.tbgfuseamt").append("\n");
    		sql.append("WHERE mem_soc_id = ? ").append("\n");
    		//sql.append("AND use_yyyymm >= TO_CHAR(ADD_MONTHS(SYSDATE, -?), 'YYYYMM')").append("\n");
    		sql.append("AND use_yyyymm >= ? ").append("\n");
    		sql.append("AND use_yyyymm <= ? ").append("\n");
            return sql.toString();
        }

    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery2(){//평가점수(이용금액합계)
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt").append("\n");		
    		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b").append("\n");
    		sql.append("WHERE a.bkng_eval_clss = '10'").append("\n");//카드이용실적
    		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND TO_NUMBER(a.srt_condition) < ?").append("\n");//이용금액합계
    		sql.append("AND TO_NUMBER(a.end_condition) >= ?").append("\n");//이용금액합계
    		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
    		sql.append("AND b.set_to = a.set_to").append("\n");
    		sql.append("AND b.set_from = a.set_from").append("\n");
            return sql.toString();
        }	

    //신규가입-----------------------------------------------------------
    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery3(){//부킹횟수
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT count(*) booking_count").append("\n");		
    		sql.append("FROM bcdba.tbgfbooking").append("\n");
    		sql.append("WHERE memid = ?").append("\n");
    		sql.append("AND bkng_stat = '31'").append("\n");
            return sql.toString();
        }

    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery4(){//신규가입 경과 월수
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT MONTHS_BETWEEN(TO_DATE(?, 'YYYYMMDD'), TO_DATE(?, 'YYYYMMDD')) passed_months").append("\n");		
    		sql.append("FROM DUAL").append("\n");
            return sql.toString();
        }

    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery5(){//평가점수(신규가입 경과 월수)
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt").append("\n");
    		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b").append("\n");
    		sql.append("WHERE a.bkng_eval_clss = '20'").append("\n");//신규가입
    		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND TO_NUMBER(a.srt_condition) < ?").append("\n");//월수
    		sql.append("AND TO_NUMBER(a.end_condition) >= ?").append("\n");//월수
    		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
    		sql.append("AND b.set_to = a.set_to").append("\n");
    		sql.append("AND b.set_from = a.set_from").append("\n");
            return sql.toString();
        }
    	
    //부킹실적-----------------------------------------------------------
    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery6(){//최근 6개월 간 부킹횟수
            StringBuffer sql = new StringBuffer();
            sql.append("\n");
            sql.append(" SELECT  COUNT(*) booking_count, green_no , bkng_obj_clss    \n");
            sql.append(" FROM   \n");
            sql.append(" (   \n");
            sql.append("     SELECT TO_CHAR(green_no) AS green_no , bkng_obj_clss  \n");
            sql.append("     FROM  bcdba.tbgfbooking  \n");
            sql.append("     WHERE memid =  ?  \n");
            sql.append("     AND bkng_stat = '31' \n");
            sql.append("     AND round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM') \n");
            
            sql.append("   UNION ALL");
            sql.append("     SELECT GREEN_NM AS green_no ,CDHD_NON_CDHD_CLSS AS bkng_obj_clss  \n");
            sql.append("     from BCDBA.TBGAPLCMGMT  \n");
            sql.append("     where teof_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')  \n");
            sql.append("     and  pgrs_yn = 'B' \n");
            sql.append("     AND GOLF_SVC_APLC_CLSS='1000' \n");
            sql.append("     AND cdhd_id =  ?  \n");
            sql.append(" )   \n");
            sql.append(" GROUP BY green_no, bkng_obj_clss   \n");
            return sql.toString();
        }

    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
    	private String getSelectQuery7(){//평가점수(부킹실적)
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT b.eval_apply_psnt/100 eval_apply_psnt").append("\n");		
    		sql.append("FROM bcdba.tbgfbkevlitem b").append("\n");
    		sql.append("WHERE b.bkng_eval_clss = '30'").append("\n");//부킹실적
    		sql.append("AND b.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND b.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
            return sql.toString();
        }

    //부킹취소횟수-----------------------------------------------------------
    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery8(){//최근 6개월 간 부킹취소횟수
        	 StringBuffer sql = new StringBuffer();
             sql.append("\n");
             sql.append(" SELECT  COUNT(*) booking_count, green_no , bkng_obj_clss    \n");
             sql.append(" FROM   \n");
             sql.append(" (   \n");
             sql.append("     SELECT TO_CHAR(green_no) AS green_no , bkng_obj_clss  \n");
             sql.append("     FROM  bcdba.tbgfbooking  \n");
             sql.append("     WHERE memid =  ?  \n");
             sql.append("     AND bkng_stat = '39' \n");
             sql.append("     AND round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM') \n");
             sql.append("   UNION ALL");
             sql.append("     SELECT GREEN_NM AS green_no ,CDHD_NON_CDHD_CLSS AS bkng_obj_clss  \n");
             sql.append("     from BCDBA.TBGAPLCMGMT  \n");
             sql.append("     where teof_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')  \n");
             sql.append("     and  pgrs_yn = 'E' \n");
             sql.append("     AND GOLF_SVC_APLC_CLSS='1000' \n");
             sql.append("     AND cdhd_id =  ?  \n");
             sql.append(" )   \n");
             sql.append(" GROUP BY green_no, bkng_obj_clss   \n");
             return sql.toString();
        }

    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
    	private String getSelectQuery9(){//평가점수(부킹취소횟수)
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT b.eval_apply_psnt/100 eval_apply_psnt").append("\n");		
    		sql.append("FROM bcdba.tbgfbkevlitem b").append("\n");
    		sql.append("WHERE b.bkng_eval_clss = '40'").append("\n");//부킹취소
    		sql.append("AND b.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND b.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");		
            return sql.toString();
        }

    //사이트활동-----------------------------------------------------------
    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery10(){//최근 6개월 간 사이트활동(글쓰기)
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT count(*) point_count").append("\n");		
    		sql.append("FROM bcdba.tbgfpoint").append("\n");
    		sql.append("WHERE memid = ?").append("\n");
    		sql.append("AND point_clss = '50'").append("\n");//사이트활동
    		sql.append("AND point_detl_cd IN ('5020', '5025') ").append("\n");//글쓰기
    		sql.append("AND round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')").append("\n");
            return sql.toString();
        }	

    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery11(){//평가점수(최근 6개월 간 사이트활동-글쓰기)
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt").append("\n");		
    		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b").append("\n");
    		sql.append("WHERE a.bkng_eval_clss = '50'").append("\n");//사이트활동
    		sql.append("AND a.bkng_detleval_clss IN ('5020', '5025') ").append("\n");//글쓰기
    		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND TO_NUMBER(a.srt_condition) < ?").append("\n");//최근 6개월 간 사이트활동(글쓰기)
    		sql.append("AND TO_NUMBER(a.end_condition) >= ?").append("\n");//최근 6개월 간 사이트활동(글쓰기)
    		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
    		sql.append("AND b.set_to = a.set_to").append("\n");
    		sql.append("AND b.set_from = a.set_from").append("\n");
            return sql.toString();
        }

    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery12(){//최근 6개월 간 사이트활동(비방글쓰기)
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT count(*) point_count").append("\n");		
    		sql.append("FROM bcdba.tbgfpoint").append("\n");
    		sql.append("WHERE memid = ?").append("\n");
    		sql.append("AND point_clss = '50'").append("\n");//사이트활동
    		sql.append("AND point_detl_cd IN ('5030', '5035') ").append("\n");//비방글쓰기
    		sql.append("AND round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')").append("\n");
            return sql.toString();
        }

    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQuery13(){//평가점수(최근 6개월 간 사이트활동-비방글쓰기)
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt").append("\n");		
    		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b").append("\n");
    		sql.append("WHERE a.bkng_eval_clss = '50'").append("\n");//사이트활동
    		sql.append("AND a.bkng_detleval_clss IN ('5030', '5035') ").append("\n");//비방글쓰기
    		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND TO_NUMBER(a.srt_condition) < ?").append("\n");//최근 6개월 간 사이트활동(비방글쓰기)
    		sql.append("AND TO_NUMBER(a.end_condition) >= ?").append("\n");//최근 6개월 간 사이트활동(비방글쓰기)
    		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
    		sql.append("AND b.set_to = a.set_to").append("\n");
    		sql.append("AND b.set_from = a.set_from").append("\n");
            return sql.toString();
        }

    //골프매너불량-----------------------------------------------------------
    	/** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
    	private String getSelectQuery14(){//최근 6개월 간 골프매너불량
            StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT a.eval_score, b.eval_apply_psnt/100 eval_apply_psnt, a.bkng_eval_memo").append("\n");
    		sql.append("FROM bcdba.tbgfbkevlstd a, bcdba.tbgfbkevlitem b, bcdba.tbgfpoint c ").append("\n");
    		sql.append("WHERE a.bkng_eval_clss = '60'").append("\n");//골프매너불량골프매너불량
    		sql.append("AND a.bkng_detleval_clss IN ('6010', '6015', '6020', '6025', '6030')").append("\n");//No Show,지각,복장불량,기타 수칙위반
    		sql.append("AND a.set_to >= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND a.set_from <= TO_CHAR(SYSDATE, 'YYYYMMDD')").append("\n");
    		sql.append("AND b.bkng_eval_clss = a.bkng_eval_clss").append("\n");
    		sql.append("AND b.set_to = a.set_to").append("\n");
    		sql.append("AND b.set_from = a.set_from").append("\n");
    		sql.append("AND c.memid = ?").append("\n");
    		sql.append("AND c.point_clss = a.bkng_eval_clss").append("\n");
    		sql.append("AND c.point_detl_cd = a.bkng_detleval_clss").append("\n");
    		sql.append("AND c.round_date >= TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYYMM')").append("\n");
            return sql.toString();
        }

    	//카드이용실적-------------------------------------------------------
        /** ***********************************************************************
        * Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectQueryStd() throws Exception{//이용금액
    		StringBuffer sql = new StringBuffer();
    		sql.append("\n");
    		sql.append("SELECT TO_CHAR(TO_DATE(MAX(use_yyyymm), 'YYYY.MM'), 'YYYY.MM') max_month, ").append("\n");
    		sql.append("	TO_CHAR(ADD_MONTHS(SYSDATE, -6), 'YYYY.MM') six_month, ").append("\n");
    		sql.append("	TO_CHAR(TO_DATE(?, 'YYYY.MM'), 'YYYY.MM') six_month2, ").append("\n");
    		sql.append("	TO_CHAR(SYSDATE, 'YYYY.MM') this_month").append("\n");
    		sql.append("FROM bcdba.tbgfuseamt").append("\n");
            return sql.toString();
        }

    //부킹 확정 시 점수
    	private double getBookingScore(String greenNo, String bkngObjClss){
    		if("2".equals(bkngObjClss)) return 10.0;//비회원부킹
    		double bookingScore = 0.0;//아래 조건 미등재 골프장
    		if("319".equals(greenNo) || "크리스탈밸리".equals(greenNo)) bookingScore = -70.0;//크리스탈밸리
    		if("154".equals(greenNo) || "".equals(greenNo)) bookingScore = -60.0;//양평TPC
    		if("196".equals(greenNo) || "".equals(greenNo)) bookingScore = -50.0;//프리스틴밸리
    		if("216".equals(greenNo) || "떼제베".equals(greenNo)) bookingScore = -50.0;//떼제베
    		if("255".equals(greenNo) || "상떼힐".equals(greenNo)) bookingScore = -50.0;//상떼힐
    		if("269".equals(greenNo) || "센추리21".equals(greenNo)) bookingScore = -40.0;//센츄리21
    		if("176".equals(greenNo) || "캐슬파인".equals(greenNo)) bookingScore = -10.0;//캐슬파인
    		if("114".equals(greenNo) || "강남300".equals(greenNo)) bookingScore = -10.0;//강남300
    		if("211".equals(greenNo) || "썬힐".equals(greenNo)) bookingScore = 0.0;//썬힐
    		if("239".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0;//마우나오션
    		if("310".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0;//아크로	
    		if("오펠".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0; //오펠
    		if("광릉".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0; //광릉
    		if("세이트포".equals(greenNo) || "".equals(greenNo)) bookingScore = 0.0; //세이트포
/*
 * 세인트포,오펠,광릉
 * */
    		return bookingScore;
    	}
    //부킹 확정취소 시 점수
    	private double getCancelScore(String greenNo, String bkngObjClss){
    		if("2".equals(bkngObjClss)) return -10.0;//비회원부킹
    		double cancelScore = 0.0;//아래 조건 미등재 골프장
    		if("319".equals(greenNo) || "크리스탈밸리".equals(greenNo)) cancelScore = -10.0;//크리스탈밸리
    		if("154".equals(greenNo) || "".equals(greenNo)) cancelScore = -10.0;//양평TPC
    		if("196".equals(greenNo) || "".equals(greenNo)) cancelScore = -10.0;//프리스틴밸리
    		if("216".equals(greenNo) || "떼제베".equals(greenNo)) cancelScore = -10.0;//떼제베
    		if("255".equals(greenNo) || "상떼힐".equals(greenNo)) cancelScore = -10.0;//상떼힐
    		if("269".equals(greenNo) || "센추리21".equals(greenNo)) cancelScore = -10.0;//센츄리21
    		if("176".equals(greenNo) || "캐슬파인".equals(greenNo)) cancelScore = -10.0;//캐슬파인
    		if("114".equals(greenNo) || "강남300".equals(greenNo)) cancelScore = -10.0;//강남300
    		if("211".equals(greenNo) || "강남300".equals(greenNo)) cancelScore = -10.0;//썬힐
    		if("239".equals(greenNo) || "썬힐".equals(greenNo)) cancelScore = -10.0;//마우나오션
    		if("310".equals(greenNo) || "".equals(greenNo)) cancelScore = -10.0;//아크로
    		if("오펠".equals(greenNo) || "".equals(greenNo)) cancelScore = 0.0; //오펠
    		if("광릉".equals(greenNo) || "".equals(greenNo)) cancelScore = 0.0; //광릉
    		if("세이트포".equals(greenNo) || "".equals(greenNo)) cancelScore = 0.0; //세이트포

    		return cancelScore;
    	}
    	

    	/** ***********************************************************************
        * 탑골프 예약취소 담당자에 SMS리스트
        ************************************************************************ */    	
    	private String getTopGolfResp() throws Exception{

    		StringBuffer sql = new StringBuffer();
    		
    		sql.append("\n	SELECT EXPL							");
    		sql.append("\n	FROM BCDBA.TBGCMMNCODE				");
    		sql.append("\n	WHERE GOLF_CMMN_CLSS = '0066'		");
    		
    		return sql.toString();	
    	
    	}	    	
}

