/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmEvntBsLsnAcceptInqDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  이벤트 > 특별레슨 이벤트 당참자관리 목록 조회
*   적용범위	: golf
*   작성일자	: 2009-07-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.accept;

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
public class GolfAdmEvntBsLsnAcceptInqDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 >  이벤트 > 특별레슨 이벤트 당참자관리 목록 조회";
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
	
		try {
			
			conn = context.getDbConnection("default", null);
			String evnt_clss 	= data.getString("evnt_clss");
			String search_word 	= data.getString("search_word");
			String search_clss 	= data.getString("search_clss");
			String search_eps 	= data.getString("search_eps");
			String search_evnt 	= data.getString("search_evnt");
			long page_no 		= data.getLong("page_no");
			
			int pidx = 0;
			boolean eof = false;

			
			
			
			sql = this.getSelectQuery(search_word,search_clss,search_eps,search_evnt);
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++pidx,page_no);
			pstmt.setString(++pidx,evnt_clss);
			if(!"".equals(search_word)){
				if("A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
					pstmt.setString(++pidx, "%"+search_word+"%");
				}else{
					pstmt.setString(++pidx, "%"+search_word+"%");
				}
			}
			if(!"".equals(search_eps) && !"A".equals(search_eps)){
				pstmt.setString(++pidx, search_eps);
			}
			if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
				pstmt.setString(++pidx, search_evnt);
			}
			
			pstmt.setLong(++pidx,page_no);
			
			rs = pstmt.executeQuery();
			
			
			//total 게시물 리턴
			pidx = 0;
			sql = this.getSelectTtCountQuery(search_word,search_clss,search_eps,search_evnt);
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++pidx,evnt_clss);
			if(!"".equals(search_word)){
				if("A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
					pstmt.setString(++pidx, "%"+search_word+"%");
				}else{
					pstmt.setString(++pidx, "%"+search_word+"%");
				}
			}
			if(!"".equals(search_eps) && !"A".equals(search_eps)){
				pstmt.setString(++pidx, search_eps);
			}
			if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
				pstmt.setString(++pidx, search_evnt);
			}
			rs_count = pstmt.executeQuery();
			
			String ttCnt = "0";
			if(rs_count.next()){
				ttCnt = rs_count.getString("CNT");
			}
			
			result.addString("ttCnt", ttCnt);
			
			
			int art_num_no = 0;
			
			while(rs.next()){
				if(!eof) {
					result.addString("RESULT", "00");
				}
				
				result.addLong("row_num",				rs.getLong("RNUM"));
				result.addString("seq_no",				rs.getString("SEQ_NO"));
				result.addString("titl",				rs.getString("TITL"));
				result.addString("bltn_yn",				rs.getString("BLTN_YN"));
				result.addString("reg_aton",			rs.getString("REG_ATON"));
				result.addString("inqr_num",			rs.getString("INQR_NUM"));;
				result.addLong("atr_num",				rs.getLong("ART_NUM")-art_num_no);
				result.addString("total_cnt",			rs.getString("TOT_CNT"));
				result.addString("curr_page",			rs.getString("PAGE"));
				eof = true;
				art_num_no++;
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
	private String getSelectQuery(String search_word,String search_clss,
			String search_eps,String search_evnt) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*													");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM										");
		sql.append("\n 				,SEQ_NO											");
		sql.append("\n 				,TITL											");
		sql.append("\n 				,BLTN_YN					    				");
		sql.append("\n 				,REG_ATON										");
		sql.append("\n 				,INQR_NUM										");
		sql.append("\n 				,CEIL(ROWNUM/10) AS PAGE						");
		sql.append("\n 				,MAX(RNUM) OVER() TOT_CNT 						");	
		sql.append("\n 				,((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  		");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM							"); 
		sql.append("\n 						,T1.SEQ_NO								");
		sql.append("\n 						,T1.TITL								");
		sql.append("\n 						,T1.BLTN_YN								");
		sql.append("\n 						,TO_CHAR(TO_DATE(T1.REG_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD') AS REG_ATON	   ");
		sql.append("\n 						,T1.INQR_NUM							");
		sql.append("\n 				FROM BCDBA.TBGEVNTPRZPEMGMT T1 left join BCDBA.TBGEVNTMGMT T2	");
		sql.append("\n 					on T1.EVNT_SEQ_NO = T2.EVNT_SEQ_NO 			");
		sql.append("\n 				WHERE  T2.EVNT_CLSS = ?							");
		if(!"".equals(search_word)){
			if("A".equals(search_clss)){
				sql.append("\n 			AND ( T1.TITL LIKE  ?	 OR T1.CTNT LIKE ?)	");
			}else{
				sql.append("\n          AND T1."+search_clss.trim()+" LIKE  ?		");
			}
		}
		if(!"".equals(search_eps) && !"A".equals(search_eps)){
			sql.append("\n          AND T1.BLTN_YN =  ?								");
		}
		if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
			sql.append("\n          AND T1.EVNT_SEQ_NO =  ?							");
		}
		sql.append("\n 				ORDER BY T2.EVNT_SEQ_NO DESC					");
		sql.append("\n 				)												");
		sql.append("\n 		ORDER BY RNUM 											");
		sql.append("\n 		)														");
		sql.append("\n WHERE PAGE = ?												");
		

		return sql.toString();
	}

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectTtCountQuery(String search_word,String search_clss,
			String search_eps,String search_evnt) throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT count(*)as CNT 										");
		sql.append("\n FROM BCDBA.TBGEVNTPRZPEMGMT T1 left join BCDBA.TBGEVNTMGMT T2 on T1.EVNT_SEQ_NO = T2.EVNT_SEQ_NO		");
		sql.append("\n WHERE  T2.EVNT_CLSS = ?										");
		if(!"".equals(search_word)){
			if("A".equals(search_clss)){
				sql.append("\n 			AND ( T1.TITL LIKE  ?	 OR T1.CTNT LIKE ?)	");
			}else{
				sql.append("\n          AND T1."+search_clss.trim()+" LIKE  ?		");
			}
		}
		if(!"".equals(search_eps) && !"A".equals(search_eps)){
			sql.append("\n          AND T1.BLTN_YN =  ?								");
		}
		if(!"".equals(search_evnt) && !"A".equals(search_evnt)){
			sql.append("\n          AND T1.EVNT_SEQ_NO =  ?							");
		}
		
		return sql.toString();
	}
	
}
