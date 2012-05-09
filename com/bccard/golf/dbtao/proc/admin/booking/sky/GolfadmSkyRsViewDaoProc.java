/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfadmGrUpdFormDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ ��ŷ ������ ���� �� ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.sky;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Golf
 * @author	�̵������    
 * @version	1.0
 ******************************************************************************/
public class GolfadmSkyRsViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmGrUpdFormDaoProc ���μ��� ������ 
	 * @param N/A
	 ***************************************************************** */
	public GolfadmSkyRsViewDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context, TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
						 
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO"));
			rs = pstmt.executeQuery();		

			if(rs != null) {
				while(rs.next())  {
					
					String str_appr_opion = rs.getString("APPR_OPION");
					
					result.addString("RSVT_YN" 		,rs.getString("RSVT_YN") );
					result.addString("ID" 			,rs.getString("ID") );
					result.addString("HP_NO" 		,rs.getString("HP_NO") );
					result.addString("REG_DATE" 	,rs.getString("REG_DATE") );
					result.addString("CNCL_DATE" 	,rs.getString("CNCL_DATE") );
					result.addString("BK_TIME" 		,rs.getString("BK_TIME") );
					result.addString("BK_DATE" 		,rs.getString("BK_DATE") );
					result.addString("CTNT" 		,rs.getString("CTNT") );
					result.addString("TOT_PERS_NUM" ,rs.getString("TOT_PERS_NUM") );
					result.addString("HOLE" 		,rs.getString("HOLE") );
					result.addString("NAME" 		,rs.getString("NAME") );
					result.addString("SOCID" 		,rs.getString("SOCID") );
					result.addString("EMAIL" 		,rs.getString("EMAIL1") );
					result.addString("PHONE" 		,rs.getString("PHONE") );
					result.addString("APPR_OPION"	,str_appr_opion);
					result.addString("TOTAL_CNT"	,rs.getString("TOTAL_CNT"));
					
					if(!"".equals(str_appr_opion) && str_appr_opion != null){
						result.addString("APPR_OPION_SIZE",""+str_appr_opion.length());
						
					}else{
						result.addString("APPR_OPION_SIZE","0");
					}
					
					
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT 	\n");
		
		sql.append("\t  T1.RSVT_YN ,T1.CDHD_ID AS ID, T1.TOT_PERS_NUM AS TOT_PERS_NUM	\n");
		sql.append("\t  , (T1.HP_DDD_NO||'-'||T1.HP_TEL_HNO||'-'||T1.HP_TEL_SNO) AS HP_NO	\n");
		sql.append("\t  , (TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON, 0, 8)), 'YYYY-MM-DD')||'('||	\n");
		sql.append("\t  TO_CHAR(TO_DATE(SUBSTR(T1.REG_ATON, 0, 8)),'DY')||') '||	\n");
		sql.append("\t  SUBSTR(T1.REG_ATON, 9, 2)||':'||SUBSTR(T1.REG_ATON, 11, 2)) AS REG_DATE	\n");
		sql.append("\t  , (CASE WHEN CNCL_ATON IS NULL THEN '' ELSE	\n");
		sql.append("\t  TO_CHAR(TO_DATE(SUBSTR(T1.CNCL_ATON, 0, 8)), 'YYYY-MM-DD')||'('||	\n");
		sql.append("\t  TO_CHAR(TO_DATE(SUBSTR(T1.CNCL_ATON, 0, 8)),'DY')||') '||	\n");
		sql.append("\t  SUBSTR(T1.CNCL_ATON, 9, 2)||':'||SUBSTR(T1.CNCL_ATON, 11, 2) END) AS CNCL_DATE	\n");
		sql.append("\t  , (CASE WHEN T4.SKY72_HOLE_CODE='0001' THEN '7' ELSE '14' END) AS HOLE, T1.CTNT AS CTNT	\n");
		sql.append("\t  , (SUBSTR(T2.BOKG_ABLE_TIME,0,2)||':'||SUBSTR(T2.BOKG_ABLE_TIME,3,4)) AS BK_TIME	\n");
		sql.append("\t  , (TO_CHAR(TO_DATE(T4.BOKG_ABLE_DATE), 'YYYY-MM-DD')||' ('||TO_CHAR(TO_DATE(T4.BOKG_ABLE_DATE), 'DY')||')') AS BK_DATE	\n");
		sql.append("\t  , T6.HG_NM as NAME, SUBSTR(T6.JUMIN_NO,1,6) AS SOCID, T6.EMAIL AS EMAIL1			\n");
		sql.append("\t	, T6.APPR_OPION ,T6.PHONE														\n");
		sql.append("\t  , (SELECT COUNT(*) FROM BCDBA.TBGRSVTMGMT WHERE CDHD_ID=T1.CDHD_ID AND SUBSTR(GOLF_SVC_RSVT_NO,5,1)='S' )AS TOTAL_CNT  \n");
		sql.append("\t  FROM BCDBA.TBGRSVTMGMT T1	\n");
		sql.append("\t  JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T2 ON T1.RSVT_ABLE_BOKG_TIME_SEQ_NO=T2.RSVT_ABLE_BOKG_TIME_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGRSVTABLESCDMGMT T4 ON T2.RSVT_ABLE_SCD_SEQ_NO=T4.RSVT_ABLE_SCD_SEQ_NO	\n");
		sql.append("\t  JOIN BCDBA.TBGGOLFCDHD T6 ON T6.CDHD_ID=T1.CDHD_ID	\n");
		sql.append("\t  WHERE T1.GOLF_SVC_RSVT_NO=?		\n");	
		
		return sql.toString();
    }
}
