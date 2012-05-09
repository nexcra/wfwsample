/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 수정 폼
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

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
public class GolfBoardInqDaoProc extends AbstractProc {
	
	/** ****************************************************************
	 * GolfBoardInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBoardInqDaoProc() {}	

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
					result.addString("RE_CNT"				,rs.getString("RE_CNT") );

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
			pstmt.setString(++idx, data.getString("SCH_CLUB_SEQ_NO"));
			pstmt.setString(++idx, data.getString("SCH_BBRD_SEQ_NO"));
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					resultMap.put("BBRD_SEQ_NO" 		,rs.getString("BBRD_SEQ_NO") );
					resultMap.put("CLUB_BBRD_CLSS" 		,rs.getString("CLUB_BBRD_CLSS") );
					resultMap.put("BBRD_INFO" 			,rs.getString("BBRD_INFO") );
					resultMap.put("SEQ_NO"				,rs.getString("SEQ_NO") );
					resultMap.put("TITL" 				,rs.getString("TITL") );
					resultMap.put("INQR_NUM"			,rs.getString("INQR_NUM") );
					resultMap.put("URNK_EPS_YN"			,rs.getString("URNK_EPS_YN") );
					resultMap.put("ANNX_FILE_PATH"		,rs.getString("ANNX_FILE_PATH") );					
					resultMap.put("DEL_YN"				,rs.getString("DEL_YN") );					
					resultMap.put("REG_ATON"			,rs.getString("REG_ATON") );	
					
					resultMap.put("CDHD_ID"				,rs.getString("CDHD_ID") );
					resultMap.put("CDHD_NM"				,rs.getString("CDHD_NM") );
					resultMap.put("RE_CNT"				,rs.getString("RE_CNT") );				

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
	
	/**
	 * 조회수 업데이트 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int readCntUpd(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            
			sql = this.getUpdateQuery();//Update Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("SEQ_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
			
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			

		} finally {
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}			

		return result;
	}
	
	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getPreNextInfo(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		//String result = null;

		try {
			conn = context.getDbConnection("default", null);
			
			//조회 ----------------------------------------------------------
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");	

			String sql = this.getSelectQuery(search_sel, search_word);   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("SCH_CLUB_SEQ_NO"));
			pstmt.setString(++idx, data.getString("SCH_BBRD_SEQ_NO"));
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");		
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {	
					result.addString("PRE_SEQ_NO" 		,rs.getString("PRE_SEQ_NO") );
					result.addString("PRE_TITL" 		,rs.getString("PRE_TITL") );
					result.addString("NEXT_SEQ_NO" 		,rs.getString("NEXT_SEQ_NO") );
					result.addString("NEXT_TITL" 		,rs.getString("NEXT_TITL") );
				}
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
    private String getSelectQuery(String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT 	");
        sql.append("\n     T.PRE_SEQ_NO, 	");
        sql.append("\n     NVL((SELECT TITL FROM BCDBA.TBGCLUBBBRD WHERE SEQ_NO=T.PRE_SEQ_NO),'') PRE_TITL, 	");
        sql.append("\n     T.NEXT_SEQ_NO,  	");
        sql.append("\n     NVL((SELECT TITL FROM BCDBA.TBGCLUBBBRD WHERE SEQ_NO=T.NEXT_SEQ_NO),'') NEXT_TITL 	");
        sql.append("\n FROM    ( 	");
        sql.append("\n             SELECT TCB.SEQ_NO, 	");
        sql.append("\n             		NVL((LEAD(TCB.SEQ_NO,1) OVER (ORDER BY TCB.URNK_EPS_YN DESC, TCB.SEQ_NO DESC)),'') PRE_SEQ_NO, 	");
        sql.append("\n             		NVL((LAG(TCB.SEQ_NO,1) OVER (ORDER BY TCB.URNK_EPS_YN DESC, TCB.SEQ_NO DESC)),'') NEXT_SEQ_NO 	");
        sql.append("\n             	FROM BCDBA.TBGCLUBBBRDMGMT TCBM, BCDBA.TBGCLUBBBRD TCB, BCDBA.TBGCLUBCDHDMGMT TCM 	");
		sql.append("\n 				WHERE TCBM.BBRD_SEQ_NO = TCB.BBRD_UNIQ_SEQ_NO	");
		sql.append("\n 				AND TCB.CLUB_CDHD_SEQ_NO = TCM.CLUB_CDHD_SEQ_NO(+)	");
		sql.append("\n 				AND TCB.DEL_YN = 'N'	");
		sql.append("\n 				AND TCBM.CLUB_SEQ_NO = ?	");
		sql.append("\n 				AND TCBM.BBRD_SEQ_NO = ?	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TCB.TITL LIKE ?	");
				sql.append("\n 				OR TCB.CTNT LIKE ? 	");	
				sql.append("\n 				OR TCM.CDHD_ID LIKE ? )	");	
			} else {
				sql.append("\n 				AND "+search_sel+" LIKE ?	");
			}
		}

        sql.append("\n         ) T      ");                                                             
        sql.append("\n WHERE SEQ_NO=? 	");
        
		return sql.toString();
    }    
    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT	");
		sql.append("\n 	TCBM.BBRD_SEQ_NO, TCBM.CLUB_BBRD_CLSS, TCBM.BBRD_INFO,	");
		sql.append("\n 	TCB.SEQ_NO, TCB.CLUB_CDHD_SEQ_NO, TCB.TITL, TCB.CTNT, TCB.INQR_NUM, TCB.URNK_EPS_YN, TCB.ANNX_FILE_PATH, TCB.DEL_YN, TO_CHAR(TO_DATE(TCB.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, 	");
		sql.append("\n 	TCM.CDHD_ID, NVL(TCM.CDHD_NM,'운영자') CDHD_NM, 	");
		sql.append("\n 	(SELECT COUNT(REPY_SEQ_NO) FROM BCDBA.TBGCLUBBBRDREPY WHERE BBRD_SEQ_NO=TCB.SEQ_NO) RE_CNT 	");
		sql.append("\n FROM 	");
		sql.append("\n BCDBA.TBGCLUBBBRDMGMT TCBM, BCDBA.TBGCLUBBBRD TCB, BCDBA.TBGCLUBCDHDMGMT TCM	");
		sql.append("\n WHERE TCBM.BBRD_SEQ_NO = TCB.BBRD_UNIQ_SEQ_NO	");
		sql.append("\n AND TCB.CLUB_CDHD_SEQ_NO = TCM.CLUB_CDHD_SEQ_NO(+)	");
		sql.append("\n AND TCB.DEL_YN = 'N'	");
		sql.append("\n AND TCBM.CLUB_SEQ_NO = ?	");
		sql.append("\n AND TCBM.BBRD_SEQ_NO = ?	");
		sql.append("\n AND TCB.SEQ_NO = ?	");
		
		return sql.toString();
    }

    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGCLUBBBRD SET	\n");
		sql.append("\t  INQR_NUM=INQR_NUM+1	\n");
		sql.append("\t WHERE SEQ_NO=?	\n");
        return sql.toString();
    }
}