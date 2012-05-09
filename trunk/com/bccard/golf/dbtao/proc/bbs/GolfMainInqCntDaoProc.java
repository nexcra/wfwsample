/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfPointMainInqCntDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �� ��ȸ�� ����
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
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.action.AbstractProc;

import com.bccard.golf.common.ResultException;

/** ****************************************************************************
 * golf 
 * @author
 * @version 2009-03-20
 **************************************************************************** */
public class GolfMainInqCntDaoProc extends AbstractProc{

	public static final String TITLE = "�� ��ȸ�� ����";

	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		DbTaoResult result = null;
		Connection con = null;

		try{
			//debug("==== GolfPointMainInqCntDaoProc Start ===");
			String comm_seqno		= dataSet.getString("comm_seqno");
			String comm_inq_cnt		= dataSet.getString("comm_inq_cnt");

			String sql = this.getSelectQuery();

			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			pstmt.setString(++pidx, comm_seqno);

			int res = pstmt.executeUpdate();

			result = new DbTaoResult(TITLE);

			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			//debug("==== GolfPointMainInqCntDaoProc end ===");

		}catch ( Exception e ) {
			//debug("==== GolfPointMainInqCntDaoProc ERROR Start ===");
			e.printStackTrace();
			//debug("==== GolfPointMainInqCntDaoProc ERROR End ===");
            //MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "�ý��ۿ����Դϴ�." );
			MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new DbTaoException(msgEtt,e);
		}finally{
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

		sql.append("\n	UPDATE BBS    	");
		sql.append("\n	SET HIT_CNT = HIT_CNT + 1  	");
		sql.append("\n	WHERE SEQ = ?  	");

		return sql.toString();
	}

}
