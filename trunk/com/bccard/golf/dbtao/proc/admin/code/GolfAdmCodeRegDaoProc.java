/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCodeRegDaoProc
*   작성자     : (주)미디어포스 조은미 
*   내용        : 관리자 게시판관리 등록 처리
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
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-15 
 **************************************************************************** */
public class GolfAdmCodeRegDaoProc extends AbstractProc {

	public static final String TITLE = "공통코드관리 등록 처리";
//	private String temporary;
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	 * @throws TaoException 
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws TaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		//debug("==== GolfAdmCodeRegDaoProc start ===");
		
		try{
			con = context.getDbConnection("default", null);
			con.setAutoCommit(false);
			
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id		= (String)userEtt.getMemId(); 							
			}
			
			//조회 조건
			String search_yn			= dataSet.getString("search_yn"); 		//검색여부
			
			String search_clss		= "";									//검색어구분
			String search_word		= "";									//검색어

			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// 검색어
				search_word	= dataSet.getString("search_word"); 		// 제목검색여부
			}
			long page_no 	= dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size 	= dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");

			String cd_clss		= dataSet.getString("CD_CLSS"); 
			String cd			= dataSet.getString("CD");
			String cd_nm		= dataSet.getString("CD_NM"); 
			String cd_desc		= dataSet.getString("CD_DESC"); 
			String use_yn		= dataSet.getString("USE_YN"); 
			String p_idx		= dataSet.getString("p_idx");
			String s_idx		= dataSet.getString("s_idx"); 
			String mode			= dataSet.getString("mode"); 			//처리구분
			String sql 			= "";
			
			int res = 0;	
			int pidx = 0;
			int cnt = 0;
			
			//debug("s_idx:"+s_idx);

			//등록시
			if("ins".equals(mode))
			{	
				pidx = 0; sql ="";
				sql = selQuery();
				
				pstmt = con.prepareStatement(sql);				
				pstmt.setString(++pidx, cd_clss);
				pstmt.setString(++pidx, cd);
				
				rset = pstmt.executeQuery();
				
				if (rset.next()) {				
	                cnt = rset.getInt("CNT");
				}				
			
				if(rset  != null) rset.close();
				if(pstmt != null) pstmt.close();
				
				if (cnt==0) {
					
					pidx = 0; sql ="";
					sql = this.masterCodeInExe();
					pstmt = con.prepareStatement(sql);
					pstmt.setString(++pidx, cd_clss);
					pstmt.setString(++pidx, cd);
					pstmt.setString(++pidx, cd_nm);
					pstmt.setString(++pidx, cd_desc);
					pstmt.setString(++pidx, use_yn);
					pstmt.setString(++pidx, admin_id);
					pstmt.setString(++pidx, cd_clss);
	
					res = pstmt.executeUpdate();
				
				}
							
			}
			else if("upd".equals(mode))
			{
				sql = this.getSelectUpdQuery("");
				
				pstmt = con.prepareStatement(sql);
				pidx = 0;
				
				pstmt.setString(++pidx, cd_desc);
				pstmt.setString(++pidx, use_yn);
				pstmt.setString(++pidx, admin_id);
				pstmt.setString(++pidx, p_idx);
				pstmt.setString(++pidx, s_idx);
				
				res = pstmt.executeUpdate();
				
			}
			else if("del".equals(mode))
			{
				sql = this.getSelectDelQuery("");
				
				pstmt = con.prepareStatement(sql);
				pidx = 0;
				
				pstmt.setString(++pidx, p_idx);
				pstmt.setString(++pidx, s_idx);

				res = pstmt.executeUpdate();
				 
			}
			else if("delAll".equals(mode))
			{
				pidx = 0;
				
				//master code 삭제
				sql = this.getSelectDelQuery("");
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, p_idx);
				pstmt.setString(++pidx, s_idx);
				res = pstmt.executeUpdate();	
				
				if(rset  != null) rset.close();
				if(pstmt != null) pstmt.close();				
				
				//detail code 삭제
				sql = "";
				pidx = 0;
				
				sql = this.delAllExe("");
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, s_idx);
				res = pstmt.executeUpdate();
				
			}
			else if("dtl".equals(mode))
			{
				
				long maxValue = this.selectArticleNo(context, p_idx, s_idx);				
				
				sql = this.dtlCodeInExe();
				
				pstmt = con.prepareStatement(sql);
				pidx = 0;
				
				pstmt.setString(++pidx, s_idx);
				pstmt.setString(++pidx, cd);
				pstmt.setString(++pidx, cd_nm);
				pstmt.setString(++pidx, cd_desc);
				pstmt.setLong(++pidx, maxValue);
				pstmt.setString(++pidx, use_yn);
				pstmt.setString(++pidx, p_idx);
				pstmt.setString(++pidx, s_idx);
				pstmt.setString(++pidx, admin_id);

				res = pstmt.executeUpdate();
				
			}
			
			result = new DbTaoResult(TITLE);
			
			if ( cnt > 0 ) {
				con.commit();
				result.addString("RESULT", "02");
			}
			
			boolean flag = false;
			
			if ( mode.equals("delAll") ){ 
				if ( res > 0 ) flag = true;
			}else {
				if ( res == 1 ) flag = true;
			}				

			debug ("# res ["+res+"]" + "flag :"+flag);
			
			if (flag) {
				con.commit();
				result.addString("RESULT", "00");
			}else{
				con.rollback();
				result.addString("RESULT", "01");
			}
	
		}catch ( Exception e ) {
			try { con.rollback();	} catch (SQLException e1) {	e1.printStackTrace();}
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
			
		}
		
		return result;	
				
	}
	
	/*************************************************************************
	* 이미 존재하는 코드인지 조회한다
	************************************************************************ */
	private String selQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n	SELECT COUNT(*) CNT										");
		sql.append("\n	FROM BCDBA.TBGCMMNCODE									");
		sql.append("\n	WHERE GOLF_CMMN_CLSS = ? AND GOLF_CMMN_CODE = ?			");
		
		return sql.toString();	
	
	}
	
	/*************************************************************************
	* 마스터 코드등록 기능 이용하여 등록(디테일 코드 등록도 가능)
	************************************************************************ */
	private String masterCodeInExe() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGCMMNCODE							");
		sql.append("\n	(GOLF_CMMN_CLSS,										");
		sql.append("\n	GOLF_CMMN_CODE,											");
		sql.append("\n	GOLF_CMMN_CODE_NM,										");
		sql.append("\n	EXPL,													");
		sql.append("\n	USE_YN,													");
		sql.append("\n	REG_MGR_ID,												");
		sql.append("\n	REG_ATON, GOLF_URNK_CMMN_CLSS, GOLF_URNK_CMMN_CODE)		");
		sql.append("\n	VALUES(?,?,?,?,?,	");
		sql.append("\n	?,											");
		sql.append("\n	TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'), '0000', ?)		");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectUpdQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGCMMNCODE	SET				");
		sql.append("\n	EXPL = ?	,										");
		sql.append("\n	USE_YN = ? ,												");
		sql.append("\n	CHNG_MGR_ID = ? ,														");
		sql.append("\n	CHNG_ATON = TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')					");
		sql.append("\n	WHERE GOLF_CMMN_CLSS = ?	");
		sql.append("\n	AND GOLF_CMMN_CODE = ?	");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectDelQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGCMMNCODE					");
		sql.append("\n	WHERE GOLF_CMMN_CLSS = ?	");
		sql.append("\n	AND GOLF_CMMN_CODE = ?	");

		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String delAllExe(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGCMMNCODE	");
		sql.append("\n	WHERE GOLF_CMMN_CLSS = ?		");

		return sql.toString();
	}
	
	
	/*************************************************************************
	* 디테일 코드등록 기능 이용하여 등록
	************************************************************************ */
	private String dtlCodeInExe() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGCMMNCODE			");
		sql.append("\n	(GOLF_CMMN_CLSS,						");
		sql.append("\n	GOLF_CMMN_CODE,							");
		sql.append("\n	GOLF_CMMN_CODE_NM,						");
		sql.append("\n	EXPL,									");
		sql.append("\n	SORT_SEQ,								");
		sql.append("\n	USE_YN,									");
		sql.append("\n	GOLF_URNK_CMMN_CLSS,					");
		sql.append("\n	GOLF_URNK_CMMN_CODE,					");
		sql.append("\n	REG_MGR_ID,								");
		sql.append("\n	REG_ATON		)						");
		sql.append("\n	VALUES(?,?,?,?,?,?,?,?,					");
		sql.append("\n	?,										");
		sql.append("\n	TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'))	");

		return sql.toString();
	}
	
	
	/** ***********************************************************************
	 * 정렬순서 가져오기
	************************************************************************ */
	private long selectArticleNo(WaContext context, String p_idx, String s_idx) throws BaseException {

		Connection con = null;
        PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;
        
        String sql = "select NVL(MAX(SORT_SEQ), 0) + 1  as SORT_SEQ from BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CLSS = '" + p_idx + "' AND GOLF_URNK_CMMN_CODE = '" + s_idx +"'";
		
        long pidx = 0;
        try {
        	con = context.getDbConnection("default", null);
            pstmt1 = con.prepareStatement(sql);
            rset1 = pstmt1.executeQuery();   
			if (rset1.next()) {				
                pidx = rset1.getLong(1);
			}
        } catch (Throwable t) {          // SQLException 시 예외 처리 : 쿼리에 문제가 있을때 발생
        	BaseException exception = new BaseException(t);          
            throw exception;
        } finally {
            try { if ( rset1  != null ) rset1.close();  } catch ( Throwable ignored) {}
            try { if ( pstmt1 != null ) pstmt1.close(); } catch ( Throwable ignored) {}
            try { if ( con    != null ) con.close();    } catch ( Throwable ignored) {}
        }
        return pidx;

	}
	
	
	
}

