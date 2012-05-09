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
package com.bccard.golf.dbtao.proc.admin.mania;

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
public class LimCshInsDaoProc extends AbstractProc {

 public static final String TITLE = "골프장리무진할인신청관리 등록처리";

 /** *****************************************************************
  * GolfAdmManiaInsDaoProc 프로세스 생성자
  * @param N/A
  ***************************************************************** */
 public LimCshInsDaoProc() {}
 
 /**
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

		sql = this.getNextValQuery(); //
      pstmt = conn.prepareStatement(sql);
      rs = pstmt.executeQuery();			
		long recv_no = 0L;
		long recv_CODE = 0L;
		if(rs.next()){
			recv_no   = rs.getLong("SEQ_NO");
			recv_CODE = rs.getLong("CAR_KND_CLSS");
		}
		if(rs != null) rs.close();
      if(pstmt != null) pstmt.close();
      /*****************************************************************************/
      
		sql = this.getInsertQuery();//Insert Query
		pstmt = conn.prepareStatement(sql);
   
   int idx = 0;
   pstmt.setLong(++idx, recv_no );
   pstmt.setString(++idx, data.getString("NAME") ); 
   pstmt.setString(++idx, data.getString("PRICE") );
   pstmt.setString(++idx, data.getString("PRICE2") );
   pstmt.setString(++idx, data.getString("PRICE3") ); 
   if(recv_CODE < 10)pstmt.setString(++idx, "000"+recv_CODE ); 
   if(recv_CODE >= 10)pstmt.setString(++idx, "00"+recv_CODE ); 

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
sql.append("INSERT INTO BCDBA.TBGAMTMGMT ( \n");
sql.append("\t  SEQ_NO, CAR_KND_NM, NORM_PRIC, PCT20_DC_PRIC, PCT30_DC_PRIC, CAR_KND_CLSS \n");

sql.append("\t ) VALUES ( \n"); 

sql.append("\t  ?,?,?,?,?,? \n");
sql.append("\t \n)"); 
     return sql.toString();
 }

    /** ***********************************************************************
    * Max IDX Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getNextValQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");
        sql.append("SELECT NVL(MAX(SEQ_NO),0)+1 SEQ_NO, NVL(MAX(SEQ_NO),0)+1 CAR_KND_CLSS FROM BCDBA.TBGAMTMGMT \n");
		return sql.toString();
    }
}