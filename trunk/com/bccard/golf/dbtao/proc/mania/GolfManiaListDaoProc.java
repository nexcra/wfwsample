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

package com.bccard.golf.dbtao.proc.mania;

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
public class GolfManiaListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfManiaListDaoProc() {}	

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
			
			String sckd_code		= data.getString("SCKD_CODE"); 		// �˻� ��������
			String scnsl_yn			= data.getString("SCNSL_YN"); 		// �˻� ��㿩������	
			String sprgs_yn			= data.getString("SPRGS_YN"); 		// �˻� ���࿩������
			String search_dt1		= data.getString("SEARCH_DT1"); 	// �˻� ���۳�¥����
			String search_dt2		= data.getString("SEARCH_DT2"); 	// �˻� ���ᳯ¥����
			String scoop_cp_cd		= "0002";							//data.getString("SCOOP_CP_CD"); //������ ����
			
			
			String sql = this.getSelectQuery(search_sel, search_word, sckd_code, scnsl_yn, sprgs_yn, search_dt1, search_dt2, scoop_cp_cd);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));	
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));	
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setString(++idx, data.getString("ID") ); //���ǰ�  �޾ƿ� ����
			
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");			
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			
			pstmt.setString(++idx, scoop_cp_cd);
			
			if (!GolfUtil.isNull(sckd_code))	pstmt.setString(++idx, sckd_code);
			if (!GolfUtil.isNull(scnsl_yn))		pstmt.setString(++idx, scnsl_yn);
			if (!GolfUtil.isNull(sprgs_yn))		pstmt.setString(++idx, sprgs_yn);
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, DateUtil.format(search_dt1, "yyyy-MM-dd", "yyyyMMdd000000"));
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, DateUtil.format(search_dt2, "yyyy-MM-dd", "yyyyMMdd999999"));
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					
					result.addInt("RECV_NO" 			,rs.getInt("APLC_SEQ_NO") );
					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") ); 				// �޴���ȭ ����ȣ
					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") ); 				// �޴���ȭ ����ȣ
					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") ); 				// �޴���ȭ �Ϸù�ȣ
					result.addString("CSTMR_ID" 		,rs.getString("CDHD_ID") ); 				// ��û��
					result.addString("EMAIL" 		,rs.getString("EMAIL") ); 				// �̸���
					result.addString("CAR_TYPE_CD" 		,rs.getString("GOLF_LMS_CAR_KND_CLSS") );	// �����ڵ�
					String pu_date = rs.getString("PU_DATE");
					if (!GolfUtil.isNull(pu_date)) pu_date = DateUtil.format(pu_date, "yyyyMMdd", "yyyy.MM.dd.");
					result.addString("PU_DATE"			,pu_date);
					result.addString("PU_TIME" 			,rs.getString("PU_TIME") );					// �Ⱦ��ð�
					String scr = rs.getString("PU_TIME");
					String fWord = scr.substring(0,2); 
					String lWord = scr.substring(2,4); 
					String sWord = fWord+":"+lWord;
					result.addString("PTIME"  			,sWord );
					String tee_date = rs.getString("TEOF_DATE");
					if (!GolfUtil.isNull(tee_date)) tee_date = DateUtil.format(tee_date, "yyyyMMdd", "yyyy.MM.dd.");
					result.addString("TEE_DATE"			,tee_date);
					result.addString("TEE_TIME" 		,rs.getString("TEOF_TIME") );
					String scr2 = rs.getString("TEOF_TIME");
					String fWord2 = scr2.substring(0,2); 
					String lWord2 = scr2.substring(2,4); 
					String sWord2 = fWord2+":"+lWord2;
					result.addString("TTIME"  			,sWord2 );
					result.addString("GF_NM" 			,rs.getString("GREEN_NM") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );		// �������			
					result.addString("HG_NM" 		    ,rs.getString("HAN_NM") );			// �̸�
					result.addString("CAR_KND_NM" 		,rs.getString("CAR_KND_NM") );		// ����
					result.addString("NORM_PRIC" 		,rs.getString("NORM_PRIC") );		// ������ �ݾ�
					result.addString("PCT20_DC_PRIC" 	,rs.getString("PCT20_DC_PRIC") );	// ������ �ݾ� 20%����
					result.addString("PCT30_DC_PRIC" 	,rs.getString("PCT30_DC_PRIC") );	// ������ �ݾ� 30%����
					result.addString("COUNS_YN" 		,rs.getString("CSLT_YN") );			// ��㿩��
					result.addString("PROG_TYPE" 		,rs.getString("PGRS_YN") );			// ���࿩��
					result.addString("STTL_STAT_CLSS" 		,rs.getString("STTL_STAT_CLSS") );			// ��������
					
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );     	// �� �����
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") ); 
					result.addString("RNUM"				,rs.getString("RNUM") );			// ���� ������ �����
					result.addString("RESULT", "00"); 										//������
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
    private String getSelectQuery(String search_sel, String search_word, String sckd_code, String scnsl_yn, String sprgs_yn, String search_dt1, String search_dt2, String scoop_cp_cd){
        StringBuffer sql = new StringBuffer();
                
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			APLC_SEQ_NO, ");
		sql.append("\n 	DECODE( PGRS_YN 	");
		sql.append("\n 		,'D', '�����Ϸ�'	");
		sql.append("\n 		,'P', '��������'	");
		sql.append("\n 		,'W', '�����'	"); 
		sql.append("\n 		,'', '�����ϱ�'	"); 
		sql.append("\n 	) PGRS_YN,	");
		
		sql.append("\n 	DECODE( CSLT_YN 	");
		sql.append("\n 		,'Y', '�Ϸ�'	");
		sql.append("\n 		,'N', '<font color=red>��Ȯ��</font>'	"); 
		sql.append("\n 	) CSLT_YN,	");
		
		sql.append("\n 			CDHD_ID, EMAIL, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,GOLF_LMS_CAR_KND_CLSS,");
		sql.append("\n 			PU_DATE, trim(PU_TIME)PU_TIME, TEOF_DATE, trim(TEOF_TIME)TEOF_TIME, GREEN_NM, REG_ATON, CHNG_ATON, HAN_NM, CAR_KND_NM, NORM_PRIC, PCT20_DC_PRIC, PCT30_DC_PRIC, STTL_STAT_CLSS, ");//USERCLSS,
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");	 
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		
		sql.append("\n 			APLC_SEQ_NO,  PGRS_YN, CSLT_YN, CDHD_ID, EMAIL, DDD_NO, TEL_HNO,");
		sql.append("\n 			TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, GOLF_LMS_CAR_KND_CLSS, PU_DATE, trim(PU_TIME)PU_TIME, TEOF_DATE, ");
		sql.append("\n 			trim(TEOF_TIME)TEOF_TIME, GREEN_NM,  	");
		sql.append("\n 			TO_CHAR(TO_DATE(substr(REG_ATON,1,8)), 'YY-MM-DD')||'('||substr(to_char(to_date(substr(REG_ATON,1,8),'yyyymmdd'),'DAY'),1,1)||')'||' '||substr(substr(REG_ATON,9,10),1,2)||':'||substr(substr(REG_ATON,11,12),1,2) REG_ATON, 	");
		sql.append("\n 			CHNG_ATON, 	");
		
		sql.append("\n  (SELECT HG_NM FROM BCDBA.TBGGOLFCDHD  WHERE CDHD_ID=TGL.CDHD_ID) AS HAN_NM,   ");
		sql.append("\n  (SELECT CAR_KND_NM FROM BCDBA.TBGAMTMGMT  WHERE CAR_KND_CLSS=TGL.GOLF_LMS_CAR_KND_CLSS) AS CAR_KND_NM,   ");			//����
		sql.append("\n  (SELECT NORM_PRIC FROM BCDBA.TBGAMTMGMT  WHERE CAR_KND_CLSS=TGL.GOLF_LMS_CAR_KND_CLSS) AS NORM_PRIC,   ");			//������ �ݾ�
		sql.append("\n  (SELECT PCT20_DC_PRIC FROM BCDBA.TBGAMTMGMT  WHERE CAR_KND_CLSS=TGL.GOLF_LMS_CAR_KND_CLSS) AS PCT20_DC_PRIC,   ");	//������ �ݾ� 20%����
		sql.append("\n  (SELECT PCT30_DC_PRIC FROM BCDBA.TBGAMTMGMT  WHERE CAR_KND_CLSS=TGL.GOLF_LMS_CAR_KND_CLSS) AS PCT30_DC_PRIC,   ");	//������ �ݾ� 30%����
		
		sql.append("\n  (SELECT STTL_STAT_CLSS FROM BCDBA.TBGSTTLMGMT  WHERE STTL_GDS_SEQ_NO=TGL.APLC_SEQ_NO) AS STTL_STAT_CLSS   ");	//��������

		sql.append("\n 			FROM 	");
		sql.append("\n 			BCDBA.TBGAPLCMGMT	TGL");
		sql.append("\n 			WHERE APLC_SEQ_NO = APLC_SEQ_NO	");
		sql.append("\n 			AND CDHD_ID = ?	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TGL.CDHD_ID LIKE (SELECT CDHD_ID FROM BCDBA.TBGGOLFCDHD  WHERE HAN_NM like ?)	");
				sql.append("\n 				OR ADDR LIKE ? )	");				
			} else {
				if (search_sel.equals("HAN_NM")) {
					sql.append("\n 				AND TGL.CDHD_ID LIKE (SELECT CDHD_ID FROM BCDBA.TBGGOLFCDHD  WHERE HAN_NM like ?)	");			
				} else {
					sql.append("\n 				AND "+search_sel+" LIKE ?	");
				}
			}
		}

		sql.append("\n 	AND GOLF_SVC_APLC_CLSS = ?	");
		
		if (!GolfUtil.isNull(sckd_code))sql.append("\n AND GOLF_LMS_CAR_KND_CLSS = ? ");		
		if (!GolfUtil.isNull(scnsl_yn))	sql.append("\n AND CSLT_YN = ? ");		
		if (!GolfUtil.isNull(sprgs_yn))	sql.append("\n AND PGRS_YN = ? ");
		if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	sql.append("\n AND (REG_ATON >= ? AND REG_ATON <= ?) ");		

		sql.append("\n 				ORDER BY APLC_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
