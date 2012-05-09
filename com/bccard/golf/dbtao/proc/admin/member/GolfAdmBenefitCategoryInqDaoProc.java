/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  :GolfAdmBenefitCategorySubInqDaoProc
*   작성자     : (주)미디어포스 조은미
*   내용        : 카테고리 처리 
*   적용범위  : Golf
*   작성일자  : 2009-05-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

import java.io.*;
import java.sql.*;
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

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;


/** ****************************************************************************
 * Media4th / Topn
 * @author
 * @version 2009-04-01
 **************************************************************************** */

public class GolfAdmBenefitCategoryInqDaoProc extends AbstractProc {

	public static final String TITLE = "카테고리 목록";  
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		try{ 
			int pidx = 0;
			con = context.getDbConnection("default", null);
			String search_cate_type     = dataSet.getString("search_cate_type");
			
			String sql = this.getSelectQuery(search_cate_type);				
			pstmt = con.prepareStatement(sql);			

			//if(!"".equals(search_cate_type))
			//{
			//	pstmt.setString(++pidx, search_cate_type);
			//}

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				result.addString("cd_seq",	 rset.getString("GOLF_CMMN_CODE"));
				result.addString("cd_seq_nm",	 rset.getString("GOLF_CMMN_CODE_NM"));				
				existsData = true;
				
			}
			if(!existsData){
				result.addString("RESULT", "01");
			}
			
			
		}catch ( Exception e ) {		
						
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}		
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	 * @param seq 
	************************************************************************ */
	private String getSelectQuery(String type) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 		SELECT											");
		sql.append("\n 			GOLF_CMMN_CODE,										");
		sql.append("\n 			GOLF_CMMN_CODE_NM										");
		sql.append("\n 		FROM BCDBA.TBGCMMNCODE					");	
		sql.append("\n 		WHERE GOLF_URNK_CMMN_CLSS = '0000' 								");
		sql.append("\n 		AND GOLF_URNK_CMMN_CODE = '0005' 								");
	//	if(!"".equals(type))
	//	{
	//		sql.append("\n 		AND CD = ? 							");
	//	}

		return sql.toString();
	}	

}
