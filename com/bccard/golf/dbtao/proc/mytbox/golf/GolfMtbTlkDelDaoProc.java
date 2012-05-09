/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMtFieldMutiDelDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 마이티박스(골프장정보) 다중 삭제 처리
*   적용범위  : golf
*   작성일자  : 2009-06-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.golf;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfMtbTlkDelDaoProc extends AbstractProc {

	public static final String TITLE = "마이티박스(골프장정보) 다중 삭제 처리";

	/** *****************************************************************
	 * GolfMtFieldMutiDelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfMtbTlkDelDaoProc() {}
	
	/**
	 * 마이티박스(골프장정보) 다중 삭제 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data, String[] seq_no) throws DbTaoException  {

		Connection conn = null;
		PreparedStatement pstmt = null;
		int iCount1 = 0;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//조회 ----------------------------------------------------------
		
			String sql = this.getDeleteQuery1();			
			pstmt = conn.prepareStatement(sql);
			
			for (int i = 0; i < seq_no.length; i++) {				
				if (seq_no[i] != null && seq_no[i].length() > 0) {
					pstmt.setString(1, seq_no[i]); 
					pstmt.setString(2, data.getString("ADMIN_NO"));

					iCount1 += pstmt.executeUpdate();
				}
			}
			if(pstmt != null) pstmt.close();
			
			//getDeleteQuery2 지원가 없을 경우 삭제 결과가 0이 되므로 체크하지 않음.
			if(iCount1 == seq_no.length) {
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

		return iCount1;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getDeleteQuery1(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGSCRAP 	\n");
		sql.append("\t  WHERE BOD_SEQ_NO = ?	");	
		sql.append("\t 	 AND CDHD_ID = ?	");	
		sql.append("\t 	 AND GOLF_SCRAP_CLSS = '0002'	");	
        return sql.toString();
    }
    
}
