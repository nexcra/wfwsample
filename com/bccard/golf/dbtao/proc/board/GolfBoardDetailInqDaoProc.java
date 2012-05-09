/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardDetailInqDaoProc
*   작성자     : (주)미디어포스 권영만
*   내용        : 게시판 폼 처리 
*   적용범위  : Golf
*   작성일자  : 2009-04-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.board;

import java.io.*;
import java.sql.*;
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

import oracle.jdbc.driver.OracleResultSet; 
import oracle.sql.CLOB;


/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-04-01
 **************************************************************************** */
public class GolfBoardDetailInqDaoProc extends AbstractProc {
	
	public static final String TITLE = "게시판 폼 조회";
	
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
		
		//debug("==== GolfBoardDetailInqDaoProc Start :"+TITLE+" ===");
		
		try{ 
			//조회 조건

			String p_idx			= dataSet.getString("p_idx"); 
			String boardid			= dataSet.getString("boardid"); 
			String mode				= dataSet.getString("mode"); 
			
			int pidx = 0;
			con = context.getDbConnection("default", null);
			
			// VIEW일 경우  조회수 증가
			if("view".equals(mode))
			{
				String sql_cnt = this.getCntUpdQuery(p_idx);
				pstmt = con.prepareStatement(sql_cnt);
				pidx = 0;
				pstmt.setString(++pidx, boardid);
				pstmt.setString(++pidx, p_idx);
				
				pstmt.executeUpdate();
				
			}
			
			
			String sql = this.getSelectQuery(p_idx);							
			
			pstmt = con.prepareStatement(sql);
			pidx = 0;
			pstmt.setString(++pidx, boardid);
			pstmt.setString(++pidx, p_idx);
			pstmt.setString(++pidx, "N");
			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			//clob
			String sClob = null;
			StringBuffer output = new StringBuffer();
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				
				Reader input = rset.getCharacterStream("CONTENT"); 
			     char[] buffer = new char[1024];
			     int byteRead;
			     while((byteRead=input.read(buffer,0,1024))!=-1)
			     {
			    	 output.append(buffer,0,byteRead);
			      }
			      input.close();
			      sClob = output.toString();
				
				
				result.addString("subject",					rset.getString("SUBJECT"));
				result.addString("content",					sClob);
				result.addString("point",					rset.getString("POINT"));
				result.addString("account",					rset.getString("ACCOUNT"));
				result.addString("name",					rset.getString("NAME"));
				result.addString("reg_dtime",				rset.getString("REG_DTIME"));
				result.addString("reg_ip",					rset.getString("REG_IP"));
				result.addString("from_date",				rset.getString("FROM_DATE"));				
				result.addString("read_cnt",				rset.getString("READ_CNT"));
				result.addString("eps_yn",					rset.getString("EPS_YN"));
				result.addString("img_nm",					rset.getString("IMG_NM"));
				result.addString("img_nm1",					rset.getString("IMG_NM1"));
				result.addString("admin_no",				rset.getString("ADMIN_NO"));
				result.addString("up_admin_no",				rset.getString("UP_ADMIN_NO"));
				result.addString("edit_dtime",				rset.getString("EDIT_DTIME"));
				
				existsData = true;
				
			}
			
			
			 
			

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfBoardDetailInqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfBoardDetailInqDaoProc ERROR ===");
			e.printStackTrace();
			//debug("==== GolfBoardDetailInqDaoProc ERROR ===");
		}finally{
			try { if(rset != null) {rset.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 				SELECT												");
		sql.append("\n 					SUBJECT,										");
		sql.append("\n 					CONTENT,										");
		sql.append("\n 					POINT,											");
		sql.append("\n 					ACCOUNT,										");
		sql.append("\n 					NAME,											");
		sql.append("\n 					REG_DTIME,										");
		sql.append("\n 					REG_IP,											");
		sql.append("\n 					FROM_DATE,										");
		sql.append("\n 					TO_DATE,										");
		sql.append("\n 					RAF_DATE,										");
		sql.append("\n 					RAF_TIME,										");
		sql.append("\n 					READ_CNT,										");
		sql.append("\n 					EPS_YN,											");
		sql.append("\n 					IMG_NM,											");
		sql.append("\n 					IMG_NM1,										");
		sql.append("\n 					NO,												");
		sql.append("\n 					TOT_PRTI_CNT,									");
		sql.append("\n 					DEL_YN,											");
		sql.append("\n 					EDIT_DTIME,										");
		sql.append("\n                  (select ACCOUNT from TOPNADMIN where SEQ_NO=TB.ADMIN_NO ) as ADMIN_NO,");
		sql.append("\n                  (select ACCOUNT from TOPNADMIN where SEQ_NO=TB.UP_ADMIN_NO ) as UP_ADMIN_NO");
		
		sql.append("\n 				FROM TBBBRD	 TB									");
		sql.append("\n 				WHERE BOARDID = ? 									");
		sql.append("\n 				and SEQ_NO = ? 										");
		sql.append("\n 				and DEL_YN = ? 										");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getCntUpdQuery(String idx) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE TBBBRD	SET											    ");
		sql.append("\n	READ_CNT = READ_CNT+1											");
		sql.append("\n	WHERE BOARDID = ? AND SEQ_NO = ?								");

		return sql.toString();
	}
	
	
}

