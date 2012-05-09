/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfRoundAfterInsProc
*   작성자    : shin cheong gwi
*   내용      : 라운딩후기
*   적용범위  : golfloung
*   작성일자  : 2010-11-10
************************** 수정이력 ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.premium;

import java.io.CharArrayReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfRoundAfterInsProc extends AbstractObject {
	private static GolfRoundAfterInsProc instance = null;
	static{
		synchronized(GolfRoundAfterInsProc.class){
			if(instance == null){
				instance = new GolfRoundAfterInsProc();
			}
		}
	}
	public static GolfRoundAfterInsProc getInstance(){
		return instance;
	}
	 
	/*
	 *  내용 저장 Proc
	 */
	public boolean execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException, SQLException 
	{
		boolean rtn_flag = false;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		Writer writer = null;
		Reader reader = null;
		int idx = 0;
		
		try 
		{
			String board_cd = dataSet.getString("board_cd");
			String board_subj = dataSet.getString("board_subj");
			String board_text = dataSet.getString("board_text");
			String board_data = dataSet.getString("board_data");
			String scor_appl_yn = dataSet.getString("scor_appl_yn");
			String board_html_yn = dataSet.getString("board_html_yn");
			//String replyyn = dataSet.getString("replyyn");
			String reg_nm = dataSet.getString("reg_nm");
			int reg_no = dataSet.getInt("reg_no");
			long ans_lev = dataSet.getLong("ans_lev");
			//long ref_no = dataSet.getLong("ref_no");
			long ans_stg = dataSet.getLong("ans_stg");
			int board_no = dataSet.getInt("board_no");
			String reg_ip = request.getRemoteAddr();	
			board_subj = board_subj.replaceAll("'", " ");
			board_text = board_text.replaceAll("'", " ");
			
			int seq = 0;
		      
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getRoundingSeqQuery().toString());
			rs = pstmt.executeQuery();
			if(rs.next()){
				seq = rs.getInt(1);
			}
			pstmt.close();
			rs.close();
			
			if(board_no == 0){
				board_no = seq;
			}
			
			conn.setAutoCommit(false);			 
			pstmt = conn.prepareStatement(this.setRoundingInsQuery(dataSet).toString());
				pstmt.setInt(++idx, board_no);				// 게시물번호
				//pstmt.setString(++idx, board_cd);			// 게시물코드
				//pstmt.setLong(++idx, ans_lev);			// 답글레벨수
				pstmt.setString(++idx, board_subj);			// 제목			
				pstmt.setString(++idx, board_text);			// 내용
				pstmt.setString(++idx, scor_appl_yn);		// 평점
				pstmt.setInt(++idx, board_no);				// 시퀀스
				pstmt.setString(++idx, board_cd);			// 게시물코드					
				pstmt.setString(++idx, board_subj);			// 제목
				pstmt.setString(++idx, board_text);			// 내용				
				pstmt.setString(++idx, board_html_yn);		// html적용여부
				pstmt.setInt(++idx, board_no);				// 참조번호
				pstmt.setLong(++idx, ans_stg);				// 댓글단계수
				pstmt.setLong(++idx, ans_lev);				// 댓글레벨수
				pstmt.setString(++idx, reg_nm);				// 등록자
				pstmt.setString(++idx, reg_ip);				// IP
				pstmt.setInt(++idx, reg_no);				// 사번
				pstmt.setString(++idx, scor_appl_yn);
			pstmt.execute();
			pstmt.close();
			//conn.commit();
			
			// Q&A 일경우
			if(!board_cd.equals("12")){
			
				pstmt = conn.prepareStatement(this.setboardDataQuery().toString());
					pstmt.setLong(1, board_no);
				rs = pstmt.executeQuery();
				if(rs.next()){
					Clob cb = rs.getClob("BOARD_DATA");
//					if(cb != null){
//						writer = ((weblogic.jdbc.common.OracleClob)cb).getCharacterOutputStream();
//						reader = new CharArrayReader(board_data.toCharArray());
//						char[] buffer = new char[1024];
//						int read = 0;
//						while((read = reader.read(buffer, 0, 1024)) != -1){
//							writer.write(buffer, 0, read);
//						}
//						writer.flush();
//						writer.close();
//					}
				}	
			}
			
			conn.commit();
			rtn_flag = true;
			
		}catch (Throwable t) {
			conn.rollback();
			throw new BaseException(t);
		} finally {	
			conn.setAutoCommit(true);
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		return rtn_flag;
	}
	
	/*
	 *  게시글 삭제
	 */
	public boolean del_execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException, SQLException 
	{
		boolean rtn_flag = false;
		Connection conn = null;		
		PreparedStatement pstmt = null;
				
		try
		{
			long board_no = dataSet.getLong("board_no");
			
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);	
			
			// 게시물 삭제
			pstmt = conn.prepareStatement(this.setRoundingAfterDelQuery().toString());
				pstmt.setLong(1, board_no);
				pstmt.setLong(2, board_no);
			pstmt.execute();
			pstmt.close();			
			
			// 뎃글 삭제
			pstmt = conn.prepareStatement(this.setSeqDelQuery(0L).toString());
				pstmt.setLong(1, board_no);
			pstmt.execute();
			
			conn.commit();			
			
		}catch (Throwable t) {
			conn.rollback();
			throw new BaseException(t);
		} finally {	
			conn.setAutoCommit(true);
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		return rtn_flag; 
	}
	/*
	 * 뎃글 Proc
	 */
	public boolean add_execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException, SQLException 
	{
		boolean rtn_flag = false;
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int idx = 0;
		
		try
		{
			long board_no = dataSet.getLong("board_no");
			int seq = 0;
			String add_cont = dataSet.getString("add_cont");
			String reg_nm = dataSet.getString("reg_nm");
			int reg_no = dataSet.getInt("reg_no");
			add_cont = add_cont.replaceAll("'", " ");
			
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getSeqQuery().toString());
				pstmt.setLong(1, board_no);
			rs = pstmt.executeQuery();
			if(rs.next()){
				seq = rs.getInt(1);
			}
			pstmt.close();
			rs.close();
			
			conn.setAutoCommit(false);			
			pstmt = conn.prepareStatement(this.setAddInsQuery().toString());
				pstmt.setLong(++idx, board_no);
				pstmt.setInt(++idx, seq);
				pstmt.setString(++idx, add_cont);
				pstmt.setLong(++idx, board_no);
				pstmt.setInt(++idx, seq);
				pstmt.setString(++idx, add_cont);
				pstmt.setString(++idx, reg_nm);
				pstmt.setInt(++idx, reg_no);
			pstmt.execute();
			
			conn.commit();
			rtn_flag = true;
			
		}catch (Throwable t) {
			conn.rollback();
			throw new BaseException(t);
		} finally {	
			conn.setAutoCommit(true);
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		return rtn_flag;
	}
	
	/*
	 * 뎃글 삭제
	 */
	public boolean comment_del_execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException, SQLException 
	{
		boolean rtn_flag = false;
		Connection conn = null;		
		PreparedStatement pstmt = null;
		int idx = 0;
		
		try
		{
			long board_no = dataSet.getLong("board_no");
			long seq_no = dataSet.getLong("seq_no");
			
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(this.setSeqDelQuery(seq_no).toString());
				pstmt.setLong(++idx, board_no);
				if(seq_no > 0L){
					pstmt.setLong(++idx, seq_no);
				}
			pstmt.execute();
			conn.commit();
			rtn_flag = true;
			
		}catch (Throwable t) {
			conn.rollback();
			throw new BaseException(t);
		} finally {	
			conn.setAutoCommit(true);
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		return rtn_flag;
	}
	
	/*
	 * 내용저장 쿼리 (입력/수정)
	 */
	private StringBuffer setRoundingInsQuery(TaoDataSet dataSet) throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("	MERGE INTO BCDBA.TBGFBOARD	\n");
		sb.append("	USING DUAL	\n");
		sb.append("	ON (	\n");
		sb.append("		BOARD_NO = ?	\n");
		//sb.append("		--AND BOARD_CD = ?	\n");
		//sb.append("		--AND ANS_LEV = ? 		\n");
		sb.append("		)	\n");
		sb.append("	WHEN MATCHED THEN	\n");
		sb.append("		UPDATE SET	\n");
		sb.append("			BOARD_SUBJ = ?,	\n");
		sb.append("			BOARD_TEXT = ?,	\n");
		sb.append("			BOARD_DATA = EMPTY_CLOB(),	");
		sb.append("			SCOR_APPL_YN = ? 	\n");
		//sb.append("			REG_DATE = TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS')	\n");
		sb.append("	WHEN NOT MATCHED THEN	\n");
		sb.append("		INSERT	\n");
		sb.append("			(BOARD_NO, BOARD_CD, BOARD_SUBJ, BOARD_TEXT, BOARD_DATA, BOARD_HTML_YN, READ_CNT, REF_NO, ANS_STG, ANS_LEV, 	\n");
		sb.append("				REG_DATE, REG_NM, REG_IP, REG_NO, HOT_INFO_YN, LIST_INQ_CLSS, SCOR_APPL_YN ) 	\n");
		sb.append("		VALUES		\n");
		sb.append("			( ?, ?, ?, ?, EMPTY_CLOB(), ?, 0, ?, ?, ?, 	\n");
		sb.append("				(SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') FROM DUAL), ?, ?, ?, 0, 1, ? )		\n");
				
		return sb;
	}
	
	/*
	 *  게시물 시퀀스코드
	 */
	private StringBuffer getRoundingSeqQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();		
		sb.append(" SELECT NVL(MAX(BOARD_NO), 0) + 1 FROM BCDBA.TBGFBOARD  \n");
		
		return sb;
	}
	
	/*
	 * 댓글저장 (입력/수정)
	 */
	private StringBuffer setAddInsQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("	MERGE INTO BCDBA.TBGFBRDADD  \n");
		sb.append("	USING DUAL	\n");
		sb.append("	ON (	\n");
		sb.append("		BOARD_NO = ?	\n");
		sb.append("		AND SEQ_NO = ?		\n");
		sb.append("		)	\n");
		sb.append("	WHEN MATCHED THEN	\n");
		sb.append("		UPDATE SET	\n");
		sb.append("			ADD_CONT = ?,	\n");
		sb.append("			REG_DATE = (SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') FROM DUAL)	\n");
		sb.append("	WHEN NOT MATCHED THEN	\n");
		sb.append("		INSERT	\n");
		sb.append("			(BOARD_NO, SEQ_NO, ADD_CONT, REG_DATE, REG_NM, REG_NO )	\n");
		sb.append("		VALUES (?, ?, ?, (SELECT TO_CHAR(SYSDATE,'YYYYMMDDHH24MISS') FROM DUAL), ?, ?	)	\n");
				
		return sb;
	}
	
	/*
	 * 뎃글 시퀀스 코드
	 */
	private StringBuffer getSeqQuery() throws Exception
	{		
		StringBuffer sb = new StringBuffer();		
		sb.append(" SELECT NVL(MAX(SEQ_NO), 0) + 1 FROM BCDBA.TBGFBRDADD WHERE BOARD_NO = ?  \n");
		
		return sb;
	}
	
	/*
	 * 게시물 삭제
	 */
	private StringBuffer setRoundingAfterDelQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		sb.append("	DELETE FROM BCDBA.TBGFBOARD	WHERE BOARD_NO = ? OR REF_NO = ? \n");
		
		return sb;
	}
	/*
	 * 뎃글삭제
	 */
	private StringBuffer setSeqDelQuery(long seq_no) throws Exception
	{
		StringBuffer sb = new StringBuffer();		
		sb.append("	DELETE FROM BCDBA.TBGFBRDADD WHERE BOARD_NO = ? 	\n");
		if(seq_no > 0L){
			sb.append("		AND SEQ_NO = ? \n");
		}
		
		return sb;
	}
	/*
	 * 
	 */
	private StringBuffer setboardDataQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		sb.append("	SELECT BOARD_DATA FROM BCDBA.TBGFBOARD	\n");
		sb.append("	WHERE BOARD_NO = ? FOR UPDATE	\n");
		
		return sb;
	}
}
