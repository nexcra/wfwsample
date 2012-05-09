/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardUpdFormDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 수정 폼
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

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
public class GolfClubBoardUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBoardUpdFormDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubBoardUpdFormDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("SCH_CLUB_SEQ_NO"));
			pstmt.setString(++idx, data.getString("SCH_BBRD_SEQ_NO"));
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					result.addLong("BBRD_SEQ_NO" 			,rs.getLong("BBRD_SEQ_NO") );
					result.addString("CLUB_BBRD_CLSS" 		,rs.getString("CLUB_BBRD_CLSS") );
					result.addString("BBRD_INFO" 			,rs.getString("BBRD_INFO") );
					
					result.addLong("SEQ_NO"					,rs.getLong("SEQ_NO") );
					result.addString("TITL" 				,rs.getString("TITL") );
					result.addLong("INQR_NUM"				,rs.getLong("INQR_NUM") );
					result.addString("URNK_EPS_YN"			,rs.getString("URNK_EPS_YN") );
					result.addString("ANNX_FILE_PATH"		,rs.getString("ANNX_FILE_PATH") );					
					result.addString("DEL_YN"				,rs.getString("DEL_YN") );					
					result.addString("REG_ATON"				,rs.getString("REG_ATON") );

					result.addString("CDHD_ID"				,rs.getString("CDHD_ID") );
					result.addString("CDHD_NM"				,rs.getString("CDHD_NM") );
					
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
	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT");
		sql.append("\n 	TCBM.BBRD_SEQ_NO, TCBM.CLUB_BBRD_CLSS, TCBM.BBRD_INFO,	");
		sql.append("\n 	TCB.SEQ_NO, TCB.TITL, TCB.CTNT, TCB.INQR_NUM, TCB.URNK_EPS_YN, TCB.ANNX_FILE_PATH, TCB.DEL_YN, TO_CHAR(TO_DATE(TCB.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, TCB.CHNG_ATON, 	");
		sql.append("\n 	TCM.CDHD_ID, TCM.CDHD_NM 	");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGCLUBBBRDMGMT TCBM, BCDBA.TBGCLUBBBRD TCB, BCDBA.TBGCLUBCDHDMGMT TCM	");
		sql.append("\n WHERE TCBM.BBRD_SEQ_NO = TCB.BBRD_UNIQ_SEQ_NO	");
		sql.append("\n AND TCB.CLUB_CDHD_SEQ_NO = TCM.CLUB_CDHD_SEQ_NO(+)	");
		sql.append("\n AND TCB.CLUB_CDHD_SEQ_NO = ?	");
		sql.append("\n AND TCB.BBRD_UNIQ_SEQ_NO = ?	");
		sql.append("\n AND TCB.SEQ_NO = ?	");

		return sql.toString();
    }
}
