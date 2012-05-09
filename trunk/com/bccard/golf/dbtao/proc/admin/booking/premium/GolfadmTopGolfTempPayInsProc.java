/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfTempPayInsProc
*   �ۼ���    : shin cheong gwi
*   ����      : ������ ���� ���
*   �������  : golfloung
*   �ۼ�����  : 2010-12-03
************************** �����̷� ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfadmTopGolfTempPayInsProc extends AbstractObject {

	private static GolfadmTopGolfTempPayInsProc instance = null;
	static{
		synchronized(GolfadmTopGolfTempPayInsProc.class){
			if(instance == null){
				instance = new GolfadmTopGolfTempPayInsProc();
			}
		}
	}
	public static GolfadmTopGolfTempPayInsProc getInstance()
	{
		return instance;
	}
	
	/*
	 * ��������
	 */
	public boolean execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException, SQLException 
	{
		boolean rtn_flag = false;
		Connection conn = null;		
		PreparedStatement pstmt = null;
		int idx = 0;
		
		try
		{
			String memid = dataSet.getString("memid");
			String temp_pay_yn = dataSet.getString("temp_pay_yn");
			
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			
			pstmt = conn.prepareStatement(this.setTempPayQuery().toString());
				pstmt.setInt(++idx, Integer.parseInt(memid));
				pstmt.setString(++idx, temp_pay_yn);
				pstmt.setInt(++idx, Integer.parseInt(memid));
				pstmt.setString(++idx, temp_pay_yn);
			pstmt.execute();
			conn.commit();
			rtn_flag = true;
			
		}catch (Throwable t) { 
			conn.rollback();
			throw new BaseException(t);
		} finally {	
			conn.setAutoCommit(true);
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		return rtn_flag;
	}
	
	/*
	 * ������  ����/������Ʈ ����
	 */
	private StringBuffer setTempPayQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("	MERGE INTO BCDBA.TBGFMEMADD			\n");
		sb.append("	USING DUAL 							\n");
		sb.append("	ON (								\n");
		sb.append("		MEMID = ?						\n");
		sb.append("		)								\n");
		sb.append("	WHEN MATCHED THEN					\n");
		sb.append("		UPDATE SET						\n");
		sb.append("			TEMP_PAY_YN = ?				\n");		
		sb.append("	WHEN NOT MATCHED THEN				\n");
		sb.append("		INSERT							\n");
		sb.append("			( MEMID, TEMP_PAY_YN )		\n");
		sb.append("		VALUES							\n");
		sb.append("			(?, ?)						\n");
		
		return sb;
	}
}
