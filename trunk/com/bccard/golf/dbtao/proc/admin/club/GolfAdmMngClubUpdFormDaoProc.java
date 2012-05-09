/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmMngClubChgFormDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ ��ü��ȣȸ ���� ������
*   �������  : golf
*   �ۼ�����  : 2009-05-18
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.club;

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
public class GolfAdmMngClubUpdFormDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaChgFormDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmMngClubUpdFormDaoProc() {}	

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
			pstmt.setLong(++idx, data.getLong("RECV_NO"));
			
			rs = pstmt.executeQuery();

			if(rs != null) {

				while(rs.next())  {	
					
					result.addInt("RECV_NO" 				,rs.getInt("CLUB_SEQ_NO") );
					result.addString("OPN_PE_NM" 			,rs.getString("OPN_PE_NM") ); 			// �����ڸ�
					result.addString("HP_DDD_NO" 			,rs.getString("HP_DDD_NO") ); 			// �޴���ȭ ����ȣ
					result.addString("HP_TEL_HNO" 			,rs.getString("HP_TEL_HNO") ); 			// �޴���ȭ ����ȣ
					result.addString("HP_TEL_SNO" 			,rs.getString("HP_TEL_SNO") ); 			// �޴���ȭ �Ϸù�ȣ
					result.addString("GOLF_CLUB_CTGO" 		,rs.getString("GOLF_CLUB_CTGO") );  	// ��ȣȸ �з�
					result.addString("CLUB_NM" 				,rs.getString("CLUB_NM") );				// ��ȣȸ ��Ī
					result.addString("CLUB_SBJT_CTNT" 		,rs.getString("CLUB_SBJT_CTNT") );		// ��ȣȸ ����
					result.addString("CLUB_INTD_CTNT" 		,rs.getString("CLUB_INTD_CTNT") );		// ��ȣȸ �Ұ�
					result.addString("CLUB_OPN_PRPS_CTNT" 	,rs.getString("CLUB_OPN_PRPS_CTNT") );	// ���� ����
					result.addString("CDHD_NUM_LIMT_YN" 	,rs.getString("CDHD_NUM_LIMT_YN") );	// ȸ�������ѿ���
					result.addString("CLUB_JONN_MTHD_CLSS" 	,rs.getString("CLUB_JONN_MTHD_CLSS") );	// ���Թ�� �����ڵ�
					result.addString("OPN_ATON" 			,rs.getString("OPN_ATON") ); 			// ������
					result.addString("CLUB_OPN_AUTH_YN" 	,rs.getString("CLUB_OPN_AUTH_YN") ); 	// �������ο���
					result.addString("MEMBERCNT" 			,rs.getString("MEMBERCNT") ); 			// ȸ����

					result.addString("RESULT", "00"); 												// ������
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
        
        sql.append("\n SELECT * FROM");
        
        sql.append("\n (SELECT");
		sql.append("\n 	CLUB_SEQ_NO,");
		sql.append("\n 	GOLF_CLUB_CTGO,");
		sql.append("\n 	CLUB_NM,OPN_PE_ID,OPN_PE_NM,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,CLUB_SBJT_CTNT,CLUB_IMG,CLUB_INTD_CTNT,");
		sql.append("\n 	CLUB_OPN_PRPS_CTNT,CDHD_NUM_LIMT_YN,LIMT_CDHD_NUM,CLUB_JONN_MTHD_CLSS,CLUB_OPN_AUTH_YN,CLUB_ACT_YN,APLC_ATON,OPN_ATON,CHNG_MGR_ID,CHNG_ATON,MEMBERCNT");
		sql.append("\n FROM ");
		
		sql.append("\n (SELECT");
		sql.append("\n 	CLUB_SEQ_NO,");
		sql.append("\n 	( SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE ='0042' AND GOLF_CMMN_CODE = TGL.GOLF_CLUB_CTGO AND USE_YN = 'Y') GOLF_CLUB_CTGO,");
		sql.append("\n 	CLUB_NM,OPN_PE_ID,OPN_PE_NM,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,CLUB_SBJT_CTNT,CLUB_IMG,CLUB_INTD_CTNT,");
		sql.append("\n 	CLUB_OPN_PRPS_CTNT,CDHD_NUM_LIMT_YN,LIMT_CDHD_NUM,CLUB_JONN_MTHD_CLSS,CLUB_OPN_AUTH_YN,CLUB_ACT_YN,APLC_ATON,TO_CHAR(TO_DATE(substr(OPN_ATON,1,8)), 'YY-MM-DD')||'('||substr(to_char(to_date(substr(OPN_ATON,1,8),'yyyymmdd'),'DAY'),1,1)||')'||' '||substr(substr(OPN_ATON,9,10),1,2)||':'||substr(substr(OPN_ATON,11,12),1,2) OPN_ATON,CHNG_MGR_ID,CHNG_ATON,");
		sql.append("\n 	( SELECT COUNT(CLUB_CDHD_SEQ_NO) CLUB_CDHD_SEQ_NO FROM BCDBA.TBGCLUBCDHDMGMT WHERE CLUB_SEQ_NO=TGL.CLUB_SEQ_NO ) MEMBERCNT");
		sql.append("\n FROM");
		sql.append("\n BCDBA.TBGCLUBMGMT TGL");
		sql.append("\n WHERE CLUB_SEQ_NO = ?	");	
		
		sql.append("\n ) )	");	

		return sql.toString();
    }
}
