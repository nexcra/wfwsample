/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBusDateInsDaoProc
*   �ۼ���    : (��)�̵������ �ǿ���
*   ����      : ������ > �̺�Ʈ->��������������̺�Ʈ->���� ��� ó��
*   �������  : Golf
*   �ۼ�����  : 2009-09-28
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.golfbus;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 *  Golf
 * @author	Media4th
 * @version 1.0
 **************************************************************************** */
public class GolfAdmBusDateInsDaoProc extends DbTaoProc {

	/**
	 * Proc ����. 
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(Connection con, TaoDataSet dataSet) throws TaoException {

		PreparedStatement pstmt				= null;
		ResultSet rs 						= null;
		String title						= dataSet.getString("title");
		String actnKey 						= null;
		DbTaoResult result					= new DbTaoResult(title);
		
		try {
		
			actnKey 						= dataSet.getString("actnKey");
			int res 						= 0;
			int pidx 						= 0;
			//��ȸ ����			
			String mode						= dataSet.getString("mode"); 			// ó������
			String reg_date					= dataSet.getString("reg_date").replaceAll("-", "");;			
			//��Ͻ�
			if("ins".equals(mode))
			{
				
				//�ش� ��¥�� �̹� ��ϵǾ��ִ��� üũ
				pstmt = con.prepareStatement(getCheckQuery());
				pidx = 0;				
				pstmt.setString(++pidx, reg_date);				
				rs = pstmt.executeQuery();
				
				if ( rs.next() ) 
				{
				
				}
				else
				{
					pstmt = con.prepareStatement(getInsQuery());
					pidx = 0;									
					pstmt.setString(++pidx, "9002");				
					pstmt.setString(++pidx, reg_date);
					pstmt.setString(++pidx, dataSet.getString("greenNm"));								
					res = pstmt.executeUpdate();
				}
				
				
			
			}
			
			else if("upd".equals(mode)) 
			{
				
							
				pstmt = con.prepareStatement(getUpdateQuery());
				pidx = 0;								
				pstmt.setString(++pidx, dataSet.getString("greenNm"));				
				pstmt.setString(++pidx, reg_date);
				
				res = pstmt.executeUpdate();							
				
			}
			else if("del".equals(mode))
			{
				
				pstmt = con.prepareStatement(getDeleteQuery());
				pidx = 0;					
				pstmt.setString(++pidx, reg_date);
				res = pstmt.executeUpdate();
				
			}
			
			
			if(res>0) {
				result.addString("RESULT","00");
				con.commit();
			}			
			else {
				result.addString("RESULT","01");
				con.rollback();
			}
			
		} catch(Exception e){
			// Ʈ������ �����϶��� �ѹ�			
			try { if( !con.getAutoCommit() ){ con.rollback(); } else {} } catch(Throwable ignore) {}			
			
		} finally {
			try { if( rs != null ){ rs.close(); } else {} } catch(Throwable ignore) {}
			try { if( pstmt != null ){ pstmt.close(); } else {} } catch(Throwable ignore) {}
			try { if( con != null ){ con.close(); } else {} } catch(Throwable ignore) {}
		}
		return result;
			
		
	}	
	
	
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getCheckQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	SELECT REG_DATE FROM BCDBA.TBGGREENEVNTSCD	 							");
		sql.append("\n	WHERE REG_DATE = ? 				");	
		return sql.toString();
	}	
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getInsQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
		sql.append("\n	INSERT INTO BCDBA.TBGGREENEVNTSCD	(							");
		sql.append("\n		GOLF_SVC_APLC_CLSS ,	 		");
		sql.append("\n		REG_DATE ,			");
		sql.append("\n		GREEN_NM 			");		
		sql.append("\n	) VALUES (				");
		sql.append("\n		?, ?, ? ");
		sql.append("\n	)	");
		return sql.toString();
	}	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getUpdateQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
		sql.append("\n	UPDATE BCDBA.TBGGREENEVNTSCD SET								");
		sql.append("\n		GREEN_NM = ? 									");			
		sql.append("\n	WHERE REG_DATE = ?  								");	
		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getDeleteQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGGREENEVNTSCD	 							");
		sql.append("\n	WHERE REG_DATE = ? 				");	
		return sql.toString();
	}	
	
}
