/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntBnstListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > ���׽�Ʈ > ����Ʈ
*   �������  : golf
*   �ۼ�����  : 2010-03-23
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.benest;

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
import com.bccard.waf.tao.TaoException;
import com.bccard.golf.common.AppConfig;

/******************************************************************************
 * Golf
 * @author	�̵������
 * @version	1.0 
 ******************************************************************************/
public class GolfAdmEvntBnstListXlsDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmOrdListDaoProc ���μ��� ������   
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntBnstListXlsDaoProc() {}	

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
			
			// �˻���

			String sch_yn				= data.getString("sch_yn");
			String sch_date				= data.getString("sch_date");
			String sch_date_st			= data.getString("sch_date_st");
			String sch_date_ed			= data.getString("sch_date_ed");	
			String sch_type				= data.getString("sch_type");
			String sch_text				= data.getString("sch_text");
			String sch_sttl_stat_clss	= data.getString("sch_sttl_stat_clss");		// �������� �����ڵ�
			String sch_evnt_pgrs_clss	= data.getString("sch_evnt_pgrs_clss");		// ������� �����ڵ�
			String sch_green_nm			= data.getString("sch_green_nm");
			String sch_rsvt_date		= data.getString("sch_rsvt_date");
			
			String sql = this.getSelectQuery(data);

			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			
			if(!GolfUtil.empty(sch_yn)){
				// rnum�� ������������ where�� �ٽ� ������.
				if(sch_date.equals("aplc_aton")){
					if(!GolfUtil.empty(sch_date_st))	pstmt.setString(++idx, sch_date_st+"000000");
					if(!GolfUtil.empty(sch_date_ed))	pstmt.setString(++idx, sch_date_ed+"999999");
				}else{
					if(!GolfUtil.empty(sch_date_st))	pstmt.setString(++idx, sch_date_st);
					if(!GolfUtil.empty(sch_date_ed))	pstmt.setString(++idx, sch_date_ed);
				}
				if(!GolfUtil.empty(sch_sttl_stat_clss))	pstmt.setString(++idx, sch_sttl_stat_clss);
				if(!GolfUtil.empty(sch_evnt_pgrs_clss))	pstmt.setString(++idx, sch_evnt_pgrs_clss);
				if(!GolfUtil.empty(sch_green_nm))	pstmt.setString(++idx, sch_green_nm);
				if(!GolfUtil.empty(sch_rsvt_date))	pstmt.setString(++idx, sch_rsvt_date);
				if(!GolfUtil.empty(sch_text)){
					if("MOBILE".equals(sch_type)){
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
					}else{
						pstmt.setString(++idx, "%"+sch_text+"%");
					}
				}
				

				if(sch_date.equals("aplc_aton")){
					if(!GolfUtil.empty(sch_date_st))	pstmt.setString(++idx, sch_date_st+"000000");
					if(!GolfUtil.empty(sch_date_ed))	pstmt.setString(++idx, sch_date_ed+"999999");
				}else{
					if(!GolfUtil.empty(sch_date_st))	pstmt.setString(++idx, sch_date_st);
					if(!GolfUtil.empty(sch_date_ed))	pstmt.setString(++idx, sch_date_ed);
				}
				if(!GolfUtil.empty(sch_sttl_stat_clss))	pstmt.setString(++idx, sch_sttl_stat_clss);
				if(!GolfUtil.empty(sch_evnt_pgrs_clss))	pstmt.setString(++idx, sch_evnt_pgrs_clss);
				if(!GolfUtil.empty(sch_green_nm))	pstmt.setString(++idx, sch_green_nm);
				if(!GolfUtil.empty(sch_rsvt_date))	pstmt.setString(++idx, sch_rsvt_date);
				if(!GolfUtil.empty(sch_text)){
					if("MOBILE".equals(sch_type)){
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
						pstmt.setString(++idx, "%"+sch_text+"%");
					}else{
						pstmt.setString(++idx, "%"+sch_text+"%");
					}
				}
			}
			
						
			rs = pstmt.executeQuery();
			

			String sttl_amt = "";
			String cdhd_grd = "";
			if(rs != null) {			 

				while(rs.next())  {	
					
					if(!GolfUtil.empty(rs.getString("STTL_AMT"))){
						sttl_amt = GolfUtil.comma(rs.getString("STTL_AMT"));
					}else{
						sttl_amt = "";
					}
					
					cdhd_grd = "";
					if(!GolfUtil.empty(rs.getString("CDHD_GRD_SEQ_NO"))){
						cdhd_grd = rs.getString("CDHD_GRD_SEQ_NO");
						
						if(cdhd_grd.equals("1")){
							cdhd_grd = "è�Ǿ�";
						}else if(cdhd_grd.equals("9")){
							cdhd_grd = "�÷�Ƽ��";
						}else if(cdhd_grd.equals("0")){
							cdhd_grd = "��Ÿ����ȸ��";
						}else if(cdhd_grd.equals("4")){
							cdhd_grd = "����ȸ��";
						}else if(cdhd_grd.equals("")){
							cdhd_grd = "";
						}else{
							cdhd_grd = "��Ÿ����ȸ��";
						}
					}
					
					result.addString("RNUM" 			,rs.getString("RNUM") );
					result.addString("CDHD_ID" 			,rs.getString("CDHD_ID") );
					result.addString("BKG_PE_NM" 		,rs.getString("BKG_PE_NM") );
					result.addString("APLC_PE_CLSS" 	,rs.getString("APLC_PE_CLSS") );
					result.addString("HP" 				,rs.getString("HP") );
					result.addString("JUMIN_NO" 		,rs.getString("JUMIN_NO") );
					result.addString("REG_DATE" 		,rs.getString("REG_DATE") );
					result.addString("CDHD_GRD" 		,cdhd_grd );
					result.addString("STTL_AMT" 		,rs.getString("STTL_AMT") );
					result.addString("STTL_STAT_CLSS" 	,rs.getString("STTL_STAT_CLSS") );
					result.addString("EVNT_PGRS_CLSS" 	,rs.getString("EVNT_PGRS_CLSS") );
					result.addString("GREEN_NM" 		,rs.getString("GREEN_NM") );
					result.addString("RSVT_DATE" 		,rs.getString("RSVT_DATE") );
					result.addString("RSV_TIME" 		,rs.getString("RSV_TIME") );
					result.addString("HADC_NUM" 		,rs.getString("HADC_NUM") );
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
    * ���� ����Ʈ
    ************************************************************************ */
    private String getSelectQuery(TaoDataSet data) throws TaoException{
        StringBuffer sql = new StringBuffer();

		String sch_yn				= data.getString("sch_yn");
		String sch_date				= data.getString("sch_date");
		String sch_date_st			= data.getString("sch_date_st");
		String sch_date_ed			= data.getString("sch_date_ed");	
		String sch_type				= data.getString("sch_type");
		String sch_text				= data.getString("sch_text");
		String sch_sttl_stat_clss	= data.getString("sch_sttl_stat_clss");		// �������� �����ڵ�
		String sch_evnt_pgrs_clss	= data.getString("sch_evnt_pgrs_clss");		// ������� �����ڵ�
		String sch_green_nm			= data.getString("sch_green_nm");
		String sch_rsvt_date		= data.getString("sch_rsvt_date");
		String sch_type_fornum		= "";

		sql.append("\n	SELECT EVT.RNUM, COMPN.CDHD_ID, COMPN.BKG_PE_NM	\n");
		sql.append("\t	, CASE COMPN.APLC_PE_CLSS WHEN '1' THEN '��û��' ELSE '������' END APLC_PE_CLSS	\n");
		sql.append("\t	, (COMPN.HP_DDD_NO||'-'||COMPN.HP_TEL_HNO||'-'||COMPN.HP_TEL_SNO) HP, COMPN.JUMIN_NO	\n");
		sql.append("\t	, TO_CHAR(TO_DATE(SUBSTR(EVT.APLC_ATON,1,8)),'YYYY-MM-DD') REG_DATE, COMPN.CDHD_GRD_SEQ_NO	\n");
		sql.append("\t	, COMPN.STTL_AMT, EVT.GREEN_NM, EVT.HADC_NUM	\n");
		sql.append("\t	, TO_CHAR(TO_DATE(EVT.RSVT_DATE),'YYYY-MM-DD') RSVT_DATE, CASE WHEN SUBSTR(EVT.RSV_TIME,1,2)>12 THEN '����' ELSE '����' END RSV_TIME	\n");
		sql.append("\t	, CASE COMPN.STTL_STAT_CLSS WHEN '0' THEN '�̰���' WHEN '1' THEN '�����Ϸ�' WHEN '2' THEN '�������' END STTL_STAT_CLSS	\n");
		sql.append("\t	, CASE EVT.EVNT_PGRS_CLSS WHEN 'R' THEN '��û' WHEN 'A' THEN '���' WHEN 'P' THEN '��������' WHEN 'B' THEN 'Ȯ��' WHEN 'C' THEN '�������' WHEN 'E' THEN '�������' END EVNT_PGRS_CLSS	\n");
		sql.append("\t	FROM BCDBA.TBGGOLFEVNTAPLCPE COMPN	\n");
		sql.append("\t	JOIN (SELECT ROWNUM RNUM, RSV.* FROM BCDBA.TBGGOLFEVNTAPLC RSV 	\n");
		sql.append("\t		WHERE APLC_SEQ_NO IS NOT NULL 	\n");

		if(!GolfUtil.empty(sch_yn)){
			if(sch_date.equals("aplc_aton")){
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND RSV.APLC_ATON>=?	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND RSV.APLC_ATON<=?	\n");
			}else{
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND RSV.RSVT_DATE>=?	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND RSV.RSVT_DATE<=?	\n");
			}
			if(!GolfUtil.empty(sch_sttl_stat_clss))	sql.append("\t	            AND RSV.STTL_STAT_CLSS=?	\n");
			if(!GolfUtil.empty(sch_evnt_pgrs_clss))	sql.append("\t	            AND RSV.EVNT_PGRS_CLSS=?	\n");
			if(!GolfUtil.empty(sch_green_nm))		sql.append("\t	            AND RSV.GREEN_NM=?	\n");
			if(!GolfUtil.empty(sch_rsvt_date))		sql.append("\t	            AND RSV.RSVT_DATE=?	\n");

			if(!GolfUtil.empty(sch_type) && !GolfUtil.empty(sch_text)){
				if("MOBILE".equals(sch_type)){
					sql.append("\t	            AND (RSV.HP_DDD_NO LIKE ? OR RSV.HP_TEL_HNO LIKE ? OR RSV.HP_TEL_SNO LIKE ?)	\n");
				}else{
					sch_type_fornum = GolfUtil.replace(sch_type, "EVT.", "RSV.");
					sql.append("\t	            AND "+sch_type_fornum+" LIKE ?	\n");
				}
			}
		}
		
		sql.append("\t		ORDER BY APLC_SEQ_NO DESC) EVT ON COMPN.APLC_SEQ_NO=EVT.APLC_SEQ_NO	\n");
		sql.append("\t	WHERE COMPN.APLC_SEQ_NO IS NOT NULL	\n");
        

		if(!GolfUtil.empty(sch_yn)){
			if(sch_date.equals("aplc_aton")){
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND EVT.APLC_ATON>=?	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND EVT.APLC_ATON<=?	\n");
			}else{
				if(!GolfUtil.empty(sch_date_st))		sql.append("\t	            AND EVT.RSVT_DATE>=?	\n");
				if(!GolfUtil.empty(sch_date_ed))		sql.append("\t	            AND EVT.RSVT_DATE<=?	\n");
			}
			if(!GolfUtil.empty(sch_sttl_stat_clss))	sql.append("\t	            AND EVT.STTL_STAT_CLSS=?	\n");
			if(!GolfUtil.empty(sch_evnt_pgrs_clss))	sql.append("\t	            AND EVT.EVNT_PGRS_CLSS=?	\n");
			if(!GolfUtil.empty(sch_green_nm))		sql.append("\t	            AND EVT.GREEN_NM=?	\n");
			if(!GolfUtil.empty(sch_rsvt_date))		sql.append("\t	            AND EVT.RSVT_DATE=?	\n");

			if(!GolfUtil.empty(sch_type) && !GolfUtil.empty(sch_text)){
				if("MOBILE".equals(sch_type)){
					sql.append("\t	            AND (EVT.HP_DDD_NO LIKE ? OR EVT.HP_TEL_HNO LIKE ? OR EVT.HP_TEL_SNO LIKE ?)	\n");
				}else{
					sql.append("\t	            AND "+sch_type+" LIKE ?	\n");
				}
			}
		}
		
		
		sql.append("\t	ORDER BY COMPN.APLC_SEQ_NO DESC, COMPN.SEQ_NO ASC	\n");
		
		return sql.toString();
    }

}
