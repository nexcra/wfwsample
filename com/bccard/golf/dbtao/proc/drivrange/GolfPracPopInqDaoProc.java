/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPracPopInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 드라이빙레인지/스크린 할인쿠폰(팝업)
*   적용범위  : golf
*   작성일자  : 2009-06-13
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.drivrange;

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
public class GolfPracPopInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfPracPopInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfPracPopInqDaoProc() {}	

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
		GolfUtil cstr = new GolfUtil();

		try {
			
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++idx, data.getLong("GF_SEQ_NO"));
			pstmt.setString(++idx, data.getString("CPN_SERIAL"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addLong("GF_SEQ_NO" 			,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("EXEC_TYPE_CD" 		,rs.getString("GOLF_RNG_CLSS") );
					result.addString("GF_NM" 			,rs.getString("GREEN_NM") );
					result.addString("ZIPCODE1" 			,rs.getString("ZP1") );
					result.addString("ZIPCODE2" 			,rs.getString("ZP2") );
					result.addString("ZIPADDR" 			,rs.getString("ADDR") );
					result.addString("DETAILADDR" 			,rs.getString("DTL_ADDR") );
					result.addString("CHG_DDD_NO"			,rs.getString("DDD_NO") );
					result.addString("CHG_TEL_HNO"			,rs.getString("TEL_HNO") );
					result.addString("CHG_TEL_SNO"			,rs.getString("TEL_SNO") );
					result.addString("IMG_NM"			,rs.getString("ANNX_IMG") );
					result.addString("MAP_NM"			,rs.getString("OLM_IMG") );
					result.addString("URL"			,rs.getString("GREEN_HPGE_URL") );
					result.addString("GF_SEARCH"			,rs.getString("POS_EXPL") );
					result.addString("MTTR"			,cstr.nl2br(rs.getString("CAUT_MTTR_CTNT")) );
					result.addLong("CUPN_SEQ_NO"			,rs.getLong("CUPN_SEQ_NO") );
					result.addString("REG_PE_ID"			,rs.getString("REG_MGR_ID") );
					result.addString("CORR_PE_ID"			,rs.getString("CHNG_MGR_ID") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON"));
					result.addString("CORR_ATON"		,rs.getString("CHNG_ATON"));
					result.addLong("CUPN_DC_RT"			,rs.getLong("DC_RT") );
					result.addString("HAN_NM"			,rs.getString("HG_NM"));
					result.addString("GOLF_CUPN_SERIAL"			,rs.getString("GOLF_RNG_CUPN_SEQ_NO"));
					
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
		sql.append("\n 	TGF.AFFI_GREEN_SEQ_NO, TGF.GOLF_RNG_CLSS, TGF.GREEN_NM, SUBSTR (TGF.ZP, 1, 3) ZP1, SUBSTR (TGF.ZP, 4, 6) ZP2, TGF.ADDR,  ");
		sql.append("\n 	TGF.DTL_ADDR, TGF.DDD_NO, TGF.TEL_HNO, TGF.TEL_SNO, TGF.ANNX_IMG, TGF.OLM_IMG, TGF.GREEN_HPGE_URL, TGF.POS_EXPL, TGF.CAUT_MTTR_CTNT,  ");
		sql.append("\n 	TGF.CUPN_SEQ_NO, TGF.REG_MGR_ID, TGF.CHNG_MGR_ID, TGF.REG_ATON, TGF.CHNG_ATON, TGC.DC_RT, TGU.HG_NM, TGCU.GOLF_RNG_CUPN_SEQ_NO	 ");
		sql.append("\n FROM BCDBA.TBGAFFIGREEN TGF, BCDBA.TBGCUPNMGMT TGC,  BCDBA.TBGCUPNUSEHST TGCU, BCDBA.TBGGOLFCDHD TGU 	");
		sql.append("\n WHERE TGF.CUPN_SEQ_NO = TGC.CUPN_SEQ_NO(+)	");
		sql.append("\n AND TGCU.CDHD_ID = TGU.CDHD_ID	");	
		sql.append("\n AND TGF.AFFI_GREEN_SEQ_NO = ?	");	
		sql.append("\n AND TGCU.GOLF_RNG_CUPN_SEQ_NO = ?	");	
		sql.append("\t 	AND TGF.AFFI_FIRM_CLSS = '0003'	");	

		return sql.toString();
    }
}
