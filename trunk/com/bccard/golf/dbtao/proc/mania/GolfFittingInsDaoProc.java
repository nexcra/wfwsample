/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaRegActn
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 등록처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mania;

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
* Topn
* @author (주)만세커뮤니케이션
* @version 1.0
******************************************************************************/
public class GolfFittingInsDaoProc extends AbstractProc {

 public static final String TITLE = "관리자 골프장리무진할인신청관리 등록처리";

 /** *****************************************************************
  * GolfAdmManiaInsDaoProc 프로세스 생성자
  * @param N/A
  ***************************************************************** */
 public GolfFittingInsDaoProc() {}
 
 /**
  * 관리자 레슨프로그램 등록 처리
  * @param conn
  * @param data
  * @return
  * @throws DbTaoException
  * 
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

		sql = this.getNextValQuery(); //레슨번호 쿼리
        pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, data.getString("APLC_PE_CLSS"));
        rs = pstmt.executeQuery();			
		String lsn_seq_no = "";
		if(rs.next()){
			lsn_seq_no = rs.getString("GOLF_SVC_RSVT_NO");
		}
		if(rs != null) rs.close();
        if(pstmt != null) pstmt.close();
      /*****************************************************************************/
      
		sql = this.getInsertQuery();//Insert Query
		pstmt = conn.prepareStatement(sql);
   
   int idx = 0;
   pstmt.setString(++idx, data.getString("APLC_PE_CLSS")+lsn_seq_no );
   pstmt.setString(++idx, data.getString("APLC_PE_CLSS") ); 
   pstmt.setString(++idx, data.getString("ID") );
   
   
   pstmt.setString(++idx, data.getString("HP_DDD_NO") );
   pstmt.setString(++idx, data.getString("HP_TEL_HNO") );
   pstmt.setString(++idx, data.getString("HP_TEL_SNO") );
   
   pstmt.setString(++idx, data.getString("EMAIL_ID") ); 
   pstmt.setString(++idx, data.getString("NOTE") ); 
   
   pstmt.setString(++idx, data.getString("PIC_DATE") ); 
   pstmt.setString(++idx, data.getString("PIC_TIME") );
   
   pstmt.setString(++idx, data.getString("CKD_CODE") );
   
   pstmt.setString(++idx, data.getString("GCC_NM") ); //제목
   pstmt.setString(++idx, data.getString("MEMO") );

   pstmt.setString(++idx, data.getString("ADMIN_NO") );

   result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();

   if(result > 0) {
    conn.commit();
   } else {
    conn.rollback();
   }
   
  } catch(Exception e) {
   try {
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
    private String getInsertQuery(){
        StringBuffer sql = new StringBuffer();
  sql.append("\n");
  sql.append("INSERT INTO BCDBA.TBGRSVTMGMT ( \n");
  sql.append("\t  GOLF_SVC_RSVT_NO, GOLF_SVC_RSVT_MAX_VAL, CDHD_ID, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,  \n");
  sql.append("\t  EMAIL, NOTE_MTTR_EXPL, ROUND_HOPE_DATE, ROUND_HOPE_TIME, FIT_HOPE_CLUB_CLSS, TITL, CTNT, REG_ATON, REG_MGR_ID \n");
  
  sql.append("\t ) VALUES ( \n"); 
  
  sql.append("\t  ?,?,?,?,?,?,?,?,?,?, \n");
  sql.append("\t  ?,?,?,TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'),? \n");
  sql.append("\t \n)"); 
        return sql.toString();
    }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT LPAD(TO_NUMBER(NVL(MAX(SUBSTR(GOLF_SVC_RSVT_NO,6,7)),0))+1,7,'0') GOLF_SVC_RSVT_NO FROM BCDBA.TBGRSVTMGMT \n");
        sql.append("WHERE GOLF_SVC_RSVT_MAX_VAL = ? \n");
		return sql.toString();
    }
    
}

