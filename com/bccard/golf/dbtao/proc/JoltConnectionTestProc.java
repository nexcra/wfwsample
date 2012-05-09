package com.bccard.golf.dbtao.proc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

public class JoltConnectionTestProc extends DbTaoProc {
	public static final String TITLE = "Jolt Å×½ºÆ®";
	private boolean existData;

	public TaoResult execute(Connection con, TaoDataSet dataSet)
			throws TaoException {
		String MESSAGE_KEY = "CommonProc_0000";
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		
		try {
			result = new DbTaoResult(TITLE);
			String sql = this.getSelectQuery(dataSet);
			pstmt = con.prepareStatement(sql);
			
			rset = pstmt.executeQuery();
			
			boolean existData = false;
			while (rset.next()) {
				if (!existData) {
					result.addString("result", "00");
				}
				result.addString("banner", rset.getString("banner"));
				existData = true;
			}
			
			if (!existData) {
				result.addString("result", "01");
			}
		} catch (Exception e) {
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, MESSAGE_KEY, null);
			throw new DbTaoException(msgEtt, e);
		} finally {
			try { if (rset != null) rset.close(); } catch (Exception ignored) {}
			try { if (pstmt != null) pstmt.close(); } catch (Exception ignored) {}
		}
		
		return result;
	}

	private String getSelectQuery(TaoDataSet dataSet) {
		StringBuffer sb = new StringBuffer();
		sb.append("select banner from v$version");
		
		return sb.toString();
	}

}
