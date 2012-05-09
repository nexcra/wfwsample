/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPointMainDetailInqDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �� �󼼺���
*   �������  : Golf
*   �ۼ�����  : 2009-03-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.bbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;

/** ****************************************************************************
 * Community �� ��ȸ ���� Ŭ����.
 * @author
 * @version 2008-12-07
 **************************************************************************** */
public class GolfMainDetailInqDaoProc extends AbstractProc{

	public static final String TITLE = "Main �� ��ȸ";
	private String temporary;

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

		try{
			//debug("==== GolfPointMainDetailInqDaoProc Start ===");
			//��ȸ ���� Validation
			String comm_seqno	= dataSet.getString("comm_seqno"); 		//�˻����� 
			String comm_clss	= dataSet.getString("comm_clss");
			String update_yn	= dataSet.getString("update_yn");
			String comm_cont	= "";
			result = new DbTaoResult(TITLE);
			String curDateFormated = "";

			String sql = this.getSelectQuery();

			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			int pidx = 0;

			pstmt.setString(++pidx, comm_seqno);
			rset = pstmt.executeQuery();

			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			while(rset.next()){
				if(!existsData){
					result.addString("RESULT", "00");
				}

				curDateFormated = DateUtil.format(rset.getString("REG_DATE"),"yyyyMMdd","yyyy/MM/dd");

				result.addLong("row_seq", rset.getLong("SEQ"));
				result.addString("row_title", rset.getString("TITLE"));
				result.addString("row_date", curDateFormated);
			//	result.addString("content",			GolfUtil.getUrl(rset.getString("COMM_CONT")));
				if(update_yn.equals("Y")){
					this.strInput(rset.getString("content"));
					this.br2nlup();
					comm_cont = this.strOutput();
				}else {
					this.strInput(rset.getString("content"));
					this.br2nl();
					comm_cont = this.strOutput();
				}
				result.addString("row_content",	comm_cont);
				
				existsData = true;
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			
			//debug("==== GolfPointMainDetailInqDaoProc End ===");
		}catch ( Exception e ) {
			//debug("==== GolfPointMainDetailInqDaoProc ERROR Start ===");
			e.printStackTrace();
			//debug("==== GolfPointMainDetailInqDaoProc ERROR End ===");
            //MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new DbTaoException(msgEtt,e);
		}finally{
			try { if(rset != null) {rset.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return result;
	}

	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n 	SELECT	 SEQ, TITLE, REG_DATE, CONTENT	");
		sql.append("\n 	FROM BBS			");
		sql.append("\n 	WHERE SEQ=?			");

		return sql.toString();
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
	public void br2nl() {
		this.parseStr("<br>", "\n");
		this.parseStr("&#39;", "'");
		this.parseStr("&#34;", "\"");
		this.parseStr("&#60;&#37;", "<%");
		this.parseStr("&#37;&#62;", "%>");
		this.parseStr("&#60;", "<");
		this.parseStr("&#62;", ">");
	}

	/** ***********************************************************************
	* ���� ��ȯ  (�� ������)
	************************************************************************ */
	public void br2nlup() {
		this.parseStr("&#39;", "'");
		this.parseStr("&#34;", "\"");
		this.parseStr("&#60;&#37;", "<%");
		this.parseStr("&#37;&#62;", "%>");
		this.parseStr("&#60;", "<");
		this.parseStr("&#62;", ">");
		this.parseStr("<br>", "\n");
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
