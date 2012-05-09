/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthUserUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����(proc)
*   �������  : Golf
*   �ۼ�����  : 2009-05-06 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
//import java.sql.ResultSet;

//import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.common.GolfException;
//import com.bccard.golf.dbtao.DbTaoResult;
//import com.bccard.golf.dbtao.DbTaoProc;
//import com.bccard.waf.common.StrUtil;
//import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoDataSet;
//import com.bccard.waf.tao.TaoException;
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.common.BaseException;
//import com.bccard.golf.common.ResultException;

/** ****************************************************************************
 * golf 
 * @author
 * @version 2009-05-06 
 **************************************************************************** */
public class GolfAdmAuthUserUpdDaoProc extends AbstractProc{

	public static final String TITLE = "����PROC";
	private String temporary;
	/** *****************************************************************
	 * GolfAdmAuthUserUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmAuthUserUpdDaoProc() { 

	}
	
	/****************************************************************
	* DB UPDATE
	****************************************************************/
	public int execute(WaContext context, TaoDataSet data) throws BaseException  {
		int res = 0;
		PreparedStatement pstmt = null;
		Connection con = null;

		try{
			//debug("==== GolfAdmAuthUserUpdDaoProc ����ó�� Start ===");
			//��� value

//			this.strInput(comm_cont);
//			this.nl2br();
//			comm_cont = this.strOutput();

			StringBuffer sql = new StringBuffer();			
			sql.append("	UPDATE BCDBA.TBGMGRINFO			");
			sql.append("	SET							");
			sql.append("	HG_NM=?, FIRM_NM=?, RSV_NM=?, ZP=?			");
			sql.append("	, ADDR=?, DTL_ADDR=?, EMAIL=?			");
			sql.append("	, DDD_NO=?, TEL_HNO=?, TEL_SNO=?	");
			sql.append("	, FAX_DDD_NO=?, FAX_TEL_HNO=?, FAX_TEL_SNO=?		");
			sql.append("	, HP_DDD_NO=?, HP_TEL_HNO=?, HP_TEL_SNO=?		");
			sql.append("	, MEMO_CTNT=? , CHNG_DATE=to_char(sysdate,'yyyymmdd'), INDV_INFO_RPES_NM=?, JUMIN_NO=?");
			sql.append("	where MGR_ID=?			");
			//debug("==== GolfPointMainUpdDaoProc SQL ===" + sql.toString());

			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());
			int i = 0;		
		
			pstmt.setString(++i, data.getString("name").trim());		// ����
			pstmt.setString(++i, data.getString("com_nm").trim());		// ��ü��
			pstmt.setString(++i, data.getString("prs_nm").trim());		// ��ǥ�ڸ�
			pstmt.setString(++i, data.getString("zipcode1")+data.getString("zipcode2"));		// �����ȣ
			pstmt.setString(++i, data.getString("zipaddr").trim());		// �ּ�1
			pstmt.setString(++i, data.getString("detailaddr").trim());	// �ּ�2
			pstmt.setString(++i, data.getString("email").trim());		// �̸���
			pstmt.setString(++i, data.getString("tel1").trim());		// ��ȭ1
			pstmt.setString(++i, data.getString("tel2").trim());		// ��ȭ2
			pstmt.setString(++i, data.getString("tel3").trim());		// ��ȭ3
			pstmt.setString(++i, data.getString("fax1").trim());		// �ѽ�1
			pstmt.setString(++i, data.getString("fax2").trim());		// �ѽ�2
			pstmt.setString(++i, data.getString("fax3").trim());		// �ѽ�3
			pstmt.setString(++i, data.getString("hp_tel_no1").trim());	// �ڵ���1
			pstmt.setString(++i, data.getString("hp_tel_no2").trim());	// �ڵ���2
			pstmt.setString(++i, data.getString("hp_tel_no3").trim());	// �ڵ���3
			pstmt.setString(++i, data.getString("memo").trim());		// �޸𳻿�
			pstmt.setString(++i, data.getString("INDV_INFO_RPES_NM").trim());		// �޸𳻿�
			pstmt.setString(++i, data.getString("jumin_no").trim());		// �޸𳻿�
			pstmt.setString(++i, data.getString("p_idx").trim());		// seq

			
			res = pstmt.executeUpdate();

			if ( res == 1 ) {
				//debug("==== ���� success  ===");
				con.commit();
			}else{
				//debug("==== ���� fail  ===");
				con.rollback();
			}

			//debug("==== GolfAdmAuthUserUpdDaoProc ����ó�� End ===");
		}catch ( Exception e ) {
			//debug("==== GolfAdmAuthUserUpdDaoProc ����ó�� Error Start ===");
			
			//debug("==== GolfAdmAuthUserUpdDaoProc ����ó�� Error End ===");			
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new GolfException(msgEtt, e);
		}finally{
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return res;
	}

	/** ***********************************************************************
	* ���ڿ� parsing 
	************************************************************************ */
	private void parseStr(String orig_word, String final_word) {
		for(int index = 0; (index = temporary.indexOf(orig_word, index)) >= 0; index += final_word.length() )
			temporary = temporary.substring(0, index) + final_word + temporary.substring(index + orig_word.length());
	}

	/** ***********************************************************************
	* ���� ��ȯ
	************************************************************************ */
	public void nl2br() {
		this.parseStr("\n", "<br>");
		this.parseStr("'", "&#39;");
		this.parseStr("\"", "&#34;");
		this.parseStr("<%", "&#60;&#37;");
		this.parseStr("%>", "&#37;&#62;");
		this.parseStr("<", "&#60;");
		this.parseStr(">", "&#62;");
		this.parseStr("|", ":");
	}

	/** ***********************************************************************
	* ���ڿ� Setting
	************************************************************************ */
	public void strInput(String source) {
		temporary = source;
	}

	/** ***********************************************************************
	* ���ڿ� Getting
	************************************************************************ */
	public String strOutput() {
		return temporary;
	}

} 
