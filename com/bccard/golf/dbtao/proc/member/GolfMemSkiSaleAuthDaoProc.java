/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfMemSkiSaleAuthDaoProc
*   작성자    : (주)미디어포스 진현구
*   내용      : 골프라운지 스키판권 체크
*   적용범위  : Golfloung
*   작성일자  : 2009-11-17
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.member;

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
 * 골프라운지 스키판권 체크 클래스.
 * @author
 * @version 2009-11-17
 **************************************************************************** */
public class GolfMemSkiSaleAuthDaoProc extends DbTaoProc {
	/**
	 * @info getSelectViewSql
	 * @return String
	 */
	private String getSelectViewSql() {

	    String sql = "";
	    sql = "\n	SELECT	AFFI_FIRM_NM						" +
	    "\n				from BCDBA.TBGGOLFCDHD					" +
	    "\n				WHERE cdhd_id = ? and join_chnl='2302'	";
		return sql;
	}

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {
		PreparedStatement pstmt	= null;
		ResultSet rs = null;
		String actnKey = null;

		String title = dataSet.getString("TITLE");
		
		DbTaoResult result	= new DbTaoResult(title);

		try {
			actnKey = dataSet.getString("actnKey");

			pstmt = con.prepareStatement(getSelectViewSql());
			pstmt.setString(1, dataSet.getString("account"));
			
			rs = pstmt.executeQuery();
			if (rs != null && rs.next()) {
				result.addString("RESULT", "00");
				GolfUtil.toTaoResult(result, rs);
			} else {
				result.addString("RESULT", "01");
			}

		} catch(Throwable t){
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, title, "SYSTEM_ERROR", null );
			msgEtt.addEvent( "/app/golfloung/" + actnKey + ".do", "/golfloung/img/images/office/bt_ok.gif");
			throw new DbTaoException(msgEtt,t);
			//debug(t.toString());
		} finally {
			try{ if(rs		!= null)	rs.close();		}catch(Exception ignored){}
			try{ if(pstmt	!= null)	pstmt.close();	}catch(Exception ignored){}
			try { if ( con    != null ) con.close();    } catch ( Throwable ignored) {}
		}

		return result;
	}
}