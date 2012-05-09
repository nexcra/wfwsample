/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMojibProc
*   작성자    : E4NET 은장선
*   내용      : 메인 비지니스 로직
*   적용범위  : Golf
*   작성일자  : 2009-09-03  
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.tm_member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
 
/** *****************************************************************
 * GolfAdmMojibProc 프로세스 생성자
 * @param N/A
 ***************************************************************** */
public class GolfAdmMojibEzReturnProc extends AbstractProc {
		
	//private static final String TITLE = "이지웰 수정";		
	
	/** *****************************************************************
	 * GolfAdmMojibProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMojibEzReturnProc() {}
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult 
	 */
	
	/*수정*/
	public int updState(WaContext context, TaoDataSet dataSet) throws BaseException {		

		//String title = dataSet.getString("TITLE");
		Connection con = null;
		PreparedStatement pstmt = null;
		int result = 0;
		String 	sStrSql = "";
		ResultSet rs = null;
			
		try{
			con = context.getDbConnection("default", null);	

			String aspOrderNum		= dataSet.getString("aspOrderNum");
			String orderNum			= dataSet.getString("orderNum");
			String jumin_no			= dataSet.getString("jumin_no");
			
			if(GolfUtil.empty(jumin_no)){
				// 취소대상자 주민등록번호 검색
				sStrSql = this.getTmJuminNo();
				pstmt = con.prepareStatement(sStrSql);
				pstmt.setString(1, aspOrderNum);
				pstmt.setString(2, orderNum);
				rs = pstmt.executeQuery(); 
				if (rs.next())	{		
					jumin_no	= rs.getString("JUMIN_NO");
				}
			}
			   
			// Tm 테이블 상태 취소로 업데이트
			sStrSql = this.getUpdTmSql();
			pstmt = con.prepareStatement(sStrSql);
			pstmt.setString(1, jumin_no);
			result = pstmt.executeUpdate(); 
			
			if(result>0){
				
				// 회원등급테이블 삭제
				sStrSql = this.getDelMemGradeSql();
				pstmt = con.prepareStatement(sStrSql);
				pstmt.setString(1, jumin_no);
				result = pstmt.executeUpdate(); 
				
				// 회원테이블 삭제
				sStrSql = this.getDelMemSql();
				pstmt = con.prepareStatement(sStrSql);
				pstmt.setString(1, jumin_no);
				result = pstmt.executeUpdate(); 
				
				// 신청테이블 업데이트
				sStrSql = this.getUpdAplSql();
				pstmt = con.prepareStatement(sStrSql);
				pstmt.setString(1, jumin_no);
				result = pstmt.executeUpdate(); 
			}
			
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {	
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}		
		
		return result;	
	}



	/* 삭제할 대상자 주민등록번호 가져오기 */
	public String getTmJuminNo(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n SELECT JUMIN_NO FROM BCDBA.TBGLUGANLFEECTNT WHERE MB_CDHD_NO='ezwel' AND AUTH_NO=? AND CARD_NO=?	");
		return sb.toString();
	}

	/* TM 테이블 업데이트 */
	public String getUpdTmSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n UPDATE BCDBA.TBLUGTMCSTMR SET TB_RSLT_CLSS='03', REJ_RSON='이지웰 취소' WHERE JUMIN_NO = ?	");
		return sb.toString();
	}

	/* 신청테이블 업데이트 */
	public String getUpdAplSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n UPDATE BCDBA.TBGAPLCMGMT SET PGRS_YN='F' WHERE GOLF_SVC_APLC_CLSS='0012' AND JUMIN_NO=?	");
		return sb.toString();
	}

	/* 등급 테이블 삭제 */
	public String getDelMemGradeSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n DELETE FROM BCDBA.TBGGOLFCDHDGRDMGMT WHERE CDHD_ID = (SELECT CDHD_ID FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO=?)	");
		return sb.toString();
	}
	
	/* 회원테이블 삭제 */
	public String getDelMemSql(){
		StringBuffer sb = new StringBuffer();
		sb.append("\n DELETE FROM BCDBA.TBGGOLFCDHD WHERE JUMIN_NO=?	");
		return sb.toString();
	}


}


