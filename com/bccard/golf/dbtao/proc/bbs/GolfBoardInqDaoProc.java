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

package com.bccard.golf.dbtao.proc.bbs;

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
	
	/** *****************************************************************
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
			pstmt.setString(++idx, data.getString("BBS"));
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			pstmt.setString(++idx, data.getString("BBS"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					result.addLong("SEQ_NO" 				,rs.getLong("BBRD_SEQ_NO") );
					result.addString("BBRD_UNIQ_SEQ_NO" 	,rs.getString("BBRD_CLSS") );
					result.addString("FIELD_CD" 			,rs.getString("GOLF_CLM_CLSS") );
					result.addString("CLSS_CD" 				,rs.getString("GOLF_BOKG_FAQ_CLSS") );
					result.addString("SEC_CD" 				,rs.getString("GOLF_VBL_RULE_PREM_CLSS") );
					result.addString("TITL" 				,rs.getString("TITL") );
					result.addString("ID"					,rs.getString("ID") );
					result.addString("HG_NM"				,rs.getString("HG_NM") );
					result.addString("EMAIL_ID"				,rs.getString("EMAIL") );
					result.addLong("INOR_NUM"				,rs.getLong("INQR_NUM") );
					result.addString("EPS_YN"				,rs.getString("EPS_YN") );
					result.addString("FILE_NM"				,rs.getString("ANNX_FILE_NM") );
					result.addString("PIC_NM"				,rs.getString("MVPT_ANNX_FILE_PATH") );
					result.addString("HD_YN"				,rs.getString("PROC_YN") );
					result.addString("DEL_YN"				,rs.getString("DEL_YN") );
					result.addString("REG_MGR_SEQ_NO"		,rs.getString("REG_MGR_ID") );
					result.addString("REG_ATON"				,rs.getString("REG_ATON") );
					result.addString("BEST_YN"				,rs.getString("BEST_YN") );
					result.addString("NEW_YN"				,rs.getString("ANW_BLTN_ARTC_YN") );
					result.addLong("REPY_URNK_SEQ_NO"		,rs.getLong("REPY_URNK_SEQ_NO") );
					result.addString("REPLY_YN"				,rs.getString("REPLY_YN") );
					result.addLong("RE_CNT"					,rs.getLong("RE_CNT") );
					result.addString("GOLF_CLM_CLSS_NM"		,rs.getString("GOLF_CLM_CLSS_NM") );

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
			pstmt.setString(++idx, data.getString("BBS"));
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			pstmt.setString(++idx, data.getString("BBS"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					resultMap.put("SEQ_NO" 				,rs.getString("BBRD_SEQ_NO") );
					resultMap.put("BBRD_UNIQ_SEQ_NO" 	,rs.getString("BBRD_CLSS") );
					resultMap.put("FIELD_CD" 			,rs.getString("GOLF_CLM_CLSS") );
					resultMap.put("CLSS_CD" 			,rs.getString("GOLF_BOKG_FAQ_CLSS") );
					resultMap.put("SEC_CD" 				,rs.getString("GOLF_VBL_RULE_PREM_CLSS") );
					resultMap.put("TITL" 				,rs.getString("TITL") );
					resultMap.put("ID"					,rs.getString("ID") );
					resultMap.put("HG_NM"				,rs.getString("HG_NM") );
					resultMap.put("EMAIL_ID"			,rs.getString("EMAIL") );
					resultMap.put("INOR_NUM"			,rs.getString("INQR_NUM") );
					resultMap.put("EPS_YN"				,rs.getString("EPS_YN") );
					resultMap.put("FILE_NM"				,rs.getString("ANNX_FILE_NM") );
					resultMap.put("PIC_NM"				,rs.getString("MVPT_ANNX_FILE_PATH") );
					resultMap.put("HD_YN"				,rs.getString("PROC_YN") );
					resultMap.put("DEL_YN"				,rs.getString("DEL_YN") );
					resultMap.put("REG_MGR_SEQ_NO"		,rs.getString("REG_MGR_ID") );
					resultMap.put("REG_ATON"			,rs.getString("REG_ATON") );
					resultMap.put("BEST_YN"				,rs.getString("BEST_YN") );
					resultMap.put("NEW_YN"				,rs.getString("ANW_BLTN_ARTC_YN") );
					resultMap.put("REPY_URNK_SEQ_NO"	,rs.getString("REPY_URNK_SEQ_NO") );
					resultMap.put("REPLY_YN"			,rs.getString("REPLY_YN") );
					resultMap.put("RE_CNT"				,rs.getString("RE_CNT") );
					resultMap.put("GOLF_CLM_CLSS_NM"	,rs.getString("GOLF_CLM_CLSS_NM") );

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
			pstmt.setString(++idx, data.getString("BBS") );
			
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
			String sch_field_cd		= data.getString("SCH_FIELD_CD");
			String sch_clss_cd		= data.getString("SCH_CLSS_CD");
			String sch_sec_cd		= data.getString("SCH_SEC_CD");
			String sch_hd_yn		= data.getString("SCH_HD_YN");			

			String sql = this.getSelectQuery(search_sel, search_word, sch_field_cd, sch_clss_cd, sch_sec_cd, sch_hd_yn);
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, data.getString("BBS"));
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			if (!GolfUtil.isNull(sch_field_cd))	pstmt.setString(++idx, sch_field_cd);
			if (!GolfUtil.isNull(sch_clss_cd))	pstmt.setString(++idx, sch_clss_cd);
			if (!GolfUtil.isNull(sch_sec_cd))	pstmt.setString(++idx, sch_sec_cd);
			if (!GolfUtil.isNull(sch_hd_yn))	pstmt.setString(++idx, sch_hd_yn);			
			pstmt.setString(++idx, data.getString("SEQ_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {	
					result.addString("PRE_SEQ_NO" 				,rs.getString("PRE_BBRD_SEQ_NO") );
					result.addString("PRE_TITL" 				,rs.getString("PRE_TITL") );
					result.addString("PRE_REPY_URNK_SEQ_NO" 	,rs.getString("PRE_REPY_URNK_SEQ_NO") );
					result.addString("NEXT_SEQ_NO" 				,rs.getString("NEXT_BBRD_SEQ_NO") );
					result.addString("NEXT_TITL" 				,rs.getString("NEXT_TITL") );
					result.addString("NEXT_REPY_URNK_SEQ_NO" 	,rs.getString("NEXT_REPY_URNK_SEQ_NO") );
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
    private String getSelectQuery(String search_sel, String search_word, String sch_field_cd, String sch_clss_cd, String sch_sec_cd, String sch_hd_yn){
        StringBuffer sql = new StringBuffer();

        
        sql.append("\n SELECT 	");
        sql.append("\n     T.PRE_BBRD_SEQ_NO, 	");
        sql.append("\n     NVL((SELECT TITL FROM BCDBA.TBGBBRD WHERE BBRD_SEQ_NO=T.PRE_BBRD_SEQ_NO),'') PRE_TITL, 	");
        sql.append("\n     NVL((SELECT REPY_URNK_SEQ_NO FROM BCDBA.TBGBBRD WHERE BBRD_SEQ_NO=T.PRE_BBRD_SEQ_NO),'') PRE_REPY_URNK_SEQ_NO,    	");
        sql.append("\n     T.NEXT_BBRD_SEQ_NO,  	");
        sql.append("\n     NVL((SELECT TITL FROM BCDBA.TBGBBRD WHERE BBRD_SEQ_NO=T.NEXT_BBRD_SEQ_NO),'') NEXT_TITL, 	");
        sql.append("\n     NVL((SELECT REPY_URNK_SEQ_NO FROM BCDBA.TBGBBRD WHERE BBRD_SEQ_NO=T.NEXT_BBRD_SEQ_NO),'') NEXT_REPY_URNK_SEQ_NO 	");
        sql.append("\n FROM    ( 	");
        sql.append("\n             SELECT BBRD_SEQ_NO, 	");
        sql.append("\n             NVL((LEAD(BBRD_SEQ_NO,1) OVER (ORDER BY REPY_URNK_SEQ_NO DESC, BBRD_SEQ_NO ASC)),'') PRE_BBRD_SEQ_NO, 	");
        sql.append("\n             NVL((LAG(BBRD_SEQ_NO,1) OVER (ORDER BY REPY_URNK_SEQ_NO DESC, BBRD_SEQ_NO ASC)),'') NEXT_BBRD_SEQ_NO 	");
        sql.append("\n             FROM BCDBA.TBGBBRD 	");
        sql.append("\n             WHERE BBRD_CLSS = ? 	");
        sql.append("\n         		AND DEL_YN = 'N' 	");
        if (!GolfUtil.isNull(search_word)) {
        	if (search_sel.equals("ALL")) {
        		sql.append("\n 				AND (TITL LIKE ?	");
        		sql.append("\n 				OR CTNT LIKE ? )	");	
        	} else {
        		sql.append("\n 				AND "+search_sel+" LIKE ?	");
        	}
        }
        if (!GolfUtil.isNull(sch_field_cd)) sql.append("\n 		AND GOLF_CLM_CLSS = ?	");
        if (!GolfUtil.isNull(sch_clss_cd)) sql.append("\n 		AND GOLF_BOKG_FAQ_CLSS = ?	");
        if (!GolfUtil.isNull(sch_sec_cd)) sql.append("\n 		AND GOLF_VBL_RULE_PREM_CLSS = ?	");
        if (!GolfUtil.isNull(sch_hd_yn)) sql.append("\n 		AND PROC_YN = ?	");
        sql.append("\n         ) T      ");                                                             
        sql.append("\n WHERE BBRD_SEQ_NO=? 	");
        
		return sql.toString();
    }    
    
	/** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT");
		sql.append("\n 	TBB.BBRD_SEQ_NO, TBB.BBRD_CLSS, TBB.GOLF_CLM_CLSS, TBB.GOLF_BOKG_FAQ_CLSS, TBB.GOLF_VBL_RULE_PREM_CLSS, TBB.TITL, TBB.CTNT, TBB.ID, NVL(TBB.HG_NM,'관리자') HG_NM, TBB.EMAIL, TBB.INQR_NUM, TBB.EPS_YN, TBB.ANNX_FILE_NM, TBB.MVPT_ANNX_FILE_PATH, NVL(TBB.PROC_YN,'N') PROC_YN, 	");
		sql.append("\n 	TBB.DEL_YN, TBB.REG_MGR_ID, TO_CHAR(TO_DATE(TBB.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, TBB.BEST_YN, TBB.ANW_BLTN_ARTC_YN, TBB.REPY_URNK_SEQ_NO 	");
		sql.append("\n 	,DECODE(TBB.BBRD_SEQ_NO-TBB.REPY_URNK_SEQ_NO,0,'N','Y') REPLY_YN");
		sql.append("\n 	,(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE=? AND GOLF_CMMN_CODE=TBB.GOLF_CLM_CLSS) GOLF_CLM_CLSS_NM 	");
		sql.append("\n 	,(SELECT COUNT(REPY_SEQ_NO) FROM BCDBA.TBGBBRDREPY WHERE REPY_CLSS='0001' AND BBRD_SEQ_NO=TBB.BBRD_SEQ_NO) RE_CNT");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGBBRD TBB");
		sql.append("\n WHERE TBB.BBRD_SEQ_NO = ?	");
		sql.append("\n AND TBB.BBRD_CLSS = ?	");

		return sql.toString();
    }

    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGBBRD SET	\n");
		sql.append("\t  INQR_NUM=INQR_NUM+1	\n");
		sql.append("\t WHERE BBRD_SEQ_NO=?	\n");
		sql.append("\t AND BBRD_CLSS=?	\n");
        return sql.toString();
    }
}
