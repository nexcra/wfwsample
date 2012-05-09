/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpWinnerInqDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한레슨이벤트 >당첨자목록
*   적용범위	: golf
*   작성일자	: 2009-07-07
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.special.winner;

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
public class GolfEvntSpWinnerInqDaoProc extends AbstractProc {
	public static final String TITLE = "이벤트라운지 > 특별한레슨이벤트 >레슨이벤트 목록";
	/** **************************************************************************
	 * Proc 실행.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		String sql = "";
 
		try {
			conn = context.getDbConnection("default", null);
			String evnt_clss 	= data.getString("evnt_clss");
			String search_word 	= data.getString("search_word");
			String search_clss 	= data.getString("search_clss");
			long page_no 		= data.getLong("page_no");
			
			int pidx = 0;
			boolean eof = false;
			sql = this.getSelectQuery(search_clss,search_word);
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++pidx, page_no);
			pstmt.setString(++pidx, evnt_clss);
			if(!"".equals(search_word)){
				if("A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
					pstmt.setString(++pidx, "%"+search_word+"%");
				}else{
					pstmt.setString(++pidx, "%"+search_word+"%");
				}
			}
			
			pstmt.setLong(++pidx, page_no);
			 
			rs = pstmt.executeQuery();
			
			int serial = 0;
			 
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				
				result.addString("seq_no", 			rs.getString("SEQ_NO"));
				result.addInt("serial", 			rs.getInt("ART_NUM") - serial);
				result.addString("titl", 			rs.getString("TITL"));
				result.addString("inqr_num", 		rs.getString("INQR_NUM"));
				result.addString("evnt_seq_no", 	rs.getString("EVNT_SEQ_NO"));
				result.addString("reg_aton",	 	rs.getString("REG_ATON"));
				result.addString("total_cnt",		rs.getString("TOT_CNT") );
				result.addString("curr_page",		rs.getString("PAGE") );
				eof = true;
				serial++;
			
			}
			if(!eof) result.addString("RESULT", "01");
			
			 
		} catch ( Exception e ) {			
			
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
	private String getSelectQuery(String search_clss,String search_word) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*													");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM										");
		sql.append("\n 				,SEQ_NO											");
		sql.append("\n 				,TITL											");
		sql.append("\n 				,INQR_NUM										");
		sql.append("\n 				,EVNT_SEQ_NO									");
		sql.append("\n 				,REG_ATON										");
		sql.append("\n 				,CEIL(ROWNUM/10) AS PAGE						");
		sql.append("\n 				,MAX(RNUM) OVER() TOT_CNT 						");	
		sql.append("\n 				,((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  		");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM							"); 
		sql.append("\n 						,T1.SEQ_NO								");
		sql.append("\n 						,T1.TITL								");
		sql.append("\n 						,T1.INQR_NUM							");
		sql.append("\n 						,T1.EVNT_SEQ_NO							");
		sql.append("\n 						,TO_CHAR(TO_DATE(T1.REG_ATON,'yyyy-MM-dd hh24miss'),'YYYY.MM.DD')as REG_ATON	");
		sql.append("\n 				FROM BCDBA.TBGEVNTPRZPEMGMT T1	left join BCDBA.TBGEVNTMGMT T2 on T1.EVNT_SEQ_NO = T2.EVNT_SEQ_NO	");
		sql.append("\n 				WHERE T1.BLTN_YN = 'Y'							");
		sql.append("\n 					AND T2.EVNT_CLSS = ?						");
		if(!"".equals(search_word)){
			if("A".equals(search_clss)){
				sql.append("\n 				AND (T1.TITL LIKE ? OR T1.CTNT LIKE ? )	");
			}else{
				sql.append("\n 				AND  T1."+search_clss.trim()+" LIKE ?	");
			}
		}
		sql.append("\n 				ORDER BY T1.SEQ_NO DESC							");
		sql.append("\n 				)												");
		sql.append("\n 		ORDER BY RNUM 											");
		sql.append("\n 		)														");
		sql.append("\n WHERE PAGE = ?												");
		

		return sql.toString();
	}

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectTtCountQuery(String search_clss, String search_word) throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT count(*)as CNT												");
		sql.append("\n FROM BCDBA.TBGEVNTPRZPEMGMT  T1	left join BCDBA.TBGEVNTMGMT T2 on T1.EVNT_SEQ_NO = T2.EVNT_SEQ_NO				");
		sql.append("\n WHERE  T1.BLTN_YN = 'Y'	AND T2.EVNT_CLSS = ?						");
		
		if(!"".equals(search_word)){
			if("A".equals(search_clss)){
				sql.append("\n 				AND (T1.TITL LIKE ? OR T1.CTNT LIKE ? )			");
			}else{
				sql.append("\n 				AND  T1."+search_clss.trim()+" LIKE ?			");
			}
		}
		
		return sql.toString();
	}
	
	
	/** ***********************************************************************
	* 총 게시물 수를 리턴
	************************************************************************ */
	public String getTtCount(WaContext context, TaoDataSet data) throws Exception{
		
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection con = null; 
		String result = "0";
		
		
		try{
			String evnt_clss 	= data.getString("evnt_clss");
			String search_word = data.getString("search_word");
			String search_clss = data.getString("search_clss");
			
			
			String sql = this.getSelectTtCountQuery(search_clss,search_word);
			con = context.getDbConnection("default", null);
			int pidx = 0;
			pstmt = con.prepareStatement(sql);
			pstmt.setString(++pidx, evnt_clss);
			if(!"".equals(search_word)){
				if("A".equals(search_clss)){
					pstmt.setString(++pidx, "%"+search_word+"%");
					pstmt.setString(++pidx, "%"+search_word+"%");
				}else{
					pstmt.setString(++pidx, "%"+search_word+"%");
				}
			}
			
			rset = pstmt.executeQuery();
			
			if(rset.next()){
				result = rset.getString("CNT");
			}else{
				result = "0";
			}
			
		}catch(Exception ex){
			
			
			//debug(">>>>>>>>>>>>>> ERROR getTtCount : "+ex.toString());
		}finally{
			try{ if(rset  != null) rset.close();  }catch( Exception ignored){}
			try{ if(pstmt != null) pstmt.close(); }catch( Exception ignored){}
			try{ if(con != null) con.close(); }catch( Exception ignored){}
		}
		
		return result;
	}
}
