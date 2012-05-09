/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBenefitDetailInqDaoProc
*   작성자     : (주)미디어포스 조은미
*   내용        : 관리자 회원혜택관리 목록 조회 
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

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-03-31
 **************************************************************************** */
public class GolfAdmBenefitDetailInqDaoProc extends AbstractProc {
	
	public static final String TITLE = "회원혜택 관리  조회 ";
	
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
		
		//debug("==== GolfAdmBenefitDetailInqDaoProc start ===");
		
		try{
			//조회 조건

			String p_idx			= dataSet.getString("p_idx");
			String sql = this.getSelectQuery(p_idx);
			
			int pidx = 0;
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			pidx = 0;
			pstmt.setString(++pidx, p_idx);
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
							
				result.addString("MEM_CLSS2",				rset.getString("CDHD_SQ2_CTGO"));
				result.addString("GN_BK_WK_CNT",			rset.getString("GEN_WKD_BOKG_NUM"));
				result.addString("GN_BK_WE_CNT",			rset.getString("GEN_WKE_BOKG_NUM"));
				result.addString("GN_BK_LMT_YN",			rset.getString("GEN_BOKG_LIMT_YN"));
				result.addString("PMI_BK_WK_CNT",			rset.getString("PMI_WKD_BOKG_NUM"));
				result.addString("PMI_BK_WE_CNT",			rset.getString("PMI_WKE_BOKG_NUM"));
				result.addString("PMI_WKD_BOKG_NUM",		rset.getString("PMI_WKD_BOKG_NUM"));
				result.addString("PMI_WKE_BOKG_NUM",		rset.getString("PMI_WKE_BOKG_NUM"));
				result.addString("PR_BK_APP_YN",			rset.getString("PMI_BOKG_APO_YN"));
				result.addString("PR_EVT_YEAR_CNT",			rset.getString("PMI_EVNT_NUM"));
				result.addString("PR_EVT_APP_YN",			rset.getString("PMI_EVNT_APO_YN"));
				result.addString("PAR_BK_YEAR_CNT",			rset.getString("PAR_3_BOKG_YR_ABLE_NUM"));
				result.addString("PAR_BK_MON_CNT",			rset.getString("PAR_3_BOKG_MO_ABLE_NUM"));
				result.addString("PAR_BK_LMT_YN",			rset.getString("PAR_3_BOKG_LIMT_YN"));
				result.addString("PAR_BK_APP_YN",			rset.getString("PAR_3_BOKG_APO_YN"));
				result.addString("GRN_PEE_APP_YN",			rset.getString("WKD_GREEN_DC_APO_YN"));
				result.addString("DRM_YEAR_CNT",			rset.getString("DRDS_BOKG_YR_ABLE_NUM"));
				result.addString("DRM_MON_CNT",				rset.getString("DRDS_BOKG_MO_ABLE_NUM"));
				result.addString("DRM_LMT_YN",				rset.getString("DRDS_BOKG_LIMT_YN"));
				result.addString("DRM_APP_YN",				rset.getString("DRDS_BOKG_APO_YN"));
				result.addString("GF_RG_YEAR_CNT",			rset.getString("DRGF_YR_ABLE_NUM"));
				result.addString("GF_RG_MON_CNT",			rset.getString("DRGF_MO_ABLE_NUM"));
				result.addString("GF_RG_LMT_YN",			rset.getString("DRGF_LIMT_YN"));
				result.addString("GF_RG_APP_YN",			rset.getString("DRGF_APO_YN"));
				result.addString("CPN_PRT_YEAR_CNT",		rset.getString("CUPN_PRN_NUM"));
				result.addString("LMS_VIP_YEAR_CNT",		rset.getString("LMS_VIP_YR_ABLE_NUM"));
				result.addString("LMS_VIP_LMT_YN",			rset.getString("LMS_VIP_LIMT_YN"));
				result.addString("PKG_YEAR_CNT",			rset.getString("PKG_YR_ABLE_NUM"));
				result.addString("PKG_LMT_YN",				rset.getString("PKG_LIMT_YN"));
				result.addString("PKG_APP_YN",				rset.getString("PKG_APO_YN"));
				result.addString("LMS_APP_YN",				rset.getString("LMS_APO_YN"));
				result.addString("JJ_GN_APP_YN",			rset.getString("JEJU_GREEN_APO_YN"));
				result.addString("MGZ_APP_YN",				rset.getString("MGZ_SBSC_APO_YN"));
				result.addString("BGN_LSN_APP_YN",			rset.getString("BGN_LESN_APO_YN"));
				result.addString("PNT_LSN_APP_YN",			rset.getString("PNT_LESN_APO_YN"));
				result.addString("RP_LSN_APP_YN",			rset.getString("REP_LESN_APO_YN"));
				result.addString("PGA_LSN_APP_YN",			rset.getString("PGA_LESN_APO_YN"));
				result.addString("IMG_SW_APP_YN",			rset.getString("IMG_SWI_APO_YN"));
				result.addString("DV_APP_YN",				rset.getString("DRVR_APO_YN"));
				result.addString("FD_APP_YN",				rset.getString("ETHS_APO_YN"));
				result.addString("LSN_EVT_APP_YN",			rset.getString("SP_LESN_EVNT_APO_YN"));
				result.addString("SP_LSN_APP_YN",			rset.getString("SP_LESN_APO_YN"));
				result.addString("GF_MT_APP_YN",			rset.getString("GOLF_TNMT_APO_YN"));
				result.addString("PMI_LESN_1_APO_YN",		rset.getString("PMI_LESN_1_APO_YN"));
				result.addString("PMI_LESN_2_APO_YN",		rset.getString("PMI_LESN_2_APO_YN"));
				result.addString("PMI_LESN_3_APO_YN",		rset.getString("PMI_LESN_3_APO_YN"));
				result.addString("PMI_LESN_4_APO_YN",		rset.getString("PMI_LESN_4_APO_YN"));
				result.addString("PMI_LESN_5_APO_YN",		rset.getString("PMI_LESN_5_APO_YN"));
				result.addString("PMI_LESN_6_APO_YN",		rset.getString("PMI_LESN_6_APO_YN"));
				result.addString("PMI_LESN_7_APO_YN",		rset.getString("PMI_LESN_7_APO_YN"));
				result.addString("REG_MGR_ID",				rset.getString("REG_MGR_ID"));
				result.addString("CORR_MGR_ID",				rset.getString("CHNG_MGR_ID"));
				result.addString("REG_ATON",				rset.getString("REG_ATON"));
				result.addString("CORR_ATON",				rset.getString("CHNG_ATON"));
				
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

	public TaoResult execute_green(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		try{ 
			con = context.getDbConnection("default", null);
			
			String p_idx = dataSet.getString("p_idx");
			String bokg_knd = dataSet.getString("bokg_knd");
			int idx = 0;
			
			String sql = this.getParBnftQuery();				
			pstmt = con.prepareStatement(sql);			
			pstmt.setString(++idx, p_idx);		
			pstmt.setString(++idx, bokg_knd);

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				result.addString("AFFI_GREEN_SEQ_NO"	, rset.getString("AFFI_GREEN_SEQ_NO"));
				result.addString("GREEN_NM"				, rset.getString("GREEN_NM"));	
				result.addString("YR_BOKG_ABLE_NUM"		, rset.getString("YR_BOKG_ABLE_NUM"));			
				result.addString("MO_BOKG_ABLE_NUM"		, rset.getString("MO_BOKG_ABLE_NUM"));			
				result.addString("WKD_BOKG_ABLE_NUM"	, rset.getString("WKD_BOKG_ABLE_NUM"));			
				result.addString("WKE_BOKG_ABLE_NUM"	, rset.getString("WKE_BOKG_ABLE_NUM"));						
				existsData = true;
				
			}
			if(!existsData){
				result.addString("RESULT", "01");
			}
			
			
		}catch ( Exception e ) {		
						
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
	private String getSelectQuery(String p_idx) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 			SELECT								");
		sql.append("\n 				CDHD_SQ2_CTGO,					");
		sql.append("\n 				GEN_WKD_BOKG_NUM,							");
		sql.append("\n 				GEN_WKE_BOKG_NUM,							");
		sql.append("\n 				GEN_BOKG_LIMT_YN,							");
		sql.append("\n 				PMI_WKD_BOKG_NUM,				");
		sql.append("\n 				PMI_WKE_BOKG_NUM,				");
		sql.append("\n 				PMI_BOKG_APO_YN,							");
		sql.append("\n 				PMI_EVNT_NUM,						");
		sql.append("\n 				PMI_EVNT_APO_YN,						");
		sql.append("\n 				PAR_3_BOKG_YR_ABLE_NUM,						");
		sql.append("\n 				PAR_3_BOKG_MO_ABLE_NUM,						");
		sql.append("\n 				PAR_3_BOKG_LIMT_YN,				");
		sql.append("\n 				PAR_3_BOKG_APO_YN,						");
		sql.append("\n 				WKD_GREEN_DC_APO_YN,						");
		sql.append("\n 				DRDS_BOKG_YR_ABLE_NUM,			");
		sql.append("\n 				DRDS_BOKG_MO_ABLE_NUM,			");
		sql.append("\n 				DRDS_BOKG_LIMT_YN,			");
		sql.append("\n 				DRDS_BOKG_APO_YN,							");
		sql.append("\n 				DRGF_YR_ABLE_NUM,							");
		sql.append("\n 				DRGF_MO_ABLE_NUM,							");
		sql.append("\n 				DRGF_LIMT_YN,							");
		sql.append("\n 				DRGF_APO_YN,						");
		sql.append("\n 				CUPN_PRN_NUM,						");
		sql.append("\n 				LMS_VIP_YR_ABLE_NUM,						");
		sql.append("\n 				LMS_VIP_LIMT_YN,						");
		sql.append("\n 				PKG_YR_ABLE_NUM,						");
		sql.append("\n 				PKG_LIMT_YN,						");
		sql.append("\n 				PKG_APO_YN,				");
		sql.append("\n 				LMS_APO_YN,						");
		sql.append("\n 				JEJU_GREEN_APO_YN,						");
		sql.append("\n 				MGZ_SBSC_APO_YN,			");
		sql.append("\n 				BGN_LESN_APO_YN,			");
		sql.append("\n 				PNT_LESN_APO_YN,			");
		sql.append("\n 				REP_LESN_APO_YN,			");
		sql.append("\n 				PGA_LESN_APO_YN,			");
		sql.append("\n 				IMG_SWI_APO_YN,			");
		sql.append("\n 				DRVR_APO_YN,			");
		sql.append("\n 				ETHS_APO_YN,			");
		sql.append("\n 				SP_LESN_EVNT_APO_YN,			");
		sql.append("\n 				SP_LESN_APO_YN,			");
		sql.append("\n 				GOLF_TNMT_APO_YN,			");
		sql.append("\n 				PMI_LESN_1_APO_YN,			");
		sql.append("\n 				PMI_LESN_2_APO_YN,			");
		sql.append("\n 				PMI_LESN_3_APO_YN,			");
		sql.append("\n 				PMI_LESN_4_APO_YN,			");
		sql.append("\n 				PMI_LESN_5_APO_YN,			");
		sql.append("\n 				PMI_LESN_6_APO_YN,			");
		sql.append("\n 				PMI_LESN_7_APO_YN,			");
		sql.append("\n 				REG_MGR_ID,				"); 
		sql.append("\n 				CHNG_MGR_ID,				");
		sql.append("\n 				to_char(to_date(REG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS REG_ATON,				");	
		sql.append("\n 				to_char(to_date(CHNG_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd hh24:mi:ss') AS CHNG_ATON				");
		sql.append("\n 			FROM BCDBA.TBGGOLFCDHDBNFTMGMT	 TB		");
		sql.append("\n 			WHERE CDHD_SQ2_CTGO = ?							");


		return sql.toString();
	}
	
	/** ***********************************************************************
	* 파3 골프장별 혜택관리
	************************************************************************ */
	private String getParBnftQuery() throws Exception{
		StringBuffer sql = new StringBuffer();

		sql.append("	\n");
		sql.append("\t 	SELECT GREEN.AFFI_GREEN_SEQ_NO, GREEN.GREEN_NM	\n");
		sql.append("\t 	, YR_BOKG_ABLE_NUM, MO_BOKG_ABLE_NUM, WKD_BOKG_ABLE_NUM, WKE_BOKG_ABLE_NUM	\n");
		sql.append("\t 	FROM BCDBA.TBGAFFIGREEN GREEN	\n");
		sql.append("\t 	LEFT JOIN BCDBA.TBGCDHDGRDBNFT BNFT ON BNFT.AFFI_GREEN_SEQ_NO=GREEN.AFFI_GREEN_SEQ_NO AND CDHD_SQ2_CTGO=? AND BOKG_KND=?	\n");
		sql.append("\t 	WHERE AFFI_FIRM_CLSS='0002'	\n");
		return sql.toString();
	}
}
