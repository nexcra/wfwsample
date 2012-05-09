/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkCheckPreListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ > �����̾� Ȯ�� ó��
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
public class GolfBkCheckPreListDaoProc extends AbstractProc {
	
	public GolfBkCheckPreListDaoProc() {}	

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
			// 00. ���� ���� üũ
			String memb_id = "";
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
					result.addString("COURSE" 		,rs.getString("COURSE") );
					result.addString("DEL_DATE" 	,rs.getString("DEL_DATE") );
					result.addString("DEL_ABLE" 	,rs.getString("DEL_ABLE") );
					result.addString("CNCL_DATE" 	,rs.getString("CNCL_DATE") );
					result.addString("TIME_SEQ_NO" 	,rs.getString("TIME_SEQ_NO") );
					
					debug(">>>>>>>>>>>>>>  CNCL_DATE : "+rs.getString("CNCL_DATE"));
					
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
        
		sql.append("\n SELECT	*																			\n");
		sql.append("\t FROM (SELECT ROWNUM RNUM, TIME_SEQ_NO												\n");
		sql.append("\t 		, RSVT_SQL_NO, RSVT_YN, GR_NM, BK_DATE, COURSE, DEL_DATE, DEL_ABLE ,CNCL_DATE 	\n");	
		sql.append("\t 		FROM (SELECT ROWNUM RNUM														\n");
		

		sql.append("\t 			    , T1.GOLF_SVC_RSVT_NO AS RSVT_SQL_NO, T1.RSVT_ABLE_BOKG_TIME_SEQ_NO AS TIME_SEQ_NO						\n");	
		sql.append("\t 			    , CASE WHEN T1.RSVT_YN='Y' THEN '����Ϸ�' WHEN T1.RSVT_YN='N' THEN '��ҿϷ�' WHEN T1.RSVT_YN='I' THEN '�ӹ����' WHEN T1.RSVT_YN='D' THEN '����' ELSE '' END RSVT_YN 	\n");	
		sql.append("\t 			    , T4.GREEN_NM AS GR_NM 																																					\n");	
		sql.append("\t 			    , TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE),'YY/MM/DD(DY) ')||SUBSTR(T2.BOKG_ABLE_TIME,1,2)||':'||SUBSTR(T2.BOKG_ABLE_TIME,3,2) AS BK_DATE 									\n");	
		sql.append("\t 			    , T3.GOLF_RSVT_CURS_NM AS COURSE 																																		\n");

		// ��ұ����� -��Ұ����� 17�÷� �Ѵ�.
		//sql.append("\t 			    , TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE)-T4.BOKG_CNCL_ABLE_TRM,'YY/MM/DD(DY) ')||SUBSTR(T2.BOKG_ABLE_TIME,1,2)||':'||SUBSTR(T2.BOKG_ABLE_TIME,3,2) AS DEL_DATE 				\n");	
		sql.append("\t 			    , TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE)-T4.BOKG_CNCL_ABLE_TRM,'YY/MM/DD(DY)')||' 17:00' AS DEL_DATE	\n");
		
		//sql.append("\t 			    , (CASE WHEN T1.RSVT_YN='N' THEN 'D' ELSE 																																\n");	
		//sql.append("\t 			        CASE WHEN T3.BOKG_ABLE_DATE-T4.BOKG_CNCL_ABLE_TRM>TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'Y' ELSE 'N' END 																\n");		
		//sql.append("\t 			    	END) AS DEL_ABLE 																					\n");
		sql.append("\t  			, (CASE WHEN T1.RSVT_YN='N' THEN 'D' ELSE  							\n");
		
		// ��ұ����� -��Ұ����� 17�÷� �Ѵ�.
		//sql.append("\t  				CASE WHEN TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE)-NVL(T4.BOKG_CNCL_ABLE_TRM,0),'YYYYMMDD')>TO_CHAR(SYSDATE,'YYYYMMDD') THEN 'Y' ELSE 'N' END   							\n");
		sql.append("\t  				CASE WHEN TO_CHAR(TO_DATE(T3.BOKG_ABLE_DATE)-NVL(T4.BOKG_CNCL_ABLE_TRM,0),'YYYYMMDD')||'1700'>TO_CHAR(SYSDATE,'YYYYMMDDHH24MI') THEN 'Y' ELSE 'N' END	\n");
		
		sql.append("\t  				END) AS DEL_ABLE  												\n");
		sql.append("\t				,(CASE WHEN T1.CNCL_ATON IS NULL OR T1.CNCL_ATON ='' THEN ' ' ELSE TO_CHAR(TO_DATE(T1.CNCL_ATON,'YYYYMMDDHH24MISS'),'YY/MM/DD(DY) ')||SUBSTR(T1.CNCL_ATON,9,2)||':'||SUBSTR(T1.CNCL_ATON,11,2) END) AS CNCL_DATE			\n");
		//sql.append("\t 				, (CASE WHEN T1.CNCL_ATON IS NOT NULL THEN TO_CHAR(TO_DATE(T1.CNCL_ATON),'YYYYMMDDHH24MISS') ELSE '' END)AS CNCL_DATE									\n");
		//sql.append("\t  			, (CASE WHEN T1.CNCL_ATON IS NOT NULL OR T1.CNCL_ATON <> '' THEN  TO_CHAR(TO_DATE(T1.CNCL_ATON),'YY/MM/DD (DY) ')||SUBSTR(T1.CNCL_ATON,9,2)||':'||SUBSTR(T1.CNCL_ATON,11,2)		\n");
		//sql.append("\t					ELSE '' END)AS CNCL_DATE 										\n");
		sql.append("\t 			    FROM BCDBA.TBGRSVTMGMT T1 																				\n");	
		sql.append("\t 			    JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO 	\n");	
		sql.append("\t 			    JOIN BCDBA.TBGRSVTABLESCDMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO 					\n");	
		sql.append("\t 			    JOIN BCDBA.TBGAFFIGREEN T4 ON T1.AFFI_GREEN_SEQ_NO=T4.AFFI_GREEN_SEQ_NO 								\n");	
		sql.append("\t 			    WHERE SUBSTR(T1.GOLF_SVC_RSVT_NO, 5, 1)='M' AND CDHD_ID=? and NVL(T1.NUM_DDUC_YN,'Y')='Y'					\n");
		
		if(listtype.equals("ALL")){
			sql.append("\t 			    AND T3.BOKG_ABLE_DATE>=TO_CHAR(SYSDATE,'YYYYMMDD') AND T1.RSVT_YN='Y'	\n");	
			sql.append("\t 			    ORDER BY T3.BOKG_ABLE_DATE, T2.BOKG_ABLE_TIME	\n");
		}else{
			sql.append("\t 			    ORDER BY T1.REG_ATON DESC 																				\n");
		}
		
		sql.append("\t 		)				\n");
		sql.append("\t 	ORDER BY RNUM		\n");
		sql.append("\t 	)					\n");
		
		if(listtype.equals("ALL")){		sql.append("\t WHERE RNUM<=1	\n");				}

		return sql.toString();
    }
}
