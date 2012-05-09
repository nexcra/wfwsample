/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtScoreUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 마이티박스 > 골프정보 > 스코어 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19 
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.golf;

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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfMtScoreUpdDaoProc extends AbstractProc {

	public static final String TITLE = "스코어 수정 처리";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMtScoreUpdDaoProc() {}
	
	/**
	 * 관리자 레슨프로그램 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			String round_DATE = data.getString("ROUND_DATE");
			round_DATE = GolfUtil.replace(round_DATE, "-", "");
            /*****************************************************************************/
            
			String sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);

			int idx = 0;
        	pstmt.setString(++idx, data.getString("GREEN_NM") ); 
        	pstmt.setString(++idx, round_DATE ); 
        	pstmt.setString(++idx, data.getString("CURS_NM") );
        	pstmt.setString(++idx, data.getString("HADC_NUM") );
        	
        	pstmt.setInt(++idx, data.getInt("HOLE01_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE02_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE03_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE04_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE05_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE06_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE07_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE08_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE09_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE10_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE11_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE12_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE13_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE14_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE15_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE16_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE17_SCOR") );
        	pstmt.setInt(++idx, data.getInt("HOLE18_SCOR") );
        	
        	pstmt.setInt(++idx, data.getInt("HIT_CNT") );
        	pstmt.setInt(++idx, data.getInt("EG_NUM") );
        	pstmt.setInt(++idx, data.getInt("BID_NUM") );
        	pstmt.setInt(++idx, data.getInt("PAR_NUM") );
        	pstmt.setInt(++idx, data.getInt("BOG_NUM") );
        	pstmt.setInt(++idx, data.getInt("DOB_BOG_NUM") );
        	pstmt.setInt(++idx, data.getInt("TRP_BOG_NUM") );
        	pstmt.setInt(++idx, data.getInt("ETC_BOG_NUM") );
        	pstmt.setString(++idx, data.getString("ROUND_MEMO_CTNT") );
        	pstmt.setInt(++idx, data.getInt("SEQ_NO") );
        	
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
    private String getInsertQuery(){
    	
        StringBuffer sql = new StringBuffer();
		sql.append("\n");		
		sql.append("\t  UPDATE BCDBA.TBGSCORINFO SET																	\n");
		sql.append("\t  	GREEN_NM=?, ROUND_DATE=?, CURS_NM=?, HADC_NUM=?											\n");
		sql.append("\t  	, HOLE01_SCOR=?, HOLE02_SCOR=?, HOLE03_SCOR=?, HOLE04_SCOR=?								\n");
		sql.append("\t  	, HOLE05_SCOR=?, HOLE06_SCOR=?, HOLE07_SCOR=?, HOLE08_SCOR=?								\n");
		sql.append("\t  	, HOLE09_SCOR=?, HOLE10_SCOR=?, HOLE11_SCOR=?, HOLE12_SCOR=?								\n");
		sql.append("\t  	, HOLE13_SCOR=?, HOLE14_SCOR=?, HOLE15_SCOR=?, HOLE16_SCOR=?								\n");
		sql.append("\t  	, HOLE17_SCOR=?, HOLE18_SCOR=?																\n");
		sql.append("\t  	, HIT_CNT=?, EG_NUM=?, BID_NUM=?, PAR_NUM=?, BOG_NUM=?, DOB_BOG_NUM=?						\n");
		sql.append("\t  	, TRP_BOG_NUM=?, ETC_BOG_NUM=?, ROUND_MEMO_CTNT=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')	\n");
		sql.append("\t  	WHERE SEQ_NO=?																				\n");
		
        return sql.toString();
    }
}
