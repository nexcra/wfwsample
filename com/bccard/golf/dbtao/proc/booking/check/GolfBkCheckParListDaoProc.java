/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkCheckParListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ > ��3��ŷ Ȯ�� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.check;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.common.loginAction.SessionUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfBkCheckParListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkCheckParListDaoProc ���μ��� ������  
	 * @param N/A
	 ***************************************************************** */
	public GolfBkCheckParListDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			String memb_id = "";
			// 00. ���� ���� üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				memb_id = userEtt.getAccount();
			}
			
			conn = context.getDbConnection("default", null);
			// 01. ��ȸ ----------------------------------------------------------
			String listtype = data.getString("LISTTYPE");		
			String sql = this.getSelectQuery(listtype);   	
			
			// 02. �Է°� ���������         
			pstmt = conn.prepareStatement(sql.toString());		
			pstmt.setString(1, memb_id);									
			rs = pstmt.executeQuery();

			// 03. ����Ʈ ��������
			if(rs != null) {			 

				while(rs.next())  {	

					result.addString("RSVT_SQL_NO" 	,rs.getString("RSVT_SQL_NO") );
					result.addString("RSVT_YN" 		,rs.getString("RSVT_YN") );
					result.addString("GR_NM" 		,rs.getString("GR_NM") );
					result.addString("BK_DATE" 		,rs.getString("BK_DATE") );
					result.addString("DEL_DATE" 	,rs.getString("DEL_DATE") );
					result.addString("DEL_ABLE" 	,rs.getString("DEL_ABLE").trim() );
					
					result.addString("RESULT", "00"); //������
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(String listtype){
        StringBuffer sql = new StringBuffer();      
        
		sql.append("\n SELECT	*																\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM													\n");
		sql.append("\t 		, RSVT_SQL_NO, RSVT_YN, GR_NM, BK_DATE, DEL_DATE, DEL_ABLE  		\n");		
		sql.append("\t 		FROM (SELECT ROWNUM RNUM											\n");		

		sql.append("\t 		, T1.GOLF_SVC_RSVT_NO AS RSVT_SQL_NO  																		\n");	
		sql.append("\t 		, (CASE WHEN T1.RSVT_YN='Y' THEN '����Ϸ�' WHEN T1.RSVT_YN='N' THEN '��ҿϷ�' WHEN T1.RSVT_YN='I' THEN '�ӹ����' WHEN T1.RSVT_YN='D' THEN '����' ELSE '' END) RSVT_YN  	\n");	
		sql.append("\t 		, T2.GREEN_NM AS GR_NM  																					\n");	
		sql.append("\t 		, (TO_CHAR(TO_DATE(T1.ROUND_HOPE_DATE),'YY/MM/DD(DY)')) AS BK_DATE  										\n");	
		sql.append("\t 		, TO_CHAR(TO_DATE(T1.ROUND_HOPE_DATE)-T2.BOKG_CNCL_ABLE_TRM,'YY/MM/DD(DY) ') AS DEL_DATE  					\n");	
		sql.append("\t 		, (CASE WHEN T1.RSVT_YN='N' THEN 'D' ELSE  																	\n");	
		//sql.append("\t 		    CASE WHEN T1.ROUND_HOPE_DATE-T2.BOKG_CNCL_ABLE_TRM>TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'Y' ELSE 'N' END  	\n");
		sql.append("\t 		    CASE WHEN  TO_CHAR(TO_DATE(T1.ROUND_HOPE_DATE)- NVL(T2.BOKG_CNCL_ABLE_TRM,0),'YYYYMMDD') >TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'Y' ELSE 'N' END  	\n");
		sql.append("\t 		END) AS DEL_ABLE  																							\n");	
		sql.append("\t 		FROM BCDBA.TBGRSVTMGMT T1 																					\n");	
		sql.append("\t 		JOIN BCDBA.TBGAFFIGREEN T2 ON T1.AFFI_GREEN_SEQ_NO=T2.AFFI_GREEN_SEQ_NO  									\n");	
		sql.append("\t 		WHERE T1.GOLF_SVC_RSVT_NO IS NOT NULL AND SUBSTR(GOLF_SVC_RSVT_MAX_VAL,5,1)='P' AND CDHD_ID=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'	\n");	

		
		if(listtype.equals("ALL")){
			sql.append("\t 			    AND T1.ROUND_HOPE_DATE>=TO_CHAR(SYSDATE,'YYYYMMDD') AND T1.RSVT_YN='Y'	\n");	
			sql.append("\t 			    ORDER BY T1.ROUND_HOPE_DATE	\n");
		}else{
			sql.append("\t 			    ORDER BY T1.REG_ATON DESC 																				\n");
		}
		
		
		//sql.append("\t 		ORDER BY T1.REG_ATON DESC  																					\n");	
		
		sql.append("\t 		)											\n");
		sql.append("\t 	ORDER BY RNUM									\n");
		sql.append("\t 	)												\n");
		if(listtype.equals("ALL")){		sql.append("\t WHERE RNUM<=1	\n");				}

		return sql.toString();
    }
}
