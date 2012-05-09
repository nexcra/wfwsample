/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntMkMemberListDaoProc
*   작성자    : 이정규
*   내용      : 마케팅 회원 신청
*   적용범위  : golf
*   작성일자  : 2010-08-31
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event;

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
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfEvntMkMemberListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntMkMemberListDaoProc() {}	

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
			String userSocid = data.getString("userSocid");
			
			conn = context.getDbConnection("default", null);
			
			String sql = this.getSelectQuery(); 
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1,userSocid);
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("TIME_SEQ_NO" 		,rs.getLong("RSVT_ABLE_BOKG_TIME_SEQ_NO") );
					result.addString("BKG_PE_NM" 			,rs.getString("BKG_PE_NM") );	//이름
					result.addString("TEOF_DATE" 			,rs.getString("TEOF_DATE") );	//쿠폰 발급일자
					
										
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
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getPreBkEvntDate(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			String sql = this.getSelectQuery2();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					String evnt_strt_date = rs.getString("EVNT_STRT_DATE");
					if (!GolfUtil.isNull(evnt_strt_date)) evnt_strt_date = DateUtil.format(evnt_strt_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("EVNT_STRT_DATE"		,evnt_strt_date);
					String evnt_end_date = rs.getString("EVNT_END_DATE");
					if (!GolfUtil.isNull(evnt_end_date)) evnt_end_date = DateUtil.format(evnt_end_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("EVNT_END_DATE"		,evnt_end_date);					
										
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
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			SELECT JUMIN_NO,SITE_CLASS,PWIN_GRD,PWIN_DATE,HG_NM,CARD_NO,CUPN_NO " );
		sql.append("\n FROM BCDBA.TBEVNTLOTPWIN WHERE SITE_CLSS ='10' 	");
		sql.append("\n 			AND EVNT_NO =120 AND JUMIN_NO = ? " );
		//sql.append("\n 			AND CARD_NO = ? " );
		sql.append("\n 			AND PROC_YN =’1’ 	");
		sql.append("\n 			)	");

		return sql.toString();
    }

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery2(){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT ");
		sql.append("\n 	MIN(EVNT_STRT_DATE) EVNT_STRT_DATE,	");
		sql.append("\n 	MAX(EVNT_END_DATE) EVNT_END_DATE	");
		sql.append("\n FROM	");
		sql.append("\n BCDBA.TBGEVNTMGMT	");
		sql.append("\n WHERE EVNT_CLSS = '0002'	");
		sql.append("\n AND BLTN_YN = 'Y'	");
		sql.append("\n AND TO_CHAR(SYSDATE, 'YYYYMMDD') BETWEEN EVNT_STRT_DATE AND EVNT_END_DATE	");

		return sql.toString();
    }
}
