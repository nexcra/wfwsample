/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemMgmtDetailDaoProc
*   작성자     : (주)미디어포스 천선정
*   내용        : 관리자 회원관리 상세조회 
*   적용범위  : Golf
*   작성일자  : 2009-11-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

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

public class GolfAdmMemMgmtDetailDaoProc extends AbstractProc {
	
	public static final String TITLE = "회원관리 상세 조회";
	
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
		
		//debug("==== GolfAdmBenefitDetailInqDaoProc start ===");
		
		try{
			//조회 조건

			
			String p_idx = dataSet.getString("p_idx");
			String sql = "";
			boolean existsData = false;
			con = context.getDbConnection("default", null);
			int pidx = 0;			
			
			if(!"".equals(p_idx)){
				
				pidx = 0;
				sql = this.getSelectQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx,	p_idx);
				
				rset = pstmt.executeQuery();
				result = new DbTaoResult(TITLE);
				
				while(rset.next()){

					if(!existsData){
						result.addString("RESULT", "00");
					}
								
					result.addString("CMMN_CODE_NM",		rset.getString("GOLF_CMMN_CODE_NM"));
					result.addString("CMMN_CODE",			rset.getString("GOLF_CMMN_CODE"));
					result.addString("EXPL",				rset.getString("EXPL"));
					result.addString("USE_YN",				rset.getString("USE_YN"));
					result.addString("CDHD_SQ1_CTGO",		rset.getString("CDHD_SQ1_CTGO"));
							
					existsData = true;
					
				}

			}else{
				sql = this.getMaxCmmnCodeQuery();
				pstmt = con.prepareStatement(sql);
				
				rset = pstmt.executeQuery();
				result = new DbTaoResult(TITLE);
				
				while(rset.next()){
					if(!existsData){
						result.addString("RESULT", "00");
					}
					
					result.addString("CMMN_CODE_NM",	"");
					result.addString("CMMN_CODE",		rset.getString("CMMN_CODE"));
					result.addString("EXPL", 			"");
					result.addString("USE_YN",			"Y");
					result.addString("CDHD_SQ1_CTGO",	"");
					
				}
				existsData = true;
			}



			if(!existsData){
				result.addString("RESULT","01");			
			}
			//debug("==== GolfAdmBenefitDetailInqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBenefitDetailInqDaoProc ERROR ===");
			
			//debug("==== GolfAdmBenefitDetailInqDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. : Detail SQL
	************************************************************************ */
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 	SELECT	 T1.GOLF_CMMN_CODE_NM								");
		sql.append("\n 			,T1.GOLF_CMMN_CODE									");
		sql.append("\n 			,T1.EXPL											");
		sql.append("\n 			,T2.CDHD_SQ1_CTGO									");
		sql.append("\n 			,T1.USE_YN											");
		sql.append("\n 			,TO_CHAR(TO_DATE(T1.REG_ATON,'YYYYMMDDHH24MISS'),'YYYY-MM-DD')as REG_ATON	");
		sql.append("\n 	FROM BCDBA.TBGCMMNCODE	T1									");
		sql.append("\n 	JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.GOLF_CMMN_CODE = T2.CDHD_SQ2_CTGO	 		");
		sql.append("\n 	WHERE T1.GOLF_URNK_CMMN_CLSS='0000' AND T1.GOLF_URNK_CMMN_CODE='0005'				");
		sql.append("\n AND T2.CDHD_CTGO_SEQ_NO = ?									");

		


		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. : Detail SQL
	************************************************************************ */
	private String getMaxCmmnCodeQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 	SELECT	TO_CHAR(MAX(GOLF_CMMN_CODE)+1 	,'0000')AS CMMN_CODE					");
		sql.append("\n 	FROM 	BCDBA.TBGCMMNCODE														");
		sql.append("\n 	WHERE 	GOLF_URNK_CMMN_CLSS='0000' AND GOLF_URNK_CMMN_CODE='0005'				");



		return sql.toString();
	}
}
