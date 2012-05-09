/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmGiftDtlInqDaoProc
*   작성자     : (주)미디어포스 조은미
*   내용        : 관리자 사은품관리 상세 조회 
*   적용범위  : Golf
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
public class GolfAdmGiftDtlInqDaoProc extends AbstractProc {
	
	public static final String TITLE = "사은품관리  조회";
	
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
		
		debug("==== GolfAdmGiftDtlInqDaoProc start ===");
		
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
				
				String sece_yn		= "";
				String sece_status		= "";

				if(!existsData){
					result.addString("RESULT", "00");
				}
							
				String addrClss = rset.getString("NW_OLD_ADDR_CLSS");
				
				if ( addrClss == null || addrClss.trim().equals("")){
					addrClss = "1";
				}
				
				result.addString("seq_no",					rset.getString("SEQ_NO"));
				result.addString("hg_nm",					rset.getString("HG_NM"));
				result.addString("cdhd_id",					rset.getString("CDHD_ID"));
				result.addString("rcvr_nm",					rset.getString("RCVR_NM"));
				result.addString("golf_cmmn_code_nm",		rset.getString("GOLF_CMMN_CODE_NM"));
				result.addString("hp_tel",					rset.getString("HP_TEL"));
				result.addString("zp",						rset.getString("ZP"));
				result.addString("addr",					rset.getString("ADDR"));
				result.addString("dtl_addr",				rset.getString("DTL_ADDR"));
				result.addString("addr_clss",				addrClss);
				result.addString("aplc_aton",				rset.getString("APLC_ATON"));
				result.addString("acrg_cdhd_jonn_date",		rset.getString("ACRG_CDHD_JONN_DATE"));
				//result.addString("sece_yn",				rset.getString("SECE_YN"));
				result.addString("snd_yn",					rset.getString("SND_YN"));
				result.addString("memo_ctnt",				rset.getString("MEMO_CTNT"));
				result.addString("golf_tmnl_gds_code",		rset.getString("GOLF_TMNL_GDS_CODE"));
				result.addString("hp_ddd_no",				rset.getString("HP_DDD_NO"));
				result.addString("hp_tel_hno",				rset.getString("HP_TEL_HNO"));
				result.addString("hp_tel_sno",				rset.getString("HP_TEL_SNO"));
				result.addString("zp1",						rset.getString("ZP1"));
				result.addString("zp2",						rset.getString("ZP2"));
				
				sece_yn =rset.getString("sece_yn");
				if ("Y".equals(sece_yn) ) {
					sece_status ="탈퇴";
				}
				else {
					sece_status ="정상";
				}
				result.addString("sece_status",						sece_status);
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			debug("==== GolfAdmGiftDtlInqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmGiftDtlInqDaoProc ERROR ===");
			
			//debug("==== GolfAdmGiftDtlInqDaoProc ERROR ===");
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
	
		sql.append("\n  	SELECT																										\n");		
		sql.append("\t  		  TBG3.SEQ_NO																							\n");
		sql.append("\t  		, TBG1.HG_NM																							\n");												
		sql.append("\t			, TBG1.CDHD_ID																							\n");											
		sql.append("\t			, TBG3.RCVR_NM																							\n");											
		sql.append("\t			, TBG4.GOLF_CMMN_CODE_NM																				\n");														
		sql.append("\t			, (TBG3.HP_DDD_NO || '-' || TBG3.HP_TEL_HNO || '-' || TBG3.HP_TEL_SNO) AS HP_TEL						\n");
		sql.append("\t			, SUBSTR(TBG3.ZP,0, 3) || '-' || SUBSTR(TBG3.ZP,3, 3) AS ZP												\n");
		sql.append("\t			, TBG3.ADDR																								\n");
		sql.append("\t			, TBG3.DTL_ADDR, TBG3.NW_OLD_ADDR_CLSS																	\n");
		sql.append("\t			, to_char(to_date(TBG3.APLC_ATON,'yyyymmddhh24miss'), 'yyyy/mm/dd') AS APLC_ATON						\n");
		sql.append("\t			, to_char(to_date(TBG3.ACRG_CDHD_JONN_DATE,'yyyymmdd'), 'yyyy/mm/dd') AS ACRG_CDHD_JONN_DATE			\n");
		sql.append("\t			, TBG1.SECE_YN																							\n");																				
		sql.append("\t			, TBG3.SND_YN, MEMO_CTNT																				\n");																				
		sql.append("\t			, TBG3.GOLF_TMNL_GDS_CODE, TBG3.HP_DDD_NO, TBG3.HP_TEL_HNO, TBG3.HP_TEL_SNO								\n");																				
		sql.append("\t			, SUBSTR(TBG3.ZP,0, 3) ZP1, SUBSTR(TBG3.ZP,3, 3) ZP2													\n");																																						
		sql.append("\t			FROM BCDBA.TBGGOLFCDHD TBG1 																			\n");																	
		//sql.append("\t			JOIN BCDBA.TBGGOLFCDHDGRDMGMT TBG2 ON TBG1.CDHD_ID = TBG2.CDHD_ID AND TBG2.CDHD_CTGO_SEQ_NO = 5			\n");	
		sql.append("\t			JOIN BCDBA.TBGGOLFCDHDGRDMGMT TBG2 ON TBG1.CDHD_ID = TBG2.CDHD_ID AND TBG2.CDHD_CTGO_SEQ_NO IN ('5', '6', '7')		\n");
		sql.append("\t			JOIN BCDBA.TBGCDHDRIKMGMT TBG3 ON TBG1.CDHD_ID = TBG3.CDHD_ID											\n");								
		sql.append("\t			JOIN BCDBA.TBGCMMNCODE TBG4 ON TBG3.GOLF_TMNL_GDS_CODE = TBG4.GOLF_CMMN_CODE 							\n");					
		sql.append("\t				AND TBG4.GOLF_URNK_CMMN_CLSS = '0000' AND TBG4.GOLF_URNK_CMMN_CODE = '0044' AND TBG4.USE_YN = 'Y'	\n");
		sql.append("\t			WHERE TBG3.SEQ_NO = ?																					\n");


		return sql.toString();
	}
}
