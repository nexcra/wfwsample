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

package com.bccard.golf.dbtao.proc.booking.sky;

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
public class GolfBkSkyTimeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmBkTimeRegFormDaoProc 프로세스 생성자  
	 * @param N/A
	 ***************************************************************** */
	public GolfBkSkyTimeListDaoProc() {}	

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
			int nYoil = data.getInt("nYoil");
			String click_DATE = data.getString("CLICK_DATE");
			 
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery(nYoil, click_DATE);
			pstmt = conn.prepareStatement(sql.toString());	
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("SDATE" 				,rs.getString("SDATE") );
					result.addString("LDATE" 				,rs.getString("LDATE") );
					result.addString("ABLE7"				,rs.getString("ABLE7") );
					result.addString("DONE7"				,rs.getString("DONE7") );
					result.addString("ABLE14"				,rs.getString("ABLE14") );
					result.addString("DONE14"				,rs.getString("DONE14") );
					result.addString("YN7"					,rs.getString("YN7") );
					result.addString("YN14"					,rs.getString("YN14") );
					result.addString("TODAY_YN"				,rs.getString("TODAY_YN") );
					result.addString("RES7"					,rs.getString("RES7") );
					result.addString("RES14"				,rs.getString("RES14") );
					result.addString("REG7"					,rs.getString("REG7") );
					result.addString("REG14"				,rs.getString("REG14") );
					result.addString("LDATE_YOIL"			,rs.getString("LDATE_YOIL") );
					result.addString("CLICK_YN"				,rs.getString("CLICK_YN") );
										
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
    private String getSelectQuery(int nYoil, String click_DATE){
        StringBuffer sql = new StringBuffer();

		sql.append("\n");	
		sql.append("\t	SELECT BASEDATE AS SDATE, TO_CHAR(TO_DATE(BASEDATE),'MM/DD') AS LDATE, ABLE7, DONE7, ABLE14, DONE14, TODAY_YN, CLICK_YN	\n");	
		sql.append("\t	, TO_CHAR(TO_DATE(BASEDATE),'DY') AS LDATE_YOIL																			\n");	
		sql.append("\t	, CASE WHEN BASEDATE<TO_CHAR(SYSDATE+7,'YYYYMMDD') THEN 'N' ELSE (CASE WHEN ABLE7>DONE7 THEN 'Y' ELSE 'N' END) END YN7	\n");	
		sql.append("\t	, CASE WHEN BASEDATE<TO_CHAR(SYSDATE+7,'YYYYMMDD') THEN 'N' ELSE (CASE WHEN ABLE14>DONE14 THEN 'Y' ELSE 'N' END) END YN14	\n");	
		sql.append("\t	, (ABLE7-DONE7) AS RES7, (ABLE14-DONE14) AS RES14																		\n");	
		sql.append("\t	, (CASE WHEN ABLE7>0 THEN 'Y' ELSE 'N' END) REG7																		\n");	
		sql.append("\t	, (CASE WHEN ABLE14>0 THEN 'Y' ELSE 'N' END) REG14																		\n");	
		sql.append("\t	        FROM (																											\n");	
		sql.append("\t	          SELECT BASEDATE																								\n");	
		sql.append("\t	          , (CASE WHEN TO_CHAR(SYSDATE,'YYYYMMDD')=T3.BASEDATE THEN 'Y' ELSE 'N' END) TODAY_YN							\n");
		sql.append("\t	          , (CASE BASEDATE WHEN '"+click_DATE+"' THEN 'Y' ELSE 'N' END) CLICK_YN										\n");
		sql.append("\t	            , (SELECT COUNT(*) FROM BCDBA.TBGRSVTABLESCDMGMT T1															\n");	
		sql.append("\t	            JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO					\n");	
		sql.append("\t	            WHERE T1.BOKG_ABLE_DATE=T3.BASEDATE AND T1.SKY72_HOLE_CODE='0001') ABLE7									\n");	
		sql.append("\t	            , (SELECT COUNT(*) FROM BCDBA.TBGRSVTABLESCDMGMT T1															\n");	
		sql.append("\t	            JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO					\n");	
		sql.append("\t	            WHERE T1.BOKG_ABLE_DATE=T3.BASEDATE AND T1.SKY72_HOLE_CODE='0001'											\n");	
		sql.append("\t	            AND T2.BOKG_RSVT_STAT_CLSS<>'0001') DONE7																	\n");	
		sql.append("\t	            , (SELECT COUNT(*) FROM BCDBA.TBGRSVTABLESCDMGMT T1															\n");	
		sql.append("\t	            JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO					\n");	
		sql.append("\t	            WHERE T1.BOKG_ABLE_DATE=T3.BASEDATE AND T1.SKY72_HOLE_CODE='0002') ABLE14									\n");	
		sql.append("\t	            , (SELECT COUNT(*) FROM BCDBA.TBGRSVTABLESCDMGMT T1															\n");	
		sql.append("\t	            JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO					\n");	
		sql.append("\t	            WHERE T1.BOKG_ABLE_DATE=T3.BASEDATE AND T1.SKY72_HOLE_CODE='0002'											\n");	
		sql.append("\t	            AND T2.BOKG_RSVT_STAT_CLSS<>'0001') DONE14																	\n");	
		sql.append("\t	            FROM (																										\n");	
		sql.append("\t	                SELECT TO_CHAR(SYSDATE+LEVEL-"+nYoil+",'YYYYMMDD') BASEDATE												\n");	
		sql.append("\t	                        FROM DUAL																						\n");	
		sql.append("\t	                        CONNECT BY SYSDATE+LEVEL-1<=SYSDATE+27															\n");	
		sql.append("\t	                    ) T3																								\n");	
		sql.append("\t	               )																										\n");	
        	
		return sql.toString();
    }
}
