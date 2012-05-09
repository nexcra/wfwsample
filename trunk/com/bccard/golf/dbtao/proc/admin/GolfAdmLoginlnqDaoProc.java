/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmLoginlnqDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 탑포인트 관리자 로그인 로직
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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;


public class GolfAdmLoginlnqDaoProc extends AbstractProc {

	public static final String TITLE = "비씨골프 관리자 조회";
	
	/** *****************************************************************
	 * TpAdmLoginlnqDaoProc 프로세스 생성자 
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLoginlnqDaoProc() { 

	}

	/** ***********************************************************************
	* Proc 실행. 
	* @param WaContext context
	* @param HttpServletRequest request
	* @param DbTaoDataSet data
	* @return DbTaoResult	result 
	************************************************************************ */
	public TaoResult execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		DbTaoResult result = null;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
	
			result = new DbTaoResult(TITLE);
			//debug("GolfAdmLoginlnqDaoProc start ");
			String jumin_no	= data.getString("jumin_no");
			
			StringBuffer sql = new StringBuffer();
			sql.append("\n").append("SELECT");
			sql.append("\n").append("	MGR_ID as ID, HG_NM, AZT_CFT_PROOF_VAL");
			sql.append("\n").append("	, INSTR(AZT_CFT_PROOF_VAL,HG_NM) NM_YN");
			sql.append("\n").append(" FROM BCDBA.TBGMGRINFO");
			sql.append("\n").append(" WHERE JUMIN_NO = ?");
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			
			int idx = 0; 		
			pstmt.setString(++idx, jumin_no);
			
			rs = pstmt.executeQuery();
			
			int ret = 0;
			if(rs != null && rs.next()) {
				result.addString("ACCOUNT"				, rs.getString("ID"));
				result.addString("NAME"					, rs.getString("HG_NM"));
				result.addObject("AZT_CFT_PROOF_VAL"	, rs.getString("AZT_CFT_PROOF_VAL"));
				result.addObject("NM_YN"				, rs.getString("NM_YN"));
				
				
				ret++;
			}
			if(ret == 0){
				result.addString("RESULT","01");
				
			} else {
				result.addString("RESULT","00");
				
			}
						
			//debug("TpAdmLoginlnqDaoProc end ");
        } catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}


	/** ***********************************************************************
	* Proc 실행. 
	* @param WaContext context
	* @param HttpServletRequest request
	* @param DbTaoDataSet data
	* @return DbTaoResult	result 
	************************************************************************ */
	public int getDbUserDn(WaContext context, TaoDataSet data) throws DbTaoException  {

		int result = 0;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
	
			//debug("GolfAdmLoginlnqDaoProc start =========== getDbUserDn====== ");
			String jumin_no	= data.getString("jumin_no");
			String userDn	= data.getString("userDn");
			String dBUserDn = "";
			boolean updYn = false;
			//debug("============287=============jumin_no => " + jumin_no);
			//debug("============288=============userDn => " + userDn);
			
			StringBuffer sql = new StringBuffer();
			sql.append("\n").append("SELECT");
			sql.append("\n").append("	AZT_CFT_PROOF_VAL");
			sql.append("\n").append(" FROM BCDBA.TBGMGRINFO");
			sql.append("\n").append(" WHERE JUMIN_NO = ?");
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());	
			
			int idx = 0; 		
			pstmt.setString(++idx, jumin_no);
			
			rs = pstmt.executeQuery();
			
			if(rs != null && rs.next()) {
				dBUserDn = rs.getString("AZT_CFT_PROOF_VAL");  
				//debug("============304=============dBUserDn => " + dBUserDn);
				
				if(GolfUtil.empty(dBUserDn)){
					updYn = true;
				}else if(!dBUserDn.equals(userDn)){
					updYn = true;
				}
			}

			//debug("============317=============updYn => " + updYn);
			
			if(updYn){
				idx = 0;
				
				String sql_query = this.setMemberDnUpdateQuery(); 
	            
				pstmt = con.prepareStatement(sql_query);
				pstmt.setString(++idx, data.getString("userDn") );
				pstmt.setString(++idx, data.getString("jumin_no") );
	            rs = pstmt.executeQuery();	
	            if(rs != null) {
	            	result = 1;
					//debug("============321=============result => " + result);
	            }
			}
			
			
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
						
			//debug("TpAdmLoginlnqDaoProc end ");
        } catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}

	
	/*******************************************************************
	* 사용자DN값을 저장
	* @return String		String 정보.
	******************************************************************/
	public int updUserDnExecute(WaContext context, TaoDataSet data) throws DbTaoException  {

		int result = 0;
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = context.getDbConnection("default", null);
			
            // 등록여부 확인
			int idx = 0;
			
			String sql = this.setMemberDnUpdateQuery(); 
            
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("userDn") );
			pstmt.setString(++idx, data.getString("jumin_no") );
            rs = pstmt.executeQuery();	
            if(rs != null) {
            	result = 1;
            }
            
			if(rs != null) rs.close();
            if(pstmt != null) pstmt.close();
            

		} catch(Exception e) {
			try	{
				conn.rollback();
			}catch (Exception c){}
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	

 	/** ***********************************************************************
	* 회원정보 DN 업데이트하기
	************************************************************************ */
	private String setMemberDnUpdateQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGMGRINFO SET AZT_CFT_PROOF_VAL=? WHERE JUMIN_NO=?	\n");
		return sql.toString();
	}	
	
}
