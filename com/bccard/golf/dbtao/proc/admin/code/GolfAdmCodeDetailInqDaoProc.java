/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCodeDetailInqDaoProc
*   작성자     : (주)미디어포스 조은미
*   내용        : 관리자 공통코드관리 목록 조회 
*   적용범위  : Golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.code;

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

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-03-31
 **************************************************************************** */
public class GolfAdmCodeDetailInqDaoProc extends AbstractProc {
	
	public static final String TITLE = "공통코드 관리  조회";
	
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
		
		//debug("==== GolfAdmCodeDetailInqDaoProc start ===");
		
		try{
			//조회 조건

			String p_idx			= dataSet.getString("p_idx");
			String s_idx			= dataSet.getString("s_idx"); 
			String sql = this.getSelectQuery(p_idx, s_idx);
			
			int pidx = 0;
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			pidx = 0;
			pstmt.setString(++pidx, p_idx);
			pstmt.setString(++pidx, s_idx);
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
							
				result.addString("CD_CLSS",				rset.getString("GOLF_CMMN_CLSS"));
				result.addString("CD",			rset.getString("GOLF_CMMN_CODE"));
				result.addString("CD_NM",			rset.getString("GOLF_CMMN_CODE_NM"));
				result.addString("CD_DESC",			rset.getString("EXPL"));
				result.addString("USE_YN",				rset.getString("USE_YN"));
				result.addString("REG_MGR_SEQ_NO",			rset.getString("REG_MGR_ID"));
				result.addString("CORR_MGR_SEQ_NO",			rset.getString("CHNG_MGR_ID"));
				result.addString("REG_DATE",			rset.getString("REG_ATON"));
				result.addString("CORR_DATE",			rset.getString("CHNG_ATON"));
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfAdmCodeDetailInqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmCodeDetailInqDaoProc ERROR ===");
			
			//debug("==== GolfAdmCodeDetailInqDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery(String p_idx, String s_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 			SELECT								");
		sql.append("\n 				GOLF_CMMN_CLSS,						");
		sql.append("\n 				GOLF_CMMN_CODE,					");
		sql.append("\n 				GOLF_CMMN_CODE_NM,						");
		sql.append("\n 				EXPL,						");
		sql.append("\n 				USE_YN,						");
		sql.append("\n 				(select MGR_ID from BCDBA.TBGMGRINFO where MGR_ID=TB.REG_MGR_ID ) as REG_MGR_ID,				");
		sql.append("\n 				(select MGR_ID from BCDBA.TBGMGRINFO where MGR_ID=TB.CHNG_MGR_ID ) as CHNG_MGR_ID,				");
		sql.append("\n 				to_char(to_date(REG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS REG_ATON,						");
		sql.append("\n 				to_char(to_date(CHNG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS CHNG_ATON						");
		
		sql.append("\n 			FROM BCDBA.TBGCMMNCODE	 TB		");
		sql.append("\n 			WHERE GOLF_CMMN_CLSS = ?							");
		sql.append("\n 			AND GOLF_CMMN_CODE = ?							");


		return sql.toString();
	}
}
