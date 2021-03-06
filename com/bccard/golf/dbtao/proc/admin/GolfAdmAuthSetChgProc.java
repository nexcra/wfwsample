/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthSetChgProc
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

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmAuthSetChgProc extends AbstractProc {

	public static final String TITLE = "관리자  권한 변경";
	
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
		
		
		//debug("==== GolfAdmAuthSetChgProc start ===");
		
		try{
			//조회 조건
			String p_idx	= dataSet.getString("p_idx"); 		//PK
					
			StringBuffer sql = new StringBuffer();
			
			sql.append("\n SELECT	SQ3_LEV_SEQ_NO, MGR_PRIV_CLSS 			");
			sql.append("\n FROM	BCDBA.TBGSQ3MENUMGRPRIVFIXN						");
			sql.append("\n WHERE MGR_ID = ?						    	");
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			
			int idx = 0; 		
			pstmt.setString(++idx, p_idx);
			
			rs = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			int ret = 0;
			while(rs.next())  {
				
				result.addString("M2_SEQ_NO",				rs.getString("SQ3_LEV_SEQ_NO"));
				result.addString("AUTH_CLSS",				rs.getString("MGR_PRIV_CLSS"));
				
				ret++;
				
			}
			if(ret == 0){
				result.addString("RESULT","01");
			
			} else {
				result.addString("RESULT","00");
			
			}
					
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmAuthSetChgProc ERROR ===");
			
			//debug("==== GolfAdmAuthSetChgProc ERROR ===");
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}
}
