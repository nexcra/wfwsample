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

package com.bccard.golf.dbtao.proc.widget;

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
 * 
 ******************************************************************************/
public class GolfWidgetXmlListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfWidgetXmlListDaoProc() {}	

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
			String scoop_cp_cd		= "0002";//data.getString("SCOOP_CP_CD"); //������ ����
			
			
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
					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") ); 	// �޴���ȭ ����ȣ
					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") ); 	// �޴���ȭ ����ȣ
					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") ); 	// �޴���ȭ �Ϸù�ȣ
					result.addString("CSTMR_ID" 		,rs.getString("CDHD_ID") ); 	// ��û��
					
					result.addString("CAR_TYPE_CD" 		,rs.getString("GOLF_LMS_CAR_KND_CLSS") );	// �����ڵ�
					
					String pu_date = rs.getString("PU_DATE");
					if (!GolfUtil.isNull(pu_date)) pu_date = DateUtil.format(pu_date, "yyyyMMdd", "yyyy�� MM�� dd��");
					result.addString("PU_DATE"		,pu_date);
					
					result.addString("PU_TIME" 			,rs.getString("PU_TIME") );		// �Ⱦ��ð�
					
					String tee_date = rs.getString("TEOF_DATE");
					if (!GolfUtil.isNull(tee_date)) tee_date = DateUtil.format(tee_date, "yyyyMMdd", "yyyy�� MM�� dd��");
					result.addString("TEE_DATE"		,tee_date);
					
					result.addString("TEE_TIME" 		,rs.getString("TEOF_DATE") );
					
					
					result.addString("GF_NM" 		,rs.getString("GREEN_NM") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );	// �������			
					//result.addString("CORR_ATON" 		,DateUtil.format(rs.getString("CORR_ATON"), "yyyyMMdd", "yyyy-MM-dd") );	//��������
					//result.addString("STTL_AMT" 		,rs.getString("STTL_AMT") );	// �����ݾ�
/*
					String STTL_DATE = rs.getString("STTL_DATE");
					if (!GolfUtil.isNull(STTL_DATE)) STTL_DATE = DateUtil.format(STTL_DATE, "yyyyMMdd", "yyyy�� MM�� dd��");
					result.addString("STTL_DATE"		,STTL_DATE);
					
					String REPY_DATE = rs.getString("REPY_DATE");
					if (!GolfUtil.isNull(REPY_DATE)) REPY_DATE = DateUtil.format(REPY_DATE, "yyyyMMdd", "yyyy�� MM�� dd��");
					result.addString("REPY_DATE"		,REPY_DATE);
*/					
					result.addString("HG_NM" 		    ,rs.getString("HAN_NM") );		// �̸�
					//result.addString("USERCLSS" 		,rs.getString("USERCLSS") );	// ���
					result.addString("COUNS_YN" 		,rs.getString("CSLT_YN") );	// ��㿩��
					result.addString("PROG_TYPE" 		,rs.getString("PGRS_YN") );	// ���࿩��
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );     // �� �����
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") ); 
					result.addString("RNUM"				,rs.getString("RNUM") );		// ���� ������ �����
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
    private String getSelectQuery(String search_sel, String search_word, String sckd_code, String scnsl_yn, String sprgs_yn, String search_dt1, String search_dt2, String scoop_cp_cd){
        StringBuffer sql = new StringBuffer();
                
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			APLC_SEQ_NO, ");
		sql.append("\n 	DECODE( PGRS_YN 	");
		sql.append("\n 		,'D', '<font color=red><b>�����Ϸ�</b></font>'	");
		sql.append("\n 		,'C', '<font color=red><b>�������</b></font>'	"); 
		sql.append("\n 		,'P', '������û'	");
		sql.append("\n 		,'W', '<font color=red><b>ȯ�ҿϷ�</b></font>'	"); 
		sql.append("\n 		,'', '�����'	");
		sql.append("\n 	) PGRS_YN,	");
		
		sql.append("\n 	DECODE( CSLT_YN 	");
		sql.append("\n 		,'Y', '�Ϸ�'	");
		sql.append("\n 		,'N', '<font color=red>��Ȯ��</font>'	"); 
		sql.append("\n 	) CSLT_YN,	");
		
		sql.append("\n 			CDHD_ID, EMAIL, DDD_NO, TEL_HNO, TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,");
		sql.append("\n 	DECODE( GOLF_LMS_CAR_KND_CLSS 	");
		sql.append("\n 		,'0001', '�׷���ī�Ϲ�'	");
		sql.append("\n 		,'0002', '����'	"); 
		sql.append("\n 		,'0003', '�̴Ϲ���ī��Ƽ'	"); 
		sql.append("\n 		,'0004', '��Ÿũ����Ʈ'	"); 
		sql.append("\n 	) GOLF_LMS_CAR_KND_CLSS,	");
		sql.append("\n 			PU_DATE, PU_TIME, TEOF_DATE, TEOF_TIME, GREEN_NM, REG_ATON, CHNG_ATON, HAN_NM, ");//USERCLSS,
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) AS LIST_NO  	");	//���ο� no������ 
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		
		sql.append("\n 				APLC_SEQ_NO,  PGRS_YN, CSLT_YN, CDHD_ID, EMAIL, DDD_NO, TEL_HNO,");
		sql.append("\n 				TEL_SNO, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, GOLF_LMS_CAR_KND_CLSS, PU_DATE, PU_TIME, TEOF_DATE, ");
		sql.append("\n 				TEOF_TIME, GREEN_NM,  	");
		sql.append("\n 				TO_CHAR(TO_DATE(substr(REG_ATON,1,8)), 'YY-MM-DD')||'('||substr(to_char(to_date(substr(REG_ATON,1,8),'yyyymmdd'),'DAY'),1,1)||')'||' '||substr(substr(REG_ATON,9,10),1,2)||':'||substr(substr(REG_ATON,11,12),1,2) REG_ATON, 	");
		sql.append("\n 				CHNG_ATON, 	");
		
		sql.append("\n  (SELECT HG_NM FROM BCDBA.TBGGOLFCDHD  WHERE CDHD_ID=TGL.CDHD_ID) AS HAN_NM   ");//USER���̺��� CDHD_ID�� �ѱ��̸� ����
		//sql.append("\n  (SELECT '����ȸ��(VIP)' FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID=TGL.CDHD_ID) AS USERCLSS  ");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAPLCMGMT	TGL");
		sql.append("\n 				WHERE APLC_SEQ_NO = APLC_SEQ_NO	");
		sql.append("\n 				AND CDHD_ID = ?	");
		
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TGL.CDHD_ID LIKE (SELECT CDHD_ID FROM BCDBA.TBGGOLFCDHD  WHERE HAN_NM like ?)	");
				sql.append("\n 				OR TGL.ZIPADDR LIKE ? )	");				
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
