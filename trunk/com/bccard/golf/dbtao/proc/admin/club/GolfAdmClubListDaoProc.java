/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmClubListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ ��ȣȸ ������ ���� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-07-06
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.club;

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
public class GolfAdmClubListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmClubListDaoProc() {}	

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
			String search_sel		= data.getString("SEARCH_SEL"); 	//�˻� �������� Ű����
			String search_word		= data.getString("SEARCH_WORD");	//�˻� ��������
			
			String sckd_code		= data.getString("SCKD_CODE"); 		// �˻� ��ȣȸ �з�
			String scnsl_yn			= data.getString("SCNSL_YN"); 		// �˻� ��������	
			String sprgs_yn			= data.getString("SPRGS_YN"); 		// �˻� ���࿩������
			String search_dt1		= data.getString("SEARCH_DT1"); 	// �˻� ���۳�¥����
			String search_dt2		= data.getString("SEARCH_DT2"); 	// �˻� ���ᳯ¥����
			String scoop_cp_cd		= data.getString("SCOOP_CP_CD"); 	// 
			
			String sql = this.getSelectQuery(search_sel, search_word, sckd_code, scnsl_yn, sprgs_yn, search_dt1, search_dt2, scoop_cp_cd);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));	
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));	
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			
			if (!GolfUtil.isNull(scoop_cp_cd))	pstmt.setString(++idx, scoop_cp_cd);
			if (!GolfUtil.isNull(sckd_code))	pstmt.setString(++idx, sckd_code);
			if (!GolfUtil.isNull(scnsl_yn))		pstmt.setString(++idx, scnsl_yn);
			if (!GolfUtil.isNull(sprgs_yn))		pstmt.setString(++idx, sprgs_yn);
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, search_dt1+"000000");
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, search_dt2+"999999");
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addInt("RECV_NO" 				,rs.getInt("CLUB_SEQ_NO") );
					result.addString("CLUB_NM" 				,rs.getString("CLUB_NM") ); 				// ��ȣȸ��
					result.addString("OPN_PE_NM" 			,rs.getString("OPN_PE_NM") ); 			// �����ڸ�
					result.addString("HP_DDD_NO" 			,rs.getString("HP_DDD_NO") ); 			// �޴���ȭ ����ȣ
					result.addString("HP_TEL_HNO" 			,rs.getString("HP_TEL_HNO") ); 			// �޴���ȭ ����ȣ
					result.addString("HP_TEL_SNO" 			,rs.getString("HP_TEL_SNO") ); 			// �޴���ȭ �Ϸù�ȣ
					result.addString("GOLF_CLUB_CTGO" 		,rs.getString("GOLF_CLUB_CTGO") );  	// ��ȣȸ �з�
					result.addString("CDHD_NUM_LIMT_YN" 	,rs.getString("CDHD_NUM_LIMT_YN") );	// ȸ�������ѿ���
					result.addString("LIMT_CDHD_NUM" 		,rs.getString("LIMT_CDHD_NUM") );		// ����ȸ����
					result.addString("CLUB_JONN_MTHD_CLSS" 	,rs.getString("CLUB_JONN_MTHD_CLSS") );	// ���Թ�� �����ڵ�
					result.addString("APLC_ATON" 			,rs.getString("APLC_ATON") ); 			// ��û ��������
					result.addString("OPN_ATON" 			,rs.getString("OPN_ATON") ); 			// ��������
					result.addString("CLUB_OPN_AUTH_YN" 	,rs.getString("CLUB_OPN_AUTH_YN") ); 	// �������ο���
					result.addString("CLUB_ACT_YN" 			,rs.getString("CLUB_ACT_YN") ); 		// �����
					result.addString("MEMBERCNT" 			,rs.getString("MEMBERCNT") ); 			// ȸ����

					result.addString("TOTAL_CNT"			,rs.getString("TOT_CNT") );     		// �� �����
					result.addString("CURR_PAGE"			,rs.getString("PAGE") );
					result.addString("LIST_NO"				,rs.getString("LIST_NO") ); 
					result.addString("RNUM"					,rs.getString("RNUM") );				// ���� ������ �����
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
    private String getSelectQuery(String search_sel, String search_word, String sckd_code, String scnsl_yn, String sprgs_yn, String search_dt1, String search_dt2, String scoop_cp_cd){
        StringBuffer sql = new StringBuffer();
                
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM, CLUB_SEQ_NO,GOLF_CLUB_CTGO,CLUB_NM,OPN_PE_ID,OPN_PE_NM,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO,");
		sql.append("\n CLUB_SBJT_CTNT,CLUB_IMG,CLUB_INTD_CTNT,CLUB_OPN_PRPS_CTNT,");
		sql.append("\n 	DECODE( CDHD_NUM_LIMT_YN 	");
		sql.append("\n 		,'Y', '����'	");
		sql.append("\n 		,'N', '���Ѿ���'	");
		sql.append("\n ) CDHD_NUM_LIMT_YN,");
		sql.append("\n LIMT_CDHD_NUM,");
		sql.append("\n 	DECODE( CLUB_JONN_MTHD_CLSS 	");
		sql.append("\n 		,'A', '�����İ���'	");
		sql.append("\n 		,'R', '��ð���'	"); 
		sql.append("\n ) CLUB_JONN_MTHD_CLSS,");
		sql.append("\n 	DECODE( CLUB_OPN_AUTH_YN 	");
		sql.append("\n 		,'Y', '��������'	");
		sql.append("\n 		,'N', '����'	"); 
		sql.append("\n 		,'W', '���'	");
		sql.append("\n ) CLUB_OPN_AUTH_YN, ");
		sql.append("\n 	DECODE( CLUB_ACT_YN 	");
		sql.append("\n 		,'Y', '�'	");
		sql.append("\n 		,'N', '���'	"); 
		sql.append("\n 		,'', '���'	");
		sql.append("\n ) CLUB_ACT_YN,	");
		sql.append("\n APLC_ATON,OPN_ATON,CHNG_MGR_ID,CHNG_ATON,MEMBERCNT, ");
		sql.append("\n	CEIL(ROWNUM/?) AS PAGE,	");
		
		sql.append("\n	MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n	(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");	
		sql.append("\n	FROM (SELECT ROWNUM RNUM,	");

		sql.append("\n	CLUB_SEQ_NO,( SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE ='0042' AND GOLF_CMMN_CODE = TGL.GOLF_CLUB_CTGO AND USE_YN = 'Y') GOLF_CLUB_CTGO,");
		sql.append("\n	CLUB_NM,OPN_PE_ID,OPN_PE_NM,HP_DDD_NO,HP_TEL_HNO,HP_TEL_SNO, ");
		sql.append("\n CLUB_SBJT_CTNT,CLUB_IMG,CLUB_INTD_CTNT,CLUB_OPN_PRPS_CTNT,CDHD_NUM_LIMT_YN,LIMT_CDHD_NUM,CLUB_JONN_MTHD_CLSS,");
		sql.append("\n CLUB_OPN_AUTH_YN,CLUB_ACT_YN, ");
		sql.append("\n TO_CHAR(TO_DATE(substr(APLC_ATON,1,8)), 'YY-MM-DD')||'('||substr(to_char(to_date(substr(APLC_ATON,1,8),'yyyymmdd'),'DAY'),1,1)||')'||' '||substr(substr(APLC_ATON,9,10),1,2)||':'||substr(substr(APLC_ATON,11,12),1,2) APLC_ATON, ");
		sql.append("\n TO_CHAR(TO_DATE(substr(OPN_ATON,1,8)), 'YY-MM-DD') OPN_ATON, ");
		sql.append("\n CHNG_MGR_ID, ");
		sql.append("\n CHNG_ATON, ");
		sql.append("\n 	( SELECT COUNT(CLUB_CDHD_SEQ_NO) CLUB_CDHD_SEQ_NO FROM BCDBA.TBGCLUBCDHDMGMT WHERE CLUB_SEQ_NO=TGL.CLUB_SEQ_NO ) MEMBERCNT");
		sql.append("\n 	FROM  	");
		sql.append("\n	BCDBA.TBGCLUBMGMT TGL");
		sql.append("\n	WHERE TGL.CLUB_SEQ_NO = CLUB_SEQ_NO	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TGL.OPN_PE_ID LIKE  ?	");
				sql.append("\n 				OR TGL.ADDR LIKE ? )	");				
			} else {
				
				
				if (search_sel.equals("HAN_NM")) {
					sql.append("\n 				AND TGL.OPN_PE_ID LIKE  ?	");	
				} else {
					sql.append("\n 				AND "+search_sel+" LIKE ?	");
				}
				
				
			}
		}


		if (!GolfUtil.isNull(sckd_code))sql.append("\n AND GOLF_CLUB_CTGO = ? ");		
		if (!GolfUtil.isNull(scnsl_yn))	sql.append("\n AND CLUB_OPN_AUTH_YN = ? ");		
		if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	sql.append("\n AND (APLC_ATON >= ? AND APLC_ATON <= ?) ");		

		sql.append("\n 				ORDER BY CLUB_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
} 
