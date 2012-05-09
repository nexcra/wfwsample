/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfRoundAfterViewProc
*   �ۼ���    : shin cheong gwi
*   ����      : �����ı� �󼼺���
*   �������  : golfloung
*   �ۼ�����  : 2010-11-11
************************** �����̷� ****************************************************************
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.booking.premium;

import java.io.Reader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;

//import com.bccard.fortify.FilterUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfRoundAfterViewProc extends AbstractObject {

	private static GolfRoundAfterViewProc instance = null;
	static{
		synchronized(GolfRoundAfterViewProc.class){
			if(instance == null){
				instance = new GolfRoundAfterViewProc();
			}
		}
	}
	public static GolfRoundAfterViewProc getInstance(){
		return instance;
	}
	
	/*
	 * ���� �󼼺��� Proc ����
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException, SQLException 
	{
		String title = dataSet.getString("TITLE");
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		DbTaoResult result = new DbTaoResult(title);
		int idx = 0;
		
		try
		{
			long board_no = dataSet.getLong("board_no");
			String board_cd = dataSet.getString("board_cd");
			
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getRoundingViewQuery().toString());
				pstmt.setLong(++idx, board_no);
				pstmt.setString(++idx, board_cd);
			rs = pstmt.executeQuery();
			
			if(rs.next()){				
				result.addLong("board_no", rs.getLong("BOARD_NO"));
				result.addString("board_cd", rs.getString("BOARD_CD"));
				result.addString("board_subj", rs.getString("BOARD_SUBJ"));
				result.addString("reg_nm", rs.getString("REG_NM"));
				result.addString("reg_no", rs.getString("REG_NO"));
				result.addString("reg_date", rs.getString("REG_DATE"));
				result.addString("scor_appl_yn", rs.getString("SCOR_APPL_YN"));
				
				if(board_cd.equals("12")){
					//result.addString("board_text", FilterUtil.getXSSFilter(rs.getString("BOARD_TEXT")));
					result.addString("board_text2", rs.getString("BOARD_TEXT"));
				}else{
					// CLOB column�� ���� ��Ʈ���� ��´�.
					StringBuffer output = new StringBuffer();
					Reader reader = rs.getCharacterStream("BOARD_DATA");					
					char[] buffer = new char[1024];
					int byteRead;
					if(reader != null){
						while((byteRead=reader.read(buffer,0,1024))!=-1){
							output.append(buffer,0,byteRead);					// ��Ʈ�����κ��� �о ��Ʈ�� ���ۿ� �ִ´�.
						}
						reader.close();
						//result.addString("board_data", FilterUtil.getXSSFilter(output.toString())); 
						result.addString("board_data2", output.toString()); 
					}else{
						//result.addString("board_data", FilterUtil.getXSSFilter(rs.getString("BOARD_TEXT"))); 
						result.addString("board_data2", rs.getString("BOARD_TEXT")); 
					}
				}
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
	 * ���� �󼼺��� �� ī��Ʈ ����	 * 
	 */
	public void readCnt_execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException, SQLException 
	{		
		Connection conn = null;		
		PreparedStatement pstmt = null;
		
		try
		{
			long board_no = dataSet.getLong("board_no");
			
			conn = context.getDbConnection("default", null);
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(this.setRoundingReadCntQuery().toString());
				pstmt.setLong(1, board_no);
			pstmt.executeUpdate();
			conn.commit();
		}catch (Throwable t) {	
			conn.rollback();
			throw new BaseException(t);
		} finally {		
			conn.setAutoCommit(true);
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
	}
	
	/*
	 * ���� �ı� ������/ ������
	 */
	public DbTaoResult nextPrev_execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException, SQLException 
	{
		String title = dataSet.getString("TITLE");
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		DbTaoResult result = new DbTaoResult(title);
		int idx = 0;
		
		try
		{
			long board_no = dataSet.getLong("board_no");
			String board_cd = dataSet.getString("board_cd");
						
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getRundingNextPrevQuery().toString());
				pstmt.setLong(++idx, board_no);
				pstmt.setString(++idx, board_cd);
				pstmt.setLong(++idx, board_no);
				pstmt.setString(++idx, board_cd);
			rs = pstmt.executeQuery();
			while(rs.next()){
				result.addLong("board_no", rs.getLong("BOARD_NO"));
				result.addString("board_subj", rs.getString("BOARD_SUBJ"));
				result.addString("gubun", rs.getString("GUBUN"));
				result.addString("reg_date", rs.getString("REG_DATE"));
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
	 * Q&A �亯 ���� Proc 
	 */
	public DbTaoResult qnaAnswer_execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException, SQLException 
	{
		String title = dataSet.getString("TITLE");
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		DbTaoResult result = new DbTaoResult(title);
		int idx = 0;
		
		try
		{
			long board_no = dataSet.getLong("board_no");
			String board_cd = dataSet.getString("board_cd");
			
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getQnaAnswerQuery().toString());
				pstmt.setLong(++idx, board_no);
			rs = pstmt.executeQuery();
			
			if(rs.next()){
				result.addLong("board_no", rs.getLong("BOARD_NO"));	
				result.addLong("board_cd", rs.getLong("BOARD_CD"));	
				result.addString("board_subj", rs.getString("BOARD_SUBJ"));				
				result.addString("reg_date", rs.getString("REG_DATE"));
				
				if(board_cd.equals("12")){
					//result.addString("board_text", FilterUtil.getXSSFilter(rs.getString("BOARD_TEXT")));					
				}else{
					// CLOB column�� ���� ��Ʈ���� ��´�.
					StringBuffer output = new StringBuffer();
					Reader reader = rs.getCharacterStream("BOARD_DATA");					
					char[] buffer = new char[1024];
					int byteRead;
					if(reader != null){
						while((byteRead=reader.read(buffer,0,1024))!=-1){
							output.append(buffer,0,byteRead);					// ��Ʈ�����κ��� �о ��Ʈ�� ���ۿ� �ִ´�.
						}
						reader.close();
						//result.addString("board_data", FilterUtil.getXSSFilter(output.toString())); 						 
					}else{
						//result.addString("board_data", FilterUtil.getXSSFilter(rs.getString("BOARD_TEXT"))); 						
					}
				}
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
	 *  ���� �ı� �󼼺��� ����
	 */
	private StringBuffer getRoundingViewQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("	SELECT	\n");
		sb.append("		BOARD_NO, BOARD_CD, BOARD_SUBJ, BOARD_TEXT, BOARD_DATA, REG_NM, REG_NO, SCOR_APPL_YN,  \n");
		sb.append("		TO_CHAR(TO_DATE(REG_DATE, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD') REG_DATE	\n");
		sb.append("	FROM 	\n");
		sb.append("		BCDBA.TBGFBOARD		\n");
		sb.append("	WHERE 	\n");
		sb.append("		BOARD_NO = ?	\n");
		sb.append("		AND BOARD_CD = ?	\n");
				
		return sb;
	}
	
	/*
	 * ���� �ı� �� ����� ī��Ʈ ���� ����
	 */
	private StringBuffer setRoundingReadCntQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("	UPDATE BCDBA.TBGFBOARD SET	\n");
		sb.append("		READ_CNT = READ_CNT + 1	\n");
		sb.append(" WHERE \n");
		sb.append("		BOARD_NO = ?	\n");
				
		return sb;
	}
	
	/*
	 *  ���� �ı� ������/������
	 */
	private StringBuffer getRundingNextPrevQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		
		sb.append("	SELECT	*	\n");
		sb.append("	FROM	\n");
		sb.append("		(SELECT 	\n");
		sb.append("			BOARD_NO, BOARD_SUBJ, 'NEXT' GUBUN,  	\n");
		sb.append("			TO_CHAR(TO_DATE(REG_DATE, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD') REG_DATE	\n");
		sb.append("		FROM BCDBA.TBGFBOARD	\n");
		sb.append("		WHERE	\n");
		sb.append("			BOARD_NO = (SELECT MIN(BOARD_NO) FROM BCDBA.TBGFBOARD WHERE REF_NO > ? AND BOARD_CD = ? AND ANS_LEV = 0)	\n");
		sb.append("			AND ANS_LEV = 0	\n");
		sb.append("		UNION ALL	\n");
		sb.append("		SELECT 	\n");
		sb.append("			BOARD_NO, BOARD_SUBJ, 'PREV' GUBUN, 	\n");
		sb.append("			TO_CHAR(TO_DATE(REG_DATE, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD') REG_DATE	\n");
		sb.append("		FROM BCDBA.TBGFBOARD	\n");
		sb.append("		WHERE	\n");
		sb.append("			BOARD_NO = (SELECT MAX(BOARD_NO) FROM BCDBA.TBGFBOARD WHERE REF_NO < ? AND BOARD_CD = ? AND ANS_LEV = 0) \n");
		sb.append("			AND ANS_LEV = 0	\n");
		sb.append("		)	\n");
						
		return sb;
	}
	
	/*
	 * Q&A �亯�� ����
	 * 
	 */
	private StringBuffer getQnaAnswerQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();
		sb.append("	SELECT	\n");
		sb.append("		BOARD_NO, BOARD_CD, BOARD_SUBJ, BOARD_TEXT, BOARD_DATA,	 	\n");
		sb.append("		TO_CHAR(TO_DATE(REG_DATE, 'YYYYMMDDHH24MISS'), 'YYYY.MM.DD HH24:MI:SS') REG_DATE	\n");
		sb.append("	FROM BCDBA.TBGFBOARD 	\n");
		sb.append("	WHERE	\n");
		sb.append("		REF_NO = ?	\n");
		sb.append("		AND ANS_LEV > 0		\n");
		
		return sb;
	}

}
