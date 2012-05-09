/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmPricesListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �ü� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-07-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lounge;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAdmPricesListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmPricesListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmPricesListDaoProc() {}	

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
			conn = context.getDbConnection("default", null);
			 
			//��ȸ ----------------------------------------------------------
			
			String green_memrtk_nm		= data.getString("GREEN_MEMRTK_NM");
			String qut_date		= data.getString("QUT_DATE");
			
			
			String sql = this.getSelectQuery(green_memrtk_nm,qut_date);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			
			if (!GolfUtil.isNull(qut_date)) pstmt.setString(++idx, qut_date); // �ü����� �˻�
			
			if (!GolfUtil.isNull(qut_date)) pstmt.setString(++idx, qut_date); // �ü����� �˻�
			
			if (!GolfUtil.isNull(green_memrtk_nm)) pstmt.setString(++idx, "%"+green_memrtk_nm+"%"); // ������� �˻�
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {
					
					result.addLong("GREEN_MEMRTK_NM_SEQ_NO" 		,rs.getLong("GREEN_MEMRTK_NM_SEQ_NO") );
					result.addString("GREEN_MEMRTK_NM" 		,rs.getString("GREEN_MEMRTK_NM") );
					result.addLong("PRIOR_QUT" 		,rs.getLong("PRIOR_QUT") );
					result.addLong("QUT" 		,rs.getLong("QUT") );
					result.addString("FLUC_UD" 		,rs.getString("FLUC_UD") );
					result.addString("FLUC" 		,rs.getString("FLUC") );
					result.addString("FLUC_RT" 		,rs.getString("FLUC_RT") );
					result.addLong("STD_MKPR" 		,rs.getLong("STD_MKPR") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
										
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
    private String getSelectQuery(String green_memrtk_nm, String qut_date){
        StringBuffer sql = new StringBuffer();
        
        sql.append("\n SELECT 	 	");
        sql.append("\n 	RNUM, GREEN_MEMRTK_NM_SEQ_NO, GREEN_MEMRTK_NM, PRIOR_QUT, QUT, FLUC_UD, FLUC, FLUC_RT, STD_MKPR, PAGE, TOT_CNT 	");
        sql.append("\n FROM (SELECT ROWNUM RNUM,	 	");
        sql.append("\n 			GREEN_MEMRTK_NM_SEQ_NO, GREEN_MEMRTK_NM, PRIOR_QUT, QUT, 	");
        sql.append("\n 	 		DECODE (SIGN (QUT - PRIOR_QUT), -1, '��', 0, '', 1, '��') FLUC_UD,	");
        sql.append("\n 	 		DECODE (QUT - PRIOR_QUT, 0, '-',    ' '|| REPLACE (LTRIM (TO_CHAR (QUT - PRIOR_QUT, '999,999')), '-', '')) FLUC,		");
        sql.append("\n 	 		TRIM (TO_CHAR (DECODE (PRIOR_QUT, 0, QUT, ((QUT - PRIOR_QUT) / PRIOR_QUT * 100)), '999990.00')) FLUC_RT,		");
        sql.append("\n 	 		STD_MKPR, CEIL (ROWNUM / ?) AS PAGE, MAX (RNUM) OVER () TOT_CNT		");
        sql.append("\n 		FROM (SELECT ROWNUM RNUM, 	");
        sql.append("\n 	 				TGM.GREEN_MEMRTK_NM_SEQ_NO, TGM.GREEN_MEMRTK_NM,	");
        sql.append("\n 	 				NVL ((SELECT QUT FROM BCDBA.TBGGREENMEMRTKQUTMGMT	");
        sql.append("\n 	 				WHERE GREEN_MEMRTK_NM_SEQ_NO =TGM.GREEN_MEMRTK_NM_SEQ_NO	");
        
		if (!GolfUtil.isNull(qut_date)) { // �ü����� �˻���
			sql.append("\n 				AND QUT_DATE = TO_CHAR (TO_DATE (?, 'YYYYMMDD') - 1, 'YYYYMMDD')		");
		}  else {
			sql.append("\n 				AND QUT_DATE = TO_CHAR (SYSDATE - 1, 'YYYYMMDD') 	");
		}
		
		sql.append("\n 						), 0) PRIOR_QUT,  	");
		sql.append("\n 						NVL ((SELECT QUT FROM BCDBA.TBGGREENMEMRTKQUTMGMT 	");
		sql.append("\n 						WHERE GREEN_MEMRTK_NM_SEQ_NO = TGM.GREEN_MEMRTK_NM_SEQ_NO	");
		
		if (!GolfUtil.isNull(qut_date)) { // �ü����� �˻�
			sql.append("\n 					AND QUT_DATE = ?		");
		}  else {
			sql.append("\n 					AND QUT_DATE = TO_CHAR (SYSDATE, 'YYYYMMDD')	");
		}
		
		sql.append("\n 						), 0) QUT, TGM.STD_MKPR 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGGREENMEMRTKMGMT TGM	");
		sql.append("\n 				WHERE TGM.GREEN_MEMRTK_NM_SEQ_NO = TGM.GREEN_MEMRTK_NM_SEQ_NO		");
		
		if (!GolfUtil.isNull(green_memrtk_nm)) sql.append("\n 	AND TGM.GREEN_MEMRTK_NM LIKE ?		"); // ������� �˻�
		
		sql.append("\n 				ORDER BY TGM.GREEN_MEMRTK_NM_SEQ_NO	)	");	
		sql.append("\n 		ORDER BY RNUM)	");	
		sql.append("\n WHERE PAGE = ?	");	
		
		return sql.toString();
    }
}
