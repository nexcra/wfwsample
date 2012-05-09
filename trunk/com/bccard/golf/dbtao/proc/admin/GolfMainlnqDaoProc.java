/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointMainlnqDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 메인 비지니스 로직
*   적용범위  : Golf
*   작성일자  : 2009-05-06  
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.*;


public class GolfMainlnqDaoProc extends AbstractProc {
		
		private static final String TITLE = "GOLF 메인 페이지";
		
		private String sStrSql;	
	
		/** *****************************************************************
		 * GolfPointMainlnqDaoProc 프로세스 생성자
		 * @param N/A
		 ***************************************************************** */
		public GolfMainlnqDaoProc() {}

			
		/**
		 * Proc 실행.
		 * @param Connection con
		 * @param TaoDataSet dataSet
		 * @return TaoResult 
		 */
		public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
			
			ResultSet rs = null;
			String title = data.getString("TITLE");
			Connection con = null;
			PreparedStatement pstmt = null;
			int count = 0;
			DbTaoResult  result =  new DbTaoResult(title);
				
			try {
				  con = context.getDbConnection("default", null);
				   
			 
				 //조회 ----------------------------------------------------------
				  String	page_no		= data.getString("page_no");
				  
				  StringBuffer sql = new StringBuffer();
				  sql.append("\n SELECT * from BCDBA.bbs  ");
				  sql.append("\n WHERE 1 = ?	");		
			    
				  sStrSql = sql.toString();    
						 
						
				  //debug("=============sql=" + sql.toString());
				
				                      
				 pstmt = con.prepareStatement(sStrSql);
				 // 입력값 (INPUT)  
				 pstmt.setString(1, "1");  
				 rs = pstmt.executeQuery();
				 
				 if(rs != null) {			 
				 
					 while(rs.next())  {
	
					 	
						result.addString("SEQ"       	, rs.getString(1));
						result.addString("TITLE"       	, rs.getString(2));
						
						////debug(rs.getString(1));
						////debug(rs.getString(2));
						
						//result.addString("CURR_PAGE", StrUtil.isNull(rs.getString("curr_page"),""));
						result.addString("STR_SQL", StrUtil.isNull(sStrSql,""));
						result.addString("RESULT"        , "00"); //정상결과
					 }
				 }
				 if(result.size() < 1) {
						result.addString("RESULT", "01");
				 }
				
				 
			} catch (Throwable t) {
				throw new BaseException(t);
			} finally {
				try { if(rs != null) rs.close(); } catch (Exception ignored) {}
	            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
	            try { if(con  != null) con.close();  } catch (Exception ignored) {}
			}
			//debug("GolfPointMainlnqDaoProc end");	
			
			return result;
			
	
			
		}
}


