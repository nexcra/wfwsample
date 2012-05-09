/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBkRecvInsDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 프리미엄 부킹 이벤트 신청 처리
*   적용범위  : golf
*   작성일자  : 2009-06-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
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
public class GolfEvntGolsinInsDaoProc extends AbstractProc {

	public static final String TITLE = "골신 이벤트 신청 처리";

	/** *****************************************************************
	 * GolfEvntBkRecvInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntGolsinInsDaoProc() {}
	
	/**
	 * 프리미엄 부킹 이벤트 신청 처리 처리
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
			// 신청 중복 체크
			int recvOverLapChk = this.getRecvOverLapChk(conn, data);
			
			if (recvOverLapChk == 0) {
				conn.setAutoCommit(false);
	            /*****************************************************************************/
				
				sql = this.getNextValQuery(); //신청번호 쿼리
	            pstmt = conn.prepareStatement(sql);
	            rs = pstmt.executeQuery();			
				String aplc_seq_no = "";
				if(rs.next()){
					aplc_seq_no = rs.getString("APLC_SEQ_NO");
				}
				if(rs != null) rs.close();
	            if(pstmt != null) pstmt.close();
	            /*****************************************************************************/
	            
				sql = this.getInsertQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
				
				int idx = 0;
				pstmt.setString(++idx, aplc_seq_no );
				pstmt.setString(++idx, data.getString("LSN_TYPE_CD") );			
				pstmt.setLong(++idx, data.getLong("RECV_NO") );	
				pstmt.setString(++idx, data.getString("userId") );
				pstmt.setString(++idx, data.getString("mobile1") );
				pstmt.setString(++idx, data.getString("mobile2") );
				pstmt.setString(++idx, data.getString("mobile3") );
				pstmt.setString(++idx, data.getString("taNum") );
				
				result = pstmt.executeUpdate();
				
				if(result > 0) {
					conn.commit();
				} else {
					conn.rollback();
				}
			} else {
				result = 2;
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

	/**
	 * 신청 중복 체크
	 * @param context
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int getRecvOverLapChk(Connection conn, TaoDataSet data) throws DbTaoException {

		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";

		try {
			
			sql = this.getSelectQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("LSN_TYPE_CD") );			
			pstmt.setLong(++idx, data.getLong("RECV_NO") );	
			pstmt.setString(++idx, data.getString("userId") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
		} catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
	}	
	
	/**
	 * 프리미엄 부킹 이벤트 신청 일련번호
	 * @param conn 
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public String getMaxSeqNo(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		String aplc_seq_no = "";
		
		try {
			conn = context.getDbConnection("default", null);	
			
			sql = this.getMaxSeqNo(); //신청번호 쿼리
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
			if(rs.next()){
				aplc_seq_no = rs.getString("APLC_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
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

		return aplc_seq_no;
	}

    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGAPLCMGMT (	\n");
		sql.append("\t  APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, LESN_SEQ_NO, PRZ_WIN_YN, CDHD_ID,	\n");
		sql.append("\t  HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, REG_ATON, RIDG_PERS_NUM	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,'N',?,	\n");
		sql.append("\t 	?,?,?,TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'), ? \n");	
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT	\n");
		sql.append("\t 	APLC_SEQ_NO	\n");
		sql.append("\t FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t WHERE GOLF_SVC_APLC_CLSS = ?	\n");
		sql.append("\t AND LESN_SEQ_NO = ?	\n");
		sql.append("\t AND CDHD_ID = ?	\n");
        return sql.toString();
    }
	
    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NVL(MAX(APLC_SEQ_NO),0)+1 APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT \n");
		return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getMaxSeqNo(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT MAX(APLC_SEQ_NO) APLC_SEQ_NO FROM BCDBA.TBGAPLCMGMT \n");
		return sql.toString();
    }
}
