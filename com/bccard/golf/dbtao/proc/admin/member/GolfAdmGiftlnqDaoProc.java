/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGiftlnqDaoProc
*   작성자     : (주)미디어포스 조은미
*   내용        : 관리자 사은품 목록 조회 
*   적용범위  : Golf
*   작성일자  : 2009-08-21
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.member;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-14
 **************************************************************************** */
public class GolfAdmGiftlnqDaoProc extends AbstractProc {
	
	public static final String TITLE = "사은품관리 목록 조회";
	
	public GolfAdmGiftlnqDaoProc() {}	
	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public DbTaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null; 
		Connection con = null;
		
		//debug("==== GolfAdmGiftlnqDaoProc start ===");
		
		try{
			//조회 조건
			int pidx = 0;
			long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size = dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");		
			con = context.getDbConnection("default", null);
			
			String search_yn		= dataSet.getString("search_yn"); 		//검색여부
			String sch_date_st		= dataSet.getString("sch_date_st");				// 신청일
			String sch_date_ed		= dataSet.getString("sch_date_ed");													// 신청일
			String sch_type			= dataSet.getString("sch_type");													// 검색조건
			String sch_text			= dataSet.getString("sch_text");													// 내용
			String sch_snd_yn		= dataSet.getString("sch_snd_yn");													// 발송여부
			String sch_join_chnl 	= dataSet.getString("sch_join_chnl");													// 가입경로
			
			if(!"".equals(sch_date_st)) GolfUtil.rplc(sch_date_st,"-","");
			if(!"".equals(sch_date_ed)) GolfUtil.rplc(sch_date_ed,"-","");
			
			String hp_tel		= "";
			String sece_yn		= "";
			String sece_status	= "";
			String snd_yn		= "";
			String snd_status	= "";
					
			
			String sql = this.getSelectQuery(search_yn, sch_date_st, sch_date_ed, sch_type, sch_text, sch_snd_yn , sch_join_chnl);
			pstmt = con.prepareStatement(sql);	

			pstmt.setLong(++pidx, page_size);
			pstmt.setLong(++pidx, page_no);
			pstmt.setLong(++pidx, page_no);
			
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			int art_num_no = 0;
			 
			while(rset.next()){
				
				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				
				result.addInt("art_num" 						,rset.getInt("ART_NUM")-art_num_no );
				result.addString("TOT_CNT"						,rset.getString("TOT_CNT") );
				result.addString("CURR_PAGE"					,rset.getString("PAGE") );
				result.addLong("row_num",						rset.getLong("RNUM"));
				result.addString("hg_nm",						rset.getString("HG_NM"));
				result.addString("cdhd_id",						rset.getString("CDHD_ID"));
				result.addString("join_chnl",					rset.getString("JOIN_CHNL"));
				result.addString("join_chnl_nm",				rset.getString("JOIN_CHNL_NM"));
				result.addString("affi_firm_nm", 				rset.getString("AFFI_FIRM_NM"));
				result.addString("rcvr_nm",						rset.getString("RCVR_NM"));
				result.addString("golf_cmmn_code_nm",			rset.getString("GOLF_CMMN_CODE_NM"));
				result.addString("aplc_aton",					rset.getString("APLC_ATON"));
				result.addString("seq_no",						rset.getString("SEQ_NO"));
				hp_tel = rset.getString("HP_DDD_NO") + "-" + rset.getString("HP_TEL_HNO") + "-" + rset.getString("HP_TEL_SNO");
				result.addString("hp_tel",						hp_tel);
				
				sece_yn =rset.getString("SECE_YN");
				if ("Y".equals(sece_yn) ) {
					sece_status ="탈퇴";
				}
				else {
					sece_status ="정상";
				}
				
				snd_yn =rset.getString("SND_YN");
				if ("Y".equals(snd_yn) ) {
					snd_status ="발송";
				}
				else {
					snd_status ="미발송";
				}
				result.addString("sesc_yn", 						sece_yn);
				result.addString("snd_yn", 							snd_yn);
				result.addString("sece_status",						sece_status);
				result.addString("snd_status",						snd_status);
				
				existsData = true;
				art_num_no++;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
			//debug("==== GolfAdmGiftlnqDaoProc end ===");
						
		}catch ( Exception e ) {
			//debug("==== GolfAdmCodelnqDaoProc ERROR ===");
			e.printStackTrace();
			//debug("==== GolfAdmCodelnqDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}

	/** ***********************************************************************
	* Proc 실행.
	* @param con Connection
	* @param dataSet 조회조건정보
	************************************************************************ */
	public DbTaoResult getJoinChnlInq(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		try{
			
			String sql = this.getSelectJoinChnlQuery();
			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			
			rset = pstmt.executeQuery();
			boolean existsData = false;
			result = new DbTaoResult(TITLE);
			
			while(rset.next()){
				if(!existsData) result.addString("RESULT", "00");
				result.addString("CODE", rset.getString("GOLF_CMMN_CODE"));
				result.addString("NM", rset.getString("GOLF_CMMN_CODE_NM"));
				
				existsData = true;
				
			}
			
			if(!existsData) result.addString("RESULT","01");
			
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmCodelnqDaoProc ERROR ===");
			e.printStackTrace();
			//debug("==== GolfAdmCodelnqDaoProc ERROR ===");
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
	private String getSelectJoinChnlQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
		
		sql.append("\n		SELECT  GOLF_CMMN_CODE							");
		sql.append("\n 				,GOLF_CMMN_CODE_NM						");
		sql.append("\n		FROM BCDBA.TBGCMMNCODE 							");
		sql.append("\n		WHERE GOLF_CMMN_CLSS='0051'	 AND USE_YN='Y'		");
		
		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다. 
	************************************************************************ */
	private String getSelectQuery(String search_yn, String sch_date_st, String sch_date_ed, 
			String sch_type, String sch_text, String sch_snd_yn, String sch_join_chnl) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*																										");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,																						");
		sql.append("\n 				HG_NM,																								");
		sql.append("\n 				CDHD_ID,																							");
		sql.append("\n 				JOIN_CHNL,																							");
		sql.append("\n 				JOIN_CHNL_NM,																						");
		sql.append("\n				AFFI_FIRM_NM,																						");
		sql.append("\n 				RCVR_NM,																							");
		sql.append("\n 				GOLF_CMMN_CODE_NM,																					");
		sql.append("\n 				HP_DDD_NO,																							");
		sql.append("\n 				HP_TEL_HNO,																							");
		sql.append("\n 				HP_TEL_SNO,																							");
		sql.append("\n 				APLC_ATON,																							");
		sql.append("\n 				SECE_YN,																							");
		sql.append("\n 				SND_YN,																								");
		sql.append("\n 				SEQ_NO,																								");
		sql.append("\n 				CEIL(ROWNUM/ ? ) AS PAGE,																			");
		sql.append("\n				((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM,															");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT																			");	
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM																				");
		sql.append("\n 						, TBG1.HG_NM																				");
		sql.append("\n 						, TBG1.CDHD_ID																				");
		sql.append("\n 						, TBG1.JOIN_CHNL																			");
		sql.append("\n						, (SELECT  GOLF_CMMN_CODE_NM  FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0051' AND TBG1.JOIN_CHNL = GOLF_CMMN_CODE) JOIN_CHNL_NM");
		sql.append("\n						, TBG1.AFFI_FIRM_NM																			");
		sql.append("\n 						, TBG3.RCVR_NM																				");
		sql.append("\n 						, TBG4.GOLF_CMMN_CODE_NM																	");
		sql.append("\n 						, TBG3.HP_DDD_NO																			");
		sql.append("\n 						, TBG3.HP_TEL_HNO																			");
		sql.append("\n 						, TBG3.HP_TEL_SNO																			");
		sql.append("\n 						, to_char(to_date(TBG3.APLC_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd') AS APLC_ATON			");
		sql.append("\n 						, TBG1.SECE_YN																			");
		sql.append("\n 						, TBG3.SND_YN																				");
		sql.append("\n 						, TBG3.SEQ_NO																				");
		sql.append("\n 				FROM BCDBA.TBGGOLFCDHD TBG1 																		");
		//sql.append("\n 				JOIN BCDBA.TBGGOLFCDHDGRDMGMT TBG2 ON TBG1.CDHD_ID = TBG2.CDHD_ID AND TBG2.CDHD_CTGO_SEQ_NO = 5		");
		sql.append("\n 				JOIN BCDBA.TBGGOLFCDHDGRDMGMT TBG2 ON TBG1.CDHD_ID = TBG2.CDHD_ID AND TBG2.CDHD_CTGO_SEQ_NO IN ('5', '6', '7') ");
		sql.append("\n 				JOIN BCDBA.TBGCDHDRIKMGMT TBG3 ON TBG1.CDHD_ID = TBG3.CDHD_ID										");
		sql.append("\n 				JOIN BCDBA.TBGCMMNCODE TBG4 ON TBG3.GOLF_TMNL_GDS_CODE = TBG4.GOLF_CMMN_CODE 						");
		sql.append("\n 				  AND TBG4.GOLF_URNK_CMMN_CLSS = '0000' AND TBG4.GOLF_URNK_CMMN_CODE = '0044' AND TBG4.USE_YN = 'Y'	");
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
		if(!sch_join_chnl.equals("A") && !sch_join_chnl.equals("")) sql.append("\t AND TBG1.JOIN_CHNL = '"+sch_join_chnl+"' ");
		
		sql.append("\n 				ORDER BY TBG3.SEQ_NO DESC						");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								"); 

		return sql.toString();
	}
	
}
