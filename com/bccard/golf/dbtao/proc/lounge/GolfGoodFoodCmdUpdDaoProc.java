/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfGoodFoodCmdUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 골프장 주변맛집 추천하기
*   적용범위  : golf
*   작성일자  : 2009-06-11
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.lounge;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfGoodFoodCmdUpdDaoProc extends AbstractProc {

	public static final String TITLE = "골프장 주변맛집 추천하기";

	/** *****************************************************************
	 * GolfGoodFoodCmdUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfGoodFoodCmdUpdDaoProc() {}
	
	/**
	 * 골프장 주변맛집 추천수 수정 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
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
			pstmt.setString(++idx, data.getString("FD_SEQ_NO") );
			
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
    private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGAFFIETHS SET	\n");
		sql.append("\t  CMD_NUM=NVL (CMD_NUM, 0)+1 	\n");
		sql.append("\t WHERE AFFI_ETHS_SEQ_NO=?	\n");
        return sql.toString();
    }
}
