/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrUpdFormDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정 폼 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.mytbox.golf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfMtScoreAvgViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMtScoreAvgViewDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data, HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			UcusrinfoEntity usrEntity = SessionUtil.getFrontUserInfo(request);
            String userId	= "";
            
            if(usrEntity != null) 
        	{
        		userId		= (String)usrEntity.getAccount(); 
        	}
						 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, userId);
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {

					result.addString("AVG_HIT" 		,rs.getString("AVG_HIT") );
					result.addString("AVG_HADC" 	,rs.getString("AVG_HADC") );
					result.addString("AVG_MAX" 		,rs.getString("AVG_MAX") );
					result.addString("AVG_MIN" 		,rs.getString("AVG_MIN") );				
					
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();	
		
        sql.append("\n SELECT AVG_HIT, AVG_HADC																				\n");
		sql.append("\t	, MAX_CNT||' ( '||T2.GREEN_NM||'/'||T2.CURS_NM||' )' AVG_MAX										\n");
		sql.append("\t	, MIN_CNT||' ( '||T3.GREEN_NM||'/'||T3.CURS_NM||' )' AVG_MIN										\n");
		sql.append("\t	FROM (																								\n");
		sql.append("\t	    SELECT																							\n");
		sql.append("\t	    TO_CHAR(AVG(HIT_CNT),'FM999,990.0') AVG_HIT,  TO_CHAR(AVG(HADC_NUM),'FM999,990.0') AVG_HADC		\n");
		sql.append("\t	    , MAX(HIT_CNT) AS MAX_CNT, MIN(HIT_CNT) MIN_CNT													\n");
		sql.append("\t	    FROM																							\n");
		sql.append("\t	    BCDBA.TBGSCORINFO WHERE CDHD_ID=?) T1											\n");
		sql.append("\t	    JOIN (SELECT * FROM BCDBA.TBGSCORINFO) T2 ON T1.MAX_CNT=T2.HIT_CNT				\n");
		sql.append("\t	    JOIN (SELECT * FROM BCDBA.TBGSCORINFO) T3 ON T1.MIN_CNT=T3.HIT_CNT				\n");
		
		return sql.toString();
    }
}
