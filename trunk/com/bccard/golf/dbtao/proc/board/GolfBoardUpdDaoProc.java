/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfBoardUpdDaoProc
*   작성자     : (주)미디어포스 권영만
*   내용        : 게시판 처리
*   적용범위  : Golf
*   작성일자  : 2009-04-01
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.board;
 
import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfAdminEtt;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-04-01
 **************************************************************************** */
public class GolfBoardUpdDaoProc extends AbstractProc {

	public static final String TITLE = "게시판  처리";
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
		GolfAdminEtt userEtt = null;
		String admin_no = "";
		
		//debug("==== GolfBoardUpdDaoProc Start :"+TITLE+" ===");
		
		try{
			con = context.getDbConnection("default", null);
			//1.세션정보체크
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			if(userEtt != null && !"".equals(userEtt.getMemId())){				
				admin_no		= (String)userEtt.getMemNo(); 							
			}
			
			//조회 조건
						
			String cate_seq_no 			= "";
			
		
			
			String subject				= dataSet.getString("subject"); 
			String content				= dataSet.getString("content"); 
			String eps_yn				= dataSet.getString("eps_yn"); 
			String board_writer_nm		= dataSet.getString("board_writer_nm"); 
			String board_input_id		= dataSet.getString("board_input_id"); 
			String ip_addr				= dataSet.getString("ip_addr"); 
			
			String boardid				= dataSet.getString("boardid");			// 게시판변호
			String p_idx				= dataSet.getString("p_idx");			// 글 일련변호 
			cate_seq_no 				= dataSet.getString("cate_seq_no");		// 카테고리 번호
			String mode					= dataSet.getString("mode"); 			// 처리구분
			
			int res = 0;
									

			//등록시
			if("ins".equals(mode))
			{
				long maxValue = this.selectArticleNo(context);
				con.setAutoCommit(false);
				//debug("==== GolfBoardUpdDaoProc maxValue : "+maxValue+" ===");	
				String sql = this.getSelectQuery("");						
				
				pstmt = con.prepareStatement(sql);
				
				int pidx = 0;
				
				pstmt.setLong(++pidx, maxValue);
				pstmt.setString(++pidx, boardid);
				pstmt.setString(++pidx, subject);
				pstmt.setString(++pidx, board_input_id);
				pstmt.setString(++pidx, board_writer_nm);
				pstmt.setString(++pidx, ip_addr);
				pstmt.setString(++pidx, eps_yn);
				pstmt.setString(++pidx, admin_no);
				pstmt.setString(++pidx, cate_seq_no);
				pstmt.setString(++pidx, "N");
				res = pstmt.executeUpdate();
				
				if (pstmt != null) pstmt.close();

				// CLOB 처리
				if (content.length() > 0) {
				
					String query2 = "select CTNT from TBBBRD where SEQ_NO = ? FOR UPDATE";
					pstmt = con.prepareStatement(query2);
					pstmt.setLong(1, maxValue);
					ResultSet rs = pstmt.executeQuery();
					if (rs.next()) {
					
						
						oracle.sql.CLOB clob = (oracle.sql.CLOB) rs.getClob(1);
						BufferedWriter writer = new BufferedWriter(clob.getCharacterOutputStream());
					    writer.write(content);
					    writer.close();
			       
						
						
					    /* 실서버에 쓰던 방식 weblogic.jar 이용
						Clob clob = rs.getClob("CONTENT");
						
						Writer writer = ((weblogic.jdbc.common.OracleClob)clob).getCharacterOutputStream();
						
						Reader src = new CharArrayReader(content.toCharArray());
						
						char[] buffer = new char[1024];
						int read = 0;
						while ((read = src.read(buffer, 0, 1024)) != -1) {
							writer.write(buffer, 0, read);
						}
						src.close();
						writer.close();
						*/ 
					}
					if (rs  != null) rs.close();
					if (pstmt != null) pstmt.close();
				}
				con.commit();
				con.setAutoCommit(true);
				
			}
			else if("upd".equals(mode))
			{
				String sql = this.getSelectUpdQuery(cate_seq_no);
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, subject);
				pstmt.setString(++pidx, content);
				pstmt.setString(++pidx, eps_yn);
				pstmt.setString(++pidx, admin_no);
				if(!"".equals(cate_seq_no)) pstmt.setString(++pidx, cate_seq_no);
				pstmt.setString(++pidx, boardid);
				pstmt.setString(++pidx, p_idx);
								
				res = pstmt.executeUpdate();
				
				
			}
			else if("del".equals(mode))
			{
				String sql = this.getSelectDelQuery("");
				
				pstmt = con.prepareStatement(sql);
				int pidx = 0;
				
				pstmt.setString(++pidx, boardid);
				pstmt.setString(++pidx, p_idx);

				res = pstmt.executeUpdate();
				
				
			}
			
			
			result = new DbTaoResult(TITLE);

			if ( res == 1 ) {
				result.addString("RESULT", "00");
			}else{
				result.addString("RESULT", "01");
			}
			
			
			//debug("==== GolfBoardUpdDaoProc end ===");	
		}catch ( Exception e ) {
			//debug("==== GolfBoardUpdDaoProc ERROR ===");
			e.printStackTrace();
			//debug("==== GolfBoardUpdDaoProc ERROR ===");
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
	private String getSelectQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	INSERT INTO TBGBBRD	(									");
		sql.append("\n		BBRD_SEQ_NO				,									");
		sql.append("\n		BBRD_CLSS				,									");
		sql.append("\n		TITL				,									");
		sql.append("\n		CTNT				,									");
		sql.append("\n		ID				,									");		
		sql.append("\n		HG_NM				,									");
		sql.append("\n		REG_MGR_ID			,									");
		sql.append("\n		REG_ATON			,									");
		sql.append("\n		REG_IP_ADDR				,									");
		sql.append("\n		READ_CNT			,									");		
		sql.append("\n		EPS_YN				,									");
		sql.append("\n 		ADMIN_NO            ,									");
		sql.append("\n		CATE_SEQ_NO			,									");
		sql.append("\n		DEL_YN		)											");

		sql.append("\n	VALUES( ?,?,?,empty_clob(),								");
		sql.append("\n	?,?, TO_CHAR(SYSDATE, 'yyyymmddhh24miss')	,			");
		sql.append("\n	?,0,?,?,?,?			)									");

		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectUpdQuery(String cate_seq_no) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	UPDATE TBBBRD	SET										");
		sql.append("\n	SUBJECT = ?			,									");
		sql.append("\n	CONTENT = ?			,									");
		sql.append("\n	EPS_YN = ? 			,									");
		sql.append("\n	UP_ADMIN_NO = ? 	,									");
		sql.append("\n	EDIT_DTIME =  TO_CHAR(SYSDATE, 'yyyymmddhh24miss')	,	");
		if(!"".equals(cate_seq_no)) sql.append("\n	CATE_SEQ_NO	= ?					");
		sql.append("\n	WHERE BOARDID = ? AND SEQ_NO = ?						");
 
		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectDelQuery(String file_yn) throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n	DELETE FROM TBBBRD					");
		sql.append("\n	WHERE BOARDID = ? AND SEQ_NO = ? ");

		return sql.toString();
	}
	/**
	 * 게시판 글번호 가져오기
	 */
	private long selectArticleNo(WaContext context) throws BaseException {

		Connection con = null;
        PreparedStatement pstmt1 = null;        
        ResultSet rset1 = null;        
        String sql = "select TBBBRD_SEQ_NO.nextval from dual";
        long pidx = 0;
        try {
        	con = context.getDbConnection("default", null);
            pstmt1 = con.prepareStatement(sql);
            rset1 = pstmt1.executeQuery();   
			if (rset1.next()) {				
                pidx = rset1.getLong(1);
			}
        } catch (Throwable t) {          // SQLException 시 예외 처리 : 쿼리에 문제가 있을때 발생
        	BaseException exception = new BaseException(t);          
            throw exception;
        } finally {
        	try { if(rset1 != null) {rset1.close();} else{} } catch (Exception ignored) {}
        	try { if(pstmt1 != null) {pstmt1.close();} else{} } catch (Exception ignored) {}
        	try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
        }
        return pidx;

	}

	
}
