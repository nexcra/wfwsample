/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeInqDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ��û �󼼺���
*   �������  : golf
*   �ۼ�����  : 2009-05-25
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
public class GolfAdmRangeInqDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmRangeInqDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeInqDaoProc() {}	

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

			// ȸ���������̺� ���� �������� ����
			//��ȸ ----------------------------------------------------------			
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("RSVT_SQL_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addString("RSVT_SQL_NO" 		,rs.getString("GOLF_SVC_RSVT_NO") );
					
					String rsvt_able_date = rs.getString("RSVT_ABLE_DATE"); 
					if (!GolfUtil.isNull(rsvt_able_date)) rsvt_able_date = DateUtil.format(rsvt_able_date, "yyyyMMdd", "yyyy�� MM�� dd��");
					result.addString("RSVT_DATE"			,rsvt_able_date);
					
					result.addString("RSVT_TIME" 			,rs.getString("RSVT_TIME") );
					result.addString("HAN_NM" 			,rs.getString("HG_NM") );
					result.addString("GF_ID" 			,rs.getString("CDHD_ID") );
					result.addString("JUMIN"			,rs.getString("JUMIN") );
					result.addString("HP_DDD_NO"			,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO"			,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO"			,rs.getString("HP_TEL_SNO") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("CNCL_ATON"			,rs.getString("CNCL_ATON") );
					result.addString("RSVT_YN"			,rs.getString("RSVT_YN") );
					result.addString("ATD_YN"			,rs.getString("ATTD_YN") );
					
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
		sql.append("\n 	TGR.GOLF_SVC_RSVT_NO, TGRD.RSVT_ABLE_DATE,  ");
		sql.append("\n 	TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24:MI') || '~' || TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24:MI') RSVT_TIME,	 ");
		sql.append("\n 	TGU.HG_NM, TGU.CDHD_ID, 	 ");
		sql.append("\n 	SUBSTR (TGU.JUMIN_NO, 1, 6) ||'-*******' JUMIN,	 ");
		sql.append("\n 	TGR.HP_DDD_NO, TGR.HP_TEL_HNO, TGR.HP_TEL_SNO,	 ");
		sql.append("\n 	TO_CHAR (TO_DATE (TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON,	 ");
		sql.append("\n 	TO_CHAR (TO_DATE (TGR.CNCL_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') CNCL_ATON,	 ");
		sql.append("\n 	TGR.RSVT_YN, TGR.ATTD_YN	 ");
		sql.append("\n FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT, BCDBA.TBGGOLFCDHD TGU 	");
		sql.append("\n WHERE TGU.CDHD_ID = TGR.CDHD_ID(+)	");
		sql.append("\n AND TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");		
		sql.append("\n AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO	");		
		sql.append("\n AND TGR.GOLF_SVC_RSVT_NO = ?	");	
		return sql.toString();
    }
}
