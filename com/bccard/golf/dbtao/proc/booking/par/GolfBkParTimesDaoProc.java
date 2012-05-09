/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreGrMapViewDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ������ �������� ó��
*   �������  : golf
*   �ۼ�����  : 2009-06-03
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking.par;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.golf.user.entity.UcusrinfoEntity;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;
import com.bccard.golf.common.AppConfig;
import com.bccard.golf.common.loginAction.SessionUtil;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfBkParTimesDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBkPermissionDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBkParTimesDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data,	HttpServletRequest request) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			int intMemGrade = 0;
			String memb_id = "";
			// 01. ��������üũ
			UcusrinfoEntity userEtt = SessionUtil.getFrontUserInfo(request);
			if(userEtt != null){
				intMemGrade = userEtt.getIntMemGrade();
				memb_id = userEtt.getAccount();
			}
						 
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery(intMemGrade, memb_id);
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
						
			if(rs != null) {
				while(rs.next())  {

					result.addString("PAR_3_BOKG_YR" 			,rs.getString("PAR_3_BOKG_YR") );
					result.addString("PAR_3_BOKG_MO" 			,rs.getString("PAR_3_BOKG_MO") );
					
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
    private String getSelectQuery(int intMemGrade, String memb_id){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\t  		SELECT 														\n");
		sql.append("\t  	    CASE PAR_3_BOKG_LIMT_YN										\n");
		sql.append("\t  	        WHEN 'A' THEN 1											\n");
		sql.append("\t  	        ELSE PAR_3_BOKG_YR_ABLE_NUM-PAR_3_BOKG_YR_DONE			\n");
		sql.append("\t  	        END AS PAR_3_BOKG_YR									\n");
		sql.append("\t  	    , CASE PAR_3_BOKG_LIMT_YN									\n");
		sql.append("\t  	        WHEN 'A' THEN 1											\n");
		sql.append("\t  	        ELSE PAR_3_BOKG_MO_ABLE_NUM-PAR_3_BOKG_MO_DONE			\n");
		sql.append("\t  	        END AS PAR_3_BOKG_MO									\n");
		sql.append("\t  	    FROM ( 														\n");
	        
		sql.append("\t  	    SELECT     													\n");
		sql.append("\t  	        PAR_3_BOKG_LIMT_YN        		\n");//-- ��3��ŷ���ѿ���
		sql.append("\t  	        , PAR_3_BOKG_YR_ABLE_NUM    	\n");//-- ��3��ŷ�Ⱑ��Ƚ��
		sql.append("\t  	        , PAR_3_BOKG_MO_ABLE_NUM    	\n");//-- ��3��ŷ������Ƚ��    
	    
	    //-- ��3��ŷ �ⰹ��
		sql.append("\t  	        , (SELECT COUNT(*)  																\n");
		sql.append("\t  	        FROM BCDBA.TBGRSVTMGMT T1															\n");
		sql.append("\t  	        LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t  	        WHERE T1.CDHD_ID='"+memb_id+"'														\n");
		sql.append("\t  	        AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')						\n");
		sql.append("\t  	        AND T1.RSVT_YN='Y'																	\n");
		sql.append("\t  	        AND T1.REG_ATON > (SELECT TO_CHAR(ADD_MONTHS(SYSDATE, -12), 'YYYY')||SUBSTR(ACRG_CDHD_JONN_DATE,5,8) FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID='"+memb_id+"')\n");
		sql.append("\t  	        AND T2.GOLF_SVC_RSVT_NO IS NULL) AS PAR_3_BOKG_YR_DONE								\n");
		
	    //-- ��3��ŷ ������
		sql.append("\t  	        , (SELECT COUNT(*)  																\n");
		sql.append("\t  	        FROM BCDBA.TBGRSVTMGMT T1															\n");
		sql.append("\t  	        LEFT JOIN BCDBA.TBGCBMOUSECTNTMGMT T2 ON T1.GOLF_SVC_RSVT_NO=T2.GOLF_SVC_RSVT_NO	\n");
		sql.append("\t  	        WHERE T1.CDHD_ID='"+memb_id+"'														\n");
		sql.append("\t  	        AND T1.GOLF_SVC_RSVT_MAX_VAL=(TO_CHAR(SYSDATE, 'YYYY')||'P')						\n");
		sql.append("\t  	        AND T1.RSVT_YN='Y'																	\n");
		sql.append("\t  	        AND T1.REG_ATON > (SELECT TO_CHAR(ADD_MONTHS(SYSDATE, -1), 'YYYY')||SUBSTR(ACRG_CDHD_JONN_DATE,5,8) FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID='"+memb_id+"')\n");
		sql.append("\t  	        AND T2.GOLF_SVC_RSVT_NO IS NULL) AS PAR_3_BOKG_MO_DONE								\n");
		

		sql.append("\t  	        FROM BCDBA.TBGGOLFCDHDBNFTMGMT													\n");
		sql.append("\t  	        WHERE CDHD_SQ2_CTGO='000"+intMemGrade+"'										\n");
		sql.append("\t  	    )																					\n");
	    
		return sql.toString();
    }
}
