/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfEvntShopListDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 이벤트 > 쇼핑 > 리스트 
*   적용범위  : Golf
*   작성일자  : 2010-02-20
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.prime;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfEvntPrimeSchDaoProc extends AbstractProc {

	public static final String TITLE = "게시판 관리 목록 조회"; 
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntPrimeSchDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException {
		PreparedStatement pstmt = null;
		PreparedStatement pstmt_detail = null;
		ResultSet rset = null;
		ResultSet rset_detail = null;
		DbTaoResult result = null;
		Connection con = null;
				
		try{
			con = context.getDbConnection("default", null);

			String bkg_pe_nm	= data.getString("bkg_pe_nm");		// 성명
			String jumin_no1	= data.getString("jumin_no1");		// 주민등록번호1
			String jumin_no2	= data.getString("jumin_no2");		// 주민등록번호2
			
			pstmt = con.prepareStatement(this.getAplQuery());
			int pidx = 0;
			pstmt.setString(++pidx, "%"+bkg_pe_nm+"%");
			pstmt.setString(++pidx, jumin_no1+""+jumin_no2);
			pstmt.setString(++pidx, "%"+bkg_pe_nm+"%");
			pstmt.setString(++pidx, jumin_no1+""+jumin_no2);

			
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			String lesn_seq_no = "";
			String txt_lesn_seq_no = "";
			String money_lesn_seq_no = "";

			if(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				lesn_seq_no = rset.getString("LESN_SEQ_NO");
				if(!GolfUtil.empty(lesn_seq_no)){
					if(lesn_seq_no.equals("1")){
						txt_lesn_seq_no = "월드 프라임 골드";
						money_lesn_seq_no = "1000000";
					}else if(lesn_seq_no.equals("2")){
						txt_lesn_seq_no = "더블 월드 프라임 골드";
						money_lesn_seq_no = "2000000";
					}
				}
				
												
				result.addString("APLC_SEQ_NO",		rset.getString("APLC_SEQ_NO"));
				result.addString("BKG_PE_NM",		rset.getString("BKG_PE_NM"));
				result.addString("JUMIN_NO",		rset.getString("JUMIN_NO"));
				result.addString("HP_DDD_NO",		rset.getString("HP_DDD_NO"));
				result.addString("HP_TEL_HNO",		rset.getString("HP_TEL_HNO"));
				result.addString("HP_TEL_SNO",		rset.getString("HP_TEL_SNO"));
				result.addString("DDD_NO",			rset.getString("DDD_NO"));
				result.addString("TEL_HNO",			rset.getString("TEL_HNO"));
				result.addString("TEL_SNO",			rset.getString("TEL_SNO"));
				result.addString("DTL_ADDR",		rset.getString("DTL_ADDR"));
				result.addString("PGRS_YN",			rset.getString("PGRS_YN"));
				result.addString("LESN_SEQ_NO",		lesn_seq_no);
				result.addString("TXT_LESN_SEQ_NO",	txt_lesn_seq_no);
				result.addString("MONEY_LESN_SEQ_NO",money_lesn_seq_no);
				result.addString("STTL_AMT",		rset.getString("STTL_AMT"));
				result.addString("PU_DATE",			rset.getString("PU_DATE"));
				result.addString("PU_DATE2",		rset.getString("PU_DATE2"));
				result.addString("MEMO_EXPL",		rset.getString("MEMO_EXPL"));
				result.addString("CO_NM",			rset.getString("CO_NM"));
				result.addString("SUM_DAY",			rset.getString("SUM_DAY")+"일");
				result.addString("CDHD_ID",			rset.getString("CDHD_ID"));

				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComListDaoProc ERROR ==="); 
			
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}
	public TaoResult memberCk(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		DbTaoResult result = null;
		Connection con = null;
				
		try{
			con = context.getDbConnection("default", null);

			String bkg_pe_nm	= data.getString("bkg_pe_nm");		// 성명
			String jumin_no1	= data.getString("jumin_no1");		// 주민등록번호1
			String jumin_no2	= data.getString("jumin_no2");		// 주민등록번호2
			
			pstmt = con.prepareStatement(this.getMemberCkQuery());
			int pidx = 0;
			pstmt.setString(++pidx, "%"+bkg_pe_nm+"%");
			pstmt.setString(++pidx, jumin_no1+""+jumin_no2);

			
			rs = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("PGRS_YN",rs.getString("PGRS_YN"));		// 회원상태
													
					result.addString("RESULT", "00");		
				}
			}
			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
		
						
			
		}catch ( Exception e ) {

		}finally{
			try{ if(rs  != null) rs.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}
	/** ***********************************************************************
	* 회원 체크
	************************************************************************ */
	private String getMemberCkQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	SELECT PGRS_YN	\n");
		sql.append("\t	 PGRS_YN	\n");
		sql.append("\t	FROM BCDBA.TBGAPLCMGMT \n");
		sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='1003' AND BKG_PE_NM LIKE ? AND JUMIN_NO=? AND TO_DATE(GREEN_NM) > TO_DATE(TO_CHAR(SYSDATE,'YYYYMMDD')) \n");
		
		return sql.toString();
	}

	public TaoResult execute_rsv(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		ResultSet rset_comp = null;
		DbTaoResult result = null;
		Connection con = null;
				
		try{
			con = context.getDbConnection("default", null);

			String bkg_pe_nm	= data.getString("bkg_pe_nm");		// 성명
			String jumin_no1	= data.getString("jumin_no1");		// 주민등록번호1
			String jumin_no2	= data.getString("jumin_no2");		// 주민등록번호2
			String aplc_seq_no	= data.getString("aplc_seq_no");	// 이벤트 등록번호
			
			
			pstmt = con.prepareStatement(this.getMonthQuery());
			int pidx = 0;
			pstmt.setString(++pidx, "%"+bkg_pe_nm+"%");
			pstmt.setString(++pidx, jumin_no1+""+jumin_no2);

			
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			int comp_num = 0;
			int comp_name_num = 0;
			String comp_name = "";
			String tel = "";
			String [] tel_arr = null;
			String ddd_no = "";
			String tel_hno = "";
			String tel_sno = "";
			String comp_yn = "N";

			if(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				comp_num = rset.getInt("COMP_NUM");
				tel = rset.getString("EMAIL");
				tel_arr = GolfUtil.split(tel, "-");
				if(!GolfUtil.empty(tel_arr[0])){
					ddd_no = tel_arr[0];
				}
				if(!GolfUtil.empty(tel_arr[1])){
					tel_hno = tel_arr[1];
				}
				if(!GolfUtil.empty(tel_arr[2])){
					tel_sno = tel_arr[2];
				}
				
				aplc_seq_no = rset.getString("APLC_SEQ_NO");
				
				result.addString("aplc_seq_no",		aplc_seq_no);
				result.addString("rsvt_date",		rset.getString("TXT_RSVT_DATE"));
				result.addString("rsvt_date2",		rset.getString("TXT_RSVT_DATE2"));
				debug("rsvt_date : " + rset.getString("TXT_RSVT_DATE") + " / rsvt_date2 : " + rset.getString("TXT_RSVT_DATE2"));
				result.addString("bkg_pe_nm",		rset.getString("BKG_PE_NM"));
				result.addString("jumin_no",		rset.getString("JUMIN_NO"));
				result.addString("hp_ddd_no",		rset.getString("HP_DDD_NO"));
				result.addString("hp_tel_hno",		rset.getString("HP_TEL_HNO"));
				result.addString("hp_tel_sno",		rset.getString("HP_TEL_SNO"));
				result.addString("email",			rset.getString("EMAIL"));
				result.addString("evnt_pgrs_clss",	rset.getString("EVNT_PGRS_CLSS"));
				result.addString("sttl_stat_clss",	rset.getString("STTL_STAT_CLSS"));
				result.addString("note",			rset.getString("NOTE"));
				result.addString("mgr_memo",		rset.getString("MGR_MEMO"));
				result.addString("comp_num",		rset.getString("COMP_NUM"));
				result.addString("hadc_num",		rset.getString("HADC_NUM"));
				result.addString("cus_rmrk",		rset.getString("CUS_RMRK"));
				result.addString("ddd_no",			ddd_no);
				result.addString("tel_hno",			tel_hno);
				result.addString("tel_sno",			tel_sno);
				
				

				existsData = true;
				
				if(comp_num>0){
					comp_yn = "Y";

					pstmt = con.prepareStatement(this.getCompQuery());
					int aidx = 0;
					pstmt.setString(++aidx, aplc_seq_no);

					
					rset_comp = pstmt.executeQuery();

					while(rset_comp.next()){
						comp_name = rset_comp.getString("BKG_PE_NM");
						comp_name_num++;
						debug("comp_name : " + comp_name + " / comp_name_num : " + comp_name_num);
						result.addString("comp_bkg_pe_nm_"+comp_name_num,		rset_comp.getString("BKG_PE_NM"));
					}
					
					if(comp_name_num<3){
						for(int u=3; u>comp_name_num; u--){
							result.addString("comp_bkg_pe_nm_"+u,		"");
							debug("u : " + u);
						}
					}
				}

				result.addString("comp_yn",			comp_yn);
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComListDaoProc ERROR ==="); 
			
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		return result;	
	}
	
	
	/** ***********************************************************************
	* 신청 테이블
	************************************************************************ */
	private String getAplQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	SELECT APLC_SEQ_NO, CDHD_ID, BKG_PE_NM, JUMIN_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, DDD_NO, TEL_HNO, TEL_SNO	\n");
		sql.append("\t	, DTL_ADDR, LESN_SEQ_NO, STTL_AMT, MEMO_EXPL, CO_NM, PGRS_YN, REG_ATON	\n");
		sql.append("\t	, TO_CHAR(TO_DATE(PU_DATE),'YYYY.MM.DD') PU_DATE	\n");
		sql.append("\t	, TO_CHAR(TO_DATE(PU_DATE)+365,'YYYY.MM.DD') PU_DATE2	\n");
		sql.append("\t	, NVL((SELECT SUM(HADC_NUM) FROM BCDBA.TBGGOLFEVNTAPLC WHERE GOLF_SVC_APLC_CLSS='1003' AND EVNT_PGRS_CLSS NOT IN ('C','E') AND BKG_PE_NM LIKE ? AND JUMIN_NO=?),0) SUM_DAY	\n");
		sql.append("\t	FROM BCDBA.TBGAPLCMGMT	\n");
		sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='1003' AND  BKG_PE_NM LIKE ? AND JUMIN_NO=? AND PGRS_YN<>'F'	\n");

		return sql.toString();
	}
	
	/** ***********************************************************************
	* 월례회 테이블
	************************************************************************ */
	private String getMonthQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	SELECT APLC_SEQ_NO, TO_CHAR(TO_DATE(RSVT_DATE),'YYYY.MM.DD') TXT_RSVT_DATE, BKG_PE_NM, CUS_RMRK	\n");
		sql.append("\t	, JUMIN_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, EVNT_PGRS_CLSS, STTL_STAT_CLSS, NOTE, MGR_MEMO	\n");
		sql.append("\t	, (SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE GOLF_SVC_APLC_CLSS='1003' AND APLC_SEQ_NO=APLC.APLC_SEQ_NO AND APLC_PE_CLSS='2') COMP_NUM	\n");
		sql.append("\t	, TO_CHAR(TO_DATE(RSVT_DATE)+HADC_NUM,'YYYY.MM.DD') TXT_RSVT_DATE2	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFEVNTAPLC APLC	\n");
		sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='1003' AND EVNT_PGRS_CLSS NOT IN ('C','E') AND BKG_PE_NM LIKE ? AND JUMIN_NO=?	\n");
		sql.append("\t	ORDER BY APLC_ATON DESC	\n");
		
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 월례회 신청자 테이블
	************************************************************************ */
	private String getCompQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("	\n");
		sql.append("\t	SELECT SEQ_NO, BKG_PE_NM, APLC_PE_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFEVNTAPLCPE	\n");
		sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='1003' AND APLC_PE_CLSS='2' AND APLC_SEQ_NO=?	\n");
		sql.append("\t	ORDER BY SEQ_NO	\n");
		
		return sql.toString();
	}

}
