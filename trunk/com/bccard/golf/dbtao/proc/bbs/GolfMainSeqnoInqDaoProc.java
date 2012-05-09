/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : mainSeqnoInqDaoProc
*   작성자    : (주)미디어포스 진현구
*   내용      : 게시판 이전/이후글 조회
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

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.ResultException;

/** ****************************************************************************
 * golf 
 * @author
 * @version 2009-03-20
 **************************************************************************** */

public class GolfMainSeqnoInqDaoProc extends AbstractProc{

	public static final String TITLE = "게시판 이전/이후글 조회 ";

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
			//debug("==== GolfPointMainSeqnoInqDaoProc Start ===");
			//조회 조건 Validation
			String comm_seqno	= dataSet.getString("comm_seqno"); 		//검색여부
			String comm_clss	= dataSet.getString("comm_clss"); 				//검색여부

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

				result.addLong("row_seq",	rset.getLong("SEQ"));
				result.addLong("row_pre_seq",		rset.getLong("PRE_SEQ"));
				result.addLong("row_next_seq",		rset.getLong("NEXT_SEQ"));
				result.addString("row_pre_title",	 		GolfUtil.getUrl(rset.getString("PRE_TITLE")));
				result.addString("row_next_title",		GolfUtil.getUrl(rset.getString("NEXT_TITLE")));

				existsData = true;
			}

			if(!existsData){
				result.addString("RESULT","01");
			}
			
			//debug("==== GolfPointMainSeqnoInqDaoProc End ===");
		}catch ( Exception e ) {
			//debug("==== GolfPointMainSeqnoInqDaoProc ERROR Start ===");
			e.printStackTrace();
			//debug("==== GolfPointMainSeqnoInqDaoProc ERROR End ===");
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

		sql.append("\n SELECT	A.*,															");
		sql.append("\n 			NVL((SELECT TITLE FROM BBS WHERE seq=A.PRE_SEQ),'') PRE_TITLE,	");
		sql.append("\n 			NVL((SELECT TITLE FROM BBS WHERE seq=A.NEXT_SEQ),'') NEXT_TITLE	");
		sql.append("\n FROM	(	SELECT SEQ,														");
		sql.append("\n 			NVL((LAG(SEQ,1) OVER (ORDER BY SEQ)),'') PRE_SEQ,				");
		sql.append("\n 			NVL((LEAD(SEQ,1) OVER (ORDER BY SEQ)),'') NEXT_SEQ				");
		sql.append("\n 			FROM BBS	");
		sql.append("\n 		) A																	");
		sql.append("\n WHERE SEQ=?														");

		return sql.toString();
	}

}
