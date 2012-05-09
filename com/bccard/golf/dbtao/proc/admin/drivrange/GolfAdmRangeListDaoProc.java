/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmRangeListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ������ �帲 ���������� ��û ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-05-22
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
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

/******************************************************************************
 * Topn
 * @author	����Ŀ�´����̼�
 * @version	1.0
 ******************************************************************************/
public class GolfAdmRangeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmRangeListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmRangeListDaoProc() {}	

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
			
			String start_dt		= data.getString("START_DT");
			String end_dt		= data.getString("END_DT");
			String rsvt_yn		= data.getString("RSVT_YN");
			String atd_yn		= data.getString("ATD_YN");
			String sch_gr			= data.getString("SCH_GR_SEQ_NO");
			String search_sel		= data.getString("SEARCH_SEL");
			String search_word		= data.getString("SEARCH_WORD");
			
			String sql = this.getSelectQuery(start_dt,end_dt,rsvt_yn,atd_yn,search_sel,search_word, sch_gr);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			
			if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // ������ �˻�				
				if (!GolfUtil.isNull(sch_gr)){
					pstmt.setString(++idx, sch_gr);
				}
				pstmt.setString(++idx, start_dt);
				pstmt.setString(++idx, end_dt);
			}
			
			if (!GolfUtil.isNull(rsvt_yn)) pstmt.setString(++idx, rsvt_yn);  // ���±��� �˻�
			
			if (!GolfUtil.isNull(atd_yn)) pstmt.setString(++idx, atd_yn);  // �������� �˻�
			
			if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //�����˻�
				
				if (search_sel.equals("ALL")){ //��ü
					pstmt.setString(++idx, "%"+search_word+"%");
					pstmt.setString(++idx, "%"+search_word+"%");
					
				} else if (search_sel.equals("GOLF_SVC_RSVT_NO")){ //�����ȣ
					pstmt.setString(++idx, "%"+search_word+"%");
					
				} else if (search_sel.equals("HG_NM")){ //�̸�
					pstmt.setString(++idx, "%"+search_word+"%");
					
				} else {
					pstmt.setString(++idx, "%"+search_word+"%");
				}
			}
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addString("RSVT_SQL_NO" 			,rs.getString("GOLF_SVC_RSVT_NO") );
					result.addString("HAN_NM" 				,rs.getString("HG_NM") );
					result.addString("JUMIN" 				,rs.getString("JUMIN") );
					result.addString("GRADE_NM" 				,rs.getString("GRADE_NM") );
					result.addString("HP_DDD_NO"			,rs.getString("HP_DDD_NO") );
					result.addString("HP_TEL_HNO"			,rs.getString("HP_TEL_HNO") );
					result.addString("HP_TEL_SNO"			,rs.getString("HP_TEL_SNO") );
					result.addString("RSVT_DATE"			,rs.getString("RSVT_ABLE_DATE") );
					result.addString("RSVT_TIME"			,rs.getString("RSVT_TIME") );
					result.addString("REG_ATON"				,rs.getString("REG_ATON") );
					result.addString("RSVT_YN"				,rs.getString("RSVT_YN") );
					result.addString("ATD_YN"				,rs.getString("ATTD_YN") );
					result.addString("GR_NM"				,rs.getString("GR_NM") );
										
					result.addString("TOTAL_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"			,rs.getString("PAGE") );
										
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
    private String getSelectQuery(String start_dt, String end_dt, String rsvt_yn, String atd_yn, String search_sel, String search_word, String sch_gr){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			GOLF_SVC_RSVT_NO, HG_NM, SUBSTR (JUMIN, 1, 6) ||'-*******' JUMIN,GRADE_NM, HP_DDD_NO, HP_TEL_HNO, HP_TEL_SNO, RSVT_ABLE_DATE, RSVT_TIME, REG_ATON, RSVT_YN, ATTD_YN, GR_NM,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT	");
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			TGR.GOLF_SVC_RSVT_NO, TGU.HG_NM,	");
		sql.append("\n 			TGU.JUMIN_NO JUMIN,T8.GOLF_CMMN_CODE_NM AS GRADE_NM,  	");
		sql.append("\n 			TGR.HP_DDD_NO, TGR.HP_TEL_HNO, TGR.HP_TEL_SNO,  	");
		//sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'YYYY-MM-DD') RSVT_ABLE_DATE, 	");
		sql.append("\n 			TGRD.RSVT_ABLE_DATE, 	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24:MI') || '~' || TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24:MI') RSVT_TIME, 	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') || ' ' || TO_CHAR (TO_DATE (TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'HH24:MI') REG_ATON, 	");
		sql.append("\n 			DECODE (TGR.RSVT_YN, 'Y', '<font color=red>����</font>', 'N', '<font color=blue>���</font>') RSVT_YN, 	");
		sql.append("\n 			DECODE (TGR.ATTD_YN, 'Y', '<font color=red>����</font>', 'N', '<font color=blue>������</font>') ATTD_YN 	");
		sql.append("\n 			, T9.GREEN_NM GR_NM 	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT, BCDBA.TBGGOLFCDHD TGU	");
		sql.append("\n				,BCDBA.TBGGOLFCDHD  T6,BCDBA.TBGGOLFCDHDCTGOMGMT T7,BCDBA.TBGCMMNCODE  T8, BCDBA.TBGAFFIGREEN T9 ");
		sql.append("\n 				WHERE TGU.CDHD_ID=TGR.CDHD_ID(+)	");
		sql.append("\n				AND T6.CDHD_ID=TGR.CDHD_ID	");
		sql.append("\n				AND T6.CDHD_CTGO_SEQ_NO=T7.CDHD_CTGO_SEQ_NO");
		sql.append("\n				AND T7.CDHD_SQ2_CTGO=T8.GOLF_CMMN_CODE AND T8.GOLF_CMMN_CLSS='0005'");
		sql.append("\n 				AND TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
		sql.append("\n 				AND TGRT.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO	");
		sql.append("\n 				AND TGRD.GOLF_RSVT_DAY_CLSS = 'D'	");
		sql.append("\n 				AND TGRD.AFFI_GREEN_SEQ_NO = T9.AFFI_GREEN_SEQ_NO	");
		
		if (!GolfUtil.isNull(sch_gr)) sql.append("\n 	AND TGRD.AFFI_GREEN_SEQ_NO = ?	"); // ������
		
		if (!GolfUtil.isNull(start_dt) && !GolfUtil.isNull(end_dt)) { // ������ �˻�
			sql.append("\n 				AND TGRD.RSVT_ABLE_DATE BETWEEN ? AND ?	");
		}
		
		if (!GolfUtil.isNull(rsvt_yn)) sql.append("\n 	AND TGR.RSVT_YN = ?	"); // ���±��� �˻�
		
		if (!GolfUtil.isNull(atd_yn)) sql.append("\n 	AND TGR.ATTD_YN = ?	"); // �������� �˻�

		
		if (!GolfUtil.isNull(search_sel) && !GolfUtil.isNull(search_word)){ //�����˻�
			
			if (search_sel.equals("ALL")){ //��ü
				sql.append("\n 	AND (TGR.GOLF_SVC_RSVT_NO LIKE ? 	");
				sql.append("\n 	OR TGU.HG_NM LIKE ?	)	");
				
			} else if (search_sel.equals("GOLF_SVC_RSVT_NO")){ //�����ȣ
				sql.append("\n 	AND TGR.GOLF_SVC_RSVT_NO LIKE ?	");
				
			} else if (search_sel.equals("HG_NM")){ //�̸�
				sql.append("\n 	AND TGU.HG_NM LIKE ?	");
				
			} else {
				sql.append("\n 	AND "+search_sel+" LIKE ?	");
			}
		}
		
		sql.append("\n 				ORDER BY TGR.GOLF_SVC_RSVT_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");	
		return sql.toString();
    }
}
