/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtFieldListDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 마이티박스(골프장정보)
*   적용범위  : golf
*   작성일자  : 2009-06-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.mytbox.golf;

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
 * Topn
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfMtFieldListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfMtFieldListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMtFieldListDaoProc() {}	

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
			 
			//조회 ----------------------------------------------------------
			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			pstmt.setString(++idx, data.getString("ADMIN_NO"));
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("GF_SEQ_NO" 			,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("IMG_NM" 		,rs.getString("ANNX_IMG") );
					result.addString("GF_NM" 			,rs.getString("GREEN_NM") );
					result.addString("ZIPADDR" 			,rs.getString("ADDR") );
					result.addString("DETAILADDR"			,rs.getString("DTL_ADDR") );
					result.addString("GF_CLSS_CD"			,rs.getString("GREEN_CLSS") );
					result.addString("GF_HOLE_CD"			,rs.getString("GREEN_ODNO_CODE") );
										
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
										
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
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_GREEN_SEQ_NO, ANNX_IMG, GREEN_NM, ADDR, DTL_ADDR, GREEN_CLSS, GREEN_ODNO_CODE,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			TGF.AFFI_GREEN_SEQ_NO, TGF.ANNX_IMG, TGF.GREEN_NM, TGF.ADDR, TGF.DTL_ADDR, TMC1.GOLF_CMMN_CODE_NM GREEN_CLSS, TMC2.GOLF_CMMN_CODE_NM GREEN_ODNO_CODE   	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIGREEN TGF, BCDBA.TBGAFFIGREENROUNDINFO TGFR, BCDBA.TBGSCRAP TGS,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0019' AND USE_YN = 'Y') TMC1,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0020' AND USE_YN = 'Y') TMC2,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0021' AND USE_YN = 'Y') TMC3		");
		sql.append("\n 				WHERE TGF.AFFI_GREEN_SEQ_NO = TGFR.AFFI_GREEN_SEQ_NO	");
		sql.append("\n 				AND TGF.GREEN_CLSS = TMC1.GOLF_CMMN_CODE(+)	");
		sql.append("\n 				AND TGF.GREEN_ODNO_CODE = TMC2.GOLF_CMMN_CODE(+)	");
		sql.append("\n 				AND TGF.GREEN_RGN_CLSS = TMC3.GOLF_CMMN_CODE(+)	");
		sql.append("\t 					AND TGF.AFFI_FIRM_CLSS = '0004'	");	
		sql.append("\t 					AND TGF.AFFI_GREEN_SEQ_NO = TGS.BOD_SEQ_NO	");	
		sql.append("\t 					AND TGS.CDHD_ID = ?	");	
		sql.append("\t 					AND TGS.GOLF_SCRAP_CLSS = '0001'	");	
		
		sql.append("\n 				ORDER BY TGF.AFFI_GREEN_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");	
		
    	return sql.toString();
    }
}
