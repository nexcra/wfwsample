/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmCouponInfoExcelDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �����μ��̷� �����ٿ�ε�
*   �������  : golf
*   �ۼ�����  : 2009-07-07
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.drivrange;

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
public class GolfAdmCouponInfoExcelDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmCouponInfoExcelDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmCouponInfoExcelDaoProc() {}	

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

			// ȸ���������̺� ���� �������� ����
			//��ȸ ----------------------------------------------------------
			String exec_type_cd		= data.getString("EXEC_TYPE_CD");
			String user_clss		= data.getString("USER_CLSS");
			String start_dt		= data.getString("START_DT");
			String end_dt		= data.getString("END_DT");
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			
			String sql = this.getSelectQuery(exec_type_cd,user_clss,start_dt,end_dt,search_sel,search_word);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (!GolfUtil.isNull(exec_type_cd)) pstmt.setString(++idx, exec_type_cd);  // ���� �˻�
			
			if (!GolfUtil.isNull(user_clss)) pstmt.setString(++idx, "%"+user_clss+"%"); // ȸ����� �˻�
			
			if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // ������� �˻�
				pstmt.setString(++idx, start_dt);
				pstmt.setString(++idx, end_dt);
			}
			
			if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //�����˻�
				
				if (search_sel.equals("ALL")){ //��ü
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					
				} else if (search_sel.equals("HG_NM")){ //�̸�
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("CDHD_ID")){ //���̵�
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("USERCLSS")){ //ȸ�����
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("CHG_TEL")){ //��ȭ��ȣ
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}  else if (search_sel.equals("GOLF_RNG_CUPN_SEQ_NO")){ //�Ϸù�ȣ
					pstmt.setString(++idx, "%"+search_word+"%");
					
				}	else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}

			//pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("CPNINF_SQL_NO" 			,rs.getLong("CUPN_HST_SEQ_NO") );
					result.addString("CPN_SERIAL" 		,rs.getString("GOLF_RNG_CUPN_SEQ_NO") );
					result.addString("EXEC_TYPE_CD" 		,rs.getString("GOLF_RNG_CLSS") );
					result.addString("GF_NM" 			,rs.getString("GREEN_NM") );
					result.addString("USERCLSS" 			,rs.getString("USERCLSS") );
					result.addString("HAN_NM" 			,rs.getString("HG_NM") );
					result.addString("PHONE" 			,rs.getString("PHONE") );
					result.addString("CPN_PT_ATON"			,rs.getString("CUPN_OUTP_ATON") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("LIST_NO"			,rs.getString("LIST_NO") );
										
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
    private String getSelectQuery(String exec_type_cd, String user_clss, String start_dt, String end_dt, String search_sel, String search_word){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			CUPN_HST_SEQ_NO, GOLF_RNG_CUPN_SEQ_NO, GOLF_RNG_CLSS, GREEN_NM, USERCLSS, HG_NM, PHONE, CUPN_OUTP_ATON,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			TGC.CUPN_HST_SEQ_NO, TGC.GOLF_RNG_CUPN_SEQ_NO, TMC1.GOLF_CMMN_CODE_NM GOLF_RNG_CLSS, TGF.GREEN_NM,   	");
		sql.append("\n 			DECODE (T.USERCLSS, '()', '��޾���', T.USERCLSS) USERCLSS,   	");
		sql.append("\n 			TGU.HG_NM ||'('|| TGU.CDHD_ID ||')' HG_NM,   	");
		sql.append("\n 			(SELECT PHONE FROM BCDBA.TBGGOLFCDHD WHERE CDHD_ID = TGU.CDHD_ID) PHONE,   	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGC.CUPN_OUTP_ATON, 'YYYYMMDDHH24MISS'), 'YY-MM-DD') ||'('|| SUBSTR (TO_CHAR (TO_DATE (TGC.CUPN_OUTP_ATON, 'YYYYMMDDHH24MISS'), 'DAY'), 1, 1) ||') ' || TO_CHAR (TO_DATE (TGC.CUPN_OUTP_ATON, 'YYYYMMDDHH24MISS'), 'HH24:MI') CUPN_OUTP_ATON 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGCUPNUSEHST TGC, BCDBA.TBGAFFIGREEN TGF, BCDBA.TBGGOLFCDHD TGU,		");
	
		sql.append("\n					(	");
		sql.append("\n 					SELECT CDHD_ID, SUBSTR(MAX(SYS_CONNECT_BY_PATH((GOLF_CMMN_CODE_NM1||'('||GOLF_CMMN_CODE_NM2||')'),',')),2) AS USERCLSS 	");
		sql.append("\n 					FROM  	");
		sql.append("\n 						( 	");
		sql.append("\n 							SELECT  	");
		sql.append("\n 								CDHD_ID, GOLF_CMMN_CODE_NM1, GOLF_CMMN_CODE_NM2, 	");
		sql.append("\n 								ROW_NUMBER() OVER(PARTITION BY CDHD_ID ORDER BY CDHD_ID) CNT 	");
		sql.append("\n 							FROM 	");
		sql.append("\n 								( 	");
		sql.append("\n 									SELECT  	");
		sql.append("\n 										TGU.CDHD_ID,TMC1.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM1, TMC2.GOLF_CMMN_CODE_NM GOLF_CMMN_CODE_NM2 	");
		sql.append("\n 									FROM 	");
		sql.append("\n 										BCDBA.TBGGOLFCDHD TGU, BCDBA.TBGGOLFCDHDGRDMGMT TGUC, BCDBA.TBGGOLFCDHDCTGOMGMT TGUD,  	");
		sql.append("\n 										(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0001') TMC1, 	");
		sql.append("\n 										(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0005') TMC2 	");
		sql.append("\n 										WHERE TGU.CDHD_ID=TGUC.CDHD_ID(+) 	");
		sql.append("\n 										AND TGUC.CDHD_CTGO_SEQ_NO=TGUD.CDHD_CTGO_SEQ_NO(+) 	");
		sql.append("\n 										AND TGUD.CDHD_SQ1_CTGO=TMC1.GOLF_CMMN_CODE(+) 	");
		sql.append("\n 										AND TGUD.CDHD_SQ2_CTGO=TMC2.GOLF_CMMN_CODE(+) 	");		
		sql.append("\n 								) 	");
		sql.append("\n 						) 	");
		sql.append("\n 					START WITH CNT=1 	");
		sql.append("\n 					CONNECT BY PRIOR CNT = CNT-1 	");
		sql.append("\n 					GROUP BY CDHD_ID 	");
		sql.append("\n					)T,	");
		
		sql.append("\n 						(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS='0008' AND USE_YN = 'Y') TMC1 	");
		sql.append("\n 						WHERE TGC.RNG_SEQ_NO = TGF.AFFI_GREEN_SEQ_NO 	");
		sql.append("\n 						AND TGC.CDHD_ID = TGU.CDHD_ID 	");
		sql.append("\n 						AND TGC.CDHD_ID = T.CDHD_ID 	");
		sql.append("\n 						AND TGF.GOLF_RNG_CLSS = TMC1.GOLF_CMMN_CODE(+) 	");
		sql.append("\t 							AND TGF.AFFI_FIRM_CLSS = '0003'	");	
		
		
		
		if (!GolfUtil.isNull(exec_type_cd)) sql.append("\n 	AND TGF.GOLF_RNG_CLSS = ?	"); // ���� �˻�
		
		if (!GolfUtil.isNull(user_clss)) sql.append("\n 		AND T.USERCLSS LIKE ?		"); // ȸ����� �˻�
		
		if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // ������� �˻�
			sql.append("\n 				AND TGC.CUPN_OUTP_ATON BETWEEN ? AND ?	");
		}
		
		if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //�����˻�
			
			if (search_sel.equals("ALL")){ //��ü
				sql.append("\n 	AND (TGU.HG_NM LIKE ? 	");
				sql.append("\n 	OR TGU.CDHD_ID LIKE ? 	");
				sql.append("\n 	OR T.USERCLSS LIKE ? 	");
				sql.append("\n 	OR TGF.DDD_NO || TGF.TEL_HNO || TGF.TEL_SNO LIKE ? 	");
				sql.append("\n 	OR TGC.GOLF_RNG_CUPN_SEQ_NO LIKE ?	)	");
				
			} else if (search_sel.equals("HG_NM")){ //�̸�
				sql.append("\n 	AND TGU.HG_NM LIKE ?	");
				
			}  else if (search_sel.equals("CDHD_ID")){ //���̵�
				sql.append("\n 	AND TGU.CDHD_ID LIKE ?	");
				
			}  else if (search_sel.equals("USERCLSS")){ //ȸ�����
				sql.append("\n 	AND T.USERCLSS LIKE ?		");
				
			}  else if (search_sel.equals("CHG_TEL")){ //��ȭ��ȣ
				sql.append("\n 	AND TGF.DDD_NO || TGF.TEL_HNO || TGF.TEL_SNO LIKE ?		");
				
			}  else if (search_sel.equals("GOLF_RNG_CUPN_SEQ_NO")){ //�Ϸù�ȣ
				sql.append("\n 	AND TGC.GOLF_RNG_CUPN_SEQ_NO LIKE ?	");
				
			}	else {
				sql.append("\n 	AND "+search_sel+" LIKE ?	");
			}
		}
		
		
		sql.append("\n 				ORDER BY TGC.CUPN_HST_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		//sql.append("\n WHERE PAGE = ?	");	
		
		return sql.toString();
    }
}
