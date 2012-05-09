/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfZipCodePopInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 공통지역별 우편번호 검색
*   적용범위  : golf
*   작성일자  : 2009-06-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.zipcode;

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
public class GolfZipCodePopInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfZipCodePopInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfZipCodePopInqDaoProc() {}	

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
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String dong		= data.getString("DONG");
			
			sql = this.getSelectQuery(dong);   
				
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			if (!GolfUtil.isNull(dong)) pstmt.setString(++idx, "%"+dong+"%");
			
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("ZIPCODE1" 		,rs.getString("ZIPCODE1") );
					result.addString("ZIPCODE2" 		,rs.getString("ZIPCODE2") );
					result.addString("ZIPADDR" 		,rs.getString("ZIPADDR") );
										
					result.addString("RESULT", "00"); //정상결과
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(String dong){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT ");
		sql.append("\n		SUBSTR (ZP, 1, 3) ZIPCODE1, SUBSTR (ZP, 4, 6) ZIPCODE2, ");									
		sql.append("\n 	LG_CTY_NM ||' '|| CTY_RGN_NM ||' '|| DONG_NM || DECODE (HOUS_NM, '', '', ' '|| HOUS_NM) || DECODE (ETC_ADDR, '', '', ' '|| ETC_ADDR) ZIPADDR ");										
		sql.append("\n FROM BCDBA.TBGZP ");	
		sql.append("\n WHERE SEQ = SEQ ");
		
		if (!GolfUtil.isNull(dong)) sql.append("\n AND DONG_NM LIKE ? ");	
		
		sql.append("\n ORDER BY ZP ");	
		return sql.toString();
    }
   
}
