/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCouponInqDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ ���� �󼼺���
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.drivrange;

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
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAdmCouponInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmCouponInqDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmCouponInqDaoProc() {}	

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
			pstmt.setLong(++idx, data.getLong("CPN_SEQ_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {
				while(rs.next())  {
					
					result.addLong("CPN_SEQ_NO" 			,rs.getLong("CPN_SEQ_NO") );
					result.addString("CUPN_CLSS" 		,rs.getString("GOLF_RNG_CUPN_CLSS") );
					result.addString("CPN_NM" 			,rs.getString("CPN_NM") );
					result.addLong("CUPN_DC_RT" 			,rs.getLong("DC_RT") );
					result.addString("IMG_NM"			,rs.getString("CUPN_IMG") );
					result.addLong("REG_PE_ID"			,rs.getLong("REG_MGR_ID") );
					result.addLong("CORR_PE_ID"			,rs.getLong("CHNG_MGR_ID") );
					result.addString("REG_DATE"			,rs.getString("REG_ATON"));
					/*
					String CHNG_ATON = rs.getString("CHNG_ATON");
					if (!GolfUtil.isNull(CHNG_ATON)) CHNG_ATON = DateUtil.format(CHNG_ATON, "yyyyMMdd", "yyyy�� MM�� dd��");
					result.addString("CORR_DATE"		,CHNG_ATON);
					*/
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
        
        sql.append("\n SELECT");
		sql.append("\n 	CPN_SEQ_NO, DECODE (GOLF_RNG_CUPN_CLSS, '0001', '����̺�������', '0002', '��ũ������ ������') GOLF_RNG_CUPN_CLSS,   ");
		sql.append("\n 	CPN_NM, DC_RT, CUPN_IMG, REG_MGR_ID, CHNG_MGR_ID, TO_CHAR(TO_DATE(REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, CHNG_ATON  ");
		sql.append("\n FROM BCDBA.TBGCUPNMGMT 	");
		sql.append("\n WHERE CPN_SEQ_NO = ?	");		

		return sql.toString();
    }
}
