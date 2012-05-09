/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMemMgmtRegDaoProc
*   �ۼ���     : (��)�̵������ õ����
*   ����        : ������ ��ް��� ���/����ó��
*   �������  : Golf
*   �ۼ�����  : 2009-11-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
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

	public static final String TITLE = "ȸ����� ��� ó��";
//	private String temporary;
	 
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
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
			
			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id		= (String)userEtt.getMemId(); 							
			}
			
			
			String p_idx			= dataSet.getString("p_idx");			//idx
			String mode				= dataSet.getString("mode"); 			//ó������
			String cmmn_code		= dataSet.getString("cmmn_code"); 		//�ڵ�
			String cmmn_code_nm		= dataSet.getString("cmmn_code_nm"); 	//�ڵ��
			String expl				= dataSet.getString("expl"); 			//�󼼼���
			String use_yn			= dataSet.getString("use_yn"); 			//��뿩��
			String cdhd_sq1_ctgo	= dataSet.getString("cdhd_sq1_ctgo"); 	//ȸ��1���з��ڵ�
			
			result = new DbTaoResult(TITLE);
			
			
			int res = 0;	
			int int_result = 0;
			String sql = "";
			
			int pidx = 0;
			
			if("ins".equals(mode)){
				con.setAutoCommit(false);
				res = 0;
				int_result = 0;
				
				//01-1.�ڵ�������̺� �����Ͱ� �ִ��� üũ
				pidx = 0;
				sql = this.getSelectCodeQuery();
				pstmt =con.prepareStatement(sql);
				pstmt.setString(++pidx, cmmn_code);
				
				rset = pstmt.executeQuery();
				
				if(rset.next()){
					res = 1;
					
				}else{
				
					//01-2.�ڵ���� insert
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
				
				
				
				//02.ȸ���з����� insert
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
				
				
				
				//03-1.ȸ�����ð������̺� �����Ͱ� �ִ��� üũ
				pidx = 0;
				sql = this.getSelectBnfQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, cmmn_code);
				
				rset = pstmt.executeQuery();
				
				
				//03-2.ȸ�����ð��� insert
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
					str_message = "����� ��ϵǾ����ϴ�.";
					con.commit();
				}else{
					res = 0;
					str_message = "����� ��ϵǴ� ���߿� ������ �߻��Ͽ����ϴ�.\\n�ٽ� �õ����ּ���.";
					con.rollback();
				}
				
				
				con.setAutoCommit(true);
				
			}else if("upd".equals(mode)){
				con.setAutoCommit(false);
				int_result = 0;
				//ȸ����ް��� �Խ��� update
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
				
				//ȸ���з����� �Խ��� update
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
					str_message = "����� �����Ǿ����ϴ�.";
					con.commit();
				}else{
					res = 0;
					str_message = "����� �����Ǵ� ���߿� ������ �߻��Ͽ����ϴ�.\n�ٽ� �õ����ּ���.";
					con.rollback();
				}
				
				con.setAutoCommit(true);
				
			}else if("del".equals(mode)){
				
				//����ȸ���з������� �����Ͱ� �ִ��� üũ
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
				
				
				//������ ����
				if(int_cnt == 0){
					//Ʈ������� ���� AutoCommit false;
					con.setAutoCommit(false);
					int_result = 0;
					
					//01.�ڵ�������̺��� ����
					pidx = 0;
					sql = this.getSelectCodeDelQuery();
					pstmt = con.prepareStatement(sql);
					pstmt.setString(++pidx, cmmn_code);
					
					res = pstmt.executeUpdate();
					int_result = int_result+res;
					
					//02.ȸ�����ð������̺��� ����
					pidx = 0;
					sql = this.getSelectBnfDelQuery();	
					pstmt = con.prepareStatement(sql);
					pstmt.setString(++pidx,cmmn_code);
					
					res = pstmt.executeUpdate();
					int_result = int_result+res;
					
					
					//03.ȸ���з����� ���̺��� ����
					pidx = 0;
					sql = this.getSelectCtgoDelQuery();
					pstmt = con.prepareStatement(sql);
					pstmt.setString(++pidx,p_idx);
					
					res = pstmt.executeUpdate();
					int_result = int_result+res;
					
					
					if(int_result == 3){
						con.commit();
						res = 1;
						str_message = "����� �����Ǿ����ϴ�.";
					}else{
						con.rollback();
						res = 0;
						str_message = "����� �����Ǵ� ���߿� ������ �߻��Ͽ����ϴ�.\\n�ٽ� �õ����ּ���.";
					}
					
					
					con.setAutoCommit(true)	;
				//������ ��������	
				}else{
					res = 0;
					str_message = "ȸ���� 1���̶� �ִ� ����� ������ �Ұ����մϴ�.\\nȸ����޺��� �� �������ּ���. ";
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
	* Query�� �����Ͽ� �����Ѵ�. : �ڵ���� insert
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
	* Query�� �����Ͽ� �����Ѵ�. : ���ð��� insert
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
	* Query�� �����Ͽ� �����Ѵ�. : ���ð��� Select
	************************************************************************ */
	private String getSelectBnfQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT  CDHD_SQ2_CTGO,	GEN_WKD_BOKG_NUM							");
		sql.append("\n	FROM 	BCDBA.TBGGOLFCDHDBNFTMGMT									");
		sql.append("\n	WHERE 	CDHD_SQ2_CTGO = ?											");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. : �ڵ���� Select
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
	* Query�� �����Ͽ� �����Ѵ�. : �з����� insert
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
	* Query�� �����Ͽ� �����Ѵ�. :�����ڵ�������̺�:update
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
	* Query�� �����Ͽ� �����Ѵ�. :�з��������̺� :update
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
	* Query�� �����Ͽ� �����Ѵ�. :�����ڵ���� ���̺�
	************************************************************************ */
	private String getSelectCodeDelQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGCMMNCODE									");
		sql.append("\n	WHERE GOLF_URNK_CMMN_CLSS='0000' AND GOLF_URNK_CMMN_CODE='0005'	");
		sql.append("\n  	  AND GOLF_CMMN_CODE=?										");
		
		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. :���ð��� ���̺�
	************************************************************************ */
	private String getSelectBnfDelQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGGOLFCDHDBNFTMGMT					");
		sql.append("\n	WHERE CDHD_SQ2_CTGO = ?									");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. :ȸ���з����� ���̺�
	************************************************************************ */
	private String getSelectCtgoDelQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGGOLFCDHDCTGOMGMT					");
		sql.append("\n	WHERE CDHD_CTGO_SEQ_NO = ?								");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�. :ȸ������� ����ϴ� ȸ���� �ִ��� Ȯ��
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
