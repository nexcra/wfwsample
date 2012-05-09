/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointAdmAuthMenulnqProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 권한 변경 
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

public class GolfAdmAuthMenulnqProc extends AbstractProc {
	public static final String TITLE = "관리자  메뉴 조회";
	
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
		
		
		//debug("==== GolfAdmAuthMenulnqProc start ===");
		
		try{
			//조회 조건
			String p_idx	= dataSet.getString("p_idx"); 		//PK
					
			StringBuffer sql = new StringBuffer();
		
			sql.append("\n select	m0.SQ1_LEV_SEQ_NO as m0_idx , 							");
			sql.append("\n 			m1.SQ2_LEV_SEQ_NO as m1_idx ,m2.SQ3_LEV_SEQ_NO as m2_idx , 	");
			sql.append("\n 			m0.SQ1_LEV_MENU_NM as m0_name ,m1.SQ2_LEV_MENU_NM as m1_name ,	 	");
			sql.append("\n 			m2.SQ3_LEV_MENU_NM as m2_name							 	");
			sql.append("\n from BCDBA.TBGSQ1MENUINFO m0, BCDBA.TBGSQ2MENUINFO m1, BCDBA.TBGSQ3MENUINFO m2				");
			sql.append("\n where m0.SQ1_LEV_SEQ_NO = m1.SQ1_LEV_SEQ_NO(+) AND m1.SQ2_LEV_SEQ_NO = m2.SQ2_LEV_SEQ_NO(+)	");
			sql.append("\n order by m0.EPS_SEQ asc, m1.EPS_SEQ asc, m2.EPS_SEQ asc					");
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			
						
			rs = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			int ret = 0;
			while(rs.next())  {
				
				result.addString("m0_idx",				rs.getString("m0_idx"));
				result.addString("m1_idx",				rs.getString("m1_idx"));
				result.addString("m2_idx",				rs.getString("m2_idx"));
				result.addString("m0_name",				rs.getString("m0_name"));
				result.addString("m1_name",				rs.getString("m1_name"));
				result.addString("m2_name",				rs.getString("m2_name"));
				
				ret++;
				
			}
			
			if(ret == 0){
				result.addString("RESULT","01");
			
			} else {
				result.addString("RESULT","00");
			}
			
		}catch ( Exception e ) {
			

		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}
}
