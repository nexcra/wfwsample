/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfCupnPrintInsDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 드라이빙레인지/스크린 할인쿠폰 인쇄이력 등록
*   적용범위  : golf
*   작성일자  : 2009-06-13
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.drivrange;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfCupnPrintInsDaoProc extends AbstractProc {

	public static final String TITLE = "드라이빙레인지/스크린 할인쿠폰 인쇄이력 등록";

	/** *****************************************************************
	 * GolfCupnPrintInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfCupnPrintInsDaoProc() {}
	
	/**
	 * 드라이빙레인지/스크린 할인쿠폰 인쇄이력 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		int rtnB = 0;
		DbTaoResult result =  new DbTaoResult(title);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

			sql = this.getNextValQuery(); //번호 쿼리
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			long seq_no = 0L;
			if(rs.next()){
				seq_no = rs.getLong("CPNINF_SQL_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
            sql = this.getGuidValQuery(); //Guid 쿼리
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String serial_no = "";
			if(rs.next()){
				serial_no = rs.getString("CUPN_GUID");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setLong(++idx, seq_no );
			pstmt.setLong(++idx, data.getLong("GF_SEQ_NO") ); 
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setString(++idx, serial_no );
			
			rtnB = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			if(rtnB > 0) {
				conn.commit();
				
				result.addString("CPN_SERIAL" 	,serial_no );
				result.addString("RESULT", "00"); //정상결과
			} else {
				conn.rollback();
				
				result.addString("RESULT", "01");	
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
		sql.append("INSERT INTO BCDBA.TBGCUPNUSEHST (	\n");
		sql.append("\t  CUPN_HST_SEQ_NO, RNG_SEQ_NO, CDHD_ID, GOLF_RNG_CUPN_SEQ_NO, CUPN_OUTP_ATON 	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(CUPN_HST_SEQ_NO),0)+1 CPNINF_SQL_NO FROM BCDBA.TBGCUPNUSEHST \n");
		return sql.toString();
    }
    
    /** ***********************************************************************
     * SYS_GUID Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getGuidValQuery(){
         StringBuffer sql = new StringBuffer();
 		sql.append("\n");
         sql.append("SELECT SUBSTR (SYS_GUID (), 1, 16) CUPN_GUID FROM DUAL \n");
 		return sql.toString();
     }
    
	
}
