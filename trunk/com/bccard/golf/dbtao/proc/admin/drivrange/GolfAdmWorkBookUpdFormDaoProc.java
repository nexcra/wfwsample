/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmWorkBookUpdFormDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 연습장 수정
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.drivrange;

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
public class GolfAdmWorkBookUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmWorkBookUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmWorkBookUpdFormDaoProc() {}	

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
					
					result.addLong("GF_SEQ_NO" 			,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("EXEC_TYPE_CD" 	,rs.getString("GOLF_RNG_CLSS") );
					result.addString("GF_NM" 			,rs.getString("GREEN_NM") );
					result.addString("ZIPCODE1" 		,rs.getString("ZP1") );
					result.addString("ZIPCODE2" 		,rs.getString("ZP2") );
					result.addString("ZIPADDR" 			,rs.getString("ADDR") );
					result.addString("DETAILADDR" 		,rs.getString("DTL_ADDR") );
					result.addString("ADDRCLSS" 		,addrClss );
					result.addString("CHG_DDD_NO"		,rs.getString("DDD_NO") );
					result.addString("CHG_TEL_HNO"		,rs.getString("TEL_HNO") );
					result.addString("CHG_TEL_SNO"		,rs.getString("TEL_SNO") );
					result.addString("IMG_NM"			,rs.getString("ANNX_IMG") );
					result.addString("MAP_NM"			,rs.getString("OLM_IMG") );
					result.addString("URL"				,rs.getString("GREEN_HPGE_URL") );
					result.addString("GF_SEARCH"		,rs.getString("POS_EXPL") );
					result.addString("MTTR"				,rs.getString("CAUT_MTTR_CTNT") );
					result.addLong("CUPN_SEQ_NO"		,rs.getLong("CUPN_SEQ_NO") );
					result.addString("REG_PE_ID"		,rs.getString("REG_MGR_ID") );
					result.addString("CORR_PE_ID"		,rs.getString("CHNG_MGR_ID") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON"));
					result.addString("CORR_ATON"		,rs.getString("CHNG_ATON"));
					result.addLong("CUPN_DC_RT"			,rs.getLong("DC_RT") );
					
					/*
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CAUT_MTTR_CTNT");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("MTTR", bufferSt.toString());
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
		sql.append("\n 	TGF.AFFI_GREEN_SEQ_NO, TGF.GOLF_RNG_CLSS, TGF.GREEN_NM, SUBSTR (TGF.ZP, 1, 3) ZP1, SUBSTR (TGF.ZP, 4, 6) ZP2, TGF.ADDR,  ");
		sql.append("\n 	TGF.DTL_ADDR, TGF.NW_OLD_ADDR_CLSS, TGF.DDD_NO, TGF.TEL_HNO, TGF.TEL_SNO, TGF.ANNX_IMG, TGF.OLM_IMG, TGF.GREEN_HPGE_URL, TGF.POS_EXPL, TGF.CAUT_MTTR_CTNT,  ");
		sql.append("\n 	TGF.CUPN_SEQ_NO, TGF.REG_MGR_ID, TGF.CHNG_MGR_ID, TGF.REG_ATON, TGF.CHNG_ATON, TGC.DC_RT	 ");
		sql.append("\n FROM BCDBA.TBGAFFIGREEN TGF, BCDBA.TBGCUPNMGMT TGC 	");
		sql.append("\n WHERE TGF.CUPN_SEQ_NO = TGC.CUPN_SEQ_NO(+)	");
		sql.append("\n AND TGF.AFFI_GREEN_SEQ_NO = ?	");	
		sql.append("\t 	AND TGF.AFFI_FIRM_CLSS = '0003'	");	
		return sql.toString();
    }
}
