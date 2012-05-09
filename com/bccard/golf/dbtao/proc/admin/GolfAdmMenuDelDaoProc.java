/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMenuDelDaoProc
*   작성자     : (주)미디어포스 임은혜
*   내용        : 관리자 메뉴 삭제 처리
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
public class GolfAdmMenuDelDaoProc extends AbstractProc {
	
	public static final String TITLE = "관리자  메뉴 삭제 처리";
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		PreparedStatement pstmt_list = null;		
		PreparedStatement pstmt_list2 = null;	
		ResultSet rs = null;
		ResultSet rs2 = null;
		DbTaoResult result = null;
		Connection con = null;
		
		
		//debug("==== GolfAdmMenuDelDaoProc start ===");
		
		try{
			// WHERE절 조회 조건
			String idx					= dataSet.getString("idx"); 
			String mode					= dataSet.getString("mode"); 							
			String m2_seq_no		= "";
			String m1_seq_no		= "";
			
			con = context.getDbConnection("default", null);
			result = new DbTaoResult(TITLE);
			int res = 0;
			int spidx = 0;	
			
			if("m2".equals(mode))
			{
				//UPDATE ORD
				StringBuffer sql_ord_m2 = new StringBuffer();
				sql_ord_m2.append("\n").append("  UPDATE BCDBA.TBGSQ3MENUINFO SET EPS_SEQ = EPS_SEQ-1 where SQ2_LEV_SEQ_NO = (select SQ2_LEV_SEQ_NO from BCDBA.TBGSQ3MENUINFO where SQ3_LEV_SEQ_NO = ? )  ");
				sql_ord_m2.append("\n").append("	and EPS_SEQ > ( select EPS_SEQ from BCDBA.TBGSQ3MENUINFO where SQ3_LEV_SEQ_NO = ? ) ");
				
				pstmt = con.prepareStatement(sql_ord_m2.toString());	
				spidx = 0;				
				pstmt.setString(++spidx, idx);
				pstmt.setString(++spidx, idx);
				res = pstmt.executeUpdate();
								
				//AUTH DELETE
				StringBuffer sql_ad_m2 = new StringBuffer();
				sql_ad_m2.append("\n").append(" DELETE FROM BCDBA.TBGSQ3MENUMGRPRIVFIXN ");
				sql_ad_m2.append("\n").append(" WHERE SQ3_LEV_SEQ_NO = ? ");
				
				pstmt = con.prepareStatement(sql_ad_m2.toString());	
				spidx = 0;				
				pstmt.setString(++spidx, idx);	
				res = pstmt.executeUpdate();
				
				//MENU2 DELETE
				StringBuffer sql_md_m2 = new StringBuffer();
				sql_md_m2.append("\n").append(" DELETE FROM BCDBA.TBGSQ3MENUINFO ");
				sql_md_m2.append("\n").append(" WHERE SQ3_LEV_SEQ_NO = ? ");
				
				pstmt = con.prepareStatement(sql_md_m2.toString());	
				spidx = 0;				
				pstmt.setString(++spidx, idx);	
				res = pstmt.executeUpdate();
				
				if ( res == 1 ) {
					result.addString("RESULT", "00");
				}else{
					result.addString("RESULT", "01");
				}				
				
			}
			
			if("m1".equals(mode))
			{
				//UPDATE ORD
				StringBuffer sql_ord_m2 = new StringBuffer();
				sql_ord_m2.append("\n").append("  UPDATE BCDBA.TBGSQ2MENUINFO SET EPS_SEQ = EPS_SEQ-1 where SQ1_LEV_SEQ_NO = (select SQ1_LEV_SEQ_NO from BCDBA.TBGSQ2MENUINFO where SQ2_LEV_SEQ_NO = ? )  ");
				sql_ord_m2.append("\n").append("	and EPS_SEQ > ( select EPS_SEQ from BCDBA.TBGSQ2MENUINFO where SQ2_LEV_SEQ_NO = ? ) ");
				
				pstmt = con.prepareStatement(sql_ord_m2.toString());	
				spidx = 0;				
				pstmt.setString(++spidx, idx);
				pstmt.setString(++spidx, idx);
				res = pstmt.executeUpdate();
				
				
				// MENU2 Loop
				StringBuffer sql_lst_m1 = new StringBuffer();
				sql_lst_m1.append("\n").append(" Select SQ3_LEV_SEQ_NO ");
				sql_lst_m1.append("\n").append(" From BCDBA.TBGSQ3MENUINFO Where SQ2_LEV_SEQ_NO = ? ");
				pstmt_list = con.prepareStatement(sql_lst_m1.toString());	
				spidx = 0;				
				pstmt_list.setString(++spidx, idx);	
				rs = pstmt_list.executeQuery();
				
				int ret = 0;
				while (rs.next()) {
					
					m2_seq_no = rs.getString("SQ3_LEV_SEQ_NO");
					
					//AUTH delete	
					StringBuffer sql_ad_m2 = new StringBuffer();
					sql_ad_m2.append("\n").append(" DELETE FROM BCDBA.TBGSQ3MENUMGRPRIVFIXN ");
					sql_ad_m2.append("\n").append(" WHERE SQ3_LEV_SEQ_NO = ? ");
					
					pstmt = con.prepareStatement(sql_ad_m2.toString());	
					spidx = 0;				
					pstmt.setString(++spidx, m2_seq_no);	
					res = pstmt.executeUpdate();
					
					//MENU2 DELETE
					StringBuffer sql_md_m2 = new StringBuffer();
					sql_md_m2.append("\n").append(" DELETE FROM BCDBA.TBGSQ3MENUINFO ");
					sql_md_m2.append("\n").append(" WHERE SQ3_LEV_SEQ_NO = ? ");
					
					pstmt = con.prepareStatement(sql_md_m2.toString());	
					spidx = 0;				
					pstmt.setString(++spidx, m2_seq_no);	
					res = pstmt.executeUpdate();
				}
				
				//MENU_1 delete
				StringBuffer sql_d_m1 = new StringBuffer();
				sql_d_m1.append("\n").append(" DELETE FROM BCDBA.TBGSQ2MENUINFO ");
				sql_d_m1.append("\n").append(" WHERE SQ2_LEV_SEQ_NO = ? ");
				
				pstmt = con.prepareStatement(sql_d_m1.toString());	
				spidx = 0;				
				pstmt.setString(++spidx, idx);	
				res = pstmt.executeUpdate();
				
				if ( res == 1 ) {
					result.addString("RESULT", "00");
				}else{
					result.addString("RESULT", "01");
				}		
				
			}
			
			if("m0".equals(mode))
			{
				//UPDATE ORD
				StringBuffer sql_ord_m2 = new StringBuffer();
				sql_ord_m2.append("\n").append("  UPDATE BCDBA.TBGSQ1MENUINFO SET EPS_SEQ = EPS_SEQ-1 where EPS_SEQ > ( select EPS_SEQ from BCDBA.TBGSQ1MENUINFO where SQ1_LEV_SEQ_NO = ? )  ");				
				pstmt = con.prepareStatement(sql_ord_m2.toString());	
				spidx = 0;				
				pstmt.setString(++spidx, idx);
				res = pstmt.executeUpdate();
				
				// 1. MENU_1 Loop
				StringBuffer sql_lst_m1 = new StringBuffer();
				sql_lst_m1.append("\n").append(" Select SQ2_LEV_SEQ_NO ");
				sql_lst_m1.append("\n").append(" From BCDBA.TBGSQ2MENUINFO Where SQ1_LEV_SEQ_NO = ? ");
				pstmt_list = con.prepareStatement(sql_lst_m1.toString());	
				spidx = 0;				
				pstmt_list.setString(++spidx, idx);	
				rs = pstmt_list.executeQuery();
				
				int ret = 0;
				while (rs.next()) {
					
					m1_seq_no = rs.getString("SQ2_LEV_SEQ_NO");
					
					
					// MENU2 Loop
					StringBuffer sql_lst_m0 = new StringBuffer();
					sql_lst_m0.append("\n").append(" Select SQ3_LEV_SEQ_NO ");
					sql_lst_m0.append("\n").append(" From BCDBA.TBGSQ3MENUINFO Where SQ2_LEV_SEQ_NO = ? ");
					pstmt_list2 = con.prepareStatement(sql_lst_m0.toString());	
					spidx = 0;				
					pstmt_list2.setString(++spidx, m1_seq_no);	
					rs2 = pstmt_list2.executeQuery();
					

					while (rs.next()) {
						
						m2_seq_no = rs.getString("SQ3_LEV_SEQ_NO");
						
						// 3. AUTH delete	
						StringBuffer sql_ad_m2 = new StringBuffer();
						sql_ad_m2.append("\n").append(" DELETE FROM BCDBA.TBGSQ3MENUMGRPRIVFIXN ");
						sql_ad_m2.append("\n").append(" WHERE SQ3_LEV_SEQ_NO = ? ");
						
						pstmt = con.prepareStatement(sql_ad_m2.toString());	
						spidx = 0;				
						pstmt.setString(++spidx, m2_seq_no);	
						res = pstmt.executeUpdate();
						
						//MENU2 DELETE
						StringBuffer sql_md_m2 = new StringBuffer();
						sql_md_m2.append("\n").append(" DELETE FROM BCDBA.TBGSQ3MENUINFO ");
						sql_md_m2.append("\n").append(" WHERE SQ3_LEV_SEQ_NO = ? ");
						
						pstmt = con.prepareStatement(sql_md_m2.toString());	
						spidx = 0;				
						pstmt.setString(++spidx, m2_seq_no);	
						res = pstmt.executeUpdate();																		
						
					}
					
					//MENU1 DELETE
					StringBuffer sql_md_m2 = new StringBuffer();
					sql_md_m2.append("\n").append(" DELETE FROM BCDBA.TBGSQ2MENUINFO ");
					sql_md_m2.append("\n").append(" WHERE SQ2_LEV_SEQ_NO = ? ");
					
					pstmt = con.prepareStatement(sql_md_m2.toString());	
					spidx = 0;				
					pstmt.setString(++spidx, m1_seq_no);	
					res = pstmt.executeUpdate();
				
				}
				
				//MENU0 DELETE
				StringBuffer sql_md_m2 = new StringBuffer();
				sql_md_m2.append("\n").append(" DELETE FROM BCDBA.TBGSQ1MENUINFO ");
				sql_md_m2.append("\n").append(" WHERE SQ1_LEV_SEQ_NO = ? ");
				
				pstmt = con.prepareStatement(sql_md_m2.toString());	
				spidx = 0;				
				pstmt.setString(++spidx, idx);	
				res = pstmt.executeUpdate();
				
				if ( res == 1 ) {
					result.addString("RESULT", "00");
				}else{
					result.addString("RESULT", "01");
				}		
			}
		
		}catch ( Exception e ) {
			//debug("==== GolfAdmMenuDelDaoProc ERROR ===");
			
			//debug("==== GolfAdmMenuDelDaoProc ERROR ===");
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(rs2  != null) rs2.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(pstmt_list != null) pstmt_list.close(); }catch( Exception ignored){}
			try{ if(pstmt_list2 != null) pstmt_list2.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}	

}
