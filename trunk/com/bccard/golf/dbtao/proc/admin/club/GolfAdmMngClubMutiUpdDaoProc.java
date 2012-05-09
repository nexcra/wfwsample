/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMngClubChgDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ ��ü��ȣȸ ���� ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.club;

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
* @author (��)����Ŀ�´����̼�
* @version 1.0
******************************************************************************/
public class GolfAdmMngClubMutiUpdDaoProc extends AbstractProc {

 public static final String TITLE = "������ �����帮�������ν�û���� ����ó��";

 /** *****************************************************************
  * GolfAdmManiaChgDaoProc ���μ��� ������
  * @param N/A
  ***************************************************************** */
 public GolfAdmMngClubMutiUpdDaoProc() {}
 
 /**
  * ������ ���������ν�û ���α׷� ���� ó��
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
   
     // Ű�������� ��������/���/�Ϲݼ��� ���� ---------------------------------------------
     String prize_yn  = data.getString("PRIZE_YN");   
    
     sql = this.getInsertQuery(prize_yn);//Insert Query
     pstmt = conn.prepareStatement(sql);   

     // ���õ� üũ�ڽ��� ����(seq_no.length)��ŭ ó���Ѵ�. 
     for (int i = 0; i < seq_no.length; i++) {    
      if (seq_no[i] != null && seq_no[i].length() > 0) {
  
       int idx = 0;
   
       if (!GolfUtil.isNull(prize_yn)) 
       pstmt.setString(++idx, data.getString("PRIZE_YN")); //���࿩�� (����/���) ������
       pstmt.setString(++idx, data.getString("ADMIN_NO"));
       pstmt.setString(++idx, seq_no[i]);
   
       iCount += pstmt.executeUpdate();
      }// end if
     }// end for
   
      // ó���Ǽ���ŭ iCount�� ������ ���� ó�� �Ȱ���.
   if(iCount == seq_no.length) {
    conn.commit();
   } else {// ���������� ������ 
    conn.rollback();
   } 
   
    
  } catch(Exception e) {
   try {
    conn.rollback();
   } catch (Exception c){}
   
   MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
   throw new DbTaoException(msgEtt,e);
  } finally {
   try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
   try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
  }   

  return iCount;
  
 }
 
 
    /*************************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getInsertQuery(String prize_yn){
        StringBuffer sql = new StringBuffer();
        sql.append("\n");
        sql.append("UPDATE BCDBA.TBGCLUBMGMT SET \n");
  
        sql.append("\t  CLUB_OPN_AUTH_YN=?,  \n");  //���࿩�� (����/���) ������
        sql.append("\t  CHNG_MGR_ID=?,  \n");
        sql.append("\t  CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDD')  \n");
        sql.append("\t WHERE CLUB_SEQ_NO=? \n");
        
        return sql.toString();
    }
    
}

