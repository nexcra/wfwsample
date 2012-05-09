/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 김태완
*   내용      : 공통게시판 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.bbs;

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
public class GolfBoardUpdDaoProc extends AbstractProc {

	public static final String TITLE = "공통게시판 수정 처리";

	/** *****************************************************************
	 * GolfBoardUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBoardUpdDaoProc() {}
	
	/**
	 * 공통게시판 등록 처리
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

			String file_nm		= data.getString("FILE_NM");
			String pic_nm		= data.getString("PIC_NM");
			
			sql = this.getUpdateQuery(file_nm, pic_nm);//Update Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			if (!GolfUtil.isNull(file_nm)) pstmt.setString(++idx, file_nm );
			if (!GolfUtil.isNull(pic_nm)) pstmt.setString(++idx, pic_nm );
			pstmt.setString(++idx, data.getString("FIELD_CD") ); 
			pstmt.setString(++idx, data.getString("CLSS_CD") ); 
			pstmt.setString(++idx, data.getString("SEC_CD") );			
			pstmt.setString(++idx, data.getString("TITL") );
			pstmt.setString(++idx, data.getString("EMAIL_ID") );

			pstmt.setString(++idx, data.getString("CHG_DDD_NO") ); 			
			pstmt.setString(++idx, data.getString("CHG_TEL_HNO") ); 
			pstmt.setString(++idx, data.getString("CHG_TEL_SNO") ); 
			pstmt.setString(++idx, data.getString("EPS_YN") );	
			pstmt.setString(++idx, data.getString("DEL_YN") );

			pstmt.setString(++idx, data.getString("REG_IP_ADDR") );
			pstmt.setString(++idx, data.getString("BEST_YN") );
			pstmt.setString(++idx, data.getString("NEW_YN") );
			pstmt.setString(++idx, data.getString("COOP_YN") );

			pstmt.setString(++idx, data.getString("SEQ_NO") );
			pstmt.setString(++idx, data.getString("BBS") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            /*****************************************************************************/
            
			sql = this.getSelectForUpdateQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, data.getString("SEQ_NO"));
            rs = pstmt.executeQuery();

//			if(rs.next()) {
//				java.sql.Clob clob = rs.getClob("CTNT");
//                //writer = ((oracle.sql.CLOB)clob).getCharacterOutputStream();
//                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//				reader = new CharArrayReader(data.getString("CTNT").toCharArray());
//				
//				char[] buffer = new char[1024];
//				int read = 0;
//				while ((read = reader.read(buffer,0,1024)) != -1) {
//					writer.write(buffer,0,read);
//				}
//				writer.flush();
//			}
			if (rs  != null) rs.close();
			
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
    private String getUpdateQuery(String file_nm, String pic_nm){
        StringBuffer sql = new StringBuffer();		
		sql.append("\n");
		sql.append("UPDATE BCDBA.TBGBBRD SET	\n");
		if (!GolfUtil.isNull(file_nm)) sql.append("\t  ANNX_FILE_NM=?,  	\n");
		if (!GolfUtil.isNull(pic_nm)) sql.append("\t  MVPT_ANNX_FILE_PATH=?,  	\n");
		sql.append("\t  GOLF_CLM_CLSS=?, GOLF_BOKG_FAQ_CLSS=?, GOLF_VBL_RULE_PREM_CLSS=?, TITL=?, CTNT=EMPTY_CLOB(), EMAIL=?,  	\n");
		sql.append("\t	DDD_NO=?, TEL_HNO=?, TEL_SNO=?, EPS_YN=?, DEL_YN=?, 	\n");
		sql.append("\t	CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'), REG_IP_ADDR=?, BEST_YN=?, ANW_BLTN_ARTC_YN=?, BC_AFFI_YN=?	\n");
		sql.append("\t WHERE BBRD_SEQ_NO=?	\n");
		sql.append("\t AND BBRD_CLSS=?	\n");
        return sql.toString();
    }
    
    
	/** ***********************************************************************
     * CLOB Query를 생성하여 리턴한다.    
     ************************************************************************ */
     private String getSelectForUpdateQuery(){
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT CTNT FROM BCDBA.TBGBBRD \n");
         sql.append("WHERE BBRD_SEQ_NO = ? \n");
         sql.append("FOR UPDATE \n");
 		return sql.toString();
     }
}
