/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardInsDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.club;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfBoardInsDaoProc extends AbstractProc {

	public static final String TITLE = "공통게시판 등록 처리";

	/** *****************************************************************
	 * GolfBoardInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBoardInsDaoProc() {}
	
	/**
	 * 공통게시판 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

			sql = this.getNextValQuery(); //일련번호
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String seq_no = "";
			if(rs.next()){
				seq_no = rs.getString("SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, seq_no );
			pstmt.setString(++idx, data.getString("BBRD_UNIQ_SEQ_NO") );
			pstmt.setString(++idx, data.getString("CLUB_CDHD_SEQ_NO") ); 
			pstmt.setString(++idx, data.getString("ID") );
			pstmt.setString(++idx, data.getString("TITL") );
			pstmt.setString(++idx, data.getString("URNK_EPS_YN") );
			pstmt.setString(++idx, data.getString("ANNX_FILE_PATH") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			sql = this.getSelectForUpdateQuery();//CTNT
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, seq_no);
            rs = pstmt.executeQuery();

//			if(rs.next()) {
//				java.sql.Clob clob = rs.getClob("CTNT");
//                //writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
//                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//				reader = new CharArrayReader(data.getString("CTNT").toCharArray());
//				
//				char[] buffer = new char[1024];
//				int read = 0;
//				while ((read = reader.read(buffer,0,1024)) != -1) {
//					writer.write(buffer,0,read);
//				}
//				writer.flush();
//			}			
			
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
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGCLUBBBRD (	\n");
		sql.append("\t  SEQ_NO, BBRD_UNIQ_SEQ_NO,   	\n");
		sql.append("\t	CLUB_CDHD_SEQ_NO, 	\n");
		sql.append("\t	TITL, CTNT, INQR_NUM, URNK_EPS_YN, ANNX_FILE_PATH, DEL_YN, REG_ATON, CHNG_ATON	\n");
		sql.append("\t ) VALUES (	\n");
		sql.append("\t  ?,?,	\n");
		sql.append("\t  (SELECT CLUB_CDHD_SEQ_NO FROM BCDBA.TBGCLUBCDHDMGMT WHERE CLUB_SEQ_NO = ? AND CDHD_ID=?),	\n");
		sql.append("\t  ?,EMPTY_CLOB(),0,?,?,'N',TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),NULL	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGCLUBBBRD \n");
		return sql.toString();
    }
    
	/** ***********************************************************************
     * CLOB Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT CTNT FROM BCDBA.TBGCLUBBBRD \n");
         sql.append("WHERE SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
} 