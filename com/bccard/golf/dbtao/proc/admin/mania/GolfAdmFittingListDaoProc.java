/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmManiaListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���ΰ�
*   ����      : ������ �����帮�������ν�û���� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.mania;

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
public class GolfAdmFittingListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmFittingListDaoProc() {}	   

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
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			String search_dt1		= data.getString("SEARCH_DT1"); //�˻� ���۳�¥����
			String search_dt2		= data.getString("SEARCH_DT2"); //�˻� ���ᳯ¥����
			String scoop_cp_cd		= data.getString("SCOOP_CP_CD");
			
			String search_club		= data.getString("SEARCH_CLUB");
			String search_csl		= data.getString("SEARCH_CSL");
			

			String sql = this.getSelectQuery(search_sel, search_word, search_dt1, search_dt2, scoop_cp_cd, search_club, search_csl);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));//���ο� no������ 
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));//���ο� no������ 
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));//���ο� no������ 
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));//���ο� no������ 
			
			
			if (!GolfUtil.isNull(search_word))	pstmt.setString(++idx, "%"+search_word+"%");
			if (!GolfUtil.isNull(scoop_cp_cd))	pstmt.setString(++idx, scoop_cp_cd);
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, search_dt1+"000000");
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, search_dt2+"999999");
			
			if (!GolfUtil.isNull(search_club))	pstmt.setString(++idx, search_club);
			if (!GolfUtil.isNull(search_csl))	pstmt.setString(++idx, search_csl);
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addString("RSVT_SQL_NO" 		,rs.getString("GOLF_SVC_RSVT_NO") );
					result.addString("ID" 				,rs.getString("CDHD_ID") );

					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") );
					
					result.addString("RSVT_YN" 			,rs.getString("RSVT_YN") );
					result.addString("NOTE_MTTR_EXPL" 			,rs.getString("NOTE_MTTR_EXPL") );
					result.addString("ROU_DATE" 		,rs.getString("ROUND_HOPE_DATE") );
					result.addString("ROU_TIME" 		,rs.getString("ROUND_HOPE_TIME") );
					result.addString("GF_CLUB_CD" 		,rs.getString("FIT_HOPE_CLUB_CLSS") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );
					result.addString("HAN_NM" 		    ,rs.getString("HG_NM") );		
			
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );					
					
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
	private String getSelectQuery(String search_sel, String search_word, String search_dt1, String search_dt2, String scoop_cp_cd, String search_club, String search_csl){
        StringBuffer sql = new StringBuffer();
                 
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			GOLF_SVC_RSVT_NO, CDHD_ID, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,  ");
		sql.append("\n 	DECODE( RSVT_YN 	");
		sql.append("\n 		,'Y', '�Ϸ�'	");
		sql.append("\n 		,'', '���'	"); 
		sql.append("\n 	) RSVT_YN, NOTE_MTTR_EXPL,	");
		sql.append("\n	ROUND_HOPE_DATE, ROUND_HOPE_TIME, ");
		
		sql.append("\n 	DECODE( FIT_HOPE_CLUB_CLSS 	");
		sql.append("\n 		,'0001', '����̹�'	");
		sql.append("\n 		,'0002', '������ ���'	");
		sql.append("\n 		,'0003', '���̾�'	");
		sql.append("\n 		,'0004', '����'	");
		sql.append("\n 	) FIT_HOPE_CLUB_CLSS,	");
		
		sql.append("\n	REG_ATON, HG_NM, ");
		
		sql.append("\n	CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n	MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n	(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");	//���ο� no������
		sql.append("\n	FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n	GOLF_SVC_RSVT_NO, A.CDHD_ID, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, RSVT_YN, NOTE_MTTR_EXPL, ");
		sql.append("\n	TO_CHAR(TO_DATE(ROUND_HOPE_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') ROUND_HOPE_DATE,");
		sql.append("\n	TO_CHAR(TO_DATE(ROUND_HOPE_TIME, 'HH24MI'), 'HH:MI') ROUND_HOPE_TIME, FIT_HOPE_CLUB_CLSS,");
		sql.append("\n 				TO_CHAR(TO_DATE(substr(A.REG_ATON,1,8)), 'YY-MM-DD')||'('||substr(to_char(to_date(substr(A.REG_ATON,1,8),'yyyymmdd'),'DAY'),1,1)||')'||' '||substr(substr(A.REG_ATON,9,10),1,2)||':'||substr(substr(A.REG_ATON,11,12),1,2) REG_ATON, HG_NM");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGRSVTMGMT A, BCDBA.TBGGOLFCDHD  B	 	");
		sql.append("\n 				WHERE A.GOLF_SVC_RSVT_NO = A.GOLF_SVC_RSVT_NO	");
		
		sql.append("\n     AND A.CDHD_ID = B.CDHD_ID ");  // �����̺�� ���� �߰�
		
		
		if (!GolfUtil.isNull(search_word)) sql.append("\n 	AND "+search_sel+" LIKE ?	");
		if (!GolfUtil.isNull(scoop_cp_cd)) sql.append("\n 	AND GOLF_SVC_RSVT_MAX_VAL = ?	");
		if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) ) sql.append("\n AND (A.ROUND_HOPE_DATE >= ? AND A.ROUND_HOPE_DATE <= ?) ");

		if (!GolfUtil.isNull(search_club)) sql.append("\n 	AND FIT_HOPE_CLUB_CLSS = ?	");
		if (!GolfUtil.isNull(search_csl)) sql.append("\n 	AND RSVT_YN = ?	");
		sql.append("\n 				ORDER BY GOLF_SVC_RSVT_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}