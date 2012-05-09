/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreGrMapViewDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 골프장 지도보기 처리
*   적용범위  : golf
*   작성일자  : 2009-06-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.par;

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
public class GolfBkParTimesDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPermissionDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBkParTimesDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			int intMemGrade = 0;
			String memb_id = "";
			// 01. 세션정보체크
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				intMemGrade = userEtt.getIntMemGrade();
				memb_id = userEtt.getAccount();
			}
						 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery(intMemGrade, memb_id);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("PAR_3_BOKG_YR" 			,rs.getString("PAR_3_BOKG_YR") );
					result.addString("PAR_3_BOKG_MO" 			,rs.getString("PAR_3_BOKG_MO") );
					
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
    private String getSelectQuery(int intMemGrade, String memb_id){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\t  		SELECT 														\n");
		sql.append("\t  	    CASE PAR_3_BOKG_LIMT_YN										\n");
		sql.append("\t  	        WHEN 'A' THEN 1											\n");
		sql.append("\t  	        ELSE PAR_3_BOKG_YR_ABLE_NUM-PAR_3_BOKG_YR_DONE			\n");
		sql.append("\t  	        END AS PAR_3_BOKG_YR									\n");
		sql.append("\t  	    , CASE PAR_3_BOKG_LIMT_YN									\n");
		sql.append("\t  	        WHEN 'A' THEN 1											\n");
		sql.append("\t  	        ELSE PAR_3_BOKG_MO_ABLE_NUM-PAR_3_BOKG_MO_DONE			\n");
		sql.append("\t  	        END AS PAR_3_BOKG_MO									\n");
		sql.append("\t  	    FROM ( 														\n");
	        
		sql.append("\t  	    SELECT     													\n");
		sql.append("\t  	        PAR_3_BOKG_LIMT_YN        		\n");//-- 파3부킹제한여부
		sql.append("\t  	        , PAR_3_BOKG_YR_ABLE_NUM    	\n");//-- 파3부킹년가능횟수
		sql.append("\t  	        , PAR_3_BOKG_MO_ABLE_NUM    	\n");//-- 파3부킹월가능횟수    
	    
	    //-- 파3부킹 년갯수
		sql.append("\t  	        , (SELECT COUNT(*)  																\n");
		sql.append("\t  	        FROM BCDBA.TBGRSVTMGMT T1															\n");
		sql.append("\t  	        LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t  	        WHERE T1.CDHD_ID='"+memb_id+"'														\n");
		sql.append("\t  	        AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')						\n");
		sql.append("\t  	        AND T1.RSVT_YN='Y'																	\n");
		sql.append("\t  	        AND T1.REG_ATON > (SELECT TO_CHAR(ADD_MONTHS(SYSDATE, -12), 'YYYY')||SUBSTR(ACRG_CDHD_JONN_DATE,5,8) FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID='"+memb_id+"')\n");
		sql.append("\t  	        AND T2.GOLF_SVC_RSVT_NO IS NULL) AS PAR_3_BOKG_YR_DONE								\n");
		
	    //-- 파3부킹 월갯수
		sql.append("\t  	        , (SELECT COUNT(*)  																\n");
		sql.append("\t  	        FROM BCDBA.TBGRSVTMGMT T1															\n");
		sql.append("\t  	        LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t  	        WHERE T1.CDHD_ID='"+memb_id+"'														\n");
		sql.append("\t  	        AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')						\n");
		sql.append("\t  	        AND T1.RSVT_YN='Y'																	\n");
		sql.append("\t  	        AND T1.REG_ATON > (SELECT TO_CHAR(ADD_MONTHS(SYSDATE, -1), 'YYYY')||SUBSTR(ACRG_CDHD_JONN_DATE,5,8) FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID='"+memb_id+"')\n");
		sql.append("\t  	        AND T2.GOLF_SVC_RSVT_NO IS NULL) AS PAR_3_BOKG_MO_DONE								\n");
		

		sql.append("\t  	        FROM BCDBA.TBGGOLFCDHDBNFTMGMT													\n");
		sql.append("\t  	        WHERE CDHD_SQ2_CTGO='000"+intMemGrade+"'										\n");
		sql.append("\t  	    )																					\n");
	    
		return sql.toString();
    }
}
