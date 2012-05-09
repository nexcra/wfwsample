/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmBoardComlnqDaoProc
*   작성자     : (주)미디어포스 임은혜
*   내용        : 관리자 게시판관리 목록 조회 
*   적용범위  : Golf
*   작성일자  : 2009-03-31
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
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;

/** ****************************************************************************
 * Media4th / Golf
 * @author 
 * @version 2009-03-31 
 **************************************************************************** */
public class GolfAdmBoardComlnqDaoProc extends AbstractProc {
	
	public static final String TITLE = "게시판 저장 처리";
	
	
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
		
		//debug("==== GolfAdmBoardComlnqDaoProc start ===");
		
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
								

			String sql = this.getSelectQuery(search_yn,search_clss);
		
			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int pidx = 0;
			pstmt.setLong(++pidx, page_no);

			rset = pstmt.executeQuery();
			
			
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			
			while(rset.next()){

				if(!existsData){
					result.addString("RESULT", "00");
				}
				
				result.addLong("row_num",				rset.getLong("RNUM"));
				result.addLong("BOARDID",				rset.getLong("BBRD_SEQ_NO"));
				result.addString("BOARD_CODE",			rset.getString("BBRD_CLSS"));
				result.addString("USE_YN",				rset.getString("USE_YN"));
				result.addString("RG_SEQ_NO",			rset.getString("REG_MGR_SEQ_NO"));
				result.addString("UP_SEQ_NO",			rset.getString("CORR_MGR_SEQ_NO"));
				result.addString("REG_DATE",			rset.getString("REG_DATE"));
				result.addString("MOD_DATE",			rset.getString("CORR_DATE"));
				
				result.addString("REG_MGR_ID",			rset.getString("REG_MGR_ID"));
				result.addString("HG_NM",				rset.getString("HG_NM"));
				
				result.addString("total_cnt",			rset.getString("TOT_CNT") );
				result.addString("curr_page",			rset.getString("PAGE") );
				
				existsData = true;
				
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		
			//debug("==== GolfAdmBoardComlnqDaoProc end ===");
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComlnqDaoProc ERROR ===");
			
			//debug("==== GolfAdmBoardComlnqDaoProc ERROR ===");
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
	private String getSelectQuery(String search_yn,String search_clss) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*																						");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,				");
		sql.append("\n 				BBRD_SEQ_NO,				");
		sql.append("\n 				BBRD_CLSS,					");
		sql.append("\n 				USE_YN,						");
		sql.append("\n 				REG_DATE,					");
		sql.append("\n 				CORR_DATE,					");
		sql.append("\n 				REG_MGR_ID,					");
		sql.append("\n 				HG_NM,					");
		sql.append("\n 				CEIL(ROWNUM/10) AS PAGE,	");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT	");	
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM,		");
		sql.append("\n 						T1.BBRD_SEQ_NO,		");
		sql.append("\n 						T1.BBRD_CLSS,			");
		sql.append("\n 						T1.EPS_YN AS USE_YN,				");
		sql.append("\n 						T1.REG_ATON AS REG_DATE,				");
		sql.append("\n 						T1.CHNG_ATON AS CORR_DATE, 				");		
		sql.append("\n 						T1.REG_MGR_ID, 				");			
		sql.append("\n 						T2.HG_NM				");		
		sql.append("\n 				FROM BCDBA.TBGBBRD T1		");	
		sql.append("\n 				JOIN BCDBA.TBGMGRINFO T2 ON T1.REG_MGR_ID=T2.MGR_ID		");
		sql.append("\n 				WHERE 1=1 						");
		sql.append("\n 				ORDER BY BBRD_SEQ_NO DESC		");
		sql.append("\n 				)								");
		sql.append("\n 		ORDER BY RNUM 							");
		sql.append("\n 		)										");
		sql.append("\n WHERE PAGE = ?								");

		return sql.toString();
	}
}
