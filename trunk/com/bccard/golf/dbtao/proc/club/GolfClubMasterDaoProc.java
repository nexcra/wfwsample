/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardComtInqDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 덧글 상세
*   적용범위  : golf
*   작성일자  : 2009-06-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.club;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	만세커뮤니케이션
 * @version	1.0
 ******************************************************************************/
public class GolfClubMasterDaoProc extends AbstractProc {
	
	public static final String TITLE = "동호회 정보 마스터";
	
	/** *****************************************************************
	 * GolfBoardComtInqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfClubMasterDaoProc() {}	

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
			pstmt.setString(++idx, data.getString("CLUB_CODE"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					result.addLong("CLUB_SEQ_NO" 		,rs.getLong("CLUB_SEQ_NO") );
					result.addString("GOLF_CLUB_CTGO" 		,rs.getString("GOLF_CLUB_CTGO") );
					result.addString("CLUB_NM" 			,rs.getString("CLUB_NM") );
					result.addString("OPN_PE_ID" 		,rs.getString("OPN_PE_ID") );
					result.addString("OPN_PE_NM" 		,rs.getString("OPN_PE_NM") );
					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO"		,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO"		,rs.getString("HP_TEL_SNO") );
					result.addString("CLUB_SBJT_CTNT"		,rs.getString("CLUB_SBJT_CTNT") );
					result.addString("CLUB_IMG"			,rs.getString("CLUB_IMG") );
					result.addString("CLUB_INTD_CTNT"		,rs.getString("CLUB_INTD_CTNT") );
					result.addString("CLUB_INTD_CTNT_TOP"		,rs.getString("CLUB_INTD_CTNT_TOP") );
					result.addString("CLUB_OPN_PRPS_CTNT"		,rs.getString("CLUB_OPN_PRPS_CTNT") );
					result.addString("CDHD_NUM_LIMT_YN"		,rs.getString("CDHD_NUM_LIMT_YN") );
					result.addString("LIMT_CDHD_NUM"		,rs.getString("LIMT_CDHD_NUM") );
					result.addString("CLUB_JONN_MTHD_CLSS"		,rs.getString("CLUB_JONN_MTHD_CLSS") );
					result.addString("CLUB_OPN_AUTH_YN"		,rs.getString("CLUB_OPN_AUTH_YN") );
					result.addString("CLUB_ACT_YN"		,rs.getString("CLUB_ACT_YN") );
					result.addString("APLC_ATON"		,rs.getString("APLC_ATON") );
					result.addString("OPN_ATON"			,rs.getString("OPN_ATON") );
					result.addString("CHNG_MGR_ID"		,rs.getString("CHNG_MGR_ID") );
					result.addString("CHNG_ATON"		,rs.getString("CHNG_ATON") );
					result.addString("GOLF_CLUB_CTGO_NM"		,rs.getString("GOLF_CLUB_CTGO_NM") );
					result.addString("MEM_CNT"		,rs.getString("MEM_CNT") );
					
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
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public String getClubMemChk(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String result = "";

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery2();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("CLUB_CODE"));
			pstmt.setString(++idx, data.getString("CDHD_ID"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					result = "Y";
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
    

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult getClubCateMemCnt(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery3();   
			
			// 입력값 (INPUT)         
			pstmt = conn.prepareStatement(sql);			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					result.addString("GOLF_CMMN_CODE" 		,rs.getString("GOLF_CMMN_CODE") );
					result.addString("GOLF_CMMN_CODE_NM" 	,rs.getString("GOLF_CMMN_CODE_NM") );
					result.addString("CLUB_CTGO_CNT" 		,rs.getString("CLUB_CTGO_CNT") );
					
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
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public String getClubCateNm(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		String result = "";

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery4();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("GOLF_CLUB_CTGO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					result = rs.getString("GOLF_CMMN_CODE_NM");
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


	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int getClubNmOverLapChk(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result = 0;

		try {
			conn = context.getDbConnection("default", null);
			 
			//조회 ----------------------------------------------------------			
			String sql = this.getSelectQuery5();   
			
			// 입력값 (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("CLUB_NM"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {
					result = 1;//rs.getString("CLUB_CDHD_SEQ_NO");
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
	/**
	 * 공통게시판 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int updClubInfo(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
            String club_img = data.getString("CLUB_IMG");
            
			String sql = this.getUpdateQuery(club_img);//Update Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("CLUB_NM") ); 
			pstmt.setString(++idx, data.getString("CLUB_SBJT_CTNT") );
			if (!GolfUtil.isNull(club_img))	pstmt.setString(++idx, club_img);
			pstmt.setString(++idx, data.getString("CLUB_INTD_CTNT") );
			pstmt.setString(++idx, data.getString("CLUB_JONN_MTHD_CLSS") );
			
			pstmt.setString(++idx, data.getString("CLUB_SEQ_NO") );
			pstmt.setString(++idx, data.getString("OPN_PE_ID") );
			
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
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
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
		sql.append("\n 	CLUB_SEQ_NO, GOLF_CLUB_CTGO, CLUB_NM, OPN_PE_ID, OPN_PE_NM, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, 	");
		sql.append("\n 	CLUB_SBJT_CTNT, CLUB_IMG, CLUB_INTD_CTNT, REPLACE(CLUB_INTD_CTNT, chr(13)||chr(10),'<BR>') CLUB_INTD_CTNT_TOP, CLUB_OPN_PRPS_CTNT, CDHD_NUM_LIMT_YN, LIMT_CDHD_NUM, 	");
		sql.append("\n 	CLUB_JONN_MTHD_CLSS, CLUB_OPN_AUTH_YN, CLUB_ACT_YN, APLC_ATON, TO_CHAR(TO_DATE(OPN_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') OPN_ATON, CHNG_MGR_ID, CHNG_ATON,	");
		sql.append("\n 	(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='0042' AND GOLF_CMMN_CODE=TC.GOLF_CLUB_CTGO) GOLF_CLUB_CTGO_NM,	");
		sql.append("\n 	NVL((SELECT COUNT(CLUB_CDHD_SEQ_NO) FROM BCDBA.TBGCLUBCDHDMGMT WHERE CLUB_SEQ_NO=TC.CLUB_SEQ_NO AND JONN_YN='Y' AND SECE_YN='N'),0) MEM_CNT	");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGCLUBMGMT TC	");
		sql.append("\n WHERE CLUB_SEQ_NO = ?	");

		return sql.toString();
    }
    
    
	/** ***********************************************************************
     * Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectQuery2(){
         StringBuffer sql = new StringBuffer();
         
 		sql.append("\n SELECT");
 		sql.append("\n 	CLUB_CDHD_SEQ_NO	");
 		sql.append("\n FROM");
 		sql.append("\n BCDBA.TBGCLUBCDHDMGMT	");
 		sql.append("\n WHERE CLUB_SEQ_NO = ?	");
 		sql.append("\n AND CDHD_ID = ?	");

 		return sql.toString();
     }

 	/** ***********************************************************************
      * Query를 생성하여 리턴한다.    
      ************************************************************************ */
      private String getSelectQuery3(){
          StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT	");
		sql.append("\n 	TGC.GOLF_CMMN_CODE, TGC.GOLF_CMMN_CODE_NM,	");
		sql.append("\n 	(SELECT COUNT(TCM.CLUB_SEQ_NO) FROM BCDBA.TBGCLUBMGMT TC, BCDBA.TBGCLUBMGMT TCM WHERE TC.CLUB_SEQ_NO=TCM.CLUB_SEQ_NO AND TCM.GOLF_CLUB_CTGO=TGC.GOLF_CMMN_CODE AND TC.CLUB_OPN_AUTH_YN = 'Y' AND TC.CLUB_ACT_YN = 'Y') CLUB_CTGO_CNT 	");
		sql.append("\n FROM 	");
		sql.append("\n BCDBA.TBGCMMNCODE TGC 	");
		sql.append("\n WHERE TGC.GOLF_URNK_CMMN_CODE = '0042'	");
		sql.append("\n AND TGC.USE_YN = 'Y' ");
		sql.append("\n ORDER BY TGC.SORT_SEQ	");		

  		return sql.toString();
      }

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.    
	************************************************************************ */
	private String getSelectQuery4(){
	    StringBuffer sql = new StringBuffer();
	
	sql.append("\n SELECT	");
	sql.append("\n 	GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM	");
	sql.append("\n FROM 	");
	sql.append("\n BCDBA.TBGCMMNCODE 	");
	sql.append("\n WHERE GOLF_URNK_CMMN_CODE = '0042'	");
	sql.append("\n AND GOLF_CMMN_CODE = ?	");
	sql.append("\n AND USE_YN = 'Y' ");
	sql.append("\n ORDER BY SORT_SEQ	");		
	
		return sql.toString();
	}


	/** ***********************************************************************
	* Query를 생성하여 리턴한다.    
	************************************************************************ */
	private String getSelectQuery5(){
	    StringBuffer sql = new StringBuffer();
	    
 		sql.append("\n SELECT");
 		sql.append("\n 	CLUB_SEQ_NO	");
 		sql.append("\n FROM");
 		sql.append("\n BCDBA.TBGCLUBMGMT		");
 		sql.append("\n WHERE CLUB_NM = ?	");

 		return sql.toString();
	}
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getUpdateQuery(String club_img){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGCLUBMGMT SET	\n");
		sql.append("\t  CLUB_NM=?, CLUB_SBJT_CTNT=?,  	\n");
		if (!GolfUtil.isNull(club_img)) sql.append("\t 	 CLUB_IMG=?,	");
		sql.append("\t  CLUB_INTD_CTNT=?, CLUB_JONN_MTHD_CLSS=?,  	\n");
		sql.append("\t  CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS')  	\n");
		sql.append("\t WHERE CLUB_SEQ_NO=?	\n");
		sql.append("\t AND OPN_PE_ID=?	\n");
        return sql.toString();
    }
}