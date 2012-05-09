/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointMainDetailInqDaoProc
*   작성자    : (주)미디어포스 진현구
*   내용      : 글 상세보기
*   적용범위  : Golf
*   작성일자  : 2009-03-19
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.bbs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.core.WaContext;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.msg.MsgEtt;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;

/** ****************************************************************************
 * Community 상세 조회 수행 클래스.
 * @author
 * @version 2008-12-07
 **************************************************************************** */
public class GolfMainDetailInqDaoProc extends AbstractProc{

	public static final String TITLE = "Main 상세 조회";
	private String temporary;

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

		try{
			//debug("==== GolfPointMainDetailInqDaoProc Start ===");
			//조회 조건 Validation
			String comm_seqno	= dataSet.getString("comm_seqno"); 		//검색여부 
			String comm_clss	= dataSet.getString("comm_clss");
			String update_yn	= dataSet.getString("update_yn");
			String comm_cont	= "";
			result = new DbTaoResult(TITLE);
			String curDateFormated = "";

			String sql = this.getSelectQuery();

			con = context.getDbConnection("default", null);
			pstmt = con.prepareStatement(sql);
			int pidx = 0;

			pstmt.setString(++pidx, comm_seqno);
			rset = pstmt.executeQuery();

			result = new DbTaoResult(TITLE);
			boolean existsData = false;

			while(rset.next()){
				if(!existsData){
					result.addString("RESULT", "00");
				}

				curDateFormated = DateUtil.format(rset.getString("REG_DATE"),"yyyyMMdd","yyyy/MM/dd");

				result.addLong("row_seq", rset.getLong("SEQ"));
				result.addString("row_title", rset.getString("TITLE"));
				result.addString("row_date", curDateFormated);
			//	result.addString("content",			GolfUtil.getUrl(rset.getString("COMM_CONT")));
				if(update_yn.equals("Y")){
					this.strInput(rset.getString("content"));
					this.br2nlup();
					comm_cont = this.strOutput();
				}else {
					this.strInput(rset.getString("content"));
					this.br2nl();
					comm_cont = this.strOutput();
				}
				result.addString("row_content",	comm_cont);
				
				existsData = true;
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			
			//debug("==== GolfPointMainDetailInqDaoProc End ===");
		}catch ( Exception e ) {
			//debug("==== GolfPointMainDetailInqDaoProc ERROR Start ===");
			e.printStackTrace();
			//debug("==== GolfPointMainDetailInqDaoProc ERROR End ===");
            //MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR,TITLE,"UNPREDICTABLE_TR_EXCEPTION", null);
            throw new DbTaoException(msgEtt,e);
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
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();

		sql.append("\n 	SELECT	 SEQ, TITLE, REG_DATE, CONTENT	");
		sql.append("\n 	FROM BBS			");
		sql.append("\n 	WHERE SEQ=?			");

		return sql.toString();
	}

/** ***********************************************************************
	* 문자열 parsing 
	************************************************************************ */
	private void parseStr(String orig_word, String final_word) {
		for(int index = 0; (index = temporary.indexOf(orig_word, index)) >= 0; index += final_word.length() )
			temporary = temporary.substring(0, index) + final_word + temporary.substring(index + orig_word.length());
	}

	/** ***********************************************************************
	* 문자 변환
	************************************************************************ */
	public void br2nl() {
		this.parseStr("<br>", "\n");
		this.parseStr("&#39;", "'");
		this.parseStr("&#34;", "\"");
		this.parseStr("&#60;&#37;", "<%");
		this.parseStr("&#37;&#62;", "%>");
		this.parseStr("&#60;", "<");
		this.parseStr("&#62;", ">");
	}

	/** ***********************************************************************
	* 문자 변환  (글 수정시)
	************************************************************************ */
	public void br2nlup() {
		this.parseStr("&#39;", "'");
		this.parseStr("&#34;", "\"");
		this.parseStr("&#60;&#37;", "<%");
		this.parseStr("&#37;&#62;", "%>");
		this.parseStr("&#60;", "<");
		this.parseStr("&#62;", ">");
		this.parseStr("<br>", "\n");
	}

	/** ***********************************************************************
	* 문자열 Setting
	************************************************************************ */
	public void strInput(String source) {
		temporary = source;
	}

	/** ***********************************************************************
	* 문자열 Getting
	************************************************************************ */
	public String strOutput() {
		return temporary;
	}

}
