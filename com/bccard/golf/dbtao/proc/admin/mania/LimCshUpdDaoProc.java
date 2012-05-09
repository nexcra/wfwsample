/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : LimCshUpdDaoProc
*   작성자	: (주)만세커뮤니케이션 김인겸
*   내용		: 관리자 > 골프마니아 > 골프장리무진할인 > 금액관리
*   적용범위	: golf
*   작성일자	: 2009-06-24
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.mania; 

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

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class LimCshUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 골프장리무진할인신청관리 수정처리";

	/** *****************************************************************
	 * GolfAdmManiaChgDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public LimCshUpdDaoProc() {}
	
	/**
	 * 관리자 리무진할인신청 프로그램 수정 처리
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
			
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			   int idx = 0;
			   pstmt.setString(++idx, data.getString("NAME") );
			   pstmt.setString(++idx, data.getString("PRICE") );
			   pstmt.setString(++idx, data.getString("PRICE2") );
			   pstmt.setString(++idx, data.getString("PRICE3") );
			   pstmt.setString(++idx, data.getString("NO") );
			   
			   
			 				
				
				
			   
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			
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
	
	//9개추가됨
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGAMTMGMT SET	\n");
		sql.append("\t  CAR_KND_NM=?, 	\n");
		sql.append("\t  NORM_PRIC=?, 	\n");
		sql.append("\t  PCT20_DC_PRIC=?, 	\n");
		sql.append("\t  PCT30_DC_PRIC=? 	\n");
		//sql.append("\t  CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD') 	\n");
		sql.append("\t WHERE SEQ_NO=?	\n");
        return sql.toString();
    }
    
}
