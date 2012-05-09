/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeResultDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 신청 내용 확인 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	미디어포스 
 * @version	1.0
 ******************************************************************************/
public class GolfBkPreTimeResultDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPreTimeResultDaoProc 부킹 신청 내용 확인 처리  
	 * @param N/A
	 ***************************************************************** */
	public GolfBkPreTimeResultDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("TIME_SEQ_NO" 		,rs.getString("TIME_SEQ_NO") );
					result.addString("GR_SEQ_NO" 		,rs.getString("GR_SEQ_NO") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("RL_GREEN_NM" 		,rs.getString("RL_GREEN_NM") );
					result.addString("BKPS_DATE" 		,rs.getString("BKPS_DATE") );
					result.addString("BKPS_YOIL" 		,rs.getString("BKPS_YOIL") );
					result.addString("BKPS_TIME" 		,rs.getString("BKPS_TIME") );
					result.addInt("BKPS_YOIL_NUM" 		,rs.getInt("BKPS_YOIL_NUM") );
					result.addString("BKPS_DATE_ALERT" 	,rs.getString("BKPS_DATE_ALERT") );
					result.addString("BKPS_TIME_ALERT" 	,rs.getString("BKPS_TIME_ALERT") );
					result.addString("BKPS_MINUTE" 		,rs.getString("BKPS_MINUTE") );
					result.addString("COURSE" 			,rs.getString("COURSE") );		
					//result.addString("GOLF_SVC_RSVT" 	,rs.getString("GOLF_SVC_RSVT") );		
					//result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") );		
					//result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") );		
					//result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") );		
					result.addString("CANCLE_DATE" 		,rs.getString("CANCLE_DATE") );			
					result.addString("BK_DATE" 			,rs.getString("BK_DATE") );
					result.addString("BK_DATE_REAL" 	,rs.getString("BK_DATE_REAL") );
					
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
        
		sql.append("\n SELECT																										\n");
		sql.append("\t  T3.GREEN_NM AS GR_NM, T3.RL_GREEN_NM																		\n");
		sql.append("\t  , T3.AFFI_GREEN_SEQ_NO AS GR_SEQ_NO 																		\n");
		sql.append("\t  , T1.RSVT_ABLE_BOKG_TIME_SEQ_NO AS TIME_SEQ_NO 																\n");
		sql.append("\t  , TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'yyyy.mm.dd') AS BKPS_DATE 											\n");
		sql.append("\t  , TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'dy') AS BKPS_YOIL 													\n");
		sql.append("\t  , TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'d') AS BKPS_YOIL_NUM 													\n");
		sql.append("\t  , TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'yy.mm.dd') AS BKPS_DATE_ALERT 										\n");
		sql.append("\t  , (CASE WHEN TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'AM')='오전' THEN 'AM' ELSE 'PM' END) AS BKPS_TIME_ALERT 	\n");
		sql.append("\t  , SUBSTR(T1.BOKG_ABLE_TIME,0,2) AS BKPS_TIME 																\n");
		sql.append("\t  , SUBSTR(T1.BOKG_ABLE_TIME,3,4) AS BKPS_MINUTE 																\n");
		sql.append("\t  , T2.GOLF_RSVT_CURS_NM AS COURSE 																			\n");
		//sql.append("\t  , T4.GOLF_SVC_RSVT_NO AS GOLF_SVC_RSVT 																		\n");
		//sql.append("\t  , T4.HP_DDD_NO AS HP_DDD_NO 																				\n");
		//sql.append("\t  , T4.HP_TEL_HNO AS HP_TEL_HNO 																				\n");
		//sql.append("\t  , T4.HP_TEL_SNO AS HP_TEL_SNO 																				\n");
		sql.append("\t  , TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'MM/DD(DY)') AS BK_DATE												\n");
		sql.append("\t  , TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'YYYYMMDD') AS BK_DATE_REAL												\n");
		sql.append("\t  , TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE)-T3.BOKG_TIME_COLL_TRM,'MM/DD(DY) ')									\n");
		sql.append("\t  ||SUBSTR(T1.BOKG_ABLE_TIME,0,2)||':'||SUBSTR(T1.BOKG_ABLE_TIME,3,4) AS CANCLE_DATE							\n");
		sql.append("\t  FROM 																										\n");
		sql.append("\t  BCDBA.TBGRSVTABLEBOKGTIMEMGMT T1 																			\n");
		sql.append("\t  LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO 					\n");
		sql.append("\t  LEFT JOIN BCDBA.TBGAFFIGREEN T3 ON T1.AFFI_GREEN_SEQ_NO=T3.AFFI_GREEN_SEQ_NO 								\n");
		//sql.append("\t  LEFT JOIN BCDBA.TBGRSVTMGMT T4 ON T4.RSVT_ABLE_BOKG_TIME_SEQ_NO=T1.RSVT_ABLE_BOKG_TIME_SEQ_NO				\n");
		//2009.10.19 수정  예약취소 제외
		//sql.append("\t  LEFT JOIN BCDBA.TBGRSVTMGMT T4 ON T4.RSVT_ABLE_BOKG_TIME_SEQ_NO=T1.RSVT_ABLE_BOKG_TIME_SEQ_NO AND T4.RSVT_YN = 'Y'			\n");
	
		sql.append("\t  WHERE T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=? 																		\n");
		
		return sql.toString(); 
    }
}
