/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : admGrListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 관리자 부킹 골프장 리스트 처리
*   적용범위  : golf
*   작성일자  : 2009-05-14
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfAdmMemBkViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmMemListDaoProc 프로세스 생성자    
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMemBkViewDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		String sql = "";
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);

			String memb_id	= data.getString("CDHD_ID");	
			int cdhd_sq2_ctgo = 0;		// 멤버십 차감 등급
			
			sql = this.getMemGradeQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, memb_id);
			rs = pstmt.executeQuery();
			if(rs != null && rs.next()) { 
				cdhd_sq2_ctgo = rs.getInt("CDHD_SQ2_CTGO");
			}
						
			
			sql = this.getSelectQuery(); 
			pstmt = conn.prepareStatement(sql);

			idx = 0;
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setInt(++idx, cdhd_sq2_ctgo);
			pstmt.setString(++idx, memb_id);
			pstmt.setString(++idx, memb_id);
			pstmt.setString(++idx, memb_id);
			rs = pstmt.executeQuery();						

			// 일반부킹
			String gen_txt = "";
			String gen_bokg_limt_yn = "";
			int gen_wkd_bokg_num = 0;
			int gen_wke_bokg_num = 0;
			int wkd_use = 0;
			int wkd_del = 0;
			int wke_use = 0;
			int wke_del = 0;

			// VIP부킹
			String pre_txt = "";
			String pmi_bokg_apo_yn = "";
			int pre_use = 0;

			// 파3부킹
			String par_txt = "";
			String par_3_bokg_limt_yn = "";
			int par_3_bokg_yr_able_num = 0;
			int par_use = 0;
			
			int club_use = 0;
			int nambu_use = 0;
			int seo_use = 0;
			int bnb_use = 0;
			
			String club_txt = "";		//클럽하이
			String nambu_txt = "";		//남부
			String seo_txt = "";		//서창
			String bnb_txt = "";		//bnb
			
			

			// VIP부킹이벤트
			String evt_txt = "";
			String pmi_evnt_apo_yn = "";
			int pmi_evnt_num = 0;
			int event_use = 0;
			
			// Sky72 드림듄스
			String sky_txt = "";
			String drds_bokg_limt_yn = "";
			int drds_bokg_yr_able_num = 0;
			int sky_use = 0;
			
			// Sky72 드림골프레인지
			String skyg_txt = "";
			String drgf_limt_yn = "";
			int drgf_yr_able_num = 0;
			int skyg_use = 0;

			// 주중 그린피할인
			String gr_txt = "";
			String wkd_green_dc_apo_yn = "";
			int gr_use = 0;
			int gr_del = 0;
			
			// 제주 그린피할인
			String jeju_txt = "";
			String jeju_green_apo_yn = "";
			int jeju_use = 0;
			
			if(rs != null) {
				while(rs.next()) {
					if(!gen_bokg_limt_yn.equals("A")){
						gen_bokg_limt_yn = rs.getString("GEN_BOKG_LIMT_YN");
					}
					gen_wkd_bokg_num += rs.getInt("GEN_WKD_BOKG_NUM");
					gen_wke_bokg_num += rs.getInt("GEN_WKE_BOKG_NUM");
					wkd_use += rs.getInt("WKD_USE");
					wkd_del += rs.getInt("WKD_DEL");
					wke_use += rs.getInt("WKE_USE");
					wke_del += rs.getInt("WKE_DEL");

					if(!pmi_bokg_apo_yn.equals("Y")){
						pmi_bokg_apo_yn = rs.getString("PMI_BOKG_APO_YN");
					}
					pre_use += rs.getInt("PRE_USE");
					

					if(!par_3_bokg_limt_yn.equals("Y")){
						par_3_bokg_limt_yn = rs.getString("PAR_3_BOKG_LIMT_YN");
					}
					par_3_bokg_yr_able_num += rs.getInt("PAR_3_BOKG_YR_ABLE_NUM");
					par_use += rs.getInt("PAR_USE");
					
					club_use += rs.getInt("CLUB_USE");
					nambu_use += rs.getInt("NAMBU_USE");
					seo_use += rs.getInt("SEO_USE");
					bnb_use += rs.getInt("BNB_USE");
					

					if(!pmi_evnt_apo_yn.equals("Y")){
						pmi_evnt_apo_yn = rs.getString("PMI_EVNT_APO_YN");
					}
					pmi_evnt_num += rs.getInt("PMI_EVNT_NUM");
					event_use += rs.getInt("EVENT_USE");

					if(!drds_bokg_limt_yn.equals("Y")){
						drds_bokg_limt_yn = rs.getString("DRDS_BOKG_LIMT_YN");
					}
					drds_bokg_yr_able_num += rs.getInt("DRDS_BOKG_YR_ABLE_NUM");
					sky_use += rs.getInt("SKY_USE");

					if(!drgf_limt_yn.equals("Y")){
						drgf_limt_yn = rs.getString("DRGF_LIMT_YN");
					}
					drgf_yr_able_num += rs.getInt("DRGF_YR_ABLE_NUM");
					skyg_use += rs.getInt("SKYG_USE");

					if(!wkd_green_dc_apo_yn.equals("Y")){
						wkd_green_dc_apo_yn= rs.getString("WKD_GREEN_DC_APO_YN");
					}
					gr_use += rs.getInt("GR_USE");
					gr_del += rs.getInt("GR_DEL");

					if(!jeju_green_apo_yn.equals("Y")){
						jeju_green_apo_yn= rs.getString("JEJU_GREEN_APO_YN");
					}
					jeju_use += rs.getInt("JEJU_USE");
				}
			}

			if(gen_bokg_limt_yn.equals("A")){
				gen_txt = (wkd_use+wke_use)-(wkd_del+wke_del)+" / 무제한";
			}else{
				gen_txt = (wkd_use+wke_use)-(wkd_del+wke_del)+" / "+(gen_wkd_bokg_num+gen_wke_bokg_num);
			}

			if(pmi_bokg_apo_yn.equals("Y")){
				pre_txt = pre_use+"";
			}else{
				pre_txt = "접근불가";
			}
			
			if(par_3_bokg_limt_yn.equals("Y")){
				par_txt = par_use+" / "+par_3_bokg_yr_able_num;
				club_txt = club_use+" / "+"6";
				seo_txt = seo_use+" / "+par_3_bokg_yr_able_num;
				bnb_txt = bnb_use+" / "+par_3_bokg_yr_able_num;
				nambu_txt = nambu_use+" / "+par_3_bokg_yr_able_num;
				
			}else{
				par_txt = "접근불가";
			}
			
			if(pmi_evnt_apo_yn.equals("Y")){
				evt_txt = event_use+" / "+pmi_evnt_num;
			}else{
				evt_txt = "접근불가";
			}
			
			if(drds_bokg_limt_yn.equals("Y")){
				sky_txt = sky_use+" / "+drds_bokg_yr_able_num;
			}else{
				sky_txt = "접근불가";
			}
			
			if(drgf_limt_yn.equals("Y")){
				skyg_txt = skyg_use+" / "+drgf_yr_able_num;
			}else{
				skyg_txt = "접근불가";
			}
			
			if(wkd_green_dc_apo_yn.equals("Y")){
				gr_txt = gr_use-gr_del+"";
			}else{
				gr_txt = "접근불가";
			}
			
			if(jeju_green_apo_yn.equals("Y")){
				jeju_txt = jeju_use+"";
			}else{
				jeju_txt = "접근불가";
			}

			result.addString("GEN_TXT" 			,gen_txt);		
			result.addString("PRE_TXT" 			,pre_txt);	
			result.addString("PAR_TXT" 			,par_txt);	
			
			result.addString("CLUB_TXT" 		,club_txt);
			result.addString("BNB_TXT" 			,bnb_txt);
			result.addString("SEO_TXT" 			,seo_txt);
			result.addString("NAMBU_TXT" 		,nambu_txt);
			
			result.addString("EVT_TXT" 			,evt_txt);	
			result.addString("SKY_TXT" 			,sky_txt);	
			result.addString("SKYG_TXT" 		,skyg_txt);	
			result.addString("GR_TXT" 			,gr_txt);	
			result.addString("JEJU_TXT" 		,jeju_txt);	
			
			result.addString("RESULT", "00"); //정상결과		
			
			
			
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	


	/** ***********************************************************************
    * 기본 등급을 가져온다.
    ************************************************************************ */
    private String getMemGradeQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT TO_NUMBER(CDHD_SQ2_CTGO) CDHD_SQ2_CTGO	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFCDHDGRDMGMT T1	\n");
		sql.append("\t	LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T2 ON T1.CDHD_CTGO_SEQ_NO=T2.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t	WHERE T1.CDHD_ID=? AND T2.CDHD_SQ1_CTGO='0002'	\n");		
		return sql.toString();
    }    
	    

	/** ***********************************************************************
    * 부킹 사용 내역을 가져온다.
    ************************************************************************ */
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	SELECT GRD_NM, GRD_SEQ, GRD_KN_SEQ, ST_DATE, ED_DATE	\n");
		
		sql.append("\t	, GEN_BOKG_LIMT_YN, GEN_WKD_BOKG_NUM, GEN_WKE_BOKG_NUM -- 일반부킹	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=T_BNF.MB_ID AND GOLF_SVC_APLC_CLSS='0006' AND PGRS_YN='Y' AND CSLT_YN='N' AND NVL(NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE) AS WKD_USE	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=T_BNF.MB_ID AND GOLF_SVC_APLC_CLSS='0006' AND PGRS_YN='N' AND CSLT_YN='N' AND NVL(NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE) AS WKD_DEL	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=T_BNF.MB_ID AND GOLF_SVC_APLC_CLSS='0007' AND PGRS_YN='Y' AND CSLT_YN='N' AND NVL(NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE) AS WKE_USE	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=T_BNF.MB_ID AND GOLF_SVC_APLC_CLSS='0007' AND PGRS_YN='N' AND CSLT_YN='N' AND NVL(NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE) AS WKE_DEL	\n");
		
		sql.append("\t	, PMI_BOKG_APO_YN    -- 프리미엄 부킹	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGAFFIGREEN T4 ON T1.AFFI_GREEN_SEQ_NO=T4.AFFI_GREEN_SEQ_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=T_BNF.MB_ID AND RSVT_YN='Y' and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='M'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(T1.REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(T1.REG_ATON,1,8)<=T_BNF.ED_DATE) AS PRE_USE	\n");
		
		sql.append("\t	, PAR_3_BOKG_LIMT_YN, PAR_3_BOKG_YR_ABLE_NUM -- 파3	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=T_BNF.MB_ID AND T1.RSVT_YN='Y'AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='P' AND NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE	\n");
		sql.append("\t	    AND T2.GOLF_SVC_RSVT_NO IS NULL) AS PAR_USE	\n");
		
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");	//클럽하이
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=T_BNF.MB_ID AND T1.RSVT_YN='Y'AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='P' AND NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE	\n");
		sql.append("\t	    AND T2.GOLF_SVC_RSVT_NO IS NULL AND T1.AFFI_GREEN_SEQ_NO = '339') AS CLUB_USE	\n");
		
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");	//남부골프연습장
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=T_BNF.MB_ID AND T1.RSVT_YN='Y'AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='P' AND NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE	\n");
		sql.append("\t	    AND T2.GOLF_SVC_RSVT_NO IS NULL AND T1.AFFI_GREEN_SEQ_NO = '340') AS NAMBU_USE	\n");
		
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");	//서창
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=T_BNF.MB_ID AND T1.RSVT_YN='Y'AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='P' AND NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE	\n");
		sql.append("\t	    AND T2.GOLF_SVC_RSVT_NO IS NULL AND T1.AFFI_GREEN_SEQ_NO = '363') AS SEO_USE	\n");
		
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");	//BNB
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=T_BNF.MB_ID AND T1.RSVT_YN='Y'AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='P' AND NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE	\n");
		sql.append("\t	    AND T2.GOLF_SVC_RSVT_NO IS NULL AND T1.AFFI_GREEN_SEQ_NO = '338') AS BNB_USE	\n");
		
		sql.append("\t	, PMI_EVNT_APO_YN, PMI_EVNT_NUM   -- 프리미엄 부킹 이벤트	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE GOLF_SVC_APLC_CLSS='9001' AND CDHD_ID=T_BNF.MB_ID and NVL(NUM_DDUC_YN,'Y')='Y' AND PGRS_YN='B'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE) AS EVENT_USE	\n");
		
		sql.append("\t	, DRDS_BOKG_LIMT_YN, DRDS_BOKG_YR_ABLE_NUM    -- 스카이 72 드림 듄스	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T4 ON T1.GOLF_SVC_RSVT_NO=T4.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=T_BNF.MB_ID AND RSVT_YN='Y' AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='S' AND NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ	\n");
		sql.append("\t	    AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE	\n");
		sql.append("\t	    AND T4.GOLF_SVC_RSVT_NO IS NULL) AS SKY_USE	\n");
		
		sql.append("\t	, DRGF_LIMT_YN, DRGF_YR_ABLE_NUM   -- 스카이 드림골프레인지	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t	    JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T4 ON T1.GOLF_SVC_RSVT_NO=T4.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=T_BNF.MB_ID AND RSVT_YN='Y' AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='D' AND NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND SUBSTR(T1.REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(T1.REG_ATON,1,8)<=T_BNF.ED_DATE	\n");
		sql.append("\t	    AND NVL(RSVT_CDHD_GRD_SEQ_NO,?)=T_BNF.GRD_SEQ AND T4.GOLF_SVC_RSVT_NO IS NULL) AS SKYG_USE	\n");
		
		sql.append("\t	, WKD_GREEN_DC_APO_YN   -- 스카이 드림골프레인지	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=T_BNF.MB_ID AND GOLF_SVC_APLC_CLSS='0008' AND PGRS_YN='Y' AND CSLT_YN='N' AND NVL(NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND GRD_KN_SEQ='2' AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE) AS GR_USE -- 그린피 할인	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	    WHERE CDHD_ID=T_BNF.MB_ID AND GOLF_SVC_APLC_CLSS='0008' AND PGRS_YN='N' AND CSLT_YN='N' AND NVL(NUM_DDUC_YN,'Y')='Y'	\n");
		sql.append("\t	    AND GRD_KN_SEQ='2' AND SUBSTR(REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(REG_ATON,1,8)<=T_BNF.ED_DATE) AS GR_DEL	\n");
		
		sql.append("\t	, JEJU_GREEN_APO_YN   -- 제주 그린피 할인	\n");
		sql.append("\t	, (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t	    LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T4 ON T1.GOLF_SVC_RSVT_NO=T4.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t	    WHERE T1.CDHD_ID=T_BNF.MB_ID AND RSVT_YN='Y' AND SUBSTR(T1.GOLF_SVC_RSVT_MAX_VAL,5,1)='J'	\n");
		sql.append("\t	    AND SUBSTR(T1.REG_ATON,1,8)>=T_BNF.ST_DATE AND SUBSTR(T1.REG_ATON,1,8)<=T_BNF.ED_DATE	\n");
		sql.append("\t	    AND GRD_KN_SEQ='2' AND T4.GOLF_SVC_RSVT_NO IS NULL) AS JEJU_USE	\n");
		
		sql.append("\t	FROM (SELECT GRD.CDHD_ID MB_ID	\n");
		sql.append("\t	    , TO_NUMBER(GRDM.CDHD_SQ2_CTGO) GRD_SEQ, CODE2.GOLF_CMMN_CODE_NM GRD_NM	\n");
		sql.append("\t	    , TO_NUMBER(GRDM.CDHD_SQ1_CTGO) GRD_KN_SEQ, CODE1.GOLF_CMMN_CODE_NM GRD_KN	\n");
		sql.append("\t	    , NVL(CASE GRDM.CDHD_SQ1_CTGO	\n");
		
		sql.append("\t	        	 WHEN '0001' THEN 		\n");
		sql.append("\t	        	 DECODE (GRD.CDHD_CTGO_SEQ_NO , 28, (SELECT   TO_CHAR(TO_DATE(CAMP_STRT_DATE,'YYYYMMDDHH24MISS'), 'YYYYMMDD')	\n");
		sql.append("\t	        										FROM BCDBA.TBACRGCDHDLODNTBL 												\n");
		sql.append("\t	        									    WHERE MEMO_EXPL = '0028'													\n");
		sql.append("\t	        									    AND PROC_RSLT_CTNT = ? 	) , 												\n");
		sql.append("\t	        										TO_CHAR(TO_DATE(SUBSTR(GRD.REG_ATON,1,8)),'YYYYMMDD') ) 					\n");
		
		sql.append("\t	    	WHEN '0002' THEN TO_CHAR(TO_DATE(MEM.ACRG_CDHD_JONN_DATE),'YYYYMMDD')	\n");
		sql.append("\t	    	END, CASE	\n");
		sql.append("\t				WHEN TO_CHAR(SYSDATE,'YYYY')||SUBSTR(JONN_ATON,5,4)>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(SYSDATE,'YYYY')-1||SUBSTR(JONN_ATON,5,4)	\n");
		sql.append("\t	        	ELSE TO_CHAR(SYSDATE,'YYYY')||SUBSTR(JONN_ATON,5,4) END) ST_DATE	\n");
		sql.append("\t		, NVL(CASE GRDM.CDHD_SQ1_CTGO	\n");
        
		sql.append("\t	        	WHEN '0001' THEN		\n");
		sql.append("\t	        	DECODE (GRD.CDHD_CTGO_SEQ_NO , 23, TO_CHAR(TO_DATE(GRD.REG_ATON,'YYYYMMDDHH24MISS')+INTERVAL '3' MONTH, 'YYYYMMDD')	\n");
		sql.append("\t	        								 , 24, TO_CHAR(TO_DATE(GRD.REG_ATON,'YYYYMMDDHH24MISS')+INTERVAL '3' MONTH, 'YYYYMMDD')	\n");
		sql.append("\t	        								 , 26, TO_CHAR(TO_DATE(GRD.REG_ATON,'YYYYMMDDHH24MISS')+INTERVAL '3' MONTH, 'YYYYMMDD')	\n");
		sql.append("\t	        								 , 28, (SELECT   TO_CHAR(TO_DATE(CAMP_END_DATE,'YYYYMMDDHH24MISS'), 'YYYYMMDD')	\n");
		sql.append("\t	        								 		FROM BCDBA.TBACRGCDHDLODNTBL \n");
		sql.append("\t	        										WHERE MEMO_EXPL = '0028'	\n");
		sql.append("\t	        										AND PROC_RSLT_CTNT = ? ),	\n");
		sql.append("\t	        										TO_CHAR(TO_DATE(SUBSTR(GRD.REG_ATON,1,8))+365,'YYYYMMDD'))	\n");
		
		sql.append("\t	   		WHEN '0002' THEN TO_CHAR(TO_DATE(MEM.ACRG_CDHD_END_DATE),'YYYYMMDD')	\n");
		sql.append("\t	    	END, CASE	\n");
		sql.append("\t	        	WHEN TO_CHAR(SYSDATE,'YYYY')||SUBSTR(JONN_ATON,5,4)>TO_CHAR(SYSDATE,'YYYYMMDD') THEN TO_CHAR(SYSDATE,'YYYY')||SUBSTR(JONN_ATON,5,4)	\n");
		sql.append("\t	        	ELSE TO_CHAR(SYSDATE,'YYYY')+1||SUBSTR(JONN_ATON,5,4) END) ED_DATE	\n");
		sql.append("\t		, GEN_BOKG_LIMT_YN, GEN_WKD_BOKG_NUM, GEN_WKE_BOKG_NUM	\n");
		sql.append("\t		, PMI_BOKG_APO_YN	\n");
		sql.append("\t		, PAR_3_BOKG_LIMT_YN, PAR_3_BOKG_YR_ABLE_NUM	\n");
		sql.append("\t		, PMI_EVNT_APO_YN, PMI_EVNT_NUM	\n");
		sql.append("\t		, DRDS_BOKG_LIMT_YN, DRDS_BOKG_YR_ABLE_NUM	\n");
		sql.append("\t		, DRGF_LIMT_YN, DRGF_YR_ABLE_NUM	\n");
		sql.append("\t		, WKD_GREEN_DC_APO_YN, JEJU_GREEN_APO_YN	\n");
		sql.append("\t		FROM BCDBA.TBGGOLFCDHDGRDMGMT GRD	\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDCTGOMGMT GRDM ON GRD.CDHD_CTGO_SEQ_NO=GRDM.CDHD_CTGO_SEQ_NO	\n");
		sql.append("\t		JOIN BCDBA.TBGCMMNCODE CODE1 ON GRDM.CDHD_SQ1_CTGO=CODE1.GOLF_CMMN_CODE AND CODE1.GOLF_CMMN_CLSS='0001'	\n");
		sql.append("\t		JOIN BCDBA.TBGCMMNCODE CODE2 ON GRDM.CDHD_SQ2_CTGO=CODE2.GOLF_CMMN_CODE AND CODE2.GOLF_CMMN_CLSS='0005'	\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHD MEM ON GRD.CDHD_ID=MEM.CDHD_ID	\n");
		sql.append("\t		JOIN BCDBA.TBGGOLFCDHDBNFTMGMT BNF ON BNF.CDHD_SQ2_CTGO=GRDM.CDHD_SQ2_CTGO	\n");
		sql.append("\t		WHERE GRD.CDHD_ID=?) T_BNF	\n");
		
		return sql.toString();
    }

}
