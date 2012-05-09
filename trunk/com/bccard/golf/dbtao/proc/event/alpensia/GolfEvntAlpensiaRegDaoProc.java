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

package com.bccard.golf.dbtao.proc.event.alpensia;

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
public class GolfEvntAlpensiaRegDaoProc extends AbstractProc {
	
	public GolfEvntAlpensiaRegDaoProc() {}	

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
		int idx = 1;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String golf_svc_aplc_clss	= data.getString("golf_svc_aplc_clss");	// 부킹 구분
			String bkg_pe_nm 			= data.getString("bkg_pe_nm");			// 예약자 이름
			String trm_unt 				= data.getString("trm_unt");			// 1:1박2일, 2:2박3일
			String cdhd_id 				= data.getString("cdhd_id");			// 아이디
			String cdhd_grd_seq_no 		= data.getString("cdhd_grd_seq_no");	// 인원수(1:1팀 신청, 2:단체 신청)
			String hp_ddd_no 			= data.getString("hp_ddd_no");			// 핸드폰
			String hp_tel_hno 			= data.getString("hp_tel_hno");			// 핸드폰
			String hp_tel_sno 			= data.getString("hp_tel_sno");			// 핸드폰
			String hadc_num 			= data.getString("hadc_num");			// 핸디
			String rsvt_date 			= data.getString("rsvt_date");			// 예약일
			String rsv_time 			= data.getString("rsv_time");			// 예약시간
			String cus_rmrk 			= data.getString("cus_rmrk");			// 고객요청

			String jumin_no 			= data.getString("jumin_no");			// 주민등록번호
			String ddd_no 				= data.getString("ddd_no");				// 전화번호
			String tel_hno 				= data.getString("tel_hno");			// 전화번호
			String tel_sno 				= data.getString("tel_sno");			// 전화번호
			String email 				= data.getString("email");				// 이메일
			int pnum 					= data.getInt("pnum");					// 인원수
			int tnum 					= data.getInt("tnum");					// 팀수
			String opt_yn 				= data.getString("opt_yn");				// 숙소구분-옵션사용구분코드 Y:2인1실 N:4인1실
			String compn_opt_yn 		= data.getString("compn_opt_yn");		// 동반자 - 숙소구분-옵션사용구분코드 Y:2인1실 N:4인1실
			String compn_bkg_pe_nm 		= data.getString("compn_bkg_pe_nm");	// 동반자 - 이름
			String compn_hp_ddd_no 		= data.getString("compn_hp_ddd_no");	// 동반자 - 연락처
			String compn_hp_tel_hno 	= data.getString("compn_hp_tel_hno");	// 동반자 - 연락처
			String compn_hp_tel_sno 	= data.getString("compn_hp_tel_sno");	// 동반자 - 연락처
			
			int aplc_seq_no = 0;
			String sql = this.getEvtSeqQuery();  
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			if (rs.next()) {
				aplc_seq_no = rs.getInt("MAX_SEQ_NO");
			}

			// 이벤트 등록
			idx = 1;
			sql = this.getEvtQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setInt(idx++, 2);						// 사이트구분코드
			pstmt.setInt(idx++, aplc_seq_no);			// 신청일련번호
			pstmt.setString(idx++, golf_svc_aplc_clss);	// 골프서비스신청구분코드
			pstmt.setString(idx++, "알펜시아");			// 골프장명
			pstmt.setString(idx++, rsvt_date);			// 예약일자
			pstmt.setString(idx++, rsv_time);			// 예약시간
			pstmt.setString(idx++, bkg_pe_nm);			// 예약자성명
			pstmt.setString(idx++, "");					// 회원비회원구분코드
			pstmt.setString(idx++, cdhd_id);			// 회원 ID

			pstmt.setString(idx++, jumin_no);			// 주민등록번호
			pstmt.setString(idx++, cdhd_grd_seq_no);	// 회원등급일련번호 = 인원
			pstmt.setString(idx++, hp_ddd_no);			// 핸드폰
			pstmt.setString(idx++, hp_tel_hno);			// 핸드폰
			pstmt.setString(idx++, hp_tel_sno);			// 핸드폰
			pstmt.setString(idx++, email);				// 이메일
			pstmt.setString(idx++, hadc_num);			// 핸디캡수
			pstmt.setString(idx++, "R");				// 이벤트진행구분코드
			pstmt.setString(idx++, "0");				// 결제상태구분코드
			pstmt.setInt(idx++, 0);						// 결제금액
			
			pstmt.setString(idx++, "");					// 취소일시
			pstmt.setString(idx++, cus_rmrk);			// 고객요청사항
			pstmt.setString(idx++, "");					// 관리자메모내용
			pstmt.setString(idx++, ddd_no+"-"+tel_hno+"-"+tel_sno);	// 비고 - 전화번호
			pstmt.setString(idx++, trm_unt);			// 기간단위코드
			pstmt.setString(idx++, opt_yn);				// 옵션사용구분코드   1: 2인1실 , 2: 4인 1실 등
			pstmt.setString(idx++, "");					// 공통안내
			result = pstmt.executeUpdate();
			
			if(golf_svc_aplc_clss.equals("8002") || golf_svc_aplc_clss.equals("8003")){

				String [] arr_compn_opt_yn 			= GolfUtil.split(compn_opt_yn, "||");
				String [] arr_compn_bkg_pe_nm 		= GolfUtil.split(compn_bkg_pe_nm, "||");
				String [] arr_compn_hp_ddd_no		= GolfUtil.split(compn_hp_ddd_no, "||");
				String [] arr_compn_hp_tel_hno		= GolfUtil.split(compn_hp_tel_hno, "||");
				String [] arr_compn_hp_tel_sno		= GolfUtil.split(compn_hp_tel_sno, "||");
				
				String str_compn_opt_yn = "";
				String str_compn_bkg_pe_nm = "";
				String str_compn_hp_ddd_no = "";
				String str_compn_hp_tel_hno = "";
				String str_compn_hp_tel_sno = "";
				int num = 0;
				
				for(int i=0; i<tnum; i++){

					if(arr_compn_opt_yn.length>i){
						str_compn_opt_yn = arr_compn_opt_yn[i];
					}else{
						str_compn_opt_yn = "";
					}
					
					for(int j=0; j<pnum; j++){

						if(arr_compn_bkg_pe_nm.length>num){
							str_compn_bkg_pe_nm = arr_compn_bkg_pe_nm[num];
						}else{
							str_compn_bkg_pe_nm = "";
						}
						if(arr_compn_hp_ddd_no.length>num){
							str_compn_hp_ddd_no = arr_compn_hp_ddd_no[num];
						}else{
							str_compn_hp_ddd_no = "";
						}
						if(arr_compn_hp_tel_hno.length>num){
							str_compn_hp_tel_hno = arr_compn_hp_tel_hno[num];
						}else{
							str_compn_hp_tel_hno = "";
						}
						if(arr_compn_hp_tel_sno.length>num){
							str_compn_hp_tel_sno = arr_compn_hp_tel_sno[num];
						}else{
							str_compn_hp_tel_sno = "";
						}
								
						// 동반자 등록
						idx = 1;
						sql = this.getEvtCompnQuery();   
						pstmt = conn.prepareStatement(sql.toString());
						pstmt.setInt(idx++, 2);						// 사이트구분코드
						pstmt.setInt(idx++, aplc_seq_no);			// 신청일련번호
						pstmt.setInt(idx++, num+1);					// SEQ_NO 동반자 인원
						pstmt.setString(idx++, golf_svc_aplc_clss);	// 골프서비스신청구분코드
						pstmt.setString(idx++, "2");				// 신청자구분코드 1:신청자, 2:동반자
						pstmt.setString(idx++, str_compn_bkg_pe_nm);// 예약자성명
						pstmt.setString(idx++, "");					// 회원비회원구분코드
						pstmt.setString(idx++, "");					// 회원 ID
						pstmt.setString(idx++, "");					// 주민등록번호
						pstmt.setString(idx++, cdhd_grd_seq_no);	// 회원등급일련번호 = 인원

						pstmt.setString(idx++, str_compn_hp_ddd_no);// 핸드폰
						pstmt.setString(idx++, str_compn_hp_tel_hno);// 핸드폰
						pstmt.setString(idx++, str_compn_hp_tel_sno);// 핸드폰
						pstmt.setString(idx++, "");					// 이메일
						pstmt.setString(idx++, "");			// 핸디캡수
						pstmt.setString(idx++, "0");				// 결제상태구분코드
						pstmt.setInt(idx++, 0);						// 결제금액
						pstmt.setString(idx++, "N");				// 취소여부
						pstmt.setString(idx++, "");					// 취소일시
						pstmt.setString(idx++, "");					// 비고 - 전화번호
						
						pstmt.setString(idx++, str_compn_opt_yn);	// 옵션사용구분코드   1: 2인1실 , 2: 4인 1실 등
						pstmt.setInt(idx++, i+1);					// 팀수
						result = pstmt.executeUpdate();	
						num++;
					}
				}
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
		sql.append("\n	    , JUMIN_NO, CDHD_GRD_SEQ_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, EVNT_PGRS_CLSS, STTL_STAT_CLSS, STTL_AMT	");
		sql.append("\n	    , CNCL_ATON, CUS_RMRK, MGR_MEMO, NOTE, TRM_UNT, OPT_YN, COMMON_RMRK	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    ?, ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?	");
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
		sql.append("\n	    , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, STTL_STAT_CLSS, STTL_AMT, CNCL_YN, CNCL_ATON, NOTE	");
		sql.append("\n	    , OPT_YN, TEAM_NUM	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    ?, ?, ?, ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?	");
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
