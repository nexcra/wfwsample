/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardComListDaoProc
*   작성자     : (주)미디어포스 임은혜
*   내용        : 관리자 게시판관리 목록 조회 
*   적용범위  : Golf
*   작성일자  : 2009-05-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.board;

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

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-16 
 **************************************************************************** */
public class GolfAdmBoardComListDaoProc extends AbstractProc {
	
	public static final String TITLE = "게시판 관리 목록 조회";
	
	
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
		
		//debug("==== GolfAdmBoardComListDaoProc start ===");
		
		GolfConfig config = GolfConfig.getInstance();
		
		try{
			//조회 조건
			String search_yn	= dataSet.getString("search_yn"); 		//검색여부
			String search_clss	= "";									//검색어구분
			String search_word	= "";									//검색어
			if("Y".equals(search_yn)){
				search_clss	= dataSet.getString("search_clss"); 		// 검색어
				search_word	= dataSet.getString("search_word"); 		// 제목검색여부
			}
			long page_no = dataSet.getLong("page_no")==0L?1L:dataSet.getLong("page_no");
			long page_size = dataSet.getLong("page_size")==0L?10L:dataSet.getLong("page_size");
			String boardid	= dataSet.getString("boardid"); 		
			
			String sql = this.getSelectQuery(search_yn,search_clss,search_word);			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			pstmt.setLong(++pidx, page_no);
			pstmt.setString(++pidx, boardid);
			pstmt.setLong(++pidx, page_no);

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			int art_num_no = 0;
			String golf_clm_clss = "";
			String title = "";
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				if(!GolfUtil.empty(rset.getString("GOLF_CLM_CLSS"))){
					golf_clm_clss = rset.getString("GOLF_CLM_CLSS");
				}else{
					golf_clm_clss = "";
				}
				
				if(!GolfUtil.empty(rset.getString("TITL"))){
					title = rset.getString("TITL");
				}
				
				if(golf_clm_clss.equals("0001")){
					title = "[알펜시아] "+title;
				}else if(golf_clm_clss.equals("0002")){
					title = "[해외프라임] "+title;
				}
				
				result.addLong("row_num",				rset.getLong("RNUM"));
				result.addString("SEQ_NO",				rset.getString("SEQ_NO"));
				result.addString("EPS_YN",				rset.getString("EPS_YN"));
				result.addString("TITL",				title);
				result.addString("REG_ATON",			rset.getString("REG_ATON"));
				result.addLong("INOR_NUM",				rset.getLong("INOR_NUM"));
				result.addString("GOLF_BOKG_FAQ_CLSS",	rset.getString("GOLF_BOKG_FAQ_CLSS"));

				result.addInt("ART_NUM" 				,rset.getInt("ART_NUM")-art_num_no );
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

		
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery(String search_yn,String search_clss,String search_word) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*	");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM, CEIL(ROWNUM/10) AS PAGE,	MAX(RNUM) OVER() TOT_CNT, 	");
		sql.append("\n 				SEQ_NO,							");
		sql.append("\n 				EPS_YN,							");
		sql.append("\n 				TITL,							");
		sql.append("\n 				REG_ATON,						");
		sql.append("\n 				INOR_NUM, GOLF_BOKG_FAQ_CLSS, GOLF_CLM_CLSS	");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,			");
		sql.append("\n 						T1.BBRD_SEQ_NO AS SEQ_NO,					");
		sql.append("\n 						(CASE T1.EPS_YN WHEN 'Y' THEN '노출' ELSE '비노출' END) AS EPS_YN,					");
		sql.append("\n 						T1.TITL,					");
		sql.append("\n 						TO_CHAR(TO_DATE(T1.REG_ATON),'YYYY-MM-DD') AS REG_ATON,				");
		sql.append("\n 						T1.INQR_NUM AS INOR_NUM				");
		sql.append("\n 						, T3.GOLF_CMMN_CODE_NM AS GOLF_BOKG_FAQ_CLSS, T1.GOLF_CLM_CLSS			");
		sql.append("\n 				FROM BCDBA.TBGBBRD T1			");
		sql.append("\n 				JOIN BCDBA.TBGMGRINFO T2 ON T1.REG_MGR_ID=T2.MGR_ID			");
		sql.append("\n 				LEFT JOIN BCDBA.TBGCMMNCODE T3 ON T1.GOLF_BOKG_FAQ_CLSS=T3.GOLF_CMMN_CODE AND GOLF_CMMN_CLSS='0018'			");
		sql.append("\n 				WHERE T1.BBRD_CLSS=?		");
		if("Y".equals(search_yn)){
			if("".equals(search_clss)){
				sql.append("\n 	AND ( TITL like '%"+ search_word + "%' or CTNT like '%"+ search_word +"%')" );
			}else{
				sql.append("\n 	AND "+ search_clss +"  like '%"+ search_word +"%'");
			}
		}
		sql.append("\n 				ORDER BY BBRD_SEQ_NO DESC			");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
}
