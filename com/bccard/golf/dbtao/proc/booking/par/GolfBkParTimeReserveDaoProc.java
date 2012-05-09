/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBkPreTimeRsViewDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 부킹 결과 확인 처리
*   적용범위  : golf
*   작성일자  : 2009-05-28
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.par;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Golf
 * @author	미디어포스 
 * @version	1.0 
 ******************************************************************************/
public class GolfBkParTimeReserveDaoProc extends AbstractProc {
	
	public static final String TITLE = "부킹 결과 확인 처리";
	
	/** *****************************************************************
	 * GolfBkPreTimeRsViewDaoProc 부킹 결과 확인 처리
	 * @param N/A
	 ***************************************************************** */
	public GolfBkParTimeReserveDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public int execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		int result = 0;
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = context.getDbConnection("default", null);
			
            // 등록여부 확인
			String rs_YN = "";
			int idx = 0;
			
			String sql = this.getReserveQuery(); 
            
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("BK_DATE") );
			pstmt.setString(++idx, data.getString("AFFI_GREEN_SEQ_NO") );
            rs = pstmt.executeQuery();	
            
			if(rs.next()){
				rs_YN = rs.getString("RS_YN");
			}
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            
            
            if(rs_YN.equals("Y")){
            	result = 1;
            }else{
            	result = 0;
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
	 * 등록여부를 리턴한다.     
	 ************************************************************************ */
	private String getReserveQuery(){
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n");
		sql.append("\t	SELECT 																	\n");
		sql.append("\t	(CASE WHEN MAX_ACPT_PNUM>RS_NUM THEN 'Y' ELSE 'N' END) RS_YN			\n");
		sql.append("\t			FROM(  															\n");
		sql.append("\t			    SELECT MAX_ACPT_PNUM  										\n");
		sql.append("\t			    , (SELECT COUNT(*) RS_NUM FROM BCDBA.TBGRSVTMGMT WHERE ROUND_HOPE_DATE=? AND AFFI_GREEN_SEQ_NO=T1.AFFI_GREEN_SEQ_NO AND RSVT_YN='Y') AS RS_NUM  \n");
		sql.append("\t			    FROM BCDBA.TBGAFFIGREEN T1  								\n");
		sql.append("\t			    WHERE AFFI_GREEN_SEQ_NO=?	 								\n");
		sql.append("\t			)  																\n");
		
		return sql.toString();
	}
 
}
