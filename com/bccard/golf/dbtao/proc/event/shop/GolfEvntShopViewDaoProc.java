/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfEvntShopViewDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : �̺�Ʈ > ���� > �󼼺��� 
*   �������  : Golf
*   �ۼ�����  : 2010-02-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.event.shop;

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

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0
 ******************************************************************************/
public class GolfEvntShopViewDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfEvntBkWinListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfEvntShopViewDaoProc() {}	

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
			String gds_code = data.getString("gds_code");	

			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT) 
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, gds_code);
			rs = pstmt.executeQuery();
			
			

			if ( rs != null ) {

				while(rs.next())  {
					result.addString("GDS_NM" 			,rs.getString("GDS_NM") );
					result.addString("LRGS_IMG" 		,rs.getString("LRGS_IMG") );
					result.addString("EXPS_IMG" 		,rs.getString("EXPS_IMG") );
					result.addString("PRMT_NM" 			,rs.getString("PRMT_NM") );
					result.addString("SALE_AMT" 		,GolfUtil.comma(rs.getString("SALE_AMT")) );
					result.addInt("INT_AMT" 			,rs.getInt("SALE_AMT") );
					result.addString("RESULT", "00"); //������
				}
				
			} else {
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

	/**
	 * ��ǰ �ɼ� ��������
	 */
	public DbTaoResult execute_opt(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			String gds_code = data.getString("gds_code");	

			String sql = this.getOptionQuery();   
			
			// �Է°� (INPUT) 
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, gds_code);
			rs = pstmt.executeQuery();
			
			

			if(rs != null) {
				
				while(rs.next())  {	
					result.addString("SGL_LST_ITM_CODE" ,rs.getString("SGL_LST_ITM_CODE") );
					result.addString("SGL_LST_ITM_DTL_CTNT" ,rs.getString("SGL_LST_ITM_DTL_CTNT") );
					result.addString("SALE_CLSS" ,rs.getString("SALE_CLSS") );
					result.addString("REG_ATON" ,rs.getString("REG_ATON") );
					result.addString("REG_PE_ID" ,rs.getString("REG_PE_ID") );
					result.addString("STCKP_QTY" ,rs.getString("STCKP_QTY") );
										
					result.addString("RESULT", "00"); //������
				}
			}else{

				result.addString("SGL_LST_ITM_DTL_CTNT", "����");		
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
	
	/**
	 * ȸ������ ��������
	 */
	public DbTaoResult execute_mem(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null);
			String userId = data.getString("userId");	

			String sql = this.getMemInfoQuery();   
			
			// �Է°� (INPUT) 
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setString(1, userId);
			rs = pstmt.executeQuery();

			if ( rs.next() ) {
				GolfUtil.toTaoResult(result, rs);
				result.addString("RESULT", "00");
			}else{
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

		sql.append("\n SELECT GDS_NM, LRGS_IMG, EXPS_IMG, PRMT_NM, CTNT	\n");
		sql.append("\t , (SELECT SALE_AMT FROM BCDBA.TBGDSPRIC WHERE APPL_DATE <= TO_CHAR(SYSDATE,'YYYYMMDD') AND GDS_CODE=TP.GDS_CODE AND ROWNUM=1) SALE_AMT	\n");
		sql.append("\t FROM BCDBA.TBGDS TP	\n");
		sql.append("\t WHERE GDS_CODE=?	\n");

		return sql.toString();
    }

    private String getOptionQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT	");
		sql.append("\n 	SGL_LST_ITM_CODE, NVL(SGL_LST_ITM_DTL_CTNT,'11') SGL_LST_ITM_DTL_CTNT, SALE_CLSS, TO_CHAR(TO_DATE(REG_ATON,'YYYYMMDDHH24MISS'),'YYYY-MM-DD') REG_ATON, REG_PE_ID, STCKP_QTY	");
		sql.append("\n FROM 	");
		sql.append("\n BCDBA.TBSGLGDS	");
		sql.append("\n WHERE GDS_CODE = ?	");		
		sql.append("\n ORDER BY SGL_LST_ITM_CODE	");	
		
		return sql.toString();
    }

    private String getMemInfoQuery(){
        StringBuffer sql = new StringBuffer();

		sql.append("\n SELECT SUBSTR(ZIP_CODE,1,3) ZIP_CODE1, SUBSTR(ZIP_CODE,5,3) ZIP_CODE2, ZIPADDR, DETAILADDR, NVL(NW_OLD_ADDR_CLSS, '1')NW_OLD_ADDR_CLSS	\n");
		sql.append("\t FROM BCDBA.TBGGOLFCDHD	\n");
		sql.append("\t WHERE CDHD_ID=?	\n");
		
		return sql.toString();
    }
}
