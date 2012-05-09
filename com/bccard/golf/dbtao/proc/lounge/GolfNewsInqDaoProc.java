/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfNewsInqDaoProc
*   작성자    : (주)미디어포스 조은미
*   내용      : 뉴스 상세보기
*   적용범위  : golf
*   작성일자  : 2009-09-17
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항 
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lounge;

import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
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
public class GolfNewsInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBoardInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfNewsInqDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("CTET_ID"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					result.addString("TITL" 				,rs.getString("TITL") );
					result.addString("NEWS_RECP_DATE"		,rs.getString("NEWS_RECP_DATE") );

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
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/**
	 * 게시물 싱세보기에서 Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public HashMap execute(WaContext context, HashMap resultMap, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("CTET_ID"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					resultMap.put("TITL" 				,rs.getString("TITL") );
					resultMap.put("NEWS_RECP_DATE"					,rs.getString("NEWS_RECP_DATE") );

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
					resultMap.put("CTNT", bufferSt.toString());
					
					resultMap.put("RESULT", "00"); //정상결과
				}
			}

			if(resultMap.size() < 1) {
				resultMap.put("RESULT", "01");			
			}

			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return resultMap;
	}	
    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n  SELECT");
		sql.append("\n 	CTET_ID, TITL, CTNT, TO_CHAR(TO_DATE(NEWS_RECP_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') AS NEWS_RECP_DATE 	");
		sql.append("\n  FROM");
		sql.append("\n  BCDBA.TBGGOLFNEWS ");
		sql.append("\n  WHERE CTET_ID = ?	");

		return sql.toString();
    }

}
