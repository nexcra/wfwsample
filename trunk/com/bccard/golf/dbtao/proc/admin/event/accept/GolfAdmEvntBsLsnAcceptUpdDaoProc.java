/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmEvntBsLsnAcceptUpdDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  이벤트 > 특별레슨 이벤트 당참자관리 처리
*   적용범위	: golf
*   작성일자	: 2009-07-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.accept;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntBsLsnAcceptUpdDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 >  이벤트 > 특별레슨 이벤트 당참자관리 처리";
	/** **************************************************************************
	 * Proc 실행. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rset = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result = null;
		String sql = "";
		Writer writer = null;
		Reader reader = null;
	
		try {  
			
			// 01.data get
			String p_idx 		= data.getString("p_idx");
			String mode 		= data.getString("mode");
			String bltn_yn		= data.getString("bltn_yn");
			String ctnt			= data.getString("ctnt");
			String titl			= data.getString("titl");
			String admId		= data.getString("admId");
			String evnt_seq_no	= data.getString("evnt_seq_no");
			
			
			// 02.connection 연결
			conn = context.getDbConnection("default", null);
			result =  new DbTaoResult(TITLE);
			
			//기본변수들 할당
			int pidx = 0;
			int rs = 0;
			boolean eof = false;
			
			if("bltnChg".equals(mode)){
				
				pidx = 0;
				sql = this.getUpdateBltnChangeQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, bltn_yn);
				pstmt.setString(++pidx, p_idx);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					eof = true;
					result.addString("RESULT", "00");
				}else {
					eof = false;
					result.addString("RESULT", "01");
				}
				
			}else if("ins".equals(mode)){
				
				long maxVal = this.selectArticleNo(context);
				
				conn.setAutoCommit(false);
				
				pidx = 0;
				sql = this.getInsertQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(++pidx, maxVal);
				pstmt.setString(++pidx,titl);
				pstmt.setString(++pidx,bltn_yn);
				pstmt.setString(++pidx,evnt_seq_no);
				pstmt.setString(++pidx,admId);
				
				rs = pstmt.executeUpdate();
				if(pstmt != null) pstmt.close();
				
				// 02.Clob 처리
				if (ctnt.length() > 0){
					
					sql = this.getSelectForUpdateQuery();
					pstmt = conn.prepareStatement(sql);
					pstmt.setLong(1, maxVal);
					rset = pstmt.executeQuery();
		
//					if(rset.next()) {
//						java.sql.Clob clob = rset.getClob("CTNT");
//		                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//						reader = new CharArrayReader(ctnt.toCharArray());
//						
//						char[] buffer = new char[1024];
//						int read = 0;
//						while ((read = reader.read(buffer,0,1024)) != -1) {
//							writer.write(buffer,0,read);
//						}
//						writer.flush();
//					}
					if (rset  != null) rset.close();
					if (pstmt != null) pstmt.close();
				}
			
			
				// 03.처리
				if(rs > 0) {
					conn.commit();
					eof = true;
					result.addString("RESULT", "00");
				} else {
					conn.rollback();
					eof = false;
					result.addString("RESULT", "01");
				}
			
			}else if("upd".equals(mode)){
				
				pidx = 0;
				sql = this.getUpdateQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, titl);
				pstmt.setString(++pidx, ctnt);
				pstmt.setString(++pidx, bltn_yn);
				pstmt.setString(++pidx, admId);
				pstmt.setString(++pidx, p_idx);
				pstmt.setString(++pidx, evnt_seq_no);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					eof = true;
					result.addString("RESULT", "00");
				}else {
					eof = false;
					result.addString("RESULT", "01");
				}
				
				
			}else if("del".equals(mode)){
				pidx = 0;
				sql = this.getDeleteQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, p_idx);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					eof = true;
					result.addString("RESULT", "00");
				}else {
					eof = false;
					result.addString("RESULT", "01");
				}
				
			}
				

			
		} catch ( Exception e ) {			
			
		} finally {
			try { if(rset != null) rset.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getUpdateBltnChangeQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n UPDATE BCDBA.TBGEVNTPRZPEMGMT  SET						");
		sql.append("\n 		  BLTN_YN = ?										");
		sql.append("\n WHERE  SEQ_NO = ?										");

		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getDeleteQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n DELETE BCDBA.TBGEVNTPRZPEMGMT  							");
		sql.append("\n WHERE  SEQ_NO = ? 										");

		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getInsertQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n INSERT INTO BCDBA.TBGEVNTPRZPEMGMT(							");
		sql.append("\n 		  SEQ_NO												");
		sql.append("\n 		  ,TITL													");
		sql.append("\n 		  ,CTNT													");
		sql.append("\n 		  ,BLTN_YN												");
		sql.append("\n 		  ,INQR_NUM												");
		sql.append("\n 		  ,EVNT_SEQ_NO											");
		sql.append("\n 		  ,REG_MGR_ID											");
		sql.append("\n 		  ,REG_ATON												");
		sql.append("\n 	)VALUES(  													");
		sql.append("\n 		?,?,EMPTY_CLOB(),?,0,									");
		sql.append("\n 		?,?,TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')				");
		sql.append("\n 	 )															");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getUpdateQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n UPDATE BCDBA.TBGEVNTPRZPEMGMT SET							");
		sql.append("\n 			TITL = ?											");
		sql.append("\n 		    ,CTNT = ?											");
		sql.append("\n 		  	,BLTN_YN = ?										");
		sql.append("\n 		  ,CHNG_MGR_ID	= ?										");
		sql.append("\n 		  ,CHNG_ATON = 	TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')	");	
		sql.append("\n 	WHERE SEQ_NO = ? AND EVNT_SEQ_NO = ?						");
		return sql.toString();
	}
	/** ******************************************************************************
	 * 게시판 글번호 가져오기
	 *********************************************************************************/
	private long selectArticleNo(WaContext context) throws Exception {

		Connection con = null;
        PreparedStatement pstmt = null;        
        ResultSet rset = null;        
        String sql = " SELECT NVL(MAX(SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGEVNTPRZPEMGMT ";
        long pidx = 0;
      
        try {
        	con = context.getDbConnection("default", null);
        	pstmt = con.prepareStatement(sql);
        	rset = pstmt.executeQuery(); 
        
			if (rset.next()) {				
                pidx = rset.getLong("SEQ_NO");
        
			}
		
        } catch (Throwable t) {        
        	Exception exception = new Exception(t);          
            throw exception;
        } finally {
            try { if ( rset  != null ) rset.close();  } catch ( Throwable ignored) {}
            try { if ( pstmt != null ) pstmt.close(); } catch ( Throwable ignored) {}
            try { if ( con    != null ) con.close();    } catch ( Throwable ignored) {}
        }
        return pidx;

	}
	/** ***********************************************************************
	    * CLOB Query를 생성하여 리턴한다.    
	************************************************************************ */
	 private String getSelectForUpdateQuery() throws Exception{
	     StringBuffer sql = new StringBuffer();
	     sql.append("\n  SELECT CTNT FROM BCDBA.TBGEVNTPRZPEMGMT WHERE SEQ_NO = ? FOR UPDATE  ");
			return sql.toString();
	 }
}
