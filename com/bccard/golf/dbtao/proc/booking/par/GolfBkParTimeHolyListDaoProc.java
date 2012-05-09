/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admTitimeRegFormDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹티타임 입력 페이지 처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.par;

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
public class GolfBkParTimeHolyListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmBkTimeRegFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBkParTimeHolyListDaoProc() {}	

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
			conn = context.getDbConnection("default", null);
			String co_nm = data.getString("co_nm");	// 주말부킹 여부
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery(co_nm);
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 0;
			pstmt.setString(++idx, data.getString("AFFI_GREEN_SEQ_NO") ); 	
			pstmt.setString(++idx, data.getString("AFFI_GREEN_SEQ_NO") ); 	
			pstmt.setString(++idx, data.getString("AFFI_GREEN_SEQ_NO") ); 	
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("BK_DATE" 					,rs.getString("BK_DATE") );
					result.addString("RESM_DAY_RSON_CTNT"		,rs.getString("RESM_DAY_RSON_CTNT") );
										
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
    private String getSelectQuery(String co_nm){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT TO_CHAR(TO_DATE(PAR_3_BOKG_RESM_DATE),'MM\"월 \"DD\"일\"(DY)') BK_DATE	\n");
		sql.append("\t , RESM_DAY_RSON_CTNT																\n");
		sql.append("\t FROM BCDBA.TBGRSVTABLESCDMGMT													\n");
		sql.append("\t WHERE AFFI_GREEN_SEQ_NO=? AND RESM_YN !='H'										\n");
		sql.append("\t AND PAR_3_BOKG_RESM_DATE BETWEEN TO_CHAR(SYSDATE+2,'YYYYMMDD') AND (				\n");
		sql.append("\t     SELECT MAX(SDATE)															\n");
		sql.append("\t         FROM (																	\n");
		sql.append("\t             SELECT RNUM, TO_CHAR(SDATE, 'YYYYMMDD') SDATE FROM (					\n");
		sql.append("\t                 SELECT ROWNUM RNUM, SDATE FROM (									\n");
		sql.append("\t                    SELECT SYSDATE+LEVEL+(SELECT BOKG_CNCL_ABLE_TRM-1 FROM BCDBA.TBGAFFIGREEN WHERE AFFI_GREEN_SEQ_NO=?) SDATE									\n");
		sql.append("\t                    FROM DUAL														\n");
		sql.append("\t                    CONNECT BY SYSDATE+LEVEL<=SYSDATE+19							\n");
		sql.append("\t                 ) T1																\n");
		sql.append("\t                 WHERE TO_CHAR(SDATE,'YYYYMMDD') NOT IN (	\n");
		sql.append("\t                     SELECT PAR_3_BOKG_RESM_DATE									\n");
		sql.append("\t                     FROM BCDBA.TBGRSVTABLESCDMGMT								\n");
		sql.append("\t                     WHERE PAR_3_BOKG_RESM_DATE IS NOT NULL AND AFFI_GREEN_SEQ_NO=?						\n");
		
		if(!co_nm.equals("Y")){
		sql.append("\t                 )																\n");
		sql.append("\t                 AND TO_CHAR(SDATE, 'DY') NOT IN ('토','일')					\n");
		sql.append("\t             ) T3																	\n");
		sql.append("\t             WHERE T3.RNUM<11														\n");
		}else{
		sql.append("\t                 )																\n");
		sql.append("\t             ) T3																	\n");
		sql.append("\t             WHERE T3.RNUM<15														\n");
		}

		sql.append("\t         ) T4																		\n");
		sql.append("\t     )																			\n");
		sql.append("\t ORDER BY PAR_3_BOKG_RESM_DATE	\n");

		return sql.toString();
    }
}
