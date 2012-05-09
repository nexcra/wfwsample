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
public class GolfAdmManiaListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmManiaListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmManiaListDaoProc() {}	

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
			String scoop_cp_cd		= data.getString("SCOOP_CP_CD"); 	//������ ����
			String mem_clss			= data.getString("MEM_CLSS"); 		//ȸ����� ���� 
			
			String sql = this.getSelectQuery(search_sel, search_word, sckd_code, scnsl_yn, sprgs_yn, search_dt1, search_dt2, scoop_cp_cd, mem_clss);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			//pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			//pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));	
			//pstmt.setLong(++idx, data.getLong("PAGE_NO"));	
			//pstmt.setLong(++idx, data.getLong("scoop_cp_cd"));
			if (!GolfUtil.isNull(scoop_cp_cd))	pstmt.setString(++idx, scoop_cp_cd);
			if (!GolfUtil.isNull(sckd_code))	pstmt.setString(++idx, sckd_code);
			if (!GolfUtil.isNull(scnsl_yn))		pstmt.setString(++idx, scnsl_yn);
			if (!GolfUtil.isNull(sprgs_yn))		pstmt.setString(++idx, sprgs_yn);
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, search_dt1+"000000");
			if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	pstmt.setString(++idx, search_dt2+"999999");
			
			//�����˻�����
			if (!GolfUtil.isNull(search_word)) {
				if (search_sel.equals("ALL")) {
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");	
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
				}else if(search_sel.equals("HG_NM")){
					pstmt.setString(++idx, "%"+search_word+"%");
				}else if(search_sel.equals("HP_TEL_HNO")){
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
				}else{
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			if (!GolfUtil.isNull(mem_clss))	pstmt.setString(++idx, mem_clss);
		
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("RESULT", "00"); //������
					result.addInt("RECV_NO" 			,rs.getInt("APLC_SEQ_NO") );
					result.addString("HP_DDD_NO" 		,rs.getString("HP_DDD_NO") ); 	// �޴���ȭ ����ȣ
					result.addString("HP_TEL_HNO" 		,rs.getString("HP_TEL_HNO") ); 	// �޴���ȭ ����ȣ
					result.addString("HP_TEL_SNO" 		,rs.getString("HP_TEL_SNO") ); 	// �޴���ȭ �Ϸù�ȣ
					
					result.addString("CSTMR_ID" 		,rs.getString("CDHD_ID") ); 	// ��û��
				
					result.addString("PU_DATE"			,rs.getString("PU_DATE"));
					result.addString("PU_TIME" 			,rs.getString("PU_TIME") );		// �Ⱦ��ð�
					result.addString("TEOF_DATE"		,rs.getString("TEOF_DATE"));
					result.addString("TEOF_TIME" 		,rs.getString("TEOF_TIME") );

					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );	// �������		
					result.addString("STTL_ATON" 		,rs.getString("STTL_ATON") );	// ��������		
					result.addString("CNCL_ATON" 		,rs.getString("CNCL_ATON") );	// �������		
				
					result.addString("HG_NM" 		    ,rs.getString("HG_NM") );		// �̸�
					result.addString("USERCLSS" 		,rs.getString("GOLF_CMMN_01") );	// ���
					result.addString("USERCLSS2" 		,rs.getString("GOLF_CMMN_02") );	// ���
					result.addString("CAR_KND_NM" 		,rs.getString("CAR_KND_NM") );	// ������
					result.addString("CAR_PRIC"			,rs.getString("CAR_STTL_ATM"));	// ���� �ݾ�
					result.addString("STTL_AMT" 		,rs.getString("STTL_AMT") );	// �����ݾ�
					result.addString("STTL_STAT_CLSS" 	,rs.getString("STTL_STAT_CLSS") );	//����/��ҿ���
					

					result.addString("COUNS_YN" 		,rs.getString("CSLT_YN") );	// ��㿩��
					result.addString("PROG_TYPE" 		,rs.getString("PGRS_YN") );	// ���࿩��
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );     // �� �����
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					//result.addString("LIST_NO"		,rs.getString("LIST_NO") ); 
					result.addString("RNUM"				,rs.getString("RNUM") );		// ���� ������ �����
					
					
					
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
    * Query�� �����Ͽ� �����Ѵ�. :paging   
    ************************************************************************ */
    private String getSelectQuery(String search_sel, String search_word, String sckd_code, String scnsl_yn, String sprgs_yn, String search_dt1, String search_dt2, String scoop_cp_cd, String mem_clss){
        StringBuffer sql = new StringBuffer();
                
        sql.append("\n SELECT	*								");
		sql.append("\n FROM	(SELECT	ROWNUM RNUM,				");
		sql.append("\n 				APLC_SEQ_NO,					");
		sql.append("\n 				HG_NM,CDHD_ID, CDHD_SQ2_CTGO, CAR_SEQ_NO,				");
		sql.append("\n 				HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO,						");
		sql.append("\n 				GOLF_CMMN_01, GOLF_CMMN_02, 						");
		sql.append("\n 				REG_ATON,				");
		sql.append("\n 				CSLT_YN, PGRS_YN,			");
		sql.append("\n 				PU_DATE, PU_TIME, 					");
		sql.append("\n 				TEOF_DATE,	TEOF_TIME, 				");
		sql.append("\n 				CAR_KND_NM, 				");
		sql.append("\n 				CAR_STTL_ATM, 				");
		sql.append("\n 				STTL_AMT, STTL_STAT_CLSS,			");
		sql.append("\n 				STTL_ATON, 				");
		sql.append("\n 				CNCL_ATON, 				");
		sql.append("\n 				CEIL(ROWNUM/10) AS PAGE,	");
		sql.append("\n 				MAX(RNUM) OVER() TOT_CNT	");	
		sql.append("\n 		FROM	(SELECT	ROWNUM AS RNUM		");
		sql.append("\n 						, TBG1.APLC_SEQ_NO, TBG2.HG_NM, TBG2.CDHD_ID, TBG1.HP_DDD_NO, TBG1.HP_TEL_HNO, TBG1.HP_TEL_SNO			");
		sql.append("\n 						, TBG6.GOLF_CMMN_CODE_NM as GOLF_CMMN_01, TBG7.GOLF_CMMN_CODE_NM as GOLF_CMMN_02					");
		sql.append("\n 						, to_char(to_date(TBG1.REG_ATON,'yyyymmddhh24miss'), 'yyyy-mm-dd hh24:mi') AS REG_ATON    ");
		sql.append("\n 						, TBG1.CSLT_YN, TBG1.PGRS_YN, TO_NUMBER(TBG5.CDHD_SQ2_CTGO)AS CDHD_SQ2_CTGO				");
		sql.append("\n 						, TBG1.PU_DATE, TBG1.PU_TIME, TBG1.TEOF_DATE, TBG1.TEOF_TIME, TBG4.CAR_KND_NM				");		
		sql.append("\n 						, TBG8.STTL_AMT	,TBG4.SEQ_NO AS CAR_SEQ_NO ,TBG8.STTL_STAT_CLSS			");
		sql.append("\n 						, to_char(to_date(TBG8.STTL_ATON,'yyyymmddhh24miss'), 'yyyy-mm-dd hh24:mi') AS STTL_ATON			");
		sql.append("\n 						, to_char(to_date(TBG8.CNCL_ATON,'yyyymmddhh24miss'), 'yyyy-mm-dd hh24:mi') AS CNCL_ATON				");
		sql.append("\n						, (CASE WHEN to_number(TBG5.CDHD_SQ2_CTGO) = 1 THEN TBG4.PCT30_DC_PRIC 									");
		sql.append("\n								WHEN to_number(TBG5.CDHD_SQ2_CTGO)= 2 OR to_number(TBG5.CDHD_SQ2_CTGO)= 5 OR					");
		sql.append("\n								 	 to_number(TBG5.CDHD_SQ2_CTGO)= 6 OR to_number(TBG5.CDHD_SQ2_CTGO)= 7 THEN 	TBG4.PCT20_DC_PRIC	");
		sql.append("\n							ELSE TBG4.NORM_PRIC END) AS CAR_STTL_ATM									");
		sql.append("\n 				from BCDBA.TBGAPLCMGMT TBG1			");
		sql.append("\n 				join BCDBA.TBGGOLFCDHD TBG2 on TBG1.CDHD_ID = TBG2.CDHD_ID			");
		sql.append("\n 				join BCDBA.TBGGOLFCDHDCTGOMGMT TBG5 on TBG5.CDHD_CTGO_SEQ_NO = TBG2.CDHD_CTGO_SEQ_NO		");
		sql.append("\n 				join BCDBA.TBGCMMNCODE TBG6 on TBG5.CDHD_SQ1_CTGO = TBG6.GOLF_CMMN_CODE and TBG6.GOLF_CMMN_CLSS='0001'			");
		sql.append("\n 				join BCDBA.TBGCMMNCODE TBG7 on TBG5.CDHD_SQ2_CTGO = TBG7.GOLF_CMMN_CODE and TBG7.GOLF_CMMN_CLSS='0005'			");
		sql.append("\n 				left join BCDBA.TBGAMTMGMT TBG4 on  TBG1.GOLF_LMS_CAR_KND_CLSS = TBG4.CAR_KND_CLSS			");
		sql.append("\n 				left join BCDBA.TBGSTTLMGMT TBG8 on TBG1.APLC_SEQ_NO = TBG8.STTL_GDS_SEQ_NO			");
		sql.append("\n 				where TBG1.GOLF_SVC_APLC_CLSS =  ?						");
		
		if (!GolfUtil.isNull(sckd_code))sql.append("\n AND TBG1.GOLF_LMS_CAR_KND_CLSS = ? ");	
		if (!GolfUtil.isNull(scnsl_yn))	sql.append("\n AND TBG1.CSLT_YN = ? ");		
		//���࿩�ΰ���
		if (!GolfUtil.isNull(sprgs_yn))	{
			//���� ����϶�
			if("Y".equals(sprgs_yn)){
				sql.append("\n AND TBG8.STTL_STAT_CLSS = ?");
			//�����Ϸ��϶�, ������Ұ��� ������
			}else if("D".equals(sprgs_yn)){
				sql.append("\n AND TBG1.PGRS_YN = ?  AND TBG8.STTL_STAT_CLSS = 'N'	");
			}else{
				sql.append("\n AND TBG1.PGRS_YN = ? ");
			}
		}
		if (!GolfUtil.isNull(search_dt1) && !GolfUtil.isNull(search_dt2) )	sql.append("\n AND (TBG1.REG_ATON >= ? AND TBG1.REG_ATON <= ?) ");
		//�����˻�����
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TBG2.HG_NM LIKE  ?	");
				sql.append("\n 				OR TBG1.ADDR LIKE ?  	");			
				sql.append("\n 				OR (TBG1.HP_TEL_HNO LIKE ? OR TBG1.HP_DDD_NO LIKE ? OR TBG1.HP_TEL_SNO LIKE ?)  )	");	
			} else {
				
				
			if (search_sel.equals("HG_NM")) {
				sql.append("\n 				AND TBG2.HG_NM LIKE  ?	");	
			} else if(search_sel.equals("HP_TEL_HNO")){
				sql.append("\n 				AND (TBG1.HP_TEL_HNO LIKE ? OR TBG1.HP_DDD_NO LIKE ? OR TBG1.HP_TEL_SNO LIKE ?) 	");
			}else{
				sql.append("\n 				AND TBG1."+search_sel+" LIKE  ?	");	
			}
				
				
			}
		}
		
		if (!GolfUtil.isNull(mem_clss))sql.append("\n AND TBG7.GOLF_CMMN_CODE = ? ");	
		
		sql.append("\n 				ORDER BY APLC_SEQ_NO DESC	");		
		sql.append("\n 				)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
    
    
	
     
}
