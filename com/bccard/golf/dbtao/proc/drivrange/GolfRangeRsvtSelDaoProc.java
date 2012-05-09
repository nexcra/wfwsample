/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfRangeRsvtSelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : SKY72�帲���������� ����� ȸ�� �������� ���� 
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
public class GolfRangeRsvtSelDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfRangeRsvtSelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfRangeRsvtSelDaoProc() {}	

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
		String sql = "";

		try {
			conn = context.getDbConnection("default", null);
			
			//��ȸ ----------------------------------------------------------			
			sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++idx, data.getString("USERID"));
			pstmt.setString(++idx, data.getString("USERID"));
			pstmt.setString(++idx, data.getString("USERID"));
			pstmt.setString(++idx, data.getString("USERID"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {
				while(rs.next())  {
					
					result.addString("RSVT_YEAR_CNT", rs.getString("RSVT_YEAR_CNT") ); 	
					result.addString("RSVT_MONTH_CNT", rs.getString("RSVT_MONTH_CNT") ); 	
					
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
        
        sql.append("\n SELECT T1.RSVT_YEAR_CNT, T2.RSVT_MONTH_CNT	");
        sql.append("\n FROM (SELECT (RSVT_CNT - COME_CNT) RSVT_YEAR_CNT		");
        sql.append("\n 		FROM (SELECT RSVT_CNT,		");
        sql.append("\n 					(SELECT COUNT (COME_SEQ_SEQ_NO) COME_CNT		");
        sql.append("\n 					FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT, BCDBA.TBGCBMOUSECTNTMGMT TGCM	");
        sql.append("\n 					WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
        sql.append("\n 					AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO		");
        sql.append("\n 					AND TGR.GOLF_SVC_RSVT_NO = TGCM.GOLF_SVC_RSVT_NO	");
        sql.append("\n 					AND TGR.RSVT_YN = 'Y'		");
        sql.append("\n 					AND TGCM.ACM_DDUC_CLSS = 'N'	");
        sql.append("\n 					AND TGR.CDHD_ID = ?		");
        sql.append("\n 					AND TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'YYYY') = TO_CHAR (SYSDATE, 'YYYY')) COME_CNT	");
        sql.append("\n 				FROM (SELECT COUNT (TGRD.RSVT_ABLE_DATE) RSVT_CNT		");
        sql.append("\n 						FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT		");
        sql.append("\n 						WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
        sql.append("\n 						AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO		");
        sql.append("\n 						AND TGR.RSVT_YN = 'Y'		");
        sql.append("\n 						AND TGR.CDHD_ID = ?		");
        sql.append("\n 						AND TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'YYYY') = TO_CHAR (SYSDATE, 'YYYY') 	");
        sql.append("\n 						)	");
        sql.append("\n 				)	");
        sql.append("\n 		) T1, 		");
        sql.append("\n 		(SELECT (RSVT_CNT - COME_CNT) RSVT_MONTH_CNT	");
        sql.append("\n 		FROM (SELECT RSVT_CNT,		");
        sql.append("\n 					(SELECT COUNT (COME_SEQ_SEQ_NO) COME_CNT		");
        sql.append("\n 					FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT, BCDBA.TBGCBMOUSECTNTMGMT TGCM	");
        sql.append("\n 					WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
        sql.append("\n 					AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO		");
        sql.append("\n 					AND TGR.GOLF_SVC_RSVT_NO = TGCM.GOLF_SVC_RSVT_NO	");
        sql.append("\n 					AND TGR.RSVT_YN = 'Y'		");
        sql.append("\n 					AND TGCM.ACM_DDUC_CLSS = 'N'	");
        sql.append("\n 					AND TGR.CDHD_ID = ?		");
        sql.append("\n 					AND TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'MM') = TO_CHAR (SYSDATE, 'MM')) COME_CNT	");
        sql.append("\n 				FROM (SELECT COUNT (TGRD.RSVT_ABLE_DATE) RSVT_CNT		");
        sql.append("\n 						FROM BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT		");
        sql.append("\n 						WHERE TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
        sql.append("\n 						AND TGRD.RSVT_ABLE_SCD_SEQ_NO = TGRT.RSVT_ABLE_SCD_SEQ_NO		");
        sql.append("\n 						AND TGR.RSVT_YN = 'Y'		");
        sql.append("\n 						AND TGR.CDHD_ID = ?		");
        sql.append("\n 						AND TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'MM') = TO_CHAR (SYSDATE, 'MM') 	");
        sql.append("\n 						)	");
        sql.append("\n 				)	");
        sql.append("\n 		) T2 		");
        
		return sql.toString();
    }
   
}
