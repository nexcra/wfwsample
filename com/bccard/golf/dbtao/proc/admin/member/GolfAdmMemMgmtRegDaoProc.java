/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmMemMgmtRegDaoProc
*   작성자     : (주)미디어포스 천선정
*   내용        : 관리자 등급관리 등록/수정처리
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
import java.util.ArrayList;
import java.util.StringTokenizer;

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

public class GolfAdmMemMgmtRegDaoProc extends AbstractProc {

	public static final String TITLE = "회원등급 등록 처리";
//	private String temporary;
	 
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
		
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		String str_message = "";
		
		//debug("==== GolfAdmBenefitRegDaoProc start ===");
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id		= (String)userEtt.getMemId(); 							
			}
			
			
			String p_idx			= dataSet.getString("p_idx");			//idx
			String mode				= dataSet.getString("mode"); 			//처리구분
			String cmmn_code		= dataSet.getString("cmmn_code"); 		//코드
			String cmmn_code_nm		= dataSet.getString("cmmn_code_nm"); 	//코드명
			String expl				= dataSet.getString("expl"); 			//상세설명
			String use_yn			= dataSet.getString("use_yn"); 			//사용여부
			String cdhd_sq1_ctgo	= dataSet.getString("cdhd_sq1_ctgo"); 	//회원1차분류코드
			
			result = new DbTaoResult(TITLE);
			
			
			int res = 0;	
			int int_result = 0;
			String sql = "";
			
			int pidx = 0;
			
			if("ins".equals(mode)){
				con.setAutoCommit(false);
				res = 0;
				int_result = 0;
				
				//01-1.코드관리테이블에 데이터가 있는지 체크
				pidx = 0;
				sql = this.getSelectCodeQuery();
				pstmt =con.prepareStatement(sql);
				pstmt.setString(++pidx, cmmn_code);
				
				rset = pstmt.executeQuery();
				
				if(rset.next()){
					res = 1;
					
				}else{
				
					//01-2.코드관리 insert
					pidx = 0;
					sql = this.getSelectCodeInsQuery();
					pstmt = con.prepareStatement(sql);
					pstmt.setString(++pidx, cmmn_code);
					pstmt.setString(++pidx, cmmn_code_nm);
					pstmt.setString(++pidx, expl);
					pstmt.setString(++pidx, use_yn);
					pstmt.setString(++pidx, admin_id);
					
					res = pstmt.executeUpdate();					
				}
				int_result = int_result + res;
				
				
				
				//02.회원분류관리 insert
				pidx = 0;
				sql = this.getSelectCtgoInsQuery();
				String ctgoSeq = this.getSelectCtgoMaxSeq(context);
				
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, ctgoSeq);
				pstmt.setString(++pidx, cdhd_sq1_ctgo);
				pstmt.setString(++pidx, cmmn_code);
				pstmt.setString(++pidx, admin_id);
				
				res = pstmt.executeUpdate();
				int_result = int_result + res;
				
				
				
				//03-1.회원혜택관리테이블에 데이터가 있는지 체크
				pidx = 0;
				sql = this.getSelectBnfQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, cmmn_code);
				
				rset = pstmt.executeQuery();
				
				
				//03-2.회원혜택관리 insert
				if(rset.next()){
					res = 1;
					
				}else{
					pidx = 0;
					sql = this.getSelectBnfInsQuery();
					pstmt = con.prepareStatement(sql);
					pstmt.setString(++pidx,cmmn_code);
					pstmt.setString(++pidx,admin_id);
					
					res = pstmt.executeUpdate();
					
				}
				int_result = int_result + res;
				
				if(int_result == 3){
					res = 1;
					result.addString("p_idx",cmmn_code);
					str_message = "등급이 등록되었습니다.";
					con.commit();
				}else{
					res = 0;
					str_message = "등급이 등록되는 도중에 문제가 발생하였습니다.\\n다시 시도해주세요.";
					con.rollback();
				}
				
				
				con.setAutoCommit(true);
				
			}else if("upd".equals(mode)){
				con.setAutoCommit(false);
				int_result = 0;
				//회원등급관리 게시판 update
				pidx = 0;
				sql = this.getSelectCodeUpdQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, cmmn_code_nm);
				pstmt.setString(++pidx, expl);
				pstmt.setString(++pidx, use_yn);
				pstmt.setString(++pidx, admin_id);
				pstmt.setString(++pidx, cmmn_code);
				
				res = pstmt.executeUpdate();
				int_result = int_result + res;
				
				//회원분류관리 게시판 update
				pidx = 0;
				sql = this.getSelectCtgoUpdQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, cdhd_sq1_ctgo);
				pstmt.setString(++pidx, admin_id);
				pstmt.setString(++pidx, p_idx);
				
				res = pstmt.executeUpdate();
				int_result = int_result + res;
				
				if(int_result == 2){
					res = 1;
					str_message = "등급이 수정되었습니다.";
					con.commit();
				}else{
					res = 0;
					str_message = "등급이 수정되는 도중에 문제가 발생하였습니다.\n다시 시도해주세요.";
					con.rollback();
				}
				
				con.setAutoCommit(true);
				
			}else if("del".equals(mode)){
				
				//골프회원분류관리에 데이터가 있는지 체크
				int int_cnt = 0;
				String str_cdhd_ctgo_seq_no = "";
				pidx = 0;
				sql = this.getSelectDelCountQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, p_idx);
				
				rset = pstmt.executeQuery();
				
				while(rset.next()){
					int_cnt = rset.getInt("CNT");
					str_cdhd_ctgo_seq_no = rset.getString("CDHD_CTGO_SEQ_NO");
				}
				
				
				//없으면 삭제
				if(int_cnt == 0){
					//트랜잭션을 위해 AutoCommit false;
					con.setAutoCommit(false);
					int_result = 0;
					
					//01.코드관리테이블에서 삭제
					pidx = 0;
					sql = this.getSelectCodeDelQuery();
					pstmt = con.prepareStatement(sql);
					pstmt.setString(++pidx, cmmn_code);
					
					res = pstmt.executeUpdate();
					int_result = int_result+res;
					
					//02.회원혜택관리테이블에서 삭제
					pidx = 0;
					sql = this.getSelectBnfDelQuery();	
					pstmt = con.prepareStatement(sql);
					pstmt.setString(++pidx,cmmn_code);
					
					res = pstmt.executeUpdate();
					int_result = int_result+res;
					
					
					//03.회원분류관리 테이블에서 삭제
					pidx = 0;
					sql = this.getSelectCtgoDelQuery();
					pstmt = con.prepareStatement(sql);
					pstmt.setString(++pidx,p_idx);
					
					res = pstmt.executeUpdate();
					int_result = int_result+res;
					
					
					if(int_result == 3){
						con.commit();
						res = 1;
						str_message = "등급이 삭제되었습니다.";
					}else{
						con.rollback();
						res = 0;
						str_message = "등급이 삭제되는 도중에 문제가 발생하였습니다.\\n다시 시도해주세요.";
					}
					
					
					con.setAutoCommit(true)	;
				//있으면 삭제못함	
				}else{
					res = 0;
					str_message = "회원이 1명이라도 있는 등급은 삭제가 불가능합니다.\\n회원등급변경 후 삭제해주세요. ";
				}
				
			}
			
			
			

			if ( res == 1 ) {
				result.addString("RESULT", "00");
				result.addString("message",str_message);
			}else{
				result.addString("RESULT", "01");
				result.addString("message",str_message);
			}
			
			
			//debug("==== GolfAdmCodeRegDaoProc end ===");	
		}catch ( Exception e ) {
			//debug("==== GolfAdmCodeRegDaoProc ERROR ===");
			e.printStackTrace();
			//debug("==== GolfAdmCodeRegDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
				
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. : 코드관리 insert
	************************************************************************ */
	private String getSelectCodeInsQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGCMMNCODE(										");
		sql.append("\n		GOLF_CMMN_CLSS,GOLF_CMMN_CODE,GOLF_CMMN_CODE_NM,EXPL,USE_YN		");
		sql.append("\n		,GOLF_URNK_CMMN_CLSS,GOLF_URNK_CMMN_CODE,REG_MGR_ID,REG_ATON	");
		sql.append("\n	)VALUES(															");
		sql.append("\n		'0005',?,?,?,?													");
		sql.append("\n		,'0000','0005',?,TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'))			");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. : 혜택관리 insert
	************************************************************************ */
	private String getSelectBnfInsQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGGOLFCDHDBNFTMGMT(								");
		sql.append("\n		CDHD_SQ2_CTGO,REG_MGR_ID,REG_ATON								");
		sql.append("\n	)VALUES(															");
		sql.append("\n		?,?,TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')	)					");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. : 혜택관리 Select
	************************************************************************ */
	private String getSelectBnfQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT  CDHD_SQ2_CTGO,	GEN_WKD_BOKG_NUM							");
		sql.append("\n	FROM 	BCDBA.TBGGOLFCDHDBNFTMGMT									");
		sql.append("\n	WHERE 	CDHD_SQ2_CTGO = ?											");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. : 코드관리 Select
	************************************************************************ */
	private String getSelectCodeQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT  GOLF_CMMN_CODE												");
		sql.append("\n	FROM BCDBA.TBGCMMNCODE												");
		sql.append("\n	WHERE 	GOLF_CMMN_CLSS='0005' AND GOLF_URNK_CMMN_CLSS='0000' 		");
		sql.append("\n		AND GOLF_URNK_CMMN_CODE='0005' AND GOLF_CMMN_CODE = ?	 		");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. : 분류관리 insert
	************************************************************************ */
	private String getSelectCtgoInsQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGGOLFCDHDCTGOMGMT(									");
		sql.append("\n		CDHD_CTGO_SEQ_NO,CDHD_SQ1_CTGO,CDHD_SQ2_CTGO,REG_MGR_ID,REG_ATON	");
		sql.append("\n	)VALUES(																");
		sql.append("\n		?,?,?,?,TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'))						");

		return sql.toString();
	}

	/** ***********************************************************************
	* Query를 생성하여 리턴한다. :공통코드관리테이블:update
	************************************************************************ */
	private String getSelectCodeUpdQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGCMMNCODE	SET									");
		//sql.append("\n		 GOLF_CMMN_CODE = ?,									");
		sql.append("\n		GOLF_CMMN_CODE_NM = ?,										");
		sql.append("\n		EXPL = ?,													");
		sql.append("\n		USE_YN = ?,													");
		sql.append("\n		CHNG_MGR_ID = ?,											");
		sql.append("\n		CHNG_ATON = TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')			");
		sql.append("\n	WHERE GOLF_CMMN_CLSS='0005' AND GOLF_URNK_CMMN_CLSS = '0000'	");
		sql.append("\n		  AND GOLF_URNK_CMMN_CODE='0005' AND GOLF_CMMN_CODE = ?		");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. :분류관리테이블 :update
	************************************************************************ */
	private String getSelectCtgoUpdQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHDCTGOMGMT	SET					");
		sql.append("\n		CDHD_SQ1_CTGO = ?	,								");
		sql.append("\n		CHNG_MGR_ID = ?	,									");
		sql.append("\n		CHNG_ATON = TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')	");
		sql.append("\n	WHERE CDHD_CTGO_SEQ_NO = ?								");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. :공통코드관리 테이블
	************************************************************************ */
	private String getSelectCodeDelQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGCMMNCODE									");
		sql.append("\n	WHERE GOLF_URNK_CMMN_CLSS='0000' AND GOLF_URNK_CMMN_CODE='0005'	");
		sql.append("\n  	  AND GOLF_CMMN_CODE=?										");
		
		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. :혜택관리 테이블
	************************************************************************ */
	private String getSelectBnfDelQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGGOLFCDHDBNFTMGMT					");
		sql.append("\n	WHERE CDHD_SQ2_CTGO = ?									");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. :회원분류관리 테이블
	************************************************************************ */
	private String getSelectCtgoDelQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGGOLFCDHDCTGOMGMT					");
		sql.append("\n	WHERE CDHD_CTGO_SEQ_NO = ?								");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. :회원등급을 사용하는 회원이 있는지 확인
	************************************************************************ */
	private String getSelectDelCountQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT 	COUNT(*)AS CNT									");
		sql.append("\n			,MAX(T2.CDHD_CTGO_SEQ_NO)AS CDHD_CTGO_SEQ_NO	");
		sql.append("\n	FROM 	BCDBA.TBGGOLFCDHDGRDMGMT T1						");
		sql.append("\n	JOIN 	BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO = T2.CDHD_CTGO_SEQ_NO			");
		sql.append("\n	WHERE 	T2.CDHD_CTGO_SEQ_NO=?							");

		return sql.toString();
	}
	
	private String getSelectCtgoMaxSeq(WaContext context) throws Exception{
		String maxSeq = "";
		String maxSql = " SELECT NVL(MAX(CDHD_CTGO_SEQ_NO),0)+1 AS IDX  FROM BCDBA.TBGGOLFCDHDCTGOMGMT ";
		
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection con = null;
		
		try{
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(maxSql);
			rset = pstmt.executeQuery();
			
			while(rset.next()){
				maxSeq = rset.getString("IDX");
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
			
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		
		
		return maxSeq;
	} 

}
