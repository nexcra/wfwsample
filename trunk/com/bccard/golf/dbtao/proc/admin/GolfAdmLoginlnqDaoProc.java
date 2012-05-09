/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLoginlnqDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ž����Ʈ ������ �α��� ����
*   �������  : Golf
*   �ۼ�����  : 2009-05-06 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   ������� 
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

	public static final String TITLE = "�񾾰��� ������ ��ȸ";
	
	/** *****************************************************************
	 * TpAdmLoginlnqDaoProc ���μ��� ������ 
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLoginlnqDaoProc() { 

	}

	/** ***********************************************************************
	* Proc ����. 
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
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}


	/** ***********************************************************************
	* Proc ����. 
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
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}

	
	/*******************************************************************
	* �����DN���� ����
	* @return String		String ����.
	******************************************************************/
	public int updUserDnExecute(WaContext context, TaoDataSet data) throws DbTaoException  {

		int result = 0;
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;

		try {
			conn = context.getDbConnection("default", null);
			
            // ��Ͽ��� Ȯ��
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
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            throw new DbTaoException(msgEtt,e);
		} finally {
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}			

		return result;
	}
	

 	/** ***********************************************************************
	* ȸ������ DN ������Ʈ�ϱ�
	************************************************************************ */
	private String setMemberDnUpdateQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n");
		sql.append("\t	UPDATE BCDBA.TBGMGRINFO SET AZT_CFT_PROOF_VAL=? WHERE JUMIN_NO=?	\n");
		return sql.toString();
	}	
	
}
