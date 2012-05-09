/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : admSkyTimeListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ �����̾� ƼŸ�� ����Ʈ ó��
*   �������  : golf
*   �ۼ�����  : 2009-05-14
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.booking.sky;

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
 * Golf
 * @author	�̵������   
 * @version	1.0 
 ******************************************************************************/
public class GolfadmSkyTimeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfAdmLessonListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfadmSkyTimeListDaoProc() {}	

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
			String sch_SKY_CODE = data.getString("SCH_SKY_CODE");
			String sch_RESER_CODE= data.getString("SCH_RESER_CODE");
			String sch_DATE = data.getString("SCH_DATE");
			String sch_DATE_ST = data.getString("SCH_DATE_ST");
			String sch_DATE_ED = data.getString("SCH_DATE_ED");	
			sch_DATE_ST = GolfUtil.replace(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.replace(sch_DATE_ED, "-", "");
			String listtype = data.getString("LISTTYPE");	
			String sql = this.getSelectQuery(sch_SKY_CODE, sch_RESER_CODE, sch_DATE, sch_DATE_ST, sch_DATE_ED, listtype);   

			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			if (!sch_SKY_CODE.equals("")){
				pstmt.setString(++idx, sch_SKY_CODE);
			}
			if (!sch_RESER_CODE.equals("")){
				pstmt.setString(++idx, sch_RESER_CODE);
			}

			if(sch_DATE.equals("rounding")){
				if(!sch_DATE_ST.equals("")){
					pstmt.setString(++idx, sch_DATE_ST);
				}
				if(!sch_DATE_ED.equals("")){
					pstmt.setString(++idx, sch_DATE_ED);
				}
			}else{
				if(!sch_DATE_ST.equals("")){
					pstmt.setString(++idx, sch_DATE_ST);
				}
				if(!sch_DATE_ED.equals("")){
					pstmt.setString(++idx, sch_DATE_ED);
				}
			}

			if (listtype.equals("")){	pstmt.setLong(++idx, data.getLong("page_no"));	}
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			boolean eof = false;
			
			String bkps_time = "";  
			String bkps_date = "";
			String reg_date = "";
			
			if(rs != null) {			 

				while(rs.next())  {
					
					bkps_time = "";
					bkps_date = "";
					reg_date = "";
					
					if(!GolfUtil.empty(rs.getString("BKPS_TIME"))){
						bkps_time = rs.getString("BKPS_TIME").substring(0,2)+":"+rs.getString("BKPS_TIME").substring(2,4);
					}
					if(!GolfUtil.empty(rs.getString("BKPS_DATE"))){
						bkps_date = DateUtil.format(rs.getString("BKPS_DATE"), "yyyyMMdd", "yy-MM-dd");
					}
					if(!GolfUtil.empty(rs.getString("REG_DATE"))){
						reg_date = DateUtil.format(rs.getString("REG_DATE"), "yyyyMMdd", "yy-MM-dd");
					}

					result.addInt("TIME_SEQ_NO" 		,rs.getInt("TIME_SEQ_NO") );
					result.addString("SKY_CODE" 		,rs.getString("SKY_CODE") );
					result.addString("BKPS_DATE"		,bkps_date );
					
					result.addString("BKPS_TIME" 		,bkps_time);
					result.addString("RESER_CODE" 		,rs.getString("RESER_CODE") );
					result.addString("REG_DATE"			,reg_date );
									
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
										
					result.addString("RESULT", "00"); //������
					art_num_no++;
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
    private String getSelectQuery(String sch_SKY_CODE, String sch_RESER_CODE, String sch_DATE, String sch_DATE_ST, String sch_DATE_ED, String listtype){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 		, TIME_SEQ_NO, SKY_CODE, BKPS_DATE, BKPS_TIME, RESER_CODE, REG_DATE  	");		
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE	");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  																				\n");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 			, T1.RSVT_ABLE_BOKG_TIME_SEQ_NO AS TIME_SEQ_NO, (CASE WHEN T2.SKY72_HOLE_CODE='0001' THEN '7Ȧ' ELSE '14Ȧ' END) SKY_CODE	");
		sql.append("\n 			, T2.BOKG_ABLE_DATE AS BKPS_DATE, T1.BOKG_ABLE_TIME AS BKPS_TIME	");
		sql.append("\n 			, (CASE WHEN T1.BOKG_RSVT_STAT_CLSS='0001' THEN '��ŷ���' ELSE '��ŷȮ��' END) RESER_CODE	");
		sql.append("\n 			, T2.REG_ATON AS REG_DATE	");
		sql.append("\n 			FROM 	");
		sql.append("\n 			BCDBA.TBGRSVTABLEBOKGTIMEMGMT T1  	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO 	");
		sql.append("\n 			WHERE T2.SKY72_HOLE_CODE IS NOT NULL	");
		
		if(!sch_SKY_CODE.equals("")){
			sql.append("\n 				AND T2.SKY72_HOLE_CODE = ?	");
		}
		if(!sch_RESER_CODE.equals("")){
			sql.append("\n 				AND T1.BOKG_RSVT_STAT_CLSS = ?	");
		}
		if(sch_DATE.equals("rounding")){
			if(!sch_DATE_ST.equals("")){
				sql.append("\n 			AND T2.BOKG_ABLE_DATE >= ?	");
			}
			if(!sch_DATE_ED.equals("")){
				sql.append("\n 			AND T2.BOKG_ABLE_DATE <= ?	");
			}
		}else{
			if(!sch_DATE_ST.equals("")){
				sql.append("\n 			AND T2.REG_ATON >= ?	");
			}
			if(!sch_DATE_ED.equals("")){
				sql.append("\n 			AND T2.REG_ATON <= ?	");
			}
		}		
		
		sql.append("\n 				ORDER BY T2.BOKG_ABLE_DATE DESC, T1.BOKG_ABLE_TIME DESC 	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		if(listtype.equals("")){		sql.append("\n WHERE PAGE = ?	");				}	

		return sql.toString();
    }
}
