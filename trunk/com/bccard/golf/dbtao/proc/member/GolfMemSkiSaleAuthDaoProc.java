/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfMemSkiSaleAuthDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��������� ��Ű�Ǳ� üũ
*   �������  : Golfloung
*   �ۼ�����  : 2009-11-17
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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
 * ��������� ��Ű�Ǳ� üũ Ŭ����.
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
	 * Proc ����.
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