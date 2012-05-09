/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : mainSeqnoInqDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �Խ��� ����/���ı� ��ȸ
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
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;

/** ****************************************************************************
 * golf 
 * @author
 * @version 2009-03-20
 **************************************************************************** */

public class GolfMainSeqnoInqDaoProc extends AbstractProc{

	public static final String TITLE = "�Խ��� ����/���ı� ��ȸ ";

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
			//debug("==== GolfPointMainSeqnoInqDaoProc Start ===");
			//��ȸ ���� Validation
			String comm_seqno	= dataSet.getString("comm_seqno"); 		//�˻�����
			String comm_clss	= dataSet.getString("comm_clss"); 				//�˻�����

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

				result.addLong("row_seq",	rset.getLong("SEQ"));
				result.addLong("row_pre_seq",		rset.getLong("PRE_SEQ"));
				result.addLong("row_next_seq",		rset.getLong("NEXT_SEQ"));
				result.addString("row_pre_title",	 		GolfUtil.getUrl(rset.getString("PRE_TITLE")));
				result.addString("row_next_title",		GolfUtil.getUrl(rset.getString("NEXT_TITLE")));

				existsData = true;
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			
			//debug("==== GolfPointMainSeqnoInqDaoProc End ===");
		}catch ( Exception e ) {
			//debug("==== GolfPointMainSeqnoInqDaoProc ERROR Start ===");
			e.printStackTrace();
			//debug("==== GolfPointMainSeqnoInqDaoProc ERROR End ===");
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

		sql.append("\n SELECT	A.*,															");
		sql.append("\n 			NVL((SELECT TITLE FROM BBS WHERE seq=A.PRE_SEQ),'') PRE_TITLE,	");
		sql.append("\n 			NVL((SELECT TITLE FROM BBS WHERE seq=A.NEXT_SEQ),'') NEXT_TITLE	");
		sql.append("\n FROM	(	SELECT SEQ,														");
		sql.append("\n 			NVL((LAG(SEQ,1) OVER (ORDER BY SEQ)),'') PRE_SEQ,				");
		sql.append("\n 			NVL((LEAD(SEQ,1) OVER (ORDER BY SEQ)),'') NEXT_SEQ				");
		sql.append("\n 			FROM BBS	");
		sql.append("\n 		) A																	");
		sql.append("\n WHERE SEQ=?														");

		return sql.toString();
	}

}
