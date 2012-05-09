/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntKvpDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 월례회 > 등록처리
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
* 커멘드구분자	변경일		변경자	변경내용
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.kvp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfEvntKvpDaoProc extends AbstractProc {
	
	public GolfEvntKvpDaoProc() {}	

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
		int result =  0;
		int max_num = 0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			String socid						= data.getString("socid");
			String social_id_1					= data.getString("social_id_1");
			String name							= data.getString("name");
			String ddd_no						= data.getString("ddd_no");
			String tel_hno						= data.getString("tel_hno");
			String tel_sno						= data.getString("tel_sno");
			String hp_ddd_no					= data.getString("hp_ddd_no");
			String hp_tel_hno					= data.getString("hp_tel_hno");
			String hp_tel_sno					= data.getString("hp_tel_sno");
			String email						= data.getString("email");
			String kvp_idx						= data.getString("idx");
			String sttl_amt						= data.getString("realPayAmt");
			String card_no						= data.getString("CARD_NO");
			String vald_date					= data.getString("VALD_DATE");
			String chkResult					= data.getString("chkResult");		// 4일 경우 유료회원기간 연장처리 한다.
			String payType						= data.getString("payType");	// 1:카드 2:카드+포인트 3:타사카드
			
			 
			String cdhd_id = "";
			String cdhd_ctgo_seq_no = "";
			String cslt_yn = "N";
			
			//CSLT_YN, CDHD_ID
			
			String golf_cdhd_grd_clss = "";
			String golf_cdhd_grd_bnf = "";
			if(kvp_idx.equals("1")){
				golf_cdhd_grd_clss = "3";
				golf_cdhd_grd_bnf = "5";
			}else if(kvp_idx.equals("2")){
				golf_cdhd_grd_clss = "2";
				golf_cdhd_grd_bnf = "6";
			}else if(kvp_idx.equals("3")){
				golf_cdhd_grd_clss = "1";
				golf_cdhd_grd_bnf = "7";
			}
			
			else if(kvp_idx.equals("4")){
				
				golf_cdhd_grd_clss = "3";
				golf_cdhd_grd_bnf = "5";
				
			}else if(kvp_idx.equals("5")){
				
				golf_cdhd_grd_clss = "2";
				golf_cdhd_grd_bnf = "6";
				
			}else if(kvp_idx.equals("6")){
				
				golf_cdhd_grd_clss = "1";
				golf_cdhd_grd_bnf = "7";
				
			}else if(kvp_idx.equals("7")){
				
				golf_cdhd_grd_clss = "3";
				golf_cdhd_grd_bnf = "5";
				
			}else if(kvp_idx.equals("8")){
				
				golf_cdhd_grd_clss = "2";
				golf_cdhd_grd_bnf = "6";
				
			}else if(kvp_idx.equals("9")){
				
				golf_cdhd_grd_clss = "1";
				golf_cdhd_grd_bnf = "7";
			}				
			
			
			if(chkResult.equals("4")){
				cslt_yn = "Y";
				
				// 회원기간 연장일 경우 회원정보 가져오기
				pstmt = conn.prepareStatement(getIsMemQuery());
				pstmt.setString(1, socid);
				rs = pstmt.executeQuery();
				if(rs != null && rs.next()){
					cdhd_id = rs.getString("CDHD_ID");
					cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
				}				
			}

			
			// 신청테이블 idx 가져오기 - getMaxIdxQuery
			pstmt = conn.prepareStatement(getMaxIdxQuery());
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				max_num = rs.getInt("MAX_NUM");
			}
			
						
			// 신청테이블 등록 - 월결제 회원
			String sql = this.getEvtQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setInt(idx++, max_num);
			pstmt.setString(idx++, socid);
			pstmt.setString(idx++, sttl_amt);
			pstmt.setString(idx++, name);
			pstmt.setString(idx++, ddd_no);
			pstmt.setString(idx++, tel_hno);
			pstmt.setString(idx++, tel_sno);
			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);
			pstmt.setString(idx++, email);
			pstmt.setString(idx++, kvp_idx);
			pstmt.setString(idx++, cslt_yn);
			pstmt.setString(idx++, cdhd_id);
			result = pstmt.executeUpdate();
			
			if(result > 0){
				if(chkResult.equals("4")){
					// 유료회원기간 연장 처리 해준다.						
					sql = this.getMemPrdExQuery();   
					pstmt = conn.prepareStatement(sql.toString());	
					idx = 1;					
					pstmt.setString(idx++, golf_cdhd_grd_bnf);
					pstmt.setString(idx++, cdhd_id);
					result += pstmt.executeUpdate();
					
					sql = this.getMemGrdUpdQuery();   
					pstmt = conn.prepareStatement(sql.toString());	
					idx = 1;				
					pstmt.setString(idx++, golf_cdhd_grd_bnf);						
					pstmt.setString(idx++, cdhd_id);				
					pstmt.setString(idx++, cdhd_ctgo_seq_no);	
					result += pstmt.executeUpdate();
				}
				else{
					// TM 결과 테이블에 인서트 해준다. getTmInsQuery
					sql = this.getTmInsQuery();   
					pstmt = conn.prepareStatement(sql.toString());
					idx = 1;
					
					pstmt.setString(idx++, "1");				//MB_CDHD_NO - 회원사회원번호
					pstmt.setString(idx++, "4");				//ACPT_CHNL_CLSS - 접수채널 구분 코드
					pstmt.setString(idx++, name);				//HG_NM
					pstmt.setString(idx++, social_id_1);		//BTHD
					pstmt.setString(idx++, email);				//EMAIL_ID
					pstmt.setString(idx++, ddd_no);				//HOM_DDD_NO
					pstmt.setString(idx++, tel_hno);			//HOM_TEL_HNO
					pstmt.setString(idx++, tel_sno);			//HOM_TEL_SNO
					pstmt.setString(idx++, hp_ddd_no);			//HP_DDD_NO
					pstmt.setString(idx++, hp_tel_hno);			//HP_TEL_HNO
					
					pstmt.setString(idx++, hp_tel_sno);			//HP_TEL_SNO
					pstmt.setString(idx++, "01");				//TB_RSLT_CLSS - TM 결과 구분코드 01, 회원가입하면 00로 변경
	//				pstmt.setString(idx++, "");					//RECP_DATE - 등록일
					pstmt.setString(idx++, socid);				//JUMIN_NO
					pstmt.setString(idx++, "2");				//RND_CD_CLSS - 사이트 구분
					pstmt.setString(idx++, "01");				//JOIN_CHNL - 가입구분경로
					pstmt.setString(idx++, payType);				//AUTH_CLSS - 승인구분코드 1-카드 승인, 2-복합결제, 3-포인트결제
					pstmt.setString(idx++, card_no);			//CARD_NO
					pstmt.setString(idx++, golf_cdhd_grd_clss);	//GOLF_CDHD_GRD_CLSS - 등급
					pstmt.setString(idx++, vald_date);			//VALD_LIM - 유효기간
					//pstmt.setString(idx++, "5000");				//RCRU_PL_CLSS - 모집경로구분코드 5000->KVP
					pstmt.setString(idx++, "4200");				//RCRU_PL_CLSS - 모집경로구분코드 4200-> KT Olleh Club
					
					result += pstmt.executeUpdate();
				}
				
			}


			if(result > 1) {
				conn.commit();
				result = max_num;
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
	

	public int execute_isJoin(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);
			String socid = data.getString("socid");
			String join_chnl = "";
			String cdhd_ctgo_seq_no = "";
			String pay_over = "";
						
			// 이미 가입한 회원인지 알아보기  
			pstmt = conn.prepareStatement(getIsMemQuery());
			pstmt.setString(1, socid);
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				join_chnl = rs.getString("JOIN_CHNL");
				cdhd_ctgo_seq_no = rs.getString("CDHD_CTGO_SEQ_NO");
				pay_over = rs.getString("PAY_OVER");
				
				if(join_chnl.equals("4003") && cdhd_ctgo_seq_no.equals("18")){
					// 기업은행 이벤트 회원일경우 신청되도록 한다.
					result = 3;
				}else{
					if(pay_over.equals("Y")){
						// 유료회원기간이 지났다면 유료회원기간 연장으로 처리한다.
						result = 4;
					}else{
						result = 1;
					}
				}
			}else{
				// 이미 신청한 회원인지 알아보기
				pstmt = conn.prepareStatement(getIsAplyQuery());
				pstmt.setString(1, socid);
				rs = pstmt.executeQuery();
				if(rs != null && rs.next()){
					result = 2;
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
    * 신청테이블의 max_idx 가져오기
    ************************************************************************ */
    private String getMaxIdxQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT NVL(MAX(APLC_SEQ_NO),0)+1 MAX_NUM FROM BCDBA.TBGAPLCMGMT	");		
		return sql.toString();
    }


	/** ***********************************************************************
    * 이벤트 등록 
    ************************************************************************ */
    private String getEvtQuery(){
        StringBuffer sql = new StringBuffer();	
        
		sql.append("\n	INSERT INTO BCDBA.TBGAPLCMGMT (	");
		sql.append("\n	    APLC_SEQ_NO, GOLF_LESN_RSVT_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, JUMIN_NO, PU_DATE, CHNG_ATON, REG_ATON, STTL_AMT	");
		sql.append("\n	    , CO_NM, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, RSVT_CDHD_GRD_SEQ_NO, CSLT_YN, CDHD_ID	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    ?, 1, '1005', 'Y', ?, TO_CHAR(ADD_MONTHS(SYSDATE,1),'YYYYMMDD'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),? 	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?	");
		sql.append("\n	)	");
		
		return sql.toString();
    }

	/** ***********************************************************************
    * 같은 주민등록번호 회원이 있는지 확인 
    ************************************************************************ */
    private String getIsMemQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT JOIN_CHNL, CDHD_CTGO_SEQ_NO, CDHD_ID	");
		sql.append("\n	, CASE WHEN ACRG_CDHD_END_DATE<TO_DATE(SYSDATE) THEN 'Y' ELSE 'N' END PAY_OVER	");
		sql.append("\n	FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO = ? AND NVL(SECE_YN,'N')<>'Y'	AND CDHD_CTGO_SEQ_NO != '8' ");
		sql.append("\n	ORDER BY MEMBER_CLSS	");
		return sql.toString();
    }

	/** ***********************************************************************
    * 같은 주민등록번호 신청내역 있는지 확인
    ************************************************************************ */
    private String getIsAplyQuery(){
        StringBuffer sql = new StringBuffer();	
		//sql.append("\n	SELECT * FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='1001' AND GREEN_NM='KVP' AND PGRS_YN='Y' AND JUMIN_NO=?	");
		sql.append("\n	SELECT * FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='1005' AND PGRS_YN='Y' AND JUMIN_NO=?	");
		return sql.toString();
    }

	/** ***********************************************************************
    * TM 테이블 등록하기
    ************************************************************************ */
    private String getTmInsQuery(){
        StringBuffer sql = new StringBuffer();	
		sql.append("\n	INSERT INTO BCDBA.TBLUGTMCSTMR 	");
		sql.append("\n	(MB_CDHD_NO, ACPT_CHNL_CLSS, HG_NM, BTHD, EMAIL_ID, HOM_DDD_NO, HOM_TEL_HNO, HOM_TEL_SNO, HP_DDD_NO, HP_TEL_HNO	");
		sql.append("\n	 ,HP_TEL_SNO,TB_RSLT_CLSS,RECP_DATE, JUMIN_NO, RND_CD_CLSS, JOIN_CHNL, AUTH_CLSS, CARD_NO, GOLF_CDHD_GRD_CLSS, VALD_LIM, RCRU_PL_CLSS )	");
		sql.append("\n	VALUES (?,?,?,?,?,?,?,?,?,?	");
		sql.append("\n			,?,?,TO_CHAR(SYSDATE,'YYYYMMDD'),?,?,?,?,?,?,?,?)	");
		return sql.toString();
    }

	/** ***********************************************************************
    * 유료회원기간 연장하기
    ************************************************************************ */
    private String getMemPrdExQuery(){
        StringBuffer sql = new StringBuffer();	
		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHD SET  CDHD_CTGO_SEQ_NO=? 	");
		sql.append("\n	, ACRG_CDHD_JONN_DATE=TO_CHAR(SYSDATE,'YYYYMMDD'), ACRG_CDHD_END_DATE=TO_CHAR(SYSDATE+365,'YYYYMMDD') 	");
		sql.append("\n	WHERE CDHD_ID=?	");
		return sql.toString();
    }

	/** ***********************************************************************
    * 유료회원기간 연장하기
    ************************************************************************ */
    private String getMemGrdUpdQuery(){
        StringBuffer sql = new StringBuffer();	
		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHDGRDMGMT SET CDHD_CTGO_SEQ_NO=? WHERE CDHD_ID=? AND CDHD_CTGO_SEQ_NO=? 	");
		return sql.toString();
    }

}
