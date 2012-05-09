/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmRangeSkyUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 드림 골프레인지 신청(sky72) 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmRangeSkyUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 드림 골프레인지 수정(sky72) 처리";

	/** *****************************************************************
	 * GolfAdmRangeSkyUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeSkyUpdDaoProc() {}
	
	/**
	 * 관리자 드림 골프레인지 수정 처리
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
		String sql = "";
				
		try {
			
			String appr_opion = data.getString("APPR_OPION");
            String add_appr_opion = data.getString("ADD_APPR_OPION");
            String cdhd_id = data.getString("CDHD_ID");
            
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
			
			
            /*****************************************************************************/
            
			sql = this.getInsertQuery();//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			pstmt.setString(++idx, data.getString("ATD_YN") ); 
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
           
			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			
			conn.setAutoCommit(true);
			
			//회원평가 수정 : / 추가
            if(!add_appr_opion.equals("")){
            	sql = this.getUpdCdhdQuery();
            	pstmt = conn.prepareStatement(sql);
            	
            	//공백이 아니면 /추가
            	if(!appr_opion.equals("")) add_appr_opion = "/"+ add_appr_opion;
            	
            	pstmt.setString(1, appr_opion + add_appr_opion);
            	pstmt.setString(2, cdhd_id);
            	result = pstmt.executeUpdate();
            	
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
		sql.append("\t  ATTD_YN=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t WHERE GOLF_SVC_RSVT_NO=?	\n");
        return sql.toString();
    }
 	/** ***********************************************************************
     * 해당회원에대한 평가수정쿼리
     ************************************************************************ */
     private String getUpdCdhdQuery(){
       StringBuffer sql = new StringBuffer();
 		sql.append("\n  UPDATE BCDBA.TBGGOLFCDHD   SET						");
 		sql.append("\n   APPR_OPION =  ?									");
 		sql.append("\n  WHERE CDHD_ID = ?									");
 		
     return sql.toString();
     }
}
