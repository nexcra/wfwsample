/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfLessonUccPreNextDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 친절한 ucc 레슨 목록 이전, 다음게시물 리턴
*   적용범위	: golf
*   작성일자	: 2009-07-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.lesson;

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
 * Golf
 * @author	미디어포스
 * @version	1.0 
 ******************************************************************************/
public class GolfLessonUccPreNextDaoProc extends AbstractProc {
	public static final String TITLE = "친절한 ucc 레슨 상세 처리";
	
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
		String sql = "";
		
		try{  
			//01. 조회 조건
			String bbrd_clss = dataSet.getString("bbrd_clss");
			String idx 		 = dataSet.getString("idx");
			String chk 		 = dataSet.getString("chk");

			// 02. 환경설정
			result = new DbTaoResult(TITLE);
			boolean existsData = false;
			con = context.getDbConnection("default", null);
			
			// 03. 쿼리가져오기
			int pidx = 0;
			if("pre".equals(chk)){
				sql = this.getPreQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, bbrd_clss);
				pstmt.setString(++pidx, idx);
				
				rset = pstmt.executeQuery();
				
				while(rset.next()){
					if(!existsData){
						result.addString("RESULT","00");
					}
					result.addString("seq_no",rset.getString("BBRD_SEQ_NO"));
					result.addString("titl", rset.getString("TITL"));
					existsData = true;
				}
				
				
			}else if("next".equals(chk)){
				sql = this.getNextQuery();
				pstmt = con.prepareStatement(sql);
				pstmt.setString(++pidx, bbrd_clss);
				pstmt.setString(++pidx, idx);
				
				rset = pstmt.executeQuery();
				
				while(rset.next()){
					if(!existsData){
						result.addString("RESULT","00");
					}
					result.addString("seq_no",rset.getString("BBRD_SEQ_NO"));
					result.addString("titl", rset.getString("TITL"));
					existsData = true;
				}
				
			}
			
			if(!existsData){
				result.addString("RESULT","01");
			}
		
						
			
		}catch ( Exception e ) {
			
			
			
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
	private String getPreQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 		SELECT													");
		sql.append("\n 					BBRD_SEQ_NO									");
		sql.append("\n 					,TITL										");
		sql.append("\n 		FROM ( SELECT BBRD_SEQ_NO								");
		sql.append("\n 					,TITL										");
		sql.append("\n 				FROM BCDBA.TBGBBRD 								");
		sql.append("\n 				WHERE 	BBRD_CLSS = ?							");
		sql.append("\n 					AND  EPS_YN = 'Y' 							");
		sql.append("\n 					AND  DEL_YN = 'N' 							");
		sql.append("\n 					AND  BBRD_SEQ_NO > ? 						");	
		sql.append("\n 				ORDER BY  BBRD_SEQ_NO ASC	)					");	
		sql.append("\n 		WHERE 	ROWNUM = 1										");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getNextQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 		SELECT													");
		sql.append("\n 					BBRD_SEQ_NO									");
		sql.append("\n 					,TITL										");
		sql.append("\n 		FROM ( SELECT BBRD_SEQ_NO								");
		sql.append("\n 					,TITL										");
		sql.append("\n 				FROM BCDBA.TBGBBRD 								");
		sql.append("\n 				WHERE 	BBRD_CLSS = ?							");
		sql.append("\n 					AND  EPS_YN = 'Y' 							");
		sql.append("\n 					AND  DEL_YN = 'N' 							");
		sql.append("\n 					AND  BBRD_SEQ_NO < ? 						");	
		sql.append("\n 				ORDER BY  BBRD_SEQ_NO DESC	)					");	
		sql.append("\n 		WHERE 	ROWNUM = 1										");

		return sql.toString();
	}
	
}
