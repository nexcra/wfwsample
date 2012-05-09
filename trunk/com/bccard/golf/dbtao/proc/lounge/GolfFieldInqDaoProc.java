/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfFieldInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 전국골프장 안내 상세보기
*   적용범위  : golf
*   작성일자  : 2009-06-04
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
public class GolfFieldInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfFieldInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfFieldInqDaoProc() {}	

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
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addLong("GF_SEQ_NO", rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("GF_NM", rs.getString("GREEN_NM") ); 		
					result.addString("CP_NM", rs.getString("CO_NM") ); 			
					result.addString("ZIPADDR", rs.getString("ADDR") ); 
					result.addString("DETAILADDR", rs.getString("DTL_ADDR") ); 
					result.addString("OPEN_DATE", rs.getString("OMK_DATE") );
					result.addString("WEATH_CD", rs.getString("GREEN_WEATH_CLSS") );
					result.addString("GF_CLSS_CD", rs.getString("GREEN_CLSS") );
					result.addString("GF_HOLE_CD", rs.getString("GREEN_ODNO_CODE") );
					result.addString("SUBF", rs.getString("INCI_FACI_CTNT") );
					result.addString("CHG_DDD_NO", rs.getString("DDD_NO") );
					result.addString("CHG_TEL_HNO" ,rs.getString("TEL_HNO") );
					result.addString("CHG_TEL_SNO" ,rs.getString("TEL_SNO") );
					result.addString("URL", rs.getString("GREEN_HPGE_URL") );
					result.addString("FX_DDD_NO", rs.getString("FAX_DDD_NO") );
					result.addString("FX_TEL_HNO", rs.getString("FAX_TEL_HNO") );
					result.addString("FX_TEL_SNO", rs.getString("FAX_TEL_SNO") );
					result.addString("IMG_NM", rs.getString("ANNX_IMG") );
					result.addString("MAP_NM", rs.getString("OLM_IMG") );
					result.addString("TITL", rs.getString("TITL") );
					result.addString("CTNT", cstr.nl2br(rs.getString("CTNT")) );
					
					/*
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("CTNT", bufferSt.toString());
					*/
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
        sql.append("\t  	TGF.AFFI_GREEN_SEQ_NO, TGF.GREEN_NM, TGF.CO_NM, TGF.ADDR, TGF.DTL_ADDR,  \n");
        sql.append("\t  	TO_CHAR (TO_DATE (TGF.OMK_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') OMK_DATE, GREEN_WEATH_CLSS, \n");
		sql.append("\t	 	(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0019' AND USE_YN = 'Y' AND GOLF_CMMN_CODE = TGF.GREEN_CLSS) GREEN_CLSS,  \n");
		sql.append("\t	 	(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0020' AND USE_YN = 'Y' AND GOLF_CMMN_CODE = TGF.GREEN_ODNO_CODE) GREEN_ODNO_CODE,  	\n");
		sql.append("\t	 	TGF.INCI_FACI_CTNT, TGF.DDD_NO, TGF.TEL_HNO, TGF.TEL_SNO, TGF.GREEN_HPGE_URL, TGF.FAX_DDD_NO, TGF.FAX_TEL_HNO, TGF.FAX_TEL_SNO, TGF.ANNX_IMG, TGF.OLM_IMG, TGF.TITL, TGF.CTNT	\n");
		sql.append("\n FROM BCDBA.TBGAFFIGREEN TGF	");
		sql.append("\n WHERE AFFI_GREEN_SEQ_NO = AFFI_GREEN_SEQ_NO	");
		sql.append("\n AND TGF.AFFI_GREEN_SEQ_NO = ?	");	
		sql.append("\n AND TGF.AFFI_FIRM_CLSS = '0004'	");	
		
    	return sql.toString();
    }
}
