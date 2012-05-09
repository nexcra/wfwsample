/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmAuthUserInsDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : Golf ��� ���� �Է� Proc
*   �������  : Golf
*   �ۼ�����  : 2009-05-06 
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.*;
import javax.servlet.http.*;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.common.GolfBbsConEtt;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.initech.dbprotector.CipherClient; //DB ��ȣȭ ����
import com.bccard.golf.common.BcLog;
import com.bccard.golf.common.JoinConEtt;

public class GolfAdmAuthUserInsDaoProc extends AbstractProc {

	public static final String TITLE = "Golf ��� ���� �Է�";
	/** *****************************************************************
	 * GolfPointMainInsDaoProc ���μ��� ������
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmAuthUserInsDaoProc() { 

	}
	
	public int execute(WaContext context,  TaoDataSet data) throws DbTaoException  {
		String returnCode = "0";

		GolfBbsConEtt tEtt = new GolfBbsConEtt();

		int result = 0;
		Connection con = null;
		PreparedStatement pstmt = null;
		StringBuffer sqlstr = new StringBuffer();
		DbTaoResult taoResult = null;

		try{
			String id_check_yn = "00";
			// id_check_yn : 00 �̸� �ߺ��Ǵ� ���̵���
			
			GolfAdmLoginlnqDaoProc proc = (GolfAdmLoginlnqDaoProc)context.getProc("GolfAdmLoginlnqDaoProc");
			taoResult = (DbTaoResult)proc.execute(context, data);	// ������ ��ȸ
			
			if(taoResult != null && taoResult.isNext() ) {
				//MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"��ϵ� ���̵� �ֽ��ϴ�.");
				//throw new DbTaoException(msgEtt);
				taoResult.next();
				//debug("RESULT : "+taoResult.getString("RESULT"));
				id_check_yn = taoResult.getString("RESULT");				
				result = -1;
			} else {
				id_check_yn = "01";
			}
			
			if("01".equals(id_check_yn))
			{
				JoinConEtt cEtt = new JoinConEtt();
				// �ؽ� ��й�ȣ ����
				/*
				String p_passwd		= data.getString("passwd").trim(); 		//��й�ȣ
				cEtt.setLogin_passwd(p_passwd.trim()); 				
				String hashpass = cEtt.getLogin_passwd();
				
				byte[] encData = CipherClient.encrypt(CipherClient.MASTERKEY1,hashpass.getBytes());
				*/

				
				int i=0;
				sqlstr.append("insert into BCDBA.TBGMGRINFO							\n");
				sqlstr.append("\t (MGR_ID, PASWD, HG_NM, FIRM_NM, RSV_NM			\n");
				sqlstr.append("\t , ZP, ADDR, DTL_ADDR, EMAIL						\n");
				sqlstr.append("\t , DDD_NO, TEL_HNO, TEL_SNO						\n");
				sqlstr.append("\t , FAX_DDD_NO, FAX_TEL_HNO, FAX_TEL_SNO			\n");
				sqlstr.append("\t , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO	 			\n");
				sqlstr.append("\t , MEMO_CTNT, REG_DATE, INDV_INFO_RPES_NM, JUMIN_NO)	\n");
				sqlstr.append("\t  VALUES											\n");
				sqlstr.append("\t (?, ?, ?, ?, ?									\n");
				sqlstr.append("\t , ?, ?, ?, ?										\n");
				sqlstr.append("\t , ?, ?, ?											\n");
				sqlstr.append("\t , ?, ?, ?											\n");
				sqlstr.append("\t , ?, ?, ?											\n");
				sqlstr.append("\t , ?, to_char(sysdate,'yyyymmdd'), ?, ?)				\n");
				//sqlstr.append("\t ?,to_char(sysdate,'yyyymmdd'),to_char(sysdate,'hh24miss'))");

				//debug("===== ���� : " + sqlstr.toString());

				con = context.getDbConnection("default", null);
				con.setAutoCommit(false); 

				pstmt = con.prepareStatement(sqlstr.toString());
				pstmt.setString(++i, data.getString("account").trim());		// ���̵�
				pstmt.setString(++i, data.getString("passwd").trim());		// ��ȣ				
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
				//pstmt.setBytes(++i, encData);								// ��ȣ	
				
				//���� ����
				result = pstmt.executeUpdate();
	
				if(result > 0) {
					con.commit();
					debug("===== INSERT ���� | ");
				} else {
					con.rollback();
					debug("===== INSERT ���� | ");
				}
			}
		}catch(Exception e){
			try	{
				con.rollback();
			}catch (Exception c){}
			debug("===== INSERT ���� | " + e + " ===========");
			
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new DbTaoException(msgEtt,e);
		}finally{
			if(pstmt != null) try{ pstmt.close(); }catch(Exception e){}
			if(con != null) try{ con.close(); }catch(Exception ex1){}
		}
		return result; 
	}
}
