/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admGrDelDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 레슨프로그램 삭제 처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;

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
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfadmGrDelDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 부킹골프장 삭제 처리";

	/** *****************************************************************
	 * GolfAdmLessonDelDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfadmGrDelDaoProc() {}
	
	/**
	 * 관리자 레슨프로그램 삭제 처리
	 * @param conn
	 * @param data 
	 * @return
	 * @throws DbTaoException
	 */
	public int execute(WaContext context,
	                   HttpServletRequest request,
	                   TaoDataSet data) throws DbTaoException  {

		int result = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
				
		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			//조회 ----------------------------------------------------------
		
			String sql1 = this.getDeleteQuery1();
			pstmt = conn.prepareStatement(sql1);
			pstmt.setLong(1, data.getLong("SEQ_NO") ); 			
			int res1 = pstmt.executeUpdate();			

			String sql2 = this.getDeleteQuery2();
			pstmt = conn.prepareStatement(sql2);
			pstmt.setLong(1, data.getLong("SEQ_NO") ); 			
			int res2 = pstmt.executeUpdate();			

			String sql3 = this.getDeleteQuery3();
			pstmt = conn.prepareStatement(sql3);
			pstmt.setLong(1, data.getLong("SEQ_NO") ); 			
			int res3 = pstmt.executeUpdate();

			String sql = this.getDeleteQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, data.getLong("SEQ_NO") ); 			
			int res = pstmt.executeUpdate();			
		    /** ***********************************************************************/
			
			if(res == 1) {
				result = 1;
				conn.commit();
			} else {
				result = 0;
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
    * Query를 생성하여 리턴한다.    - 골프장
    ************************************************************************ */
    private String getDeleteQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("DELETE BCDBA.TBGAFFIGREEN 	\n");
		sql.append("\t  WHERE AFFI_GREEN_SEQ_NO = ?	\n");
        return sql.toString();
    }
     /** ***********************************************************************
      * Query를 생성하여 리턴한다.    - 예약관리
      ************************************************************************ */
      private String getDeleteQuery1(){
  		StringBuffer sql = new StringBuffer();
  		sql.append("DELETE  	\n");
  		sql.append("\t  FROM BCDBA.TBGRSVTMGMT	\n");
  		sql.append("\t  WHERE AFFI_GREEN_SEQ_NO = ?	\n");
          return sql.toString();
      }
      /** ***********************************************************************
       * Query를 생성하여 리턴한다.    - 일정관리(날짜)
       ************************************************************************ */
       private String getDeleteQuery2(){
   		StringBuffer sql = new StringBuffer();
   		sql.append("DELETE 	\n");
   		sql.append("\t  FROM BCDBA.TBGRSVTABLESCDMGMT T1	\n");
   		sql.append("\t  WHERE RSVT_ABLE_SCD_SEQ_NO IN (SELECT RSVT_ABLE_SCD_SEQ_NO FROM BCDBA.TBGRSVTABLESCDMGMT WHERE AFFI_GREEN_SEQ_NO = ?)	\n");
           return sql.toString();
       }
       /** ***********************************************************************
        * Query를 생성하여 리턴한다.    - 일정관리(시간)
        ************************************************************************ */
        private String getDeleteQuery3(){
    		StringBuffer sql = new StringBuffer();
    		sql.append("DELETE 	\n");
    		sql.append("\t  FROM BCDBA.TBGRSVTABLESCDMGMT T1	\n");
    		sql.append("\t  WHERE AFFI_GREEN_SEQ_NO = ? 		\n");
            return sql.toString();
        }
}
