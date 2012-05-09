/******************************************************************************
* �� �ҽ��� �ߺ�ī�� �����Դϴ�.
* �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
* �ۼ� : 2004.10.28 [������]
* ���� : ���Ʈ���� �߰� ���μ���
* ���� : 
* ���� : ���Ʈ���� ������ �߰��Ѵ�.
******************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.action.AbstractProc;  
import com.bccard.waf.core.WaContext;  

import com.bccard.golf.msg.MsgEtt;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoException;
/** ****************************************************************************
 * ���Ʈ���� �߰� ���� ���μ���
 * @author ������
 * @version 2004.10.28
 **************************************************************************** */
public class GolfadmTopPenaltyNewInsProc extends AbstractProc  {
		public static final String TITLE = "���Ʈ���� ���";

		/** ***********************************************************************
		* Proc ����.
		* @param con Connection
		* @param dataSet ��ȸ��������
		************************************************************************ */
		public DbTaoResult execute(WaContext context, TaoDataSet dataSet) throws BaseException { // ������ �߰�    
				String messageKey = "DbTaoFail_100";
				PreparedStatement pstmt = null;
				DbTaoResult result = null;
				Connection con = null;
				
			try{
					//��ȸ ���� Validation 
					String memId	        = dataSet.getString("memId");				  //ȸ����ȣ
					String pointClss	= dataSet.getString("pointClss");			//�г�Ƽ����
					String pointDetlCd      = dataSet.getString("pointDetlCd");		//�г�Ƽ����
					String pointMemo	= dataSet.getString("pointMemo");			//���೻��
					long   affiGreenSeqNo	= dataSet.getLong("affiGreenSeqNo");				//�������ȣ
					String roundDate	= dataSet.getString("roundDate");			//��������
					String breachCont	= dataSet.getString("breachCont");		//���볻��
					String penaltyApplyClss	= dataSet.getString("penaltyApplyClss");	//���Ƽ���뱸��
					String setFrom		= dataSet.getString("setFrom");				//�����������
					String setTo		= dataSet.getString("setTo");						//������������
					String penaltyResnCd	= dataSet.getString("penaltyResnCd");	//���Ƽ�����ڵ�
					String regIp		= dataSet.getString("regIp");	              // �����IP
					String regAdminNo	= dataSet.getString("regAdminNo");        // ����ڻ��
					String regAdminNm	= dataSet.getString("regAdminNm");        // ����ڼ���

					if ( "".equals(memId) || "".equals(roundDate) ){
					    messageKey = "DbTao_Noti_001";
					    throw new Exception(messageKey);
					}
					
					con = context.getDbConnection("default", null);
					con.setAutoCommit(false);
					String sql = this.getSelectQuery();
		      pstmt = con.prepareStatement(sql);
		      
					int pidx = 0;
					pstmt.setLong(++pidx, Long.parseLong(memId));				//ȸ����ȣ
					pstmt.setLong(++pidx, Long.parseLong(memId));				//memId
					pstmt.setString(++pidx, StrUtil.isNull(pointClss,""));		//�г�Ƽ����
					pstmt.setString(++pidx, StrUtil.isNull(pointDetlCd,""));	//�г�Ƽ����
					pstmt.setString(++pidx, StrUtil.isNull(pointMemo,""));		//���೻��
					if(affiGreenSeqNo!=0L) pstmt.setLong(++pidx, affiGreenSeqNo);		//�������ȣ
					pstmt.setString(++pidx, StrUtil.isNull(roundDate,""));		//��������
					pstmt.setString(++pidx, StrUtil.isNull(breachCont,""));		//���볻��
					pstmt.setString(++pidx, StrUtil.isNull(penaltyApplyClss,""));	//���Ƽ���뱸��
					pstmt.setString(++pidx, StrUtil.isNull(setFrom,""));		//�����������
					pstmt.setString(++pidx, StrUtil.isNull(setTo,""));			//������������
					pstmt.setString(++pidx, StrUtil.isNull(penaltyResnCd,""));	//���Ƽ�����ڵ�
					pstmt.setString(++pidx, StrUtil.isNull(regAdminNm,""));		//ó�������ڸ�
					pstmt.setString(++pidx, StrUtil.isNull(regIp,""));			//ó��IP
					pstmt.setLong(++pidx, Long.parseLong(regAdminNo));			//ó�������ڹ�ȣ
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

		sql.append("\n insert into BCDBA.TBGFPOINT ");
		sql.append("\n (MEMID, SEQ_NO, POINT_CLSS, POINT_DETL_CD, POINT_MEMO, GREEN_NO, ");
		sql.append("\n ROUND_DATE, BREACH_CONT, PENALTY_APPLY_CLSS, SET_FROM, SET_TO, ");
		sql.append("\n PENALTY_RESN_CD, REG_DATE, REG_ADMIN_NM, REG_IP, REG_ADMIN_NO) ");
		sql.append("\n values (?, ");
		sql.append("\n (select lpad(nvl(max(SEQ_NO),'0')+1,10,'0') from BCDBA.TBGFPOINT where MEMID = ?), ");
		sql.append("\n ?, ?, ?, ?, ");
		sql.append("\n ?, ?, ?, ?, ?, ");
		sql.append("\n ?, to_char(sysdate,'YYYYMMDDHH24MISS'), ?, ?, ?) ");
		return sql.toString();
		}
}
