/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfadmTopGolfMemListProc
*   작성자    : shin cheong gwi
*   내용      : 가결제 여부 회원검색
*   적용범위  : golfloung 
*   작성일자  : 2010-12-07
************************** 수정이력 ****************************************************************
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

public class GolfadmTopGolfMemListProc extends AbstractObject {

	private static GolfadmTopGolfMemListProc instance = null;
	static{
		synchronized(GolfadmTopGolfMemListProc.class){
			if(instance == null){
				instance = new GolfadmTopGolfMemListProc();
			}
		}
	}
	public static GolfadmTopGolfMemListProc getInstance(){
		return instance;
	}
	
	/*
	 * 가결제 대상 회원내역
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
			String sh_nm = dataSet.getString("sh_nm");
			String sh_id = dataSet.getString("sh_id");
			long pageNo	= dataSet.getLong("pageNo");
			long recordsInPage = dataSet.getLong("recordsInPage");
			
			if (pageNo <= 0) pageNo = 1L;
			long startR = (pageNo-1L) * recordsInPage + 1L;
			long endR = pageNo * recordsInPage;
			
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getTempMemListQuery(dataSet).toString());
				if(!sh_nm.equals("")){
					pstmt.setString(++idx, sh_nm);
				}
				if(!sh_id.equals("")){
					pstmt.setString(++idx, sh_id);
				}
				if(!sh_nm.equals("")){
					pstmt.setString(++idx, sh_nm);
				}
				if(!sh_id.equals("")){
					pstmt.setString(++idx, sh_id);
				}
				
				pstmt.setLong(++idx, endR);				
				pstmt.setLong(++idx, startR);				
				pstmt.setLong(++idx, endR);
			
			rs = pstmt.executeQuery();
			
			boolean existsData = false;
			while(rs.next()){
				if(!existsData){
                    result.addString("RESULT", "00");
                }
				//GolfUtil.toTaoResult(result, rs);
				result.addString("NAME", rs.getString("NAME"));
				result.addString("ACCOUNT", rs.getString("ACCOUNT"));				
				result.addLong("MEMID", rs.getLong("MEMID"));
				result.addString("PHONE", rs.getString("PHONE"));
				result.addString("MOBILE", rs.getString("MOBILE"));
				result.addString("SOCID", rs.getString("SOCID"));
				result.addLong("RECORD_CNT", rs.getLong("RECORD_CNT"));
				result.addLong("ROW_NUM", rs.getLong("ROW_NUM"));
				existsData = true;
			}
			
			if(!existsData){
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
	 * 가결제 대상 회원 내역 쿼리
	 */
	public StringBuffer getTempMemListQuery(TaoDataSet dataSet) throws Exception
	{
		String sh_nm = dataSet.getString("sh_nm");
		String sh_id = dataSet.getString("sh_id");
		
		StringBuffer sb = new StringBuffer();
		sb.append("	SELECT *			\n");
		sb.append("		FROM (			\n");
		sb.append("			SELECT X.*, Y.RECORD_CNT, ROWNUM ROW_NUM	\n");
		sb.append("			FROM (			\n");
		sb.append("				SELECT A.NAME, A.ACCOUNT, A.MEMID, A.PHONE, A.MOBILE, SUBSTR(A.SOCID, 0, 6)||' - *******' AS SOCID	\n");
		sb.append("				FROM BCDBA.UCUSRINFO A		\n");
		sb.append("				WHERE A.SITE_CLSS = '0'		\n");
		sb.append("					AND A.MEMBER_CLSS IN ('1','3','5')		\n");
		if(!sh_nm.equals("")){
			sb.append("				AND A.NAME = ?		\n");
		}
		if(!sh_id.equals("")){
			sb.append("				AND A.ACCOUNT = ?	\n");
		}
		sb.append("			) X, (		\n");
		sb.append("			SELECT COUNT(*) RECORD_CNT		\n");
		sb.append("			FROM BCDBA.UCUSRINFO A			\n");
		sb.append("			WHERE A.SITE_CLSS = '0'			\n");
		sb.append("				AND A.MEMBER_CLSS IN ('1','3','5')		\n");
		if(!sh_nm.equals("")){
			sb.append("			AND A.NAME = ?		\n");
		}
		if(!sh_id.equals("")){
			sb.append("			AND A.ACCOUNT = ?	\n");
		}
		sb.append("			) Y		\n");		
		sb.append("		WHERE ROWNUM <= ?	\n");	
		sb.append("		)		\n");
		sb.append("	WHERE ROW_NUM >= ?		\n");		
		sb.append("	AND ROW_NUM <= ?		\n");			
		
		return sb;
	}
}
