/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLessonChgDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 관리자 레슨프로그램 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmLessonUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 레슨프로그램 수정 처리";

	/** *****************************************************************
	 * GolfAdmLessonChgDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLessonUpdDaoProc() {}
	
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
		ResultSet rs = null;
		Writer writer = null;
		Reader reader = null;
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
            String img_nm = data.getString("IMG_NM");
            String map_nm = data.getString("MAP_NM");
            String main_banner_img = data.getString("MAIN_BANNER_IMG");
            
			sql = this.getInsertQuery(img_nm, map_nm, main_banner_img);//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			if (!GolfUtil.isNull(img_nm))	pstmt.setString(++idx, img_nm);
			if (!GolfUtil.isNull(map_nm))	pstmt.setString(++idx, map_nm);			
			pstmt.setString(++idx, data.getString("LSN_NM") ); 
			pstmt.setString(++idx, data.getString("EVNT_YN") );
			pstmt.setString(++idx, data.getString("LSN_PRD_CLSS") );
			pstmt.setString(++idx, data.getString("LSN_START_DT") );
			pstmt.setString(++idx, data.getString("LSN_END_DT") );
			pstmt.setString(++idx, data.getString("APLC_END_DT") );
			pstmt.setString(++idx, data.getString("LSN_PRD_INFO") );
			
			pstmt.setInt(++idx, data.getInt("LSN_STTL_CST") ); 
			pstmt.setInt(++idx, data.getInt("LSN_DC_CST") ); 
			pstmt.setString(++idx, data.getString("LSN_PL") ); 
			pstmt.setString(++idx, data.getString("CHG_DDD_NO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_HNO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_SNO") );
			pstmt.setString(++idx, data.getString("APLC_MTHD") );
			pstmt.setString(++idx, data.getString("APLC_LETE_NUM") );
			pstmt.setString(++idx, data.getString("LSN_INTD") );

			pstmt.setString(++idx, data.getString("COOP_CP_CD") );
			pstmt.setString(++idx, data.getString("COOP_RMRK") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setInt(++idx, data.getInt("LSN_DC_RT") );
			if (!GolfUtil.isNull(main_banner_img))	pstmt.setString(++idx, main_banner_img);
			pstmt.setString(++idx, data.getString("MAIN_BANNER_URL") );
			pstmt.setString(++idx, data.getString("MAIN_EPS_YN") );
			
			pstmt.setString(++idx, data.getString("LSN_SEQ_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
			
			sql = this.getSelectForUpdateQuery();//레슨번호 쿼리
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("LSN_SEQ_NO"));
            rs = pstmt.executeQuery();

//			if(rs.next()) {
//				java.sql.Clob clob = rs.getClob("LESN_CTNT");
//                //writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
//                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//				reader = new CharArrayReader(data.getString("LSN_CTNT").toCharArray()); 
//				
//				char[] buffer = new char[1024];
//				int read = 0;
//				while ((read = reader.read(buffer,0,1024)) != -1) {
//					writer.write(buffer,0,read);
//				}
//				writer.flush();
//			}
			
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
    private String getInsertQuery(String img_nm, String map_nm, String main_banner_img){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGLESNMGMT SET	\n");
		if (!GolfUtil.isNull(img_nm)) sql.append("\t 	 LESN_IMG=?,	");
		if (!GolfUtil.isNull(map_nm)) sql.append("\t 	 OLM_IMG=?,	");
		sql.append("\t  LESN_NM=?, EVNT_YN=?, LESN_TRM_INP_TP_CLSS=?, LESN_STRT_DATE=?, LESN_END_DATE=?, APLC_FIN_DATE=?, LESN_TRM_INFO=?, 	\n");
		sql.append("\t  LESN_NORM_COST=?, LESN_DC_COST=?, LESN_PL_INFO=?, DDD_NO=?, TEL_HNO=?, TEL_SNO=?, LESN_APLC_MTHD_INFO=?, APLC_LIMT_PERS_NUM=?, LESN_EXPL=?, 	\n");
		sql.append("\t  GOLF_LESN_AFFI_FIRM_CLSS=?, LESN_AFFI_EXPL=?, LESN_CTNT=EMPTY_CLOB(), CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'), LESN_DC_RT=?, 	\n");
		if (!GolfUtil.isNull(main_banner_img)) sql.append("\t 	 MAIN_BANNER_IMG=?,	");
		sql.append("\t  MAIN_BANNER_URL=?, MAIN_EPS_YN=?	\n");
		sql.append("\t WHERE LESN_SEQ_NO=?	\n");
        return sql.toString();
    }
    
	/** ***********************************************************************
     * CLOB Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT LESN_CTNT FROM BCDBA.TBGLESNMGMT \n");
         sql.append("WHERE LESN_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
}
