/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpMyprizeUpdDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한레슨이벤트 > 나의당첨내역 > 결재처리
*   적용범위	: golf
*   작성일자	: 2009-07-09
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.special.myprize;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfEvntSpMyprizeUpdDaoProc extends AbstractProc {
	public static final String TITLE = "이벤트라운지 > 특별한레슨이벤트 > 나의당첨내역 > 결재처리";
	/** **************************************************************************
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet 
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rset = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		String sql = "";

		try { 
			
			
			String p_idx 		= data.getString("p_idx");
			String mode 		= data.getString("mode");
			String userId 		= data.getString("userId");
			String sex_clss 	= data.getString("sex_clss");
			String hp_ddd_no 	= data.getString("hp_ddd_no");
			String hp_tel_hno	= data.getString("hp_tel_hno");
			String hp_tel_sno	= data.getString("hp_tel_sno");
			String email 		= data.getString("email");
			String sttl_amt 	= data.getString("sttl_amt");
			String golf_svc_aplc_clss 	= data.getString("golf_svc_aplc_clss");			
			
			
			
			int pidx = 0;
			int rs = 0;
			boolean eof = false;
			conn = context.getDbConnection("default", null);
			
			
			if("ins".equals(mode)){
				pidx = 0;
				long maxVal = this.selectArticleNo(context);
				sql = this.getInsertQuery();
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(++pidx,maxVal);
				pstmt.setString(++pidx, golf_svc_aplc_clss);
				pstmt.setString(++pidx, p_idx); 
				pstmt.setString(++pidx, userId);
				pstmt.setString(++pidx, sex_clss);
				pstmt.setString(++pidx, email);
				pstmt.setString(++pidx, hp_ddd_no);
				pstmt.setString(++pidx, hp_tel_hno);
				pstmt.setString(++pidx, hp_tel_sno);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					eof = true;
					result.addString("RESULT","00");
				}else{
					result.addString("RESULT","01");
					eof = false;
				}
				
			}else if("pgrs".equals(mode)){
				pidx = 0;
				sql = this.getUpdatePgrsQuery();
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, sttl_amt);
				pstmt.setString(++pidx, p_idx);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					eof = true;
					result.addString("RESULT","00");
				}else{
					result.addString("RESULT","01");
					eof = false;
				}
				
				
				
			}else if("cncl".equals(mode)){
				pidx = 0;
				sql = this.getUpdateCancelQuery();
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, p_idx);
				
				rs = pstmt.executeUpdate();
				
				if(rs > 0){
					eof = true;
					result.addString("RESULT","00");
				}else{
					result.addString("RESULT","01");
					eof = false;
				}
				
				
			}else if("userChk".equals(mode)){
				
				
				
				
			}
			
			
			 
		} catch ( Exception e ) {			
			
		} finally {
			try { if(rset != null) rset.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getInsertQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 		INSERT INTO BCDBA.TBGAPLCMGMT(					"); 
		sql.append("\n 			 APLC_SEQ_NO								");
		sql.append("\n 			,GOLF_SVC_APLC_CLSS							");
		sql.append("\n 			,LESN_SEQ_NO								");
		sql.append("\n 			,PGRS_YN									");
		sql.append("\n 			,CDHD_ID									");
		sql.append("\n 			,SEX_CLSS									");
		sql.append("\n 			,EMAIL										");
		sql.append("\n 			,HP_DDD_NO									");
		sql.append("\n 			,HP_TEL_HNO									");
		sql.append("\n 			,HP_TEL_SNO									");
		sql.append("\n 			,REG_ATON									");
		sql.append("\n 			,PRZ_WIN_YN									");
		sql.append("\n 		)VALUES(										");
		sql.append("\n 			?,?,?,'N',?,								");
		sql.append("\n 			?,?,?,?,?,									");
		sql.append("\n 			TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'),'N'	");
		sql.append("\n 		)												");

		

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getUpdatePgrsQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 		UPDATE BCDBA.TBGAPLCMGMT SET					"); 
		sql.append("\n 			PGRS_YN = 'D'								");
		sql.append("\n 			,STTL_AMT = ?								");
		sql.append("\n 		WHERE APLC_SEQ_NO = ?							");

		 

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getUpdateCancelQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 		UPDATE BCDBA.TBGAPLCMGMT SET					"); 
		sql.append("\n 			PGRS_YN = 'N'								");
		sql.append("\n 			,STTL_AMT = NULL							");
		sql.append("\n 		WHERE APLC_SEQ_NO = ?							");

		 

		return sql.toString();
	}
	/** ******************************************************************************
	 * 게시판 글번호 가져오기
	 *********************************************************************************/
	private long selectArticleNo(WaContext context) throws Exception {

		Connection con = null;
        PreparedStatement pstmt = null;        
        ResultSet rset = null;        
        String sql = " SELECT NVL(MAX(APLC_SEQ_NO),0)+1 SEQ_NO FROM BCDBA.TBGAPLCMGMT ";
        long pidx = 0;
        try {
        	con = context.getDbConnection("default", null);
        	pstmt = con.prepareStatement(sql);
        	rset = pstmt.executeQuery();   
			if (rset.next()) {				
                pidx = rset.getLong(1);
			}
        } catch (Throwable t) {        
        	Exception exception = new Exception(t);          
            throw exception;
        } finally {
            try { if ( rset  != null ) rset.close();  } catch ( Throwable ignored) {}
            try { if ( pstmt != null ) pstmt.close(); } catch ( Throwable ignored) {}
            try { if ( con    != null ) con.close();    } catch ( Throwable ignored) {}
        }
        return pidx;

	}
}
