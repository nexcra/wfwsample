/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBkPreGrListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ��ŷ ������ ����Ʈ ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-25
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking;

import java.io.Reader;
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
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	�̵������ 
 * @version	1.0 
 ******************************************************************************/
public class GolfWidgetBkPreTimeXmlDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLessonListDaoProc ���μ��� ������ 
	 * @param N/A
	 ***************************************************************** */
	public GolfWidgetBkPreTimeXmlDaoProc() {}	

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
			String sql = this.getSelectQuery();   

			// �Է°� (INPUT)   
			pstmt = conn.prepareStatement(sql.toString());
			
			rs = pstmt.executeQuery();

			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("GREEN_NM" 			,rs.getString("GREEN_NM") );
					result.addString("AFFI_GREEN_SEQ_NO" 	,rs.getString("AFFI_GREEN_SEQ_NO") );
					result.addString("BK_DATE" 				,rs.getString("BK_DATE") );
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

		sql.append("\n");
		sql.append("\t SELECT 																									\n");
		sql.append("\t     T1.GREEN_NM, T1.AFFI_GREEN_SEQ_NO																							\n");
		sql.append("\t     , TO_CHAR(TO_DATE(T2.BOKG_ABLE_DATE),'YYYY.MM.DD (DAY)') AS BK_DATE										\n");
		sql.append("\t     FROM BCDBA.TBGAFFIGREEN T1																			\n");
		sql.append("\t     JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.AFFI_GREEN_SEQ_NO=T2.AFFI_GREEN_SEQ_NO						\n");
		sql.append("\t     JOIN BCDBA.TBGRSVTABLEBOKGTIMEMGMT T3 ON T2.RSVT_ABLE_SCD_SEQ_NO=T3.RSVT_ABLE_SCD_SEQ_NO				\n");
		sql.append("\t     WHERE T1.AFFI_FIRM_CLSS='0001'																		\n");
		sql.append("\t     AND T2.BOKG_ABLE_DATE IS NOT NULL																	\n");
		sql.append("\t     AND T3.BOKG_RSVT_STAT_CLSS='0001'																	\n");
		sql.append("\t     AND T2.BOKG_ABLE_DATE BETWEEN TO_CHAR(SYSDATE+7,'YYYYMMDD') AND TO_CHAR(SYSDATE+14,'YYYYMMDD')		\n");
		sql.append("\t     ORDER BY GREEN_NM																					\n");
        
        
		return sql.toString();
    }
}
