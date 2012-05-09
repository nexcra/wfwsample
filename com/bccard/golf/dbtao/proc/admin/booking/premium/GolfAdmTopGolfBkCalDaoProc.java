/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명  : GolfAdmTopGolfBkCalDaoProc
*   작성자    : shin cheong gwi
*   내용      : 부킹 대상자  내역 -- Calendar
*   적용범위  : golf
*   작성일자  : 2010-11-03
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.booking.premium;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import com.bccard.golf.common.GolfUtil;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractObject;
import com.bccard.waf.common.BaseException;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;

public class GolfAdmTopGolfBkCalDaoProc extends AbstractObject {

	public static final String TITLE = "부킹대상자달력조회";
	private static GolfAdmTopGolfBkCalDaoProc instance = null;
	static{
		synchronized(GolfAdmTopGolfBkCalDaoProc.class){
			if(instance == null){
				instance = new GolfAdmTopGolfBkCalDaoProc();
			}
		}
	}
	public static GolfAdmTopGolfBkCalDaoProc getInstance(){
		return instance;
	}	
	
	/*
	 * Proc 실행- 부킹내역 조회
	 */
	public DbTaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet dataSet) throws BaseException 
	{
		String title = dataSet.getString("TITLE");
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(title);
		String v_start_dt = "";
		String v_end_dt = "";
		int idx = 0;
		
		try
		{
			//조회 조건 
			String p_yyyymm = dataSet.getString("yyyymm");
			String p_prgs_yn = dataSet.getString("PGRS_YN");
			String p_green_nm = dataSet.getString("GREEN_NM");			
			v_start_dt = p_yyyymm+"01";
			v_end_dt = p_yyyymm+"31";
						
			conn = context.getDbConnection("default", null);
			pstmt = conn.prepareStatement(this.getSelectQuery(dataSet).toString());
				pstmt.setString(++idx, v_start_dt);
				pstmt.setString(++idx, v_end_dt);
				if(!p_prgs_yn.equals("")){
					pstmt.setString(++idx, p_prgs_yn);
				}
				if(!p_green_nm.equals("")){
					pstmt.setString(++idx, p_green_nm);
				}
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				GolfUtil.toTaoResult(result, rs);
				result.addString("RESULT", "00");
			}
			
			if(result.size() < 0){
				result.addString("RESULT", "01");
			}
			
		}catch (Throwable t) {
			throw new BaseException(t);
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}
		
		return result;
	}	
	
	/*
	 *  해당날짜에 부킹사항이 있는지 조회한다.
	 */
	/*
	private StringBuffer getSelectQuery() throws Exception
	{		
        StringBuffer sb = new StringBuffer();

		sb.append(" SELECT TEOF_DATE from BCDBA.TBGAPLCMGMT \n");
        sb.append(" WHERE TEOF_DATE BETWEEN ? AND ?	 \n");
      	sb.append("		AND GOLF_SVC_APLC_CLSS='1000' \n");			
		
        return sb;
    }
    */
	/*
	 *  부킹내역 조회 쿼리
	 */
	private StringBuffer getSelectQuery(TaoDataSet dataSet) throws Exception
	{		
		String p_prgs_yn = dataSet.getString("PGRS_YN");
		String p_green_nm = dataSet.getString("GREEN_NM");
				
		StringBuffer sb = new StringBuffer();		
		sb.append("	SELECT	\n");
		sb.append("		A.APLC_SEQ_NO, A.CDHD_ID, A.SEX_CLSS, A.CO_NM, A.PGRS_YN,	\n");
		sb.append("		A.GREEN_NM, A.TEOF_DATE, A.TEOF_TIME, A.CDHD_NON_CDHD_CLSS, A.BKG_PE_NM, 	\n");
		sb.append("		A.HP_DDD_NO, A.HP_TEL_HNO, A.HP_TEL_SNO, A.HP_DDD_NO||'-'||A.HP_TEL_HNO||'-'||A.HP_TEL_SNO HP_NO,	\n");
		sb.append("		B.GOLF_LESN_RSVT_NO		\n");
		sb.append("	FROM	\n");
		sb.append("		BCDBA.TBGAPLCMGMT A, BCDBA.TBGAPLCMGMT B	\n");
		sb.append("	WHERE   1=1	\n");
		sb.append("		AND A.GOLF_SVC_APLC_CLSS='1000'	\n");	
		sb.append("		--AND A.CDHD_NON_CDHD_CLSS = '1'	\n");
		sb.append("		AND A.APLC_SEQ_NO = B.APLC_SEQ_NO(+)	\n");
		sb.append("		AND A.TEOF_DATE BETWEEN ? AND ?	\n");
		if(!p_prgs_yn.equals("")){
			sb.append("		AND A.PGRS_YN = ?		\n");
		}else{
			sb.append("		AND A.PGRS_YN IN ('R','A','W','B','C','F')		\n");
		}
		if(!p_green_nm.equals("")){
			sb.append("		AND A.GREEN_NM = ?	\n");
		}		
		
		sb.append("	ORDER BY A.TEOF_DATE	\n");
		return sb;
	}
	
	/*
	 * 부킹
	 */
	private StringBuffer getSelectRsvtQuery() throws Exception
	{
		StringBuffer sb = new StringBuffer();		
		sb.append("	SELECT GOLF_LESN_RSVT_NO FROM BCDBA.TBGAPLCMGMT WHERE APLC_SEQ_NO = ?	 \n");
		
		return sb;
	}
}
