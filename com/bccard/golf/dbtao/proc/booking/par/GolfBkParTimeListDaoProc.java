/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkParTimeListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 > 파3 > 티타임 리스트
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
public class GolfBkParTimeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmBkTimeRegFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBkParTimeListDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("AFFI_GREEN_SEQ_NO") );
			pstmt.setString(++idx, data.getString("AFFI_GREEN_SEQ_NO") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {
					
					result.addString("RNUM" 				,rs.getString("RNUM") );
					result.addString("SDATE"				,rs.getString("SDATE") );
					result.addString("BK_DATE"				,rs.getString("BK_DATE") );
					result.addString("RS_NUM"				,rs.getString("RS_NUM") );
					result.addString("MAX_ACPT_PNUM"		,rs.getString("MAX_ACPT_PNUM") );
					result.addString("ABLE_YN"				,rs.getString("ABLE_YN") );
					result.addString("IS_TODAY"				,rs.getString("IS_TODAY") );
					result.addString("IS_HOLY"				,rs.getString("IS_HOLY") );
										
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
	
		sql.append("\n	SELECT SDATE2, SDATE, RNUM,   RS_NUM, MAX_ACPT_PNUM, TO_CHAR(TO_DATE(SDATE), 'MM/DD') BK_DATE	\n");
		sql.append("\t	, CASE WHEN TO_CHAR(SYSDATE,'YYYYMMDD')=SDATE THEN 'Y' ELSE 'N' END IS_TODAY	\n");
		sql.append("\t	, CASE WHEN MAX_ACPT_PNUM>RS_NUM THEN 'Y' ELSE 'N' END AS ABLE_YN	\n");
		sql.append("\t	, NVL((SELECT RESM_YN FROM BCDBA.TBGRSVTABLESCDMGMT WHERE AFFI_GREEN_SEQ_NO=? AND PAR_3_BOKG_RESM_DATE=SDATE), 'N') AS IS_HOLY	\n");
			
		sql.append("\t	FROM (	\n");
		sql.append("\t	    SELECT TO_CHAR (BASE_DAY + LEVEL - 1, 'YYYYMMDD') SDATE, (SELECT MAX_ACPT_PNUM FROM BCDBA.TBGAFFIGREEN WHERE AFFI_GREEN_SEQ_NO=?) AS MAX_ACPT_PNUM	\n");
		sql.append("\t	    FROM (SELECT TO_DATE (TO_CHAR (SYSDATE - (TO_CHAR (SYSDATE, 'D') - 1), 'YYYYMMDD')) BASE_DAY FROM DUAL)	\n");
		sql.append("\t	    CONNECT BY BASE_DAY + LEVEL - 1 <= BASE_DAY + 27	\n");
		sql.append("\t	)B_TB LEFT JOIN (	\n");
		sql.append("\t	    SELECT RNUM, SDATE SDATE2, RS_NUM	\n");
		sql.append("\t	    FROM (	\n");
		sql.append("\t        SELECT RNUM, SDATE	\n");
		sql.append("\t	        , (SELECT COUNT(*) RS_NUM FROM BCDBA.TBGRSVTMGMT WHERE ROUND_HOPE_DATE=T4.SDATE AND AFFI_GREEN_SEQ_NO = ? AND RSVT_YN='Y') AS RS_NUM	\n");
		sql.append("\t	        FROM (	\n");
		sql.append("\t	          SELECT RNUM, TO_CHAR(SDATE, 'YYYYMMDD') SDATE	\n");
		sql.append("\t	            FROM (	\n");
		sql.append("\t	                SELECT ROWNUM RNUM, SDATE	\n");
		sql.append("\t	                FROM (	\n");
		sql.append("\t	                    SELECT SYSDATE+LEVEL+(SELECT BOKG_CNCL_ABLE_TRM-1 FROM BCDBA.TBGAFFIGREEN WHERE AFFI_GREEN_SEQ_NO=?) SDATE FROM DUAL	\n");
		sql.append("\t                    	CONNECT BY SYSDATE+LEVEL<=SYSDATE+19	\n");
		sql.append("\t	                    ) T1	\n");
		sql.append("\t	                WHERE TO_CHAR(SDATE,'YYYYMMDD') NOT IN	\n");
		sql.append("\t	                (SELECT PAR_3_BOKG_RESM_DATE FROM BCDBA.TBGRSVTABLESCDMGMT WHERE PAR_3_BOKG_RESM_DATE IS NOT NULL AND RESM_YN !='H' AND AFFI_GREEN_SEQ_NO=?)	\n");
		
	if(!co_nm.equals("Y")){
		sql.append("\t	                AND TO_CHAR(SDATE, 'DY') NOT IN ('토','일')	\n");
		sql.append("\t	            ) T3	\n");
		sql.append("\t            WHERE T3.RNUM<11	\n");
	}else{
		sql.append("\t	            ) T3	\n");
		sql.append("\t            WHERE T3.RNUM<15	\n");
	}
	
		sql.append("\t        ) T4	\n");
		sql.append("\t    ) T5	\n");
		sql.append("\t) P_TB ON B_TB.SDATE=P_TB.SDATE2	\n");
			
		return sql.toString();
    }
}
