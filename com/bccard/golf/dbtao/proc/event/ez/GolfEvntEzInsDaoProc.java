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

package com.bccard.golf.dbtao.proc.event.ez;

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
public class GolfEvntEzInsDaoProc extends AbstractProc {
	
	public GolfEvntEzInsDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	
	// 유료회원 여부 가져오기 (유료회원 종료일자)
	public String cntMemFunction(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String result = "";

		try {
			conn = context.getDbConnection("default", null);
			
			String jumin_no		= data.getString("jumin_no");
		
			// 회원여부가져오기
			pstmt = conn.prepareStatement(getCntMem());
			pstmt.setString(1, jumin_no);
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				result = rs.getString("END_DATE");
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
	
	// 신청내역 등록여부
	public int cntEvntFunction(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String jumin_no		= data.getString("jumin_no");
		
			pstmt = conn.prepareStatement(getCntEvnt());
			pstmt.setString(1, jumin_no);
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
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
	
	// 신청내역 등록하기
	public int insEvnt(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int maxSeq = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String jumin_no		= data.getString("jumin_no");
			String ur_name		= data.getString("ur_name");
			String payMoney		= data.getString("payMoney");
			String goodsCd		= data.getString("goodsCd");
			

			pstmt = conn.prepareStatement(getEvntSeq());
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()){
				maxSeq = rs.getInt("SEQ");
			}	

			
			// 이벤트 내역 등록하기
			int idx = 0;
			pstmt = conn.prepareStatement(getInsEvnt());
			pstmt.setInt(++idx, maxSeq);
			pstmt.setString(++idx, jumin_no);
			pstmt.setString(++idx, ur_name);
			pstmt.setString(++idx, payMoney);
			pstmt.setString(++idx, goodsCd);
			result = pstmt.executeUpdate();
			
			if(result>0){
				result = maxSeq;
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
    * 유료회원 여부 가져오기
    ************************************************************************ */
    private String getCntMem(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT TO_CHAR(TO_DATE(ACRG_CDHD_END_DATE),'YYYY-MM-DD') END_DATE 	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t	WHERE JUMIN_NO = ? AND NVL(SECE_YN,'N') = 'N'	\n");
		sql.append("\t	    AND ACRG_CDHD_JONN_DATE <= TO_CHAR(SYSDATE,'yyyyMMdd') AND ACRG_CDHD_END_DATE >= TO_CHAR(SYSDATE,'yyyyMMdd')	\n");
		sql.append("\t	    AND CDHD_CTGO_SEQ_NO IN (SELECT CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	        FROM BCDBA.TBGCMMNCODE T1	\n");
		sql.append("\t	        JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.GOLF_CMMN_CODE = T2.CDHD_SQ2_CTGO	\n");
		sql.append("\t	        WHERE T1.GOLF_URNK_CMMN_CLSS='0000' AND T1.GOLF_URNK_CMMN_CODE='0005' AND CDHD_SQ1_CTGO='0002'	\n");
		sql.append("\t	        AND CDHD_CTGO_SEQ_NO NOT IN ('8','18','16') )	\n");
		return sql.toString();
    }

	/** ***********************************************************************
    * 유료회원 여부 가져오기
    ************************************************************************ */
    private String getCntEvnt(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT COUNT(*) CNT FROM BCDBA.TBGAPLCMGMT WHERE GOLF_SVC_APLC_CLSS='0012' AND PGRS_YN IN ('Y') AND CO_NM=? 	\n");
		return sql.toString();
    }

	/** ***********************************************************************
    * 이벤트 테이블 최대값가져오기
    ************************************************************************ */
    private String getEvntSeq(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	SELECT MAX(APLC_SEQ_NO)+1 SEQ FROM BCDBA.TBGAPLCMGMT	\n");
		return sql.toString();
    }

	/** ***********************************************************************
    * 이벤트 테이블 저장하기
    ************************************************************************ */
    private String getInsEvnt(){
        StringBuffer sql = new StringBuffer();	
		sql.append("	\n");
		sql.append("\t	INSERT INTO BCDBA.TBGAPLCMGMT (APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, PGRS_YN, CDHD_ID, JUMIN_NO, BKG_PE_NM, REG_ATON, CHNG_ATON, STTL_AMT, RSVT_CDHD_GRD_SEQ_NO)	\n");
		sql.append("\t	VALUES	\n");
		sql.append("\t	(?, '0012', 'I', '', ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), '', ?, ?)	\n");
		return sql.toString();
    }

}
