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
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
 * Golf
 * @author	미디어포스
 * @version	1.0
 ******************************************************************************/
public class GolfEvntPrimeRsvDaoProc extends AbstractProc {

	public static final String TITLE = "게시판 관리 목록 조회";
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntPrimeRsvDaoProc() {}	

	/**
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
				
		try{
			con = context.getDbConnection("default", null);
			
			long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			
			pstmt = con.prepareStatement(this.getSelectQuery());
			int pidx = 0;
			pstmt.setLong(++pidx, page_no);
			pstmt.setLong(++pidx, page_no);

			
			rset = pstmt.executeQuery();
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			int art_num_no= 0;
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
								
				result.addLong("row_num",				rset.getLong("RNUM"));
				result.addString("SEQ_NO",				rset.getString("SEQ_NO"));
				result.addString("EPS_YN",				rset.getString("EPS_YN"));
				result.addString("TITL",				rset.getString("TITL"));
				result.addString("REG_ATON",			rset.getString("REG_ATON"));
				result.addLong("INOR_NUM",				rset.getLong("INOR_NUM"));

				result.addInt("ART_NUM" 				,rset.getInt("ART_NUM")-art_num_no);
				result.addString("total_cnt",			rset.getString("TOT_CNT") );
				result.addString("curr_page",			rset.getString("PAGE") );
				
				art_num_no++;
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


	public int execute_rsv(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);

			String sql = "";
			int idx = 1;
			
			String mod							= data.getString("mod");
			int aplc_seq_no						= data.getInt("aplc_seq_no");
			String cdhd_id						= data.getString("cdhd_id");
			String cdhd_non_cdhd_clss			= data.getString("cdhd_non_cdhd_clss");
			String cdhd_grd_seq_no				= data.getString("cdhd_grd_seq_no");
			String bkg_pe_nm					= data.getString("bkg_pe_nm");
			String bkg_pe_nm_eng				= data.getString("bkg_pe_nm_eng");
			String jumin_no1					= data.getString("jumin_no1");
			String jumin_no2					= data.getString("jumin_no2");
			String hp_ddd_no					= data.getString("hp_ddd_no");
			String hp_tel_hno					= data.getString("hp_tel_hno");
			String hp_tel_sno					= data.getString("hp_tel_sno");
			String ddd_no						= data.getString("ddd_no");
			String tel_hno						= data.getString("tel_hno");
			String tel_sno						= data.getString("tel_sno");
			String rsvt_date					= data.getString("rsvt_date");
			String hadc_num						= data.getString("hadc_num");
			String note							= data.getString("note"); 
			String mgr_memo						= data.getString("mgr_memo");	
			String cus_rmrk						= data.getString("cus_rmrk");	
			int comp_num						= data.getInt("comp_num");	
			
			String jumin_no 					= jumin_no1+""+jumin_no2;
			String email 						= ddd_no+"-"+tel_hno+"-"+tel_sno;
			
			if(!GolfUtil.empty(bkg_pe_nm_eng)){
				bkg_pe_nm = bkg_pe_nm+" ("+bkg_pe_nm_eng+")";
			}
			

			if(mod.equals("ins")){
				// 이벤트 등록
				aplc_seq_no = 0;
				sql = this.getEvtSeqQuery();  
				pstmt = conn.prepareStatement(sql.toString());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					aplc_seq_no = rs.getInt("MAX_SEQ_NO");
				}
	
				sql = this.getEvtQuery();   
				pstmt = conn.prepareStatement(sql.toString());
				idx = 1;	// 11
				pstmt.setInt(idx++, aplc_seq_no);
				pstmt.setString(idx++, "태국");
				pstmt.setString(idx++, rsvt_date);
				pstmt.setString(idx++, "000000");
				pstmt.setString(idx++, bkg_pe_nm);
				pstmt.setString(idx++, cdhd_non_cdhd_clss);
				pstmt.setString(idx++, cdhd_id);
				pstmt.setString(idx++, jumin_no);
				pstmt.setString(idx++, cdhd_grd_seq_no);
				pstmt.setString(idx++, hp_ddd_no);
				pstmt.setString(idx++, hp_tel_hno);
				pstmt.setString(idx++, hp_tel_sno);
				pstmt.setString(idx++, email);
				pstmt.setString(idx++, hadc_num);
				pstmt.setString(idx++, note);
				pstmt.setString(idx++, mgr_memo);
				pstmt.setString(idx++, cus_rmrk);
				result = pstmt.executeUpdate();
				
				// 참가자 등록 - 신청자
				sql = this.getEvtCompnQuery();   
				pstmt = conn.prepareStatement(sql.toString());
				idx = 1;	// 13
				pstmt.setInt(idx++, aplc_seq_no);
				pstmt.setString(idx++, "1");
				pstmt.setString(idx++, "1");
				pstmt.setString(idx++, bkg_pe_nm);
				pstmt.setString(idx++, cdhd_non_cdhd_clss);
				pstmt.setString(idx++, cdhd_id);
				pstmt.setString(idx++, jumin_no);
				pstmt.setString(idx++, cdhd_grd_seq_no);
				pstmt.setString(idx++, hp_ddd_no);
				pstmt.setString(idx++, hp_tel_hno);
				pstmt.setString(idx++, hp_tel_sno);
				pstmt.setString(idx++, email);
				pstmt.setString(idx++, hadc_num);
				result = pstmt.executeUpdate();
				
				
				// 참가자 등록 - 동반자
				for(int i=1; i<4; i++){
					if(!GolfUtil.empty(data.getString("comp_bkg_pe_nm_"+i))){
						sql = this.getEvtCompnQuery();   
						pstmt = conn.prepareStatement(sql.toString());
						idx = 1;	// 13
						pstmt.setInt(idx++, aplc_seq_no);
						pstmt.setInt(idx++, i+2);
						pstmt.setString(idx++, "2");
						pstmt.setString(idx++, data.getString("comp_bkg_pe_nm_"+i));
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						result = pstmt.executeUpdate();
					}
				}
			}else if(mod.equals("upd")){
				
				// 수정
				sql = this.getChgEvnt();   
				pstmt = conn.prepareStatement(sql.toString());
				idx = 1;	// 11
				pstmt.setString(idx++, rsvt_date);
				pstmt.setString(idx++, hp_ddd_no);
				pstmt.setString(idx++, hp_tel_hno);
				pstmt.setString(idx++, hp_tel_sno);
				pstmt.setString(idx++, email);
				pstmt.setString(idx++, hadc_num);
				pstmt.setString(idx++, note);
				pstmt.setString(idx++, mgr_memo);
				pstmt.setString(idx++, cus_rmrk);
				pstmt.setInt(idx++, aplc_seq_no);
				result = pstmt.executeUpdate();
				
				
				// 동반자 삭제
				sql = this.getDelComp();   
				pstmt = conn.prepareStatement(sql.toString());
				idx = 1;
				pstmt.setInt(idx++, aplc_seq_no);
				result = pstmt.executeUpdate();
				
				
				// 참가자 등록 - 동반자
				for(int i=1; i<4; i++){
					if(!GolfUtil.empty(data.getString("comp_bkg_pe_nm_"+i))){
						sql = this.getEvtCompnQuery();   
						pstmt = conn.prepareStatement(sql.toString());
						idx = 1;	// 13
						pstmt.setInt(idx++, aplc_seq_no);
						pstmt.setInt(idx++, i+2);
						pstmt.setString(idx++, "2");
						pstmt.setString(idx++, data.getString("comp_bkg_pe_nm_"+i));
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						pstmt.setString(idx++, "");
						result = pstmt.executeUpdate();
					}
				}
				
			}else if(mod.equals("del")){
				
				// 취소
				sql = this.getChgEvntStat();   
				pstmt = conn.prepareStatement(sql.toString());
				idx = 1;
				pstmt.setInt(idx++, aplc_seq_no);
				result = pstmt.executeUpdate();
				
			}
			

			if(result > 0) {
				conn.commit();
			} else {
				conn.rollback();
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	


	public int execute_jumin(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int result =  0;
		int idx = 0;

		try {
			conn = context.getDbConnection("default", null);
			
			String jumin_no 	= data.getString("jumin_no");
			String green_nm 	= data.getString("green_nm");
			String rsvt_date 	= data.getString("rsvt_date");
			String rsv_time 	= data.getString("rsv_time");
			
			String sql = this.getCntJuminQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(++idx, jumin_no);
			pstmt.setString(++idx, green_nm);
			pstmt.setString(++idx, rsvt_date);
			pstmt.setString(++idx, rsv_time);

			rs = pstmt.executeQuery();

			if ( rs != null ) {
				rs.next();
				result = rs.getInt("CNT");
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	

	public int execute_hp(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		int  result =  0;

		try {
			conn = context.getDbConnection("default", null);

			String hp_ddd_no	= data.getString("hp_ddd_no");
			String hp_tel_hno	= data.getString("hp_tel_hno");
			String hp_tel_sno	= data.getString("hp_tel_sno");
			String green_nm 	= data.getString("green_nm");
			String rsvt_date 	= data.getString("rsvt_date");
			String rsv_time 	= data.getString("rsv_time");
			
			String sql = this.getCntHpQuery();   
			pstmt = conn.prepareStatement(sql.toString());
			int idx = 0;
			pstmt.setString(++idx, hp_ddd_no);
			pstmt.setString(++idx, hp_tel_hno);
			pstmt.setString(++idx, hp_tel_sno);
			pstmt.setString(++idx, green_nm);
			pstmt.setString(++idx, rsvt_date);
			pstmt.setString(++idx, rsv_time);

			rs = pstmt.executeQuery();

			if ( rs != null ) {
				rs.next();
				result = rs.getInt("CNT");
			}
			 
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
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery() throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*																						");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,					");
		sql.append("\n 				SEQ_NO,							");
		sql.append("\n 				EPS_YN,							");
		sql.append("\n 				TITL,							");
		sql.append("\n 				REG_ATON,						");
		sql.append("\n 				INOR_NUM,						");
		sql.append("\n 				CEIL(ROWNUM/10) AS PAGE,		");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT		");	
		sql.append("\n 				, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,			");
		sql.append("\n 						T1.BBRD_SEQ_NO AS SEQ_NO,					");
		sql.append("\n 						(CASE T1.EPS_YN WHEN 'Y' THEN '노출' ELSE '비노출' END) AS EPS_YN,					");
		sql.append("\n 						T1.TITL,					");
		sql.append("\n 						TO_CHAR(TO_DATE(T1.REG_ATON),'YYYY-MM-DD') AS REG_ATON,				");
		sql.append("\n 						T1.INQR_NUM AS INOR_NUM				");

		sql.append("\n 				FROM BCDBA.TBGBBRD T1			");
		sql.append("\n 				JOIN BCDBA.TBGMGRINFO T2 ON T1.REG_MGR_ID=T2.MGR_ID			");
		sql.append("\n 				WHERE T1.EPS_YN='Y' AND T1.BBRD_CLSS='0001' AND GOLF_CLM_CLSS='0002'		");
		sql.append("\n 				ORDER BY BBRD_SEQ_NO DESC			");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}

	/** ***********************************************************************
	* 이벤트 max_seq 가져오기
	************************************************************************ */
	private String getEvtSeqQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n SELECT NVL(MAX(APLC_SEQ_NO),0)+1 MAX_SEQ_NO FROM BCDBA.TBGGOLFEVNTAPLC	\n");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 이벤트 등록 
	************************************************************************ */
	private String getEvtQuery(){
		StringBuffer sql = new StringBuffer();	
		sql.append("\n	INSERT INTO BCDBA.TBGGOLFEVNTAPLC (	");
		sql.append("\n		SITE_CLSS, APLC_SEQ_NO, GOLF_SVC_APLC_CLSS, GREEN_NM, APLC_ATON, RSVT_DATE, RSV_TIME, BKG_PE_NM, CDHD_NON_CDHD_CLSS, CDHD_ID	");
		sql.append("\n	    , JUMIN_NO, CDHD_GRD_SEQ_NO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, EVNT_PGRS_CLSS, STTL_STAT_CLSS, NOTE, MGR_MEMO, CUS_RMRK	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    '2', ?, '1003', ?, TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS'), ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, ?, ?, 'R', '0', ?, ?, ?	");
		sql.append("\n	)	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 동반자 등록
	************************************************************************ */
	private String getEvtCompnQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n	INSERT INTO BCDBA.TBGGOLFEVNTAPLCPE (	");
		sql.append("\n	    SITE_CLSS, APLC_SEQ_NO, SEQ_NO, GOLF_SVC_APLC_CLSS, APLC_PE_CLSS, BKG_PE_NM, CDHD_NON_CDHD_CLSS, CDHD_ID, JUMIN_NO, CDHD_GRD_SEQ_NO	");
		sql.append("\n	    , HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, EMAIL, HADC_NUM, STTL_STAT_CLSS, CNCL_YN	");
		sql.append("\n	) VALUES (	");
		sql.append("\n	    '2', ?, ?, '1003', ?, ?, ?, ?, ?, ?	");
		sql.append("\n	    , ?, ?, ?, ?, ?, '0', 'N'	");
		sql.append("\n	)	");
		return sql.toString();
	}
	
	/** ***********************************************************************
	* 같은 주민등록 번호로 등록된 신청내역이 있는지 알아본다.
	************************************************************************ */
	private String getCntJuminQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLC 	\n");
		sql.append("\t	WHERE JUMIN_NO=? AND GREEN_NM=? AND RSVT_DATE=? AND RSV_TIME=?	\n");
		return sql.toString();
	}

	/** ***********************************************************************
	* 같은 핸드폰 번호로 등록된 신청내역이 있는지 알아본다.
	************************************************************************ */
	private String getCntHpQuery(){
		StringBuffer sql = new StringBuffer();
		sql.append("\n	SELECT COUNT(*) CNT FROM BCDBA.TBGGOLFEVNTAPLC	\n");
		sql.append("\t	WHERE HP_DDD_NO=? AND HP_TEL_HNO=? AND HP_TEL_SNO=? AND GREEN_NM=? AND RSVT_DATE=? AND RSV_TIME=?	\n");
		return sql.toString();
	}	


	/** ***********************************************************************
	* 이벤트 내용 수정
	************************************************************************ */
	private String getChgEvnt(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFEVNTAPLC SET	\n");
		sql.append("\t	RSVT_DATE=?, HP_DDD_NO=?, HP_TEL_HNO=?, HP_TEL_SNO=?, EMAIL=?, HADC_NUM=?, NOTE=?, MGR_MEMO=?, CUS_RMRK=?	\n");
		sql.append("\t	WHERE APLC_SEQ_NO=?	\n");
		return sql.toString();
	}		

	/** ***********************************************************************
	* 동반자 삭제
	************************************************************************ */
	private String getDelComp(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	DELETE FROM BCDBA.TBGGOLFEVNTAPLCPE WHERE GOLF_SVC_APLC_CLSS='1003' AND APLC_SEQ_NO=?	\n");
		return sql.toString();
	}		

	/** ***********************************************************************
	* 이벤트 상태 변경 - 취소
	************************************************************************ */
	private String getChgEvntStat(){
		StringBuffer sql = new StringBuffer();
		sql.append("	\n");
		sql.append("\t	UPDATE BCDBA.TBGGOLFEVNTAPLC SET EVNT_PGRS_CLSS='C'	\n");
		sql.append("\t	WHERE GOLF_SVC_APLC_CLSS='1003' AND APLC_SEQ_NO=?	\n");
		return sql.toString();
	}		
		
	
	
	
}
