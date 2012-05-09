/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2010.12.07 [����]
* ���� : ���Ʈ���� ���� ���μ���
* ���� : 
* ���� : ���Ʈ���� ������ �����Ѵ�.
******************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.common.StrUtil;

import com.bccard.waf.action.AbstractProc; 
import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.BaseException;

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoException;
/** ****************************************************************************
 * ���Ʈ���� �߰� ���� ���μ���
 * @author  ����
 * @version 2010.12.07
 **************************************************************************** */
public class GolfadmTopPenaltyDelInsProc extends AbstractProc  {
		public static final String TITLE = "���Ʈ���� ����";

		/** ***********************************************************************
		* Proc ����.
		* @param con Connection
		* @param dataSet ��ȸ��������
		************************************************************************ */
		public DbTaoResult  execute(WaContext context, TaoDataSet dataSet) throws BaseException {
				String messageKey = "DbTaoFail_100";
				PreparedStatement pstmt = null;
				DbTaoResult result = null;
				Connection con = null;
				
			try{
			//��ȸ ���� Validation 
				String seqNo		= dataSet.getString("seqNo");
				String memId		= dataSet.getString("memId");

				if ( "".equals(seqNo) || "".equals(memId) ){
				    messageKey = "DbTao_Noti_001";
				    throw new Exception(messageKey);
				}
				
				con = context.getDbConnection("default", null);
				con.setAutoCommit(false);
				String sql = this.getSelectQuery();
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				pstmt.setLong(++pidx, Long.parseLong(memId));				//ȸ����ȣ
				pstmt.setLong(++pidx, Long.parseLong(seqNo));				//�Ϸù�ȣ
				int rset = pstmt.executeUpdate();

				if(rset != 1){  
					messageKey = "DbTao_Noti_201";
						throw new Exception(messageKey);
				}

				result = new DbTaoResult(TITLE);
				result.addString("result","00");
				con.commit();

			}catch(Exception e){
			try{ con.rollback(); }catch(Exception ee){}
						MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, messageKey, null );
						throw new DbTaoException(msgEtt,e);
			}finally{
			try{ if(pstmt != null) pstmt.close();  con.setAutoCommit(true); }catch( Exception ignored){}
			try{ if(con != null) con.close();  }catch( Exception ignored){}
			}
			return result;
		}

		/** ***********************************************************************
		* Query�� �����Ͽ� �����Ѵ�.    
		************************************************************************ */
		private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n delete from BCDBA.TBGFPOINT ");
		sql.append("\n where MEMID=? and SEQ_NO=? ");
			return sql.toString();
		}

}
