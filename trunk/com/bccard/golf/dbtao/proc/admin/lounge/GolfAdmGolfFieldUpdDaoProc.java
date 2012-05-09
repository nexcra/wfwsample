/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGolfFieldUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 골프장 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lounge;

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
public class GolfAdmGolfFieldUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 골프장 수정 처리";

	/** *****************************************************************
	 * GolfAdmGolfFieldUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmGolfFieldUpdDaoProc() {}
	
	/**
	 * 관리자 골프장 수정 처리
	 * @param conn
	 * @param data
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {
		
		int result1 = 0;
		int result2 = 0;
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
            
			sql = this.getInsertQuery1(img_nm, map_nm);//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			if (!GolfUtil.isNull(img_nm))	pstmt.setString(++idx, img_nm);
			if (!GolfUtil.isNull(map_nm)) pstmt.setString(++idx, map_nm);	
			pstmt.setString(++idx, data.getString("GF_NM") ); 			
			pstmt.setString(++idx, data.getString("CP_NM") ); 
			pstmt.setString(++idx, data.getString("GF_CLSS_CD") ); 
			pstmt.setString(++idx, data.getString("GF_HOLE_CD") );
			pstmt.setString(++idx, data.getString("GF_AREA_CD") );
			pstmt.setString(++idx, data.getString("URL") );
			pstmt.setString(++idx, data.getString("OPEN_DATE") );
			pstmt.setString(++idx, data.getString("WEATH_CD") );
			pstmt.setString(++idx, data.getString("ZIPCODE") );
			pstmt.setString(++idx, data.getString("ZIPADDR") );
			pstmt.setString(++idx, data.getString("DETAILADDR") );
			pstmt.setString(++idx, data.getString("CHG_DDD_NO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_HNO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_SNO") );
			pstmt.setString(++idx, data.getString("FX_DDD_NO") );
			pstmt.setString(++idx, data.getString("FX_TEL_HNO") );
			pstmt.setString(++idx, data.getString("FX_TEL_SNO") );
			pstmt.setString(++idx, data.getString("GF_SEARCH") );
			pstmt.setString(++idx, data.getString("SUBF") );
			pstmt.setString(++idx, data.getString("TITL") );
			pstmt.setString(++idx, data.getString("CTNT") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setString(++idx, data.getString("ADDR_CLSS") );
			pstmt.setLong(++idx, data.getLong("GF_SEQ_NO") );
			
			result1 = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            sql = this.getInsertQuery2();//Insert Query
			pstmt = conn.prepareStatement(sql);
            
			idx = 0;
			pstmt.setString(++idx, data.getString("RSV_DDD_NO") ); 			
			pstmt.setString(++idx, data.getString("RSV_TEL_HNO") ); 
			pstmt.setString(++idx, data.getString("RSV_TEL_SNO") ); 
			pstmt.setString(++idx, data.getString("MB_DAY") );
			pstmt.setString(++idx, data.getString("SLS_END_DAY") );
			pstmt.setString(++idx, data.getString("CADDIE_SYS") );
			pstmt.setString(++idx, data.getString("CART_SYS") );
			pstmt.setString(++idx, data.getString("MB_DAY_RSVT") );
			pstmt.setString(++idx, data.getString("NMB_DAY_RSVT") );
			pstmt.setString(++idx, data.getString("WKEND_MB_RSVT") );
			pstmt.setString(++idx, data.getString("WKEND_NMB_RSVT") );
			pstmt.setString(++idx, data.getString("WK_MB_RSVT") );
			pstmt.setString(++idx, data.getString("WK_NMB_RSVT") );
			
			pstmt.setLong(++idx, data.getLong("GRNFEE_WK_MB_AMT") ); 
			pstmt.setLong(++idx, data.getLong("GRNFEE_WK_NMB_AMT") );
			pstmt.setLong(++idx, data.getLong("GRNFEE_WK_WMB_AMT") );
			pstmt.setLong(++idx, data.getLong("GRNFEE_WK_FMB_AMT") );
			pstmt.setLong(++idx, data.getLong("GRNFEE_WKEND_MB_AMT") );
			pstmt.setLong(++idx, data.getLong("GRNFEE_WKEND_NMB_AMT") );
			pstmt.setLong(++idx, data.getLong("GRNFEE_WKEND_WMB_AMT") );
			pstmt.setLong(++idx, data.getLong("GRNFEE_WKEND_FMB_AMT") );
			
			pstmt.setString(++idx, data.getString("CADDIE_MB_AMT") );
			pstmt.setString(++idx, data.getString("CADDIE_NMB_AMT") );
			pstmt.setString(++idx, data.getString("CADDIE_WMB_AMT") );
			pstmt.setString(++idx, data.getString("CADDIE_FMB_AMT") );
			pstmt.setString(++idx, data.getString("CART_MB_AMT") );
			pstmt.setString(++idx, data.getString("CART_NMB_AMT") );
			pstmt.setString(++idx, data.getString("CART_WMB_AMT") );
			pstmt.setString(++idx, data.getString("CART_FMB_AMT") );
			
			pstmt.setLong(++idx, data.getLong("GF_SEQ_NO") );
			
			result2 = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            /*****************************************************************************/
			/*
			sql = this.getSelectForUpdateQuery();//일련번호 쿼리
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, data.getLong("GF_SEQ_NO"));
            rs = pstmt.executeQuery();

			if(rs.next()) {
				java.sql.Clob clob = rs.getClob("CTNT");
                writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
				reader = new CharArrayReader(data.getString("CTNT").toCharArray());
				
				char[] buffer = new char[1024];
				int read = 0;
				while ((read = reader.read(buffer,0,1024)) != -1) {
					writer.write(buffer,0,read);
				}
				writer.flush();
                writer.close();
			}
			if (rs  != null) rs.close();
			if (pstmt != null) pstmt.close();
			*/
			if(result1 > 0 && result2 > 0) {
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

		return result1;
	}
	
	
    /** ***********************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery1(String img_nm, String map_nm){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGAFFIGREEN SET	\n");
		if (!GolfUtil.isNull(img_nm)) sql.append("\t 	 ANNX_IMG=?,	");
		if (!GolfUtil.isNull(map_nm)) sql.append("\t 	 OLM_IMG=?,	");
		sql.append("\t  	GREEN_NM=?, CO_NM=?, GREEN_CLSS=?, GREEN_ODNO_CODE=?, GREEN_RGN_CLSS=?, GREEN_HPGE_URL=?, OMK_DATE=?, GREEN_WEATH_CLSS=?, \n");	
		sql.append("\t	 	ZP=?, ADDR=?, DTL_ADDR=?, DDD_NO=?, TEL_HNO=?, TEL_SNO=?, FAX_DDD_NO=?, FAX_TEL_HNO=?, FAX_TEL_SNO=?,   	\n");		
		sql.append("\t	 	POS_EXPL=?, INCI_FACI_CTNT=?, TITL=?, CTNT=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), NW_OLD_ADDR_CLSS=? \n");
		sql.append("\t 	WHERE AFFI_GREEN_SEQ_NO = ?	\n");
		sql.append("\t AND AFFI_FIRM_CLSS = '0004'	");	
        return sql.toString();
    }
    
    private String getInsertQuery2(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGAFFIGREENROUNDINFO SET	\n");
		sql.append("\t  	RSVT_DDD_NO=?, RSVT_TEL_HNO=?, RSVT_TEL_SNO=?, CDHD_SVC_DAY_INFO=?, GREEN_RESM_DAY_INFO=?, CADY_SYS_INFO=?, CART_SYS_INFO=?, CDHD_SVC_DAY_CDHD_RSVT_INFO=?, CDHD_SVC_DAY_NONCDHD_RSVT_INFO=?,   	\n");
		sql.append("\t 	 	WKE_CDHD_RSVT_INFO=?, WKE_NON_CDHD_RSVT_INFO=?, WKD_CDHD_RSVT_INFO=?, WKD_NON_CDHD_RSVT_INFO=?, GREEN_WKD_CDHD_CHRG=?, GREEN_WKD_NON_CDHD_CHRG=?, GREEN_WKD_WKD_CDHD_CHRG=?, GREEN_WKD_FMLY_CDHD_CHRG=?, GREEN_WKE_CDHD_CHRG=?, GREEN_WKE_NON_CDHD_CHRG=?, 	\n");	
		sql.append("\t  	GREEN_WKE_WKD_CDHD_CHRG=?, GREEN_WKE_FMLY_CDHD_CHRG=?, CADY_CDHD_CHRG_INFO=?, CADY_NON_CDHD_CHRG_INFO=?, CADY_WKD_CDHD_CHRG_INFO=?, CADY_FMLY_CDHD_CHRG_INFO=?, CART_CDHD_CHRG_INFO=?, CART_NON_CDHD_CHRG_INFO=?, CART_WKD_CDHD_CHRG_INFO=?, CART_FMLY_CDHD_CHRG_INFO=?		\n");
		sql.append("\t 	WHERE AFFI_GREEN_SEQ_NO = ?	\n");
        return sql.toString();
    }
    
	/** ***********************************************************************
     * CLOB Query를 생성하여 리턴한다.    
     ************************************************************************ */
    /*
    private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT CTNT FROM BCDBA.TBGAFFIGREEN \n");
         sql.append("WHERE AFFI_GREEN_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
	*/
}
