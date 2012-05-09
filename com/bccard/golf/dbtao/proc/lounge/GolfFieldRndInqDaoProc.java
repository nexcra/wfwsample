/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfFieldRndInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 전국골프장 안내 상세보기(라운딩정보)
*   적용범위  : golf
*   작성일자  : 2009-06-05
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lounge;

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
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfFieldRndInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfFieldRndInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfFieldRndInqDaoProc() {}	

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
			pstmt.setLong(++idx, data.getLong("GF_SEQ_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addString("RSV_DDD_NO", rs.getString("RSVT_DDD_NO") ); 		
					result.addString("RSV_TEL_HNO", rs.getString("RSVT_TEL_HNO") ); 			
					result.addString("RSV_TEL_SNO", rs.getString("RSVT_TEL_SNO") ); 
					result.addString("MB_DAY", rs.getString("CDHD_SVC_DAY_INFO") ); 
					result.addString("SLS_END_DAY", rs.getString("GREEN_RESM_DAY_INFO") );
					result.addString("CADDIE_SYS", rs.getString("CADY_SYS_INFO") );
					result.addString("CART_SYS", rs.getString("CART_SYS_INFO") );
					result.addString("MB_DAY_RSVT", rs.getString("CDHD_SVC_DAY_CDHD_RSVT_INFO") );
					result.addString("NMB_DAY_RSVT", rs.getString("CDHD_SVC_DAY_NONCDHD_RSVT_INFO") );
					result.addString("WKEND_MB_RSVT" ,rs.getString("WKE_CDHD_RSVT_INFO") );
					result.addString("WKEND_NMB_RSVT" ,rs.getString("WKE_NON_CDHD_RSVT_INFO") );
					result.addString("WK_MB_RSVT", rs.getString("WKD_CDHD_RSVT_INFO") );
					result.addString("WK_NMB_RSVT", rs.getString("WKD_NON_CDHD_RSVT_INFO") );
					result.addLong("GRNFEE_WK_MB_AMT", rs.getLong("GREEN_WKD_CDHD_CHRG") );
					result.addLong("GRNFEE_WK_NMB_AMT", rs.getLong("GREEN_WKD_NON_CDHD_CHRG") );
					result.addLong("GRNFEE_WK_WMB_AMT", rs.getLong("GREEN_WKD_WKD_CDHD_CHRG") );
					result.addLong("GRNFEE_WK_FMB_AMT", rs.getLong("GREEN_WKD_FMLY_CDHD_CHRG") );
					result.addLong("GRNFEE_WKEND_MB_AMT", rs.getLong("GREEN_WKE_CDHD_CHRG") );
					result.addLong("GRNFEE_WKEND_NMB_AMT", rs.getLong("GREEN_WKE_NON_CDHD_CHRG") );
					result.addLong("GRNFEE_WKEND_WMB_AMT", rs.getLong("GREEN_WKE_WKD_CDHD_CHRG") );
					result.addLong("GRNFEE_WKEND_FMB_AMT", rs.getLong("GREEN_WKE_FMLY_CDHD_CHRG") );
					result.addString("CADDIE_MB_AMT", rs.getString("CADY_CDHD_CHRG_INFO") );
					result.addString("CADDIE_NMB_AMT", rs.getString("CADY_NON_CDHD_CHRG_INFO") );
					result.addString("CADDIE_WMB_AMT", rs.getString("CADY_WKD_CDHD_CHRG_INFO") );
					result.addString("CADDIE_FMB_AMT", rs.getString("CADY_FMLY_CDHD_CHRG_INFO") );
					result.addString("CART_MB_AMT", rs.getString("CART_CDHD_CHRG_INFO") );
					result.addString("CART_NMB_AMT", rs.getString("CART_NON_CDHD_CHRG_INFO") );
					result.addString("CART_WMB_AMT", rs.getString("CART_WKD_CDHD_CHRG_INFO") );
					result.addString("CART_FMB_AMT", rs.getString("CART_FMLY_CDHD_CHRG_INFO") );
					
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
        
        sql.append("\n SELECT");
        sql.append("\t  	RSVT_DDD_NO, RSVT_TEL_HNO, RSVT_TEL_SNO, CDHD_SVC_DAY_INFO, GREEN_RESM_DAY_INFO, CADY_SYS_INFO, CART_SYS_INFO,   \n");
        sql.append("\n 	CDHD_SVC_DAY_CDHD_RSVT_INFO, CDHD_SVC_DAY_NONCDHD_RSVT_INFO, WKE_CDHD_RSVT_INFO, WKE_NON_CDHD_RSVT_INFO, WKD_CDHD_RSVT_INFO,WKD_NON_CDHD_RSVT_INFO,	");
		sql.append("\t	 	GREEN_WKD_CDHD_CHRG, GREEN_WKD_NON_CDHD_CHRG, GREEN_WKD_WKD_CDHD_CHRG, GREEN_WKD_FMLY_CDHD_CHRG, GREEN_WKE_CDHD_CHRG, GREEN_WKE_NON_CDHD_CHRG, GREEN_WKE_WKD_CDHD_CHRG, GREEN_WKE_FMLY_CDHD_CHRG,   \n");
		sql.append("\n 	DECODE(NVL (LENGTH (RTRIM (TRANSLATE (CADY_CDHD_CHRG_INFO, '0123456789', ' '))), 0),0,NVL (LTRIM (TO_CHAR (CADY_CDHD_CHRG_INFO, '999,999')),'-'),CADY_CDHD_CHRG_INFO) CADY_CDHD_CHRG_INFO,	");
		sql.append("\n 	DECODE(NVL (LENGTH (RTRIM (TRANSLATE (CADY_NON_CDHD_CHRG_INFO, '0123456789', ' '))), 0),0,NVL (LTRIM (TO_CHAR (CADY_NON_CDHD_CHRG_INFO, '999,999')),'-'),CADY_NON_CDHD_CHRG_INFO) CADY_NON_CDHD_CHRG_INFO,	");
		sql.append("\n 	DECODE(NVL (LENGTH (RTRIM (TRANSLATE (CADY_WKD_CDHD_CHRG_INFO, '0123456789', ' '))), 0),0,NVL (LTRIM (TO_CHAR (CADY_WKD_CDHD_CHRG_INFO, '999,999')),'-'),CADY_WKD_CDHD_CHRG_INFO) CADY_WKD_CDHD_CHRG_INFO,	");
		sql.append("\n 	DECODE(NVL (LENGTH (RTRIM (TRANSLATE (CADY_FMLY_CDHD_CHRG_INFO, '0123456789', ' '))), 0),0,NVL (LTRIM (TO_CHAR (CADY_FMLY_CDHD_CHRG_INFO, '999,999')),'-'),CADY_FMLY_CDHD_CHRG_INFO) CADY_FMLY_CDHD_CHRG_INFO,	");
		sql.append("\n 	DECODE(NVL (LENGTH (RTRIM (TRANSLATE (CART_CDHD_CHRG_INFO, '0123456789', ' '))), 0),0,NVL (LTRIM (TO_CHAR (CART_CDHD_CHRG_INFO, '999,999')),'-'),CART_CDHD_CHRG_INFO) CART_CDHD_CHRG_INFO,	");
		sql.append("\n 	DECODE(NVL (LENGTH (RTRIM (TRANSLATE (CART_NON_CDHD_CHRG_INFO, '0123456789', ' '))), 0),0,NVL (LTRIM (TO_CHAR (CART_NON_CDHD_CHRG_INFO, '999,999')),'-'),CART_NON_CDHD_CHRG_INFO) CART_NON_CDHD_CHRG_INFO,	");
		sql.append("\n 	DECODE(NVL (LENGTH (RTRIM (TRANSLATE (CART_WKD_CDHD_CHRG_INFO, '0123456789', ' '))), 0),0,NVL (LTRIM (TO_CHAR (CART_WKD_CDHD_CHRG_INFO, '999,999')),'-'),CART_WKD_CDHD_CHRG_INFO) CART_WKD_CDHD_CHRG_INFO,	");
		sql.append("\n 	DECODE(NVL (LENGTH (RTRIM (TRANSLATE (CART_FMLY_CDHD_CHRG_INFO, '0123456789', ' '))), 0),0,NVL (LTRIM (TO_CHAR (CART_FMLY_CDHD_CHRG_INFO, '999,999')),'-'),CART_FMLY_CDHD_CHRG_INFO) CART_FMLY_CDHD_CHRG_INFO	");
		sql.append("\n FROM BCDBA.TBGAFFIGREENROUNDINFO	");
		sql.append("\n WHERE AFFI_GREEN_SEQ_NO = AFFI_GREEN_SEQ_NO	");
		sql.append("\n AND AFFI_GREEN_SEQ_NO = ?	");	

		return sql.toString();
    }
}
