/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.io.Reader;
import java.io.Writer;
import java.io.CharArrayReader;
import java.net.InetAddress;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.AppConfig;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfadmGrUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 부킹 골프장 수정 처리";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmGrUpdDaoProc() {}
	
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
			
			//debug("seq -> " + data.getLong("SEQ_NO"));
		
			int idx = 0;
			pstmt.setString(++idx, data.getString("GR_NM") );
        	pstmt.setString(++idx, data.getString("RL_GREEN_NM") ); 
			pstmt.setString(++idx, data.getString("BANNER") );
			pstmt.setString(++idx, data.getString("GR_URL") );
			pstmt.setString(++idx, data.getString("GR_ADDR") );
			pstmt.setString(++idx, data.getString("MAP_NM") );
			
			pstmt.setString(++idx, data.getString("COURSE") );
			pstmt.setString(++idx, data.getString("SUBEQUIP") );
			pstmt.setInt(++idx, data.getInt("DEL_LIMIT") );
			pstmt.setInt(++idx, data.getInt("PER_LIMIT") );
			pstmt.setString(++idx, data.getString("CORR_MGR_SEQ_NO") );
			
			pstmt.setString(++idx, data.getString("PIC1_DESC") );
			pstmt.setString(++idx, data.getString("PIC1") );			
			pstmt.setString(++idx, data.getString("PIC2_DESC") );
			pstmt.setString(++idx, data.getString("PIC2") );
			pstmt.setString(++idx, data.getString("PIC3_DESC") );
			
			pstmt.setString(++idx, data.getString("PIC3") );			
			pstmt.setString(++idx, data.getString("PIC4_DESC") );
			pstmt.setString(++idx, data.getString("PIC4") );
			pstmt.setString(++idx, data.getString("PIC5_DESC") );
			pstmt.setString(++idx, data.getString("PIC5") );
			
			pstmt.setString(++idx, data.getString("PIC6_DESC") );
			pstmt.setString(++idx, data.getString("PIC6") );		
			pstmt.setString(++idx, data.getString("GR_INFO") );		
			pstmt.setString(++idx, data.getString("MAX_ACPT_PNUM") );		
			pstmt.setString(++idx, data.getString("GR_NOTI") );		
        	pstmt.setString(++idx, data.getString("MAIN_BANNER_IMG") );
        	pstmt.setString(++idx, data.getString("MAIN_EPS_YN") );
        	pstmt.setString(++idx, data.getString("CO_NM") );
			pstmt.setLong(++idx, data.getLong("SEQ_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();

        	/**clob 처리**20100310 개발기에서는 내용등록안되도록 수정한다.*******************************************************************/
            String serverip = "";  // 서버아이피
            String devip = "";	   // 개발기 ip 정보

            try {
            	serverip = InetAddress.getLocalHost().getHostAddress();
            } catch(Throwable t) {}

            try {
            	devip = AppConfig.getAppProperty("DV_WAS_1ST");
            } catch(Throwable t) {}
            
            if(!serverip.equals(devip)){
				sql = this.getSelectForUpdateQuery2();
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(1, data.getLong("SEQ_NO"));
	            rs = pstmt.executeQuery();
	
//				if(rs.next()) {
//					java.sql.Clob clob = rs.getClob("COURSE_INFO");
//	                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//					reader = new CharArrayReader(data.getString("COURSE_INFO").toCharArray());
//					
//					char[] buffer = new char[1024];
//					int read = 0;
//					while ((read = reader.read(buffer,0,1024)) != -1) {
//						writer.write(buffer,0,read);
//					}
//					writer.flush();
//				}
				if (rs  != null) rs.close();
				if (pstmt != null) pstmt.close();
				
			
				sql = this.getSelectForUpdateQuery3();
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(1, data.getLong("SEQ_NO"));
	            rs = pstmt.executeQuery();
	
//				if(rs.next()) {
//					java.sql.Clob clob = rs.getClob("CH_INFO");
//	                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//					reader = new CharArrayReader(data.getString("CH_INFO").toCharArray());
//					
//					char[] buffer = new char[1024];
//					int read = 0;
//					while ((read = reader.read(buffer,0,1024)) != -1) {
//						writer.write(buffer,0,read);
//					}
//					writer.flush();
//				}
				if (rs  != null) rs.close();
				if (pstmt != null) pstmt.close();
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
		sql.append("UPDATE BCDBA.TBGAFFIGREEN SET	\n");
		sql.append("\t  GREEN_NM=?, RL_GREEN_NM=?, BANNER_IMG=?, GREEN_HPGE_URL=?, DTL_ADDR=?, OLM_IMG=?, 						\n");
		sql.append("\t  CURS_SCAL_INFO=?, INCI_FACI_CTNT=?, BOKG_CNCL_ABLE_TRM=?, BOKG_TIME_COLL_TRM=?, CHNG_MGR_ID=?, 			\n");
		sql.append("\t  GALR_1_IMG_EXPL=?, GALR_1_IMG=?, GALR_2_IMG_EXPL=?, GALR_2_IMG=?, GALR_3_IMG_EXPL=?, 					\n");
		sql.append("\t  GALR_3_IMG=?, GALR_4_IMG_EXPL=?, GALR_4_IMG=?, GALR_5_IMG_EXPL=?, GALR_5_IMG=?,							\n");
		sql.append("\t  GALR_6_IMG_EXPL=?, GALR_6_IMG=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), GREEN_EXPL=?,  			\n");
		sql.append("\t  CURS_INTD_CTNT=EMPTY_CLOB(), GREEN_CHRG_INFO=EMPTY_CLOB(), MAX_ACPT_PNUM=?, CAUT_MTTR_CTNT=?,			\n");
		sql.append("\t  MAIN_BANNER_IMG=?, MAIN_EPS_YN=?, CO_NM=?			\n");
		sql.append("\t WHERE AFFI_GREEN_SEQ_NO=?	\n");
        return sql.toString();
    }
    
 	/** ***********************************************************************
      * CLOB Query를 생성하여 리턴한다.    
      ************************************************************************ */
      private String getSelectForUpdateQuery2(){
          StringBuffer sql = new StringBuffer();
          sql.append("SELECT CURS_INTD_CTNT AS COURSE_INFO FROM BCDBA.TBGAFFIGREEN \n");
          sql.append("WHERE AFFI_GREEN_SEQ_NO = ? \n");
          sql.append("FOR UPDATE \n");
  		return sql.toString();
      }
      
   	/** ***********************************************************************
        * CLOB Query를 생성하여 리턴한다.    
        ************************************************************************ */
        private String getSelectForUpdateQuery3(){
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT GREEN_CHRG_INFO AS CH_INFO FROM BCDBA.TBGAFFIGREEN \n");
            sql.append("WHERE AFFI_GREEN_SEQ_NO = ? \n");
            sql.append("FOR UPDATE \n");
    		return sql.toString();
        }
}
