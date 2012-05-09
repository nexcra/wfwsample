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
* @author (주)만세커뮤니케이션 
* @version 1.0
******************************************************************************/
public class GolfAdmClubMutiUpdDaoProc extends AbstractProc {

 public static final String TITLE = "관리자 골프장리무진할인신청관리 수정처리";

 /** *****************************************************************
  * GolfAdmManiaChgDaoProc 프로세스 생성자
  * @param N/A
  ***************************************************************** */
 public GolfAdmClubMutiUpdDaoProc() {}
 
 /**
  * 관리자 리무진할인신청 프로그램 수정 처리
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
   
     
   // 게시판관리 번호 쿼리
   sql = this.getNextValQuery(); //일련번호
   pstmt = conn.prepareStatement(sql);
   rs = pstmt.executeQuery();			
	String bbrd_seq_no = "";
	if(rs.next()){
		bbrd_seq_no = rs.getString("BBRD_SEQ_NO");
	}
	if(rs != null) rs.close();
   if(pstmt != null) pstmt.close();
   
   
   
   // 키값에따라 결제진행/취소/일반수정 구분 ---------------------------------------------
	String prize_yn  = data.getString("PRIZE_YN");   
     // 선택된 체크박스의 갯수(seq_no.length)만큼 처리한다. 
	for (int i = 0; i < seq_no.length; i++) {   
    	 
		if (seq_no[i] != null && seq_no[i].length() > 0) {
  
				
			
				int idx = 0;
   
				if (!GolfUtil.isNull(prize_yn))  {
					sql = this.getInsertQuery(prize_yn);//Insert Query
					pstmt = conn.prepareStatement(sql);   

					pstmt.setString(++idx, data.getString("PRIZE_YN")); //진행여부 (진행/취소) 수정시
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
						
						if(j == 0){bbrdnm = "동호회 공지사항"; bbrdcd = "0001";}
						if(j == 1){bbrdnm = "게시판"; bbrdcd = "0002";}
						if(j == 2){bbrdnm = "자료실형"; bbrdcd = "0003";}
						if(j == 3){bbrdnm = "포토게시판"; bbrdcd = "0004";}
						
						//pstmt.setLong(++idx, num );		// 일련번호
						pstmt.setString(++idx1, bbrdcd);	// 게시판 코드번호
						pstmt.setString(++idx1, snum); 		// 동호회번호
						pstmt.setString(++idx1, bbrdnm); 	// 게시판 이름
						pstmt.setLong(++idx1, ++k); 	// SORT_SEQ
						
						//num = num+1;
						result = pstmt.executeUpdate();
					
					}
				}
					
		}// end if
		
	}// end for
   
	
	
	
	
	
      // 처리건수만큼 iCount가 같으면 정상 처리 된것임.
   if(iCount == seq_no.length) {
    conn.commit();
   } else {// 정상적이지 않으면 
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
     sql.append("UPDATE BCDBA.TBGCLUBMGMT SET \n");

     sql.append("\t  CLUB_OPN_AUTH_YN=?,  \n");  //진행여부 (진행/취소) 수정시
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
  * Max IDX Query를 생성하여 리턴한다.    
  ************************************************************************ */
  private String getNextValQuery(){
      StringBuffer sql = new StringBuffer();
      sql.append("SELECT NVL(MAX(BBRD_SEQ_NO),0)+1 BBRD_SEQ_NO FROM BCDBA.TBGCLUBBBRDMGMT		 \n");
		return sql.toString();
  }
    
}

