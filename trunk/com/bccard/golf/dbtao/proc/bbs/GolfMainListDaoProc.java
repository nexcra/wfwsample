/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointMainListDaoProc
*   작성자    : (주)미디어포스 권영만
*   내용      : 메인 비지니스 로직
*   적용범위  : Golf
*   작성일자  : 2009-03-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.bbs; 

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;


public class GolfMainListDaoProc extends AbstractProc {
		
		private static final String TITLE = "Golf 메인 페이지";
	
		/** *****************************************************************
		 * BcasLoadProc 프로세스 생성자
		 * @param N/A
		 ***************************************************************** */
		public GolfMainListDaoProc() {}
			
		/**
		 * Proc 실행.
		 * @param Connection con
		 * @param TaoDataSet dataSet
		 * @return TaoResult
		 */
		public DbTaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet dataSet) throws DbTaoException {
			PreparedStatement pstmt = null;
			ResultSet rset = null;
			DbTaoResult result = null;
			Connection con = null;
				
			//debug("==== GolfPointMainlnqDaoProc start ===");
			try{
				//조회 조건 Validation
				String search_yn	= dataSet.getString("search_yn"); 		//검색여부
				String search_clss	= "";									//검색어구분
				String search_word	= "";									//검색어
				
				if("Y".equals(search_yn)){
					search_clss	= dataSet.getString("search_clss"); 			// 검색어
					search_word	= dataSet.getString("search_word"); 				// 제목검색여부
				}

				int page_no = dataSet.getInt("page_no")==0?1:dataSet.getInt("page_no");
				

				String curDateFormated = "";

				String sql = this.getSelectQuery(search_yn,search_clss);

				////debug("==== GolfPointMainlnqDaoProc Status === 1");
				con = context.getDbConnection("default", null);
				////debug("==== GolfPointMainlnqDaoProc Status === 2");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;

				if("Y".equals(search_yn)){
					pstmt.setString(++pidx, "%"+search_word+"%");
				}
				pstmt.setLong(++pidx, page_no);

				rset = pstmt.executeQuery();
				////debug("==== GolfPointMainlnqDaoProc Status === 3");

				result = new DbTaoResult(TITLE);
				boolean existsData = false;

				while(rset.next()){

					if(!existsData){
						result.addString("RESULT", "00");
					}
					//debug(rset.getString("TITLE"));

					curDateFormated = DateUtil.format(rset.getString("REG_DATE"),"yyyyMMdd","yyyy/MM/dd");
					
					result.addLong("row_num", rset.getLong("RNUM"));
					result.addLong("row_seq", rset.getLong("SEQ"));
					result.addString("row_title", rset.getString("TITLE"));
					result.addString("row_date", curDateFormated);
					result.addString("total_cnt", rset.getString("TOT_CNT") );
					result.addString("curr_page", rset.getString("PAGE") );
					existsData = true;
				}

				if(!existsData){
					result.addString("RESULT","01");
				}
				
			//debug("==== GolfPointMainlnqDaoProc end ===");

			}catch ( Exception e ) {
				//debug("==== GolfPointMainlnqDaoProc Start ===");
				e.printStackTrace();
				//debug("==== GolfPointMainlnqDaoProc End ===");
				MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
	            throw new DbTaoException(msgEtt,e);
			}finally{
				try { if(rset != null) {rset.close();} else{} } catch (Exception ignored) {}
				try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
				try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
			}
			return result;
		}
	
	
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery(String search_yn,String search_clss) throws Exception{
	
		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*					");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,		");
		sql.append("\n 				SEQ, TITLE,	REG_DATE, 	");
		sql.append("\n 				CEIL(ROWNUM/10) AS PAGE,	");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,		");
		sql.append("\n 						SEQ, TITLE,	REG_DATE		");
		sql.append("\n 				FROM BBS					");
if("Y".equals(search_yn)){
	if("T".equals(search_clss)){
		sql.append("\n 				WHERE TITLE  like ?		");
	}else if("C".equals(search_clss)){
		sql.append("\n 				WHERE CONTENT  like ?	");
	}
}
		sql.append("\n 				ORDER BY SEQ DESC	");
		sql.append("\n 				)																						");
		sql.append("\n 		ORDER BY RNUM 																		");
		sql.append("\n 		)																								");
		sql.append("\n WHERE PAGE = ?																				");

		return sql.toString();
	}
}