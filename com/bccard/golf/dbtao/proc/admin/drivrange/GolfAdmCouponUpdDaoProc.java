/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCouponUpdDaoProc
*   작성자    : (주)만세커뮤니케이션 엄지환
*   내용      : 관리자 쿠폰 수정 처리
*   적용범위  : golf
*   작성일자  : 2009-05-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.drivrange;

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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.*;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Topn
* @author	(주)만세커뮤니케이션
* @version	1.0
******************************************************************************/
public class GolfAdmCouponUpdDaoProc extends AbstractProc {

	public static final String TITLE = "관리자 쿠폰 수정 처리";

	/** *****************************************************************
	 * GolfAdmCouponUpdDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmCouponUpdDaoProc() {}
	
	/**
	 * 관리자 쿠폰 수정 처리
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
			conn = context.getDbConnection("default", null);	
			conn.setAutoCommit(false);
            /*****************************************************************************/
			String img_nm = data.getString("IMG_NM");
            
			sql = this.getInsertQuery(img_nm);//Insert Query
			pstmt = conn.prepareStatement(sql);
			
			int idx = 0;
			if (!GolfUtil.isNull(img_nm))	pstmt.setString(++idx, img_nm);
			pstmt.setString(++idx, data.getString("CUPN_CLSS") ); 
			pstmt.setString(++idx, data.getString("CUPN_NM") ); 
			pstmt.setLong(++idx, data.getLong("CUPN_DC_RT") );
			pstmt.setString(++idx, data.getString("ADMIN_NO") );
			pstmt.setLong(++idx, data.getLong("CUPN_SEQ_NO") );
			
			result = pstmt.executeUpdate();
            if(pstmt != null) pstmt.close();
	
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
    private String getInsertQuery(String img_nm){
        StringBuffer sql = new StringBuffer();
		sql.append("\n");		
		sql.append("UPDATE BCDBA.TBGCUPNMGMT SET	\n");
		if (!GolfUtil.isNull(img_nm)) sql.append("\t 	 CUPN_IMG=?,	");
		sql.append("\t  GOLF_RNG_CUPN_CLSS=?, CUPN_NM=?, DC_RT=?, CHNG_MGR_ID=?, CHNG_ATON=TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') 	\n");
		sql.append("\t WHERE CUPN_SEQ_NO=?	\n");		
        return sql.toString();
    }
}
