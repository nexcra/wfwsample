/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfTempPayInsProc
*   작성자    : shin cheong gwi
*   내용      : 가결제 여부 등록
*   적용범위  : golfloung
*   작성일자  : 2010-12-03
************************** 수정이력 ****************************************************************
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
	 * 내용저장
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
	 * 가결제  저장/업데이트 쿼리
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
