/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmManiaChgDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ����ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.club;

import java.sql.Connection; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;

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
public class GolfAdmClubMutiUpdDaoProc extends AbstractProc {

 public static final String TITLE = "������ �����帮�������ν�û���� ����ó��";

 /** *****************************************************************
  * GolfAdmManiaChgDaoProc ���μ��� ������
  * @param N/A
  ***************************************************************** */
 public GolfAdmClubMutiUpdDaoProc() {}
 
 /**
  * ������ ���������ν�û ���α׷� ���� ó��
  * @param conn
  * @param data
  * @return
  * @throws DbTaoException
  */
 public int execute(WaContext context, TaoDataSet data, String[] seq_no) throws DbTaoException  {
  
	 int result = 0;
	 Connection conn = null;
  PreparedStatement pstmt = null;  
  String sql = ""; 
  ResultSet rs = null;
  int iCount = 0;
    
  try {
   conn = context.getDbConnection("default", null); 
   conn.setAutoCommit(false);
   
     
   // �Խ��ǰ��� ��ȣ ����
   sql = this.getNextValQuery(); //�Ϸù�ȣ
   pstmt = conn.prepareStatement(sql);
   rs = pstmt.executeQuery();			
	String bbrd_seq_no = "";
	if(rs.next()){
		bbrd_seq_no = rs.getString("BBRD_SEQ_NO");
	}
	if(rs != null) rs.close();
   if(pstmt != null) pstmt.close();
   
   
   
   // Ű�������� ��������/���/�Ϲݼ��� ���� ---------------------------------------------
	String prize_yn  = data.getString("PRIZE_YN");   
     // ���õ� üũ�ڽ��� ����(seq_no.length)��ŭ ó���Ѵ�. 
	for (int i = 0; i < seq_no.length; i++) {   
    	 
		if (seq_no[i] != null && seq_no[i].length() > 0) {
  
				
			
				int idx = 0;
   
				if (!GolfUtil.isNull(prize_yn))  {
					sql = this.getInsertQuery(prize_yn);//Insert Query
					pstmt = conn.prepareStatement(sql);   

					pstmt.setString(++idx, data.getString("PRIZE_YN")); //���࿩�� (����/���) ������
					pstmt.setString(++idx, data.getString("ADMIN_NO"));
					pstmt.setString(++idx, seq_no[i]);
					iCount += pstmt.executeUpdate();
					
					
					//int num = Integer.parseInt(bbrd_seq_no); 
					String snum = seq_no[i];
					String bbrdnm = "";
					String bbrdcd = "";

					int k=0;
					for (int j = 0; j < 4; j++) {
						
						sql = this.getInsertQuery2();//Insert Query
						pstmt = conn.prepareStatement(sql); 
						int idx1 = 0;
						
						if(j == 0){bbrdnm = "��ȣȸ ��������"; bbrdcd = "0001";}
						if(j == 1){bbrdnm = "�Խ���"; bbrdcd = "0002";}
						if(j == 2){bbrdnm = "�ڷ����"; bbrdcd = "0003";}
						if(j == 3){bbrdnm = "����Խ���"; bbrdcd = "0004";}
						
						//pstmt.setLong(++idx, num );		// �Ϸù�ȣ
						pstmt.setString(++idx1, bbrdcd);	// �Խ��� �ڵ��ȣ
						pstmt.setString(++idx1, snum); 		// ��ȣȸ��ȣ
						pstmt.setString(++idx1, bbrdnm); 	// �Խ��� �̸�
						pstmt.setLong(++idx1, ++k); 	// SORT_SEQ
						
						//num = num+1;
						result = pstmt.executeUpdate();
					
					}
				}
					
		}// end if
		
	}// end for
   
	
	
	
	
	
      // ó���Ǽ���ŭ iCount�� ������ ���� ó�� �Ȱ���.
   if(iCount == seq_no.length) {
    conn.commit();
   } else {// ���������� ������ 
    conn.rollback();
   } 
   debug("#######################################################################"); 
   debug("lessonInq.sihhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhze() ::> " + iCount); 
   debug("#######################################################################"); 
   debug("lessonInq.sihhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhze() ::> " + seq_no.length); 
    
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
     
	if(prize_yn.equals("Y")){
		sql.append("\t  , OPN_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
	}
		
     sql.append("\t WHERE CLUB_SEQ_NO=? \n");
     
     return sql.toString();
 }
 
 private String getInsertQuery2(){
     StringBuffer sql = new StringBuffer();
     sql.append("\n");
     sql.append("INSERT INTO BCDBA.TBGCLUBBBRDMGMT ( \n");
     sql.append("\t  BBRD_SEQ_NO, CLUB_BBRD_CLSS, CLUB_SEQ_NO, BBRD_INFO, REG_ATON, SORT_SEQ  \n");
     sql.append("\t ) ( \n"); 
     sql.append("\t SELECT \n");      
     sql.append("\t  NVL(MAX(BBRD_SEQ_NO),0)+1,?,?,?,TO_CHAR(SYSDATE,'YYYYMMDD')||TO_CHAR(SYSDATE,'HH24MISS'),?  \n");
     sql.append("\t FROM BCDBA.TBGCLUBBBRDMGMT  \n"); 
     sql.append("\t )  \n"); 

           return sql.toString();
       }
 
 
 /** ***********************************************************************
  * Max IDX Query�� �����Ͽ� �����Ѵ�.    
  ************************************************************************ */
  private String getNextValQuery(){
      StringBuffer sql = new StringBuffer();
      sql.append("SELECT NVL(MAX(BBRD_SEQ_NO),0)+1 BBRD_SEQ_NO FROM BCDBA.TBGCLUBBBRDMGMT		 \n");
		return sql.toString();
  }
    
}

