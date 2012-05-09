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
public class GolfAdmBenefitlnqDaoProc extends AbstractProc {
	
	public static final String TITLE = "ȸ�����ð��� ��� ��ȸ ";
	
	
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
			String pmi_lesn = "";	// �����̾� ������ ��� �ؽ�Ʈ
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				//curDateFormated = DateUtil.format(rset.getString("REG_DATE"),"yyyyMMdd","yyyy/MM/dd");
				
				result.addLong("row_num",					rset.getLong("RNUM"));
				result.addString("CD_NM",					rset.getString("GOLF_CMMN_CODE_NM"));
				result.addString("MEM_CLSS2",					rset.getString("CDHD_SQ2_CTGO"));
				result.addString("GN_BK_WK_CNT",						rset.getString("GEN_WKD_BOKG_NUM"));
				result.addString("GN_BK_WE_CNT",					rset.getString("GEN_WKE_BOKG_NUM"));
				result.addString("GN_BK_LMT_YN",					rset.getString("GEN_BOKG_LIMT_YN"));
				result.addString("PMI_BK_WK_CNT",					rset.getString("PMI_WKD_BOKG_NUM"));
				result.addString("PMI_BK_WE_CNT",					rset.getString("PMI_WKE_BOKG_NUM"));
				result.addString("PR_BK_APP_YN",					rset.getString("PMI_BOKG_APO_YN"));
				result.addString("PR_EVT_YEAR_CNT",			rset.getString("PMI_EVNT_NUM"));
				result.addString("PR_EVT_APP_YN",			rset.getString("PMI_EVNT_APO_YN"));
				result.addString("PAR_BK_YEAR_CNT",			rset.getString("PAR_3_BOKG_YR_ABLE_NUM"));
				result.addString("PAR_BK_MON_CNT",			rset.getString("PAR_3_BOKG_MO_ABLE_NUM"));
				result.addString("PAR_BK_LMT_YN",			rset.getString("PAR_3_BOKG_LIMT_YN"));
				result.addString("GRN_PEE_APP_YN",					rset.getString("WKD_GREEN_DC_APO_YN"));
				result.addString("DRM_YEAR_CNT",					rset.getString("DRDS_BOKG_YR_ABLE_NUM"));
				result.addString("DRM_MON_CNT",						rset.getString("DRDS_BOKG_MO_ABLE_NUM"));
				result.addString("DRM_LMT_YN",						rset.getString("DRDS_BOKG_LIMT_YN"));
				result.addString("GF_RG_YEAR_CNT",					rset.getString("DRGF_YR_ABLE_NUM"));
				result.addString("GF_RG_MON_CNT",					rset.getString("DRGF_MO_ABLE_NUM"));
				result.addString("GF_RG_LMT_YN",					rset.getString("DRGF_LIMT_YN"));
				
				pmi_lesn = "";
				if("Y".equals(rset.getString("PMI_LESN_1_APO_YN"))) pmi_lesn = pmi_lesn + "<nobr>���� �׷���Ʈ Ƽ�Ľ�1ź</nobr><br>";
				if("Y".equals(rset.getString("PMI_LESN_2_APO_YN"))) pmi_lesn = pmi_lesn + "�ܰ躰 ���� ����<br>";
				if("Y".equals(rset.getString("PMI_LESN_3_APO_YN"))) pmi_lesn = pmi_lesn + "������ ����<br>";
				if("Y".equals(rset.getString("PMI_LESN_4_APO_YN"))) pmi_lesn = pmi_lesn + "��Ȳ�� ����<br>";
				if("Y".equals(rset.getString("PMI_LESN_5_APO_YN"))) pmi_lesn = pmi_lesn + "�����Թ�<br>";
				if("Y".equals(rset.getString("PMI_LESN_6_APO_YN"))) pmi_lesn = pmi_lesn + "���� �׷���Ʈ Ƽ�Ľ�2ź<br>";
				if("Y".equals(rset.getString("PMI_LESN_7_APO_YN"))) pmi_lesn = pmi_lesn + "���� �׷���Ʈ Ƽ�Ľ�3ź<br>";
				
				result.addString("PMI_LESN",				pmi_lesn	);
				
				result.addString("REG_MGR_ID",				rset.getString("REG_MGR_ID"));
				result.addString("REG_ATON",				rset.getString("REG_ATON"));
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
		sql.append("\n 				GOLF_CMMN_CODE_NM,					");
		sql.append("\n 				CDHD_SQ2_CTGO,					");
		sql.append("\n 				GEN_WKD_BOKG_NUM,							");
		sql.append("\n 				GEN_WKE_BOKG_NUM,							");
		sql.append("\n 				GEN_BOKG_LIMT_YN,							");
		sql.append("\n 				PMI_WKD_BOKG_NUM,							");
		sql.append("\n 				PMI_WKE_BOKG_NUM,							");
		sql.append("\n 				PMI_BOKG_APO_YN,							");
		sql.append("\n 				PMI_EVNT_NUM,						");
		sql.append("\n 				PMI_EVNT_APO_YN,						");
		sql.append("\n 				PAR_3_BOKG_YR_ABLE_NUM,						");
		sql.append("\n 				PAR_3_BOKG_MO_ABLE_NUM,						");
		sql.append("\n 				PAR_3_BOKG_LIMT_YN,						");
		sql.append("\n 				WKD_GREEN_DC_APO_YN,						");
		sql.append("\n 				DRDS_BOKG_YR_ABLE_NUM,				");
		sql.append("\n 				DRDS_BOKG_MO_ABLE_NUM,						");
		sql.append("\n 				DRDS_BOKG_LIMT_YN,						");
		sql.append("\n 				DRGF_YR_ABLE_NUM,			");
		sql.append("\n 				DRGF_MO_ABLE_NUM,			");
		sql.append("\n 				DRGF_LIMT_YN,			");
		sql.append("\n 				PMI_LESN_1_APO_YN,			");
		sql.append("\n 				PMI_LESN_2_APO_YN,			");
		sql.append("\n 				PMI_LESN_3_APO_YN,			");
		sql.append("\n 				PMI_LESN_4_APO_YN,			");
		sql.append("\n 				PMI_LESN_5_APO_YN,			");
		sql.append("\n 				PMI_LESN_6_APO_YN,			");
		sql.append("\n 				PMI_LESN_7_APO_YN,			");
		sql.append("\n 				REG_MGR_ID,					");
		sql.append("\n 				REG_ATON,					");
		sql.append("\n 				CEIL(ROWNUM/30) AS PAGE,	");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT	");	
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,		");
		sql.append("\n 						A.GOLF_CMMN_CODE_NM,			");
		sql.append("\n 						B.CDHD_SQ2_CTGO,			");
		sql.append("\n 						B.GEN_WKD_BOKG_NUM,					");
		sql.append("\n 						B.GEN_WKE_BOKG_NUM,							");
		sql.append("\n 						B.GEN_BOKG_LIMT_YN,							");
		sql.append("\n 						B.PMI_WKD_BOKG_NUM,							");
		sql.append("\n 						B.PMI_WKE_BOKG_NUM,							");
		sql.append("\n 						B.PMI_BOKG_APO_YN,							");
		sql.append("\n 						B.PMI_EVNT_NUM,						");
		sql.append("\n 						B.PMI_EVNT_APO_YN,							");
		sql.append("\n 						B.PAR_3_BOKG_YR_ABLE_NUM,						");
		sql.append("\n 						B.PAR_3_BOKG_MO_ABLE_NUM,						");
		sql.append("\n 						B.PAR_3_BOKG_LIMT_YN,							");
		sql.append("\n 						B.WKD_GREEN_DC_APO_YN,						");
		sql.append("\n 						B.DRDS_BOKG_YR_ABLE_NUM,				");
		sql.append("\n 						B.DRDS_BOKG_MO_ABLE_NUM,						");
		sql.append("\n 						B.DRDS_BOKG_LIMT_YN,							");
		sql.append("\n 						B.DRGF_YR_ABLE_NUM,			");
		sql.append("\n 						B.DRGF_MO_ABLE_NUM,			");
		sql.append("\n 						B.DRGF_LIMT_YN,							");
		sql.append("\n 						B.PMI_LESN_1_APO_YN,							");
		sql.append("\n 						B.PMI_LESN_2_APO_YN,							");
		sql.append("\n 						B.PMI_LESN_3_APO_YN,							");
		sql.append("\n 						B.PMI_LESN_4_APO_YN,							");
		sql.append("\n 						B.PMI_LESN_5_APO_YN,							");
		sql.append("\n 						B.PMI_LESN_6_APO_YN,							");
		sql.append("\n 						B.PMI_LESN_7_APO_YN,							");
		sql.append("\n 						B.REG_MGR_ID,				");
		sql.append("\n 						to_char(to_date(B.REG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS REG_ATON				");	
		sql.append("\n 				FROM BCDBA.TBGCMMNCODE A			");
		sql.append("\n 				JOIN BCDBA.TBGGOLFCDHDBNFTMGMT B ON A.GOLF_CMMN_CODE = B.CDHD_SQ2_CTGO			");
		sql.append("\n 				WHERE  1=1 						");
		sql.append("\n 				AND A.GOLF_URNK_CMMN_CLSS = '0000' AND  A.GOLF_URNK_CMMN_CODE = '0005' AND A.USE_YN = 'Y'						");
		sql.append("\n 				ORDER BY B.CDHD_SQ2_CTGO ASC						");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
}
