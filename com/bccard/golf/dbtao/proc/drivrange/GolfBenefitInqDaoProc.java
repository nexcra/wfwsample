/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfBenefitInqDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ȸ�� �������� ��������
*   �������  : golf
*   �ۼ�����  : 2009-07-02
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.drivrange;

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
public class GolfBenefitInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfBenefitInqDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfBenefitInqDaoProc() {}	

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
		GolfUtil cstr = new GolfUtil();

		try {
			
			conn = context.getDbConnection("default", null);
			 
			//��ȸ ---------------------------------------------------------- 			
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("USERID"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addString("CDHD_SQ2_CTGO" 		,rs.getString("CDHD_SQ2_CTGO") ); // ȸ��2���з��ڵ�
					result.addString("DRGF_LIMT_YN" 		,rs.getString("DRGF_LIMT_YN") ); // �帲�������ѿ���(Y:���ٰ��� N:���ٺҰ�)
					result.addString("DRGF_APO_YN"			,rs.getString("DRGF_APO_YN") ); // �帲���� ���ٿ���(Y:���ٰ��� N:���ٺҰ�)
					result.addString("CUPN_PRN_NUM"			,rs.getString("CUPN_PRN_NUM") ); // �����μ�Ƚ��
					result.addString("DRVR_APO_YN"			,rs.getString("DRVR_APO_YN") ); // ����̺����������ٿ���(Y:���ٰ��� N:���ٺҰ�)
					result.addString("DRGF_YR_ABLE_NUM"		,rs.getString("DRGF_YR_ABLE_NUM") ); // �帲�����Ⱑ��Ƚ��
					result.addString("DRGF_MO_ABLE_NUM"		,rs.getString("DRGF_MO_ABLE_NUM") ); // �帲����������Ƚ��
					result.addString("ETHS_APO_YN"			,rs.getString("ETHS_APO_YN") ); // �������ֺ��������ٿ���(Y:���ٰ��� N:���ٺҰ�)
					
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();

        sql.append("\n SELECT");
		sql.append("\n 	TGB.CDHD_SQ2_CTGO, TGB.DRGF_LIMT_YN, TGB.DRGF_APO_YN, TGB.CUPN_PRN_NUM, TGB.DRVR_APO_YN, TGB.DRGF_YR_ABLE_NUM, TGB.DRGF_MO_ABLE_NUM, ETHS_APO_YN	 ");
		sql.append("\n FROM BCDBA.TBGGOLFCDHDBNFTMGMT TGB, BCDBA.TBGGOLFCDHDGRDMGMT TGUC, BCDBA.TBGGOLFCDHDCTGOMGMT TGUD 	");
		sql.append("\n WHERE TGUC.CDHD_CTGO_SEQ_NO = TGUD.CDHD_CTGO_SEQ_NO(+)	");
		sql.append("\n AND TGB.CDHD_SQ2_CTGO = TGUD.CDHD_SQ2_CTGO	");	
		sql.append("\n AND TGUC.CDHD_ID = ?	");	
		
		return sql.toString();
    }
}
