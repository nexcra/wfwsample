/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmCodeRegActn
*   작성자     : (주)미디어포스 조은미	
*   내용        : 관리자 게시판 관리 등록 처리
*   적용범위  : Golf
*   작성일자  : 2009-05-06
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.action.admin.member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.bccard.waf.core.*;
import com.bccard.waf.action.*;
import com.bccard.waf.common.*;
import com.bccard.waf.tao.*; 

import com.bccard.golf.common.ResultException;
import com.bccard.golf.dbtao.DbTaoDataSet;
import com.bccard.golf.dbtao.DbTaoConnection;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.proc.admin.member.GolfAdmBenefitRegDaoProc;

import com.bccard.golf.common.GolfException;
import com.bccard.golf.common.GolfActn;
import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.msg.MsgEtt;

/******************************************************************************
* Golf
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmBenefitRegActn extends GolfActn  {
	
	public static final String TITLE = "관리자 회원혜택 관리 등록 처리"; 
	
	/***************************************************************************************
	* 비씨골프 프로세스 
	* @param context		WaContext 객체. 
	* @param request		HttpServletRequest 객체. 
	* @param response		HttpServletResponse 객체. 
	* @return ActionResponse	Action 처리후 화면에 디스플레이할 정보. 
	***************************************************************************************/	
	public ActionResponse execute(WaContext context, HttpServletRequest request, HttpServletResponse response) throws BaseException {
		
		DbTaoConnection con = null;

		ResultException rx;

		//debug("==== GolfAdmCodeRegActn start ===");
		
		try {
			RequestParser	parser	= context.getRequestParser("default", request, response);
			//1. 파라메타 값 
			String search_yn	= parser.getParameter("search_yn", "N");					// 검색여부
			String mode			= parser.getParameter("mode", "ins");						// 처리구분
			String search_clss	= "";
			String search_word	= "";
			if("Y".equals(search_yn)){
				search_clss		= parser.getParameter("search_clss");						// 검색구분
				search_word		= parser.getParameter("search_word");						// 검색어
			}
			long page_no		= parser.getLongParameter("page_no", 1L);				// 페이지번호
			long page_size		= parser.getLongParameter("page_size", 10L);			// 페이지당출력수	

			String p_idx				= parser.getParameter("p_idx", "");
			String mem_CLSS2			= parser.getParameter("MEM_CLSS2"); 		//등급	
			String gn_BK_WK_CNT			= parser.getParameter("GN_BK_WK_CNT"); 
			String gn_BK_WE_CNT			= parser.getParameter("GN_BK_WE_CNT"); 
			String gn_BK_LMT_YN			= parser.getParameter("GN_BK_LMT_YN");
			String pmi_BK_WK_CNT		= parser.getParameter("PMI_BK_WK_CNT"); 
			String pmi_BK_WE_CNT		= parser.getParameter("PMI_BK_WE_CNT"); 
			String pr_BK_APP_YN			= parser.getParameter("PR_BK_APP_YN"); 
			String pr_EVT_YEAR_CNT		= parser.getParameter("PR_EVT_YEAR_CNT");
			String pr_EVT_APP_YN		= parser.getParameter("PR_EVT_APP_YN"); 
			String par_BK_YEAR_CNT		= parser.getParameter("PAR_BK_YEAR_CNT"); 
			String par_BK_MON_CNT		= parser.getParameter("PAR_BK_MON_CNT"); 
			String par_BK_LMT_YN		= parser.getParameter("PAR_BK_LMT_YN");
			String par_BK_APP_YN		= parser.getParameter("PAR_BK_APP_YN"); 
			String grn_PEE_APP_YN		= parser.getParameter("GRN_PEE_APP_YN");
			String drm_YEAR_CNT			= parser.getParameter("DRM_YEAR_CNT"); 
			String drm_MON_CNT			= parser.getParameter("DRM_MON_CNT"); 
			String drm_LMT_YN			= parser.getParameter("DRM_LMT_YN"); 
			String drm_APP_YN			= parser.getParameter("DRM_APP_YN");
			String gf_RG_YEAR_CNT		= parser.getParameter("GF_RG_YEAR_CNT"); 
			String gf_RG_MON_CNT		= parser.getParameter("GF_RG_MON_CNT");
			String gf_RG_LMT_YN			= parser.getParameter("GF_RG_LMT_YN"); 
			String gf_RG_APP_YN			= parser.getParameter("GF_RG_APP_YN"); 
			String cpn_PRT_YEAR_CNT		= parser.getParameter("CPN_PRT_YEAR_CNT"); 
			String lms_VIP_YEAR_CNT		= parser.getParameter("LMS_VIP_YEAR_CNT");
			String lms_VIP_LMT_YN		= parser.getParameter("LMS_VIP_LMT_YN"); 
			String pkg_YEAR_CNT			= parser.getParameter("PKG_YEAR_CNT");
			String pkg_LMT_YN			= parser.getParameter("PKG_LMT_YN"); 
			String pkg_APP_YN			= parser.getParameter("PKG_APP_YN"); 
			String lms_APP_YN			= parser.getParameter("LMS_APP_YN"); 
			String jj_GN_APP_YN			= parser.getParameter("JJ_GN_APP_YN");
			String mgz_APP_YN			= parser.getParameter("MGZ_APP_YN"); 
			String bgn_LSN_APP_YN		= parser.getParameter("BGN_LSN_APP_YN");
			String pnt_LSN_APP_YN		= parser.getParameter("PNT_LSN_APP_YN"); 
			String rp_LSN_APP_YN		= parser.getParameter("RP_LSN_APP_YN"); 
			String pga_LSN_APP_YN		= parser.getParameter("PGA_LSN_APP_YN"); 
			String img_SW_APP_YN		= parser.getParameter("IMG_SW_APP_YN");
			String dv_APP_YN			= parser.getParameter("DV_APP_YN"); 
			String fd_APP_YN			= parser.getParameter("FD_APP_YN");
			String lsn_EVT_APP_YN		= parser.getParameter("LSN_EVT_APP_YN");
			String sp_LSN_APP_YN		= parser.getParameter("SP_LSN_APP_YN");
			String gf_MT_APP_YN			= parser.getParameter("GF_MT_APP_YN"); 
			String pmi_LESN_1_APO_YN	= parser.getParameter("PMI_LESN_1_APO_YN"); 
			String pmi_LESN_2_APO_YN	= parser.getParameter("PMI_LESN_2_APO_YN"); 
			String pmi_LESN_3_APO_YN	= parser.getParameter("PMI_LESN_3_APO_YN"); 
			String pmi_LESN_4_APO_YN	= parser.getParameter("PMI_LESN_4_APO_YN"); 
			String pmi_LESN_5_APO_YN	= parser.getParameter("PMI_LESN_5_APO_YN"); 
			String pmi_LESN_6_APO_YN	= parser.getParameter("PMI_LESN_6_APO_YN");
			String pmi_LESN_7_APO_YN	= parser.getParameter("PMI_LESN_7_APO_YN");			
			int par_num					= parser.getIntParameter("par_num"); 	// 파3 골프장 갯수
			
			//2.조회
			DbTaoDataSet input = new DbTaoDataSet(TITLE);
			input.setString("search_yn",	search_yn);
			if("Y".equals(search_yn)){
				input.setString("search_clss",	search_clss);
				input.setString("search_word",	search_word);
			}
			input.setString("mode",		mode);
			input.setString("MEM_CLSS2",		mem_CLSS2);
			input.setString("GN_BK_WK_CNT",		gn_BK_WK_CNT);
			input.setString("GN_BK_WE_CNT",		gn_BK_WE_CNT);
			input.setString("GN_BK_LMT_YN",		gn_BK_LMT_YN);
			input.setString("PMI_BK_WK_CNT",		pmi_BK_WK_CNT);
			input.setString("PMI_BK_WE_CNT",		pmi_BK_WE_CNT);
			input.setString("PR_BK_APP_YN",		pr_BK_APP_YN);
			input.setString("PR_EVT_YEAR_CNT",		pr_EVT_YEAR_CNT);
			input.setString("PR_EVT_APP_YN",		pr_EVT_APP_YN);
			input.setString("MEM_CLSS2",		mem_CLSS2);
			input.setString("PAR_BK_YEAR_CNT",		par_BK_YEAR_CNT);
			input.setString("PAR_BK_MON_CNT",		par_BK_MON_CNT);
			input.setString("PAR_BK_LMT_YN",		par_BK_LMT_YN);
			input.setString("PAR_BK_APP_YN",		par_BK_APP_YN);
			input.setString("GRN_PEE_APP_YN",		grn_PEE_APP_YN);
			input.setString("DRM_YEAR_CNT",		drm_YEAR_CNT);
			input.setString("DRM_MON_CNT",		drm_MON_CNT);
			input.setString("DRM_LMT_YN",		drm_LMT_YN);
			input.setString("DRM_APP_YN",		drm_APP_YN);
			input.setString("GF_RG_YEAR_CNT",		gf_RG_YEAR_CNT);
			input.setString("GF_RG_MON_CNT",		gf_RG_MON_CNT);
			input.setString("GF_RG_LMT_YN",		gf_RG_LMT_YN);
			input.setString("GF_RG_APP_YN",		gf_RG_APP_YN);
			input.setString("CPN_PRT_YEAR_CNT",		cpn_PRT_YEAR_CNT);
			input.setString("LMS_VIP_YEAR_CNT",		lms_VIP_YEAR_CNT);
			input.setString("LMS_VIP_LMT_YN",		lms_VIP_LMT_YN);
			input.setString("PKG_YEAR_CNT",		pkg_YEAR_CNT);
			input.setString("PKG_LMT_YN",		pkg_LMT_YN);
			input.setString("PKG_APP_YN",		pkg_APP_YN);
			input.setString("LMS_APP_YN",		lms_APP_YN);
			input.setString("JJ_GN_APP_YN",		jj_GN_APP_YN);
			input.setString("MGZ_APP_YN",		mgz_APP_YN);
			input.setString("BGN_LSN_APP_YN",		bgn_LSN_APP_YN);
			input.setString("PNT_LSN_APP_YN",		pnt_LSN_APP_YN);
			input.setString("RP_LSN_APP_YN",		rp_LSN_APP_YN);
			input.setString("PGA_LSN_APP_YN",		pga_LSN_APP_YN);
			input.setString("IMG_SW_APP_YN",		img_SW_APP_YN);
			input.setString("DV_APP_YN",		dv_APP_YN);
			input.setString("FD_APP_YN",		fd_APP_YN);
			input.setString("LSN_EVT_APP_YN",		lsn_EVT_APP_YN);
			input.setString("SP_LSN_APP_YN",		sp_LSN_APP_YN);
			input.setString("GF_MT_APP_YN",		gf_MT_APP_YN);
			input.setString("PMI_LESN_1_APO_YN", pmi_LESN_1_APO_YN);
			input.setString("PMI_LESN_2_APO_YN", pmi_LESN_2_APO_YN);
			input.setString("PMI_LESN_3_APO_YN", pmi_LESN_3_APO_YN);
			input.setString("PMI_LESN_4_APO_YN", pmi_LESN_4_APO_YN);
			input.setString("PMI_LESN_5_APO_YN", pmi_LESN_5_APO_YN);
			input.setString("PMI_LESN_6_APO_YN", pmi_LESN_6_APO_YN);
			input.setString("PMI_LESN_7_APO_YN", pmi_LESN_7_APO_YN);
			input.setString("p_idx",		p_idx);
			input.setLong("page_no",		page_no);
			input.setLong("page_size",		page_size);
			input.setInt("par_num",		par_num);
			
//			debug("par_num : " + par_num);
			
			for(int i=1; i<=par_num; i++){		
				input.setString("aff_green_seq_"+i, parser.getParameter("AFFI_GREEN_SEQ_NO_"+i));
				input.setString("yr_bokg_able_num_"+i, parser.getParameter("YR_BOKG_ABLE_NUM_"+i));
				input.setString("mo_bokg_able_num_"+i, parser.getParameter("MO_BOKG_ABLE_NUM_"+i));
//				debug("i : " + i + " / aff_green_seq_ : " + parser.getParameter("AFFI_GREEN_SEQ_NO_"+i)
//						+ " / yr_bokg_able_num_ : " + parser.getParameter("YR_BOKG_ABLE_NUM_"+i)
//						+ " / mo_bokg_able_num_ : " + parser.getParameter("AFFI_GREEN_SEQ_NO_"+i)
//						);
			}
			
			
			
			
			Map paramMap = parser.getParameterMap();	
			
			// 3. DB 처리 
			GolfAdmBenefitRegDaoProc proc = (GolfAdmBenefitRegDaoProc)context.getProc("GolfAdmBenefitRegDaoProc");
			DbTaoResult benefitInq = (DbTaoResult)proc.execute(context, request, input);
				
			request.setAttribute("benefitInq", benefitInq);						
					
			request.setAttribute("paramMap", paramMap);
			
			//debug("==== GolfAdmCodeRegActn end ===");
			
		}catch(Throwable t) {
			return errorHandler(context,request,response,t);
		}finally{
			try{ if(con  != null) con.close();  }catch( Exception ignored){}
		}
		return super.getActionResponse(context);
		
	}
}
