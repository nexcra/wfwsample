/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmLsnUccInqActn
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  레슨 > UCC 레슨 목록 조회 처리
*   적용범위	: golf
*   작성일자	: 2009-07-02
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.lesson.ucc;

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
public class GolfAdmLsnUccInqDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 >  레슨 > UCC 레슨 목록 조회 처리";
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
			// 회원통합테이블 관련 수정사항 진행
			conn = context.getDbConnection("default", null);
			String bbrd_clss 	= data.getString("bbrd_clss");
			String search_clss 	= data.getString("search_clss");
			String search_word 	= data.getString("search_word");
			String search_answ 	= data.getString("search_answ");
			long page_no 		= data.getLong("page_no");
			
			int pidx = 0;
			boolean eof = false;
			
			sql = this.getSelectQuery(search_clss, search_answ);
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(++pidx,page_no);
			pstmt.setString(++pidx,bbrd_clss);
			pstmt.setLong(++pidx,page_no);
			if(!"".equals(search_clss) && !"A".equals(search_clss)){
				pstmt.setString(++pidx, "%"+search_word+"%");
				
			}else if("A".equals(search_clss)){
				pstmt.setString(++pidx, "%"+search_word+"%");
				pstmt.setString(++pidx, "%"+search_word+"%");
			}
			if(!"".equals(search_answ) && !"A".equals(search_answ)){
				pstmt.setString(++pidx,search_answ);					
			}
			
			rs = pstmt.executeQuery();
			int art_num_no = 0;
			
			while(rs.next()){
				if(!eof) {
					result.addString("RESULT", "00");
				}
				
				result.addLong("row_num",				rs.getLong("RNUM"));
				result.addString("seq_no",				rs.getString("BBRD_SEQ_NO"));
				result.addString("titl",				rs.getString("TITL"));
				result.addString("eps_yn",				rs.getString("EPS_YN"));
				result.addString("del_yn",				rs.getString("DEL_YN"));
				result.addString("reg_aton",			rs.getString("REG_ATON"));
				result.addString("inqr_num",			rs.getString("INQR_NUM"));
				result.addString("use_nm",				rs.getString("USE_NM"));
				result.addString("answ_yn",				rs.getString("ANSW_YN"));
				result.addLong("atr_num",				rs.getLong("ART_NUM")-art_num_no);
				result.addString("total_cnt",			rs.getString("TOT_CNT") );
				result.addString("curr_page",			rs.getString("PAGE") );
				eof = true;
				art_num_no++;
			}
			
			if(!eof) {
				result.addString("RESULT", "01");
			}
			 
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
	private String getSelectQuery(String search_clss, String search_answ) throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*													");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM										");
		sql.append("\n 				,BBRD_SEQ_NO									");
		sql.append("\n 				,TITL											");
		sql.append("\n 				,CTNT											");
		sql.append("\n 				,EPS_YN											");
		sql.append("\n 				,DEL_YN											");
		sql.append("\n 				,REG_ATON										");
		sql.append("\n 				,INQR_NUM										");
		sql.append("\n 				,USE_NM											");
		sql.append("\n 				,ANSW_YN										");
		sql.append("\n 				,CEIL(ROWNUM/10) AS PAGE						");
		sql.append("\n 				,MAX(RNUM) OVER() TOT_CNT 						");	
		sql.append("\n 				,((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  		");
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM							"); 
		sql.append("\n 						,T1.BBRD_SEQ_NO							");
		sql.append("\n 						,T1.TITL								");
		sql.append("\n 						,T1.CTNT								");
		sql.append("\n 						,T1.EPS_YN								");
		sql.append("\n 						,T1.DEL_YN								");
		sql.append("\n 						,TO_CHAR(TO_DATE(T1.REG_ATON),'YYYY-MM-DD') AS REG_ATON	");
		sql.append("\n 						,T1.INQR_NUM							");
		sql.append("\n 						,(SELECT  HG_NM AS NAME FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = T1.ID)as USE_NM	    ");
		sql.append("\n 						,(CASE WHEN T1.ANSW_CTNT is not null THEN 'Y' ELSE 'N' END)as ANSW_YN	");
		sql.append("\n 				FROM BCDBA.TBGBBRD	T1							");
		sql.append("\n 				WHERE T1.BBRD_CLSS = ?							");
		sql.append("\n 				ORDER BY T1.BBRD_SEQ_NO DESC					");
		sql.append("\n 				)												");
		sql.append("\n 		ORDER BY RNUM 											");
		sql.append("\n 		)														");
		sql.append("\n WHERE PAGE = ?												");
		if(!"".equals(search_clss) && !"A".equals(search_clss)){
			sql.append("\n 	AND "+search_clss+" LIKE   ?							");
		}else if("A".equals(search_clss)){
			sql.append("\n 	AND ( TITL  LIKE   ?	 OR	 CTNT LIKE ? )				");
		}
		if(!"".equals(search_answ) && !"A".equals(search_answ)){
			sql.append("\n 	AND ANSW_YN  =   ?										");
		}

		return sql.toString();
	}

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectTtCountQuery(String search_clss, String search_answ) throws Exception{
		StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT count(*)as CNT FROM BCDBA.TBGBBRD 					");
		sql.append("\n WHERE  BBRD_CLSS = ? 										");
		if(!"".equals(search_clss) && !"A".equals(search_clss)){
			sql.append("\n 	AND "+search_clss+" LIKE   ?							");
		}else if("A".equals(search_clss)){
			sql.append("\n 	AND ( TITL  LIKE   ?	 OR	 CTNT LIKE ? )				");
		}
		if(!"".equals(search_answ) && !"A".equals(search_answ)){
			sql.append("\n 	AND ANSW_YN  =   ?										");
		}
		
		return sql.toString();
	}
	
	
	/** ***********************************************************************
	* 총 게시물 수를 리턴
	************************************************************************ */
	public String getTtCount(WaContext context, TaoDataSet dataSet) throws Exception{
		
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		Connection con = null;
		String result = "0";
		
		
		try{
			String bbrd_clss 	= dataSet.getString("bbrd_clss");
			String search_clss 	= dataSet.getString("search_clss");
			String search_word 	= dataSet.getString("search_word");
			String search_answ 	= dataSet.getString("search_answ");
			
			String sql = this.getSelectTtCountQuery(search_clss, search_answ);
			con = context.getDbConnection("default", null);
			int pidx = 0;
			pstmt = con.prepareStatement(sql);
			pstmt.setString(++pidx, bbrd_clss);
			if(!"".equals(search_clss) && !"A".equals(search_clss)){
				pstmt.setString(++pidx, "%"+search_word+"%");
				
			}else if("A".equals(search_clss)){
				pstmt.setString(++pidx, "%"+search_word+"%");
				pstmt.setString(++pidx, "%"+search_word+"%");
			}
			if(!"".equals(search_answ) && !"A".equals(search_answ)){
				pstmt.setString(++pidx,search_answ);					
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
