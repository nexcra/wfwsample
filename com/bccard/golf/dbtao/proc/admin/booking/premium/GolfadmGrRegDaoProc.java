/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmGrRegDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 프리미엄부킹 골프장 등록 처리
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
* Topn
* @author	(주)미디어포스
* @version	1.0  
******************************************************************************/
public class GolfadmGrRegDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 프리미엄부킹 골프장 등록 처리";

	/** *****************************************************************
	 * GolfadmGrRegDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmGrRegDaoProc() {}
	
	/**
	 * 관리자 프리미엄부킹 골프장 등록 처리
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

            /**SEQ_NO 가져오기**************************************************************/
			sql = this.getNextValQuery(); 
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();			
			long max_SEQ_NO = 0L;
			if(rs.next()){
				max_SEQ_NO = rs.getLong("SEQ_NO");
			}
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            /**Insert************************************************************************/
            sql = this.getInsertQuery();
			pstmt = conn.prepareStatement(sql);
			//debug("pstmt => " + pstmt);
			
			int idx = 0;
        	pstmt.setLong(++idx, max_SEQ_NO );
        	pstmt.setString(++idx, data.getString("SORT") ); 
        	pstmt.setString(++idx, data.getString("GR_NM") ); 
        	pstmt.setString(++idx, data.getString("RL_GREEN_NM") ); 
        	pstmt.setString(++idx, data.getString("GR_ID") );
        	pstmt.setString(++idx, data.getString("BANNER") );
        	
        	pstmt.setString(++idx, data.getString("GR_URL") );
        	pstmt.setString(++idx, data.getString("GR_INFO") );
        	pstmt.setString(++idx, data.getString("GR_ADDR") );
        	pstmt.setString(++idx, data.getString("MAP_NM") );
        	pstmt.setString(++idx, data.getString("COURSE") );
        	
        	pstmt.setString(++idx, data.getString("SUBEQUIP") );
        	pstmt.setInt(++idx, data.getInt("DEL_LIMIT") );
        	pstmt.setInt(++idx, data.getInt("PER_LIMIT") );
        	pstmt.setString(++idx, data.getString("REG_MGR_SEQ_NO") );
        	        	
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
        	
        	pstmt.setString(++idx, data.getString("GR_NOTI") );
        	pstmt.setString(++idx, data.getString("MAX_ACPT_PNUM") );
        	
        	pstmt.setString(++idx, data.getString("MAIN_BANNER_IMG") );
        	pstmt.setString(++idx, data.getString("MAIN_EPS_YN") );
        	pstmt.setString(++idx, data.getString("CO_NM") );
        	
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
				String course_INFO	= data.getString("COURSE_INFO");
				if (course_INFO.length() > 0){
				
					sql = this.getSelectForUpdateQuery2();
					pstmt = conn.prepareStatement(sql);
					pstmt.setLong(1, max_SEQ_NO);
		            rs = pstmt.executeQuery();
		
//					if(rs.next()) {
//						java.sql.Clob clob = rs.getClob("COURSE_INFO");
//		                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//						reader = new CharArrayReader(course_INFO.toCharArray());
//						
//						char[] buffer = new char[1024];
//						int read = 0;
//						while ((read = reader.read(buffer,0,1024)) != -1) {
//							writer.write(buffer,0,read);
//						}
//						writer.flush();
//					}
					if (rs  != null) rs.close();
					if (pstmt != null) pstmt.close();
				}
				
				String ch_INFO	= data.getString("CH_INFO");
				if (ch_INFO.length() > 0){
				
					sql = this.getSelectForUpdateQuery3();
					pstmt = conn.prepareStatement(sql);
					pstmt.setLong(1, max_SEQ_NO);
		            rs = pstmt.executeQuery();
		
//					if(rs.next()) {
//						java.sql.Clob clob = rs.getClob("CH_INFO");
//		                writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
//						reader = new CharArrayReader(ch_INFO.toCharArray());
//						
//						char[] buffer = new char[1024];
//						int read = 0;
//						while ((read = reader.read(buffer,0,1024)) != -1) {
//							writer.write(buffer,0,read);
//						}
//						writer.flush();
//					}
					if (rs  != null) rs.close();
					if (pstmt != null) pstmt.close();
				}
            }
        	/**clob 처리**20100310 개발기에서는 내용등록안되도록 수정한다.*******************************************************************/
			
			
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
		sql.append("INSERT INTO BCDBA.TBGAFFIGREEN (	\n");
		sql.append("\t  AFFI_GREEN_SEQ_NO, AFFI_FIRM_CLSS, GREEN_NM, RL_GREEN_NM, GREEN_ID, BANNER_IMG,	\n");
		sql.append("\t  GREEN_HPGE_URL, GREEN_EXPL, DTL_ADDR, OLM_IMG, CURS_SCAL_INFO, 		\n");
		sql.append("\t  INCI_FACI_CTNT, BOKG_CNCL_ABLE_TRM, BOKG_TIME_COLL_TRM, REG_MGR_ID, \n");
		
		sql.append("\t  GALR_1_IMG_EXPL, GALR_1_IMG, GALR_2_IMG_EXPL, GALR_2_IMG, GALR_3_IMG_EXPL, GALR_3_IMG,	\n");
		sql.append("\t  GALR_4_IMG_EXPL, GALR_4_IMG, GALR_5_IMG_EXPL, GALR_5_IMG, GALR_6_IMG_EXPL, GALR_6_IMG,	\n");
		
		sql.append("\t  CAUT_MTTR_CTNT, CURS_INTD_CTNT, GREEN_CHRG_INFO, REG_ATON, MAX_ACPT_PNUM, 	\n");
		sql.append("\t  MAIN_BANNER_IMG, MAIN_EPS_YN, CO_NM 	\n");
		sql.append("\t ) VALUES (	\n");	
		sql.append("\t  ?,?,?,?,?,?, \n");
		sql.append("\t  ?,?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,?,?,	\n");
		sql.append("\t  ?,?,?,?,?,?,	\n");
		sql.append("\t  ?,EMPTY_CLOB(),EMPTY_CLOB(),TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?,		\n");
		sql.append("\t  ?,?,?	\n");
		sql.append("\t  \n)");	
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(AFFI_GREEN_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGAFFIGREEN \n");
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
