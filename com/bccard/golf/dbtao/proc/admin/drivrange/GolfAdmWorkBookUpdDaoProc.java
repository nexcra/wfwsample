/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmWorkBookUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 연습장 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

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
public class GolfAdmWorkBookUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 연습장 수정 처리";

	/** *****************************************************************
	 * GolfAdmWorkBookUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmWorkBookUpdDaoProc() {}
	
	/**
	 * 관리자 연습장 수정 처리
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
            
			sql = this.getInsertQuery(img_nm);//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			if (!GolfUtil.isNull(img_nm))	pstmt.setString(++idx, img_nm);
			pstmt.setString(++idx, data.getString("EXEC_TYPE_CD") ); 
			pstmt.setString(++idx, data.getString("GF_NM") ); 
			pstmt.setString(++idx, data.getString("ZIPCODE") );
			pstmt.setString(++idx, data.getString("ZIPADDR") );
			pstmt.setString(++idx, data.getString("DETAILADDR") );
			pstmt.setString(++idx, data.getString("CHG_DDD_NO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_HNO") );
			pstmt.setString(++idx, data.getString("CHG_TEL_SNO") );
			pstmt.setString(++idx, data.getString("URL") ); 
			pstmt.setString(++idx, data.getString("GF_SEARCH") ); 
			pstmt.setString(++idx, data.getString("MTTR") ); 
			pstmt.setLong(++idx, data.getLong("CUPN_SEQ_NO") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setString(++idx, data.getString("ADDR_CLSS") );
			pstmt.setLong(++idx, data.getLong("GF_SEQ_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
/*
			sql = this.getSelectForUpdateQuery();//일련번호 쿼리
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, data.getLong("SEQ_NO"));
            rs = pstmt.executeQuery();

			if(rs.next()) {
				java.sql.Clob clob = rs.getClob("CAUT_MTTR_CTNT");
                //writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
				reader = new CharArrayReader(data.getString("MTTR").toCharArray());
				
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
    private String getInsertQuery(String img_nm){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGAFFIGREEN SET	\n");
		if (!GolfUtil.isNull(img_nm)) sql.append("\t 	 ANNX_IMG=?,	");
		sql.append("\t  GOLF_RNG_CLSS=?, GREEN_NM=?, ZP=?, ADDR=?, DTL_ADDR=?, DDD_NO=?, TEL_HNO=?, TEL_SNO=?,   	\n");
		sql.append("\t  GREEN_HPGE_URL=?, POS_EXPL=?, CAUT_MTTR_CTNT=?, CUPN_SEQ_NO=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') , NW_OLD_ADDR_CLSS=?	\n");
		sql.append("\t WHERE AFFI_GREEN_SEQ_NO=?	\n");
		sql.append("\t 	AND AFFI_FIRM_CLSS = '0003'	");	
        return sql.toString();
    }
    
    
	/** ***********************************************************************
     * CLOB Query를 생성하여 리턴한다.    
     ************************************************************************ */
/*
    private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT CAUT_MTTR_CTNT FROM BCDBA.TBGAFFIGREEN \n");
         sql.append("WHERE AFFI_GREEN_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
*/
}
