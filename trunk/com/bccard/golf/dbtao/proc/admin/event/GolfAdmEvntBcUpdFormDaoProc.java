/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmEvntBcUpdFormDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 BC Golf 이벤트 수정 폼
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event;

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
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfAdmEvntBcUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmEvntBcUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBcUpdFormDaoProc() {}	

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

					result.addLong("SEQ_NO" 		,rs.getLong("EVNT_SEQ_NO") );		
					result.addString("EVNT_CLSS" 	,rs.getString("EVNT_CLSS") );
					result.addString("EVNT_NM" 		,rs.getString("EVNT_NM") );
					result.addString("EVNT_FROM" 	,rs.getString("EVNT_STRT_DATE") );
					result.addString("EVNT_TO" 		,rs.getString("EVNT_END_DATE") );					
					result.addString("TITL" 		,rs.getString("TITL") );
					result.addString("DISP_YN"		,rs.getString("BLTN_YN"));
					result.addString("IMG_NM"		,rs.getString("IMG_FILE_PATH"));
					result.addString("BLTN_STRT_DATE"		,rs.getString("BLTN_STRT_DATE"));
					result.addString("BLTN_END_DATE"		,rs.getString("BLTN_END_DATE"));

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
    **************************************************************************/
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT");
		sql.append("\n 	EVNT_SEQ_NO, EVNT_CLSS, EVNT_NM,");
		sql.append("\n 	TO_CHAR(TO_DATE(EVNT_STRT_DATE,'YYYYMMDD'),'YYYY-MM-DD') EVNT_STRT_DATE, TO_CHAR(TO_DATE(EVNT_END_DATE,'YYYYMMDD'),'YYYY-MM-DD') EVNT_END_DATE, ");
		sql.append("\n 	TO_CHAR(TO_DATE(BLTN_STRT_DATE,'YYYYMMDD'),'YYYY-MM-DD') BLTN_STRT_DATE, TO_CHAR(TO_DATE(BLTN_END_DATE,'YYYYMMDD'),'YYYY-MM-DD') BLTN_END_DATE, ");
		sql.append("\n 	TITL, CTNT, BLTN_YN, IMG_FILE_PATH		");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGEVNTMGMT");
		sql.append("\n WHERE EVNT_SEQ_NO = ?	");	
		sql.append("\n AND EVNT_CLSS = '0001'	");		

		return sql.toString();
    }
}
