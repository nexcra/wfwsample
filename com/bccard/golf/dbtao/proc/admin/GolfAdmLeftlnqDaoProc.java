/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfPointAdmlnqDaoProc
*   작성자    : (주)미디어포스 임은혜
*   내용      : 탑포인트 관리자 상단 메뉴 가져오기 
*   적용범위  : Golf
*   작성일자  : 2009-05-06  
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.msg.MsgEtt;
/** ****************************************************************************
 * Media4th 
 * @author
 * @version 2009-05-06
 **************************************************************************** */
public class GolfAdmLeftlnqDaoProc extends AbstractProc {
	public static final String TITLE = "비씨골프 관리자  왼쪽 메뉴 가져오기";
	/** *****************************************************************
	 * GolfPointAdmLeftlnqDaoProc 프로세스 생성자
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLeftlnqDaoProc() { 

	}
	
	/** ***********************************************************************
	* Proc 실행.
	* @param WaContext context
	* @param HttpServletRequest request
	* @param DbTaoDataSet data
	* @return DbTaoResult	result 
	************************************************************************ */
	public TaoResult execute(WaContext context, TaoDataSet data) throws DbTaoException  {

		DbTaoResult result = null;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		try {				
			
			//debug("GolfPointAdmLeftlnqDaoProc >> 쿼리 시작 >> ");
			
			con = context.getDbConnection("default", null);
			result = new DbTaoResult(TITLE);
			
			String account		= data.getString("account");	// 세션아이디
			String log_p_idx		= data.getString("log_p_idx");	// PK
			String m0_idx			= StrUtil.isNull(data.getString("m0_idx"),"");
			
			
			// 초기화된 대메뉴 번호
			if("".equals(m0_idx))
			{
				StringBuffer sql = new StringBuffer();	
				
				sql.append("\n").append(" SELECT rownum, t. m0_idx as m0_idx , t.m0_name  as m0_name ");
				sql.append("\n").append(" FROM ( ");
				sql.append("\n").append(" SELECT t0.SQ1_LEV_SEQ_NO AS m0_idx, MAX(t0.SQ1_LEV_MENU_NM) AS m0_name ");
				sql.append("\n").append(" FROM BCDBA.TBGSQ1MENUINFO t0, BCDBA.TBGSQ2MENUINFO t1, BCDBA.TBGSQ3MENUINFO t2, BCDBA.TBGSQ3MENUMGRPRIVFIXN t3 ");
				sql.append("\n").append(" WHERE t0.SQ1_LEV_SEQ_NO = t1.SQ1_LEV_SEQ_NO ");
				sql.append("\n").append(" AND t1.SQ2_LEV_SEQ_NO = t2.SQ2_LEV_SEQ_NO ");
				sql.append("\n").append(" AND t2.SQ3_LEV_SEQ_NO = t3.SQ3_LEV_SEQ_NO ");
				sql.append("\n").append(" AND t3.MGR_ID = ? ");
				sql.append("\n").append(" GROUP BY t0.SQ1_LEV_SEQ_NO ");
				sql.append("\n").append(" ) t ");
				sql.append("\n").append(" WHERE rownum < 2 ");

				
				pstmt = con.prepareStatement(sql.toString());	
				
				int idx = 0; 		
				pstmt.setString(++idx, account);
				
				rs = pstmt.executeQuery();
				
				if(rs != null)
				{
					while(rs.next())  
					{
						m0_idx = rs.getString("m0_idx");
					}
				}
													
			}
			
			
			
			int ret2 = 0;
			int ret = 0;

				
			StringBuffer sql_m = new StringBuffer();	
			sql_m.append("\n").append(" select ");
			sql_m.append("\n").append(" t1.SQ2_LEV_SEQ_NO AS m1_idx , ");
			sql_m.append("\n").append(" t2.SQ3_LEV_SEQ_NO AS m2_idx , ");
			sql_m.append("\n").append(" t1.SQ2_LEV_MENU_NM AS m1_name ,  ");
			sql_m.append("\n").append(" t2.SQ3_LEV_MENU_NM AS m2_name ,  ");
			sql_m.append("\n").append(" t2.LINK_URL AS m2_url ,  ");
			sql_m.append("\n").append(" t3.MGR_ID AS p_idx ,  ");
			sql_m.append("\n").append(" t4.MGR_ID as p_user_id ");
			sql_m.append("\n").append(" from BCDBA.TBGSQ2MENUINFO t1, BCDBA.TBGSQ3MENUINFO t2, BCDBA.TBGSQ3MENUMGRPRIVFIXN t3, BCDBA.TBGMGRINFO t4");
			sql_m.append("\n").append(" where  t1.SQ2_LEV_SEQ_NO = t2.SQ2_LEV_SEQ_NO(+)  AND  t2.SQ3_LEV_SEQ_NO = t3.SQ3_LEV_SEQ_NO(+)   AND t3.MGR_ID = t4.MGR_ID  ");
			sql_m.append("\n").append(" AND t4.MGR_ID = ?  AND t1.SQ1_LEV_SEQ_NO = ? ");
			sql_m.append("\n").append(" order by  t1.EPS_SEQ asc, t2.EPS_SEQ asc ");
					
			pstmt = con.prepareStatement(sql_m.toString());
			int idx_m = 0; 		
			pstmt.setString(++idx_m, account);
			pstmt.setString(++idx_m, m0_idx);
			rs = pstmt.executeQuery();
			
			if(rs != null )
			{
				while(rs.next())  {

			
				result.addString("m1_idx",		rs.getString("m1_idx"));
				result.addString("m2_idx",		rs.getString("m2_idx"));
				result.addString("m1_name",	rs.getString("m1_name"));
				result.addString("m2_name",	rs.getString("m2_name"));
				result.addString("m2_url",			rs.getString("m2_url"));
					
				
				
				ret2++;
				}
			}

			if(ret2 == 0){
				result.addString("RESULT","01");
				
			} else {
				result.addString("RESULT","00");	
			}
						

        } catch(Exception e) {
			
            MsgEtt msgEtt = new MsgEtt(MsgEtt.TYPE_ERROR, TITLE, "시스템오류입니다." );
            throw new DbTaoException(msgEtt,e);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
            try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
            try { if(con  != null) con.close();  } catch (Exception ignored) {}
		}

		return result;
	}
}
