/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmManiaChgDaoProc
*   작성자    : (주)만세커뮤니케이션 김인겸
*   내용      : 관리자 골프장리무진할인신청관리 수정처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.mania;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author (주)만세커뮤니케이션
* @version 1.0
******************************************************************************/
public class GolfAdmClbNtsMutiUpdDaoProc extends AbstractProc {

 public static final String TITLE = "관리자 골프장리무진할인신청관리 수정처리";

 /** *****************************************************************
  * GolfAdmManiaChgDaoProc 프로세스 생성자
  * @param N/A
  ***************************************************************** */
 public GolfAdmClbNtsMutiUpdDaoProc() {}
 
 /** 
  * 관리자 리무진할인신청 프로그램 수정 처리  
  * @param conn
  * @param data
  * @return
  * @throws DbTaoException
  */
 public int execute(WaContext context, TaoDataSet data, String[] seq_no) throws DbTaoException  {
  
  Connection conn = null;
  PreparedStatement pstmt = null;  
  String sql = "";  
  int iCount = 0;
    
  try {
   conn = context.getDbConnection("default", null); 
   conn.setAutoCommit(false);
   
     // 키값에따라 결제진행/취소/일반수정 구분 ---------------------------------------------
     String prize_yn  = data.getString("PRIZE_YN");   
    
     sql = this.getInsertQuery(prize_yn);//Insert Query
     pstmt = conn.prepareStatement(sql);   

     // 선택된 체크박스의 갯수(seq_no.length)만큼 처리한다. 


  
       int idx = 0;
       if (!GolfUtil.isNull(prize_yn)) 
       pstmt.setString(++idx, data.getString("PRIZE_YN")); //진행여부 (진행/취소) 수정시
       pstmt.setString(++idx, data.getString("ADMIN_NO"));
       pstmt.setString(++idx, data.getString("SUBKEY"));
   
       iCount += pstmt.executeUpdate();

   
      // 처리건수만큼 iCount가 같으면 정상 처리 된것임.

    conn.commit();

   
    
  } catch(Exception e) {
   try {
    conn.rollback();
   } catch (Exception c){}
   
   MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
   throw new DbTaoException(msgEtt,e);
  } finally {
   try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
   try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
  }   

  return iCount;
  
 }
 
 
    /*************************************************************************
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getInsertQuery(String prize_yn){
        StringBuffer sql = new StringBuffer();
        sql.append("\n");
        sql.append("UPDATE BCDBA.TBGBBRD SET \n");
  
        sql.append("\t  EPS_YN=?,  \n");  //진행여부 (진행/취소) 수정시
        sql.append("\t  CHNG_MGR_ID=?,  \n");
        sql.append("\t  CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')  \n");
        sql.append("\t WHERE BBRD_SEQ_NO=? \n");
        
        return sql.toString();
    }
    
}

