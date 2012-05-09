/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCodeSelDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 코드 셀렉트 박스 생성
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.code;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmCodeSelDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 코드 셀렉트 박스 생성";
	
	/** *****************************************************************
	 * GolfAdmCodeSelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmCodeSelDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data, String Code, String use_yn) throws BaseException {
		
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);

		try {
			con = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	");
			sql.append("\n 	GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM	");
			sql.append("\n FROM 	");
			sql.append("\n BCDBA.TBGCMMNCODE	");
			sql.append("\n WHERE GOLF_URNK_CMMN_CODE =?	");
			if (use_yn != null && !use_yn.equals("")) { sql.append("\n AND USE_YN = ? ");	}
			sql.append("\n ORDER BY SORT_SEQ	");									
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = con.prepareStatement(sql.toString());
			pstmt.setString(++idx, Code);
			if (use_yn != null && !use_yn.equals("")) {  pstmt.setString(++idx, use_yn);	}
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("CD" ,rs.getString("GOLF_CMMN_CODE") );
					result.addString("CD_NM" ,rs.getString("GOLF_CMMN_CODE_NM") );
					
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
			try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}
}
