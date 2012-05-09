/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmWorkBookInqDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 연습장 상세보기
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
public class GolfAdmWorkBookInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmWorkBookInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmWorkBookInqDaoProc() {}	

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
					
					result.addLong("GF_SEQ_NO" 			,rs.getLong("GF_SEQ_NO") );
					
					result.addString("EXEC_TYPE_CD" 		,rs.getString("EXEC_TYPE_CD") );
					result.addString("WB_NM" 			,rs.getString("WB_NM") );
					result.addString("ZIPCODE" 			,rs.getString("ZIPCODE") );
					result.addString("ZIPADDR" 			,rs.getString("ZIPADDR") );
					result.addString("DETAILADDR" 			,rs.getString("DETAILADDR") );
					result.addString("CHG_DDD_NO"			,rs.getString("CHG_DDD_NO") );
					result.addString("CHG_TEL_HNO"			,rs.getString("CHG_TEL_HNO") );
					result.addString("CHG_TEL_SNO"			,rs.getString("CHG_TEL_SNO") );
					result.addString("IMG_NM"			,rs.getString("IMG_NM") );
					result.addString("MAP_NM"			,rs.getString("MAP_NM") );
					result.addString("URL"			,rs.getString("URL") );
					result.addString("MAP"			,rs.getString("MAP") );
					
					result.addLong("CPN_SEQ_NO"			,rs.getLong("CPN_SEQ_NO") );
					result.addLong("REG_MGR_SEQ_NO"			,rs.getLong("REG_MGR_SEQ_NO") );
					result.addLong("CORR_MGR_SEQ_NO"			,rs.getLong("CORR_MGR_SEQ_NO") );
					
					String reg_date = rs.getString("REG_DATE");
					if (!GolfUtil.isNull(reg_date)) reg_date = DateUtil.format(reg_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("REG_DATE"			,reg_date);
					
					String corr_date = rs.getString("CORR_DATE");
					if (!GolfUtil.isNull(corr_date)) corr_date = DateUtil.format(corr_date, "yyyyMMdd", "yyyy년 MM월 dd일");
					result.addString("CORR_DATE"		,corr_date);
					
					result.addLong("DISCOUNT_RATE"			,rs.getLong("DISCOUNT_RATE") );
					
									
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CARE_MTTR");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("CARE_MTTR", bufferSt.toString());
					
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
		sql.append("\n 	TGW.GF_SEQ_NO, DECODE (TGW.EXEC_TYPE_CD, '0001', '드라이빙레인지', '0002', '스크린골프 연습장') EXEC_TYPE_CD, TGW.WB_NM, TGW.ZIPCODE, TGW.ZIPADDR,  ");
		sql.append("\n 	TGW.DETAILADDR, TGW.CHG_DDD_NO, TGW.CHG_TEL_HNO, TGW.CHG_TEL_SNO, TGW.IMG_NM, TGW.MAP_NM, TGW.URL, TGW.MAP, TGW.CARE_MTTR,  ");
		sql.append("\n 	TGW.CPN_SEQ_NO, TGW.REG_MGR_SEQ_NO, TGW.CORR_MGR_SEQ_NO, TGW.REG_DATE, TGW.CORR_DATE, TGC.DISCOUNT_RATE	 ");
		sql.append("\n FROM BCDBA.TBGFWB TGW, BCDBA.TBGFCPN TGC 	");
		sql.append("\n WHERE TGW.CPN_SEQ_NO = TGC.CPN_SEQ_NO(+)	");
		sql.append("\n AND TGW.GF_SEQ_NO = ?	");		

		return sql.toString();
    }
}
