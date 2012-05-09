/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfAdmEvntApsTimeListDaoProc
*   �ۼ���    : (��)�̵������ ������
*   ����      : ������ > �̺�Ʈ > ����þ� > �󼼺���
*   �������  : golf
*   �ۼ�����  : 2010-06-28
************************** �����̷� ****************************************************************
* Ŀ��屸����	������		������	���泻��
* 
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.admin.event.alpensia;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.bccard.golf.common.GolfAdminEtt;
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
public class GolfAdmEvntApsTimeListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfadmPreTimeListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfAdmEvntApsTimeListDaoProc() {}	

	/**
	 * Proc ����.
	 * @param Connection con
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws BaseException {

		GolfAdminEtt userEtt = null;
		String title = data.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);

		try {
			conn = context.getDbConnection("default", null); 

			//1.��������üũ
			HttpSession session = request.getSession(true);
			userEtt =(GolfAdminEtt)session.getAttribute("SESSION_ADMIN");
			String admId = userEtt.getMemId();
			String admClss = userEtt.getAdm_clss();
			 
			//��ȸ ----------------------------------------------------------
			String sch_YN = data.getString("SCH_YN");
			String sch_GR_SEQ_NO = data.getString("SCH_GR_SEQ_NO");
			String sch_RESER_CODE = data.getString("SCH_RESER_CODE");
			String sch_VIEW_YN = data.getString("SCH_VIEW_YN");
			String sch_DATE = data.getString("SCH_DATE");
			String sch_DATE_ST = data.getString("SCH_DATE_ST");
			String sch_DATE_ED = data.getString("SCH_DATE_ED");		
			sch_DATE_ST = GolfUtil.replace(sch_DATE_ST, "-", "");
			sch_DATE_ED = GolfUtil.replace(sch_DATE_ED, "-", "");
			String listtype = data.getString("LISTTYPE");	
			String sql = this.getSelectQuery(sch_YN, sch_GR_SEQ_NO, sch_RESER_CODE, sch_VIEW_YN, sch_DATE, sch_DATE_ST, sch_DATE_ED, listtype, admId, admClss);   

			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("record_size"));
			pstmt.setLong(++idx, data.getLong("page_no"));

			if (!sch_GR_SEQ_NO.equals("")){
				pstmt.setString(++idx, sch_GR_SEQ_NO);
			}
			if (!sch_RESER_CODE.equals("")){
				pstmt.setString(++idx, sch_RESER_CODE);
			}
			if (!sch_VIEW_YN.equals("")){
				pstmt.setString(++idx, sch_VIEW_YN);
			}

			if (listtype.equals("")){	pstmt.setLong(++idx, data.getLong("page_no"));	}
			
			rs = pstmt.executeQuery();

			int art_num_no = 0;
			if(rs != null) {			 

				while(rs.next())  {	

					result.addInt("ART_NUM" 			,rs.getInt("ART_NUM")-art_num_no );
					result.addInt("TIME_SEQ_NO" 		,rs.getInt("TIME_SEQ_NO") );
					result.addString("VIEW_YN" 			,rs.getString("VIEW_YN") );
					result.addString("GR_NM" 			,rs.getString("GR_NM") );
					result.addString("BKPS_DATE"		,DateUtil.format(rs.getString("BKPS_DATE"), "yyyyMMdd", "yy-MM-dd") );
					result.addString("COURSE" 			,rs.getString("COURSE") );
					result.addString("BKPS_TIME" 		,rs.getString("BKPS_TIME").substring(0,2)+":"+rs.getString("BKPS_TIME").substring(2,4) );
					result.addString("RESER_CODE" 		,rs.getString("RESER_CODE") );
					result.addString("REG_DATE"			,DateUtil.format(rs.getString("REG_DATE"), "yyyyMMdd", "yy-MM-dd") );
									
					result.addString("TOT_CNT"			,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
					result.addString("RNUM"				,rs.getString("RNUM") );
										
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
    private String getSelectQuery(String sch_YN, String sch_GR_SEQ_NO, String sch_RESER_CODE, String sch_VIEW_YN, String sch_DATE, String sch_DATE_ST, String sch_DATE_ED, String listtype, String admId, String admClss){
        StringBuffer sql = new StringBuffer();
		
		sql.append("\n SELECT	*	");
		sql.append("\n FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 		, TIME_SEQ_NO, VIEW_YN, GR_NM, BKPS_DATE, COURSE, BKPS_TIME, RESER_CODE, REG_DATE  	");		
		sql.append("\n 		, CEIL(ROWNUM/?) AS PAGE	");
		sql.append("\n 		, MAX(RNUM) OVER() TOT_CNT	");
		sql.append("\n 		, ((MAX(RNUM) OVER())-(?-1)*10) AS ART_NUM  	");	
		sql.append("\n 		FROM (SELECT ROWNUM RNUM	");
		sql.append("\n 			, T1.RSVT_ABLE_BOKG_TIME_SEQ_NO AS TIME_SEQ_NO	");
		sql.append("\n 			, (CASE WHEN T1.EPS_YN='Y' THEN '����' ELSE '�����' END) VIEW_YN	");
		sql.append("\n 			, T3.GREEN_NM AS GR_NM	");
		sql.append("\n 			, T2.BOKG_ABLE_DATE AS BKPS_DATE, T2.GOLF_RSVT_CURS_NM AS COURSE, T1.BOKG_ABLE_TIME AS BKPS_TIME	");
		sql.append("\n 			, (CASE WHEN T1.BOKG_RSVT_STAT_CLSS='0001' THEN '��ŷ���' ELSE '��ŷȮ��' END) RESER_CODE	");
		sql.append("\n 			, T2.REG_ATON AS REG_DATE	");
		sql.append("\n 			FROM 	");
		sql.append("\n 			BCDBA.TBGRSVTABLEBOKGTIMEMGMT T1  	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGRSVTABLESCDMGMT T2 ON T1.RSVT_ABLE_SCD_SEQ_NO=T2.RSVT_ABLE_SCD_SEQ_NO 	");
		sql.append("\n 			LEFT JOIN BCDBA.TBGAFFIGREEN T3 ON T2.AFFI_GREEN_SEQ_NO=T3.AFFI_GREEN_SEQ_NO	");
		sql.append("\n 			WHERE T2.GOLF_RSVT_CURS_NM IS NOT NULL AND T2.BOKG_ABLE_DATE IS NOT NULL  	");
		sql.append("\n 			AND T1.REG_MGR_ID='bcgolf'	");
		sql.append("\n 			AND T2.PAR_3_BOKG_RESM_DATE IS NULL	");
		sql.append("\n 			AND T2.SKY72_HOLE_CODE IS NULL	");
		sql.append("\n 			AND T2.GOLF_RSVT_DAY_CLSS='P'	");
		
		
		if(sch_YN.equals("Y")){
			if(!sch_GR_SEQ_NO.equals("")){
				sql.append("\n 				AND T3.AFFI_GREEN_SEQ_NO = ?	");
			}
			if(!sch_RESER_CODE.equals("")){
				sql.append("\n 				AND T1.BOKG_RSVT_STAT_CLSS = ?	");
			}
			if(!sch_VIEW_YN.equals("")){
				sql.append("\n 				AND T1.EPS_YN = ?	");
			}
			if(sch_DATE.equals("rounding")){
				if(!sch_DATE_ST.equals("")){
					sql.append("\n 			AND T2.BOKG_ABLE_DATE >= '"+sch_DATE_ST+"'	");
				}
				if(!sch_DATE_ED.equals("")){
					sql.append("\n 			AND T2.BOKG_ABLE_DATE <= '"+sch_DATE_ED+"'	");
				}
			}else{
				if(!sch_DATE_ST.equals("")){
					sql.append("\n 			AND T2.REG_ATON >= '"+sch_DATE_ST+"000000'	");
				}
				if(!sch_DATE_ED.equals("")){
					sql.append("\n 			AND T2.REG_ATON <= '"+sch_DATE_ED+"240000'	");
				}
			}		
		}
		

		if(!admClss.equals("master"))	sql.append("\n		AND T3.GREEN_ID='"+admId+"'  	");	
		
		sql.append("\n 				ORDER BY T2.BOKG_ABLE_DATE DESC, T1.BOKG_ABLE_TIME DESC 	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		if(listtype.equals("")){		sql.append("\n WHERE PAGE = ?	");				}

		return sql.toString();
    }
}
