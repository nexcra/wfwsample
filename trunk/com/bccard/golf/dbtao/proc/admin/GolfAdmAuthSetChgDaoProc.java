/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmAuthSetChgDaoProc
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
import java.util.ArrayList;
import java.util.StringTokenizer;


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
public class GolfAdmAuthSetChgDaoProc extends AbstractProc {

	public static final String TITLE = "관리자  권한 변경 처리";
	
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
		
		
		//debug("==== GolfAdmAuthSetChgDaoProc start ===");
		
		try{
			result = new DbTaoResult(TITLE);
			
			//조회 조건
			String p_idx	= dataSet.getString("p_idx"); 		//PK
			String chk_list	= dataSet.getString("chk_list"); 
			String ori_list	= dataSet.getString("ori_list"); 
			
		
			
			ArrayList list_chk = new ArrayList();
			ArrayList list_ori = new ArrayList();
			ArrayList del_list = new ArrayList();
			ArrayList ins_list = new ArrayList();
			
			StringTokenizer st1 = new StringTokenizer(chk_list, ";");
			while (st1.hasMoreTokens()) {
				 list_chk.add(st1.nextToken());
			 }

			StringTokenizer st2 = new StringTokenizer(ori_list, ";");
			while (st2.hasMoreTokens()) {
				 list_ori.add(st2.nextToken());
			 }
		
			// 1. 삭제 대상
			del_list = getDel_list(list_chk, list_ori);	

			// 2. 입력 대상
			ins_list = getIns_list(list_chk, list_ori);
					
			// 3. 삭제 - AUTH
			con = context.getDbConnection("default", null);
			int res = 0;
			
			int del_int = del_list.size();
			if(del_int > 0){
				
				for(int i = 0; i<del_list.size(); i++){
					
					StringBuffer sql = new StringBuffer();			
					sql.append("\n DELETE FROM	BCDBA.TBGSQ3MENUMGRPRIVFIXN				");
					sql.append("\n WHERE  MGR_ID = ?	AND SQ3_LEV_SEQ_NO = ?	");
					
					pstmt = con.prepareStatement(sql.toString());	
					
					int idx = 0; 		
					pstmt.setString(++idx, p_idx);
					pstmt.setString(++idx, (String)del_list.get(i));
					
					res = pstmt.executeUpdate();
					
				}
			}
			
			// 4. 입력 AUTH
			int ins_int = ins_list.size();
			
			if(ins_int > 0){
				for(int i = 0; i<ins_list.size(); i++){
					StringBuffer sql = new StringBuffer();			
					sql.append("\n Insert into BCDBA.TBGSQ3MENUMGRPRIVFIXN						");
					sql.append("\n (SQ3_LEV_SEQ_NO, MGR_ID, MGR_PRIV_CLSS	)		");
					sql.append("\n values ( ?, ?, ? )									");
					
					
					pstmt = con.prepareStatement(sql.toString());	
					
					int idx = 0; 		
					pstmt.setString(++idx, (String)ins_list.get(i));
					pstmt.setString(++idx, p_idx);
					pstmt.setString(++idx, "W");
					
					res = pstmt.executeUpdate();
				}
			}
			
			if(ins_int == 0 && del_int == 0){
				result.addString("RESULT","02");
			}
			else if(res == 0){
				result.addString("RESULT","01");
			
			} else {
				result.addString("RESULT","00");
			
			}			
					
			

			//debug("==== GolfAdmAuthSetChgDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmAuthSetChgDaoProc ERROR ===");
			
			//debug("==== GolfAdmAuthSetChgDaoProc ERROR ===");
		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}
	public ArrayList getDel_list(ArrayList chk, ArrayList ori){
		ArrayList del_list = new ArrayList();
		boolean equals_ok = false;

		for(int i = 0; i<ori.size(); i++){
			equals_ok = false;
			String o_idx = (String)ori.get(i);
			
			for(int j =0; j<chk.size(); j++){
				String c_idx = (String)chk.get(j);

			if(o_idx.equals(c_idx)) equals_ok = true;
			} // end for

			if(!equals_ok) del_list.add(o_idx);
		} // end for

		return del_list;
	}
	public ArrayList getIns_list(ArrayList chk, ArrayList ori){

		ArrayList ins_list = new ArrayList();
		boolean equals_ok = false;

		for(int i = 0; i<chk.size(); i++){
			equals_ok = false;
			String c_idx = (String)chk.get(i);
			
			for(int j =0; j<ori.size(); j++){
				String o_idx = (String)ori.get(j);

				if(c_idx.equals(o_idx)) equals_ok = true;
			} // end for

			if(!equals_ok) ins_list.add(c_idx);
		} // end for

		return ins_list;

	}

}
