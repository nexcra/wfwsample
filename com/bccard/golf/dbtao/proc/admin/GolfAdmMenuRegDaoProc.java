/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMenuRegDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 메뉴 등록 
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

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmMenuRegDaoProc extends AbstractProc  {
	public static final String TITLE = "관리자  메뉴 등록";
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보 
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result = null;
		Connection con = null;
		
		
		
		try{
			// WHERE절 조회 조건
			String str_table_name		= dataSet.getString("str_table_name"); 		//테이블이름
			String str_idx_col_name		= dataSet.getString("str_idx_col_name"); 	//컬럼이름
			String str_pidx_col_name	= dataSet.getString("str_pidx_col_name"); 	//컬럼이름
			
			StringBuffer sql = new StringBuffer();		

			//SEQ 최대값 구하기
			sql.append("\n").append(" SELECT 							");
			sql.append("\n").append(" NVL(MAX( "+str_idx_col_name+" ),0) + 1 AS idx 		");			
			sql.append("\n").append(" FROM BCDBA."+str_table_name+" 							");
						
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			//int pidx = 0;
			//pstmt.setString(++pidx, str_idx_col_name.trim());
			//pstmt.setString(++pidx, str_table_name.trim());
			
			
			rs = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			int ret = 0;
			while (rs.next()) {
				
				result.addString("idx",				rs.getString("idx"));				
				
				ret++;				
			}
			if(ret == 0){
				result.addString("RESULT","01");
			
			} else {
				result.addString("RESULT","00");
			
			}
			

			//debug("==== GolfAdmMenuRegDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmMenuRegDaoProc ERROR ===");
			
			//debug("==== GolfAdmMenuRegDaoProc ERROR ===");
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

}
