/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkCheckUpdDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 > 부킹 상태 변경 처리
*   적용범위  : golf
*   작성일자  : 2009-05-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
* 20091117			  진현구   예약취소 일경우 CNCL_ATON 컬럼값 추가
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.check;

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
* Golf
* @author	(주)미디어포스
* @version	1.0 
******************************************************************************/
public class GolfBkCheckUpdDaoProc extends AbstractProc {

	public static final String TITLE = "부킹 상태 변경 처리";

	/** *****************************************************************
	 * GolfadmGrUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfBkCheckUpdDaoProc() {}
	
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
		String sql = "";
				
		try {
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
			String type = data.getString("type");
			String idx = data.getString("idx");
            
			// 예약상태를 취소로 돌린다.
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
            
			pstmt.setString(1, idx );			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
            
            
            // 다시 부킹 할 수 있는 상태로 돌려준다. 예약취소 : N / 임박취소 : I
            if(type.equals("M") || type.equals("S")){
				sql = this.getUpdateQuery();//Insert Query
				pstmt = conn.prepareStatement(sql);
				
				pstmt.setString(1, idx );
				int result2 = pstmt.executeUpdate();
	            if(pstmt != null) pstmt.close();
            }
            /////////////////////////////////////////////

			if(result>0) {
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
		sql.append("UPDATE BCDBA.TBGRSVTMGMT SET	\n");
		sql.append("\t  RSVT_YN='N', CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sql.append("\t  , CNCL_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')				\n");
		sql.append("\t WHERE GOLF_SVC_RSVT_NO=?	\n");
        return sql.toString();
    }
     
 	/** ***********************************************************************
      * CLOB Query를 생성하여 리턴한다.    - 다시 부킹할 수 있는 상태로 돌린다.
      ************************************************************************ */
      private String getUpdateQuery(){
        StringBuffer sql = new StringBuffer();
  		sql.append("\n");
  		sql.append("\t	UPDATE BCDBA.TBGRSVTABLEBOKGTIMEMGMT SET	\n");
  		sql.append("\t	BOKG_RSVT_STAT_CLSS='0001'					\n");
  		sql.append("\t	WHERE RSVT_ABLE_BOKG_TIME_SEQ_NO=(			\n");
  		sql.append("\t		SELECT RSVT_ABLE_BOKG_TIME_SEQ_NO		\n");
  		sql.append("\t		FROM BCDBA.TBGRSVTMGMT					\n");
  		sql.append("\t		WHERE GOLF_SVC_RSVT_NO=?)	\n");
        	  
  		return sql.toString();
      }
     
}
