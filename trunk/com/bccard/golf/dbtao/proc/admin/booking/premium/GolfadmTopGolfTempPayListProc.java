/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmTopGolfTempPayListProc
*   �ۼ���    : shin cheong gwi
*   ����      : ������ ����
*   �������  : golfloung 
*   �ۼ�����  : 2010-12-02
************************** �����̷� ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfadmTopGolfTempPayListProc extends AbstractObject {

	private static GolfadmTopGolfTempPayListProc instance = null;
	static{
		synchronized(GolfadmTopGolfTempPayListProc.class){
			if(instance == null){
				instance = new GolfadmTopGolfTempPayListProc();
			} 
		}
	}
	public static GolfadmTopGolfTempPayListProc getInstance(){
		return instance;
	}
	
	/*
	 * ������ ����
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException 
	{
		String title = dataSet.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		int idx = 0;
		
		try
		{
			long pageNo = dataSet.getLong("pageNo");
            long recordsInPage = dataSet.getLong("recordsInPage");
            String account = dataSet.getString("sh_id");  
            String usrName = dataSet.getString("sh_nm");   
            int existdata = 0;
            
            if ( pageNo <= 0 ) pageNo = 1;
            if ( recordsInPage <= 0 ) recordsInPage = 10;            
            
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getTempPayListQuery(dataSet).toString());
				pstmt.setLong(++idx, recordsInPage);
				if(!account.equals("")){
					pstmt.setString(++idx, account);
				}
				if(!usrName.equals("")){
					pstmt.setString(++idx, usrName);
				}
				pstmt.setLong(++idx, pageNo * recordsInPage);
				if(!account.equals("")){
					pstmt.setString(++idx, account);
				}
				if(!usrName.equals("")){
					pstmt.setString(++idx, usrName);
				}
				if( account.equals("") && usrName.equals("") ){
					pstmt.setLong(++idx, pageNo);
				}
				rs = pstmt.executeQuery();
				
				while(rs.next()){
					existdata++;
					result.addLong("vno", (pageNo-1) * recordsInPage + existdata);
					result.addLong("memid", rs.getLong("MEMID"));
					result.addString("account", rs.getString("ACCOUNT"));
					result.addString("name", rs.getString("NAME"));
					result.addString("socid", rs.getString("SOCID"));
					result.addString("temp_pay_yn", rs.getString("TEMP_PAY_YN"));
					result.addLong("pageNo", rs.getLong("PAGE"));
					result.addLong("totalRecord", rs.getLong("TOT"));
				}
				if ( existdata > 0 ) {
	                result.addString("RESULT","00");
	            } else {
	                result.addString("RESULT","01");
	            }
			
		}catch (Throwable t) { 
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		return result;
	}
	
	/*
	 * ������ ��� ���� ����
	 */
	private StringBuffer getTempPayListQuery(TaoDataSet dataSet) throws Exception
	{
		String account = dataSet.getString("sh_id");
        String usrName = dataSet.getString("sh_nm");
        
		StringBuffer sb = new StringBuffer();		
		sb.append("	SELECT P1.*, P2.TOT FROM (	\n");
		sb.append("		SELECT T1.*, CEIL(ROWNUM/?) AS PAGE FROM (	\n");
		sb.append("			SELECT USR.MEMID, USR.ACCOUNT, USR.NAME, SUBSTR(USR.SOCID, 0, 6)||' - *******' AS SOCID, COLMN.TEMP_PAY_YN	\n");
		sb.append("			FROM BCDBA.TBGFMEMADD COLMN, UCUSRINFO USR	\n");
		sb.append("			WHERE COLMN.MEMID = USR.MEMID	\n");
		if(!account.equals("")){
			sb.append("				AND USR.ACCOUNT = ?		\n");
		}
		if(!usrName.equals("")){
			sb.append("				AND USR.NAME = ?		\n");
		}
		sb.append("			ORDER BY USR.MEMID, USR.ACCOUNT ) T1 WHERE ROWNUM <= ?	\n");
		sb.append("		) P1, (		\n");
		sb.append("		SELECT COUNT(*) AS TOT	\n");
		sb.append("		FROM BCDBA.TBGFMEMADD COLMN, UCUSRINFO USR	\n");
		sb.append("		WHERE COLMN.MEMID = USR.MEMID	\n");
		if(!account.equals("")){
			sb.append("			AND USR.ACCOUNT = ?		\n");
		}
		if(!usrName.equals("")){
			sb.append("			AND USR.NAME = ?		\n");
		}
		sb.append("	) P2	\n");
		if( account.equals("") && usrName.equals("") ){
			sb.append("	WHERE P1.PAGE = ?	\n");
		}
		return sb;
	}
}