/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointMainRepInqDaoProc
*   작성자    : (주)미디어포스 진현구
*   내용      : 답글 조회
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
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.golf.common.ResultException;

/** ****************************************************************************
 * golf 
 * @author
 * @version 2009-03-20
 **************************************************************************** */

public class GolfMainRepInqDaoProc extends AbstractProc{

	public static final String TITLE = "Cyber 답글 조회";

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

		//debug("==== GolfPointMainRepInqDaoProc start ===");
		try{
			//조회 조건 Validation
			String comm_seqno	= dataSet.getString("comm_seqno"); 		//검색여부

			result = new DbTaoResult(TITLE);
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
				
				result.addLong("row_seq",		rset.getLong("SEQ"));
				result.addLong("row_rep_seq",			rset.getLong("REP_SEQ"));
				result.addString("rep_cont",			rset.getString("REP_CONT"));
				result.addString("rep_input_date",	rset.getString("REP_INPUT_DATE"));

				existsData = true;
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
		}catch ( Exception e ) {
			//debug("==== GolfPointMainlnqDaoProc ERROR Start ===");
			e.printStackTrace();
			//debug("==== GolfPointMainlnqDaoProc ERROR End ===");
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

		sql.append("\n SELECT	SEQ,		");
		sql.append("\n			TITLE,		");
		sql.append("\n			REG_DATE	");
		sql.append("\n FROM		BBS			");
		sql.append("\n WHERE	SEQ = ? ");

		return sql.toString();
	}

}
