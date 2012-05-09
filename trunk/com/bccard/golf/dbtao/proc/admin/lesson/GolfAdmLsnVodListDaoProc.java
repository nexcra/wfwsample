/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmLsnVodListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ���¿�
*   ����      : ������ ���������� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-05-20
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.lesson;

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
public class GolfAdmLsnVodListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLsnVodListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmLsnVodListDaoProc() {}	

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
			String svod_clss		= data.getString("SVOD_CLSS");
			String svod_lsn_clss	= data.getString("SVOD_LSN_CLSS");
			String pre_yn			= data.getString("PRE_YN");		// �����̾������󱸺� (Y:�����̾�������, N:�Ϲݵ�����)
			if (GolfUtil.isNull(pre_yn))	pre_yn = "N";

			String sql = this.getSelectQuery(search_sel, search_word, svod_clss, svod_lsn_clss, pre_yn);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString()); 
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
			if (!GolfUtil.isNull(svod_clss))		pstmt.setString(++idx, svod_clss);
			if (!GolfUtil.isNull(svod_lsn_clss))	pstmt.setString(++idx, svod_lsn_clss);
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("SEQ_NO" 			,rs.getLong("GOLF_MVPT_SEQ_NO") );
					result.addString("VOD_CLSS" 		,rs.getString("GOLF_MVPT_CLSS") );
					result.addString("VOD_LSN_CLSS" 	,rs.getString("GOLF_MVPT_LESN_CLSS") );
					result.addString("TITL" 			,rs.getString("TITL") );
					result.addString("BEST_YN" 			,rs.getString("BEST_YN") );
					result.addString("NEW_YN" 			,rs.getString("ANW_BLTN_ARTC_YN") );
					result.addLong("INQR_NUM" 			,rs.getLong("INQR_NUM") );
					result.addString("REG_ATON"			,rs.getString("REG_ATON") );
					result.addString("VOD_CLSS_NM"		,rs.getString("GOLF_MVPT_CLSS_NM") );
					result.addString("VOD_LSN_CLSS_NM"	,rs.getString("GOLF_MVPT_LESN_CLSS_NM") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					art_num_no++;
					
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
    private String getSelectQuery(String search_sel, String search_word, String svod_clss, String svod_lsn_clss, String pre_yn){
        StringBuffer sql = new StringBuffer();
        
        String clss_code = "";	//�����󱸺��ڵ�
        String lsn_code = "";	//�����з��ڵ�
        
        if(pre_yn.equals("Y"))	clss_code = "0045";
        else					clss_code = "0007";
  
        if(pre_yn.equals("Y"))	lsn_code = "0046";
        else					lsn_code = "0013";
        
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 			GOLF_MVPT_SEQ_NO, GOLF_MVPT_CLSS, GOLF_MVPT_LESN_CLSS, TITL, BEST_YN, ANW_BLTN_ARTC_YN, INQR_NUM, REG_ATON, 	");
		sql.append("\n 			GOLF_MVPT_CLSS_NM, GOLF_MVPT_LESN_CLSS_NM,	");
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\t 			((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  ");
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		sql.append("\n 				TGV.GOLF_MVPT_SEQ_NO, TGV.GOLF_MVPT_CLSS, TGV.GOLF_MVPT_LESN_CLSS, TGV.TITL, TGV.BEST_YN, TGV.ANW_BLTN_ARTC_YN, TGV.INQR_NUM, TO_CHAR(TO_DATE(TGV.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY-MM-DD') REG_ATON, 	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='" + clss_code + "' AND GOLF_CMMN_CODE=TGV.GOLF_MVPT_CLSS) GOLF_MVPT_CLSS_NM, 	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_URNK_CMMN_CODE='" + lsn_code + "' AND GOLF_CMMN_CODE=TGV.GOLF_MVPT_LESN_CLSS) GOLF_MVPT_LESN_CLSS_NM 	");
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGMVPTMGMT TGV	");
		sql.append("\n 				WHERE TGV.GOLF_MVPT_SEQ_NO = TGV.GOLF_MVPT_SEQ_NO	");
			
		if (!GolfUtil.isNull(search_word)) {
			if (search_sel.equals("ALL")) {
				sql.append("\n 				AND (TGV.TITL LIKE ?	");
				sql.append("\n 				OR TGV.CTNT LIKE ? )	");				
			} else {
				sql.append("\n 				AND "+search_sel+" LIKE ?	");
			}
		}
		if (!GolfUtil.isNull(svod_clss)) 		sql.append("\n 		AND TGV.GOLF_MVPT_CLSS = ?	");
		if (!GolfUtil.isNull(svod_lsn_clss)) 	sql.append("\n 		AND TGV.GOLF_MVPT_LESN_CLSS = ?	");
		if (!GolfUtil.isNull(pre_yn)){
			if(pre_yn.equals("Y"))		sql.append("\n 		AND TGV.PMI_MVPT_YN = 'Y'	");	
			else 						sql.append("\n 		AND (TGV.PMI_MVPT_YN IS NULL OR TGV.PMI_MVPT_YN = '' OR TGV.PMI_MVPT_YN = 'N') ");
		}

		sql.append("\n 				ORDER BY TGV.GOLF_MVPT_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");		

		return sql.toString();
    }
}
