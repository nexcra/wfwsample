/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfEvntSpWinnerDetailDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 이벤트라운지 > 특별한레슨이벤트 >당첨확인 상세보기
*   적용범위	: golf
*   작성일자	: 2009-07-09
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.event.special.winner;

import java.io.Reader;
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
public class GolfEvntSpWinnerDetailDaoProc extends AbstractProc {
	public static final String TITLE = "이벤트라운지 > 특별한레슨이벤트 >당첨확인 상세보기";
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
		String ctnt = "";

 
		try {
			conn = context.getDbConnection("default", null);
   
			String evnt_clss 	= data.getString("evnt_clss");
			String p_idx 		= data.getString("p_idx");
			
			int pidx = 0;
			int rset = 0;
			boolean eof = false;
			sql = this.getSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++pidx, p_idx);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				
				//본문 내용 CLOB처리
				Reader reader = null;
				StringBuffer bufferSt = new StringBuffer();
				reader = rs.getCharacterStream("CTNT");
				if( reader != null )  {
					char[] buffer = new char[1024]; 
					int byteRead; 
					while((byteRead=reader.read(buffer,0,1024))!=-1)  
						bufferSt.append(buffer,0,byteRead);  
					reader.close();
				}
				ctnt = bufferSt.toString();
				ctnt = ctnt.replaceAll("\r\n", "<br/>");
				
				
				result.addString("seq_no", 		rs.getString("SEQ_NO"));
				result.addString("titl", 		rs.getString("TITL"));
				result.addString("inqr_num", 	rs.getString("INQR_NUM"));
				result.addString("reg_aton", 	rs.getString("REG_ATON"));
				result.addString("ctnt", 		ctnt);
				
				//조회수 +1
				pidx = 0;
				sql = this.getInqrPlusQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, p_idx);
				
				rset = pstmt.executeUpdate();
				
				if(rset > 0){
					eof = true;
				}else{
					eof = false;
				}
				
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
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 	SELECT														"); 
		sql.append("\n 		 SEQ_NO													");
		sql.append("\n 		,TITL													");
		sql.append("\n 		,CTNT													");
		sql.append("\n 		,INQR_NUM												");
		sql.append("\n 		,TO_CHAR(TO_DATE(T1.REG_ATON,'yyyy-MM-dd hh24miss'),'YYYY.MM.DD')AS REG_ATON  ");
		sql.append("\n FROM BCDBA.TBGEVNTPRZPEMGMT T1								");
		sql.append("\n WHERE  T1.BLTN_YN = 'Y'	AND T1.SEQ_NO = ?					");
		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getInqrPlusQuery() throws Exception{ 

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n 	UPDATE	BCDBA.TBGEVNTPRZPEMGMT SET							"); 
		sql.append("\n 		INQR_NUM = INQR_NUM +1									");
		sql.append("\n WHERE  SEQ_NO = ?											");

		return sql.toString();
	}


}
 