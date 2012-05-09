/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmEvntBsLsnInqDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  이벤트 > 특별레슨 이벤트 목록
*   적용범위	: golf
*   작성일자	: 2009-07-04
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.accept;

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
public class GolfAdmEvntBsLsnInqDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 >  이벤트 > 특별레슨 이벤트 목록";
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
		String strDefault = "";
  
		try {
			conn = context.getDbConnection("default", null);
			String p_idx 	= data.getString("p_idx");
			String evnt_clss = data.getString("evnt_clss");
			String evntListMode = data.getString("evntListMode");
			String golf_svc_aplc_clss = data.getString("golf_svc_aplc_clss");
			
			int pidx = 0;
			boolean eof = false;
			
			if("Inq".equals(evntListMode)){
				pidx = 0;
				sql = this.getSelectInqQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx,evnt_clss);
				rs = pstmt.executeQuery();
				
				while(rs.next()){
					if(!eof) {
						result.addString("RESULT", "00");
					}
					
					result.addString("seq_no",	rs.getString("EVNT_SEQ_NO"));
					result.addString("evnt_nm",	rs.getString("EVNT_NM"));
					eof = true;
					
				}
				
				if(!eof) {
					result.addString("RESULT", "01");
				}
			
			}else if("RegFormInq".equals(evntListMode)){
				
				pidx = 0;
				sql = this.getSelectRegFormInqQuery();
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(++pidx, golf_svc_aplc_clss);
				pstmt.setString(++pidx,evnt_clss);
				
				rs = pstmt.executeQuery();
				int serial = 0;
				while(rs.next()){
					if(!eof) {
						result.addString("RESULT", "00");
					}

					result.addString("seq_no",	rs.getString("EVNT_SEQ_NO"));
					result.addString("evnt_nm",	rs.getString("EVNT_NM"));
					eof = true;
					serial++;
				}
				

				
				if(!eof) {
					result.addString("RESULT", "01");
				}
				
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
	private String getSelectInqQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	EVNT_SEQ_NO								");
		sql.append("\n 			,EVNT_NM								");
		sql.append("\n FROM		BCDBA.TBGEVNTMGMT						");
		sql.append("\n WHERE EVNT_CLSS = ?								");
		sql.append("\n ORDER BY  EVNT_SEQ_NO DESC						");
		return sql.toString();
	}
	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectRegFormInqQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*												");
		sql.append("\n FROM	(SELECT T1.EVNT_SEQ_NO								");
		sql.append("\n 			,T1.EVNT_NM 									");
		sql.append("\n 			,(select count(*)as cnt from BCDBA.TBGAPLCMGMT  ");
		sql.append("\n            where GOLF_SVC_APLC_CLSS = ? and PRZ_WIN_YN = 'Y' and LESN_SEQ_NO= T1.EVNT_SEQ_NO)AS CNT	");
		sql.append("\n 		FROM BCDBA.TBGEVNTMGMT T1 							");
		sql.append("\n 		WHERE T1.EVNT_CLSS = ?								");
		sql.append("\n 		)													");
		sql.append("\n 	WHERE TO_NUMBER(CNT) > 0								");
		
		
		return sql.toString();
	}
}
