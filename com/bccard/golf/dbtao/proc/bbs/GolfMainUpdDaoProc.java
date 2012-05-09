/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPointMainUpdDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ����(proc)
*   �������  : Golf
*   �ۼ�����  : 2009-03-23
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.bbs;

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
 * @version 2008-12-07
 **************************************************************************** */
public class GolfMainUpdDaoProc extends AbstractProc{

	public static final String TITLE = "�� ����PROC";
	private String temporary;
	/** *****************************************************************
	 * GolfPointMainUpdDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfMainUpdDaoProc() { 

	}
	
	/****************************************************************
	* DB UPDATE
	****************************************************************/
	public int execute(WaContext context, TaoDataSet dataSet) throws BaseException  {
		int res = 0;
		PreparedStatement pstmt = null;
		Connection con = null;

		try{
			//debug("==== GolfPointMainUpdDaoProc ����ó�� Start ===");
			//��� value
			String contTitle		= dataSet.getString("contTitle"); 				// ����
			String contText			= dataSet.getString("contText"); 			// ����1
			String comm_seqno		= dataSet.getString("comm_seqno");			//�� ������ ��ȣ
			String ip_addr			= dataSet.getString("ip_addr");				//������
//			String file_yn			= dataSet.getString("file_yn"); 			//����÷�ο���
//			String filesystem_name	= dataSet.getString("filesystem_name"); 	//���ε�� �����̸�
//			String orgin_file_name	= dataSet.getString("orgin_file_name"); 	//���� �����̸�
//			long fsize				= dataSet.getLong("fsize"); 				//���ϻ�����
//			String upFilePath		= dataSet.getString("upFilePath");			//���� path

//			this.strInput(comm_cont);
//			this.nl2br();
//			comm_cont = this.strOutput();

			StringBuffer sql = new StringBuffer();
			//debug("==== contTitle ===" + contTitle + "/n");
			
			sql.append("	UPDATE BBS					");
			sql.append("	SET							");
			sql.append("	TITLE  = ?					");
			sql.append("	, CONTENT = ?				");
			sql.append("	, UPD_DATE = sysdate		");
			sql.append("	, UPD_IP = ?				");
			sql.append("	WHERE SEQ = ?				");
			//debug("==== GolfPointMainUpdDaoProc SQL ===" + sql.toString());
			
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql.toString());
			int pidx = 0;
			pstmt.setString(++pidx, contTitle);
			pstmt.setString(++pidx, contText);
			pstmt.setString(++pidx, ip_addr);
			pstmt.setString(++pidx, comm_seqno);

			res = pstmt.executeUpdate();

			if ( res == 1 ) {
				//debug("==== ���� success  ===");
				con.commit();
			}else{
				//debug("==== ���� fail  ===");
				con.rollback();
			}

			//debug("==== GolfPointMainUpdDaoProc ����ó�� End ===");
		}catch ( Exception e ) {
			//debug("==== GolfPointMainUpdDaoProc ����ó�� Error Start ===");
			e.printStackTrace();
			//debug("==== GolfPointMainUpdDaoProc ����ó�� Error End ===");			
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new GolfException(msgEtt, e);
		}finally{
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
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
