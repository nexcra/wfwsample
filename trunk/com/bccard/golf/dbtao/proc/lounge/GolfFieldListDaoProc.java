/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfFieldListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : ���������� �ȳ� ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2009-06-04
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.lounge;

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
public class GolfFieldListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfFieldListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfFieldListDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data, String[] gf_hole_cd, String[] gf_area_cd ) throws BaseException {
		
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		String area_nm = "";
		boolean flag1 = false;
		boolean flag2 = false;
		
		try {
			conn = context.getDbConnection("default", null);
			 
			//��ȸ ----------------------------------------------------------
			
			String search_cd		= data.getString("SEARCH_CD");
			String gf_clss_cd		= data.getString("GF_CLSS_CD");
			String grnfee_mb		= data.getString("GRNFEE_MB");
			String grnfee_wk		= data.getString("GRNFEE_WK");
			String grnfee_wkend		= data.getString("GRNFEE_WKEND");
			long grnfee_amt1		= data.getLong("GRNFEE_AMT1");
			long grnfee_amt2		= data.getLong("GRNFEE_AMT2");
			String sido		= data.getString("SIDO");
			String gugun		= data.getString("GUGUN");
			String dong		= data.getString("DONG");
			String gf_nm		= data.getString("GF_NM");
			
			/*
			if (!GolfUtil.isNull(sido) && GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) { 
				area_nm = sido;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun ;
				
			} else if (!GolfUtil.isNull(sido) && !GolfUtil.isNull(gugun) && !GolfUtil.isNull(dong)) {
				area_nm = sido +" "+ gugun +" "+ dong ;
			}
			*/
			
			for (int i = 0; i < gf_hole_cd.length; i++) { 
				if (gf_hole_cd[i] != null && gf_hole_cd[i].length() > 0) {
					flag1 = true;
				}
			}
			
			for (int i = 0; i < gf_area_cd.length; i++) { 		
				if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
					flag2 = true;
				}
			}
			
			String sql = this.getSelectQuery(search_cd, gf_clss_cd, gf_hole_cd, gf_area_cd, grnfee_mb, grnfee_wk, 
					grnfee_wkend, grnfee_amt1, grnfee_amt2, sido, gugun, dong, gf_nm, flag1, flag2);   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			if (search_cd.equals("1")) { // �󼼰˻�
				
				if (!GolfUtil.isNull(gf_clss_cd)) pstmt.setString(++idx, gf_clss_cd); // ���� �˻�
				
				for (int i = 0; i < gf_hole_cd.length; i++) { // Ȧ�� �˻�			
					if (gf_hole_cd[i] != null && gf_hole_cd[i].length() > 0) {
						pstmt.setString(++idx, gf_hole_cd[i]);
					}
				}
				
				for (int i = 0; i < gf_area_cd.length; i++) { // ������ �˻�			
					if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
						pstmt.setString(++idx, gf_area_cd[i]);
					}
				}
				
				if (grnfee_mb.equals("ALL") && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // �׸��� �˻�
					if ((!GolfUtil.isNull(grnfee_wk) && !GolfUtil.isNull(grnfee_wkend)) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // ���� & �ָ�
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						
					} else if (!GolfUtil.isNull(grnfee_wk) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // ����
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						
					} else if (!GolfUtil.isNull(grnfee_wkend) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // �ָ�
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
					}
					
				} else {
					if ((!GolfUtil.isNull(grnfee_wk) && !GolfUtil.isNull(grnfee_wkend)) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // ���� & �ָ�
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						
					} else if (!GolfUtil.isNull(grnfee_wk) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // ����
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
						
					} else if (!GolfUtil.isNull(grnfee_wkend) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // �ָ�
						pstmt.setLong(++idx, grnfee_amt1);
						pstmt.setLong(++idx, grnfee_amt2);
					}
				}
			} else if (search_cd.equals("2")) { // �����˻�
				
				if (!GolfUtil.isNull(sido)) pstmt.setString(++idx, "%"+sido+"%");  // �õ����� �˻�
				
				if (!GolfUtil.isNull(gugun)) pstmt.setString(++idx, "%"+gugun+"%");  // �������� �˻�
				
				if (!GolfUtil.isNull(dong)) pstmt.setString(++idx, "%"+dong+"%");  // ���鵿���� �˻�
			}
			
			if (!GolfUtil.isNull(gf_nm)) pstmt.setString(++idx, gf_nm); // ������ �˻�
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			if(rs != null) {			 

				while(rs.next())  {	
					result.addLong("GF_SEQ_NO" 			,rs.getLong("AFFI_GREEN_SEQ_NO") );
					result.addString("IMG_NM" 		,rs.getString("ANNX_IMG") );
					result.addString("GF_NM" 			,rs.getString("GREEN_NM") );
					result.addString("ZIPADDR" 			,rs.getString("ADDR") );
					result.addString("DETAILADDR"			,rs.getString("DTL_ADDR") );
					result.addString("GF_CLSS_CD"			,rs.getString("GREEN_CLSS") );
					result.addString("GF_HOLE_CD"			,rs.getString("GREEN_ODNO_CODE") );
										
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
			try { if(rs != null) {rs.close();} else{} } catch (Exception ignored) {}
			try { if(pstmt != null) {pstmt.close();} else{} } catch (Exception ignored) {}
			try { if(conn != null) {conn.close();} else{} } catch (Exception ignored) {}
		}

		return result;
	}	
	

	/** ***********************************************************************
    * Query�� �����Ͽ� �����Ѵ�.    
    ************************************************************************ */
    private String getSelectQuery(String search_cd, String gf_clss_cd, String[] gf_hole_cd, String[] gf_area_cd, String grnfee_mb, String grnfee_wk, 
    		String grnfee_wkend, long grnfee_amt1, long grnfee_amt2, String sido, String gugun, String dong, String gf_nm, boolean flag1, boolean flag2){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			AFFI_GREEN_SEQ_NO, ANNX_IMG, GREEN_NM, ADDR, DTL_ADDR, GREEN_CLSS, GREEN_ODNO_CODE,   	");
		
		sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT,	");
		sql.append("\n 			(MAX(RNUM) OVER()-(?*(?-1))-((ROWNUM-(?*(?-1)))-1)) LIST_NO  	");	
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM,	");
		
		sql.append("\n 			TGF.AFFI_GREEN_SEQ_NO, TGF.ANNX_IMG, TGF.GREEN_NM, TGF.ADDR, TGF.DTL_ADDR, TMC1.GOLF_CMMN_CODE_NM GREEN_CLSS, TMC2.GOLF_CMMN_CODE_NM GREEN_ODNO_CODE   	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGAFFIGREEN TGF, BCDBA.TBGAFFIGREENROUNDINFO TGFR,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0019' AND USE_YN = 'Y') TMC1,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0020' AND USE_YN = 'Y') TMC2,	");
		sql.append("\n 				(SELECT GOLF_CMMN_CODE, GOLF_CMMN_CODE_NM FROM BCDBA.TBGCMMNCODE WHERE GOLF_CMMN_CLSS = '0021' AND USE_YN = 'Y') TMC3		");
		sql.append("\n 				WHERE TGF.AFFI_GREEN_SEQ_NO = TGFR.AFFI_GREEN_SEQ_NO(+)	");
		sql.append("\n 				AND TGF.GREEN_CLSS = TMC1.GOLF_CMMN_CODE(+)	");
		sql.append("\n 				AND TGF.GREEN_ODNO_CODE = TMC2.GOLF_CMMN_CODE(+)	");
		sql.append("\n 				AND TGF.GREEN_RGN_CLSS = TMC3.GOLF_CMMN_CODE(+)	");
		sql.append("\t 					AND TGF.AFFI_FIRM_CLSS = '0004'	");	
		
		if (search_cd.equals("1")) { // �󼼰˻�
			if (!GolfUtil.isNull(gf_clss_cd)) sql.append("\n 	AND TGF.GREEN_CLSS = ?	"); // ���� �˻�
			
			if (flag1) { // Ȧ�� �˻�	
				sql.append("\n 	AND ( ");
				
				for (int i = 0; i < gf_hole_cd.length; i++) { 		
					if (gf_hole_cd[i] != null && gf_hole_cd[i].length() > 0) {
						if (i > 0) sql.append("\n 	OR ");
						sql.append("\n 	TMC2.GOLF_CMMN_CODE = ? ");
					}
				}
				
				sql.append("\n 		) ");
			}
			
			if (flag2) { // ������ �˻�	
				sql.append("\n 	AND ( ");
				
				for (int i = 0; i < gf_area_cd.length; i++) { 		
					if (gf_area_cd[i] != null && gf_area_cd[i].length() > 0) {
						if (i > 0) sql.append("\n 	OR ");
						sql.append("\n 	TMC3.GOLF_CMMN_CODE = ? ");
					}
				}
				
				sql.append("\n 		) ");
			}
		
			if (grnfee_mb.equals("ALL") && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // �׸��� �˻�
				if ((!GolfUtil.isNull(grnfee_wk) && !GolfUtil.isNull(grnfee_wkend)) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // ���� & �ָ�
					sql.append("\n 	AND (NVL(TGFR.GRNFEE_"+grnfee_wk+"_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_NON_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_WKD_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_FMLY_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_NON_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_WKD_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_FMLY_CDHD_CHRG, 0) BETWEEN ? AND ?)	");
					
				} else if (!GolfUtil.isNull(grnfee_wk) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // ����
					sql.append("\n 	AND (NVL(TGFR.GREEN_"+grnfee_wk+"_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_NON_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_WKD_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wk+"_FMLY_CDHD_CHRG, 0) BETWEEN ? AND ?)	");
					
				} else if (!GolfUtil.isNull(grnfee_wkend) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // �ָ�
					sql.append("\n 	AND (NVL(TGFR.GREEN_"+grnfee_wkend+"_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_NON_CDHD_CHRG, 0) BETWEEN ? AND ?		");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_WKD_CDHD_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_FMLY_CDHD_CHRG, 0) BETWEEN ? AND ?)	");
				} 
				
			} else {
				if ((!GolfUtil.isNull(grnfee_wk) && !GolfUtil.isNull(grnfee_wkend)) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // ���� & �ָ�
					sql.append("\n 	AND (NVL(TGFR.GREEN_"+grnfee_wk+"_"+grnfee_mb+"_CHRG, 0) BETWEEN ? AND ?	");
					sql.append("\n 	OR NVL(TGFR.GREEN_"+grnfee_wkend+"_"+grnfee_mb+"_CHRG, 0) BETWEEN ? AND ?)	");
					
				} else if (!GolfUtil.isNull(grnfee_wk) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // ����
					sql.append("\n 	AND NVL(TGFR.GREEN_"+grnfee_wk+"_"+grnfee_mb+"_CHRG, 0) BETWEEN ? AND ?	");
					
				} else if (!GolfUtil.isNull(grnfee_wkend) && (grnfee_amt1 != 0L && grnfee_amt2 != 0L)) { // �ָ�
					sql.append("\n 	AND NVL(TGFR.GREEN_"+grnfee_wkend+"_"+grnfee_mb+"_CHRG, 0) BETWEEN ? AND ?	");
				}
			}
			
		} else if (search_cd.equals("2")) { // �����˻�
			if (!GolfUtil.isNull(sido)) sql.append("\n 	AND REPLACE(TGF.ADDR, ' ', '') LIKE ?	"); // �õ����� �˻�
			
			if (!GolfUtil.isNull(gugun)) sql.append("\n 	AND REPLACE(TGF.ADDR, ' ', '') LIKE ?	"); // �������� �˻�
			
			if (!GolfUtil.isNull(dong)) sql.append("\n 	AND REPLACE(TGF.ADDR, ' ', '') LIKE ?	"); // ���鵿���� �˻�
		}
		
		if (!GolfUtil.isNull(gf_nm)) sql.append("\n 	AND TGF.GREEN_NM = ?	"); // ������ �˻�
	
	
		sql.append("\n 				ORDER BY TGF.AFFI_GREEN_SEQ_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");	
		
    	return sql.toString();
    }
}
