/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmEvntSpSettleInqDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  이벤트 > 특별레슨 이벤트 결제관리 목록조회
*   적용범위	: golf
*   작성일자	: 2009-07-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.settlement;

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

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntSpSettleInqDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 >  이벤트 > 특별레슨 이벤트 신청관리 목록 조회";
	/** **************************************************************************
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet 
	 * @return TaoResult 
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rs = null;
		ResultSet rs_count = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		String sql = "";
		String ttCnt = "";
	
		try {

			// 회원통합테이블 관련 수정사항 진행
			conn = context.getDbConnection("default", null);
			String search_sex 		  = data.getString("search_sex");
			String search_evnt 		  = data.getString("search_evnt");
			String search_word 		  = data.getString("search_word");
			String search_clss 		  = data.getString("search_clss");
			String search_status	  = data.getString("search_status");
			String search_grade	  	  = data.getString("search_grade");
			String golf_svc_aplc_clss = data.getString("golf_svc_aplc_clss");
			long page_no 			  = data.getLong("page_no");
			//debug("---------------------------------- proc search_evnt  : "+search_evnt);
			int pidx = 0;
			boolean eof = false;
			
			//게시물 리턴
			sql = this.getSelectQuery(search_sex, search_grade, search_status, search_evnt, search_clss, search_word);
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++pidx, page_no);
			pstmt.setString(++pidx, golf_svc_aplc_clss);
			if(!"".equals(search_sex) && !"A".equals(search_sex)){
				pstmt.setString(++pidx, search_sex);
			}	
			if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
				pstmt.setString(++pidx, search_evnt);
			}

			if(!"".equals(search_word)){
				if(!"".equals(search_clss) && !"A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
					pstmt.setString(++pidx, "%"+search_word+"%");
				}else if("A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
				}
			}
			if(!"".equals(search_status) && !"A".equals(search_status)){
				pstmt.setString(++pidx, search_status);
			}
			if(!"".equals(search_grade) && !"A".equals(search_grade)){
				pstmt.setString(++pidx, search_grade);
			}
			pstmt.setLong(++pidx, page_no);
			
			rs = pstmt.executeQuery();
			
			//총 갯수
			pidx = 0;
			sql = this.getSelectTtCountQuery(search_sex, search_grade, search_status, search_evnt, search_clss, search_word);
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(++pidx, golf_svc_aplc_clss);
			if(!"".equals(search_sex) && !"A".equals(search_sex)){
				pstmt.setString(++pidx, search_sex);
			}	
			if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
				pstmt.setString(++pidx, search_evnt);
			}
			if(!"".equals(search_word)){
				if(!"".equals(search_clss) && !"A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
					pstmt.setString(++pidx, "%"+search_word+"%");
				}else if("A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
				}
			}
			if(!"".equals(search_status) && !"A".equals(search_status)){
				pstmt.setString(++pidx, search_status);
			}
			if(!"".equals(search_grade) && !"A".equals(search_grade)){
				pstmt.setString(++pidx, search_grade);
			}
			rs_count = pstmt.executeQuery();
			if(rs_count.next()){
				ttCnt = rs_count.getString("CNT");
			}
			debug("===========================  ttCnt : "+ttCnt);
			result.addString("ttCnt", ttCnt);
			int art_num_no = 0;
			
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				//debug("+++++++++++++++++++++++++++++++   1   +++++++++++++++++++++++++++++++++++");
				result.addLong("row_num",				rs.getLong("RNUM"));
				result.addString("seq_no",				rs.getString("APLC_SEQ_NO"));
				result.addString("odr_no",				rs.getString("ODR_NO"));
				result.addString("sttl_aton",			rs.getString("STTL_ATON"));
				result.addString("reg_aton",			rs.getString("REG_ATON"));
				result.addString("sttl_amt",			rs.getString("STTL_AMT"));
				result.addString("evnt_nm",				rs.getString("EVNT_NM"));
				result.addString("evnt_seq_no",			rs.getString("EVNT_SEQ_NO"));
				result.addString("name",				rs.getString("NAME"));
				result.addString("hp_ddd_no",			rs.getString("HP_DDD_NO"));
				result.addString("hp_tel_hno",			rs.getString("HP_TEL_HNO"));
				result.addString("hp_tel_sno",			rs.getString("HP_TEL_SNO"));
				result.addString("sex",					rs.getString("SEX"));
				result.addString("usergrade",			rs.getString("USERGRADE"));
				result.addString("lesn_dc_cost",		rs.getString("LESN_DC_COST"));
				result.addString("status",				rs.getString("STATUS"));				
				result.addLong("atr_num",				rs.getLong("ART_NUM")-art_num_no);
				result.addString("total_cnt",			rs.getString("TOT_CNT"));
				result.addString("curr_page",			rs.getString("PAGE"));
				eof = true;
				art_num_no++;
				//debug("+++++++++++++++++++++++++++++++   2   +++++++++++++++++++++++++++++++++++");
			}
			if(!eof) {
				result.addString("RESULT", "01");
			}
			 
		} catch ( Exception e ) {			 
			
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(rs_count != null) rs_count.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery(String search_sex, String search_grade, String search_status, 
			String search_evnt,String search_clss, String search_word) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*													");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM										");
		sql.append("\n 				,APLC_SEQ_NO									");
		sql.append("\n 				,ODR_NO											");
		sql.append("\n 				,(CASE WHEN REAL_COST = '0' AND STATUS ='Y' THEN REG_ATON  WHEN REAL_COST <> '0' AND STATUS ='Y' THEN STTL_ATON ELSE '-' END)AS STTL_ATON  ");
		sql.append("\n 				,REG_ATON						  				");
		sql.append("\n 				,STTL_AMT										");
		sql.append("\n 				,EVNT_NM										");
		sql.append("\n 				,EVNT_SEQ_NO									");
		sql.append("\n 				,NAME											");
		sql.append("\n 				,HP_DDD_NO										");
		sql.append("\n 				,HP_TEL_HNO										");
		sql.append("\n 				,HP_TEL_SNO										");
		sql.append("\n 				,SEX											");
		sql.append("\n 				,USERGRADE										");
		sql.append("\n 				,LESN_DC_COST									");
		sql.append("\n 				,REAL_COST										");
		sql.append("\n 				,STATUS											");
		sql.append("\n 				,CEIL(ROWNUM/10) AS PAGE						");
		sql.append("\n 				,MAX(RNUM) OVER() TOT_CNT 						");	
		sql.append("\n 				,((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  		");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM							"); 
		sql.append("\n 						,T1.APLC_SEQ_NO							");
		sql.append("\n 						,T2.ODR_NO								");
		sql.append("\n 						,TO_CHAR(TO_DATE(T2.STTL_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD')AS STTL_ATON	");
		sql.append("\n 						,TO_CHAR(TO_DATE(T1.REG_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD')AS REG_ATON	");
		sql.append("\n 						,T2.STTL_AMT							");
		sql.append("\n 						,T3.EVNT_NM								");
		sql.append("\n 						,T3.EVNT_SEQ_NO 						");
		sql.append("\n 						,T4.HG_NM AS NAME								");
		sql.append("\n 						,T1.HP_DDD_NO							");
		sql.append("\n 						,T1.HP_TEL_HNO							");
		sql.append("\n 						,T1.HP_TEL_SNO							");
		sql.append("\n 						,(CASE SUBSTR(T4.JUMIN_NO,7,1) WHEN '1' THEN '1' WHEN '3' THEN '1' ELSE '2' END) AS SEX									");
		sql.append("\n 						,TO_NUMBER(T6.CDHD_SQ2_CTGO) AS USERGRADE						");
		sql.append("\n 						,TO_CHAR(T3.LESN_DC_COST,'999,999,999,999,999')AS LESN_DC_COST	");
		sql.append("\n 						,T3.LESN_DC_COST AS REAL_COST			");
		sql.append("\n 						,(CASE WHEN T3.LESN_DC_COST <> '0' AND T2.STTL_STAT_CLSS= 'N' AND T1.PGRS_YN = 'D' THEN 'Y' ");
		sql.append("\n 						       WHEN T3.LESN_DC_COST <> '0' AND T2.STTL_STAT_CLSS= 'Y' THEN 'C' 						");
		sql.append("\n 						       WHEN T3.LESN_DC_COST <> '0' AND T1.PGRS_YN='N' THEN 'N' WHEN T3.LESN_DC_COST ='0' THEN 'Y' ELSE '-' END)AS STATUS  ");
		sql.append("\n 				FROM BCDBA.TBGAPLCMGMT T1 LEFT JOIN BCDBA.TBGSTTLMGMT T2 ON T1.APLC_SEQ_NO = T2.STTL_GDS_SEQ_NO 	");
		sql.append("\n 					LEFT JOIN BCDBA.TBGEVNTMGMT T3 ON T1.LESN_SEQ_NO = T3.EVNT_SEQ_NO 								");
		sql.append("\n 					LEFT JOIN BCDBA.TBGGOLFCDHD T4 ON T1.CDHD_ID = T4.CDHD_ID 										");
		sql.append("\n 					LEFT JOIN BCDBA.TBGGOLFCDHDGRDMGMT T5 ON T1.CDHD_ID = T5.CDHD_ID 								");
		sql.append("\n 					LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T6 ON T5.CDHD_CTGO_SEQ_NO = T6.CDHD_CTGO_SEQ_NO 			");
		sql.append("\n 				WHERE  T1.GOLF_SVC_APLC_CLSS = ?				");
		if(!"".equals(search_sex) && !"A".equals(search_sex)){
			sql.append("\n 				AND  SUBSTR(T4.JUMIN_NO,7,1) = ?								");
		}	
		if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
			sql.append("\n				AND T1.LESN_SEQ_NO = ?						");
		}
		sql.append("\n 				ORDER BY T1.APLC_SEQ_NO DESC					");
		sql.append("\n 				)												");
		sql.append("\n 		WHERE 1 = 1 											");
		if(!"".equals(search_word)){
			if(!"".equals(search_clss) && !"A".equals(search_clss)){
				sql.append("\n 		 AND ( EVNT_NM LIKE ?  OR  NAME LIKE ?		)	");
			}else if("A".equals(search_clss)){
				sql.append("\n		 AND "+search_clss+"  LIKE ?					");
			}
		}
		if(!"".equals(search_status) && !"A".equals(search_status)){
			sql.append("\n		 AND STATUS = ?										");
		}
		if(!"".equals(search_grade) && !"A".equals(search_grade)){
			sql.append("\n		 AND USERGRADE = ?									");
		}
		sql.append("\n 		ORDER BY RNUM 											");
		sql.append("\n 		)														");
		sql.append("\n WHERE PAGE = ?												");
		

		return sql.toString();
	}

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectTtCountQuery(String search_sex, String search_grade, String search_status, 
			String search_evnt,String search_clss, String search_word) throws Exception{
		
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n 		SELECT count(*)as CNT 									");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM							"); 
		sql.append("\n 						,T1.APLC_SEQ_NO							");
		sql.append("\n 						,T2.ODR_NO								");
		sql.append("\n 						,TO_CHAR(TO_DATE(T2.STTL_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD')AS STTL_ATON	");
		sql.append("\n 						,T2.STTL_AMT							");
		sql.append("\n 						,T3.EVNT_NM								");
		sql.append("\n 						,T3.EVNT_SEQ_NO 						");
		sql.append("\n 						,T4.HG_NM AS NAME								");
		sql.append("\n 						,T1.HP_DDD_NO							");
		sql.append("\n 						,T1.HP_TEL_HNO							");
		sql.append("\n 						,T1.HP_TEL_SNO							");
		sql.append("\n 						,(CASE SUBSTR(T4.JUMIN_NO,7,1) WHEN '1' THEN '1' WHEN '3' THEN '1' ELSE '2' END) AS SEX									");
		sql.append("\n 						,TO_NUMBER(T6.CDHD_SQ1_CTGO) AS USERGRADE						");
		sql.append("\n 						,TO_CHAR(T3.LESN_DC_COST,'999,999,999,999,999')AS LESN_DC_COST	");
		sql.append("\n 						,(CASE WHEN T2.STTL_STAT_CLSS= 'N' AND T1.PGRS_YN = 'D' THEN 'Y' WHEN T2.STTL_STAT_CLSS= 'Y' THEN 'C' WHEN  T1.PGRS_YN='N' THEN 'N' ELSE 'N' END)AS STATUS  ");
		sql.append("\n 				FROM BCDBA.TBGAPLCMGMT T1 LEFT JOIN BCDBA.TBGSTTLMGMT T2 ON T1.APLC_SEQ_NO = T2.STTL_GDS_SEQ_NO 	");
		sql.append("\n 					LEFT JOIN BCDBA.TBGEVNTMGMT T3 ON T1.LESN_SEQ_NO = T3.EVNT_SEQ_NO 								");
		sql.append("\n 					LEFT JOIN BCDBA.TBGGOLFCDHD T4 ON T1.CDHD_ID = T4.CDHD_ID 										"); 
		sql.append("\n 					LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T6 ON T4.CDHD_CTGO_SEQ_NO = T6.CDHD_CTGO_SEQ_NO 			");
		sql.append("\n 				WHERE  T1.GOLF_SVC_APLC_CLSS = ?				");
		if(!"".equals(search_sex) && !"A".equals(search_sex)){
			sql.append("\n 				AND  SUBSTR(T4.JUMIN_NO,7,1) = ?								"); 
		}	
		if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
			sql.append("\n				AND T3.EVNT_SEQ_NO = ?						");
		}
		sql.append("\n 				ORDER BY T1.APLC_SEQ_NO DESC					");
		sql.append("\n 				)												");
		sql.append("\n 		WHERE 1 = 1 											");
		if(!"".equals(search_word)){
			if(!"".equals(search_clss) && !"A".equals(search_clss)){
				sql.append("\n 		 AND ( EVNT_NM LIKE ?  OR  NAME LIKE ?		)	");
			}else if("A".equals(search_clss)){
				sql.append("\n		 AND "+search_clss+"  LIKE ?					");
			}
		}
		if(!"".equals(search_status) && !"A".equals(search_status)){
			sql.append("\n		 AND STATUS = ?										");
		}
		if(!"".equals(search_grade) && !"A".equals(search_grade)){
			sql.append("\n		 AND USERGRADE = ?									");
		}
		
		return sql.toString();
	}
}
