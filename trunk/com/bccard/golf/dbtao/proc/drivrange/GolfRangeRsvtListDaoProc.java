/***************************************************************************************************
*   �� �ҽ��� �ߺ�ī�� �����Դϴ�.
*   �� �ҽ��� �������� �����ϸ� ���� ���� ó���� ���� �� �ֽ��ϴ�.
*   Ŭ������  : GolfRangeRsvtListDaoProc
*   �ۼ���    : (��)����Ŀ�´����̼� ����ȯ
*   ����      : SKY72�帲���������� ����Ȯ��
*   �������  : golf
*   �ۼ�����  : 2009-06-16
************************** �����̷� ****************************************************************
*    ����      ����   �ۼ���   �������
*
***************************************************************************************************/

package com.bccard.golf.dbtao.proc.drivrange;

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
public class GolfRangeRsvtListDaoProc extends AbstractProc {
	
	/** *****************************************************************
	 * GolfRangeRsvtListDaoProc ���μ��� ������
	 * @param N/A
	 ***************************************************************** */
	public GolfRangeRsvtListDaoProc() {}	

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
			
			String sql = this.getSelectQuery();   
			
			// �Է°� (INPUT)         
			int idx = 0;
			pstmt = conn.prepareStatement(sql.toString());
			pstmt.setLong(++idx, data.getLong("RECORD_SIZE"));
			
			pstmt.setString(++idx, data.getString("GF_ID"));
			
			pstmt.setLong(++idx, data.getLong("PAGE_NO"));
			
			rs = pstmt.executeQuery();
			
			String rsvt_strt_time = "";
			String rsvt_end_time = "";
			String rsvt_time = "";
			
			if(rs != null) {
			
				while(rs.next())  {
					if(!GolfUtil.empty(rs.getString("RSVT_STRT_TIME"))){
						rsvt_strt_time = rs.getString("RSVT_STRT_TIME").trim();
					}else{
						rsvt_strt_time = "";
					}
					
					if(!GolfUtil.empty(rs.getString("RSVT_END_TIME"))){
						rsvt_end_time = rs.getString("RSVT_END_TIME").trim();
					}else{
						rsvt_end_time = "";
					}
					
					// 2010/8/14 ������ ��ŷ�� �ð��븦 �������� �ʾƵ� �ȴ�. �⺻���� 0000�� �ð���� ������� �ʴ´�.
					if(rsvt_strt_time.equals("0000") && rsvt_end_time.equals("0000")){
						rsvt_time = "";
					}else{
						rsvt_time = rs.getString("RSVT_TIME");
					}
					
					//debug("rsvt_strt_time : " + rsvt_strt_time + " / rsvt_end_time : " + rsvt_end_time);
					
					result.addString("RSVT_SQL_NO" 		,rs.getString("GOLF_SVC_RSVT_NO") );
					result.addString("RSVT_YN" 			,rs.getString("RSVT_YN") );
					result.addString("REG_ATON" 		,rs.getString("REG_ATON") );
					result.addString("RSVT_DATE" 		,rs.getString("RSVT_ABLE_DATE") );
					result.addString("RSVT_TIME" 		,rsvt_time );
					result.addString("CNCL_DATE" 		,rs.getString("CNCL_DATE") );
					result.addLong("DAY_CNT" 			,rs.getLong("DAY_CNT") );
					result.addString("RSVT_DAY_YN" 		,rs.getString("RSVT_DAY_YN") );
					result.addString("GR_NM" 		,rs.getString("GR_NM") );
					
					result.addString("TOTAL_CNT"		,rs.getString("TOT_CNT") );
					result.addString("CURR_PAGE"		,rs.getString("PAGE") );
										
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
    private String getSelectQuery(){
        StringBuffer sql = new StringBuffer();
		
        sql.append("\n SELECT *	");
		sql.append("\n FROM (SELECT ROWNUM RNUM, GR_NM,	");
		
		sql.append("\n 			GOLF_SVC_RSVT_NO, RSVT_YN, REG_ATON, RSVT_ABLE_DATE, RSVT_TIME, CNCL_DATE, DAY_CNT,  	");
		sql.append("\n 			CASE WHEN DAY_CNT >= 2 THEN 'Y' END AS RSVT_DAY_YN,		"); //2�������� �ޱ� 2009.10.07 ����
		sql.append("\n 			RSVT_STRT_TIME, RSVT_END_TIME, 		");
        sql.append("\n 			CEIL(ROWNUM/?) AS PAGE,	");
		sql.append("\n 			MAX(RNUM) OVER() TOT_CNT	");
		
		sql.append("\n 			FROM (SELECT ROWNUM RNUM, TGRN.GREEN_NM GR_NM,		");
		
		sql.append("\n 			TGR.GOLF_SVC_RSVT_NO, TGR.RSVT_YN, 	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGR.REG_ATON, 'YYYYMMDDHH24MISS'), 'YYYY/MM/DD') REG_ATON,  	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD'), 'YYYY/MM/DD') RSVT_ABLE_DATE, 	");
		sql.append("\n 			TO_CHAR (TO_DATE (TGRT.RSVT_STRT_TIME, 'HH24MI'), 'HH24:MI') ||' ~ '|| TO_CHAR (TO_DATE (TGRT.RSVT_END_TIME, 'HH24MI'), 'HH24:MI') RSVT_TIME,  	");
		sql.append("\n 			TGRT.RSVT_STRT_TIME, TGRT.RSVT_END_TIME, 	");
		sql.append("\n 			TO_CHAR (  TO_DATE (TGRD.RSVT_ABLE_DATE, 'YYYYMMDD') - 2, 'YYYY/MM/DD') CNCL_DATE,  	");//2�������� �ޱ� 2009.10.07 ����
		sql.append("\n 			TRUNC (TGRD.RSVT_ABLE_DATE) - TRUNC (TO_CHAR (SYSDATE, 'YYYYMMDD')) DAY_CNT  	");
		
		sql.append("\n 				FROM 	");
		sql.append("\n 				BCDBA.TBGRSVTMGMT TGR, BCDBA.TBGRSVTABLESCDMGMT TGRD, BCDBA.TBGRSVTABLEBOKGTIMEMGMT TGRT, BCDBA.TBGGOLFCDHD TGU, BCDBA.TBGAFFIGREEN TGRN	");
		sql.append("\n 				WHERE TGU.CDHD_ID = TGR.CDHD_ID(+)	");
		sql.append("\n 				AND TGR.RSVT_ABLE_BOKG_TIME_SEQ_NO = TGRT.RSVT_ABLE_BOKG_TIME_SEQ_NO	");
		sql.append("\n 				AND TGRT.RSVT_ABLE_SCD_SEQ_NO = TGRD.RSVT_ABLE_SCD_SEQ_NO	");
		sql.append("\n 				AND SUBSTR(TGR.GOLF_SVC_RSVT_MAX_VAL, 5,1) = 'D'	");
		sql.append("\n 				AND TGRD.AFFI_GREEN_SEQ_NO = TGRN.AFFI_GREEN_SEQ_NO	");
		sql.append("\n 				AND TGU.CDHD_ID = ?	");		
	
		sql.append("\n 				ORDER BY TGR.GOLF_SVC_RSVT_NO DESC	");		
		sql.append("\n 			)	");
		sql.append("\n 	ORDER BY RNUM	");
		sql.append("\n 	)	");
		sql.append("\n WHERE PAGE = ?	");
		
        return sql.toString();
    }
}
