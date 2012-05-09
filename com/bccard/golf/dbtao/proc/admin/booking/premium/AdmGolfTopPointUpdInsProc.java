/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2010.12.29 [����]
* ���� : ����Ʈ���� ���� ���μ���
* ���� : 
* ���� : ����Ʈ���� ������ �����Ѵ�.
******************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection; 								// ������ �߰�
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;

import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;	// ������ �߰�
import com.bccard.waf.core.WaContext;				// ������ �߰�

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoException;
/** ****************************************************************************
 * ����Ʈ���� ���� ���� ���μ���
 * @author  ����
 * @version 2010.12.29
 **************************************************************************** */

public class AdmGolfTopPointUpdInsProc extends  AbstractProc {	// ������ �߰�

	public static final String TITLE = "����Ʈ���� ����";

		/** ***********************************************************************
		* Proc ����.
		* @param con Connection
		* @param dataSet ��ȸ��������
		************************************************************************ */

		public DbTaoResult execute(WaContext context, TaoDataSet dataSet) throws BaseException { // ������ �߰�

			String messageKey = "DbTaoFail_100";
			PreparedStatement pstmt = null;
			Connection con = null;

			DbTaoResult result = null;

			try{
					debug("*********************************************************************** ");
					debug("* AdmGolfTopPointUpdInsProc ����.                                     ");
					debug("* @param con Connection                                                 ");
					debug("* @param dataSet ��ȸ��������                                           ");
					debug("*********************************************************************** ");
					//��ȸ ���� Validation 
					String roundDate        = dataSet.getString("roundDate");
					String seqNo            = dataSet.getString("seqNo");
					String memId            = dataSet.getString("memId");
					String pointClss        = dataSet.getString("pointClss");
					String pointDetlCd      = dataSet.getString("pointDetlCd");
					String pointMemo        = dataSet.getString("pointMemo");
					String regIp            = dataSet.getString("regIp");
					String regAdminNo       = dataSet.getString("regAdminNo");
					String regAdminNm       = dataSet.getString("regAdminNm");
					if ( "".equals(seqNo) || "".equals(memId) ){
							messageKey = "DbTao_Noti_001";
							throw new Exception(messageKey);
					}

					con = context.getDbConnection("default", null);

					con.setAutoCommit(false);
					String sql = this.getSelectQuery();
					pstmt = con.prepareStatement(sql);
					int pidx = 0;
					
					pstmt.setString(++pidx, StrUtil.isNull(pointClss,""));          //����Ʈ����
					pstmt.setString(++pidx, StrUtil.isNull(pointDetlCd,""));        //����Ʈ�����ڵ�
					pstmt.setString(++pidx, StrUtil.isNull(pointMemo,""));          //����Ʈ����
					pstmt.setString(++pidx, StrUtil.isNull(roundDate,""));          //��������(�����)
					pstmt.setString(++pidx, StrUtil.isNull(regAdminNm,""));         //ó�������ڸ�
					pstmt.setString(++pidx, StrUtil.isNull(regIp,""));                      //ó��IP
					pstmt.setLong(++pidx, Long.parseLong(regAdminNo));                      //ó�������ڹ�ȣ
					pstmt.setLong(++pidx, Long.parseLong(memId));                           //ȸ����ȣ
					pstmt.setLong(++pidx, Long.parseLong(seqNo));                           //�Ϸù�ȣ
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
				try{ if(con != null) con.close();  }catch( Exception ignored){}  // ������ �߰�
			}
				return result;
		}

		/** ***********************************************************************
		* Query�� �����Ͽ� �����Ѵ�.    
		************************************************************************ */
		private String getSelectQuery() throws Exception{
		
				StringBuffer sql = new StringBuffer();
		
				sql.append("\n update BCDBA.TBGFPOINT set ");
				sql.append("\n POINT_CLSS=?, POINT_DETL_CD=?, POINT_MEMO=?, ROUND_DATE=?, ");
				sql.append("\n REG_DATE=to_char(sysdate,'YYYYMMDDHH24MISS'), ");
				sql.append("\n REG_ADMIN_NM=?, REG_IP=?, REG_ADMIN_NO=? ");
				sql.append("\n where MEMID=? and SEQ_NO=? ");
				return sql.toString();
		}
}
