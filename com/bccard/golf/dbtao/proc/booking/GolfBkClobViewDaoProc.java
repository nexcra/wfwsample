/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admGrUpdFormDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �������α׷� ���� ��
*   �������  : golf
*   �ۼ�����  : 2009-05-19
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.io.Reader;

import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.common.DateUtil;
import com.bccard.waf.common.StrUtil;

/******************************************************************************
 * Topn
 * @author	�̵������ 
 * @version	1.0
 ******************************************************************************/
public class GolfBkClobViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLessonChgFormDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBkClobViewDaoProc() {}	

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
			
			String col_NM = data.getString("COL_NM");
			String ta_NM = data.getString("TA_NM");
			String seq_NO = data.getString("SEQ_NO");
			 
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery(col_NM, ta_NM);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, seq_NO);
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {
					
					Reader reader = null;
					StringBuffer bufferSt = new StringBuffer();
					reader = rs.getCharacterStream("CONTEXT_CLOB");
					if( reader != null )  {
						char[] buffer = new char[1024]; 
						int byteRead; 
						while((byteRead=reader.read(buffer,0,1024))!=-1) 
							bufferSt.append(buffer,0,byteRead); 
						reader.close();
					}
					result.addString("CONTEXT_CLOB", bufferSt.toString());				
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
    private String getSelectQuery(String col_NM, String ta_NM){
        StringBuffer sql = new StringBuffer();
        
		sql.append("\n SELECT");
		
		if(col_NM.equals("CURS_INTD_CTNT")){
			sql.append("\t  CURS_INTD_CTNT AS CONTEXT_CLOB 	\n");
		}else if(col_NM.equals("CHARGE_INFO")){
			sql.append("\t  GREEN_CHRG_INFO AS CONTEXT_CLOB 	\n");
		}
		
		sql.append("\n FROM");
		
		if(ta_NM.equals("TBGAFFIGREEN")){
			sql.append("\n BCDBA.TBGAFFIGREEN");
		}
		
		sql.append("\n WHERE AFFI_GREEN_SEQ_NO = ?	");		
		
		return sql.toString();
    }
}
