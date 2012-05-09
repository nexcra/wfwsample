/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmSysWidgetRegDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 > 위젯 > 등록처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.system.widget;

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

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfadmSysWidgetRegDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 프리미엄부킹 골프장 등록 처리";

	/** *****************************************************************
	 * GolfadmGrRegDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmSysWidgetRegDaoProc() {}
	
	/**
	 * 관리자 프리미엄부킹 골프장 등록 처리
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
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

            /**SEQ_NO 가져오기**************************************************************/
			sql = this.getNextValQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			long max_seq_no = 0L;
			if(rs.next()){
				max_seq_no = rs.getLong("SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql);
			//debug("pstmt => " + pstmt);
			
			int idx = 0;
        	pstmt.setLong(++idx, max_seq_no );
        	pstmt.setString(++idx, data.getString("EPS_YN") ); 
        	pstmt.setString(++idx, data.getString("ANNX_FILE_NM") ); 
        	pstmt.setString(++idx, data.getString("MVPT_ANNX_FILE_PATH") );
        	
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
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}

	

	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t  INSERT INTO BCDBA.TBGBBRD(																\n");
		sql.append("\t  		BBRD_SEQ_NO, BBRD_CLSS, EPS_YN, ANNX_FILE_NM, MVPT_ANNX_FILE_PATH, REG_ATON		\n");
		sql.append("\t  		) VALUES (																		\n");
		sql.append("\t  		?, '0021', ?, ?, ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')							\n");
		sql.append("\t  		)																				\n");
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(BBRD_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGBBRD \n");
		return sql.toString();
    }
}
