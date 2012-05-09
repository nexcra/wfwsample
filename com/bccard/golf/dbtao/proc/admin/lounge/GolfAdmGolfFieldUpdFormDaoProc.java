/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGolfFieldUpdFormDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 골프장 수정
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lounge;

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
public class GolfAdmGolfFieldUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmGolfFieldUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmGolfFieldUpdFormDaoProc() {}	

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
					
					
					String addrClss = rs.getString("NW_OLD_ADDR_CLSS");
					
					if ( addrClss == null || addrClss.trim().equals("")){
						addrClss = "1";
					}
					
					
					result.addString("GF_NM", rs.getString("GREEN_NM") ); 			
					result.addString("CP_NM", rs.getString("CO_NM") ); 
					result.addString("GF_CLSS_CD", rs.getString("GREEN_CLSS") ); 
					result.addString("GF_HOLE_CD", rs.getString("GREEN_ODNO_CODE") );
					result.addString("GF_AREA_CD", rs.getString("GREEN_RGN_CLSS") );
					result.addString("URL", rs.getString("GREEN_HPGE_URL") );
					result.addString("OPEN_DATE", rs.getString("OMK_DATE") );
					result.addString("WEATH_CD", rs.getString("GREEN_WEATH_CLSS") );
					result.addString("ZIPCODE1" ,rs.getString("ZP1") );
					result.addString("ZIPCODE2" ,rs.getString("ZP2") );
					result.addString("ZIPADDR", rs.getString("ADDR") );
					result.addString("DETAILADDR", rs.getString("DTL_ADDR") ); 
					result.addString("ADDRCLSS"	,addrClss );
					result.addString("CHG_DDD_NO", rs.getString("DDD_NO") );
					result.addString("CHG_TEL_HNO", rs.getString("TEL_HNO") );
					result.addString("CHG_TEL_SNO", rs.getString("TEL_SNO") );
					result.addString("FX_DDD_NO", rs.getString("FAX_DDD_NO") );
					result.addString("FX_TEL_HNO", rs.getString("FAX_TEL_HNO") );
					result.addString("FX_TEL_SNO", rs.getString("FAX_TEL_SNO") );
					result.addString("IMG_NM", rs.getString("ANNX_IMG") );
					result.addString("MAP_NM", rs.getString("OLM_IMG") );
					result.addString("GF_SEARCH", rs.getString("POS_EXPL") );
					result.addString("SUBF", rs.getString("INCI_FACI_CTNT") );
					result.addString("TITL", rs.getString("TITL") );
					result.addString("CTNT", rs.getString("CTNT") );
					
					result.addString("RSV_DDD_NO", rs.getString("RSVT_DDD_NO") ); 			
					result.addString("RSV_TEL_HNO", rs.getString("RSVT_TEL_HNO") ); 
					result.addString("RSV_TEL_SNO", rs.getString("RSVT_TEL_SNO") ); 
					result.addString("MB_DAY", rs.getString("CDHD_SVC_DAY_INFO") );
					result.addString("SLS_END_DAY", rs.getString("GREEN_RESM_DAY_INFO") );
					result.addString("CADDIE_SYS", rs.getString("CADY_SYS_INFO") );
					result.addString("CART_SYS", rs.getString("CART_SYS_INFO") );
					result.addString("MB_DAY_RSVT", rs.getString("CDHD_SVC_DAY_CDHD_RSVT_INFO") );
					result.addString("NMB_DAY_RSVT", rs.getString("CDHD_SVC_DAY_NONCDHD_RSVT_INFO") );
					result.addString("WKEND_MB_RSVT", rs.getString("WKE_CDHD_RSVT_INFO") );
					result.addString("WKEND_NMB_RSVT", rs.getString("WKE_NON_CDHD_RSVT_INFO") );
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
        
        sql.append("\n SELECT");
        sql.append("\t  	TGF.AFFI_GREEN_SEQ_NO, TGF.GREEN_NM, TGF.CO_NM, TGF.GREEN_CLSS, TGF.GREEN_ODNO_CODE, TGF.GREEN_RGN_CLSS, TGF.GREEN_HPGE_URL,  \n");	
        sql.append("\t  	TO_CHAR (TO_DATE (TGF.OMK_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') OMK_DATE, TGF.GREEN_WEATH_CLSS, \n");	
		sql.append("\t	 	SUBSTR (TGF.ZP, 1, 3) ZP1, SUBSTR (TGF.ZP, 4, 6) ZP2,  \n");
		sql.append("\t	 	TGF.ADDR, TGF.DTL_ADDR, TGF.NW_OLD_ADDR_CLSS, TGF.DDD_NO, TGF.TEL_HNO, TGF.TEL_SNO, TGF.FAX_DDD_NO, TGF.FAX_TEL_HNO, TGF.FAX_TEL_SNO, TGF.ANNX_IMG,  	\n");
		sql.append("\t	 	TGF.OLM_IMG, TGF.POS_EXPL, TGF.INCI_FACI_CTNT, TGF.TITL, TGF.CTNT,  	\n");
		sql.append("\t  	TGFR.RSVT_DDD_NO, TGFR.RSVT_TEL_HNO, TGFR.RSVT_TEL_SNO, TGFR.CDHD_SVC_DAY_INFO, TGFR.GREEN_RESM_DAY_INFO, TGFR.CADY_SYS_INFO, TGFR.CART_SYS_INFO, TGFR.CDHD_SVC_DAY_CDHD_RSVT_INFO, TGFR.CDHD_SVC_DAY_NONCDHD_RSVT_INFO,   	\n");
		sql.append("\t 	 	TGFR.WKE_CDHD_RSVT_INFO, TGFR.WKE_NON_CDHD_RSVT_INFO, TGFR.WKD_CDHD_RSVT_INFO, TGFR.WKD_NON_CDHD_RSVT_INFO, TGFR.GREEN_WKD_CDHD_CHRG, TGFR.GREEN_WKD_NON_CDHD_CHRG, TGFR.GREEN_WKD_WKD_CDHD_CHRG, TGFR.GREEN_WKD_FMLY_CDHD_CHRG, TGFR.GREEN_WKE_CDHD_CHRG, TGFR.GREEN_WKE_NON_CDHD_CHRG, 	\n");	
		sql.append("\t  	TGFR.GREEN_WKE_WKD_CDHD_CHRG, TGFR.GREEN_WKE_FMLY_CDHD_CHRG, TGFR.CADY_CDHD_CHRG_INFO, TGFR.CADY_NON_CDHD_CHRG_INFO, TGFR.CADY_WKD_CDHD_CHRG_INFO, TGFR.CADY_FMLY_CDHD_CHRG_INFO, TGFR.CART_CDHD_CHRG_INFO, TGFR.CART_NON_CDHD_CHRG_INFO, TGFR.CART_WKD_CDHD_CHRG_INFO, TGFR.CART_FMLY_CDHD_CHRG_INFO	\n");
		sql.append("\n FROM BCDBA.TBGAFFIGREEN TGF, BCDBA.TBGAFFIGREENROUNDINFO TGFR	");
		sql.append("\n WHERE TGF.AFFI_GREEN_SEQ_NO = TGFR.AFFI_GREEN_SEQ_NO(+)	");
		sql.append("\n AND TGF.AFFI_GREEN_SEQ_NO = ?	");	
		sql.append("\n AND TGF.AFFI_FIRM_CLSS = '0004'	");
		return sql.toString();
    }
}
