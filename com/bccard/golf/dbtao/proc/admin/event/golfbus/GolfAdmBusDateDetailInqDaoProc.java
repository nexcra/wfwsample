/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBusDateDetailInqDaoProc
*   작성자    : (주)미디어포스 권영만
*   내용      : 관리자 > 비쥬박스 > 경품관리 조회
*   적용범위  : Golf
*   작성일자  : 2009-09-25
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.golfbus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;


/** ****************************************************************************
 *  Golf
 * @author	(주)미디어포스 
 * @version 1.0
 **************************************************************************** */
public class GolfAdmBusDateDetailInqDaoProc extends DbTaoProc {
	
	/**
	 * Proc 실행. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		PreparedStatement pstmt			= null;
		ResultSet rs 					= null;
		String title					= dataSet.getString("TITLE");
		String actnKey 					= null;
		DbTaoResult result				= new DbTaoResult(title);

		try {
			
			actnKey 					= dataSet.getString("actnKey");						
			int pidx 					= 0;
			
			pstmt = con.prepareStatement(getSelectQuery());		
			pidx = 0;
			pstmt.setString(++pidx, dataSet.getString("p_idx"));
			
			
			rs = pstmt.executeQuery();
			
			if ( rs.next() ) {
				//GolfUtil.toTaoResultBoard(result, rs, serial);
				GolfUtil.toTaoResult(result, rs);
				result.addString("RESULT", "00");
			} else {
				result.addString("RESULT", "01");
			}		
			
		} catch(Throwable t){
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "SYSTEM_ERROR", null );
			msgEtt.addEvent( actnKey + ".do", "bt_ok.gif");
			throw new DbTaoException(msgEtt,t);
		} finally {
			try { if( rs != null ){ rs.close(); } else {} } catch(Throwable ignore) {}
			try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}

		return result;
	}
	
	/** ***********************************************************************
	* 일정관리 목록 조회
	************************************************************************ */
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n	SELECT																");
		sql.append("\n				REG_DATE, GREEN_NM										");
		
		sql.append("\n	FROM BCDBA.TBGGREENEVNTSCD TBDR										");
		sql.append("\n	WHERE REG_DATE = ?													");				
		
		return sql.toString();
	}

		

}
