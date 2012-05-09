/***************************************************************************************************
*   이 소스는 ㈜비씨카드 소유입니다.
*   이 소스를 무단으로 도용하면 법에 따라 처벌을 받을 수 있습니다.
*   클래스명	: GolfAdmEvntSpSettleDetailDaoProc
*   작성자	: (주)미디어포스 천선정
*   내용		: 관리자 >  이벤트 > 특별레슨 이벤트 > 결제관리 상세
*   적용범위	: golf
*   작성일자	: 2009-07-16
************************** 수정이력 ****************************************************************
*    일자      버전   작성자   변경사항
*
***************************************************************************************************/
package com.bccard.golf.dbtao.proc.admin.event.settlement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;

import com.bccard.golf.dbtao.DbTaoException;
import com.bccard.golf.dbtao.DbTaoResult;
import com.bccard.waf.action.AbstractProc;
import com.bccard.waf.core.WaContext;
import com.bccard.waf.tao.TaoDataSet;
import com.bccard.waf.tao.TaoResult;

/******************************************************************************
* Topn
* @author	(주)미디어포스
* @version	1.0
******************************************************************************/
public class GolfAdmEvntSpSettleDetailDaoProc extends AbstractProc {
	public static final String TITLE = "관리자 >  이벤트 > 특별레슨 이벤트 결제관리 상세";
	/** **************************************************************************
	 * Proc 실행.
	 * @param Connection con 
	 * @param TaoDataSet dataSet
	 * @return TaoResult
	 ************************************************************************** **/
	public TaoResult execute(WaContext context,  HttpServletRequest request ,TaoDataSet data) throws DbTaoException {
		
		
		ResultSet rs = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		DbTaoResult  result =  new DbTaoResult(TITLE);
		String sql = "";
	
		try { 

			// 회원통합테이블 관련 수정사항 진행
			conn = context.getDbConnection("default", null);
		
			String p_idx 		  	  = data.getString("p_idx");
			String golf_svc_aplc_clss = data.getString("golf_svc_aplc_clss");
			
			int pidx = 0;
			boolean eof = false;
			sql = this.getSelectQuery();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(++pidx, golf_svc_aplc_clss);
			pstmt.setString(++pidx, p_idx);
			
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				if(!eof) result.addString("RESULT", "00");
				result.addString("seq_no",			rs.getString("APLC_SEQ_NO"));
				result.addString("odr_no",			rs.getString("ODR_NO"));
				result.addString("sttl_amt",		rs.getString("STTL_AMT"));
				result.addString("evnt_nm",			rs.getString("EVNT_NM"));
				result.addString("evnt_seq_no",		rs.getString("EVNT_SEQ_NO"));
				result.addString("name",			rs.getString("NAME"));
				result.addString("sex",				rs.getString("SEX"));
				result.addString("hp_ddd_no",		rs.getString("HP_DDD_NO"));
				result.addString("hp_tel_hno",		rs.getString("HP_TEL_HNO"));
				result.addString("hp_tel_sno",		rs.getString("HP_TEL_SNO"));
				result.addString("lesc_dc_cost",	rs.getString("LESN_DC_COST"));
				result.addString("real_cost",		rs.getString("REAL_COST"));
				result.addString("sttl_aton",		rs.getString("STTL_ATON"));
				result.addString("cncl_aton",		rs.getString("CNCL_ATON"));
				result.addString("reg_aton",		rs.getString("REG_ATON"));
				result.addString("email",			rs.getString("EMAIL"));
				result.addString("date_status",		rs.getString("DATE_STATUS"));
				result.addString("status",			rs.getString("STATUS"));
				eof = true;
				
			}
			
			if(!eof) result.addString("RESULT", "01");
			
			
			 
		} catch ( Exception e ) {			
			
		} finally {
			try { if(rs != null) rs.close(); } catch (Exception ignored) {}
			try { if(pstmt != null) pstmt.close(); } catch (Exception ignored) {}
			try { if(conn  != null) conn.close();  } catch (Exception ignored) {}
		}

		return result;
	}	 
	

	/** ***********************************************************************
	* Query를 생성하여 리턴한다.
	************************************************************************ */
	private String getSelectQuery() throws Exception{

		StringBuffer sql = new StringBuffer();
	
		sql.append("\n SELECT																");
		sql.append("\n 		T1.APLC_SEQ_NO													");
		sql.append("\n 		,T2.ODR_NO														");
		sql.append("\n 		,T2.STTL_AMT													");
		sql.append("\n 		,T3.EVNT_NM														");
		sql.append("\n 		,T3.EVNT_SEQ_NO 												");
		sql.append("\n 		,T4.HG_NM AS NAME														");
		sql.append("\n 		,T1.HP_DDD_NO													");
		sql.append("\n 		,T1.HP_TEL_HNO													");
		sql.append("\n 		,T1.HP_TEL_SNO													");
		sql.append("\n 		,(CASE SUBSTR(T4.JUMIN_NO,7,1) WHEN '1' THEN '1' WHEN '3' THEN '1' ELSE '2' END) AS SEX	");
		sql.append("\n 		,T1.EMAIL														");
		sql.append("\n 		,T3.LESN_DC_COST AS REAL_COST									");
		sql.append("\n 		,TO_NUMBER(T6.CDHD_SQ2_CTGO) AS USERGRADE						");
		sql.append("\n 		,TO_CHAR(T3.LESN_DC_COST,'999,999,999,999,999')AS LESN_DC_COST	");
		sql.append("\n 		,TO_CHAR(TO_DATE(T2.STTL_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD')AS STTL_ATON					");
		sql.append("\n 		,TO_CHAR(TO_DATE(T2.CNCL_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD')AS CNCL_ATON					");
		sql.append("\n 		,TO_CHAR(TO_DATE(T1.REG_ATON,'yyyy-MM-dd hh24miss'),'YYYY-MM-DD')AS REG_ATON					");
		sql.append("\n 		,(CASE WHEN TO_NUMBER(T3.EVNT_STRT_DATE) > TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')) THEN '0'		");
		sql.append("\n 			   WHEN TO_NUMBER(T3.EVNT_STRT_DATE) <= TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')) AND TO_NUMBER(T3.EVNT_END_DATE) >= TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')) THEN '1' ");
		sql.append("\n 		       WHEN TO_NUMBER(T3.EVNT_END_DATE) < TO_NUMBER(TO_CHAR(SYSDATE,'YYYYMMDD')) THEN '2' ELSE '2' END)AS DATE_STATUS										");		
		sql.append("\n 		,(CASE WHEN T3.LESN_DC_COST= '0' THEN 'Y' WHEN T3.LESN_DC_COST <> '0' AND T2.STTL_STAT_CLSS= 'N' AND T1.PGRS_YN = 'D' THEN 'Y' WHEN  T3.LESN_DC_COST <> '0' AND T2.STTL_STAT_CLSS= 'Y' THEN 'C' WHEN  T1.PGRS_YN='N' THEN 'N' ELSE '-' END)AS STATUS  ");
		sql.append("\n FROM BCDBA.TBGAPLCMGMT T1 LEFT JOIN BCDBA.TBGSTTLMGMT T2 ON T1.APLC_SEQ_NO = T2.STTL_GDS_SEQ_NO 		");
		sql.append("\n 		LEFT JOIN BCDBA.TBGEVNTMGMT T3 ON T1.LESN_SEQ_NO = T3.EVNT_SEQ_NO 								");
		sql.append("\n 		LEFT JOIN BCDBA.TBGGOLFCDHD T4 ON T1.CDHD_ID = T4.CDHD_ID 										");
		sql.append("\n 		LEFT JOIN BCDBA.TBGGOLFCDHDGRDMGMT T5 ON T1.CDHD_ID = T5.CDHD_ID 								");
		sql.append("\n 		LEFT JOIN BCDBA.TBGGOLFCDHDCTGOMGMT T6 ON T5.CDHD_CTGO_SEQ_NO = T6.CDHD_CTGO_SEQ_NO 			");
		sql.append("\n WHERE  T1.GOLF_SVC_APLC_CLSS = ?	 AND T1.APLC_SEQ_NO = ?				");
		

		return sql.toString();
	}
}
