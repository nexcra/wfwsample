/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntBkRecvIns9DaoProc
*   작성자    :
*   내용      : 프리미엄 부킹 이벤트 신청 처리
*   적용범위  : golf
*   작성일자  : 2009-08-08
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
* @author	
* @version	1.0
******************************************************************************/
public class GolfEvntBkRecvIns9DaoProc extends AbstractProc {

	public static final String TITLE = "9월 VIP 부킹 이벤트 신청 처리";

	/** *****************************************************************
	 * GolfEvntBkRecvIns9DaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntBkRecvIns9DaoProc() {}
	
	/**
	 * 9월 VIP 부킹 이벤트 신청 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet dataSet) throws DbTaoException  {
		
		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);
			// 신청 중복 체크 
			int recvOverLapChk = this.getRecvOverLapChk(conn, dataSet);
			
			String golf_svc_aplc_clss = dataSet.getString("GOLF_SVC_APLC_CLSS");		
			String cdhd_id = dataSet.getString("CDHD_ID");
			String co_nm = dataSet.getString("CO_NM");
			String email = dataSet.getString("EMAIL");
			String hp_ddd_no = dataSet.getString("HP_DDD_NO");
			String hp_tel_hno = dataSet.getString("HP_TEL_HNO");
			String hp_tel_sno = dataSet.getString("HP_TEL_SNO");
			String teof_date = dataSet.getString("TEOF_DATE");
			String teof_time = dataSet.getString("TEOF_TIME");
			String green_nm = dataSet.getString("GREEN_NM");
			String memo_expl = dataSet.getString("MEMO_EXPL");
			String lesn_seq_no = dataSet.getString("LESN_SEQ_NO");




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
				pstmt.setString(++idx, golf_svc_aplc_clss );
				pstmt.setString(++idx, lesn_seq_no );
				pstmt.setString(++idx, cdhd_id );			
				pstmt.setString(++idx, co_nm );	
				pstmt.setString(++idx, email );

				pstmt.setString(++idx, hp_ddd_no );
				pstmt.setString(++idx, hp_tel_hno );
				pstmt.setString(++idx, hp_tel_sno );
				pstmt.setString(++idx, teof_date );
				pstmt.setString(++idx, teof_time );
				pstmt.setString(++idx, green_nm );
				pstmt.setString(++idx, memo_expl );
				
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
			e.printStackTrace();
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
			pstmt.setString(++idx, data.getString("GOLF_SVC_APLC_CLSS") );	
			pstmt.setString(++idx, data.getString("LESN_SEQ_NO") );
			pstmt.setString(++idx, data.getString("CDHD_ID") );
			pstmt.setString(++idx, data.getString("TEOF_DATE") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result++;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}

		}
		
		return result;
	}	
	

	public DbTaoResult getResult(WaContext context, TaoDataSet data) throws DbTaoException {
		
		//String title = data.getString("TITLE");
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		
		DbTaoResult  result =  new DbTaoResult(TITLE);
		Connection conn = null;

		try {
			conn = context.getDbConnection("default", null);

			sql = this.getSelectAplQuery();//Select Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("GOLF_SVC_APLC_CLSS") );	
			pstmt.setString(++idx, data.getString("LESN_SEQ_NO") );
			pstmt.setString(++idx, data.getString("CDHD_ID") );
			pstmt.setString(++idx, data.getString("TEOF_DATE") );
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				result.addString("CO_NM" 			,rs.getString("CO_NM") );
				result.addString("TEOF_DATE" 		,rs.getString("TEOF_DATE") );
				result.addString("TEOF_TIME" 		,rs.getString("TEOF_TIME") );
				result.addString("GREEN_NM" 		,rs.getString("GREEN_NM") );

				result.addString("RESULT", "00"); //정상결과
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			
		} catch(Exception e) {
			e.printStackTrace();
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
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
		sql.append("INSERT INTO BCDBA.TBGAPLCMGMT (	\n");
		sql.append("\t  APLC_SEQ_NO, GOLF_SVC_APLC_CLSS,LESN_SEQ_NO, PRZ_WIN_YN, CDHD_ID, CO_NM, EMAIL,	\n");
		sql.append("\t  HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, TEOF_DATE, TEOF_TIME, GREEN_NM, MEMO_EXPL, REG_ATON	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ? ,?, ?,'N',?, ?, ?,	\n");
		sql.append("\t 	?,?,?,?,?,?,?, TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS') \n");	
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
		sql.append("\t AND TEOF_DATE = ?	\n");
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
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
	private String getSelectAplQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("SELECT	\n");
		sql.append("\t 	CO_NM,TO_CHAR(TO_DATE(TEOF_DATE,'YYYYMMDD'),'YYYY-MM-DD') TEOF_DATE, TO_NUMBER(SUBSTR(TEOF_TIME,0,2)) TEOF_TIME, GREEN_NM 	\n");
		sql.append("\t FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t WHERE GOLF_SVC_APLC_CLSS = ?	\n");
		sql.append("\t AND LESN_SEQ_NO = ?	\n");
		sql.append("\t AND CDHD_ID = ?	\n");
		sql.append("\t AND TEOF_DATE = ?	\n");
        return sql.toString();
    }


}
