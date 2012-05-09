/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAreaSelInqDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ���������� ����Ʈ�ڽ� ����
*   �������  : golf
*   �ۼ�����  : 2009-06-01
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.area;

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
 * Golf
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAreaSelInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAreaSelInqDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAreaSelInqDaoProc() {}	

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
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------
			String sido		= data.getString("SIDO");
			String gugun		= data.getString("GUGUN");
			
			if (!GolfUtil.isNull(sido) && GolfUtil.isNull(gugun)){ // ���� �˻�
				sql = this.getSelectQuery2();   
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun)){ // ���鵿 �˻�
				sql = this.getSelectQuery3();   
				
			} else { // �õ� �˻�
				sql = this.getSelectQuery1();
			}
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			if (!GolfUtil.isNull(sido) && GolfUtil.isNull(gugun)){ // ���� �˻�
				pstmt.setString(++idx, sido);
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun)){ // ���鵿 �˻�
				pstmt.setString(++idx, sido);
				pstmt.setString(++idx, gugun);
			}
			
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("AREA_NM" 				,rs.getString("AREA_NM") );
										
					result.addString("RESULT", "00"); //������
				}
			}

			if(result.size() < 1) {
				result.addString("RESULT", "01");			
			}
			 
		} catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery1(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT ");
		sql.append("\n		LG_CTY_NM AS AREA_NM ");									
		sql.append("\n FROM BCDBA.TBGZP ");										
		sql.append("\n WHERE ROWID IN (SELECT   MAX (ROWID) FROM BCDBA.TBGZP GROUP BY LG_CTY_NM) ");								
		sql.append("\n ORDER BY AREA_NM ");						
	
		return sql.toString();
    }
    
    private String getSelectQuery2(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT ");
		sql.append("\n		CTY_RGN_NM AS AREA_NM ");									
		sql.append("\n FROM BCDBA.TBGZP ");										
		sql.append("\n WHERE ROWID IN (SELECT   MAX (ROWID) FROM BCDBA.TBGZP GROUP BY CTY_RGN_NM) ");
		sql.append("\n AND LG_CTY_NM LIKE ? ");	
		sql.append("\n ORDER BY AREA_NM ");						
	
		return sql.toString();
    }
    
    private String getSelectQuery3(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT ");
		sql.append("\n		DONG_NM AS AREA_NM ");									
		sql.append("\n FROM BCDBA.TBGZP ");										
		sql.append("\n WHERE ROWID IN (SELECT   MAX (ROWID) FROM BCDBA.TBGZP GROUP BY DONG_NM) ");
		sql.append("\n AND LG_CTY_NM LIKE ? ");	
		sql.append("\n AND CTY_RGN_NM LIKE ? ");	
		sql.append("\n ORDER BY AREA_NM ");						
	
		return sql.toString();
    }
}
