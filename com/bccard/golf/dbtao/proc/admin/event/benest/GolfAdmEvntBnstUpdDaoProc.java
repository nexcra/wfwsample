/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBnstUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 이벤트 > 월례회 > 수정 처리
*   적용범위  : golf
*   작성일자  : 2010-03-24
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		20100524	임은혜	6월 이벤트
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.benest;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfPayAuthEtt;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentDaoProc;
import com.bccard.golf.dbtao.proc.payment.GolfPaymentRegDaoProc;
import com.bccard.golf.msg.MsgEtt;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/****************************************************************************** 
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBnstUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 > 이벤트 > 월례회 > 수정 처리";

	/** *****************************************************************
	 * GolfAdmOrdUpdDaoProc 프로세스 생성자
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmEvntBnstUpdDaoProc() {} 

	/** *****************************************************************
	 * execute_grd 등급변경 => 동반자 등급, 금액, 예약사항 금액
	 ***************************************************************** */
	public int execute_grd(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no = data.getString("aplc_seq_no");	
			String seq_no = data.getString("seq_no");
			String cdhd_grd_seq_no = data.getString("cdhd_grd_seq_no");
			String email = "";
			String sttl_amt = "";

			if(cdhd_grd_seq_no.equals("1")){
				sttl_amt = "150000";
			}else if(cdhd_grd_seq_no.equals("7")){
				sttl_amt = "160000";
			}else if(cdhd_grd_seq_no.equals("2")){
				sttl_amt = "160000";
			}else if(cdhd_grd_seq_no.equals("3")){ 
				sttl_amt = "160000";
			}else if(cdhd_grd_seq_no.equals("9")){
				sttl_amt = "150000";
			}else if(cdhd_grd_seq_no.equals("4")){ 
				sttl_amt = "180000";
			}
            /*****************************************************************************/
			
			sql = this.getGrdCompnUpdQuery();	// 동반인 수정
			pstmt = conn.prepareStatement(sql); 

			int idx = 0;
			pstmt.setString(++idx, cdhd_grd_seq_no );
			pstmt.setString(++idx, sttl_amt );
			pstmt.setString(++idx, email );
			pstmt.setString(++idx, aplc_seq_no );
			pstmt.setString(++idx, seq_no );
			result = pstmt.executeUpdate();
			
			if(result>0){
				sql = this.getGrdSttlUpdQuery();// 예약상태 금액 변경
				pstmt = conn.prepareStatement(sql);
	
				idx = 0;
				pstmt.setString(++idx, aplc_seq_no );
				pstmt.setString(++idx, aplc_seq_no );
				result = pstmt.executeUpdate();
			}

			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace(); 
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/** *****************************************************************
	 * execute_sttl 결제상태 변경
	 ***************************************************************** */
	public int execute_sttl(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null; 
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no = data.getString("aplc_seq_no");	
			String sttl_stat_clss = data.getString("sttl_stat_clss");
            /*****************************************************************************/
			
			sql = this.getSttlUpdQuery();	// 동반인 수정
			pstmt = conn.prepareStatement(sql);

			int idx = 0;
			pstmt.setString(++idx, sttl_stat_clss );
			pstmt.setString(++idx, aplc_seq_no );
			result = pstmt.executeUpdate();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}


	/** *****************************************************************
	 * execute_sttl 참가상태 변경
	 ***************************************************************** */
	public int execute_evnt(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no = data.getString("aplc_seq_no");	
			String evnt_pgrs_clss = data.getString("evnt_pgrs_clss");
            /*****************************************************************************/
			
			sql = this.getEvntUpdQuery();	// 동반인 수정
			pstmt = conn.prepareStatement(sql);

			int idx = 0;
			pstmt.setString(++idx, evnt_pgrs_clss );
			pstmt.setString(++idx, aplc_seq_no );
			result = pstmt.executeUpdate();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/** *****************************************************************
	 * execute_all 전체내역 변경
	 ***************************************************************** */
	public int execute_all(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs2 = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no 		= data.getString("aplc_seq_no");
			String green_nm 		= data.getString("green_nm");
			String note 			= data.getString("note");
			String months 			= data.getString("months");
			String sttl_stat_clss 	= data.getString("sttl_stat_clss");
			String evnt_pgrs_clss 	= data.getString("evnt_pgrs_clss");
			String mgr_memo 		= data.getString("mgr_memo");
			String rsvt_date 		= data.getString("rsvt_date");
			String rsv_time 		= data.getString("rsv_time");
			
			int cnt 				= data.getInt("cnt");
			
			String trm_unt 		= data.getString("trm_unt");				//월례회 seq
			String cdhd_grd_seq_no	= "";
			String email			= "";
			String sttl_amt 		= "";
			String email_amt		= "";
			int int_sttl_amt		= 0;
			int sttl_amt_all		= 0;
			int idx = 0;
			String del_yn			= "";
			String seq_no			= "";
			String bkg_pe_nm		= "";
			String hp_ddd_no		= "";
			String hp_tel_hno		= "";
			String hp_tel_sno		= "";
			String type_cal			= "";	// 금액 합계구분
            /*****************************************************************************/
			
			String input_cdhd_grd_seq_no="";
			String input_sttl_amt="";
			// 1. 동반인 수 만큼 변경 => 등급, 금액, 전화번호
			for(int i=1; i<=20; i++){

				if(!GolfUtil.empty(data.getString("seq_no"+i))){

					if(!GolfUtil.empty(data.getString("del_yn"+i))){
						del_yn = data.getString("del_yn"+i);
					}else{
						del_yn = "";
					}
					debug("del_yn : " + del_yn);
					seq_no = data.getString("seq_no"+i);
				
					if(del_yn.equals("Y")){
						
						
						sql = this.getGrdCompnDelQuery();	// 동반인 삭제
						pstmt = conn.prepareStatement(sql);
						idx = 0;
						pstmt.setString(++idx, aplc_seq_no );
						pstmt.setString(++idx, seq_no );
	
					}else{
						
						if(!GolfUtil.empty(data.getString("cdhd_grd_seq_no"+i))){
							cdhd_grd_seq_no = data.getString("cdhd_grd_seq_no"+i);
							
							/*debug("@@@@cdhd_grd_seq_no :" + cdhd_grd_seq_no);
							//1,0,4
							String sql2 = this.getPayCost();  
							pstmt = conn.prepareStatement(sql2.toString());
							pstmt.setString(0, trm_unt );				//seq
							rs2 = pstmt.executeQuery();
							if (rs2.next()) {				//CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT
								if(cdhd_grd_seq_no.equals("1")){
									input_sttl_amt = rs2.getString("CPO_AMT");
								}else if(cdhd_grd_seq_no.equals("0")){
									input_sttl_amt = rs2.getString("ACRG_CDHD_AMT");
								}else if(cdhd_grd_seq_no.equals("4")){
									input_sttl_amt = rs2.getString("FREE_CDHD_AMT");
								}
								debug("@@@@input_sttl_amt :" + input_sttl_amt);
							}*/
						}else{
							cdhd_grd_seq_no = "";
						}
						if(!GolfUtil.empty(data.getString("email"+i))){
							email = data.getString("email"+i);
						}else{
							email = "";
						}
						if(!GolfUtil.empty(data.getString("bkg_pe_nm"+i))){
							bkg_pe_nm = data.getString("bkg_pe_nm"+i);
						}else{
							bkg_pe_nm = "";
						}
						if(!GolfUtil.empty(data.getString("hp_ddd_no"+i))){
							hp_ddd_no = data.getString("hp_ddd_no"+i);
						}else{
							hp_ddd_no = "";
						}
						if(!GolfUtil.empty(data.getString("hp_tel_hno"+i))){
							hp_tel_hno = data.getString("hp_tel_hno"+i);
						}else{
							hp_tel_hno = "";
						}
						if(!GolfUtil.empty(data.getString("hp_tel_sno"+i))){
							hp_tel_sno = data.getString("hp_tel_sno"+i);
						}else{
							hp_tel_sno = "";
						}
												
						if(months.equals("05")){
							if(green_nm.equals("가평베네스트 골프클럽")){
								if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){
									sttl_amt = "160000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "170000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "180000";
								}else{
									sttl_amt = "0";
								}
							}else if(green_nm.equals("신라 골프클럽")){
								if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){
									sttl_amt = "125000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "135000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "145000";
								}else{
									sttl_amt = "0";
								}
							}
						}else if(months.equals("06")){

							if(note.equals("A")){
								if(cdhd_grd_seq_no.equals("99")){
									sttl_amt = "125000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("B")){
								if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){
									sttl_amt = "125000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "135000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "145000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("C")){
								if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){
									sttl_amt = "155000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "165000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "175000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("D")){
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "150000";
								}else if(cdhd_grd_seq_no.equals("2")){
									sttl_amt = "190000";
								}else if(cdhd_grd_seq_no.equals("3")){
									sttl_amt = "350000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "420000";
								}else if(cdhd_grd_seq_no.equals("5")){
									sttl_amt = "500000";
								}else if(cdhd_grd_seq_no.equals("6")){
									sttl_amt = "530000";
								}else if(cdhd_grd_seq_no.equals("7")){
									sttl_amt = "600000";
								}else if(cdhd_grd_seq_no.equals("8")){
									sttl_amt = "690000";
								}else{
									sttl_amt = "0";
								}

								if(email.equals("1")){
									email_amt = "40000";
									type_cal = "plus";
								}else if(email.equals("2")){
									email_amt = "50000";
									type_cal = "plus";
								}else if(email.equals("3")){
									email_amt = "80000";
									type_cal = "plus";
								}else if(email.equals("4")){
									email_amt = "100000";
									type_cal = "plus";
								}else if(email.equals("5")){
									email_amt = "10000";
									type_cal = "minus";
								}else if(email.equals("6")){
									email_amt = "20000";
									type_cal = "minus";
								}else if(email.equals("7")){
									email_amt = "50000";
									type_cal = "minus";
								}else{
									email_amt = "0";
								}
								
								//debug("sttl_amt : " + sttl_amt + " / email_amt : " + email_amt);
								
								if(type_cal.equals("minus")){
									int_sttl_amt = Integer.parseInt(sttl_amt)-Integer.parseInt(email_amt);
								}else{
									int_sttl_amt = Integer.parseInt(sttl_amt)+Integer.parseInt(email_amt);
								}
								sttl_amt = int_sttl_amt+"";
							}

						}else if(months.equals("07")){
							if(note.equals("A")){
								if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "120000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "130000";
								}else{
									sttl_amt = "0";
								}
							}
						}else if(months.equals("08")){
							if(note.equals("A")){
								if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "120000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "130000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("B")){
								if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "100000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "120000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("C")){
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "390000";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "410000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "420000";
								}else{
									sttl_amt = "0";
								}
							}
						}else if(months.equals("09")){
							if(note.equals("A")){    //신라골프 클럽(9월)
								if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "125000";
								}else if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "135000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "145000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("B")){	//이븐데일(9월) 
								if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "100000";
								}else if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "120000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("C")){ //오크밸리(9월)
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "390000";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "410000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "420000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("D")){ //오크밸리(9월 당일)
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "197500";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "197500";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "197500";
								}else{
									sttl_amt = "0";
								}
							}
						}else if(months.equals("10")){
							 if(note.equals("C")){ //오크밸리(10월)
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "390000";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "410000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "420000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("D")){ //오크밸리(10월 당일)
								if(cdhd_grd_seq_no.equals("1")){
									sttl_amt = "197500";
								}else if(cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("0")){
									sttl_amt = "197500";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "197500";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("A")){    //신라골프 클럽(10월)
								if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "125000";
								}else if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "135000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "145000";
								}else{
									sttl_amt = "0";
								}
							}else if(note.equals("B")){	//이븐데일(10월) 
								if(cdhd_grd_seq_no.equals("0")){
									sttl_amt = "100000";
								}else if(cdhd_grd_seq_no.equals("9")){
									sttl_amt = "110000";
								}else if(cdhd_grd_seq_no.equals("4")){
									sttl_amt = "120000";
								}else{
									sttl_amt = "0";
								}
							}
						}
						
						sttl_amt_all += Integer.parseInt(sttl_amt);
			            /*****************************************************************************/
						
						if(del_yn.equals("N")){
								
							sql = this.getEvtCompnQuery();   // 동반인 등록
							pstmt = conn.prepareStatement(sql.toString());
							idx = 1;	// 13
							pstmt.setString(idx++, aplc_seq_no);
							pstmt.setString(idx++, seq_no);
							pstmt.setString(idx++, "2");
							pstmt.setString(idx++, bkg_pe_nm);
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, cdhd_grd_seq_no);	//
							pstmt.setString(idx++, hp_ddd_no);
							pstmt.setString(idx++, hp_tel_hno);
							pstmt.setString(idx++, hp_tel_sno);
							pstmt.setString(idx++, email);
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, sttl_amt);
							
						}else{
							
							sql = this.getGrdCompnUpdQuery();	// 동반인 수정
							pstmt = conn.prepareStatement(sql);
							idx = 0;
							pstmt.setString(++idx, cdhd_grd_seq_no );
							pstmt.setString(++idx, sttl_amt );
							pstmt.setString(++idx, hp_ddd_no );
							pstmt.setString(++idx, hp_tel_hno );
							pstmt.setString(++idx, hp_tel_sno );
							pstmt.setString(++idx, bkg_pe_nm );
							pstmt.setString(++idx, email );
							pstmt.setString(++idx, aplc_seq_no );
							pstmt.setString(++idx, seq_no );
							
						}
						
					}
					result += pstmt.executeUpdate();
				}
			}

			debug("sttl_amt_all : " + sttl_amt_all + " / cdhd_grd_seq_no : " + cdhd_grd_seq_no + " / months : " + months + " / note : " + note);
			
			
			// 2. 이벤트 변경 => 금액, 결제상태, 참가등록상태, 날짜
			if(result>0){
				sql = this.getEvtUpdQuery();// 예약상태 금액 변경
				pstmt = conn.prepareStatement(sql);
	
				idx = 0;
				pstmt.setString(++idx, aplc_seq_no );
				pstmt.setString(++idx, sttl_stat_clss );
				pstmt.setString(++idx, evnt_pgrs_clss );
				pstmt.setString(++idx, mgr_memo );
				pstmt.setString(++idx, rsvt_date );
				pstmt.setString(++idx, rsv_time );
				pstmt.setString(++idx, aplc_seq_no );
				result += pstmt.executeUpdate();
			}
			
			if(result > 1) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
					
	
	/** *****************************************************************
	 * execute_all 전체내역 변경
	 ***************************************************************** */
	public int execute_update(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs2 = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no 		= data.getString("aplc_seq_no");
			String rsvt_date 		= data.getString("rsvt_date");
			String rsv_time 		= data.getString("rsv_time");
			String sttl_stat_clss 	= data.getString("sttl_stat_clss");
			String evnt_pgrs_clss 	= data.getString("evnt_pgrs_clss");
			String mgr_memo 		= data.getString("mgr_memo");
			String trm_unt 		= data.getString("trm_unt");				//월례회 seq
			
			int cnt 				= data.getInt("cnt");
			String cdhd_grd_seq_no	= "";
			String email			= "";
			String sttl_amt 		= "";
			String email_amt		= "";
			int int_sttl_amt		= 0;
			int sttl_amt_all		= 0;
			int idx = 0;
			String del_yn			= "";
			String seq_no			= "";
			String bkg_pe_nm		= "";
			String hp_ddd_no		= "";
			String hp_tel_hno		= "";
			String hp_tel_sno		= "";
			String type_cal			= "";	// 금액 합계구분
            /*****************************************************************************/
			debug("@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			
			String input_sttl_amt="";
			// 1. 동반인 수 만큼 변경 => 등급, 금액, 전화번호
			for(int i=1; i<=20; i++){

				if(!GolfUtil.empty(data.getString("seq_no"+i))){

					if(!GolfUtil.empty(data.getString("del_yn"+i))){
						del_yn = data.getString("del_yn"+i);
					}else{
						del_yn = "";
					}
					debug("del_yn : " + del_yn);
					seq_no = data.getString("seq_no"+i);
				
					if(del_yn.equals("Y")){
						sql = this.getGrdCompnDelQuery();	// 동반인 삭제
						pstmt = conn.prepareStatement(sql);
						idx = 0;
						pstmt.setString(++idx, aplc_seq_no );
						pstmt.setString(++idx, seq_no );
	
					}else{
						if(!GolfUtil.empty(data.getString("cdhd_grd_seq_no"+i))){
							cdhd_grd_seq_no = data.getString("cdhd_grd_seq_no"+i);
							
							debug("@@@@cdhd_grd_seq_no :" + cdhd_grd_seq_no);
							//1,0,4
							sql = this.getPayCost();  
							pstmt = conn.prepareStatement(sql.toString());
							pstmt.setString(1, trm_unt );				//seq
							rs2 = pstmt.executeQuery();
							if (rs2.next()) {				//CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT
								if(cdhd_grd_seq_no.equals("1")){
									input_sttl_amt = rs2.getString("CPO_AMT");
								}else if(cdhd_grd_seq_no.equals("0")){
									input_sttl_amt = rs2.getString("ACRG_CDHD_AMT");
								}else if(cdhd_grd_seq_no.equals("4")){
									input_sttl_amt = rs2.getString("FREE_CDHD_AMT");
								}
								debug("@@@@input_sttl_amt :" + input_sttl_amt);
							}
						}else{
							cdhd_grd_seq_no = "";
						}
						if(!GolfUtil.empty(data.getString("bkg_pe_nm"+i))){
							bkg_pe_nm = data.getString("bkg_pe_nm"+i);
						}else{
							bkg_pe_nm = "";
						}
						if(!GolfUtil.empty(data.getString("hp_ddd_no"+i))){
							hp_ddd_no = data.getString("hp_ddd_no"+i);
						}else{
							hp_ddd_no = "";
						}
						if(!GolfUtil.empty(data.getString("hp_tel_hno"+i))){
							hp_tel_hno = data.getString("hp_tel_hno"+i);
						}else{
							hp_tel_hno = "";
						}
						if(!GolfUtil.empty(data.getString("hp_tel_sno"+i))){
							hp_tel_sno = data.getString("hp_tel_sno"+i);
						}else{
							hp_tel_sno = "";
						}
						
			            /*****************************************************************************/
						
						if(del_yn.equals("N")){
							debug("@@@@@@@@@@@2del_yn = N");
							sql = this.getEvtCompnQuery();   // 동반인 등록
							pstmt = conn.prepareStatement(sql.toString());
							idx = 1;	// 13
							pstmt.setString(idx++, aplc_seq_no);
							pstmt.setString(idx++, seq_no);
							pstmt.setString(idx++, "2");
							pstmt.setString(idx++, bkg_pe_nm);
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, cdhd_grd_seq_no);
							pstmt.setString(idx++, hp_ddd_no);
							pstmt.setString(idx++, hp_tel_hno);
							pstmt.setString(idx++, hp_tel_sno);
							pstmt.setString(idx++, email);
							pstmt.setString(idx++, "");
							pstmt.setString(idx++, input_sttl_amt);
							
						}else{
							debug("@@@@@@@@@@@2del_yn = Y");
							sql = this.getGrdCompnUpdQuery();	// 동반인 수정
							pstmt = conn.prepareStatement(sql);
							idx = 0;
							pstmt.setString(++idx, cdhd_grd_seq_no );
							pstmt.setString(++idx, input_sttl_amt );
							pstmt.setString(++idx, hp_ddd_no );
							pstmt.setString(++idx, hp_tel_hno );
							pstmt.setString(++idx, hp_tel_sno );
							pstmt.setString(++idx, bkg_pe_nm );
							pstmt.setString(++idx, email );
							pstmt.setString(++idx, aplc_seq_no );
							pstmt.setString(++idx, seq_no );
							
						}
						
					}
					result += pstmt.executeUpdate();
				}
			}

			
			
			// 2. 이벤트 변경 => 금액, 결제상태, 참가등록상태, 날짜
			if(result>0){
				sql = this.getEvtUpdQuery();// 예약상태 금액 변경
				pstmt = conn.prepareStatement(sql);
	
				idx = 0;
				pstmt.setString(++idx, aplc_seq_no );
				pstmt.setString(++idx, sttl_stat_clss );
				pstmt.setString(++idx, evnt_pgrs_clss );
				pstmt.setString(++idx, mgr_memo );
				pstmt.setString(++idx, rsvt_date );
				pstmt.setString(++idx, rsv_time );
				pstmt.setString(++idx, aplc_seq_no );
				result += pstmt.executeUpdate();
			}
			
			if(result > 1) {
				conn.commit();
			} else {
				conn.rollback();
			}
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	/** *****************************************************************
	 * execute_del 결제 취소 
	 ***************************************************************** */
	public int execute_del(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException  {
		int result = 0;
		int result_upd = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
		ResultSet rs = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String aplc_seq_no 		= data.getString("aplc_seq_no");
			

            /*****************************************************************************/
    		// 연회비 환급처리 해준다.
    		boolean payCancelResult = false;
    		GolfPayAuthEtt payEtt = new GolfPayAuthEtt();			// 승인정보
    		GolfPaymentDaoProc payProc = (GolfPaymentDaoProc)context.getProc("GolfPaymentDaoProc");

			// 결제변수
			String odr_no = "";		// 주문번호
			String sttl_amt = "";	// 결제금액
			String mer_no = "";		// 가맹점번호
			String card_no = "";	// 카드번호
			String vald_date = "";	// 유효일자
			String ins_mcnt = "";	// 할부개월수
			String auth_no = "";	// 승인번호
			String ip = request.getRemoteAddr();  // 단말번호(IP, '.'제외)
			String sttl_mthd_clss = "";	// 결제방법구분코드
			String sttl_gds_clss = "";	// 결제상품구분코드
			String cdhd_id = "";
			

			// 결제 내역 조회
			sql = this.setPayBackListQuery(); 
            pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, aplc_seq_no );
            rs = pstmt.executeQuery();	
            
            while(rs.next()){
            	payCancelResult = false;

            	odr_no = rs.getString("ODR_NO");
				sttl_amt = rs.getString("STTL_AMT");
				mer_no = rs.getString("MER_NO");
				card_no = rs.getString("CARD_NO");
				vald_date = rs.getString("VALD_DATE");
				ins_mcnt = rs.getString("INS_MCNT");
				auth_no = rs.getString("AUTH_NO");
				sttl_mthd_clss = rs.getString("STTL_MTHD_CLSS");
				sttl_gds_clss = rs.getString("STTL_GDS_CLSS");
				cdhd_id = rs.getString("CDHD_ID");

				// 비씨카드 또는 복합결제인 경우
				if("0001".equals(sttl_mthd_clss) || "0002".equals(sttl_mthd_clss)) {
				
					payEtt.setMerMgmtNo(mer_no);		// 가맹점 번호
					payEtt.setCardNo(card_no);			// isp카드번호
					payEtt.setValid(vald_date);			// 만료 일자
					payEtt.setAmount(sttl_amt);			// 결제금액	
					payEtt.setInsTerm(ins_mcnt);		// 할부개월수
					payEtt.setRemoteAddr(ip);			// ip 주소
					payEtt.setUseNo(auth_no);			// 승인번호

					String host_ip = java.net.InetAddress.getLocalHost().getHostAddress();
					if( "211.181.255.40".equals(host_ip)) {
						payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
					} else {
						payCancelResult = payProc.executePayAuthCancel(context, payEtt);			// 취소전문 호출
					}
					debug("payCancelResult========> " + payCancelResult);
				}
				
				// 타사카드 또는 계좌이체인 경우(올앳페이)
				else if("0003".equals(sttl_mthd_clss) || "0004".equals(sttl_mthd_clss)) {
				
					String payType = "";
					if("0003".equals(sttl_mthd_clss))			payType = "CARD";	// 신용카드
					else if("0004".equals(sttl_mthd_clss))		payType = "ABANK";	// 계좌이체

					String sShopid  = "bcgolfpkg";							// 샵아이디
					String sCrossKey  = "337475c07e73d2d8fb92230d09daa87a";	// 크로스키
					
					payEtt.setOrderNo(odr_no);			// 주문번호
					payEtt.setAmount(sttl_amt);			// 결제금액	
					payEtt.setPayType(payType);			// 결제방식
					payEtt.setShopId(sShopid);
					payEtt.setCrossKey(sCrossKey);

					payCancelResult = payProc.executePayAuthCancel_Allat(context, payEtt);		// 승인취소 호출	
					
					debug("payCancelResult========> " + payCancelResult + " | odr_no : " + odr_no + " | sttl_amt : " + sttl_amt + " | payType : " + payType);
				}					
				
				if(payCancelResult){
					
					sql = this.getPayUpdateQuery(); 
		            pstmt = conn.prepareStatement(sql);
		        	pstmt.setString(1, odr_no );
					result_upd = pstmt.executeUpdate();
					
					debug("ODR_NO========> " + odr_no);
					debug("result_upd========> " + result_upd);

			    	if(result_upd > 0){
			    		
			    		// 동반자 업데이트
						sql = this.getUpdCompnQuery();
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, aplc_seq_no );
						result_upd += pstmt.executeUpdate();
						
			    		// 이벤트 업데이트
						sql = this.getUpdEvtQuery();
						pstmt = conn.prepareStatement(sql);
						pstmt.setString(1, aplc_seq_no );
						result_upd += pstmt.executeUpdate();
						
						result = 1;
						debug("결제성공내역저장결과========> " + result_upd);		
			    	}

				}
				else{	// 결제실패시 내역 저장 2009.11.26
					
					GolfPaymentRegDaoProc payFailProc = (GolfPaymentRegDaoProc)context.getProc("GolfPaymentRegDaoProc");						
					
					DbTaoDataSet dataSet = new DbTaoDataSet(TITLE);						
					dataSet.setString("CDHD_ID", cdhd_id);						//회원아이디
					dataSet.setString("STTL_MTHD_CLSS", sttl_mthd_clss);		//결제방법구분코드 :0001 : BC카드 / 0002:BC카드 + TOP포인트 / 0003:타사카드 / 0004:계좌이체 
					dataSet.setString("STTL_GDS_CLSS", sttl_gds_clss);			//결제상품구분코드 0001:챔피온연회비 0002:블루연회비 0003:골드연회비 0008:블랙연회비 
					dataSet.setString("STTL_STAT_CLSS", "Y");					//결제여부 N:결제완료 / Y:결재취소
						
					result_upd = payFailProc.failExecute(context, dataSet, request, payEtt);
					
					debug("결제실패내역저장결과========> " + result_upd);		
					result = 0;
					
				}
            }		
	        /*****************************************************************************/

			debug("리턴코드 result ========> " + result);		
			
			if(result_upd > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
						
		} catch(Exception e) {
			try	{
				try { if( conn != null ){ conn.close(); } else {} } catch(Throwable ignore) {}
			}catch (Exception c){}
			//e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
	        try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
								
	/** ***********************************************************************
	* 동반자 등급, 금액, 전화번호 수정
	************************************************************************ */
	private String getGrdCompnUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLCPE	");
		sql.append("\n	SET CDHD_GRD_SEQ_NO=?, STTL_AMT=?, HP_DDD_NO=?, HP_TEL_HNO=?, HP_TEL_SNO=?, BKG_PE_NM=?, EMAIL=?	");
		sql.append("\n	WHERE APLC_SEQ_NO=? AND SEQ_NO=?	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 동반자 등급, 금액, 전화번호 수정
	************************************************************************ */
	private String getGrdCompnUpdQuery2(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLCPE	");
		sql.append("\n	SET CDHD_GRD_SEQ_NO=?, STTL_AMT=?, HP_DDD_NO=?, HP_TEL_HNO=?, HP_TEL_SNO=?, BKG_PE_NM=?	");
		sql.append("\n	WHERE APLC_SEQ_NO=? AND SEQ_NO=?	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 동반자 삭제
	************************************************************************ */
	private String getGrdCompnDelQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=? AND SEQ_NO=?	");
		
		return sql.toString();
	}

	/** ***********************************************************************
    * 동반자 등록
    ************************************************************************ */
    private String getEvtCompnQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGGOLFEVNTAPLCPE (	");
		sql.append("\n	    SITE_CLSS, APLC_SEQ_NO, SEQ_NO, GOLF_SVC_APLC_CLSS, APLC_PE_CLSS, BKG_PE_NM, CDHD_NON_CDHD_CLSS, CDHD_ID, JUMIN_NO, CDHD_GRD_SEQ_NO	");
		sql.append("\n	    , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, STTL_STAT_CLSS, CNCL_YN, STTL_AMT	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    '2', ?, ?, '9003', ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, '0', 'N', ?	");
		sql.append("\n	)	");
		
		return sql.toString();
    }
		
	/** ***********************************************************************
	* 이벤트 금액 수정
	************************************************************************ */
	private String getEvtUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC 	");
		sql.append("\n	SET STTL_AMT=(SELECT SUM(STTL_AMT) FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=?), STTL_STAT_CLSS=?, EVNT_PGRS_CLSS=?, MGR_MEMO=?	");
		sql.append("\n	, RSVT_DATE=? , RSV_TIME=?	");
		sql.append("\n	WHERE APLC_SEQ_NO=?	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 이벤트 금액 수정
	************************************************************************ */
	private String getGrdSttlUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC 	");
		sql.append("\n	SET STTL_AMT=(SELECT SUM(STTL_AMT) FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE APLC_SEQ_NO=?)	");
		sql.append("\n	WHERE APLC_SEQ_NO=?	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 이벤트 결제상태 수정
	************************************************************************ */
	private String getSttlUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC SET STTL_STAT_CLSS=? WHERE APLC_SEQ_NO=? 	");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 이벤트 참가등록수정
	************************************************************************ */
	private String getEvntUpdQuery(){
		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC SET EVNT_PGRS_CLSS=? WHERE APLC_SEQ_NO=? 	");
		
		return sql.toString();
	}


	/** ***********************************************************************
	 * 승인 내역 가져오기
	 ************************************************************************ */
	private String setPayBackListQuery(){
		StringBuffer sql = new StringBuffer();        
		sql.append("\n	SELECT ODR_NO, STTL_AMT, MER_NO, CARD_NO, VALD_DATE, INS_MCNT, AUTH_NO, STTL_MTHD_CLSS, STTL_GDS_CLSS, CDHD_ID	\n");
		sql.append("\t	FROM BCDBA.TBGSTTLMGMT	\n");
		sql.append("\t	WHERE STTL_GDS_SEQ_NO=? AND STTL_GDS_CLSS='0010' AND STTL_STAT_CLSS='N'	\n");
		return sql.toString();
	}
	
	
	  /** ***********************************************************************
     *  가격정보 가져오기
     ************************************************************************ */
     public String getPayCost(){
 		StringBuffer sql = new StringBuffer();
 		sql.append("\n 			SELECT CPO_AMT, ACRG_CDHD_AMT, FREE_CDHD_AMT " );
 		sql.append("\n 			FROM BCDBA.TBGMMLYMTNG ");
 		sql.append("\n			WHERE SEQ_NO = ? 	");
 		return sql.toString();
 	}
	
    /** ***********************************************************************
    * 결제 취소 업데이트    
    ************************************************************************ */
    private String getPayUpdateQuery(){
        StringBuffer sql = new StringBuffer();
 		sql.append("\n");
 		sql.append("\t	UPDATE BCDBA.TBGSTTLMGMT	\n");
 		sql.append("\t	SET STTL_STAT_CLSS='Y', CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
 		sql.append("\t	WHERE ODR_NO=?	\n");
        return sql.toString();
    }
    
    /** ***********************************************************************
     * 구매 상태를 변경한다. - 동반자
     ************************************************************************ */
 	private String getUpdCompnQuery(){
 		StringBuffer sql = new StringBuffer();

 		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLCPE SET STTL_STAT_CLSS='2' WHERE APLC_SEQ_NO=?	\n");
 				
 		return sql.toString();
 	}
    
    /** ***********************************************************************
     * 구매 상태를 변경한다. - 이벤트
     ************************************************************************ */
 	private String getUpdEvtQuery(){
 		StringBuffer sql = new StringBuffer();

 		sql.append("\n	UPDATE BCDBA.TBGGOLFEVNTAPLC SET STTL_STAT_CLSS='2', EVNT_PGRS_CLSS='E' WHERE APLC_SEQ_NO=?	\n");
 				
 		return sql.toString();
 	}
}


