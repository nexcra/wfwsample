/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmTopGolfRoundAfterListProc
*   작성자    : shin cheong gwi
*   내용      : 라운딩후기 내역
*   적용범위  : golfloung
*   작성일자  : 2010-11-22
************************** 수정이력 ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

//import com.bccard.fortify.FilterUtil;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfAdmTopGolfRoundAfterListProc extends AbstractObject {

	private static GolfAdmTopGolfRoundAfterListProc instance = null;
	static{
		synchronized(GolfAdmTopGolfRoundAfterListProc.class){
			if(instance == null){
				instance = new GolfAdmTopGolfRoundAfterListProc();
			}
		}
	}
	public static GolfAdmTopGolfRoundAfterListProc getInstance(){
		return instance;
	}
	
	/*
	 * 라운딩 게시물 Proc
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException 
	{
		String title = dataSet.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		int idx = 0;
		String vsearch_word = "";
				
		try
		{
			String add_yn = dataSet.getString("add_yn");				// 댓글의 개수
			String board_cd = dataSet.getString("board_cd"); 			// 게시물코드
			String board_dtl_cd = dataSet.getString("board_dtl_cd");	// 게시물세부코드
			String search_type = dataSet.getString("search_type");		// 검색구분
			String search_word = dataSet.getString("search_word");		// 검색어			
			long pageNo = dataSet.getLong("pageNo")==0L?1L:dataSet.getLong("pageNo");
			long recordsInPage = dataSet.getLong("recordsInPage")==0L?10L:dataSet.getLong("recordsInPage");
			
			if (pageNo <= 0) pageNo = 1L;
			long startR = (pageNo-1L) * recordsInPage + 1L;
			long endR = pageNo * recordsInPage;	
			vsearch_word = "%"+search_word+"%";
			
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getRoundingListSelect(dataSet).toString());
				
				pstmt.setString(++idx, board_cd);	
				if(!search_word.equals("")){
					if(search_type.equals("BOARD_SUBJ") || search_type.equals("BOARD_TEXT")){					
						pstmt.setString(++idx, vsearch_word);
					}
				}
				pstmt.setString(++idx, board_cd);	
				
				if(!search_word.equals("")){
					if(search_type.equals("BOARD_SUBJ") || search_type.equals("BOARD_TEXT")){					
						pstmt.setString(++idx, vsearch_word);					
					}	
				}
				pstmt.setLong(++idx, endR);
				pstmt.setLong(++idx, startR);
				pstmt.setLong(++idx, endR);				
				
			rs = pstmt.executeQuery();
			long record_cnt = 0;
			boolean existsData = false;
			String subj = null;
			int i =1;
						
			while(rs.next()){

				if(!existsData){
					result.addString("RESULT", "00");
					record_cnt = rs.getLong("RECORD_CNT");
				}
				result.addLong("BOARD_NO", rs.getLong("BOARD_NO"));
				
				subj = "";//FilterUtil.getXSSFilter(rs.getString("BOARD_SUBJ"));
				
				if (subj.getBytes().length > 50){
					subj = GolfUtil.getCutKSCString(subj,50,"...");
				}
				
				result.addString("BOARD_SUBJ", subj);
				result.addLong("READ_CNT", rs.getLong("READ_CNT"));            
				result.addString("LIST_INQ_CLSS", rs.getString("LIST_INQ_CLSS"));
				result.addString("LIST_IMG_PATH", rs.getString("LIST_IMG_PATH"));
                result.addLong("SORT_KEY", rs.getLong("SORT_KEY"));
                result.addLong("REF_NO", rs.getLong("REF_NO"));
                result.addLong("ANS_STG", rs.getLong("ANS_STG"));
                result.addLong("ANS_LEV", rs.getLong("ANS_LEV"));
                result.addString("REG_DATE", rs.getString("REG_DATE"));
                result.addString("REG_NM", rs.getString("REG_NM"));
                result.addString("REG_ACCOUNT", rs.getString("REG_ACCOUNT"));
				result.addString("REG_IP", rs.getString("REG_IP"));
				result.addLong("RECORD_CNT", rs.getLong("RECORD_CNT"));
                result.addString("REG_DATE2", rs.getString("REG_DATE2"));
                result.addString("TODAY", rs.getString("TODAY"));
                result.addString("HOT_INFO_YN", rs.getString("HOT_INFO_YN"));
                result.addString("ATC_FILE_YN", rs.getString("ATC_FILE_YN"));
                result.addLong("ROW_NUM", record_cnt - (recordsInPage * (pageNo-1) + (i++)) + 1);
				result.addLong("PAGENO", pageNo);
				result.addString("NEWYN" , rs.getString("NEWYN") );
				result.addString("SCOR_APPL", rs.getString("SCOR_APPL"));
				if(!board_cd.equals("12")){
					result.addInt("AN_CNT", rs.getInt("AN_CNT"));
				}
				
				// 댓글 개수
				if ( "Y".equals(add_yn) ) {
					result.addInt("ADD_CNT", rs.getInt("ADD_CNT"));
				}
				if(rs.getString("SCOR_APPL_YN") == null){
                	result.addString("SCOR_APPL_YN","0");
                }else{
                	result.addString("SCOR_APPL_YN",rs.getString("SCOR_APPL_YN"));
                }
				existsData = true;

			}
			
			if(result.size() < 0){
				result.addString("RESULT", "01");
			}
			
		}catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		return result;
	}
	 
	/*
	 * 라운딩 뎃글 Proc
	 */
	public DbTaoResult comment_execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException 
	{
		String title = dataSet.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		int idx = 0;
				
		try
		{
			long board_no = dataSet.getLong("board_no");
			
			
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getCommentListSelect().toString());
				pstmt.setLong(++idx, board_no);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				result.addLong("board_no", rs.getLong("BOARD_NO"));
				result.addLong("seq_no", rs.getLong("SEQ_NO"));
				result.addString("add_cont", rs.getString("ADD_CONT"));
				result.addString("reg_date", rs.getString("REG_DATE"));
				result.addString("reg_nm", rs.getString("REG_NM"));
				result.addString("reg_no", rs.getString("REG_NO"));
			}
			
			if(result.size() < 0){
				result.addString("RESULT", "01");
			}
			
		}catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		
		return result;
	}
	
	/*
	 *  라운딩 게시물 내역 쿼리
	 */
	private StringBuffer getRoundingListSelect(TaoDataSet data) throws Exception
	{
		String add_yn = data.getString("add_yn");					// 댓글의 개수
		String search_type = data.getString("search_type");			// 검색구분
		String board_cd = data.getString("board_cd"); 				// 게시물코드
		String search_word = data.getString("search_word");
				
		StringBuffer sb = new StringBuffer();		
		sb.append("	SELECT *	\n");
		sb.append("	FROM(		\n");
		sb.append("		SELECT X.*, Y.RECORD_CNT, ROWNUM ROW_NUM	\n");
		if(!board_cd.equals("12")){
			sb.append("			,(SELECT COUNT(BOARD_NO) FROM BCDBA.TBGFBOARD WHERE REF_NO = X.REFNO) AN_CNT	\n");
		}
		sb.append("		FROM(	\n");
		sb.append("			SELECT 	\n");
		sb.append("				A.BOARD_NO, A.BOARD_SUBJ, A.BOARD_TEXT, A.HOT_INFO_YN, A.READ_CNT, SCOR_APPL_YN, A.LIST_INQ_CLSS, A.LIST_IMG_PATH,	\n");
		sb.append("				A.SORT_KEY,  A.REF_NO, A.ANS_STG, A.ANS_LEV, TO_CHAR(TO_DATE(A.REG_DATE, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD') REG_DATE,	\n");
		sb.append("				TO_CHAR(TO_DATE(A.REG_DATE, 'YYYYMMDDHH24MISS'),'YYYYMMDD') REG_DATE2, TO_CHAR(SYSDATE, 'YYYYMMDD') TODAY, 	\n");
		sb.append("				A.REG_NM, A.REG_IP, ATC_FILE_YN,	\n");
		sb.append("				CASE WHEN TO_CHAR(SYSDATE,'YYYYMMDD') >= TO_CHAR(TO_DATE(REG_DATE,'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')	\n");
		sb.append("					AND TO_CHAR(SYSDATE,'YYYYMMDD') <= TO_CHAR(TO_DATE(REG_DATE,'YYYY-MM-DD HH24:MI:SS'),'YYYYMMDD')+4 	\n");
		sb.append("					THEN '<img src=\"/golf//img/booking/icon_new.gif\">' ELSE '' END AS NEWYN,		\n");
		
		if(add_yn.equals("Y")){
			sb.append("				NVL((SELECT COUNT(C.BOARD_NO) FROM BCDBA.TBGFBRDADD C WHERE C.BOARD_NO=A.BOARD_NO), 0) ADD_CNT,	\n");
		}
		
		sb.append("					NVL(UC.ACCOUNT, ' ') REG_ACCOUNT, NVL(A.SCOR_APPL_YN, '0') SCOR_APPL, A.REF_NO REFNO	\n");
		sb.append("			FROM BCDBA.TBGFBOARD A, BCDBA.UCUSRINFO UC	\n");
		sb.append("			WHERE A.BOARD_CD = ?	\n");
		sb.append("				AND UC.MEMID(+) = A.REG_NO \n");
		if(!board_cd.equals("12")){
			sb.append("			AND A.ANS_LEV = 0		\n");
		}
		if(!search_word.equals("")){
			if(search_type.equals("BOARD_SUBJ")){
				sb.append("		AND A.BOARD_SUBJ LIKE ?	\n");
			}else if(search_type.equals("BOARD_TEXT")){
				sb.append("		AND A.BOARD_TEXT LIKE ?	\n");
			} 	
		}
		
		sb.append("			ORDER BY A.REF_NO DESC, A.ANS_STG ASC ) X,	\n");
	
		sb.append("			(SELECT COUNT(*) RECORD_CNT FROM BCDBA.TBGFBOARD		\n");
		sb.append("			WHERE	1=1 AND BOARD_CD = ? \n");
		if(!search_word.equals("")){
			if(search_type.equals("BOARD_SUBJ")){
				sb.append("			AND BOARD_SUBJ LIKE ?	\n");
			}else if(search_type.equals("BOARD_TEXT")){
				sb.append("			AND BOARD_TEXT LIKE ?	\n");
			}
		}
		if(!board_cd.equals("12")){
			sb.append("			AND ANS_LEV = 0		\n");
		}
		sb.append("			) Y	\n");		
		sb.append("		WHERE ROWNUM <= ? )			\n");
		sb.append("	WHERE ROW_NUM BETWEEN ? AND ?				\n"); 
		//sb.append("	WHERE ROW_NUM >= ? 			\n"); 
		//sb.append("		AND ROW_NUM <= ?			\n");
		
		 		
		return sb;
	}
	
	/*
	 *  댓글 내역 쿼리
	 */
	public StringBuffer getCommentListSelect() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("	SELECT	\n");
		sb.append("		BOARD_NO, SEQ_NO, ADD_CONT, TO_CHAR(TO_DATE(REG_DATE, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD  HH24:MI') REG_DATE,	\n");
		sb.append("		REG_NM, REG_NO	\n");
		sb.append("	FROM BCDBA.TBGFBRDADD	\n");
		sb.append("	WHERE BOARD_NO = ?	\n");
		sb.append("	ORDER BY REG_DATE DESC	\n");
		
		return sb;
	}

}
