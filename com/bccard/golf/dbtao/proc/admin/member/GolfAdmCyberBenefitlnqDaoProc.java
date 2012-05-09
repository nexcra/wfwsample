/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBenefitlnqDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ ȸ������ ��� ��ȸ 
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

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-14
 **************************************************************************** */
public class GolfAdmCyberBenefitlnqDaoProc extends AbstractProc {
	
	public static final String TITLE = "���̹��Ӵ� ��� ��ȸ";
	
	
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
		
		//debug("==== GolfAdmBenefitlnqDaoProc start ===");
		
		GolfConfig config = GolfConfig.getInstance();
		
		try{
			//��ȸ ����
			String search_yn	= dataSet.getString("search_yn"); 		//�˻�����
			String search_clss	= "";									//�˻����
			String search_word	= "";									//�˻���

			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// �˻���
				search_word	= dataSet.getString("search_word"); 		// ����˻�����
			}
			long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size = dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");
								

			String sql = this.getSelectQuery(search_yn);	
			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			
			pstmt.setLong(++pidx, page_no);
			
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				//curDateFormated = DateUtil.format(rset.getString("REG_DATE"),"yyyyMMdd","yyyy/MM/dd");
				
				result.addLong("row_num",					rset.getLong("RNUM"));
				result.addString("GN_BK",					rset.getString("GEN_WKD_BOKG_AMT"));
				result.addString("GN_BK_WD",					rset.getString("GEN_WKE_BOKG_AMT"));
				result.addString("GRN_PEE",						rset.getString("WKD_GREEN_DC_AMT"));
				result.addString("PAR_BK",					rset.getString("PAR_3_BOKG_AMT"));
				result.addString("DRM",					rset.getString("DRDS_AMT"));
				result.addString("DV_RG",			rset.getString("DRVR_AMT"));
				result.addString("total_cnt",				rset.getString("TOT_CNT") );
				result.addString("curr_page",				rset.getString("PAGE") );
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
			//debug("==== GolfAdmBenefitlnqDaoProc end ===");
						
		}catch ( Exception e ) {
			//debug("==== GolfAdmBenefitlnqDaoProc ERROR ===");
			
			//debug("==== GolfAdmBenefitlnqDaoProc ERROR ===");
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
	private String getSelectQuery(String search_yn) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*								");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,				");
		sql.append("\n 				GEN_WKD_BOKG_AMT,					");
		sql.append("\n 				GEN_WKE_BOKG_AMT,							");
		sql.append("\n 				WKD_GREEN_DC_AMT,							");
		sql.append("\n 				PAR_3_BOKG_AMT,							");
		sql.append("\n 				DRDS_AMT,						");
		sql.append("\n 				DRVR_AMT,						");
		sql.append("\n 				CEIL(ROWNUM/10) AS PAGE,	");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT	");	
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,		");
		sql.append("\n 					GEN_WKD_BOKG_AMT,					");
		sql.append("\n 					GEN_WKE_BOKG_AMT,							");
		sql.append("\n 					WKD_GREEN_DC_AMT,							");
		sql.append("\n 					PAR_3_BOKG_AMT,							");
		sql.append("\n 					DRDS_AMT,						");
		sql.append("\n 					DRVR_AMT						");
		sql.append("\n 				FROM BCDBA.TBGCBMOPLCYMGMT			");
		sql.append("\n 				WHERE  1=1 						");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
}
