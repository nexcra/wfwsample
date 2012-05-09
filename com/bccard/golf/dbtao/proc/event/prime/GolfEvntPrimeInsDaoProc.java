/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntShopListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 리스트 
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.prime;

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
public class GolfEvntPrimeInsDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntPrimeInsDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);

			// 신청정보
			String cdhd_id		= data.getString("cdhd_id");		// 회원아이디
			String bkg_pe_num	= data.getString("bkg_pe_num");		// 성명
			String jumin_no1	= data.getString("jumin_no1");		// 주민등록번호1
			String jumin_no2	= data.getString("jumin_no2");		// 주민등록번호2
			String hp_ddd_no	= data.getString("hp_ddd_no");		// 연락처1
			String hp_tel_hno	= data.getString("hp_tel_hno");		// 연락처2
			String hp_tel_sno	= data.getString("hp_tel_sno");		// 연락처3
			String ddd_no		= data.getString("ddd_no");			// 집전화1
			String tel_hno		= data.getString("tel_hno");		// 집전화2
			String tel_sno		= data.getString("tel_sno");		// 집전화3
			String dtl_addr		= data.getString("dtl_addr");		// 주소
			String lesn_seq_no	= data.getString("lesn_seq_no");	// 가입멤버십
			String pu_date		= data.getString("pu_date");		// 회원시작일
			String memo_expl	= data.getString("memo_expl");		// 기타 요청 사항
			
			// 결제정보
			String order_no		= data.getString("order_no");		// 주문코드
			String realPayAmt	= data.getString("realPayAmt");		// 결제 금액
			 
			
			// 신청정보 입력
			pstmt = conn.prepareStatement(this.getEvntInsQuery());
			int idx = 1;
			pstmt.setString(idx++, cdhd_id);
			pstmt.setString(idx++, bkg_pe_num);
			pstmt.setString(idx++, jumin_no1+jumin_no2);
			pstmt.setString(idx++, hp_ddd_no);
			pstmt.setString(idx++, hp_tel_hno);
			pstmt.setString(idx++, hp_tel_sno);
			pstmt.setString(idx++, ddd_no);
			pstmt.setString(idx++, tel_hno);
			pstmt.setString(idx++, tel_sno);
			pstmt.setString(idx++, dtl_addr);
			pstmt.setString(idx++, lesn_seq_no);
			pstmt.setString(idx++, realPayAmt);
			pstmt.setString(idx++, pu_date);
			pstmt.setString(idx++, memo_expl);
			pstmt.setString(idx++, order_no);
			result = pstmt.executeUpdate();

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	// 결제 실패했을 경우 주문내역 업데이트
	public int execute_upd(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);

			String order_no			= data.getString("order_no");			// 주문번호
			String pgrs_yn			= data.getString("pgrs_yn");			// 결제상태
			String cslt_yn			= data.getString("cslt_yn");			// 결제방식 1:온라인결제
			
			String sql = this.getUpdQuery();   
			
			// 입력값 (INPUT)   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++, pgrs_yn);
			pstmt.setString(idx++, cslt_yn);
			pstmt.setString(idx++, order_no);
			result = pstmt.executeUpdate();

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	
	// 이미 신청한 내역이 있는지 알아본다.
	public int execute_insYn(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;
		ResultSet rs = null;

		try {
			conn = context.getDbConnection("default", null);

			String jumin_no			= data.getString("jumin_no");			// 주민등록번호
			String bkg_pe_num		= data.getString("bkg_pe_num");			// 성명
			
			String sql = this.getEvntInsYnQuery();   
			
			// 입력값 (INPUT)   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 1;
			pstmt.setString(idx++, jumin_no);
			pstmt.setString(idx++, bkg_pe_num);
			rs = pstmt.executeQuery();

			if(rs.next()){
				result = rs.getInt("CNT");
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	/** ***********************************************************************
    * 이미 신청한 내역이 있는지 알아본다.
    ************************************************************************ */
	
    private String getEvntInsYnQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT COUNT(*) CNT	\n");
		sql.append("\t	FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='1003' AND PGRS_YN IN ('I','Y') AND JUMIN_NO=? AND BKG_PE_NM=?	\n");
		return sql.toString();
    }
    
	/** ***********************************************************************
    * 신청내용 등록
    ************************************************************************ */
	
    private String getEvntInsQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	INSERT INTO BCDBA.TBGAPLCMGMT (	\n");
		sql.append("\t	APLC_SEQ_NO, CDHD_ID, BKG_PE_NM, JUMIN_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, DDD_NO, TEL_HNO, TEL_SNO	\n");
		sql.append("\t	, DTL_ADDR, LESN_SEQ_NO, STTL_AMT, PU_DATE, MEMO_EXPL, CO_NM, PGRS_YN, REG_ATON, GREEN_NM, GOLF_SVC_APLC_CLSS	\n");
		sql.append("\t	) VALUES (	\n");
		sql.append("\t	(SELECT MAX(APLC_SEQ_NO)+1 FROM BCDBA.TBGAPLCMGMT), ?, ?, ?, ?, ?, ?, ?, ?, ?	\n");
		sql.append("\t	, ?, ?, ?, ?, ?, ?, 'I', TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), TO_CHAR(SYSDATE+365,'YYYYMMDDHH24MISS'), '1003'	\n");
		sql.append("\t	)	\n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * 신청내용 - 결제 실패 저장
     ************************************************************************ */
 	
     private String getUpdQuery(){
         StringBuffer sql = new StringBuffer();		

  		sql.append("\n	UPDATE BCDBA.TBGAPLCMGMT 	\n");
 		sql.append("\t	SET PGRS_YN = ? , CSLT_YN = ?	\n");
 		sql.append("\t	WHERE CO_NM = ?	\n");
 		
 		return sql.toString();
     }
}
