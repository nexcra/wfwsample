/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBusUsedCkInqDaoProc
*   작성자    : (주)미디어포스 권영만
*   내용      : 관리자 > 이벤트->골프장버스운행이벤트->이용내역 체크
*   적용범위  : Golf
*   작성일자  : 2009-09-28
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
public class GolfAdmBusUsedCkInqDaoProc extends DbTaoProc {
	
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
			String userId 				= dataSet.getString("userId");
			String intMemGrade 			= dataSet.getString("intMemGrade");	
			int pidx 					= 0;
			
			pstmt = con.prepareStatement(getSelectQuery());		
			pidx = 0;			
			pstmt.setString(++pidx, intMemGrade);
			pstmt.setString(++pidx, userId);
			
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
    * Query를 생성하여 리턴한다.    
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT COUNT(*) CNT,DECODE(?,'1',4,'2',3,'3',2,'4',1,'',0) TOT    ");
		sql.append("\n	 FROM BCDBA.TBGAPLCMGMT			     					");
		sql.append("\n  WHERE GOLF_SVC_APLC_CLSS = '9002'						");
		sql.append("\n	  AND CDHD_ID = ?		             					");
		sql.append("\n	  AND NUM_DDUC_YN = 'Y'              					");
		sql.append("\n	  AND SUBSTR(TEOF_DATE,1,6) = TO_CHAR(SYSDATE,'YYYYMM') ");

		return sql.toString();
    }  

		

}
