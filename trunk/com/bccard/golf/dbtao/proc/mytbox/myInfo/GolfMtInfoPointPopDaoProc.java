/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmBoardComListDaoProc
*   �ۼ���     : (��)�̵������ ������
*   ����        : ������ �Խ��ǰ��� ��� ��ȸ 
*   �������  : Golf
*   �ۼ�����  : 2009-05-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.mytbox.myInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;

import com.bccard.waf.core.WaContext;
import com.bccard.waf.common.StrUtil;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoException;
import com.bccard.waf.tao.TaoResult; 
import com.bccard.waf.action.AbstractProc;

import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

import com.bccard.golf.common.GolfConfig;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.dbtao.DbTaoProc;
import com.bccard.golf.user.entity.UcusrinfoEntity;

/** ****************************************************************************
 * Media4th / Golf
 * @author
 * @version 2009-05-16  
 **************************************************************************** */
public class GolfMtInfoPointPopDaoProc extends AbstractProc {
	
	public static final String TITLE = "�Խ��� ���� ��� ��ȸ";
	
	
	/** ***********************************************************************
	* Proc ����.
	* @param con Connection
	* @param dataSet ��ȸ��������
	************************************************************************ */
	public TaoResult execute(WaContext context, HttpServletRequest request, TaoDataSet data) throws DbTaoException {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		DbTaoResult result = null;
		Connection con = null;
		
		//debug("==== GolfAdmBoardComListDaoProc start ===");
				
		try{
			// 01.��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			String memID = userEtt.getAccount();
			//��ȸ ����
			
			String sql = this.getSelectQuery();			
			con = context.getDbConnection("default", null);
			
			pstmt = con.prepareStatement(sql);
			int idx = 0;
			pstmt.setLong(++idx, data.getLong("page_no"));
			pstmt.setString(++idx, memID);
			pstmt.setLong(++idx, data.getLong("page_no"));
			rset = pstmt.executeQuery();	
			
			result = new DbTaoResult(TITLE);

			int art_NUM_NO = 0;
			
			if(rset != null) {
				while(rset.next())  {

				result.addLong("row_num",				rset.getLong("RNUM"));
				result.addInt("ART_NUM" 				,rset.getInt("ART_NUM")-art_NUM_NO );
				result.addString("total_cnt",			rset.getString("TOT_CNT") );
				result.addString("curr_page",			rset.getString("PAGE") );
				art_NUM_NO++;
				
				result.addString("ATON_DATE",			rset.getString("ATON_DATE"));
				result.addString("CODE_NM",				rset.getString("CODE_NM"));
				result.addString("CONT",				rset.getString("CONT"));
				result.addString("ACM_DDUC_AMT",		GolfUtil.comma(rset.getString("ACM_DDUC_AMT")));
				
				result.addString("RESULT","00");
				}
				
			}else{
				result.addString("RESULT","01");
			}
		
						
			
		}catch ( Exception e ) {
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
			
			//debug("==== GolfAdmBoardComListDaoProc ERROR ===");
		}finally{
			try { if(rset != null) {rset.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(con != null) {con.close();} else{} } catch (Exception ignored) {}
		}
		return result;	
	}

		
	/** ***********************************************************************
	* Query�� �����Ͽ� �����Ѵ�.
	************************************************************************ */
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT	*																									\n");
		sql.append("\t FROM	(SELECT	ROWNUM RNUM,																					\n");
		sql.append("\t 				ATON_DATE, CODE_NM,	CONT, ACM_DDUC_AMT,															\n");
		sql.append("\t 				CEIL(ROWNUM/10) AS PAGE,																		\n");
		sql.append("\t 				MAX(RNUM) OVER() TOT_CNT																		\n");	
		sql.append("\t 				, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  													\n");
		sql.append("\t 		FROM	(SELECT	ROWNUM AS RNUM,																			\n");
		sql.append("\t 				TO_CHAR(TO_DATE(SUBSTR(COME_ATON,1,8)),'YYYY.MM.DD') AS ATON_DATE								\n");
		sql.append("\t 				, (CASE ACM_DDUC_CLSS WHEN 'Y' THEN '���̹� �Ӵ� ����' ELSE T2.GOLF_CMMN_CODE_NM END) AS CODE_NM	\n");
		sql.append("\t 				, (CASE ACM_DDUC_CLSS WHEN 'Y' THEN '����(BCī��)' ELSE '���' END) AS CONT						\n");
		sql.append("\t 				, ACM_DDUC_AMT																					\n");
		sql.append("\t 				FROM BCDBA.TBGCBMOUSECTNTMGMT T1																\n");
		sql.append("\t 				LEFT JOIN BCDBA.TBGCMMNCODE T2 ON T2.GOLF_CMMN_CODE=T1.CBMO_USE_CLSS AND GOLF_CMMN_CLSS='0014'	\n");
		sql.append("\t 				WHERE CDHD_ID=?																					\n");
		sql.append("\t 				ORDER BY COME_ATON DESC																			\n");			
		sql.append("\t 				)								\n");
		sql.append("\t 		ORDER BY RNUM 							\n");
		sql.append("\t 		)										\n");
		sql.append("\t WHERE PAGE = ?								\n");

		return sql.toString();
	}
}
