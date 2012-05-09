/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBenefitDetailInqDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ ȸ�����ð��� ��� ��ȸ 
*   �������  : Golf
*   �ۼ�����  : 2009-05-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

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
 * @version 2009-03-31
 **************************************************************************** */
public class GolfAdmCyberBenefitDetailInqDaoProc extends AbstractProc {
	
	public static final String TITLE = "���̹��Ӵ� ����  ��ȸ";
	
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
		
		//debug("==== GolfAdmBenefitDetailInqDaoProc start ===");
		
		try{
			//��ȸ ����

			String sql = this.getSelectQuery();
			
			int pidx = 0;
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			pidx = 0;
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
							
				result.addString("GN_BK",				rset.getString("GEN_WKD_BOKG_AMT"));
				result.addString("GN_BK_WD",			rset.getString("GEN_WKE_BOKG_AMT"));
				result.addString("GRN_PEE",			rset.getString("WKD_GREEN_DC_AMT"));
				result.addString("PAR_BK",			rset.getString("PAR_3_BOKG_AMT"));
				result.addString("DRM",			rset.getString("DRDS_AMT"));
				result.addString("DV_RG",			rset.getString("DRVR_AMT"));
						
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			//debug("==== GolfAdmBenefitDetailInqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBenefitDetailInqDaoProc ERROR ===");
			
			//debug("==== GolfAdmBenefitDetailInqDaoProc ERROR ===");
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
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 			SELECT								");
		sql.append("\n 						GEN_WKD_BOKG_AMT,			");
		sql.append("\n 						GEN_WKE_BOKG_AMT,					");
		sql.append("\n 						WKD_GREEN_DC_AMT,							");
		sql.append("\n 						PAR_3_BOKG_AMT,							");
		sql.append("\n 						DRDS_AMT,						");
		sql.append("\n 						DRVR_AMT,						");
		sql.append("\n 						REG_MGR_ID,				");
		sql.append("\n 						to_char(to_date(REG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS REG_ATON				");	
		sql.append("\n 			FROM BCDBA.TBGCBMOPLCYMGMT	");


		return sql.toString();
	}
}
