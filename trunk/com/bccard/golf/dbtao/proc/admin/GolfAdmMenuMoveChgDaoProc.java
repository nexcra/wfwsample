/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : golfAdmMenuMoveChgDaoProc
*   작성자     : (주)미디어포스 임은혜
*   내용        : 관리자 메뉴 수정 처리
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
public class GolfAdmMenuMoveChgDaoProc extends AbstractProc { 
	public static final String TITLE = "관리자  메뉴 이동 처리";
	
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
		
		
		//debug("==== golfAdmMenuChgDaoProc start ===");
		
		try{
			// WHERE절 조회 조건
			String str_t_name		= dataSet.getString("str_t_name"); 		//테이블이름
			String tmp_mode			= dataSet.getString("mode"); 	//컬럼이름
			String idx					= dataSet.getString("idx"); 
			String mode					= dataSet.getString("mode"); 	
			String state						= dataSet.getString("state"); 
			String pidx			 	= "";
			String str_ord 			= "";
			String str_max_ord = "";
			
			String msg				= "";
			String proc = "true";
			
			StringBuffer sql = new StringBuffer();	
			con = context.getDbConnection("default", null);
			result = new DbTaoResult(TITLE);
			int res = 0;
			int spidx = 0;

			// -1. 상위 IDX 구하기.
			if(!"SQ1".equals(mode))
			{
				sql.append("\n").append(" select "+tmp_mode+"_LEV_SEQ_NO from BCDBA. "+str_t_name+" ");
				sql.append("\n").append(" where "+mode+"_LEV_SEQ_NO = ? ");
				pstmt = con.prepareStatement(sql.toString());	
				
				spidx = 0;
				pstmt.setString(++spidx, idx);
				
				rs = pstmt.executeQuery();
				
				
				if(rs != null && rs.next()) {
										
					pidx = rs.getString(tmp_mode+"_LEV_SEQ_NO");					

				}
			} else {
				if("UP".equals(state)){
					sql.append("\n").append(" select SQ1_LEV_SEQ_NO from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where EPS_SEQ = (");
					sql.append("\n").append(" select max(EPS_SEQ) as EPS_SEQ from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where EPS_SEQ<(");
					sql.append("\n").append(" select EPS_SEQ from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where SQ1_LEV_SEQ_NO = ?");
					sql.append("\n").append(" ))");
				}else{
					sql.append("\n").append(" select SQ1_LEV_SEQ_NO from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where EPS_SEQ = (");
					sql.append("\n").append(" select min(EPS_SEQ) as EPS_SEQ from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where EPS_SEQ>(");
					sql.append("\n").append(" select EPS_SEQ from BCDBA.TBGSQ1MENUINFO");
					sql.append("\n").append(" where SQ1_LEV_SEQ_NO = ?");
					sql.append("\n").append(" ))");
				}
				pstmt = con.prepareStatement(sql.toString());	
				
				spidx = 0;
				pstmt.setString(++spidx, idx);
				
				rs = pstmt.executeQuery();
				
				if(rs != null && rs.next()) {
					pidx = rs.getString(tmp_mode+"_LEV_SEQ_NO");
				}
				
			}

			// 0. 현재 idx 인 값의 위치를 가져온다.
			StringBuffer sql_2 = new StringBuffer();	
			sql_2.append("\n").append(" select EPS_SEQ from BCDBA."+str_t_name+ " ");
			sql_2.append("\n").append(" where "+mode+"_LEV_SEQ_NO= ? ");
			pstmt = con.prepareStatement(sql_2.toString());	
			spidx = 0;
			pstmt.setString(++spidx, idx);		
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()) {
				str_ord = rs.getString("EPS_SEQ");						
			}
			
			// 1. 최고 높은 ord 값을 가져온다.
			StringBuffer sql_3 = new StringBuffer();	
			sql_3.append("\n").append(" select NVL(MAX(EPS_SEQ),0) as ord from BCDBA."+str_t_name+ " ");
			if(!"".equals(pidx))
			{
			sql_3.append("\n").append(" where "+tmp_mode+"_LEV_SEQ_NO = ? ");
			}
			
			pstmt = con.prepareStatement(sql_3.toString());	
			spidx = 0;
			if(!"".equals(pidx))
			{
			pstmt.setString(++spidx, pidx);	
			}
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()) {
				str_max_ord = rs.getString("ord");							
			}
	

			if(str_ord.equals("") || str_max_ord.equals("")){
				msg = "잘못된 데이터 입니다.";
				proc = "false";				
				result.addString("RESULT", "01");
				result.addString("msg", msg);
				result.addString("proc", proc);
			}
			
			if(state.equals("UP") && str_ord.equals("1")){
				
				msg = "현재 위치가 가장 최상위 입니다.\\n \\n더이상 상위로 올릴 수 없습니다.";
				proc = "false";
				result.addString("RESULT", "01");
				result.addString("msg", msg);
				result.addString("proc", proc);
			}
			
			if(state.equals("DOWN") && str_ord.equals(str_max_ord)){
				msg = "현재 위치가 가장 최하위 입니다.\\n \\n더이상 하위로 내릴 수 없습니다.";
				proc = "false";
				result.addString("RESULT", "01");
				result.addString("msg", msg);
				result.addString("proc", proc);
				
			}
			
			if("true".equals(proc)){
				
				if(state.equals("UP")){					

					// 2. idx 인 값의 ord -1인 해당 idx의 ord 값을 +1 한다.		
					StringBuffer sql_4 = new StringBuffer();	
					sql_4.append("\n").append(" update BCDBA."+str_t_name+" set  ");
					sql_4.append("\n").append(" EPS_SEQ = ?  ");
					sql_4.append("\n").append(" Where EPS_SEQ = ( "+str_ord+" - 1  )  ");
					if(!"M0".equals(mode)){
						sql_4.append("\n").append(" AND "+tmp_mode+"_LEV_SEQ_NO = ?  ");
					}
					pstmt = con.prepareStatement(sql_4.toString());	
					spidx = 0;
					pstmt.setString(++spidx, str_ord);				
					if(!"M0".equals(mode)){
					pstmt.setString(++spidx, pidx);
					}
					pstmt.executeUpdate(); 

					// 3. idx 인 값의 ord를 -1 한다.
					StringBuffer sql_5 = new StringBuffer();	
					sql_5.append("\n").append(" update BCDBA."+str_t_name+" set  ");
					sql_5.append("\n").append(" EPS_SEQ =  ( EPS_SEQ - 1 ) ");
					sql_5.append("\n").append(" Where "+mode+"_LEV_SEQ_NO = ?  ");
					pstmt = con.prepareStatement(sql_5.toString());	
					spidx = 0;
					pstmt.setString(++spidx, idx);
					res = pstmt.executeUpdate();
				
				}
				else if(state.equals("DOWN")){			
					
					// 2. idx 인 값의 ord +1인 해당 idx의 ord 값을 -1 한다.
					StringBuffer sql_6 = new StringBuffer();	
					sql_6.append("\n").append(" update BCDBA."+str_t_name+" set  ");
					sql_6.append("\n").append(" EPS_SEQ = ?  ");
					sql_6.append("\n").append(" Where EPS_SEQ = ( "+str_ord+" + 1  )  ");
					if(!"M0".equals(mode)){
						sql_6.append("\n").append(" AND "+tmp_mode+"_LEV_SEQ_NO = ?  ");
					}
					pstmt = con.prepareStatement(sql_6.toString());	
					spidx = 0;
					pstmt.setString(++spidx, str_ord);
					
					if(!"M0".equals(mode)){
					pstmt.setString(++spidx, pidx);
					}
					pstmt.executeUpdate();
					
					// 3. idx 인 값의 ord를 -1 한다.
					StringBuffer sql_7 = new StringBuffer();	
					sql_7.append("\n").append(" update BCDBA."+str_t_name+" set  ");
					sql_7.append("\n").append(" EPS_SEQ =  ( EPS_SEQ + 1 ) ");
					sql_7.append("\n").append(" Where "+mode+"_LEV_SEQ_NO = ?  ");
					pstmt = con.prepareStatement(sql_7.toString());	
					spidx = 0;
					pstmt.setString(++spidx, idx);
					res = pstmt.executeUpdate();
				
				}
				proc = "true";
				msg = "수정 되었습니다.";
				result.addString("msg", msg);
				result.addString("proc", proc);
			}
	
			//debug("proc:"+proc);
			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			

			//debug("==== golfAdmMenuChgDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== golfAdmMenuChgDaoProc ERROR ===");
			
			//debug("==== golfAdmMenuChgDaoProc ERROR ===");
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}	

}
