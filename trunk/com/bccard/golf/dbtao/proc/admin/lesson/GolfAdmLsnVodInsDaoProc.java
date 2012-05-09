/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLsnVodInsDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨동영상 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lesson;

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
public class GolfAdmLsnVodInsDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 레슨동영상 등록 처리";

	/** *****************************************************************
	 * GolfAdmLsnVodInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLsnVodInsDaoProc() {}
	
	/**
	 * 관리자 레슨동영상 등록 처리
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

			sql = this.getNextValQuery(); //동영상일련번호 쿼리
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			String seq_no = "";
			if(rs.next()){
				seq_no = rs.getString("GOLF_MVPT_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
            String preYn = data.getString("PMI_MVPT_YN");	// 프리미엄동영상구분 (Y:프리미엄동영상, N:일반동영상)
            if(preYn == null || preYn.equals("")){
            	preYn = "N";
            }
             
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, seq_no );
			pstmt.setString(++idx, data.getString("VOD_CLSS") ); 			
			pstmt.setString(++idx, data.getString("VOD_LSN_CLSS") ); 
			pstmt.setString(++idx, data.getString("VOD_NM") ); 
			pstmt.setString(++idx, data.getString("IMG_NM") );			
			pstmt.setString(++idx, data.getString("TITL") );	
			pstmt.setString(++idx, data.getString("CTNT") );
			pstmt.setString(++idx, data.getString("BEST_YN") );
			pstmt.setString(++idx, data.getString("NEW_YN") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setString(++idx, preYn );
			
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
		sql.append("INSERT INTO BCDBA.TBGMVPTMGMT (	\n");
		sql.append("\t  GOLF_MVPT_SEQ_NO, GOLF_MVPT_CLSS, GOLF_MVPT_LESN_CLSS, MVPT_ANNX_FILE_PATH, ANNX_IMG, TITL, CTNT, BEST_YN, ANW_BLTN_ARTC_YN, INQR_NUM, 	\n");
		sql.append("\t  REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON, PMI_MVPT_YN 	\n");	// PMI_MVPT_YN 추가 - 2009.12.04
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,?,?,?,?,0,	\n");
		sql.append("\t  ?,NULL,TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),NULL,?	\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(GOLF_MVPT_SEQ_NO),0)+1 GOLF_MVPT_SEQ_NO FROM BCDBA.TBGMVPTMGMT \n");
		return sql.toString();
    }
}
