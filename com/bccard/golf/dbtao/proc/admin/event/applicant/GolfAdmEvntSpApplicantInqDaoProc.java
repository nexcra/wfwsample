/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmEvntSpApplicantInqDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  이벤트 > 특별레슨 이벤트 신청관리 목록조회
*   적용범위	: golf
*   작성일자	: 2009-07-08
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.applicant;

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
public class GolfAdmEvntSpApplicantInqDaoProc extends AbstractProc {
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
		int ttCnt = 0;
	
		try {

			// 회원통합테이블 관련 수정사항 진행
			conn = context.getDbConnection("default", null);
		
			String search_evnt 		  = data.getString("search_evnt");
			String search_word 		  = data.getString("search_word");
			String search_clss 		  = data.getString("search_clss");
			String search_status	  = data.getString("search_status");
			String search_przwin	  = data.getString("search_przwin");
			String search_sdate 	  = data.getString("search_sdate");
			String search_edate 	  = data.getString("search_edate");
			String evnt_clss		  = data.getString("evnt_clss");
			String golf_svc_aplc_clss = data.getString("golf_svc_aplc_clss");
			long page_no 			  = data.getLong("page_no");
			//debug("---------------------------------- proc search_evnt  : "+search_evnt);
			int pidx = 0;
			boolean eof = false;
			
			sql = this.getSelectQuery(search_clss, search_word, search_sdate, search_edate, 
					search_evnt, search_status, search_przwin);
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++pidx, page_no);
			pstmt.setString(++pidx, golf_svc_aplc_clss);
			pstmt.setString(++pidx, evnt_clss);
			
			if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
				pstmt.setString(++pidx, search_evnt);
			}
			
			if(!"".equals(search_word)){
				if("A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
					pstmt.setString(++pidx, "%"+search_word+"%");
					
				}else{
					pstmt.setString(++pidx, "%"+search_word+"%");
				}
			}
			if(!"".equals(search_sdate) && !"".equals(search_edate)){
				if(search_sdate.equals(search_edate)){
					pstmt.setString(++pidx, search_sdate);
				}else{
					pstmt.setString(++pidx, search_sdate);
					pstmt.setString(++pidx, search_edate);
				}
			}
			if(!"".equals(search_status) && !"A".equals(search_status)){
				pstmt.setString(++pidx, search_status);
			}
			if(!"".equals(search_przwin) && !"A".equals(search_przwin)){
				pstmt.setString(++pidx, search_przwin);
			}
			
			
			
			pstmt.setLong(++pidx, page_no);
			rs = pstmt.executeQuery();
			
			//TOTAL COUNT 구하기
			pidx = 0;
			sql = this.getSelectTtCountQuery(search_clss, search_word, search_sdate, 
					search_edate, search_evnt, search_status, search_przwin);
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(++pidx, golf_svc_aplc_clss);
			pstmt.setString(++pidx, evnt_clss);
			if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
				pstmt.setString(++pidx, search_evnt);
			}
			if(!"".equals(search_word)){
				if("A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
					pstmt.setString(++pidx, "%"+search_word+"%");
					
				}else{
					pstmt.setString(++pidx, "%"+search_word+"%");
				}
			}
			if(!"".equals(search_sdate) && !"".equals(search_edate)){
				if(search_sdate.equals(search_edate)){
					pstmt.setString(++pidx, search_sdate);
				}else{
					pstmt.setString(++pidx, search_sdate);
					pstmt.setString(++pidx, search_edate);
				}
			}
			if(!"".equals(search_status) && !"A".equals(search_status)){
				pstmt.setString(++pidx, search_status);
			}
			if(!"".equals(search_przwin) && !"A".equals(search_przwin)){
				pstmt.setString(++pidx, search_przwin);
			}
			rs_count = pstmt.executeQuery();
			if(rs_count.next()){
				ttCnt = rs_count.getInt("CNT");
			}
			
			result.addString("ttCnt", Integer.toString(ttCnt));
			int serial = (int) (ttCnt - (10 * (page_no-1)));	
		
			
			while(rs.next()){
				if(!eof) {
					result.addString("RESULT", "00");
				}
			
				
				result.addLong("row_num",				rs.getLong("RNUM"));
				result.addString("seq_no",				rs.getString("APLC_SEQ_NO"));
				result.addString("evnt_nm",				rs.getString("EVNT_NM"));
				result.addString("usr_nm",				rs.getString("USR_NM"));
				result.addString("hp_ddd_no",			rs.getString("HP_DDD_NO"));
				result.addString("hp_tel_hno",			rs.getString("HP_TEL_HNO"));
				result.addString("hp_tel_sno",			rs.getString("HP_TEL_SNO"));
				result.addString("reg_aton",			rs.getString("REG_ATON"));
				result.addString("status",				rs.getString("STATUS"));
				result.addString("prz_win_yn",			rs.getString("PRZ_WIN_YN"));
				result.addInt("atr_num",				serial--);
				result.addString("total_cnt",			rs.getString("TOT_CNT"));
				result.addString("curr_page",			rs.getString("PAGE"));
				eof = true;
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
	private String getSelectQuery(String search_clss,String search_word,String search_sdate,String search_edate,
			String search_evnt,String search_status,String search_przwin) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*													");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM										");
		sql.append("\n 				,APLC_SEQ_NO									");
		sql.append("\n 				,EVNT_NM										");
		sql.append("\n 				,USR_NM					    					");
		sql.append("\n 				,HP_DDD_NO										");
		sql.append("\n 				,HP_TEL_HNO										");
		sql.append("\n 				,HP_TEL_SNO										");
		sql.append("\n 				,REG_ATON										");
		sql.append("\n 				,STATUS											");
		sql.append("\n 				,PRZ_WIN_YN										");
		sql.append("\n 				,CEIL(ROWNUM/10) AS PAGE						");
		sql.append("\n 				,MAX(RNUM) OVER() TOT_CNT 						");	
		sql.append("\n 				,((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  		");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM							"); 
		sql.append("\n 						,T1.APLC_SEQ_NO							");
		sql.append("\n 						,T2.EVNT_NM 							");
		sql.append("\n 						,(SELECT HG_NM AS NAME FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=T1.CDHD_ID)AS USR_NM  ");
		sql.append("\n 						,T1.HP_DDD_NO	 						");
		sql.append("\n 						,T1.HP_TEL_HNO							");
		sql.append("\n 						,T1.HP_TEL_SNO							");
		sql.append("\n 						,T1.PRZ_WIN_YN							");
		sql.append("\n 						,T2.EVNT_CLSS							");
		sql.append("\n 						,T2.EVNT_SEQ_NO							");
		sql.append("\n 						,TO_CHAR(TO_DATE(T1.REG_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD')AS REG_ATON				  ");
		sql.append("\n 						,(CASE WHEN TO_NUMBER(T2.EVNT_STRT_DATE) <= TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) AND TO_NUMBER(T2.EVNT_END_DATE) >= TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) THEN '1' ");
		sql.append("\n 							   WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) < TO_NUMBER(T2.EVNT_STRT_DATE) THEN '0'							");
		sql.append("\n 							   WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) > TO_NUMBER(T2.EVNT_END_DATE) THEN '2' ELSE '2' END)as STATUS 	");
		sql.append("\n 				FROM BCDBA.TBGAPLCMGMT T1 left join BCDBA.TBGEVNTMGMT T2 on T1.LESN_SEQ_NO = T2.EVNT_SEQ_NO 	");
		sql.append("\n 				WHERE  T1.GOLF_SVC_APLC_CLSS = ?				");
		sql.append("\n 				ORDER BY T1.APLC_SEQ_NO DESC					");
		sql.append("\n 				)												");
		sql.append("\n 		WHERE EVNT_CLSS =?										");
		if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
			sql.append("\n          AND  EVNT_SEQ_NO = ?							");
		}
		if(!"".equals(search_word)){
			if("A".equals(search_clss)){
				sql.append("\n 			AND ( EVNT_NM LIKE  ?	OR  USR_NM LIKE ? ) ");
			}else{
				sql.append("\n          AND  "+search_clss.trim()+"  LIKE  ?		");
			}
		}
		if(!"".equals(search_sdate) && !"".equals(search_edate)){
			if(search_sdate.equals(search_edate)){
				sql.append("\n 			AND  REG_ATON = ? 						 	");
			}else{
				sql.append("\n          AND (	REG_ATON BETWEEN ? AND ?	)		");
			}
		}
		if(!"".equals(search_status) && !"A".equals(search_status)){
			sql.append("\n          AND  STATUS = ?									");
		}
		if(!"".equals(search_przwin) && !"A".equals(search_przwin)){
			sql.append("\n          AND  PRZ_WIN_YN = ?								");
		}
		sql.append("\n 		ORDER BY RNUM 											");
		sql.append("\n 		)														");
		sql.append("\n WHERE PAGE = ?												");
		

		return sql.toString();
	}

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectTtCountQuery(String search_clss,String search_word,String search_sdate,String search_edate,
			String search_evnt,String search_status,String search_przwin) throws Exception{
		StringBuffer sql = new StringBuffer();
		sql.append("\n 		SELECT count(*)as CNT 									");
		sql.append("\n 		FROM ( SELECT	T1.APLC_SEQ_NO							");
		sql.append("\n 						,T2.EVNT_NM 							");
		sql.append("\n 						,(SELECT HG_NM AS NAME FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=T1.CDHD_ID)AS USR_NM  ");
		sql.append("\n 						,T1.HP_DDD_NO	 						");
		sql.append("\n 						,T1.HP_TEL_HNO							");
		sql.append("\n 						,T1.HP_TEL_SNO							");
		sql.append("\n 						,T1.PRZ_WIN_YN							");
		sql.append("\n 						,T2.EVNT_CLSS							");
		sql.append("\n 						,T2.EVNT_SEQ_NO							");
		sql.append("\n 						,TO_CHAR(TO_DATE(T1.REG_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD')AS REG_ATON				  ");
		sql.append("\n 						,(CASE WHEN TO_NUMBER(T2.EVNT_STRT_DATE) <= TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) AND TO_NUMBER(T2.EVNT_END_DATE) >= TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) THEN '1' ");
		sql.append("\n 							   WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) < TO_NUMBER(T2.EVNT_STRT_DATE) THEN '0'							");
		sql.append("\n 							   WHEN TO_NUMBER(TO_CHAR(SYSDATE,'yyyyMMdd')) > TO_NUMBER(T2.EVNT_END_DATE) THEN '2' ELSE '2' END)as STATUS 	");
		sql.append("\n 		FROM BCDBA.TBGAPLCMGMT T1 left join BCDBA.TBGEVNTMGMT T2 on T1.LESN_SEQ_NO = T2.EVNT_SEQ_NO 	");
		sql.append("\n 		WHERE  T1.GOLF_SVC_APLC_CLSS = ?						");
		sql.append("\n		)														");
		sql.append("\n 		WHERE EVNT_CLSS =?										");
		if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
			sql.append("\n          AND  EVNT_SEQ_NO = ?							");
		}
		if(!"".equals(search_word)){
			if("A".equals(search_clss)){
				sql.append("\n 			AND ( EVNT_NM LIKE  ?	OR  USR_NM LIKE ? ) ");
			}else{
				sql.append("\n          AND  "+search_clss.trim()+"  LIKE  ?		");
			}
		}
		if(!"".equals(search_sdate) && !"".equals(search_edate)){
			if(search_sdate.equals(search_edate)){
				sql.append("\n 			AND  REG_ATON = ? 						 	");
			}else{
				sql.append("\n          AND (	REG_ATON BETWEEN ? AND ?	)		");
			}
		}
		if(!"".equals(search_status) && !"A".equals(search_status)){
			sql.append("\n          AND  STATUS = ?									");
		}
		if(!"".equals(search_przwin) && !"A".equals(search_przwin)){
			sql.append("\n          AND  PRZ_WIN_YN = ?								");
		}
		
		
		return sql.toString();
	}

}
