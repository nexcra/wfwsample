/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGiftXlsDaoProc
*   작성자    : (주)미디어포스 조은미
*   내용      : 관리자 > 회원관리 >  사은품관리 > 엑셀
*   적용범위  : golf
*   작성일자  : 2009-08-25
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
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0  
 ******************************************************************************/
public class GolfAdmGiftXlsDaoProc extends AbstractProc {
	
	public static final String TITLE = "사은품관리 엑셀";
	/** *****************************************************************
	 * GolfAdmMemListDaoProc 프로세스 생성자   
	 * @param N/A 
	 ***************************************************************** */
	public GolfAdmGiftXlsDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws Exception {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		
		//debug("==== GolfAdmGiftXlsDaoProc start ==="); 

		try {
			conn = context.getDbConnection("default", null);
			
			String search_yn	= data.getString("search_yn"); 					//검색여부
			String sch_date_st	= "";											// 신청일
			String sch_date_ed	= "";											// 신청일
			String sch_type		= "";											// 검색조건
			String sch_text		= "";											// 내용
			String hp_tel		= "";
			String sece_yn		= "";
			String sece_status		= "";
			String snd_yn			= "";
			String snd_status		= "";
			String sch_snd_yn = "";
			
			//if("Y".equals(search_yn)){
				sch_date_st		= data.getString("sch_date_st");				// 신청일
				sch_date_ed		= data.getString("sch_date_ed");				// 신청일
				sch_type		= data.getString("sch_type");					// 검색조건
				sch_text		= data.getString("sch_text");						// 내용
				sch_snd_yn 		= data.getString("sch_snd_yn");
				
				sch_date_st = GolfUtil.rplc(sch_date_st, "-", "");
				sch_date_ed = GolfUtil.rplc(sch_date_ed, "-", "");
			//}	
			
			
			
			//조회 ----------------------------------------------------------
			String sql = this.getSelectQuery(search_yn, sch_date_st, sch_date_ed, sch_type, sch_text, sch_snd_yn);   
			
			// 입력값 (INPUT)    
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			 
			while(rs.next())  {	
				
				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				result.addLong("row_num",						rs.getLong("RNUM"));
				result.addString("hg_nm",						rs.getString("HG_NM"));
				result.addString("rcvr_nm",						rs.getString("RCVR_NM"));
				result.addString("golf_cmmn_code_nm",			rs.getString("GOLF_CMMN_CODE_NM"));
				result.addString("join_chnl",					rs.getString("JOIN_CHNL"));
				result.addString("join_chnl_nm",				rs.getString("JOIN_CHNL_NM"));
				result.addString("affi_firm_nm", 				rs.getString("AFFI_FIRM_NM"));
				result.addString("aplc_aton",					rs.getString("APLC_ATON"));
				result.addString("acrg_cdhd_jonn_date",			rs.getString("ACRG_CDHD_JONN_DATE"));
				hp_tel = rs.getString("HP_DDD_NO") + "-" + rs.getString("HP_TEL_HNO") + "-" + rs.getString("HP_TEL_SNO");
				result.addString("hp_tel",						hp_tel);
				result.addString("zp",							rs.getString("ZP"));
				result.addString("addr",						rs.getString("ADDR"));
				result.addString("dtl_addr",					rs.getString("DTL_ADDR"));
				
				sece_yn =rs.getString("SECE_YN");
				if ("Y".equals(sece_yn) ) {
					sece_status ="탈퇴";
				}
				else {
					sece_status ="정상";
				}
				
				snd_yn =rs.getString("SND_YN");
				if ("Y".equals(snd_yn) ) {
					snd_status ="발송";
				}
				else {
					snd_status ="미발송";
				}
				result.addString("sece_status",						sece_status);
				result.addString("snd_status",						snd_status);				
				
				existsData = true;
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			
		//	debug("==== GolfAdmGiftXlsDaoProc end ===");
			 
		} catch ( Exception e ) {
			
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	/** ***********************************************************************
    * Query를 생성하여 리턴한다.      
    ************************************************************************ */
    private String getSelectQuery(String search_yn, String sch_date_st, String sch_date_ed, String sch_type,
    		String sch_text, String sch_snd_yn) throws Exception{
        StringBuffer sql = new StringBuffer();        
        
        sql.append("\n  SELECT	*																										");
		sql.append("\n 	FROM	(SELECT	ROWNUM RNUM,																					");
		sql.append("\n 				HG_NM,																								");
		sql.append("\n 				JOIN_CHNL,																							");
		sql.append("\n 				JOIN_CHNL_NM,																						");
		sql.append("\n				AFFI_FIRM_NM,																						");
		sql.append("\n 				RCVR_NM,																							");
		sql.append("\n 				GOLF_CMMN_CODE_NM,																					");
		sql.append("\n 				HP_DDD_NO,																							");
		sql.append("\n 				HP_TEL_HNO,																							");
		sql.append("\n 				HP_TEL_SNO,																							");
		sql.append("\n 				ZP,																									");
		sql.append("\n 				ADDR,																								");
		sql.append("\n 				DTL_ADDR,																							");
		sql.append("\n 				ACRG_CDHD_JONN_DATE,																				");
		sql.append("\n 				APLC_ATON,																							");
		sql.append("\n 				SECE_YN,																							");
		sql.append("\n 				SND_YN																								");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM																				");
		sql.append("\n 						, TBG1.HG_NM																				");
		sql.append("\n 						, TBG1.JOIN_CHNL																			");
		sql.append("\n						, (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0051' AND TBG1.JOIN_CHNL = GOLF_CMMN_CODE) JOIN_CHNL_NM");
		sql.append("\n						, TBG1.AFFI_FIRM_NM																			");
		sql.append("\n 						, TBG3.RCVR_NM																				");
		sql.append("\n 						, TBG4.GOLF_CMMN_CODE_NM																	");
		sql.append("\n 						, TBG3.HP_DDD_NO																			");
		sql.append("\n 						, TBG3.HP_TEL_HNO																			");
		sql.append("\n 						, TBG3.HP_TEL_SNO																			");
		sql.append("\n 						, TBG3.ZP																					");
		sql.append("\n 						, TBG3.ADDR																					");
		sql.append("\n 						, TBG3.DTL_ADDR																				");
		sql.append("\n 						, to_char(to_date(TBG3.ACRG_CDHD_JONN_DATE,'yyyymmdd'), 'yyyy/mm/dd') AS ACRG_CDHD_JONN_DATE		");
		sql.append("\n 						, to_char(to_date(TBG3.APLC_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd') AS APLC_ATON			");
		sql.append("\n 						, TBG1.SECE_YN																				");
		sql.append("\n 						, TBG3.SND_YN																				");
		sql.append("\n 				FROM BCDBA.TBGGOLFCDHD TBG1 																		");
		//sql.append("\n 				JOIN BCDBA.TBGGOLFCDHDGRDMGMT TBG2 ON TBG1.CDHD_ID = TBG2.CDHD_ID AND TBG2.CDHD_CTGO_SEQ_NO = 5		");
		sql.append("\n 				JOIN BCDBA.TBGGOLFCDHDGRDMGMT TBG2 ON TBG1.CDHD_ID = TBG2.CDHD_ID AND TBG2.CDHD_CTGO_SEQ_NO  IN ('5', '6', '7')	");
		sql.append("\n 				JOIN BCDBA.TBGCDHDRIKMGMT TBG3 ON TBG1.CDHD_ID = TBG3.CDHD_ID										");
		sql.append("\n 				JOIN BCDBA.TBGCMMNCODE TBG4 ON TBG3.GOLF_TMNL_GDS_CODE = TBG4.GOLF_CMMN_CODE 						");
		sql.append("\n 					AND TBG4.GOLF_URNK_CMMN_CLSS = '0000' AND TBG4.GOLF_URNK_CMMN_CODE = '0044' AND TBG4.USE_YN = 'Y'		");
		sql.append("\n	            WHERE TBG1.CDHD_ID IS NOT NULL	\n");
		
		if(!GolfUtil.empty(sch_date_st))	sql.append("\t AND TBG3.APLC_ATON >='"+sch_date_st+"000000'\n");
		if(!GolfUtil.empty(sch_date_ed))	sql.append("\t AND TBG3.APLC_ATON <='"+sch_date_ed+"240000'\n");
		if(sch_type.equals("HG_NM") || sch_type.equals("CDHD_ID")){
			if(!GolfUtil.empty(sch_text))	sql.append("\t AND TBG1."+sch_type+" LIKE '%"+sch_text+"%'\n");
		}
		if(sch_type.equals("RCVR_NM") || sch_type.equals("HP_TEL_SNO")){
			if(!GolfUtil.empty(sch_text))	sql.append("\t AND TBG3."+sch_type+" LIKE '%"+sch_text+"%'\n");
		}
		if(!sch_snd_yn.equals("A") && !sch_snd_yn.equals("")) sql.append("\t AND TBG3.SND_YN = '"+sch_snd_yn+"' ");
		
		sql.append("\n 				ORDER BY TBG3.SEQ_NO DESC							");
		sql.append("\n 				)													");
		sql.append("\n 		ORDER BY RNUM 												");
		sql.append("\n 		)															");
		
		return sql.toString();
    }

}
