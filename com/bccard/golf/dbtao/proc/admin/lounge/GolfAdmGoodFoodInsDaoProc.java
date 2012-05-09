/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGoodFoodInsDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 맛집 등록 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28	
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lounge;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
public class GolfAdmGoodFoodInsDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 맛집 등록 처리";

	/** *****************************************************************
	 * GolfAdmGoodFoodInsDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmGoodFoodInsDaoProc() {}
	
	/**
	 * 관리자 맛집 등록 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		int result = 0;
		int iCount = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);		

			sql = this.getNextValQuery(); //번호 쿼리
            pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();			
			long seq_no = 0L;
			if(rs.next()){
				seq_no = rs.getLong("FD_SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getInsertQuery1();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setLong(++idx, seq_no );
			pstmt.setString(++idx, data.getString("FD_NM") ); 			
			pstmt.setString(++idx, data.getString("FD_AREA_CD") ); 
			pstmt.setString(++idx, data.getString("FD1_LEV_CD") ); 
			pstmt.setString(++idx, data.getString("FD2_LEV_CD") );
			pstmt.setString(++idx, data.getString("FD3_LEV_CD") );
			pstmt.setString(++idx, data.getString("IMG_NM") );
			pstmt.setString(++idx, data.getString("BEST_YN") );
			pstmt.setString(++idx, data.getString("NEW_YN") );
			pstmt.setString(++idx, data.getString("ZIPCODE") );
			pstmt.setString(++idx, data.getString("ZIPADDR") );
			pstmt.setString(++idx, data.getString("DETAILADDR") );
			pstmt.setString(++idx, data.getString("CHG_DDD_NO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_HNO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_SNO") );
			pstmt.setString(++idx, data.getString("ROAD_SRCH") );
			pstmt.setString(++idx, data.getString("MAP_NM") );
			pstmt.setString(++idx, data.getString("PARKING_YN") );
			pstmt.setString(++idx, data.getString("SLS_STRT_TIME") );
			pstmt.setString(++idx, data.getString("SLS_END_TIME") );
			pstmt.setString(++idx, data.getString("SLS_END_DAY") );
			pstmt.setString(++idx, data.getString("URL") );
			pstmt.setString(++idx, data.getString("FD_MENU") );
			pstmt.setString(++idx, data.getString("CTNT") );
			pstmt.setString(++idx, data.getString("REGION_GREEN_NM") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );

			pstmt.setString(++idx, data.getString("MAIN_BANNER_IMG") );
			pstmt.setString(++idx, data.getString("MAIN_BANNER_URL") );
			pstmt.setString(++idx, data.getString("MAIN_EPS_YN") );
			pstmt.setString(++idx, data.getString("MAIN_RPRS_IMG") );
			pstmt.setString(++idx, data.getString("MAIN_RPRS_IMG_URL") );
			pstmt.setString(++idx, data.getString("MAIN_RPRS_IMG_EPS_YN") );
			pstmt.setString(++idx, data.getString("ADDR_CLSS") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            /*****************************************************************************/
            String[] gf_seq_no = data.getString("GF_SEQ_NO").split(",");
            
            for (int i = 0; i < gf_seq_no.length; i++) {				
				if (gf_seq_no[i] != null && gf_seq_no[i].length() > 0) {
		            
		            sql = this.getInsertQuery2();//Insert Query
					pstmt = conn.prepareStatement(sql);
					
					idx = 0;
					pstmt.setLong(++idx, seq_no );
					pstmt.setLong(++idx, Integer.parseInt(gf_seq_no[i]) );
					
					iCount += pstmt.executeUpdate();
					
			    }
            }
            
            if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();

			if(iCount > 0) {
				if(result > 0 || iCount == gf_seq_no.length) {
					conn.commit();
				} else {
					conn.rollback();
				}
			} else {
				if(result > 0) {
					conn.commit();
				} else {
					conn.rollback();
				}
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
    private String getInsertQuery1(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGAFFIETHS (	\n");
		sql.append("\t  AFFI_ETHS_SEQ_NO, ETHS_NM, ETHS_RGN_CODE, FOOD_SQ1_CTGO, FOOD_SQ2_CTGO, FOOD_SQ3_CTGO, ETHS_IMG, BEST_YN, ANW_BLTN_ARTC_YN, ZP,  \n");	
		sql.append("\t	 ADDR, DTL_ADDR, DDD_NO, TEL_HNO, TEL_SNO, POS_EXPL, OLM_IMG, PARK_ABLE_YN, SLS_STRT_TIME, SLS_END_TIME, \n");
		sql.append("\t	 HDAY_INFO, WEB_SITE_URL, ETHS_MAI_MENU_EXPL, CTNT, NGHB_GREEN_EXPL, REG_MGR_ID, CHNG_MGR_ID, REG_ATON, CHNG_ATON,  	\n");
		sql.append("\t	 MAIN_BANNER_IMG, MAIN_BANNER_URL, MAIN_EPS_YN, MAIN_RPRS_IMG, MAIN_RPRS_IMG_URL, MAIN_RPRS_IMG_EPS_YN, NW_OLD_ADDR_CLSS 	\n");
		sql.append("\t ) VALUES (	\n");
		sql.append("\t  ?,?,?,?,?,?,?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,?,?,?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,?,?,0,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),NULL,	\n");
		sql.append("\t  ?,?,?,?,?,?,?		\n");
		sql.append("\t \n)");	
        return sql.toString();
    }
    
    private String getInsertQuery2(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("INSERT INTO BCDBA.TBGETHSNGHBGREEN (	\n");
		sql.append("\t  AFFI_ETHS_SEQ_NO, AFFI_GREEN_SEQ_NO  	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?		\n");
		sql.append("\t \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(AFFI_ETHS_SEQ_NO),0)+1 FD_SEQ_NO FROM BCDBA.TBGAFFIETHS \n");
		return sql.toString();
    }
    
    /** ***********************************************************************
     * CLOB Query를 생성하여 리턴한다.    
     ************************************************************************ */
    /* 
    private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT CTNT FROM BCDBA.TBGFFD \n");
         sql.append("WHERE FD_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
  	*/
    
}
