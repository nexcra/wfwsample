/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBookingDetailInqDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��ǰ��� ��� ��ȸ 
*   �������  : Golf
*   �ۼ�����  : 2009-05-11
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-11  
 **************************************************************************** */
public class GolfAdmBookingDetailInqDaoProc extends AbstractProc {
	
	public static final String TITLE = "�Խ��� ����  ��ȸ";
	
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
		
		//debug("==== GolfAdmBookingDetailInqDaoProc start ===");
		
		try{
			//��ȸ ����

			String p_idx			= dataSet.getString("p_idx"); 

			String sql = this.getSelectQuery(p_idx);
				
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			pstmt.setString(++pidx, p_idx);
			
			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				
				
				result.addLong("BookingID",				rset.getLong("BookingID"));
				result.addString("Booking_CODE",		rset.getString("Booking_CODE"));
				result.addString("Booking_NM",			rset.getString("Booking_NM"));
				result.addString("USE_YN",				rset.getString("USE_YN"));
				result.addString("RG_SEQ_NO",		rset.getString("RG_SEQ_NO"));
				result.addString("UP_SEQ_NO",		rset.getString("UP_SEQ_NO"));
				result.addString("REG_DATE",			rset.getString("REG_DATE"));
				result.addString("MOD_DATE",			rset.getString("MOD_DATE"));
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfAdmBookingDetailInqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBookingDetailInqDaoProc ERROR ===");
			
			//debug("==== GolfAdmBookingDetailInqDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 				SELECT								");
		sql.append("\n 					BookingID,						");
		sql.append("\n 					Booking_CODE,					");
		sql.append("\n 					Booking_NM,						");
		sql.append("\n 					USE_YN,						");
		sql.append("\n 					(select ID from TBMGRINFO where MGR_SEQ_NO=TB.RG_SEQ_NO ) as RG_SEQ_NO,				");
		sql.append("\n 					(select ID from TBMGRINFO where MGR_SEQ_NO=TB.UP_SEQ_NO ) as UP_SEQ_NO,				");
		sql.append("\n 					REG_DATE,						");
		sql.append("\n 					MOD_DATE						");
		
		sql.append("\n 				FROM BCGOLF.TBBBRDMGMT	 TB		");
		sql.append("\n 				WHERE BookingID = ?							");


		return sql.toString();
	}
}
