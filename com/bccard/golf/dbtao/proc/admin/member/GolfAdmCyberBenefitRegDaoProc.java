/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBenefitRegDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ ȸ�����ð��� ��� ó��
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
import javax.servlet.http.HttpSession;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-15
 **************************************************************************** */
public class GolfAdmCyberBenefitRegDaoProc extends AbstractProc {

	public static final String TITLE = "���̹��Ӵ� ��� ó��";
//	private String temporary;
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		PreparedStatement pstmt1 = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		//debug("==== GolfAdmCodeRegDaoProc start ===");
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id		= (String)userEtt.getMemId(); 							
			}
			
			//��ȸ ����
			String search_yn			= dataSet.getString("search_yn"); 		//�˻�����
			
			String search_clss		= "";									//�˻����
			String search_word		= "";									//�˻���

			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// �˻���
				search_word	= dataSet.getString("search_word"); 		// ����˻�����
			}
			long page_no 	= dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size 	= dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");

			String gn_BK		= dataSet.getString("GN_BK"); 
			String gn_BK_WD		= dataSet.getString("GN_BK_WD");
			String grn_PEE		= dataSet.getString("GRN_PEE"); 
			String par_BK		= dataSet.getString("PAR_BK"); 
			String drm			= dataSet.getString("DRM");
			String dv_RG		= dataSet.getString("DV_RG"); 
			String mode			= dataSet.getString("mode"); 			//ó������
			
			int res = 0;	
			

			//��Ͻ�
			if("ins".equals(mode))
			{
				String sql = this.getSelectQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, gn_BK);
				pstmt.setString(++pidx, gn_BK_WD);
				pstmt.setString(++pidx, grn_PEE);
				pstmt.setString(++pidx, par_BK);
				pstmt.setString(++pidx, drm);
				pstmt.setString(++pidx, dv_RG);
				pstmt.setString(++pidx, admin_id);

				res = pstmt.executeUpdate();
							
			}
			else if("upd".equals(mode))
			{
				String sql = this.getSelectDelQuery("");
				
				pstmt = con.prepareStatement(sql);
				res = pstmt.executeUpdate();
				
				String sql1 = this.getSelectQuery("");
				
				pstmt1 = con.prepareStatement(sql1);
				int pidx1 = 0;
				
				pstmt1.setString(++pidx1, gn_BK);
				pstmt1.setString(++pidx1, gn_BK_WD);
				pstmt1.setString(++pidx1, grn_PEE);
				pstmt1.setString(++pidx1, par_BK);
				pstmt1.setString(++pidx1, drm);
				pstmt1.setString(++pidx1, dv_RG);
				pstmt1.setString(++pidx1, admin_id);
				
				res = pstmt1.executeUpdate();
				
			}
			
			result = new DbTaoResult(TITLE);

			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			
			
			//debug("==== GolfAdmCodeRegDaoProc end ===");	
		}catch ( Exception e ) {
			//debug("==== GolfAdmCodeRegDaoProc ERROR ===");
			
			//debug("==== GolfAdmCodeRegDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(pstmt1 != null) pstmt1.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
				
	}
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGCBMOPLCYMGMT					");
		sql.append("\n	(GEN_WKD_BOKG_AMT,									");
		sql.append("\n	GEN_WKE_BOKG_AMT,											");
		sql.append("\n	WKD_GREEN_DC_AMT,											");
		sql.append("\n	PAR_3_BOKG_AMT,										");
		sql.append("\n	DRDS_AMT,									");
		sql.append("\n	DRVR_AMT,										");
		sql.append("\n	REG_MGR_ID,								");
		sql.append("\n	REG_ATON		)							");
		sql.append("\n	VALUES(?,?,?,?,?,?,	");
		sql.append("\n	?,											");
		sql.append("\n	TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'))				");

		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectDelQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGCBMOPLCYMGMT					");

		return sql.toString();
	}
	
	
}
