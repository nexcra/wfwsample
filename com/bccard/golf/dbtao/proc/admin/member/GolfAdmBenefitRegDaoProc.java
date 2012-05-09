/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBenefitRegDaoProc
*   작성자     : (주)미디어포스 조은미
*   내용        : 관리자 회원혜택관리 등록 처리
*   적용범위  : Golf
*   작성일자  : 2009-05-18
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
public class GolfAdmBenefitRegDaoProc extends AbstractProc {

	public static final String TITLE = "회원혜택 등록 처리 ";
//	private String temporary;
	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		GolfAdminEtt userEtt = null;
		String admin_id = "";
		
		//debug("==== GolfAdmBenefitRegDaoProc start ===");
		
		try{
			con = context.getDbConnection("default", null);
			
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_id		= (String)userEtt.getMemId(); 							
			}
			
			//조회 조건
			String search_yn			= dataSet.getString("search_yn"); 		//검색여부
			
			String search_clss		= "";									//검색어구분
			String search_word		= "";									//검색어

			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// 검색어
				search_word	= dataSet.getString("search_word"); 		// 제목검색여부
			}
			long page_no 	= dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size 	= dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");

			String mem_CLSS2		= dataSet.getString("MEM_CLSS2"); 		//등급	
			String gn_BK_WK_CNT		= dataSet.getString("GN_BK_WK_CNT"); 
			String gn_BK_WE_CNT		= dataSet.getString("GN_BK_WE_CNT"); 
			String gn_BK_LMT_YN		= dataSet.getString("GN_BK_LMT_YN");
			String pmi_BK_WK_CNT	= dataSet.getString("PMI_BK_WK_CNT"); 
			String pmi_BK_WE_CNT	= dataSet.getString("PMI_BK_WE_CNT"); 
			String pr_BK_APP_YN				= dataSet.getString("PR_BK_APP_YN"); 
			String pr_EVT_YEAR_CNT		= dataSet.getString("PR_EVT_YEAR_CNT");
			String pr_EVT_APP_YN		= dataSet.getString("PR_EVT_APP_YN"); 
			String par_BK_YEAR_CNT		= dataSet.getString("PAR_BK_YEAR_CNT"); 
			String par_BK_MON_CNT			= dataSet.getString("PAR_BK_MON_CNT"); 
			String par_BK_LMT_YN		= dataSet.getString("PAR_BK_LMT_YN");
			String par_BK_APP_YN				= dataSet.getString("PAR_BK_APP_YN"); 
			String grn_PEE_APP_YN		= dataSet.getString("GRN_PEE_APP_YN");
			String drm_YEAR_CNT		= dataSet.getString("DRM_YEAR_CNT"); 
			String drm_MON_CNT		= dataSet.getString("DRM_MON_CNT"); 
			String drm_LMT_YN			= dataSet.getString("DRM_LMT_YN"); 
			String drm_APP_YN		= dataSet.getString("DRM_APP_YN");
			String gf_RG_YEAR_CNT				= dataSet.getString("GF_RG_YEAR_CNT"); 
			String gf_RG_MON_CNT		= dataSet.getString("GF_RG_MON_CNT");
			String gf_RG_LMT_YN		= dataSet.getString("GF_RG_LMT_YN"); 
			String gf_RG_APP_YN		= dataSet.getString("GF_RG_APP_YN"); 
			String cpn_PRT_YEAR_CNT			= dataSet.getString("CPN_PRT_YEAR_CNT"); 
			String lms_VIP_YEAR_CNT		= dataSet.getString("LMS_VIP_YEAR_CNT");
			String lms_VIP_LMT_YN				= dataSet.getString("LMS_VIP_LMT_YN"); 
			String pkg_YEAR_CNT		= dataSet.getString("PKG_YEAR_CNT");
			String pkg_LMT_YN		= dataSet.getString("PKG_LMT_YN"); 
			String pkg_APP_YN		= dataSet.getString("PKG_APP_YN"); 
			String lms_APP_YN			= dataSet.getString("LMS_APP_YN"); 
			String jj_GN_APP_YN		= dataSet.getString("JJ_GN_APP_YN");
			String mgz_APP_YN				= dataSet.getString("MGZ_APP_YN"); 
			String bgn_LSN_APP_YN		= dataSet.getString("BGN_LSN_APP_YN");
			String pnt_LSN_APP_YN		= dataSet.getString("PNT_LSN_APP_YN"); 
			String rp_LSN_APP_YN		= dataSet.getString("RP_LSN_APP_YN"); 
			String pga_LSN_APP_YN			= dataSet.getString("PGA_LSN_APP_YN"); 
			String img_SW_APP_YN		= dataSet.getString("IMG_SW_APP_YN");
			String dv_APP_YN				= dataSet.getString("DV_APP_YN"); 
			String fd_APP_YN		= dataSet.getString("FD_APP_YN");
			String lsn_EVT_APP_YN		= dataSet.getString("LSN_EVT_APP_YN");
			String sp_LSN_APP_YN		= dataSet.getString("SP_LSN_APP_YN");
			String gf_MT_APP_YN		= dataSet.getString("GF_MT_APP_YN"); 
			String pmi_LESN_1_APO_YN	= dataSet.getString("PMI_LESN_1_APO_YN"); 
			String pmi_LESN_2_APO_YN	= dataSet.getString("PMI_LESN_2_APO_YN"); 
			String pmi_LESN_3_APO_YN	= dataSet.getString("PMI_LESN_3_APO_YN"); 
			String pmi_LESN_4_APO_YN	= dataSet.getString("PMI_LESN_4_APO_YN"); 
			String pmi_LESN_5_APO_YN	= dataSet.getString("PMI_LESN_5_APO_YN"); 
			String pmi_LESN_6_APO_YN	= dataSet.getString("PMI_LESN_6_APO_YN");
			String pmi_LESN_7_APO_YN	= dataSet.getString("PMI_LESN_7_APO_YN");
			int par_num					= dataSet.getInt("par_num"); 
			
			String p_idx			= dataSet.getString("p_idx");
			String mode				= dataSet.getString("mode"); 			//처리구분
			
			int res = 0;	
			
			//등록시
			if("ins".equals(mode))
			{
					String sql = this.getSelectQuery("");
					
					pstmt = con.prepareStatement(sql);
					int pidx = 0;
					
					pstmt.setString(++pidx, mem_CLSS2);
					pstmt.setString(++pidx, gn_BK_WK_CNT);
					pstmt.setString(++pidx, gn_BK_WE_CNT);
					pstmt.setString(++pidx, gn_BK_LMT_YN);
					pstmt.setString(++pidx, pmi_BK_WK_CNT);
					pstmt.setString(++pidx, pmi_BK_WE_CNT);
					pstmt.setString(++pidx, pr_BK_APP_YN);
					pstmt.setString(++pidx, pr_EVT_YEAR_CNT);
					pstmt.setString(++pidx, pr_EVT_APP_YN);
					pstmt.setString(++pidx, par_BK_YEAR_CNT);
					pstmt.setString(++pidx, par_BK_MON_CNT);
					pstmt.setString(++pidx, par_BK_LMT_YN);
					pstmt.setString(++pidx, par_BK_APP_YN);
					pstmt.setString(++pidx, grn_PEE_APP_YN);
					pstmt.setString(++pidx, drm_YEAR_CNT);
					pstmt.setString(++pidx, drm_MON_CNT);
					pstmt.setString(++pidx, drm_LMT_YN);
					pstmt.setString(++pidx, drm_APP_YN);
					pstmt.setString(++pidx, gf_RG_YEAR_CNT);
					pstmt.setString(++pidx, gf_RG_MON_CNT);
					pstmt.setString(++pidx, gf_RG_LMT_YN);
					pstmt.setString(++pidx, gf_RG_APP_YN);
					pstmt.setString(++pidx, cpn_PRT_YEAR_CNT);
					pstmt.setString(++pidx, lms_VIP_YEAR_CNT);
					pstmt.setString(++pidx, lms_VIP_LMT_YN);
					pstmt.setString(++pidx, pkg_YEAR_CNT);
					pstmt.setString(++pidx, pkg_LMT_YN);
					pstmt.setString(++pidx, pkg_APP_YN);
					pstmt.setString(++pidx, lms_APP_YN);
					pstmt.setString(++pidx, jj_GN_APP_YN);
					pstmt.setString(++pidx, mgz_APP_YN);
					pstmt.setString(++pidx, bgn_LSN_APP_YN);
					pstmt.setString(++pidx, pnt_LSN_APP_YN);
					pstmt.setString(++pidx, rp_LSN_APP_YN);
					pstmt.setString(++pidx, pga_LSN_APP_YN);
					pstmt.setString(++pidx, img_SW_APP_YN);
					pstmt.setString(++pidx, dv_APP_YN);
					pstmt.setString(++pidx, fd_APP_YN);
					pstmt.setString(++pidx, lsn_EVT_APP_YN);
					pstmt.setString(++pidx, sp_LSN_APP_YN);
					pstmt.setString(++pidx, gf_MT_APP_YN);
					pstmt.setString(++pidx, pmi_LESN_1_APO_YN);
					pstmt.setString(++pidx, pmi_LESN_2_APO_YN);
					pstmt.setString(++pidx, pmi_LESN_3_APO_YN);
					pstmt.setString(++pidx, pmi_LESN_4_APO_YN);
					pstmt.setString(++pidx, pmi_LESN_5_APO_YN);
					pstmt.setString(++pidx, pmi_LESN_6_APO_YN);
					pstmt.setString(++pidx, pmi_LESN_7_APO_YN);
					pstmt.setString(++pidx, admin_id);
					
					res = pstmt.executeUpdate();
								
			}
			else if("upd".equals(mode))
			{
				String sql = this.getSelectUpdQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, gn_BK_WK_CNT);
				pstmt.setString(++pidx, gn_BK_WE_CNT);
				pstmt.setString(++pidx, gn_BK_LMT_YN);
				pstmt.setString(++pidx, pmi_BK_WK_CNT);
				pstmt.setString(++pidx, pmi_BK_WE_CNT);
				pstmt.setString(++pidx, pr_BK_APP_YN);
				pstmt.setString(++pidx, pr_EVT_YEAR_CNT);
				pstmt.setString(++pidx, pr_EVT_APP_YN);
				pstmt.setString(++pidx, par_BK_YEAR_CNT);
				pstmt.setString(++pidx, par_BK_MON_CNT);
				pstmt.setString(++pidx, par_BK_LMT_YN);
				pstmt.setString(++pidx, par_BK_APP_YN);
				pstmt.setString(++pidx, grn_PEE_APP_YN);
				pstmt.setString(++pidx, drm_YEAR_CNT);
				pstmt.setString(++pidx, drm_MON_CNT);
				pstmt.setString(++pidx, drm_LMT_YN);
				pstmt.setString(++pidx, drm_APP_YN);
				pstmt.setString(++pidx, gf_RG_YEAR_CNT);
				pstmt.setString(++pidx, gf_RG_MON_CNT);
				pstmt.setString(++pidx, gf_RG_LMT_YN);
				pstmt.setString(++pidx, gf_RG_APP_YN);
				pstmt.setString(++pidx, cpn_PRT_YEAR_CNT);
				pstmt.setString(++pidx, lms_VIP_YEAR_CNT);
				pstmt.setString(++pidx, lms_VIP_LMT_YN);
				pstmt.setString(++pidx, pkg_YEAR_CNT);
				pstmt.setString(++pidx, pkg_LMT_YN);
				pstmt.setString(++pidx, pkg_APP_YN);
				pstmt.setString(++pidx, lms_APP_YN);
				pstmt.setString(++pidx, jj_GN_APP_YN);
				pstmt.setString(++pidx, mgz_APP_YN);
				pstmt.setString(++pidx, bgn_LSN_APP_YN);
				pstmt.setString(++pidx, pnt_LSN_APP_YN);
				pstmt.setString(++pidx, rp_LSN_APP_YN);
				pstmt.setString(++pidx, pga_LSN_APP_YN);
				pstmt.setString(++pidx, img_SW_APP_YN);
				pstmt.setString(++pidx, dv_APP_YN);
				pstmt.setString(++pidx, fd_APP_YN);
				pstmt.setString(++pidx, lsn_EVT_APP_YN);
				pstmt.setString(++pidx, sp_LSN_APP_YN);
				pstmt.setString(++pidx, gf_MT_APP_YN);
				pstmt.setString(++pidx, pmi_LESN_1_APO_YN);
				pstmt.setString(++pidx, pmi_LESN_2_APO_YN);
				pstmt.setString(++pidx, pmi_LESN_3_APO_YN);
				pstmt.setString(++pidx, pmi_LESN_4_APO_YN);
				pstmt.setString(++pidx, pmi_LESN_5_APO_YN);
				pstmt.setString(++pidx, pmi_LESN_6_APO_YN);
				pstmt.setString(++pidx, pmi_LESN_7_APO_YN);
				pstmt.setString(++pidx, admin_id);
				pstmt.setString(++pidx, p_idx);
				
				res = pstmt.executeUpdate();
				
			}
			else if("del".equals(mode))
			{
				String sql = this.getSelectDelQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, p_idx);

				res = pstmt.executeUpdate();
				
			}
			
			// 파3 골프장별 혜택 입력,수정
			int greenCnt = 0;
			for(int i=1; i<=par_num; i++){	
				// 해당 등급이 있는지 확인한다.
				String sql = this.getGreenCntQuery();				
				pstmt = con.prepareStatement(sql);
				int idx = 0;
				pstmt.setString(++idx, dataSet.getString("p_idx"));	
				pstmt.setString(++idx, "0001");	
				pstmt.setString(++idx, dataSet.getString("aff_green_seq_"+i));	
				rset = pstmt.executeQuery();
				if(rset.next()){
					greenCnt = rset.getInt("CNT");
					
					if(greenCnt==0){
						// insert
						pstmt = con.prepareStatement(getGreenInsQuery());
						idx = 0;
						pstmt.setString(++idx, dataSet.getString("p_idx"));
						pstmt.setString(++idx, "0001");
						pstmt.setString(++idx, dataSet.getString("aff_green_seq_"+i));
						pstmt.setString(++idx, dataSet.getString("yr_bokg_able_num_"+i));
						pstmt.setString(++idx, dataSet.getString("mo_bokg_able_num_"+i));
						res = pstmt.executeUpdate();
					}else{
						// update			
						pstmt = con.prepareStatement(getGreenUpdQuery());
						idx = 0;
						pstmt.setString(++idx, dataSet.getString("yr_bokg_able_num_"+i));
						pstmt.setString(++idx, dataSet.getString("mo_bokg_able_num_"+i));
						pstmt.setString(++idx, dataSet.getString("p_idx"));
						pstmt.setString(++idx, "0001");
						pstmt.setString(++idx, dataSet.getString("aff_green_seq_"+i));
						res = pstmt.executeUpdate();			
					}
				}
				
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
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
				
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO BCDBA.TBGGOLFCDHDBNFTMGMT					");
		sql.append("\n	(CDHD_SQ2_CTGO,									");
		sql.append("\n	GEN_WKD_BOKG_NUM,											");
		sql.append("\n	GEN_WKE_BOKG_NUM,											");
		sql.append("\n	GEN_BOKG_LIMT_YN,										");
		sql.append("\n	PMI_WKD_BOKG_NUM,											");
		sql.append("\n	PMI_WKE_BOKG_NUM,											");
		sql.append("\n	PMI_BOKG_APO_YN,									");
		sql.append("\n	PMI_EVNT_NUM,										");
		sql.append("\n	PMI_EVNT_APO_YN,										");
		sql.append("\n	PAR_3_BOKG_YR_ABLE_NUM,											");
		sql.append("\n	PAR_3_BOKG_MO_ABLE_NUM,											");
		sql.append("\n	PAR_3_BOKG_LIMT_YN,											");
		sql.append("\n	PAR_3_BOKG_APO_YN,										");
		sql.append("\n	WKD_GREEN_DC_APO_YN,									");
		sql.append("\n	DRDS_BOKG_YR_ABLE_NUM,										");
		sql.append("\n	DRDS_BOKG_MO_ABLE_NUM,										");
		sql.append("\n	DRDS_BOKG_LIMT_YN,											");
		sql.append("\n	DRDS_BOKG_APO_YN,											");
		sql.append("\n	DRGF_YR_ABLE_NUM,											");
		sql.append("\n	DRGF_MO_ABLE_NUM,										");
		sql.append("\n	DRGF_LIMT_YN,									");
		sql.append("\n	DRGF_APO_YN,										");
		sql.append("\n	CUPN_PRN_NUM,										");
		sql.append("\n	LMS_VIP_YR_ABLE_NUM,											");
		sql.append("\n	LMS_VIP_LIMT_YN,											");
		sql.append("\n	PKG_YR_ABLE_NUM,											");
		sql.append("\n	PKG_LIMT_YN,										");
		sql.append("\n	PKG_APO_YN,									");
		sql.append("\n	LMS_APO_YN,										");
		sql.append("\n	JEJU_GREEN_APO_YN,										");
		sql.append("\n	MGZ_SBSC_APO_YN,											");
		sql.append("\n	BGN_LESN_APO_YN,										");
		sql.append("\n	PNT_LESN_APO_YN,									");
		sql.append("\n	REP_LESN_APO_YN,										");
		sql.append("\n	PGA_LESN_APO_YN,										");
		sql.append("\n	IMG_SWI_APO_YN,											");
		sql.append("\n	DRVR_APO_YN,										");
		sql.append("\n	ETHS_APO_YN,										");
		sql.append("\n	SP_LESN_EVNT_APO_YN,											");
		sql.append("\n	SP_LESN_APO_YN,										");
		sql.append("\n	GOLF_TNMT_APO_YN,										");
		sql.append("\n	PMI_LESN_1_APO_YN,										");
		sql.append("\n	PMI_LESN_2_APO_YN,										");
		sql.append("\n	PMI_LESN_3_APO_YN,										");
		sql.append("\n	PMI_LESN_4_APO_YN,										");
		sql.append("\n	PMI_LESN_5_APO_YN,										");
		sql.append("\n	PMI_LESN_6_APO_YN,										");
		sql.append("\n	PMI_LESN_7_APO_YN,										");
		sql.append("\n	REG_MGR_ID,								");
		sql.append("\n	REG_ATON		)							");
		sql.append("\n	VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,	");
		sql.append("\n	?,											");
		sql.append("\n	TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS'))				");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectUpdQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE BCDBA.TBGGOLFCDHDBNFTMGMT	SET					");
		sql.append("\n	GEN_WKD_BOKG_NUM = ? ,											");
		sql.append("\n	GEN_WKE_BOKG_NUM = ? ,											");
		sql.append("\n	GEN_BOKG_LIMT_YN = ? ,										");
		sql.append("\n	PMI_WKD_BOKG_NUM = ? ,											");
		sql.append("\n	PMI_WKE_BOKG_NUM = ? ,											");
		sql.append("\n	PMI_BOKG_APO_YN = ? ,									");
		sql.append("\n	PMI_EVNT_NUM = ? ,										");
		sql.append("\n	PMI_EVNT_APO_YN = ? ,										");
		sql.append("\n	PAR_3_BOKG_YR_ABLE_NUM = ? ,											");
		sql.append("\n	PAR_3_BOKG_MO_ABLE_NUM = ? ,											");
		sql.append("\n	PAR_3_BOKG_LIMT_YN = ? ,											");
		sql.append("\n	PAR_3_BOKG_APO_YN = ? ,										");
		sql.append("\n	WKD_GREEN_DC_APO_YN = ? ,									");
		sql.append("\n	DRDS_BOKG_YR_ABLE_NUM = ? ,										");
		sql.append("\n	DRDS_BOKG_MO_ABLE_NUM  = ? ,										");
		sql.append("\n	DRDS_BOKG_LIMT_YN = ? ,											");
		sql.append("\n	DRDS_BOKG_APO_YN = ? ,											");
		sql.append("\n	DRGF_YR_ABLE_NUM = ? ,											");
		sql.append("\n	DRGF_MO_ABLE_NUM = ? ,										");
		sql.append("\n	DRGF_LIMT_YN = ? ,									");
		sql.append("\n	DRGF_APO_YN = ? ,										");
		sql.append("\n	CUPN_PRN_NUM = ? ,										");
		sql.append("\n	LMS_VIP_YR_ABLE_NUM = ? ,											");
		sql.append("\n	LMS_VIP_LIMT_YN = ? ,											");
		sql.append("\n	PKG_YR_ABLE_NUM = ? ,											");
		sql.append("\n	PKG_LIMT_YN = ? ,										");
		sql.append("\n	PKG_APO_YN = ? ,									");
		sql.append("\n	LMS_APO_YN = ? ,										");
		sql.append("\n	JEJU_GREEN_APO_YN = ? ,										");
		sql.append("\n	MGZ_SBSC_APO_YN = ? ,											");
		sql.append("\n	BGN_LESN_APO_YN = ? ,										");
		sql.append("\n	PNT_LESN_APO_YN = ? ,									");
		sql.append("\n	REP_LESN_APO_YN = ? ,										");
		sql.append("\n	PGA_LESN_APO_YN = ? ,										");
		sql.append("\n	IMG_SWI_APO_YN = ? ,											");
		sql.append("\n	DRVR_APO_YN = ? ,										");
		sql.append("\n	ETHS_APO_YN = ? ,										");
		sql.append("\n	SP_LESN_EVNT_APO_YN = ? ,											");
		sql.append("\n	SP_LESN_APO_YN = ? ,										");
		sql.append("\n	GOLF_TNMT_APO_YN = ? ,										");
		sql.append("\n	PMI_LESN_1_APO_YN = ? ,										");
		sql.append("\n	PMI_LESN_2_APO_YN = ? ,										");
		sql.append("\n	PMI_LESN_3_APO_YN = ? ,										");
		sql.append("\n	PMI_LESN_4_APO_YN = ? ,										");
		sql.append("\n	PMI_LESN_5_APO_YN = ? ,										");
		sql.append("\n	PMI_LESN_6_APO_YN = ? ,										");
		sql.append("\n	PMI_LESN_7_APO_YN = ? ,										");
		sql.append("\n	CHNG_MGR_ID = ? ,								");
		sql.append("\n	CHNG_ATON = TO_CHAR(SYSDATE, 'YYYYMMDDHH24MISS')");
		sql.append("\n	WHERE CDHD_SQ2_CTGO = ?	");

		return sql.toString();
	}
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectDelQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM BCDBA.TBGGOLFCDHDBNFTMGMT					");
		sql.append("\n	WHERE CDHD_SQ2_CTGO = ?	");

		return sql.toString();
	}
	
	public ArrayList getIns_list(ArrayList chk){

		ArrayList ins_list = new ArrayList();
		boolean equals_ok = false;

		for(int i = 0; i<chk.size(); i++){
			equals_ok = false;
			String c_idx = (String)chk.get(i);
		} // end for

		return ins_list;

	}
	
	/** ***********************************************************************
	* 해당 골프장 혜택관리가 등록되어 있는지 확인
	************************************************************************ */
	private String getGreenCntQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGCDHDGRDBNFT WHERE CDHD_SQ2_CTGO=? AND BOKG_KND=? AND AFFI_GREEN_SEQ_NO=?	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 해당 골프장 혜택관리 등록
	************************************************************************ */
	private String getGreenInsQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	INSERT INTO BCDBA.TBGCDHDGRDBNFT	\n");
		sql.append("\t	(CDHD_SQ2_CTGO, BOKG_KND, AFFI_GREEN_SEQ_NO, YR_BOKG_ABLE_NUM, MO_BOKG_ABLE_NUM)	\n");
		sql.append("\t	VALUES (?, ?, ?, ?, ?)	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 해당 골프장 혜택관리 수정  
	************************************************************************ */
	private String getGreenUpdQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGCDHDGRDBNFT SET YR_BOKG_ABLE_NUM=?, MO_BOKG_ABLE_NUM=?	\n");
		sql.append("\t	WHERE CDHD_SQ2_CTGO=? AND BOKG_KND=? AND AFFI_GREEN_SEQ_NO=?	\n");
		return sql.toString();
	}
		
}
