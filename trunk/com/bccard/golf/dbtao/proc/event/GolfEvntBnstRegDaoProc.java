/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBnstRegDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 월례회 > 등록처리
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
* golfloung		20100524	임은혜	6월 이벤트
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
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfEvntBnstRegDaoProc extends AbstractProc {
	
	public GolfEvntBnstRegDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);

			String green_nm						= data.getString("green_nm");
			String green_id						= data.getString("green_id");
			String rsvt_date					= data.getString("rsvt_date");
			String rsv_time						= data.getString("rsv_time");
			String note							= data.getString("note");
			
			String bkg_pe_nm					= data.getString("bkg_pe_nm");
			String jumin_no						= data.getString("jumin_no");
			String hp_ddd_no					= data.getString("hp_ddd_no");
			String hp_tel_hno					= data.getString("hp_tel_hno");
			String hp_tel_sno					= data.getString("hp_tel_sno");
			String hadc_num						= data.getString("hadc_num");
			String email						= data.getString("email");
			String chdh_non_cdhd_clss			= data.getString("chdh_non_cdhd_clss");
			String cdhd_id						= data.getString("cdhd_id");
			String cdhd_grd_seq_no				= data.getString("cdhd_grd_seq_no");

			// 0 이면 로그아웃 상태
			if(cdhd_grd_seq_no.equals("0")){
				cdhd_grd_seq_no = "";
			}else if(!(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9") || cdhd_grd_seq_no.equals("4"))){
				cdhd_grd_seq_no = "0";
			}
			
			String sttl_amt = "";
			if(green_id.equals("bn")){
				if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){	// 챔피언, 플래티넘
					sttl_amt = "160000";
				}else if(cdhd_grd_seq_no.equals("0")){	// 유료회원
					sttl_amt = "170000";
				}else if(cdhd_grd_seq_no.equals("4")){	// 무료회원
					sttl_amt = "180000";
				}
			}else if(green_id.equals("sr")){
				if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){	// 챔피언, 플래티넘
					sttl_amt = "125000";
				}else if(cdhd_grd_seq_no.equals("0")){	// 유료회원
					sttl_amt = "135000";
				}else if(cdhd_grd_seq_no.equals("4")){	// 무료회원
					sttl_amt = "145000";
				}
			}else{

				if(note.equals("A")){
					sttl_amt = "125000";
					cdhd_grd_seq_no = "99";
				}else{
					if(cdhd_grd_seq_no.equals("1") || cdhd_grd_seq_no.equals("9")){	// 챔피언, 플래티넘
						sttl_amt = "125000";
					}else if(cdhd_grd_seq_no.equals("0")){	// 유료회원
						sttl_amt = "135000";
					}else if(cdhd_grd_seq_no.equals("4")){	// 무료회원
						sttl_amt = "145000";
					}
				}
				
			}
			
			sttl_amt = "0"; 

			
			int compn_no						= data.getInt("compn_no");
			String arr_compn_bkg_pe_nm			= data.getString("arr_compn_bkg_pe_nm");
			String arr_compn_hp_ddd_no			= data.getString("arr_compn_hp_ddd_no");
			String arr_compn_hp_tel_hno			= data.getString("arr_compn_hp_tel_hno");
			String arr_compn_hp_tel_sno			= data.getString("arr_compn_hp_tel_sno");
			String arr_compn_cdhd_grd_seq_no	= data.getString("arr_compn_cdhd_grd_seq_no");
			
			
			int aplc_seq_no = 0;
			String sql = this.getEvtSeqQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				aplc_seq_no = rs.getInt("MAX_SEQ_NO");
			}

			// 이벤트 등록
			sql = this.getEvtQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;	// 11
			pstmt.setInt(idx++, aplc_seq_no);
			pstmt.setString(idx++, green_nm);
			pstmt.setString(idx++, rsvt_date);
			pstmt.setString(idx++, rsv_time);
			pstmt.setString(idx++, bkg_pe_nm);
			pstmt.setString(idx++, chdh_non_cdhd_clss);
			pstmt.setString(idx++, cdhd_id);
			pstmt.setString(idx++, jumin_no);
			pstmt.setString(idx++, cdhd_grd_seq_no);
			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);
			pstmt.setString(idx++, email);
			pstmt.setString(idx++, hadc_num);
			pstmt.setString(idx++, note);
			result = pstmt.executeUpdate();
			
			// 참가자 등록 - 신청자
			sql = this.getEvtCompnQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			idx = 1;	// 13
			pstmt.setInt(idx++, aplc_seq_no);
			pstmt.setString(idx++, "1");
			pstmt.setString(idx++, "1");
			pstmt.setString(idx++, bkg_pe_nm);
			pstmt.setString(idx++, chdh_non_cdhd_clss);
			pstmt.setString(idx++, cdhd_id);
			pstmt.setString(idx++, jumin_no);
			pstmt.setString(idx++, cdhd_grd_seq_no);
			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);
			pstmt.setString(idx++, email);
			pstmt.setString(idx++, hadc_num);
			pstmt.setString(idx++, sttl_amt);
			result = pstmt.executeUpdate();
			
			
			// 참가자 등록 - 동반자
			String [] compn_bkg_pe_nm 			= GolfUtil.split(arr_compn_bkg_pe_nm, "||||");
			String [] compn_hp_ddd_no 			= GolfUtil.split(arr_compn_hp_ddd_no, "||||");
			String [] compn_hp_tel_hno 			= GolfUtil.split(arr_compn_hp_tel_hno, "||||");
			String [] compn_hp_tel_sno 			= GolfUtil.split(arr_compn_hp_tel_sno, "||||");
			String [] compn_cdhd_grd_seq_no 	= GolfUtil.split(arr_compn_cdhd_grd_seq_no, "||||");
			
			String arr_bkg_pe_nm = "";
			String arr_hp_ddd_no = "";
			String arr_hp_tel_hno = "";
			String arr_hp_tel_sno = "";
			String arr_cdhd_grd_seq_no = "";
			
			for(int i=0; i<compn_no-1; i++){

				if(compn_bkg_pe_nm.length>i){
					arr_bkg_pe_nm = compn_bkg_pe_nm[i];
				}else{
					arr_bkg_pe_nm = "";
				}
				
				if(compn_hp_ddd_no.length>i){
					arr_hp_ddd_no = compn_hp_ddd_no[i];
				}else{
					arr_hp_ddd_no = "";
				}
				
				if(compn_hp_tel_hno.length>i){
					arr_hp_tel_hno = compn_hp_tel_hno[i];
				}else{
					arr_hp_tel_hno = "";
				}
				
				if(compn_hp_tel_sno.length>i){
					arr_hp_tel_sno = compn_hp_tel_sno[i];
				}else{
					arr_hp_tel_sno = "";
				}
				
				if(compn_cdhd_grd_seq_no.length>i){
					arr_cdhd_grd_seq_no = compn_cdhd_grd_seq_no[i];
				}else{
					arr_cdhd_grd_seq_no = "";
				}

				sql = this.getEvtCompnQuery();   
				pstmt = conn.prepareStatement(sql.toString());
				idx = 1;	// 13
				pstmt.setInt(idx++, aplc_seq_no);
				pstmt.setInt(idx++, i+2);
				pstmt.setString(idx++, "2");
				pstmt.setString(idx++, arr_bkg_pe_nm);
				pstmt.setString(idx++, "");
				pstmt.setString(idx++, "");
				pstmt.setString(idx++, "");
				pstmt.setString(idx++, arr_cdhd_grd_seq_no);
				pstmt.setString(idx++, arr_hp_ddd_no);
				pstmt.setString(idx++, arr_hp_tel_hno);
				pstmt.setString(idx++, arr_hp_tel_sno);
				pstmt.setString(idx++, "");
				pstmt.setString(idx++, "");
				pstmt.setString(idx++, "");
				result = pstmt.executeUpdate();
			}
			

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

	public int execute_jumin(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String jumin_no 	= data.getString("jumin_no");
			String green_nm 	= data.getString("green_nm");
			String rsvt_date 	= data.getString("rsvt_date");
			String rsv_time 	= data.getString("rsv_time");
			
			String sql = this.getCntJuminQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, jumin_no);
			pstmt.setString(++idx, green_nm);
			pstmt.setString(++idx, rsvt_date);
			pstmt.setString(++idx, rsv_time);

			rs = pstmt.executeQuery();

			if ( rs != null ) {
				rs.next();
				result = rs.getInt("CNT");
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

	public int execute_hp(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);

			String hp_ddd_no	= data.getString("hp_ddd_no");
			String hp_tel_hno	= data.getString("hp_tel_hno");
			String hp_tel_sno	= data.getString("hp_tel_sno");
			String green_nm 	= data.getString("green_nm");
			String rsvt_date 	= data.getString("rsvt_date");
			String rsv_time 	= data.getString("rsv_time");
			
			String sql = this.getCntHpQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 0;
			pstmt.setString(++idx, hp_ddd_no);
			pstmt.setString(++idx, hp_tel_hno);
			pstmt.setString(++idx, hp_tel_sno);
			pstmt.setString(++idx, green_nm);
			pstmt.setString(++idx, rsvt_date);
			pstmt.setString(++idx, rsv_time);

			rs = pstmt.executeQuery();

			if ( rs != null ) {
				rs.next();
				result = rs.getInt("CNT");
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
    * 이벤트 max_seq 가져오기
    ************************************************************************ */
    private String getEvtSeqQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT NVL(MAX(APLC_SEQ_NO),0)+1 MAX_SEQ_NO FROM BCDBA.TBGGOLFEVNTAPLC	\n");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * 이벤트 등록 
    ************************************************************************ */
    private String getEvtQuery(){
        StringBuffer sql = new StringBuffer();	

		sql.append("\n	INSERT INTO BCDBA.TBGGOLFEVNTAPLC (	");
		sql.append("\n		SITE_CLSS, APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, GREEN_NM, APLC_ATON, RSVT_DATE, RSV_TIME, BKG_PE_NM, CDHD_NON_CDHD_CLSS, CDHD_ID	");
		sql.append("\n	    , JUMIN_NO, CDHD_GRD_SEQ_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, EVNT_PGRS_CLSS, STTL_STAT_CLSS, NOTE	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    '2', ?, '9003', ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?, 'R', '0', ?	");
		sql.append("\n	)	");

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
    * 같은 주민등록 번호로 등록된 신청내역이 있는지 알아본다.
    ************************************************************************ */
    private String getCntJuminQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLC 	\n");
		sql.append("\t	WHERE JUMIN_NO=? AND GREEN_NM=? AND RSVT_DATE=? AND RSV_TIME=?	\n");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * 같은 핸드폰 번호로 등록된 신청내역이 있는지 알아본다.
    ************************************************************************ */
    private String getCntHpQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLC	\n");
		sql.append("\t	WHERE HP_DDD_NO=? AND HP_TEL_HNO=? AND HP_TEL_SNO=? AND GREEN_NM=? AND RSVT_DATE=? AND RSV_TIME=?	\n");
		
		return sql.toString();
    }
}
